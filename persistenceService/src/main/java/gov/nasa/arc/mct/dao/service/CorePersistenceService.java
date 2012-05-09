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
package gov.nasa.arc.mct.dao.service;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.dao.persistence.strategy.ComponentSpecificationDaoStrategy;
import gov.nasa.arc.mct.dao.specifications.ComponentSpecification;
import gov.nasa.arc.mct.dao.specifications.Discipline;
import gov.nasa.arc.mct.dao.specifications.MCTUser;
import gov.nasa.arc.mct.persistence.util.HibernateUtil;
import gov.nasa.arc.mct.persistmgr.PersistenceBroker;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.registry.GlobalComponentRegistry;
import gov.nasa.arc.mct.services.internal.component.User;
import gov.nasa.arc.mct.util.logging.MCTLogger;
import gov.nasa.arc.mct.util.property.MCTProperties;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;

/**
 * Core persistence service implementation.
 *
 */
public class CorePersistenceService {
    private static final MCTLogger logger = MCTLogger.getLogger(CorePersistenceService.class);
    private static final int MAX_RESULTS = Integer.parseInt(((String) MCTProperties.DEFAULT_MCT_PROPERTIES
            .get("database.max.results")));

    private static final Map<Class<?>, JAXBContext> marshalCache = new ConcurrentHashMap<Class<?>, JAXBContext>();

    private static final String SEARCH_SESSION = "searchSession";
    private static final String DBPOLL_SESSION = "databasePollSession";
    private static final String DUPCOMP_SESSION = "duplicateComponentSession";
    
    private CorePersistenceService() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
           @Override
           public void run() {
               HibernateUtil.getCurrentSession(SEARCH_SESSION).cancelQuery();
           } 
        });
    }
    
    /**
     * Loads the mine component based on MCT user.
     * @param user - MCTUser
     * @return ComponentSpecification - the component spec.
     */
    @SuppressWarnings("unchecked")
    public static ComponentSpecification loadMineComponent(MCTUser user) {

        Session session = HibernateUtil.getSession();
        try {
            List<ComponentSpecification> components = session.createCriteria(ComponentSpecification.class).add(
                    Restrictions.eq("owner", user.getUserId())).add(Restrictions.eq("name", GlobalComponentRegistry.MINE)).list();

            assert components != null && components.size() > 0 : "No " + GlobalComponentRegistry.MINE
                    + " found for user " + user.getUserId();

            return components.get(0);

        } finally {
            HibernateUtil.closeSession();
        }

    }

    /**
     * Loads user mine root component based on regular user. 
     * @param user - Regular user.
     * @return list of ComponentSpecification.
     */
    @SuppressWarnings("unchecked")
    public static List<ComponentSpecification> loadUserMineRootComponents(User user) {

        Session session = HibernateUtil.getSession();
        try {
            List<ComponentSpecification> components = session.createCriteria(ComponentSpecification.class).add(Restrictions.eq("owner", user.getUserId()))
                    .add(Restrictions.isEmpty("parentComponents")).list();
            return components;

        } finally {
            HibernateUtil.closeSession();
        }
    }

    /**
     * Gets list of users from disciplines.
     * @param disciplines - string of disciplines.
     * @return list of users
     */
    @SuppressWarnings("unchecked")
    public static List<User> getUsersFromDisciplines(String... disciplines) {
        boolean hasCurrentSession = HibernateUtil.hasCurrentSession();
        Session session = HibernateUtil.getSession();
        try {
            List<Discipline> disciplineList = session.createCriteria(Discipline.class).add(
                    Restrictions.in("disciplineId", disciplines)).list();
            List<User> users = new ArrayList<User>();
            for (Discipline discipline : disciplineList)
                users.addAll(discipline.getUsers());
            return users;
        } finally {
            if (!hasCurrentSession) {
                HibernateUtil.closeSession();
            }
        }
    }
    
    /**
     * Gets the search results for telemetry metadata from PUI pattern.
     * @param puiPattern - regex pattern
     * @return QueryResult for search telemetry metadata
     */
    @SuppressWarnings("unchecked")
    public static QueryResult searchTelemetryMeta(String puiPattern) {
        puiPattern = formatPUIPattern(puiPattern);
        StringBuilder query = new StringBuilder("select c.* from component_spec c where c.component_type='gov.nasa.arc.mct.components.telemetry.TelemetryElementComponent' and c.external_key = :puiPattern");
        Session session = HibernateUtil.getCurrentSession(SEARCH_SESSION);
        try {
            Query q = session.createSQLQuery(query.toString()).addEntity(ComponentSpecification.class);
            q.setParameter("puiPattern", puiPattern);
            List<ComponentSpecification> list = q.list();
            return new QueryResult(list.size(), list);
        } catch (Exception t) {
            logger.error("error executing query", t);
            return null;
        } finally {
            HibernateUtil.closeSession(SEARCH_SESSION);
        }
        
    }
    
    private static void makeSessionReadOnly(Session session) {
        session.doWork(new Work() {

            @Override
            public void execute(Connection conn) throws SQLException {
                conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                conn.setReadOnly(true);
            }
            
        });
    }
    
    /**
     * Finds all the components by base display name regex pattern.
     * @param pattern - regex.
     * @param props <code>Properties</code> set arguments for SQL query.
     * @return QueryResult - search results.
     */
    @SuppressWarnings("unchecked")
    public static QueryResult findComponentsByBaseDisplayedNamePattern(String pattern, Properties props) {
        User currentUser = PlatformAccess.getPlatform().getCurrentUser();
        String username = currentUser.getUserId();
        String rootComponentId = PlatformAccess.getPlatform().getComponentRegistry().getRootComponentId(); 
        
        pattern = pattern.isEmpty() ? "%" : pattern.replace('*', '%');
        
        String countQuery = "select count(*) from component_spec c "
                        + "where c.deleted = 0 "
                        + "and c.creator_user_id like :creator "
                        + "and c.component_id != :rootComponentId "
                        + "and (c.component_type != 'gov.nasa.arc.mct.core.components.MineTaxonomyComponent' or c.owner = :owner) "
                        + "and c.component_name like :pattern ;";
                
        String entitiesQuery = "select c.* from component_spec c "
            + "where c.deleted = 0 "
            + "and c.creator_user_id like :creator "
            + "and c.component_id != :rootComponentId "
            + "and (c.component_type != 'gov.nasa.arc.mct.core.components.MineTaxonomyComponent' or c.owner = :owner) "
            + "and c.component_name like :pattern "
            + "limit " + MAX_RESULTS + ";";
        

        Session session = HibernateUtil.getCurrentSession(SEARCH_SESSION);
        CorePersistenceService.makeSessionReadOnly(session);
        try {
            Query q = session.createSQLQuery(countQuery);
            q.setParameter("pattern", pattern);
            q.setParameter("owner", username);
            q.setParameter("rootComponentId", rootComponentId);
            q.setParameter("creator", (props != null && props.get("creator") != null) ? props.get("creator") : "%" );    
            List<Object> list = q.list();
            int count = ((Number) list.get(0)).intValue();

            q = session.createSQLQuery(entitiesQuery).addEntity(ComponentSpecification.class);
            q.setParameter("pattern", pattern);
            q.setParameter("owner", username);
            q.setParameter("rootComponentId", rootComponentId); 
            q.setParameter("creator", (props != null && props.get("creator") != null) ? props.get("creator") : "%" );    
            List<ComponentSpecification> daoObjects = q.list();
            return new QueryResult(count, daoObjects);
        } catch (Exception t) {
            logger.error("error executing query", t);
            return null;
        } finally {
            HibernateUtil.closeSession(SEARCH_SESSION);
        }        
    }
   
    /**
     * Gets all the referencing components based on component id.
     * @param componentId - the component id.
     * @return collection of ComponentSpecification.
     */
    public static Collection<ComponentSpecification> getReferencingComponents(String componentId) {
        String query = "select c1.* from component_spec c1 join component_relationship r join component_spec c2 on c1.component_id = r.component_id and c2.component_id = r.associated_component_id where c2.component_id = :id ;";
        String sessionId = UUID.randomUUID().toString();
        Session session = HibernateUtil.getCurrentSession(sessionId);
        try {
            Query q = session.createSQLQuery(query).addEntity(ComponentSpecification.class);
            q.setParameter("id", componentId);
            @SuppressWarnings("unchecked")
            List<ComponentSpecification> daoObjects = q.list();
            return daoObjects;
        } catch (Exception t) {
            logger.error("error executing query", t);
            return Collections.emptySet();
        } finally {
            HibernateUtil.closeSession(sessionId);            
        }        
    }
    
    /**
     * Opens the Hibernate session.
     * @param sessionId - Hibernate session id.
     */
    public static void openSession(String sessionId) {
        GlobalContext.getGlobalContext().getSynchronousPersistenceBroker().startSession(sessionId);
    }
    
    /**
     * Closes Hibernate session.
     * @param sessionId - Hibernate session id.
     */             
    public static void closeSession(String sessionId) {
        GlobalContext.getGlobalContext().getSynchronousPersistenceBroker().closeSession(sessionId);
    }
    
    /**
     * Gets the component based upon session id and component id.
     * @param loadSessionId - session id.
     * @param compId - the component id.
     * @return compSpec - Component spec.
     */
    public static ComponentSpecification getComponent(String loadSessionId, String compId) {
        PersistenceBroker syncPersistenceBroker = GlobalContext.getGlobalContext().getSynchronousPersistenceBroker();
        ComponentSpecification compSpec = syncPersistenceBroker.loadByIdEagerly(loadSessionId, ComponentSpecification.class, compId, new String[]{"parentComponents", "associatedComponents"});
        return compSpec;
    }
    
    /**
     * Saves the component.
     * @param sessionId - Hibernate session id.
     * @param comp - ComponentSpecification.
     */
    public static void saveComponent(String sessionId, ComponentSpecification comp) {
        PersistenceBroker syncPersistenceBroker = GlobalContext.getGlobalContext().getSynchronousPersistenceBroker();
        syncPersistenceBroker.save(sessionId, comp, null);
    }
    
    /**
     * Gets MCT user.
     * @param sessionId - Hibernate session id.
     * @param userId - MCT user id.
     * @return user - MCTUser object.
     */
    public static MCTUser getUser(String sessionId, String userId) {
        PersistenceBroker syncPersistenceBroker = GlobalContext.getGlobalContext().getSynchronousPersistenceBroker();
        MCTUser user = syncPersistenceBroker.loadById(sessionId, MCTUser.class, userId);
        return user;
    }

    /**
     * Gets all the disciplines.
     * @return returnDisciplines - collection of disciplines.
     */
    public static Collection<String> getAllDisciplines() {
        PersistenceBroker syncPersistenceBroker = GlobalContext.getGlobalContext().getSynchronousPersistenceBroker();
        Collection<Discipline> allDisciplines = syncPersistenceBroker
                .loadAllOrderedBy(Discipline.class, "disciplineId");
        List<String> returnDisciplines = new ArrayList<String>();

        for (Discipline discipline : allDisciplines) {
            returnDisciplines.add(discipline.getDisciplineId());
        }

        return returnDisciplines;
    }

    /**
     * Gets all user per discipline.
     * @param disciplineId - The discipline id.
     * @return collection of users per specific discipline.
     */
    public static Collection<String> getAllUsersOfDiscipline(String disciplineId) {
        PersistenceBroker syncPersistenceBroker = GlobalContext.getGlobalContext().getSynchronousPersistenceBroker();
        Discipline discipline = syncPersistenceBroker.lazilyLoad(disciplineId, Discipline.class, disciplineId);
        try {
            Set<MCTUser> users = discipline.getUsers();
            List<String> userIds = new ArrayList<String>();
            for (MCTUser user : users) {
                userIds.add(user.getUserId());
            }
            return userIds;
        } finally {
            syncPersistenceBroker.lazilyLoadCompleted(disciplineId);
        }
    }

    /**
     * Gets all users.
     * @return collection of users.
     */
    public static Set<String> getAllUsers() {
        PersistenceBroker syncPersistenceBroker = GlobalContext.getGlobalContext().getSynchronousPersistenceBroker();
        Collection<MCTUser> allUsers = syncPersistenceBroker.loadAllOrderedBy(MCTUser.class, "userId");
        Set<String> returnUsers = new HashSet<String>();
        for (MCTUser u : allUsers) {
            returnUsers.add(u.getUserId());
        }
        return returnUsers;
    }
    
    /**
     * Loads the component from component id.
     * @param componentId - the component id.
     * @return AbstractComponent
     */
    public static AbstractComponent loadComponent(String componentId) {
        return ComponentSpecificationDaoStrategy.loadComponent(componentId);
    }

    /**
     * Marshals the data and return the marshalled String. The object is
     * converted between byte and Unicode characters using the named encoding
     * "UTF-8".
     * 
     * @param toBeMarshalled
     *            the object whose data is to be marshalled.
     * @throws JAXBException - JAXB XML exception handling.
     * @throws UnsupportedEncodingException - Unsupported encoding exception handling.
     * @return out marshalled string.
     * @param <T> string
     */
    public static <T> String marshal(T toBeMarshalled) throws JAXBException, UnsupportedEncodingException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Class<?> clazz = toBeMarshalled.getClass();
        JAXBContext ctxt = marshalCache.get(clazz);
        if (ctxt == null) {
            ctxt = JAXBContext.newInstance(clazz);
            marshalCache.put(clazz, ctxt);
        }
        Marshaller marshaller = ctxt.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "ASCII");
        marshaller.marshal(toBeMarshalled, out);
        return out.toString("ASCII");
    }

    /**
     * Unmarshals the supplied bytes and return the unmarshalled object.
     * 
     * @param unMarshalledClazz
     *            the class of the object to be created from the unmarshalling
     *            operation
     * @param bytes
     *            the byte that contains the marshalled data.
     * @return the object created from unmarshalling the model role data.
     * @throws DataBindingException - data binding exception handling.
     * @throws JAXBException - JAXB XML exception handling.
     * @param <T> unmarshal
     */
    public static <T> T unmarshal(Class<T> unMarshalledClazz, byte[] bytes) throws DataBindingException, JAXBException {
        InputStream is = new ByteArrayInputStream(bytes);
        JAXBContext jc = marshalCache.get(unMarshalledClazz);
        if (jc == null) {
            jc = JAXBContext.newInstance(unMarshalledClazz);
            marshalCache.put(unMarshalledClazz, jc);
        }
        Unmarshaller u = jc.createUnmarshaller();
        return unMarshalledClazz.cast(u.unmarshal(is));
    }

    /**
     * Formats the PUI search pattern for SQL.
     * 
     * @param puiPattern
     * @return the formatted search pattern
     */
    static String formatPUIPattern(String puiPattern) {
        return puiPattern.replace('*', '%');
    }

    /**
     * Formats the Ops name search pattern for SQL.
     * 
     * @param opsPattern
     * @return the formated search pattern
     */
    static String formatOpsPattern(String opsPattern) {
        return opsPattern.replace('*', '%');
    }
    
    /**
     * Gets the displine.
     * @param disciplineId - the discipline id.
     * @param sessionId - Hibernate session id.
     * @return discipline 
     */
    public static Discipline getDispline(String disciplineId, String sessionId) {
        PersistenceBroker broker = GlobalContext.getGlobalContext().getSynchronousPersistenceBroker();
        Discipline discipline = broker.loadById(sessionId, Discipline.class, disciplineId);
        return discipline;        
    }
    
    /**
     * This interface allows the caller to iterator over the list of changed components with an active cursor.
     *
     */
    public interface ChangedComponentVisitor {
        /**
         * This method is invoked for each component that has changed. 
         * @param component that has changed 
         */
        void operateOnComponent(ComponentSpecification component);
    }
    
    /**
     * Iterates over changed components.
     * @param visitor - Changed component visitor.
     */
    public static void iterateOverChangedComponents(ChangedComponentVisitor visitor) {
        String query = "select * from component_spec where last_modified > subtime(now(), '00:05:00');";        
        Session session = HibernateUtil.getCurrentSession(DBPOLL_SESSION);
        CorePersistenceService.makeSessionReadOnly(session);
        try {
            Query q = session.createSQLQuery(query).addEntity(ComponentSpecification.class);
            final int MAX_CACHE_SIZE = 500;
            int count = 0;
            ScrollableResults results = q.scroll(ScrollMode.FORWARD_ONLY);
            while (results.next()) {
                ComponentSpecification cs = (ComponentSpecification) results.get()[0];
                if (++count % MAX_CACHE_SIZE == 0) {
                    session.clear();
                }
                visitor.operateOnComponent(cs);
            }
        } catch (Exception t) {
            logger.error("error executing query", t);
        } finally {
            HibernateUtil.closeSession(DBPOLL_SESSION);
        }        
    }
    
    /**
     * Gets all the changed component ids.
     * @return list of component specs.
     */
    @SuppressWarnings("unchecked")
    public static List<ComponentSpecification> getChangedComponentIDs() {
        String query = "select * from component_spec where last_modified > subtime(now(), '00:05:00');";        
        Session session = HibernateUtil.getCurrentSession(DBPOLL_SESSION);
        CorePersistenceService.makeSessionReadOnly(session);
        try {
            Query q = session.createSQLQuery(query).addEntity(ComponentSpecification.class);
            return q.list();
        } catch (Exception t) {
            logger.error("error executing query", t);
            return null;
        } finally {
            HibernateUtil.closeSession(DBPOLL_SESSION);
        }        
    }
    
    /**
     * Duplicates the component based upon target id, parent id, and name.
     * @param targetId - target id.
     * @param parentId - parent id.
     * @param name - the component name.
     * @return id - the UUID
     */
    public static String duplicateComponent(String targetId, String parentId, String name) {
        String id = UUID.randomUUID().toString().replace("-", "");
        String userId = PlatformAccess.getPlatform().getCurrentUser().getUserId();
        
        Session session = HibernateUtil.getCurrentSession(DUPCOMP_SESSION);
        try {
            String str;
            str = "insert into component_spec (component_id, creator_user_id, component_name, owner, component_type, model_info) select :id, creator_user_id, :name, :userId, component_type, model_info from component_spec where component_id = :targetId ;";      
            Query q = session.createSQLQuery(str);
            q.setParameter("id", id);
            q.setParameter("name", name);
            q.setParameter("userId", userId);
            q.setParameter("targetId", targetId);
            q.executeUpdate();
            
            str = "insert into component_relationship (component_id, associated_component_id, seq_no) select :id, associated_component_id, seq_no from component_relationship where component_id = :targetId ;";
            q = session.createSQLQuery(str);
            q.setParameter("id", id);
            q.setParameter("targetId", targetId);
            q.executeUpdate();
            
            str = "insert into component_relationship (component_id, associated_component_id, seq_no) select :parentId, :id, (select coalesce ((max(seq_no) + 1), 0) from component_relationship where component_id = :parentId);";
            q = session.createSQLQuery(str);
            q.setParameter("id", id);
            q.setParameter("parentId", parentId);
            q.executeUpdate();
            
            str = "update component_spec set obj_version = obj_version + 1 where component_id = :parentId ;";
            q = session.createSQLQuery(str);
            q.setParameter("parentId", parentId);
            q.executeUpdate();
            
            session.getTransaction().commit();
        } catch (Exception t) {
            logger.error("error executing query", t);
            return null;
        } finally {
        	if (session.getTransaction().isActive())
        	    session.getTransaction().rollback();
            HibernateUtil.closeSession(DUPCOMP_SESSION);
        }        

        return id;
        
    }
        
}
