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
 * DaoStrategy.java Sep 28, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.persistence.strategy;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * Interface that provides ability to persist and de-persist components
 * @param <T> the datatype of the component associated with D
 * @param <D> the DAO datatype
 */

public interface DaoStrategy<T, D extends DaoObject> {
    
    /**
     * Loads all the children of the component associated with this DAO strategy. The persisted states of the children
     * are loaded, and then attached to this the component associated with this DOA strategy.
     */
    public void load();
    
    /**
     * Persists the state of the component associated with this DAO strategy.
     */
    public void saveObject();
    
    /**
     * Persists the state of a parent and a child. The parent is the component associated with this DAO strategy. 
     * The child's state is converted to a DAO then persisted.
     * @param childIndex the index within the children of the parent at which to add the child component
     * @param childComp the child component to be persisted.
     */
    public void saveObject(int childIndex, T childComp);
    
    /**
     * Persists the state of the parent and its children. The parent is the component associated with this DAO strategy. 
     * Children states are converted to DAOs, then persisted.
     * @param childIndex the index within the children of the parent at which to add the child components
     * @param childComps the collection of child components to be persisted
     */
    public void saveObjects(int childIndex, Collection<T> childComps);
    
    /**
     * Delete a component from persistence store.
     * @param comp the component to be deleted.
     */
    public void deleteObject(T comp);
    
    /**
     * Remove a component association of this DAO's component
     * @param mctComp the component association which will be removed. This must have a parent.
     */
    public void removeObject(T mctComp);
    
    /**
     * Remove a collection of component associations of this DAO's component
     * @param mctComps the component associations which will be removed. Each of these must have a parent.
     */
    public void removeObjects(Collection<T> mctComps);
    
    /**
     * Sets a reference to the persistence layer.
     */
    public void refreshDAO();
    
    /**
     * Not implemented
     */
    public void refreshDAO(T mctComp);
    
    /**
     * Gets the component associated with this DAO strategy.
     * @return the component associated with this DAO strategy.
     */
	public T getMCTComp();
	
	/**
	 * Loads this DAO object using the ID of the component associated with this DAO strategy.
	 * @return this DAO strategy
	 */
	public D getDaoObject();
	
	/**
	 * Loads all DAO objects in a batch.
	 * @param comps list of objects 
	 * @return a batch of DAO objects
	 */
	public Map<String, D> getDaoObjects(List<T> comps);
	
	/**
     * Loads this DAO object by session ID and the ID of the component associated with this DAO strategy.
	 * @param sessionId the session ID used to select the DAO strategy.
	 * @return this DAO strategy by session ID.
	 */
	public D getDaoObject(String sessionId);
	
	/**
	 * Associate a session id with another session id.
	 * @param sessionId the session id that one needs to link to another session id
	 * @param delegateSessionId the target session id
	 */
	public void associateDelegateSessionId(String sessionId, String delegateSessionId);
}
