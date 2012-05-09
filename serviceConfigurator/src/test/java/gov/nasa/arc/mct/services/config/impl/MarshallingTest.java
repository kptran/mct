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

import static org.testng.Assert.*;
import gov.nasa.arc.mct.services.config.impl.properties.ArrayProperty;
import gov.nasa.arc.mct.services.config.impl.properties.SimpleProperty;
import gov.nasa.arc.mct.services.config.impl.properties.SimpleValue;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.testng.annotations.Test;

public class MarshallingTest {
	
	@Test
	public void testPropertyMarshalling() throws JAXBException {
		SimpleProperty prop = new SimpleProperty("x", "y");
		
		String s = marshalObject(prop);
		assertTrue(s.contains("<property"));
		assertTrue(s.contains("name=\"x\""));
		assertTrue(s.contains("value=\"y\""));
	}
	
	@Test
	public void testValueMarshalling() throws JAXBException {
		SimpleValue value = new SimpleValue("hello");
		String s = marshalObject(value);
		assertTrue(s.contains("<entry value=\"hello\""));
	}
	
	@Test
	public void testArrayMarshalling() throws JAXBException {
		ArrayProperty array = new ArrayProperty("people");
		array.addEntry("john");
		array.addEntry("martha");
		
		String s = marshalObject(array);
		assertTrue(s.contains("<array name=\"people\""));
		assertTrue(s.contains("<entry value=\"john\""));
		assertTrue(s.contains("<entry value=\"martha\""));
	}
	
	@Test
	public void testServiceMarshalling() throws JAXBException {
		Service service = new Service();
		service.setServiceID("myPID");
		service.getSimpleProps().add(new SimpleProperty("x", "y"));
		ArrayProperty array = new ArrayProperty("people");
		array.addEntry("john");
		array.addEntry("martha");
		service.getArrayProps().add(array);
		
		String s = marshalObject(service);
		assertTrue(s.contains("<service pid=\"myPID\""));
		assertTrue(s.contains("<property"));
		assertTrue(s.contains("name=\"x\""));
		assertTrue(s.contains("value=\"y\""));
		assertTrue(s.contains("<array name=\"people\""));
		assertTrue(s.contains("<entry value=\"john\""));
		assertTrue(s.contains("<entry value=\"martha\""));
	}
	
	protected String marshalObject(Object o) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(ServiceConfiguration.class);
		Marshaller marshaller = context.createMarshaller();
		StringWriter out = new StringWriter();
		marshaller.marshal(o, out);
		return out.toString();
	}
	
	@Test
	public void testSimpleValueUnmarshalling() throws JAXBException {
		SimpleValue value;
		
		value = (SimpleValue) unmarshalString("<entry value='red' />");
		assertEquals(value.getValue(), "red");
	}

	@Test
	public void testSimplePropertyUnmarshalling() throws JAXBException {
		SimpleProperty prop;
		
		prop = (SimpleProperty) unmarshalString("<property name='color' value='red' />");
		assertEquals(prop.getName(), "color");
		assertEquals(prop.getValue(), "red");
	}

	protected Object unmarshalString(String s) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(ServiceConfiguration.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return unmarshaller.unmarshal(new StringReader(s));
	}

}
