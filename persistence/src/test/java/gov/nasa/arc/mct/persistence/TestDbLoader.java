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
 * TestDbLoader.java Sep 28, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;

public class TestDbLoader {
    public static void load(Session session) {
        session.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                PreparedStatement stmt;
                stmt = connection.prepareStatement("drop table mine");
                stmt.execute();
                stmt = connection.prepareStatement("drop table mct_users");
                stmt.execute();
                stmt = connection.prepareStatement("drop table disciplines");
                stmt.execute();
                stmt = connection.prepareStatement("drop table telemetry_components");
                stmt.execute();

                stmt = connection
                                .prepareStatement("create table telemetry_components(component_id varchar(20), component_name varchar(50), parent_component_id varchar(20), component_type varchar(100), obj_version int default 0, PRIMARY KEY(component_id), foreign key(parent_component_id) references telemetry_components(component_id))");
                stmt.execute();
                stmt = connection
                                .prepareStatement("create table disciplines(discipline_id varchar(50), description varchar(500), program varchar(50),  obj_version int default 0, PRIMARY KEY(discipline_id))");
                stmt.execute();
                stmt = connection
                                .prepareStatement("create table mct_users(user_id varchar(20), firstname varchar(50), lastname varchar(50), discipline_id varchar(50), obj_version int default 0, PRIMARY KEY(user_id), foreign key(discipline_id) references disciplines(discipline_id))");
                stmt.execute();
                stmt = connection
                                .prepareStatement("create table mine(user_id varchar(20), component_id varchar(20), obj_version int default 0, PRIMARY KEY(user_id, component_id),  foreign key(user_id) references mct_users(user_id), foreign key(component_id) references telemetry_components(component_id))");
                stmt.execute();

                stmt = connection.prepareStatement("create index tel_comp_version_index on telemetry_components(obj_version)");
                stmt.execute();
                stmt = connection.prepareStatement("create unique index tel_comp_id_index on telemetry_components(component_id)");
                stmt.execute();
                stmt = connection.prepareStatement("create index disciplines_version_index on disciplines(obj_version)");
                stmt.execute();
                stmt = connection.prepareStatement("create index mct_users_version_index on mct_users(obj_version)");
                stmt.execute();
                stmt = connection.prepareStatement("create index mine_version_index on mine(obj_version)");
                stmt.execute();

                stmt = connection.prepareStatement("insert into disciplines (discipline_id, description) values ('ACO', 'Assembly and Checkout Officer')");
                stmt.execute();
                stmt = connection
                                .prepareStatement("insert into disciplines (discipline_id, description) values ('ADCO', 'Attitude Determination and Control Officer')");
                stmt.execute();
                stmt = connection.prepareStatement("insert into disciplines (discipline_id, description) values ('ATLAS', 'PHALCON, THOR and ECLSS combined')");
                stmt.execute();
                stmt = connection.prepareStatement("insert into disciplines (discipline_id, description) values ('CAPCOM', 'Capsule Communicator')");
                stmt.execute();
                stmt = connection
                                .prepareStatement("insert into disciplines (discipline_id, description) values ('CATO', 'Communication and Tracking Officer')");
                stmt.execute();
                stmt = connection
                                .prepareStatement("insert into disciplines (discipline_id, description) values ('ECLSS', 'Environmental Control and Life Support System')");
                stmt.execute();
                stmt = connection.prepareStatement("insert into disciplines (discipline_id, description) values ('EVA', 'Extravehicular Activity Officer')");
                stmt.execute();
                stmt = connection.prepareStatement("insert into disciplines (discipline_id, description) values ('FLIGHT', 'Flight Director')");
                stmt.execute();
                stmt = connection
                                .prepareStatement("insert into disciplines (discipline_id, description) values ('ODIN', 'Onboard, Data, Interfaces and Networks')");
                stmt.execute();
                stmt = connection.prepareStatement("insert into disciplines (discipline_id, description) values ('OPSPLAN', 'Operations Planner')");
                stmt.execute();
                stmt = connection.prepareStatement("insert into disciplines (discipline_id, description) values ('OSO', 'Operations Support Officer')");
                stmt.execute();
                stmt = connection.prepareStatement("insert into disciplines (discipline_id, description) values ('PAO', 'Public Affairs Officer ')");
                stmt.execute();
                stmt = connection
                                .prepareStatement("insert into disciplines (discipline_id, description) values ('PHALCON', 'Power, Heating, Articulation, Lighting Control Officer')");
                stmt.execute();
                stmt = connection.prepareStatement("insert into disciplines (discipline_id, description) values ('RIO', 'Remote Integration Officer')");
                stmt.execute();
                stmt = connection
                                .prepareStatement("insert into disciplines (discipline_id, description) values ('ROBO', 'Robotics Operations Systems Officer')");
                stmt.execute();
                stmt = connection.prepareStatement("insert into disciplines (discipline_id, description) values ('SURGEON', 'Flight Surgeon')");
                stmt.execute();
                stmt = connection.prepareStatement("insert into disciplines (discipline_id, description) values ('THOR', 'Thermal Operations and Resources')");
                stmt.execute();
                stmt = connection.prepareStatement("insert into disciplines (discipline_id, description) values ('TITAN', 'ADCO, ODIN and CATO combined')");
                stmt.execute();
                stmt = connection.prepareStatement("insert into disciplines (discipline_id, description) values ('VVO', 'Visiting Vehicle Officer')");
                stmt.execute();
                stmt = connection
                                .prepareStatement("insert into mct_users(user_id, firstname, lastname, discipline_id) values ('alTomotsugu', 'Alan', 'Tomotsugu', 'ACO')");
                stmt.execute();
                stmt = connection.prepareStatement("insert into mct_users(user_id, firstname, lastname, discipline_id) values ('amy', 'Amy', 'Amy', 'ACO')");
                stmt.execute();
                stmt = connection.prepareStatement("insert into mct_users(user_id, firstname, lastname, discipline_id) values ('asi', 'asi', 'asi', 'CATO')");
                stmt.execute();
                stmt = connection
                                .prepareStatement("insert into telemetry_components(component_id, component_name, parent_component_id, component_type) values ('1', 'Group A', null, 'gov.nasa.arc.mct.components.collection.CollectionComponent')");
                stmt.execute();
                stmt = connection
                                .prepareStatement("insert into telemetry_components(component_id, component_name, parent_component_id, component_type) values ('2', 'Group B', '1', 'gov.nasa.arc.mct.components.collection.CollectionComponent')");
                stmt.execute();
                stmt = connection
                                .prepareStatement("insert into telemetry_components(component_id, component_name, parent_component_id, component_type) values ('3', 'Group C', '1', 'gov.nasa.arc.mct.components.collection.CollectionComponent')");
                stmt.execute();
                stmt = connection
                                .prepareStatement("insert into telemetry_components(component_id, component_name, parent_component_id, component_type) values ('4', 'Group D', '2', 'gov.nasa.arc.mct.components.collection.CollectionComponent')");
                stmt.execute();
                stmt = connection
                                .prepareStatement("insert into telemetry_components(component_id, component_name, parent_component_id, component_type) values ('5', 'Group D', null, 'gov.nasa.arc.mct.components.collection.CollectionComponent')");
                stmt.execute();

                stmt = connection.prepareStatement("insert into mine(user_id, component_id) values ('alTomotsugu', '1')");
                stmt.execute();
                stmt = connection.prepareStatement("insert into mine(user_id, component_id) values ('alTomotsugu', '5')");
                stmt.execute();
            }
        });
    }
}
