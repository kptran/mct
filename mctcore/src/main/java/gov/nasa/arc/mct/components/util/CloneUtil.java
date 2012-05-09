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
package gov.nasa.arc.mct.components.util;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.ExtendedProperties;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.services.internal.component.ComponentInitializer;
import gov.nasa.arc.mct.util.exception.MCTRuntimeException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enum for clone utility.
 *
 */
public enum CloneUtil {

    /** 
     * Clones an AbstractComponent by version.
     */
    VERSION() {

        @Override
        public AbstractComponent clone(AbstractComponent component) {
            try {
                AbstractComponent clonedComponent = component.clone();
                clonedComponent.getCapability(ComponentInitializer.class).setId(component.getId());
                Field field = AbstractComponent.class.getDeclaredField("masterComponent");
                field.setAccessible(true);
                field.set(clonedComponent, component);

                return clonedComponent;
            } catch (SecurityException e) {
                throw new MCTRuntimeException(e);
            } catch (NoSuchFieldException e) {
                throw new MCTRuntimeException(e);
            } catch (IllegalArgumentException e) {
                throw new MCTRuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new MCTRuntimeException(e);
            } 
        }

    },

    /**
     * Clones an AbstractComponent by duplication.
     */
    DUPLICATE() {

        @Override
        public AbstractComponent clone(AbstractComponent component) {
            return component.clone();
        }

    },
    
    /**
     * Clones an AbstractComponent by twiddle.
     */
    TWIDDLE() {

        @Override
        public AbstractComponent clone(AbstractComponent component) {
            try {
                AbstractComponent clonedComponent = component.clone();
                Field field = AbstractComponent.class.getDeclaredField("masterComponent");
                field.setAccessible(true);
                field.set(clonedComponent, component);
                
                ComponentInitializer clonedCapability = clonedComponent.getCapability(ComponentInitializer.class);
                clonedComponent.setOwner(PlatformAccess.getPlatform().getCurrentUser().getUserId());

                // Clone view role properties
                ComponentInitializer masterCapability = component.getCapability(ComponentInitializer.class);
                for (Entry<String, ExtendedProperties> extendedPropertiesEntry : masterCapability.getAllViewRoleProperties().entrySet()) {
                    ExtendedProperties clonedProperties = new ExtendedProperties();                                    
                    for (Entry<String, Set<Object>> setEntry: extendedPropertiesEntry.getValue().getAllProperties().entrySet()) {            
                        for (Object obj : setEntry.getValue()) {
                            Object clonedObject = null;
                            try {
                                clonedObject = obj.getClass().getMethod("clone", new Class<?>[0]).invoke(obj, new Object[0]);
                            } catch (IllegalArgumentException e1) {
                                LOGGER.warn(e1.getMessage(), e1);
                            } catch (SecurityException e1) {
                                LOGGER.warn(e1.getMessage(), e1);
                            } catch (IllegalAccessException e1) {
                                LOGGER.warn(e1.getMessage(), e1);
                            } catch (InvocationTargetException e1) {
                                LOGGER.warn(e1.getMessage(), e1);
                            } catch (NoSuchMethodException e1) {
                                LOGGER.info(e1.getMessage(), e1);
                            }
                            
                            clonedProperties.addProperty(setEntry.getKey(), clonedObject == null ? obj : clonedObject);
                        }
                        clonedCapability.setViewRoleProperty(extendedPropertiesEntry.getKey(), clonedProperties);
                    }
                    
                }
                
                clonedComponent.setDaoStrategy(NullDaoStrategy.getInstance());

                return clonedComponent;
                } catch (SecurityException e) {
                    throw new MCTRuntimeException(e);
                } catch (NoSuchFieldException e) {
                    throw new MCTRuntimeException(e);
                } catch (IllegalArgumentException e) {
                    throw new MCTRuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new MCTRuntimeException(e);
                }
        }
        
    };

    /**
     * Abstract clone utility.
     * @param component - Pass in AbstractComponent.
     * @return AbstractComponent - Returns the cloned AbstractComponent object in concrete method.
     */
    public abstract AbstractComponent clone(AbstractComponent component);
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CloneUtil.class);

}
