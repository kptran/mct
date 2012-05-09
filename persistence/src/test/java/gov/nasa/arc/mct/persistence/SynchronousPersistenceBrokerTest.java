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
 * SynchronousPersistenceBrokerTest.java Sep 28, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.persistence;

import gov.nasa.arc.mct.persistence.util.HibernateUtil;
import gov.nasa.arc.mct.persistmgr.SynchronousPersistenceBroker;
import gov.nasa.arc.mct.persistmgr.callback.PersistenceCompletedCallbackHandler;
import gov.nasa.arc.mct.telemetry.persistence.dao.DisciplineTest;
import gov.nasa.arc.mct.telemetry.persistence.dao.MCTUserTest;
import gov.nasa.arc.mct.telemetry.persistence.dao.TelemetryComponentTest;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.hibernate.Session;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SynchronousPersistenceBrokerTest {
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

    @AfterMethod
    public void shutdown() {
    }

    @Test
    public void loadChildChildrenTest() {
        List<TelemetryComponentTest> allComponents = synchronousBroker.loadChildren("parentComponent", null, TelemetryComponentTest.class, "parentComponent");
        Assert.assertEquals(allComponents.size(), 2);

        TelemetryComponentTest parent = synchronousBroker.loadById("1", TelemetryComponentTest.class, "1");
        List<TelemetryComponentTest> childComponents = synchronousBroker.loadChildren("parentComponent", parent, TelemetryComponentTest.class,
                        "parentComponent");
        Assert.assertEquals(childComponents.size(), 2);
    }

    @Test
    public void loadDisciplineTest() {
        List<DisciplineTest> allComponents = synchronousBroker.loadAll(DisciplineTest.class);
        Assert.assertEquals(allComponents.size(), 19);
    }

    @Test
    public void testLoadAllNullField() {
        List<TelemetryComponentTest> topLevelComponents = synchronousBroker.loadAll(TelemetryComponentTest.class, new String[] { "parentComponent" },
                        new Object[] { null }, null, null, null);
        Assert.assertEquals(topLevelComponents.size(), 2);
    }

    @Test
    public void loadByIdTest() {
        List<TelemetryComponentTest> allComponents = synchronousBroker.loadAll(TelemetryComponentTest.class);
        Assert.assertTrue(allComponents.size() > 0);

        TelemetryComponentTest compDAO = allComponents.get(0);
        String componentId = compDAO.getComponentId();

        TelemetryComponentTest compDAOFromId = synchronousBroker.loadById(componentId, TelemetryComponentTest.class, componentId);
        Assert.assertNotNull(compDAOFromId);
        Assert.assertEquals(componentId, compDAOFromId.getComponentId());

        TelemetryComponentTest compDAOFromId2 = synchronousBroker.loadById(componentId, TelemetryComponentTest.class, componentId);
        Assert.assertNotNull(compDAOFromId);
        Assert.assertEquals(componentId, compDAOFromId2.getComponentId());

        Assert.assertTrue(compDAOFromId.equals(compDAOFromId2));
    }

    @Test
    public void testSessionCache() {
        List<TelemetryComponentTest> allComponents = synchronousBroker.loadAll(TelemetryComponentTest.class);
        Assert.assertTrue(allComponents.size() > 0);

        TelemetryComponentTest compDAO = allComponents.get(0);
        String componentId = compDAO.getComponentId();

        synchronousBroker.startSession(componentId);
        try {
            TelemetryComponentTest compDAOFromId1 = synchronousBroker.loadById(componentId, TelemetryComponentTest.class, componentId);

            TelemetryComponentTest compDAOFromId2 = synchronousBroker.loadById(componentId, TelemetryComponentTest.class, componentId);

            Assert.assertSame(compDAOFromId1, compDAOFromId2);
        } finally {
            synchronousBroker.closeSession(componentId);
        }
    }
    
    @Test
    public void testSessionCloseAndOpen() {
        List<TelemetryComponentTest> allComponents = synchronousBroker.loadAll(TelemetryComponentTest.class);
        Assert.assertTrue(allComponents.size() > 0);

        TelemetryComponentTest compDAO = allComponents.get(0);
        String componentId = compDAO.getComponentId();

        TelemetryComponentTest compDAOFromId1 = null;
        TelemetryComponentTest compDAOFromId2 = null;
        synchronousBroker.startSession(componentId);
        try {
            compDAOFromId1 = synchronousBroker.loadById(componentId, TelemetryComponentTest.class, componentId);
        } finally {
            synchronousBroker.closeSession(componentId);
        }
        
        synchronousBroker.startSession(componentId);
        try {
            compDAOFromId2 = synchronousBroker.loadById(componentId, TelemetryComponentTest.class, componentId);
        } finally {
            synchronousBroker.closeSession(componentId);
        }

        Assert.assertFalse(compDAOFromId1 == compDAOFromId2);
    }

    @Test
    public void testMultipleSessions() {
        List<TelemetryComponentTest> allComponents = synchronousBroker.loadAll(TelemetryComponentTest.class);
        Assert.assertTrue(allComponents.size() > 0);

        TelemetryComponentTest compDAO1 = allComponents.get(0);
        String componentId1 = compDAO1.getComponentId();

        TelemetryComponentTest compDAO2 = allComponents.get(1);
        String componentId2 = compDAO2.getComponentId();

        synchronousBroker.startSession(componentId1);
        synchronousBroker.startSession(componentId2);
        TelemetryComponentTest compDAOFromId1a, compDAOFromId1b = null;
        TelemetryComponentTest compDAOFromId2a, compDAOFromId2b = null;
        try {
            compDAOFromId1a = synchronousBroker.lazilyLoad(componentId1, TelemetryComponentTest.class, componentId1);
        } finally {
            synchronousBroker.lazilyLoadCompleted(componentId1);
        }
        try {
            compDAOFromId2a = synchronousBroker.loadById(componentId2, TelemetryComponentTest.class, componentId1);
        } finally {
            synchronousBroker.lazilyLoadCompleted(componentId2);
        }

        Assert.assertFalse(compDAOFromId1a == compDAOFromId2a);

        // read from the session again
        try {
            compDAOFromId1b = synchronousBroker.lazilyLoad(componentId1, TelemetryComponentTest.class, componentId1);
        } finally {
            synchronousBroker.lazilyLoadCompleted(componentId1);
        }
        try {
            compDAOFromId2b = synchronousBroker.loadById(componentId2, TelemetryComponentTest.class, componentId1);
        } finally {
            synchronousBroker.lazilyLoadCompleted(componentId2);
        }

        Assert.assertSame(compDAOFromId1a, compDAOFromId1b);
        Assert.assertSame(compDAOFromId2a, compDAOFromId2b);

        synchronousBroker.closeSession(componentId1);
        synchronousBroker.closeSession(componentId2);

    }

    @Test
    public void synchronousSaveTest() {
        List<MCTUserTest> allMctUsers = synchronousBroker.loadAll(MCTUserTest.class, new String[] { "userId" }, new Object[] { "tmpUser" }, null, null, null);
        Assert.assertEquals(allMctUsers.size(), 0);

        MCTUserTest tmpUser = new MCTUserTest();
        tmpUser.setFirstName("tmpUser");
        tmpUser.setUserId("tmpUser");
        tmpUser.setLastName("tmpUser");
        synchronousBroker.save(tmpUser.getUserId(), tmpUser, null);

        allMctUsers = synchronousBroker.loadAll(MCTUserTest.class, new String[] { "userId" }, new Object[] { "tmpUser" }, null, null, null);
        Assert.assertEquals(allMctUsers.size(), 1);
    }

    @Test
    public void callbackTest() {
        List<MCTUserTest> allMctUsers = synchronousBroker.loadAll(MCTUserTest.class, new String[] { "userId" }, new Object[] { "tmpUser" }, null, null, null);
        Assert.assertEquals(allMctUsers.size(), 0);

        MCTUserTest tmpUser = new MCTUserTest();
        tmpUser.setFirstName("tmpUser");
        tmpUser.setUserId("tmpUser");
        tmpUser.setLastName("tmpUser");
        final AtomicBoolean called = new AtomicBoolean(false);
        synchronousBroker.save(tmpUser.getUserId(), tmpUser, new PersistenceCompletedCallbackHandler() {
            @Override
            public void saveCompleted() {
                Assert.assertFalse(called.get());
                called.set(true);
            }
        });

        allMctUsers = synchronousBroker.loadAll(MCTUserTest.class, new String[] { "userId" }, new Object[] { "tmpUser" }, null, null, null);
        Assert.assertEquals(allMctUsers.size(), 1);
        Assert.assertTrue(called.get());
    }

    @Test
    public void testRemoveBatch() {
        TelemetryComponentTest parent = synchronousBroker.loadById("2", TelemetryComponentTest.class, "2");
        List<TelemetryComponentTest> childComponents = synchronousBroker.loadChildren("parentComponent", parent, TelemetryComponentTest.class,
                        "parentComponent");
        Assert.assertEquals(childComponents.size(), 1);

        synchronousBroker.removeBatch("default", childComponents);

        childComponents = synchronousBroker.loadChildren("parentComponent", parent, TelemetryComponentTest.class, "parentComponent");
        Assert.assertEquals(childComponents.size(), 0);
    }

    @Test
    public void testLoadAllOrderedBy() {
        List<DisciplineTest> allComponents = synchronousBroker.loadAllOrderedBy(DisciplineTest.class, "description");

        for (int i = 0; i + 1 < allComponents.size(); ++i) {
            DisciplineTest first = allComponents.get(i);
            DisciplineTest second = allComponents.get(i + 1);
            Assert.assertTrue(first.getDescription().compareTo(second.getDescription()) <= 0);
        }
    }

    @Test
    public void testLoadByHQL() {
        List<Object> users = synchronousBroker.loadByHQL(MCTUserTest.class, "u", new String[] { "u.userId" }, new String[] { "u.userId" },
                        new Object[] { "amy" }, null, null, null);
        Assert.assertEquals(users.size(), 1);

        // Test w/ 2 property values
        users = synchronousBroker.loadByHQL(MCTUserTest.class, "u", new String[] { "u.userId" }, new String[] { "u.userId", "u.firstName" }, new Object[] {
                        "amy", "Amy" }, null, null, null);
        Assert.assertEquals(users.size(), 1);

        users = synchronousBroker.loadByHQL(MCTUserTest.class, "u", new String[] { "u.userId", "u.firstName" }, new String[] {}, new Object[] {}, null, null, null);
        Assert.assertEquals(users.size(), 3);
    }

    @Test
    public void testLazilyLoadDaoObject() {
        synchronousBroker.startSession("2");
        Session session = HibernateUtil.getCurrentSession("2");

        TelemetryComponentTest component = synchronousBroker.loadById("2", TelemetryComponentTest.class, "2");
        synchronousBroker.lazilyLoadCompleted("2");
        
        TelemetryComponentTest other = synchronousBroker.lazilyLoad("2", TelemetryComponentTest.class, "2");
        Assert.assertSame(component, other);
        synchronousBroker.lazilyLoadCompleted("2");

        TelemetryComponentTest third = synchronousBroker.lazilyLoad("2", component);
        Assert.assertSame(component, third);
        synchronousBroker.lazilyLoadCompleted("2");

        TelemetryComponentTest another = new TelemetryComponentTest();
        another.setComponentId("999");
        TelemetryComponentTest copy = synchronousBroker.lazilyLoad("2", another);
        Assert.assertSame(another, copy);
        synchronousBroker.lazilyLoadCompleted("2");
        
        synchronousBroker.closeSession("2");
        Assert.assertFalse(session.isOpen());
    }

    @Test
    public void testDelete() {
        List<MCTUserTest> allMctUsers = synchronousBroker.loadAll(MCTUserTest.class, new String[] { "userId" }, new Object[] { "amy" }, null, null, null);
        Assert.assertEquals(allMctUsers.size(), 1);

        MCTUserTest amy = allMctUsers.get(0);
        synchronousBroker.delete(amy.getUserId(), amy);

        allMctUsers = synchronousBroker.loadAll(MCTUserTest.class, new String[] { "userId" }, new Object[] { "amy" }, null, null, null);
        Assert.assertEquals(allMctUsers.size(), 0);

        // Try to delete again--should do nothing.
        synchronousBroker.delete(amy.getUserId(), amy);

        allMctUsers = synchronousBroker.loadAll(MCTUserTest.class, new String[] { "userId" }, new Object[] { "amy" }, null, null, null);
        Assert.assertEquals(allMctUsers.size(), 0);
    }

}
