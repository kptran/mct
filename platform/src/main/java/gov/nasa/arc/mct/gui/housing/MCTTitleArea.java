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
/**
 * MCTTitleArea.java Aug 18, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.gui.housing;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.Twistie;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.gui.ViewRoleSelection;
import gov.nasa.arc.mct.services.component.ViewType;
import gov.nasa.arc.mct.util.LafColor;

import java.awt.Color;
import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

/**
 * This class implement the title area used in the directory and inspector areas.
 * 
 * @author nshi
 *
 */
@SuppressWarnings("serial")
public class MCTTitleArea extends JPanel {

    private static final Color BACKGROUND_COLOR = LafColor.WINDOW_BORDER.darker();
    private static final Color FOREGROUND_COLOR = LafColor.WINDOW.brighter();
    private static final int HORIZONTAL_SPACING = 5;

    private Twistie toggle;
    private AbstractComponent component = null;
    private View titleViewManifestation;

    private String componentInTitle;

    private JLabel titleLabel;

    private MCTInspectionArea inspector;
    
    public MCTTitleArea(MCTInspectionArea inspector, String text, Twistie toggle) {
        this.inspector = inspector;
        this.toggle = toggle;
        
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        titleLabel = new JLabel(text);
        titleLabel.setForeground(FOREGROUND_COLOR);
        this.setBackground(BACKGROUND_COLOR);

        if (inspector.getHousedViewManifestation() != null ) {
            AbstractComponent comp = inspector.getHousedViewManifestation().getManifestedComponent();
            titleViewManifestation = comp.getViewInfos(ViewType.NODE).iterator().next().createView(comp);
        }
        layoutTitleArea();
        instrumentNames();
        
        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent  e) {
                // on double click
                if (e.getClickCount() == 2) {
                    if (component!= null) {
                        // open inspected component
                          component.open();
                    } 
                    assert component!=null : "Expected component to be initialized before title bar becomes visible";
                }   
          }
          });
    }

    private void instrumentNames() {
        titleLabel.setName("inspectorTitle");
    }
    
    private void layoutTitleArea() {
        this.removeAll();
        assert(this.getComponentCount() == 0);
        add(Box.createHorizontalStrut(HORIZONTAL_SPACING));
        add(titleLabel);
        if (titleViewManifestation != null) {
            add(Box.createHorizontalStrut(HORIZONTAL_SPACING));
            add(titleViewManifestation);

            titleViewManifestation.setBackground(BACKGROUND_COLOR);
            Component[] comps = titleViewManifestation.getComponents();
            for (Component uiComp : comps) {
                if (uiComp instanceof JLabel) {
                    uiComp.setForeground(FOREGROUND_COLOR);
                }
            }
        }
        add(Box.createHorizontalStrut(HORIZONTAL_SPACING));
        if (toggle != null)
            add(toggle); // The toggle button that shows or hides its associated control area.
        repaint();
    }

    /**
     * The component used in the title changes dynamically based on user selection gestures.
     * @param comp
     */
    public void setComponent(AbstractComponent comp) {
        component = comp;
        if (comp == null) {
            componentInTitle = null;
            titleViewManifestation = null;
            layoutTitleArea();
            return;
        }
        if (componentInTitle == null || !componentInTitle.equals(comp.getDisplayName())) {
            componentInTitle = comp.getDisplayName();
            titleViewManifestation = comp.getViewInfos(ViewType.NODE).iterator().next().createView(comp);

            if (titleViewManifestation != null) {
                titleViewManifestation.addMouseListener(new MCTPopupOpenerForInspector(inspector));
                titleViewManifestation.addMouseMotionListener(new WidgetDragger());
                titleViewManifestation.setTransferHandler(new WidgetTransferHandler());
            }
        }
        layoutTitleArea();
    }

    public View getViewManifestation() {
        return titleViewManifestation;
    }

    public String getComponentInTitle() {
        return componentInTitle;
    }

    public void refreshTitle() {
        layoutTitleArea();
    }

    private static final class WidgetDragger extends MouseMotionAdapter {
        @Override
        public void mouseDragged(MouseEvent e) {
            JComponent c = (JComponent) e.getSource();
            TransferHandler th = c.getTransferHandler();
            th.exportAsDrag(c, e, TransferHandler.COPY);
        }
    }

    private final class WidgetTransferHandler extends TransferHandler {
        @Override
        public int getSourceActions(JComponent c) {
            return COPY;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            if (component != null && inspector.getHousedViewManifestation() != null) {
                return new ViewRoleSelection(new View[] { inspector.getHousedViewManifestation()});
            } else {
                return null;
            }
        }
    }

}
