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
package gov.nasa.arc.mct.fastplot.policy;

import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.components.FeedProvider.FeedType;
import gov.nasa.arc.mct.fastplot.view.PlotViewManifestation;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.Policy;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.services.component.ViewInfo;

public class PlotStringPolicy implements Policy {

	private static final String EM_STRING = "Can't show plot for string";

	@Override
	public ExecutionResult execute(PolicyContext context) {
		ViewInfo viewInfo = context.getProperty(
				PolicyContext.PropertyName.TARGET_VIEW_INFO.getName(),
				ViewInfo.class);

		// Check if this is a plot view
		if (viewInfo.getViewClass().equals(PlotViewManifestation.class)) {
			// Get the telemetry component
			FeedProvider provider = context.getProperty(
					PolicyContext.PropertyName.TARGET_COMPONENT.getName(),
					FeedProvider.class);

			// Check if the feed type is String. We don't want to plot or
			// graphicalmatize them
			if (provider != null) {
				if (provider.getFeedType() == FeedType.STRING) {
					return new ExecutionResult(context, false, EM_STRING);
				}
			}
		}
		return new ExecutionResult(context, true, EM_STRING);
	}
}
