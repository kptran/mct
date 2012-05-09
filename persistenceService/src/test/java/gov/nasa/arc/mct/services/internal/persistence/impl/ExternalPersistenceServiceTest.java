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
package gov.nasa.arc.mct.services.internal.persistence.impl;

import gov.nasa.arc.mct.dao.service.CorePersistenceService;
import gov.nasa.arc.mct.dao.specifications.MCTUser;
import gov.nasa.arc.mct.persistence.PersistenceSystemTest;

import java.util.Collection;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ExternalPersistenceServiceTest extends PersistenceSystemTest {
	private ExternalPersistenceServiceImpl persistenceService;
	
	@Override
	protected void setupUser() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void postSetup() {
		persistenceService = new ExternalPersistenceServiceImpl();
	}
	
	@Test
	public void testGetAllDisciplines() {
		Collection<String> allDisciplines = CorePersistenceService.getAllDisciplines();
		Assert.assertEquals(allDisciplines.size(), 20);
	}
	
	@Test
	public void testGetUser() {
		MCTUser user = CorePersistenceService.getUser("0", "alTomotsugu");
		Assert.assertEquals(user.getUserId(), "alTomotsugu");
	}
	
	@Test
	public void testGetAllUsersOfDiscipline() {
		Collection<String> users = persistenceService.getAllUsersOfDiscipline("ACO");
		Assert.assertEquals(users.size(), 2);
	}

}
