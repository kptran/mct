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
package gov.nasa.arc.mct.menu;

import gov.nasa.arc.mct.canvas.panel.Panel;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.GroupAction;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.Action;
import javax.swing.SwingUtilities;

/**
 * This class implements actions allowing a component to change views in the
 * content area.
 * 
 */
@SuppressWarnings("serial")
public class ChangeViewAction extends GroupAction {
    private static final ResourceBundle BUNDLE = 
                    ResourceBundle.getBundle(
                                    ChangeViewAction.class.getName().substring(0, 
                                                    ChangeViewAction.class.getName().lastIndexOf("."))+".Bundle");
    private View windowManifestation;
    private Collection<Panel> selectedPanels = new HashSet<Panel>();

    public ChangeViewAction() {
        this("Change <option> view");
    }

    protected ChangeViewAction(String name) {
        super(name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public boolean canHandle(ActionContext context) {
        Collection<View> selectedManifestations = context.getSelectedManifestations();
        if (selectedManifestations.isEmpty())
            return false;
        
        Iterator<View> iterator = selectedManifestations.iterator();
        AbstractComponent firstComponent = iterator.next().getManifestedComponent();
        Class<? extends AbstractComponent> componentType = firstComponent.getClass();
        Set<ViewInfo> commonViewRoles = new LinkedHashSet<ViewInfo>();
        commonViewRoles.addAll(firstComponent.getViewInfos(ViewType.EMBEDDED));
        
        for (View manifestation : selectedManifestations) {
            Container container = SwingUtilities.getAncestorOfClass(Panel.class, manifestation);
            if (container == null)
                return false;
            if (componentType != manifestation.getManifestedComponent().getClass())
                return false;

            Set<ViewInfo> viewRoles = new LinkedHashSet<ViewInfo>();
            viewRoles.addAll(firstComponent.getViewInfos(ViewType.EMBEDDED));
            commonViewRoles.retainAll(viewRoles);
            selectedPanels.add((Panel) container);            
        }        

        windowManifestation = context.getWindowManifestation();
        
        List<ChangeSpecificViewAction> changeViewActions = new ArrayList<ChangeSpecificViewAction>();
        for (ViewInfo viewInfo : commonViewRoles) {
            changeViewActions.add(new ChangeSpecificViewAction(viewInfo));
        }
        setActions(changeViewActions.toArray(new RadioAction[changeViewActions.size()]));
        return true;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    private class ChangeSpecificViewAction extends GroupAction.RadioAction {
        private ViewInfo viewInfo;

        public ChangeSpecificViewAction(ViewInfo viewRole) {
            this.viewInfo = viewRole;
            putValue(Action.NAME, MessageFormat.format(BUNDLE.getString("ViewDisplayName"), this.viewInfo.getViewName()));
        }

        @Override
        public boolean isEnabled() {
            return !windowManifestation.isLocked();
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            for (Panel panel : selectedPanels) {
                panel.changeToView(viewInfo);
            }
            MenuUtil.batchSave(selectedPanels);
        }

        @Override
        public boolean isMixed() {
            return false;
        }
        
        @Override
        public boolean isSelected() {
            for (Panel panel : selectedPanels) {
                if (!viewInfo.equals(panel.getWrappedManifestation().getInfo()))
                    return false;
            }
            return true;
        }

    }
}
