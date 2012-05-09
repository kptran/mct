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
package gov.nasa.arc.mct.loader;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.dao.persistence.strategy.ComponentSpecificationDaoStrategy;
import gov.nasa.arc.mct.dao.specifications.ComponentSpecification;
import gov.nasa.arc.mct.persistmgr.PersistenceBroker;
import gov.nasa.arc.mct.registry.GlobalComponentRegistry;
import gov.nasa.arc.mct.services.internal.component.ComponentInitializer;
import gov.nasa.arc.mct.services.internal.component.Updatable;
import gov.nasa.arc.mct.util.logging.MCTLogger;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This class loads the data from the database. This is only used by the
 * internal platform
 * 
 */
public class DataLoader {
    private static final MCTLogger LOGGER = MCTLogger.getLogger(DataLoader.class);
    
    private static final PersistenceBroker persistenceBroker = GlobalContext.getGlobalContext()
            .getSynchronousPersistenceBroker();

    /**
     * This method loads the data from the database.
     */
    public void loadComponents() {
        AbstractComponent rootComponent = populateTaxonomyComponents();
        GlobalComponentRegistry.ROOT_COMPONENT_ID = rootComponent.getId();
        GlobalContext.getGlobalContext().initialize();
        loadMine(rootComponent);
    }

    private AbstractComponent populateTaxonomyComponents() {
        AbstractComponent rootComponent;

        String sessionId = "0";

        persistenceBroker.startSession(sessionId);

        try {
            List<ComponentSpecification> allTaxonomies = persistenceBroker.loadAllOrderedBy(sessionId,
                    ComponentSpecification.class, "componentId", new String[] { "parentComponents", "deleted" },
                    new Object[] { Collections.EMPTY_LIST, Boolean.FALSE }, new String[] { "name" },
                    new Object[] { GlobalComponentRegistry.MINE });
            if (allTaxonomies.size() == 0) {  
            	LOGGER.info("Data Loader root taxonomy size is zero since no components have been loaded.");
            } else if (allTaxonomies.size() > 1) { 
            		LOGGER.error("Data Loader taxonomy size is greater than 1.");
                    recoverStartup(allTaxonomies);
            }

            List<AbstractComponent> mctComps = ComponentSpecificationDaoStrategy
                    .transformTo(allTaxonomies, false);
            assert mctComps.size() == 1;

            rootComponent = mctComps.get(0);
            
            List<AbstractComponent> childComps = ComponentSpecificationDaoStrategy.transformTo(allTaxonomies.get(
                    0).getAssociatedComponents(), true);
            
            rootComponent.getCapability(ComponentInitializer.class).setComponentReferences(childComps);           
        } finally {
            persistenceBroker.closeSession(sessionId);
        }

        return rootComponent;
    }
    
    private void recoverStartup(List<ComponentSpecification> allTaxonomies) {
        for (Iterator<ComponentSpecification> it=allTaxonomies.iterator(); it.hasNext(); ) {
            ComponentSpecification taxonomy = it.next();
            if (taxonomy.getName().equals(GlobalComponentRegistry.ROOT_COMPONENT_NAME)) {
                continue;
            }
            LOGGER.error("Component " + taxonomy.getComponentId().toString() + " has no parent. Database cleanup is necessary! MCT will continue to run without problem.");
            it.remove();
        }
    }

    /**
     * This method loads the data under the "Mine" node.
     */
    private void loadMine(AbstractComponent rootComponent) {
        AbstractComponent mineComponent = ComponentSpecificationDaoStrategy.loadMine();
        rootComponent.getCapability(Updatable.class).addReferences(Collections.singletonList(mineComponent));
    }
}
