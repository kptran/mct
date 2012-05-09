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
package gov.nasa.arc.mct.evaluator.api;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.evaluator.spi.EvaluatorProvider;

/**
 * The <code>EvaluatorCreationService</code> provides a service for creating
 * evaluator instances from outside this module. This prevents the need for
 * exposing the component and model classes.
 * 
 * The evaluation infrastructure is currently being developed, so this interface
 * is subject to change.
 */

public interface EvaluatorCreationService {
	/**
	 * Create an evaluator with the specified language type and code. This method is intended to be used by
	 * {@link EvaluatorProvider} bundles to create language specific evaluator components. 
	 * @param languageType type of language to use. This should correspond to a serivce where the {@link EvaluatorProvider#getLanguage()} 
	 * equals this language.
	 * @param code to evaluate. 
	 * @return the evaluator component
	 */
	AbstractComponent createEvaluator(String languageType, String code);
}
