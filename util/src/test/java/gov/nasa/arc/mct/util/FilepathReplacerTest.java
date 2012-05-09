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
package gov.nasa.arc.mct.util;

import java.util.Properties;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


public class FilepathReplacerTest {
    
    @DataProvider(name = "expressionsDOS")
    public Object[][] expressionDOS() {

        Properties evarData = new Properties();
        evarData.put("rw", "C:\\Program Files");
        evarData.put("group", "BillGates");
        evarData.put("MCCflightID", "Melina Gates");


        return new Object[][] {
                // expression, evarData, expected output
                new Object[] {"anything%(rw)\\%(MCCflightID)\\somestring%(group)\\testFile.log", 
                        evarData, "anythingC:\\Program Files\\Melina Gates\\somestringBillGates\\testFile.log"},
                new Object[] {"%(rw)\\%(MCCflightID)%(group)", 
                        evarData, "C:\\Program Files\\Melina GatesBillGates"},
                new Object[] {"((%(rw)\\))\\somestring%(group)\\t%", 
                        evarData, "((C:\\Program Files\\somestringBillGates\\t%"}
                        
        };
    }
    
    @DataProvider(name = "expressions")
    public Object[][] expressionTests() {

        Properties evarData = new Properties();
        evarData.put("rw", ".");
        evarData.put("group", "JimmyDurante");
        evarData.put("MCCflightID", "SteveMartin");
        evarData.put("MCCreconID", "TheBangels");

        return new Object[][] {
                // expression, evarData, expected output
                new Object[] {"/tmp/%(rw)/%(group)/%(MCCflightID)%(MCCreconID)/testFile.log", evarData, "/tmp/./JimmyDurante/SteveMartinTheBangels/testFile.log"},
                new Object[] {"%(rw)/%(group)/%(MCCflightID)%(MCCreconID)/testFile.log", evarData, "./JimmyDurante/SteveMartinTheBangels/testFile.log"},
                new Object[] {"%(rw)/%(group)/%(evarUNSET)%(MCCreconID)/testFile.log", evarData, "./JimmyDurante/TheBangels/testFile.log"},  // unset evar adjacent to evar
                new Object[] {"%(rw)/%(evarUNSET)/%(MCCflightID)%(MCCreconID)/testFile.log", evarData, ".//SteveMartinTheBangels/testFile.log"},  // unset evar adjacent slash
                new Object[] {"", evarData, ""},
                new Object[] {"/tmp/%(group)/testFile.log", new Properties(), "/tmp//testFile.log"}, //empty map
        };
    }

    @DataProvider(name = "errorExpressions")
    public Object[][] errorData() {
        
        Properties evarData = new Properties();
        evarData.put("group", "JimmyDurante");

        return new Object[][] {
                // expression, evarData
                new Object[] {"", null},
                new Object[] {null, evarData}, //empty expression
        };
    }

    @Test(dataProvider="expressionsDOS")
    public void testDOS(final String expression, final Properties p, final String expectedOutput) throws Exception {
        Assert.assertEquals(FilepathReplacer.substitute(expression, p), expectedOutput);
    }
    
    @Test(dataProvider="expressions")
    public void test(final String expression, final Properties p, final String expectedOutput) throws Exception {
        Assert.assertEquals(FilepathReplacer.substitute(expression, p), expectedOutput);
    }
    
    @Test(dataProvider="errorExpressions",expectedExceptions=java.lang.AssertionError.class)
    public void testErrors(final String expression, final Properties p) throws Exception {
        FilepathReplacer.substitute(expression, p);
    }
}
