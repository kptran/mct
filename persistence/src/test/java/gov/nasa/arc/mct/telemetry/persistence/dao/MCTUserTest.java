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
 * MCTUserTest.java Sep 28, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.telemetry.persistence.dao;

import gov.nasa.arc.mct.persistence.strategy.AbstractDaoObject;
import gov.nasa.arc.mct.persistence.strategy.DaoObject;

import java.io.Serializable;
import java.util.Set;

public class MCTUserTest extends AbstractDaoObject {
    private String userId;
    private String firstName;
    private String lastName;
    private DisciplineTest discipline;
    private Set<TelemetryComponentTest> components;
    
    @Override
    public Serializable getId() {
        return userId;
    }

    public Set<TelemetryComponentTest> getComponents() {
        return components;
    }

    public void setComponents(Set<TelemetryComponentTest> components) {
        this.components = components;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public DisciplineTest getDiscipline() {
        return discipline;
    }

    public void setDiscipline(DisciplineTest discipline) {
        this.discipline = discipline;
    }
    @Override
    public void addTag(String tagId, String tagProperty) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof MCTUserTest)) { return false; }
        if (this == obj) { return true; }
        MCTUserTest mctUserObj = (MCTUserTest)obj;
        return userId.equals(mctUserObj.userId);
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
