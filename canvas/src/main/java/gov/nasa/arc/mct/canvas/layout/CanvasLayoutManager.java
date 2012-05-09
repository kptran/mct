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
package gov.nasa.arc.mct.canvas.layout;

import gov.nasa.arc.mct.canvas.formatting.ControlAreaFormattingConstants;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CanvasLayoutManager implements LayoutManager2 {
    private final static Logger LOGGER = LoggerFactory.getLogger(CanvasLayoutManager.class);
    
    private static enum LAYOUT_ARRANGEMENT {
        free, tile, mix
    }

    public final static LAYOUT_ARRANGEMENT TILE = LAYOUT_ARRANGEMENT.tile;
    public final static LAYOUT_ARRANGEMENT MIX = LAYOUT_ARRANGEMENT.mix;

    private LinkedHashMap<Component, Rectangle> newlyAddedComponents = new LinkedHashMap<Component, Rectangle>();
    private LAYOUT_ARRANGEMENT componentLayout;
    private Point nextLocation = new Point(0, 0);

    private Dimension minDimension = new Dimension(0, 0);
    private Dimension preferredDimension = new Dimension(0, 0);
    private boolean sizeUnknown = true;
    private int gridSize = ControlAreaFormattingConstants.NO_GRID_SIZE;
    private boolean snapToGrid = false;
    private int snapGridSize = 1;

    public CanvasLayoutManager() {
        componentLayout = LAYOUT_ARRANGEMENT.free;
    }

    public CanvasLayoutManager(LAYOUT_ARRANGEMENT layout) {
        componentLayout = layout;
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        assert constraints == null || constraints instanceof Rectangle;
        Rectangle bound = (Rectangle) constraints;
        newlyAddedComponents.put(comp, bound);
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        return 0;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        return 0;
    }

    @Override
    @SuppressWarnings("fallthrough")
    public void invalidateLayout(Container target) {
        try {
            Set<Component> components = newlyAddedComponents.keySet();
            LAYOUT_ARRANGEMENT layout = componentLayout;
            layout = ((componentLayout == LAYOUT_ARRANGEMENT.mix) && (newlyAddedComponents.size() == 1)) ? LAYOUT_ARRANGEMENT.free
                            : componentLayout;

            switch (layout) {

            case mix:
                if (!newlyAddedComponents.isEmpty()) {
                    Entry<Component, Rectangle> firstEntry = newlyAddedComponents.entrySet()
                                    .iterator().next();
                    Rectangle r = firstEntry.getValue();
                    if (r != null) {
                        nextLocation = r.getLocation();
                    }
                }
            case tile:
                Container parent = target.getParent();
                Dimension parentDimension = parent.getBounds().getSize();

                int largestHeight = 0;
                for (Component comp : components) {
                    Rectangle r = newlyAddedComponents.get(comp);
                    if (r != null) {
                        if (nextLocation.x + r.width >= parentDimension.width) {
                            nextLocation.x = 0;
                            nextLocation.y += largestHeight;
                            largestHeight = 0;
                        }
                        largestHeight = Math.max(largestHeight, r.height);
                        nextLocation = marshalLocation(nextLocation);
                        comp.setBounds(nextLocation.x, nextLocation.y, r.width, r.height);
                        nextLocation.x += r.width;
                    } else {
                        r = marshalLocation(comp.getBounds());
                        nextLocation.x = r.x + r.width;
                    }
                }
                break;
            case free:
            default:
                for (Component comp : components) {
                    Rectangle r = marshalLocation(newlyAddedComponents.get(comp));
                    if (r != null) {
                        comp.setBounds(r);
                    }
                }
                break;
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            newlyAddedComponents.clear();
        }

    }
    
    private Point marshalLocation(Point origPoint) {
        if (origPoint == null) { return null; }
        
        Point marshallPoint = new Point();
        marshallPoint.x = origPoint.x - (origPoint.x % snapGridSize);
        marshallPoint.y = origPoint.y - (origPoint.y % snapGridSize);
        if (marshallPoint.x < origPoint.x) {
            marshallPoint.x += snapGridSize;
        }
        if (marshallPoint.y < origPoint.y) {
            marshallPoint.y += snapGridSize;
        }

        return marshallPoint;
    }

    public Rectangle marshalLocation(Rectangle origBound) {
        if (origBound == null) { return null; }
        
        origBound.x = origBound.x - (origBound.x % snapGridSize);
        origBound.y = origBound.y - (origBound.y % snapGridSize);

        return origBound;
    }

    @Override
    public Dimension maximumLayoutSize(Container target) {
        if (sizeUnknown) {
            setSizes(target);
        }

        int width = target.getParent().getBounds().width, height = target.getParent().getBounds().height;
        for (Component component : target.getComponents()) {
            Point location = component.getLocation();
            int rightX = location.x + component.getWidth();
            int bottomY = location.y + component.getHeight();
            if (rightX > width)
                width = rightX;
            if (bottomY > height)
                height = bottomY;
        }

        Dimension dim = new Dimension(width, height);

        // Always add the container's insets
        Insets insets = target.getInsets();
        dim.width += insets.left + insets.right;
        dim.height += insets.top + insets.bottom;
        return dim;
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
        //
    }

    @Override
    public void layoutContainer(Container parent) {
        try {
            switch (componentLayout) {
            case tile:
                Component[] components = parent.getComponents();
                Dimension parentDimension = parent.getBounds().getSize();

                int largestHeight = 0;
                Point nextLocation = new Point(0, 0);
                for (Component comp : components) {
                    Rectangle r = comp.getBounds();
                    if (nextLocation.x + r.width >= parentDimension.width) {
                        nextLocation.x = 0;
                        nextLocation.y += largestHeight;
                        largestHeight = 0;
                    }
                    largestHeight = Math.max(largestHeight, r.height);
                    nextLocation = marshalLocation(nextLocation);
                    comp.setBounds(nextLocation.x, nextLocation.y, r.width, r.height);
                    nextLocation.x += r.width;
                }
                break;
            case mix:
            case free:
            default:
                break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            newlyAddedComponents.clear();
        }

    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        if (sizeUnknown) {
            setSizes(parent);
        }
        Dimension dim = new Dimension(0, 0);

        // Always add the container's insets
        Insets insets = parent.getInsets();
        dim.width = minDimension.width + insets.left + insets.right;
        dim.height = minDimension.height + insets.top + insets.bottom;
        return dim;
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        if (sizeUnknown) {
            setSizes(parent);
        }
        Dimension dim = new Dimension(0, 0);

        // Always add the container's insets
        Insets insets = parent.getInsets();
        dim.width = preferredDimension.width + insets.left + insets.right;
        dim.height = preferredDimension.height + insets.top + insets.bottom;
        return dim;
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        //
    }

    private void setSizes(Container parent) {
        preferredDimension = parent.getSize();
        minDimension = parent.getSize();
        sizeUnknown = false;
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
        enableSnap(snapToGrid);
    }

    public boolean isSnapEnable() {
        return this.snapToGrid;
    }

    public void enableSnap(boolean snapToGrid) {
        this.snapToGrid = snapToGrid;
        if (snapToGrid) {
            if (gridSize != ControlAreaFormattingConstants.NO_GRID_SIZE) {
                snapGridSize = gridSize;
                return;
            }
        }
        snapGridSize = 1;
    }
    
    public void switchLayout(LAYOUT_ARRANGEMENT layout) {
        this.componentLayout = layout;
    }
}
