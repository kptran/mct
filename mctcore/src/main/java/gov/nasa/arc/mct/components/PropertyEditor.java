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
package gov.nasa.arc.mct.components;

import java.util.List;



/**
 * Provides values for inspectable fields.  Validates the values.
 * java's PropertyEditorSupport implements a similar API.
 * @param <E> collection element data type 
 */
public interface PropertyEditor<E> {
    
    /**
     * Gets the property value as text.
     * @return the property value as text
     */
    public String getAsText();
    
    /**
     * Set the property value by parsing a given String. 
     * Implementations should override this method to provide MCT component-specific operations
     * such as business model updates and field validation. 
     * If  a prospective field value is not valid, the implementation should
     * throw an exception.  
     * @param text the new text
     * @throws  IllegalArgumentException upon error, typically used for validity checking
     */
    public void setAsText(String text) throws java.lang.IllegalArgumentException;

    /**
     * Gets the property value.
     * @return the value
     */
    public Object getValue();

    /**
     * Set (or change) the object that is to be edited.
     * @param value the new value
     * @throws  IllegalArgumentException upon error, typically used for validity checking
     */
    public void setValue(Object value) throws java.lang.IllegalArgumentException;
    
    /**
     *.Get a tag list. 
     * If the property value must be one of a set of known tagged values, then this method should return an array of the tags.
     * @return  a list of values for the property
     */
    public List<E> getTags();
  
}
