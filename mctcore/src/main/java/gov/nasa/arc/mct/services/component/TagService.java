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

import gov.nasa.arc.mct.persistence.strategy.DaoObject;

import java.util.Collection;
import java.util.Set;

/**
 * This interface provides the general tag related functionalities.
 * 
 * An instance of this class can be obtained via OSGI, either programmatically
 * or via <a href=" http://www.eclipsezone.com/eclipse/forums/t97690.html">OSGI declarative
 * service functionality</a>.
 */
public interface TagService {
    /**
     * Add a tag if the tag does not already exist.
     * @param tagId the tag id
     */
    public void addTagIfNotExist(String tagId);
    
    /**
     * Tag a DaoObject with a set of tags.
     * @param <T> type of the DaoObject
     * @param tagIds set of tags to tag with
     * @param object the DaoObject to associate with the tags
     * @param autoFlush whether autoFlush should occur after tagging
     */
    public<T extends DaoObject> void tag(Set<String> tagIds, T object, boolean autoFlush);
    
    /**
     * Flush the tags to the database.
     */
    public void flush();
    
    /**
     * Returns all tags from the database.
     * @return a collection tag strings
     */
    public Collection<String> getAllTags();
}
