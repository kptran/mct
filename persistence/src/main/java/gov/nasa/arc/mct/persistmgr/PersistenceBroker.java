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
 * PersistenceBroker.java Sep 28, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.persistmgr;

import gov.nasa.arc.mct.persistence.strategy.DaoObject;
import gov.nasa.arc.mct.persistmgr.callback.PersistenceCompletedCallbackHandler;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.hibernate.type.Type;

public interface PersistenceBroker {

    public <T> boolean save(String sessionId, T obj, PersistenceCompletedCallbackHandler handler);

    public <T> boolean save(T obj, PersistenceCompletedCallbackHandler handler);
    
    public <T extends DaoObject> boolean saveDao(String sessionId, T obj, PersistenceCompletedCallbackHandler handler);

    public <T> void saveBatch(String sessionId, List<T> daoObjects, PersistenceCompletedCallbackHandler handler);

    public <T extends DaoObject> void saveDaoBatch(String sessionId, List<T> daoObjects,
            PersistenceCompletedCallbackHandler handler);
    
    public void executeNativeSQL(String sessionId, String nativeSQL, String[] parameters);
    
    public List<Object> queryNativeSQL(String sessionId, String nativeSQL, String[] parameters);
    
    public List<?> loadAllByNativeSQL(String sessionId, String nativeSQL, String[] scalarAttribute, Type[] scalarType);
    
    public <T> List<T> loadByIdsEagerly(String sessionId, Class<T> type, String idPropertyName, Collection<Serializable> ids, String[] eagerlyFetchedFields);

    public <T> List<T> loadAll(String sessionId, Class<T> type);

    public <T> List<T> loadAll(Class<T> type);

    public <T> List<T> loadChildren(String sessionId, T parent, Class<T> type, String propertyName);

    public <T> List<T> loadAll(String sessionId, Class<T> type, String[] propertyNames, Object[] propertyValues);

    public <T> List<T> loadAll(Class<T> type, String[] propertyNames, Object[] propertyValues, String filterName, String[] filterParams, Object[] filterValues);

    public <T> List<T> loadAllEagerly(String sessionId, Class<T> type, String[] propertyNames, Object[] propertyValues,
            String[] eagerlyFetchedFields);
    
    public <T> List<Object> loadByHQL(Class<T> type, String alias, String[] projectedPropertyNames,
            String[] propertyNames, Object[] propertyValues, String filterName, String[] filterParams, Object[] filterValues);

    public <T> boolean delete(String sessionId, T obj);

    public <T> boolean delete(T obj);

    public <T extends DaoObject> T lazilyLoad(String sessionId, T daoObject);

    public <T> T lazilyLoad(String sessionId, Class<T> type, Serializable id);

    public <T> T lazilyLoad(Class<T> type, Serializable id);
    
    public <T> void attachToSession(String sessionId, T obj);

    public <T> void forceLoad(String sessionId, T obj);

    public void lazilyLoadCompleted(String sessionId);
    
    public void lazilyLoadCompleted(String sessionId, boolean retrySave);

    public <T> List<T> loadAllOrderedBy(String sessionId, Class<T> type, String orderedByProperty, String[] eqPropertyNames,
            Object[] eqPropertyValues, String[] neqPropertyNames, Object[] neqPropertyValues);

    public <T> List<T> loadAllOrderedBy(Class<T> type, String orderedByProperty);

    public <T> List<T> loadAllOrderedByDesc(Class<T> type, String orderedByProperty);

    public <T> List<T> loadAllByLeftOuterJoin(String sessionId, Class<T> type, String[] joinPropertyNames,
            String[] joinAliases, String[] eqPropertyNames, Object[] eqPropertyValues, String filterName,
            String[] filterPropertyNames, Object[] filterPropertyValues);
    
    public <T> T loadByIdEagerly(String sessionId, Class<T> type, Serializable id, String[] eagerlyFetchedFields);

    public <T> T loadById(String sessionId, Class<T> type, Serializable id);

    public void startSession(String sessionId);

    public void closeSession(String sessionId);

    public <T> void persist(String sessionId, T daoObject);
    
    public void abortSession(String sessionId);
    
    public boolean isReadOnly();
}
