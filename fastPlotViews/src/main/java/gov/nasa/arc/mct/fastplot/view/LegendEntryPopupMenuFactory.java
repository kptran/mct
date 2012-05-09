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
package gov.nasa.arc.mct.fastplot.view;

import gov.nasa.arc.mct.fastplot.bridge.LegendEntry;
import gov.nasa.arc.mct.fastplot.bridge.PlotConstants;
import gov.nasa.arc.mct.fastplot.bridge.PlotLineColorPalette;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

/**
 * Provides popup menus to legend entries upon request. 
 * @author vwoeltje
 */
public class LegendEntryPopupMenuFactory {
	private static final ResourceBundle BUNDLE = 
        ResourceBundle.getBundle(LegendEntryPopupMenuFactory.class.getName().substring(0, 
        		LegendEntryPopupMenuFactory.class.getName().lastIndexOf("."))+".Bundle");
	
	private PlotViewManifestation manifestation;

	public LegendEntryPopupMenuFactory(PlotViewManifestation targetPlotManifestation) {
		manifestation = targetPlotManifestation;
	}
	
	/**
	 * Get a popup menu for a specified legend entry
	 * @param entry the legend entry to produce a popup menu
	 * @return a popup menu with options appropriate to the specified legend entry
	 */
	public JPopupMenu getPopup(LegendEntry entry) {
		LegendEntryPopup popup = new LegendEntryPopup(manifestation, entry);
		return popup;
	}
	
	private class LegendEntryPopup extends JPopupMenu {
		private static final long serialVersionUID = -4846098785335776279L;
		
		public LegendEntryPopup(final PlotViewManifestation manifestation, final LegendEntry legendEntry) {
			super();
			
			Color assigned = legendEntry.getForeground();
			
			String name = legendEntry.getComputedBaseDisplayName();
			if (name.isEmpty()) name = legendEntry.getFullBaseDisplayName();
			
			String subMenuText = String.format(BUNDLE.getString("SelectColor.label"), 
			                     name);
			
			JMenu subMenu = new JMenu(subMenuText);
			
			if (!manifestation.isLocked()) {
				for (int i = 0; i < PlotConstants.MAX_NUMBER_OF_DATA_ITEMS_ON_A_PLOT; i++) {
					JMenuItem item = new JRadioButtonMenuItem("", 
							new SolidColorIcon(PlotLineColorPalette.getColor(i)),
							(assigned == PlotLineColorPalette.getColor(i))
							);
					final int colorIndex = i;
					item.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {				
							legendEntry.setForeground(PlotLineColorPalette.getColor(colorIndex));
							manifestation.setupPlotLineColors();
						}					
					});
					subMenu.add(item);
				}
				
				add(subMenu);
			}
			
		}	
		
		private class SolidColorIcon implements Icon {
			private Color iconColor;
						
			public SolidColorIcon (Color c) {
				iconColor = c;
			}

			@Override
			public int getIconHeight() {
				return 12;
			}

			@Override
			public int getIconWidth() {
				return 48;
			}

			@Override
			public void paintIcon(Component arg0, Graphics g, int x,
					int y) {
				g.setColor(iconColor);
				g.fillRect(x, y, getIconWidth(), getIconHeight() - 1);
				g.setColor(iconColor.darker());
				g.drawRect(x, y, getIconWidth(), getIconHeight() - 1);				
			}
			
		}
	}

}
