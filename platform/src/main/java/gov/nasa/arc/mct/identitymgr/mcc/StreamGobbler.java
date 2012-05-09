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
 * IdentityManager
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */

package gov.nasa.arc.mct.identitymgr.mcc;

import gov.nasa.arc.mct.util.logging.MCTLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Processes streams for the shift change reporter. 
 *
 */
public class StreamGobbler extends Thread {
    private static MCTLogger logger = MCTLogger.getLogger(StreamGobbler.class);

    InputStream is;
    String type;
    ShiftChangeReporter parent;

    /**
     * Creates a stream gobbler.
     * @param is stream to process
     * @param type name of this gobbler
     */
    public StreamGobbler(InputStream is, String type) {
        this(is, type, null);
    }

    /**
     * Creates a stream gobbler.
     * @param is stream to process
     * @param type name of this gobbler
     * @param parent parent of this thread
     */
    public StreamGobbler(InputStream is, String type, ShiftChangeReporter parent) {
        this.is = is;
        this.type = type;
        this.parent = parent;
    }

    /**
     * Begins a stream processor.
     */
    public void run() {

        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;

            while ((line = br.readLine()) != null) {
                if (line != null) {

                    if (parent != null) {
                        parent.reportIfValueChanged(line);
                    }
                }
                logger.debug(type + ">" + line);
            }

        } catch (IOException ioe) {
            logger.error("IO Exception :", ioe);
        }
    }
}
