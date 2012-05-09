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
/**
 * MCTInspectionArea.java Aug 18, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.gui.housing;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.gui.OptionBox;
import gov.nasa.arc.mct.gui.SelectionProvider;
import gov.nasa.arc.mct.gui.TwiddleView;
import gov.nasa.arc.mct.gui.Twistie;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.gui.ViewProvider;
import gov.nasa.arc.mct.gui.housing.MCTHousing.ControlProvider;
import gov.nasa.arc.mct.lock.manager.LockManager;
import gov.nasa.arc.mct.roles.events.ReloadEvent;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;
import gov.nasa.arc.mct.util.logging.MCTLogger;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This class generates the Inspection Area of a Housing
 *
 */
@SuppressWarnings("serial")
public class MCTInspectionArea extends View implements ViewProvider, ControlProvider, TwiddleView {
    public static final String DEFAULT_INSPECTOR_VIEW_PROP_KEY = "DefaultInspector";

    private static final ResourceBundle BUNDLE = 
        ResourceBundle.getBundle(
                MCTInspectionArea.class.getName().substring(0, 
                        MCTInspectionArea.class.getName().lastIndexOf("."))+".Bundle");
    private static final String INSPECTOR_TITLE =  BUNDLE.getString("MCTInspectionArea.inspect"); //RStrings.INSPECTOR;
    private MCTTitleArea titleArea;
    protected boolean controlAreaVisible = false;
    private JPanel controlPanel;
    private JTabbedPane tabbedPane;
    private final List<ViewInfo> currentViewRoles = new ArrayList<ViewInfo>();
    private AbstractComponent currentComponent;
    
    private final PropertyChangeListener selectionChangeListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName() == InspectionArea.SELECTION_WILL_CHANGE_PROP) {
                    releaseCurrentManifestationLocks();
                } else {
                    @SuppressWarnings("unchecked")
                    Collection<View> selectedViews =  (Collection<View>) evt.getNewValue();
                    if (selectedViews.isEmpty() || selectedViews.size() > 1)
                        return;
                    
                    selectedManifestationChanged(selectedViews.iterator().next());
                }
            }
        };


    public MCTInspectionArea(final AbstractComponent ac, ViewInfo vi) {
        setLayout(new BorderLayout());
        initializeSelectionListener();

     	// The following adds the title and control areas,
     	// where the control area is initially hidden.
        controlPanel = new JPanel(new BorderLayout());
        titleArea = new MCTTitleArea(this, INSPECTOR_TITLE, null);
        controlPanel.add(titleArea, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.NORTH);
        
        tabbedPane = new JTabbedPane(JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT);
        add(tabbedPane, BorderLayout.CENTER);
        tabbedPane.getModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Component comp = tabbedPane.getSelectedComponent();
                int selectedIndex = tabbedPane.getSelectedIndex();
                
                if (comp == null) {
                    if (selectedIndex >= 0) {
                        View manifestation = getMCTViewManifestation(currentComponent, currentViewRoles.get(selectedIndex));
                        addManifestation(manifestation,selectedIndex);
                    }
                }
                
                // hide all twisties that are not currently active
                for (int i =0; i < tabbedPane.getTabCount(); i++) {
                    Component c = tabbedPane.getTabComponentAt(i);
                    if (c instanceof JComponent) {
                        JComponent jc = JComponent.class.cast(c);
                        for (int ci=0; ci < jc.getComponentCount(); ci++) {
                            Component component = jc.getComponent(ci);
                            if (component instanceof TabTwistie) {
                                component.setVisible(i == selectedIndex);
                            }
                        }
                    }
                }
            }
            
        });

        setMinimumSize(new Dimension(0, 0));
    }
    
    private void releaseCurrentManifestationLocks() {
        LockManager lockManager = GlobalContext.getGlobalContext().getLockManager();
        View lockedManifestation = null;
        AbstractComponent component = null;
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            Component c = tabbedPane.getComponentAt(i);
            if (c instanceof InspectorPane) {
                InspectorPane ip = InspectorPane.class.cast(c);
                View viewManifestation = ip.getViewManifestation();
                AbstractComponent targetComponent = viewManifestation.getManifestedComponent();
                if (targetComponent.getMasterComponent() != null) {
                    targetComponent = targetComponent.getMasterComponent();
                }
                
                boolean isLocked = !(lockManager.isManifestationLocked(targetComponent.getId(), viewManifestation) && !lockManager.isLockedForAllUsers(targetComponent.getId()));
                if (!isLocked) {
                    lockedManifestation = viewManifestation;
                    component = targetComponent;
                }
            }
        }
        
        if (lockedManifestation == null) {
            return;
        }
       
        // Release lock for the component.
        if (!lockManager.hasPendingTransaction(component.getId())) {
            lockManager.unlock(component.getId(), lockedManifestation);
        }
        else {
            String targetComponentId = component.getId();

            Object[] options = {
                    BUNDLE.getString("MCTInspectionArea.commitOption"),
                    BUNDLE.getString("MCTInspectionArea.abortOption"),
                    };
            
            int answer = OptionBox.showOptionDialog(lockedManifestation, 
                    MessageFormat.format(BUNDLE.getString("MCTInspectionArea.lockedManifestationWarningText"), 
                            lockedManifestation.getInfo().getViewName()), 
                    BUNDLE.getString("MCTInspectionArea.lockedManifestationWarningTitle"),
                    OptionBox.YES_NO_OPTION,
                    OptionBox.WARNING_MESSAGE,
                    null,
                    options, options[0]);
            
            if (answer == OptionBox.YES_OPTION) {
                lockManager.unlock(targetComponentId, lockedManifestation);
            } else {
                lockManager.abort(targetComponentId, Collections.singleton(lockedManifestation));
            }
        }
    }

    private void initializeSelectionListener() {
        
        addAncestorListener(new AncestorListener() {
            SelectionProvider selectionProvider;
            @Override
            public void ancestorAdded(AncestorEvent event) {
                selectionProvider = (SelectionProvider) event.getAncestorParent();
                selectionProvider.addSelectionChangeListener(selectionChangeListener);
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
                
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                if (selectionProvider != null) {
                    selectionProvider.removeSelectionChangeListener(selectionChangeListener);
                }
            }
            
        });
    }
    
    MCTHousing findHousing() {
        return (MCTHousing) SwingUtilities.getAncestorOfClass(MCTHousing.class, this);
    }
    
    public void showControl(boolean flag) {
        int selected = tabbedPane.getSelectedIndex();

        if (selected != -1) {
            Component container = tabbedPane.getTabComponentAt(selected);
            if (container instanceof JComponent) {
                JComponent jc = (JComponent) container;
                for (int ci=0; ci < jc.getComponentCount(); ci++) {
                    Component comp = jc.getComponent(ci);
                    if (comp instanceof TabTwistie) {
                        ((TabTwistie)comp).changeState(flag);
                        ((TabTwistie)comp).changeStateAction(flag);
                        break;
                    }
                }
            }
        }
    }
    
    private void selectedManifestationChanged(View newView) {        
//        KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
//        Component focusOwner = focusManager.getFocusOwner();
//        boolean isFocusInThisInspectorArea  = this == SwingUtilities.getAncestorOfClass(MCTInspectionArea.class, focusOwner);
//        if (isFocusInThisInspectorArea) {
//            focusManager.clearGlobalFocusOwner();
//            SwingUtilities.invokeLater(new Runnable() {
//                public void run() {
//                    selectedManifestationChanged();
//                }
//            });
//            return;
//        }
        setComponent(newView);
    }

    /**
     * Add a view manifestation such as Info, Alpha, Plot to the Inspection area
     */
    private void addManifestation(View view, int index) {
        if (view == null) {
            return;
        }
        JPanel tabPanel = new JPanel();
        tabPanel.setOpaque(false);
        tabPanel.setLayout(new BoxLayout(tabPanel, BoxLayout.X_AXIS));
        tabPanel.add(new JLabel(view.getInfo().getViewName()));
        
        JComponent controlManifestation = view.getControlManifestation();
        InspectorPane pane = new InspectorPane(view, controlManifestation);
        tabbedPane.setComponentAt(index, pane);        
        tabbedPane.setTabComponentAt(index, tabPanel);            
        if (controlManifestation != null) {
            // Create tab component
            tabPanel.add(Box.createHorizontalStrut(5));
            TabTwistie twistie = new TabTwistie(pane);
            tabPanel.add(twistie);
            twistie.setVisible(true);
        }
    }
    
    @Override
    public void removeAll() {
        tabbedPane.removeAll();
    }
    
    @Override
    public void updateMonitoredGUI(ReloadEvent event) {
        refresh();
    }
    
    public void refresh() {
        titleArea.refreshTitle();
        tabbedPane.removeAll();
        revalidate();
    }
    
    public int getNumberOfInspectedViews() {
        return tabbedPane.getTabCount();
    }

    public void setComponent(View object) {
        //releaseCurrentManifestationLocks();
        // Save the name of the currently selected tab, if any, so we can
        // select the tab with the same name for the new component.
        String lastSelectedViewRoleName =
            (tabbedPane.getSelectedIndex() >= 0) ?
                    tabbedPane.getTitleAt(tabbedPane.getSelectedIndex())
                    : null;

        tabbedPane.removeAll();
        currentViewRoles.clear();
        AbstractComponent selected = object == null ? null : object.getManifestedComponent();
        if (selected != null) {
            Set<ViewInfo> roles = selected.getViewInfos(ViewType.OBJECT);
            if (roles != null && !roles.isEmpty()) {
                titleArea.setComponent(selected);
                currentViewRoles.addAll(roles);
                currentComponent = selected;
                for (ViewInfo role:currentViewRoles) {
                    tabbedPane.addTab(role.getViewName(), null);
                }
            } else
                titleArea.setComponent(null);
        } else
        	titleArea.setComponent(null);
        
        // Try to find a tab with the same name as last selected. If found, select it.
        // Otherwise, leave the default tab selected.
        if (lastSelectedViewRoleName != null) {
            for (int i=0; i<tabbedPane.getTabCount(); ++i) {
                if (tabbedPane.getTitleAt(i).equalsIgnoreCase(lastSelectedViewRoleName)) {
                    tabbedPane.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        revalidate();
    }

    private View getMCTViewManifestation(AbstractComponent ac, ViewInfo role) {
        JComponent comp = role.createView(ac);
        if (!(comp instanceof View)) {
            MCTLogger.getLogger(MCTInspectionArea.class).error(
                    "role was not an instance of View " +
                    role.getClass().getName()
            );
            comp = null;
        }
        
        return (View) comp;
    }
    
    @Override
    public View getHousedViewManifestation() {
        if (tabbedPane == null) {
            return null;
        }
        Component selectedComponent = tabbedPane.getSelectedComponent();
        if (selectedComponent == null)
            return null;
        return ((InspectorPane) selectedComponent).getViewManifestation();
    }

    public MCTTitleArea getTitleArea() {
        return titleArea;
    }

    private final class TabTwistie extends Twistie {

        private static final String COLLAPSED_ICON = "images/controlToggleClosedDark.png";
        private static final String EXPANDED_ICON = "images/controlToggleOpenDark.png";
        private InspectorPane pane;
        
        public TabTwistie(InspectorPane pane) {
            super(new ImageIcon(TabTwistie.class.getClassLoader().getResource(COLLAPSED_ICON)),
                    new ImageIcon(TabTwistie.class.getClassLoader().getResource(EXPANDED_ICON)));
            this.pane = pane;            
        }
        
        @Override
        protected void changeStateAction(boolean state) {
            pane.setControlVisible(state);
        }
        
    }
    
    protected static final class InspectorPane extends JPanel {
        private JComponent controlManifestation;
        private View viewManifestation;
        private JSplitPane splitPane;
        private int dividerSize;
        public InspectorPane(View viewManifestation, JComponent controlManifestation) {
            this.viewManifestation = viewManifestation;
            this.controlManifestation = controlManifestation;
            
            // Setup scroll pane.
            JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            // add status bar if needed
            List<? extends JComponent> statusWidgets = viewManifestation.getStatusWidgets();
            if (!statusWidgets.isEmpty()) {
                JPanel contentPanel = new JPanel(new BorderLayout(0, 0));
                contentPanel.add(viewManifestation, BorderLayout.CENTER);
                JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
                for (JComponent widget : statusWidgets) {
                    statusBar.add(widget);
                }
                contentPanel.add(statusBar, BorderLayout.SOUTH);               
                scrollPane.setViewportView(contentPanel);
            } else
                scrollPane.setViewportView(viewManifestation);

            // Stop scroll bars responding to arrow keys
            scrollPane.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "doNothing");
            scrollPane.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "doNothing");
            scrollPane.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("LEFT"), "doNothing");
            scrollPane.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                    .put(KeyStroke.getKeyStroke("RIGHT"), "doNothing");
            
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            if (controlManifestation != null) {
                splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, controlManifestation, scrollPane);
                splitPane.setResizeWeight(0.66); // Give 2/3 of the window size to the control manifestation.
                splitPane.setOneTouchExpandable(true);
                splitPane.setContinuousLayout(true);
                splitPane.setBorder(null);
                dividerSize = splitPane.getDividerSize();
                setControlVisible(false);
                add(splitPane);
                instrumentNames();
            } else
                add(scrollPane);
        }
        
        private void instrumentNames() {
            splitPane.setName("inspectorAreaSplitPane");
        }

        public void setControlVisible(boolean flag) {
            controlManifestation.setVisible(flag);
            splitPane.setDividerLocation(flag ? -1 : 0);
            splitPane.setDividerSize(flag ? dividerSize : 0);
            revalidate();
        }
        
        public View getViewManifestation() {
            return viewManifestation;
        }
    }

    @Override
    public void enterTwiddleMode(AbstractComponent twiddledComponent) {
        setComponent(twiddledComponent.getViewInfos(ViewType.NODE).iterator().next().createView(twiddledComponent));
    }

    @Override
    public void exitTwiddleMode(AbstractComponent originalComponent) {
        setComponent(originalComponent.getViewInfos(ViewType.NODE).iterator().next().createView(originalComponent));
    }

}
