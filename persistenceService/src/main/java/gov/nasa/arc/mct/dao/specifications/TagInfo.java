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
package gov.nasa.arc.mct.dao.specifications;

import gov.nasa.arc.mct.persistence.strategy.DaoObject;

import java.io.Serializable;

/**
 * Defines TagInfo DAO object.
 * @param <T> - extends DaoObject.
 */
public class TagInfo<T extends DaoObject> implements Serializable {
    private static final long serialVersionUID = -3826577573624655754L;
    
    private int id;
    private String tagProperty;
    private T component;
    
    /**
     * Gets the id.
     * @return id - the identifier.
     */
    public int getId() {
        return id;
    }
    
    /**
     * Sets the id.
     * @param id - the identifier.
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Gets the tag property.
     * @return tagProperty - the tag property.
     */
    public String getTagProperty() {
        return tagProperty;
    }
    
    /**
     * Sets the tag property.
     * @param tagProperty - the tag property.
     */
    public void setTagProperty(String tagProperty) {
        this.tagProperty = tagProperty;
    }
    
    /**
     * Gets the component.
     * @return component - the component.
     */
    public T getComponent() {
        return component;
    }
    
    /**
     * Sets the component.
     * @param component - the component.
     */
    public void setComponent(T component) {
        this.component = component;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof TagInfo<?>)) { return false; }
        TagInfo<?> tagInfo = (TagInfo<?>)obj;
        
        if (!this.component.getClass().equals(tagInfo.component.getClass())) { return false; }
        
        boolean returnVal = (this.component.equals(tagInfo.component));
        return returnVal;
    }
    
    @Override
    public int hashCode() {
        return component.hashCode();
    }
    
}
