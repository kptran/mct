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
 * Discipline.java Sep 24, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.dao.specifications;

import gov.nasa.arc.mct.persistence.strategy.AbstractDaoObject;
import gov.nasa.arc.mct.persistence.strategy.DaoObject;

import java.io.Serializable;
import java.util.Set;

/**
 * Discipline DAO implementation.
 *
 */
public class Discipline extends AbstractDaoObject {
    private int version; // For optimistic concurrency control of hibernate.
	private String disciplineId;
	private String description;
	private String program;
	private Set<ComponentSpecification> disciplineDropBoxes;

	private Set<MCTUser> users;
	
	/**
	 * Gets the version number.
	 * @return version - number.
	 */
	public int getVersion() {
	    return version;
	}
	
	@Override
	public Serializable getId() {
		return disciplineId;
	}
	
	/**
	 * Gets the discipline id.
	 * @return disciplineId - discipline id.
	 */
	public String getDisciplineId() {
		return disciplineId;
	}
	
	/**
	 * Sets the discipline id.
	 * @param disciplineId - discipline id.
	 */
	public void setDisciplineId(String disciplineId) {
		this.disciplineId = disciplineId;
	}
	
	/**
	 * Gets the description.
	 * @return description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Sets the description.
	 * @param description - the description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Gets the MCT users.
	 * @return users - set of MCT users.
	 */
	public Set<MCTUser> getUsers() {
	    return users;
    }
	
	/**
	 * Set the MCT users.
	 * @param users - set of MCT users.
	 */
    public void setUsers(Set<MCTUser> users) {
        this.users = users;
    }
    
    /**
     * Gets the discipline drop boxes.
     * @return disciplineDropBoxes - set of component specs.
     */
    public Set<ComponentSpecification> getDisciplineDropBoxes() {
		return disciplineDropBoxes;
	}

    /**
     * Sets the discipline drop boxes.
     * @param disciplineDropBoxes - set of discipline drop boxes.
     */
	public void setDisciplineDropBoxes(
			Set<ComponentSpecification> disciplineDropBoxes) {
		this.disciplineDropBoxes = disciplineDropBoxes;
	}
	
    @Override
    public void addTag(String tagId, String tagProperty) {
        throw new UnsupportedOperationException();
    }
    
	@Override
	public boolean equals(Object obj) {
	    if (! (obj instanceof Discipline)) { return false; }
	    if (this == obj) { return true; }
	    Discipline disObj = (Discipline)obj;
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
	    // 
	}

	/**
	 * Gets the program.
	 * @return program 
	 */
	public String getProgram() {
		return program;
	}

	/**
	 * Sets the program.
	 * @param program - the program name.
	 */
	public void setProgram(String program) {
		this.program = program;
	}
    @Override
    public void merge(DaoObject toObject) {
        //
    }

}
