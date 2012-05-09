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
package gov.nasa.arc.mct.core.roles;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.OptionBox;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.platform.core.access.PlatformAccess;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.policy.PolicyInfo;
import gov.nasa.arc.mct.services.component.ViewInfo;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class DropboxCanvasView extends View {

    private final JPanel innerContentPanel;
    private final JLabel statusMessage = new JLabel();

    public DropboxCanvasView(AbstractComponent ac, ViewInfo vi) {
        super(ac,vi);
        setLayout(new BorderLayout());
        innerContentPanel = new JPanel();
        innerContentPanel.setDropTarget(new CanvasViewRoleDropTarget());
        innerContentPanel.setBackground(UIManager.getColor("background"));
        innerContentPanel.setBorder(new EmptyBorder(6, 6, 6, 6));
        innerContentPanel.add(statusMessage, BorderLayout.CENTER);
        statusMessage.setFont(innerContentPanel.getFont().deriveFont(Font.BOLD));
        add(innerContentPanel);
    }

    private final class CanvasViewRoleDropTarget extends DropTarget {
        public void actionPerformed(CanvasViewDropActionEvent event) {
            final Container container = event.getContainer();
            Collection<AbstractComponent> sourceComponents = event.getSources();

            AbstractComponent targetComponent = event.getTarget();

            // This is a composing operation
            PolicyContext context = new PolicyContext();
            context.setProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName(), targetComponent);
            context.setProperty(PolicyContext.PropertyName.SOURCE_COMPONENTS.getName(), sourceComponents);
            context.setProperty(PolicyContext.PropertyName.ACTION.getName(), Character.valueOf('w'));
            context.setProperty(PolicyContext.PropertyName.VIEW_MANIFESTATION_PROVIDER.getName(), event.getTargetManifestation());
            
            final ExecutionResult result = PlatformAccess.getPlatform().getPolicyManager().execute(PolicyInfo.CategoryType.COMPOSITION_POLICY_CATEGORY.getKey(), context);
            if (result.getStatus()) {
                targetComponent.addDelegateComponents(sourceComponents);

                if (event.getTargetManifestation() instanceof DropboxCanvasView) {
                    StringBuilder sentObjects = new StringBuilder();
                    for (AbstractComponent sourceComponent : sourceComponents)
                        sentObjects.append("\"" + sourceComponent.getExtendedDisplayName() + "\" ");
                    DropboxCanvasView dropboxManifestation = (DropboxCanvasView) event.getTargetManifestation();
                    dropboxManifestation.statusMessage.setText((sourceComponents.size() == 0) ? "No objects sent." : 
                                "Accepted at " + DateFormat.getInstance().format(new Date()) +": "+ sentObjects.toString());
                    dropboxManifestation.revalidate();
                }
                // Pull tarmanifestInfoget housing window to front.
                event.getHousingWindow().toFront();
            } else {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        OptionBox.showMessageDialog(container, result.getMessage(), "Composition Error - ", OptionBox.ERROR_MESSAGE);
                    }
                });
            }
        }
        
        private Collection<AbstractComponent> getComponents(View[] views) {
            List<AbstractComponent> components = new ArrayList<AbstractComponent>();
            for (View v:views) {
            	AbstractComponent component = v.getManifestedComponent();
            	
            	// Drop box should only accept the real component
            	if (component.getMasterComponent() != null) {
            	    component = component.getMasterComponent();
            	}
            	
            	components.add(component);
            }
            
            return components;
        }
        
        @Override
        public synchronized void drop(DropTargetDropEvent dtde) {
            Transferable data = dtde.getTransferable();
            try {
                if (!data.isDataFlavorSupported(View.DATA_FLAVOR)) {
                    dtde.rejectDrop();
                    return;
                }
                View[] views = (View[]) data.getTransferData(View.DATA_FLAVOR);
                AbstractComponent manifestedComponent = getManifestedComponent();
                if (manifestedComponent != null) {
                    DropboxCanvasView.this.requestFocusInWindow();
                    CanvasViewDropActionEvent event = new CanvasViewDropActionEvent(innerContentPanel, 
                            SwingUtilities.getWindowAncestor(DropboxCanvasView.this),
                            manifestedComponent, getComponents(views), dtde.getLocation(),
                            DropboxCanvasView.this);
                    actionPerformed(event);
                }
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}