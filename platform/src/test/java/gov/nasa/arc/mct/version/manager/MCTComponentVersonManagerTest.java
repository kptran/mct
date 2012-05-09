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
package gov.nasa.arc.mct.version.manager;

import gov.nasa.arc.mct.component.MockComponent;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.ExtendedProperties;
import gov.nasa.arc.mct.components.JAXBModelStatePersistence;
import gov.nasa.arc.mct.components.ModelStatePersistence;
import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.lock.manager.MCTLockManagerFactory;
import gov.nasa.arc.mct.persistence.PersistenceUnitTest;
import gov.nasa.arc.mct.persistmgr.PersistenceBroker;
import gov.nasa.arc.mct.roles.gui.MockViewManifestation;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;
import gov.nasa.arc.mct.services.internal.component.ComponentInitializer;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MCTComponentVersonManagerTest extends PersistenceUnitTest {
    
    @Mock private PersistenceBroker persistenceBroker;

    @Override
    protected void postSetup() {
        MockitoAnnotations.initMocks(this);
        
        GlobalContext.getGlobalContext().setLockManager(MCTLockManagerFactory.getLockManager());
        GlobalContext.getGlobalContext().setSynchronousPersistenceManager(persistenceBroker);
    }

    @Test
    public void versionizedTest() {
        AbstractComponent comp = new MockComponent();
        comp.getCapability(ComponentInitializer.class).setId("Mock Version Comp 1");
        comp.getCapability(ComponentInitializer.class).initialize();

        comp.setShared(false);
        
        View v = new MockViewManifestation(comp, new ViewInfo(MockViewManifestation.class,"", ViewType.CENTER));
        comp.addViewManifestation(v);

        Set<View> viewManifests = comp.getAllViewManifestations();
        View viewManifestation = viewManifests.iterator().next();
        MCTComponentVersionManager.versionized(viewManifestation);
        AbstractComponent versionizedComp = viewManifestation.getManifestedComponent();
        Assert.assertNotNull(versionizedComp);
        Assert.assertTrue(comp == versionizedComp); // A component will not get versionized if it is not shared.
        Assert.assertTrue(versionizedComp.getAllViewManifestations().contains(v));
        
        comp.setShared(true);
        MCTComponentVersionManager.versionized(viewManifestation);
        versionizedComp = viewManifestation.getManifestedComponent();
        Assert.assertNotNull(versionizedComp);
        Assert.assertNotSame(comp, versionizedComp);
        
        ExtendedProperties vrp = viewManifestation.getViewProperties();
        Assert.assertNotNull(vrp);
        ComponentInitializer initializer = versionizedComp.getCapability(ComponentInitializer.class);
        ExtendedProperties vrpFromVersionComp = initializer.getViewRoleProperties(viewManifestation.getInfo().getType());
        Assert.assertNotNull(vrpFromVersionComp);
        Assert.assertSame(vrpFromVersionComp, vrp);
        
        initializer = comp.getCapability(ComponentInitializer.class);
        ExtendedProperties vrpFromMasterComp = initializer.getViewRoleProperties(viewManifestation.getInfo().getType());
        Assert.assertNotSame(vrpFromMasterComp, vrpFromVersionComp);
    }
    
    @Test
    public void mergeVersionAndUpdateTest() {
        ComponentWithModel comp = new ComponentWithModel();
        comp.getModel().setTestField("origTest");
        comp.getCapability(ComponentInitializer.class).setId("Mock Version Comp 2");
        comp.getCapability(ComponentInitializer.class).initialize();
        comp.setShared(false);
        View v = new MockViewManifestation(comp, new ViewInfo(MockViewManifestation.class,"", ViewType.CENTER));
        comp.addViewManifestation(v);

        Set<View> viewManifests = comp.getAllViewManifestations();
        View viewManifestation = viewManifests.iterator().next();
        MCTComponentVersionManager.versionized(viewManifestation);
        AbstractComponent versionizedComp = viewManifestation.getManifestedComponent();
        Assert.assertNotNull(versionizedComp);
        Assert.assertTrue(comp == versionizedComp); // A component will not get versionized if it is not shared.
        
        comp.setShared(true);
        MCTComponentVersionManager.versionized(viewManifestation);
        versionizedComp = viewManifestation.getManifestedComponent();
        Assert.assertNotNull(versionizedComp);
        Assert.assertFalse(comp == versionizedComp);
        ComponentWithModel versionizedModelComp = (ComponentWithModel)versionizedComp;
        Assert.assertEquals(versionizedModelComp.getModel().getTestField(), "origTest");
        versionizedModelComp.getModel().setTestField("updated");
        
        MCTComponentVersionManager.mergeVersionAndUpdate(Collections.singleton(viewManifestation));
        versionizedComp = viewManifestation.getManifestedComponent();
        Assert.assertNotNull(versionizedComp);
        Assert.assertTrue(comp == versionizedComp);
        Assert.assertEquals(comp.getModel().getTestField(), "updated");
    }
    
    @Test
    public void testViewChangeRefresh() throws Exception {
        // Setup
        AbstractComponent versionedComponent = Mockito.mock(AbstractComponent.class);
        ViewInfo vi = new ViewInfo(MockViewManifestation.class,"test",ViewType.CENTER);
        ExtendedProperties versionedProps = Mockito.mock(ExtendedProperties.class);
        View versionedManifestation = Mockito.mock(View.class);
        ComponentInitializer versionedInit = Mockito.mock(ComponentInitializer.class);
        
        AbstractComponent masterComponent = Mockito.mock(AbstractComponent.class);
        ExtendedProperties masterProps = Mockito.mock(ExtendedProperties.class);
        ComponentInitializer masterInit = Mockito.mock(ComponentInitializer.class);
        
        Mockito.when(versionedManifestation.getManifestedComponent()).thenReturn(versionedComponent);
        Mockito.when(versionedManifestation.getInfo()).thenReturn(vi);
        Mockito.when(versionedComponent.getMasterComponent()).thenReturn(masterComponent);
        Map<String, ExtendedProperties> versionedCompProps = new HashMap<String, ExtendedProperties>();
        versionedCompProps.put(vi.getType(), versionedProps);
        versionedComponent.getCapability(ComponentInitializer.class).setViewRoleProperties(versionedCompProps);
        Mockito.when(versionedComponent.getComponents()).thenReturn(Collections.<AbstractComponent>emptyList());
        Field initializerField = AbstractComponent.class.getDeclaredField("initializer");
        initializerField.setAccessible(true);
        initializerField.set(versionedComponent, versionedInit);
        
        Mockito.when(masterComponent.isShared()).thenReturn(true);
        Map<String, ExtendedProperties> masterCompProps = new HashMap<String, ExtendedProperties>();
        masterCompProps.put(vi.getType(), masterProps);
        masterComponent.getCapability(ComponentInitializer.class).setViewRoleProperties(masterCompProps);
        Mockito.when(masterComponent.getComponents()).thenReturn(Collections.<AbstractComponent>emptyList());
        initializerField.set(masterComponent, masterInit);
        
        // Changes in view properties
        MCTComponentVersionManager.mergeVersionAndUpdate(Collections.singleton(versionedManifestation));
        
        // Verify
        Mockito.verify(masterComponent, Mockito.atMost(1)).save(vi);
        Mockito.verify(masterComponent, Mockito.never()).save();
    }
    
    
    public static class ComponentWithModel extends AbstractComponent {
        private AtomicReference<SimpleModel> model = new AtomicReference<SimpleModel>(new SimpleModel());
        
        @Override
        protected <T> T handleGetCapability(Class<T> capability) {
            if (ModelStatePersistence.class.isAssignableFrom(capability)) {
                JAXBModelStatePersistence<SimpleModel> persistence = new JAXBModelStatePersistence<SimpleModel>() {

                    @Override
                    protected SimpleModel getStateToPersist() {
                        return model.get();
                    }

                    @Override
                    protected void setPersistentState(SimpleModel modelState) {
                        model.set(modelState);
                    }

                    @Override
                    protected Class<SimpleModel> getJAXBClass() {
                        return SimpleModel.class;
                    }
                    
                };
                
                return capability.cast(persistence);
            }
            
            return null;
        }
        
        public SimpleModel getModel() {
            return model.get();
        }
        
    }
    
    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SimpleModel {
        private String testField;

        /**
         * @return the testField
         */
        public String getTestField() {
            return testField;
        }

        /**
         * @param testField the testField to set
         */
        public void setTestField(String testField) {
            this.testField = testField;
        }
    }
    
    
}
