/*******************************************************************************
 * Mission Control Technologies, Copyright (c) 2009-2012, United States Government
 * as represented by the Administrator of the National Aeronautics and Space 
 * Administration. All rights reserved.
 *
 * The MCT platform is licensed under the Apache License, Version 2.0 (the 
 * "License"); you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations under 
 * the License.
 *
 * MCT includes source code licensed under additional open source licenses. See 
 * the MCT Open Source Licenses file included with this distribution or the About 
 * MCT Licenses dialog available at runtime from the MCT Help menu for additional 
 * information. 
 *******************************************************************************/
package gov.nasa.arc.mct.osgi.platform;

import gov.nasa.arc.mct.util.FilepathReplacer;
import gov.nasa.arc.mct.util.exception.MCTRuntimeException;
import gov.nasa.arc.mct.util.logging.MCTLogger;
import gov.nasa.arc.mct.util.property.MCTProperties;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.service.log.LogReaderService;
import org.osgi.util.tracker.ServiceTracker;

public class EquinoxOSGIRuntimeImpl implements OSGIRuntime {
    private static final MCTLogger logger = MCTLogger.getLogger(EquinoxOSGIRuntimeImpl.class);

    /**
     * A directory along the classpath where we expect to find base OSGi
     * bundles.
     */
    private static final String OSGI_BUNDLE_DIR = "osgi";
    private static final String OSGI_BUNDLE_SYS_PROPERTY = "osgiPluginsList";

    /** A directory along the classpath where we expect to find MCT plugins. */
    private static final String PLATFORM_BUNDLE_DIR = "platform";
    private static final String PLATFORM_BUNDLE_SYS_PROPERTY = "platformPluginsList";

    /**
     * A directory along the classpath where we expect to find additional
     * bundles
     */
    private static final String EXTERNAL_BUNDLE_DIR = "plugins";
    private static final String EXTERNAL_BUNDLE_SYS_PROPERTY = "externalPluginsList";
    private static final String EXCLUDE_BUNDLES_SYS_PROPERTY = "excludePluginsList";
    
    /** Amount of time to wait for framework to stop when stopping the framework. */
    private static final long FRAMEWORK_STOP_WAIT_TIME = 5000;

    private static EquinoxOSGIRuntimeImpl osgiRuntime = new EquinoxOSGIRuntimeImpl();

    public static EquinoxOSGIRuntimeImpl getOSGIRuntime() {
        return osgiRuntime;
    }

    private Framework framework = null;
    private BundleContext bc = null;
    private File cacheDir = null;

    @Override
    public MCTProperties getConfig() {
        return MCTProperties.DEFAULT_MCT_PROPERTIES;
    }

    @Override
    public void startOSGi() {
        configOSGI();
        startOSGI();
    }
    
    private void configOSGI() throws MCTRuntimeException {
        MCTProperties config = getConfig();

        String filePath = FilepathReplacer.substitute(config.getProperty("cacheDir") );
        logger.info("OSGI cache location {0}", filePath);
        
        // Want to make sure that any old bundles are cleared out.
        cacheDir = getFile(filePath);

        if (cacheDir.exists()) {
            if (!deleteDir(cacheDir)) {
                logger.warn("Could not delete OSGi cache directory");
            }
        }

        // (Re)create the cache directory.
        if (!cacheDir.mkdirs()) {
            throw new MCTRuntimeException("Could not create osgi cache dir (" + cacheDir
                    + "). Ensure that the directory is writable and executable.");
        }

    }

    private boolean deleteDir(File f) {
        if (f.isDirectory()) {
            for (File child : f.listFiles()) {
                if (!deleteDir(child)) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return f.delete(); 
    }

    private void startOSGI() {
        logger.debug("Starting osgi framework");

        Map<String, String> props = new HashMap<String, String>();
        
        // Start with a clean bundle cache.
        props.put(Constants.FRAMEWORK_STORAGE_CLEAN, Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);
        
        // Set the cache directory path.
        props.put(Constants.FRAMEWORK_STORAGE, cacheDir.getAbsolutePath());
        
        // Bundles should have the extension classloader as their parent classloader.
        props.put(Constants.FRAMEWORK_BUNDLE_PARENT, Constants.FRAMEWORK_BUNDLE_PARENT_EXT);
        
        // felix specific properties
        props.put("org.osgi.framework.bootdelegation", getConfig().getProperty("org.osgi.framework.bootdelegation", ""));
        //props.put("felix.log.level","4");

        // Add all system properties that seem to be OSGi property names.
        for (String key : System.getProperties().stringPropertyNames()) {
            if (key.startsWith("osgi.") || key.startsWith("org.osgi.")) {
                props.put(key, System.getProperty(key));
            }
        }
        logger.debug("osgi launch properties {0}", props);

        // Iterate over frameworks on the classpath to see if one matches. See the
        // Javadoc for org.osgi.framework.launch.FrameworkFactory for information
        // about using ServiceLoader to find the available framework implementations.
        for (FrameworkFactory factory : ServiceLoader.load(FrameworkFactory.class)) {
            framework = factory.newFramework(props);
        }

        if (framework == null) {
            throw new MCTRuntimeException("Cannot find an OSGi framework");
        }
        try {
            framework.start();
        } catch (BundleException e) {
            throw new MCTRuntimeException("Cannot start OSGi framework", e);
        }
        
        bc = framework.getBundleContext();

        // Install a listener to get framework and bundle events so we can log
        // them.
        bc.addFrameworkListener(OSGiFrameworkListener.getInstance());
        bc.addBundleListener(OSGiFrameworkListener.getInstance());

        // Install a tracker for the OSGi log service to add a log listener.
        trackService(LogReaderService.class.getName(), new ServicesChanged() {
            @Override
            public void servicesChanged(Map<String, Collection<Object>> services) {
                // do nothing
            }

            @Override
            public void serviceAdded(String bundleId, Object service) {
                ((LogReaderService) service).addLogListener(OSGiLogListener.getInstance());
            }

            @Override
            public void serviceRemoved(String bundleId, Object service) {
                ((LogReaderService) service).removeLogListener(OSGiLogListener.getInstance());
            }

        });

        startOSGIBundles();
    }

    private void startOSGIBundles() {
        List<String> osgiBundleList = getBundleList(new DirectoryBundlesListTracker(), OSGI_BUNDLE_SYS_PROPERTY,
                OSGI_BUNDLE_DIR, Collections.<String>emptySet());
        logger.debug("osgi bundles to be started {0}", osgiBundleList);
        // load fragments first, to ensure libraries that are used by the platform that are not
        // imported will give precedence to the fragment bundle
        String systemExportBundle = null;
        for (String bundleName : osgiBundleList) {
            if (bundleName.contains("systemExports")) {
                systemExportBundle = bundleName;
            }
        }
        assert systemExportBundle != null;
        osgiBundleList.remove(systemExportBundle);
        osgiBundleList.add(0,systemExportBundle);
        loadBundles(osgiBundleList);
    }
    
    private Set<String> getExcludeBundles() {
        String propertyVal = MCTProperties.DEFAULT_MCT_PROPERTIES.getProperty(EXCLUDE_BUNDLES_SYS_PROPERTY);
        Set<String> excludeBundles = new HashSet<String>();
        if (propertyVal != null) {
            String[] bundles = propertyVal.split("[ \\t]*,[ \\t]*");
            for (String bundlePath : bundles) {
                excludeBundles.add(bundlePath);
            }
        }
        return excludeBundles;
    }

    private List<String> getBundleList(BundlesListTracker bundleListTracker, String bundleListSysProperty,
            String bundleListPath, Collection<String> excludeBundles) {
        String propertyVal = MCTProperties.DEFAULT_MCT_PROPERTIES.getProperty(bundleListSysProperty);
        if (propertyVal != null) {
            String[] bundles = propertyVal.split("[ \\t]*,[ \\t]*");
            for (String bundlePath : bundles) {
                if (!excludeBundles.contains(bundlePath)) {
                    bundleListTracker.addBundle(bundlePath);
                }
            }
        }

        logger.debug("Looking for bundles in location {0}", bundleListPath);
        List<String> bundlesLoc = bundleListTracker.getBundlesLocation();
        if (bundlesLoc == null || bundlesLoc.isEmpty()) {
            URL bundleDirURL = getResourceURL(bundleListPath);
            if (bundleDirURL == null) {
                logger.error("Bundle directory not found, skipped: " + bundleListPath);
                return Collections.emptyList();
            }

            File bundleDir;
            try {
                bundleDir = getFile(bundleDirURL);
            } catch (URISyntaxException e) {
                logger.error("Error getting path to bundle directory " + bundleListPath, e);
                return Collections.emptyList();
            }

            if (!bundleDir.isDirectory()) {
                logger.error("Bundle directory path not a directory: " + bundleListPath);
                return Collections.emptyList();
            }

            logger.debug("Loading bundles from directory {0}", bundleDir.getAbsolutePath());
            File[] bundles = bundleDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".jar");
                }
            });
            for (File bundleFile : bundles) {
                if (!excludeBundles.contains(bundleFile.getName())) {
                    bundleListTracker.addBundle(bundleFile.getAbsolutePath());
                    logger.debug("Found a bundle: {0}", bundleFile.getAbsolutePath());
                }
            }
        }

        return bundleListTracker.getBundlesLocation();
    }

    private void loadBundles(List<String> bundlesList) {
        List<Bundle> bundles = new ArrayList<Bundle>();

        for (String bundlePath : bundlesList) {
            logger.info("loadBundles path : " + bundlePath);

            File bundleFile = getFile(bundlePath);
            if (!bundleFile.exists()) {
                logger.error("Plugin path not found: " + bundlePath);
            } else if (!bundleFile.canRead()) {
                logger.error("Cannot read plugin file: " + bundlePath);
            } else {
                Bundle b = loadBundle(bundleFile);
                if (b != null) {
                    bundles.add(b);
                }
            }
        }

        startBundles(bundles);
    }

    Bundle loadBundle(File bundleFile) {
        String bundleURL = null;
        try {
            if (bundleFile.isDirectory()) {
                bundleURL = "reference:" + bundleFile.toURI().toURL().toExternalForm();
            } else {
                bundleURL = bundleFile.toURI().toURL().toExternalForm();
            }
        } catch (MalformedURLException e) {
            logger.error("Bad path to plugin bundle: " + bundleFile.getAbsolutePath());
            return null;
        }

        try {
            Bundle b = bc.installBundle(URLDecoder.decode(bundleURL, "UTF8"));
            logger.debug("Loaded plugin from path: {0}", bundleURL);
            return b;
        } catch (Exception e) {
            logger.error(e, "Error installing bundle from {0}", bundleURL);
            return null;
        }
    }

    /**
     * Starts an ordered list of bundles.
     * 
     * @param bundles
     *            the list of bundles to start
     */
    private void startBundles(List<Bundle> bundles) {
        for (Bundle bundle : bundles) {
            if (!isFragment(bundle)) {
                try {
                    bundle.start();
                    logger.debug("Started bundle {0}", bundle.getLocation());
                } catch (BundleException ex) {
                    logger.error(ex, "Error starting bundle {0}", bundle.getLocation());
                }
            }
        }
    }

    private boolean isFragment(Bundle bundle) {
        return (bundle.getHeaders().get("Fragment-Host") != null);
    }

    @Override
    public void startPlatformBundles() {
        List<String> platformBundles = getBundleList(new DirectoryBundlesListTracker(), PLATFORM_BUNDLE_SYS_PROPERTY,
                PLATFORM_BUNDLE_DIR, Collections.<String>emptySet());
        assert platformBundles != null && platformBundles.size() > 0;
        loadBundles(platformBundles);
    }

    @Override
    public void startExternalBundles() {
        Set<String> excludeBundles = getExcludeBundles();
        List<String> externalBundles = getBundleList(new ExternalBundlesListTracker(), EXTERNAL_BUNDLE_SYS_PROPERTY,
                EXTERNAL_BUNDLE_DIR, excludeBundles);
        loadBundles(externalBundles);
    }

    @Override
    public synchronized BundleContext getBundleContext() {
        return bc;
    }

    public <T> T getService(Class<T> serviceClass, String filter) {
        BundleContext bc = getBundleContext();
        if (bc == null) { return null; }
        ServiceReference[] srs;
        try {
            srs = bc.getServiceReferences(serviceClass.getName(), filter);
        } catch (InvalidSyntaxException e) {
            throw new RuntimeException(e);
        }

        if (srs != null && srs.length > 0) {
            ServiceReference sr = srs[0];
            return serviceClass.cast(bc.getService(sr));

        }
        return null;
    }

    @Override
    public void stopOSGI() throws BundleException, InterruptedException {
        framework.stop();
        framework.waitForStop(FRAMEWORK_STOP_WAIT_TIME);
        framework = null;
        bc = null;
    }

    @Override
    public void openServiceTracker(ServiceTracker tracker) {
        tracker.open();
    }

    @Override
    public void closeServiceTracker(ServiceTracker tracker) {
        tracker.close();
    }

    @Override
    public Object getService(ServiceTracker tracker, ServiceReference reference) {
        return tracker.getService(reference);
    }

    private URL getResourceURL(String path) {
        return getClass().getClassLoader().getResource(path);
    }

    private File getFile(String path) {
        return new File(path);
    }

    private File getFile(URL url) throws URISyntaxException {
        return new File(url.toURI());
    }

    @Override
    public void registerService(String[] serviceInterfaces, Object serviceInstance, Dictionary props) {
        getBundleContext().registerService(serviceInterfaces, serviceInstance, props);
    }

    private String getSymbolicName(Bundle bundle) {
        String symbolicName = bundle.getSymbolicName();

        return symbolicName;
    }

    private void dispatchServiceTrackerChanges(ServicesChanged handler, ServiceReference[] aRefs) {
        if (aRefs != null) {
            Map<String, Collection<Object>> services = new HashMap<String, Collection<Object>>();
            for (int i = 0; i < aRefs.length; i++) {
                final String symbolicName = getSymbolicName(aRefs[i].getBundle());
                Collection<Object> currentServices = services.get(symbolicName);
                Object service = bc.getService(aRefs[i]);
                if (currentServices == null) {
                    currentServices = new ArrayList<Object>();
                    services.put(symbolicName, currentServices);
                }

                currentServices.add(service);
            }
            handler.servicesChanged(services);
        }
    }

    @Override
    public void trackService(final String anInterface, final ServicesChanged handler) {
        final BundleContext bc = getBundleContext();
        ServiceTracker st = new ServiceTracker(bc, anInterface, null) {

            void refreshAllServices() {
                ServiceReference[] refs = null;
                try {
                    refs = bc.getAllServiceReferences(anInterface, null);
                } catch (InvalidSyntaxException e) {
                    throw new RuntimeException(e);
                }
                dispatchServiceTrackerChanges(handler, refs);
            }

            @Override
            public Object addingService(ServiceReference reference) {
                Object obj = super.addingService(reference);
                refreshAllServices();

                Object service = bc.getService(reference);
                handler.serviceAdded(getSymbolicName(reference.getBundle()), service);

                return obj;
            }

            @Override
            public void remove(ServiceReference reference) {
                super.remove(reference);
                refreshAllServices();
            }

            @Override
            public void modifiedService(ServiceReference reference, Object service) {
                super.modifiedService(reference, service);
                refreshAllServices();
            }

            @Override
            public void removedService(ServiceReference reference, Object service) {
                super.removedService(reference, service);
                refreshAllServices();

                handler.serviceRemoved(getSymbolicName(reference.getBundle()), service);
                bc.ungetService(reference);
            }
        };
        openServiceTracker(st);
    }

}
