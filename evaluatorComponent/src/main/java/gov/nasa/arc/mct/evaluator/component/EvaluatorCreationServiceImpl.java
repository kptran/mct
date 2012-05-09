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
import gov.nasa.arc.mct.evaluator.api.EvaluatorCreationService;
import gov.nasa.arc.mct.services.component.ComponentRegistry;

import java.util.concurrent.atomic.AtomicReference;

public class EvaluatorCreationServiceImpl implements EvaluatorCreationService {
	private final static AtomicReference<ComponentRegistry> registry = new AtomicReference<ComponentRegistry>();
	
	public void setComponentRegistry(ComponentRegistry registry) {
		EvaluatorCreationServiceImpl.registry.set(registry);
	}
	
	public void removeComponentRegistry(ComponentRegistry registry) {
		EvaluatorCreationServiceImpl.registry.set(null);
	}
	
	@Override
	public AbstractComponent createEvaluator(String languageType, String code) {
		ComponentRegistry registry = EvaluatorCreationServiceImpl.registry.get();
		EvaluatorComponent evaluator = registry.newInstance(EvaluatorComponent.class, null);
		evaluator.getData().setLanguage(languageType);
		evaluator.getData().setCode(code);
		evaluator.save();
		
		return evaluator;
	}

}
