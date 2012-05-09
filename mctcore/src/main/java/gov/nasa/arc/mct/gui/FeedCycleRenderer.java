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
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.util.internal.ElapsedTimer;
import gov.nasa.arc.mct.util.logging.MCTLogger;

import java.util.ArrayList;
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

import javax.swing.SwingWorker;

/**
 * Provide a worker to retrieve feed data for the cycle set in batch (all the
 * views requesting) and render the views in the AWT thread. The feed paint
 * requests happen in batch at the frequency of the paint rate.
 * 
 */
class FeedCycleRenderer extends SwingWorker<Map<String, List<Map<String, String>>>, Map<String, List<Map<String, String>>>> {
    private static final MCTLogger LOGGER = MCTLogger.getLogger(FeedCycleRenderer.class);
    private static final MCTLogger PERF_LOGGER = MCTLogger
                    .getLogger("gov.nasa.arc.mct.performance.feeds.pool");

    private final Set<FeedView> activeFeedViews;
    private final Map<FeedProvider, Long[]> times;
    private final ElapsedTimer dataRequestTimer = new ElapsedTimer();
    private final ElapsedTimer uiRenderingTimer = new ElapsedTimer();

    /**
     * Create a new instance of a worker to retrieve and render feed data.
     * 
     * @param timeMapping
     *            currently in the request set for the feed providers.
     * @param activeViews
     *            views current maintained
     * 
     */
    public FeedCycleRenderer(Map<FeedProvider, Long[]> timeMapping,
                    Set<FeedView> activeViews) {
        activeFeedViews = activeViews;
        times = timeMapping;
    }

    /**
     * Returns the list of the feed providers from a manifestation. The default
     * implementation invokes
     * {@link FeedView#getVisibleFeedProviders()}.
     * 
     * @param manifestation
     *            to retrieve feed providers from
     * @return set of feed providers to retrieve data for
     */
    protected Collection<FeedProvider> getProviders(FeedView manifestation) {
        return manifestation.getVisibleFeedProviders();
    }

    /**
     * Group the requests by the specified time mapping. Overriding methods should delegate to this method and then operate on the returned map.
     * @return the batched set of requests. 
     */
    protected Map<Request, Set<FeedProvider>> batchByRequestTime() {
        // build a set of feed providers
        Set<FeedProvider> allProviders = new HashSet<FeedProvider>();
        for (FeedView manifestation : activeFeedViews) {
            allProviders.addAll(getProviders(manifestation));
        }

        // build a list of requests by time
        Map<Request, Set<FeedProvider>> feedRequests = new HashMap<Request, Set<FeedProvider>>();
        for (FeedProvider provider : allProviders) {
            try {
                Long[] timeMap = times.get(provider);
                if (timeMap == null) {
                    continue;
                }
                Request r = new Request(timeMap[0], timeMap[1]);
                Set<FeedProvider> ids = feedRequests.get(r);
                if (ids == null) {
                    ids = new HashSet<FeedProvider>();
                    feedRequests.put(r, ids);
                }
                ids.add(provider);
            } catch (Exception e) {
                LOGGER.error("exception occurred while creating a request for " + provider, e);
            }
        }

        return feedRequests;
    }

    
    /**
     * This method provides the ability to make requests which are a subset of the full request. 
     * @param fullSpanRequests the set of requests initially issued with the full request span. This list may be mutated.
     * @param lastRequests the last set of requests issued
     * @return the requests for this iteration
     */
    protected Map<Request, Set<FeedProvider>> getCurrentIterationRequests(final Map<Request, Set<FeedProvider>> fullSpanRequests,
                                                                          final Map<Request, Set<FeedProvider>> lastRequests) {
        return lastRequests.isEmpty() ? fullSpanRequests:Collections.<Request,Set<FeedProvider>>emptyMap();
    }
    
    @Override
    protected Map<String, List<Map<String, String>>> doInBackground() {
        dataRequestTimer.startInterval();
        Map<String, List<Map<String, String>>> values = new HashMap<String, List<Map<String, String>>>();
        PERF_LOGGER.debug("size of feed views {0}", activeFeedViews.size());
        try {
            final Map<Request, Set<FeedProvider>> fullSpanRequests = batchByRequestTime();
            Map<Request, Set<FeedProvider>> currentIterationRequests = getCurrentIterationRequests(fullSpanRequests,Collections.<Request,Set<FeedProvider>>emptyMap());
            while (!currentIterationRequests.isEmpty()) {
                Set<Entry<Request, Set<FeedProvider>>> entries = currentIterationRequests.entrySet();
                for (Entry<Request, Set<FeedProvider>> request : entries) {
                    if (isCancelled()) {
                        return Collections.emptyMap();
                    }
                    Request r = request.getKey();
                    Set<String> feedIds = new HashSet<String>();
                    for (FeedProvider provider : request.getValue()) {
                        try {
                            feedIds.add(provider.getSubscriptionId());
                        } catch (Exception e) {
                            LOGGER.error("exception occurred while getting subscription id from "
                                            + provider, e);
                        }
                    }

                    FeedAggregator feedAggregator = PlatformAccess.getPlatform().getFeedAggregator();
                    
                    Map<String, List<Map<String, String>>> data = new HashMap<String, List<Map<String, String>>>();
                    if (feedAggregator != null) { 
                        data = feedAggregator.getData(feedIds, TimeUnit.MILLISECONDS, r.getStartTime(), r.getEndTime());
                    }
                    
                    Map<String, List<Map<String,String>>> adjValues = adjustResponses(data, r.getStartTime());
                    values.putAll(adjValues);
                    requestCompleted(values, r.getStartTime(), r.getEndTime());
                }
                currentIterationRequests = getCurrentIterationRequests(fullSpanRequests, currentIterationRequests);
            }
        } catch (Exception e) {
            LOGGER.error("exception occurred while retrieving data ", e);
        }
        additionalBackgroundProcessing(values);
        dataRequestTimer.stopInterval();

        return values;
    }
    
    /**
     * Iterate through the set of response values to ensure that times which are earlier than the specific time are 
     * moved to the current time.
     * @param values returned from the invocation
     * @param startTime for the request, values which are less than the request time will be adjusted to reflect this time.
     */
    private Map<String, List<Map<String,String>>> adjustResponses(Map<String, List<Map<String,String>>> values, long startTime) {
        for (Entry<String, List<Map<String,String>>> entry: values.entrySet()) {
            // adjust the time for the initial value to be the maximum of the time in the value or the request start time
            List<Map<String,String>> dataValues = entry.getValue();
            // only adjust the value for a request that returns a single data point as this could be the case where the
            // time of the data point is less than the start time, if so then shift the request time to be the value at the
            // start time
            if (dataValues.size() >= 1) {
                Map<String, String> value = dataValues.get(0);
                Long l = Long.parseLong(value.get(FeedProvider.NORMALIZED_TIME_KEY));
                if (l < startTime) {
                    ArrayList<Map<String,String>> dataValues2 = new ArrayList<Map<String,String>>(dataValues);
                    Map<String, String> value2 = new HashMap<String, String>(value);
                    value2.put(FeedProvider.NORMALIZED_TIME_KEY, Long.toString(startTime));
                    dataValues2.set(0, value2);
                    entry.setValue(dataValues2);
                }
            }
        }
        
        return values;
    }

    /**
     * Perform additional background processing before rendering the data.
     * 
     * @param values
     *            to mutate if necessary prior to rendering the data.
     */
    protected void additionalBackgroundProcessing(Map<String, List<Map<String, String>>> values) {
        
    }
    
    /**
     * Perform background processing on each request before going to the next request. The overriding
     * implementation is responsible for adjusting the data structure if necessary. For example, if
     * the implementation is going to be incremental dispatching, using {@link #publish(Object...)}, then
     * the values dispatched should be cleared before the method returns. 
     * @param values that have currently been retrieved.
     * @param startTime used for values
     * @param endTime used for values
     */
    protected void requestCompleted(Map<String, List<Map<String, String>>> values, long startTime, long endTime) {
        
    }
    
    /**
     * Return the requests that should be generated from the existing request list. The expected overriding
     * of this method is for expanding the number of requests (chunking), such that the number of requests
     * increases, but the size of each request decreases. This can help reduce the amount of memory for
     * burst requests. 
     * @param requests the current set of requests
     * @return the possibly expanded set of requests
     */
    protected Map<Request, Set<FeedProvider>> adjustRequests(Map<Request, Set<FeedProvider>> requests) {
        return requests;
    }
    
    @Override
    protected void process(List<Map<String, List<Map<String, String>>>> chunks) {
        if (isCancelled()) {
            LOGGER.debug("swing worker cancel detected in process method");
            return;
        }
        LOGGER.debug("Chunks size {0}", chunks.size());
        
        for (Map<String, List<Map<String, String>>> chunk:chunks) {
            dispatchDataToFeeds(chunk);
        }

        PERF_LOGGER.debug(
                        "feed cycle performance: data retrieval {0}, ui rendering {1}, total {2}",
                        dataRequestTimer.getIntervalInMillis(), uiRenderingTimer
                                        .getIntervalInMillis(), dataRequestTimer
                                        .getIntervalInMillis()
                                        + uiRenderingTimer.getIntervalInMillis());
    }
    
    @Override
    protected void done() {
        if (isCancelled()) {
            LOGGER.debug("swing worker cancel detected in done method");
            return;
        }

        renderFeeds();
        PERF_LOGGER.debug(
                        "feed cycle performance: data retrieval {0}, ui rendering {1}, total {2}",
                        dataRequestTimer.getIntervalInMillis(), uiRenderingTimer
                                        .getIntervalInMillis(), dataRequestTimer
                                        .getIntervalInMillis()
                                        + uiRenderingTimer.getIntervalInMillis());
    }

    /**
     * Returns the data obtained in the background thread.
     * 
     * @return data obtained in the background thread.
     */
    Map<String, List<Map<String, String>>> getData() throws InterruptedException,
                    ExecutionException {
        return get();
    }

    /**
     * Invoked when data has been acquired and is ready for rendering.
     * 
     * @param manifestation
     *            pass the results to
     * @param data
     *            retrieved from the feed
     */
    protected void dispatchToFeed(FeedView manifestation,
                    Map<String, List<Map<String, String>>> data) {
        manifestation.updateFromFeed(data);
    }
    
    private void dispatchDataToFeeds(Map<String, List<Map<String, String>>> data) {
        uiRenderingTimer.startInterval();
        for (FeedView fvm : activeFeedViews) {
            try {
                dispatchToFeed(fvm, data);
            } catch (Exception e) {
                LOGGER.error("exception occurred while invoking updateFromFeed " + fvm, e);
            }
        }
        uiRenderingTimer.stopInterval();
    }

    void renderFeeds() {
        try {
            Map<String, List<Map<String, String>>> data = getData();
            dispatchDataToFeeds(data);
        } catch (Exception e) {
            LOGGER.error("get generated exception, this indicates a problem in the platform", e);
        }
    }

}
