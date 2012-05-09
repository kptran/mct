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
 * CompatibilityChecker.java October 21, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */

package gov.nasa.arc.mct.platform;

import gov.nasa.arc.mct.dao.specifications.DatabaseIdentification;
import gov.nasa.arc.mct.util.exception.MCTException;
import gov.nasa.arc.mct.util.logging.MCTLogger;

import java.util.List;

public class CompatibilityChecker {
    
    private static MCTLogger logger = MCTLogger.getLogger(CompatibilityChecker.class);
    private String schemaID;
    private String storedProceduresID;
    
    public CompatibilityChecker(String schemaID, String storedProceduresID) {
        super();
        this.schemaID = schemaID;
        this.storedProceduresID = storedProceduresID;
    }

    /** 
     * Ensures the schema ID of the runtime database is compatible with the schema ID for MCT application.
     * Ensures that terracotta cache is valid for a single database instance ID.
     * 
     * @param dbidList list of all database identification rows
     * @throws MCTException if the compatibility check fails
     */
    protected void checkDatabaseCompatibility(List<DatabaseIdentification> dbidList) throws MCTException {
      
        String runtimeSchemaID = getDatabaseIdentificationValue("schema_id", dbidList);
       // String runtimeStoredProceduresID = getDatabaseIdentificationValue("stored_procedures_id", dbidList);
        String databaseInstanceID = getDatabaseIdentificationValue("creation_timestamp", dbidList);
        logger.debug("Runtime schema ID is {0} ; db ID is {1}", runtimeSchemaID, databaseInstanceID );
        
        if (!schemaID.equals(runtimeSchemaID)) {
            logger.error("Mismatched schemaID. Runtime schema ID is " + runtimeSchemaID); 
            throw new MCTException ("Mismatched schemaID.\nDeployed schema ID is " + runtimeSchemaID + 
                                  "\nbut MCT requires schema ID " + schemaID);
        } 

        // do not check stored procedures id so that we can have multiple versions running concurrently
        /*
        if (!storedProceduresID.equals(runtimeStoredProceduresID) || false) {
            logger.error("Mismatched storedProceduresID. Runtime storedProceduresID ID is " + runtimeStoredProceduresID); 
            throw new MCTException ("Mismatched storedProceduresID.\nDeployed storedProceduresID ID is " + runtimeStoredProceduresID + 
                                  "\nbut MCT requires stored procedures ID " + storedProceduresID);
        } */
      
    }
    
    /** 
     * Returns the value corresponding to a database attribute name 
     * 
     * @param name name of database identification attribute
     * @param dbID list of all database identification rows
     * @return the value corresponding to name
     * @throws MCTException if the database identification value cannot be set for name
     */
    protected static String getDatabaseIdentificationValue(String name, List <DatabaseIdentification> dbID) throws MCTException {

        for (DatabaseIdentification databaseIdentification : dbID) {
            if ((databaseIdentification.getName()).equalsIgnoreCase(name) ) {
                return databaseIdentification.getValue();
            } 
        }       
        throw new MCTException("Database identification table is missing attribute " + name + 
                               ".  The database version may be wrong.");        
    }
    
    
}
