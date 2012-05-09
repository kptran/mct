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
package gov.nasa.jsc.mct.executable.buttons.view;

import gov.nasa.arc.mct.components.ExtendedProperties;
import gov.nasa.arc.mct.util.StandardComboBoxColors;
import gov.nasa.jsc.mct.executable.buttons.ExecutableButtonSettings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class ExecutableButtonControlPanel extends JPanel { 
    
	private static final Logger logger = LoggerFactory.getLogger(ExecutableButtonControlPanel.class);
	private static final ResourceBundle bundle = ResourceBundle.getBundle("ResourceBundle");
	
	private ExecutableButtonSettings execButtonSettings;
	private ExecutableButtonManifestation manifestation;
		
	private JLabel buttonLabel; 
	private JLabel buttonLabelColor;
	private JLabel buttonBGColor;
	
	private JTextField buttonField;
	private String buttonText;
	private JComboBox buttonColorComboBox;
	private JComboBox buttonBGColorComboBox;
		
	private Map<String, Color> colorMap;
	private Map<String, String> defaultSettingsMap;
	private JPanel mainComponent;
	private StandardComboBoxColors stdComboBoxColors;
	
	public ExecutableButtonControlPanel(ExecutableButtonManifestation manifestation) {
		this.manifestation = manifestation;
		initializeStandardComboBoxColors();
		execButtonSettings = manifestation.getSettings();
		buildView(manifestation);		
	}
	
	private void initializeStandardComboBoxColors() {
		stdComboBoxColors = new StandardComboBoxColors();
		colorMap = stdComboBoxColors.getColorMap();
		defaultSettingsMap = stdComboBoxColors.getDefaultSettingsMap();
		logger.debug("colorMap: {}", colorMap);
	}
	
	private void buildView(ExecutableButtonManifestation manifestation) {
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        mainComponent = new JPanel(new GridBagLayout());
        mainComponent.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        mainComponent.setAlignmentY(TOP_ALIGNMENT);
        mainComponent.setAlignmentX(LEFT_ALIGNMENT);
        
        if (execButtonSettings.getSetting(StandardComboBoxColors.EXEC_BUTTON_LABEL_TEXT) != null) {
        	buttonText = (String)execButtonSettings.getSetting(StandardComboBoxColors.EXEC_BUTTON_LABEL_TEXT);
        }
        
        buttonField = new JTextField(buttonText, ExecutableButtonSettings.LABEL_TEXT_SIZE); 
        buttonField.setToolTipText(buttonText);
        buttonField.setEditable(true);
       
        buttonField.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
                String currentText = buttonField.getText();
                
                if (currentText != null) {
                	currentText = currentText.trim();
                }
                
                if (!currentText.equals(buttonText)) {
                	saveSettings(StandardComboBoxColors.EXEC_BUTTON_LABEL_TEXT, currentText);
                    buttonText = currentText;
                }
            }
            
            @Override
            public void focusGained(FocusEvent e) {
            }
        });
             
        buttonField.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent keyEvt) {
				int key = keyEvt.getKeyCode();
				
				if ( (key == KeyEvent.VK_ENTER) || (key == KeyEvent.VK_TAB) ) {
					saveSettings(StandardComboBoxColors.EXEC_BUTTON_LABEL_TEXT, buttonField.getText());
				}
			}

			@Override
			public void keyReleased(KeyEvent keyEvt) {
			}

			@Override
			public void keyTyped(KeyEvent keyEvt) {
			}
        	
        });
        instrumentNamesForButtonLabel();
        
        buttonColorComboBox = makeComboBox("ButtonLabelColor", stdComboBoxColors.getSupportedColors());
        buttonBGColorComboBox = makeComboBox("ButtonBGColor", stdComboBoxColors.getSupportedColors());
        
        buttonLabel = new JLabel(bundle.getString("ButtonLabel"));
        buttonLabel.setLabelFor(buttonField);
        buttonLabelColor = new JLabel(bundle.getString("ButtonLabelColor"));
        buttonLabelColor.setLabelFor(buttonColorComboBox);
        buttonBGColor =  new JLabel(bundle.getString("ButtonBGColor"));
        buttonBGColor.setLabelFor(buttonBGColorComboBox);
        
        instrumentLabelNames();
        
        mainComponent.add(buttonLabel, stdComboBoxColors.getConstraints(1,0)); mainComponent.add(buttonField, stdComboBoxColors.getConstraints(1,1));
        mainComponent.add(buttonLabelColor, stdComboBoxColors.getConstraints(2,0)); mainComponent.add(buttonColorComboBox, stdComboBoxColors.getConstraints(2,1));
        mainComponent.add(buttonBGColor, stdComboBoxColors.getConstraints(3,0)); mainComponent.add(buttonBGColorComboBox, stdComboBoxColors.getConstraints(3,1));
        
        add(mainComponent, BorderLayout.NORTH);
	}
	
	private void saveSettings(String name, String text) {
		execButtonSettings.setByObject(name, text);
		execButtonSettings.updateManifestation();
	}
	
	private void instrumentLabelNames() {    
		buttonLabel.getAccessibleContext().setAccessibleName("buttonLabel");
	    buttonLabelColor.getAccessibleContext().setAccessibleName("buttonLabelColor");
	    buttonBGColor.getAccessibleContext().setAccessibleName("buttonBGColor");
	}
	
	public Object getSetting(String name) {
		String choice = getProps(name);
		if (colorMap.containsKey(choice)) {
			return colorMap.get(choice);
		} else { 
			return choice;
		}
	}
	
	private boolean isValidKey(String key) {
		return defaultSettingsMap.containsKey(key);
	}
    
    private String getProps(String key) {
		String value = manifestation.getViewProperties().getProperty(key, String.class);
		if (value == null) {
			if (!isValidKey(key)) return null;
			setProps(key, defaultSettingsMap.get(key));
			value = defaultSettingsMap.get(key);
		}
		return value;
	}
    
    private void setProps(String key, String value) {
		ExtendedProperties viewProperties = manifestation.getViewProperties();
		viewProperties.setProperty(key, value);
	}
	
	private JComboBox makeComboBox(String name, Collection<?> items) {
		JComboBox box = new JComboBox (items.toArray());
		box.setName(name);
		
		box.setRenderer(new ListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list,
					Object obj, int arg2, boolean arg3, boolean arg4) {		
				if (obj instanceof Color) {
					return new StandardComboBoxColors.ColorPanel((Color) obj);
				}
				return new JPanel();
			}			
		});
		
		Object selected = execButtonSettings.getSetting(name);
		if (selected != null) {
			box.setSelectedItem(selected);
		} else if (items.size() > 0) {  
			box.setSelectedIndex(0);
		}
		
		box.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				JComponent comp = (JComponent)evt.getSource();
				Object value = null;
				String name = comp.getName();
			
				if (comp instanceof JComboBox) {
					value = ((JComboBox) comp).getSelectedItem();
				}
			
				if (value != null) {				
					execButtonSettings.setByObject(name, value);	
					execButtonSettings.updateManifestation();
				}
			}
		});
		
		return box;
	}
	
	private void instrumentNamesForButtonLabel() {
    	buttonField.getAccessibleContext().setAccessibleName("buttonLabelTextField");
    }	
}
