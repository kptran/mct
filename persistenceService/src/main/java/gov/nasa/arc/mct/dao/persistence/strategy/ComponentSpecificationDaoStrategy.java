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
 * TelemetryComponentDaoStrategy.java Sep 28, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.dao.persistence.strategy;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.ExtendedProperties;
import gov.nasa.arc.mct.components.ModelStatePersistence;
import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.dao.service.CorePersistenceService;
import gov.nasa.arc.mct.dao.service.TagServiceImpl;
import gov.nasa.arc.mct.dao.specifications.ComponentSpecification;
import gov.nasa.arc.mct.dao.specifications.MCTUser;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.lock.manager.LockManager;
import gov.nasa.arc.mct.persistence.strategy.DaoObject;
import gov.nasa.arc.mct.persistence.strategy.DaoStrategy;
import gov.nasa.arc.mct.persistence.util.HibernateUtil;
import gov.nasa.arc.mct.persistmgr.PersistenceBroker;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.registry.ExternalComponentRegistryImpl;
import gov.nasa.arc.mct.registry.GlobalComponentRegistry;
import gov.nasa.arc.mct.services.component.TagService;
import gov.nasa.arc.mct.services.internal.component.ComponentInitializer;
import gov.nasa.arc.mct.services.internal.component.User;
import gov.nasa.arc.mct.util.exception.MCTRuntimeException;
import gov.nasa.arc.mct.util.logging.MCTLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of data access object (DAO) interface that provides a strategy
 * to persist ComponentSpecification data model.
 */

public class ComponentSpecificationDaoStrategy implements DaoStrategy<AbstractComponent, ComponentSpecification> {
    private final static MCTLogger logger = MCTLogger.getLogger(ComponentSpecificationDaoStrategy.class);

    private AbstractComponent mctComp;
    private transient PersistenceBroker syncPersistenceBroker = GlobalContext.getGlobalContext()
            .getSynchronousPersistenceBroker();
    
    private TagService tagService = TagServiceImpl.getTagService();

    /**
     * Loads the abstract component based upon the component id.
     * @param componentId - The component id.
     * @return AbstractComponent
     */
    public static AbstractComponent loadComponent(String componentId) {
        ComponentSpecification compDao = GlobalContext.getGlobalContext().getSynchronousPersistenceBroker().lazilyLoad(
                componentId, ComponentSpecification.class, componentId);
        try {
            return fromDatabaseObjectToComponent(compDao, true);
        } finally {
            GlobalContext.getGlobalContext().getSynchronousPersistenceBroker().lazilyLoadCompleted(componentId);
        }
    }

    /**
     * Constructs/rehydrates a component from its saved state in the database.
     * For newly constructed components the state is saved as DAOs. If the
     * component is already in the component registry, returns the component.
     * 
     * @param compSpec
     *            ComponentSpecifcaiton DAO holding state of prospective
     *            component
     * @param requiredPublish
     *            flag indicating whether this component is to be shared
     * 
     * @return the component associated with daoOfNew. It may have been
     *         rehydrated.
     */
    public final static AbstractComponent fromDatabaseObjectToComponent(ComponentSpecification compSpec,
            boolean requiredPublish) {
        if (compSpec == null) {
            return null;
        }

        LockManager lockManager = GlobalContext.getGlobalContext().getLockManager();

        AbstractComponent mctComp = ExternalComponentRegistryImpl.getInstance().getComponent(
                String.valueOf(compSpec.getComponentId()));
        if (mctComp != null) {
            return mctComp;
        }

        String componentType = compSpec.getComponentType();
        boolean broken = false;

        Class<? extends AbstractComponent> componentClass;
        try {
            componentClass = GlobalComponentRegistry.getComponentType(componentType);
        } catch (ClassNotFoundException cnfe) {
            logger.error("Unable to load class " + componentType + ", this is caused by a missing bundle."
                    + " Add the bundle containing this component and restart MCT.");
            // this class for the component cannot be found, this is likely
            // caused by a
            // bundle configuration different from when the component is
            // created.
            // use a broken component to represent this state. The broken
            // component needs
            // to be supplied as a service so this can be created without adding
            // dependencies
            // on other bundles.
            try {
                String brokenComponentType = PlatformAccess.getPlatform().getDefaultComponentProvider()
                        .getBrokenComponent().getName();
                componentClass = GlobalComponentRegistry.getComponentType(brokenComponentType);
                broken = true;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("broken component not configured", e);
            }
        }

        try {
            mctComp = componentClass.newInstance();
            mctComp.setVersion(compSpec.getVersion());
            mctComp.setId(compSpec.getComponentId().toString());
            mctComp.setShared(compSpec.isShared());
            String displayName = compSpec.getName();
            if (broken) {
                displayName = componentType;
            }
            mctComp.setDisplayName(displayName);
            mctComp.setExternalKey(compSpec.getExternalKey());
            ComponentSpecificationDaoStrategy strategy = new ComponentSpecificationDaoStrategy(mctComp);
            mctComp.setDaoStrategy(strategy);
            String newModelState = compSpec.getModelState();
            if (newModelState != null && !broken ) {
                ModelStatePersistence persister = mctComp.getCapability(ModelStatePersistence.class);
                if (persister != null) {
                    persister.setModelState(newModelState);
                } 
            } else {
                logger.debug("Model state was not unmarshalled for componentID: {0} of type: {1}", compSpec
                                .getComponentId(), compSpec.getComponentType());
            }

            Map<String, ExtendedProperties> properties = compSpec.getViewInfo();

            ComponentInitializer initializer = mctComp.getCapability(ComponentInitializer.class);
            initializer.setViewRoleProperties(properties);
            
            mctComp.setShared(compSpec.isShared());
            mctComp.setOwner(compSpec.getOwner());
            initializer.setCreator(compSpec.getCreator());
            initializer.setCreationDate(compSpec.getCreationDate());
            
            if (!broken && !mctComp.isLeaf()) {
                initializer.setComponentReferences(Collections.<AbstractComponent> 
                    singletonList(AbstractComponent.NULL_COMPONENT));
            }

            // fully initialize the component
            initializer.initialize();

            if (requiredPublish && mctComp.isShared()) {
                if (!mctComp.share()) {
                    GlobalComponentRegistry.removeComponent(mctComp.getId());
                    // this code is exercised if publish failed, which happens
                    // only if there
                    // are multiple users which are trying to share a component
                    // at approximately
                    // the same time, where the actual objects shared would be
                    // different so this
                    // would create instances which are disconnected. For
                    // example, one user would
                    // have the instance shared in TC but the other instances
                    // would be different
                    return fromDatabaseObjectToComponent(compSpec, requiredPublish);
                }
            }

            assert initializer.isInitialized() : "component not initialized";

            if (!mctComp.isShared()) {
                lockManager.newLock(mctComp.getId());
                lockManager.lock(mctComp.getId(), View.WILD_CARD_VIEW_MANIFESTATION);
            }

            return mctComp;
        } catch (SecurityException e) {
            logger.error(e.getMessage(), e);
            throw new MCTRuntimeException(e);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
            throw new MCTRuntimeException(e);
        } catch (InstantiationException e) {
            logger.error(e.getMessage(), e);
            throw new MCTRuntimeException(e);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
            throw new MCTRuntimeException(e);
        } 
    }

    /**
     * Transforms a collection of component DAOs to a collection of components.
     * 
     * @param compSpecs
     *            collection of component DAO
     * @param ignoreSandBox
     *            if a DOA is proxy and this boolean is true, this proxy
     *            component will not be transformed to a component
     * @return mctComps - collection of abstract components
     */
    public final static List<AbstractComponent> transformTo(Collection<ComponentSpecification> compSpecs,
            boolean ignoreSandBox) {
        List<AbstractComponent> mctComps = new LinkedList<AbstractComponent>();

        for (Iterator<ComponentSpecification> i = compSpecs.iterator(); i.hasNext();) {
            ComponentSpecification compSpec = i.next();
            if (compSpec == null) continue; //Hibernate injects a NULL value for each missing seq_no.
            if (ignoreSandBox && compSpec.getName().equals(GlobalComponentRegistry.MINE)) {
                continue;
            }

            AbstractComponent mctComp = ExternalComponentRegistryImpl.getInstance().getComponent(
                    String.valueOf(compSpec.getComponentId()));
            if (mctComp == null) {
                mctComp = fromDatabaseObjectToComponent(compSpec, true);
            }

            mctComps.add(mctComp);
        }
        return mctComps;
    }

    /**
     * Transforms lazily a collection of ComponentSpecification to a list of AbstractComponent.
     * @param compSpecs - Collection of component specs DAO objects.
     * @param ignoreProxyComponent - Ignores the proxy component.
     * @return list of AbstractComponent
     */
    public final static List<AbstractComponent> lazilyTransformTo(Collection<ComponentSpecification> compSpecs,
            boolean ignoreProxyComponent) {
        PersistenceBroker persistenceBroker = GlobalContext.getGlobalContext().getSynchronousPersistenceBroker();

        List<AbstractComponent> mctComps = new ArrayList<AbstractComponent>(compSpecs.size());
        for (ComponentSpecification compSpec : compSpecs) {
            AbstractComponent mctComp = ExternalComponentRegistryImpl.getInstance().getComponent(
                    String.valueOf(compSpec.getComponentId()));
            if (mctComp == null) {
                persistenceBroker.startSession(compSpec.getComponentId().toString());
                try {
                    persistenceBroker.attachToSession(compSpec.getComponentId().toString(), compSpec);
                    mctComp = fromDatabaseObjectToComponent(compSpec, true);
                } finally {
                    persistenceBroker.closeSession(compSpec.getComponentId().toString());
                }
            }

            mctComps.add(mctComp);
        }
        return mctComps;
    }

    /**
     * Loads lazily the mine component.
     * @return mineComponent - AbstractComponent.
     */
    public static AbstractComponent loadMine() {
        User user = GlobalContext.getGlobalContext().getUser();
        PersistenceBroker syncPersistenceBroker = GlobalContext.getGlobalContext().getSynchronousPersistenceBroker();
        MCTUser mctUser = syncPersistenceBroker.lazilyLoad(user.getUserId(), MCTUser.class, user.getUserId());
        AbstractComponent mineComponent;

        try {
            List<ComponentSpecification> mineDaos = syncPersistenceBroker.loadAll(user.getUserId(),
                    ComponentSpecification.class, new String[] { "componentType", "owner" }, new Object[] {
                "gov.nasa.arc.mct.core.components.MineTaxonomyComponent", mctUser.getUserId() });
            assert mineDaos != null && mineDaos.size() > 0 : "No " + GlobalComponentRegistry.MINE + " found for user "
                    + user.getUserId();
            ComponentSpecification mineDao = mineDaos.iterator().next();
            mineComponent = ComponentSpecificationDaoStrategy.fromDatabaseObjectToComponent(mineDao, false);
            GlobalComponentRegistry.PRIVATE_COMPONENT_ID = mineComponent.getComponentId();
        } finally {
            syncPersistenceBroker.lazilyLoadCompleted(mctUser.getUserId());
        }
        mineComponent.getComponents(); // Force mineComponent to load
        return mineComponent;

    }

    /**
     * Constructs a DAO strategy and sets it to a component.
     * 
     * @param mctComp
     *            the component which will own the newly created DAO strategy.
     */
    public ComponentSpecificationDaoStrategy(AbstractComponent mctComp) {
        this.mctComp = mctComp;
    }

    @Override
    public void removeObject(AbstractComponent mctComp) {
        ComponentSpecification telCompDao = syncPersistenceBroker.lazilyLoad(this.mctComp.getId(),
                ComponentSpecification.class, this.mctComp.getId());
        try {
            if (telCompDao != null) {
                DaoObject daoObject = mctComp.getDaoStrategy().getDaoObject(this.mctComp.getId());

                if (!hasNoParent(daoObject)) {
                    if (daoObject instanceof ComponentSpecification) {
                        telCompDao.removeAssociatedComponent((ComponentSpecification) daoObject);
                        telCompDao.save();

                    } 
                }
            }
        } finally {
            syncPersistenceBroker.lazilyLoadCompleted(this.mctComp.getId());
        }
    }

    @Override
    public void removeObjects(Collection<AbstractComponent> mctComps) {
        if (mctComps != null && !mctComps.isEmpty()) {
            ComponentSpecification telCompDao = syncPersistenceBroker.lazilyLoad(this.mctComp.getId(),
                    ComponentSpecification.class, this.mctComp.getId());
            try {
                for (AbstractComponent mctComp : mctComps) {
                    DaoObject daoObject = syncPersistenceBroker.loadById(this.mctComp.getId(),
                            ComponentSpecification.class, mctComp.getId());

                    if (!hasNoParent(daoObject)) {
                        if (daoObject instanceof ComponentSpecification) {
                            telCompDao.removeAssociatedComponent((ComponentSpecification) daoObject);
                        } 
                    }
                }
                telCompDao.save();
            } finally {
                syncPersistenceBroker.lazilyLoadCompleted(this.mctComp.getId());
            }
        }
    }

    @Override
    public final void saveObjects(int childIndex, Collection<AbstractComponent> mctComps) {
        if (mctComps == null || mctComps.size() == 0) {
            return;
        }

        if (mctComps.size() == 1) {
            saveObject(childIndex, mctComps.iterator().next());
            return;
        }

        ComponentSpecification telCompDao = syncPersistenceBroker.lazilyLoad(
                this.mctComp.getId(), ComponentSpecification.class, this.mctComp.getId());
        try {
            for (AbstractComponent mctComp : mctComps) {
                checkInitialized(mctComp);
                DaoObject daoObject = toDaoObject(childIndex, telCompDao, mctComp, this.mctComp.getId());
                if (telCompDao == null) {
                    throw new MCTRuntimeException("Component " + daoObject.toString() +" "+ mctComp.getDisplayName() + " has no parent.");
                }
                syncPersistenceBroker.persist(this.mctComp.getId(), daoObject);
                tagService.tag(mctComp.getPendingTags(), daoObject, false);
                mctComp.clearPendingTags();
            }

            telCompDao.save();
        } catch(Exception e) {
            logger.error("Exception in saveObjects: {0}", e);
        } finally {
            if (telCompDao != null) {
                syncPersistenceBroker.lazilyLoadCompleted(this.mctComp.getId(), false);
                tagService.flush();
            }
        }
    }

    @Override
    public void deleteObject(AbstractComponent mctComp) {
        checkInitialized(mctComp);
        ComponentSpecification telCompDao = syncPersistenceBroker.lazilyLoad(mctComp
                .getId(), ComponentSpecification.class, mctComp.getId());
        assert telCompDao != null;
        try {
            telCompDao.setDeleted(true);
            telCompDao.save();
        } finally {
            syncPersistenceBroker.lazilyLoadCompleted(mctComp.getId());
        }

    }

    @Override
    public final void saveObject(int childIndex, AbstractComponent mctComp) {
        DaoObject telComp = null;
        checkInitialized(mctComp);
        ComponentSpecification telCompDao = syncPersistenceBroker.lazilyLoad(
                this.mctComp.getId(), ComponentSpecification.class, this.mctComp.getId());
        try {
            telComp = toDaoObject(childIndex, telCompDao, mctComp, this.mctComp.getId());
            if (telCompDao == null) {
                throw new MCTRuntimeException("Component " + telComp.toString() +" "+ mctComp.getDisplayName() + " has no parent.");
            }
            syncPersistenceBroker.persist(this.mctComp.getId(), telComp);
            telCompDao.save();
            tagService.tag(mctComp.getPendingTags(), telComp, false);
            mctComp.clearPendingTags();

        } catch(Exception e) {
            logger.error("Exception in saveObject: {0}", e);
        } finally {
            syncPersistenceBroker.lazilyLoadCompleted(this.mctComp.getId(), false);
            tagService.flush();
        }
    }

    private void checkInitialized(AbstractComponent component) {
        if (!component.getCapability(ComponentInitializer.class).isInitialized()) {
            throw new IllegalStateException("component not initialized " + component);
        }
    }

    /*
     * Gets the persisted state of a child component and adds it to the parent
     * state. If child Dao can be loaded, then persist model states of both
     * child and parent and return.
     * 
     * If child Dao does not yet exist, create the child Dao for the first time.
     * Since bundle ID life is attached to child Doa, save its bundle Id. Also
     * persist child model state.
     * 
     * Persist parent model state.
     * 
     * @param telComDao DAO of the parent
     * 
     * @param mctComp child component whose state is to be added to its parent
     * 
     * @return the DAO of the child just added to its parent
     */
    private final DaoObject toDaoObject(int childIndex, ComponentSpecification parentComponentDao, AbstractComponent childComponent,
            String sessionId) {
        if (childComponent == null) {
            return null;
        }

        ComponentSpecification childComponentDao = syncPersistenceBroker.loadById(sessionId, ComponentSpecification.class,
                childComponent.getId());

        if (parentComponentDao != null && childComponentDao != null) {
            parentComponentDao.addAssociatedComponent(childIndex, childComponentDao);

            childComponentDao.setShared(childComponentDao.isShared() || parentComponentDao.isShared());

            marshalModelState(this.getMCTComp(), parentComponentDao);
            marshalModelState(childComponent, childComponentDao);
            marshalViewState(parentComponentDao);
            return childComponentDao;

        }

        if (childComponentDao == null) {
            MCTUser user = CorePersistenceService.getUser(sessionId, childComponent.getOwner());
            childComponentDao = new ComponentSpecification();
            childComponentDao.setComponentId(childComponent.getId());
            childComponentDao.setName(childComponent.getDisplayName());
            childComponentDao.setExternalKey(childComponent.getExternalKey());
            childComponentDao.setComponentType(childComponent.getComponentTypeID());
            childComponentDao.setOwner(user.getUserId());
            childComponentDao.setCreator(childComponent.getCreator());
            childComponentDao.setCreationDate(childComponent.getCreationDate());
            if (parentComponentDao != null) {
                childComponentDao.setShared(parentComponentDao.isShared());

                parentComponentDao.addAssociatedComponent(childIndex, childComponentDao);

            }

            ComponentSpecificationDaoStrategy daoStrategy = new ComponentSpecificationDaoStrategy(childComponent);
            childComponent.setDaoStrategy(daoStrategy);

        } 
        marshalModelState(this.getMCTComp(), parentComponentDao);
        marshalModelState(childComponent, childComponentDao);
        marshalViewState(parentComponentDao);
        return childComponentDao;
    }
    
    private void marshalViewState(ComponentSpecification telComp) {
        Map<String, ExtendedProperties> allViewProperties = mctComp.getCapability(ComponentInitializer.class).getAllViewRoleProperties();
        for (String key : allViewProperties.keySet()) {
            telComp.setViewState(key, allViewProperties.get(key));
        }        
    }

    @Override
    public Map<String, ComponentSpecification> getDaoObjects(List<AbstractComponent> comps) {
        Map<String, ComponentSpecification> daoObjects = new HashMap<String, ComponentSpecification>(comps.size());
        syncPersistenceBroker.lazilyLoad(this.mctComp.getId(), ComponentSpecification.class, this.mctComp.getId());
        try {
            for (AbstractComponent comp : comps) {
                ComponentSpecification compSpec = syncPersistenceBroker.loadById(this.mctComp.getId(),
                        ComponentSpecification.class, comp.getId());
                daoObjects.put(comp.getId(), compSpec);
            }
        } finally {
            syncPersistenceBroker.lazilyLoadCompleted(this.mctComp.getId());
        }
        return daoObjects;
    }

    @Override
    public ComponentSpecification getDaoObject() {
        return syncPersistenceBroker.loadById(this.mctComp.getId(), ComponentSpecification.class, this.mctComp.getId());
    }

    @Override
    public ComponentSpecification getDaoObject(String sessionId) {
        return syncPersistenceBroker.loadById(sessionId, ComponentSpecification.class, this.mctComp.getId());
    }

    @Override
    public AbstractComponent getMCTComp() {
        return this.mctComp;
    }

    @Override
    public void refreshDAO(AbstractComponent childComponent) {
        //
    }

    @Override
    public void refreshDAO() {
        if (syncPersistenceBroker == null) {
            syncPersistenceBroker = GlobalContext.getGlobalContext().getSynchronousPersistenceBroker();
        }
    }

    @Override
    public void load() {
        syncPersistenceBroker.startSession(this.mctComp.getId());
        try {
            ComponentSpecification telCompDao = syncPersistenceBroker.loadByIdEagerly(this.mctComp.getId(),
                    ComponentSpecification.class, this.mctComp.getId(),
                    new String[] { "associatedComponents" });

            List<ComponentSpecification> children = telCompDao.getAssociatedComponents();
            List<AbstractComponent>      childComps = new ArrayList<AbstractComponent>(children.size());
            for (ComponentSpecification child : children) {
            	// Since seq_no is list-index column, Hibernate injects a NULL value for
            	// each missing seq_no.
                if (child == null) {
                    logger.debug("{} contains a NULL child.",telCompDao.getName());
                    continue;
                }
                AbstractComponent childMCTComp = ExternalComponentRegistryImpl.getInstance().getComponent(
                        String.valueOf(child.getComponentId()));
                if (childMCTComp == null) {
                    childMCTComp = fromDatabaseObjectToComponent(child, true);
                }                
                childComps.add(childMCTComp);
            }
            mctComp.getCapability(ComponentInitializer.class).setComponentReferences(childComps);
        } finally {
            syncPersistenceBroker.closeSession(this.mctComp.getId());
        }
    }

    @Override
    public void saveObject() {
        ComponentSpecification telComp = syncPersistenceBroker.lazilyLoad(this.mctComp.getId(),
                ComponentSpecification.class, this.mctComp.getId());
        if (telComp == null) {
            telComp = new ComponentSpecification();
            telComp.setComponentId(this.mctComp.getId());
            telComp.setComponentType(mctComp.getComponentTypeID());
            telComp.setCreator(mctComp.getCreator());
            telComp.setCreationDate(new Date());
            ComponentSpecificationDaoStrategy daoStrategy = new ComponentSpecificationDaoStrategy(mctComp);
            mctComp.setDaoStrategy(daoStrategy);
        }
        try {
            telComp.setName(mctComp.getDisplayName());
            telComp.setExternalKey(mctComp.getExternalKey());
            telComp.setOwner(mctComp.getOwner());
            if (mctComp.isVersionedComponent()) {
                telComp.setShared(mctComp.getMasterComponent().isShared());
            } else {
                telComp.setShared(mctComp.isShared());
            }
            
            this.marshalModelState(mctComp, telComp);

            marshalViewState(telComp);
            
            telComp.save();
        } finally {
            syncPersistenceBroker.lazilyLoadCompleted(this.mctComp.getId());
        }
    }
    
    @Override
    public void associateDelegateSessionId(String sessionId, String delegateSessionId) {
        HibernateUtil.associateDelegateSessionId(sessionId, delegateSessionId);
    }

    private boolean hasNoParent(DaoObject telComp) {
        if (telComp instanceof ComponentSpecification) {
            return ((ComponentSpecification) telComp).hasNoParent();
        }

        return false;
    }

    /**
     * Saves the model state of a component. If this is the first time saving
     * the state, a model state DAO is created.
     * 
     * @param comp
     *            component whose state is to be saved
     * @param dao
     *            data access object for the component
     */
    private void marshalModelState(AbstractComponent comp, ComponentSpecification dao) {

        assert (comp != null);
        if (dao == null)
            return;

        ModelStatePersistence persister = comp.getCapability(ModelStatePersistence.class);
        if (persister != null) {
            dao.setModelState(persister.getModelState());
        }
    }
}
