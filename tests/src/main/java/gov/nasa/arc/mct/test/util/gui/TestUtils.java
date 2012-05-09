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
package gov.nasa.arc.mct.test.util.gui;

import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.Robot;
import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.launcher.ApplicationLauncher;

public class TestUtils {

	public static final int DEFAULT_DELAY_BETWEEN_EVENTS = 200;
	public static final int DEFAULT_TREE_EXPAND_DELAY = 1000;
	public static final String DEFAULT_PATH_SEPARATOR = ":";
	public static String[] STRING_ARRAY_TYPE = new String[0];
	
	private static String treePathSeparator = DEFAULT_PATH_SEPARATOR;

//	private static NoExitSecurityManagerInstaller noExitSecurityManagerInstaller;
	private static boolean started = false;
	private static Robot robot = null;

	public static synchronized void startApplication(String className, String frameTitle, int timeout) {
		if (!started) {
//			noExitSecurityManagerInstaller = NoExitSecurityManagerInstaller.installNoExitSecurityManager();
			FailOnThreadViolationRepaintManager.install();
			robot = BasicRobot.robotWithCurrentAwtHierarchy();
			robot.settings().delayBetweenEvents(DEFAULT_DELAY_BETWEEN_EVENTS);

			// Start MCT
			ApplicationLauncher.application("gov.nasa.arc.mct.platform.Startup").start();
			new Query().titleMatches(frameTitle).withTimeout(timeout).findFrame();
		}
	}

	public static synchronized void stopApplication() {
		if (started) {
//			noExitSecurityManagerInstaller.uninstall();
			robot.cleanUp();
		}
	}

	public static Robot getRobot() {
		return robot;
	}
	
	public static Robot newRobot() {
		robot = BasicRobot.robotWithCurrentAwtHierarchy();
		robot.settings().delayBetweenEvents(DEFAULT_DELAY_BETWEEN_EVENTS);
		return robot;
	}

	public static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// ignore
		}
	}

	public static String getTreePathSeparator() {
		return treePathSeparator;
	}

	public static void setTreePathSeparator(String treePathSeparator) {
		TestUtils.treePathSeparator = treePathSeparator;
	}

	public static String getDefaultPathSeparator() {
		return DEFAULT_PATH_SEPARATOR;
	}

}
