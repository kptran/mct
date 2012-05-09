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
package gov.nasa.arc.mct.roles.gui;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.dao.specifications.MCTUser;
import gov.nasa.arc.mct.defaults.view.MCTHousingViewManifestation;
import gov.nasa.arc.mct.gui.housing.MCTControlArea;
import gov.nasa.arc.mct.gui.housing.MCTHousing;
import gov.nasa.arc.mct.gui.housing.MCTInspectionArea;
import gov.nasa.arc.mct.gui.util.TestSetupUtilities;
import gov.nasa.arc.mct.persistence.PersistenceUnitTest;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.internal.component.User;

import java.awt.GraphicsEnvironment;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.Test;

/**
 * This class mainly tests MCTHousingViewRoleManifestation.
 */
public class TestMCTHousingViewRole extends PersistenceUnitTest {

    private MCTHousingViewManifestation viewManifestation;
    private MCTHousing housing;
    @Mock
    private MCTUser user;

    @Override
    protected User getUser() {
        MockitoAnnotations.initMocks(this);

        when(user.getUserId()).thenReturn("asi");
        when(user.getDisciplineId()).thenReturn("CATO");
        return user;
    }

    @Override
    protected void postSetup() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        housing = TestSetupUtilities.setUpActiveHousing();
        viewManifestation = (MCTHousingViewManifestation) housing.getHousedViewManifestation();

    }

    @Test
    public void testConstructor() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        assertNotNull(viewManifestation);
    }

    @Test
    public void testBuildGui() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        viewManifestation.buildGUI();
    }

    @Test
    public void testGettersSetters() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        // menu bar needs more setup work
        // viewManifestation.setMenuBarArea(new MCTMenuBarArea(new
        // MCTStandardHousing(0, 0, "x", 0, (byte) 0, new
        // MCTHousingViewRole(0)), false));
        viewManifestation.setControlArea(new MCTControlArea(housing));
        viewManifestation.setControlAreaVisible(true);
        assertTrue(viewManifestation.isControlAreaVisible());
        // directory area needs a root node with a parent tree
        // viewManifestation.setDirectoryArea(new MCTDirectoryArea(housing,
        // TestSetupUtilities.createMCTMutableTreeStructure("x"), false,
        // false));
        // viewManifestation.setContentArea(new MCTContentArea(housing,
        // (ViewRole) null));
        viewManifestation.setInspectionArea(new MCTInspectionArea(Mockito.mock(AbstractComponent.class), Mockito.mock(ViewInfo.class)));
        assertTrue(housing.getSelectionProvider().getSelectedManifestations().isEmpty());
        assertNotNull(viewManifestation.getInspectionArea());
        assertNull(viewManifestation.getDirectoryArea());
        // See MODI-558, if no other manifestations are selected in a housing
        // window,
        // then the method should at least return the current housing view
        // manifestation.
        assertNotNull(viewManifestation.getCurrentManifestation());
    }

}
