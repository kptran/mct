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
package gov.nasa.arc.mct.identitymgr.mcc;

import static org.testng.Assert.assertEquals;
import gov.nasa.arc.mct.util.exception.MCTException;

import java.io.IOException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ShiftChangeReporterTest {
    static int port = ShiftChangeMonitor.DEFAULT_PORT;
    static int testInterval = 14;
    String MCCuserID = "testUSERID";

    @BeforeClass
    void init() {
        IdTestUtils.setMctProperties();
    }

    @AfterClass
    void undo() {
        IdTestUtils.restoreMctProperties();
    }

    @Test(expectedExceptions = MCTException.class)
    public void testUnsetUserParameter() throws MCTException, IOException {
        new ShiftChangeReporter("");
    }

    @Test(expectedExceptions = MCTException.class)
    public void testNonNumericPortParameter() throws Exception {
        String save = System.getProperty("mcc.monitor.port");
        System.setProperty("mcc.monitor.port", "xxx");
        try {
            new ShiftChangeReporter(MCCuserID);
        } catch (Exception e) {
            throw e;
        } finally {
            System.setProperty("mcc.monitor.port", save);
        }
    }

    @Test(expectedExceptions = MCTException.class)
    public void testEmptyPortParameter() throws Exception {
        String save = System.getProperty("mcc.monitor.port");
        System.setProperty("mcc.monitor.port", "");
        try {
            new ShiftChangeReporter(MCCuserID);
        } catch (Exception e) {
            throw e;
        } finally {
            System.setProperty("mcc.monitor.port", save);
        }
    }

    @Test(expectedExceptions = MCTException.class)
    public void testEmptyCmdParameter() throws Exception {
        String save = System.getProperty("mcc.monitor.command");
        System.setProperty("mcc.monitor.command", "");
        try {
            new ShiftChangeReporter(MCCuserID);
        } catch (Exception e) {
            throw e;
        } finally {
            System.setProperty("mcc.monitor.command", save);
        }
    }

    @Test
    public void testNonNumericIntervalParameter() throws MCTException, IOException {
        String save = System.getProperty("mcc.monitor.interval");
        System.setProperty("mcc.monitor.interval", "xxx");
        ShiftChangeReporter shiftChangeReporter = new ShiftChangeReporter(MCCuserID);
        assertEquals(shiftChangeReporter.getInterval(), ShiftChangeReporter.DEFAULT_INTERVAL);
        System.setProperty("mcc.monitor.interval", save);
    }

    @Test
    public void testNullIntervalParameter() throws MCTException, IOException {
        String save = System.getProperty("mcc.monitor.interval");
        System.setProperty("mcc.monitor.interval", "");
        ShiftChangeReporter shiftChangeReporter = new ShiftChangeReporter(MCCuserID);
        assertEquals(shiftChangeReporter.getInterval(), ShiftChangeReporter.DEFAULT_INTERVAL);
        System.setProperty("mcc.monitor.interval", save);
    }

    @Test
    public void testNumericIntervalParameter() throws MCTException, IOException {
        String save = System.getProperty("mcc.monitor.interval");
        System.setProperty("mcc.monitor.interval", new Integer(testInterval).toString());
        ShiftChangeReporter shiftChangeReporter = new ShiftChangeReporter(MCCuserID);
        assertEquals(shiftChangeReporter.getInterval(), testInterval);
        System.setProperty("mcc.monitor.interval", save);
    }

    @Test
    public void testDetect() throws MCTException, IOException {
        ShiftChangeReporter shiftChangeReporter = new ShiftChangeReporter(MCCuserID);
        shiftChangeReporter.setReporterExpiration(3);
        shiftChangeReporter.detectShiftChange();
    }
}
