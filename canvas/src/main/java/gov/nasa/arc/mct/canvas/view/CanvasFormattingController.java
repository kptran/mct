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
package gov.nasa.arc.mct.canvas.view;

import gov.nasa.arc.mct.canvas.panel.Panel;
import gov.nasa.arc.mct.canvas.panel.PanelBorder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

public class CanvasFormattingController {

    /**
     * Construct controller.
     * 
     */
    private CanvasFormattingController() {
        //
    }

    /* Handle notifications from viewer */

    public static void notifyXPropertyChange(int newXValue, Panel selectedPanel) {
        Point existingLocation = selectedPanel.getLocation();
        Dimension existingSize = selectedPanel.getBounds().getSize();
        selectedPanel.setBounds(newXValue, existingLocation.y, existingSize.width,
                        existingSize.height);
    }

    public static void notifyYPropertyChange(int newYValue, Panel selectedPanel) {
        Point existingLocation = selectedPanel.getLocation();
        Dimension existingSize = selectedPanel.getBounds().getSize();
        selectedPanel.setBounds(existingLocation.x, newYValue, existingSize.width,
                        existingSize.height);
    }

    public static void notifyWidthPropertyChange(int newWdith, List<Panel> selectedPanels) {
        for (Panel selectedPanel : selectedPanels) {
            Point existingLocation = selectedPanel.getLocation();
            Dimension existingSize = selectedPanel.getBounds().getSize();
            selectedPanel.setBounds(existingLocation.x, existingLocation.y, newWdith,
                            existingSize.height);
        }
    }

    public static void notifyHeightPropertyChange(int newHeight, List<Panel> selectedPanels) {
        for (Panel selectedPanel : selectedPanels) {
            Point existingLocation = selectedPanel.getLocation();
            Dimension existingSize = selectedPanel.getBounds().getSize();
            selectedPanel.setBounds(existingLocation.x, existingLocation.y, existingSize.width,
                            newHeight);
        }
    }

    // Alignment
    public static void notifyAlignLeftSelected(List<Panel> selectedPanels) {
        int leftMostX = selectedPanels.get(0).getBounds().x;

        int numOfPanels = selectedPanels.size();
        for (int i = 1; i < numOfPanels; i++) {
            Panel panel = selectedPanels.get(i);
            Rectangle bound = panel.getBounds();
            int oldx = bound.getLocation().x;

            if (oldx < leftMostX) {
                leftMostX = oldx;
            }
        }

        for (Panel panel : selectedPanels) {
            Rectangle bound = panel.getBounds();
            int oldy = bound.getLocation().y;
            int width = bound.getSize().width;
            int height = bound.getSize().height;

            panel.setBounds(leftMostX, oldy, width, height);
        }
    }

    public static void notifyAlignCenterHSelected(List<Panel> selectedPanels) {
        Rectangle firstBound = selectedPanels.get(0).getBounds();
        int leftMostX = firstBound.x;
        int rightMostX = firstBound.x;

        int numOfPanels = selectedPanels.size();

        for (int i = 0; i < numOfPanels; i++) {
            Panel panel = selectedPanels.get(i);
            Rectangle bound = panel.getBounds();
            int oldx = bound.x;
            int width = bound.width;
            if (oldx < leftMostX)
                leftMostX = oldx;
            if (oldx + width > rightMostX)
                rightMostX = oldx + width;
        }
        for (Panel panel : selectedPanels) {
            Rectangle bound = panel.getBounds();
            //
            // Algorithm: the bounding box is determined by finding the
            // rightmost edge
            // along with the left most edge. The difference between
            // these two points is the area lengthwise of the bounding box.
            //
            // distance = rightMostX - leftMostX;
            //
            // Thus, the center of the bounding area would be the would
            // be the x position that is midway between the bounding length
            // added to the leftmost edge:
            //            
            // midwayPoint = leftMostX + (distance / 2);
            //
            // So, to center align each box in the bounding box, find that
            // component's midway point (x pos + (width / 2)).
            // move that component such that it's midway
            // point is equal to the bounding box midway point. Thus,
            // all items are aligned.

            int midwayPoint = leftMostX + ((rightMostX - leftMostX) / 2);
            int componentMidwayPoint = bound.x + (bound.width / 2);

            if (componentMidwayPoint > midwayPoint) { // it's to the right of
                // the center - move it
                // back
                int diff = componentMidwayPoint - midwayPoint;
                panel.setBounds(bound.x - diff, bound.y, bound.width, bound.height);
            } else { // it's to the left of center - move it towards the
                // center...
                int diff = midwayPoint - componentMidwayPoint;
                panel.setBounds(bound.x + diff, bound.y, bound.width, bound.height);
            }
        }
    }

    public static void notifyAlignRightSelected(List<Panel> selectedPanels) {
        int rightMostX = selectedPanels.get(0).getBounds().x+selectedPanels.get(0).getBounds().width;

        int numOfPanels = selectedPanels.size();
        for (int i = 1; i < numOfPanels; i++) {
            Panel panel = selectedPanels.get(i);
            Rectangle bound = panel.getBounds();
            int oldx = bound.x;
            int width = bound.width;

            if (oldx + width > rightMostX)
                rightMostX = oldx + width;
        }

        for (Panel panel : selectedPanels) {
            Rectangle bound = panel.getBounds();
            int width = bound.width;
            panel.setBounds(rightMostX - width, bound.y, width, bound.height);
        }
    }

    public static void notifyAlignTopSelected(List<Panel> selectedPanels) {
        int topMostY = selectedPanels.get(0).getBounds().y;

        int numOfPanels = selectedPanels.size();
        for (int i = 1; i < numOfPanels; i++) {
            Panel panel = selectedPanels.get(i);
            Rectangle bound = panel.getBounds();
            int oldy = bound.y;

            if (oldy < topMostY)
                topMostY = oldy;
        }

        for (Panel panel : selectedPanels) {
            Rectangle bound = panel.getBounds();
            panel.setBounds(bound.x, topMostY, bound.width, bound.height);
        }
    }

    public static void notifyAlignBottomSelected(List<Panel> selectedPanels) {
        int bottomMostY = selectedPanels.get(0).getBounds().y+selectedPanels.get(0).getBounds().height;

        int numOfPanels = selectedPanels.size();
        for (int i = 1; i < numOfPanels; i++) {
            Panel panel = selectedPanels.get(i);
            Rectangle bound = panel.getBounds();
            int oldy = bound.y;
            int height = bound.height;

            if (oldy + height > bottomMostY)
                bottomMostY = oldy + height;
        }

        for (Panel panel : selectedPanels) {
            Rectangle bound = panel.getBounds();
            int height = bound.height;
            panel.setBounds(bound.x, bottomMostY - height, bound.width, height);
        }
    }

    public static void notifyAlignVCenterSelected(List<Panel> selectedPanels) {
        Rectangle firstBound = selectedPanels.get(0).getBounds();
        int topMostY = firstBound.y;
        int bottomMostY = firstBound.y;

        int numOfPanels = selectedPanels.size();

        for (int i = 0; i < numOfPanels; i++) {
            Panel panel = selectedPanels.get(i);
            Rectangle bound = panel.getBounds();
            int oldy = bound.y;
            int height = bound.height;
            if (oldy < topMostY)
                topMostY = oldy;
            if (oldy + height > bottomMostY)
                bottomMostY = oldy + height;
        }

        for (Panel panel : selectedPanels) {
            Rectangle bound = panel.getBounds();
            int midPoint = topMostY + ((bottomMostY - topMostY) / 2);
            int componentMidPoint = bound.y + (bound.height / 2);

            if (componentMidPoint > midPoint) {
                int diff = componentMidPoint - midPoint;
                panel.setBounds(bound.x, bound.y - diff, bound.width, bound.height);
            } else { // it's to the left of center - move it towards the
                // center...
                int diff = midPoint - componentMidPoint;
                panel.setBounds(bound.x, bound.y + diff, bound.width, bound.height);
            }
        }
    }

    // Borders
    public static void notifyWestBorderStatus(boolean status, List<Panel> selectedPanels) {
        if (status) {
            for (Panel panel : selectedPanels) {
                panel.addPanelBorder(PanelBorder.WEST_BORDER);
            }
        } else {
            for (Panel panel : selectedPanels) {
                panel.removePanelBorder(PanelBorder.WEST_BORDER);
            }
        }
    }

    public static void notifyEastBorderStatus(boolean status, List<Panel> selectedPanels) {
        if (status) {
            for (Panel panel : selectedPanels) {
                panel.addPanelBorder(PanelBorder.EAST_BORDER);
            }
        } else {
            for (Panel panel : selectedPanels) {
                panel.removePanelBorder(PanelBorder.EAST_BORDER);
            }
        }
    }

    public static void notifyNorthBorderStatus(boolean status, List<Panel> selectedPanels) {
        if (status) {
            for (Panel panel : selectedPanels) {
                panel.addPanelBorder(PanelBorder.NORTH_BORDER);
            }
        } else {
            for (Panel panel : selectedPanels) {
                panel.removePanelBorder(PanelBorder.NORTH_BORDER);
            }
        }
    }

    public static void notifySouthBorderStatus(boolean status, List<Panel> selectedPanels) {
        if (status) {
            for (Panel panel : selectedPanels) {
                panel.addPanelBorder(PanelBorder.SOUTH_BORDER);
            }
        } else {
            for (Panel panel : selectedPanels) {
                panel.removePanelBorder(PanelBorder.SOUTH_BORDER);
            }
        }
    }

    public static void notifyAllBorderStatus(boolean status, List<Panel> selectedPanels) {
        if (status) {
            for (Panel panel : selectedPanels) {
                panel.addPanelBorder(PanelBorder.ALL_BORDERS);
            }
        } else {
            for (Panel panel : selectedPanels) {
                panel.removePanelBorder(PanelBorder.ALL_BORDERS);
            }
        }
    }

    public static void notifyBorderColorSelected(Color selectedColor, List<Panel> selectedPanels) {
        for (Panel panel : selectedPanels) {
            panel.setBorderColor(selectedColor);
        }
    }

    public static void notifyBorderFormattingStyle(int style, List<Panel> selectedPanels) {
        for (Panel panel : selectedPanels) {
            panel.setBorderStyle(style);
        }
    }

    public static void notifyTitleBarStatus(boolean status, List<Panel> selectedPanels) {
        for (Panel panel : selectedPanels) {
            panel.hideTitle(status);
        }
    }
    
    /** Set the title font in the panel
     * @param fontFamilyName
     * @param selectedPanels
     */
    public static void notifyTitleBarFontSelected(String fontFamilyName, List<Panel> selectedPanels) {
        for (Panel panel : selectedPanels) {
            panel.setTitleFont(fontFamilyName);
        }
    }
    
    /** Set the the title font size for the panel
     * @param fontSize
     * @param selectedPanels
     */
    public static void notifyTitleBarFontSizeSelected(Integer fontSize, List<Panel> selectedPanels) {
        for (Panel panel : selectedPanels) {
            panel.setTitleFontSize(fontSize);
        }
    }
    
    /** Set the Title font style for the panel
     * @param fontStyle
     * @param selectedPanels
     */
    public static void notifyTitleBarFontStyleSelected(Integer fontStyle, List<Panel> selectedPanels) {
        for (Panel panel : selectedPanels) {
            panel.setTitleFontStyle(fontStyle);
        }
    }
    
    /** Set the Title font text attribute (underline) for the panel
     * @param fontStyle
     * @param selectedPanels
     */
    public static void notifyTitleBarFontUnderlineSelected(Integer fontStyle, List<Panel> selectedPanels) {
        for (Panel panel : selectedPanels) {
            panel.setTitleFontUnderline(fontStyle);
        }
    }
    
    /** Set the title font color for the panel
     * @param fontForegroundColor
     * @param selectedPanels
     */
    public static void notifyTitleBarFontForegroundColorSelected(Integer fontForegroundColor, List<Panel> selectedPanels) {
        for (Panel panel : selectedPanels) {
            panel.setTitleFontForegroundColor(fontForegroundColor);
        }
    }
    
    /** Set the title background color for the panel
     * @param fontBackgroundColor
     * @param selectedPanels
     */
    public static void notifyTitleBarFontBackgroundColorSelected(Integer fontBackgroundColor, List<Panel> selectedPanels) {
        for (Panel panel : selectedPanels) {
            panel.setTitleFontBackgroundColor(fontBackgroundColor);
        }
    }

    static void notifyNewTitle(String newTitle, List<Panel> selectedPanels) {
        for (Panel panel : selectedPanels) {
            panel.setTitle(newTitle);
        }
    }

}
