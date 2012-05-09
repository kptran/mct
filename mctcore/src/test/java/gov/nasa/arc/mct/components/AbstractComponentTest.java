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
package gov.nasa.arc.mct.components;

import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.lock.manager.LockManager;
import gov.nasa.arc.mct.persistence.strategy.DaoObject;
import gov.nasa.arc.mct.persistence.strategy.DaoStrategy;
import gov.nasa.arc.mct.platform.spi.PersistenceService;
import gov.nasa.arc.mct.platform.spi.Platform;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.platform.spi.WindowManager;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.policy.PolicyInfo;
import gov.nasa.arc.mct.registry.GlobalComponentRegistry;
import gov.nasa.arc.mct.services.component.PolicyManager;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;
import gov.nasa.arc.mct.services.internal.component.ComponentInitializer;
import gov.nasa.arc.mct.services.internal.component.CoreComponentRegistry;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class AbstractComponentTest {

    public static class BaseComponentSub1 extends AbstractComponent {
        public BaseComponentSub1(int a){
        }
    }
    
    public static class BaseComponentSub3 extends AbstractComponent {
        BaseComponentSub3(){
        }
    }
    
    public static class BaseComponentSub2 extends AbstractComponent {
        public BaseComponentSub2(){
        }
    }
    
    public static class TestingView3 extends View {
        private static final long serialVersionUID = 1L;
        public TestingView3(AbstractComponent ac, ViewInfo vi) {
            super(ac,vi);
        }
        
    }
    
    @Mock
    private Platform mockPlatform;
    
    @Mock
    private PersistenceService mockPersistenceService;
    
    @Mock
    private LockManager mockLockManager;
    
    @Mock
    private WindowManager mockWindowManager;
    
    @BeforeMethod
    public void setup() {
        PolicyManager mockManager = new PolicyManager() {
            
            @Override
            public ExecutionResult execute(String categoryKey, PolicyContext context) {
                return new ExecutionResult(null, true, null);
            }
        };
        
        MockitoAnnotations.initMocks(this);
        Mockito.when(mockPlatform.getPolicyManager()).thenReturn(mockManager);
        Mockito.when(mockPlatform.getPersistenceService()).thenReturn(mockPersistenceService);
        Mockito.when(mockPlatform.getWindowManager()).thenReturn(mockWindowManager);
        Mockito.when(mockPlatform.getLockManager()).thenReturn(mockLockManager);

        (new PlatformAccess()).setPlatform(mockPlatform);
        GlobalContext.getGlobalContext().setLockManager(mockLockManager);
    }
    
    @SuppressWarnings({ "rawtypes" })
    @Test
    public void testShareComponent() throws Exception {
        AbstractComponent bc = Mockito.mock(AbstractComponent.class);
        Method shareComponentMethod = AbstractComponent.class.getDeclaredMethod("shareComponent", AbstractComponent.class, String.class, Boolean.TYPE, List.class);
        shareComponentMethod.setAccessible(true);
        
        AbstractComponent componentToShare = Mockito.mock(AbstractComponent.class);
        Mockito.when(componentToShare.isShared()).thenReturn(false);
        final String compId = "123";
        Mockito.when(componentToShare.getComponentId()).thenReturn(compId);
        LockManager lm = Mockito.mock(LockManager.class);
        Platform p = Mockito.mock(Platform.class);
        Mockito.when(p.getLockManager()).thenReturn(lm);
        new PlatformAccess().setPlatform(p);
        Mockito.when(lm.isLockedForAllUsers(compId)).thenReturn(true);
        
        shareComponentMethod.invoke(bc, componentToShare, "a", true, new ArrayList());
        Mockito.verify(componentToShare, Mockito.never()).setShared(Mockito.anyBoolean());
    }
    
    @SuppressWarnings({ "rawtypes" })
    @Test
    public void testRecursiveShareComponent() throws Exception {
        AbstractComponent bc = Mockito.mock(AbstractComponent.class);
        Method shareComponentMethod = AbstractComponent.class.getDeclaredMethod("shareComponent", AbstractComponent.class, String.class, Boolean.TYPE, List.class);
        shareComponentMethod.setAccessible(true);
        
        AbstractComponent componentToShare = Mockito.mock(AbstractComponent.class);
        Mockito.when(componentToShare.isShared()).thenReturn(true);
        final String compId = "123";
        
        Mockito.when(componentToShare.getComponentId()).thenReturn(compId);
        LockManager lm = Mockito.mock(LockManager.class);
        Platform p = Mockito.mock(Platform.class);
        Mockito.when(p.getLockManager()).thenReturn(lm);
        new PlatformAccess().setPlatform(p);
        Mockito.when(lm.isLockedForAllUsers(compId)).thenReturn(false);
        
        shareComponentMethod.invoke(bc, componentToShare, "a", true, new ArrayList());

        Mockito.verify(componentToShare, Mockito.never()).save();
    }

    /**
     * Test MCT-2581: Sharing systems causes client to hang eventually OOM. 
     * @throws Exception
     */
    @SuppressWarnings({ "rawtypes" })
    @Test
    public void testVisibleToAllShareComponent() throws Exception {
        AbstractComponent bc = Mockito.mock(AbstractComponent.class);
        Method shareComponentMethod = AbstractComponent.class.getDeclaredMethod("shareComponent", AbstractComponent.class, String.class, Boolean.TYPE, List.class);
        shareComponentMethod.setAccessible(true);
        
        AbstractComponent componentToShare = Mockito.mock(AbstractComponent.class);
        Mockito.when(componentToShare.isShared()).thenReturn(true);
        final String compId = "123";
        
        Mockito.when(componentToShare.getComponentId()).thenReturn(compId);
        LockManager lm = Mockito.mock(LockManager.class);
        Platform p = Mockito.mock(Platform.class);
        Mockito.when(p.getLockManager()).thenReturn(lm);
        new PlatformAccess().setPlatform(p);
        Mockito.when(lm.isLockedForAllUsers(compId)).thenReturn(false);
        
        shareComponentMethod.invoke(bc, componentToShare, "a", true, new ArrayList());

        Mockito.verify(componentToShare, Mockito.never()).save();
    }
    
    @Test
    public void testCheckBaseComponentRequirements() {        
        AbstractComponent.checkBaseComponentRequirements(BaseComponentSub2.class);
        checkConstructor(BaseComponentSub1.class);
        checkConstructor(BaseComponentSub3.class);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @DataProvider(name="viewInfoData")
    public Object[][] viewInfoData() {
        View v1 = Mockito.mock(View.class);
        View v2 = Mockito.mock(TestingView3.class);
        ViewInfo vi = new ViewInfo(v1.getClass(),"v1", ViewType.CENTER);
        //ViewInfo vi2 = new ViewInfo(v2.getClass(), "v2", ViewType.INSPECTION);
        ViewInfo vi3 = new ViewInfo(v2.getClass(), "v2", ViewType.CENTER);
        
        return new Object[][] {
                        new Object[] {
                           new LinkedHashSet(Arrays.asList(vi,vi3)), Collections.emptySet(), ViewType.CENTER, new LinkedHashSet(Arrays.asList(vi,vi3))            
                        },
                        new Object[] {
                           new LinkedHashSet(Arrays.asList(vi,vi3)), Collections.singleton(vi), ViewType.CENTER, Collections.singleton(vi3)            
                        },
                        
                        new Object[] {
                                        Collections.emptySet(), Collections.emptySet(), ViewType.LAYOUT, Collections.emptySet()
                                     } 
        };
    }
    
    @SuppressWarnings("rawtypes")
    @Test(dataProvider="viewInfoData")
    public void testGetViewInfos(Set<ViewInfo> viewInfos, final Set<ViewInfo> filterOut, ViewType type, Set<ViewInfo> expected) {
        AbstractComponent ac = new BaseComponentSub2();
        CoreComponentRegistry mockRegistry = Mockito.mock(CoreComponentRegistry.class);
        PolicyManager mockPolicyManager = Mockito.mock(PolicyManager.class);
        Mockito.when(mockPlatform.getComponentRegistry()).thenReturn(mockRegistry);
        Mockito.when(mockRegistry.getViewInfos(Mockito.anyString(), Mockito.same(type))).thenReturn(viewInfos);
        Mockito.when(mockPlatform.getPolicyManager()).thenReturn(mockPolicyManager);
        Mockito.when(mockPolicyManager.execute(Mockito.matches(PolicyInfo.CategoryType.FILTER_VIEW_ROLE.getKey()), Mockito.any(PolicyContext.class))).thenAnswer(
                        new Answer() {
                            public Object answer(InvocationOnMock invocation) {
                                Object[] args = invocation.getArguments();
                                PolicyContext pc = (PolicyContext) args[1];
                                ViewInfo vi = pc.getProperty(PolicyContext.PropertyName.TARGET_VIEW_INFO.getName(), ViewInfo.class);
                                return new ExecutionResult(pc, !filterOut.contains(vi), "");
                            }
                        }
        );
        Mockito.when(mockPolicyManager.execute(Mockito.matches(PolicyInfo.CategoryType.PREFERRED_VIEW.getKey()), Mockito.any(PolicyContext.class))).thenReturn(new ExecutionResult(new PolicyContext(),true, ""));
        Set<ViewInfo> infos = ac.getViewInfos(type);
        Assert.assertEquals(infos, expected);
    }
    
    @Test
    public void testInitialize() {
        AbstractComponent comp = new BaseComponentSub2();
        Assert.assertNull(comp.getId());
        comp.initialize();
        Assert.assertTrue(comp.getCapability(ComponentInitializer.class).isInitialized());
        Assert.assertNotNull(comp.getId());
    }
    
    @Test
    public void addDelegateComponentsTest() {
        (new PlatformAccess()).setPlatform(mockPlatform);
        BaseComponentSub2 comp = new BaseComponentSub2();
        comp.setShared(true);
        BaseComponentSub2 comp2 = new BaseComponentSub2();
        comp.addDelegateComponent(comp2);
        
        Assert.assertEquals(comp.getComponents().size(), 1);
        Assert.assertEquals(comp.getComponents().iterator().next(), comp2);
    }
    
    @Test
    public void deleteTest() {
        BaseComponentSub2 parentComp = new BaseComponentSub2();
        parentComp.setId("1");
        GlobalComponentRegistry.registerComponent(parentComp);
        BaseComponentSub2 childComp = new BaseComponentSub2();
        childComp.setId("2");
        BaseComponentSub2 parentComp2 = new BaseComponentSub2();
        parentComp2.setId("3");
        GlobalComponentRegistry.registerComponent(parentComp2);
        parentComp.addDelegateComponent(childComp);
        parentComp2.addDelegateComponent(childComp);

        Assert.assertSame(parentComp.getComponents().iterator().next(), childComp);
        Assert.assertSame(parentComp2.getComponents().iterator().next(), childComp);
        childComp.delete();
        Assert.assertTrue(parentComp.getComponents().isEmpty());
        Assert.assertTrue(parentComp2.getComponents().isEmpty());

    }
    
    
    @Test
    public void loadBeforeInitializeTest() {
        BaseComponentSub2 comp = new BaseComponentSub2();
        final AtomicBoolean loaded = new AtomicBoolean(false);
        
        DaoStrategy<AbstractComponent, DaoObject> daoStrategy = new DaoStrategy<AbstractComponent, DaoObject>() {

            @Override
            public void deleteObject(AbstractComponent comp) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public DaoObject getDaoObject() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public DaoObject getDaoObject(String sessionId) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Map<String, DaoObject> getDaoObjects(List<AbstractComponent> comps) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public AbstractComponent getMCTComp() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void load() {
                loaded.set(true);
            }

            @Override
            public void refreshDAO() {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void refreshDAO(AbstractComponent mctComp) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void removeObject(AbstractComponent mctComp) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void removeObjects(Collection<AbstractComponent> mctComps) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void saveObject() {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void saveObject(int childIndex, AbstractComponent childComp) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void saveObjects(int childIndex, Collection<AbstractComponent> childComps) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void associateDelegateSessionId(String sessionId, String delegateSessionId) {
                // TODO Auto-generated method stub
                
            }
        };
        comp.setDaoStrategy(daoStrategy);
        comp.load();
        Assert.assertTrue(loaded.get());
    }
    
    private void checkConstructor(Class<? extends AbstractComponent> clazz) {
        try {
            AbstractComponent.checkBaseComponentRequirements(clazz);
            Assert.fail("only public no argument constructors should be allowed " + clazz.getName());
        } catch (IllegalArgumentException e) {
            //
        }
    }

}
