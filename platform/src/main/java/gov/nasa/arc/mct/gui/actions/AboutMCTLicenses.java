/*******************************************************************************
 * Mission Control Technologies is Copyright 2007-2012 NASA Ames Research Center
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use 
 * this file except in compliance with the License. See the MCT Open Source 
 * Licenses file distributed with this work for additional information regarding copyright 
 * ownership. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES 
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for 
 * the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package gov.nasa.arc.mct.gui.actions;

import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.ContextAwareAction;
import gov.nasa.arc.mct.gui.dialogs.AboutLicensesDialog;
import gov.nasa.arc.mct.gui.housing.registry.UserEnvironmentRegistry;

import java.awt.event.ActionEvent;

/**
 * This class implements the About MCT Licenses dialog.
 */
@SuppressWarnings("serial")
public class AboutMCTLicenses extends ContextAwareAction {
    
    private static String TEXT = "About MCT Licenses";
    
    public AboutMCTLicenses() {
        super(TEXT);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AboutLicensesDialog dlg = new AboutLicensesDialog(UserEnvironmentRegistry.getActiveHousing().getHostedFrame());
        dlg.setVisible(true);
    }

    @Override
    public boolean canHandle(ActionContext context) {
        return true;
    }
}
