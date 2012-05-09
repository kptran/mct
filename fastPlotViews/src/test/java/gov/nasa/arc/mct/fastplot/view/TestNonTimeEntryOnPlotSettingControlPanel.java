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

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.fastplot.access.PolicyManagerAccess;
import gov.nasa.arc.mct.fastplot.bridge.PlotView;
import gov.nasa.arc.mct.fastplot.bridge.PlotterPlot;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Work through all permutations of non time setting entries on the plot setting control panel
 * and insure the request sent to the plot package match. 
 *
 */
public class TestNonTimeEntryOnPlotSettingControlPanel {

	private AbstractComponent mockComponent;
	
	@BeforeMethod
	public void setup() {
		new PolicyManagerAccess().unsetPolicyManager(null);
		mockComponent = new DummyComponent();
	}
	
	// min: max-span, max: Current Largest.
	@Test
	public void testMinMaxMinusSpanMaxCurrentLargest() {
		PlotSettingsControlPanel testPanel = makeTestControlPanel();
		PlotSettingController controller = testPanel.getController();

		double span = 100;
		double maxValue = 200;

		testPanel.nonTimeAxisMinAutoAdjust.setSelected(true);
		testPanel.nonTimeAxisMaxCurrent.setSelected(true);

		testPanel.nonTimeAxisMaxCurrentValue.setValue(maxValue);
		testPanel.nonTimeSpanValue.setValue(span);
		testPanel.updateNonTimeAxisControls(); 

		// Span should be enabled.
		Assert.assertTrue(testPanel.nonTimeSpanValue.isEnabled());

		// Simulate setup plot button pressed.
		testPanel.setupPlot();

        // No data coming in, so these will be set to the default of 1 and zero.
		Assert.assertTrue(controller.nonTimeMax == 1.0);   
		Assert.assertTrue(controller.nonTimeMin ==  0.0);	
	}

	// min: max-span max: manual
	@Test
	public void testMinMaxMinusSpanMaxManual() {
		PlotSettingsControlPanel testPanel = makeTestControlPanel();
		PlotSettingController controller = testPanel.getController();

		double span = 100;
		double maxValue = 200;

		testPanel.nonTimeAxisMinAutoAdjust.setSelected(true);
		testPanel.nonTimeAxisMaxManual.setSelected(true);

		testPanel.nonTimeAxisMaxManualValue.setValue(maxValue);
		testPanel.nonTimeSpanValue.setValue(span);
		testPanel.updateNonTimeAxisControls(); 

		// Span should be enabled.
		Assert.assertTrue(testPanel.nonTimeSpanValue.isEnabled());

		// Simulate setup plot button pressed.
		testPanel.setupPlot();

		Assert.assertEquals(controller.nonTimeMax, maxValue);   
		Assert.assertEquals(controller.nonTimeMin, maxValue - span);	  
	}
	
	// min: max-span max: min+span (should be disabled)
	public void testMinMaxMinusSpanMaxMinMinusSpan() {
		PlotSettingsControlPanel testPanel = makeTestControlPanel();
	
		testPanel.nonTimeAxisMinAutoAdjust.setSelected(true);
		testPanel.nonTimeAxisMinAutoAdjust.setSelected(true);
	
		testPanel.updateNonTimeAxisControls(); 

		// Span should be disabled
		Assert.assertFalse(testPanel.nonTimeSpanValue.isEnabled());	 
	}


	// min: manual max: current largest
	public void testMinManualMaxCurrentLargest() {
		PlotSettingsControlPanel testPanel = makeTestControlPanel();
		PlotSettingController controller = testPanel.getController();

		double minValue = 100;
		double maxValue = 200;

		testPanel.nonTimeAxisMinManual.setSelected(true);
		testPanel.nonTimeAxisMaxCurrent.setSelected(true);

		testPanel.nonTimeAxisMinManualValue.setValue(minValue);
		testPanel.nonTimeAxisMaxManualValue.setValue(maxValue);

		testPanel.updateNonTimeAxisControls(); 

		// Span should be enabled.
		Assert.assertFalse(testPanel.nonTimeSpanValue.isEnabled());

		// Simulate setup plot button pressed.
		testPanel.setupPlot();

		Assert.assertTrue(controller.nonTimeMax == maxValue);
		Assert.assertTrue(controller.nonTimeMin == minValue);
	}


	// min: manual max: manual
	@Test
	public void testMinManualMaxManual() {
		PlotSettingsControlPanel testPanel = makeTestControlPanel();
		PlotSettingController controller = testPanel.getController();

		double minValue = 100;
		double maxValue = 200;

		testPanel.nonTimeAxisMinManual.setSelected(true);
		testPanel.nonTimeAxisMaxManual.setSelected(true);

		testPanel.nonTimeAxisMinManualValue.setValue(minValue);
		testPanel.nonTimeAxisMaxManualValue.setValue(maxValue);

		testPanel.updateNonTimeAxisControls(); 

		// Span should be enabled.
		Assert.assertFalse(testPanel.nonTimeSpanValue.isEnabled());

		// Simulate setup plot button pressed.
		testPanel.setupPlot();

		Assert.assertTrue(controller.nonTimeMax == maxValue);
		Assert.assertTrue(controller.nonTimeMin == minValue);
	}

	// min:manual max: min + span
	@Test
	public void testMinManualMaxMinMinusSpan() {
		PlotSettingsControlPanel testPanel = makeTestControlPanel();
		PlotSettingController controller = testPanel.getController();

		double span = 100;
		double minValue = 200;

		testPanel.nonTimeAxisMaxAutoAdjust.setSelected(true);
		testPanel.nonTimeAxisMinManual.setSelected(true);

		testPanel.nonTimeAxisMinManualValue.setValue(minValue);
		testPanel.nonTimeSpanValue.setValue(span);
		testPanel.updateNonTimeAxisControls(); 

		// Span should be enabled.
		Assert.assertTrue(testPanel.nonTimeSpanValue.isEnabled());

		// Simulate setup plot button pressed.
		testPanel.setupPlot();

		Assert.assertEquals(controller.nonTimeMax, minValue + span);   
		Assert.assertEquals(controller.nonTimeMin, minValue );	  
	}

	// min: current smallest max: current largest
	public void testMinCurrentSmallestMaxCurrentLargest() {
		PlotSettingsControlPanel testPanel = makeTestControlPanel();
		PlotSettingController controller = testPanel.getController();

		double minValue = 100;
		double maxValue = 200;

		testPanel.nonTimeAxisMinCurrent.setSelected(true);
		testPanel.nonTimeAxisMaxCurrent.setSelected(true);

		testPanel.nonTimeAxisMinCurrentValue.setValue(minValue);
		testPanel.nonTimeAxisMaxManualValue.setValue(maxValue);

		testPanel.updateNonTimeAxisControls(); 

		// Span should be enabled.
		Assert.assertFalse(testPanel.nonTimeSpanValue.isEnabled());

		// Simulate setup plot button pressed.
		testPanel.setupPlot();

		Assert.assertTrue(controller.nonTimeMax == maxValue);
		Assert.assertTrue(controller.nonTimeMin == minValue);
	}

	// min: current smallest max: manual
	@Test
	public void testMinCurrentSmallestMaxManual() {
		PlotSettingsControlPanel testPanel = makeTestControlPanel();
		PlotSettingController controller = testPanel.getController();

		double minValue = 100;
		double maxValue = 200;

		testPanel.nonTimeAxisMinCurrent.setSelected(true);
		testPanel.nonTimeAxisMaxManual.setSelected(true);

		testPanel.nonTimeAxisMinCurrentValue.setValue(minValue);
		testPanel.nonTimeAxisMaxManualValue.setValue(maxValue);

		testPanel.updateNonTimeAxisControls(); 

		// Span should be enabled.
		Assert.assertFalse(testPanel.nonTimeSpanValue.isEnabled());

		// Simulate setup plot button pressed.
		testPanel.setupPlot();		
		Assert.assertTrue(controller.nonTimeMax == maxValue);
		// No data, this is always pushed to be zero. 
		Assert.assertTrue(controller.nonTimeMin == 0);
	}

	// min: current smallest max: min + span
	@Test
	public void testMinCurrentSmallestMaxMinPlusSpanl() {
		PlotSettingsControlPanel testPanel = makeTestControlPanel();
		PlotSettingController controller = testPanel.getController();

		double span = 100;
		double minValue = 200;

		testPanel.nonTimeAxisMinCurrent.setSelected(true);
		testPanel.nonTimeAxisMaxAutoAdjust.setSelected(true);

		testPanel.nonTimeAxisMinManualValue.setValue(minValue);
		testPanel.nonTimeSpanValue.setValue(span);
		testPanel.updateNonTimeAxisControls(); 

		// Span should be enabled.
		Assert.assertTrue(testPanel.nonTimeSpanValue.isEnabled());

		// Simulate setup plot button pressed.
		testPanel.setupPlot();

		// Current smallest will be rest back to zero with no data on plot. 
		Assert.assertEquals(controller.nonTimeMax, 0 + span);   
		Assert.assertEquals(controller.nonTimeMin, 0.0 );	  
	}

	private PlotSettingsControlPanel makeTestControlPanel() {
		PlotViewManifestation panel = new PlotViewManifestation(mockComponent, new ViewInfo(PlotViewManifestation.class,"",ViewType.OBJECT));
		panel.thePlot = new PlotView.Builder(PlotterPlot.class).build();
		return new PlotSettingsControlPanel(panel);
	}
	
	private static class DummyComponent extends AbstractComponent {
		public DummyComponent() {
		}
	}
}
