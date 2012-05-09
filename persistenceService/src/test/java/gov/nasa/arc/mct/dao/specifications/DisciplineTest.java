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

public class DisciplineTest {

    @Test
    public void testGettersSetters() {
        Discipline d = new Discipline();
        
        assertNull(d.getId());
        assertNull(d.getDisciplineId());
        
        d.setDisciplineId("abc");
        assertEquals(d.getId(), "abc");
        assertEquals(d.getDisciplineId(), "abc");
        assertEquals(d.hashCode(), "abc".hashCode());
    }
    
    @Test
    public void testEquals() {
        Discipline d1 = new Discipline();
        Discipline d2 = new Discipline();
        Discipline d3 = new Discipline();
        
        d1.setDisciplineId("1");
        d2.setDisciplineId("1");
        d3.setDisciplineId("2");
        
        assertFalse(d1.equals(new Object()));
        assertTrue(d1.equals(d1));
        assertTrue(d1.equals(d2));
        assertFalse(d1.equals(d3));
    }
    
}
