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
package gov.nasa.arc.mct.util.ext.commands;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command process builder for executing external command processes.
 *
 */
public class CmdProcessBuilder {

	private static Logger logger = LoggerFactory.getLogger(CmdProcessBuilder.class);
	
	// Sets to not wait since causes the current thread to block.
	private static final boolean WAIT_PROCESS_EXIT_FLAG = false;
	private String execSuccessfulMsg = "";
	private String execFailedMsg = "";
	
	/**
	 * Default constructor.
	 */
	public CmdProcessBuilder() { }
	
	/**
	 * Executes external multiple commands.
	 * @param execPath - The execution path.
	 * @param commandList - Array list of commands
	 * @return boolean - flag whether executing multiple commands succeeded.
	 */
	public boolean execMultipleCommands(String execPath, List<String> commandList) {
		boolean execSuccess = false;
		String errorMsg = "";
		
		if (isUNIXLinux() || isMacOS()) {
		
			try { 
				String commandArgs = "";
				for (int i=0; i < commandList.size(); i++) {
				
					if (i != commandList.size()-1)
						commandArgs += commandList.get(i) + ",";
					else 
						commandArgs += commandList.get(i);
				}
			
				logger.debug("commandArgs: " + commandArgs);
			
				ProcessBuilder builder = new ProcessBuilder(commandArgs);
				// Setups default OS specific environment variables if necessary
				Map<String, String> env = builder.environment();
				env.put("EXECDIR", execPath);
			
				builder.directory(new File(env.get("EXECDIR")));
				logger.debug("Execution Path: " + builder.directory().getAbsolutePath());
			
				Runtime runTime = Runtime.getRuntime();
				Process process = runTime.exec(env.get("EXECDIR") + commandArgs);
							
				// Prints out any stderror msgs
				CmdProcessBuilderStreamHelper stdErrorStreamHelper = new CmdProcessBuilderStreamHelper(process.getErrorStream(), "ERROR");            
            				
				// Prints out any msgs to stdoutput
				CmdProcessBuilderStreamHelper stdOutputStreamHelper = new CmdProcessBuilderStreamHelper(process.getInputStream(), "OUTPUT");
							
				stdErrorStreamHelper.start();
				stdOutputStreamHelper.start();
				                
				if (stdErrorStreamHelper.getIOStreamMessages() != null) {
				    logger.info(stdErrorStreamHelper.getIOStreamMessages());
				    errorMsg = stdErrorStreamHelper.getIOStreamMessages();
				} else {
				    logger.error("STDERROR I/O Stream Helper is null: {}", errorMsg);
				}
								
				if (stdOutputStreamHelper.getIOStreamMessages() != null) {
				    logger.info(stdOutputStreamHelper.getIOStreamMessages());
				    execSuccessfulMsg = stdOutputStreamHelper.getIOStreamMessages();
				} else { 
				    logger.error("STDOUTPUT I/O Stream Helper is null: {}", execSuccessfulMsg);
				}
				
				if (WAIT_PROCESS_EXIT_FLAG) {
				    int exitValue = process.exitValue();
				
				    if (exitValue == 0) {
				        execSuccess = true;
				        logger.info("Command: " + env.get("EXECDIR") + builder.command().toString() + " executed successfully. [Process.exitValue=" + exitValue + "]");
				        execSuccessfulMsg += "Command: " + env.get("EXECDIR") + builder.command().toString() + " executed successfully. [Process.exitValue=" + exitValue + "]";
				    } else {
				        execSuccess = false;
				        logger.error("Command: " + env.get("EXECDIR") + builder.command().toString() + " failed because Process.exitValue()=" + exitValue);
				        errorMsg = "Command: " + env.get("EXECDIR") + builder.command().toString() + " failed because Process.exitValue()=" + exitValue;
				    }
				} else {
				    execSuccess = true;
                    logger.info("Command: " + env.get("EXECDIR") + builder.command().toString() + " executed successfully.");
                    execSuccessfulMsg += "Command: " + env.get("EXECDIR") + builder.command().toString() + " executed successfully.";
				}
				
			} catch(IOException ioe) {
				logger.error("IOException: ", ioe);
				errorMsg += ioe.getMessage();
			}  catch (IllegalArgumentException iae) { 
				logger.error("IllegalArgumentException: ", iae);
				errorMsg += iae.getMessage();
			}  
		
			if (errorMsg != null && !errorMsg.isEmpty())
				execFailedMsg = errorMsg;
			
		} else {
			logger.error(printOSPlatform("Windows OS"));
			execFailedMsg = printOSPlatform("Windows OS");
		}
		return execSuccess;
	}
	
	/**
	 * Gets the success message after execution attempt.
	 * @return the success message.
	 */
	public String getExecSuccessfulMsg() {
		return execSuccessfulMsg;
	}
	
	/**
	 * Gets the failed message after execution attempt.
	 * @return the failed message.
	 */
	public String getExecFailedMsg() {
		return execFailedMsg;
	}
	
	/**
	 * Tests whether the OS platform execution environment is a Windows, MacOSX, or UNIX/Linux;  
	 * because Limit Manager software utility tool (isplimit or limb) is only available in UNIX/Linux.
	 */
	
	/**
	 * Checks for WindowsOS platform.
	 * @return boolean - WindowsOS platform
	 */
	 public boolean isWindows() {
	     return checkOSPlatform().toLowerCase().contains("windows");
	 }
	 
	 /**
	  * Checks for MacOS platform.
	  * @return boolean - MacOS platform
	  */
	 public boolean isMacOS() {
	     return checkOSPlatform().toLowerCase().contains("mac");
	 }
	 
	 /**
	  * Checks for UNIX/Linux OS platform.
	  * @return boolean - UNIX/Linux OS platform
	  */
	 public boolean isUNIXLinux() {
		 return checkOSPlatform().toLowerCase().contains("nix")
		 	|| checkOSPlatform().toLowerCase().contains("nux");
	 }
	
	 private String printOSPlatform(String osPlatform) {
		 StringBuffer formatMsg = new StringBuffer();
		 formatMsg.append("Limit Manager is currently not supported on ");
		 formatMsg.append(osPlatform);
		 formatMsg.append(".");
		 
		 return formatMsg.toString();
	 }
	
	 /**
	  * Gets the target OS platform name.
	  * @return OS platform name
	  */
	 public String checkOSPlatform() {
		 return System.getProperty("os.name");
	 }
	 
	/**
	 * For unit testing purposes to set the target OS platform.
	 * @param osName - OS platform
	 */
	 public void setOSPlatform(String osName) {
		 System.setProperty("os.name", osName);
	 }
}
