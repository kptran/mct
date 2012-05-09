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
package gov.nasa.arc.mct.services.component;

/**
 * Provides a status object for a plugin to report warning or error during MCT startup. 
 */
public final class PluginStartupStatus {
    
    /**
     * Status instance with a true status flag.
     */
    public static final PluginStartupStatus STATUS_OK = new PluginStartupStatus(true);
    private boolean status;
    private String message;
    private Severity severity;
    
    /**
     * Defines the severity levels for this status. 
     */
    public enum Severity {
        /**
         * Error status.
         */
        ERROR,
        
        /**
         * Warning status. 
         */
        WARNING;
    };

    /**
     * Creates a <code>PluginStartupStatus</code> with a status boolean.
     * @param status boolean status
     */
    public PluginStartupStatus(boolean status) {
        this.status = status;
    }
    
    /**
     * Creates a <code>PluginStartupStatus</code>.
     * @param status boolean status
     * @param message status message
     * @param severity severity level of the status
     */
    public PluginStartupStatus(boolean status, String message, Severity severity) {
        this.status = status;
        this.message = message;
        this.severity = severity;
    }

    /**
     * Returns the status message.
     * @return status message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Returns the severity level of the status.
     * @return severity level of the status
     */
    public Severity getSeverity() {
        return severity;
    }
        
    
    /**
     * Returns the boolean status.
     * @return true if status OK; false, otherwise
     */
    public boolean getStatus() {
        return status;
    }
}
