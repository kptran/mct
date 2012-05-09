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
package gov.nasa.arc.mct.evaluator.expressions;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.evaluator.component.EvaluatorComponent;
import gov.nasa.arc.mct.evaluator.component.EvaluatorData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

import javax.swing.JFrame;

import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.Robot;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.FrameFixture;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ExpressionsFormattingControlsPanelTest {
	private Robot robot;
	@Mock 
	private ExpressionsViewManifestation mockExpManifestation;
	@Mock 
	private EvaluatorComponent ec;
	@Mock 
	private EvaluatorData ed;
	private ArrayList<AbstractComponent> tList;
	@Mock
	private AbstractComponent ac;
	private ExpressionsFormattingControlsPanel controlPanel;
	private static final String TITLE = "Test Frame";
	private static final ResourceBundle bundle = ResourceBundle.getBundle("Enumerator");

	@BeforeMethod
	public void setUp(){
		MockitoAnnotations.initMocks(this);
		
		robot = BasicRobot.robotWithCurrentAwtHierarchy();
		tList = new ArrayList<AbstractComponent>();
		tList.add(ac);
		
		Mockito.when(mockExpManifestation.getExpressions()).thenReturn(new ExpressionList(""));
		Mockito.when(mockExpManifestation.getEnum()).thenReturn(ec);
		Mockito.when(ec.getData()).thenReturn(ed);
		Mockito.when(ec.getComponents()).thenReturn(Collections.<AbstractComponent>emptyList());
		
		Mockito.when(mockExpManifestation.getSelectedTelemetry()).thenReturn(ac);
		Mockito.when(mockExpManifestation.getTelemetry()).thenReturn(tList);
		
		GuiActionRunner.execute(new GuiTask(){

			@Override
			protected void executeInEDT() throws Throwable {
				controlPanel = new ExpressionsFormattingControlsPanel(mockExpManifestation);
				JFrame frame = new JFrame(TITLE);
				frame.setName(TITLE);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				controlPanel.setOpaque(true);
				frame.setContentPane(controlPanel);
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
	
	@AfterMethod
	public void tearDown() {
		robot.cleanUp();
	}
	
	@Test
	public void testPersistenceIsCalledDuringActions() {
		int persistentCount = 0;
		FrameFixture window = WindowFinder.findFrame(TITLE).using(robot);
		window.button(bundle.getString("AddExpressionTitle")).click();
		Mockito.verify(mockExpManifestation, Mockito.times(++persistentCount)).fireFocusPersist();
		
		window.button(bundle.getString("DeleteExpressionTitle")).click();
		Mockito.verify(mockExpManifestation, Mockito.times(++persistentCount)).fireFocusPersist();

		window.button(bundle.getString("AddAboveTitle")).click();
		Mockito.verify(mockExpManifestation, Mockito.times(++persistentCount)).fireFocusPersist();
		
		window.button(bundle.getString("AddBelowTitle")).click();
		Mockito.verify(mockExpManifestation, Mockito.times(++persistentCount)).fireFocusPersist();

		window.button(bundle.getString("MoveUpOneTitle")).click();
		Mockito.verify(mockExpManifestation, Mockito.times(++persistentCount)).fireFocusPersist();

		window.button(bundle.getString("MoveDownOneTitle")).click();
		Mockito.verify(mockExpManifestation, Mockito.times(++persistentCount)).fireFocusPersist();

		window.button(bundle.getString("MoveToTopTitle")).click();
		Mockito.verify(mockExpManifestation, Mockito.times(++persistentCount)).fireFocusPersist();

		window.button(bundle.getString("MoveToBottomTitle")).click();
		Mockito.verify(mockExpManifestation, Mockito.times(++persistentCount)).fireFocusPersist();

		window.button(bundle.getString("RemoveTelemetryTitle")).click();
		Mockito.verify(mockExpManifestation, Mockito.times(++persistentCount)).fireFocusPersist();
		
	}
}
