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
import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.platform.spi.Platform;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FeedCycleRendererTest {
    @Mock 
    private FeedView fv1;
    @Mock
    private FeedView fv2;
    @Mock
    private FeedView fv3;
    @Mock
    private FeedView fv4;
    @Mock
    private FeedView fv5;
    @Mock
    private FeedProvider numericProvider;
    @Mock   
    private FeedProvider alphaProvider;
    @Mock   
    private FeedProvider exceptionProvider;
    private Map<String, List<Map<String,String>>> expectedValues;
    private List<Map<String,String>> singleValue;
    @Mock
    private Platform platform;
    
    
    @SuppressWarnings("unchecked")
    @BeforeMethod
    public void initialize() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(fv1.getVisibleFeedProviders()).thenReturn(Collections.singleton(numericProvider));
        Mockito.when(fv2.getVisibleFeedProviders()).thenReturn(Collections.singleton(alphaProvider));
        Mockito.when(fv3.getVisibleFeedProviders()).thenReturn(Collections.singleton(exceptionProvider));
        Mockito.when(fv4.getVisibleFeedProviders()).thenReturn(Collections.singleton(numericProvider));
        Mockito.when(fv5.getVisibleFeedProviders()).thenReturn(Collections.<FeedProvider>emptySet());
        
        expectedValues = new HashMap<String, List<Map<String,String>>>();
        Map<String,String> singleValueMap = new HashMap<String,String>();
        singleValueMap.put(FeedProvider.NORMALIZED_TIME_KEY, "1000");
        singleValue = Collections.singletonList(singleValueMap);
        expectedValues.put("numeric", singleValue);
        expectedValues.put("alpha", singleValue);
        
        Mockito.when(numericProvider.getSubscriptionId()).thenReturn("numeric");
        Mockito.when(alphaProvider.getSubscriptionId()).thenReturn("alpha");
        Mockito.when(exceptionProvider.getSubscriptionId()).thenReturn("exception");

        Mockito.when(platform.getFeedAggregator()).thenReturn(new FeedAggregator() {
            
            @Override
            public Map<String, List<Map<String, String>>> getData(Set<String> feedIDs, TimeUnit timeUnit,
                            long startTime, long endTime) {
                return expectedValues;
            }
            
        });
        new PlatformAccess().setPlatform(platform);
    }

    @AfterMethod
    public void tearDown() {
        new PlatformAccess().releasePlatform();
    }
    
    @Test
    public void testAdjustResponse() throws Exception {
        Map<FeedProvider, Long[]> times = new HashMap<FeedProvider, Long[]>();
        FeedCycleRenderer worker = new FeedCycleRenderer(times, Collections.<FeedView>singleton(fv1)) {
            @Override
            public Map<String, List<Map<String,String>>> doInBackground() {
                return super.doInBackground();
            }
        };
        
        Method m = worker.getClass().getSuperclass().getDeclaredMethod("adjustResponses", Map.class, Long.TYPE);
        long startTime = 1000;
        Map<String, List<Map<String,String>>> values = new HashMap<String, List<Map<String,String>>>();
        values.put("1", Collections.singletonList(Collections.singletonMap(FeedProvider.NORMALIZED_TIME_KEY, Long.toString(startTime-1))));
        values.put("2", Collections.singletonList(Collections.singletonMap(FeedProvider.NORMALIZED_TIME_KEY, Long.toString(startTime+1))));
        m.setAccessible(true);
        m.invoke(worker, values, startTime);
        Assert.assertEquals(values.get("1").get(0).get(FeedProvider.NORMALIZED_TIME_KEY), Long.toString(startTime));
        Assert.assertEquals(values.get("2").get(0).get(FeedProvider.NORMALIZED_TIME_KEY), Long.toString(startTime+1));

    }
    
    @Test
    public void testDoInBackground() throws Exception {
        Map<FeedProvider, Long[]> times = new HashMap<FeedProvider, Long[]>();
        times.put(numericProvider, new Long[]{0L,1L});
        times.put(alphaProvider, new Long[]{0L,1L});
        times.put(exceptionProvider, new Long[]{0L,1L});

        FeedCycleRenderer worker = new FeedCycleRenderer(times, Collections.<FeedView>singleton(fv1)) {
            @Override
            public Map<String, List<Map<String,String>>> doInBackground() {
                return super.doInBackground();
            }
        };
        worker.cancel(true);
        Assert.assertTrue(worker.doInBackground().isEmpty());
        
        worker = new FeedCycleRenderer(times, new HashSet<FeedView>(Arrays.asList(fv1, fv2, fv3, fv4, fv5))) {
            @Override
            public Map<String, List<Map<String,String>>> doInBackground() {
                return super.doInBackground();
            }
        };
        Map<String, List<Map<String,String>>> values = worker.doInBackground();
        Assert.assertEquals(values.size(), expectedValues.size());
        
        for (String key:expectedValues.keySet()) {
            Assert.assertTrue(values.containsKey(key));
        }

        Assert.assertEquals(1, values.get(numericProvider.getSubscriptionId()).size());
        
        Assert.assertEquals(1, values.get(alphaProvider.getSubscriptionId()).size());

        
        Assert.assertNull(values.get(exceptionProvider.getSubscriptionId()));

        worker.cancel(true);
        worker.done();
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testDone() throws Exception {
        Map<FeedProvider, Long[]> times = new HashMap<FeedProvider, Long[]>();
        
        // setup an exception during rendering to make an exception in the platform won't blog future
        // rendering cycles
        FeedCycleRenderer worker = new FeedCycleRenderer(times, new HashSet<FeedView>(Arrays.asList(fv1, fv2, fv3))) {
            @Override
            Map<String, List<Map<String, String>>> getData() throws InterruptedException, ExecutionException {
                throw new RuntimeException();
            }
        };
        worker.done();

        FeedView exManifestation = Mockito.mock(FeedView.class);
        Mockito.doThrow(new RuntimeException()).when(exManifestation).updateFromFeed(Mockito.anyMap());
        FeedView goodManifestation = Mockito.mock(FeedView.class);
        
        worker = new FeedCycleRenderer(times, new HashSet<FeedView>(Arrays.asList(exManifestation, goodManifestation))) {
            @Override
            Map<String, List<Map<String, String>>> getData() throws InterruptedException, ExecutionException {
                return Collections.emptyMap();
            }
        };
        worker.done();
        
        Mockito.verify(exManifestation).updateFromFeed(Mockito.anyMap());
        Mockito.verify(goodManifestation).updateFromFeed(Mockito.anyMap());
        
    }

}
