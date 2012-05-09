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
package gov.nasa.arc.mct.services.component;

import gov.nasa.arc.mct.components.AbstractComponent;

import java.util.Collection;
import java.util.Map;

/**
 * This interface provides the tag functionalities specifically for AbstractComponent.
 *
 */
public interface ComponentTagService extends TagService {
        
    /**
     * Obtain all components that are associated with a particular tag.
     * @param tagId the tag id
     * @return All components that are associated with the tag id.
     */
    public Collection<AbstractComponent> getTaggedComponents(String tagId);

    /**
     * Remove a tag from a collection of components.
     * @param comps the collection of components
     * @param tagId a tag
     * @return the collection of components whose tags are successfully removed
     */
    public Collection<AbstractComponent> removeTag(Collection<AbstractComponent> comps, String tagId);
    
    /**
     * Tag a tag to a collection of components.
     * @param tagId a tag
     * @param comps the collection of components
     */
    public void tag(String tagId, Collection<AbstractComponent> comps);
    
    /**
     * Determine if a component is tagged with any of the provided tag.
     * @param tagIds the collection of tags.
     * @param comp the component
     * @return if comp is tagged with any of the tag.
     */
    public boolean isTagged(Collection<String> tagIds, AbstractComponent comp);
    
    /**
     * Get the info tagged to this <code>AbstractComponent</code> 
     * by this tagId.
     * @param tagId tag id
     * @param comps the components if tagged with the tag id
     * @return tagged info as <code>Map<String, String></code>
     */
    public Map<String, String> getTaggedInfo(String tagId, Collection<AbstractComponent> comps);
    
    /**
     * Checks if <code>tagId</code> is used on existing components persisted in the database.
     * @param tagId tag ID
     * @return true there are components tagged with <code>tagId</code>; false, otherwise. 
     */
    public boolean hasComponentsTaggedBy(String tagId);
}
