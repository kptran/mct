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
/**
 * ComponentRegistry.java Aug 18, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.registry;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;

import java.util.HashMap;
import java.util.Map;

/**
 * This class maintains a registry of components based on their id. This class is not intended
 * to be used by component developers and will be removed from the public API. 
 * 
 * 
 * @author asi
 * 
 */
public class GlobalComponentRegistry {
    /** Component id for the all node. This is root node in the taxonomy. */
    public static String ROOT_COMPONENT_ID;
    /** Component name for the all node. This is root node in the taxonomy. */
    public static final String ROOT_COMPONENT_NAME = "All";
    /** Display name for Disciplines node. */
    public static final String DISCIPLINES_NAME = "Disciplines";
    /** Component id of Mine, determined at component load time. */
    public static String PRIVATE_COMPONENT_ID;
    /** Display Name for mine. */
    public static final String MINE = "My Sandbox";  

    private final static GlobalComponentRegistry instance = new GlobalComponentRegistry();

    private final Map<String, AbstractComponent> dataMap = new HashMap<String, AbstractComponent>();
  
    /** A map between component type IDs and the actual component type. */
    private final Map<String, Class<? extends AbstractComponent>> componentTypes = new HashMap<String, Class<? extends AbstractComponent>>();
    
    private GlobalComponentRegistry() {
        super();
    }
    
    /**
     * Removes all registrations. This method in only intended for the MCT platform. 
     */
    public static void clearRegistry() {
        instance.clear();
    }
    
    /**
     * Registers a model class for a component. 
     * @param componentClass to register model
     */
    public static void registerComponentType(Class<? extends AbstractComponent> componentClass) {
        registerComponentType(componentClass.getName(), componentClass);
    }

    /**
     * Registers a component type and model.
     * @param componentTypeID to use for the mapping.
     * @param componentClass representing the component
     */
    public static void registerComponentType(String componentTypeID, Class<? extends AbstractComponent> componentClass) {
        instance.addComponentType(componentTypeID, componentClass);
    }
    
    /**
     * Gets the component class based on the id.
     * @param componentTypeID to 
     * @return the component class or null if the mapping does not exist
     * @throws ClassNotFoundException if the class cannot be loaded, this can occur if an OSGi bundle
     * containing the class definition has been unloaded. 
     */
    public static Class<? extends AbstractComponent> getComponentType(String componentTypeID) throws ClassNotFoundException {
        return instance.getComponentClass(componentTypeID);
    }

    /**
     * Registers an instance of a component with the platform. Registration is required 
     * as part of creating a new instance of a component. 
     * @param comp to register
     */
    public static void registerComponent(AbstractComponent comp) {
        instance.addComponent(comp);
    }

    /**
     * Gets an instance of a component based on an id. A component must have been registered using 
     * {@link #registerComponent(AbstractComponent)} prior to calling this method.
     * @param id of the component instance
     * @return instance of component or null if no component is registered
     */
    public static AbstractComponent getComponent(String id) {
        return instance.get(id);
    }
    
    /**
     * Removes the component instance from the registry. This method has no effect if 
     * there is no component with the specified id. 
     * @param id of the component instance
     */
    public static void removeComponent(String id) {
        instance.remove(id);
        PlatformAccess.getPlatform().getLockManager().removeLock(id);
    }

    private synchronized void addComponentType(String componentTypeID, Class<? extends AbstractComponent> componentClass) {
        componentTypes.put(componentTypeID, componentClass);
    }

    @SuppressWarnings("unchecked")
    private synchronized Class<? extends AbstractComponent> getComponentClass(String componentTypeID) throws ClassNotFoundException {
        Class<? extends AbstractComponent> componentClass = componentTypes.get(componentTypeID);
        if (componentClass != null) {
            return componentClass;
        } 
        
        componentClass = ExternalComponentRegistryImpl.getInstance().getComponentType(componentTypeID);
        if (componentClass != null) {
            return componentClass;
        }
        
        return (Class<? extends AbstractComponent>) Class.forName(componentTypeID, true, this.getClass().getClassLoader());
    }

    private synchronized void addComponent(AbstractComponent comp) {
        this.dataMap.put(comp.getId(), comp);
    }

    private synchronized AbstractComponent get(String id) {
        return this.dataMap.get(id);
    }
    
    private synchronized void remove(String id) {
        this.dataMap.remove(id);
    }
    
    private synchronized void clear() {
        this.dataMap.clear();
//        this.componentTypes.clear();
//        this.modelTypes.clear();
    }
    
    
}
