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
package gov.nasa.arc.mct.graphics;

import gov.nasa.arc.mct.graphics.component.GraphicalComponent;
import gov.nasa.arc.mct.graphics.component.GraphicalComponentWizardUI;
import gov.nasa.arc.mct.graphics.view.GraphicalManifestation;
import gov.nasa.arc.mct.graphics.view.StaticGraphicalView;
import gov.nasa.arc.mct.policy.PolicyInfo;
import gov.nasa.arc.mct.services.component.AbstractComponentProvider;
import gov.nasa.arc.mct.services.component.ComponentTypeInfo;
import gov.nasa.arc.mct.services.component.ViewInfo;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class GraphicalComponentProviderTest {

	private AbstractComponentProvider provider;
	
	@BeforeTest
	public void setup() {
		provider = new GraphicalComponentProvider();
	}
	
	@Test
	public void testViewInfos() {
		boolean foundDynamicGraphicalView = false;
		boolean foundStaticGraphicalView = true;
		
		for (ViewInfo info : provider.getViews(null)) {
			if (info.getViewClass().equals(GraphicalManifestation.class)) foundDynamicGraphicalView = true;
			if (info.getViewClass().equals(StaticGraphicalView.class)) foundStaticGraphicalView = true;
		}
		
		Assert.assertTrue(foundDynamicGraphicalView);
		Assert.assertTrue(foundStaticGraphicalView);
	}

	@Test
	public void testPolicyInfos() {
		boolean foundPolicy = false;
		
		for (PolicyInfo info : provider.getPolicyInfos()) {
			for (Class c : info.getPolicyClasses()) {
				if (c.equals(GraphicalViewPolicy.class)) {
					foundPolicy = true;
					Assert.assertEquals(info.getCategoryKey(), PolicyInfo.CategoryType.FILTER_VIEW_ROLE.getKey());
				}
			}
			
		}
		
		Assert.assertTrue(foundPolicy);
	}
	
	@Test
	public void testComponentTypeInfos() {
		boolean foundGraphicalComponent = false;
		
		for (ComponentTypeInfo info : provider.getComponentTypes()) {
			if (info.getComponentClass().equals(GraphicalComponent.class)){
				foundGraphicalComponent = true;
				Assert.assertTrue(info.isCreatable());
				Assert.assertTrue(info.getWizardUI() instanceof GraphicalComponentWizardUI);
			}
		}
		
		Assert.assertTrue(foundGraphicalComponent);
	}
	
}
