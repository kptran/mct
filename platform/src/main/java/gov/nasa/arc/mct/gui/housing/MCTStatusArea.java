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
 * MCTStatusArea.java - Mar 26, 2009.
 *
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.gui.housing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Defines a status area with two UI widgets located at far left and right. This area
 * appears at the bottom of a Housing window. A rectangle is drawn around each of the widgets.
 * 
 * @author atomo
 *
 */
@SuppressWarnings("serial")
public class MCTStatusArea extends JPanel {

    private static final int VERTICAL_MARGIN = 0;
    private static final int HORIZONTAL_MARGIN = 7;
    private JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, HORIZONTAL_MARGIN, VERTICAL_MARGIN));
    private JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, HORIZONTAL_MARGIN, VERTICAL_MARGIN));

    private MCTHousing parentHousing;

    public MCTStatusArea(MCTHousing housing) {
        this.parentHousing = housing;
        setLayout(new BorderLayout());

        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        instrumentNames();

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
        parentHousing.setStatusArea(this);
    }

    public void addToLeft(JComponent widget) {
        leftPanel.add(widget);
        revalidate();
    }
    
    public void removeFromLeft(JComponent widget) {
        leftPanel.remove(widget);
        revalidate();
    }
    
    public void setLeftWidget(JComponent widget) {
        leftPanel.removeAll();
        leftPanel.add(widget);
        instrumentNames(widget, "leftWidget");
        revalidate();
    }

    public void setRightWidget(JComponent widget) {
        rightPanel.removeAll();
        rightPanel.add(widget);
        instrumentNames(widget, "rightWidget");
        revalidate();
    }

    private void instrumentNames() {
        setName("statusArea");
        leftPanel.setName("leftPanel");
        rightPanel.setName("rightPanel");
    }

    private void instrumentNames(JComponent widget, String tag) {
        widget.setName(tag);
    }
}
