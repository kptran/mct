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
package gov.nasa.arc.mct.transaction;

import gov.nasa.arc.mct.persistence.TestDbLoader;
import gov.nasa.arc.mct.persistence.util.HibernateUtil;
import gov.nasa.arc.mct.persistmgr.SynchronousPersistenceBroker;
import gov.nasa.arc.mct.telemetry.persistence.dao.MCTUserTest;

import java.util.List;

import org.hibernate.Session;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MCTTransactionTest {
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
    public void synchronousTransactionTest() {
        MCTTransaction mctTransaction = new MCTTransaction();
        List<MCTUserTest> allMctUsers = synchronousBroker.loadAll(MCTUserTest.class, new String[] { "userId" }, new Object[] { "tmpUser" }, null, null, null);
        Assert.assertEquals(allMctUsers.size(), 0);

        MCTUserTest tmpUser = new MCTUserTest();
        tmpUser.setFirstName("tmpUser");
        tmpUser.setUserId("tmpUser");
        tmpUser.setLastName("tmpUser");
        synchronousBroker.save(tmpUser.getUserId(), tmpUser, null);
        
        allMctUsers = synchronousBroker.loadAll(MCTUserTest.class, new String[] { "userId" }, new Object[] { "tmpUser" }, null, null, null);
        Assert.assertEquals(allMctUsers.size(), 1);
        
        tmpUser.setUserId("tmpUser2");
        mctTransaction.pushChanges(tmpUser);
        Assert.assertTrue(mctTransaction.hasPendingChanges());
        mctTransaction.synchronousCommit();
        
        allMctUsers = synchronousBroker.loadAll(MCTUserTest.class, new String[] { "userId" }, new Object[] { "tmpUser2" }, null, null, null);
        Assert.assertEquals(allMctUsers.size(), 1);
        
        MCTUserTest user = allMctUsers.get(0);
        Assert.assertEquals(user.getUserId(), "tmpUser2");
    }
}
