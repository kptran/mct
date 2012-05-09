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
package gov.nasa.arc.mct.gui.housing;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

/**
 * Implements the panel in the standard housing that holds the directory tree.
 * That pane includes a control area that can be hidden or shown, and an optional
 * "page" that is shown below the directory tree, with a split bar betweent them.
 * The "page" component is currently used only by the FilterComponent to show
 * the search form and results.
 */
public class DirectoryTreePanel extends JPanel {
    
    private static final long serialVersionUID = -3112863261904652961L;

    
    /**
     * Creates a new directory tree panel with the given control area. The tree
     * and the "page" pane will initially be empty panels, and the split bar will
     * be hidden.
     */
    public DirectoryTreePanel() {
        super(new BorderLayout());
       
        setName("directoryPanel");
    }

  
    
    /**
     * Sets the tree to show in the tree panel.
     * 
     * @param tree the JTree to show
     */
    public void setTree(JTree tree) {
        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane);
    }
    

}
