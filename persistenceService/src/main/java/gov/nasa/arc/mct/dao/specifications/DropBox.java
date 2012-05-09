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
 * DropBox.java Sep 24, 2008
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
import gov.nasa.arc.mct.util.exception.MCTRuntimeException;
import gov.nasa.arc.mct.util.logging.MCTLogger;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * Dropbox DAO implementation.
 *
 */
public class DropBox extends AbstractDaoObject {
    private static final MCTLogger logger = MCTLogger.getLogger(DropBox.class);

    private static final PersistenceBroker syncPersistenceBroker = GlobalContext.getGlobalContext().getSynchronousPersistenceBroker();

    private int version; // For optimistic concurrency control of hibernate.
	private String disciplineId;
	private String description;
	private Set<ComponentSpecification> components;
	
	private final Semaphore lock = new Semaphore(1, true);
	
	/**
	 * Gets the version number.
	 * @return version - number
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
	 * @return disciplineId - the discipline id.
	 */
	public String getDisciplineId() {
		return disciplineId;
	}
	
	/**
	 * Sets the discipline id.
	 * @param disciplineId - the discipline id.
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
	 * @param description  - the description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Gets the set of component specification.
	 * @return components - sets of component specs.
	 */
	public Set<ComponentSpecification> getComponents() {
	    return this.components;
    }
	
	/**
	 * Sets the set of component specification.
	 * @param components - sets of component specs.
	 */
    public void setComponents(Set<ComponentSpecification> components) {
        this.components = components;
    }
    
    /**
     * Adds the component specification to a hash set.
     * @param component - the component spec.
     */
    public void addComponents(ComponentSpecification component) {
        if (components == null) {
            components = new HashSet<ComponentSpecification>();
        }
        components.add(component);
    }
    @Override
    public void addTag(String tagId, String tagProperty) {
        throw new UnsupportedOperationException();
    }
    
    @Override
	public boolean equals(Object obj) {
	    if (! (obj instanceof DropBox)) { return false; }
	    if (this == obj) { return true; }
	    DropBox dropBoxObj = (DropBox)obj;
	    return disciplineId.equals(dropBoxObj.disciplineId);
	}
	
	@Override
	public int hashCode() {
	    return disciplineId.hashCode();
	}

	/**
	 * Saves to persistence DB storage.
	 */
	public void save() {
        syncPersistenceBroker.save(disciplineId, this, null);
    }

	/**
	 * Locks the DAO object.
	 */
    public void lockDaoObject() {
        try {
            lock.acquire();
        } catch (InterruptedException e) {
            logger.error("Unable to acquire a semaphore during save. No save is performed.", e);
            throw new MCTRuntimeException(e);
        }
    }

    /**
     * Unlocks the DAO object.
     */
    public void unlockDaoObject() {
        lock.release();
    }
    
    @Override
    public void merge(DaoObject toObject) {
        //
    }
}
