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
package gov.nasa.arc.mct.loader;

import gov.nasa.arc.mct.components.AbstractComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements the master component loader for MCT. This is
 * called upon MCT startup to load all the programmatically-generated
 * components. We use a singleton pattern to give access to a
 * single instance of the loader.
 */
public class GlobalComponentLoader implements ComponentLoader {
    private final static GlobalComponentLoader globalLoader = new GlobalComponentLoader();
    private final List<ComponentLoader> myLoaders = new ArrayList<ComponentLoader>();

    /**
     * Gets the component loader instance.
     * 
     * @return the component loader
     */
    public static GlobalComponentLoader getLoader() {
        return globalLoader;
    }
    
    /** Private constructor to enforce the singleton pattern. */ 
    private GlobalComponentLoader() {
        // do nothing
    }
    
    /**
     * Adds a new subordinate component loader. Subordinate
     * component loaders are called when needed to load
     * their components.
     * 
     * @param loader the new subordinate loader
     */
    public void addLoader(ComponentLoader loader) {
        myLoaders.add(loader);
    }
    
    /**
     * Removes a subordinate component loader.
     * 
     * @param loader the loader to remove
     */
    public void removeLoader(ComponentLoader loader) {
        myLoaders.remove(loader);
    }
    
    @Override
    public AbstractComponent getRootComponent() {
        return myLoaders.get(0).getRootComponent();
    }

    @Override
    public void loadComponents() {
        for (ComponentLoader loader: myLoaders) {
            loader.loadComponents();
        }
    }
    
    @Override
    public void reload() {
        for (ComponentLoader loader: myLoaders) {
            loader.reload();
        }
    }
    
}
