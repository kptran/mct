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
package gov.nasa.arc.mct.gui;

import gov.nasa.arc.mct.components.ExtendedProperties;

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
// TODO: Replace concrete collection implementations with interfaces in
// member variable declarations and method parameters.
@XmlType(name = "ManifestInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public final class MCTViewManifestationInfoImpl implements MCTViewManifestationInfo, Cloneable {
    private static final long serialVersionUID = -8606703540641388677L;

    private static final int DEFAULT_HEIGHT = 150;
    private static final int DEFAULT_WIDTH = 250;

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
    
    /** The title font family to show in the view title bar. */
    protected String panelTitleFont;
    /** The title font size to show in the view title bar. */
    protected Integer panelTitleFontSize;
    /** The title font style to show in the view title bar. */
    protected Integer panelTitleFontStyle;
    /** The title font size to show in the view title bar. */
    protected Integer panelTitleFontForegroundColor;
    /** The title font size to show in the view title bar. */
    protected Integer panelTitleFontBackgroundColor;
    /** The title font text attribute to show in the view title bar. */
    protected Integer panelTitleFontTextAttribute;

    /** The view role type we are manifesting. */
    protected String manifestedViewRoleType;

    private boolean hasBorder = true;

    private String componentId;

    private Map<String, String> infoProperties = new HashMap<String, String>();
    
    private List<ExtendedProperties> ownedProperties;

    private transient boolean dirty = false;

    /**
     * For jaxb internal use only.
     */
    public MCTViewManifestationInfoImpl() {
        super();
    }

    /**
     * Creates new view manifestation information for the given view role type.
     * 
     * @param viewType
     *            the referenced view type
     */
    public MCTViewManifestationInfoImpl(String viewType) {
        this.manifestedViewRoleType = viewType;
    }

    @Override
    public Dimension getDimension() {
        return new Dimension(sizeWidth, sizeHeight);
    }

    @Override
    public void setDimension(Dimension dimension) {
        this.sizeWidth = dimension.width;
        this.sizeHeight = dimension.height;
    }

    @Override
    public void setStartPoint(Point p) {
        startPointX = p.x;
        startPointY = p.y;
    }

    @Override
    public String getPanelTitle() {
        return panelTitle;
    }
    @Override
    public String getPanelTitleFont() {
        return panelTitleFont;
    }
    @Override
    public Integer getPanelTitleFontSize() {
        return panelTitleFontSize;
    }
    @Override
    public void setPanelTitleFontSize(Integer i) {
        panelTitleFontSize = i;
    }
    @Override
    public Integer getPanelTitleFontStyle() {
        return panelTitleFontStyle;
    }
    @Override
    public void setPanelTitleFontStyle(Integer i) {
        panelTitleFontStyle = i;
    }
    @Override
    public Integer getPanelTitleFontUnderline() {
        return panelTitleFontTextAttribute;
    }
    @Override
    public void setPanelTitleFontUnderline(Integer i) {
        panelTitleFontTextAttribute = i;
    }
    @Override
    public Integer getPanelTitleFontForegroundColor() {
        return panelTitleFontForegroundColor;
    }
    @Override
    public void setPanelTitleFontForegroundColor(Integer s) {
        panelTitleFontForegroundColor = s;
    }
    @Override
    public Integer getPanelTitleFontBackgroundColor() {
        return panelTitleFontBackgroundColor;
    }
    @Override
    public void setPanelTitleFontBackgroundColor(Integer s) {
        panelTitleFontBackgroundColor = s;
    }

    @Override
    public void setPanelTitle(String s) {
        panelTitle = s;
    }
    @Override
    public void setPanelTitleFont(String s) {
        panelTitleFont = s;
    }
    
    @Override
    public String getContextualName() {
        if (!this.hasTitlePanel()) return null;
        return getPanelTitle();
    }
    
    @Override
    public NamingContext getParentContext() {
        return null;
    }

    @Override
    public Point getStartPoint() {
        return new Point(startPointX, startPointY);
    }

    @Override
    public String getManifestedViewType() {
        return this.manifestedViewRoleType;
    }

    @Override
    public void setManifestedViewType(String viewRoleTypeName) {
        this.manifestedViewRoleType = viewRoleTypeName;
    }

    private transient boolean visited = false;
    
    @Override
    public Object clone() {
        if (visited) {
            throw new RuntimeException("cycle detected during clone");
        }
        
        MCTViewManifestationInfoImpl clonedManifestationInfo = new MCTViewManifestationInfoImpl();
        try {
            visited = true;
            clonedManifestationInfo.componentId = this.componentId;
            clonedManifestationInfo.manifestedViewRoleType = this.manifestedViewRoleType;
            clonedManifestationInfo.sizeWidth = this.sizeWidth;
            clonedManifestationInfo.sizeHeight = this.sizeHeight;
            clonedManifestationInfo.startPointX = startPointX;
            clonedManifestationInfo.startPointY = startPointY;
            clonedManifestationInfo.hasBorder = this.hasBorder();
            clonedManifestationInfo.borderColorRGB = this.borderColorRGB;
            clonedManifestationInfo.hasTitlePanel = this.hasTitlePanel();
            clonedManifestationInfo.panelTitle = this.panelTitle;
            clonedManifestationInfo.panelTitleFont = this.panelTitleFont;
            clonedManifestationInfo.panelTitleFontSize = this.panelTitleFontSize;
            clonedManifestationInfo.panelTitleFontStyle = this.panelTitleFontStyle;
            clonedManifestationInfo.panelTitleFontTextAttribute = this.panelTitleFontTextAttribute;
            clonedManifestationInfo.panelTitleFontForegroundColor = this.panelTitleFontForegroundColor;
            clonedManifestationInfo.panelTitleFontBackgroundColor = this.panelTitleFontBackgroundColor;
            clonedManifestationInfo.infoProperties.putAll(this.infoProperties);
            clonedManifestationInfo.ownedProperties = ownedProperties == null ? null : new ArrayList<ExtendedProperties>(ownedProperties.size());
            if (ownedProperties != null) {
                for (ExtendedProperties ep:ownedProperties) {
                    clonedManifestationInfo.ownedProperties.add(ep.clone());
                }
            }
        } finally {
            visited = false;
        }
        
        return clonedManifestationInfo;
    }

    @Override
    public boolean hasBorder() {
        return hasBorder;
    }

    @Override
    public void setHasBorder(boolean setting) {
        this.hasBorder = setting;
    }

    @Override
    public boolean hasTitlePanel() {
        return hasTitlePanel;
    }

    @Override
    public void setHasTitlePanel(boolean setting) {
        hasTitlePanel = setting;
    }

    @Override
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

    @Override
    public void setBorders(List<Integer> borderList) {
        this.borders = borderList;
    }

    @Override
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

    @Override
    public void removeBorder(Integer border) {
        if (borders != null) {
            while (borders.contains(border)) {
                borders.remove(border);
            }
        }
    }

    @Override
    public void addBorder(Integer border) {
        if (borders == null)
            borders = new ArrayList<Integer>();
        if (!containsBorder(border))
            borders.add(border);

        setBorders(this.borders);
    }

    @Override
    public Color getBorderColor() {
        return new Color(borderColorRGB);
    }

    @Override
    public void setBorderColor(Color newColor) {
        this.borderColorRGB = newColor.getRGB();
    }

    @Override
    public int getBorderStyle() {
        if (borderStyle == null)
            borderStyle = BORDER_STYLE_SINGLE;
        return borderStyle;
    }

    @Override
    public void setBorderStyle(int newStyle) {
        this.borderStyle = newStyle;
    }

    @Override
    public String getComponentId() {
        return componentId;
    }

    @Override
    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    @Override
    public void addInfoProperty(String property, String value) {
        this.infoProperties.put(property, value);
    }

    @Override
    public String getInfoProperty(String property) {
        return infoProperties.get(property);
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
    
    @Override
    public Color getColor(String name) {
        return UIManager.getColor(name);        
    }

    @Override
    public List<ExtendedProperties> getOwnedProperties() {
       if (ownedProperties == null) {
           ownedProperties = new ArrayList<ExtendedProperties>();
       }
       return ownedProperties;
    }
}
