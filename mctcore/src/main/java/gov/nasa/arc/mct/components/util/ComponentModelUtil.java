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
package gov.nasa.arc.mct.components.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Component model utility for comparing asymmetric set differences.
 *
 */
public final class ComponentModelUtil {
    
    /**
     * Private constructor.
     */
    private ComponentModelUtil() { }
    
    /**
     * Computes the set of elements contained in either of the two specified sets but not in both.
     * That is, set diff1 = set1 - set2 and diff2 = set2 - set1.
     * @param <T> - static generic type reference
     * @param set1 - First set.
     * @param set2 - Second set.
     * @param diff1 pre-condition: non null; post-condition: contains the asymmetric set difference set1 - set2.
     * @param diff2 pre-condition: non null; post-condition: contains the asymmetric set difference set2 - set1.
     * @param set pre-condition: non-null; post-condition:unspecified. This Set allows a comparator to be specified. 
     * 
     */
    public static<T> void computeAsymmetricSetDifferences(final Collection<T> set1, final Collection<T> set2, final Collection<T> diff1, final Collection<T> diff2, final Collection<T> set) {
        if (set1 == null && set2 == null)
            return;
        
        if (set1 == null) {
            diff2.addAll(set2);
            return;
        }
        
        if (set2 == null) {
            diff1.addAll(set1);
            return;
        }
        set.removeAll(set2);
        diff1.addAll(set);
        
        set.clear();
        set.addAll(set2);
        set.removeAll(set1);
        diff2.addAll(set);
    }
    
    /**
     * Computes the set of elements contained in either of the two specified sets but not in both.
     * That is, set diff1 = set1 - set2 and diff2 = set2 - set1.
     * @param <T> - static generic type reference
     * @param set1 - First set.
     * @param set2 - Second set.
     * @param diff1 pre-condition: non null; post-condition: contains the asymmetric set difference set1 - set2.
     * @param diff2 pre-condition: non null; post-condition: contains the asymmetric set difference set2 - set1.
     */
    public static<T> void computeAsymmetricSetDifferences(final Collection<T> set1, final Collection<T> set2, final Collection<T> diff1, final Collection<T> diff2) {
        ComponentModelUtil.computeAsymmetricSetDifferences(set1, set2, diff1, diff2, set1 == null ? null : new ArrayList<T>(set1));
    }
}
