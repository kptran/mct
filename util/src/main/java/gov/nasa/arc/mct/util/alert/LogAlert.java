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
 * A utility for logging errors that you also want to prompt about
 * 
 * Created By Blake Arnold
 */

package gov.nasa.arc.mct.util.alert;

import gov.nasa.arc.mct.util.logging.MCTLogger;
import gov.nasa.arc.mct.util.property.MCTProperties;

import java.io.IOException;

import javax.swing.JOptionPane;


public class LogAlert {

	private MCTLogger logger;
	private static MCTLogger alertLog = MCTLogger.getLogger(LogAlert.class);
	private boolean fork;
	private final static String DELIMITER = "//";


	/**
	 * enum for logging
	 * 
	 * @author Blake Arnold
	 * 
	 */
	public enum Level {

		DEBUG("Debug"), ERROR("Error"), FATAL("Warning"), WARN("Warning"), INFO(
				"Information");

		private final String name;

		private Level(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	};

	/**
	 * Initializes log alert
	 * 
	 * @param logger
	 *            the MCT logger for the class
	 * @param fork
	 *            true if you want a new JVM machine for the error, useful if
	 *            the awt thread hangs in main program
	 */
	public LogAlert(MCTLogger logger, boolean fork) {
		this.logger = logger;
		this.fork = fork;
	}

	/**
	 * Returns an log alerter
	 * 
	 * @param c
	 *            class for the alerter
	 * @param fork
	 *            true if you want a new JVM machine for the error, useful if
	 *            the awt thread hangs in main program
	 * @return LogAlert instance
	 */
	public static LogAlert getAlerter(java.lang.Class<?> c, boolean fork) {

		MCTLogger logger = MCTLogger.getLogger(c);
		return new LogAlert(logger, fork);
	}

	/**
	 * Alerts info
	 * 
	 * @param message
	 *            message to alert
	 */
	public void alertInfo(String message) {
		alert(formatTitle(0, Level.INFO, null), formatMessage(Level.INFO, message), getMessageType(Level.INFO));
	}

	/**
	 * Alerts a warning
	 * @param title Title for Alert box
	 * @param message
	 *            warning message.
	 *      
	 */
	public void alertWarn(String title, String message) {

		alertWarn(title, formatMessage(Level.WARN, message), message);
	}
	
	/**
	 * Private method to issue warning alert
	 * @param title title of alert
	 * @param message 
	 * @param log
	 */
	private void alertWarn(String title, String message, String log) {
		
		this.logger.warn(log);

		alert(title, message, getMessageType(Level.ERROR));
	}

	/**
	 * Alerts a debug
	 * 
	 * @param message
	 *            debug message
	 * @param t
	 *            throwable error message, for logging only
	 */
	public void alertDebug(String message, Throwable t) {

		if (t != null)
			this.logger.debug(message, t);
		else
			this.logger.debug(message);

		alert(formatTitle(0, Level.DEBUG, null), formatMessage(Level.DEBUG, message), getMessageType(Level.DEBUG));
	}

	/**
	 * Error message
	 * @param title title of message
	 * @param message
	 *            Error message
	 * @param t
	 *            throwable to log, can be null
	 */
	public void alertError(String title, String message, Throwable t) {

		alertError(title, formatMessage(Level.ERROR, message), message, t);
	}
	
	private void alertError(String title, String message, String log, Throwable t) {
		
		if (t != null)
			this.logger.error(log, t);
		else
			this.logger.error(log);

		alert(title, message, getMessageType(Level.ERROR));
	}

	/**
	 * Fatal Error message
	 * @param title title of alert
	 * @param message
	 *            Fatal message
	 * @param t
	 *            throwable to log
	 * @param exit
	 *            set true if main application needs to be killed
	 */
	public void alertFatal(String title, String message, Throwable t, boolean exit) {

		alertFatal(title, formatMessage(Level.FATAL, message), message, t, exit);

	}
	
	private void alertFatal(String title, String message, String log, Throwable t, boolean exit)
	{
		if (t != null)
			this.logger.fatal(log, t);
		else
			this.logger.fatal(log);

		alert(title, message, getMessageType(Level.FATAL));

		if (exit)
			System.exit(1);
	}
	


	/**
	 * Creates the alert box for the message
	 * 
	 * @param l
	 *            level of message
	 * @param message
	 *            message to alert
	 */
	private void alert(String title, String message, int messageType) {
		
		if (fork) {
			String java = javaPath();
			try {
				Runtime.getRuntime().exec(
						new String[] { java, "-cp",
								System.getProperty("java.class.path"),
								"gov.nasa.arc.mct.util.alert.AlertAppl",
								DELIMITER, title, DELIMITER, message, DELIMITER, Integer.toString(messageType)});
			} catch (IOException e) {
				alertLog.error("Could not run AlertAppl using java path: "
						+ java);

			}
		} else {
			AlertBox.logBox(title, message, messageType);
		}

	}

	/**
	 * determines path to java runtime, changes based on object sharing
	 * 
	 * @return java path
	 */
	public String javaPath() {

		final MCTProperties mctProperties = MCTProperties.DEFAULT_MCT_PROPERTIES;

		String java = mctProperties.getProperty("mct.java.home");

		boolean objectShared = mctProperties.getProperty(
				"mct.objectsharing.enabled","false").equals("true") ? true : false;
		String separator = System.getProperty("file.separator");

		if (!objectShared) {
			java = System.getProperty("java.home");
		}

		// removes trailing quotation
		if (java.charAt(java.length() - 1) == '\"') {
			java = java.substring(0, java.length() - 1);
		}

		// checking for and adding trailing separator in JAVA_HOME
		if (java.charAt(java.length() - 1) != separator.charAt(0)) {
			java += separator;
		}

		java += "bin" + separator + "java";

		return java;
	}
	/**
	 * Formats mesage for use when given message
	 * @param l level of message
	 * @param m message
	 * @return
	 */
	private static String formatMessage(Level l, String m)
	{

		String message = "<html>";
		switch (l) {
		case DEBUG:
			message += "<B>Debugging with message:</B><br>";
			break;
		case ERROR:
			message += "<B>An Error has occured with message:</B><br>";
			break;
		case FATAL:
			message += "<B>A Fatal Error has occured with message:</B><br>";
			break;
		case WARN:
			message += "<B>Warning</B><br>";
			break;
		case INFO:
			message += "<B>An error has occured with message:</B><br>";
			break;
		default:
			message += "<B>MCT " + l.toString() + " with message:</B><br>";
			break;
		}
		message += " " + m + "<br>" + "<br>"
				+ "See MCT log for more information." + "</html>";
		return message;
	}
	
	/**
	 * formats title with error number
	 * @param num number of error
	 * @param l level of error
	 * @param title title of error
	 * @return
	 */
	private String formatTitle(int num, Level l, String title)
	{
		String t ="";
		switch (l) {
		case DEBUG:
			t += "Debugging" + " - ";
			break;
		case ERROR:
			t += "Error #" + num + " - ";
			break;
		case FATAL:
			t += "Error #" + num + " - ";
			break;
		case WARN:
			t += "Warning #" + num + " - ";
			break;
		case INFO:
			t += "Information #" + num + " - ";
			break;
		default:
			t += "Alert #" + num + " - ";
			break;
		}
		
		t += title;
		
		return t;
	}
	
	/**
	 * Returns integer of message type to use with JOptionPane
	 * @param l level
	 * @return message type
	 */
	private int getMessageType(Level l)
	{
		int format = JOptionPane.PLAIN_MESSAGE;

		switch (l) {
		case DEBUG:
			format = JOptionPane.INFORMATION_MESSAGE;
			break;
		case ERROR:
			format = JOptionPane.ERROR_MESSAGE;
			break;
		case FATAL:
			format = JOptionPane.ERROR_MESSAGE;
			break;
		case WARN:
			format = JOptionPane.WARNING_MESSAGE;
			break;
		case INFO:
			format = JOptionPane.INFORMATION_MESSAGE;
			break;
		default:
			format = JOptionPane.PLAIN_MESSAGE;
			break;
		}
		
		return format;
	}

}
