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

import gov.nasa.arc.mct.persistence.strategy.AbstractDaoObject;
import gov.nasa.arc.mct.persistence.strategy.DaoObject;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Defines Tag DAO object.
 *
 */
public class Tag extends AbstractDaoObject {
    private int version; // For optimistic concurrency control of hibernate.
    private String tagId;
    private String tagProperty;
    private Set<TagInfo<?>> tagInfos;
    
    /**
     * Gets the version number.
     * @return version - number.
     */
    public int getVersion() {
        return version;
    }
    
    /**
     * Sets the version number.
     * @param version - number.
     */
    public void setVersion(int version) {
        this.version = version;
    }
    
    /**
     * Gets the tag id.
     * @return tagId - the tag id.
     */
    public String getTagId() {
        return tagId;
    }
    
    /**
     * Sets the tag id.
     * @param tagId - the tag id.
     */
    public void setTagId(String tagId) {
        this.tagId = tagId;
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
     * Gets the set of tag info.
     * @return tagInfos - the set of tag info.
     */
    public Set<TagInfo<?>> getTagInfos() {
        return tagInfos;
    }
    
    /**
     * Sets the set of tag info.
     * @param tagInfos - the set of tag info.
     */
    public void setTagInfos(Set<TagInfo<?>> tagInfos) {
        this.tagInfos = tagInfos;
    }
    
    /**
     * Removes the specific tag info.
     * @param tagInfo - the tag info.
     * @return boolean - flag check to remove tag info; else false.
     */
    public boolean removeTagInfo(TagInfo<?> tagInfo) {
        if (tagInfos != null) {
            return tagInfos.remove(tagInfo);
        }
        return false;
    }
    
    /**
     * Adds the specific tag info.
     * @param tagInfo - the tag info.
     */
    public void addTagInfo(TagInfo<?> tagInfo) {
        if (tagInfos == null) {
            tagInfos = new HashSet<TagInfo<?>>();
        }
//        if (!tagInfos.contains(tagInfo)) {
//            tagInfos.add(tagInfo);
//        }
        tagInfos.add(tagInfo);
    }
    
    /**
     * Add a set of tag info.
     * @param tagInfos - the set of tag info.
     */
    public void addTagInfos(Set<TagInfo<?>> tagInfos) {
        for (TagInfo<?> tagInfo: tagInfos) {
            addTagInfo(tagInfo);
        }
    }
    
    /**
     * Add tag info with property and tagged object.
     * @param <T> - inherits from DaoObject.
     * @param property - the string property name.
     * @param taggedObject - the tagged object.
     */             
    public<T extends DaoObject> void addTagInfo(String property, T taggedObject) {
        TagInfo<T> tagInfo = new TagInfo<T>();
        tagInfo.setComponent(taggedObject);
        tagInfo.setTagProperty(property);
        addTagInfo(tagInfo);
    }
    
    @Override
    public void addTag(String tagId, String tagProperty) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Checks whether the tag info. is contained in the set.
     * @param tagInfo - the tag info object to check with.
     * @return true if exist; false otherwise.
     */
    public boolean containsTagInfo(TagInfo<?> tagInfo) {
        return tagInfos.contains(tagInfo);
    }
    
    @Override
    public Serializable getId() {
        return tagId;
    }
    
    @Override
    public void lockDaoObject() {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void merge(DaoObject toObject) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void unlockDaoObject() {
        // TODO Auto-generated method stub
        
    }

}
