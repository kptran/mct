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
package gov.nasa.arc.mct.dao.specifications;

import static org.mockito.Mockito.when;
import gov.nasa.arc.mct.component.MockComponent;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.ExtendedProperties;
import gov.nasa.arc.mct.components.util.ComponentModelUtil;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.platform.spi.Platform;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.services.component.PolicyManager;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;
import gov.nasa.arc.mct.services.internal.component.ComponentInitializer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ComponentSpecificationTest {
    @Mock
    private Platform mockPlatform;
    @Mock
    private PolicyManager mockPolicyMgr;
    private AbstractComponent comp;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
        comp = new MockComponent();
        comp.getCapability(ComponentInitializer.class).initialize();
        when(mockPlatform.getPolicyManager()).thenReturn(mockPolicyMgr);
        when(mockPolicyMgr.execute(Matchers.anyString(), (PolicyContext) Matchers.anyObject())).thenReturn(
                new ExecutionResult(null, true, ""));
    }

    @Test
    public void addViewStateTest() {
        ComponentSpecification compSpec = new ComponentSpecification();
        Map<String, ExtendedProperties> vProps = compSpec.getViewInfo();
        Assert.assertEquals(vProps.size(), 0);

        final AbstractComponent component = new MockComponent();
        ViewInfo vi = new ViewInfo(TestView.class,"",ViewType.OBJECT);
        View view = new TestView(component, vi);
        
        view.getViewProperties().setProperty("TEST_PROPERTY", "testValue");
        compSpec.setViewState(vi.getType(), view.getViewProperties());

        vProps = compSpec.getViewInfo();
        Assert.assertEquals(vProps.size(), 1);
        ExtendedProperties props = vProps.get(vi.getType());
        Assert.assertNotNull(props);
    }

    @Test
    public void testGettersSetters() {
        ComponentSpecification spec = new ComponentSpecification();
        
        Assert.assertNull(spec.getId());
        spec.setComponentId("1");
        Assert.assertEquals(spec.getId(), "1");

        Assert.assertEquals(spec.toString(), "1");
        
        Assert.assertEquals(spec.getVersion(), 0);
        spec.setVersion(1);
        Assert.assertEquals(spec.getVersion(), 1);
    }
    
    @Test
    public void testParentComponents() {
        ComponentSpecification parent1 = new ComponentSpecification();
        parent1.setComponentId("1");
        ComponentSpecification parent2 = new ComponentSpecification();
        parent2.setComponentId("2");
        ComponentSpecification spec = new ComponentSpecification();

        // We start off with no parent set.
        Assert.assertNull(spec.getParentComponents());
        Assert.assertTrue(spec.hasNoParent());
        
        // Adding a null parent should do nothing.
        spec.addParentComponent(null);
        Assert.assertNull(spec.getParentComponents());
        Assert.assertTrue(spec.hasNoParent());
        
        spec.addParentComponent(parent1);
        Assert.assertEquals(spec.getParentComponents().size(), 1);
        Assert.assertTrue(spec.getParentComponents().contains(parent1));
        Assert.assertFalse(spec.hasNoParent());
        
        spec.addParentComponent(parent2);
        Assert.assertEquals(spec.getParentComponents().size(), 2);
        Assert.assertTrue(spec.getParentComponents().contains(parent1));
        Assert.assertTrue(spec.getParentComponents().contains(parent2));
        Assert.assertFalse(spec.hasNoParent());
        
        Set<ComponentSpecification> parents = new HashSet<ComponentSpecification>();
        spec.setParentComponents(parents);
        Assert.assertTrue(spec.getParentComponents().isEmpty());
        Assert.assertTrue(spec.hasNoParent());
    }

    @Test
    public void testAssociatedComponents() {
        ComponentSpecification associate1 = new ComponentSpecification();
        associate1.setComponentId("1");
        ComponentSpecification associate2 = new ComponentSpecification();
        associate2.setComponentId("2");
        ComponentSpecification spec = new ComponentSpecification();

        // We start off with no associated list set.
        Assert.assertNull(spec.getAssociatedComponents());
        Assert.assertTrue(spec.hasNoParent());
        
        // Adding a null associate should do nothing.
        spec.addAssociatedComponent(null);
        Assert.assertNull(spec.getAssociatedComponents());
        
        // Removing a null associate should do nothing.
        spec.removeAssociatedComponent(null);
        Assert.assertNull(spec.getAssociatedComponents());
        
        spec.addAssociatedComponent(associate1);
        Assert.assertEquals(spec.getAssociatedComponents().size(), 1);
        Assert.assertTrue(spec.getAssociatedComponents().contains(associate1));
        
        spec.addAssociatedComponent(associate2);
        Assert.assertEquals(spec.getAssociatedComponents().size(), 2);
        Assert.assertTrue(spec.getAssociatedComponents().contains(associate1));
        Assert.assertTrue(spec.getAssociatedComponents().contains(associate2));
        
        spec.removeAssociatedComponent(associate1);
        Assert.assertEquals(spec.getAssociatedComponents().size(), 1);
        Assert.assertTrue(spec.getAssociatedComponents().contains(associate2));
        
        List<ComponentSpecification> associatedComponents = new ArrayList<ComponentSpecification>();
        spec.setAssociatedComponents(associatedComponents);
        Assert.assertTrue(spec.getAssociatedComponents().isEmpty());
    }
    
    // Test removing a component when nothing else has been done.
    @Test
    public void testAssociatedComponents2() {
        ComponentSpecification associate1 = new ComponentSpecification();
        associate1.setComponentId("1");
        ComponentSpecification spec = new ComponentSpecification();

        // Removing an associate if not present should do nothing.
        spec.removeAssociatedComponent(associate1);
        Assert.assertTrue(spec.getAssociatedComponents().isEmpty());
    }
    
    // Test that we can add to a child position, and that adding the same
    // component twice removes the old instance.
    @Test
    public void testAssociatedComponents3() {
        ComponentSpecification associate1 = new ComponentSpecification();
        associate1.setComponentId("1");
        ComponentSpecification associate2 = new ComponentSpecification();
        associate2.setComponentId("2");
        ComponentSpecification associate3 = new ComponentSpecification();
        associate3.setComponentId("3");
        ComponentSpecification spec = new ComponentSpecification();

        spec.addAssociatedComponent(0, associate1);
        Assert.assertEquals(spec.getAssociatedComponents().size(), 1);
        Assert.assertSame(spec.getAssociatedComponents().get(0), associate1);
        
        spec.addAssociatedComponent(0, associate2);
        Assert.assertEquals(spec.getAssociatedComponents().size(), 2);
        Assert.assertSame(spec.getAssociatedComponents().get(0), associate2);
        Assert.assertSame(spec.getAssociatedComponents().get(1), associate1);
        
        spec.addAssociatedComponent(-1, associate3);
        Assert.assertEquals(spec.getAssociatedComponents().size(), 3);
        Assert.assertSame(spec.getAssociatedComponents().get(0), associate2);
        Assert.assertSame(spec.getAssociatedComponents().get(1), associate1);
        Assert.assertSame(spec.getAssociatedComponents().get(2), associate3);
        
        spec.addAssociatedComponent(2, associate2);
        Assert.assertEquals(spec.getAssociatedComponents().size(), 3);
        Assert.assertSame(spec.getAssociatedComponents().get(0), associate1);
        Assert.assertSame(spec.getAssociatedComponents().get(1), associate2);
        Assert.assertSame(spec.getAssociatedComponents().get(2), associate3);
    }
    
    @Test
    public void testEquals() {
        ComponentSpecification spec1a = new ComponentSpecification();
        spec1a.setComponentId("1");
        ComponentSpecification spec1b = new ComponentSpecification();
        spec1b.setComponentId("1");
        ComponentSpecification spec2 = new ComponentSpecification();
        spec2.setComponentId("2");
        
        // Never equal to null
        Assert.assertFalse(spec1a.equals(null));
        
        // Never equal to another type
        Assert.assertFalse(spec1a.equals(new Object()));
        
        // Always equal to itself.
        Assert.assertTrue(spec1a.equals(spec1a));
        
        // Equal if component IDs are the same
        Assert.assertTrue(spec1a.equals(spec1b));
        Assert.assertTrue(spec1b.equals(spec1a));
        
        // Unequal if component IDs don't match
        Assert.assertFalse(spec1a.equals(spec2));
        Assert.assertFalse(spec2.equals(spec1a));
    }

    @Test
    public void testSetDifferencesNullSets() {
        List<ComponentSpecification> oneMinusTwo = new ArrayList<ComponentSpecification>();
        List<ComponentSpecification> twoMinusOne = new ArrayList<ComponentSpecification>();
        
        ComponentModelUtil.computeAsymmetricSetDifferences(null, null, oneMinusTwo, twoMinusOne);
        Assert.assertTrue(oneMinusTwo.isEmpty());
        Assert.assertTrue(twoMinusOne.isEmpty());

        ComponentSpecification spec = new ComponentSpecification();
        spec.setComponentId("1");
        List<ComponentSpecification> l = new ArrayList<ComponentSpecification>();
        l.add(spec);
        
        oneMinusTwo.clear();
        twoMinusOne.clear();
        ComponentModelUtil.computeAsymmetricSetDifferences(l, null, oneMinusTwo, twoMinusOne);
        Assert.assertEquals(oneMinusTwo.size(), 1);
        Assert.assertSame(oneMinusTwo.get(0), spec);
        Assert.assertTrue(twoMinusOne.isEmpty());
        
        oneMinusTwo.clear();
        twoMinusOne.clear();
        ComponentModelUtil.computeAsymmetricSetDifferences(null, l, oneMinusTwo, twoMinusOne);
        Assert.assertTrue(oneMinusTwo.isEmpty());
        Assert.assertEquals(twoMinusOne.size(), 1);
        Assert.assertSame(twoMinusOne.get(0), spec);
    }
    
    @Test(dataProvider="setDifferences")
    public void testSetDifferences(String[] ids1, String[] ids2, String[] oneMinusTwo, String[] twoMinusOne) {
        List<ComponentSpecification> diff1 = new ArrayList<ComponentSpecification>();
        List<ComponentSpecification> diff2 = new ArrayList<ComponentSpecification>();
                
        List<ComponentSpecification> one = new ArrayList<ComponentSpecification>();
        List<ComponentSpecification> two = new ArrayList<ComponentSpecification>();
        
        for (String id : ids1) {
            ComponentSpecification spec = new ComponentSpecification();
            spec.setComponentId(id);
            one.add(spec);
        }
        
        for (String id : ids2) {
            ComponentSpecification spec = new ComponentSpecification();
            spec.setComponentId(id);
            two.add(spec);
        }
        
        ComponentModelUtil.computeAsymmetricSetDifferences(one, two, diff1, diff2);
        Assert.assertEquals(diff1.size(), oneMinusTwo.length);
        Assert.assertEquals(diff2.size(), twoMinusOne.length);
        
        for (String id : oneMinusTwo) {
            ComponentSpecification spec = new ComponentSpecification();
            spec.setComponentId(id);
            Assert.assertTrue(diff1.contains(spec));
        }
        
        for (String id : twoMinusOne) {
            ComponentSpecification spec = new ComponentSpecification();
            spec.setComponentId(id);
            Assert.assertTrue(diff2.contains(spec));
        }
    }
    
    @DataProvider(name="setDifferences")
    public Object[][] getSetDifferences() {
        return new Object[][] {
                new Object[]{ new String[]{}, new String[]{}, new String[]{}, new String[]{} },
                new Object[]{ new String[]{"1"}, new String[]{}, new String[]{"1"}, new String[]{} },
                new Object[]{ new String[]{}, new String[]{"1"}, new String[]{}, new String[]{"1"} },
                new Object[]{ new String[]{"1", "2"}, new String[]{"3", "4"}, new String[]{"1", "2"}, new String[]{"3", "4"} },
                new Object[]{ new String[]{"1", "2", "3"}, new String[]{"2", "3", "4"}, new String[]{"1"}, new String[]{"4"} },
        };
    }
    
    @Test
    public void testViewStates() {
        ComponentSpecification spec = new ComponentSpecification();

        Assert.assertNull(spec.getViewStates());
        
        spec.addViewState("type1", "info1");
        Assert.assertEquals(spec.getViewStates().size(), 1);
        Assert.assertEquals(spec.getViewStates().get("type1"), "info1");
        
        spec.addViewState("type2", "info2");
        Assert.assertEquals(spec.getViewStates().size(), 2);
        Assert.assertEquals(spec.getViewStates().get("type1"), "info1");
        Assert.assertEquals(spec.getViewStates().get("type2"), "info2");
    }
    
    @SuppressWarnings("serial")
    public static class TestView extends View {
        public TestView(AbstractComponent ac, ViewInfo vi) {
            super(ac,vi);
        }
        
    }
}
