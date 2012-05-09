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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import gov.nasa.arc.mct.policy.Policy;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.policy.PolicyInfo;
import gov.nasa.arc.mct.registry.ExternalComponentRegistryImpl.ExtendedComponentProvider;
import gov.nasa.arc.mct.services.component.AbstractComponentProvider;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Unit test for the policy manager.
 * @author nija.shi@nasa.gov
 *
 */
public class PolicyManagerTest {
	
	private class TestComponentProvider extends AbstractComponentProvider {
		@Override
		public Collection<PolicyInfo> getPolicyInfos() {
			return Collections.singletonList(new PolicyInfo(MY_POLICY_CATEGORY_KEY, SamplePolicy.class));
		}

	}

	private static final String MY_POLICY_CATEGORY_KEY = "MY_POLICY_CATEGORY";	
	private static final String POLICY_ACTION_KEY = "MY_ACTION";
	static final String POLICY_ACTION_DESC = "do nothing";
	
	private PolicyManagerImpl manager;
	private Policy policy;
	private TestComponentProvider provider;

	/**
	 * Starts up the policy manager and register a policy.
	 */
	@BeforeClass
	public void setUp() {
		// Initialize the policy manager
		manager = PolicyManagerImpl.getInstance();
		
		// Create a policy
		policy = new SamplePolicy();
		
		// Create a component provider
		provider = new TestComponentProvider();
		
		// Register the policy to the policy manager
		manager.refreshExtendedPolicies(Collections.singletonList(new ExtendedComponentProvider(provider, "test")));
	}
	
	 /**
	  * Creates a context and executes the registered policy.
	  */
	 @Test
	 public void test() {
		 assertNotNull(manager);		 
		 assertNotNull(policy);
		 
		 PolicyContext context = new PolicyContext();
		 context.setProperty(POLICY_ACTION_KEY, POLICY_ACTION_DESC);
		 assertTrue(manager.execute(MY_POLICY_CATEGORY_KEY, context).getStatus());
	 }
	 
	 @Test
	 public void testReturnNullFromProvider() {
		 TestComponentProvider badProvider = new TestComponentProvider() {
			 @Override
			public Collection<PolicyInfo> getPolicyInfos() {
				return null;
			}
		 };
		 manager.refreshExtendedPolicies(
				 Arrays.asList(new ExtendedComponentProvider(provider,"test"),
						 	   new ExtendedComponentProvider(badProvider, "test2"))
		 );
		 
		 PolicyContext context = new PolicyContext();
		 context.setProperty(POLICY_ACTION_KEY, POLICY_ACTION_DESC);
		 assertTrue(manager.execute(MY_POLICY_CATEGORY_KEY, context).getStatus());
	 }
	 
	 @Test
	 public void testExceptionFromProvider() {
		 TestComponentProvider badProvider = new TestComponentProvider() {
			 @Override
			public Collection<PolicyInfo> getPolicyInfos() {
				throw new NullPointerException();
			}
		 };
		 manager.refreshExtendedPolicies(
				 Arrays.asList(new ExtendedComponentProvider(provider,"test"),
						 	   new ExtendedComponentProvider(badProvider, "test2"))
		 );
		 
		 PolicyContext context = new PolicyContext();
		 context.setProperty(POLICY_ACTION_KEY, POLICY_ACTION_DESC);
		 assertTrue(manager.execute(MY_POLICY_CATEGORY_KEY, context).getStatus());
	 }

}
