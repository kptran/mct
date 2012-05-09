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
 * HibernateUtil.java Sep 28, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.persistence.util;

import gov.nasa.arc.mct.persistence.config.access.DatabaseNameConfigAccess;
import gov.nasa.arc.mct.persistence.hibernate.MCTCurrentSessionContext;
import gov.nasa.arc.mct.persistence.hibernate.MCTHibernateSessionFactoryWrapper;
import gov.nasa.arc.mct.util.StringUtil;
import gov.nasa.arc.mct.util.exception.MCTException;
import gov.nasa.arc.mct.util.exception.MCTRuntimeException;
import gov.nasa.arc.mct.util.logging.MCTLogger;
import gov.nasa.arc.mct.util.property.MCTProperties;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.Configuration;
import org.hibernate.stat.Statistics;

public class HibernateUtil {
    private static final MCTLogger ADVISORY_SERVICE_LOGGER = MCTLogger.getLogger("gov.nasa.jsc.advisory.service");
    private static final String DEFAULT_DATABASE_NAME = "mct";
    private static final MCTProperties databaseProperties;

    private static MCTHibernateSessionFactoryWrapper sessionFactory;

    public static final String ENCODED_PASSWORD_PREFIX = "base64:";
    private static int MAX_POOL_SIZE;

    static {
        try {
            databaseProperties = new MCTProperties("properties/persistence.properties");
        } catch (IOException e) {
            throw new MCTRuntimeException(e);
        }
    }

    public static synchronized MCTHibernateSessionFactoryWrapper getSessionFactory() {
        if (sessionFactory == null) {
            initSessionFactory(databaseProperties);
        }
        return sessionFactory;
    }

    /**
     * Primary entry for DB loader session creation.
     * 
     * @param properties
     *            set of properties containing database spec, and the hibernate
     *            config file.
     */
    public static synchronized void initSessionFactory(Properties properties) {
        String hibernateConfigFile = properties.getProperty("hibernate_config_file", "hibernate.cfg.xml");

        if (sessionFactory == null) {
            initSessionFactory(hibernateConfigFile, properties);
        }
        initDBIfNeeded(hibernateConfigFile);
    }

    /**
     *Test entry for MCT session creation.
     * 
     * @param configPath
     *            hibernate config file
     */
    public static synchronized void initSessionFactory(String configPath) {
        if (sessionFactory == null) {
            Configuration cfg = new Configuration();

            sessionFactory = new MCTHibernateSessionFactoryWrapper(cfg.configure(configPath).buildSessionFactory());
        }
    }

    public static synchronized void initSessionFactory(String configPath, Properties userDatabaseProperties) {
        if (sessionFactory == null) {     
            Configuration cfg = new Configuration();
            createHibernateConfig(cfg, userDatabaseProperties);
            
            sessionFactory = new MCTHibernateSessionFactoryWrapper(cfg.configure(configPath).buildSessionFactory());
            
            MAX_POOL_SIZE = Integer.valueOf(cfg.getProperty("hibernate.c3p0.max_size"));

            ADVISORY_SERVICE_LOGGER.info("MCT database parameters are" + " Connection URL: " + getDatabaseURL()
                    + " Database Username: " + getDatabaseUserName() + " Database name: " + getDatabaseName());

        }
    }

    /**
     * Create a stateless session.  The hiberate config file (xml file with <hibernate-configuration>) is taken
     * from the deployment, however, the database properties are defined independently. This is primarily used
     * for admin tasks that target a database independent of the MCT deployment.
     * 
     * @param dbProps definition of the database
     * @return a stateless hibernate session
     * @throws MCTException upon failure
     */
    public static synchronized StatelessSession createStatelessSession(Properties dbProps) throws MCTException {
        String hibernateConfigFile = databaseProperties.getProperty("hibernate_config_file");
        Configuration cfg = new Configuration();
        createHibernateConfig(cfg, dbProps);

        // create a new session with this config
        org.hibernate.SessionFactory sFactory = cfg.configure(hibernateConfigFile).buildSessionFactory();

        StatelessSession s = sFactory.openStatelessSession();
        if (s == null)
            throw new MCTException("bad session creation");
        return s;
    }

    /** utility to transfer database definitions into the hibernate Configuration */
    private static void createHibernateConfig(Configuration cfg, Properties userDatabaseProperties) {

        String databaseName = userDatabaseProperties.getProperty("mct.database_name");
        if (databaseName == null) {
            if (DatabaseNameConfigAccess.getDatabaseNameConfig() != null) {
                databaseName = DatabaseNameConfigAccess.getDatabaseNameConfig().getDatabaseName();
            }
        }

        String databaseConnectionURL = userDatabaseProperties.getProperty("mct.database_connectionUrl");
        String databaseUserName = userDatabaseProperties.getProperty("mct.database_userName");
        String databasePasswordEncoded = userDatabaseProperties.getProperty("mct.database_password");
        String databaseConnectionProperties = userDatabaseProperties.getProperty("mct.database_properties", "")
                .trim();
        if (!databaseConnectionProperties.isEmpty()) {
            databaseConnectionProperties = "?" + databaseConnectionProperties;
        }

        String databasePassword = null;
        if (databasePasswordEncoded != null) {
            if (!databasePasswordEncoded.startsWith(ENCODED_PASSWORD_PREFIX)) {
                databasePassword = databasePasswordEncoded;
            } else {
                byte[] encoded;
                try {
                    encoded = databasePasswordEncoded.substring(ENCODED_PASSWORD_PREFIX.length()).getBytes(
                            "ISO-8859-1");
                    byte[] unencoded = Base64.decodeBase64(encoded);
                    databasePassword = new String(unencoded, "ISO-8859-1");
                } catch (UnsupportedEncodingException e) {
                    // Ignore - shouldn't happen
                }
            }
        }

        if (!StringUtil.isEmpty(databaseConnectionURL)) {
            if (!databaseConnectionURL.endsWith("/")) {
                databaseConnectionURL += "/";
            }
            cfg.setProperty("hibernate.connection.url", databaseConnectionURL + databaseName
                    + databaseConnectionProperties);
        }
        if (!StringUtil.isEmpty(databaseUserName)) {
            cfg.setProperty("hibernate.connection.username", databaseUserName);
        }
        if (!StringUtil.isEmpty(databasePassword)) {
            cfg.setProperty("hibernate.connection.password", databasePassword);
        }

        cfg.setProperty("hibernate.current_session_context_class", MCTCurrentSessionContext.class.getName());
        
    }

    
    private static void initDBIfNeeded(String configPath) {
        if (configPath.indexOf("derby") != -1) {
            Session session = getSession();
            try {
                SampleDbLoader.load(session);
            } finally {
                closeSession();
            }
        }
    }

    public static Session getSession() {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        return session;
    }

    public static Session getCurrentSession() {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        return session;

    }

    public static Session startSession() {
        Session session = getSessionFactory().getCurrentSession();
        return session;
    }

    public static boolean hasCurrentSession() {
        Session session = getSessionFactory().getCurrentSession();
        boolean sessionAlreadyOpen = session.isOpen() && session.getTransaction().isActive();
        return sessionAlreadyOpen;
    }

    public static void closeSession() {
        Session currentSession = getSessionFactory().getCurrentSession();
        closeSession(currentSession);
    }

    public static void closeSession(Session session) {
        if (session.isOpen()) {
            session.close();
        }
    }

    public static boolean hasCurrentSession(String id) {
        id = getAssociatedDelegateSessionId(id);
        Session session = getSessionFactory().getCurrentSession(id);
        boolean sessionAlreadyOpen = session.isOpen() && session.getTransaction().isActive();
        return sessionAlreadyOpen;
    }

    public static Session getCurrentSession(String id) {
        id = getAssociatedDelegateSessionId(id);
        Session session = getSessionFactory().getCurrentSession(id);
        session.beginTransaction();
        return session;
    }

    public static Session openSession(String id) {
        id = getAssociatedDelegateSessionId(id);
        Session session = getSessionFactory().getCurrentSession(id);

        return session;
    }

    public static void closeSession(String id) {
        getSessionFactory().closeSession(id);
        disassociateDelegateSessionId(id);
    }

    public static void associateDelegateSessionId(String sessionId, String delegateSessionId) {
        getSessionFactory().associateDelegateSessionId(sessionId, delegateSessionId);
    }

    public static void disassociateDelegateSessionId(String sessionId) {
        getSessionFactory().disassociateDelegateSessionId(sessionId);
    }

    public static boolean hasAssociatedDelegateSessionId(String sessionId) {
        return getSessionFactory().hasAssociatedDelegateSessionId(sessionId);
    }

    public static String getAssociatedDelegateSessionId(String sessionId) {
        String id = getSessionFactory().getAssociatedDelegateSessionId(sessionId);
        if (id == null) {
            id = sessionId;
        }
        return id;
    }

    public static String getDatabaseUserName() {
        return databaseProperties.getProperty("mct.database_userName", "unset");
    }

    public static String getDatabaseURL() {
        return databaseProperties.getProperty("mct.database_connectionUrl", "unset");
    }

    public static String getDatabaseName() {
        return databaseProperties.getProperty("mct.database_name", DEFAULT_DATABASE_NAME);
    }
    
    public static boolean canOpenAnotherConnection() {
        Statistics statistics = sessionFactory.getStatistics();
        long sessionOpenCount = statistics.getSessionOpenCount();
        long sessionCloseCount = statistics.getSessionCloseCount();
        
        return MAX_POOL_SIZE - (sessionOpenCount - sessionCloseCount) > 1;
    }
}
