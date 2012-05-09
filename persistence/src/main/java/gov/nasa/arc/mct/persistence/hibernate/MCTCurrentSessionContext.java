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

import gov.nasa.arc.mct.persistence.interceptor.OptimisticSessionInterceptor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.ConnectionReleaseMode;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.context.CurrentSessionContext;
import org.hibernate.engine.SessionFactoryImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * public methods of this class is protected by MCTHibernateSessionFactoryWrapper indirectly. One should not call methods of this
 * class directly.
 *
 */
public final class MCTCurrentSessionContext implements CurrentSessionContext {
    private static final long serialVersionUID = 8420241763662117507L;
    private static final Class<?>[] SESS_PROXY_INTERFACES = new Class[] { org.hibernate.classic.Session.class, org.hibernate.engine.SessionImplementor.class,
                    org.hibernate.jdbc.JDBCContext.Context.class, org.hibernate.event.EventSource.class, Interceptable.class };
    private final static Map<SessionFactoryImplementor, Map<String, Session>> sessionMaps = new HashMap<SessionFactoryImplementor, Map<String, Session>>();
    private static final Logger log = LoggerFactory.getLogger( MCTCurrentSessionContext.class );

    private final SessionFactoryImplementor factory;

    public MCTCurrentSessionContext(SessionFactoryImplementor factory) {
        this.factory = factory;
    }

    @Override
    public org.hibernate.classic.Session currentSession() throws HibernateException {
        if (PersistenceContext.id == null) {
            PersistenceContext.reset();
        }
        Session current = existingSession(factory);
        if (current == null) {
            OptimisticSessionInterceptor interceptor = new OptimisticSessionInterceptor();
            current = buildOrObtainSession(interceptor);
            if (needsWrapping(current)) {
                current = wrap(current, interceptor);
            }
            // bind it
            doBind(current, factory);
        }
        return current;
    }
    
    private static Session existingSession(SessionFactory factory) {
        Map<String, Session> sessionMap = sessionMap(factory);
        if (sessionMap == null) {
            return null;
        } else {
            Session session = sessionMap.get(PersistenceContext.id);
            return session;
        }
    }

    private static Map<String, Session> sessionMap(SessionFactory factory) {
        return sessionMaps.get(factory);
    }

    private Session buildOrObtainSession(OptimisticSessionInterceptor interceptor) {
        Session session = factory.openSession(interceptor);
        return session;
    }

    /**
     * Mainly for subclass usage. This impl always returns after_transaction.
     * 
     * @return The connection release mode for any built sessions.
     */
    protected ConnectionReleaseMode getConnectionReleaseMode() {
        return factory.getSettings().getConnectionReleaseMode();
    }

    private boolean needsWrapping(Session session) {
        // try to make sure we don't wrap an already wrapped session
        if (session == null) {
            return false;
        } else {
            return !Proxy.isProxyClass(session.getClass())
                || (Proxy.getInvocationHandler(session) != null && !(Proxy.getInvocationHandler(session) instanceof TransactionProtectionWrapper));
        }
    }

    private Session wrap(Session session, Interceptor interceptor) {
        TransactionProtectionWrapper wrapper = new TransactionProtectionWrapper(session, PersistenceContext.id, interceptor);
        Session wrapped = (Session) Proxy.newProxyInstance(Session.class.getClassLoader(), SESS_PROXY_INTERFACES, wrapper);
        wrapper.setWrapped(wrapped);
        return wrapped;
    }

    private void doBind(Session session, SessionFactoryImplementor factory) {
        Map<String, Session> sessionMap = sessionMap(factory);
        if (sessionMap == null) {
            sessionMap = new HashMap<String, Session>();
            sessionMaps.put(factory, sessionMap);
        }
        log.debug("Binding {}", PersistenceContext.id);
        sessionMap.put(PersistenceContext.id, session);
    }

    private static Session unbind(SessionFactory factory, boolean releaseMapIfEmpty, String sessionId) {
        Map<String, Session> sessionMap = sessionMap(factory);
        Session session = null;
        if (sessionMap != null) {
            session = sessionMap.remove(sessionId);
            if (releaseMapIfEmpty && sessionMap.isEmpty()) {
                sessionMaps.remove(factory);
            }
        }
        log.debug("Unbinding {}", sessionId);
        return session;
    }

    private final class TransactionProtectionWrapper implements InvocationHandler, Serializable {
        private static final long serialVersionUID = -5502759741632534102L;

        private final Session realSession;
        private Session wrappedSession;
        private final String sessionId;
        private final Interceptor interceptor;

        public TransactionProtectionWrapper(Session realSession, String sessionId, Interceptor interceptor) {
            this.realSession = realSession;
            this.sessionId = sessionId;
            this.interceptor = interceptor;
        }

        /**
         * {@inheritDoc}
         */
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                if ("getInterceptor".equals(method.getName())) {
                    return interceptor;
                }
                // If close() is called, guarantee unbind()
                if ("close".equals(method.getName())) {
                    unbind(realSession.getSessionFactory(), true, this.sessionId);
                } else if ("toString".equals(method.getName()) || "equals".equals(method.getName()) || "hashCode".equals(method.getName())
                                || "getStatistics".equals(method.getName()) || "isOpen".equals(method.getName())) {
                    // allow these to go through the the real session no matter
                    // what
                } else if (!realSession.isOpen()) {
                    // essentially, if the real session is closed allow any
                    // method call to pass through since the real session
                    // will complain by throwing an appropriate exception;
                    // NOTE that allowing close() above has the same basic
                    // effect,
                    // but we capture that there simply to perform the unbind...
                } else if (!realSession.getTransaction().isActive()) {
                    // limit the methods available if no transaction is active
                    if ("beginTransaction".equals(method.getName()) || "getTransaction".equals(method.getName())
                                    || "isTransactionInProgress".equals(method.getName()) || "setFlushMode".equals(method.getName())
                                    || "getSessionFactory".equals(method.getName())) {
                        log.trace("allowing method [{}] in non-transacted context", method.getName());
                    } else if ("reconnect".equals(method.getName()) || "disconnect".equals(method.getName())) {
                        // allow these (deprecated) methods to pass through
                    } else {
                        throw new HibernateException(method.getName() + " is not valid without active transaction");
                    }
                }
                log.trace("allowing proxied method [{}] to proceed to real session", method.getName());
                return method.invoke(realSession, args);
            } catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof RuntimeException) {
                    throw (RuntimeException) e.getTargetException();
                } else {
                    throw e;
                }
            }
        }

        /**
         * Setter for property 'wrapped'.
         * 
         * @param wrapped
         *            Value to set for property 'wrapped'.
         */
        public void setWrapped(Session wrapped) {
            this.wrappedSession = wrapped;
        }

        // serialization ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        private void writeObject(ObjectOutputStream oos) throws IOException {
            // if a ThreadLocalSessionContext-bound session happens to get
            // serialized, to be completely correct, we need to make sure
            // that unbinding of that session occurs.
            oos.defaultWriteObject();
            if (existingSession(factory) == wrappedSession) {
                unbind(factory, true, this.sessionId);
            }
        }

        private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
            // on the inverse, it makes sense that for a
            // bound session deserialized to go ahead and re-bind it
            // to the session map.
            ois.defaultReadObject();
            PersistenceContext.id = this.sessionId;
            //realSession.getTransaction().registerSynchronization(buildCleanupSynch());
            doBind(wrappedSession, factory);
        }
    }
}
