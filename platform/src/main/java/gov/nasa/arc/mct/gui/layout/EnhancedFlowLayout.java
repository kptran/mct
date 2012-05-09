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
package gov.nasa.arc.mct.gui.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Point;

/**
 * An extended version of FlowLayout, that supports different types of vertical
 * alignment.
 * 
 * Adapted from a workaround in http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4295966.
 * 
 * @author mrose
 *
 */
public class EnhancedFlowLayout extends FlowLayout {

    public final static long serialVersionUID = 1;
    
    /**
     * Items in the layout should be top-aligned.
     */
    public final static int TOP = 2;
    
    /**
     * Items in the layout should be bottom-aligned.
     */
    public final static int BOTTOM = 3;

    private int verticalAlignment = TOP;

    /**
     * Create a new layout with the given horizontal and vertical alignment. The vertical gap,
     * as in FlowLayout, is set to zero if this constructor is used.
     *  
     * @param horizontalAlignment the desired horizontal alignment, as in FlowLayout
     * @param verticalAlignment the desired vertical alignment
     */
    public EnhancedFlowLayout(int horizontalAlignment, int verticalAlignment) {
        this(horizontalAlignment, verticalAlignment, 0, 0);
    }

    /**
     * Create a new layout with given horizonal and vertical alignment, and horizontal
     * and vertical gaps to place between items in the layout.
     * 
     * @param horizontalAlignment the desired horizontal alignment, as in FlowLayout
     * @param verticalAlignment the desired vertical alignment
     * @param hGap the desired horizontal gap between adjacent items in the layout
     * @param vGap the desired vertical gap between adjacent items in the layout
     */
    public EnhancedFlowLayout(int horizontalAlignment, int verticalAlignment, int hGap, int vGap) {
        super(horizontalAlignment, hGap, vGap);
        setVerticalAlignment(verticalAlignment);
    }

    /**
     * Return the current vertical alignment. Will be one of TOP, BOTTOM, or CENTER.
     * 
     * @return the vertical alignment setting
     */
    public int getVerticalAlignment() {
        return verticalAlignment;
    }

    /**
     * Change the vertical alignment. Should be one of TOP, BOTTOM, or CENTER.
     * 
     * @param verticalAlignment the new vertical alignment value.
     */
    public void setVerticalAlignment(int verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    @Override
    public void layoutContainer(Container target)
    {
        synchronized (target.getTreeLock())
        {
            super.layoutContainer(target);
            if (verticalAlignment != TOP)
            {
                // first, find the highest and lowest points
                int minY=Integer.MAX_VALUE;
                int maxY=0;

                int nMembers = target.getComponentCount();
                for (int i=0; i<nMembers; ++i)
                {
                    Component comp = target.getComponent(i);
                    int compMinY = comp.getLocation().y;
                    int compMaxY = compMinY + comp.getHeight();
                    maxY = Math.max(compMaxY, maxY);
                    minY = Math.min(compMinY, minY);
                }
                
                // Now, calculate how far to drop each component.
                Insets insets = target.getInsets();
                int availableHeight = target.getHeight() - (insets.bottom + insets.top + getVgap()*2);

                // delta is the amount to move each component
                int delta = availableHeight - (maxY - minY);    // bottom alignment
                if (verticalAlignment == CENTER)
                    delta /= 2;                      // center alignment

                // Now, move each component down.
                for (int i=0; i<nMembers; ++i)
                {
                    Component comp = target.getComponent(i);
                    Point newLoc = comp.getLocation();
                    newLoc.y += delta;
                    comp.setLocation(newLoc);
                }
            }
        }
    }

}
