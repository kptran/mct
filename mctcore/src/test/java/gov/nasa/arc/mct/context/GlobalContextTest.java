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
package gov.nasa.arc.mct.context;

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import gov.nasa.arc.mct.identitymgr.IIdentityManager;
import gov.nasa.arc.mct.loader.ComponentLoader;
import gov.nasa.arc.mct.persistmgr.PersistenceBroker;
import gov.nasa.arc.mct.services.internal.component.User;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class GlobalContextTest {
    
    @Mock private User user1;
    @Mock private User user2;
    
    @Mock private PersistenceBroker persistenceBroker;
    @Mock private ComponentLoader componentLoader;
    @Mock private IIdentityManager identityManager;
    
    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(user1.getUserId()).thenReturn("asi");
        Mockito.when(user2.getUserId()).thenReturn("jimbooster");
    }
    
    @Test
    public void switchUserTest() {
        final CyclicBarrier barrier = new CyclicBarrier(2);
        final AtomicBoolean runnableRan = new AtomicBoolean(false);
        final Runnable testRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    runnableRan.set(true);
                    barrier.await();
                } catch (InterruptedException e) {
                    throw new AssertionError(e);
                } catch (BrokenBarrierException e) {
                    throw new AssertionError(e);
                }
            }
        };
        
        GlobalContext.getGlobalContext().switchUser(user1, null);
        assertSame(GlobalContext.getGlobalContext().getUser(), user1);
        
        Thread t = new Thread(new Runnable() {
            
            @Override
            public void run() {
                GlobalContext.getGlobalContext().switchUser(user2, testRunnable);
            }
        });
        t.start();
        
        try {
            barrier.await();
        } catch (InterruptedException e) {
            throw new AssertionError(e);
        } catch (BrokenBarrierException e) {
            throw new AssertionError(e);
        }
        assertTrue(runnableRan.get());
    }
    
    @Test
    public void testGettersSetters() {
        GlobalContext context = GlobalContext.getGlobalContext();
        
        assertNull(context.getSynchronousPersistenceBroker());
        assertNull(context.getComponentLoader());
        assertNull(context.getIdManager());
        
        context.setSynchronousPersistenceManager(persistenceBroker);
        assertSame(context.getSynchronousPersistenceBroker(), persistenceBroker);
        
        context.setComponentLoader(componentLoader);
        assertSame(context.getComponentLoader(), componentLoader);
                
        context.setIdManager(identityManager);
        assertSame(context.getIdManager(), identityManager);
    }
    
}
