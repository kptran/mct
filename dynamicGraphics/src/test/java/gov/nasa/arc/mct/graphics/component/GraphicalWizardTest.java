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
package gov.nasa.arc.mct.graphics.component;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.services.component.ComponentRegistry;
import gov.nasa.arc.mct.services.component.CreateWizardUI;

import java.awt.Component;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class GraphicalWizardTest {

	private static final String CHECKER = "src/test/resources/checker.svg";
	
	private CreateWizardUI wizard;
	
	ComponentRegistry mockRegistry;
	@Mock GraphicalComponent mockComponent;
	
	@BeforeTest
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		mockRegistry = new ComponentRegistry() {

			@SuppressWarnings("unchecked")
			@Override
			public <T extends AbstractComponent> T newInstance(
					Class<T> componentClass, AbstractComponent parent) {
				if (componentClass.isAssignableFrom(GraphicalComponent.class)) return (T) new GraphicalComponent();
				return null;
			}

			@Override
			public AbstractComponent getComponent(String id) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public AbstractComponent newCollection(
					Collection<AbstractComponent> components) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getRootComponentId() {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
		
		wizard = new GraphicalComponentWizardUI();
	}
	
	@Test
	public void testWizard() {
		JButton create = new JButton("test");
		
		JComponent ui = wizard.getUI(create);
		
		Assert.assertFalse(create.isEnabled());
		
		JTextField field = findTextField(ui);
		Assert.assertNotNull(field);
		
		field.setText(CHECKER);
		for (KeyListener l : field.getKeyListeners()) l.keyTyped(null);
		Assert.assertTrue(create.isEnabled());
		
		AbstractComponent c = wizard.createComp(mockRegistry, mockComponent);
		Assert.assertTrue(c instanceof GraphicalComponent);
	}
	
	private JTextField findTextField(JComponent comp) {
		if (comp instanceof JTextField) return (JTextField) comp;
		for (Component c : comp.getComponents()) {
			if (c instanceof JComponent) {
				JTextField f = findTextField((JComponent)c);
				if (f != null) return f;
			}
		}
		return null;
	}
}
