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
package gov.nasa.arc.mct.core.roles;

import gov.nasa.arc.mct.gui.table.AlternateRowColorTable;
import gov.nasa.arc.mct.platform.spi.PersistenceService;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.platform.spi.RoleAccess;
import gov.nasa.arc.mct.services.internal.component.User;

import java.awt.GridLayout;
import java.util.ResourceBundle;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class UsersPage extends JPanel {
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            UsersPage.class.getName().substring(0, 
                    UsersPage.class.getName().lastIndexOf("."))+".Bundle");

    private static final String USER_ID = BUNDLE.getString("users.table.column.header.userid");
    private static final String GROUP_ID = BUNDLE.getString("users.table.column.header.groupid");
    private static final String PRIM_ROLE = "Primary Role";
        
    private DefaultTableModel tableModel;
    private String discipline;
    
    public UsersPage(String discipline) {
        this.discipline = discipline;
        tableModel = new DefaultTableModel(new String[]{USER_ID, GROUP_ID, PRIM_ROLE}, 0);
        refresh();
        JTable table = new AlternateRowColorTable();
        table.setModel(tableModel);
        setLayout(new GridLayout(1, 1));
        add(new JScrollPane(table));        
    }
    
    public void refresh() {
        clearRows();
        GenericUser genericUser = new GenericUser();
        PersistenceService persistenceService = PlatformAccess.getPlatform().getPersistenceService();
        for (String user : persistenceService.getAllUsersOfDiscipline(discipline)) {
            genericUser.setUserId(user);
            genericUser.setDisciplineId(discipline);            
            tableModel.addRow(new Object[]{user, discipline, RoleAccess.getPrimaryRole(genericUser)});                
        }        
    }
    
    private void clearRows() {
        if (tableModel.getRowCount() > 0) {
            for (int r = tableModel.getRowCount() - 1; r >= 0; r--)
                tableModel.removeRow(r);
        }        
    }
        
    private static class GenericUser implements User {
        
        private String userId, disciplineId;
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
        
        public void setDisciplineId(String disciplineId) {
            this.disciplineId = disciplineId; 
        }

        @Override
        public String getUserId() {
            return userId;
        }

        @Override
        public String getDisciplineId() {
            return disciplineId;
        }

        @Override
        public User getValidUser(String userID) {
            return null;
        }

        @Override
        public boolean hasRole(String role) {
            return false;
        }
        
    }
}
