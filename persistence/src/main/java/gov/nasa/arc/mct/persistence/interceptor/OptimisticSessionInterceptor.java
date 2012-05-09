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
package gov.nasa.arc.mct.persistence.interceptor;

import gov.nasa.arc.mct.persistence.strategy.DaoObject;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

public class OptimisticSessionInterceptor extends EmptyInterceptor {
    private static final long serialVersionUID = 5005578789134932772L;
    
    private Set<Object> dirtyEntities = new HashSet<Object>();
    
    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
            String[] propertyNames, Type[] types) {
        dirtyEntities.add(entity);
        
        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }
        
    public Set<Object> getDirtyEntities() {
        return dirtyEntities;
    }

    @Override
    public void postFlush(Iterator entities) {
        for (Object entity : dirtyEntities) {
            if (entity instanceof DaoObject) {
                ((DaoObject) entity).postFlush();
            }
        }
        super.postFlush(entities);
    }
}
