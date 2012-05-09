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


/**
 * The evaluator interface is implemented by classes providing evaluation capabilities based on telemetry values. This
 * interface is designed to provide a language and execution agnostic interface, so it supports multiple
 * languages. 
 * 
 * This interface is obtained through the {@link gov.nasa.arc.mct.components.AbstractComponent#getCapability(Class)} method and
 * thus the absence of an evaluator must be considered in client code. 
 * 
 * The evaluation infrastructure is currently being developed, so this interface is subject to change. 
 */
public interface Evaluator extends Executor {
	/**
	 * Gets a human readable string for the evaluator. This is suitable for use in user interfaces. This
	 * could be the same as the component name but this is not required. 
	 * @return the string naming the evaluator
	 */
	String getDisplayName();
	
	/**
	 * Returns the language type of {@link #getCode()}. This should be unique but the burden is on the 
	 * evaluation creator to ensure evaluation providers can be discovered. 
	 * @return the string representing the language
	 */
	String getLanguage();
	
	/**
	 * Get a textual representation of the evaluator, that can be displayed for user viewing.
	 * @return the code describing the evaluation.
	 */
	String getCode();
	
	/**
	 *  Determines if the evaluator requires multiple inputs (which are specified by the child relationships). If this 
	 *  method returns false, each child in the child relationships represents one input that can be evaluated. 
	 *  @return true if all the children need to be used for evaluation or false if only one child needs to be used as input
	 */
	boolean requiresMultipleInputs();
}
