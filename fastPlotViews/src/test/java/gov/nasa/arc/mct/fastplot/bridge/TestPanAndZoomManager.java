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

import gov.nasa.arc.mct.fastplot.bridge.PlotConstants.AxisOrientationSetting;
import gov.nasa.arc.mct.fastplot.bridge.PlotConstants.PanDirection;
import gov.nasa.arc.mct.fastplot.bridge.PlotConstants.PlotDisplayState;
import gov.nasa.arc.mct.fastplot.bridge.PlotConstants.ZoomDirection;
import gov.nasa.arc.mct.fastplot.view.Axis;
import gov.nasa.arc.mct.fastplot.view.PinSupport;

import java.util.GregorianCalendar;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import plotter.xy.XYAxis;

public class TestPanAndZoomManager {

	@Mock
	private PlotAbstraction plotAbstraction;
	
	private PlotterPlot plotTimeOnX;
	private PlotterPlot plotTimeOnY;

	PanAndZoomManager panAndZoomManagerTimeOnX;
	PanAndZoomManager panAndZoomManagerTimeOnY;
	
	@BeforeMethod
	public void setup() {
		MockitoAnnotations.initMocks(this);
		PinSupport pins = new PinSupport();
		Mockito.when(plotAbstraction.getCurrentMCTTime()).thenReturn(new GregorianCalendar().getTimeInMillis());
		Mockito.when(plotAbstraction.getTimeAxis()).thenReturn(new Axis());
		Mockito.when(plotAbstraction.getTimeAxisUserPin()).thenReturn(pins.createPin());

		PlotAbstraction testPlotTimeX = new PlotView.Builder(PlotterPlot.class).
			                        axisOrientation(AxisOrientationSetting.X_AXIS_AS_TIME).
		                            nonTimeVaribleAxisMaxValue(100).
		                            nonTimeVaribleAxisMinValue(0).
		                            build();
	    plotTimeOnX = (PlotterPlot) testPlotTimeX.returnPlottingPackage();
	    plotTimeOnX.plotAbstraction = plotAbstraction;
	    panAndZoomManagerTimeOnX = plotTimeOnX.panAndZoomManager;
	    
	    PlotAbstraction testPlotTimeY = new PlotView.Builder(PlotterPlot.class).
        axisOrientation(AxisOrientationSetting.Y_AXIS_AS_TIME).
        nonTimeVaribleAxisMaxValue(100).
        nonTimeVaribleAxisMinValue(0).
        build();
        plotTimeOnY = (PlotterPlot) testPlotTimeY.returnPlottingPackage();
        plotTimeOnY.plotAbstraction = plotAbstraction;
        panAndZoomManagerTimeOnY = plotTimeOnY.panAndZoomManager;  
	}
	
	@Test
	public void TestEnteringPanMode() {
		plotTimeOnX.setPlotDisplayState(PlotDisplayState.DISPLAY_ONLY);
		panAndZoomManagerTimeOnX.enteredPanMode();
		Assert.assertEquals(plotTimeOnX.getPlotDisplayState(), PlotDisplayState.USER_INTERACTION);	
		
		panAndZoomManagerTimeOnX.exitedPanMode();
		
	}
	
	@Test
	public void TestEnteringZoomMode() {
		plotTimeOnX.setPlotDisplayState(PlotDisplayState.DISPLAY_ONLY);
		panAndZoomManagerTimeOnX.enteredZoomMode();
		Assert.assertEquals(plotTimeOnX.getPlotDisplayState(), PlotDisplayState.USER_INTERACTION);
		
		panAndZoomManagerTimeOnX.exitedZoomMode();
	}
	
	@Test
	public void TestPanningActionsTimeOnX() {
		XYAxis xAxis = plotTimeOnX.plotView.getXAxis();
		XYAxis yAxis = plotTimeOnX.plotView.getYAxis();

		double xStart = xAxis.getStart();
		double xStop = xAxis.getEnd();
		
		double yStart = yAxis.getStart();
		double yStop = yAxis.getEnd();
		
		
		panAndZoomManagerTimeOnX.panAction(PanDirection.PAN_HIGHER_X_AXIS);
		
		double newXStart = xAxis.getStart();
		double newXStop = xAxis.getEnd();
		double newYStart = yAxis.getStart();
		double newYStop = yAxis.getEnd();
		
		Assert.assertTrue(newXStart > xStart);
		Assert.assertTrue(newXStop > xStop);
		Assert.assertEquals(newYStart, yStart);
		Assert.assertEquals(newYStop, yStop);
		
		panAndZoomManagerTimeOnX.panAction(PanDirection.PAN_LOWER_X_AXIS);
		
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertEquals(newXStart, xStart);
		Assert.assertEquals(newXStop, xStop);	
		Assert.assertEquals(newYStart, yStart);
		Assert.assertEquals(newYStop, yStop);
		
		panAndZoomManagerTimeOnX.panAction(PanDirection.PAN_LOWER_X_AXIS);
		
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertTrue(newXStart < xStart);
		Assert.assertTrue(newXStop < xStop);
		Assert.assertEquals(newYStart, yStart);
		Assert.assertEquals(newYStop, yStop);
		
		panAndZoomManagerTimeOnX.panAction(PanDirection.PAN_HIGHER_X_AXIS);
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertEquals(newYStart, yStart);
		Assert.assertEquals(newYStop, yStop);
		Assert.assertEquals(newXStart, xStart);
		Assert.assertEquals(newXStop, xStop);
		
		panAndZoomManagerTimeOnX.panAction(PanDirection.PAN_HIGHER_Y_AXIS);
	
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertTrue(newYStart > yStart);
		Assert.assertTrue(newYStop > yStop);
		Assert.assertEquals(newXStart, xStart);
		Assert.assertEquals(newXStop, xStop);
		
		panAndZoomManagerTimeOnX.panAction(PanDirection.PAN_LOWER_Y_AXIS);	
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertEquals(newYStart, yStart);
		Assert.assertEquals(newYStop, yStop);
		Assert.assertEquals(newXStart, xStart);
		Assert.assertEquals(newXStop, xStop);
		
		panAndZoomManagerTimeOnX.panAction(PanDirection.PAN_LOWER_Y_AXIS);	
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertTrue(newYStart < yStart);
		Assert.assertTrue(newYStop < yStop);
		Assert.assertEquals(newXStart, xStart);
		Assert.assertEquals(newXStop, xStop);
	}
	
	
	@Test
	public void TestPanningActionsTimeOnY() {
		XYAxis xAxis = plotTimeOnY.plotView.getXAxis();
		XYAxis yAxis = plotTimeOnY.plotView.getYAxis();

		double xStart = xAxis.getStart();
		double xStop = xAxis.getEnd();
		
		double yStart = yAxis.getStart();
		double yStop = yAxis.getEnd();
		
		
		panAndZoomManagerTimeOnY.panAction(PanDirection.PAN_HIGHER_X_AXIS);
		
		double newXStart = xAxis.getStart();
		double newXStop = xAxis.getEnd();
		double newYStart = yAxis.getStart();
		double newYStop = yAxis.getEnd();
		
		Assert.assertTrue(newXStart > xStart);
		Assert.assertTrue(newXStop > xStop);
		Assert.assertEquals(newYStart, yStart);
		Assert.assertEquals(newYStop, yStop);
		
		panAndZoomManagerTimeOnY.panAction(PanDirection.PAN_LOWER_X_AXIS);
		
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertEquals(newXStart, xStart);
		Assert.assertEquals(newXStop, xStop);	
		Assert.assertEquals(newYStart, yStart);
		Assert.assertEquals(newYStop, yStop);
		
		panAndZoomManagerTimeOnY.panAction(PanDirection.PAN_LOWER_X_AXIS);
		
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertTrue(newXStart < xStart);
		Assert.assertTrue(newXStop < xStop);
		Assert.assertEquals(newYStart, yStart);
		Assert.assertEquals(newYStop, yStop);
		
		panAndZoomManagerTimeOnY.panAction(PanDirection.PAN_HIGHER_X_AXIS);
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertEquals(newYStart, yStart);
		Assert.assertEquals(newYStop, yStop);
		Assert.assertEquals(newXStart, xStart);
		Assert.assertEquals(newXStop, xStop);
		
		panAndZoomManagerTimeOnY.panAction(PanDirection.PAN_HIGHER_Y_AXIS);
	
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertTrue(newYStart > yStart);
		Assert.assertTrue(newYStop > yStop);
		Assert.assertEquals(newXStart, xStart);
		Assert.assertEquals(newXStop, xStop);
		
		panAndZoomManagerTimeOnY.panAction(PanDirection.PAN_LOWER_Y_AXIS);	
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertEquals(newYStart, yStart);
		Assert.assertEquals(newYStop, yStop);
		Assert.assertEquals(newXStart, xStart);
		Assert.assertEquals(newXStop, xStop);
		
		panAndZoomManagerTimeOnY.panAction(PanDirection.PAN_LOWER_Y_AXIS);	
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertTrue(newYStart < yStart);
		Assert.assertTrue(newYStop < yStop);
		Assert.assertEquals(newXStart, xStart);
		Assert.assertEquals(newXStop, xStop);
	}
	
	
	
	@Test
	public void TestZoomingActionsTimeX() {
		XYAxis xAxis = plotTimeOnX.plotView.getXAxis();
		XYAxis yAxis = plotTimeOnX.plotView.getYAxis();

		double xStart = xAxis.getStart();
		double xStop = xAxis.getEnd();	
		double yStart = yAxis.getStart();
		double yStop = yAxis.getEnd();
		
		
		panAndZoomManagerTimeOnX.zoomAction(ZoomDirection.ZOOM_IN_HIGH_Y_AXIS);

		double newXStart = xAxis.getStart();
		double newXStop = xAxis.getEnd();
		double newYStart = yAxis.getStart();
		double newYStop = yAxis.getEnd();
		
		Assert.assertEquals(newYStart, yStart);
		Assert.assertTrue(newYStop < yStop);
		Assert.assertEquals(newXStart, xStart);
		Assert.assertEquals(newXStop, xStop);
		
		panAndZoomManagerTimeOnX.zoomAction(ZoomDirection.ZOOM_OUT_HIGH_Y_AXIS);
		panAndZoomManagerTimeOnX.zoomAction(ZoomDirection.ZOOM_OUT_HIGH_Y_AXIS);
		
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertEquals(newYStart, yStart);
		Assert.assertTrue(newYStop > yStop);
		Assert.assertEquals(newXStart, xStart);
		Assert.assertEquals(newXStop, xStop);
		
		panAndZoomManagerTimeOnX.zoomAction(ZoomDirection.ZOOM_IN_CENTER_Y_AXIS);
	
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertTrue(newYStart > yStart);
		Assert.assertTrue(newYStop < yStop);
		Assert.assertEquals(newXStart, xStart);
		Assert.assertEquals(newXStop, xStop);
		
		panAndZoomManagerTimeOnX.zoomAction(ZoomDirection.ZOOM_OUT_CENTER_Y_AXIS);
		panAndZoomManagerTimeOnX.zoomAction(ZoomDirection.ZOOM_OUT_CENTER_Y_AXIS);
	
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertTrue(newYStart < yStart);
		Assert.assertTrue(newYStop > yStop);
		Assert.assertEquals(newXStart, xStart);
		Assert.assertEquals(newXStop, xStop);
		
		panAndZoomManagerTimeOnX.zoomAction(ZoomDirection.ZOOM_IN_LOW_Y_AXIS);
		panAndZoomManagerTimeOnX.zoomAction(ZoomDirection.ZOOM_IN_LOW_Y_AXIS);
		
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertTrue(newYStart > yStart);
		Assert.assertTrue(newYStop > yStop);
		Assert.assertEquals(newXStart, xStart);
		Assert.assertEquals(newXStop, xStop);
		
		panAndZoomManagerTimeOnX.zoomAction(ZoomDirection.ZOOM_OUT_LOW_Y_AXIS);
		panAndZoomManagerTimeOnX.zoomAction(ZoomDirection.ZOOM_OUT_LOW_Y_AXIS);
		
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertTrue(newYStart < yStart);
		Assert.assertTrue(newYStop > yStop);
		Assert.assertEquals(newXStart, xStart);
		Assert.assertEquals(newXStop, xStop);
		
		panAndZoomManagerTimeOnX.zoomAction(ZoomDirection.ZOOM_IN_LEFT_X_AXIS);
		panAndZoomManagerTimeOnX.zoomAction(ZoomDirection.ZOOM_IN_LEFT_X_AXIS);
		
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertTrue(newYStart < yStart);
		Assert.assertTrue(newYStop > yStop);
		Assert.assertTrue(newXStart > xStart);
		Assert.assertEquals(newXStop, xStop);
		
		panAndZoomManagerTimeOnX.zoomAction(ZoomDirection.ZOOM_OUT_LEFT_X_AXIS);
		panAndZoomManagerTimeOnX.zoomAction(ZoomDirection.ZOOM_OUT_LEFT_X_AXIS);
		
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertTrue(newYStart < yStart);
		Assert.assertTrue(newYStop > yStop);
		Assert.assertTrue(newXStart > xStart);
		Assert.assertEquals(newXStop, xStop);
			
		panAndZoomManagerTimeOnX.zoomAction(ZoomDirection.ZOOM_IN_CENTER_X_AXIS);
		
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
	    Assert.assertTrue(newXStart > xStart);
	    Assert.assertTrue(newXStop < xStop);
	    Assert.assertTrue(newYStart < yStart);
	    Assert.assertTrue(newYStop > yStop);
		
		panAndZoomManagerTimeOnX.zoomAction(ZoomDirection.ZOOM_OUT_CENTER_X_AXIS);
		
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertTrue(newYStart < yStart);
		Assert.assertTrue(newYStop > yStop);
		Assert.assertTrue(newXStart > xStart);
		Assert.assertTrue(newXStop < xStop);
		
		panAndZoomManagerTimeOnX.zoomAction(ZoomDirection.ZOOM_IN_RIGHT_X_AXIS);
		
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertTrue(newYStart < yStart);
		Assert.assertTrue(newYStop > yStop);
		Assert.assertTrue(newXStart > xStart);
		Assert.assertTrue(newXStop < xStop);

		
		panAndZoomManagerTimeOnX.zoomAction(ZoomDirection.ZOOM_OUT_RIGHT_X_AXIS);
		
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertTrue(newYStart < yStart);
		Assert.assertTrue(newYStop > yStop);
		Assert.assertTrue(newXStart > xStart);
		Assert.assertTrue(newXStop < xStop);	
	}
	
	@Test
	public void TestZoomingActionsTimeY() {
		XYAxis xAxis = plotTimeOnY.plotView.getXAxis();
		XYAxis yAxis = plotTimeOnY.plotView.getYAxis();
		
		double xStart = xAxis.getStart();
		double xStop = xAxis.getEnd();	
		double yStart = yAxis.getStart();
		double yStop = yAxis.getEnd();
		
		
		panAndZoomManagerTimeOnY.zoomAction(ZoomDirection.ZOOM_IN_HIGH_Y_AXIS);

		double newXStart = xAxis.getStart();
		double newXStop = xAxis.getEnd();
		double newYStart = yAxis.getStart();
		double newYStop = yAxis.getEnd();
		
		Assert.assertEquals(newYStart, yStart);
		Assert.assertTrue(newYStop < yStop);
		Assert.assertEquals(newXStart, xStart);
		Assert.assertEquals(newXStop, xStop);
		
		panAndZoomManagerTimeOnY.zoomAction(ZoomDirection.ZOOM_OUT_HIGH_Y_AXIS);
		panAndZoomManagerTimeOnY.zoomAction(ZoomDirection.ZOOM_OUT_HIGH_Y_AXIS);
		
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertEquals(newYStart, yStart);
		Assert.assertTrue(newYStop > yStop);
		Assert.assertEquals(newXStart, xStart);
		Assert.assertEquals(newXStop, xStop);
		
		panAndZoomManagerTimeOnY.zoomAction(ZoomDirection.ZOOM_IN_CENTER_Y_AXIS);
	
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertTrue(newYStart > yStart);
		Assert.assertTrue(newYStop < yStop);
		Assert.assertEquals(newXStart, xStart);
		Assert.assertEquals(newXStop, xStop);
		
		panAndZoomManagerTimeOnY.zoomAction(ZoomDirection.ZOOM_OUT_CENTER_Y_AXIS);
		panAndZoomManagerTimeOnY.zoomAction(ZoomDirection.ZOOM_OUT_CENTER_Y_AXIS);
	
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertTrue(newYStart < yStart);
		Assert.assertTrue(newYStop > yStop);
		Assert.assertEquals(newXStart, xStart);
		Assert.assertEquals(newXStop, xStop);
		
		panAndZoomManagerTimeOnY.zoomAction(ZoomDirection.ZOOM_IN_LOW_Y_AXIS);
		panAndZoomManagerTimeOnY.zoomAction(ZoomDirection.ZOOM_IN_LOW_Y_AXIS);
		
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertTrue(newYStart > yStart);
		Assert.assertTrue(newYStop > yStop);
		Assert.assertEquals(newXStart, xStart);
		Assert.assertEquals(newXStop, xStop);
		
		panAndZoomManagerTimeOnY.zoomAction(ZoomDirection.ZOOM_OUT_LOW_Y_AXIS);
		panAndZoomManagerTimeOnY.zoomAction(ZoomDirection.ZOOM_OUT_LOW_Y_AXIS);
		
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertTrue(newYStart < yStart);
		Assert.assertTrue(newYStop > yStop);
		Assert.assertEquals(newXStart, xStart);
		Assert.assertEquals(newXStop, xStop);
		
		panAndZoomManagerTimeOnY.zoomAction(ZoomDirection.ZOOM_IN_LEFT_X_AXIS);
		panAndZoomManagerTimeOnY.zoomAction(ZoomDirection.ZOOM_IN_LEFT_X_AXIS);
		
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertTrue(newYStart < yStart);
		Assert.assertTrue(newYStop > yStop);
		Assert.assertTrue(newXStart > xStart);
		Assert.assertEquals(newXStop, xStop);
		
		panAndZoomManagerTimeOnY.zoomAction(ZoomDirection.ZOOM_OUT_LEFT_X_AXIS);
		panAndZoomManagerTimeOnY.zoomAction(ZoomDirection.ZOOM_OUT_LEFT_X_AXIS);
		
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertTrue(newYStart < yStart);
		Assert.assertTrue(newYStop > yStop);
		Assert.assertTrue(newXStart > xStart);
		Assert.assertEquals(newXStop, xStop);
			
		panAndZoomManagerTimeOnY.zoomAction(ZoomDirection.ZOOM_IN_CENTER_X_AXIS);
		
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
	    Assert.assertTrue(newXStart > xStart);
	    Assert.assertTrue(newXStop < xStop);
	    Assert.assertTrue(newYStart < yStart);
	    Assert.assertTrue(newYStop > yStop);
		
		panAndZoomManagerTimeOnY.zoomAction(ZoomDirection.ZOOM_OUT_CENTER_X_AXIS);
		
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertTrue(newYStart < yStart);
		Assert.assertTrue(newYStop > yStop);
		Assert.assertTrue(newXStart > xStart);
		Assert.assertTrue(newXStop < xStop);
		
		panAndZoomManagerTimeOnY.zoomAction(ZoomDirection.ZOOM_IN_RIGHT_X_AXIS);
		
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertTrue(newYStart < yStart);
		Assert.assertTrue(newYStop > yStop);
		Assert.assertTrue(newXStart > xStart);
		Assert.assertTrue(newXStop < xStop);

		
		panAndZoomManagerTimeOnY.zoomAction(ZoomDirection.ZOOM_OUT_RIGHT_X_AXIS);
		
		newXStart = xAxis.getStart();
		newXStop = xAxis.getEnd();
		newYStart = yAxis.getStart();
		newYStop = yAxis.getEnd();
		
		Assert.assertTrue(newYStart < yStart);
		Assert.assertTrue(newYStop > yStop);
		Assert.assertTrue(newXStart > xStart);
		Assert.assertTrue(newXStop < xStop);
		
	}
	
	
	
	
}
