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
package gov.nasa.arc.mct.gui.formatting;

import java.awt.Color;

public class ControlAreaFormattingConstants {

    /**
     * Set of formatting actions the control area and items in the control area can recognize.
     */
    public static enum FormattingAction { 
            SET_GRID_FINE,
            SET_GRID_SMALL,
            SET_GRID_MEDIUM,
            SET_GRID_LARGE,
            SET_GRID_NONE,
            
            SELECT_ALL_PANELS,
            
            MOVE_OBJECTS_UP,
            MOVE_OBJECTS_DOWN,
            MOVE_OBJECTS_LEFT,
            MOVE_OBJECTS_RIGHT,
            
            SNAP_OBJECTS,
            
            SET_SNAP_TO_GRID,
            SET_FREE_FORM,
            SET_SNAP_TO_OBJECTS,
            
            BRING_TO_FRONT,
            SEND_TO_BACK,
            
            ALIGN_LEFT,
            ALIGN_RIGHT,
            ALIGN_HCENTER,
            ALIGN_TOP,
            ALIGN_BOTTOM,
            ALIGN_VCENTER,
            
            SET_BORDER_STYLE_SINGLE,
            SET_BORDER_STYLE_DOUBLE,
            SET_BORDER_STYLE_DASHED,
            SET_BORDER_STYLE_DOTS,
            SET_BORDER_STYLE_MIXED,
            
            SET_BORDER_COLOR,
            
            SET_NORTH_BORDER_ON,
            SET_NORTH_BORDER_OFF,
            SET_SOUTH_BORDER_ON,
            SET_SOUTH_BORDER_OFF,
            SET_EAST_BORDER_ON,
            SET_EAST_BORDER_OFF,
            SET_WEST_BORDER_ON,
            SET_WEST_BORDER_OFF,
            SET_ALL_BORDERS_ON,
            SET_ALL_BORDERS_OFF,
            
            SET_VIEW_PANEL_TITLE_BAR_ON,
            SET_VIEW_PANEL_TITLE_BAR_OFF,
            
            };
            
    // These are the properties that the FormattingObject can inquire about...
           
    public static enum FormattingProperty {
        CANVAS_FREE_FORM,
        CANVAS_SNAP_TO_GRID,
        CANVAS_SNAP_TO_OBJECT,
        CANVAS_GRID_SIZE_FINE,
        CANVAS_GRID_SIZE_SMALL,
        CANVAS_GRID_SIZE_MEDIUM,
        CANVAS_GRID_SIZE_LARGE,
        CANVAS_GRID_OFF,
        
        // now some properties for the view panels..
        VIEW_PANEL_TITLE,
        VIEW_PANEL_LEFT_BORDER,
        VIEW_PANEL_RIGHT_BORDER,
        VIEW_PANEL_TOP_BORDER,
        VIEW_PANEL_BOTTOM_BORDER,
        VIEW_PANEL_NO_BORDERS,
        VIEW_PANEL_ALL_BORDERS,
        VIEW_PANEL_BORDER_STYLE_SINGLE,
        VIEW_PANEL_BORDER_STYLE_DOUBLE,
        VIEW_PANEL_BORDER_STYLE_DASHED,
        VIEW_PANEL_BORDER_STYLE_DOTS,
        VIEW_PANEL_BORDER_STYLE_MIXED,
        VIEW_PANEL_BORDER_STYLE,
        VIEW_PANEL_BORDER_COLOR,
        
        VIEW_PANEL_LOCATION_X,
        VIEW_PANEL_LOCATION_Y,
        VIEW_PANEL_WIDTH,
        VIEW_PANEL_HEIGHT,
    }
            
    // these next items determine the grid size drawn on the DesignCanvas...
            
    public static final int NO_GRID_PIXEL_SIZE = 1;
    public static final int FINE_GRID_SIZE = 6;
    public static final int SMALL_GRID_SIZE = 9;
    public static final int MED_GRID_SIZE = 36;
    public static final int LARGE_GRID_SIZE = 72;
    public static final int MAJOR_GRID_LINE = 72;
    
    public static final int MAX_GRID_SIZE_IN_PIXELS = 1024;
    public static final Color MINOR_GRID_LINE_COLOR = new Color(204,204,204);
    public static final Color MAJOR_GRID_LINE_COLOR = new Color(153,153,153);
    
    // Items for use in drawing borders...
    
    public static enum BorderStyle { SINGLE, DOUBLE, DASHED, DOTS, MIXED };
    public static final int NUMBER_BORDER_STYLES = BorderStyle.values().length;
    
    public final static Integer BORDER_STYLE_SINGLE = 0;
    public final static Integer BORDER_STYLE_DOUBLE = 1;
    public final static Integer BORDER_STYLE_DASHED = 2;
    public final static Integer BORDER_STYLE_DOTS = 3;
    public final static Integer BORDER_STYLE_MIXED = 4;
    
          
}
