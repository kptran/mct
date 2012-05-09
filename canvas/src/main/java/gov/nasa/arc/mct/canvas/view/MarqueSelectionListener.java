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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

/**
 * Provide a mouse listener to implement marque selection. This will allow multiple panels to be selected in a group.
 * The selection algorithm is based on the set of panels that is directly underneath the selection area. Currently,
 * this works based on any part of the contained panel being selected; however, this needs to change to ignore at
 * the top level events solely within the content area of a panel. 
 *
 */
public class MarqueSelectionListener extends MouseInputAdapter {
    private JPanel selection;
    private int clickX, clickY;
    private final JComponent rootPanel;
    private boolean dragStart=false;
    private final MultipleSelectionProvider selectionProvider;

    public MarqueSelectionListener(JComponent panel, MultipleSelectionProvider provider) {
        rootPanel = panel;
        selectionProvider = provider;
    }
    
    public interface MultipleSelectionProvider {
        /**
         * Select the set of panels
         * @param selection of panels 
         */
        public void selectPanels(Collection<Panel> selection);
        
        /**
         * Determines if the point is in the top selection scope.
         * @param point in screen coordinates
         */
        public boolean pointInTopLevelPanel(Point p);
    }
    
    /**
     * Returns true if the drag action should not trigger marque selection. Move and resize are 
     * currently delegated to the panel if they are active.
     * @param c container for the event 
     */
    private boolean isInOverridingAction(Container c) {
        int cursorType = c.getCursor().getType();
        return cursorType == Cursor.MOVE_CURSOR ||
               cursorType == Cursor.N_RESIZE_CURSOR ||
               cursorType == Cursor.NE_RESIZE_CURSOR ||
               cursorType == Cursor.NW_RESIZE_CURSOR ||
               cursorType == Cursor.S_RESIZE_CURSOR ||
               cursorType == Cursor.SE_RESIZE_CURSOR ||
               cursorType == Cursor.SW_RESIZE_CURSOR ||
               cursorType == Cursor.E_RESIZE_CURSOR ||
               cursorType == Cursor.W_RESIZE_CURSOR;
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (selection == null) {
            // this method is invoked during each drag event, so dragStart tracks the start of the drag event
            if (!dragStart) {
                // check the cursor to see if the user is currently in a move or resize mode
                // also verify whether the selected point is directly in the top level panel. This
                // should prevent nested selections from triggering selections at the top level. 
                Container c = (Container) e.getSource();
                if (!isInOverridingAction(c) && 
                                selectionProvider.pointInTopLevelPanel(e.getLocationOnScreen())) {
                    selection = createSelection();
                    clickX = e.getX();
                    clickY = e.getY();
                    selection.setBounds(clickX, clickY, 1, 1);
                    rootPanel.add(selection);
                    rootPanel.setComponentZOrder(selection, 0);
                }
            }
        } else {
            int mouseX = e.getX();
            int mouseY = e.getY();

            if (mouseX >= clickX) {
                if (mouseY >= clickY) {
                    // existing upper left point is unchanged, so just adjust
                    // the height, width
                    selection.setBounds(clickX, clickY, mouseX - clickX, mouseY - clickY);
                } else {
                    // original click is now the lower left point
                    int width = mouseX - clickX;
                    selection.setBounds(mouseX - width, mouseY, width, clickY - mouseY);
                }
            } else {
                if (mouseY >= clickY) {
                    // original click is now the upper right point
                    selection.setBounds(mouseX, clickY, clickX - mouseX, mouseY - clickY);
                } else {
                    // original click is now the lower right point
                    selection.setBounds(mouseX, mouseY, clickX - mouseX, clickY - mouseY);
                }
            }
        }
        dragStart = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        dragStart = false;
        if (selection != null) {
            rootPanel.remove(selection);
            // figure out what components are selected if any
            List<Component> selectedComponents = new ArrayList<Component>();
            Rectangle selectedRegion = selection.getBounds();
            for (Component c : rootPanel.getComponents()) {
                if (selectedRegion.intersects(c.getBounds())) {
                    selectedComponents.add(c);
                }
            }
            List<Panel> selectedPanels = new ArrayList<Panel>(selectedComponents.size());
            for (Component selected : selectedComponents) {
                selectedPanels.add((Panel)selected);
            }
            selectionProvider.selectPanels(selectedPanels);
        }
        selection = null;
    }

    private JPanel createSelection() {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createLineBorder(Color.gray, 5));
        p.setOpaque(false);
        p.setBackground(null);
        return p;
    }
}
