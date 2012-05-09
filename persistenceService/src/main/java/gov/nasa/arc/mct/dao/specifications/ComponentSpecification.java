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
 * ComponentSpecification.java Sep 24, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.dao.specifications;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.ExtendedProperties;
import gov.nasa.arc.mct.components.util.ComponentModelUtil;
import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.dao.service.CorePersistenceService;
import gov.nasa.arc.mct.lock.manager.LockManager;
import gov.nasa.arc.mct.persistence.strategy.DaoObject;
import gov.nasa.arc.mct.registry.GlobalComponentRegistry;
import gov.nasa.arc.mct.services.internal.component.Updatable;
import gov.nasa.arc.mct.util.exception.MCTRuntimeException;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXBException;

import org.hibernate.Hibernate;

/**
 * Data access object (DAO) for component_spec.
 */
public class ComponentSpecification implements DaoObject {
    private int version; // For optimistic concurrency control of hibernate.
    private String componentId;
    private String name;
    private String externalKey;
    private String componentType;
    private boolean shared = false;
    private Set<ComponentSpecification> parentComponents;
    private List<ComponentSpecification> associatedComponents;
    private Map<String, String> viewStates;
    private String owner;
    private String creator;
    private Date creationDate;
    private String modelState;
    private boolean deleted;
    private Map<String, String> tags;

    /**
     * Default constructor calls parent constructor.
     */
    public ComponentSpecification() {
        super();
    }

    /**
     * Gets the version number.
     * @return version number.
     */
    public int getVersion() {
        return version;
    }

    /**
     * Sets the version number.
     * @param version - sets local version number to instance variable.
     */
    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public Serializable getId() {
        return componentId;
    }

    /**
     * Gets the component id.
     * @return componentId - component id.
     */
    public String getComponentId() {
        return componentId;
    }

    /**
     * Sets the component id.
     * @param id - sets the local component id to instance id. 
     */
    public void setComponentId(String id) {
        this.componentId = id;
    }

    /**
     * Gets the component name.
     * @return name component 
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the component name.
     * @param name component.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the external key.
     * @return externalKey
     */
    public String getExternalKey() {
        return externalKey;
    }
    
    /**
     * Sets the external key.
     * @param key - external key
     */
    public void setExternalKey(String key) {
        externalKey = key;
    }

    /**
     * Gets the parent component specs.
     * @return parentComponents - set of component specs.
     */
    public Set<ComponentSpecification> getParentComponents() {
        return parentComponents;
    }

    /**
     * Checks whether component has a parent or not.
     * @return boolean - flag check whether componet has parents or not.
     */
    public boolean hasNoParent() {
        return parentComponents == null || parentComponents.isEmpty();
    }

    /**
     * Sets the parent components.
     * @param parentComponents - set of component specs.
     */
    public void setParentComponents(Set<ComponentSpecification> parentComponents) {
        this.parentComponents = parentComponents;
    }

    /**
     * Adds parent component.
     * @param parentComponent - ComponentSpecification.
     */
    public void addParentComponent(ComponentSpecification parentComponent) {
        if (parentComponent != null) {
            if (this.parentComponents == null) {
                this.parentComponents = new HashSet<ComponentSpecification>();
            }
            this.parentComponents.add(parentComponent);
        }
    }

    /**
     * Gets the assoiciated components.
     * @return list of component specification.
     */
    public List<ComponentSpecification> getAssociatedComponents() {
        return associatedComponents;
    }

    /**
     * Sets the associated components.
     * @param associatedComponents - list of component specs.
     */
    public void setAssociatedComponents(List<ComponentSpecification> associatedComponents) {
        this.associatedComponents = associatedComponents;
    }

    /**
     * Adds to assoiciated component.
     * @param associatedComponent - Component spec.
     */
    public void addAssociatedComponent(ComponentSpecification associatedComponent) {
        addAssociatedComponent(-1, associatedComponent);
    }

    /**
     * Adds to the associated component.
     * @param childIndex - the child index.
     * @param associatedComponent - the associated component.
     */
    public void addAssociatedComponent(int childIndex, ComponentSpecification associatedComponent) {
        if (associatedComponent != null) {
            if (associatedComponents == null) {
                this.associatedComponents = new ArrayList<ComponentSpecification>();
            }
            // Remove an existing copy, if the component is already in the list.
            int currentPosition = this.associatedComponents.indexOf(associatedComponent);
            if (currentPosition >= 0) {
                this.associatedComponents.remove(currentPosition);
                if (currentPosition < childIndex) {
                    --childIndex;
                }
            }

            if ((this.associatedComponents.size() > 0) && (childIndex >= 0)) {
                this.associatedComponents.add(childIndex, associatedComponent);
            } else {
                this.associatedComponents.add(associatedComponent);
            }
        }
    }

    /**
     * Removes the associated component.
     * @param associatedComponent - the associated component.
     */
    public void removeAssociatedComponent(ComponentSpecification associatedComponent) {
        if (associatedComponent != null) {
            if (associatedComponents == null) {
                this.associatedComponents = new ArrayList<ComponentSpecification>();
            }
            this.associatedComponents.remove(associatedComponent);
        }
    }

    /**
     * Gets the view states.
     * @return viewStates - map of view states.
     */
    public Map<String, String> getViewStates() {
        return viewStates;
    }

    /**
     * Sets the view states.
     * @param viewStates - map of view states.
     */
    public void setViewStates(Map<String, String> viewStates) {
        this.viewStates = viewStates;
    }

    /**
     * Adds to the view state.
     * @param viewType - string classname view type.
     * @param viewInfo - string classname view info.
     */
    public void addViewState(String viewType, String viewInfo) {
        if (viewStates == null) {
            this.viewStates = new HashMap<String, String>();
        }
        viewStates.put(viewType, viewInfo);
    }

    /**
     * Sets the view state.
     * @param viewType - string classname view type.
     * @param properties - extended properties.
     */
    public void setViewState(String viewType, ExtendedProperties properties) {
        if (viewStates == null) {
            this.viewStates = new HashMap<String, String>();
        }
        try {
            viewStates.put(viewType, CorePersistenceService.marshal(properties));
        } catch (JAXBException e) {
            throw new MCTRuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new MCTRuntimeException(e);
        }        
    }

    /**
     * Get view info.
     * @return viewInfo - map of extended properties
     */
    public Map<String, ExtendedProperties> getViewInfo() {
        if (viewStates == null) {
            this.viewStates = new HashMap<String, String>();
        }
        Map<String, ExtendedProperties> viewInfo = new HashMap<String, ExtendedProperties>(viewStates.size());
        for (String viewRoleType : viewStates.keySet()) {
            String viewState = viewStates.get(viewRoleType);
            ExtendedProperties props;
            try {
                props = CorePersistenceService.unmarshal(ExtendedProperties.class, viewState.getBytes("ASCII"));
                viewInfo.put(viewRoleType, props);
            } catch (DataBindingException e) {
                throw new MCTRuntimeException(e);
            } catch (UnsupportedEncodingException e) {
                throw new MCTRuntimeException(e);
            } catch (JAXBException e) {
                throw new MCTRuntimeException(e);
            }
        }
        return viewInfo;
    }

    /**
     * Gets the component type.
     * @return componentType - component type.
     */
    public String getComponentType() {
        return componentType;
    }

    /**
     * Sets the component type.
     * @param componentType - the component type.
     */
    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    /**
     * Checks for is shared.
     * @return boolean - flag for is shared.
     */
    public boolean isShared() {
        return shared;
    }

    /**
     * Sets the shared flag.
     * @param shared - boolean flag.
     */
    public void setShared(boolean shared) {
        this.shared = shared;
    }

    /**
     * Returns the current MCT owner user.
     * @return owner - MCTUser.
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the owner to current MCT user.
     * @param owner - MCTUser.
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Gets the creator of this component.
     * @param creator of the component.
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }
    
    /**
     * Gets the creator of this component.
     * @return creator of this component
     */
    public String getCreator() {
        return creator;
    }
    
    /**
     * Gets the date the component was created.
     * @return creation date.
     */
    public Date getCreationDate() {
        return creationDate;
    }
    
    /**
     * Sets the creation date.
     * @param cDate the component was created.
     */
    public void setCreationDate(Date cDate) {
        creationDate = cDate;
    }
    
    /**
     * Returns the model state associated with this component specification.
     * 
     * @return modelState ModelState associated with this component
     *         specification
     */
    public String getModelState() {
        return modelState;
    }

    /**
     * Sets the model state associated with this component specification.
     * 
     * @param modelState
     *            the model state
     */
    public void setModelState(String modelState) {
        this.modelState = modelState;
    }

    /**
     * Checks for is deleted.
     * @return deleted - boolean flag.
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Sets the deleted flag.
     * @param deleted - boolean flag.
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    
    /**
     * Gets the tag property.
     * @param tagId - tag id.
     * @return tag
     */
    public String getTagProperty(String tagId) {
        if (tags == null) { return null; }
        return tags.get(tagId);
    }

    /**
     * Gets the tags.
     * @return tags - map of string
     */
    public Map<String, String> getTags() {
        return tags;
    }

    /**
     * Sets the tag.
     * @param tags - map of strings.
     */
    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }
    
    /**
     * Adds the tag id and property.
     * @param tagId - tag id.
     * @param tagProperty - tag property.
     */
    public void addTag(String tagId, String tagProperty) {
        if (tags == null) {
            tags = new HashMap<String, String>();
        }
        tags.put(tagId, tagProperty);
    }
    
    /**
     * Removes the tag.
     * @param tagId - tag id.
     */
    public void removeTag(String tagId) {
        if (tags == null) {
            return;
        }
        tags.remove(tagId);
    }
    
    /**
     * Checks for is tagged.
     * @param tagId - tag id.
     * @return boolean - flag
     */
    public boolean isTagged(String tagId) {
        return tags != null && tags.containsKey(tagId);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ComponentSpecification)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        ComponentSpecification compObj = (ComponentSpecification) obj;
        boolean returnVal = getComponentId().equals(compObj.getComponentId());
        return returnVal;
    }

    @Override
    public int hashCode() {
        return componentId.hashCode();
    }

    @Override
    public String toString() {
        return String.valueOf(componentId);
    }

    /**
     * Saves the component.
     */
    public void save() {
        GlobalContext.getGlobalContext().getLockManager().pushChanges(
                GlobalContext.getGlobalContext().getPersistenceTransactionId(componentId.toString()), this);
    }

    /**
     * Locks the DAO object.
     */
    public void lockDaoObject() {
        //
    }

    /**
     * Unlocks DAO object.
     */
    public void unlockDaoObject() {
    }

    @Override
    public void merge(DaoObject toObject) {
        if (!getClass().isAssignableFrom(toObject.getClass())) {
            return;
        }

        mergeAll(toObject, true);
    };

    private void mergeAll(DaoObject toObject, boolean mergeChildren) {
        if (!Hibernate.isInitialized(associatedComponents)) {
            return;
        }
        ComponentSpecification toCompSpec = (ComponentSpecification) toObject;

        Set<ComponentSpecification> childCompsToBeAdded = new HashSet<ComponentSpecification>();
        Set<ComponentSpecification> childCompsToBeRemoved = new HashSet<ComponentSpecification>();
        ComponentModelUtil.computeAsymmetricSetDifferences(associatedComponents, toCompSpec.getAssociatedComponents(),
                childCompsToBeAdded, childCompsToBeRemoved);

        if (mergeChildren) {
            Set<ComponentSpecification> newAddedSet = new HashSet<ComponentSpecification>();
            for (Iterator<ComponentSpecification> it = childCompsToBeAdded.iterator(); it.hasNext();) {
                ComponentSpecification childComp = it.next();
                ComponentSpecification loadedChildComp = GlobalContext.getGlobalContext()
                        .getSynchronousPersistenceBroker().loadById(this.getId().toString(),
                                ComponentSpecification.class, childComp.getId());
                if (loadedChildComp == null) {
                    continue;
                }
                newAddedSet.add(loadedChildComp);
                it.remove();
            }
            childCompsToBeAdded.addAll(newAddedSet);
        }

        LockManager lockManager = GlobalContext.getGlobalContext().getLockManager();
        if (!lockManager.isLockedForAllUsers(String.valueOf(this.componentId))) {
            toCompSpec.getAssociatedComponents().removeAll(childCompsToBeRemoved);
        }
        toCompSpec.getAssociatedComponents().addAll(childCompsToBeAdded);
    }

    @Override
    public void postFlush() {

        final AbstractComponent mctComponent = GlobalComponentRegistry.getComponent(componentId);
        mctComponent.resetComponentProperties(new AbstractComponent.ResetPropertiesTransaction() {
            
            @Override
            public void perform() {
                mctComponent.getCapability(Updatable.class).setVersion(version);        
                
            }
        });
    }
}
