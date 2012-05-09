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
package gov.nasa.arc.mct.canvas.panel;

import gov.nasa.arc.mct.canvas.formatting.ControlAreaFormattingConstants.BorderStyle;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;

public class PanelBorder extends AbstractBorder {
    private static final long serialVersionUID = -4591457942438896139L;

    public static final byte NO_BORDER = 0;
    public static final byte NORTH_BORDER = 0x1;
    public static final byte SOUTH_BORDER = 0x2;
    public static final byte EAST_BORDER = 0x4;
    public static final byte WEST_BORDER = 0x8;
    public static final byte ALL_BORDERS = NORTH_BORDER | SOUTH_BORDER | EAST_BORDER | WEST_BORDER;

    /** The border color. */
    private Color borderColor = Color.BLACK;
    private Color defaultBorderColor = Color.DARK_GRAY;

    private static final int borderWidth = 1;
    private static final int borderInsetValue = 2;

    private byte borders;
    private BorderStyle borderStyle = BorderStyle.SINGLE;
    private int offset = 0;
    
    /**
     * Creates a new border with the default border color, black. Borders will
     * be hidden on all four sides.
     * 
     * @param initialBorderState the initial border state.
     */
    public PanelBorder(byte initialBorderState) {
        this(Color.BLACK);
        this.borders = initialBorderState;
    }

    /**
     * Creates a new border with the given border color. Borders will be shown
     * on all four sides.
     * 
     * @param color
     *            the border color to use
     */
    public PanelBorder(Color color) {
        borderColor = color;

        borders = ALL_BORDERS;
    }

    /**
     * Gets a list of borders for all four sides. The list holds borders for the
     * top, bott, right, and left sides, in that order.
     * 
     * @return a list of borders
     */
    public byte getBorders() {
        return borders;
    }

    @Override
    public void paintBorder(Component c, Graphics gInit, int x, int y, int width, int height) {
        Graphics g = gInit.create();
        g.setColor(visibleBorderColor(borderColor, c.getBackground()));
        if (hasNorthBorder(this.borders)) {
            drawBorder(g, x+offset, y, x + width - 1, y, this.borderStyle);
        }
        if (hasSouthBorder(this.borders)) {
            drawBorder(g, x+offset, y + height-1, x+width, y + height-1,
                            this.borderStyle);
        }
        if (hasEastBorder(this.borders)) {
            drawBorder(g, x + width - 1, y, x + width - 1, y + height - 1, this.borderStyle);
        }
        if (hasWestBorder(this.borders)) {
            drawBorder(g, x, y + height, x, y, this.borderStyle);
        }
        g.dispose();

    }

    private void drawBorder(Graphics gOrig, int x1, int y1, int x2, int y2,
                    BorderStyle borderStyle) {
        float[] dashPattern;

        Graphics2D g = (Graphics2D) gOrig;
        g.setStroke(new BasicStroke(borderWidth));

        switch (borderStyle) {
        case SINGLE:
            g.setStroke(new BasicStroke(0));
            g.drawLine(x1, y1, x2, y2);
            break;
        case DOUBLE:
            g.setStroke(new BasicStroke(borderWidth*3));
            g.drawLine(x1, y1, x2, y2);
            break;
        case DASHED:
            dashPattern = new float[] { 5, 5 };
            g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
                                          BasicStroke.JOIN_MITER, 10,
                                          dashPattern, 0));
            g.drawLine(x1, y1, x2, y2);
            
            break;
        case DOTS:
            dashPattern = new float[] { 2, 5 };
            g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
                                          BasicStroke.JOIN_MITER, 10,
                                          dashPattern, 0));
            g.drawLine(x1, y1, x2, y2);
            
            break;
        case MIXED:
            dashPattern = new float[] {10, 3, 3, 3 };
            g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
                                          BasicStroke.JOIN_MITER, 10,
                                          dashPattern, 0));
            g.drawLine(x1, y1, x2, y2);

            break;
        }
    }

    /**
     * Sets the style for drawing any borders that are shown.
     * 
     * @param newStyle
     *            the new border style
     */
    public void setBorderStyle(int newStyle) {
        this.borderStyle = BorderStyle.getBorderStyle(newStyle);
    }
    
    public void setBorderStyle(BorderStyle newStyle) {
        this.borderStyle = newStyle;
    }

    /**
     * Sets the border drawing state for a specified border.
     * 
     * @param borderState
     *            the border state
     */
    public void setBorderState(byte newBorderState) {
        removeAllBorders();

        if (hasNorthBorder(newBorderState)) {
            this.addBorderState(NORTH_BORDER);
        }
        if (hasSouthBorder(newBorderState)) {
            this.addBorderState(SOUTH_BORDER);
        }
        if (hasEastBorder(newBorderState)) {
            this.addBorderState(EAST_BORDER);
        }
        if (hasWestBorder(newBorderState)) {
            this.addBorderState(WEST_BORDER);
        }
    }

    /**
     * Adds a border to the display.
     * 
     * @param newBorder
     *            the border to add
     */
    public void addBorderState(byte newBorder) {
        this.borders = (byte) (this.borders | newBorder);
    }

    /**
     * Removes the border on a specified side.
     * 
     * @param border
     *            the border to remove
     */
    public void removeBorderState(byte removeBorder) {
        this.borders = (byte) (this.borders & ~removeBorder);
    }

    /**
     * Removes all borders from view.
     */
    public void removeAllBorders() {
        this.borders = 0;
    }

    @Override
    public Insets getBorderInsets(Component c) {
        int top = 0;
        int left = 0;
        int rite = 0;
        int bottom = 0;

        int addlOffset = borderInsetValue;

        if (borderStyle == BorderStyle.DOUBLE) { // have to give more room in
                                                 // insets...
            addlOffset = (borderInsetValue * 2);
        }

        if (hasNorthBorder(this.borders)) {
            top = addlOffset;
        }
        if (hasSouthBorder(this.borders)) {
            bottom = addlOffset;
        }
        if (hasEastBorder(this.borders)) {
            rite = addlOffset;
        }
        if (hasWestBorder(this.borders)) {
            left = addlOffset;
        }
        return new Insets(top, left, bottom, rite);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    /**
     * Tests whether a particular border is active.
     * 
     * @param fp
     *            the border formatting property
     * @return true, if the border is active
     */
    public boolean isBorderActive(byte border) {
        switch (border) {
        case NORTH_BORDER:
            return hasNorthBorder(this.borders);
        case EAST_BORDER:
            return hasEastBorder(this.borders);
        case WEST_BORDER:
            return hasWestBorder(this.borders);
        case SOUTH_BORDER:
            return hasSouthBorder(this.borders);
        case NO_BORDER:
                return this.borders == 0;
        case ALL_BORDERS:
                return this.borders == ALL_BORDERS;
        }

        return false;
    }

    /**
     * Gets the border style used for drawing all borders.
     * 
     * @return the current border style
     */
    public int getBorderStyle() {
        return this.borderStyle.ordinal();
    }
    
    public Color getBorderColor() {
        return this.borderColor;
    }
    
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public static boolean hasNorthBorder(byte borders) {
        return (borders & NORTH_BORDER) != 0;
    }

    public static boolean hasEastBorder(byte borders) {
        return (borders & EAST_BORDER) != 0;
    }

    public static boolean hasSouthBorder(byte borders) {
        return (borders & SOUTH_BORDER) != 0;
    }

    public static boolean hasWestBorder(byte borders) {
        return (borders & WEST_BORDER) != 0;
    }
    
    public void setOffset(int offset) {
        this.offset = offset;
    }
    
    public void setDefaultBorderColor(Color defaultBorderColor) {
        this.defaultBorderColor  = defaultBorderColor;
    }
    
    private Color visibleBorderColor(Color borderColor, Color backgroundColor) {
        if (borderColor != null && backgroundColor != null && borderColor.getRGB() == backgroundColor.getRGB()) {
            return defaultBorderColor;
        } else {
            return borderColor;
        }
    }
}
