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

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Implements a wrapper around a resource bundle which translates ASCII characters
 * to accented characters or Unicode double-width characters. The translation is
 * set by a system property (<code>PSEUDO_BUNDLE_PROPERTY</code>, below).
 * 
 * @author mrose
 *
 */
public class PseudoBundle extends ResourceBundle {
	
	/** The system property that controls whether pseudotranslation is used,
	 * and what mode.
	 */
	public static final String PSEUDO_BUNDLE_PROPERTY = "mct.pseudotranslation";
	
	/** The property value for normal-width pseudotranslation. */
	public static final String PSEUDO_BUNDLE_NORMAL = "normal";
	/** The property value for double-width pseudotranslation. */
	public static final String PSEUDO_BUNDLE_WIDE = "wide";
	
	/** The possible pseudotranslation modes, normal, wide characters, or none. */
	protected enum BundleMode {
		NORMAL,
		WIDE,
		NO_TRANSLATION
	}

	/** The pseudotranslation mode. */
	private BundleMode mode;
	
	/** A message format for bundle keys for which there is no value. */
	protected static final String NO_TRANSLATION_FORMAT = "???{0}???";
	/** A message format for bundle values that we have pseudotranslated. */
	protected static final String TRANSLATED_FORMAT = "\u00AB{0}\u00BB";

	/** The base Unicode character in the double-width ASCII set. */
	protected static final char FULL_WIDTH_ASCII_BASE = '\uFF01';

	/** A map of characters to their translations, for normal-width translation. */
	protected static final Map<Character,Character> normalTranslations = new HashMap<Character,Character>();
	static {
		// Some commented out because they don't appear in all of the
		// fonts Arial, Lucida, and Times.
		normalTranslations.put('!', '\u00A1'); // inverse !
		normalTranslations.put('0', '\u00D8'); // O stroke
		normalTranslations.put('?', '\u00BF'); // inverse ?
		normalTranslations.put('A', '\u00C2'); // A hat
		normalTranslations.put('B', '\u00DF'); // Scharfes Ess
		normalTranslations.put('C', '\u00C7'); // Capital C cedilla
		normalTranslations.put('D', '\u00D0'); // D with bar
		normalTranslations.put('E', '\u00C9'); // E acute
		// F - no suitable translation
		normalTranslations.put('G', '\u0120'); // G with dot above
		normalTranslations.put('H', '\u0124'); // H circumflex
		normalTranslations.put('I', '\u00CE'); // I hat
		normalTranslations.put('J', '\u0134'); // J circumflex
		normalTranslations.put('K', '\u0136'); // K cedilla
		normalTranslations.put('L', '\u0141'); // L with stroke
		// M - no suitable translation
		normalTranslations.put('N', '\u0143'); // N cedilla
		normalTranslations.put('O', '\u00D6'); // O umlaut
		normalTranslations.put('P', '\u00DE'); // Thorn
		// Q - no suitable translation
		normalTranslations.put('R', '\u0158'); // R with caron
		normalTranslations.put('S', '\u0160'); // S with caron
		normalTranslations.put('T', '\u0166'); // T stroke
		normalTranslations.put('U', '\u00DC'); // U umlaut
		// V - no suitable translation
		normalTranslations.put('W', '\u0174'); // W circumflex
		// X - no suitable translation
		normalTranslations.put('Y', '\u00A5'); // Yen symbol
		normalTranslations.put('Z', '\u017B'); // Z with dot above
		normalTranslations.put('a', '\u00E4'); // a umlaut
		normalTranslations.put('b', '\u0253'); // b with hook (!)
		normalTranslations.put('c', '\u00E7'); // c cedilla
		normalTranslations.put('d', '\u0111'); // d with stroke
		normalTranslations.put('e', '\u00E9'); // e acute
		normalTranslations.put('f', '\u0192'); // f with hook
		normalTranslations.put('g', '\u0123'); // g with cedilla above
		normalTranslations.put('h', '\u0125'); // h circumflex
		normalTranslations.put('i', '\u00EF'); // i umlaut
		normalTranslations.put('j', '\u0135'); // j circumflex
		normalTranslations.put('k', '\u0137'); // k cedilla
		normalTranslations.put('l', '\u013C'); // l cedilla
		// m - no suitable translation
		normalTranslations.put('n', '\u00F1'); // n tilde
		normalTranslations.put('o', '\u00F6'); // o umlaut
		normalTranslations.put('p', '\u00FE'); // thorn
		// q - no suitable translation
		normalTranslations.put('r', '\u0159'); // r circumflex
		normalTranslations.put('s', '\u015D'); // s circumflex
		normalTranslations.put('t', '\u0167'); // t stroke
		normalTranslations.put('u', '\u00FC'); // u umlaut
		// v - no suitable translation
		normalTranslations.put('w', '\u0175'); // w circumflex
		normalTranslations.put('x', '\u00D7'); // times
		normalTranslations.put('y', '\u00FD'); // y acute
		normalTranslations.put('z', '\u017C'); // z with dot above
	}

	/**
	 * Return an indication of whether pseudotranslation is enabled.
	 * 
	 * @return true, if pseudotranslation is enabled
	 */
	public static boolean isEnabled() {
		return (getMode() != BundleMode.NO_TRANSLATION);
	}

	/**
	 * Return the current pseudotranslation mode by looking at the system
	 * properties.
	 * 
	 * @return the current pseudotranslation mode
	 */
	protected static BundleMode getMode() {
		// Look up the system property to set the pseudotranslation mode.
		String modeString = System.getProperty(PseudoBundle.PSEUDO_BUNDLE_PROPERTY);
		if (modeString == null) {
			return BundleMode.NO_TRANSLATION;
		} else if (modeString.equals(PSEUDO_BUNDLE_NORMAL)) {
			return BundleMode.NORMAL;
		} else if (modeString.equals(PSEUDO_BUNDLE_WIDE)) {
			return BundleMode.WIDE;
		} else {
			return BundleMode.NO_TRANSLATION;
		}

	}

	/** The bundle we're wrapping. */
	private ResourceBundle baseBundle;

	/**
	 * Wrap a resource bundle with a pseudotranslated bundle in the current pseudotranslation mode.
	 * 
	 * @param baseBundle the bundle to wrap
	 */
	public PseudoBundle(ResourceBundle baseBundle) {
		this.baseBundle = baseBundle;
		this.mode = getMode();
	}

	@Override
	public Enumeration<String> getKeys() {
		return baseBundle.getKeys();
	}

	@Override
	protected Object handleGetObject(String key) {
		try {
			// Try to find the value in the bundle and translate according to the mode.
			Object value = baseBundle.getObject(key);
			String stringValue = (String) value;
			String newValue;
			switch (mode) {
				case NORMAL: newValue = translateNormal(stringValue); break;
				case WIDE: newValue = translateWide(stringValue); break;
				default: return stringValue;
			}

			return MessageFormat.format(TRANSLATED_FORMAT, newValue);
		} catch (MissingResourceException e) {
			// No value for the given key. Return a special string with the
			// property name inside.
			return MessageFormat.format(NO_TRANSLATION_FORMAT, key);
		}
	}

	/**
	 * Convert a string to double-width ASCII characters. The double-width ASCII set
	 * starts at Unicode position 0xFF01 and has '!' through '~', in the same order
	 * as the ASCII set, so we can do arithmetic to convert each character.
	 * 
	 * @param stringValue the value to convert
	 * @return the value converted to double-width ASCII
	 */
	protected String translateWide(String stringValue) {
		boolean inFormat = false; // If true, we're in the middle of a {format} string.
		StringBuffer result = new StringBuffer();
		for (int i=0; i<stringValue.length(); ++i) {
			char c = stringValue.charAt(i);
			
			if (c == '{') {
				inFormat = true;
			}
			
			if (!inFormat && '!'<=c && c<='~') {
				result.append((char) (c - '!' + FULL_WIDTH_ASCII_BASE));
			} else {
				result.append(c);
			}
			
			if (c == '}') {
				inFormat = false;
			}
		}
		return result.toString();
	}

	/**
	 * Convert a string to a normal width pseudotranslation. We use the "normal"
	 * map, defined earlier, to convert each character. Any character not in the
	 * map is left unchanged.
	 * 
	 * @param stringValue the value to convert
	 * @return the pseudotranslated value
	 */
	protected String translateNormal(String stringValue) {
		boolean inFormat = false; // If true, we're in the middle of a {format} string.
		StringBuffer result = new StringBuffer();
		for (int i=0; i<stringValue.length(); ++i) {
			char c = stringValue.charAt(i);
			
			if (c == '{') {
				inFormat = true;
			}
			
			if (!inFormat && normalTranslations.containsKey(c)) {
				result.append(normalTranslations.get(c));
			} else {
				result.append(c);
			}
			
			if (c == '}') {
				inFormat = false;
			}
		}
		return result.toString();
	}

}
