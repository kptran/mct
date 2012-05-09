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
package gov.nasa.arc.mct.platform.spi;

import gov.nasa.arc.mct.components.AbstractComponent;

/**
 * The <code>DefaultComponentProvider</code> interface represents the various default component types that are
 * available in the system. 
 * <em>This class is not intended to be used by component authors</em>
 * @author chris.webster@nasa.gov
 */
public interface DefaultComponentProvider {
    
    /**
     * Gets the class representing a broken component. A broken component is one which cannot be loaded because 
     * a provider is no longer available to provide the component type. 
     * @return class supporting a broken component
     */
    public Class<? extends AbstractComponent> getBrokenComponent();
}
