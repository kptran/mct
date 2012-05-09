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

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.services.component.ViewInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingWorker;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * This is the super class for feed displays. All feed views are painted by periodic feed data
 * at most once per second. Special requests for data (perhaps for historical data) are excluded from 
 * this normalized refreshing. A subclass must do the following:
 * <ul>
 * <li>Override {@link #updateFromFeed(Map)} to refresh the view with the incoming data set.</li>
 * </ul>
 */
public abstract class FeedView extends View {
    private static final long serialVersionUID = 1L;
    private static final int PAINT_RATE = 250; // paint rate in milliseconds
    private static final FeedRenderingPool feedPool = new FeedRenderingPool(PAINT_RATE);
    /**
     * The maximum number of data points that are returned from a data request. This will cause the
     * requests to be split into a number of requests that the client will need to merge as they are completed. 
     * 
     */
    private static final int MAX_DATA_POINTS = 1000;
    
    /**
     * Adjust the rendering cycle based on the visibility of this component, if the component is visible
     * then add to the rendering list if it is not, then remove it. 
     */
    private final AncestorListener ancestorListener = new AncestorListener() {

        @Override
        public void ancestorAdded(AncestorEvent event) {
            feedPool.addFeedView(FeedView.this);
        }

        @Override
        public void ancestorMoved(AncestorEvent event) {
        }

        @Override
        public void ancestorRemoved(AncestorEvent event) {
            feedPool.removeFeedView(FeedView.this);
        }
        
    };
    
    /**
     * Creates a new feed view manifestation with characteristics given by
     * persisted view manifestation information.
     * 
     * @param component the currently bound component
     * @param info info for this component
     */
    public FeedView(AbstractComponent component, ViewInfo info) {
        super(component,info);
        addAncestorListener(ancestorListener);
    }

    /**
     * Creates a new feed view manifestation with characteristics given by
     * persisted view manifestation information.
     * 
     * @param ac the <code>AbstractComponent</code>
     */
    public FeedView(AbstractComponent ac) {
        addAncestorListener(ancestorListener);
    }

    /**
     * Updates the view manifestation because of a change in the data available from the feed. 
     * This method will be invoked for periodic refreshes as well as special requests from data. 
     * Invocation of this method from the MCT platform will always be in the AWT thread. 
     * @param data to update this view with. The map will contain a mapping from subscriptionId for
     * each feed to the list (time sequenced) set of values. The will be one element in the list for each change occurring during
     * the last paint cycle. There will be at least one data point per time unit. 
     */
    public abstract void updateFromFeed(Map<String,List<Map<String,String>>> data);
    
    /**
     * Adjust the max time request to ensure that data is not requested beyond the end of the possible time. 
     * @param requestSet to adjust
     */
    private Map<Request,Set<FeedProvider>> adjustEndTimeForRequest(Map<Request,Set<FeedProvider>> requestSet) {
        Map<Request,Set<FeedProvider>> adjustedRequest = new HashMap<Request,Set<FeedProvider>>();
        for (Entry<Request,Set<FeedProvider>> entry: requestSet.entrySet()) {
            // check each entry and adjust the maximum request time
            for (FeedProvider fp:entry.getValue()) {
                Request request = new Request(Math.min(entry.getKey().getStartTime(), fp.getValidDataExtent()),Math.min(entry.getKey().getEndTime(), fp.getValidDataExtent()));
                Set<FeedProvider> providers = adjustedRequest.get(request);
                if (providers == null) {
                    adjustedRequest.put(request, providers = new HashSet<FeedProvider>());
                }
                providers.add(fp);
            }
        }
        
        return adjustedRequest;
    }
    
    private int getMaxSamplesSecond(Set<FeedProvider> feeds) {
        int maxSamples = 0;
        for (FeedProvider fp: feeds) {
            maxSamples += fp.getMaximumSampleRate();
        }
        return maxSamples;
    }
    
    /**
     * Perform a special request for data. Invoking this method is asynchronous and will return 
     * immediately, the transformer and renderer callback parameters are used to transform data off the AWT 
     * thread and to render the data. The data request may be split in multiple requests which are dispatched
     * incrementally. 
     * @param providers to use for the data retrieval operation. If this argument is null, then the 
     * return value from {@link #getVisibleFeedProviders()}. No attempt is made to subscribe to ongoing events
     * for feed providers.
     * @param startTime to use for the request, in milliseconds since January 1, 1970
     * @param endTime to use for the request, in milliseconds since January 1, 1970
     * @param transformer to use during the background processing (this will not be invoked in the AWT thread). 
     * Null can be passed, in which case no transformation will be applied to the data. 
     * @param renderer to use for visualizing the data. This will be invoked in the AWT thread and must not 
     * be null. 
     * @param reverseOrder true if the data should be retrieved in reverse order, with the slice containing the end time arriving first, false otherwise.
     * @throws IllegalArgumentException if renderer is null
     * @return SwingWorker representing the running task, which may be canceled. 
     */
    public SwingWorker<Map<String, List<Map<String, String>>>, Map<String, List<Map<String, String>>>> requestData(Collection<FeedProvider> providers, long startTime, long endTime,
                            final DataTransformation transformer, final RenderingCallback renderer, final boolean reverseOrder) throws IllegalArgumentException {
        if (renderer == null) {
            throw new IllegalArgumentException("renderer cannot be null");
        }
        
        if (providers == null) {
            providers = getVisibleFeedProviders();
        }
        
        assert providers != null : "visible feed providers cannot be null";
        
        final Collection<FeedProvider> feedProviders = providers;
        Map<FeedProvider, Long[]> requestTimes = new HashMap<FeedProvider, Long[]>();
        for (FeedProvider provider:feedProviders) {
            requestTimes.put(provider, new Long[]{!reverseOrder?startTime:endTime,!reverseOrder?endTime:startTime});
        }
        
        final Semaphore s = new Semaphore(1);
        try {
            s.acquire();
        } catch (InterruptedException ie) {
            // ignore this exception 
        }
        
        FeedCycleRenderer worker = new FeedCycleRenderer(requestTimes, Collections.singleton(this)) {
            
            @Override
            protected void dispatchToFeed(FeedView manifestation,
                    Map<String, List<Map<String, String>>> data) {
                assert manifestation == FeedView.this;
                renderer.render(data);
                s.release();
            }
            
            @Override
            protected Collection<FeedProvider> getProviders(FeedView manifestation) {
                assert manifestation == FeedView.this;
                return feedProviders;
            }
            
            @Override
            protected Map<Request, Set<FeedProvider>> batchByRequestTime() {
                return adjustEndTimeForRequest(super.batchByRequestTime());
            }
            
            @Override
            protected Map<Request, Set<FeedProvider>> getCurrentIterationRequests(
                            Map<Request, Set<FeedProvider>> fullSpanRequests,
                            Map<Request, Set<FeedProvider>> lastRequests) {
                return getCurrentIterationRequestsImpl(fullSpanRequests, lastRequests, MAX_DATA_POINTS);
            }
            
            @SuppressWarnings("unchecked")
            @Override
            protected void requestCompleted(Map<String, List<Map<String, String>>> values, long startTime, long endTime) {
                Map<String, List<Map<String, String>>> clonedValues = 
                    new HashMap<String, List<Map<String, String>>>(values);
                if (transformer != null) {
                    transformer.transform(clonedValues, startTime, endTime);
                }
                publish(clonedValues);
                values.clear();
                try {
                    while (!isCancelled() && !s.tryAcquire(300, TimeUnit.MILLISECONDS)) {
                        // nothing
                    }
                } catch (InterruptedException ie) {
                    // this may be interrupted if the request is canceled so just return
                    // from this method
                }
            }
        };
        
        worker.execute();
        return worker;
    }
    
    private Request findLastRequest(Set<FeedProvider> provider, Map<Request,Set<FeedProvider>> lastRequests) {
        // simply iterate through the list to find the last request from the feed provider
        Request r = null;
        for (Entry<Request, Set<FeedProvider>> entry:lastRequests.entrySet()) {
            if (entry.getValue() == provider) {
                r = entry.getKey();
                break;
            }
        }
        
        return r;
    }
    
    private Map<Request, Set<FeedProvider>> getCurrentIterationRequestsImpl(
                    Map<Request, Set<FeedProvider>> fullSpanRequests,
                    Map<Request, Set<FeedProvider>> lastRequests, int maxPoints) {
        Map<Request, Set<FeedProvider>> iterationRequests = new LinkedHashMap<Request, Set<FeedProvider>>();
        for (Entry<Request, Set<FeedProvider>> entry: fullSpanRequests.entrySet()) {
            int maxSamplesSecond = getMaxSamplesSecond(entry.getValue());
            long requestInterval = entry.getKey().getEndTime() - entry.getKey().getStartTime();
            double requestSpan = requestInterval/(double)1000;
            double requests = Math.abs((requestSpan * maxSamplesSecond) / maxPoints);
            long chunks = Math.max(1, Double.valueOf(Math.ceil(requests)).longValue());
            long chunkSize = requestInterval / chunks;
            
            Request lastRequest = findLastRequest(entry.getValue(), lastRequests);
            Request fullRangeRequest = entry.getKey();
            if (lastRequests.isEmpty() || (lastRequest == null)) {
                // bootstrap the request, initialize the request to decrement, which is the default
                long start = fullRangeRequest.getStartTime()+1;     
                long end = start-chunkSize;
                if (requestInterval > 0) {
                    end = fullRangeRequest.getStartTime()-1;
                    start = end + chunkSize;
                }
                lastRequest = new Request(start,end);
            }
            
            assert lastRequest != null : "Last request should not be null.";
            
            Request r;
            if (requestInterval != 0) {
                boolean startToEnd = requestInterval > 0;
                if (startToEnd) {
                    assert chunkSize > 0;
                    r = new Request(lastRequest.getEndTime()+1, Math.min(lastRequest.getEndTime()+chunkSize,fullRangeRequest.getEndTime()));
                    if (r.getStartTime() > fullRangeRequest.getEndTime()) {
                        r = null;
                    }
                } else {
                    assert chunkSize < 0;
                    r = new Request(Math.max(lastRequest.getStartTime()+chunkSize, fullRangeRequest.getEndTime()), lastRequest.getStartTime()-1);
                    if (r.getEndTime() < fullRangeRequest.getEndTime()) {
                        r = null;
                    }
                }
            } else {
                r = lastRequests.isEmpty() ? fullRangeRequest:null;
            }
            if (r != null) {
                iterationRequests.put(r, entry.getValue());
            }
        }
        
        return iterationRequests;
    } 
    
    /**
     * This interface defines an arbitrary operation performed on set of feed data. This is intended to be used
     * for both data transformation and rendering.
     */
    public interface RenderingCallback {
        /**
         * Render the supplied feed data. The type of processing is arbitrary and based on the context
         * in which the method is invoked.
         * @param data to use during the operation
         */
        void render(Map<String,List<Map<String,String>>> data);
    }
    
    /**
     * This interface defines an arbitrary transformation on a set of feed data. 
     *
     */
    public interface DataTransformation {
        /**
         * Provide a data transformation on the supplied feed data. The type of processing is arbitrary and based on the context
         * in which the method is invoked.
         * @param data for the parameters in the given time range
         * @param startTime for the data request, this may not correspond to any times in the data set.
         * @param endTime for the data request, this may not correspond to any times in the data set.
         */
        void transform(Map<String, List<Map<String,String>>> data, long startTime, long endTime);
    }
        
    /**
     * Update the view manifestation because of a request to synchronize all views to the
     * given time. The definition of synchronization of time is specific to the view. 
     * @param data retrieved that was available at the syncTime
     * @param syncTime to show in the view
     */
    public abstract void synchronizeTime(Map<String,List<Map<String,String>>> data, long syncTime);

    /**
     * Gets called when time synchronization is over.
     */
    protected void synchronizationDone() {
    }

    /**
     * Show the synchronize time in a view specific manner. BETA.
     * @param time to synchronize across all views, in Unix Epoch time. 
     * @return control or null if time cannot currently be synchronized
     */
    public SynchronizationControl synchronizeTime(long time) {
        return feedPool.synchronizeTime(time);
    }
    
    /**
     * Provide interaction with the synchronization time state. 
     * 
     */
    public interface SynchronizationControl {
        /**
         * Updates the synchronization time.
         * Using this method is preferable to calling {@link #synchronizationDone()} followed by {@link FeedView#synchronizeTime(long)}
         * because it is atomic.
         * @param time new time to synchronize on
         */
        void update(long time);

        /**
         * Complete the time synchronization across views. This must be called in order to
         * restart normal feed activity. 
         */
        void synchronizationDone();
    }
   
    /**
     * Return feed providers current used in this manifestation. An implementation of this method
     * should attempt to use visibility to only request feeds which are currently being used (instead of
     * everything that could potentially be used). This method may be called from multiple threads and thus
     * the implementation should ensure that iterating over the collection will not cause a concurrent modification 
     * exception. The easiest way to achieve this is to create the collection upon each subsequent change so the 
     * collection will only be write once. 
     * @return the feed providers currently visible in this component. 
     */
    public abstract Collection<FeedProvider> getVisibleFeedProviders();
    
    /**
     * A convenience method to retrieve a feed provider from a component. 
     * @param component to extract feed provider from
     * @return FeedProvider instance or null if the component does not support feeds
     */
    public FeedProvider getFeedProvider(AbstractComponent component) {
        return component.getCapability(FeedProvider.class);
    }
    
    /**
     * Clear the display of this manifestation that matches the provided feedProviders.
     * @param feedProviders the feedProviders to be cleared.
     */
    public void clear(Collection<FeedProvider> feedProviders) {
        //
    }
    
    // exposed for white box testing
    FeedRenderingPool getRenderingPool() {
        return feedPool;
    }
    
    /**
     * Get all the active feed manifestations.
     * @return all the active feed manifestations.
     */
    public static Set<FeedView> getAllActiveFeedManifestations() {
        return feedPool.getAllActiveFeedManifestations();
    }
    
    static void resetLastDataRequestTimeToCurrentTime() {
        feedPool.resetLastDataRequestTimeToCurrentTime();
    }
}
