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
package gov.nasa.arc.mct.services.internal.persistence.impl;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.ExtendedProperties;
import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.dao.persistence.strategy.ComponentSpecificationDaoStrategy;
import gov.nasa.arc.mct.dao.service.CorePersistenceService;
import gov.nasa.arc.mct.dao.service.CorePersistenceService.ChangedComponentVisitor;
import gov.nasa.arc.mct.dao.service.QueryResult;
import gov.nasa.arc.mct.dao.specifications.ComponentSpecification;
import gov.nasa.arc.mct.dao.specifications.Discipline;
import gov.nasa.arc.mct.dao.specifications.MCTUser;
import gov.nasa.arc.mct.persistence.strategy.DaoObject;
import gov.nasa.arc.mct.persistence.strategy.DaoStrategy;
import gov.nasa.arc.mct.persistence.util.HibernateUtil;
import gov.nasa.arc.mct.persistmgr.PersistenceBroker;
import gov.nasa.arc.mct.platform.spi.DuplicateUserException;
import gov.nasa.arc.mct.platform.spi.PersistenceService;
import gov.nasa.arc.mct.platform.spi.Platform;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.registry.GlobalComponentRegistry;
import gov.nasa.arc.mct.services.component.ProviderDelegate;
import gov.nasa.arc.mct.services.internal.component.Updatable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.NonUniqueResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * External persistence service implementation.
 *
 */
public class ExternalPersistenceServiceImpl implements PersistenceService {
        
	private static final String ADD_USER_SESSION = "ADD_USER_SESSION";
	private static final Logger LOGGER = LoggerFactory.getLogger(ExternalPersistenceServiceImpl.class);

    @Override
	public Collection<String> getAllDisciplines() {
		return CorePersistenceService.getAllDisciplines();
	}
	
    @Override
    public Set<String> getAllUsers() {
        return CorePersistenceService.getAllUsers();
    }
    
	@Override
	public Collection<String> getAllUsersOfDiscipline(String disciplineId) {
		return CorePersistenceService.getAllUsersOfDiscipline(disciplineId);
	}
	
	@Override
	public AbstractComponent loadComponent(String componentId) {
        return CorePersistenceService.loadComponent(componentId);
    }
  
    @Override
    public void setComponentDaoStrategy(AbstractComponent mctComp) {
        mctComp.setDaoStrategy(new ComponentSpecificationDaoStrategy(mctComp));
    }

    @SuppressWarnings("unchecked")
    @Override
    public AbstractComponent findPUI(String pui) {
        QueryResult result = CorePersistenceService.searchTelemetryMeta(pui);
        int count = result.getCount();
        if (count == 0)
            return null;
        if (count > 1) {
            throw new RuntimeException("Duplicate:"+pui,new NonUniqueResultException(count));
        }
        
        List<ComponentSpecification> records = (List<ComponentSpecification>) result.getRecords();
        List<AbstractComponent> components = ComponentSpecificationDaoStrategy.lazilyTransformTo(records, false);
        return components.get(0);
    }
    
    
    @Override
    public Collection<AbstractComponent> getReferences(AbstractComponent component) {
        Collection<ComponentSpecification> refs = CorePersistenceService.getReferencingComponents(component.getComponentId());
        return ComponentSpecificationDaoStrategy.lazilyTransformTo(refs, false);
    }

    @Override
    public void addNewUser(String userId, String group) throws DuplicateUserException, InterruptedException {
        String id = ADD_USER_SESSION;

        boolean success = false;
        PersistenceBroker synchronousPersistenceBroker = GlobalContext.getGlobalContext().getSynchronousPersistenceBroker();        
        Platform platform = PlatformAccess.getPlatform();
        Collection<ProviderDelegate> delegates = platform.getProviderDelegateService().getDelegates();

        DuplicateUserException duplicateUserException = null;
        try {
            synchronousPersistenceBroker.startSession(id);
            if (CorePersistenceService.getUser(id, userId) != null)
                duplicateUserException = new DuplicateUserException(userId);
            
            MCTUser user = new MCTUser();
            user.setUserId(userId);
            
            Discipline discipline = CorePersistenceService.getDispline(group, id);
            user.setDiscipline(discipline);

            synchronousPersistenceBroker.save(id, user, null);
            
            platform.getLockManager().lock(id);
            try {
                for (ProviderDelegate delegate : delegates)
                    delegate.userAdded(id, userId, group);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }finally {
                platform.getLockManager().unlock(id);
            }
            success = true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (success) {
                synchronousPersistenceBroker.closeSession(id);
                for (ProviderDelegate delegate : delegates)
                    delegate.userAddedSuccessful(userId, group);
            } else {
                synchronousPersistenceBroker.abortSession(id);
                if (platform.getLockManager().isLocked(id))
                    platform.getLockManager().unlock(id);
                for (ProviderDelegate delegate : delegates)
                    delegate.userAddedFailed(userId, group);
                if (duplicateUserException != null)
                    throw duplicateUserException;
                else
                    throw new InterruptedException();
            }
       }
        

    }

    @Override
    public void associateSessions(String session, String target) {
        HibernateUtil.associateDelegateSessionId(session, target);
    }

    @Override
    public void disassociateSession(String session) {
        HibernateUtil.disassociateDelegateSessionId(session);
        
    }

    @Override
    public Collection<AbstractComponent> findComponentByName(String sessionId, String name) {
        PersistenceBroker broker = GlobalContext.getGlobalContext().getSynchronousPersistenceBroker();
        List<ComponentSpecification> list = broker.loadAll(sessionId, ComponentSpecification.class, new String[]{"name"}, new String[]{name});
        Collection<AbstractComponent> mctComponents = new LinkedList<AbstractComponent>();
        for (ComponentSpecification compSpec : list)
            mctComponents.add(ComponentSpecificationDaoStrategy.fromDatabaseObjectToComponent(compSpec, compSpec.isShared()));        
        return mctComponents;
    }

    /**
     * Updates the component from DB.
     * @param component - the abstract component.
     */
    public void updateComponentFromDatabase(AbstractComponent component) {
        DaoStrategy<AbstractComponent, ? extends DaoObject> strategy = component.getDaoStrategy();
        DaoObject object = strategy.getDaoObject();
        assert object instanceof ComponentSpecification;
        if (object instanceof ComponentSpecification) {
            updateComponentIfNecessary((ComponentSpecification) object, component);
        }
    }
    
    private void updateComponentIfNecessary(final ComponentSpecification c, final AbstractComponent component) {
        if (component.getVersion() < c.getVersion()) {
            component.resetComponentProperties(new AbstractComponent.ResetPropertiesTransaction() {
                
                @Override
                public void perform() {
                    Map<String, ExtendedProperties> viewProps = c.getViewInfo();
                    Updatable updatable = component.getCapability(Updatable.class);
                    updatable.setViewRoleProperties(viewProps);
                    updatable.setVersion(c.getVersion());
                    updatable.setBaseDisplayedName(c.getName());
                    updatable.removalAllAssociatedComponents();
                    updatable.setOwner(c.getOwner());
                    updatable.setShared(c.isShared());
                    if (!component.isLeaf())
                        updatable.addReferences(Collections.singletonList(AbstractComponent.NULL_COMPONENT));
                }
            });
            component.refreshViewManifestations();
            LOGGER.debug("{} updated", c.getName());
        }
    }
    
    @Override
    public void updateComponentsFromDatabase() { 
        CorePersistenceService.iterateOverChangedComponents(
            new ChangedComponentVisitor() {
                @Override
                public void operateOnComponent(ComponentSpecification c) {
                    AbstractComponent cachedComponents = GlobalComponentRegistry.getComponent(c.getComponentId());
                    if (cachedComponents != null) {
                       updateComponentIfNecessary(c, cachedComponents);
                    }
                }
            }
        );
    }
}
