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

import gov.nasa.arc.mct.util.logging.MCTLogger;

import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;

/**
 * Implements a listener for framework and bundle events.
 * All events received are send to the MCT log system.
 */
public class OSGiFrameworkListener implements FrameworkListener, BundleListener {
	
	private static MCTLogger logger = MCTLogger.getLogger(OSGiFrameworkListener.class);
	private static final OSGiFrameworkListener INSTANCE = new OSGiFrameworkListener();
	
	/**
	 * Gets the singleton instance of the listener.
	 * 
	 * @return the listener
	 */
	public static OSGiFrameworkListener getInstance() {
		return INSTANCE;
	}
	
	/**
	 * A private constructor, to enforce the singleton pattern.
	 */
	private OSGiFrameworkListener() {
		// do nothing
	}
	
	@Override
	public void frameworkEvent(FrameworkEvent event) {
		switch (event.getType()) {
		case FrameworkEvent.ERROR: {
			if (event.getThrowable() != null) {
				logger.error("OSGi error: " + event, event.getThrowable());
			} else {
				logger.error("OSGi error: " + event);
			}
			break;
		}
		case FrameworkEvent.INFO: {
			logger.info("OSGi info: " + event);
			break;
		}
		case FrameworkEvent.PACKAGES_REFRESHED: {
			logger.info("OSGi packages refreshed: " + event);
			break;
		}
		case FrameworkEvent.STARTED: {
			logger.info("OSGi framework started: " + event);
			break;
		}
		case FrameworkEvent.STARTLEVEL_CHANGED: {
			logger.info("OSGi framework start level changed: " + event);
			break;
		}
		case FrameworkEvent.WARNING: {
			logger.warn("OSGi warning: " + event);
			break;
		}
		default: {
			logger.warn("OSGi framework event: " + event);
			break;
		}
		}
	}

	@Override
	public void bundleChanged(BundleEvent event) {
		switch (event.getType()) {
		case BundleEvent.INSTALLED: {
			logger.debug("Bundle installed: {0}", event);
			break;
		}
		case BundleEvent.RESOLVED: {
			logger.debug("Bundle resolved: {0}", event);
			break;
		}
		case BundleEvent.STARTED: {
			logger.debug("Bundle started: {0}", event);
			break;
		}
		case BundleEvent.STARTING: {
			logger.debug("Bundle starting: {0}", event);
			break;
		}
		case BundleEvent.STOPPED: {
			logger.debug("Bundle stopped: {0}", event);
			break;
		}
		case BundleEvent.STOPPING: {
			logger.debug("Bundle stopping: {0}", event);
			break;
		}
		case BundleEvent.UNINSTALLED: {
			logger.debug("Bundle uninstalled: {0}", event);
			break;
		}
		case BundleEvent.UNRESOLVED: {
			logger.debug("Bundle unresolved: {0}", event);
			break;
		}
		case BundleEvent.UPDATED: {
			logger.debug("Bundle updated: {0}", event);
			break;
		}
		default: {
			logger.debug("Bundle event: {0}", event);
			break;
		}
		}
	}

}
