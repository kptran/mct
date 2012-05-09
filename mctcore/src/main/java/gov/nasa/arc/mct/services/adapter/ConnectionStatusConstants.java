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
package gov.nasa.arc.mct.services.adapter;

/**
 * Constants for names of connection status event properties.
 */
public final class ConnectionStatusConstants {

    /** The event topic name indicating a telemetry connection status event. */
    public static final String CONNECTION_STATUS_TOPIC = "gov/nasa/arc/telemetry/isp/connection/status";

    /** The event property name indicating the connection status. */
    public static final String CONNECTION_STATUS = "connection.status";
    
    /** The event property name indicating whether the connection is active. (A Boolean) */
    public static final String CONNECTION_IS_CONNECTED = "connection.connected";
    
    /** The event property name indicating the server hostname to which we are connected. */
    public static final String CONNECTION_HOST = "connection.host";
    
    /** The event property name indicating the server port to which we are connected. */
    public static final String CONNECTION_PORT = "connection.port";

}
