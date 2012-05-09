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
package gov.nasa.arc.mct.evaluator.spi;

import gov.nasa.arc.mct.evaluator.api.Evaluator;
import gov.nasa.arc.mct.evaluator.api.Executor;

/**
 * The evaluator provider interface is implemented by classes providing evaluation capabilities based on telemetry values. This
 * interface is designed to be supplied as an OSGI service that is used by components supplying the {@link Evaluator} interface.
 * Matching is done by iterating through the list of EvaluatorProviders and determining the first match based 
 * on language. 
 * 
 * The evaluation infrastructure is currently being developed, so this interface is subject to change. 
 */
public interface EvaluatorProvider {
	/**
	 * Returns the language type of {@link #getCode()}. This should be unique but the burden is on the 
	 * evaluation creator to ensure evaluation providers can be discovered. 
	 * @return the string representing the language
	 */
	String getLanguage();
	
	/**
	 * Takes the code to execute and verifies its correctness. 
	 * @param code to execute
	 * @return executor that can evaluate the given code. 
	 */
	Executor compile(String code); // this will eventually throw an exception to allow verification of code
}
