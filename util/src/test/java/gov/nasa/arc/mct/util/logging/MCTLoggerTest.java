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
 * unit test for logging Aug 18, 2008
 * 
 * This code is the property of the National Aeronautics and Space
 * Administration and was produced for the Mission Control Technologies (MCT)
 * project.
 * 
 */

package gov.nasa.arc.mct.util.logging;


import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.varia.NullAppender;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MCTLoggerTest  {
  
	protected  final static String msg = "the log message";
	
    MCTLogger logger;
    Logger baseLogger;
    boolean saveAdditivity;
    MyAppender appender;

    @BeforeClass
    public void initRootLogger() {
    	saveAdditivity = Logger.getLogger(MCTLoggerTest.class).getAdditivity();
    }
    
    @AfterClass
    public void restoreRootLogger() {
    	Logger.getLogger(MCTLoggerTest.class).setAdditivity(saveAdditivity);
    }
    
	@BeforeMethod
	public void initLogging() {
		// First get the MCT logger so it will intialize.
    	logger = MCTLogger.getLogger(MCTLoggerTest.class);
    	
    	MCTLogger.getLogger("abc");

    	// Then get the Log4J logger so we can set up a custom appender.
	    baseLogger = Logger.getLogger(MCTLoggerTest.class);
	    baseLogger.setAdditivity(false);
	    baseLogger.setLevel(Level.ALL);
	    appender = new MyAppender();
	    baseLogger.removeAllAppenders();
        baseLogger.addAppender(appender);
	}
	
	@AfterMethod
	public void restore() {
    	System.setProperty(MCTLogger.MCT_LOG_FILE, "");
	}
	
    @Test
    public void testDebug() throws Exception {
         logger.debug(msg);
         assertEquals(appender.getEvents().size(), 1);
         assertEquals(appender.getEvents().get(0).getLevel(), Level.DEBUG);
         assertEquals(appender.getEvents().get(0).getMessage(), msg);
         assertNull(appender.getEvents().get(0).getThrowableInformation());
         
         appender.clearEvents();
         logger.debug(msg, new Throwable("exception message"));
         assertEquals(appender.getEvents().size(), 1);
         assertEquals(appender.getEvents().get(0).getLevel(), Level.DEBUG);
         assertEquals(appender.getEvents().get(0).getMessage(), msg);
         assertNotNull(appender.getEvents().get(0).getThrowableInformation());
         
         appender.clearEvents();
         logger.debug("abc {0}", "h");
         assertEquals(appender.getEvents().size(), 1);
         assertEquals(appender.getEvents().get(0).getLevel(), Level.DEBUG);
         assertEquals(appender.getEvents().get(0).getMessage(), "abc h");
    }
    
    @Test
    public void testDebugMessageFormat() throws Exception {
        logger.info("Hello {0}", "World");
        assertEquals(appender.getEvents().size(), 1);
        assertEquals(appender.getEvents().get(0).getLevel(), Level.INFO);
        assertEquals(appender.getEvents().get(0).getMessage(), "Hello World");
        assertNull(appender.getEvents().get(0).getThrowableInformation());
    }
    
    @Test
    public void testInfo() throws Exception {
        logger.info(msg);
        assertEquals(appender.getEvents().size(), 1);
        assertEquals(appender.getEvents().get(0).getLevel(), Level.INFO);
        assertEquals(appender.getEvents().get(0).getMessage(), msg);
        assertNull(appender.getEvents().get(0).getThrowableInformation());
    }
    
    @Test
    public void testInfoMessageFormat() throws Exception {
        logger.info("Hello {0}", "World");
        assertEquals(appender.getEvents().size(), 1);
        assertEquals(appender.getEvents().get(0).getLevel(), Level.INFO);
        assertEquals(appender.getEvents().get(0).getMessage(), "Hello World");
        assertNull(appender.getEvents().get(0).getThrowableInformation());
    }
    
    @Test
    public void testWarn() throws Exception {
        logger.warn(msg);
        assertEquals(appender.getEvents().size(), 1);
        assertEquals(appender.getEvents().get(0).getLevel(), Level.WARN);
        assertEquals(appender.getEvents().get(0).getMessage(), msg);
        assertNull(appender.getEvents().get(0).getThrowableInformation());
    }
    
    @Test
    public void testError() throws Exception {
        logger.error(msg);
        assertEquals(appender.getEvents().size(), 1);
        assertEquals(appender.getEvents().get(0).getLevel(), Level.ERROR);
        assertEquals(appender.getEvents().get(0).getMessage(), msg);
        assertNull(appender.getEvents().get(0).getThrowableInformation());
        
        appender.clearEvents();
        Throwable ex = new Throwable("exception message");
        logger.error(msg, ex);
        assertEquals(appender.getEvents().size(), 1);
        assertEquals(appender.getEvents().get(0).getLevel(), Level.ERROR);
        assertEquals(appender.getEvents().get(0).getMessage(), msg);
        assertNotNull(appender.getEvents().get(0).getThrowableInformation());
    }
    
    @Test
    public void testFatal() throws Exception {
        logger.fatal(msg);
        assertEquals(appender.getEvents().size(), 1);
        assertEquals(appender.getEvents().get(0).getLevel(), Level.FATAL);
        assertEquals(appender.getEvents().get(0).getMessage(), msg);
        assertNull(appender.getEvents().get(0).getThrowableInformation());
        
        appender.clearEvents();
        Throwable ex = new Throwable("exception message");
        logger.fatal(msg, ex);
        assertEquals(appender.getEvents().size(), 1);
        assertEquals(appender.getEvents().get(0).getLevel(), Level.FATAL);
        assertEquals(appender.getEvents().get(0).getMessage(), msg);
        assertNotNull(appender.getEvents().get(0).getThrowableInformation());
    }
    
    @Test
    public void testFileAppenderNominal()  {
    	FileAppender fa = new FileAppender();
    	fa.setFile("origFilename");
    	System.setProperty(MCTLogger.MCT_LOG_FILE, "newFilename");
    	MCTLogger.initializeAppender(fa);
    	Assert.assertTrue(! fa.getFile().equals("origFilename"));
    }    
    
    @Test
    public void testFileAppenderEmptyProperty()  {
    	FileAppender fa = new FileAppender();
    	fa.setFile("origFilename");
    	System.setProperty(MCTLogger.MCT_LOG_FILE, "");
    	MCTLogger.initializeAppender(fa);
    	Assert.assertTrue( fa.getFile().equals("origFilename"));
    }    
    
    @Test
    public void testFileAppenderUnsetProperty()  {
    	FileAppender fa = new FileAppender();
    	fa.setFile("origFilename");
    	MCTLogger.initializeAppender(fa);
    	Assert.assertTrue( fa.getFile().equals("origFilename"));
    }  
    
    @Test
    public void testFileAppenderError()  {
        System.setProperty(MCTLogger.MCT_LOG_FILE, "newFilename");
        MCTLogger.initializeAppender(null);  //should not throw Null Pointer
    } 
    
    /*
     * appender for testing written msgs
     */
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

    
}
