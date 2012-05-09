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
package gov.nasa.arc.mct.core.policy;

import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.PolicyContext;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ReservedWordsNamingPolicyTest {
    
    private static final String TEST_STRING_SHOULD_FAIL = "UserDropBox";
    private static final String TEST_STRING_SHOULD_PASS = "UsersBox";
 
    private ReservedWordsNamingPolicy policy = new ReservedWordsNamingPolicy();
    
    @BeforeClass
    public void setUp() {
    }
    
 
    
    @AfterTest
    public void shutDown() {
   }
    
    @Test
    public void testReservedWordsNamingPolicy() {

        PolicyContext context = new  PolicyContext();
        context.setProperty("NAME", TEST_STRING_SHOULD_FAIL);
        ExecutionResult exResult = policy.execute(context);
        
        Assert.assertFalse(exResult.getStatus());
        
        context.setProperty("NAME", TEST_STRING_SHOULD_PASS);
        exResult = policy.execute(context);
        
        Assert.assertTrue(exResult.getStatus());
    }


}
