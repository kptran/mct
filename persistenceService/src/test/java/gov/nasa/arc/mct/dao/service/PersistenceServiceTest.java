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
package gov.nasa.arc.mct.dao.service;

import gov.nasa.arc.mct.dao.specifications.ComponentSpecification;
import gov.nasa.arc.mct.dao.specifications.MCTUser;
import gov.nasa.arc.mct.persistence.PersistenceSystemTest;
import gov.nasa.arc.mct.platform.spi.Platform;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.services.internal.component.User;

import java.util.List;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PersistenceServiceTest extends PersistenceSystemTest {
	@Mock private Platform mockPlatform;
	
	@Override
	protected void setupUser() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void postSetup() {
		MockitoAnnotations.initMocks(this);
		
        (new PlatformAccess()).setPlatform(mockPlatform);
	}

    @Test
    public void testLoadMineRootComponents() {
        String testUser = "alTomotsugu";
        List<MCTUser> mctUsers = persistenceBroker.loadAll(MCTUser.class, new String[] { "userId" }, new Object[] { testUser }, null, null, null);
        Assert.assertNotNull(mctUsers);
        MCTUser mctUser = mctUsers.get(0);
        Assert.assertNotNull(mctUser);
        
        List<ComponentSpecification> components = CorePersistenceService.loadUserMineRootComponents(mctUser);
        Assert.assertNotNull(components);
    }
    
    @Test
    public void testMineTelemetryMetaFilter() {
        String testUser = "amy";
        List<MCTUser> mctUsers = persistenceBroker.loadAll(MCTUser.class, new String[] { "userId" }, new Object[] { testUser }, null, null, null);
        Assert.assertNotNull(mctUsers);
        MCTUser mctUser = mctUsers.get(0);
        Assert.assertNotNull(mctUser);
    }

    @Test
    public void testDisciplineTelemetryMetaFilter() {
        String testUser = "amy";
        List<MCTUser> mctUsers = persistenceBroker.loadAll(MCTUser.class, new String[] { "userId" }, new Object[] { testUser }, null, null, null);
        Assert.assertNotNull(mctUsers);
        MCTUser mctUser = mctUsers.get(0);
        Assert.assertNotNull(mctUser);
    }
    
    @Test
    public void testGetUsersFromDisciplines() {
    	List<User> users = CorePersistenceService.getUsersFromDisciplines("ACO", "CATO");
    	Assert.assertNotNull(users);
    	Assert.assertEquals(users.size(), 3);
    }
    
    @Test
    public void testPatternCoversion() {
        String puiPattern = "Z*";
        String opsPattern = "Air*";
        
        Assert.assertEquals(CorePersistenceService.formatPUIPattern(puiPattern), puiPattern.replace('*', '%'));
        Assert.assertEquals(CorePersistenceService.formatOpsPattern(opsPattern), opsPattern.replace('*', '%'));
    }
    
}
