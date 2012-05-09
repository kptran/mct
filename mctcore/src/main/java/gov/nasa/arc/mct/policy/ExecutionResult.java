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
 * Defines the execution result returned by a {@link Policy}. An <code>ExecutionResult</code>
 * contains a <code>PolicyContext</code>
 * @author nija.shi@nasa.gov
 */
public final class ExecutionResult {
    private PolicyContext context;
    private boolean status;
    private String message;
    
    /**
     * Creates an <code>ExecutionResult</code>.
     * @param context a policy context
     */
    public ExecutionResult(PolicyContext context) {
        this.setContext(context);
    }

    /**
     * Creates an <code>ExecutionResult</code>.
     * @param context the policy context
     * @param status set to false if the policy fails on a <code>PolicyContext</code>; otherwise true.
     * @param message if the policy failed, a detailed message can be provided indicating why it failed.
     */
    public ExecutionResult(PolicyContext context, boolean status, String message) {
        this.context = context;
        this.status = status;
        this.message = message;
    }
    
    private void setContext(PolicyContext context) {
        this.context = context;
    }

    /**
     * Returns a <code>PolicyContext</code>.
     * @return a policy context
     */
    public PolicyContext getContext() {
        return context;
    }
    
    /**
     * Sets the status of the policy execution result.
     * @param status indicating if a policy passed or failed on a <code>PolicyContext</code>.
     */
    public void setStatus(boolean status) {
        this.status = status;
    }
    
    /**
     * Returns the status of the policy execution result.
     * @return the status indicating if a policy passed or failed on a <code>PolicyContext</code>.
     */
    public boolean getStatus() {
        return status;
    }
    
    /**
     * Sets a detailed message for the <code>ExecutionResult</code>. This is usually used
     * to provide information indicating why the policy failed on a given <code>PolicyContext</code>.
     * This message is included in the log and dialog windows triggered by user actions. 
     * @param message for the <code>ExecutionResult</code>
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns a message from the <code>ExecutionResult</code>.
     * @return message from the <code>ExecutionResult</code>
     */
    public String getMessage() {
        return message;
    }
}
