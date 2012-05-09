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
package gov.nasa.arc.mct.components;

import gov.nasa.arc.mct.persistence.strategy.DaoObject;
import gov.nasa.arc.mct.persistence.strategy.DaoStrategy;
import gov.nasa.arc.mct.services.component.ViewInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This class provides a factory for creating instances of
 * <code>DaoStrategy</code>. This is a temporary workaround as the DaoStrategy
 * is not currently publicly available, so this functionality should not be used. 
 */
public class DaoStrategyFactory {
    private static final class DelegatingDaoStrategy implements
                    DaoStrategy<AbstractComponent, DaoObject> {
        private final AbstractComponent owningComponent;
        private final ViewInfo view;

        private DelegatingDaoStrategy(AbstractComponent owningComponent, ViewInfo view) {
            this.owningComponent = owningComponent;
            this.view = view;
        }

        @Override
        public void load() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void saveObject() {
            owningComponent.save(view);
        }

        @Override
        public void saveObject(int childIndex, AbstractComponent childComp) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void saveObjects(int childIndex, Collection<AbstractComponent> childComps) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteObject(AbstractComponent comp) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeObject(AbstractComponent mctComp) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeObjects(Collection<AbstractComponent> mctComps) {
        }

        @Override
        public void refreshDAO() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void refreshDAO(AbstractComponent mctComp) {
            throw new UnsupportedOperationException();
        }

        @Override
        public AbstractComponent getMCTComp() {
            throw new UnsupportedOperationException();
        }

        @Override
        public DaoObject getDaoObject() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<String, DaoObject> getDaoObjects(List<AbstractComponent> comps) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DaoObject getDaoObject(String sessionId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void associateDelegateSessionId(String sessionId, String delegateSessionId) {
            throw new UnsupportedOperationException();
        }
    }

    private DaoStrategyFactory() {
    }

    /**
     * Returns true if the alternative save strategy is being used on the specified component.
     * @param component to introspect the save strategy
     * @return true if the alternative save strategy is being used false otherwise
     */
    public static boolean isAlternativeSaveStrategyInUse(AbstractComponent component) {
        return component.getDaoStrategy() instanceof DelegatingDaoStrategy;
    }
    
    /**
     * Adds a strategy that delegates saving to the owningComponent.
     * @param target component to delegate saves from
     * @param owningComponent to delegate saves to
     * @param view to use for event dispatching
     */
    public static void addAlternateSaveStrategy(final AbstractComponent target, final AbstractComponent owningComponent, final ViewInfo view) {
        target.setDaoStrategy(new DelegatingDaoStrategy(owningComponent, view));
    }
}
