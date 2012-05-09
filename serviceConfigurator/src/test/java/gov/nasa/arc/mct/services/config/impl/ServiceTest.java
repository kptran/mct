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
import static org.mockito.Mockito.verify;
import gov.nasa.arc.mct.services.config.impl.properties.SimpleProperty;

import java.util.ArrayList;
import java.util.List;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ServiceTest {

	private Logger oldLog;
	@Mock private Logger log;
	
	private Service service;
	
	@BeforeMethod
	public void setup() throws InvalidSyntaxException {
		oldLog = Service.log;
		
		MockitoAnnotations.initMocks(this);

		Service.log = this.log;
	}

	@AfterMethod
	public void cleanup() {
		Service.log = oldLog;
	}
	
	@Test 
	public void testBadPropertyTypeConversion() {
		service = new Service();
		List<SimpleProperty> expected = new ArrayList<SimpleProperty> ();
		SimpleProperty sp = new SimpleProperty("Integer","aaaa");
		sp.setTypeName("Integer");
		expected.add(sp);		
		service.setSimpleProps(expected);
		List<SimpleProperty> actual = service.getSimpleProps();
		Assert.assertEquals(actual, expected);
		
		service.getProperties();	
		// Should have 1 error log message.
		verify(log).error(anyString(), anyString(), any(Exception.class));
	}
}
