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
package gov.nasa.arc.mct.gui.housing;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import gov.nasa.arc.mct.dao.specifications.MCTUser;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.persistence.PersistenceUnitTest;
import gov.nasa.arc.mct.services.internal.component.User;

import java.awt.GraphicsEnvironment;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.Test;

/**
 * Remove any of the supplied statements as needed, then press Ctrl-Shift-O to fix imports
 */
public class TestStatusArea extends PersistenceUnitTest {

    @SuppressWarnings("serial")
    class HousingCombo extends MCTStandardHousing implements MCTHousing {

        public HousingCombo(int width, int height, String title, int closeAction, byte areaSelection, View housingView) {
            super(title, width, height, closeAction, housingView);
        }
    }

    @Mock HousingCombo housing;
    Object x;
    private MCTStatusArea statusArea;
    
    @Mock private MCTUser user;
    
    @Override
    protected User getUser() {
        MockitoAnnotations.initMocks(this);
        
        when(user.getUserId()).thenReturn("asi");
        when(user.getDisciplineId()).thenReturn("CATO");
        return user;
    }
    
    @Override
    protected void postSetup() {
        if(GraphicsEnvironment.isHeadless()) {
            return;
        }
        MockitoAnnotations.initMocks(this);
        statusArea = new MCTStatusArea(housing);

    }

    @Test
    public void testConstructor() {
        if(GraphicsEnvironment.isHeadless()) {
            return;
        }
        assertNotNull(statusArea);
    }

    @Test(dependsOnMethods = "testConstructor")
    public void testSetLeftWidget() {
        if(GraphicsEnvironment.isHeadless()) {
            return;
        }
        JLabel label = new JLabel("X");
        statusArea.setLeftWidget(label);
        JPanel left = (JPanel) statusArea.getComponent(0);
        assertEquals(left.getComponent(0), label);
    }

    @Test(dependsOnMethods = "testSetLeftWidget")
    public void testSetRightWidget() {
        if(GraphicsEnvironment.isHeadless()) {
            return;
        }
        JLabel label2 = new JLabel("Y");
        statusArea.setRightWidget(label2);
        JPanel right = (JPanel) statusArea.getComponent(1);
        assertEquals(right.getComponent(0), label2);
    }
}
