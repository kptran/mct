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
package gov.nasa.arc.mct.util;

import java.util.Iterator;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LinkedHashSetTest {
    private LinkedHashSet<String> strSet;
    
    @BeforeMethod
    public void setup() {
        strSet = new LinkedHashSet<String>();
    }
    
    @Test()
    public void addTest() {
        String[] data = getStringTestData();
        for (String str: data) {
            strSet.add(str);
        }
        
        int index = data.length-1;
        for (String str: strSet) {
            Assert.assertEquals(str, data[index--]);
        }
    }
    
    @Test()
    public void offerLastTest() {
        String[] data = getStringTestData();
        for (String str: data) {
            strSet.offerLast(str);
        }
        int index = 0;
        for (String str: strSet) {
            Assert.assertEquals(str, data[index++]);
        }
    }
    
    @Test()
    public void removeTest() {
        String[] data = getStringTestData();
        for (String str: data) {
            strSet.offerLast(str);
        }
        Assert.assertEquals(strSet.size(), 3);
        
        strSet.remove(data[1]);
        Assert.assertEquals(strSet.size(), 2);
        
        Iterator<String> it = strSet.iterator();
        Assert.assertTrue(it.hasNext());
        
        String str = it.next();
        Assert.assertEquals(str, data[0]);
        
        Assert.assertTrue(it.hasNext());
        str = it.next();
        Assert.assertEquals(str, data[2]);
        
        Assert.assertFalse(it.hasNext());
    }
    
    public String[] getStringTestData() {
        return new String[] { "str1", "str2", "str3" };
    }
}
