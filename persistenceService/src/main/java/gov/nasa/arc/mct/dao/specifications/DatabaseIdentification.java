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
 * DatabaseIdentification.java October 21, 2009
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
 * Data access object for the database identification table.
 */
public class DatabaseIdentification extends AbstractDaoObject {
    private int version; // For optimistic concurrency control of hibernate.
	private String name;
	private String value;
	
	@Override
	public Serializable getId() {
		return null;
	}

	/**
	 * Gets the version number.
	 * @return version - number.
	 */
	public int getVersion() {
	    return version;
	}
	
	@Override
	public void lockDaoObject() {
	}

	@Override
	public void unlockDaoObject() {
	}

	/**
	 * Gets the component name.
	 * @return name - component name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the value.
	 * @return value - the value.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the component name.
	 * @param name - component name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the component value.
	 * @param value - component value.
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
    @Override
    public void merge(DaoObject toObject) {
        //
    }
    
    @Override
    public void addTag(String tagId, String tagProperty) {
        throw new UnsupportedOperationException();
    }
}
