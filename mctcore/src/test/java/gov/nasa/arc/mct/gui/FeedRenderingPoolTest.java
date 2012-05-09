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
package gov.nasa.arc.mct.gui;

import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.gui.FeedView.SynchronizationControl;
import gov.nasa.arc.mct.platform.spi.SubscriptionManager;
import gov.nasa.arc.mct.services.activity.TimeService;

import java.awt.Window;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FeedRenderingPoolTest {
   
    private FeedRenderingPool pool;
    @Mock 
    private FeedView fv1;
    @Mock
    private FeedProvider fp;
    @Mock
    private SubscriptionManager manager;
    private volatile FeedCycleRendererTest activeRenderer;
    private AtomicLong time;
    private final AtomicReference<String> feedId1 = new AtomicReference<String>();
    @Mock 
    private TimeService timeService;
    
    
    @AfterMethod
    public void terminate() throws Exception {
        pool.cancelTimer();
        if (activeRenderer != null) {
            activeRenderer.cancel(true);
        }
    }
    
    @BeforeMethod
    public void initialize() throws Exception {
        MockitoAnnotations.initMocks(this);
        time = new AtomicLong(0);
        Field f = FeedView.class.getDeclaredField("feedPool");
        f.setAccessible(true);
        FeedRenderingPool rp = (FeedRenderingPool) f.get(fv1);
        rp.cancelTimer();
        Mockito.when(fv1.getVisibleFeedProviders()).thenReturn(Collections.singleton(fp));
        Mockito.when(fp.getSubscriptionId()).thenAnswer(new Answer<String>() {
           @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return feedId1.get();
            } 
        });
        Mockito.when(fp.getTimeService()).thenReturn(timeService);
        Mockito.when(fp.toString()).thenAnswer(new Answer<String>() {
            @Override
             public String answer(InvocationOnMock invocation) throws Throwable {
                 return "fp : " + feedId1.get();
             } 
         });
        Mockito.when(timeService.getCurrentTime()).thenAnswer(new Answer<Long>() {
            @Override
            public Long answer(InvocationOnMock invocation) throws Throwable {
                return time.getAndIncrement();
            }
        });
        
        pool = new FeedRenderingPool(1000) {
            @Override
            FeedCycleRenderer createWorker(Map<FeedProvider, Long[]> timeMapping, Set<FeedView> activeFeedViews) {
               return activeRenderer = new FeedCycleRendererTest(timeMapping, activeFeedViews);
            }
            
            @Override
            FeedCycleRenderer createSyncWorker(long syncTime, Map<FeedProvider, Long[]> times,
                    Set<FeedView> activeFeedViews, Set<FeedView> syncedManifestations) {
                return new SyncFeedCycleRenderer(times, activeFeedViews);
            }
            
            @Override
            SubscriptionManager getSubscriptionManager() {
                return manager;
            }
        };
        activeRenderer = null;

    }
    
    static class SyncFeedCycleRenderer extends FeedCycleRenderer {
        public SyncFeedCycleRenderer(Map<FeedProvider, Long[]> timeMappings,
                Set<FeedView> activeViews) {
            super(timeMappings,activeViews);
        }
        
        @Override
        protected Map<String, List<Map<String,String>>> doInBackground() {
            return new HashMap<String, List<Map<String,String>>>();
        }
        
        @Override
        Map<String, List<Map<String, String>>> getData() throws InterruptedException, ExecutionException {
            return Collections.emptyMap();
        }
    }
    
    static class FeedCycleRendererTest extends FeedCycleRenderer {
        private Semaphore lock = new Semaphore(1,true);
        
        public FeedCycleRendererTest(Map<FeedProvider, Long[]> timeMappings,
                Set<FeedView> activeViews) {
            super(timeMappings,activeViews);
        }
        
        public Semaphore getSemaphore() {
            return lock;
        }
        
        @Override
        protected Map<String, List<Map<String,String>>> doInBackground() {
            try {
                lock.acquire();
            } catch (InterruptedException ie) {}
            return new HashMap<String, List<Map<String,String>>>();
        }
        
        @Override
        protected void done() {
            lock.release();
        }
    }
    
    static class FeedCycleRendererTest2 extends FeedCycleRendererTest {
        private Map<FeedProvider, Long[]> timeMappings;
        public FeedCycleRendererTest2(Map<FeedProvider, Long[]> timeMappings,
                Set<FeedView> activeViews) {
            super(timeMappings,activeViews);
            this.timeMappings = timeMappings;
        }
        
        @Override
        protected Map<String, List<Map<String,String>>> doInBackground() {
            Map<String, List<Map<String,String>>> values = new HashMap<String, List<Map<String, String>>>();
            for (FeedProvider fp:timeMappings.keySet()) {
                values.put(fp.getSubscriptionId(), Collections.<Map<String,String>>emptyList());
            }
            return values;
        }
        
        @Override
        protected void done() {
            
        }
        
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class)
    public void testIntervalChecking() {
        new FeedRenderingPool(-1);
    }
    
    @Test
    public void testSynchronizeTime() {
        pool.addFeedView(fv1);
        SynchronizationControl sc = 
            pool.synchronizeTime(System.currentTimeMillis() - 2);
        Assert.assertNotNull(sc);
        Assert.assertNull(pool.synchronizeTime(System.currentTimeMillis()));
        sc.synchronizationDone();
        Assert.assertNotNull(pool.synchronizeTime(System.currentTimeMillis()));
    }
    
    @Test
    public void testAddFeedView() throws Exception {
       pool.addFeedView(fv1);
       Thread.sleep(750);
       int maxCount = 15;
       while (time.get() < 3 && maxCount-- > 0) {
           Thread.yield();
           Thread.sleep(100);
       }
      
       activeRenderer.getSemaphore().release();
       activeRenderer.getSemaphore().acquire();
       Assert.assertTrue(activeRenderer.isDone());
       pool.removeFeedView(fv1);
    }
    
    @Test
    public void testHandleSubscriptions() throws Exception {
        pool.cancelTimer();
        Field f = FeedView.class.getDeclaredField("feedPool");
        f.setAccessible(true);
        FeedRenderingPool rp = (FeedRenderingPool) f.get(fv1);
        rp.cancelTimer();
        final String feedId = "f1";
        FeedView fv2 = Mockito.mock(FeedView.class);
        FeedProvider fp2 = Mockito.mock(FeedProvider.class);
        Mockito.when(fp2.getSubscriptionId()).thenReturn(feedId);
        Mockito.when(fp2.toString()).thenReturn("fp2 : " + feedId);
        Mockito.when(fp2.getTimeService()).thenReturn(timeService);
        Mockito.when(fv2.getVisibleFeedProviders()).thenReturn(Collections.singleton(fp2)); 
        
        feedId1.set(feedId);
        Assert.assertEquals(fv1.getVisibleFeedProviders().iterator().next().getSubscriptionId(), feedId);
        Assert.assertEquals(fv1.getVisibleFeedProviders().iterator().next().getSubscriptionId().compareTo(fv2.getVisibleFeedProviders().iterator().next().getSubscriptionId()),0);
        pool.addFeedView(fv1);
        pool.handleSubscriptions();
        Mockito.verify(manager).subscribe(feedId);
        pool.handleSubscriptions();
        Mockito.verify(manager).subscribe(feedId);
        pool.addFeedView(fv2);
        pool.handleSubscriptions();
        Mockito.verify(manager,Mockito.times(1)).subscribe(feedId);
        pool.removeFeedView(fv1);
        pool.removeFeedView(fv2);
        pool.handleSubscriptions();
        Mockito.verify(manager).unsubscribe(feedId);
    }
    
    @Test
    public void testSubscriptionsExceeded() {
        for (Window w: Window.getWindows()) {
            w.setVisible(false);
            w.dispose();
        }
        Assert.assertTrue(Window.getWindows().length == 0);
        Collection<FeedProvider> providers = new ArrayList<FeedProvider>(3000);
        for (int i = 0; i < 3001; i++) {
            FeedProvider fp = Mockito.mock(FeedProvider.class);
            Mockito.when(fp.getSubscriptionId()).thenReturn(Integer.toString(i));
            providers.add(fp);
        }
        Mockito.when(fv1.getVisibleFeedProviders()).thenReturn(providers);
        pool.cancelTimer();
        pool = new FeedRenderingPool(10000) {
            SubscriptionManager getSubscriptionManager() {
                return manager;
            }
            
            @Override
            FeedCycleRenderer createWorker(Map<FeedProvider, Long[]> timeMapping, Set<FeedView> activeFeedViews) {
               return activeRenderer = new FeedCycleRendererTest(timeMapping, activeFeedViews);
            }
        };
        pool.addFeedView(fv1);
        pool.handleSubscriptions();
        final AtomicBoolean invokedLater = new AtomicBoolean(false);
        final AtomicBoolean dialogAppeared = new AtomicBoolean(false);
        // verify that the max subscriptions exceeded dialog appears
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                int windowCount = 0;
                for (Window w: Window.getWindows()) {
                    if (w instanceof JDialog) {
                        JDialog d = (JDialog) w;
                        String title = d.getTitle();
                        if (title.equals("Maximum subscriptions Error")) {
                            windowCount++;
                        }
                        d.setVisible(false);
                        d.dispose();
                    }
                }
                dialogAppeared.set(windowCount == 1);
                invokedLater.set(true);
            }
        });
        while (!invokedLater.get()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
        }
        Assert.assertTrue(dialogAppeared.get());
    }
    
}
