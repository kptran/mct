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
package gov.nasa.arc.mct.canvas.formatting;

import java.awt.Color;
import java.awt.font.TextAttribute;
import java.util.Hashtable;
import java.util.Map;

public class ControlAreaFormattingConstants {
    // these next items determine the grid size drawn on the DesignCanvas...
            
    public static final int NO_GRID_SIZE = 0;
    public static final int FINE_GRID_SIZE = 3;
    public static final int SMALL_GRID_SIZE = 9;
    public static final int MED_GRID_SIZE = 36;
    public static final int LARGE_GRID_SIZE = 72;
    public static final int MAJOR_GRID_LINE = 72;
    
    public static final int MAX_GRID_SIZE_IN_PIXELS = 1024;
    public static final Color MINOR_GRID_LINE_COLOR = new Color(204,204,204);
    public static final Color MAJOR_GRID_LINE_COLOR = new Color(153,153,153);
    
    // Items for use in drawing borders...
    
    public static final Color BorderColors[] = { new Color(000, 000, 000), // black
        new Color(000, 000, 255), // blue
        new Color(000, 128, 000), // Green
        new Color(032, 179, 170), // light sea green
        new Color(152, 251, 152), // Pale Green
        new Color(255, 140, 000), // Dark Orange
        new Color(255, 000, 255), // Magenta
        new Color(255, 69, 000), // Orange Red
        new Color(255, 215, 000), // Gold
        new Color(047, 79, 79), // Dark Slate Gray
        new Color(128, 128, 128), // Gray
        new Color(100, 149, 237), // Corn Flower blue
        new Color(000, 49, 042), // Brown
        new Color(000, 176, 176), // Aquamarine
        new Color(102, 051, 255) // Intermediate Violate blue
};
    
    public static enum BorderStyle { SINGLE, DOUBLE, DASHED, DOTS, MIXED;
        public static BorderStyle getBorderStyle(int i) {
            switch (i) {
            case 0: return SINGLE;
            case 1: return DOUBLE;
            case 2: return DASHED;
            case 3: return DOTS;
            case 4: return MIXED;
            default: return SINGLE;
            }
        }
    }
    public static final int NUMBER_BORDER_STYLES = BorderStyle.values().length;
    
    public final static String PANEL_BORDER_PROPERTY = "PANEL BORDER PROPERTY";
    
    public final static String PANEL_BORDER_STYLE_PROPERTY = "PANEL BORDER STYLE PROPERTY";
    
    public static enum PANEL_ZORDER { FRONT, BACK }
    
    public static final String PANEL_ORDER = "PANEL_ORDER";
    
    public static int UNDERLINE_OFF = -1;
    
    public static final Map<TextAttribute, Object> underlineMap = new Hashtable<TextAttribute, Object>();

    
    /**
     * @author dcberrio
     * Enumerated standard JVM font families
     */
    public enum JVMFontFamily {
        Dialog ("Dialog"),
        DialogInput ("Input"),
        Monospaced ("Monospaced"),
        SansSerif ("Sans Serif"),
        Serif ("Serif");
        
        private final String displayName;
        JVMFontFamily(String displayName) {
            this.displayName = displayName;
        }
        /** Get pretty name for controls
         * @return displayName
         */
        public String getDisplayName() {
            return displayName;
        }
    }
}
