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
 * SynchronousPersistenceBroker.java Sep 28, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.persistmgr;

import gov.nasa.arc.mct.persistence.hibernate.Interceptable;
import gov.nasa.arc.mct.persistence.interceptor.OptimisticSessionInterceptor;
import gov.nasa.arc.mct.persistence.strategy.DaoObject;
import gov.nasa.arc.mct.persistence.strategy.OptimisticLockException;
import gov.nasa.arc.mct.persistence.util.HibernateUtil;
import gov.nasa.arc.mct.persistmgr.callback.PersistenceCompletedCallbackHandler;
import gov.nasa.arc.mct.util.StringUtil;
import gov.nasa.arc.mct.util.logging.MCTLogger;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Filter;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.hibernate.type.Type;

public class SynchronousPersistenceBroker implements PersistenceBroker {
    private static final MCTLogger LOGGER = MCTLogger.getLogger(SynchronousPersistenceBroker.class);

    private static final SynchronousPersistenceBroker instance = new SynchronousPersistenceBroker();

    private ThreadLocal<Set<Serializable>> daoObjects = new ThreadLocal<Set<Serializable>>();

    public static SynchronousPersistenceBroker getSynchronousPersistenceBroker() {
        return instance;
    }

    private SynchronousPersistenceBroker() {
        //
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> loadChildren(String sessionId, final T parent, final Class<T> type, final String propertyName) {
        final List<T>[] allComponents = new List[1];
        new PersistenceTransaction() {
            public void perform(Session session) {
                Criterion restriction = null;
                if (parent == null) {
                    restriction = Restrictions.isNull(propertyName);
                } else {
                    restriction = Restrictions.eq(propertyName, parent);
                }
                allComponents[0] = session.createCriteria(type).add(restriction).list();
            }
        }.run(sessionId, hasOpenSession(sessionId));

        return allComponents[0];
    }

    @Override
    public <T> boolean save(String sessionId, final T daoObject, PersistenceCompletedCallbackHandler handler) {
        new PersistenceTransaction() {
            public void perform(Session session) {
                session.saveOrUpdate(daoObject);
            }
        }.run(sessionId, handler, hasOpenSession(sessionId), false);

        return true;
    }

    @Override
    public <T> boolean save(final T daoObject, PersistenceCompletedCallbackHandler handler) {
        new PersistenceTransaction() {
            public void perform(Session session) {
                session.saveOrUpdate(daoObject);
            }
        }.run(handler);

        return true;
    }

    @Override
    public <T extends DaoObject> boolean saveDao(String sessionId, T obj, PersistenceCompletedCallbackHandler handler) {
        return save(sessionId, obj, handler);
    };

    @Override
    public <T> void saveBatch(String sessionId, final List<T> daoObjects, PersistenceCompletedCallbackHandler handler) {
        new PersistenceTransaction() {
            public void perform(Session session) {
                for (T daoObject : daoObjects) {
                    session.saveOrUpdate(daoObject);
                }
            }
        }.run(sessionId, handler, hasOpenSession(sessionId), false);
    }

    @Override
    public <T extends DaoObject> void saveDaoBatch(String sessionId, List<T> daoObjects,
            PersistenceCompletedCallbackHandler handler) {
        saveBatch(sessionId, daoObjects, handler);
    }

    // @Override
    public <T> void removeBatch(String sessionId, final List<T> daoObjects) {
        new PersistenceTransaction() {
            public void perform(Session session) {
                for (T daoObject : daoObjects) {
                    session.delete(daoObject);
                }
            }
        }.run(sessionId, hasOpenSession(sessionId));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> loadAll(String sessionId, final Class<T> type) {
        final List<T>[] allComponents = new List[1];
        new PersistenceTransaction() {
            public void perform(Session session) {
                allComponents[0] = session.createCriteria(type).list();
            }
        }.run(sessionId, hasOpenSession(sessionId));

        return allComponents[0];
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> loadByIdsEagerly(String sessionId, final Class<T> type, final String idPropertyName, final Collection<Serializable> ids, final String[] eagerlyFetchedFields) {
        if (ids == null || ids.isEmpty()) { return Collections.emptyList(); }
        
        final List<T>[] allComponents = new List[1];
        new PersistenceTransaction() {
            public void perform(Session session) {
                Criteria criteria = session.createCriteria(type);
                criteria.add(Restrictions.in(idPropertyName, ids));
                for (int i = 0; i < eagerlyFetchedFields.length; i++) {
                    criteria.setFetchMode(eagerlyFetchedFields[i], FetchMode.JOIN);
                }
                allComponents[0] = criteria.list();
            }
        }.run(sessionId, hasOpenSession(sessionId));

        return allComponents[0];
    }

    @SuppressWarnings("unchecked")
    public List<Object> queryNativeSQL(String sessionId, final String nativeSQL, final String[] parameters) {
        final List<Object>[] allComponents = new List[1];
        new PersistenceTransaction() {
            public void perform(Session session) {
                SQLQuery q = session.createSQLQuery(nativeSQL);
                if (parameters != null) {
                    for (int i=0; i<parameters.length; i++) {
                        q.setString(i, parameters[i]);
                    }
                }
                allComponents[0] = q.list();
            }
        }.run(sessionId, null, hasOpenSession(sessionId), false);
        return allComponents[0];
    }
    
    public void executeNativeSQL(String sessionId, final String nativeSQL, final String[] parameters) {
        new PersistenceTransaction() {
            public void perform(Session session) {
                SQLQuery q = session.createSQLQuery(nativeSQL);
                if (parameters != null) {
                    for (int i=0; i<parameters.length; i++) {
                        q.setString(i, parameters[i]);
                    }
                }
                q.executeUpdate();
            }
        }.run(sessionId, null, hasOpenSession(sessionId), true);
    }
    
    public List<?> loadAllByNativeSQL(String sessionId, final String nativeSQL, final String[] scalarAttribute, final Type[] scalarType) {
        final List<?>[] allComponents = new List[1];
        new PersistenceTransaction() {
            public void perform(Session session) {
                SQLQuery q = session.createSQLQuery(nativeSQL);
                for (int i=0; i<scalarAttribute.length; i++) {
                    q.addScalar(scalarAttribute[i], scalarType[i]);
                }
                allComponents[0] = q.list();
            }
        }.run(sessionId, hasOpenSession(sessionId));

        return allComponents[0];
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> loadAll(final Class<T> type) {
        final List<T>[] allComponents = new List[1];
        new PersistenceTransaction() {
            @Override
            public void perform(Session session) {
                allComponents[0] = session.createCriteria(type).list();
            }
        }.run();
        return allComponents[0];
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> loadAllOrderedBy(String sessionId, final Class<T> type, final String orderedByProperty,
            final String[] eqPropertyNames, final Object[] eqPropertyValues, final String[] neqPropertyNames, final Object[] neqPropertyValues) {
        final List<T>[] allComponents = new List[1];
        new PersistenceTransaction() {
            public void perform(Session session) {
                Criteria criteria = session.createCriteria(type);
                criteria.addOrder(Order.asc(orderedByProperty));

                for (int i = 0; i < eqPropertyNames.length; i++) {
                    if (eqPropertyValues[i] != null && !(eqPropertyValues[i] instanceof Collection<?>)) {
                        criteria.add(Restrictions.eq(eqPropertyNames[i], eqPropertyValues[i]));
                    } else if (eqPropertyValues[i] != null && eqPropertyValues[i] instanceof Collection<?>
                            && Collection.class.cast(eqPropertyValues[i]).isEmpty()) {
                        criteria.add(Restrictions.isEmpty(eqPropertyNames[i]));
                    } else if (eqPropertyValues[i] == null) {
                        criteria.add(Restrictions.isNull(eqPropertyNames[i]));
                    }
                }
                
                for (int i=0; i<neqPropertyNames.length; i++) {
                    criteria.add(Restrictions.ne(neqPropertyNames[i], neqPropertyValues[i]));
                }

                allComponents[0] = criteria.list();
            }
        }.run(sessionId, hasOpenSession(sessionId));

        return allComponents[0];
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> loadAllOrderedBy(final Class<T> type, final String orderedByProperty) {
        final List<T>[] allComponents = new List[1];
        new PersistenceTransaction() {
            public void perform(Session session) {
                Criteria criteria = session.createCriteria(type);
                criteria.addOrder(Order.asc(orderedByProperty));
                allComponents[0] = criteria.list();
            }
        }.run();
        return allComponents[0];
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> loadAllOrderedByDesc(final Class<T> type, final String orderedByProperty) {
        final List<T>[] allComponents = new List[1];
        new PersistenceTransaction() {
            public void perform(Session session) {
                Criteria criteria = session.createCriteria(type);
                criteria.addOrder(Order.desc(orderedByProperty));
                allComponents[0] = criteria.list();
            }
        }.run();
        return allComponents[0];
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> loadAll(String sessionId, final Class<T> type, final String[] propertyNames,
            final Object[] propertyValues) {
        final List<T>[] allComponents = new List[1];
        new PersistenceTransaction() {
            public void perform(Session session) {
                Criteria criteria = session.createCriteria(type);
                for (int i = 0; i < propertyNames.length; i++) {
                    if (propertyValues[i] != null) {
                        criteria.add(Restrictions.eq(propertyNames[i], propertyValues[i]));
                    } else {
                        criteria.add(Restrictions.isNull(propertyNames[i]));
                    }
                }
                allComponents[0] = criteria.list();
            }
        }.run(sessionId, hasOpenSession(sessionId));

        return allComponents[0];
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> loadAll(final Class<T> type, final String[] propertyNames, final Object[] propertyValues, final String filterName, final String[] filterParams, final Object[] filterValues) {
        final List<T>[] allComponents = new List[1];
        new PersistenceTransaction() {
            public void perform(Session session) {
                if (!StringUtil.isEmpty(filterName)) {
                    Filter filter = session.enableFilter(filterName);
                    int i=0;
                    for (String filterParam: filterParams) {
                        filter.setParameter(filterParam, filterValues[i++].toString());
                    }
                }
                Criteria criteria = session.createCriteria(type);
                for (int i = 0; i < propertyNames.length; i++) {
                    if (propertyValues[i] != null) {
                        criteria.add(Restrictions.eq(propertyNames[i], propertyValues[i]));
                    } else {
                        criteria.add(Restrictions.isNull(propertyNames[i]));
                    }
                }
                allComponents[0] = criteria.list();
            }
        }.run();

        return allComponents[0];

    }
    
    @SuppressWarnings("unchecked")
    public <T> List<T> loadAllEagerly(String sessionId, final Class<T> type, final String[] propertyNames,
            final Object[] propertyValues, final String[] eagerlyFetchedFields) {
        final List<T>[] allComponents = new List[1];
        new PersistenceTransaction() {
            public void perform(Session session) {
                Criteria criteria = session.createCriteria(type);
                for (int i = 0; i < propertyNames.length; i++) {
                    criteria.add(Restrictions.eq(propertyNames[i], propertyValues[i]));
                }
                for (int i = 0; i < eagerlyFetchedFields.length; i++) {
                    criteria.setFetchMode(eagerlyFetchedFields[i], FetchMode.JOIN);
                }
                allComponents[0] = criteria.list();
            }
        }.run(sessionId, hasOpenSession(sessionId));

        return allComponents[0];
    }

    @SuppressWarnings("unchecked")
    public <T> List<Object> loadByHQL(final Class<T> type, final String alias, final String[] projectedPropertyNames,
            final String[] propertyNames, final Object[] propertyValues, final String filterName, final String[] filterParams, final Object[] filterValues ) {
        final StringBuilder query = new StringBuilder("select ");
        for (int i = 0; i < projectedPropertyNames.length; i++) {
            query.append(projectedPropertyNames[i]);
            if (i < projectedPropertyNames.length - 1) {
                query.append(", ");
            }
        }
        query.append(" from ");
        query.append(type.getName());
        query.append(" ");
        query.append(alias);

        if (propertyNames.length > 0) {
            query.append(" where ");
            for (int i = 0; i < propertyNames.length; i++) {
                query.append(propertyNames[i]);
                query.append(" = ");
                query.append(" ? ");
                if (i < propertyNames.length - 1) {
                    query.append(" and ");
                }
            }
        }

        final List<Object>[] allComponents = new List[1];
        new PersistenceTransaction() {
            public void perform(Session session) {
                if (!StringUtil.isEmpty(filterName)) {
                    Filter filter = session.enableFilter(filterName);
                    int i=0;
                    for (String filterParam: filterParams) {
                        filter.setParameter(filterParam, filterValues[i++]);
                    }
                }
                Query q = session.createQuery(query.toString());
                for (int i = 0; i < propertyValues.length; i++) {
                    q.setParameter(i, propertyValues[i]);
                }
                allComponents[0] = q.list();
            }
        }.run();

        return allComponents[0];
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends DaoObject> T lazilyLoad(String sessionId, T daoObject) {
        Session session = HibernateUtil.getCurrentSession(sessionId);
        T daoObjectUpdate = (T) session.get(daoObject.getClass(), daoObject.getId());
        daoObjectUpdate = refresh(daoObjectUpdate);
        if (daoObjectUpdate == null) {
            daoObjectUpdate = daoObject;
        }
        return daoObjectUpdate;
    }

    private <T> T refresh(T daoObject) {
        return daoObject;
    }

    private Set<Serializable> getIds() {
        Set<Serializable> ids = this.daoObjects.get();
        if (ids == null) {
            ids = new HashSet<Serializable>();
            this.daoObjects.set(ids);
        }
        return ids;
    }

    @Override
    public void startSession(String sessionId) {
        if (HibernateUtil.hasAssociatedDelegateSessionId(sessionId))
            return;
        
        HibernateUtil.openSession(sessionId);
        Set<Serializable> ids = getIds();
        ids.add(sessionId);
    }

    @Override
    public void closeSession(String sessionId) {
        closeSession(sessionId,true);
    }
    
    private void closeSession(String sessionId, boolean retrySave) {
        if (HibernateUtil.hasAssociatedDelegateSessionId(sessionId))
            return;

        Set<Object> dirtyEntitles = null;
        
        Set<Serializable> ids = getIds();
        ids.remove(sessionId);
        if (ids.isEmpty()) {
            this.daoObjects.remove();
        }
        Session session = HibernateUtil.getCurrentSession(sessionId);
        Transaction transaction = session.getTransaction();
        if (transaction.isActive()) {
            try {
                try {
                    transaction.commit();
                } catch (HibernateException he) {
                    if (transaction.isActive()) {
                        transaction.rollback();
                    }
                    if (!retrySave && he instanceof StaleObjectStateException) {
                        throw new OptimisticLockException(he);
                    }
                    LOGGER.debug("Retrying save", he);
                    throw he;
                } finally {
                    HibernateUtil.closeSession(sessionId);
                }
            } catch (HibernateException he) {
                if (session instanceof Interceptable) {
                    Interceptor interceptor = Interceptable.class.cast(session).getInterceptor();
                    if (OptimisticSessionInterceptor.class.isAssignableFrom(interceptor.getClass())) {
                        dirtyEntitles = OptimisticSessionInterceptor.class.cast(interceptor).getDirtyEntities();
                    }
                }

                if (dirtyEntitles == null || dirtyEntitles.isEmpty()) {
                    throw he;
                }
                retrySaving(sessionId, dirtyEntitles);
            }
        }
    }

    private void retrySaving(String sessionId, Set<Object> dirtyEntityObjects) {
        startSession(sessionId);
        Session session = HibernateUtil.getCurrentSession(sessionId);

        Transaction transaction = session.getTransaction();
        try {
            for (Object object : dirtyEntityObjects) {
                if (DaoObject.class.isAssignableFrom(object.getClass())) {
                    Object loadedObject = loadById(sessionId, object.getClass(), DaoObject.class.cast(object)
                            .getId());
                    if (loadedObject == null) {
                        loadedObject = object; // If it is not yet in the
                        // database.
                    } else {
                        HibernateUtil.associateDelegateSessionId(DaoObject.class.cast(loadedObject).getId()
                                .toString(), sessionId);
                        DaoObject.class.cast(object).merge(DaoObject.class.cast(loadedObject));
                    }
                    session.saveOrUpdate(loadedObject);
                }
            }
            transaction.commit();
        } finally {
            Set<Serializable> ids = getIds();
            ids.remove(sessionId);
            if (ids.isEmpty()) {
                this.daoObjects.remove();
            }
            HibernateUtil.closeSession(sessionId);
        }
    }

    private boolean hasOpenSession(String sessionId) {
        Set<Serializable> ids = getIds();
        return ids.contains(sessionId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T lazilyLoad(String sessionId, Class<T> type, Serializable id) {
        Session session = HibernateUtil.getCurrentSession(sessionId);
        T daoObject = (T) session.get(type, id);
        daoObject = refresh(daoObject);
        return daoObject;
    }
    
    @Override
    public <T> void forceLoad(String sessionId, T obj) {
        Hibernate.initialize(obj);
    }

    @Override
    public <T> void attachToSession(String sessionId, T obj) {
        Session session = HibernateUtil.getCurrentSession(sessionId);
        session.lock(obj, LockMode.NONE);
    }

    @Override
    public <T> T lazilyLoad(Class<T> type, Serializable id) {
        Session session = HibernateUtil.getCurrentSession();
        T daoObject = type.cast(session.get(type, id));
        daoObject = refresh(daoObject);
        return daoObject;
    }

	@Override
    public void lazilyLoadCompleted(String sessionId, boolean doRetrySave) {
        Session session = HibernateUtil.getCurrentSession(sessionId);
        Set<Serializable> ids = getIds();
        if ((!HibernateUtil.hasAssociatedDelegateSessionId(sessionId) || !ids.contains(HibernateUtil
                .getAssociatedDelegateSessionId(sessionId)))
                && (!ids.contains(sessionId))) {
            closeSession(sessionId, doRetrySave);
        } 
        else if (!HibernateUtil.hasAssociatedDelegateSessionId(sessionId)) {
            Transaction transaction = session.getTransaction();
            transaction.rollback();
        }
    }
    
    @Override
    public void lazilyLoadCompleted(String sessionId) {
       lazilyLoadCompleted(sessionId,true);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> loadAllByLeftOuterJoin(String sessionId, final Class<T> type, final String[] joinPropertyNames,
            final String[] joinAliases, final String[] eqPropertyNames, final Object[] eqPropertyValues,
            final String filterName, final String[] filterPropertyNames, final Object[] filterPropertyValues) {
        final List<T>[] allComponents = new List[1];

        new PersistenceTransaction() {
            public void perform(Session session) {
                if (!StringUtil.isEmpty(filterName)) {
                    Filter filter = session.enableFilter(filterName);
                    for (int i = 0; i < filterPropertyNames.length; i++) {
                        filter.setParameter(filterPropertyNames[i], filterPropertyValues[i]);
                    }
                }
                Criteria criteria = session.createCriteria(type);
                for (int i = 0; i < joinPropertyNames.length; i++) {
                    criteria = criteria.createAlias(joinPropertyNames[i], joinAliases[i],
                            CriteriaSpecification.LEFT_JOIN);
                }
                for (int i = 0; i < eqPropertyNames.length; i++) {
                    criteria = criteria.add(Restrictions.eq(eqPropertyNames[i], eqPropertyValues[i]));
                }
                allComponents[0] = criteria.list();
            }
        }.run(sessionId, hasOpenSession(sessionId));

        return allComponents[0];
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T loadById(String sessionId, final Class<T> type, final Serializable id) {
        final T[] result = (T[]) new Object[1];

        new PersistenceTransaction() {
            public void perform(Session session) {
                T daoObject = (T) session.get(type, id);
                result[0] = refresh(daoObject);
            }
        }.run(sessionId, hasOpenSession(sessionId));

        return result[0];
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T loadByIdEagerly(String sessionId, final Class<T> type, final Serializable id, final String[] eagerlyFetchedFields) {
        final List<T>[] allComponents = new List[1];
        new PersistenceTransaction() {
            public void perform(Session session) {
                Criteria criteria = session.createCriteria(type);
                criteria.add(Restrictions.idEq(id));
                for (int i = 0; i < eagerlyFetchedFields.length; i++) {
                    criteria.setFetchMode(eagerlyFetchedFields[i], FetchMode.JOIN);
                }
                allComponents[0] = criteria.list();
            }
        }.run(sessionId, hasOpenSession(sessionId));

        return allComponents[0].get(0);
    }
    
    @Override
    public <T> boolean delete(String sessionId, final T daoObject) {
        new PersistenceTransaction() {
            public void perform(Session session) {
                session.delete(daoObject);
            }
        }.run(sessionId, hasOpenSession(sessionId));

        return true;
    }

    @Override
    public <T> boolean delete(final T daoObject) {
        new PersistenceTransaction() {
            public void perform(Session session) {
                session.delete(daoObject);
            }
        }.run();

        return true;
    };

    @Override
    public <T> void persist(String sessionId, final T daoObject) {
        new PersistenceTransaction() {
            public void perform(Session session) {
                session.saveOrUpdate(daoObject);
            }
        }.run(sessionId, true);
    }

    @Override
    public void abortSession(String sessionId) {
        Set<Serializable> ids = getIds();
        ids.remove(sessionId);
        if (ids.isEmpty()) {
            this.daoObjects.remove();
        }
        Session session = HibernateUtil.getCurrentSession(sessionId);
        Transaction transaction = session.getTransaction();
        if (transaction.isActive()) {
            try {
                transaction.rollback();
            } finally {
                HibernateUtil.closeSession(sessionId);
            }

        }
    }
    
    @Override
    public boolean isReadOnly() {
        final boolean[] b = new boolean[1];
        Work work = new Work() {
            
            @Override
            public void execute(Connection connection) throws SQLException {
                b[0] = connection.isReadOnly();
            }
        };
        HibernateUtil.getCurrentSession().doWork(work);
        return b[0];
    }

}
