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
package gov.nasa.arc.mct.persistence;

import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.identitymgr.MockIdentityManager;
import gov.nasa.arc.mct.lock.manager.MockLockManager;
import gov.nasa.arc.mct.platform.spi.MockPlatform;
import gov.nasa.arc.mct.platform.spi.Platform;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.registry.GlobalComponentRegistry;
import gov.nasa.arc.mct.services.internal.component.User;
import gov.nasa.arc.mct.util.IdGenerator;

import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class PersistenceUnitTest {
	protected MockPersistenceBroker persistenceBroker;
	protected Platform platform;
	protected String sampleReconID = "cyclec";
	
	@BeforeMethod
	public void setup() {
		MockitoAnnotations.initMocks(this);
	
		platform = new MockPlatform();
		(new PlatformAccess()).setPlatform(platform);
		persistenceBroker = MockPersistenceBroker.getInstance();
		GlobalContext.getGlobalContext().setSynchronousPersistenceManager(persistenceBroker);
		GlobalContext.getGlobalContext().setLockManager(new MockLockManager());
		GlobalContext.getGlobalContext().setIdManager(new MockIdentityManager());
		GlobalContext.getGlobalContext().setPersistenceTransaction(new PersistenceTransaction() {
			@Override
			public String getCurrentTransactionId(String sessionId) {
				return sessionId;
			}
		});
		User user = getUser();
		if (user != null) {
			GlobalContext.getGlobalContext().switchUser(user, null);
		}
		
		IdGenerator.reset();
		
        System.setProperty("MCCreconID", sampleReconID);
		
		postSetup();
	}
	
	@AfterMethod
	public void shutdown() {
		GlobalComponentRegistry.clearRegistry();
		persistenceBroker.reset();
		(new PlatformAccess()).releasePlatform();
	}
	
    protected User getUser() {
        // TODO Auto-generated method stub
        return null;
    }
    
    protected void postSetup() {
        // TODO Auto-generated method stub
        
    }
    

	
}
