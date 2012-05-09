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
package gov.nasa.arc.mct.policy;

/**
 * Defines the interface for a policy. 
 * @author nija.shi@nasa.gov
 */
public interface Policy {
    /**
     * Implements the policy execution on a given <code>PolicyContext</code>.
     * The <code>ExecutionResult</code> should include a boolean status indicating 
     * if a policy passed or failed on a <code>PolicyContext</code>. A text message 
     * should be included if the policy failed, since this message will be used in 
     * the log and dialog windows triggered by a user action. 
     * @param context the policy context
     * @return the result of the policy execution
     */
    public ExecutionResult execute(PolicyContext context);

}
