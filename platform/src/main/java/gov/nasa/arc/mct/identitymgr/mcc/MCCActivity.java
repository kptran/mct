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
 * MCCActivity.java Sep 24, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.identitymgr.mcc;

/**
 * Provides access to MCC site activity variables.
 */
import gov.nasa.arc.mct.util.property.MCTProperties;

import java.io.IOException;
import java.util.Properties;

public class MCCActivity {

    private String activityID;
    private String activityName; 
    private String activityType; 
    private String flightID;
    private String groupID; 
    private String program; 
    private String reconID;
    private String simType; 
    private String softwareLevel; 
    private String userID; // user changes with Shift Change Event
    private String vehicleID;
    private static final String unset = "unset";

    /**
     * Creates an activity info.
     * @throws IOException
     */
    public MCCActivity() throws IOException {
        this(new MCTProperties("properties/identityManager.properties"), MCTProperties.DEFAULT_MCT_PROPERTIES);
    }

    /**
     * Creates an activity info.
     * @param mccProps MCC site specific input properties
     * @throws IOException
     */
    public MCCActivity(Properties mccProps) throws IOException {
        this(mccProps, MCTProperties.DEFAULT_MCT_PROPERTIES);
    }

    /**
     * Creates an activity info.
     * @param mccProperties MCC site specific input properties
     * @param mctProperties MCT input properties
     * @throws IOException
     */
    public MCCActivity(Properties mccProperties, MCTProperties mctProperties) throws IOException {

        this.setActivityID(mccProperties.getProperty("mcc.activityID", unset));
        this.setActivityName(mccProperties.getProperty("mcc.activityName", unset));
        this.setActivityType(mccProperties.getProperty("mcc.activityType", unset));
        this.setFlightID(mccProperties.getProperty("mcc.flightID", unset));
        this.setProgram(mccProperties.getProperty("mcc.program", unset));
        this.setReconID(mccProperties.getProperty("mcc.reconID", unset));
        this.setSimType(mccProperties.getProperty("mcc.simType", unset));
        this.setSoftwareLevel(mccProperties.getProperty("mcc.softwareLevel", unset));
        this.setVehicleID(mccProperties.getProperty("mcc.vehicleID", unset));

        this.setUserID(mctProperties.getProperty("mct.user", unset));
        this.setGroupID(mccProperties.getProperty("mct.group", unset));
    }

    /**
     * Get activity ID.
     * @return ID
     */
    public String getActivityID() {
        return activityID;
    }

    void setActivityID(String activityID) {
        this.activityID = activityID;
    }

    /**
     * Get activity name.
     * @return name
     */
    public String getActivityName() {
        return activityName;
    }

    void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    /**
     * Get activity type.
     * @return type
     */
    public String getActivityType() {
        return activityType;
    }

    void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    /**
     * Get activity simulation type.
     * @return simulation type
     */
    public String getSimType() {
        return simType;
    }

    void setSimType(String simType) {
        this.simType = simType;
    }

    /**
     * Get software level (certification level)
     * @return level
     */
    public String getSoftwareLevel() {
        return softwareLevel;
    }

    void setSoftwareLevel(String softwareLevel) {
        this.softwareLevel = softwareLevel;
    }

    /**
     * Get MCC program (vehicle)
     * @return program
     */
    public String getProgram() {
        return program;
    }

    void setProgram(String program) {
        this.program = program;
    }
 
    /**
     * Get vehicle ID
     * @return ID
     */
    public String getVehicleID() {
        return vehicleID;
    }

    void setVehicleID(String vehicleID) {
        this.vehicleID = vehicleID;
    }

    /** 
     * Get flight ID.
     * @return  ID
     */
    public String getFlightID() {
        return flightID;
    }

    void setFlightID(String flightID) {
        this.flightID = flightID;
    }

    /**
     * Get reconfiguration ID.
     * @return ID
     */
    public String getReconID() {
        return reconID;
    }

    void setReconID(String reconID) {
        this.reconID = reconID;
    }

    /**
     * Get group ID
     * @return ID
     */
    public String getGroupID() {
        return groupID;
    }

    void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    /** 
     * Get user ID
     * @return  ID
     */
    public String getUserID() {
        return userID;
    }

    void setUserID(String userID) {
        this.userID = userID;
    }

}
