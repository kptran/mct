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
/**
 * Startup.java Aug 18, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.platform;

import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.dao.service.TagServiceImpl;
import gov.nasa.arc.mct.dao.specifications.DatabaseIdentification;
import gov.nasa.arc.mct.dao.specifications.MCTUser;
import gov.nasa.arc.mct.defaults.view.DefaultViewPolicyProvider;
import gov.nasa.arc.mct.defaults.view.DefaultViewProvider;
import gov.nasa.arc.mct.exception.DefaultExceptionHandler;
import gov.nasa.arc.mct.gui.FeedManagerImpl;
import gov.nasa.arc.mct.gui.MenuExtensionManager;
import gov.nasa.arc.mct.gui.StatusAreaWidgetRegistryImpl;
import gov.nasa.arc.mct.gui.dialogs.AboutDialog;
import gov.nasa.arc.mct.identitymgr.IdentityManagerFactory;
import gov.nasa.arc.mct.loader.DataLoader;
import gov.nasa.arc.mct.loader.GlobalComponentLoader;
import gov.nasa.arc.mct.lock.manager.MCTLockManagerFactory;
import gov.nasa.arc.mct.osgi.platform.EquinoxOSGIRuntimeImpl;
import gov.nasa.arc.mct.osgi.platform.OSGIRuntime;
import gov.nasa.arc.mct.osgi.platform.OSGIRuntime.ServicesChanged;
import gov.nasa.arc.mct.persistence.config.DatabaseNameConfig;
import gov.nasa.arc.mct.persistence.config.DatabaseNameConfigImpl;
import gov.nasa.arc.mct.persistmgr.SynchronousPersistenceBroker;
import gov.nasa.arc.mct.platform.spi.PersistenceService;
import gov.nasa.arc.mct.platform.spi.Platform;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.platform.spi.RoleAccess;
import gov.nasa.arc.mct.platform.spi.RoleService;
import gov.nasa.arc.mct.policymgr.PolicyManagerImpl;
import gov.nasa.arc.mct.registry.ExternalComponentRegistryImpl;
import gov.nasa.arc.mct.registry.ExternalComponentRegistryImpl.ExtendedComponentProvider;
import gov.nasa.arc.mct.registry.GlobalComponentRegistry;
import gov.nasa.arc.mct.search.SearchServiceImpl;
import gov.nasa.arc.mct.services.component.ComponentProvider;
import gov.nasa.arc.mct.services.component.ComponentTagService;
import gov.nasa.arc.mct.services.component.FeedManager;
import gov.nasa.arc.mct.services.component.MenuManager;
import gov.nasa.arc.mct.services.component.PolicyManager;
import gov.nasa.arc.mct.services.component.TagService;
import gov.nasa.arc.mct.services.internal.component.MCTCountDownLatch;
import gov.nasa.arc.mct.services.internal.component.impl.MCTCountDownLatchImpl;
import gov.nasa.arc.mct.util.LookAndFeelSettings;
import gov.nasa.arc.mct.util.exception.MCTException;
import gov.nasa.arc.mct.util.exception.MCTRuntimeException;
import gov.nasa.arc.mct.util.internal.ElapsedTimer;
import gov.nasa.arc.mct.util.logging.MCTLogger;
import gov.nasa.arc.mct.util.property.MCTProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

public class Startup {

    private static final MCTLogger logger = MCTLogger.getLogger(Startup.class);
    private static final MCTLogger ADVISORY_SERVICE_LOGGER = MCTLogger.getLogger("gov.nasa.jsc.advisory.service");
    private static final MCTLogger PERF_LOGGER = MCTLogger.getLogger("gov.nasa.arc.mct.performance.startup");

    private GlobalContext globalContext = GlobalContext.getGlobalContext();
    private Configuration configuration = new Configuration();
    private final Runnable refreshRunnable;
    private final ElapsedTimer timer;

    Startup() {
        timer = new ElapsedTimer();
        timer.startInterval();
        String startupMessage = "Starting MCT version: " + AboutDialog.getBuildNumber();    
        logger.info(startupMessage);
        ADVISORY_SERVICE_LOGGER.info(startupMessage);
        DefaultExceptionHandler defaultExceptionHandler = new DefaultExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(defaultExceptionHandler);

        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                GlobalComponentRegistry.clearRegistry();
                loadComponents();
                PlatformImpl.getInstance().getWindowManager().refreshWindows();
            }
        };

        ElapsedTimer startupTimer = new ElapsedTimer();
        
        try {
            MCTCountDownLatch latch = new MCTCountDownLatchImpl(1);
            
            startupTimer.startInterval();
            startupCore();
            startupTimer.stopInterval();
            PERF_LOGGER.info("time to startup core {0}", startupTimer.getIntervalInMillis());
            
            startupTimer.startInterval();
            initOsgiPlatform(latch);
            startupTimer.stopInterval();
            PERF_LOGGER.info("time to initialize OSGI platform {0}", startupTimer.getIntervalInMillis());

            startupMCT(latch);
        } catch (Exception t) {
            defaultExceptionHandler.uncaughtException(Thread.currentThread(), t);
            System.exit(1);
        }
    }

    private void startupCore() throws Exception {
        initProperties();
        injectPlatform();
        initPersistenceManager();
        initIDManager();
        loadUser();
        // Don't check schema versions if a special JVM parameter is set.
        if (System.getProperty("mct.db.check-schema-version", Boolean.TRUE.toString()).equals(Boolean.TRUE.toString())) {
            checkDatabaseCompatibility();
        }
        initLockManager();
        initBackgroundTasks();
    }

    /**
     * Inject the platform implementation into the MCT core module. This is
     * currently done without OSGi, but once the APIs become a bundle, this
     * could be done using declarative services.
     */
    private void injectPlatform() {
        PlatformAccess platformAccess = new PlatformAccess();
        platformAccess.setPlatform(PlatformImpl.getInstance());
    }

    private void startupMCT(MCTCountDownLatch latch) throws Exception {
        latch.await();

        synchronized (this) {

            GlobalComponentRegistry.clearRegistry();

            ElapsedTimer startupTimer = new ElapsedTimer();
            
            startupTimer.startInterval();
            initComponentLoader();
            startupTimer.stopInterval();
            PERF_LOGGER.info("time to initialize component loader {0}", startupTimer.getIntervalInMillis());

            startupTimer.startInterval();
            loadComponents();
            startupTimer.stopInterval();
            PERF_LOGGER.info("time to loadComponents {0}", startupTimer.getIntervalInMillis());
            
            startupTimer.startInterval();
            startupTimer.stopInterval();
            PERF_LOGGER.info("time to startJMXAgent {0}", startupTimer.getIntervalInMillis());
                        
            startupTimer.startInterval();
            initUserInterface();
            startupTimer.stopInterval();
            PERF_LOGGER.info("time to startUserInterface {0}", startupTimer.getIntervalInMillis());

            timer.stopInterval();
            PERF_LOGGER.info("total time to start MCT {0}", timer.getIntervalInMillis());
        }
    }

    /**
     * Loads all entries from the database identification table and calls the
     * compatibility checker.
     * 
     * @throws MCTException
     *             if the compatibility check fails
     */
    private void checkDatabaseCompatibility() throws MCTException {
        SynchronousPersistenceBroker p = SynchronousPersistenceBroker.getSynchronousPersistenceBroker();
        List<DatabaseIdentification> dbidList = p.loadAll(DatabaseIdentification.class);
        CompatibilityChecker compatibilityChecker =
            new CompatibilityChecker(
                    configuration.getSchemaID(),
                    configuration.getStoredProceduresID());
        compatibilityChecker.checkDatabaseCompatibility(dbidList);
    }

    private void initBackgroundTasks() {
        Timer databasePollingTimer = new Timer();
        databasePollingTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                PlatformAccess.getPlatform().getPersistenceService().updateComponentsFromDatabase();
            }
            
        }, Calendar.getInstance().getTime(), 3000);
    }
    
    private void initUserInterface() {
        // For now, make the platform provide the window decorations,
        // specifically for Linux. Using false here will use the platform
        // style, with solid background color and platform-standard window
        // controls.
        // This method has to be invoked before a JFrame is instantiated, and
        // it stays in effect for all JFrames until the method is called again.
        JFrame.setDefaultLookAndFeelDecorated(false);

        // Initialize the Look and Feel Setting object to the value in
        // mct.properties file, or else use the default LAF.
        String lookAndFeelStr = MCTProperties.DEFAULT_MCT_PROPERTIES.getProperty("mct.look.and.feel");
        LookAndFeelSettings.INSTANCE.setLAF(lookAndFeelStr);
        if (! LookAndFeelSettings.INSTANCE.isInitialized()) {
            logger.error("Could not initialize the Swing Look and Feel settings, MCT is closing.");
            System.exit(1);
        }

        initUserEnvironment();
    }

    private void initComponentLoader() {
        GlobalComponentLoader globalLoader = GlobalComponentLoader.getLoader();
        globalContext.setComponentLoader(globalLoader);
    }

    private void initOsgiPlatform(MCTCountDownLatch latch) {
        OSGIRuntime osgiRuntime = EquinoxOSGIRuntimeImpl.getOSGIRuntime();
        osgiRuntime.startOSGi();
        initServicesAndHandlers(latch);
        osgiRuntime.startPlatformBundles();
        osgiRuntime.startExternalBundles();
    }

    private void initServicesAndHandlers(MCTCountDownLatch latch) {
        OSGIRuntime osgiRuntime = EquinoxOSGIRuntimeImpl.getOSGIRuntime();
        
        osgiRuntime.registerService(new String[] { MCTCountDownLatch.class.getName() }, latch, new Properties());
        osgiRuntime.registerService(new String[] { gov.nasa.arc.mct.services.component.ComponentRegistry.class
                .getName() }, ExternalComponentRegistryImpl.getInstance(), new Properties());

        osgiRuntime.registerService(new String[] { PolicyManager.class.getName() }, PolicyManagerImpl.getInstance(),
                new Properties());

        osgiRuntime.registerService(new String[] { FeedManager.class.getName() }, FeedManagerImpl.getInstance(),
                new Properties());

        osgiRuntime.registerService(new String[] { MenuManager.class.getName() }, MenuExtensionManager.getInstance(),
                new Properties());
                
        osgiRuntime.registerService(new String[] { Platform.class.getName() }, PlatformImpl.getInstance(),
                new Properties());

        osgiRuntime.registerService(new String[] { PersistenceService.class.getName() }, PlatformImpl.getInstance()
                .getPersistenceService(), new Properties());
        
        osgiRuntime.registerService(new String[] { ComponentProvider.class.getName() }, 
                new DefaultViewPolicyProvider(), new Properties());
        
        osgiRuntime.registerService(new String[] { TagService.class.getName(), ComponentTagService.class.getName() }, TagServiceImpl.getTagService(), new Properties());
        
        ExternalComponentRegistryImpl.getInstance().setDefaultViewProvider(new DefaultViewProvider());

        ServicesChanged handler = new ServicesChanged() {

            @Override
            public void servicesChanged(Map<String, Collection<Object>> services) {
                synchronized (Startup.this) {
                    List<ExtendedComponentProvider> providers = new ArrayList<ExtendedComponentProvider>(services
                            .size());
                    for (Map.Entry<String, Collection<Object>> entry : services.entrySet()) {
                        for (Object o : entry.getValue()) {
                            providers.add(new ExtendedComponentProvider((ComponentProvider) o, entry.getKey()));
                        }
                    }

                    ExternalComponentRegistryImpl.getInstance().refreshComponents(providers);
                    MenuExtensionManager.getInstance().refreshExtendedMenus(providers);
                    PolicyManagerImpl.getInstance().refreshExtendedPolicies(providers);
                    ProviderDelegateServiceImpl.getInstance().refresh(providers);
                    StatusAreaWidgetRegistryImpl.getInstance().refresh(providers);
                    SearchServiceImpl.getInstance().refresh(providers);
                }
            }

            @Override
            public void serviceAdded(String bundleId, Object service) {
               logger.info("Service added: "+service);
            }

            @Override
            public void serviceRemoved(String bundleId, Object service) {
                synchronized (Startup.this) {
                    refresh();
                }
            }

        };
        osgiRuntime.trackService(ComponentProvider.class.getName(), handler);
        
        ServicesChanged roleServiceHandler = new ServicesChanged() {
            
            @Override
            public void servicesChanged(Map<String, Collection<Object>> services) {
                //
            }
            
            @Override
            public void serviceRemoved(String bundleId, Object service) {
                RoleService roleService = (RoleService)service;
                RoleAccess.removeRoleService(roleService);
            }
            
            @Override
            public void serviceAdded(String bundleId, Object service) {
                RoleService roleService = (RoleService)service;
                RoleAccess.addRoleService(roleService);
            }
        };
        osgiRuntime.trackService(RoleService.class.getName(), roleServiceHandler);
    }

    private void refresh() {
        GlobalContext.getGlobalContext().switchUser(GlobalContext.getGlobalContext().getUser(), refreshRunnable);
    }

    private void initPersistenceManager() {
        globalContext.setSynchronousPersistenceManager(SynchronousPersistenceBroker.getSynchronousPersistenceBroker());
    }

    private void initIDManager() {
        try {
            globalContext.setIdManager(IdentityManagerFactory.newIdentityManager(this.refreshRunnable));
        } catch (MCTException e) {
            throw new MCTRuntimeException(e);
        } catch (IOException e) {
            throw new MCTRuntimeException(e);
        }
    }

    private boolean loadUser() {
        String whoami = GlobalContext.getGlobalContext().getIdManager().getCurrentUser();
        List<MCTUser> mctUsers = SynchronousPersistenceBroker.getSynchronousPersistenceBroker().loadAll(whoami,
                MCTUser.class, new String[] { "userId" }, new Object[] { whoami });
        if (mctUsers.isEmpty()) {
            throw new MCTRuntimeException("MCT user '" + whoami
                    + "' is not in the MCT database. You can load MCT user(s) using MCT's load user tool.");
        }
        MCTUser mctUser = mctUsers.get(0);
        GlobalContext.getGlobalContext().switchUser(mctUser, null);
        return true;
    }

    private void initLockManager() {
        globalContext.setLockManager(MCTLockManagerFactory.getLockManager());
    }

    private void initUserEnvironment() {
        new UserEnvironment();
    }

    private void loadComponents() {
        DataLoader loader = new DataLoader();
        loader.loadComponents();
    }

    /**
     * Sets resources values for Startup properties. Enforces list of required
     * properties.
     * 
     * @param propertyResource
     *            name of the property resource
     * @throws MCTException
     *             if the property cannot be set
     */
    private void initProperties() throws MCTException {
        String str;
        final MCTProperties mctProperties = MCTProperties.DEFAULT_MCT_PROPERTIES;
        MCTProperties versionProperties = null;
        try {
            versionProperties = new MCTProperties("properties/version.properties");
        } catch (IOException e) {
            throw new MCTException("Cannot load version properties (properties/version.properties)", e);
        }
        
        if ((str = mctProperties.getProperty("MCCreconID")) != null) {
            System.setProperty("MCCreconID", str);  // propagate to bundles
        } else {
            throw new MCTException("required property for default ReconId is undefined");
        }
        
        if ((str = versionProperties.getProperty("mct.db.schema_id")) == null) {
            throw new MCTException("required property mct.db.schema_id is undefined");
        }
        configuration.setSchemaID(str);

        if ((str = versionProperties.getProperty("mct.db.stored_procedures_id")) == null) {
            throw new MCTException("required property mct.db.stored_procedures_id is undefined");
        }
        configuration.setStoredProceduresID(str);

        configuration.setServiceLocatorEnabled(Boolean.valueOf(mctProperties.getProperty("serviceLocator.enabled",
                "false")));

        if ((str = versionProperties.getProperty("mct.activity.id")) != null) {
            DatabaseNameConfig activityConfig = new DatabaseNameConfigImpl();
            activityConfig.setDatabaseNameSuffix(str);
        
            GlobalContext.getGlobalContext().setDatabaseNameConfig(activityConfig);
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        new Startup();
    }
}
