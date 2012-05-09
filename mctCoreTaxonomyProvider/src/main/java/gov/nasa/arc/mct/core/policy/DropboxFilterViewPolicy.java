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
import gov.nasa.arc.mct.core.roles.DropboxCanvasView;
import gov.nasa.arc.mct.platform.core.access.PlatformAccess;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.Policy;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.util.Arrays;

public final class DropboxFilterViewPolicy implements Policy {
    
    private final String[] dropBoxComponentTypes = {
            TelemetryAllDropBoxComponent.class.getName(),
            TelemetryUserDropBoxComponent.class.getName()};

    @Override
    public ExecutionResult execute(PolicyContext context) {
        ExecutionResult trueResult = new ExecutionResult(context, true, "");

        AbstractComponent component = context.getProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName(), AbstractComponent.class);        
        if (!checkArguments(context, component))
            return trueResult; // pass it over to the next policy in the chain        

        ViewType viewType = context
                .getProperty(PolicyContext.PropertyName.VIEW_TYPE.getName(), ViewType.class);
        ViewInfo targetViewInfo = context.getProperty(PolicyContext.PropertyName.TARGET_VIEW_INFO.getName(), ViewInfo.class);
        if (viewType == ViewType.OBJECT || viewType == ViewType.CENTER) {
            if (hasPermission(component)) {
                if (DropboxCanvasView.class.isAssignableFrom(targetViewInfo.getViewClass()))
                    return new ExecutionResult(context, false, "");
            } else {
                if (!(DropboxCanvasView.class.isAssignableFrom(targetViewInfo.getViewClass())))
                    return new ExecutionResult(context, false, "");
            }

        }
        
        return trueResult;
    }
    
    private boolean hasPermission(AbstractComponent component) {        
        return PlatformAccess.getPlatform().getCurrentUser().getUserId().equals(component.getOwner());
    }
    
    /*
     * Checks whether context contains the correct set of arguments to process this policy.
     */
    private boolean checkArguments(PolicyContext context, AbstractComponent component) {
        if (context.getProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName(), AbstractComponent.class) == null)
            return false;

        if (!Arrays.asList(dropBoxComponentTypes).contains(component.getClass().getName()))
            return false;
        
        return !(context.getProperty(PolicyContext.PropertyName.VIEW_TYPE.getName(), ViewType.class) != ViewType.CENTER
                && context.getProperty(PolicyContext.PropertyName.VIEW_TYPE.getName(), ViewType.class) != ViewType.OBJECT);
    }

}
