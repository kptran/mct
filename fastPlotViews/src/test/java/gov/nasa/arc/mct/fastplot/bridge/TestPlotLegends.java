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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.testng.Assert;
import org.testng.annotations.Test;

import plotter.xy.LinearXYAxis;
import plotter.xy.LinearXYPlotLine;
import plotter.xy.XYDimension;

public class TestPlotLegends {

	private static final String DATA_SET_1_NAME = "DataSet1";
	private static final String DATA_SET_2_NAME = "DataSet2";
	
	private static final String DATA_SET_1_LEGEND = "DataSet1Leg";
	private static final String DATA_SET_2_LEGEND = "DataSet2Leg";

	AbbreviatingPlotLabelingAlgorithm plotLabelingAlgorithm = new AbbreviatingPlotLabelingAlgorithm();

	@Test
	public void testLegendsCreatedForPlot() {
		
		// Plan: part 1: create a plot and add two data series to it then check that
		// two legends are created with the correct labeling.
		
		// Plan: part 2 Simulate mouse movement into
		// one data series and test that plot lines and legend labels are correctly highlighted
		
		plotLabelingAlgorithm.setPanelOrWindowTitle("ABC 123");
		
		// Setup plot
		PlotView testPlot = new PlotView.Builder(PlotterPlot.class).build();
		// Add a data sets
		testPlot.addDataSet(DATA_SET_1_NAME, "\n" + DATA_SET_1_LEGEND);
		testPlot.addDataSet(DATA_SET_2_NAME, "\n" + DATA_SET_2_LEGEND);
		
	    LegendManager legendPanel = ((PlotterPlot) testPlot.getLastPlot()).getLegendManager();
	    
	    // Part 1
	    
	    // Plot must have a legend panel.
		Assert.assertNotNull(legendPanel);
		Assert.assertTrue(legendPanel.getComponents().length == 1); // legends only have one component - inner panel.
		
		JPanel innerPanel = (JPanel) legendPanel.getComponent(0);	
		List<LegendEntry> legendEntries = new ArrayList<LegendEntry>();
		
		// Extract the legend entries for the legend panel.
		 for (Component component: innerPanel.getComponents()) {
		    	if (component instanceof LegendEntry) {
		    		legendEntries.add((LegendEntry) component);
		    	}
		 }
		 
		 Assert.assertTrue(legendEntries.size() == 2); // one for each data set.
		 
		 // Part 2.
		 
		 LegendEntry legendEntry1 = legendEntries.get(0);
		 LegendEntry legendEntry2 = legendEntries.get(1);
		 
		 Assert.assertFalse(legendEntry1.selected);
		 Assert.assertFalse(legendEntry2.selected);
		 
		 // Move mouse into legend entry. 
	     MouseEvent evt = new MouseEvent(legendEntry1, 1, 0, 0, legendEntry1.getX() + 1, legendEntry1.getY(), 0, false);
		 legendEntry1.mouseEntered(evt);		
	     Assert.assertTrue(legendEntry1.selected);
	     Assert.assertFalse(legendEntry2.selected);
	     
	     // Checks that legendEntry mouse entered hover over then toolTipText is set.
	     if (legendEntry1.selected)
	    	 Assert.assertNotNull(legendEntry1.getToolTipText());
	     
	     // Checks again for MouseEvent for setToolTipText
	     Assert.assertNotNull(legendEntry1.getToolTipText(evt));
	
	     // Move mouse out of legend entry.
	     legendEntry1.mouseExited(new MouseEvent(legendEntry1, 1, 0, 0, legendEntry1.getX() + 1, legendEntry1.getY(), 0, false));		
	     Assert.assertFalse(legendEntry1.selected);
		 Assert.assertFalse(legendEntry2.selected);
	    
	}

 	@Test
	public void testLegendsNaming() {
		
		final String BaseDisplayName = "Hello World";
		plotLabelingAlgorithm.setPanelOrWindowTitle(BaseDisplayName);
		AbbreviatingPlotLabelingAlgorithm.globalContextLabels.clear();
		
		// Setup plot
		PlotView testPlot = new PlotView.Builder(PlotterPlot.class).build();
		// Add a data sets
		testPlot.addDataSet(BaseDisplayName, BaseDisplayName + "\n" + "Leg");

		
	    LegendManager legendPanel = ((PlotterPlot) testPlot.getLastPlot()).getLegendManager();
	    
	    // Part 1
	    
	    // Plot must have a legend panel.
		Assert.assertNotNull(legendPanel);
		Assert.assertTrue(legendPanel.getComponents().length == 1); // legends only have one component - inner panel.
		
		JPanel innerPanel = (JPanel) legendPanel.getComponent(0);	
		List<LegendEntry> legendEntries = new ArrayList<LegendEntry>();
		
		// Extract the legend entries for the legend panel.
		 for (Component component: innerPanel.getComponents()) {
		    	if (component instanceof LegendEntry) {
		    		legendEntries.add((LegendEntry) component);
		    	}
		 }
		 
		 Assert.assertTrue(legendEntries.size() == 1); // one for each data set.
		 
		 // Part 2.
		 
		 LegendEntry legendEntry1 = legendEntries.get(0);
		 
		 Assert.assertEquals(legendEntry1.getComputedBaseDisplayName(), BaseDisplayName,
				 "legend entry whose base display name would result in a empty plot label should not be fully removed");
	}

	@Test
	public void testPlotLegendBaseDisplayNameTruncation() {
		final String panelOrWindowTitleTest = "DEF";
		final String canvasPanelTitleTest = "123";

		plotLabelingAlgorithm.setPanelOrWindowTitle(panelOrWindowTitleTest);
		Assert.assertTrue(plotLabelingAlgorithm.getPanelOrWindowTitle().equals(panelOrWindowTitleTest));
		
		plotLabelingAlgorithm.setCanvasPanelTitle(canvasPanelTitleTest);
		Assert.assertTrue(plotLabelingAlgorithm.getCanvasPanelTitle().equals(canvasPanelTitleTest));
		
		LegendEntry testEntry = new LegendEntry(Color.white, Color.white, new Font("Arial", Font.PLAIN, 12), new AbbreviatingPlotLabelingAlgorithm());
		testEntry.setPlot(new LinearXYPlotLine(new LinearXYAxis(XYDimension.X), new LinearXYAxis(XYDimension.Y), XYDimension.X));
		
		// Empty string.
		testEntry.setBaseDisplayName("");
		Assert.assertTrue(testEntry.getBaseDisplayNameLabel().equalsIgnoreCase(""));
		
		// No line break
		testEntry.setBaseDisplayName("Legend Text Legend Text");
		Assert.assertTrue(testEntry.getBaseDisplayNameLabel().equalsIgnoreCase("Legend Text Legend Text"));
		
		// Modified for testing labeling algorithm
		testEntry.setBaseDisplayName("AB_C");
		Assert.assertTrue(testEntry.getFullBaseDisplayName().equalsIgnoreCase("AB C"));
		
		testEntry.setBaseDisplayName("A_B_C");
		Assert.assertTrue(testEntry.getFullBaseDisplayName().equalsIgnoreCase("A B C"));
		
		testEntry.setBaseDisplayName("ABC_DEF");
		Assert.assertTrue(testEntry.getFullBaseDisplayName().equalsIgnoreCase("ABC DEF"));
		
		List<String> identifiers = new ArrayList<String>();
		List<String> contextLabels = new ArrayList<String>();
		
		identifiers.add("ABC_DEF_GHIJ");
		contextLabels.add(plotLabelingAlgorithm.getPanelOrWindowTitle());	
		Assert.assertTrue(plotLabelingAlgorithm.computeLabel(identifiers, contextLabels).equals("ABC GHIJ"));
		
		identifiers.clear();
		identifiers.add("ABC_DEF_123_BBB");
		contextLabels.add(plotLabelingAlgorithm.getPanelOrWindowTitle());
		contextLabels.add(plotLabelingAlgorithm.getCanvasPanelTitle());	
		Assert.assertTrue(plotLabelingAlgorithm.computeLabel(identifiers, contextLabels).equals("ABC BBB"));
		
	}
	
}
