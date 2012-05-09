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
package gov.nasa.arc.mct.core.components;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.services.component.ViewInfo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public final class BrokenInfoPanel extends View {
    private static ResourceBundle bundle = ResourceBundle.getBundle("CoreTaxonomyResourceBundle"); //NOI18N 
    public static final String VIEW_NAME = bundle.getString("BrokenInspectorViewName");
    
	public BrokenInfoPanel(AbstractComponent ac, ViewInfo vi) {
		super(ac,vi);
		
		JPanel view = new JPanel();
		view.setLayout(new BoxLayout(view, BoxLayout.Y_AXIS));
		
		// Add the header for this view manifestation.
		view.add(createHeaderRow(bundle.getString("BrokenInspectorInformation"), Color.red, 15)); //NOI18N
		view.add(new JLabel());
		
		setLayout(new BorderLayout());
		add(view, BorderLayout.NORTH);
	}
	
	// The following are the utility methods for formatting this 
	// info view manifestation.
	
	// Creates a formatted JPanel that contains the header in a JLabel
	private JPanel createHeaderRow(String title, Color color, float size) {
		JPanel panel = new JPanel(new BorderLayout());
		JLabel label = new JLabel(title);
		label.setFont(label.getFont().deriveFont(Font.BOLD, size));
		label.setForeground(color);
		panel.add(label, BorderLayout.WEST);
		return panel;
	}
	
}