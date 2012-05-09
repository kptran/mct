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

import gov.nasa.arc.mct.api.feed.FeedAggregator;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.components.FeedProvider.RenderingInfo;
import gov.nasa.arc.mct.gui.FeedView.DataTransformation;
import gov.nasa.arc.mct.gui.FeedView.RenderingCallback;
import gov.nasa.arc.mct.platform.spi.Platform;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.services.activity.TimeService;

import java.awt.Color;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.SwingWorker;
import javax.swing.event.AncestorListener;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class FeedViewManifestationTest {
   
    private FeedView feedManifestation;
    private FeedView feedManifestation2;
    
    private AbstractComponent component;
    
    @Mock
    private FeedProvider provider;
    @Mock
    private FeedProvider provider2;
    @Mock
    private TimeService timeService;
    @Mock
    private TimeService timeService2;
    @Mock
    private Platform platform;
    
    @BeforeMethod
    public void initialize() {
        MockitoAnnotations.initMocks(this);
        component = new FeedComponent();
        Mockito.when(provider.getTimeService()).thenReturn(timeService);
        Mockito.when(provider2.getTimeService()).thenReturn(timeService2);
        feedManifestation = new FeedView(component,null) {
            private static final long serialVersionUID = 1L;

            @Override
            public void updateFromFeed(Map<String, List<Map<String,String>>> data) {
            }
            
            @Override
            public void synchronizeTime(Map<String, List<Map<String, String>>> data, long syncTime) {
            }
            
            @Override
            public Collection<FeedProvider> getVisibleFeedProviders() {
                return Collections.singleton(getFeedProvider(getManifestedComponent()));
            }
            
        };
        
        feedManifestation2 = new FeedView(component,null) {
            private static final long serialVersionUID = 1L;

            @Override
            public void updateFromFeed(Map<String, List<Map<String,String>>> data) {
            }
            
            @Override
            public void synchronizeTime(Map<String, List<Map<String, String>>> data, long syncTime) {
            }
            
            @Override
            public Collection<FeedProvider> getVisibleFeedProviders() {
                return Collections.singleton(getFeedProvider(getManifestedComponent()));
            }
        };

        new PlatformAccess().setPlatform(platform);
    }


    @AfterMethod
    public void tearDown() {
        new PlatformAccess().releasePlatform();
    }
    
    private static class FeedComponent extends AbstractComponent implements FeedProvider {
        public FeedComponent() {
        }
        
        @Override
        public String getSubscriptionId() {
            return null;
        }
        
        @Override
        public TimeService getTimeService() {
            return null;
        }
        
        @Override
        public int getMaximumSampleRate() {
            return 0;
        }
        
        @Override
        public boolean isPrediction() {
            return false;
        }
        
        @Override
        public String getCanonicalName() {
            // TODO Auto-generated method stub
            return null;
        }
        
        @Override
        public long getValidDataExtent() {
            return 0;
        }
        
        @Override
        protected <T> T handleGetCapability(Class<T> capability) {
            if (FeedProvider.class.isAssignableFrom(capability)) {
                return capability.cast(this);
            }
            return null;
        }

        @Override
        public String getLegendText() {
            return null;
        }

        @Override
        public RenderingInfo getRenderingInfo(Map<String, String> data) {
            return null;
        }
        
        @Override
        public FeedType getFeedType() {
            return FeedType.STRING;
        }
    }
    
    @Test
    public void testPool() {
        Assert.assertNotNull(feedManifestation.getRenderingPool());
    }
    
    @Test
    public void testgetFeedProvider() {
        Assert.assertSame(feedManifestation.getVisibleFeedProviders().iterator().next(), component);
        Assert.assertSame(feedManifestation2.getVisibleFeedProviders().iterator().next(), component);
        
        // increase coverage by invoking ancestor listeners
        Assert.assertEquals(feedManifestation.getAncestorListeners().length, 1);
        for (AncestorListener l : feedManifestation.getAncestorListeners()) {
            l.ancestorAdded(null);
            l.ancestorMoved(null);
            l.ancestorRemoved(null);
        }
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class)
    public void testRequestDataIllegalArgumentException() {
        feedManifestation.requestData(null, 0, 1, null, null, true);
    }
    
    @DataProvider(name="feedRequests")
    Object[][] generateFeedRequestTestData() {
        return new Object[][] {
             new Object[] {
                             1, 100, new long[] {50,Long.MAX_VALUE,50}, new long[] {50,100,50}
             },
             new Object[] {
                             7, 100, new long[] {50,25,50,2000}, new long[] {50,25,50,100}
             }
        };
    }
    
    @Test(dataProvider="feedRequests")
    public void testAdjustEndTimeForRequest(long start, long end, long[] maxTimes, long[] expectedTimes) throws Exception {
        Method m = feedManifestation.getClass().getSuperclass().getDeclaredMethod("adjustEndTimeForRequest", new Class[]{Map.class});
        m.setAccessible(true);
        
        Map<Request,Set<FeedProvider>> requestSet = new HashMap<Request,Set<FeedProvider>>();
        Request r = new Request(start,end);
        Set<FeedProvider> providers = new HashSet<FeedProvider>();
        Map<FeedProvider,Request> expectedMaxValues = new HashMap<FeedProvider,Request>();
        Set<Request> expectedRequests = new HashSet<Request>();
        for (int i = 0; i < maxTimes.length; i++) {
            FeedProvider fp = Mockito.mock(FeedProvider.class);
            Mockito.when(fp.getValidDataExtent()).thenReturn(maxTimes[i]);
            Mockito.when(fp.getMaximumSampleRate()).thenReturn(1);
            Mockito.when(fp.getSubscriptionId()).thenReturn("feed"+i);
            providers.add(fp);
            expectedMaxValues.put(fp, new Request(start,expectedTimes[i]));
            expectedRequests.add(new Request(start, expectedTimes[i]));
        }
        requestSet.put(r, providers);
        
        @SuppressWarnings("unchecked")
        Map<Request,Set<FeedProvider>> returnValue =
                        (Map<Request,Set<FeedProvider>>) m.invoke(feedManifestation, requestSet);
        
        Assert.assertEquals(returnValue.size(), expectedRequests.size());
        for (Entry<FeedProvider,Request> entry:expectedMaxValues.entrySet()) {
            Assert.assertTrue(returnValue.get(entry.getValue()).contains(entry.getKey()));
        }
    }
    
    @Test
    public void testRequestData() throws InterruptedException {
        TestDataTransform transformer = new TestDataTransform();
        TestDataCallback renderer = new TestDataCallback();
        final Map<String, List<Map<String, String>>> value = Collections.singletonMap("abc", 
                        Collections.singletonList(Collections.singletonMap("time", "0")));
        Mockito.when(timeService.getCurrentTime()).thenReturn(System.currentTimeMillis());
        Mockito.when(provider.getSubscriptionId()).thenReturn("abc");
        FeedAggregator feedAggregator = new FeedAggregator() {
            @Override
            public Map<String, List<Map<String, String>>> getData(Set<String> feedIDs,
                            TimeUnit timeUnit, long startTime, long endTime) {
                return value;
            }

        };
        Mockito.when(platform.getFeedAggregator()).thenReturn(feedAggregator);
        final AtomicBoolean visibleProvidersInvoked = new AtomicBoolean(false);
        
        feedManifestation = new FeedView(component,null) {
            private static final long serialVersionUID = 1L;

            @Override
            public void updateFromFeed(Map<String, List<Map<String,String>>> data) {
            }
            
            @Override
            public void synchronizeTime(Map<String, List<Map<String, String>>> data, long syncTime) {
            }
            
            @Override
            public Collection<FeedProvider> getVisibleFeedProviders() {
                visibleProvidersInvoked.set(true);
                return Collections.singleton(provider);
            }
            
        };
        
        feedManifestation.requestData(null, 0, 1, transformer, renderer, false);

        long startTime = System.currentTimeMillis();
        while ((!renderer.invoked.get() || !transformer.invoked.get()) && ((System.currentTimeMillis() - startTime)<3000)) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
            }
        }
        
        // verify that the callbacks were invoked
        Assert.assertTrue(renderer.invoked.get());
        Assert.assertTrue(transformer.invoked.get());
        Assert.assertTrue(visibleProvidersInvoked.get());
        
        visibleProvidersInvoked.set(false);
        transformer = new TestDataTransform();
        renderer = new TestDataCallback();
        SwingWorker<Map<String, List<Map<String, String>>>, Map<String, List<Map<String, String>>>> worker =
         feedManifestation.requestData(Collections.singleton(provider), 0, 1, transformer, renderer, false);
        
        startTime = System.currentTimeMillis();
        while ((!renderer.invoked.get() || !transformer.invoked.get() || !worker.isDone()) && ((System.currentTimeMillis() - startTime)<3000)) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
            }
        }
        
        // verify that the callbacks were invoked
        Assert.assertTrue(renderer.invoked.get());
        Assert.assertTrue(transformer.invoked.get());
        Assert.assertFalse(visibleProvidersInvoked.get());
    }
    
    @DataProvider(name="requestProvider")
    Object[][] generateDataForCurrentIterationRequestImpl() {
        FeedProvider fp = Mockito.mock(FeedProvider.class);
        Mockito.when(fp.getMaximumSampleRate()).thenReturn(1);
        return new Object[][] {
            new Object[] {Collections.singletonMap(new Request(1000,3000), Collections.singleton(fp)), Arrays.asList(new Request(1000,1999), new Request(2000,2999), new Request(3000,3000))},
            new Object[] {Collections.singletonMap(new Request(3000,1000), Collections.singleton(fp)), Arrays.asList(new Request(2001,3000), new Request(1001,2000), new Request(1000,1000))},
            new Object[] {Collections.singletonMap(new Request(1000,1000), Collections.singleton(fp)), Collections.singletonList(new Request(1000,1000))}
        };
    }
    
    
    @SuppressWarnings("unchecked")
    @Test(dataProvider="requestProvider")
    public void testGetCurrentIterationRequestImpl(Map<Request, Set<FeedProvider>> initialRequest, List<Request> requests) throws Exception {
        feedManifestation = new FeedView(component,null) {
            private static final long serialVersionUID = 1L;

            @Override
            public void updateFromFeed(Map<String, List<Map<String,String>>> data) {
            }
            
            @Override
            public void synchronizeTime(Map<String, List<Map<String, String>>> data, long syncTime) {
            }
            
            @Override
            public Collection<FeedProvider> getVisibleFeedProviders() {
                return Collections.emptySet();
            }
        };
        
        Method chunkMethod = FeedView.class.getDeclaredMethod("getCurrentIterationRequestsImpl", Map.class, Map.class, Integer.TYPE);
        chunkMethod.setAccessible(true);
        
        int requestNumber = 0;
        Map<Request, Set<FeedProvider>> iterationRequest = (Map<Request, Set<FeedProvider>>) chunkMethod.invoke(feedManifestation, initialRequest, Collections.emptyMap(), 1);
        while (!iterationRequest.isEmpty()) {
           Assert.assertEquals(1, iterationRequest.size());
           Entry<Request, Set<FeedProvider>> next = iterationRequest.entrySet().iterator().next();
           Assert.assertEquals(next.getKey(),requests.get(requestNumber++));
           iterationRequest = (Map<Request, Set<FeedProvider>>) chunkMethod.invoke(feedManifestation, initialRequest, iterationRequest, 1);
        }
        Assert.assertEquals(requestNumber, requests.size());
    }
    
    @Test
    public void testMultipleTimeServices() throws InterruptedException, ExecutionException {
        final Map<String, List<Map<String, String>>> value = Collections.singletonMap("abc",
                        Collections.singletonList(Collections.singletonMap("time", "1")));
        final Map<String, List<Map<String, String>>> value2 = Collections.singletonMap("abc2",
                        Collections.singletonList(Collections.singletonMap("time", "1")));
        Mockito.when(provider.getSubscriptionId()).thenReturn("abc");
        Mockito.when(provider2.getSubscriptionId()).thenReturn("abc2");
        FeedAggregator feedAggregator = new FeedAggregator() {
            @Override
            public Map<String, List<Map<String, String>>> getData(Set<String> feedIDs,
                            TimeUnit timeUnit, long startTime, long endTime) {
                Map<String, List<Map<String, String>>> m = new HashMap<String, List<Map<String, String>>>();
                m.putAll(value);
                m.putAll(value2);
                m.keySet().retainAll(feedIDs);
                return m;
            }

        };
        Mockito.when(platform.getFeedAggregator()).thenReturn(feedAggregator);
        final Set<String> returnedFeeds = new HashSet<String>();
        FeedView.RenderingCallback callback = new FeedView.RenderingCallback() {
            @Override
            public void render(Map<String, List<Map<String, String>>> data) {
                returnedFeeds.addAll(data.keySet());
            }
        };

        feedManifestation = new FeedView(component,null) {
            private static final long serialVersionUID = 1L;

            @Override
            public void updateFromFeed(Map<String, List<Map<String, String>>> data) {
            }

            @Override
            public void synchronizeTime(Map<String, List<Map<String, String>>> data, long syncTime) {
            }

            @Override
            public Collection<FeedProvider> getVisibleFeedProviders() {
                return Arrays.asList(provider, provider2);
            }
        };

        SwingWorker<Map<String, List<Map<String, String>>>, Map<String, List<Map<String, String>>>> w = feedManifestation
                        .requestData(Arrays.asList(provider, provider2), 0, 10, null, callback, true);
        w.get();
        Assert.assertEquals(returnedFeeds, new HashSet<String>(Arrays.asList("abc", "abc2")));
    }
    
    public static class TestDataCallback implements RenderingCallback {
        public Map<String, List<Map<String, String>>> data;
        public AtomicBoolean invoked = new AtomicBoolean(false);
        
        @Override
        public void render(Map<String, List<Map<String, String>>> data) {
            this.data = data;
            invoked.set(true);
        }
    }
    
    public static class TestDataTransform implements DataTransformation {
        public Map<String, List<Map<String, String>>> data;
        public AtomicBoolean invoked = new AtomicBoolean(false);
        public long startTime;
        public long endTime;
        
        @Override
        public void transform(Map<String, List<Map<String, String>>> data, long startTime, long endTime) {
            this.data = data;
            invoked.set(true);
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }
    
    @Test
    public void testRenderingInfo() {
         RenderingInfo ri =  new RenderingInfo("value&value", Color.red, "", Color.orange, true);
         String riAsString = ri.toString();
         RenderingInfo ri2 =  RenderingInfo.valueOf(riAsString); 
         Assert.assertEquals(ri2.getStatusColor(), Color.orange);
         Assert.assertEquals(ri2.getValueText(), "value&value");
         Assert.assertEquals(ri2.getValueColor(), Color.red);
    }
}
