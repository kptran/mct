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
 * AbstractMCTViewManifestationInfo.java Feb 10, 2009
 * 
 * This code is the property of the National Aeronautics and Space
 * Administration and was produced for the Mission Control Technologies (MCT)
 * project.
 * 
 */
package gov.nasa.arc.mct.canvas.persistence;

import gov.nasa.arc.mct.gui.NamingContext;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.UIManager;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Implements the rendering information used in the persistence of composed
 * views.
 */
@XmlType(name = "ManifestInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class MCTViewManifestationInfo implements NamingContext, Cloneable {
    private static final int DEFAULT_HEIGHT = 150;
    private static final int DEFAULT_WIDTH = 250;
    
    /** A sentinel value indicating that a point variable has not yet been set. */
    public final static Point UNDEFINED_POINT = new Point(0, 0);
    
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

    /** The width of the manifested view. */
    protected int sizeWidth = DEFAULT_WIDTH;
    /** The height of the manifested view. */
    protected int sizeHeight = DEFAULT_HEIGHT;
    /** The x location of the manifested view. */
    protected int startPointX = UNDEFINED_POINT.x;
    /** The y location of the manifested view. */
    protected int startPointY = UNDEFINED_POINT.y;
    /** Settings for all four borders. */
    protected List<Integer> borders;
    /** The border style to use. */
    protected Integer borderStyle;
    /** The color to use for the borders. */
    protected int borderColorRGB;

    /** If true, the manifested view should be shown with a title bar. */
    protected boolean hasTitlePanel = true;
    /** The title to show in the view title bar. */
    protected String panelTitle;

    /** The view role type we are manifesting. */
    protected String viewType;

    private boolean hasBorder = true;

    private String componentId;

    private Map<String, String> infoProperties = new HashMap<String, String>();

    private transient boolean dirty = false;

    /**
     * For jaxb internal use only.
     */
    public MCTViewManifestationInfo() {
        super();
    }

    /**
     * Creates new view manifestation information for the given view role type.
     * 
     * @param aViewType
     *            the view type we will manifest
     */
    public MCTViewManifestationInfo(String aViewType) {
        viewType = aViewType;
    }

    public Dimension getDimension() {
        return new Dimension(sizeWidth, sizeHeight);
    }

    public void setDimension(Dimension dimension) {
        this.sizeWidth = dimension.width;
        this.sizeHeight = dimension.height;
    }

    public void setStartPoint(Point p) {
        startPointX = p.x;
        startPointY = p.y;
    }

    public String getPanelTitle() {
        return panelTitle;
    }

    public void setPanelTitle(String s) {
        panelTitle = s;
    }
    
    @Override
    public String getContextualName() {
        return getPanelTitle();
    }
    
    @Override
    public NamingContext getParentContext() {
        return null;
    }

    public Point getStartPoint() {
        return new Point(startPointX, startPointY);
    }

    public String getManifestedViewType() {
        return this.viewType;
    }

    public void setManifestedViewType(String viewType) {
        this.viewType = viewType;
    }
    
    public final static MCTViewManifestationInfo NULL_VIEW_MANIFESTATION_INFO = new MCTViewManifestationInfo();

    public Object clone() throws CloneNotSupportedException {
        if (this == NULL_VIEW_MANIFESTATION_INFO) {
            return NULL_VIEW_MANIFESTATION_INFO;
        }
        
        MCTViewManifestationInfo clonedManifestationInfo = new MCTViewManifestationInfo();
        clonedManifestationInfo.componentId = this.componentId;
        clonedManifestationInfo.viewType = this.viewType;
        clonedManifestationInfo.sizeWidth = this.sizeWidth;
        clonedManifestationInfo.sizeHeight = this.sizeHeight;
        clonedManifestationInfo.startPointX = startPointX;
        clonedManifestationInfo.startPointY = startPointY;
        clonedManifestationInfo.hasBorder = this.hasBorder();
        clonedManifestationInfo.borderColorRGB = this.borderColorRGB;
        clonedManifestationInfo.hasTitlePanel = this.hasTitlePanel();
        clonedManifestationInfo.panelTitle = this.panelTitle;
        clonedManifestationInfo.infoProperties.putAll(this.infoProperties);
        
        return clonedManifestationInfo;
    }

    public boolean hasBorder() {
        return hasBorder;
    }

    public void setHasBorder(boolean setting) {
        this.hasBorder = setting;
    }

    public boolean hasTitlePanel() {
        return hasTitlePanel;
    }

    public void setHasTitlePanel(boolean setting) {
        hasTitlePanel = setting;
    }

    public List<Integer> getBorders() {
        if (borders == null) { // not initialized yet...
            borders = new ArrayList<Integer>();
            borders.add(BORDER_LEFT);
            borders.add(BORDER_RIGHT);
            borders.add(BORDER_TOP);
            borders.add(BORDER_BOTTOM);
        }

        return borders;
    }

    public void setBorders(List<Integer> borderList) {
        this.borders = borderList;
    }

    public boolean containsBorder(Integer border) {
        if (borders == null) {
            return false;
        }

        Iterator<Integer> iter = borders.iterator();
        while (iter.hasNext()) {
            Integer aBorder = iter.next();
            if (border.intValue() == aBorder.intValue()) {
                return true;
            }
        }

        return false;
    }

    public void removeBorder(Integer border) {
        if (borders != null) {
            while (borders.contains(border)) {
                borders.remove(border);
            }
        }
    }

    public void addBorder(Integer border) {
        if (borders == null)
            borders = new ArrayList<Integer>();
        if (!containsBorder(border))
            borders.add(border);

        setBorders(this.borders);
    }

    public Color getBorderColor() {
        return new Color(borderColorRGB);
    }

    public void setBorderColor(Color newColor) {
        this.borderColorRGB = newColor.getRGB();
    }

    public int getBorderStyle() {
        if (borderStyle == null)
            borderStyle = BORDER_STYLE_SINGLE;
        return borderStyle;
    }

    public void setBorderStyle(int newStyle) {
        this.borderStyle = newStyle;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public void addInfoProperty(String property, String value) {
        this.infoProperties.put(property, value);
    }

    public String getInfoProperty(String property) {
        return infoProperties.get(property);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
    
    public Color getColor(String name) {
        return UIManager.getColor(name);        
    }
}
