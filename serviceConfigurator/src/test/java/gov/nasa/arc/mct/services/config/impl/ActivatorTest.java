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
package gov.nasa.arc.mct.services.config.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Dictionary;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ActivatorTest {

	private Logger oldLog;
	@Mock private Logger log;
	
	@Mock private ConfigurationAdmin cm;
	@Mock private BundleContext bc;
	@Mock private Filter filter;

	private Activator activator;
	
	@BeforeMethod
	public void setup() throws InvalidSyntaxException {
		oldLog = Activator.log;
		
		MockitoAnnotations.initMocks(this);

		Activator.log = this.log;
		activator = new Activator();

		// The framework may need to create a filter to match
		// our service tracker.
		when(bc.createFilter(isA(String.class))).thenReturn(filter);
	}

	@AfterMethod
	public void cleanup() {
		Activator.log = oldLog;
	}
	
	@Test
	public void testEmptyConfiguration() {
		ServiceConfiguration serviceConfig = new ServiceConfiguration();
		
		Activator.configureServices(cm, serviceConfig);
		Activator.configureFactories(cm, serviceConfig);
		
		// Shouldn't need to talk to ConfigurationAdmin at all.
		verifyZeroInteractions(cm);
	}
	
	@Test
	public void testSimpleConfig() throws IOException {
		ServiceConfiguration serviceConfig = getSimpleConfig();
		
		Configuration standaloneConfig = mock(Configuration.class);
		Configuration instanceConfig = mock(Configuration.class);
		
		when(cm.getConfiguration("standaloneID", null)).thenReturn(standaloneConfig);
		when(cm.createFactoryConfiguration("factoryID", null)).thenReturn(instanceConfig);
		
		Activator.configureServices(cm, serviceConfig);
		Activator.configureFactories(cm, serviceConfig);
		
		verify(cm).getConfiguration("standaloneID", null);
		verify(standaloneConfig).update(eq(serviceConfig.getServices().get(0).getProperties()));
		
		verify(cm).createFactoryConfiguration("factoryID", null);
		verify(instanceConfig).update(eq(serviceConfig.getFactories().get(0).getServices().get(0).getProperties()));
	}

	@Test
	public void testStandaloneConfigWithException() throws IOException {
		ServiceConfiguration serviceConfig = getSimpleConfig();
		
		when(cm.getConfiguration("standaloneID", null)).thenThrow(new IOException());
		
		Activator.configureServices(cm, serviceConfig);
		
		// Should have 1 error log message.
		verify(log).error(anyString(), anyString(), any(Exception.class));
	}
	
	@Test
	public void testInstanceConfigWithException() throws IOException {
		ServiceConfiguration serviceConfig = getSimpleConfig();
		
		when(cm.createFactoryConfiguration("factoryID", null)).thenThrow(new IOException());
		
		Activator.configureFactories(cm, serviceConfig);
		
		// Should have 1 error log message.
		verify(log).error(anyString(), anyString(), any(Exception.class));
	}
	
	protected ServiceConfiguration getSimpleConfig() {
		Service standalone = new Service();
		standalone.setServiceID("standaloneID");
		
		Service instance = new Service();
		instance.setServiceID("instanceID");
		
		ServiceFactory f = new ServiceFactory();
		f.setFactoryID("factoryID");
		f.getServices().add(instance);
		
		ServiceConfiguration serviceConfig = new ServiceConfiguration();
		serviceConfig.getServices().add(standalone);
		serviceConfig.getFactories().add(f);
		return serviceConfig;
	}
	
	@Test
	public void testStartStop() throws Exception {
		activator.start(bc);
		activator.stop(bc);
		
		// We shouldn't have used ConfigurationAdmin for anything, since
		// it wasn't added as a service yet.
		verifyZeroInteractions(cm);
	}

	/**
	 * This tests whether we handle the case when ConfigurationAdmin gets
	 * added as a service, but then goes away before we can convert the
	 * service reference into an object. (An unlikely event, though legal.)
	 * @throws Exception
	 */
	@Test
	public void testAddingCMServiceWhenGoesAway() throws Exception {
		ServiceReference reference = mock(ServiceReference.class);
		
		when(bc.getService(reference)).thenReturn(null); // Don't really need this.
		
		activator.start(bc);
		activator.getCmTracker().addingService(reference);
		
		verifyZeroInteractions(cm);
	}	

	@Test
	public void testAddingCMService() throws Exception {
		ServiceReference reference = mock(ServiceReference.class);
		Configuration config = mock(Configuration.class);
		
		when(bc.getService(reference)).thenReturn(cm);
		when(cm.getConfiguration("gov.nasa.arc.mct.extsvsmgr.adapter.isp",null)).thenReturn(config);
		
		activator.start(bc);
		activator.getCmTracker().addingService(reference);
		
		verify(cm).getConfiguration("gov.nasa.arc.mct.extsvsmgr.adapter.isp",null);
		verify(config).update(isA(Dictionary.class));
	}

}
