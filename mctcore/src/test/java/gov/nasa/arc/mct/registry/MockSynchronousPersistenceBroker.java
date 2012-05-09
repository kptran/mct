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

import gov.nasa.arc.mct.persistence.strategy.DaoObject;
import gov.nasa.arc.mct.persistmgr.PersistenceBroker;
import gov.nasa.arc.mct.persistmgr.callback.PersistenceCompletedCallbackHandler;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.hibernate.type.Type;

/**
 * This is a mocked-up persistence broker that implements <code>PersistenceBroker</code>
 * and is used in {@link TestExternalComponentRegistryImpl#testNewCollection()}.
 *  
 */
public class MockSynchronousPersistenceBroker implements PersistenceBroker {
    
    private int sessionsStartedCt = 0;
    private int sessionsAbortedCt = 0;
    private int sessionsClosedCt = 0;
    
    public int getSessionsAbortedCt() {
        return sessionsAbortedCt;
    }
    
    public int getSessionsClosedCt() {
        return sessionsClosedCt;
    }
    
    public int getSessionsStartedCt() {
        return sessionsStartedCt;
    }
    
    public void clearBroker() {
        sessionsAbortedCt = 0;
        sessionsClosedCt = 0;
        sessionsStartedCt = 0;
    }

    @Override
    public void abortSession(String sessionId) {
        sessionsAbortedCt++;
    }

    @Override
    public <T> void attachToSession(String sessionId, T obj) {
        

    }

    @Override
    public void closeSession(String sessionId) {
        sessionsClosedCt++;
    }

    @Override
    public <T> boolean delete(String sessionId, T obj) {
        
        return false;
    }

    @Override
    public <T> boolean delete(T obj) {
        
        return false;
    }

    @Override
    public <T> void forceLoad(String sessionId, T obj) {
        

    }

    @Override
    public <T extends DaoObject> T lazilyLoad(String sessionId, T daoObject) {
        
        return null;
    }

    @Override
    public <T> T lazilyLoad(String sessionId, Class<T> type, Serializable id) {
        
        return null;
    }

    @Override
    public <T> T lazilyLoad(Class<T> type, Serializable id) {
        
        return null;
    }

    @Override
    public void lazilyLoadCompleted(String sessionId) {

    }
    
    @Override
    public void lazilyLoadCompleted(String sessionId, boolean retrySave) {
        
    }

    @Override
    public <T> List<T> loadAll(String sessionId, Class<T> type) {
        
        return null;
    }

    @Override
    public <T> List<T> loadAll(Class<T> type) {
        
        return null;
    }

    @Override
    public <T> List<T> loadAll(String sessionId, Class<T> type, String[] propertyNames, Object[] propertyValues) {
        
        return null;
    }
    
    @Override
    public <T> List<T> loadAll(Class<T> type, String[] propertyNames, Object[] propertyValues,
                    String filterName, String[] filterParams, Object[] filterValues) {
        return null;
    }

    @Override
    public <T> List<T> loadAllByLeftOuterJoin(String sessionId, Class<T> type, String[] joinPropertyNames,
            String[] joinAliases, String[] eqPropertyNames, Object[] eqPropertyValues, String filterName,
            String[] filterPropertyNames, Object[] filterPropertyValues) {
        
        return null;
    }

    @Override
    public <T> List<T> loadAllEagerly(String sessionId, Class<T> type, String[] propertyNames, Object[] propertyValues,
            String[] eagerlyFetchedFields) {
        
        return null;
    }

    @Override
    public List<?> loadAllByNativeSQL(String sessionId, String nativeSQL, String[] scalarAttribute, Type[] scalarType) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public List<Object> queryNativeSQL(String sessionId, String nativeSQL,
                    String[] parameters) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public <T> List<T> loadByIdsEagerly(String sessionId, Class<T> type, String idPropertyName,
                    Collection<Serializable> ids, String[] eagerlyFetchedFields) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> List<T> loadAllOrderedBy(Class<T> type, String orderedByProperty) {
        
        return null;
    }
    
    @Override
    public void executeNativeSQL(String sessionId, String nativeSQL, String[] parameters) {
        // TODO Auto-generated method stub
        
    } 
    
    @Override
    public <T> List<T> loadAllOrderedBy(String sessionId, Class<T> type, String orderedByProperty,
            String[] eqPropertyNames, Object[] eqPropertyValues, String[] neqPropertyNames, Object[] neqPropertyValues) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> List<T> loadAllOrderedByDesc(Class<T> type, String orderedByProperty) {
        
        return null;
    }

    @Override
    public <T> List<Object> loadByHQL(Class<T> type, String alias, String[] projectedPropertyNames,
                    String[] propertyNames, Object[] propertyValues, String filterName,
                    String[] filterParams, Object[] filterValues) {
        return null;
    }

    @Override
    public <T> T loadById(String sessionId, Class<T> type, Serializable id) {
        return null;
    }
    
    @Override
    public <T> T loadByIdEagerly(String sessionId, Class<T> type, Serializable id, String[] eagerlyFetchedFields) {
        return null;
    }

    @Override
    public <T> List<T> loadChildren(String sessionId, T parent, Class<T> type, String propertyName) {
        
        return null;
    }

    @Override
    public <T> void persist(String sessionId, T daoObject) {
        

    }

    @Override
    public <T> boolean save(String sessionId, T obj, PersistenceCompletedCallbackHandler handler) {
        
        return false;
    }

    @Override
    public <T> boolean save(T obj, PersistenceCompletedCallbackHandler handler) {
        
        return false;
    }

    @Override
    public <T> void saveBatch(String sessionId, List<T> daoObjects, PersistenceCompletedCallbackHandler handler) {
        

    }

    @Override
    public <T extends DaoObject> boolean saveDao(String sessionId, T obj, PersistenceCompletedCallbackHandler handler) {
        
        return false;
    }

    @Override
    public <T extends DaoObject> void saveDaoBatch(String sessionId, List<T> daoObjects,
            PersistenceCompletedCallbackHandler handler) {
        

    }

    @Override
    public boolean isReadOnly() {
        return false;
    }
    
    @Override
    public void startSession(String sessionId) {
        sessionsStartedCt++;
    }

}
