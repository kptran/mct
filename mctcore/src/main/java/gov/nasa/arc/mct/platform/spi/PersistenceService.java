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
package gov.nasa.arc.mct.platform.spi;

import gov.nasa.arc.mct.components.AbstractComponent;

import java.util.Collection;
import java.util.Set;

public interface PersistenceService {
    
    /**
     * Gets all users
     * @return a collection of User IDs
     */
	public Collection<String> getAllDisciplines();
	
	 /**
     * Gets all users.
     * @return collection of users.
     */
    public Set<String> getAllUsers();
    
	/**
	 * Gets all users in a discipline
	 * @param disciplineId select by discipline ID
	 * @return a collection of User IDs
	 */
	public Collection<String> getAllUsersOfDiscipline(String disciplineId);
	
	/**
	 * Load a component from the database and convert it to MCT component model.
	 * @param componentId component id
	 * @return
	 */
	public AbstractComponent loadComponent(String componentId);

    /**
     * Assign a ComponentSpecificationDaoStrategy to a component.
     * @param mctComp the target component.
     */
    public void setComponentDaoStrategy(AbstractComponent mctComp);
    
    /**
     * Returns the telemetry transformed to an MCT component.
     * @param pui
     * @return telemetry as an MCT component
     */
    public AbstractComponent findPUI(String pui);
    
    /**
     * Gets the references to this component. Currently, this is only based on relationships.
     * @param component to find references to
     * @return Collection that can be empty but never null of components referencing this component.
     */
    public Collection<AbstractComponent> getReferences(AbstractComponent component);
    
    /**
     * Adds a new user to MCT
     * @param userId user ID
     * @param group group name
     * @throws DuplicateUserException
     * @throws InterruptedException
     */
    public void addNewUser(String userId, String group) throws DuplicateUserException, InterruptedException;

    /**
     * Associate <code>session</code> to <code>target</code>.
     * @param session session ID
     * @param target session ID to be mapped to
     */
    public void associateSessions(String session, String target);

    /**
     * Disassociate this session from the mapped session.
     * @param session
     */
    public void disassociateSession(String session);
    
    /**
     * Returns the components where display names match <code>name</code>
     * @param session
     * @param name
     * @return component
     */
    public Collection<AbstractComponent> findComponentByName(String session, String name);
    
    /**
     * Polls component changes from database and updates those changes to the 
     * corresponding components. Changes include: component properties, view
     * properties, models, and model data.
     */
    public void updateComponentsFromDatabase();
    
    /**
     * Update the given component from the database if necessary. 
     * 
     * @see <#updateComponentsFromDatabase>
     */
    public void updateComponentFromDatabase(AbstractComponent component);


}
