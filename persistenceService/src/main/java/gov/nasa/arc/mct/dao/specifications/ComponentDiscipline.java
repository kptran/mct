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
 * ComponentDiscipline.java Sep 24, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.dao.specifications;

import java.io.Serializable;

import gov.nasa.arc.mct.persistence.strategy.AbstractDaoObject;
import gov.nasa.arc.mct.persistence.strategy.DaoObject;

/**
 * Component discipline DAO object.
 *
 */
public class ComponentDiscipline extends AbstractDaoObject {
    private int version; // For optimistic concurrency control of hibernate.
	private Integer componentId;
	private String disciplineId;
	
	@Override
	public Serializable getId() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gets the version number.
	 * @return version - version number.
	 */
	public int getVersion() {
	    return version;
	}
	
	/**
	 * Gets the discipline id.
	 * @return disciplineId - discipline id.
	 */
	public String getDisciplineId() {
		return disciplineId;
	}
	
	/**
	 * Gets the component id.
	 * @return componentId - Integer.
	 */
	public Integer getComponentId() {
		return componentId;
	}

	/**
	 * Sets the component id.
	 * @param id - component id.
	 */             
	public void setComponentId(Integer id) {
		this.componentId = id;
	}
	
    @Override
    public void addTag(String tagId, String tagProperty) {
        throw new UnsupportedOperationException();
    }
    
    @Override
	public boolean equals(Object obj) {
	    if (! (obj instanceof ComponentDiscipline)) { return false; }
	    if (this == obj) { return true; }
	    ComponentDiscipline disObj = (ComponentDiscipline)obj;
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
	 * Sets the discipline id.
	 * @param disciplineId - the discipline id.
	 */
	public void setDisciplineId(String disciplineId) {
		this.disciplineId = disciplineId;
	}
	
	@Override
	public void merge(DaoObject toObject) {
	    //
	}
}
