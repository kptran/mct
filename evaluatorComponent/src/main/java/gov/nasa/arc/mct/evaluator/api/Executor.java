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

import gov.nasa.arc.mct.components.FeedProvider;

import java.util.List;
import java.util.Map;

/**
 * The executor interface is implemented by classes that can execute evaluations based on telemetry values. This interface
 * defines common methods used across API and SPI classes. 
 * 
 * The evaluation infrastructure is currently being developed, so this interface is subject to change. 
 */
public interface Executor {
	
	
	/**
	 *  Determines if the evaluator requires multiple inputs (which are specified by the child relationships). If this 
	 *  method returns false, each child in the child relationships represents one input that can be evaluated. 
	 *  @return true if all the children need to be used for evaluation or false if only one child needs to be used as input
	 */
	boolean requiresMultipleInputs();
	
	/**
	 * Execute the evaluator with the given data values. The caller is responsible for ensuring the 
	 * required set of values is available for evaluation. The children of the evaluator represent
	 * a super set of required feed values. 
	 * @param data feed data that should be evaluated (this is the same structure that is provided in
	 * {@link gov.nasa.arc.mct.gui.FeedView#updateFromFeed(Map)}; however, the only property guaranteed
	 * to be available is {@link gov.nasa.arc.mct.components.FeedProvider#NORMALIZED_VALUE_KEY}.
	 * @param providers to be used when interacting with the feed, this can be used to change the 
	 * parameters to be used (using a supplemental identification); however, when invoking this method
	 * the general approach should use the model children
	 * @return value from executing the evaluator
	 */
	FeedProvider.RenderingInfo evaluate(Map<String,List<Map<String,String>>> data, List<FeedProvider> providers);
}
