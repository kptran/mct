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
package gov.nasa.arc.mct.dao.specifications;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class MCTUserTest {

    @Test
    public void testGettersSetters() {
        MCTUser user = new MCTUser();
        
        assertNull(user.getId());
        user.setUserId("user");
        assertEquals(user.getId(), "user");
        
        Discipline d = new Discipline();
        d.setDisciplineId("discipline1");
        assertNull(user.getDiscipline());
        user.setDiscipline(d);
        assertSame(user.getDiscipline(), d);
        assertEquals(user.getDisciplineId(), "discipline1");
        
        // Should be version zero, since we haven't done any changes.
        assertEquals(user.getVersion(), 0);
    }
    
    @Test
    public void testEquals() {
        MCTUser user1a = new MCTUser();
        user1a.setUserId("1");
        MCTUser user1b = new MCTUser();
        user1b.setUserId("1");
        MCTUser user2 = new MCTUser();
        user2.setUserId("2");
        Object other = new Object();
        
        assertTrue(user1a.equals(user1a)); // Reflexive
        assertTrue(user1a.equals(user1b)); // Symmetric
        assertTrue(user1b.equals(user1a));
        assertFalse(user1a.equals(user2)); // Not equal if IDs don't match
        assertFalse(user2.equals(user1a));
        assertFalse(user1a.equals(other)); // Not equal if wrong type
    }
    
}
