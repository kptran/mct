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
package gov.nasa.arc.mct.components;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Detects multiple monitor displays singleton helper class.
 * 
 */
public class DetectGraphicsDevices {
	
    private static final Logger logger = LoggerFactory.getLogger(DetectGraphicsDevices.class);
    private static final DetectGraphicsDevices detectGraphicsDevices = new DetectGraphicsDevices();
    
    /** Minimum display monitor check. */
    public static final int MINIMUM_MONITOR_CHECK = 1;
    
    /** Open multiple monitor Objects menu action. */
    public static final String OPEN_MULTIPLE_MONITORS_OBJECTS_ACTION = "OPEN_MULTIPLE_MONITORS_OBJECTS_ACTION";
    
    /** Open multiple monitor This menu action. */
    public static final String OPEN_MULTIPLE_MONITORS_THIS_ACTION = "OPEN_MULTIPLE_MONITORS_THIS_ACTION";
    
    /** Objects menu additions menu path. */
    public static final String OBJECTS_ADDITIONS_MENU_PATH = "/objects/additions";
    
    /** This menu additions menu path. */
    public static final String THIS_ADDITIONS_MENU_PATH = "/this/additions";
    
    /** Objects submenu path. */
    public static final String OBJECTS_SUBMENU_PATH = "objects/submenu.ext";
    
    /** Text menu for Objects menu action. */
    public static final String SHOW_OBJECTS_ACTION_MULTIPLE_MONITORS_TEXT = "Open in Another Monitor";
    
    /** Open multiple monitor Objects menu name. */
    public static final String OBJECTS_OPEN_MULTIPLE_MONITORS_MENU = "OBJECTS_OPEN_MULTIPLE_MONITORS_MENU";
    
    /** Open multiple monitor This menu name. */
    public static final String THIS_OPEN_MULTIPLE_MONITORS_MENU = "THIS_OPEN_MULTIPLE_MONITORS_MENU";
    
    /** Text menu for This menu action. */
    public static final String SHOW_THIS_ACTION_MULTIPLE_MONITORS_TEXT = "Open This Object in Another Monitor";
    
    /** Default text menu. */
    public static final String DEFAULT_MULTIPLE_MONITOR_TEXT = "Default 'Display0' Monitor";
    
    /** Prefix for menu name. */
    public static final String PROPER_DEVICE_NAME_PREFIX = "Monitor";
    
        
    private GraphicsEnvironment graphicsEnv = null;
    private GraphicsDevice[] graphicsDevices = null;
    private GraphicsDevice singleGraphicsDevice = null;
    
    /**
     * Singleton getInstance.
     * 
     * @return detectGraphicsDevices
     */
    public static DetectGraphicsDevices getInstance() {
        return detectGraphicsDevices;
    }
    
    /**
     * Private constructor for Singleton.
     */
    private DetectGraphicsDevices() { 
        graphicsEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        graphicsDevices = graphicsEnv.getScreenDevices();
    }
    
    /**
     * Determines whether graphics device environment is headless or not.
     * 
     * @return isHeadless - boolean (default is false)
     */
    public boolean isGraphicsEnvHeadless() {
        // Tests whether the display (or keyboard, mouse) an be supported within this GraphicsEnvironment
        // If true, then HeadlessException is thrown and GraphicsEnv cannot support display; false otherwise 
        boolean isHeadless = false;
        
        try {
            
            isHeadless = GraphicsEnvironment.isHeadless();
            logger.debug("GraphicsEnvironment.isHeadless(): " + GraphicsEnvironment.isHeadless());
            
        } catch(HeadlessException headlessEx) {
            logger.error("HeadlessException: {0}", headlessEx);
        }
        
        logger.debug("isHeadless: {0}", isHeadless);
        return isHeadless;
    }
    
    /**
     * Determines whether graphics device environment is headless or not.
     * 
     * @return isHeadlessInstance - boolean (default is false)
     */
    public boolean isGraphicsEnvHeadlessInstance() {
        // Tests whether the display (or keyboard, mouse) an be supported within this GraphicsEnvironment
        // If true, then HeadlessException is thrown and GraphicsEnv can support display; false otherwise 
        boolean isHeadlessInstance = false;
        
        try {
            
            isHeadlessInstance = graphicsEnv.isHeadlessInstance();
            logger.debug("graphicsEnv.isHeadlessInstance(): {0}", graphicsEnv.isHeadlessInstance());
            
        } catch(HeadlessException headlessEx) {
            logger.error("HeadlessException: {0}", headlessEx);
        }
        
        logger.debug("isHeadlessInstance: {0}", isHeadlessInstance);
        return isHeadlessInstance;
    }
    
    /**
     * Gets the total number of graphics device available.
     * 
     * @return graphicsDevices.length - Total number of graphics devices detected
     */
    public int getNumberGraphicsDevices() {
        logger.debug("Total number of graphics device monitors (graphicsDevices.length): {0}", graphicsDevices.length);
        return (graphicsDevices != null ? graphicsDevices.length : 0);
    }
    
    /**
     * Gets the array of GraphicsDevices available.
     * 
     * @return graphicsDevices - Array of graphics devices objects
     */
    public GraphicsDevice[] getGraphicsDevice() {
            return graphicsDevices;
    }
    
    /**
     * Sets the array of GraphicsDevices.
     * 
     * @param graphicsDevices - Array of graphics devices objects
     */
    public void setGraphicsDevice(GraphicsDevice[] graphicsDevices) {
        this.graphicsDevices = graphicsDevices;
    }
    
    /**
     * Gets a single primary graphics device.
     * 
     * @return singleGraphicsDevice
     */
    public GraphicsDevice getSingleGraphicDevice() {
        return singleGraphicsDevice;
    }
    
    /**
     * Gets a single primary graphics device configuration based on graphics device name.
     * 
     * @param graphicsDeviceName - The graphics device name.
     * @return graphicsDeviceConfig - The graphics device configuration object or null.
     */
    public GraphicsConfiguration getSingleGraphicDeviceConfig(String graphicsDeviceName) {
        GraphicsConfiguration graphicsDeviceConfig = null;
        
        GraphicsDevice[] graphicsDevices = getGraphicsDevice();
        for (int i=0; i < graphicsDevices.length; i++) {
            GraphicsConfiguration[] graphicsConfigs = graphicsDevices[i].getConfigurations();
            logger.debug("[i={0}]: Total Graphics Configs: {1}", i, graphicsConfigs.length);
          
                String graphicsDeviceNameID = graphicsDevices[i].getIDstring();
                graphicsDeviceNameID = graphicsDeviceNameID.replace("\\", "");
                graphicsDeviceNameID = graphicsDeviceNameID.trim();
                graphicsDeviceName = graphicsDeviceName.trim();
                
                logger.debug("graphicsDeviceNameID: {0}", graphicsDeviceNameID + " ==? graphicsDeviceName: {1}", graphicsDeviceName 
                        + " check=" + (graphicsDeviceNameID.equals(graphicsDeviceName)));
                
                if (graphicsDeviceNameID.equals(graphicsDeviceName)) {
                    graphicsDeviceConfig = graphicsDevices[i].getDefaultConfiguration();
                    singleGraphicsDevice = graphicsDevices[i];
                    
                    if (graphicsDevices[i] == null) {
                        logger.error("Multiple monitor graphics devices are NULL will open on default current monitor.");
                    }
                    
                    logger.debug("singleGraphicsDevice: {0}", singleGraphicsDevice);
                    
                    return graphicsDeviceConfig;
                }
        }

        return graphicsDeviceConfig;
    }
    
    /**
     * Checks whether target is Windows OS platform.
     * 
     * @return boolean - WindowsOS or not
     */
    public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf("win") >= 0);
    }
 
    /**
     * Checks whether target is MacOS platform.
     * 
     * @return boolean - MacOS or not
     */
    public static boolean isMac() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf("mac") >= 0); 
    }
 
    /**
     * Checks whether target is UNIX/Linux platform.
     * 
     * @return boolean - UNIX or Linux OS platform or not
     */
    public static boolean isUnixLinux() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);
    }
    
    /**
     * Gets an array list of graphics device names available.
     * 
     * @return graphicsDeviceNames - ArrayList of strings for graphics device names
     */
    public ArrayList<String> getGraphicDeviceNames() {
        ArrayList<String> graphicsDeviceNames = new ArrayList<String>();
        
        for (int i=0; i < graphicsDevices.length; i++) {
            graphicsDeviceNames.add(graphicsDevices[i].getIDstring());
		}
		return graphicsDeviceNames;
	}
}
