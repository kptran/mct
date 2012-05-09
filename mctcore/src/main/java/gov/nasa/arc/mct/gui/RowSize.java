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
package gov.nasa.arc.mct.gui;

import java.io.Serializable;

/**
 * Row size stored as a key-value pair.
 *
 */
@SuppressWarnings("serial")
public class RowSize implements Serializable {

    /*
     * Row key
     */
    private Object rowKey;
    
    /**
     * Gets the key for the row we are storing the row size for.
     * The row key is used by the caller to remember the row for
     * which we hold the size. The key is not interpreted by
     * this class.
     * 
     * @return the row key
     */
    public Object getRowKey() {
        return rowKey;
    }
    
    /**
     * Sets the key for the row we are storing the row size for.
     * 
     * @param rowKey the key to the row
     */
    public void setRowKey(Object rowKey) {
        this.rowKey = rowKey;
    }

    /*
     * Row height
     */
    private Integer height;

    /**
     * Gets the height of the row we are holding
     * the height for.
     * 
     * @return the row height
     */
    public Integer getHeight() {
        return height;
    }
    
    /**
     * Sets the row height to a new value.
     * 
     * @param height the new row height
     */
    public void setHeight(Integer height) {
        this.height = height;
    }
    
}
