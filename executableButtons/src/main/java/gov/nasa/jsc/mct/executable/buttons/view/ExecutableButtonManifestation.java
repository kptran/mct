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

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.roles.events.PropertyChangeEvent;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.util.StandardComboBoxColors;
import gov.nasa.arc.mct.util.ext.commands.CmdProcessBuilder;
import gov.nasa.jsc.mct.executable.buttons.ExecutableButtonComponent;
import gov.nasa.jsc.mct.executable.buttons.ExecutableButtonModel;
import gov.nasa.jsc.mct.executable.buttons.ExecutableButtonSettings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class ExecutableButtonManifestation extends View { 
		
	private static final Logger logger = LoggerFactory.getLogger(ExecutableButtonManifestation.class);
	private static final Logger ADVISORY_LOGGER = LoggerFactory.getLogger("gov.nasa.jsc.advisory.service");
	private static final ResourceBundle bundle = ResourceBundle.getBundle("ResourceBundle");
	private static final int MAX_BASE_DISPLAY_NAME_LENGTH = 30;
	private static final String ELLIPSE = "...";
	
	private ExecutableButtonModel execButtonModel;
	private JButton execButton;
	private String execCmd;
	private String baseDisplayName;
	private AbstractComponent component;
	public static final String VIEW_NAME = bundle.getString("ViewRoleName");
	private ExecutableButtonSettings execButtonSettings;
	private List<Color> colorList = new ArrayList<Color>();
	private String labelText;
	private ExecutableButtonControlPanel execButtonControlPanel;
	
	public ExecutableButtonManifestation(AbstractComponent ac, ViewInfo vi) {
		super(ac,vi);
		setBackground(UIManager.getColor("background"));
		execButtonSettings = new ExecutableButtonSettings(this);
		this.setOpaque(true);
		createExecButtons();
	}
	
	public ExecutableButtonSettings getSettings() {
		return execButtonSettings;
	}
	
	@Override
	protected JComponent initializeControlManifestation() {
		initializeExecButtonControlPanel();
		return new JScrollPane(execButtonControlPanel,
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);		
	}
	
	private void initializeExecButtonControlPanel() {
		execButtonControlPanel = new ExecutableButtonControlPanel(this);
	}
	
	public List<Color> getSavedColorList() {
		return colorList;
	}
	
	public void buildFromSettings() {
		colorList = execButtonSettings.getSavedColors();
		logger.debug("Saved settings colorList: {}", colorList);
		
		if ( (execButtonSettings.getSetting(StandardComboBoxColors.EXEC_BUTTON_LABEL_TEXT) != null)
				&& !((String)execButtonSettings.getSetting(StandardComboBoxColors.EXEC_BUTTON_LABEL_TEXT)).isEmpty()) {
			labelText = (String)execButtonSettings.getSetting(StandardComboBoxColors.EXEC_BUTTON_LABEL_TEXT);
			execButton.setText(labelText);
		} else {
			if (baseDisplayName == null) {
				execButton.setText("");
				logger.error("Base Display Name is NULL.");
			} else {
				execButton.setText(baseDisplayName);
			}
		}
		
		if (execButtonSettings.getSetting(StandardComboBoxColors.BACKGROUND_COLOR) != null) {
			execButton.setBackground((Color)execButtonSettings.getSetting(StandardComboBoxColors.BACKGROUND_COLOR));
		}
		
		if (execButtonSettings.getSetting(StandardComboBoxColors.FOREGROUND_COLOR) != null) {
			execButton.setForeground((Color)execButtonSettings.getSetting(StandardComboBoxColors.FOREGROUND_COLOR));
		}
	}
	
	private void createExecButtons() {	
		
		Rectangle bounds = this.getBounds().getBounds();
		int padding = Math.min(bounds.width, bounds.height) / 20;
		bounds.grow(-padding, -padding);
		
		component = getManifestedComponent();
		execButtonModel = ExecutableButtonComponent.class.cast(getManifestedComponent()).getModel(); 
		execCmd = execButtonModel.getData().getExecCmd();
		
		baseDisplayName = component.getDisplayName();
		
		if (execCmd != null) { 
			execCmd = execCmd.trim();
		}
		
		if (baseDisplayName != null) {
			baseDisplayName = baseDisplayName.trim();
		}
		
		String cmdLine = "Exec Cmd: " + execCmd;
		
		if (baseDisplayName.length() > MAX_BASE_DISPLAY_NAME_LENGTH) {
			baseDisplayName = baseDisplayName.substring(0, MAX_BASE_DISPLAY_NAME_LENGTH) + ELLIPSE;
		}
		
		execButton = new JButton(baseDisplayName);
		execButton.setBounds(bounds);
		execButton.setToolTipText(cmdLine);
		execButton.setActionCommand(execCmd);
		
		buildFromSettings();
		
		logger.debug("execButton.getBackground(): " + execButton.getBackground() + ", execButton.getForeground(): " + execButton.getForeground() 
				+ ", execButton.getColorModel(): " + execButton.getColorModel());
		
		execButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				buttonPressed();
			}
			
		});
		
		setLayout(new BorderLayout());
		add(execButton, BorderLayout.CENTER);
		
		if (execButtonControlPanel == null) {
			initializeExecButtonControlPanel();
		}
		
	}


	private void updateExecCmdValue() {
		execCmd = execButtonModel.getData().getExecCmd();
		String cmdLine = "Exec Cmd: " + execCmd;
		baseDisplayName = component.getDisplayName();
		execButton.setText(baseDisplayName);
		execButton.setToolTipText(cmdLine);
		execButton.setActionCommand(execCmd);
	}
	
	public void buttonPressed() {
		execCmd = execButtonModel.getData().getExecCmd();

		if (execCmd == null) {
			String msg = "Button execute command is null.";
			logger.error(msg);
			ADVISORY_LOGGER.error(msg);
		}

		if (execCmd.endsWith("&")) {
			execCmd = execCmd.replace("&", "");
		}
		
		List<String> commandList = new ArrayList<String>();
		commandList.add(execCmd);
		CmdProcessBuilder cmdProcessBuilder = new CmdProcessBuilder();

		if (cmdProcessBuilder.execMultipleCommands("", commandList)) {
			String successMsg = bundle.getString("ExecButtonSuccessMsg");
			successMsg += "Execution Message: " + cmdProcessBuilder.getExecSuccessfulMsg();
			ADVISORY_LOGGER.info(successMsg);
		} else { 
			String errorMsg = bundle.getString("ExecButtonFailedMsg");
			errorMsg += "Error Message: " + cmdProcessBuilder.getExecFailedMsg();
			ADVISORY_LOGGER.error(errorMsg);
		}

	}
	
	@Override
	public void updateMonitoredGUI() {
		updateExecCmdValue();
		buildFromSettings();
	}
	
	@Override
	public void updateMonitoredGUI(PropertyChangeEvent evt) {
		updateExecCmdValue();
		buildFromSettings();
	}
	
}
