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
package gov.nasa.arc.mct.registry;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.persistmgr.PersistenceBroker;

import java.util.Collection;

import org.mockito.Mockito;

/**
 * This is a mocked-up component registry that extends <code>ExternalComponentRegistry</code>
 * and is used in {@link TestExternalComponentRegistryImpl#testNewCollection()}.
 *  
 */
public class MockComponentRegistry extends ExternalComponentRegistryImpl {
    
    private static final String ALL = "All";
    private AbstractComponent defaultCollection;
    private boolean expectedResult = false;
    private AbstractComponent rootComponent = Mockito.mock(AbstractComponent.class);
    private MockSynchronousPersistenceBroker persistenceBroker = new MockSynchronousPersistenceBroker();
    
    // Statistics
    private int associateCreatedByMeAndAllSessionsCt = 0;
    private int associateCollectionAndCreatedByMeSessionsCt = 0;
    
    public enum SessionAssociationType {
        CREATED_BY_ME_TO_ALL, COLLECTION_TO_ALL;
    }
    
    public MockComponentRegistry() {
        Mockito.when(rootComponent.getId()).thenReturn(ALL);
    }
    
    public void setDefaultCollection(AbstractComponent collection) {
        defaultCollection = collection;
    }
    
    public void setExpectedResultForAddComponents(boolean result) {
        expectedResult = result;
    }
    
    public void clearRegistry() {
        associateCollectionAndCreatedByMeSessionsCt = 0;
        associateCreatedByMeAndAllSessionsCt = 0;
        defaultCollection = null;
        expectedResult = false;
        persistenceBroker.clearBroker();
    }
    
    public int getSessionAssociationCount(SessionAssociationType type) {
        if (type == SessionAssociationType.CREATED_BY_ME_TO_ALL)
            return associateCreatedByMeAndAllSessionsCt;
        if (type == SessionAssociationType.COLLECTION_TO_ALL)
            return associateCollectionAndCreatedByMeSessionsCt;
        return 0;
    }
    
    public int getSessionsAborted() {
        return persistenceBroker.getSessionsAbortedCt();
    }
    
    public int getSessionsClosed() {
        return persistenceBroker.getSessionsClosedCt();
    }
    
    public int getSessionsStarted() {
        return persistenceBroker.getSessionsStartedCt();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T extends AbstractComponent> T newInstance(Class<T> componentClass, AbstractComponent parent) {
        return (T) defaultCollection;
    }
    
    @Override
    void addComponentToTransaction(AbstractComponent child, AbstractComponent parent) {
        if (rootComponent.equals(parent))
            associateCreatedByMeAndAllSessionsCt++;
        else if (child.equals(defaultCollection) && rootComponent.equals(parent))
            associateCollectionAndCreatedByMeSessionsCt++;
    }
    
    @Override
    boolean addComponents(Collection<AbstractComponent> childComponents, AbstractComponent parentComponent) {
        return expectedResult;
    }
    
    @Override
    protected AbstractComponent getRootComponent() {
        return rootComponent;
    }
    
    @Override
    protected PersistenceBroker getSynchronousPersistenceBroker() {
        return persistenceBroker;
    }
}
