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
package gov.nasa.arc.mct.policymgr;

import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.Policy;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.policy.PolicyInfo;
import gov.nasa.arc.mct.registry.ExternalComponentRegistryImpl.ExtendedComponentProvider;
import gov.nasa.arc.mct.services.component.PolicyManager;
import gov.nasa.arc.mct.util.internal.ElapsedTimer;
import gov.nasa.arc.mct.util.logging.MCTLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides an instance of a {@link PolicyManager}.
 * @author nija.shi@nasa.gov
 */
public final class PolicyManagerImpl implements PolicyManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(PolicyManagerImpl.class);
	private static final MCTLogger PERF_LOGGER = MCTLogger.getLogger("gov.nasa.arc.mct.performance.policies");
	private final Map<String, List<Policy>> map = new HashMap<String, List<Policy>>();
	
	private static final PolicyManagerImpl instance = new PolicyManagerImpl();
	
	private PolicyManagerImpl() {	
	}
	
	public static PolicyManagerImpl getInstance() {
		return instance;
	}
	
	private void register (String categoryKey, Policy policy) {
		List<Policy> list = map.get(categoryKey);
		if (list == null) {
			list = new ArrayList<Policy>();
			map.put(categoryKey, list);
		}
		list.add(policy);
	}
	
	public synchronized void refreshExtendedPolicies(List<ExtendedComponentProvider> providers) {
		// Clear registry.
		map.clear();
		
		// Register extended policies.
		for (ExtendedComponentProvider provider : providers) {
			try {
				if (provider.getPolicyInfos() != null) {
					Collection<PolicyInfo> policyInfos = provider.getPolicyInfos();
					for (PolicyInfo info : policyInfos) {
						Class<?>[] policyClasses = info.getPolicyClasses();
						for (Class<?> policyClass : policyClasses) {
							try {
								register(info.getCategoryKey(), (Policy) policyClass.newInstance());
							} catch (InstantiationException e) {
								LOGGER.error(e.getMessage(), e);
							} catch (IllegalAccessException e) {
								LOGGER.error(e.getMessage(), e);
							} catch (ClassCastException e) {
								LOGGER.error(e.getMessage(), e);
							}
						}
					}
				}
			} catch (Exception e) {
				// if an exception occurs, log an error for the provider to resolve but
				// continue through the rest of the providers
				LOGGER.error("Error occurred while get policies from provider: " + provider.getClass().getName() +
						" from bundle: " + provider.getBundleSymbolicName(), e);
			}
		}
	}
	
	@Override
	public ExecutionResult execute(String categoryKey, PolicyContext context) {
		ExecutionResult result;	
		List<Policy> list = map.get(categoryKey);
		if (list == null)
			return new ExecutionResult(context, true, "No policies registered for " + categoryKey);
		ElapsedTimer categoryTimer = new ElapsedTimer();
		categoryTimer.startInterval();
		for (Policy policy : list) {
			ElapsedTimer policyTimer = new ElapsedTimer();
			policyTimer.startInterval();
			result = policy.execute(context);
			policyTimer.stopInterval();
			PERF_LOGGER.debug("time to execute policy {0} {1}", policy.getClass().getName(), policyTimer.getIntervalInMillis());
			if (!result.getStatus()) {
				LOGGER.debug("Policy category {} failed on policy {}", categoryKey, policy.getClass().getName());
				return result;
			}
		}
		categoryTimer.stopInterval();
		PERF_LOGGER.debug("time to execute policy category {0} {1}", categoryKey, categoryTimer.getIntervalInMillis());
		return new ExecutionResult(context, true, categoryKey + " passed.");
	}
}
