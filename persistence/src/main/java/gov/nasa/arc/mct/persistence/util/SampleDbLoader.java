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
 * SampleDbLoader.java Sep 28, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.persistence.util;

import gov.nasa.arc.mct.util.logging.MCTLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;

/**
 * This is only a sample test db used only for non-production environment.
 * 
 * @author asi
 * 
 */
public class SampleDbLoader {
    private final static MCTLogger logger = MCTLogger.getLogger(SampleDbLoader.class);
    
    public static void load(Session session) {
        session.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                try {
                clearTableIfExist(connection, "tag_association");
                clearTableIfExist(connection, "tag");
    
                clearTableIfExist(connection, "component_relationship");
                clearTableIfExist(connection, "view_state");
                dropTableIfExist(connection, "drop table edit_locks");
                clearTableIfExist(connection, "component_spec");
                clearTableIfExist(connection, "mct_users");
                clearTableIfExist(connection, "disciplines");

                PreparedStatement stmt = null;
                
                
                stmt = connection
                        .prepareStatement("create table edit_locks(component_id varchar(32) NOT NULL, " +
                                          "session varchar(32) NOT NULL, " +
                                          "user_id varchar(20) NOT NULL, " +
                                          "version int NOT NULL default 0, " +
                                          "lease_start TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                                          "exclusive varchar(5), " +
    
                                           "PRIMARY KEY (component_id, session) )");
                stmt.execute();
                
                stmt = connection.
                    prepareStatement(" create unique index component_id_exclusive_index on edit_locks (component_id,exclusive)");
                                
                stmt = connection
                        .prepareStatement("insert into disciplines (discipline_id, description, program, obj_version) values ('ACO', 'Assembly and Checkout Officer', 'Station', 0)");
                stmt.execute();
                stmt = connection
                        .prepareStatement("insert into disciplines (discipline_id, description, program, obj_version) values ('ADCO', 'Attitude Determination and Control Officer', 'Station', 0)");
                stmt.execute();
                stmt = connection
                        .prepareStatement("insert into disciplines (discipline_id, description, program, obj_version) values ('ATLAS', 'PHALCON, THOR and ECLSS combined', 'Station', 0)");
                stmt.execute();
                stmt = connection
                        .prepareStatement("insert into disciplines (discipline_id, description, program, obj_version) values ('CAPCOM', 'Capsule Communicator', 'Station', 0)");
                stmt.execute();
                stmt = connection
                        .prepareStatement("insert into disciplines (discipline_id, description, program, obj_version) values ('CATO', 'Communication and Tracking Officer', 'Station', 0)");
                stmt.execute();
                stmt = connection
                        .prepareStatement("insert into disciplines (discipline_id, description, program, obj_version) values ('ECLSS', 'Environmental Control and Life Support System', 'Station', 0)");
                stmt.execute();
                stmt = connection
                        .prepareStatement("insert into disciplines (discipline_id, description, program, obj_version) values ('EVA', 'Extravehicular Activity Officer', 'Station', 0)");
                stmt.execute();
                stmt = connection.prepareStatement("insert into disciplines (discipline_id, description, program, obj_version) values ('FLIGHT', 'Flight Director', 'Station', 0)");
                stmt.execute();
                stmt = connection
                        .prepareStatement("insert into disciplines (discipline_id, description, program, obj_version) values ('ODIN', 'Onboard, Data, Interfaces and Networks', 'Station', 0)");
                stmt.execute();
                stmt = connection
                        .prepareStatement("insert into disciplines (discipline_id, description, program, obj_version) values ('OPSPLAN', 'Operations Planner', 'Station', 0)");
                stmt.execute();
                stmt = connection
                        .prepareStatement("insert into disciplines (discipline_id, description, program, obj_version) values ('OSO', 'Operations Support Officer', 'Station', 0)");
                stmt.execute();
                stmt = connection
                        .prepareStatement("insert into disciplines (discipline_id, description, program, obj_version) values ('PAO', 'Public Affairs Officer','Station',  0)");
                stmt.execute();
                stmt = connection
                        .prepareStatement("insert into disciplines (discipline_id, description, program, obj_version) values ('PHALCON', 'Power, Heating, Articulation, Lighting Control Officer', 'Station', 0)");
                stmt.execute();
                stmt = connection
                        .prepareStatement("insert into disciplines (discipline_id, description, program, obj_version) values ('RIO', 'Remote Integration Officer', 'Station', 0)");
                stmt.execute();
                stmt = connection
                        .prepareStatement("insert into disciplines (discipline_id, description, program, obj_version) values ('ROBO', 'Robotics Operations Systems Officer','Station',  0)");
                stmt.execute();
                stmt = connection.prepareStatement("insert into disciplines (discipline_id, description, program, obj_version) values ('SURGEON', 'Flight Surgeon', 'Station', 0)");
                stmt.execute();
                stmt = connection
                        .prepareStatement("insert into disciplines (discipline_id, description, program, obj_version) values ('THOR', 'Thermal Operations and Resources', 'Station', 0)");
                stmt.execute();
                stmt = connection
                        .prepareStatement("insert into disciplines (discipline_id, description, program, obj_version) values ('TITAN', 'ADCO, ODIN and CATO combined', 'Station', 0)");
                stmt.execute();
                stmt = connection
                        .prepareStatement("insert into disciplines (discipline_id, description, program, obj_version) values ('VVO', 'Visiting Vehicle Officer', 'Station', 0)");
                stmt.execute();
                stmt = connection
                .prepareStatement("insert into disciplines (discipline_id, description, program, obj_version) values ('ADMIN', 'Admin', 'Admin', 0)");
                stmt.execute();

                stmt = connection
                        .prepareStatement("insert into mct_users(user_id, firstname, lastname, discipline_id, obj_version) values ('alTomotsugu', 'Alan', 'Tomotsugu', 'ACO', 0)");
                stmt.execute();
                stmt = connection
                        .prepareStatement("insert into mct_users(user_id, firstname, lastname, discipline_id, obj_version) values ('amy', 'Amy', 'Amy', 'ACO', 0)");
                stmt.execute();
                stmt = connection
                        .prepareStatement("insert into mct_users(user_id, firstname, lastname, discipline_id, obj_version) values ('asi', 'asi', 'asi', 'CATO', 0)");
                stmt.execute();
                stmt = connection
                .prepareStatement("insert into mct_users(user_id, firstname, lastname, discipline_id, obj_version) values ('admin', 'admin', 'admin', 'ADMIN', 0)");
                stmt.execute();
                
                stmt = connection
                .prepareStatement("insert into component_spec(component_id, component_name, owner, shared, component_type, deleted, obj_version) values ('1', 'Systems', 'admin', 1, 'gov.nasa.arc.mct.core.components.TelemetryDataTaxonomyComponent', 0, 0)");
                stmt.execute();
                
                stmt = connection
                .prepareStatement("insert into component_spec(component_id, component_name, owner, shared, component_type, deleted, obj_version) values ('2', 'Comps', 'admin', 1, 'gov.nasa.arc.mct.core.components.TelemetryDataTaxonomyComponent', 0, 0)");
                stmt.execute();
                
                stmt = connection
                .prepareStatement("insert into component_spec(component_id, component_name, owner, shared, component_type, deleted, obj_version) values ('3', 'Station Telemetry', 'admin', 1, 'gov.nasa.arc.mct.core.components.TelemetryDataTaxonomyComponent', 0, 0)");
                stmt.execute();
                
                stmt = connection
                .prepareStatement("insert into component_spec(component_id, component_name, owner, shared, component_type, deleted, obj_version) values ('4', 'Disciplines', 'admin', 1, 'gov.nasa.arc.mct.core.components.TelemetryDataTaxonomyComponent', 0, 0)");
                stmt.execute();

                stmt = connection.prepareStatement("insert into component_relationship(component_id, associated_component_id, seq_no) values ('1', '2', 0)");
                stmt.execute();
                
                stmt = connection.prepareStatement("insert into component_relationship(component_id, associated_component_id, seq_no) values ('1', '3', 1)");
                stmt.execute();
                
                stmt = connection
                        .prepareStatement("insert into component_spec(component_id, component_name, owner, shared, component_type, deleted, obj_version) values ('5', 'Group A', 'alTomotsugu', 1, 'gov.nasa.arc.mct.components.collection.CollectionComponent', 0, 0)");
                stmt.execute();
                stmt = connection
                        .prepareStatement("insert into component_spec(component_id, component_name, owner, shared, component_type, deleted, obj_version) values ('6', 'Group B', 'amy', 1, 'gov.nasa.arc.mct.components.collection.CollectionComponent', 0, 0)");
                stmt.execute();
                stmt = connection
                        .prepareStatement("insert into component_spec(component_id, component_name, owner, shared, component_type, deleted, obj_version) values ('7', 'Group C', 'amy', 1, 'gov.nasa.arc.mct.components.collection.CollectionComponent', 0, 0)");
                stmt.execute();
                stmt = connection
                        .prepareStatement("insert into component_spec(component_id, component_name, owner, shared, component_type, deleted, obj_version) values ('8', 'Group D', 'asi', 0, 'gov.nasa.arc.mct.components.collection.CollectionComponent', 0, 0)");
                stmt.execute();
                stmt = connection
                        .prepareStatement("insert into component_spec(component_id, component_name, owner, shared, component_type, deleted, obj_version) values ('9', 'Group E', 'alTomotsugu', 0, 'gov.nasa.arc.mct.components.collection.CollectionComponent', 0, 0)");
                stmt.execute();
                stmt = connection
                .prepareStatement("insert into component_spec(component_id, component_name, owner, shared, component_type, deleted, obj_version) values ('10', 'My Sandbox', 'alTomotsugu', 0, 'gov.nasa.arc.mct.core.components.MineTaxonomyComponent', 0, 0)");
                stmt.execute();
                stmt = connection
                .prepareStatement("insert into component_spec(component_id, component_name, owner, shared, component_type, deleted, obj_version) values ('11', 'alTomotsugu Drop Box', 'alTomotsugu', 1, 'gov.nasa.arc.mct.core.components.TelemetryUserDropBoxComponent', 0, 0)");
                stmt.execute();
                stmt = connection
                .prepareStatement("insert into component_spec(component_id, component_name, owner, shared, component_type, deleted, obj_version) values ('13', 'My Sandbox', 'asi', 0, 'gov.nasa.arc.mct.core.components.MineTaxonomyComponent', 0, 0)");
                stmt.execute();

                stmt = connection
                .prepareStatement("insert into component_spec(component_id, component_name, owner, shared, component_type, deleted, obj_version) values ('15', 'Group F', 'asi', 0, 'gov.nasa.arc.mct.components.collection.CollectionComponent', 0, 0)");
                stmt.execute();
                stmt = connection
                .prepareStatement("insert into component_spec(component_id, component_name, owner, shared, component_type, deleted, obj_version, model_info) values ('16', 'LADP01MDCSJAU', 'asi', 0, 'gov.nasa.arc.mct.components.telemetry.TelemetryElementComponent', 0, 0,'<value></value>')");
                stmt.execute();
                
                stmt = connection.prepareStatement("insert into component_relationship(component_id, associated_component_id, seq_no) values ('3', '16', 0)");
                stmt.execute();
                
                stmt = connection
                .prepareStatement("insert into component_spec(component_id, component_name, owner, shared, component_type, deleted, obj_version) values ('17', 'Collection A', 'alTomotsugu', 0, 'gov.nasa.arc.mct.components.collection.CollectionComponent', 0, 0)");
                stmt.execute();
                stmt = connection
                .prepareStatement("insert into component_spec(component_id, component_name, owner, shared, component_type, deleted, obj_version) values ('18', 'Collection B', 'alTomotsugu', 0, 'gov.nasa.arc.mct.components.collection.CollectionComponent', 0, 0)");
                stmt.execute();
                stmt = connection
                .prepareStatement("insert into component_spec(component_id, component_name, owner, shared, component_type, deleted, obj_version) values ('19', 'Collection C', 'alTomotsugu', 0, 'gov.nasa.arc.mct.components.collection.CollectionComponent', 0, 0)");
                stmt.execute();

                stmt = connection
                .prepareStatement("insert into component_spec(component_id, component_name, owner, shared, component_type, deleted, obj_version) values ('20', 'CanvasCollection A', 'alTomotsugu', 0, 'gov.nasa.arc.mct.components.collection.CollectionComponent', 0, 0)");
                stmt.execute();
                stmt = connection
                .prepareStatement("insert into component_spec(component_id, component_name, owner, shared, component_type, deleted, obj_version) values ('21', 'PAOP01MDCSJAU', 'asi', 0, 'gov.nasa.arc.mct.components.telemetry.TelemetryElementComponent', 0, 0)");
                stmt.execute();
                stmt = connection.prepareStatement("insert into component_relationship(component_id, associated_component_id, seq_no) values ('20', '21', 0)");
                stmt.execute();

                
                stmt = connection.prepareStatement("insert into component_relationship(component_id, associated_component_id, seq_no) values ('5', '6', 0)");
                stmt.execute();
                stmt = connection.prepareStatement("insert into component_relationship(component_id, associated_component_id, seq_no) values ('5', '7', 1)");
                stmt.execute();
                stmt = connection.prepareStatement("insert into component_relationship(component_id, associated_component_id, seq_no) values ('6', '7', 0)");
                stmt.execute();
                stmt = connection.prepareStatement("insert into component_relationship(component_id, associated_component_id, seq_no) values ('6', '8', 1)");
                stmt.execute();
                stmt = connection.prepareStatement("insert into component_relationship(component_id, associated_component_id, seq_no) values ('7', '8', 0)");
                stmt.execute();
                stmt = connection.prepareStatement("insert into component_relationship(component_id, associated_component_id, seq_no) values ('10', '11', 0)");
                stmt.execute();

                stmt = connection.prepareStatement("insert into component_relationship(component_id, associated_component_id, seq_no) values ('17', '18', 0)");
                stmt.execute();
                stmt = connection.prepareStatement("insert into component_relationship(component_id, associated_component_id, seq_no) values ('18', '19', 0)");
                stmt.execute();
                
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                    throw e;
                }
            }
            
            private void clearTableIfExist(Connection connection, String tableName) {
                try {
                    PreparedStatement stmt;
                    stmt = connection.prepareStatement("delete from " + tableName);
                    stmt.execute();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }

            private void dropTableIfExist(Connection connection, String dropTableSmt) {
                try {
                    PreparedStatement stmt;
                    stmt = connection.prepareStatement(dropTableSmt);
                    stmt.execute();
                } catch (Exception e) {
                    // ignore exception and do not log for unit tests
                }
            }
        });
    }
}
