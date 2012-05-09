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
import gov.nasa.arc.mct.lock.manager.MockLockManager;
import gov.nasa.arc.mct.persistence.util.HibernateUtil;
import gov.nasa.arc.mct.persistmgr.PersistenceBroker;
import gov.nasa.arc.mct.persistmgr.SynchronousPersistenceBroker;
import gov.nasa.arc.mct.platform.spi.MockPlatform;
import gov.nasa.arc.mct.platform.spi.Platform;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.registry.GlobalComponentRegistry;
import gov.nasa.arc.mct.util.IdGenerator;
import gov.nasa.arc.mct.util.property.MCTProperties;

import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class PersistenceSystemTest {
	protected PersistenceBroker persistenceBroker;
	private Platform mockPlatform = new MockPlatform();
	
	@BeforeMethod
	public void setup() {
		MockitoAnnotations.initMocks(this);
	    (new PlatformAccess()).setPlatform(mockPlatform);
	    
		persistenceBroker = SynchronousPersistenceBroker.getSynchronousPersistenceBroker();
		GlobalContext.getGlobalContext().setSynchronousPersistenceManager(persistenceBroker);
		GlobalContext.getGlobalContext().setLockManager(new MockLockManager());
		MCTProperties mctProperties = MCTProperties.DEFAULT_MCT_PROPERTIES;
        mctProperties.setProperty("hibernate_config_file", getHibernateConfigFile());
        HibernateUtil.initSessionFactory(mctProperties);
		
        IdGenerator.reset();

        setupUser();
		
		postSetup();
	}
	
	protected String getHibernateConfigFile() {
		return "persistence/hibernate_derby.cfg.xml";
	}
	
	@AfterMethod
	public void shutdown() {
		GlobalComponentRegistry.clearRegistry();
		(new PlatformAccess()).releasePlatform();
	}
	
	protected abstract void setupUser();
	protected abstract void postSetup();
}
