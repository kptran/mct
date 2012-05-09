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
package gov.nasa.arc.mct.persistence.hibernate;

import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.transaction.TransactionManager;

import org.hibernate.ConnectionReleaseMode;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cache.QueryCache;
import org.hibernate.cache.Region;
import org.hibernate.cache.UpdateTimestampsCache;
import org.hibernate.cfg.Settings;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.SQLFunctionRegistry;
import org.hibernate.engine.FilterDefinition;
import org.hibernate.engine.NamedQueryDefinition;
import org.hibernate.engine.NamedSQLQueryDefinition;
import org.hibernate.engine.ResultSetMappingDefinition;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.query.QueryPlanCache;
import org.hibernate.exception.SQLExceptionConverter;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.EntityNotFoundDelegate;
import org.hibernate.stat.Statistics;
import org.hibernate.stat.StatisticsImplementor;
import org.hibernate.type.Type;

public class MCTHibernateSessionFactoryWrapper implements SessionFactory, SessionFactoryImplementor {
    private static final long serialVersionUID = -7123298328791238782L;

    private final SessionFactoryImplementor sessionFactory;
    private final Map<String, String> delegateSessionIdMap = new HashMap<String, String>();
    private final Map<String, Set<String>> reverseSessionIdMap = new HashMap<String, Set<String>>();

    public MCTHibernateSessionFactoryWrapper(SessionFactory sessionFactory) {
        this.sessionFactory = (SessionFactoryImplementor) sessionFactory;
    }

    public synchronized Session getCurrentSession(String id) {
        PersistenceContext.setId(getRealSessionId(id));

        return sessionFactory.getCurrentSession();
    }

    public synchronized void closeSession(String id) {
        Session session = getCurrentSession(id);
        if (session.isOpen()) {
            session.close();
        }
    }

    private String getRealSessionId(String id) {
        if (id == null) {
            return null;
        }
        String deletegateID = delegateSessionIdMap.get(id);
        if (deletegateID == null) {
            deletegateID = id;
        }
        return deletegateID;
    }

    public synchronized org.hibernate.classic.Session getCurrentSession() {
        PersistenceContext.reset();
        return sessionFactory.getCurrentSession();
    }

    public synchronized void closeSession() {
        Session session = getCurrentSession();
        if (session.isOpen()) {
            session.close();
        }
    }

    @Override
    public void close() throws HibernateException {
        this.sessionFactory.close();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void evict(Class persistentClass) throws HibernateException {
        this.sessionFactory.evict(persistentClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void evict(Class persistentClass, Serializable id) throws HibernateException {
        this.sessionFactory.evict(persistentClass, id);
    }

    @Override
    public void evictCollection(String roleName) throws HibernateException {
        this.sessionFactory.evictCollection(roleName);
    }

    @Override
    public void evictCollection(String roleName, Serializable id) throws HibernateException {
        this.sessionFactory.evictCollection(roleName, id);
    }

    @Override
    public void evictEntity(String entityName) throws HibernateException {
        this.sessionFactory.evictEntity(entityName);
    }

    @Override
    public void evictEntity(String entityName, Serializable id) throws HibernateException {
        this.sessionFactory.evictEntity(entityName, id);
    }

    @Override
    public void evictQueries() throws HibernateException {
        this.sessionFactory.evictQueries();
    }

    @Override
    public void evictQueries(String cacheRegion) throws HibernateException {
        this.sessionFactory.evictQueries(cacheRegion);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map getAllClassMetadata() throws HibernateException {
        return this.sessionFactory.getAllClassMetadata();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map getAllCollectionMetadata() throws HibernateException {
        return this.sessionFactory.getAllCollectionMetadata();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException {
        return this.sessionFactory.getClassMetadata(persistentClass);
    }

    @Override
    public ClassMetadata getClassMetadata(String entityName) throws HibernateException {
        return this.sessionFactory.getClassMetadata(entityName);
    }

    @Override
    public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException {
        return this.sessionFactory.getCollectionMetadata(roleName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set getDefinedFilterNames() {
        return this.sessionFactory.getDefinedFilterNames();
    }

    @Override
    public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
        return this.sessionFactory.getFilterDefinition(filterName);
    }

    @Override
    public Statistics getStatistics() {
        return this.sessionFactory.getStatistics();
    }

    @Override
    public boolean isClosed() {
        return this.sessionFactory.isClosed();
    }

    @Override
    public org.hibernate.classic.Session openSession() throws HibernateException {
        return this.sessionFactory.openSession();
    }

    @Override
    public org.hibernate.classic.Session openSession(Connection connection) {
        return this.sessionFactory.openSession(connection);
    }

    @Override
    public org.hibernate.classic.Session openSession(Interceptor interceptor) throws HibernateException {
        return this.sessionFactory.openSession(interceptor);
    }

    @Override
    public org.hibernate.classic.Session openSession(Connection connection, Interceptor interceptor) {
        return this.sessionFactory.openSession(connection, interceptor);
    }

    @Override
    public StatelessSession openStatelessSession() {
        return this.sessionFactory.openStatelessSession();
    }

    @Override
    public StatelessSession openStatelessSession(Connection connection) {
        return this.sessionFactory.openStatelessSession(connection);
    }

    @Override
    public Reference getReference() throws NamingException {
        return this.sessionFactory.getReference();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map getAllSecondLevelCacheRegions() {
        return this.sessionFactory.getAllSecondLevelCacheRegions();
    }

    @Override
    public CollectionPersister getCollectionPersister(String role) throws MappingException {
        return this.sessionFactory.getCollectionPersister(role);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set getCollectionRolesByEntityParticipant(String entityName) {
        return this.sessionFactory.getCollectionRolesByEntityParticipant(entityName);
    }

    @Override
    public ConnectionProvider getConnectionProvider() {
        return this.sessionFactory.getConnectionProvider();
    }

    @Override
    public Dialect getDialect() {
        return this.sessionFactory.getDialect();
    }

    @Override
    public EntityNotFoundDelegate getEntityNotFoundDelegate() {
        return this.sessionFactory.getEntityNotFoundDelegate();
    }

    @Override
    public EntityPersister getEntityPersister(String entityName) throws MappingException {
        return this.sessionFactory.getEntityPersister(entityName);
    }

    @Override
    public IdentifierGenerator getIdentifierGenerator(String rootEntityName) {
        return this.sessionFactory.getIdentifierGenerator(rootEntityName);
    }

    @Override
    public String[] getImplementors(String className) throws MappingException {
        return this.sessionFactory.getImplementors(className);
    }

    @Override
    public String getImportedClassName(String name) {
        return this.sessionFactory.getImportedClassName(name);
    }

    @Override
    public Interceptor getInterceptor() {
        return this.sessionFactory.getInterceptor();
    }

    @Override
    public NamedQueryDefinition getNamedQuery(String queryName) {
        return this.sessionFactory.getNamedQuery(queryName);
    }

    @Override
    public NamedSQLQueryDefinition getNamedSQLQuery(String queryName) {
        return this.sessionFactory.getNamedSQLQuery(queryName);
    }

    @Override
    public QueryCache getQueryCache() {
        return this.sessionFactory.getQueryCache();
    }

    @Override
    public QueryCache getQueryCache(String regionName) throws HibernateException {
        return this.sessionFactory.getQueryCache(regionName);
    }

    @Override
    public QueryPlanCache getQueryPlanCache() {
        return this.sessionFactory.getQueryPlanCache();
    }

    @Override
    public ResultSetMappingDefinition getResultSetMapping(String name) {
        return this.sessionFactory.getResultSetMapping(name);
    }

    @Override
    public String[] getReturnAliases(String queryString) throws HibernateException {
        return this.sessionFactory.getReturnAliases(queryString);
    }

    @Override
    public Type[] getReturnTypes(String queryString) throws HibernateException {
        return this.sessionFactory.getReturnTypes(queryString);
    }

    @Override
    public SQLExceptionConverter getSQLExceptionConverter() {
        return this.sessionFactory.getSQLExceptionConverter();
    }

    @Override
    public Region getSecondLevelCacheRegion(String regionName) {
        return this.sessionFactory.getSecondLevelCacheRegion(regionName);
    }

    @Override
    public Settings getSettings() {
        return this.sessionFactory.getSettings();
    }

    @Override
    public SQLFunctionRegistry getSqlFunctionRegistry() {
        return this.sessionFactory.getSqlFunctionRegistry();
    }

    @Override
    public StatisticsImplementor getStatisticsImplementor() {
        return this.sessionFactory.getStatisticsImplementor();
    }

    @Override
    public TransactionManager getTransactionManager() {
        return this.sessionFactory.getTransactionManager();
    }

    @Override
    public UpdateTimestampsCache getUpdateTimestampsCache() {
        return this.sessionFactory.getUpdateTimestampsCache();
    }

    @Override
    public org.hibernate.classic.Session openSession(Connection connection, boolean flushBeforeCompletionEnabled,
            boolean autoCloseSessionEnabled, ConnectionReleaseMode connectionReleaseMode) throws HibernateException {
        return this.sessionFactory.openSession(connection, flushBeforeCompletionEnabled, autoCloseSessionEnabled,
                connectionReleaseMode);
    }

    @Override
    public org.hibernate.classic.Session openTemporarySession() throws HibernateException {
        return this.sessionFactory.openTemporarySession();
    }

    @Override
    public String getIdentifierPropertyName(String className) throws MappingException {
        return this.sessionFactory.getIdentifierPropertyName(className);
    }

    @Override
    public Type getIdentifierType(String className) throws MappingException {
        return this.sessionFactory.getIdentifierType(className);
    }

    @Override
    public Type getReferencedPropertyType(String className, String propertyName) throws MappingException {
        return this.sessionFactory.getReferencedPropertyType(className, propertyName);
    }

    public synchronized void associateDelegateSessionId(String sessionId, String delegateSessionId) {
        this.delegateSessionIdMap.put(sessionId, delegateSessionId);
        Set<String> ids = this.reverseSessionIdMap.get(delegateSessionId);
        if (ids == null) {
            ids = new HashSet<String>();
            this.reverseSessionIdMap.put(delegateSessionId, ids);
        }
        ids.add(sessionId);
    }

    public synchronized void disassociateDelegateSessionId(String sessionId) {
        this.delegateSessionIdMap.remove(sessionId);

        Set<String> ids = this.reverseSessionIdMap.remove(sessionId);
        if (ids != null) {
            for (String id : ids) {
                this.delegateSessionIdMap.remove(id);
            }
        }
    }

    public synchronized boolean hasAssociatedDelegateSessionId(String sessionId) {
        return this.delegateSessionIdMap.containsKey(sessionId);
    }

    public synchronized String getAssociatedDelegateSessionId(String sessionId) {
        return this.delegateSessionIdMap.get(sessionId);
    }
}
