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
package gov.nasa.arc.mct.evaluator.component;

import gov.nasa.arc.mct.evaluator.api.Executor;
import gov.nasa.arc.mct.evaluator.spi.EvaluatorProvider;

import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * The <code>EvaluatorProviderRegistry</code> class is used to monitor <code>EvaluatorProvider</code> instances using declarative 
 * services. This OSGi component does not expose an interface (see OSGI-INF/component.xml) and thus will be usable from other bundles (
 * the class is not exported from this bundle). This class is thread safe as this may be access 
 * from multiple threads and the registry instance must be visible across all threads. 
 *
 */
public class EvaluatorProviderRegistry {
	private static Set<EvaluatorProvider> registry = new ConcurrentSkipListSet<EvaluatorProvider>(
			new Comparator<EvaluatorProvider>() {

				@Override
				public int compare(EvaluatorProvider o1, EvaluatorProvider o2) {
					return o1 == o2 ? 0 : System.identityHashCode(o1) - System.identityHashCode(o2); 
				}
			});

	// this is not a traditional singleton as this class is created by the OSGi declarative services mechanism. 
	
	public static Executor getExecutor(EvaluatorComponent component) {
		EvaluatorData data = component.getData();
		String language = data.getLanguage();
		Executor executor = null;
		// cache here if necessary
		for (EvaluatorProvider provider : registry) {
			if (provider.getLanguage().equals(language)) {
				executor = provider.compile(data.getCode());
				break;
			}
		}
		return executor;
	}
	
	/**
	 * Adds an <code>EvaluatorProvider</code> to the set of managed evaluator providers.
	 * @param EvaluatorProvider provider to use when determining evaluators.
	 */
	public void addProvider(EvaluatorProvider provider) {
		registry.add(provider);
	}
	
	/**
	 * Removes an <code>EvaluatorProvider</code> from the set of managed evaluator providers.
	 * @param EvaluatorProvider provider to remove from the list of active evaluators. 
	 */
	public void removeProvider(EvaluatorProvider provider) {
		registry.remove(provider);
	}
}
