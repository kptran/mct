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

import java.util.Collections;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.core.components.TelemetryAllDropBoxComponent;
import gov.nasa.arc.mct.core.components.TelemetryUserDropBoxComponent;
import gov.nasa.arc.mct.persistence.PersistenceUnitTest;
import gov.nasa.arc.mct.platform.core.access.PlatformAccess;
import gov.nasa.arc.mct.platform.spi.MockPlatform;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.services.internal.component.ComponentInitializer;
import gov.nasa.arc.mct.services.internal.component.User;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test for DropBoxInspectionPolicy.
 * 
 * @author nshi
 *
 */
@Test(groups = { "policy.publication.tests" })
public class DropBoxInspectionPolicyTest extends PersistenceUnitTest {
    
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_GROUP = "admin";

    private static final String TEST_USER = "test";
    private static final String TEST_GROUP = "TEST";
    
    
    private TelemetryAllDropBoxComponent allDropBox;
    private TelemetryUserDropBoxComponent userDropBox;
    
    private DropBoxInspectionPolicy dropboxInspectionPolicy;
    
    @Override
    protected User getUser() {
    	User user = new User() {

            @Override
            public String getDisciplineId() {
                return TEST_GROUP;
            }

            @Override
            public String getUserId() {
                return TEST_USER;
            }

            @Override
            public User getValidUser(String userID) {
                return null;
            }
            
            @Override
            public boolean hasRole(String role) {
                return false;
            }
            
        };
        
        return user;
    }
    
    @Override
    protected void postSetup() {
        dropboxInspectionPolicy = new DropBoxInspectionPolicy();  
        (new PlatformAccess()).setPlatform(new MockPlatform());
        
        allDropBox = createTelemetryAllDropBoxComponent(genComponentID(), true, ADMIN_USER, ADMIN_GROUP);
        userDropBox = createTelemetryUserDropBoxComponent(genComponentID(), true, ADMIN_USER, ADMIN_GROUP);
        ComponentInitializer initializer = allDropBox.getCapability(ComponentInitializer.class);
        initializer.setComponentReferences(Collections.<AbstractComponent> singletonList(userDropBox));
    }
    
    @Test
    public void basicRegistrationTest() {
        Assert.assertNotNull(dropboxInspectionPolicy);
    }
    
    @Test
    public void basicComponentModelTest() {
        Assert.assertNotNull(allDropBox);
        Assert.assertNotNull(userDropBox);
    }
    
    @Test (dependsOnMethods = { "basicRegistrationTest", "basicComponentModelTest" })
    public void allDropBoxTest() {
        PolicyContext context = new PolicyContext();
        context.setProperty("TARGET", allDropBox);
        
        ExecutionResult result = dropboxInspectionPolicy.execute(context);
        Assert.assertNotNull(result);
        // Content in the Discipline's All Drop Boxes should always be invisible. 
        Assert.assertFalse(result.getStatus());
    }

    @Test (dependsOnMethods = { "basicRegistrationTest", "basicComponentModelTest" })
    public void userDropBoxTest() {
        PolicyContext context = new PolicyContext();
        context.setProperty("TARGET", userDropBox);
        
        ExecutionResult result = dropboxInspectionPolicy.execute(context);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.getStatus());
    }

    /*
     * Utility methods.
     */
    
    private static long counter = 0;
    private String genComponentID() {
        return Long.toString(System.currentTimeMillis() + counter++); 
    }
    
    private TelemetryAllDropBoxComponent createTelemetryAllDropBoxComponent(String id, boolean needPublish, String owner, String group) {
        TelemetryAllDropBoxComponent component = new TelemetryAllDropBoxComponent();
        component.setId(id);
        component.setShared(needPublish);
        component.setOwner(owner);
        return component;
    }
    
    private TelemetryUserDropBoxComponent createTelemetryUserDropBoxComponent(String id, boolean needPublish, String owner, String group) {
        TelemetryUserDropBoxComponent component = new TelemetryUserDropBoxComponent();
        component.setId(id);
        component.setShared(needPublish);
        component.setOwner(owner);
        return component;
    }

}
