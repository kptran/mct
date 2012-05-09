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
package gov.nasa.arc.mct.gui.housing;

import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.gui.SelectionProvider;
import gov.nasa.arc.mct.util.logging.MCTLogger;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This class manages selection based on a set of disjoint containers, each having their own notion of 
 * selection. The selection manager returns the last active set of selected manifestations based on the set of managed <code>SelectionProvider</code> 
 * instances. This is determined based on the events fired. 
 * This class is not thread safe and is expected to be used from the AWT Event Thread.
 * @author chris.webster@nasa.gov
 */
public class SelectionManager implements SelectionProvider, PropertyChangeListener {
    private final PropertyChangeSupport pcs;
    private final List<SelectionProvider> containers;
    private Collection<View> activeSelection = Collections.emptyList();
    
    private static final MCTLogger LOGGER = MCTLogger.getLogger(SelectionManager.class);
    
    /**
     * Creates a new SelectionManager instance.
     * @param eventSource to use for <code>PropertyChangeEvent</code>'s
     */
    public SelectionManager(Object eventSource) {
        pcs = new PropertyChangeSupport(eventSource);
        containers = new ArrayList<SelectionProvider>();
    }
    
    /**
     * Add component to the list of managed components 
     * @param c  component to listen for focus changes on, the components should also implement <code>SelectionProvider</code>
     */
    public void manageComponent(Component c) {
        if (!(c instanceof SelectionProvider)) {
            LOGGER.warn("class " + c + "not instance of SelectionProvider. Focus will be ignored");
        } else {
            SelectionProvider provider = (SelectionProvider) c;
            containers.add(provider);
            provider.addSelectionChangeListener(this);
        }
    }
    
    @Override
    public void addSelectionChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(SelectionProvider.SELECTION_CHANGED_PROP, listener);
    }

    @Override
    public Collection<View> getSelectedManifestations() {
        return activeSelection;
    }

    @Override
    public void removeSelectionChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(SelectionProvider.SELECTION_CHANGED_PROP, listener);
    }
    
    @Override
    public void clearCurrentSelections() {
        clearSelections(null);
    }
    
    private void clearSelections(Object skipProvider) {
        for (SelectionProvider provider:containers) {
            if (skipProvider != provider) {
                provider.removeSelectionChangeListener(this);
                provider.clearCurrentSelections();
                provider.addSelectionChangeListener(this);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Collection<View> oldValue = activeSelection;
        @SuppressWarnings("unchecked")
        Collection<View> newValue = (Collection<View>) evt.getNewValue();
        activeSelection = newValue;
        assert evt.getNewValue() == activeSelection;
        clearSelections(evt.getSource());
        pcs.firePropertyChange(SelectionProvider.SELECTION_CHANGED_PROP, oldValue, activeSelection);
    }

}
