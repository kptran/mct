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
 * UserEnvironmentRegistry.java Aug 18, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.gui.housing.registry;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.gui.housing.MCTAbstractHousing;
import gov.nasa.arc.mct.gui.housing.MCTHousing;
import gov.nasa.arc.mct.util.exception.MCTRuntimeException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserEnvironmentRegistry {

    private static final UserEnvironmentRegistry instance = new UserEnvironmentRegistry();
    private final Map<String, List<MCTAbstractHousing>> housingRegistry = new LinkedHashMap<String, List<MCTAbstractHousing>>();
    
    /**
     * @return a collection of all MCT housing windows that are showing on screen.
     */
    public static Collection<MCTAbstractHousing> getAllHousings() {
        return instance._getAllHousings();
    }

    /**
     * @param id component id
     * @return a list of MCT housing windows of this component
     */
    public static List<MCTAbstractHousing> getHousingsByComponetId(String id) {
        return instance._getHousingsByComponentId(id);
    }
    
    /**
     * @return the number of existing MCT housing windows
     */
    public static int getHousingCount() {
        int count = getAllHousings().size();
        return count;
    }

    /**
     * @return the active MCT housing window
     */
    public static MCTHousing getActiveHousing() {
        return instance._getActiveHousing();
    }

    /**
     * @param id component id
     * @return the active MCT housing window of this component
     */
    public static MCTHousing getActiveHousingByComponentId(String id) {
        return instance._getActiveHousingByComponentId(id);
    }
    
    /**
     * @param housing to be removed from registry
     */
    public static void removeHousing(MCTAbstractHousing housing) {
        instance._removeHousing(housing);
    }

    /**
     * @param housing to be registered
     */
    public static void registerHousing(final MCTAbstractHousing housing) {
        instance._registerHousing(housing);
    }

    /**
     * @param clear housing registry
     */
    static void clearRegistry() {
        instance._clearRegistry();
    }

    // Private utility methods.
    
    private MCTHousing _getActiveHousingByComponentId(String id) {
        List<MCTAbstractHousing> housings = housingRegistry.get(id);
        if (housings == null || housings.isEmpty())
            return null;
        
        for (MCTAbstractHousing housing : housings) {
            if (housing.isActive())
                return housing;
        }
        
        return null;
    }

    private void _registerHousing(MCTAbstractHousing housing) {
        String id = housing.getRootComponent().getId();
        List<MCTAbstractHousing> housings = housingRegistry.get(id);
        if (housings == null) {
            housings = new ArrayList<MCTAbstractHousing>();
            housings.add(housing);
            housingRegistry.put(id, housings);
        }
        else {
            housings.add(housing);
        }
    }

    private void _removeHousing(MCTAbstractHousing housing) {
        AbstractComponent rootComponent = housing.getRootComponent();
        String id = rootComponent.isTwiddledComponent() ? rootComponent.getMasterComponent().getId() : rootComponent.getId();
        List<MCTAbstractHousing> housings = housingRegistry.get(id);
        if (housings == null)
            throw new MCTRuntimeException("Inconsistent housing registry state: deleting an unregistered housing for component: {id: " + id + ", component name: " + rootComponent.getDisplayName() +"} by user " + GlobalContext.getGlobalContext().getUser().getUserId() + " in thread " + Thread.currentThread().getName() + ".");
        
        housings.remove(housing);
        if (housings.isEmpty())
            housingRegistry.remove(id);
    }

    private MCTHousing _getActiveHousing() {
        for (String id : housingRegistry.keySet()) {
            List<MCTAbstractHousing> housings = housingRegistry.get(id);
            for (MCTAbstractHousing housing : housings) {
                if (housing.isActive())
                    return housing;
            }
        }
        return null;
    }

    private Collection<MCTAbstractHousing> _getAllHousings() {
        Collection<MCTAbstractHousing> collection = new ArrayList<MCTAbstractHousing>();
        for (String id : housingRegistry.keySet())
            collection.addAll(housingRegistry.get(id));
        return collection;
    }

    private List<MCTAbstractHousing> _getHousingsByComponentId(String id) {
        return housingRegistry.get(id);
    }
    
    private void _clearRegistry() {
        housingRegistry.clear();
    }
}
