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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.testng.annotations.Test;

public class WeakHashSetTest {

    @Test(expectedExceptions = NoSuchElementException.class)
    public void emptySetTest() {
        WeakHashSet<MyReclaimable> hs = new WeakHashSet<MyReclaimable>();
        assertEquals(hs.size(), 0);

        Iterator<MyReclaimable> it = hs.iterator();
        it.next();
    }

    @Test
    public void fromExistingSetTest() {
        WeakHashSet<MyReclaimable> hs;

        Set<MyReclaimable> s = new HashSet<MyReclaimable>();
        hs = new WeakHashSet<MyReclaimable>(s);
        assertEquals(hs.size(), 0);
        assertTrue(hs.isEmpty());

        s.add(new MyReclaimable("hello"));
        hs = new WeakHashSet<MyReclaimable>(s);
        assertEquals(hs.size(), 1);
        assertTrue(!hs.isEmpty());
        assertTrue(hs.contains(new MyReclaimable("hello")));

        Iterator<MyReclaimable> it = hs.iterator();
        boolean found = false;
        while (it.hasNext()) {
            MyReclaimable value = it.next();
            if (value.equals(new MyReclaimable("hello"))) {
                found = true;
            }
        }
        assertTrue(found);

        assertTrue(!hs.add(new MyReclaimable("hello")));
        assertEquals(hs.size(), 1);

        assertTrue(hs.add(new MyReclaimable("goodbye")));
        assertEquals(hs.size(), 2);

        assertTrue(!hs.add(new MyReclaimable("goodbye")));
        assertEquals(hs.size(), 2);

        assertTrue(hs.remove(new MyReclaimable("goodbye")));
        assertEquals(hs.size(), 1);

        assertFalse(hs.remove(new MyReclaimable("goodbye")));
        assertEquals(hs.size(), 1);

        hs.clear();
        assertEquals(hs.size(), 0);

        s.clear();
    }

    @Test
    public void gcTest() throws Exception {
        WeakHashSet<MyReclaimable> hs;

        Set<MyReclaimable> s = new HashSet<MyReclaimable>();
        s.add(new MyReclaimable("one"));
        s.add(new MyReclaimable("two"));
        s.add(new MyReclaimable("three"));

        hs = new WeakHashSet<MyReclaimable>(s);
        assertEquals(hs.size(), 3);
        assertFalse(hs.isEmpty());

        s.clear();
        Thread.sleep(2000);
        gc();

        Thread.sleep(2000);
        assertEquals(hs.size(), 0);
    }

    private void gc() {
        Runtime rt = Runtime.getRuntime();
        for (int i = 0; i < 3; i++) {
            try {
                allocateMemory(1000000);
            } catch (Throwable th) {
            }
            for (int j = 0; j < 3; j++)
                rt.gc();
        }
        rt.runFinalization();
        try {
            Thread.sleep(50);
        } catch (Throwable th) { // 
        }
    }

    private void allocateMemory(int memAmount) {
        byte[] big = new byte[memAmount];
        // Fight against clever compilers/JVMs that may not allocate
        // unless we actually use the elements of the array
        int total = 0;
        for (int i = 0; i < 10; i++) {
            // we don't touch all the elements, would take too long.
            if (i % 2 == 0)
                total += big[i];
            else
                total -= big[i];
        }
    }

    private static class MyReclaimable {
        private final String str;

        public MyReclaimable(String str) {
            this.str = str;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof MyReclaimable)) {
                return false;
            }

            MyReclaimable m = (MyReclaimable) obj;
            return str.equals(m.str);
        }

        @Override
        public int hashCode() {
            return str.hashCode();
        }
    }
}
