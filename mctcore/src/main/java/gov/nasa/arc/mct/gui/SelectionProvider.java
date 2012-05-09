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
package gov.nasa.arc.mct.gui;

import java.beans.PropertyChangeListener;
import java.util.Collection;

/**
 * This specifies the interface for widgets that contribute to the selection model. This is a widget which can change
 * the set of selected components within one of the recognized selection areas (content area, context area, or 
 * directory area). A <code>SelectionProvider</code> determines the selected component within this
 * selection scope and broadcasts events based on the selected set of manifestations. Implementations must ensure that only
 * selection change events are published to the registered change listeners.
 * @author chris.webster@nasa.gov
 *
 */
public interface SelectionProvider {
    /**
     * Defines the property name when the set of selected manifestations changes.
     */
    public static String SELECTION_CHANGED_PROP = "selectionChanged";
    
    /**
     * Adds a listener to receive events when the set of selected manifestations
     * changes. The property name will be {@link SelectionProvider#SELECTION_CHANGED_PROP}.
     * @param listener to receive events when the selection is changed
     */
    void addSelectionChangeListener(PropertyChangeListener listener);
    
    /**
     * Removes listener.
     * @param listener to remove.
     */
    void removeSelectionChangeListener(PropertyChangeListener listener);
    
    /**
     * Returns the selected manifestations. 
     * @return currently selected manifestations, the result may be empty but not null
     */
    Collection<View> getSelectedManifestations();
    
    /**
     * Clears the set of current selections such that calling {@link #getSelectedManifestations()} will return
     * an empty collection <code>getSelectedManifestations().isEmpty() == true</code>. Selection events must not be fired during 
     * the invocation of this method. 
     */
    void clearCurrentSelections();
}
