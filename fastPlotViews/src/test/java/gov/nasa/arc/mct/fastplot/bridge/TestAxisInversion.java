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
import gov.nasa.arc.mct.fastplot.bridge.PlotConstants.NonTimeAxisSubsequentBoundsSetting;
import gov.nasa.arc.mct.fastplot.bridge.PlotConstants.XAxisMaximumLocationSetting;
import gov.nasa.arc.mct.fastplot.bridge.PlotConstants.YAxisMaximumLocationSetting;
import gov.nasa.arc.mct.fastplot.view.PlotViewManifestation;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import plotter.xy.XYAxis;

/**
 * Suite of tests that make sure Plots with inverted axis stay inverted when scrolling. 
 */
public class TestAxisInversion {
	@Mock
	private PlotViewManifestation mockPlotViewManifestation;


	@BeforeMethod
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Mockito.when(mockPlotViewManifestation.getCurrentMCTTime()).thenReturn(new GregorianCalendar().getTimeInMillis());
	}


	@Test
	public void NonTimeInvertedTimeOnX() {
		
		GregorianCalendar time = new GregorianCalendar();
		GregorianCalendar endTime = new GregorianCalendar();
		endTime.add(Calendar.MINUTE, 10);
		
		PlotView testPlot = new PlotView.Builder(PlotterPlot.class)
		.nonTimeAxisMinSubsequentSetting(NonTimeAxisSubsequentBoundsSetting.SEMI_FIXED)
		.nonTimeAxisMaxSubsequentSetting(NonTimeAxisSubsequentBoundsSetting.SEMI_FIXED)
		.nonTimeVaribleAxisMaxValue(10)
		.nonTimeVaribleAxisMinValue(0)
	    .timeVariableAxisMinValue(time.getTimeInMillis())
	    .timeVariableAxisMaxValue(endTime.getTimeInMillis())
	    .axisOrientation(AxisOrientationSetting.X_AXIS_AS_TIME)
	    .yAxisMaximumLocation(YAxisMaximumLocationSetting.MAXIMUM_AT_BOTTOM)
		.build();
		testPlot.setManifestation(mockPlotViewManifestation);
		
		PlotterPlot qcPlot = (PlotterPlot) testPlot.returnPlottingPackage();
		
		XYAxis xAxis = qcPlot.plotView.getXAxis();
		XYAxis yAxis = qcPlot.plotView.getYAxis();
		Assert.assertTrue(yAxis.getStart() > yAxis.getEnd());
		Assert.assertFalse(xAxis.getStart() > xAxis.getEnd());
		
		testPlot.addDataSet("DataSet1");
		testPlot.addData("DataSet1", time.getTimeInMillis(), 1);
		
		Assert.assertTrue(yAxis.getStart() > yAxis.getEnd());
		Assert.assertFalse(xAxis.getStart() > xAxis.getEnd());
		
		time.add(Calendar.MINUTE, 1);
		testPlot.addData("DataSet1", time.getTimeInMillis(), 11);
		
		Assert.assertTrue(yAxis.getStart() > yAxis.getEnd());
		Assert.assertFalse(xAxis.getStart() > xAxis.getEnd());
		
		
		time.add(Calendar.MINUTE, 20);
		testPlot.addData("DataSet1", time.getTimeInMillis(), 50);
		
		Assert.assertTrue(yAxis.getStart() > yAxis.getEnd());
		Assert.assertFalse(xAxis.getStart() > xAxis.getEnd());
	}
	
	@Test
	public void NonTimeInvertedTimeOnY() {
		
		GregorianCalendar time = new GregorianCalendar();
		GregorianCalendar endTime = new GregorianCalendar();
		endTime.add(Calendar.MINUTE, 10);
		
		PlotView testPlot = new PlotView.Builder(PlotterPlot.class)
		.nonTimeAxisMinSubsequentSetting(NonTimeAxisSubsequentBoundsSetting.SEMI_FIXED)
		.nonTimeAxisMaxSubsequentSetting(NonTimeAxisSubsequentBoundsSetting.SEMI_FIXED)
		.nonTimeVaribleAxisMaxValue(10)
		.nonTimeVaribleAxisMinValue(0)
	    .timeVariableAxisMinValue(time.getTimeInMillis())
	    .timeVariableAxisMaxValue(endTime.getTimeInMillis())
	    .axisOrientation(AxisOrientationSetting.Y_AXIS_AS_TIME)
	    .xAxisMaximumLocation(XAxisMaximumLocationSetting.MAXIMUM_AT_LEFT)
		.build();
		testPlot.setManifestation(mockPlotViewManifestation);
		
		PlotterPlot qcPlot = (PlotterPlot) testPlot.returnPlottingPackage();
		
		XYAxis xAxis = qcPlot.plotView.getXAxis();
		XYAxis yAxis = qcPlot.plotView.getYAxis();
		Assert.assertFalse(yAxis.getStart() > yAxis.getEnd());
		Assert.assertTrue(xAxis.getStart() > xAxis.getEnd());
		
		testPlot.addDataSet("DataSet1");
		testPlot.addData("DataSet1", time.getTimeInMillis(), 1);
		
		Assert.assertFalse(yAxis.getStart() > yAxis.getEnd());
		Assert.assertTrue(xAxis.getStart() > xAxis.getEnd());
		
		time.add(Calendar.MINUTE, 1);
		testPlot.addData("DataSet1", time.getTimeInMillis(), 11);
		
		Assert.assertFalse(yAxis.getStart() > yAxis.getEnd());
		Assert.assertTrue(xAxis.getStart() > xAxis.getEnd());
		
		
		time.add(Calendar.MINUTE, 20);
		testPlot.addData("DataSet1", time.getTimeInMillis(), 50);
		
		Assert.assertFalse(yAxis.getStart() > yAxis.getEnd());
		Assert.assertTrue(xAxis.getStart() > xAxis.getEnd());
	}
	
	
	@Test
	public void TimeInvertedTimeOnX() {	
		GregorianCalendar time = new GregorianCalendar();
		GregorianCalendar endTime = new GregorianCalendar();
		endTime.add(Calendar.MINUTE, 10);
		
		PlotView testPlot = new PlotView.Builder(PlotterPlot.class)
		.nonTimeAxisMinSubsequentSetting(NonTimeAxisSubsequentBoundsSetting.SEMI_FIXED)
		.nonTimeAxisMaxSubsequentSetting(NonTimeAxisSubsequentBoundsSetting.SEMI_FIXED)
		.nonTimeVaribleAxisMaxValue(10)
		.nonTimeVaribleAxisMinValue(0)
	    .timeVariableAxisMinValue(time.getTimeInMillis())
	    .timeVariableAxisMaxValue(endTime.getTimeInMillis())
	    .axisOrientation(AxisOrientationSetting.X_AXIS_AS_TIME)
	    .xAxisMaximumLocation(XAxisMaximumLocationSetting.MAXIMUM_AT_LEFT)
		.build();
		testPlot.setManifestation(mockPlotViewManifestation);
		
		PlotterPlot qcPlot = (PlotterPlot) testPlot.returnPlottingPackage();
		
		XYAxis xAxis = qcPlot.plotView.getXAxis();
		XYAxis yAxis = qcPlot.plotView.getYAxis();
		Assert.assertFalse(yAxis.getStart() > yAxis.getEnd());
		Assert.assertTrue(xAxis.getStart() > xAxis.getEnd());
		
		testPlot.addDataSet("DataSet1");
		testPlot.addData("DataSet1", time.getTimeInMillis(), 1);
		
		Assert.assertFalse(yAxis.getStart() > yAxis.getEnd());
		Assert.assertTrue(xAxis.getStart() > xAxis.getEnd());
		
		time.add(Calendar.MINUTE, 1);
		testPlot.addData("DataSet1", time.getTimeInMillis(), 11);
		
		Assert.assertFalse(yAxis.getStart() > yAxis.getEnd());
		Assert.assertTrue(xAxis.getStart() > xAxis.getEnd());
		
		
		time.add(Calendar.MINUTE, 20);
		testPlot.addData("DataSet1", time.getTimeInMillis(), 50);
		
		Assert.assertFalse(yAxis.getStart() > yAxis.getEnd());
		Assert.assertTrue(xAxis.getStart() > xAxis.getEnd());
	}
	
	@Test
	public void TimeInvertedTimeOnY() {	
		GregorianCalendar time = new GregorianCalendar();
		GregorianCalendar endTime = new GregorianCalendar();
		endTime.add(Calendar.MINUTE, 10);
		
		PlotView testPlot = new PlotView.Builder(PlotterPlot.class)
		.nonTimeAxisMinSubsequentSetting(NonTimeAxisSubsequentBoundsSetting.SEMI_FIXED)
		.nonTimeAxisMaxSubsequentSetting(NonTimeAxisSubsequentBoundsSetting.SEMI_FIXED)
		.nonTimeVaribleAxisMaxValue(10)
		.nonTimeVaribleAxisMinValue(0)
	    .timeVariableAxisMinValue(time.getTimeInMillis())
	    .timeVariableAxisMaxValue(endTime.getTimeInMillis())
	    .axisOrientation(AxisOrientationSetting.Y_AXIS_AS_TIME)
	    .yAxisMaximumLocation(YAxisMaximumLocationSetting.MAXIMUM_AT_BOTTOM)
		.build();
		testPlot.setManifestation(mockPlotViewManifestation);
		
		PlotterPlot qcPlot = (PlotterPlot) testPlot.returnPlottingPackage();
		
		XYAxis xAxis = qcPlot.plotView.getXAxis();
		XYAxis yAxis = qcPlot.plotView.getYAxis();
		Assert.assertTrue(yAxis.getStart() > yAxis.getEnd());
		Assert.assertFalse(xAxis.getStart() > xAxis.getEnd());
		
		testPlot.addDataSet("DataSet1");
		testPlot.addData("DataSet1", time.getTimeInMillis(), 1);
		
		Assert.assertTrue(yAxis.getStart() > yAxis.getEnd());
		Assert.assertFalse(xAxis.getStart() > xAxis.getEnd());
		
		time.add(Calendar.MINUTE, 1);
		testPlot.addData("DataSet1", time.getTimeInMillis(), 11);
		
		Assert.assertTrue(yAxis.getStart() > yAxis.getEnd());
		Assert.assertFalse(xAxis.getStart() > xAxis.getEnd());
		
		
		time.add(Calendar.MINUTE, 20);
		testPlot.addData("DataSet1", time.getTimeInMillis(), 50);
		
		Assert.assertTrue(yAxis.getStart() > yAxis.getEnd());
		Assert.assertFalse(xAxis.getStart() > xAxis.getEnd());
	}
	
	
	@Test
	public void TimeAndNonTimeInvertedTimeOnX() {	
		GregorianCalendar time = new GregorianCalendar();
		GregorianCalendar endTime = new GregorianCalendar();
		endTime.add(Calendar.MINUTE, 10);
		
		PlotView testPlot = new PlotView.Builder(PlotterPlot.class)
		.nonTimeAxisMinSubsequentSetting(NonTimeAxisSubsequentBoundsSetting.SEMI_FIXED)
		.nonTimeAxisMaxSubsequentSetting(NonTimeAxisSubsequentBoundsSetting.SEMI_FIXED)
		.nonTimeVaribleAxisMaxValue(10)
		.nonTimeVaribleAxisMinValue(0)
	    .timeVariableAxisMinValue(time.getTimeInMillis())
	    .timeVariableAxisMaxValue(endTime.getTimeInMillis())
	    .axisOrientation(AxisOrientationSetting.X_AXIS_AS_TIME)
	     .xAxisMaximumLocation(XAxisMaximumLocationSetting.MAXIMUM_AT_LEFT)
	    .yAxisMaximumLocation(YAxisMaximumLocationSetting.MAXIMUM_AT_BOTTOM)
		.build();
		testPlot.setManifestation(mockPlotViewManifestation);
		
		PlotterPlot qcPlot = (PlotterPlot) testPlot.returnPlottingPackage();
		
		XYAxis xAxis = qcPlot.plotView.getXAxis();
		XYAxis yAxis = qcPlot.plotView.getYAxis();
		Assert.assertTrue(yAxis.getStart() > yAxis.getEnd());
		Assert.assertTrue(xAxis.getStart() > xAxis.getEnd());
		
		testPlot.addDataSet("DataSet1");
		testPlot.addData("DataSet1", time.getTimeInMillis(), 1);
		
		Assert.assertTrue(yAxis.getStart() > yAxis.getEnd());
		Assert.assertTrue(xAxis.getStart() > xAxis.getEnd());
		
		time.add(Calendar.MINUTE, 1);
		testPlot.addData("DataSet1", time.getTimeInMillis(), 11);
		
		Assert.assertTrue(yAxis.getStart() > yAxis.getEnd());
		Assert.assertTrue(xAxis.getStart() > xAxis.getEnd());
		
		
		time.add(Calendar.MINUTE, 20);
		testPlot.addData("DataSet1", time.getTimeInMillis(), 50);
		
		Assert.assertTrue(yAxis.getStart() > yAxis.getEnd());
		Assert.assertTrue(xAxis.getStart() > xAxis.getEnd());
	}
	
	@Test
	public void TimeAndNonTimeInvertedTimeOnY() {	
		GregorianCalendar time = new GregorianCalendar();
		GregorianCalendar endTime = new GregorianCalendar();
		endTime.add(Calendar.MINUTE, 10);
		
		PlotView testPlot = new PlotView.Builder(PlotterPlot.class)
		.nonTimeAxisMinSubsequentSetting(NonTimeAxisSubsequentBoundsSetting.SEMI_FIXED)
		.nonTimeAxisMaxSubsequentSetting(NonTimeAxisSubsequentBoundsSetting.SEMI_FIXED)
		.nonTimeVaribleAxisMaxValue(10)
		.nonTimeVaribleAxisMinValue(0)
	    .timeVariableAxisMinValue(time.getTimeInMillis())
	    .timeVariableAxisMaxValue(endTime.getTimeInMillis())
	    .axisOrientation(AxisOrientationSetting.Y_AXIS_AS_TIME)
	     .xAxisMaximumLocation(XAxisMaximumLocationSetting.MAXIMUM_AT_LEFT)
	    .yAxisMaximumLocation(YAxisMaximumLocationSetting.MAXIMUM_AT_BOTTOM)
		.build();
		testPlot.setManifestation(mockPlotViewManifestation);
		
		PlotterPlot qcPlot = (PlotterPlot) testPlot.returnPlottingPackage();
		
		XYAxis xAxis = qcPlot.plotView.getXAxis();
		XYAxis yAxis = qcPlot.plotView.getYAxis();
		Assert.assertTrue(yAxis.getStart() > yAxis.getEnd());
		Assert.assertTrue(xAxis.getStart() > xAxis.getEnd());
		
		testPlot.addDataSet("DataSet1");
		testPlot.addData("DataSet1", time.getTimeInMillis(), 1);
		
		Assert.assertTrue(yAxis.getStart() > yAxis.getEnd());
		Assert.assertTrue(xAxis.getStart() > xAxis.getEnd());
		
		time.add(Calendar.MINUTE, 1);
		testPlot.addData("DataSet1", time.getTimeInMillis(), 11);
		
		Assert.assertTrue(yAxis.getStart() > yAxis.getEnd());
		Assert.assertTrue(xAxis.getStart() > xAxis.getEnd());
		
		
		time.add(Calendar.MINUTE, 20);
		testPlot.addData("DataSet1", time.getTimeInMillis(), 50);
		
		Assert.assertTrue(yAxis.getStart() > yAxis.getEnd());
		Assert.assertTrue(xAxis.getStart() > xAxis.getEnd());
	}
	
	@Test
	public void NothingInverstedTimeOnX() {
		GregorianCalendar time = new GregorianCalendar();
		GregorianCalendar endTime = new GregorianCalendar();
		endTime.add(Calendar.MINUTE, 10);
		
		PlotView testPlot = new PlotView.Builder(PlotterPlot.class)
		.nonTimeAxisMinSubsequentSetting(NonTimeAxisSubsequentBoundsSetting.SEMI_FIXED)
		.nonTimeAxisMaxSubsequentSetting(NonTimeAxisSubsequentBoundsSetting.SEMI_FIXED)
		.nonTimeVaribleAxisMaxValue(10)
		.nonTimeVaribleAxisMinValue(0)
	    .timeVariableAxisMinValue(time.getTimeInMillis())
	    .timeVariableAxisMaxValue(endTime.getTimeInMillis())
	    .axisOrientation(AxisOrientationSetting.X_AXIS_AS_TIME)
		.build();
		testPlot.setManifestation(mockPlotViewManifestation);
		
		PlotterPlot qcPlot = (PlotterPlot) testPlot.returnPlottingPackage();
		
		XYAxis xAxis = qcPlot.plotView.getXAxis();
		XYAxis yAxis = qcPlot.plotView.getYAxis();
		Assert.assertFalse(yAxis.getStart() > yAxis.getEnd());
		Assert.assertFalse(xAxis.getStart() > xAxis.getEnd());
		
		testPlot.addDataSet("DataSet1");
		testPlot.addData("DataSet1", time.getTimeInMillis(), 1);
		
		Assert.assertFalse(yAxis.getStart() > yAxis.getEnd());
		Assert.assertFalse(xAxis.getStart() > xAxis.getEnd());
		
		time.add(Calendar.MINUTE, 1);
		testPlot.addData("DataSet1", time.getTimeInMillis(), 11);
		
		Assert.assertFalse(yAxis.getStart() > yAxis.getEnd());
		Assert.assertFalse(xAxis.getStart() > xAxis.getEnd());
		
		
		time.add(Calendar.MINUTE, 20);
		testPlot.addData("DataSet1", time.getTimeInMillis(), 50);
		
		Assert.assertFalse(yAxis.getStart() > yAxis.getEnd());
		Assert.assertFalse(xAxis.getStart() > xAxis.getEnd());
	}
	
	@Test
	public void NothingInverstedTimeOnY() {
		GregorianCalendar time = new GregorianCalendar();
		GregorianCalendar endTime = new GregorianCalendar();
		endTime.add(Calendar.MINUTE, 10);
		
		PlotView testPlot = new PlotView.Builder(PlotterPlot.class)
		.nonTimeAxisMinSubsequentSetting(NonTimeAxisSubsequentBoundsSetting.SEMI_FIXED)
		.nonTimeAxisMaxSubsequentSetting(NonTimeAxisSubsequentBoundsSetting.SEMI_FIXED)
		.nonTimeVaribleAxisMaxValue(10)
		.nonTimeVaribleAxisMinValue(0)
	    .timeVariableAxisMinValue(time.getTimeInMillis())
	    .timeVariableAxisMaxValue(endTime.getTimeInMillis())
	    .axisOrientation(AxisOrientationSetting.Y_AXIS_AS_TIME)
		.build();
		testPlot.setManifestation(mockPlotViewManifestation);
		
		PlotterPlot qcPlot = (PlotterPlot) testPlot.returnPlottingPackage();
		
		XYAxis xAxis = qcPlot.plotView.getXAxis();
		XYAxis yAxis = qcPlot.plotView.getYAxis();
		Assert.assertFalse(yAxis.getStart() > yAxis.getEnd());
		Assert.assertFalse(xAxis.getStart() > xAxis.getEnd());
		
		testPlot.addDataSet("DataSet1");
		testPlot.addData("DataSet1", time.getTimeInMillis(), 1);
		
		Assert.assertFalse(yAxis.getStart() > yAxis.getEnd());
		Assert.assertFalse(xAxis.getStart() > xAxis.getEnd());
		
		time.add(Calendar.MINUTE, 1);
		testPlot.addData("DataSet1", time.getTimeInMillis(), 11);
		
		Assert.assertFalse(yAxis.getStart() > yAxis.getEnd());
		Assert.assertFalse(xAxis.getStart() > xAxis.getEnd());
		
		
		time.add(Calendar.MINUTE, 20);
		testPlot.addData("DataSet1", time.getTimeInMillis(), 50);
		
		Assert.assertFalse(yAxis.getStart() > yAxis.getEnd());
		Assert.assertFalse(xAxis.getStart() > xAxis.getEnd());
	}
	
}
