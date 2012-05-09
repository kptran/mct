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
 * DisciplineTest.java Sep 28, 2008
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

public class DisciplineTest extends AbstractDaoObject {
	private String disciplineId;
	private String description;
	private String program;

	private Set<MCTUserTest> users;
	
	@Override
	public Serializable getId() {
	    return disciplineId;
	}
	
	public String getDisciplineId() {
		return disciplineId;
	}
	public void setDisciplineId(String disciplineId) {
		this.disciplineId = disciplineId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getProgram() {
        return program;
    }
    public void setProgram(String program) {
        this.program = program;
    }
    public Set<MCTUserTest> getUsers() {
		return users;
	}
	public void setUsers(Set<MCTUserTest> users) {
		this.users = users;
	}
    @Override
    public void addTag(String tagId, String tagProperty) {
        throw new UnsupportedOperationException();
    }
	
	@Override
	public boolean equals(Object obj) {
	    if (! (obj instanceof DisciplineTest)) { return false; }
	    if (this == obj) { return true; }
	    DisciplineTest disObj = (DisciplineTest)obj;
	    return disciplineId.equals(disObj.disciplineId);
	}
	
	@Override
	public int hashCode() {
	    return disciplineId.hashCode();
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
