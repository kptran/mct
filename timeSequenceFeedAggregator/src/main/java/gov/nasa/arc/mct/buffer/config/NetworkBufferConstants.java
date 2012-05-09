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
package gov.nasa.arc.mct.buffer.config;

public class NetworkBufferConstants {
    public final static String HTTP_PROTOCOL = "http://";
    public final static char DELIMITER = '/';
    public final static char PORT_DELIMITER = ':';
    public final static String GET_DATA_COMMAND = "requestData";
    public final static String FEED_ID_PARAMETER = "feeds";
    public final static String START_TIME_PARAMETER = "startTime";
    public final static String END_TIME_PARAMETER = "endTime";
    public final static char PARAMETER_DELIMITER = ',';
    
    public final static String constructURL(String host, int port, String command) {
        StringBuilder sb = new StringBuilder(HTTP_PROTOCOL);
        sb.append(host);
        sb.append(PORT_DELIMITER);
        sb.append(port);
        sb.append(DELIMITER);
        sb.append(command);
        sb.append(DELIMITER);
        return sb.toString();
    }
    
}
