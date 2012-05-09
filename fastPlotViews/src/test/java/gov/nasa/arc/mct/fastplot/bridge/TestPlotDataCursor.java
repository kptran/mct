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

import gov.nasa.arc.mct.fastplot.view.Axis;
import gov.nasa.arc.mct.fastplot.view.PinSupport;
import gov.nasa.arc.mct.fastplot.view.Pinnable;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestPlotDataCursor {
	
	@Mock 
	private PlotAbstraction plotView;

	private List<AbstractPlottingPackage> plots = new ArrayList<AbstractPlottingPackage>();
	
	@BeforeMethod
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Mockito.when(plotView.getCurrentMCTTime()).thenReturn(new GregorianCalendar().getTimeInMillis());
    	Mockito.when(plotView.getTimeAxis()).thenReturn(new Axis());
    	Mockito.when(plotView.getSubPlots()).thenReturn(plots);
		final PinSupport pins = new PinSupport();
		Mockito.when(plotView.createPin()).thenAnswer(new Answer<Pinnable>() {
			@Override
			public Pinnable answer(InvocationOnMock invocation) throws Throwable {
				return pins.createPin();
			}
		});
	}
	
	
    @Test
    public void testFormatTime() {	
    	Assert.assertEquals(PlotDataCursor.formatTime(PlotConstants.MILLISECONDS_IN_SECOND), "+0/00:00:01" );
    	Assert.assertEquals(PlotDataCursor.formatTime(PlotConstants.MILLISECONDS_IN_MIN), "+0/00:01:00" );
    	Assert.assertEquals(PlotDataCursor.formatTime(PlotConstants.MILLISECONDS_IN_HOUR), "+0/01:00:00" );
    	Assert.assertEquals(PlotDataCursor.formatTime(PlotConstants.MILLISECONDS_IN_DAY), "+1/00:00:00" );
    	Assert.assertEquals(PlotDataCursor.formatTime(-PlotConstants.MILLISECONDS_IN_SECOND), "-0/00:00:01" );
    	Assert.assertEquals(PlotDataCursor.formatTime(-PlotConstants.MILLISECONDS_IN_MIN), "-0/00:01:00" );
    	Assert.assertEquals(PlotDataCursor.formatTime(-PlotConstants.MILLISECONDS_IN_HOUR), "-0/01:00:00" );
    	Assert.assertEquals(PlotDataCursor.formatTime(-PlotConstants.MILLISECONDS_IN_DAY), "-1/00:00:00" );
    	
    	
    	Assert.assertEquals(PlotDataCursor.formatTime(10 * PlotConstants.MILLISECONDS_IN_SECOND), "+0/00:00:10" );
    	Assert.assertEquals(PlotDataCursor.formatTime(10 * PlotConstants.MILLISECONDS_IN_MIN), "+0/00:10:00" );
    	Assert.assertEquals(PlotDataCursor.formatTime(10 * PlotConstants.MILLISECONDS_IN_HOUR), "+0/10:00:00" );
    	Assert.assertEquals(PlotDataCursor.formatTime(10 * PlotConstants.MILLISECONDS_IN_DAY), "+10/00:00:00" );
    	Assert.assertEquals(PlotDataCursor.formatTime(10 * -PlotConstants.MILLISECONDS_IN_SECOND), "-0/00:00:10" );
    	Assert.assertEquals(PlotDataCursor.formatTime(10 * -PlotConstants.MILLISECONDS_IN_MIN), "-0/00:10:00" );
    	Assert.assertEquals(PlotDataCursor.formatTime(10 * -PlotConstants.MILLISECONDS_IN_HOUR), "-0/10:00:00" );
    	Assert.assertEquals(PlotDataCursor.formatTime(10 * -PlotConstants.MILLISECONDS_IN_DAY), "-10/00:00:00" );
 
    	//1 day, 1 hours, 1 min, 1 second
    	long oneDayOneHourOneMinOneSecond = PlotConstants.MILLISECONDS_IN_DAY + PlotConstants.MILLISECONDS_IN_HOUR + PlotConstants.MILLISECONDS_IN_MIN +
    	                                    PlotConstants.MILLISECONDS_IN_SECOND;
    	Assert.assertEquals(PlotDataCursor.formatTime(oneDayOneHourOneMinOneSecond), "+1/01:01:01" );
    	
    	//10 day, 10 hours, 10 min, 10 second
    	long tenDaytenHourTenMinTenSecond = 10 * PlotConstants.MILLISECONDS_IN_DAY + 
    	                                    10 * PlotConstants.MILLISECONDS_IN_HOUR + 
    	                                    10 * PlotConstants.MILLISECONDS_IN_MIN +
    	                                    10 * PlotConstants.MILLISECONDS_IN_SECOND;
    	Assert.assertEquals(PlotDataCursor.formatTime(tenDaytenHourTenMinTenSecond), "+10/10:10:10" );
    }
    
}
