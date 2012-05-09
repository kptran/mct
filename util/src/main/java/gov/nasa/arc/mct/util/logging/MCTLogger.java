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
 * MCTLogger.java Aug 18, 2008
 * 
 * This code is the property of the National Aeronautics and Space
 * Administration and was produced for the Mission Control Technologies (MCT)
 * project.
 * 
 */
package gov.nasa.arc.mct.util.logging;

import gov.nasa.arc.mct.util.StringUtil;

import java.text.MessageFormat;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class MCTLogger {
    private static final String FQCN = MCTLogger.class.getName();
    static final String MCT_LOG_FILE="mct.log.file";
    private static boolean initializedAppender = false;
    private Logger logger;
    
    private MCTLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * Find or create a logger for MCT subsystem. If a logger has already been
     * created with the given name it is returned. Otherwise a new logger is
     * created.
     * 
     * If a new logger is created it will be configured with MCT central logging.
     */ 
    
    public static MCTLogger getLogger(java.lang.Class<?> c) {
    		
    	Logger logger = Logger.getLogger(c);

    	if( !initializedAppender){
    		initializedAppender= true; 
    		initializeAppender((FileAppender) logger.getParent().getAppender("file"));
    	}   	
        return new MCTLogger(logger);
    }
    
    /**
     * Find or create a logger for an MCT subsystem. If a logger has already been created with a 
     * given name it is returned. Otherwise a new logger is created. 
     * @param loggerName to use for the logger
     * @return logger
     */
    public static MCTLogger getLogger(String loggerName) {
       return new MCTLogger(Logger.getLogger(loggerName));
    }
    
    /** 
     * Sets the filename using the value of a system property.
     * @param fAppender object to modify
     * 
     */
    static void initializeAppender(FileAppender fAppender) {
        if (fAppender == null) {
              return;
        }
       	String newFilename = System.getProperty(MCT_LOG_FILE);
    	if (! StringUtil.isEmpty(newFilename)) {
    		fAppender.setFile(newFilename);
    		fAppender.activateOptions(); 
    	}
    }
    
    public void info (Object message) {
        this.logger.log(FQCN, Level.INFO, message, null);
    }
    
    public void info(String message, Object...params) {
    	log(Level.INFO, message, params);
    }
    
    public void warn (Object message) {
        this.logger.log(FQCN, Level.WARN, message, null);
    }
    
    public void error (Object message) {
        this.logger.log(FQCN, Level.ERROR, message, null);
    }
    
    public void error(Object message, Throwable t) {
        this.logger.log(FQCN, Level.ERROR, message, t);
    }
    
    public void error(Throwable t, String message, Object...params) {
        this.log(Level.ERROR, t, message, params);
    }
  
    public void debug (Object message) {
        this.logger.log(FQCN, Level.DEBUG, message, null);
    }
    
    public void debug (Object message, Throwable t) {
        this.logger.log(FQCN, Level.DEBUG, message, t);
    }
    
    public void warn(Object message, Throwable t) {
    	this.logger.log(FQCN, Level.WARN, message, t);
    }
    
    public void warn(String message, Object...params) {
        log(Level.WARN, message, params);
    }
    
    public void debug(String message, Object...params) {
    	log(Level.DEBUG, message, params);
    }
    
    private void log(Level l, String message, Object...params) {
    	if (this.logger.isEnabledFor(l)) {
    		String logMessage = MessageFormat.format(message, params);
    		this.logger.log(FQCN,l,logMessage, null);
    	}
    }
    
    private void log(Level l, Throwable t, String message, Object...params) {
        if (this.logger.isEnabledFor(l)) {
            String logMessage = MessageFormat.format(message, params);
            this.logger.log(FQCN, l, logMessage, t);
        }
    }
    
    public void fatal (Object message) {
        this.logger.log(FQCN, Level.FATAL, message, null);
    }
    
    public void fatal (Object message, Throwable t) {
        this.logger.log(FQCN, Level.FATAL, message, t);
    }

}
