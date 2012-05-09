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

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.fest.swing.core.Robot;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.FrameFixture;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseUITest {

	private Robot robot;

	@BeforeMethod
	public void baseSetup() {
		robot = TestUtils.newRobot();
	}
	
	@AfterMethod
	public void baseTeardown() {
		robot.cleanUp();
	}

	public FrameFixture showInFrame(final JPanel panel, final String name) {
		assert panel != null;
		getRobot().waitForIdle();
		
		GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				JFrame frame = new JFrame("Swing Test");
				panel.setOpaque(true); //content panes must be opaque
				frame.setContentPane(panel);
				frame.pack();
				frame.setName(name);
				frame.setVisible(true);
			}
		});
		getRobot().waitForIdle();
		
		return new Query().name(name).visible(true).findFrame();
	}

	public Robot getRobot() {
		return robot;
	}

}
