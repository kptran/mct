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
package gov.nasa.arc.mct.fastplot.bridge;

import gov.nasa.arc.mct.fastplot.utils.AbbreviatingPlotLabelingAlgorithm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * Provides the panel holding the individual legend entries for a plot.
 */
@SuppressWarnings("serial")
public class LegendManager extends JPanel implements MouseListener {

	public static final int MAX_NUMBER_LEGEND_COLUMNS = 1;
	
	private PlotterPlot plot;
	// Panel holding the legend items.
	private JPanel innerPanel;
	private Color backgroundColor;
	private LegendEntry legendEntry;
	private AbbreviatingPlotLabelingAlgorithm plotLabelingAlgorithm = new AbbreviatingPlotLabelingAlgorithm();
	private List<LegendEntry> legendEntryList = new ArrayList<LegendEntry> ();
	
	/**
	 * Included only to support unit testing.
	 */
	protected LegendManager() {}
	
	/**
	 * Construct the legend panel for a plot
	 * @param legendBackgroundColor the background color of the legend
	 */
	LegendManager(PlotterPlot thePlot, Color legendBackgroundColor, AbbreviatingPlotLabelingAlgorithm plotLabelingAlgorithm) {
		this.plotLabelingAlgorithm = plotLabelingAlgorithm;
				
		plot = thePlot;
		backgroundColor = legendBackgroundColor;
		setBackground(legendBackgroundColor);
		setLayout(new BorderLayout());

		innerPanel = new JPanel();	
		innerPanel.setBackground(legendBackgroundColor);	
		
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
		
		add(innerPanel, BorderLayout.NORTH);	
		setVisible(false);
		plot.plotView.add(this);
		
	}	

	/**
	 * Add new entry to the legend.
	 * @param entry to add
	 */
	public void addLegendEntry(LegendEntry entry) {
		entry.setBackground(backgroundColor);
				
		legendEntry = entry;
		legendEntry.setPlotLabelingAlgorithm(this.plotLabelingAlgorithm);

		legendEntryList.add(entry);
		
		innerPanel.add(legendEntry);
	}
	
	public LegendEntry getLegendEntry() {
		return legendEntry;
	}
	
	public List<LegendEntry> getLegendEntryList() {
		return legendEntryList;
	}
	

	@Override
	public void mouseClicked(MouseEvent e) {
		//do nothing
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
		String toolTipText = legendEntry.getToolTipText();
		legendEntry.setToolTipText(toolTipText);
		this.setToolTipText(toolTipText);
			
	}

	@Override
	public void mouseExited(MouseEvent e) {
		//do nothing
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		//do nothing
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//do nothing
	}
	
	public String getToolTipText() {
		return legendEntry.getToolTipText();
	}
}
