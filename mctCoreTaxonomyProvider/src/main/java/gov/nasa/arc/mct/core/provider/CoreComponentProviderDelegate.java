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
package gov.nasa.arc.mct.core.provider;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.core.components.MineTaxonomyComponent;
import gov.nasa.arc.mct.core.components.TelemetryAllDropBoxComponent;
import gov.nasa.arc.mct.core.components.TelemetryUserDropBoxComponent;
import gov.nasa.arc.mct.lock.manager.LockManager;
import gov.nasa.arc.mct.platform.core.access.PlatformAccess;
import gov.nasa.arc.mct.platform.spi.PersistenceService;
import gov.nasa.arc.mct.services.component.AbstractProviderDelegate;
import gov.nasa.arc.mct.services.internal.component.ComponentInitializer;
import gov.nasa.arc.mct.services.internal.component.CoreComponentRegistry;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class CoreComponentProviderDelegate extends AbstractProviderDelegate {
    
    private static final String DELIM = ",";
    /*
     * Mapping
     * <userid, group> => <created component, set of parent components>
     */
    private Map<String, Map<AbstractComponent, Collection<AbstractComponent>>> map 
                    = new HashMap<String, Map<AbstractComponent,Collection<AbstractComponent>>>();

    @Override
    public void userAdded(String session, String userId, String group) {
        Map<AbstractComponent, Collection<AbstractComponent>> userMap = new HashMap<AbstractComponent, Collection<AbstractComponent>>();
        map.put(userId + DELIM + group, userMap);
        
        PersistenceService persistenceService = PlatformAccess.getPlatform().getPersistenceService();
        CoreComponentRegistry componentRegistry = PlatformAccess.getPlatform().getComponentRegistry();
                        
        AbstractComponent mySandbox = createMySandbox(persistenceService, componentRegistry, session, userMap, userId, group);
        createUserDropbox(persistenceService, session, userMap, userId, group, mySandbox);        
    }
    
    private AbstractComponent createMySandbox(PersistenceService persistenceService, CoreComponentRegistry componentRegistry, 
            String session, Map<AbstractComponent, Collection<AbstractComponent>> userMap, String userId, String group) {
        // Create My Sandbox, which goes under All
        AbstractComponent all = componentRegistry.getComponent(componentRegistry.getRootComponentId());
        persistenceService.associateSessions(all.getComponentId(), session);
        AbstractComponent mySandbox = createComponent(MineTaxonomyComponent.class);        
        mySandbox.setDisplayName("My Sandbox");
        mySandbox.setOwner(userId);
        all.addDelegateComponent(mySandbox);
        persistenceService.disassociateSession(all.getComponentId());
        
        userMap.put(mySandbox, Collections.singleton(all));
        return mySandbox;
    }    
        
    private void createUserDropbox(PersistenceService persistenceService, String session, 
            Map<AbstractComponent, Collection<AbstractComponent>> userMap, 
            String userId, String group, AbstractComponent mySandbox) {
        
        persistenceService.associateSessions("TAG_SESSION_ID", session);

        // Create DropBox under My Sandbox
        AbstractComponent userDropBox = createComponent(TelemetryUserDropBoxComponent.class);
        userDropBox.setOwner(userId);
        ComponentInitializer ci = userDropBox.getCapability(ComponentInitializer.class);
        ci.setCreator(userId);
        ci.setCreationDate(new Date());
        
        persistenceService.associateSessions(userDropBox.getComponentId(), session);
        userDropBox.setDisplayName(userId + "'s Drop Box");
        persistenceService.disassociateSession(userDropBox.getComponentId());
        
        persistenceService.associateSessions(mySandbox.getComponentId(), session);
        mySandbox.addDelegateComponent(userDropBox);
        persistenceService.disassociateSession(mySandbox.getComponentId());
        Collection<AbstractComponent> dropboxParents = new LinkedHashSet<AbstractComponent>();
        dropboxParents.add(mySandbox);
        
        userMap.put(userDropBox, dropboxParents);
        
        // Place user dropbox under the Discpline's Drop Boxes
        AbstractComponent dropboxContainer = ownedByAdmin(persistenceService.findComponentByName(session, group + "\'s Drop Boxes"));
        TelemetryAllDropBoxComponent alldropboxes = (TelemetryAllDropBoxComponent) ownedByAdmin(persistenceService.findComponentByName(session, "All " + group + "\'s Drop Boxes"));
        
        assert dropboxContainer != null : "Cannot find " + group + "'s Drop Boxes component";
        persistenceService.associateSessions(dropboxContainer.getComponentId(), session);
        PlatformAccess.getPlatform().getLockManager().lock(dropboxContainer.getComponentId());
        dropboxContainer.addDelegateComponents(Collections.singleton(userDropBox));
        PlatformAccess.getPlatform().getLockManager().unlock(dropboxContainer.getComponentId());
        persistenceService.disassociateSession(dropboxContainer.getComponentId());
        
        dropboxParents.add(dropboxContainer);
        
        // Place user dropbox under All Discipline's Drop Boxes
        assert alldropboxes != null : "Cannot find All " + group + "'s Drop Boxes component";
        persistenceService.associateSessions(alldropboxes.getComponentId(), session);
        alldropboxes.addNewAndInitializeUserDropboxes(Collections.singleton(userDropBox));
        persistenceService.disassociateSession(alldropboxes.getComponentId());

        dropboxParents.add(alldropboxes);

        userDropBox.share();                
        persistenceService.disassociateSession("TAG_SESSION_ID");
    }
    
    private AbstractComponent createComponent(Class<? extends AbstractComponent> clazz) {
        AbstractComponent newInstance = null;
        try {
            newInstance = clazz.newInstance();
            newInstance.getCapability(ComponentInitializer.class).initialize();
            if (!newInstance.isShared()) {
                LockManager lockManager = PlatformAccess.getPlatform().getLockManager();
                lockManager.newLock(newInstance.getId());
                lockManager.lock(newInstance.getId());
            } 

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return newInstance;
    }
    private AbstractComponent ownedByAdmin(Collection<AbstractComponent> components) {
        for (AbstractComponent component : components) {
            if (component.getOwner().equals("admin"))
                return component;
        }
        return null;            
    }
    
    @Override
    public void userAddedFailed(String userId, String group) {
        Map<AbstractComponent, Collection<AbstractComponent>> userMap = map.get(userId + DELIM + group);
        if (userMap == null)
            return;
        
        // Remove connections to parent components
        for (AbstractComponent child : userMap.keySet()) {
            for (AbstractComponent parent : userMap.get(child)) {
                parent.removeDelegateComponent(child);
            }
        }
        // Unregister created components
        PlatformAccess.getPlatform().getComponentRegistry().unregister(userMap.keySet());
        
        // Remove from this delegate's map
        map.remove(userId + DELIM + group);
    }
    
    @Override
    public void userAddedSuccessful(String userId, String group) {
        map.remove(userId + DELIM + group);        
    }

}
