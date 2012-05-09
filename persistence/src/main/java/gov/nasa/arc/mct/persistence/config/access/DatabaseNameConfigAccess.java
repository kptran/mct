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
package gov.nasa.arc.mct.persistence.config.access;

import gov.nasa.arc.mct.persistence.config.DatabaseNameConfig;
import gov.nasa.arc.mct.persistence.util.HibernateUtil;

import java.util.concurrent.atomic.AtomicReference;

public class DatabaseNameConfigAccess {
    private static DatabaseNameConfig DEFAULT_ACTIVITY_CONFIG = new DatabaseNameConfig() {
        
        @Override
        public void setDatabaseNameSuffix(String activityId) {
            // cannot be changed
        }
        
        @Override
        public String getDatabaseName() {
            return HibernateUtil.getDatabaseName();
        }
    };
    
    private static AtomicReference<DatabaseNameConfig> reference =
        new AtomicReference<DatabaseNameConfig>(DEFAULT_ACTIVITY_CONFIG);

    public static DatabaseNameConfig getDatabaseNameConfig() {
        return reference.get();
    }
    
    public void setDatabaseNameConfig(DatabaseNameConfig activityConfig) {
        reference.set(activityConfig);
    }
    
    public void releaseConfig() {
        reference.set(null);
    }
}
