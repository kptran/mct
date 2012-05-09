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
package gov.nasa.arc.mct.persistmgr;

import gov.nasa.arc.mct.persistence.TestDbLoader;
import gov.nasa.arc.mct.persistence.util.HibernateUtil;
import gov.nasa.arc.mct.telemetry.persistence.dao.TelemetryComponentTest;

import java.util.List;

import org.hibernate.Session;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PersistenceTransactionTest {

    private SynchronousPersistenceBroker synchronousBroker;

    static {
        HibernateUtil.initSessionFactory("/hibernate_derby_test.cfg.xml");
    }

    @BeforeMethod
    public void setUp() {
        synchronousBroker = SynchronousPersistenceBroker.getSynchronousPersistenceBroker();
        Session session = HibernateUtil.getSession();
        try {
            TestDbLoader.load(session);
        } finally {
            session.close();
        }
    }

    /**
     * Test that a transaction is rolled back if we try to insert
     * a duplicate component ID. This is really a test of PersistenceTransaction,
     * but is included here because we need a test DB set up. At some point it
     * would be good to refactor this to be in a test for PersistenceTransaction,
     * and to derive from PersistenceSystemTest. However, adding a dependency on
     * the "tests" project introduces a circular dependency that causes compile
     * errors.
     */
    @Test
    public void testRollback() {
        List<TelemetryComponentTest> components = synchronousBroker.loadAll(TelemetryComponentTest.class);
        Assert.assertTrue(components.size() > 0);
        
        final TelemetryComponentTest newComponent = new TelemetryComponentTest();
        newComponent.setComponentId("2");
        newComponent.setComponentType("gov.nasa.arc.mct.components.collection.CollectionComponent");
        newComponent.setName("New component");
        
        try {
            new PersistenceTransaction() {
                public void perform(Session session) {
                    session.save(newComponent);
                }
            }.run();
            Assert.fail("Inserting duplicate component ID did not raise exception");
        } catch (Exception ex) {
            // ignore--expected
        }
        
        List<TelemetryComponentTest> components2 = synchronousBroker.loadAll(TelemetryComponentTest.class);
        Assert.assertEquals(components.size(), components2.size());
    }
    
}
