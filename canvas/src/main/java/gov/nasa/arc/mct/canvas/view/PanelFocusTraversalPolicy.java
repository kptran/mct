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
package gov.nasa.arc.mct.canvas.view;

import gov.nasa.arc.mct.canvas.panel.Panel;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.gui.SelectionProvider;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.swing.SortingFocusTraversalPolicy;
class PanelFocusTraversalPolicy extends SortingFocusTraversalPolicy implements PropertyChangeListener {
    public static final String FOCUS_SELECTION_CHANGE_PROP = "FOCUS_SELECTION_CHANGE_PROP";
    
    private Set<Panel> visitedPanels = new LinkedHashSet<Panel>();
    private LinkedList<Panel> orderedListPanels = new LinkedList<Panel>();
    
    public PanelFocusTraversalPolicy(Map<Integer, Panel> renderedPanels) {
        addToOrderedList(renderedPanels);
    }
    
    private void addToOrderedList(Map<Integer, Panel> panelMap) {
        LinkedList<Integer> keyList = new LinkedList<Integer>(panelMap.keySet());
        Collections.sort(keyList);
        
        for (Integer key : keyList) {
            Panel panel = panelMap.get(key);
            orderedListPanels.add(panel);
        }        
    }

    @Override
    public Component getLastComponent(Container aContainer) {
        Panel panel = orderedListPanels.getLast();
        visitedPanels.add(panel);
        return panel;
    }
    
    @Override
    public Component getFirstComponent(Container aContainer) {
        Panel panel = orderedListPanels.getFirst();
        visitedPanels.add(panel);
        return panel;
    }
    
    @Override
    public Component getDefaultComponent(Container aContainer) {
        if (visitedPanels.size() == orderedListPanels.size()) {
            if (aContainer instanceof SelectionProvider) {
                ((SelectionProvider) aContainer).clearCurrentSelections();
                restartFocusTraversalCycle();
            }
            KeyboardFocusManager.getCurrentKeyboardFocusManager().upFocusCycle(aContainer);
            return KeyboardFocusManager.getCurrentKeyboardFocusManager().getCurrentFocusCycleRoot();
        }
        
        return getFirstComponent(aContainer);
    }
    
    @Override
    public Component getComponentBefore(Container aContainer, Component aComponent) {        
        if (!orderedListPanels.contains(aComponent))
            return getDefaultComponent(aContainer);

        int index = orderedListPanels.indexOf(aComponent);
        if (index == 0) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().upFocusCycle(aContainer);
            return KeyboardFocusManager.getCurrentKeyboardFocusManager().getCurrentFocusCycleRoot();
        } else {
            Panel panel = orderedListPanels.get(index - 1);
            View wrappedManifestation = panel.getWrappedManifestation();
            if (wrappedManifestation.isFocusTraversalPolicyProvider()) {
                FocusTraversalPolicy focusTraversalPolicy = wrappedManifestation.getFocusTraversalPolicy();
                return focusTraversalPolicy.getLastComponent(wrappedManifestation);
            }
            visitedPanels.add(panel);
            return panel;
        }
    }
    
    @Override
    public Component getComponentAfter(Container aContainer, Component aComponent) {        
        if (!orderedListPanels.contains(aComponent))
            return getDefaultComponent(aContainer);

        if (aComponent instanceof Panel) {
            View wrappedManifestation = ((Panel) aComponent).getWrappedManifestation();
            if (wrappedManifestation.isFocusTraversalPolicyProvider()) {           
                FocusTraversalPolicy focusTraversalPolicy = wrappedManifestation.getFocusTraversalPolicy();
                if (focusTraversalPolicy instanceof PanelFocusTraversalPolicy) {
                    if (!((PanelFocusTraversalPolicy) focusTraversalPolicy).finishedTraversalCycle()) {
                        return focusTraversalPolicy.getDefaultComponent(aContainer);
                    }
                }
            }
        }

        int lastIndex = orderedListPanels.size() - 1;
        int index = orderedListPanels.indexOf(aComponent);
        if (index == lastIndex) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().upFocusCycle(aContainer);
            return KeyboardFocusManager.getCurrentKeyboardFocusManager().getCurrentFocusCycleRoot();
        } else {
            Panel panel = orderedListPanels.get(index + 1);
            visitedPanels.add(panel);
            return panel;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        restartFocusTraversalCycle();
        orderedListPanels.clear();
        addToOrderedList((Map<Integer, Panel>) evt.getNewValue());
    }
    
    private void restartFocusTraversalCycle() {
        for (Panel panel : orderedListPanels) {
            View wrappedManifestation = panel.getWrappedManifestation();
            if (wrappedManifestation.isFocusTraversalPolicyProvider()) {
                FocusTraversalPolicy focusTraversalPolicy = wrappedManifestation.getFocusTraversalPolicy();
                if (focusTraversalPolicy instanceof PanelFocusTraversalPolicy)
                    ((PanelFocusTraversalPolicy) focusTraversalPolicy).restartFocusTraversalCycle();
            }
        }
        visitedPanels.clear();
    }
    
    private boolean finishedTraversalCycle() {
        return visitedPanels.size() == orderedListPanels.size();
    }
}
