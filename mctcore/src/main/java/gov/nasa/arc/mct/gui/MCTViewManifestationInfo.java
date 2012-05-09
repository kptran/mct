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
/*
 * MCTViewManifestationInfo.java Jan 9, 2009
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.gui;

import gov.nasa.arc.mct.components.ExtendedProperties;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.Serializable;
import java.util.List;

/**
 * Defines the UI rendering info used in persistence of composed views. This class should not generally be used and is scheduled for removal.
 *
 */
public interface MCTViewManifestationInfo extends Serializable, NamingContext {
    /** The serial version ID. */
    static final long serialVersionUID = 4604437053179587058L;
    
    /** A sentinel value indicating that a point variable has not yet been set. */
    public final static Point UNDEFINED_POINT = new Point(0, 0);
    
    // constants for the Formatting features...
    
    /** A flag indicating the left border of a panel. */
    public final static int BORDER_LEFT = 0;
    /** A flag indicating the right border of a panel. */
    public final static int BORDER_RIGHT = 1;
    /** A flag indicating the top border of a panel. */
    public final static int BORDER_TOP = 2;
    /** A flag indicating the bottom border of a panel. */
    public final static int BORDER_BOTTOM = 3;
    
    /** A flag indicating that a border is a single-line border. */
    public final static int BORDER_STYLE_SINGLE = 0;
    /** A flag indicating that a border is a double-line border. */
    public final static int BORDER_STYLE_DOUBLE = 1;
    /** A flag indicating that a border is a dashed line border. */
    public final static int BORDER_STYLE_DASHED = 2;
    /** A flag indicating that a border is a dotted line border. */
    public final static int BORDER_STYLE_DOTS = 3;
    /** A flag indicating that a border is a mixed, dot-dash border. */
    public final static int BORDER_STYLE_MIXED = 4;
    
    /*
     * ----- The interface methods -----
     */
    
    /**
     * Gets the dimensions of the view manifestation.
     * 
     * @return the manifestation dimensions
     */
    public Dimension getDimension();
    
    /**
     * Sets the view manifestation dimensions.
     * 
     * @param dimension the new dimensions
     */
    public void setDimension(Dimension dimension);

    /**
     * Gets the upper left point for the view manifestation.
     * 
     * @return the start point
     */
    public Point getStartPoint();
    
    /**
     * Sets the upper left point for the view manifestation.
     * 
     * @param startPoint the new start point
     */
    public void setStartPoint(Point startPoint);
    
    /**
     * Gets the title string to show in the title panel of the view manifestation.
     * 
     * @return the title of the view manifestation
     */
    public String getPanelTitle();
    
    /**
     * Gets the title font to show in the title panel of the view manifestation.
     * 
     * @return the title font of the view manifestation
     */
    public String getPanelTitleFont();
    /**
     * Gets the title font size to show in the title panel of the view manifestation.
     * 
     * @return the title font size of the view manifestation
     */
    public Integer getPanelTitleFontSize();
    /**
     * Gets the title font style to show in the title panel of the view manifestation.
     * 
     * @return the title font style of the view manifestation
     */
    public Integer getPanelTitleFontStyle();
    /**
     * Gets the title font underline style to show in the title panel of the view manifestation.
     * 
     * @return the title font underline style of the view manifestation
     */
    public Integer getPanelTitleFontUnderline();
    /**
     * Gets the title font color to show in the title panel of the view manifestation.
     * 
     * @return the title font color of the view manifestation
     */
    public Integer getPanelTitleFontForegroundColor();
    /**
     * Gets the title background color to show in the title panel of the view manifestation.
     * 
     * @return the title background color of the view manifestation
     */
    public Integer getPanelTitleFontBackgroundColor();
    
    /**
     * Sets the title string for the view manifestation.
     * 
     * @param title the new title string
     */
    public void setPanelTitle(String title);
    
    /**
     * Sets the title font for the view manifestation.
     * 
     * @param font the new font string
     */
    public void setPanelTitleFont(String font);
    /**
     * Sets the title font size for the view manifestation.
     * 
     * @param fontSize the new font size
     */
    public void setPanelTitleFontSize(Integer fontSize);
    /**
     * Sets the title font sytle for the view manifestation.
     * 
     * @param fontStyle the new font style
     */
    public void setPanelTitleFontStyle(Integer fontStyle);
    /**
     * Sets the title underline style for the view manifestation.
     * 
     * @param fontStyle the new font underline style
     */
    public void setPanelTitleFontUnderline(Integer fontStyle);
    /**
     * Sets the title font color for the view manifestation.
     * 
     * @param color the new font color
     */
    public void setPanelTitleFontForegroundColor(Integer color);
    /**
     * Sets the title background color for the view manifestation.
     * 
     * @param color the new background color in sRGB
     */
    public void setPanelTitleFontBackgroundColor(Integer color);

    /**
     * Gets the type of the view role for this view manifestation.
     * 
     * @return the view role class
     */
    public String getManifestedViewType();
    
    /**
     * Sets the view role class for this view manifestation.
     * 
     * @param viewRoleTypeName the unique identified for this view
     */
    public void setManifestedViewType(String viewRoleTypeName);
    
    /**
     * Tests whether the view manifestation has a border shown.
     * 
     * @return true, if a border is shown
     */
    public boolean hasBorder();
    
    /**
     * Tests whether the view manifestation has a border on a given
     * side.
     * 
     * @param border the border side value to test
     * @return true, if the view manifestation has a border on the given side
     */
    public boolean containsBorder(Integer border);

    /**
     * Sets whether to show a border in the view manifestation.
     * 
     * @param showBorder true, if a border should be shown
     */
    public void setHasBorder(boolean showBorder);
    
    // formatting aspects of the panel...
    
    /**
     * Gets the border indications for the view manifestation.
     * 
     * @return a list of border indications
     */
    public List<Integer> getBorders(); // returns which borders are on/off...
    
    /**
     * Sets the border values for the view manifestation.
     * 
     * @param borderList a list of border values for each border
     */
    public void setBorders(List<Integer> borderList);
    
    /**
     * Remove a given border from the view manifestation. 
     * @param border the border to remove (top, right, bottom, left)
     */
    public void removeBorder(Integer border);
    
    /**
     * Adds a border if it isn't already present.
     * 
     * @param border the border to add (top, right, bottom, left)
     */
    public void addBorder(Integer border);
    
    /**
     * Gets the border color to use in drawing the borders.
     * 
     * @return the border color
     */
    public Color getBorderColor();
    
    /**
     * Sets the color to use in drawing the borders.
     * 
     * @param newColor the new border color
     */
    public void setBorderColor(Color newColor);
    
    /**
     * Gets the style to use in drawing the borders.
     * 
     * @return the border style
     */
    public int getBorderStyle();
    
    /**
     * Sets the style to use in drawing the borders.
     * 
     * @param newStyle the new border style
     */
    public void setBorderStyle(int newStyle);
    
    /**
     * Tests whether the title panel is displayed.
     * 
     * @return true, if the title panel is displayed
     */
    public boolean hasTitlePanel();
    
    /**
     * Sets whether the title panel is displayed.
     * 
     * @param hasPanel true, if the title panel should be displayed
     */
    public void setHasTitlePanel(boolean hasPanel);

    /**
     * Gets the component id of this manifestation info.
     * 
     * @return the component id
     */
    public String getComponentId();

    /**
     * Sets the component id of this manifestation info.
     * 
     * @param componentId the component id of this manifestation info
     */
    public void setComponentId(String componentId);
    
    /**
     * Add a property.
     * 
     * @param property property name
     * @param value property value
     */
    public void addInfoProperty(String property, String value);
    
    /**
     * Get a property value.
     * @param property property name
     * @return property value
     */
    public String getInfoProperty(String property);
    
    /**
     * Check if the manifestation info is dirty.
     * @return if the manifestation info is dirty.
     */
    public boolean isDirty();
    
    /**
     * Set the dirty flag of this manifestation info.
     * @param dirty dirty flag.
     */
    public void setDirty(boolean dirty);
    
    /**
     * Get a named color. The ManifestationInfo is responsible for 
     * deciding if this comes from the UIManager or somewhere else.
     * @param name the name of the color requested
     * @return a color which corresponds to the name
     */
    public Color getColor (String name);
    
    /**
     * Gets the nested properties that are owned by this view.
     * @return the extended properties that are owned by this view
     */
    public List<ExtendedProperties> getOwnedProperties();
    
}
