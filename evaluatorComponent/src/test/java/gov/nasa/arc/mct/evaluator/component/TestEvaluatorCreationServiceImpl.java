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
package gov.nasa.arc.mct.evaluator.component;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.services.component.ComponentRegistry;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestEvaluatorCreationServiceImpl {
	@Mock
	private ComponentRegistry registry;
	private EvaluatorCreationServiceImpl service;
	@Mock
	private EvaluatorComponent mockComponent;
	private EvaluatorData data;
	
	@BeforeMethod
	public void setUp(){
		MockitoAnnotations.initMocks(this);
		service = new EvaluatorCreationServiceImpl();
		data = new EvaluatorData();
		Mockito.when(mockComponent.getData()).thenReturn(data);
		Mockito.when(registry.newInstance(EvaluatorComponent.class, null)).thenReturn(mockComponent);
	}
	
	@Test
	public void testCreationService() {
		service.setComponentRegistry(registry);
		final String languageType = "lang";
		final String code = "123";
		AbstractComponent component = service.createEvaluator(languageType, code);
		Assert.assertSame(component, mockComponent);
		
		Assert.assertEquals(data.getLanguage(), languageType);
		Assert.assertEquals(data.getCode(), code);
	}
}
