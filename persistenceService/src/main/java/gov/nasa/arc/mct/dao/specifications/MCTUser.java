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
 * MCTUser.java Sep 24, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.dao.specifications;

import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.persistence.strategy.AbstractDaoObject;
import gov.nasa.arc.mct.persistence.strategy.DaoObject;
import gov.nasa.arc.mct.persistmgr.PersistenceBroker;
import gov.nasa.arc.mct.platform.spi.RoleAccess;
import gov.nasa.arc.mct.services.internal.component.User;

import java.io.Serializable;
import java.util.List;

/**
 * Defines a MCT user DAO object.
 *
 */
public class MCTUser extends AbstractDaoObject implements User {
    private final PersistenceBroker syncPersistenceBroker = GlobalContext.getGlobalContext().getSynchronousPersistenceBroker();
    private int version; // For optimistic concurrency control of hibernate.
    private String userId;
    private String firstName;
    private String lastName;
    private Discipline discipline;
    
    /**
     * Gets the version number.
     * @return version - number.
     */
    public int getVersion() {
        return version;
    }
    
    @Override
    public Serializable getId() {
    	return userId;
    }

    /**
     * Gets the discipline id.
     * @return discipline id.
     */
    public String getDisciplineId() {
        return discipline.getDisciplineId();
    }
    
    /**
     * Gets the user id.
     * @return userId.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user id.
     * @param userId - sets the user id.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the first name of user.
     * @return firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of user.
     * @param firstName - first name of user.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name of user.
     * @return lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of user.
     * @param lastName - last name of user.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the user's discipline.
     * @return discipline
     */
    public Discipline getDiscipline() {
        return discipline;
    }

    /**
     * Sets the user's discipline.
     * @param discipline - the discipline.
     */
    public void setDiscipline(Discipline discipline) {
        this.discipline = discipline;
    }
    
    @Override
    public void addTag(String tagId, String tagProperty) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof MCTUser)) { return false; }
        if (this == obj) { return true; }
        MCTUser mctUserObj = (MCTUser)obj;
        return userId.equals(mctUserObj.getUserId());
    }
    
    /**
     * Gets valid MCT user based upon userid.
     * @param userID - pass in user id.
     * @return User - MCT User object (returns null if none found)
     */
    public User getValidUser(String userID)   {
    	List<MCTUser> mctUsers = syncPersistenceBroker.loadAll(userID, MCTUser.class, new String[] { "userId" }, new Object[] { userID });
    	if (!mctUsers.isEmpty()){
    		return mctUsers.iterator().next();
    	}
    	return null;
    }
    
    @Override
    public boolean hasRole(String role) {
        return RoleAccess.hasRole(this, role);
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }
    
    @Override
    public void lockDaoObject() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void unlockDaoObject() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void merge(DaoObject toObject) {
        //
    }
}
