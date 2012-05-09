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
 * RStrings.java Oct 7, 2008
 *
 * This code is property of the National Aeronautics and Space Administration and was
 * produced for the Mission Control Technologies (MCT) Project.
 *
 */
package gov.nasa.arc.mct.util.resource;

import java.util.ResourceBundle;

/**
 * @author atomo
 *
 */
public class RStrings {
    private final static ResourceBundle bundle = BundleFactory.getBundle("properties.RStrings");

    public final static String CANCEL = bundle.getString("CANCEL");
    public final static String CLOSE = bundle.getString("CLOSE");
    public final static String CREATE = bundle.getString("CREATE");
    public final static String OK = bundle.getString("OK");
    public final static String LAYOUT_C = bundle.getString("LAYOUT_C");
    public final static String SHOW = bundle.getString("SHOW");
    public final static String SHOW_ALL = bundle.getString("SHOW_ALL");
    public final static String EXTENDED_CONTROL_AREA = bundle.getString("EXTENDED_CONTROL_AREA");
    public final static String LEGENDS = bundle.getString("LEGENDS");
    public final static String LINES = bundle.getString("LINES");
    public final static String NAMES = bundle.getString("NAMES");
    public final static String PUIS= bundle.getString("PUIS");
    public final static String UNITS = bundle.getString("UNITS");
    public final static String PRINT_DOT = bundle.getString("PRINT_DOT");
    public final static String EXPORT_DOT = bundle.getString("EXPORT_DOT");
    public final static String CLEAR_PLOT = bundle.getString("CLEAR_PLOT");

    /**
     * Use this method if new resource strings are to be added without changing this class.
     * @param key the key to the desired resource string
     * @return the string corresponding to the key
     */
    public static String get(String key) {
        return bundle.getString(key);
    }
}
