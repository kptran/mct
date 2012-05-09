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
package gov.nasa.jsc.mct.executables.buttons.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.ContextAwareAction;
import gov.nasa.arc.mct.gui.OptionBox;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.util.ext.commands.CmdProcessBuilder;
import gov.nasa.jsc.mct.executable.buttons.ExecutableButtonComponent;
import gov.nasa.jsc.mct.executable.buttons.ExecutableButtonModel;

@SuppressWarnings("serial")
public class ExecutableButtonAction extends ContextAwareAction {

	private static final Logger logger = LoggerFactory.getLogger(ExecutableButtonAction.class);
	private static final Logger ADVISORY_LOGGER = LoggerFactory.getLogger("gov.nasa.jsc.advisory.service");
	private static final String WINDOWS_NOT_SUPPORTED_MSG = "Windows OS platform is currently not supported yet for Executable Buttons feature.";
	private static final ResourceBundle bundle = ResourceBundle.getBundle("ResourceBundle");
	private ActionContext currentContext;
	private View manifestation;
	private String execCmd;

	public ExecutableButtonAction() {
		super(bundle.getString("ExecButtonActionLabel"));
	}


	@Override
	public boolean canHandle(ActionContext context) {
		currentContext = context;
		
		if ( (manifestation == null) && currentContext.getSelectedManifestations().iterator().hasNext()) {
			manifestation = currentContext.getSelectedManifestations().iterator().next();
		}
		
		return (context.getSelectedManifestations().size() == 1) 
			 	&& isExecutableButtonComponent(context.getSelectedManifestations().iterator().next().getManifestedComponent());
	}

	@Override
	public boolean isEnabled() {
		
		manifestation = currentContext.getSelectedManifestations().iterator().next();
		
		if (isWindows()) {
			logger.error(WINDOWS_NOT_SUPPORTED_MSG);
			OptionBox.showMessageDialog(manifestation, WINDOWS_NOT_SUPPORTED_MSG, bundle.getString("ExecButtonFailedMsg"), JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		// Executable Buttons enabled only for MacOS X and UNIX/Linux OS platforms for now.
		return (isExecutableButtonComponent(manifestation.getManifestedComponent())
				&& (isMac() || isUnixLinux()));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if ( (manifestation == null) && currentContext.getSelectedManifestations().iterator().hasNext()) {
			manifestation = currentContext.getSelectedManifestations().iterator().next();
		}
		
		ExecutableButtonModel execButtonModel = ExecutableButtonComponent.class.cast(manifestation.getManifestedComponent()).getModel();
		
		execCmd = execButtonModel.getData().getExecCmd();

		if (execCmd == null) {
			String msg = "Button execute command is null.";
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
			successMsg += "\n\nExecution Message:\n" + cmdProcessBuilder.getExecSuccessfulMsg();
			ADVISORY_LOGGER.info(successMsg);
		} else { 
			String errorMsg = bundle.getString("ExecButtonFailedMsg");
			errorMsg += "\n\nError Message:\n" + cmdProcessBuilder.getExecFailedMsg();
			ADVISORY_LOGGER.error(errorMsg);
		}
	}

	// Windows OS platform
    protected static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf("win") >= 0);
    }
 
    // MacOS platform
    protected static boolean isMac() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf("mac") >= 0); 
    }
 
    // UNIX/Linux platform
    protected static boolean isUnixLinux() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);
    }
	
    private boolean isExecutableButtonComponent(AbstractComponent component) {
		return component.getComponentTypeID().equals(ExecutableButtonComponent.class.getName());
	}
    
}
