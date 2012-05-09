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
 * CompatibilityCheckerTest.java October 21, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
 
 package gov.nasa.arc.mct.platform;

import gov.nasa.arc.mct.dao.specifications.DatabaseIdentification;
import gov.nasa.arc.mct.util.exception.MCTException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CompatibilityCheckerTest {

    final private String shemaIDName =  "schema_id";
    final private String storedProceduresIDName =  "stored_procedures_id";
    final private String timestampName =  "creation_timestamp";
    final private String databaseName =  "database_name";

    /**
     * Make a list of DB ids.
     * @return list of DAO objects
     */
    private List<DatabaseIdentification> getDefaultDatabaseIdentificationList() {
        List<DatabaseIdentification> dbidList = new ArrayList<DatabaseIdentification>();
        
        DatabaseIdentification s = new DatabaseIdentification();
        s.setName(shemaIDName);
        s.setValue("1");
        dbidList.add(s);
        
        DatabaseIdentification procs = new DatabaseIdentification();
        procs.setName(storedProceduresIDName);
        procs.setValue("1");
        dbidList.add(procs);

        DatabaseIdentification c = new DatabaseIdentification();
        c.setName(timestampName);
        c.setValue("date");
        dbidList.add(c);

        DatabaseIdentification n = new DatabaseIdentification();
        n.setName(databaseName);
        n.setValue("some_dbname");
        dbidList.add(n);
        
        return dbidList;
    }
    
    @Test (expectedExceptions = MCTException.class)
    public void testCheckDatabaseCompatibilitySchemaIncompatible() throws MCTException, IOException  {
        CompatibilityChecker compatibilityChecker= new CompatibilityChecker("1","1");
        List<DatabaseIdentification> dbidList = new ArrayList<DatabaseIdentification>();

        DatabaseIdentification s = new DatabaseIdentification();
        s.setName(shemaIDName);
        s.setValue("2");
        dbidList.add(s);

        DatabaseIdentification c = new DatabaseIdentification();
        c.setName(timestampName);
        c.setValue("date");
        dbidList.add(c);

        DatabaseIdentification n = new DatabaseIdentification();
        n.setName(databaseName);
        n.setValue("some_dbname");
        dbidList.add(n);

        compatibilityChecker.checkDatabaseCompatibility(dbidList);
    }
    
    @Test
    public void testGetDatabaseIdentification() throws MCTException  {  
        Assert.assertEquals(  CompatibilityChecker.getDatabaseIdentificationValue(shemaIDName,  getDefaultDatabaseIdentificationList()) , "1");
    }
    
    @Test (expectedExceptions = {MCTException.class})
    public void testGetDatabaseIdentificationNameMissing() throws MCTException  {
        CompatibilityChecker.getDatabaseIdentificationValue("missingAttribute",  getDefaultDatabaseIdentificationList());
    } 
}
