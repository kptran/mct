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
package gov.nasa.arc.mct.exception;

import static org.testng.Assert.assertEquals;
import gov.nasa.arc.mct.persistence.util.HibernateUtil;
import gov.nasa.arc.mct.util.property.MCTProperties;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.varia.NullAppender;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.exception.JDBCConnectionException;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DefaultExceptionHandlerTest {

    protected static class MyAppender extends NullAppender {
        
        List<LoggingEvent> events = new ArrayList<LoggingEvent>();

        @Override
        protected void append(LoggingEvent event) {
            events.add(event);
        }

        @Override
        public void doAppend(LoggingEvent event) {
            append(event);
        }

        public void clearEvents() {
            events.clear();
        }

        public List<LoggingEvent> getEvents() {
            return events;
        }

    }
    // Then, configure the underlying logger the way we want.
    Logger logger = Logger.getLogger(DefaultExceptionHandler.class);
    MyAppender appender = new MyAppender();

    @BeforeClass
    public void initLogging() {
        logger.addAppender(appender);
    }
    
    @BeforeMethod
    public void initTest() {
        appender.clearEvents();
    }
    
    @AfterMethod
    public void cleanTest() {
        appender.clearEvents();
    }
    
    
    @Test 
    public void testInstantiation() throws Exception {
        DefaultExceptionHandler handler = new DefaultExceptionHandler(false);
        assertEquals(appender.getEvents().size(), 1);
    }
    
    @Test
    public void testExceptionHandler() throws Exception {
        UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(false));
        
        Thread t = new Thread() {
            public void run() {
                throw new RuntimeException("Should be uncaught");
            }
        };
        t.start();
        t.join();
        
        Thread.setDefaultUncaughtExceptionHandler(handler);
        assertEquals(appender.getEvents().size(), 2);
    }
    
    @Test 
    public void testHibernateException()  {
        DefaultExceptionHandler mct_def = new DefaultExceptionHandler(false);
        mct_def.uncaughtException(Thread.currentThread(), new HibernateException("testHibernateException"));     
        assertEquals(appender.getEvents().size(), 2);
        List<LoggingEvent> loggingEvents = appender.getEvents();
        Throwable throwable = loggingEvents.get(1).getThrowableInformation().getThrowable();
        Assert.assertTrue(throwable instanceof HibernateException );    
    }
    
    @Test 
    public void testJDBCConnectionException()  {

        MCTProperties mctProperties = MCTProperties.DEFAULT_MCT_PROPERTIES;
        //mctProperties.setProperty("hibernate_config_file", "defaultTest/hibernate_derby_test.cfg.xml");
        mctProperties.setProperty("hibernate_config_file", "/persistence/hibernate_derby.cfg.xml");
        HibernateUtil.initSessionFactory(mctProperties);
        
        DefaultExceptionHandler mct_def = new DefaultExceptionHandler(false);
        mct_def.uncaughtException(Thread.currentThread(), new JDBCConnectionException("test", null));     
        
        assertEquals(appender.getEvents().size(), 2);
        List<LoggingEvent> loggingEvents = appender.getEvents();
        Throwable throwable = loggingEvents.get(1).getThrowableInformation().getThrowable();
        Assert.assertTrue(throwable instanceof JDBCConnectionException );    
    }
    
    @Test 
    public void testJDBCException()  {
        DefaultExceptionHandler mct_def = new DefaultExceptionHandler(false);
        mct_def.uncaughtException(Thread.currentThread(), new JDBCException("test", null));     
        assertEquals(appender.getEvents().size(), 2);
        List<LoggingEvent> loggingEvents = appender.getEvents();
        Throwable throwable = loggingEvents.get(1).getThrowableInformation().getThrowable();
        Assert.assertTrue(throwable instanceof JDBCException );    
    }
}
