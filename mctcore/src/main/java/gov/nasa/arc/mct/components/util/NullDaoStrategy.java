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
package gov.nasa.arc.mct.components.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.persistence.strategy.DaoStrategy;

/**
 * Null DAO strategy implementation.
 */
public class NullDaoStrategy implements DaoStrategy<AbstractComponent, NullDaoObject> {
   
    /**
     * Singleton instance.
     */
    private static final NullDaoStrategy INSTANCE = new NullDaoStrategy();
    
    /**
     * Private Singleton constructor.
     */
    private NullDaoStrategy() {
        
    }
    
    /**
     * Gets single instance of NullDaoStrategy.
     * @return INSTANCE of NullDaoStrategy.
     */
    public static final NullDaoStrategy getInstance() {
        return INSTANCE;
    }   

    @Override
    public void associateDelegateSessionId(String sessionId, String delegateSessionId) {
    }

    @Override
    public void deleteObject(AbstractComponent comp) {
    }

    @Override
    public NullDaoObject getDaoObject() {
        return NullDaoObject.getInstance();
    }

    @Override
    public NullDaoObject getDaoObject(String sessionId) {
        return NullDaoObject.getInstance();
    }

    @Override
    public Map<String, NullDaoObject> getDaoObjects(List<AbstractComponent> comps) {
        return Collections.emptyMap();
    }

    @Override
    public AbstractComponent getMCTComp() {
        return null;
    }

    @Override
    public void load() {
    }

    @Override
    public void refreshDAO() {
    }

    @Override
    public void refreshDAO(AbstractComponent mctComp) {
    }

    @Override
    public void removeObject(AbstractComponent mctComp) {
    }

    @Override
    public void removeObjects(Collection<AbstractComponent> mctComps) {
    }

    @Override
    public void saveObject() {
    }

    @Override
    public void saveObject(int childIndex, AbstractComponent childComp) {
    }

    @Override
    public void saveObjects(int childIndex, Collection<AbstractComponent> childComps) {
    }

}
