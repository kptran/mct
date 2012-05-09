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
package gov.nasa.arc.mct.components.util;

import gov.nasa.arc.mct.api.feed.FeedAggregator;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.MCTLock;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.gui.util.MockComponent;
import gov.nasa.arc.mct.gui.util.MockDaoObject;
import gov.nasa.arc.mct.gui.util.MockDaoStrategy;
import gov.nasa.arc.mct.lock.manager.LockManager;
import gov.nasa.arc.mct.persistence.strategy.DaoObject;
import gov.nasa.arc.mct.persistence.strategy.DaoStrategy;
import gov.nasa.arc.mct.platform.spi.DefaultComponentProvider;
import gov.nasa.arc.mct.platform.spi.DuplicateUserException;
import gov.nasa.arc.mct.platform.spi.PersistenceService;
import gov.nasa.arc.mct.platform.spi.Platform;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.platform.spi.SubscriptionManager;
import gov.nasa.arc.mct.platform.spi.WindowManager;
import gov.nasa.arc.mct.services.activity.TimeService;
import gov.nasa.arc.mct.services.component.ComponentTagService;
import gov.nasa.arc.mct.services.component.MenuManager;
import gov.nasa.arc.mct.services.component.PolicyManager;
import gov.nasa.arc.mct.services.component.ProviderDelegateService;
import gov.nasa.arc.mct.services.component.TagService;
import gov.nasa.arc.mct.services.internal.component.ComponentInitializer;
import gov.nasa.arc.mct.services.internal.component.CoreComponentRegistry;
import gov.nasa.arc.mct.services.internal.component.User;
import gov.nasa.arc.mct.util.IdGenerator;

import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Map;
import java.util.Set;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CloneUtilTest {

    private MockComponent component;
    @Mock private User user;
    
    @BeforeClass
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(user.getUserId()).thenReturn("user22");
        
        ((new PlatformAccess())).setPlatform(new Platform() {
            
            @Override
            public void unregisterService(Object serviceObject) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void registerService(Class<?> serviceClass, Object serviceObject,
                            Dictionary<String, Object> props) throws IllegalArgumentException {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public WindowManager getWindowManager() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public TimeService getTimeService() {
                return null;
            }
            
            @Override
            public SubscriptionManager getSubscriptionManager() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public PolicyManager getPolicyManager() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public PersistenceService getPersistenceService() {
                return new PersistenceService() {
                    
                    @Override
                    public void setComponentDaoStrategy(AbstractComponent mctComp) {
                        mctComp.setDaoStrategy(new MockDaoStrategy((MockComponent)mctComp, new MockDaoObject()));
                        
                    }
                    
                    @Override
                    public AbstractComponent loadComponent(String componentId) {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public void updateComponentFromDatabase(AbstractComponent component) {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public Collection<String> getAllUsersOfDiscipline(String disciplineId) {
                        // TODO Auto-generated method stub
                        return null;
                    }
                                        
                    @Override
                    public Collection<String> getAllDisciplines() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public AbstractComponent findPUI(String pui) {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public Collection<AbstractComponent> getReferences(AbstractComponent component) {
                        return Collections.emptyList();
                    }

                    @Override
                    public void addNewUser(String userId, String group)
                                    throws DuplicateUserException {
                        // TODO Auto-generated method stub
                        
                    }

                    @Override
                    public void associateSessions(String session, String target) {
                        // TODO Auto-generated method stub
                        
                    }

                    @Override
                    public void disassociateSession(String session) {
                        // TODO Auto-generated method stub
                        
                    }

                    @Override
                    public Collection<AbstractComponent> findComponentByName(String session,
                                    String name) {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public void updateComponentsFromDatabase() {
                        // TODO Auto-generated method stub
                        
                    }

                    @Override
                    public Set<String> getAllUsers() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                };
            }
            
            @Override
            public MenuManager getMenuManager() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public LockManager getLockManager() {
                return new MockLockManager();
            }
            
            @Override
            public DefaultComponentProvider getDefaultComponentProvider() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public User getCurrentUser() {
                return user;
            }
            
            @Override
            public CoreComponentRegistry getComponentRegistry() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public TagService getTagService() {
                return null;
            }

            @Override
            public ComponentTagService getComponentTagService() {
                return null;
            }

            @Override
            public ProviderDelegateService getProviderDelegateService() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public FeedAggregator getFeedAggregator() {
                return null;
            }
        });

        
        component = new MockComponent(IdGenerator.nextComponentId(), false);
        component.getCapability(ComponentInitializer.class).initialize();
        component.setAndUpdateDisplayName("Mock Component");
        component.setOwner("Tester");
        component.setShared(true);
        component.setDaoStrategy(new MockDaoStrategy(component, new MockDaoObject()));
    }
    
    @Test
    public void testDuplicate() {        
        AbstractComponent cloned = CloneUtil.DUPLICATE.clone(component);
        Assert.assertNotNull(cloned);
        Assert.assertTrue(!cloned.getId().equals(component.getId()));
        Assert.assertFalse(cloned.isShared());
        Assert.assertEquals(cloned.getDisplayName(), component.getDisplayName());
        Assert.assertEquals(cloned.getOwner(), component.getOwner());
    }

    @Test
    public void testVersion() {        
        AbstractComponent cloned = CloneUtil.VERSION.clone(component);
        Assert.assertNotNull(cloned);
        Assert.assertEquals(cloned.getId(), component.getId());
        Assert.assertEquals(cloned.isShared(), false);
        Assert.assertEquals(cloned.getDisplayName(), component.getDisplayName());
        Assert.assertEquals(cloned.getOwner(), component.getOwner());
        Assert.assertEquals(cloned.getMasterComponent(), component);
        Assert.assertNotNull(cloned.getDaoStrategy());
    }
    
    @Test
    public void testTwiddle() {
        AbstractComponent cloned = CloneUtil.TWIDDLE.clone(component);
        Assert.assertNotNull(cloned);
        Assert.assertFalse(component.getId().equals(cloned.getId()));
        Assert.assertEquals(cloned.isShared(), false);
        Assert.assertEquals(cloned.getDisplayName(), component.getDisplayName());
        Assert.assertEquals(cloned.getOwner(), user.getUserId());
        Assert.assertEquals(cloned.getMasterComponent(), component);
        DaoStrategy<AbstractComponent, ? extends DaoObject> daoStrategy = cloned.getDaoStrategy();
        Assert.assertNotNull(daoStrategy);
        Assert.assertSame(daoStrategy, NullDaoStrategy.getInstance());
    }
    
    class MockLockManager implements LockManager {
        @Override
        public void newLock(String componentId) {
        }
        @Override
        public void removeLock(String componentId) {
        }
        @Override
        public boolean lock(String componentId, View viewManifestation) {
            return false;
        }
        @Override
        public boolean lock(String componentId, Set<View> viewManifestation) {
            return false;
        }
        @Override
        public boolean lock(String componentId) {
            // TODO Auto-generated method stub
            return false;
        }
        @Override
        public boolean lockForAllUser(String componentId) {
            // TODO Auto-generated method stub
            return false;
        }
        @Override
        public void unlock(String componentId, View viewManifestation) {
        }
        @Override
        public void unlock(String componentId, Set<View> viewManifestation) {
        }
        @Override
        public void unlock(String componentId) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public boolean isLocked(String componentId) {
            // TODO Auto-generated method stub
            return false;
        }
        @Override
        public boolean isManifestationLocked(String componentId, View viewManifestation) {
            // TODO Auto-generated method stub
            return false;
        }
        @Override
        public boolean isManifestationSetLocked(String componentId, Set<View> viewManifestations) {
            // TODO Auto-generated method stub
            return false;
        }
        @Override
        public boolean isExtendedLocking(String componentId) {
            // TODO Auto-generated method stub
            return false;
        }
        @Override
        public boolean isLockedForAllUsers(String componentId) {
            // TODO Auto-generated method stub
            return false;
        }
        @Override
        public boolean isUnlockingInProgress(String componentId) {
            // TODO Auto-generated method stub
            return false;
        }
        @Override
        public void shareLock(String componentId) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void pushChanges(String componentId, DaoObject daoObject) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public boolean hasPendingTransaction(String componentId) {
            // TODO Auto-generated method stub
            return false;
        }
        @Override
        public void abort(String componentId, Set<View> viewManifestations) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public Map<String, Set<View>> getAllLockedManifestations() {
            // TODO Auto-generated method stub
            return null;
        }
        @Override
        public Collection<MCTLock> getAllSharedLocks() {
            // TODO Auto-generated method stub
            return null;
        }
        @Override
        public String getOwnerUserId(String componentId) {
            // TODO Auto-generated method stub
            return null;
        }
        @Override
        public String getLockOwner(String componentId) {
            // TODO Auto-generated method stub
            return null;
        }
        
    }
}
