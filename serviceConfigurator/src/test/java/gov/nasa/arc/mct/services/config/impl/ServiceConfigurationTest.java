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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import gov.nasa.arc.mct.services.config.impl.properties.ArrayProperty;
import gov.nasa.arc.mct.services.config.impl.properties.SimpleProperty;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Dictionary;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ServiceConfigurationTest {

	@Test(groups={"all"})
	public void testToXML() throws Exception {
		ServiceConfiguration config = new ServiceConfiguration();
		String xml;

		xml = toXML(config);
		assertEqualsXML(xml, "<service-configuration />");

		config.getServices().clear();
		Service s = new Service();
		s.setServiceID("servicePID");
		config.getServices().add(s);
		xml = toXML(config);
		assertEqualsXML(
				xml,
				"<service-configuration>" +
				"  <service pid='servicePID' />" +
				"</service-configuration>"
		);

		s.getSimpleProps().add(new SimpleProperty("host", "localhost"));
		s.getSimpleProps().add(new SimpleProperty("port", "7654", "Integer"));
		xml = toXML(config);
		assertEqualsXML(
				xml,
				"<service-configuration>\n" +
				"  <service pid='servicePID'>\n" +
				"    <property name='host' value='localhost' />\n" +
				"    <property name='port' value='7654' type='Integer' />\n" +
				"  </service>\n" +
				"</service-configuration>\n"
		);

		s.getArrayProps().add(new ArrayProperty("angles", "Integer"));
		s.getArrayProps().get(0).addEntry("90");
		s.getArrayProps().get(0).addEntry("45");
		xml = toXML(config);
		assertEqualsXML(
				xml,
				"<service-configuration>\n" +
				"  <service pid='servicePID'>\n" +
				"    <property name='host' value='localhost' />\n" +
				"    <property name='port' value='7654' type='Integer' />\n" +
				"    <array name='angles' type='Integer'>\n" +
				"      <entry value='90' />\n" +
				"      <entry value='45' />\n" +
				"    </array>\n" +
				"  </service>\n" +
				"</service-configuration>\n"
		);

		config.getServices().clear();

		ServiceFactory f = new ServiceFactory();
		config.getFactories().add(f);
		xml = toXML(config);
		assertEqualsXML(xml, "<service-configuration><service-factory/></service-configuration>");

		Service s2 = new Service();
		s2.setServiceID("servicePID");
		s2.getArrayProps().add(new ArrayProperty("colors"));
		s2.getArrayProps().get(0).addEntry("blue");
		config.getServices().add(s2);
		
		f.setFactoryID("factoryPID");
		s.setServiceID(null);
		f.getServices().add(s);
		xml = toXML(config);
		assertEqualsXML(
				xml,
				"<service-configuration>\n" +
				"  <service pid='servicePID'>\n" +
				"    <array name='colors'>\n" +
				"      <entry value='blue' />\n" +
				"    </array>\n" +
				"  </service>\n" +
				" <service-factory pid='factoryPID'>\n" +
				"  <service>\n" +
				"    <property name='host' value='localhost' />\n" +
				"    <property name='port' value='7654' type='Integer' />\n" +
				"    <array name='angles' type='Integer'>\n" +
				"      <entry value='90' />\n" +
				"      <entry value='45' />\n" +
				"    </array>\n" +
				"  </service>\n" +
				" </service-factory>\n" +
				"</service-configuration>\n"
		);
	}

	@SuppressWarnings("unchecked")
	@Test(groups={"all"})
	public void testFromXML() throws Exception {
		ServiceConfiguration config;
		Dictionary d;

		config = fromXML("<service-configuration />");
		assertEquals(config.getServices().size(), 0);
		assertEquals(config.getFactories().size(), 0);

		config = fromXML(
				"<service-configuration>\n" +
				"  <service pid='servicePID' />\n" +
				"  <service-factory pid='factoryPID' />\n" +
				"</service-configuration>\n"
		);
		assertEquals(config.getServices().size(), 1);
		assertEquals(config.getFactories().size(), 1);
		d = config.getServices().get(0).getProperties();
		assertNotNull(d);
		assertEquals(d.size(), 1); // Just the service.pid
		assertEquals(d.get("service.pid"), "servicePID");
		
		assertEquals(config.getFactories().size(), 1);
		assertEquals(config.getFactories().get(0).getServices().size(), 0);

		config = fromXML(
				"<service-configuration>\n" +
				"  <service pid='servicePID'>\n" +
				"    <property name='color' value='red' />\n" +
				"    <property name='opacity' value='97.5' type='Double' />\n" +
				"    <array name='countries' />\n" +
				"  </service>\n" +
				"</service-configuration>\n"
		);
		assertEquals(config.getServices().size(), 1);
		assertEquals(config.getFactories().size(), 0);
		d = config.getServices().get(0).getProperties();
		assertNotNull(d);
		assertTrue(d.get("color") instanceof String);
		assertEquals(d.get("color"), "red");
		assertTrue(d.get("opacity") instanceof Double);
		assertEquals(((Double) d.get("opacity")).doubleValue(), 97.5, 0.0001);
		assertNotNull(d.get("countries"));
		assertSame(d.get("countries").getClass(), String[].class);

		config = fromXML(
				"<service-configuration>\n" +
				"  <service pid='servicePID'>\n" +
				"    <property name='host' value='localhost' />\n" +
				"    <array name='temperatures' type='Float'>\n" +
				"      <entry value='32.0' />\n" +
				"    </array>\n" +
				"  </service>\n" +
				" <service-factory pid='factoryPID'>\n" +
				"  <service>\n" +
				"    <property name='host' value='otherhost' />\n" +
				"  </service>\n" +
				" </service-factory>\n" +
				"</service-configuration>\n"
		);

		assertEquals(config.getServices().size(), 1);
		assertEquals(config.getServices().get(0).getServiceID(), "servicePID");

		d = config.getServices().get(0).getProperties();
		assertEquals(d.get("host"), "localhost");
		assertNotNull(d.get("temperatures"));
		assertSame(d.get("temperatures").getClass(), Float[].class);
		assertEquals(((Float[]) d.get("temperatures")).length, 1);
		assertEquals(((Float[]) d.get("temperatures"))[0], new Float(32.0));

		assertEquals(config.getFactories().size(), 1);
		assertEquals(config.getFactories().get(0).getFactoryID(), "factoryPID");
		assertEquals(config.getFactories().get(0).getServices().size(), 1);

		d = config.getFactories().get(0).getServices().get(0).getProperties();
		assertEquals(d.get("host"), "otherhost");
	}
	
	protected static String toXML(ServiceConfiguration config) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(ServiceConfiguration.class);
		Marshaller marshaller = context.createMarshaller();
		StringWriter out = new StringWriter();
		
		marshaller.marshal(config, out);
		return out.toString();
	}

	protected static ServiceConfiguration fromXML(String xml) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(ServiceConfiguration.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return (ServiceConfiguration) unmarshaller.unmarshal(new StringReader(xml));
	}

	@Test(dataProvider="xmlEqualsTests")
	public void testEqualsXML(String s1, String s2) {
		assertEqualsXML(s1, s2);
	}
	
	@DataProvider(name="xmlEqualsTests")
	protected Object[][] getXMLEqualsTests() {
		return new Object[][] {
				new Object[]{ "<doc />", "<doc/>" },
				new Object[]{ "<doc a='b' x='y' />", "<doc x='y' a='b' />" },
				new Object[]{ "<doc><a /><b /></doc>", "<doc ><a/>   <b/>   </doc>" },
				new Object[]{ "<doc>hello</doc>", "<doc>    hello    </doc>" }
		};
	}
	
	@Test(dataProvider="xmlNotEqualsTests", expectedExceptions={AssertionError.class})
	public void testNotEqualsXML(String s1, String s2) {
		assertEqualsXML(s1, s2);
	}
	
	@DataProvider(name="xmlNotEqualsTests")
	protected Object[][] getXMLNotEqualsTests() {
		return new Object[][] {
				new Object[]{ "<doc />", "<doc a='b'/>" },
				new Object[]{ "<doc a='b' x='y' />", "<doc x='y' />" },
				new Object[]{ "<doc><a /><b /></doc>", "<doc >  <a/>   </doc>" },
				new Object[]{ "<doc><a /><b /></doc>", "<doc ><b /><a /></doc>" },
				new Object[]{ "<doc>hello</doc>", "<doc>      </doc>" }
		};
	}
	
	protected Document parseXML(String s) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);
		factory.setCoalescing(true);
		factory.setExpandEntityReferences(true);
		factory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new InputSource(new StringReader(s)));
	}

	protected void assertEqualsXML(String s1, String s2) throws AssertionError {
		Document doc1;
		Document doc2;
		try {
			doc1 = parseXML(s1.replaceAll(">[ \r\n\t][ \r\n\t]*<", "><"));
		} catch (Exception e) {
			throw new AssertionError("Cannot parse as XML");
		}
		try {
			doc2 = parseXML(s2.replaceAll(">[ \r\n\t][ \r\n\t]*<", "><"));
		} catch (Exception e) {
			throw new AssertionError("Cannot parse as XML");
		}
		
		assertEqualsXML(doc1.getDocumentElement(), doc2.getDocumentElement());
	}
	
	protected void assertEqualsXML(Element e1, Element e2) throws AssertionError {
		if (!e1.getNodeName().equals(e2.getNodeName())) {
			throw new AssertionError("Exception element <" + e2.getNodeName() + "> but was <" + e1.getNodeName() + ">");
		}
		
		NamedNodeMap attr1 = e1.getAttributes();
		NamedNodeMap attr2 = e2.getAttributes();
		if (attr1.getLength() != attr2.getLength()) {
			throw new AssertionError("Attributes of element <" + e1.getNodeName() + "> differ in length");
		}
		for (int i=0; i<attr1.getLength(); ++i) {
			String attrName = attr1.item(i).getNodeName();
			if (!e2.hasAttribute(attrName)) {
				throw new AssertionError("Extra attribute '" + attrName + "' on element <" + e1.getNodeName() + ">");
			}
			if (!e1.getAttribute(attrName).equals(e2.getAttribute(attrName))) {
				throw new AssertionError("Attribute '" + attrName + "' values differ: expected '"
						+ e2.getAttribute(attrName)
						+ "' but was '" + e1.getAttribute(attrName) + "'");
			}
		}
		
		NodeList children1 = e1.getChildNodes();
		NodeList children2 = e2.getChildNodes();
		if (children1.getLength() != children2.getLength()) {
			throw new AssertionError("Children of <" + e1.getNodeName() + "> differ in length");
		}
		for (int i=0; i<children1.getLength(); ++i) {
			Node n1 = children1.item(i);
			Node n2 = children2.item(i);
			
			if ((n1 instanceof Element) && (n2 instanceof Element)) {
				assertEqualsXML((Element) n1, (Element) n2);
			} else if ((n1 instanceof Text) && (n2 instanceof Text)) {
				assertEquals(n1.getTextContent().trim(), n2.getTextContent().trim());
			} else {
				throw new AssertionError("Children at index " + i + " of <" + e1.getNodeName() + "> differ in type");
			}
		}
	}

}
