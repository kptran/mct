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

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlotPersistanceHandler {
	private final static Logger logger = LoggerFactory.getLogger(PlotPersistanceHandler.class);
	
    private PlotViewManifestation plotViewManifestation;
	
	PlotPersistanceHandler(PlotViewManifestation supportedPlotViewManifestation) {
		plotViewManifestation = supportedPlotViewManifestation;
	}
	
	/**
	 * Load the settings for the manifestation from persistence.
	 * @return
	 */
	PlotSettings loadPlotSettingsFromPersistance() {

		PlotSettings settings = new PlotSettings();		

		try {
			settings.timeAxisSetting = Enum.valueOf(AxisOrientationSetting.class, plotViewManifestation.getViewProperties().getProperty(PlotConstants.TIME_AXIS_SETTING, String.class).trim().toUpperCase());
		} catch (Exception e) {
			// No persisted settings for this plot.
			return settings;
		}
	
		String pinTimeAxisAsString = plotViewManifestation.getViewProperties().getProperty(PlotConstants.PIN_TIME_AXIS, String.class);

		try {
			settings.xAxisMaximumLocation = Enum.valueOf(XAxisMaximumLocationSetting.class, plotViewManifestation.getViewProperties().getProperty(PlotConstants.X_AXIS_MAXIMUM_LOCATION_SETTING, String.class).trim().toUpperCase());
			settings.yAxisMaximumLocation = Enum.valueOf(YAxisMaximumLocationSetting.class, plotViewManifestation.getViewProperties().getProperty(PlotConstants.Y_AXIS_MAXIMUM_LOCATION_SETTING, String.class).trim().toUpperCase());
			String timeAxisSubsequent = plotViewManifestation.getViewProperties().getProperty(PlotConstants.TIME_AXIS_SUBSEQUENT_SETTING, String.class).trim().toUpperCase();
			// Support the old FIXED mode from the old plots
			if("FIXED".equals(timeAxisSubsequent)) {
				settings.timeAxisSubsequent = TimeAxisSubsequentBoundsSetting.JUMP;
				pinTimeAxisAsString = "true";
			} else {
				settings.timeAxisSubsequent = Enum.valueOf(TimeAxisSubsequentBoundsSetting.class, timeAxisSubsequent);
			}
			settings.nonTimeAxisSubsequentMinSetting = Enum.valueOf(NonTimeAxisSubsequentBoundsSetting.class, plotViewManifestation.getViewProperties().getProperty(PlotConstants.NON_TIME_AXIS_SUBSEQUENT_MIN_SETTING, String.class).trim().toUpperCase());
			settings.nonTimeAxisSubsequentMaxSetting = Enum.valueOf(NonTimeAxisSubsequentBoundsSetting.class, plotViewManifestation.getViewProperties().getProperty(PlotConstants.NON_TIME_AXIS_SUBSEQUENT_MAX_SETTING, String.class).trim().toUpperCase());
		} catch (Exception e) {
			logger.error("Problem reading plot settings back from persistence. Continuing with default settings.");
		}

		try {
			String maxTimeAsString = plotViewManifestation.getViewProperties().getProperty(PlotConstants.TIME_MAX, String.class);
			String minTimeAsString = plotViewManifestation.getViewProperties().getProperty(PlotConstants.TIME_MIN, String.class);

			String maxNonTimeAsString = plotViewManifestation.getViewProperties().getProperty(PlotConstants.NON_TIME_MAX, String.class);
			String minNonTimeAsString = plotViewManifestation.getViewProperties().getProperty(PlotConstants.NON_TIME_MIN, String.class);

			String timePaddingAsString = plotViewManifestation.getViewProperties().getProperty(PlotConstants.TIME_PADDING, String.class);

			String nonTimeMinPaddingAsString = plotViewManifestation.getViewProperties().getProperty(PlotConstants.NON_TIME_MIN_PADDING, String.class);
			String nonTimeMaxPaddingAsString = plotViewManifestation.getViewProperties().getProperty(PlotConstants.NON_TIME_MAX_PADDING, String.class);
			String groupByOrdinalPositionAsString = plotViewManifestation.getViewProperties().getProperty(PlotConstants.GROUP_BY_ORDINAL_POSITION, String.class);
			
			settings.maxTime = Long.parseLong(maxTimeAsString.trim());
			settings.minTime = Long.parseLong(minTimeAsString.trim());

			settings.maxNonTime = Double.parseDouble(maxNonTimeAsString.trim());
			settings.minNonTime = Double.parseDouble(minNonTimeAsString.trim());

			settings.timePadding = Double.parseDouble(timePaddingAsString.trim());       
			settings.nonTimeMaxPadding = Double.parseDouble(nonTimeMaxPaddingAsString.trim());        
			settings.nonTimeMinPadding = Double.parseDouble(nonTimeMinPaddingAsString.trim());    
			
			if (groupByOrdinalPositionAsString != null && !groupByOrdinalPositionAsString.isEmpty()) {
				settings.ordinalPositionForStackedPlots = Boolean.parseBoolean(groupByOrdinalPositionAsString);
			}
			
			if (pinTimeAxisAsString != null && !pinTimeAxisAsString.isEmpty()) {
				settings.pinTimeAxis = Boolean.parseBoolean(pinTimeAxisAsString);
			}

		} catch (NumberFormatException nfe) {
			logger.error("NumberFormatException: " + nfe.getMessage());
		}
		return settings;
	}
	
	

	/**
	 * Persist the plots settings.
	 * @param timeAxisSetting
	 * @param xAxisMaximumLocation
	 * @param yAxisMaximumLocation
	 * @param timeAxisSubsequentSetting
	 * @param nonTimeAxisSubsequentMinSetting
	 * @param nonTimeAxisSubsequentMaxSetting
	 * @param nonTimeMax
	 * @param nonTimeMin
	 * @param minTime
	 * @param maxTime
	 * @param timePadding
	 * @param nonTimeMaxPadding
	 * @param nonTimeMinPadding
	 */
	void persistPlotSettings(AxisOrientationSetting timeAxisSetting,
			XAxisMaximumLocationSetting xAxisMaximumLocation,
			YAxisMaximumLocationSetting yAxisMaximumLocation,
			TimeAxisSubsequentBoundsSetting timeAxisSubsequentSetting,
			NonTimeAxisSubsequentBoundsSetting nonTimeAxisSubsequentMinSetting,
			NonTimeAxisSubsequentBoundsSetting nonTimeAxisSubsequentMaxSetting,
			double nonTimeMax, double nonTimeMin, GregorianCalendar minTime,
			GregorianCalendar maxTime, 
			Double timePadding,
			Double nonTimeMaxPadding,
			Double nonTimeMinPadding, 
			boolean groupByOrdinalPosition,
			boolean timeAxisPinned) {

		ExtendedProperties viewProperties = plotViewManifestation.getViewProperties();
		viewProperties.setProperty(PlotConstants.TIME_AXIS_SETTING, "" + timeAxisSetting);
		viewProperties.setProperty(PlotConstants.X_AXIS_MAXIMUM_LOCATION_SETTING, "" + xAxisMaximumLocation);
		viewProperties.setProperty(PlotConstants.Y_AXIS_MAXIMUM_LOCATION_SETTING, "" + yAxisMaximumLocation);
		viewProperties.setProperty(PlotConstants.TIME_AXIS_SUBSEQUENT_SETTING, "" + timeAxisSubsequentSetting);
		viewProperties.setProperty(PlotConstants.NON_TIME_AXIS_SUBSEQUENT_MIN_SETTING, "" + nonTimeAxisSubsequentMinSetting);
		viewProperties.setProperty(PlotConstants.NON_TIME_AXIS_SUBSEQUENT_MAX_SETTING, "" + nonTimeAxisSubsequentMaxSetting);
		viewProperties.setProperty(PlotConstants.NON_TIME_MAX, "" + nonTimeMax);
		viewProperties.setProperty(PlotConstants.NON_TIME_MIN, "" + nonTimeMin);
		viewProperties.setProperty(PlotConstants.TIME_MIN, "" + minTime.getTimeInMillis());
		viewProperties.setProperty(PlotConstants.TIME_MAX, "" + maxTime.getTimeInMillis());
		viewProperties.setProperty(PlotConstants.TIME_PADDING, "" + timePadding);
		viewProperties.setProperty(PlotConstants.NON_TIME_MAX_PADDING, "" + nonTimeMaxPadding);
		viewProperties.setProperty(PlotConstants.NON_TIME_MIN_PADDING, "" + nonTimeMinPadding);
		viewProperties.setProperty(PlotConstants.GROUP_BY_ORDINAL_POSITION, Boolean.toString(groupByOrdinalPosition));
		viewProperties.setProperty(PlotConstants.PIN_TIME_AXIS, Boolean.toString(timeAxisPinned));
			
		if (plotViewManifestation.getManifestedComponent() != null) {
			plotViewManifestation.getManifestedComponent().save(plotViewManifestation.getInfo());
		}
	}
	
	/**
	 * Retrieve persisted feed color assignments. Each element of the returned list 
	 * corresponds, in order, to the sub-plots displayed, and maps subscription ID to 
	 * the index of the color to be assigned. 
	 * @return the persisted color assignments 
	 */
	public List<Map<String, Integer>> loadColorSettingsFromPersistence() {
		List<Map<String, Integer>> colorAssignments;

		String colorAssignmentString = plotViewManifestation.getViewProperties().getProperty(PlotConstants.COLOR_ASSIGNMENTS, String.class);
		
		if (colorAssignmentString == null) return null;
		
		StringTokenizer allAssignmentTokens = new StringTokenizer(colorAssignmentString, "\n");
		
		colorAssignments = new ArrayList<Map<String, Integer>>();
		while (allAssignmentTokens.hasMoreTokens()) {
			StringTokenizer colorAssignmentTokens = new StringTokenizer(allAssignmentTokens.nextToken(), "\t");
			
			Map<String, Integer> subPlotMap = new HashMap<String, Integer>();
			colorAssignments.add(subPlotMap);
			while (colorAssignmentTokens.hasMoreTokens()) {					
				String dataSet   = colorAssignmentTokens.nextToken();
				int colorIndex   = Integer.parseInt(colorAssignmentTokens.nextToken());
				
				subPlotMap.put(dataSet, colorIndex);
			}
		}
		
		return colorAssignments;
	}
	
	/**
	 * Persist feed color assignments. Each element of the supplied list corresponds, 
	 * in order, to the sub-plots displayed, and maps subscription ID to the index 
	 * of the color to be assigned. 
	 * @param colorAssignments the color assignments to persist. 
	 */
	public void persistColorSettings(List<Map<String, Integer>> colorAssignments) {
		/* Separate, because these are changed in a very different way from control panel settings...
		 * But should these really be separate at this level? */
		
		ExtendedProperties viewProperties = plotViewManifestation.getViewProperties();
		
		StringBuilder colorAssignmentBuilder = new StringBuilder(colorAssignments.size() * 20);
		for (Map<String, Integer> subPlotMap : colorAssignments) {
			for (Entry<String,Integer> entry : subPlotMap.entrySet()) {
				colorAssignmentBuilder.append(entry.getKey());
				colorAssignmentBuilder.append('\t');
				colorAssignmentBuilder.append(entry.getValue());
				colorAssignmentBuilder.append('\t');
			}
			colorAssignmentBuilder.append('\n');
		}
		
		viewProperties.setProperty(PlotConstants.COLOR_ASSIGNMENTS, colorAssignmentBuilder.toString());
		
		if (plotViewManifestation.getManifestedComponent() != null) {
			plotViewManifestation.getManifestedComponent().save();
		}
	}
}
