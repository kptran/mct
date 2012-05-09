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
package gov.nasa.arc.mct.gui.housing.registry;

import gov.nasa.arc.mct.component.MockComponent;
import gov.nasa.arc.mct.defaults.view.MCTHousingViewManifestation;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.gui.housing.MCTAbstractHousing;
import gov.nasa.arc.mct.gui.housing.MCTStandardHousing;
import gov.nasa.arc.mct.platform.spi.MockPlatform;
import gov.nasa.arc.mct.platform.spi.Platform;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;
import gov.nasa.arc.mct.services.internal.component.ComponentInitializer;

import java.awt.GraphicsEnvironment;
import java.util.Collection;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestUserEnvironmentRegistry {
    private MockComponent componentA;
    private MockComponent componentB;
    private MockComponent componentC;
    
    private MockHousing housingA;
    private MockHousing housingB;
    private MockHousing housingC;
    
    private final PlatformAccess access = new PlatformAccess();
    private final Platform platform = new MockPlatform();
    
    @BeforeClass
    public void setup() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        access.setPlatform(platform);

        componentA = new MockComponent();
        componentA.setShared(false);
        componentA.getCapability(ComponentInitializer.class).initialize();

        
        componentB = new MockComponent();
        componentB.setShared(false);
        componentB.getCapability(ComponentInitializer.class).initialize();

        
        componentC = new MockComponent();
        componentC.setShared(false);
        componentC.getCapability(ComponentInitializer.class).initialize();

        
        housingA = new MockHousing(new MCTHousingViewManifestation(componentA, new ViewInfo(MCTHousingViewManifestation.class,"", ViewType.LAYOUT)));
        housingB = new MockHousing(new MCTHousingViewManifestation(componentB, new ViewInfo(MCTHousingViewManifestation.class,"", ViewType.LAYOUT)));
        housingC = new MockHousing(new MCTHousingViewManifestation(componentC, new ViewInfo(MCTHousingViewManifestation.class,"", ViewType.LAYOUT)));
        
        access.releasePlatform();
        
        UserEnvironmentRegistry.clearRegistry();
        UserEnvironmentRegistry.registerHousing(housingA);
        UserEnvironmentRegistry.registerHousing(housingB);
        UserEnvironmentRegistry.registerHousing(housingC);
    }
    
    @Test
    public void testRegisteredHousings() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        Assert.assertEquals(UserEnvironmentRegistry.getHousingCount(), 3);
        
        Collection<MCTAbstractHousing> allHousings = UserEnvironmentRegistry.getAllHousings();
        Assert.assertTrue(allHousings.contains(housingA));
        Assert.assertTrue(allHousings.contains(housingB));
        Assert.assertTrue(allHousings.contains(housingC));        
    }
    
    @Test(dependsOnMethods={"testRegisteredHousings"})
    public void testHousingsOfSameComponent() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        access.setPlatform(platform);
        MockHousing newHousingA = new MockHousing(new MCTHousingViewManifestation(componentA, new ViewInfo(MCTHousingViewManifestation.class,"", ViewType.LAYOUT)));
        UserEnvironmentRegistry.registerHousing(newHousingA);
        Assert.assertEquals(UserEnvironmentRegistry.getHousingCount(), 4);
        List<MCTAbstractHousing> housings = UserEnvironmentRegistry.getHousingsByComponetId(componentA.getId());
        Assert.assertNotNull(housings);
        Assert.assertEquals(housings.size(), 2);
        Assert.assertTrue(housings.contains(housingA));
        Assert.assertTrue(housings.contains(newHousingA));
        access.releasePlatform();
    }
    
    @Test(dependsOnMethods={"testHousingsOfSameComponent"})
    public void testHousingRemoval() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        UserEnvironmentRegistry.removeHousing(housingC);
        Assert.assertEquals(UserEnvironmentRegistry.getHousingCount(), 3);
        Collection<MCTAbstractHousing> allHousings = UserEnvironmentRegistry.getAllHousings();
        Assert.assertFalse(allHousings.contains(housingC));        
    }
    
    @SuppressWarnings("serial")
    private class MockHousing extends MCTStandardHousing {
        
        public MockHousing(View housingView) {
            super("Mock", 0, 0, 0, housingView);        
        }

        public MockHousing(int width, int height, int closeAction, byte areaSelection, View housingView) {
            super("Mock", width, height, closeAction, housingView);
        }
        
    }
}
