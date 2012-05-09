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

import gov.nasa.arc.mct.components.ExtendedProperties;
import gov.nasa.arc.mct.fastplot.bridge.PlotAbstraction.PlotSettings;
import gov.nasa.arc.mct.fastplot.bridge.PlotConstants;
import gov.nasa.arc.mct.fastplot.bridge.PlotConstants.AxisOrientationSetting;
import gov.nasa.arc.mct.fastplot.bridge.PlotConstants.NonTimeAxisSubsequentBoundsSetting;
import gov.nasa.arc.mct.fastplot.bridge.PlotConstants.TimeAxisSubsequentBoundsSetting;
import gov.nasa.arc.mct.fastplot.bridge.PlotConstants.XAxisMaximumLocationSetting;
import gov.nasa.arc.mct.fastplot.bridge.PlotConstants.YAxisMaximumLocationSetting;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.util.GregorianCalendar;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestPlotPersistanceHandler {
	@Mock
	private PlotViewManifestation manifestation;

	@BeforeMethod
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Mockito.when(manifestation.getInfo()).thenReturn(new ViewInfo(PlotViewManifestation.class,"",ViewType.OBJECT));
	}


	@Test
	public void testMigrateFixed() {
		Mockito.when(manifestation.getViewProperties()).thenReturn(new ExtendedProperties());
		
		PlotPersistanceHandler h = new PlotPersistanceHandler(manifestation);
		h.persistPlotSettings(AxisOrientationSetting.X_AXIS_AS_TIME, XAxisMaximumLocationSetting.MAXIMUM_AT_RIGHT,
				YAxisMaximumLocationSetting.MAXIMUM_AT_TOP, TimeAxisSubsequentBoundsSetting.SCRUNCH, NonTimeAxisSubsequentBoundsSetting.FIXED,
				NonTimeAxisSubsequentBoundsSetting.FIXED, 0.0, 1.0, new GregorianCalendar(), new GregorianCalendar(), 0.0, 0.0, 0.0, true, false);
		manifestation.getViewProperties().setProperty(PlotConstants.TIME_AXIS_SUBSEQUENT_SETTING, "FIXED");
		PlotSettings settings = h.loadPlotSettingsFromPersistance();
		Assert.assertEquals(settings.timeAxisSubsequent, TimeAxisSubsequentBoundsSetting.JUMP);
		Assert.assertTrue(settings.pinTimeAxis);
	}


	
}
