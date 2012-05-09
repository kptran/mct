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
 * HibernateRetrievalTest.java Sep 28, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.persistence;

import gov.nasa.arc.mct.persistence.util.HibernateUtil;
import gov.nasa.arc.mct.telemetry.persistence.dao.DisciplineTest;
import gov.nasa.arc.mct.telemetry.persistence.dao.MCTUserTest;
import gov.nasa.arc.mct.telemetry.persistence.dao.TelemetryComponentTest;

import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class HibernateRetrievalTest {
    static {
        HibernateUtil.initSessionFactory("/hibernate_derby_test.cfg.xml");
    }

    @BeforeTest
    public void setUp() {
        Session session = HibernateUtil.getSession();
        try {
            TestDbLoader.load(session);
        } finally {
            session.close();
        }
    }

    @AfterTest
    public void shutdown() {
    }

    @SuppressWarnings("unchecked")
    @Test
    public void retrievalTest1() {
        Session session = HibernateUtil.getSession();
        try {
            List<MCTUserTest> allMctUsers = session.createCriteria(MCTUserTest.class).add(Restrictions.eq("userId", "alTomotsugu")).list();
            Assert.assertEquals(allMctUsers.size(), 1);

            MCTUserTest mctUser = allMctUsers.get(0);
            DisciplineTest discipline = mctUser.getDiscipline();
            Assert.assertEquals(discipline.getDisciplineId(), "ACO");

            Set<TelemetryComponentTest> components = mctUser.getComponents();
            Assert.assertEquals(components.size(), 2);
        } finally {
            session.close();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void retrievalTest2() {
        Session session = HibernateUtil.getSession();
        try {
            List<TelemetryComponentTest> allComponents = session.createCriteria(TelemetryComponentTest.class).add(Restrictions.eq("componentId", "1")).list();
            Assert.assertEquals(allComponents.size(), 1);

            TelemetryComponentTest component = allComponents.get(0);
            Set<TelemetryComponentTest> childs = component.getChildComponents();
            Assert.assertEquals(childs.size(), 2);

            for (TelemetryComponentTest child : childs) {
                if (child.getComponentId().equals("2")) {
                    Assert.assertEquals(child.getChildComponents().size(), 1);
                } else {
                    Assert.assertEquals(child.getChildComponents().size(), 0);
                }
            }
        } finally {
            session.close();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void retrievalTest3() {
        Session session = HibernateUtil.getSession();
        try {
            List<DisciplineTest> allDisciplines = session.createCriteria(DisciplineTest.class).list();
            Assert.assertEquals(allDisciplines.size(), 19);

            for (DisciplineTest discipline : allDisciplines) {
                String disciplineId = discipline.getDisciplineId();
                if ("ACO".equals(disciplineId)) {
                    Set<MCTUserTest> mctUsers = discipline.getUsers();
                    Assert.assertEquals(mctUsers.size(), 2);
                } else if ("CATO".equals(disciplineId)) {
                    Set<MCTUserTest> mctUsers = discipline.getUsers();
                    Assert.assertEquals(mctUsers.size(), 1);
                }
            }
        } finally {
            session.close();
        }
    }
}
