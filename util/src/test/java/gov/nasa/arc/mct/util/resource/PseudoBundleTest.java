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
package gov.nasa.arc.mct.util.resource;

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class PseudoBundleTest {
	
	private Properties systemProps;
	
	@BeforeTest
	public void saveProperties() {
		systemProps = System.getProperties();
	}
	
	@AfterTest
	public void restoreProperties() {
		System.setProperties(systemProps);
	}

	@Test(expectedExceptions={java.util.MissingResourceException.class})
	public void testNonexistentBundle() throws Exception {
		@SuppressWarnings("unused")
		ResourceBundle bundle = BundleFactory.getBundle("nonexistent-bundle-name");
	}
	
	@Test
	public void testNoProperty() throws Exception {
		System.setProperties(new Properties());
		assertNull(System.getProperty("mct.pseudotranslation"));
		ResourceBundle origBundle = ResourceBundle.getBundle("properties.PseudoBundle");
		ResourceBundle testBundle = BundleFactory.getBundle("properties.PseudoBundle");
		assertTrue(!PseudoBundle.isEnabled(), "Pseudotranslation disabled");
		assertEquals(origBundle.getString("ascii"), testBundle.getString("ascii"));
	}
	
	@Test
	public void testNoTranslation() throws Exception {
		System.setProperty("mct.pseudotranslation", "none");
		ResourceBundle origBundle = ResourceBundle.getBundle("properties.PseudoBundle");
		ResourceBundle testBundle = BundleFactory.getBundle("properties.PseudoBundle");
		assertTrue(!PseudoBundle.isEnabled(), "Pseudotranslation disabled");
		assertEquals(origBundle.getString("ascii"), testBundle.getString("ascii"));
	}
	
	@Test
	public void testNormal() throws Exception {
		System.setProperty("mct.pseudotranslation", "normal");
		ResourceBundle origBundle = ResourceBundle.getBundle("properties.PseudoBundle");
		ResourceBundle testBundle = BundleFactory.getBundle("properties.PseudoBundle");

		String alphabet = origBundle.getString("alphabet");
		String special = origBundle.getString("special");
		String noTranslation = origBundle.getString("no_normal_translation");
		String ascii = origBundle.getString("ascii");
		String translated = testBundle.getString("ascii");
		
		assertTrue(PseudoBundle.isEnabled(), "Pseudotranslation enabled");
		// Pseudotranslation adds 1 character of prefix and suffix.
		assertEquals(ascii.length()+2, translated.length());
		
		for (int i=0; i<ascii.length(); ++i) {
			char c = ascii.charAt(i);
			if (noTranslation.indexOf(c) >= 0) {
				assertEquals(c, translated.charAt(i+1));
			} else if (alphabet.indexOf(c)>=0 || special.indexOf(c)>=0) {
				assertTrue(c != translated.charAt(i+1),
						"Normal translation changes character " + c);
			} else {
				assertEquals(c, translated.charAt(i+1));
			}
		}
	}
	
	@Test
	public void testWide() {
		System.setProperty("mct.pseudotranslation", "wide");
		ResourceBundle origBundle = ResourceBundle.getBundle("properties.PseudoBundle");
		ResourceBundle testBundle = BundleFactory.getBundle("properties.PseudoBundle");

		String ascii = origBundle.getString("ascii");
		String translated = testBundle.getString("ascii");
		
		assertTrue(PseudoBundle.isEnabled(), "Pseudotranslation enabled");
		// Pseudotranslation adds 1 character of prefix and suffix.
		assertEquals(ascii.length()+2, translated.length());
		
		for (int i=0; i<ascii.length(); ++i) {
			char c = ascii.charAt(i);
			assertTrue(c != translated.charAt(i+1),
					"Wide translation changes character " + c);
		}
	}
	
	/** Test that we don't translate anything inside a MessageFormat format string. (That is, {...}) */
	@Test
	public void testNormalFormat() {
		System.setProperty("mct.pseudotranslation", "normal");
		ResourceBundle origBundle = ResourceBundle.getBundle("properties.PseudoBundle");
		ResourceBundle testBundle = BundleFactory.getBundle("properties.PseudoBundle");

		String alphabet = origBundle.getString("alphabet");
		String special = origBundle.getString("special");
		String noTranslation = origBundle.getString("no_normal_translation");
		String messageFormat = origBundle.getString("message_format");
		String translated = testBundle.getString("message_format");
		
		assertTrue(PseudoBundle.isEnabled(), "Pseudotranslation enabled");
		// Pseudotranslation adds 1 character of prefix and suffix.
		assertEquals(messageFormat.length()+2, translated.length());
		
		boolean inFormat = false;
		for (int i=0; i<messageFormat.length(); ++i) {
			char c = messageFormat.charAt(i);
			if (c == '{') {
				inFormat = true;
			}
			
			if (!inFormat && noTranslation.indexOf(c) >= 0) {
				assertEquals(c, translated.charAt(i+1));
			} else if (!inFormat && (alphabet.indexOf(c)>=0 || special.indexOf(c)>=0)) {
				assertTrue(c != translated.charAt(i+1),
						"Normal translation changes character " + c);
			} else {
				assertEquals(c, translated.charAt(i+1));
			}
			
			if (c == '}') {
				inFormat = false;
			}
		}
	}

	@Test
	public void testWideFormat() {
		System.setProperty("mct.pseudotranslation", "wide");
		ResourceBundle origBundle = ResourceBundle.getBundle("properties.PseudoBundle");
		ResourceBundle testBundle = BundleFactory.getBundle("properties.PseudoBundle");

		String messageFormat = origBundle.getString("message_format");
		String translated = testBundle.getString("message_format");
		
		assertTrue(PseudoBundle.isEnabled(), "Pseudotranslation enabled");
		// Pseudotranslation adds 1 character of prefix and suffix.
		assertEquals(messageFormat.length()+2, translated.length());
		
		boolean inFormat = false;
		for (int i=0; i<messageFormat.length(); ++i) {
			char c = messageFormat.charAt(i);

			if (c == '{') {
				inFormat = true;
			}
			
			if (!inFormat && '!'<=c && c<='~') {
				assertTrue(c != translated.charAt(i+1),
						"Wide translation changes character " + c);
			} else {
				assertEquals(c, translated.charAt(i+1));
			}
			
			if (c == '}') {
				inFormat = false;
			}
		}
	}
	
	@Test
	public void testGetKeys() throws Exception {
		ResourceBundle testBundle = BundleFactory.getBundle("properties.PseudoBundle");
		Enumeration<String> e = testBundle.getKeys();
		assertNotNull(e);
		boolean foundAlphabet = false;
		while (e.hasMoreElements()) {
			if ("alphabet".equals(e.nextElement())) {
				foundAlphabet = true;
			}
		}
		assertTrue(foundAlphabet, "Found the 'alphabet' bundle property");
	}
	
	@Test
	public void testDirectCreation() {
		System.setProperty("mct.pseudotranslation", "none");
		ResourceBundle origBundle = new PseudoBundle(ResourceBundle.getBundle("properties.PseudoBundle"));
		ResourceBundle testBundle = BundleFactory.getBundle("properties.PseudoBundle");
		assertTrue(!PseudoBundle.isEnabled(), "Pseudotranslation disabled");
		assertEquals(origBundle.getString("ascii"), testBundle.getString("ascii"));
	}
	
	@Test
	public void testMissingResource() {
		System.setProperty("mct.pseudotranslation", "normal");
		ResourceBundle testBundle = BundleFactory.getBundle("properties.PseudoBundle");
		
		String key = "nonexistent.property";
		String nonexistent = testBundle.getString(key);
		assertEquals(nonexistent.length(), key.length()+(2*3)); // 3 '?' on each end
		assertEquals(nonexistent.substring(3, nonexistent.length()-3), key);
	}
	
	@Test
	public void testBundleFactoryInstantiation() throws Exception {
		BundleFactory factory = new BundleFactory();
		assertNotNull(factory);
	}
	
	@Test
	public void testRStrings() throws Exception {
		System.setProperty("mct.pseudotranslation", "none");
		assertEquals(RStrings.CANCEL, "Cancel");
		
		RStrings r = new RStrings();
		assertNotNull(r);
		
		assertEquals(RStrings.get("CANCEL"), "Cancel");
	}
}
