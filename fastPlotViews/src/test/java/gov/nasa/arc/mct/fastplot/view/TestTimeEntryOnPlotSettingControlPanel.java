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
import gov.nasa.arc.mct.fastplot.bridge.PlotConstants;
import gov.nasa.arc.mct.fastplot.bridge.PlotView;
import gov.nasa.arc.mct.fastplot.bridge.PlotterPlot;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Work through all permutations of time setting entries on the plot setting control panel
 * and insure the request sent to the plot package match. 
 * 
 * A number of tests still need to be written. A number are disabled as they fail on the code at the time
 * of writing indicate problems in the code.
 *
 */
public class TestTimeEntryOnPlotSettingControlPanel {

	private static final int ACCEPTABLE_TIME_DIFFERENCE_IN_MS = 1000;
	
	private AbstractComponent mockComponent;
	
	@BeforeMethod
	public void setup() {
		mockComponent = new DummyComponent();
	}
	
	
	// min: currentMin max: currentMax
	// min: currentMin max: manual
	// min: curerntMin max: min + span
	
	// min: manual max:currentmax
	
	// min: manual max: manual
	@Test
	public void testTimeMinManualMaxManual() {
		PlotSettingsControlPanel testPanel = makeTestControlPanel();
		PlotSettingController controller = testPanel.getController();

		GregorianCalendar minTime = new GregorianCalendar();
		GregorianCalendar maxTime = new GregorianCalendar();
		minTime.setTimeZone(TimeZone.getTimeZone(PlotConstants.DEFAULT_TIME_ZONE));
		maxTime.setTimeZone(TimeZone.getTimeZone(PlotConstants.DEFAULT_TIME_ZONE));
		maxTime.add(Calendar.MINUTE, 30);

		testPanel.timeAxisMaxManual.setSelected(true);
		testPanel.timeAxisMinManual.setSelected(true);

		// Both min and max set to manual. This should disable the span. 
		Assert.assertFalse(testPanel.timeSpanValue.isEnabled());

		testPanel.timeAxisMinManualValue.setTime(minTime);
		testPanel.timeAxisMaxManualValue.setTime(maxTime);

		// Simulate setup plot button pressed.
		testPanel.setupPlot();

		// Make sure the correct values were pushed through.
		long delta = controller.minTime.getTimeInMillis() - minTime.getTimeInMillis();
		Assert.assertTrue(Math.abs(delta) < ACCEPTABLE_TIME_DIFFERENCE_IN_MS);
		delta = controller.maxTime.getTimeInMillis() - maxTime.getTimeInMillis();
		Assert.assertTrue(Math.abs(delta) < ACCEPTABLE_TIME_DIFFERENCE_IN_MS);
	}
	
	// min: manual max: min + span
	@Test (enabled = false)
	public void testTimeMinManualMaxMinPlusSpan() {
		PlotSettingsControlPanel testPanel = makeTestControlPanel();
		PlotSettingController controller = testPanel.getController();

		GregorianCalendar minTime = new GregorianCalendar();
		GregorianCalendar spanTime = new GregorianCalendar();
		spanTime.setTimeInMillis(60 * PlotConstants.MILLISECONDS_IN_MIN);

		testPanel.timeAxisMinManual.setSelected(true);
		testPanel.timeAxisMaxAuto.setSelected(true);

		testPanel.updateTimeAxisControls(); 

		// Span should be enabled.
		Assert.assertTrue(testPanel.timeSpanValue.isEnabled());

		// Simulate setup plot button pressed.
		testPanel.setupPlot();

		// Make sure the correct values were pushed through.
		// Number must be within 100 milliseconds of each other. 
		long delta = controller.minTime.getTimeInMillis() - minTime.getTimeInMillis();
		Assert.assertTrue(Math.abs(delta) < ACCEPTABLE_TIME_DIFFERENCE_IN_MS);

		delta = controller.maxTime.getTimeInMillis() - (minTime.getTimeInMillis() + spanTime.getTimeInMillis());
		Assert.assertTrue(Math.abs(delta) < ACCEPTABLE_TIME_DIFFERENCE_IN_MS);
	}
	
	// min: now max:currentmax
	// min: now max: manual

	static int TEST_HOURS = 1;
	static int TEST_MINUTES = 30;
	static TimeDuration TEST_TIME_SPAN_CALENDAR = new TimeDuration(0, TEST_HOURS, TEST_MINUTES, 0);

	// min: now max: min + span
	@Test 
	public void testTimeMinNowMaxNowPlusSpan() {
		PlotSettingsControlPanel testPanel = makeTestControlPanel();
		PlotSettingController controller = testPanel.getController();

		GregorianCalendar minTime = new GregorianCalendar();

		testPanel.timeAxisMinCurrent.setSelected(true);
		testPanel.timeAxisMaxAuto.setSelected(true);

		testPanel.timeAxisMinCurrentValue.setTime(minTime);

		// Set the span control's value
		testPanel.timeSpanValue.setTime(TEST_TIME_SPAN_CALENDAR);

		testPanel.updateTimeAxisControls(); 

		// Span should be enabled.
		Assert.assertTrue(testPanel.timeSpanValue.isEnabled());

		// Simulate setup plot button pressed.
		testPanel.setupPlot();

		// Make sure the correct values were pushed through.
		// Number must be within 100 milliseconds of each other. 	 
		long delta = controller.minTime.getTimeInMillis() - minTime.getTimeInMillis();
		Assert.assertTrue(Math.abs(delta) < ACCEPTABLE_TIME_DIFFERENCE_IN_MS);

		// Add the span value units to the Min Current value
		GregorianCalendar computedMaxTime = new GregorianCalendar();
		computedMaxTime.setTime(minTime.getTime());
		computedMaxTime.add(Calendar.HOUR_OF_DAY, testPanel.timeSpanValue.getHourOfDay());
		computedMaxTime.add(Calendar.MINUTE, testPanel.timeSpanValue.getMinute());

		delta = controller.maxTime.getTimeInMillis() - computedMaxTime.getTimeInMillis();
		Assert.assertTrue(Math.abs(delta) < ACCEPTABLE_TIME_DIFFERENCE_IN_MS);
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
