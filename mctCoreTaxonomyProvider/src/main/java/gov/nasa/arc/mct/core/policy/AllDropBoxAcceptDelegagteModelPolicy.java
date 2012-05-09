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
package gov.nasa.arc.mct.core.policy;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.core.components.TelemetryAllDropBoxComponent;
import gov.nasa.arc.mct.core.components.TelemetryUserDropBoxComponent;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.Policy;
import gov.nasa.arc.mct.policy.PolicyContext;

import java.util.Collection;

public class AllDropBoxAcceptDelegagteModelPolicy implements Policy {

    @Override
    public ExecutionResult execute(PolicyContext context) {
        ExecutionResult trueResult = new ExecutionResult(context, true, "");

        if (!checkArguments(context))
            return trueResult; // pass it over to the next policy in the chain
        
        AbstractComponent targetComponent = (AbstractComponent) context.getProperty(PolicyContext.PropertyName.TARGET_COMPONENT
                .getName());
        
        @SuppressWarnings("unchecked")
        Collection<AbstractComponent> sourceComponents = context.getProperty(PolicyContext.PropertyName.SOURCE_COMPONENTS.getName(), Collection.class);
        
        if (TelemetryAllDropBoxComponent.class.isAssignableFrom(targetComponent.getClass())) {
            for (AbstractComponent sourceComponent : sourceComponents) {
                if (!TelemetryUserDropBoxComponent.class.isAssignableFrom(sourceComponent.getClass())) {
                    return new ExecutionResult(context, false, "");
                }
            }
            return trueResult;
        }
        
        return new ExecutionResult(context, false, "");
    }
    
    /*
     * Checks whether context contains the correct set of arguments to process
     * this policy.
     */
    private boolean checkArguments(PolicyContext context) {
        Object targetComponentProp = context.getProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName());
        if (targetComponentProp == null)
            return false;

        if (!(targetComponentProp instanceof TelemetryAllDropBoxComponent))
            return false;
        
        Object sourceComponentProp = context.getProperty(PolicyContext.PropertyName.SOURCE_COMPONENTS.getName());
        if (sourceComponentProp == null) 
            return false;

        return true;
    }
}
