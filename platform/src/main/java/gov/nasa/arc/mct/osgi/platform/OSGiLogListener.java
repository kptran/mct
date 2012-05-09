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

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogService;

/**
 * Implements a listener for the OSGi log service. Sends all log
 * events received to the MCT log. Uses a singleton pattern to
 * reuse a single instance.
 */
public class OSGiLogListener implements LogListener {

	private static MCTLogger logger = MCTLogger.getLogger(OSGiLogListener.class);
	private static final LogListener INSTANCE = new OSGiLogListener();
	
	/**
	 * Gets the singleton instance of the log listener.
	 * 
	 * @return the log listener
	 */
	public static LogListener getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Private constructor to enforce the singleton pattern.
	 */
	private OSGiLogListener() {
		// do nothing
	}
	
	@Override
	public void logged(LogEntry entry) {
		String msg = entry.getMessage();
		String pidMsg = "";
		String bundle = entry.getBundle().getSymbolicName();
		
		ServiceReference ref = entry.getServiceReference();
		if (ref != null) {
			String pid = (String) entry.getServiceReference().getProperty(Constants.SERVICE_PID);
			if (pid != null) {
				pidMsg = pid + ", ";
			}
		}
		
		switch (entry.getLevel()) {
		case LogService.LOG_ERROR: {
			logger.error(msg + " [" + pidMsg + bundle + "]", entry.getException());
			break;
		}
		case LogService.LOG_WARNING: {
			logger.warn(msg + " [" + pidMsg + bundle + "]");
			break;
		}
		case LogService.LOG_INFO: {
			logger.info(msg + " [" + pidMsg + bundle + "]");
			break;
		}
		case LogService.LOG_DEBUG: {
			logger.debug(msg + " [" + pidMsg + bundle + "]", entry.getException());
			break;
		}
		}
	}

}
