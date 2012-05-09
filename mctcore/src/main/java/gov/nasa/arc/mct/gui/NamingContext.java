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
package gov.nasa.arc.mct.gui;

/**
 * This interface defines the concept of a naming context. A naming context represents a name that can be used by a view to abbreviate
 * labels accurately. A naming context may not be present for each manifestation as this is composition dependent. 
 *
 */
public interface NamingContext {
    /**
     * Returns the parent context to used for labeling.
     * @return NamingContext that represents the parent of this context or null if there is no viable parent.
     */
    public NamingContext getParentContext();
    
    /**
     * Returns a string representing the name used by this labeling context. 
     * @return name that is currently used by this context.
     */
    public String getContextualName();
}
