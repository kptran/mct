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
 * 
 */
package gov.nasa.arc.mct.gui.util;

import java.awt.Dimension;

/**
 * Standard default values for various GUI properties.
 * These should conform to the style guide.
 * 
 * @author atomotsu
 *
 */
public class GUIDefaults {
    private static final int OK_BUTTON_HEIGHT = 23;
    private static final int OK_BUTTON_WIDTH = 85;

    /**
     * OK Button dimensions.
     */
    public static final Dimension OK_BUTTON_SIZE = new Dimension(OK_BUTTON_WIDTH, OK_BUTTON_HEIGHT);

    /**
     * Margins around inside edges of panels.
     */
    public static final int LEFT_MARGIN = 12;
    
    /** Default Right margin. */
    public static final int RIGHT_MARGIN = 11;
    
    /** Default top margin. */
    public static final int TOP_MARGIN = 12;
    
    /** Default bottom margin. */
    public static final int BOTTOM_MARGIN = 11;

}
