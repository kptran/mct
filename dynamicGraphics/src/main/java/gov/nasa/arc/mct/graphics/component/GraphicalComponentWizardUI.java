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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.net.URI;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;


public class GraphicalComponentWizardUI extends CreateWizardUI {
    private static ResourceBundle bundle = ResourceBundle.getBundle("GraphicsResourceBundle");
	
	private String componentName;
	private URI    graphicURI;
	
	private FileFilter filter = new GraphicalFileFilter();
	
	private boolean updateFromFile(File f, JButton create) {
		boolean valid = f.exists() && filter.accept(f);
		
		if (valid) {
			componentName = f.getName();
			graphicURI    = f.toURI();
		}
		
		create.setEnabled(valid);
		return valid;
	}
	
	@Override
	public JComponent getUI(final JButton create) {
		
		create.setEnabled(false);

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		JLabel label = new JLabel();
		label.setText(bundle.getString("Filename_Label"));
		
		final JTextField fileField = new JTextField();
		fileField.setText("");
		fileField.setColumns(60);
		
		fileField.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {	}

			@Override
			public void keyReleased(KeyEvent arg0) {
				File f = new File(fileField.getText());
				updateFromFile(f, create);			
			}

			@Override
			public void keyTyped(KeyEvent arg0) {				
				File f = new File(fileField.getText());
				updateFromFile(f, create);
			}
			
		});

		final JButton button = new JButton();
		button.setText(bundle.getString("Chooser_Button"));
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.addChoosableFileFilter(filter);
				
				if (chooser.showOpenDialog(button) == JFileChooser.APPROVE_OPTION) {
					updateFromFile(chooser.getSelectedFile(), create);
					fileField.setText(chooser.getSelectedFile().getAbsolutePath());				
				}
				
			}
			
		});
		
		create.addFocusListener( new FocusListener() {

			@Override
			public void focusGained(FocusEvent arg0) {
				File f = new File(fileField.getText());
				updateFromFile(f, create);
			}

			@Override
			public void focusLost(FocusEvent arg0) {
			}
			
		});

		JLabel warning = new JLabel(bundle.getString("Wizard_Warning"));
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.01;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(15, 12, 12, 0);
		panel.add(label, c);
		
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.99;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(15, 5, 12, 5);
		panel.add(fileField, c);
		
		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 0.01;
		c.insets = new Insets(12, 0, 12, 11);
		panel.add(button, c);
		
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.insets = new Insets(0, 5, 17, 0);
		panel.add(warning, c);		
		
		return panel;
	}
	
	

	@Override
	public AbstractComponent createComp(ComponentRegistry registry,
			AbstractComponent parentComp) {
		
		GraphicalComponent graphicalComponent = registry.newInstance(GraphicalComponent.class, parentComp);

		graphicalComponent.setDisplayName(componentName);
		GraphicalModel model = graphicalComponent.getModelRole();
		model.setGraphicURI(graphicURI.toString());		
		
		graphicalComponent.save();
		if (parentComp.isShared()) {
			graphicalComponent.share();  
		}
		
        return graphicalComponent;
        
	}

	private class GraphicalFileFilter extends FileFilter {	
	
		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) return true;
			boolean valid = false;
			for (String suffix : ImageIO.getReaderFileSuffixes()) {
				valid |= f.getName().toLowerCase().endsWith("." + suffix.toLowerCase());
			}
			valid |= f.getName().toLowerCase().endsWith(".svg"); // We also support SVG
			return valid;
		}
	
		@Override
		public String getDescription() {
			return bundle.getString("Wizard_Filter_Description");
			
		}
		
	}

}