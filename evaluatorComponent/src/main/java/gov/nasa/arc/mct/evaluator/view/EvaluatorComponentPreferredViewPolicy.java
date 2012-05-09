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
package gov.nasa.arc.mct.evaluator.view;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.evaluator.component.EvaluatorComponent;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.Policy;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.util.Arrays;
import java.util.List;

public class EvaluatorComponentPreferredViewPolicy implements Policy {
	private static final List<String> COMPONENTS_WITH_PREFERRED_VIEW = Arrays
			.asList(EvaluatorComponent.class.getName());

	@Override
	public ExecutionResult execute(PolicyContext context) {
		ExecutionResult er = new ExecutionResult(context, true, "");

		ViewType viewType = context.getProperty(PolicyContext.PropertyName.VIEW_TYPE.getName(),ViewType.class);
		
		// return false if the specified view should be the preferred view
		boolean isInspectionView = 
			ViewType.OBJECT.equals(viewType)
				|| ViewType.CENTER.equals(viewType);

		er.setStatus(!(isInspectionView && isDefaultView(context, viewType) && componentHasDefault(context)));

		return er;
	}

	private boolean isDefaultView(PolicyContext context, ViewType viewType) {
		ViewInfo vr = context.getProperty(
				PolicyContext.PropertyName.TARGET_VIEW_INFO.getName(),
				ViewInfo.class);

		return (ViewType.OBJECT.equals(viewType) && InfoViewManifestation.class.equals(vr.getViewClass()));
	}

	private boolean componentHasDefault(PolicyContext context) {
		AbstractComponent ac = context.getProperty(
				PolicyContext.PropertyName.TARGET_COMPONENT.getName(),
				AbstractComponent.class);

		return COMPONENTS_WITH_PREFERRED_VIEW.contains(ac.getClass().getName());
	}
}
