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
package gov.nasa.arc.mct.graphics.view;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.ExtendedProperties;
import gov.nasa.arc.mct.evaluator.api.Evaluator;
import gov.nasa.arc.mct.graphics.brush.Brush;
import gov.nasa.arc.mct.graphics.brush.ClippedFill;
import gov.nasa.arc.mct.graphics.brush.ConditionalBrush;
import gov.nasa.arc.mct.graphics.brush.Fill;
import gov.nasa.arc.mct.graphics.brush.Outline;
import gov.nasa.arc.mct.graphics.brush.ScalingFill;
import gov.nasa.arc.mct.graphics.clip.AxisClip;
import gov.nasa.arc.mct.graphics.shape.RegularPolygon;
import gov.nasa.arc.mct.graphics.state.StateSensitive;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Manages the settings for a GraphicalManifestation
 * @author vwoeltje
 */
public class GraphicalSettings {
	public static final String  NO_EVALUATOR               = "No Evaluator";
	
	
	private GraphicalManifestation manifestation;
	
	private static Map<String, Shape>        shapeMap;
	private static Map<String, Color>        colorMap;
	private static Map<String, String>       defaultMap;
	private static Map<String, FillProvider> fillMap;
	
	private        Map<Object, String>       reverseMap;	
	private        Map<String, Object>       evaluatorMap;
	private        Map<Object, List<String>> enumerationMap; //Maps eval name to possible states
	
		
	public GraphicalSettings (GraphicalManifestation manifestation) {
		this.manifestation = manifestation;
		
		initializeEvaluatorMap();	
		reverseMap = new HashMap<Object, String>();
		addToReverseMap(shapeMap);
		addToReverseMap(colorMap);
		addToReverseMap(evaluatorMap);
	}	
	
	/* Setting names for persistence */
	//TODO shorten names
	public static final String  GRAPHICAL_SHAPE            = "GraphicalShapeSetting";	
	public static final String  GRAPHICAL_BACKGROUND_COLOR = "GraphicalBackgroundColor";
	public static final String  GRAPHICAL_OUTLINE_COLOR    = "GraphicalOutlineColor";
	public static final String  GRAPHICAL_OUTLINE_WEIGHT   = "GraphicalOutlineWeight";
	public static final String  GRAPHICAL_FOREGROUND_FILL  = "GraphicalFillStyle";
	public static final String  GRAPHICAL_FOREGROUND_COLOR = "GraphicalFillColor";
	public static final String  GRAPHICAL_FOREGROUND_MIN   = "GraphicalFillMinimum";
	public static final String  GRAPHICAL_FOREGROUND_MAX   = "GraphicalFillMaximum";
	public static final String  GRAPHICAL_EVALUATOR        = "GraphicalEvaluator";
	public static final String  GRAPHICAL_EVALUATOR_MAP    = "GraphicalEvaluatorMap";

	public static final String  DEFAULT_SHAPE              = "Ellipse";	
	public static final String  DEFAULT_BACKGROUND_COLOR   = "Color0";
	public static final String  DEFAULT_OUTLINE_COLOR      = "Color1";
	public static final String  DEFAULT_OUTLINE_WEIGHT     = "10";
	public static final String  DEFAULT_FOREGROUND_FILL    = "East";
	public static final String  DEFAULT_FOREGROUND_COLOR   = "Color2";
	public static final String  DEFAULT_FOREGROUND_MIN     = "0";
	public static final String  DEFAULT_FOREGROUND_MAX     = "100";
	public static final String  DEFAULT_EVALUATOR          = NO_EVALUATOR;
	public static final String  DEFAULT_EVALUATOR_MAP      = "";
	
	/* Initialize maps */	
	static {
		shapeMap = new LinkedHashMap<String, Shape>();
		shapeMap.put("Ellipse",        new Ellipse2D.Float(0, 0, 1, 1));
		shapeMap.put("Rectangle",      new Rectangle2D.Float(1, 1, 2, 2) );
		shapeMap.put("RoundRectangle", new RoundRectangle2D.Float(1.0f, 1.0f, 1,1, 0.5f, 0.5f) );		
		for (int i = 3; i <= 6; i++) {
			shapeMap.put("Polygon" + i, new RegularPolygon(i));
		}
		
		/* TODO - this needs to be color schemed */
		/* Most colors taken from canvas ControlareaFormattingConstants */
		colorMap = new LinkedHashMap<String, Color>();
		colorMap.put("Color0", Color.DARK_GRAY.darker());
		colorMap.put("Color1", Color.DARK_GRAY);
		colorMap.put("Color2", Color.BLUE);
		colorMap.put("Color3", Color.RED);
		colorMap.put("Color4", Color.green);
		colorMap.put("Color5", new Color(000, 128, 000));
		colorMap.put("Color6", new Color(032, 179, 170));
		colorMap.put("Color7", new Color(152, 251, 152));
		colorMap.put("Color8", new Color(255, 140, 000));
		colorMap.put("Color9", new Color(255, 000, 255));
		colorMap.put("Color10", new Color(255, 69, 000));
		colorMap.put("Color11", new Color(255, 215, 000));
		colorMap.put("Color12", new Color(047, 79, 79));
		colorMap.put("Color13", new Color(128, 128, 128));
		colorMap.put("Color14", new Color(100, 149, 237));
		colorMap.put("Color15", new Color(000, 49, 042));
		colorMap.put("Color16", new Color(000, 176, 176));
		colorMap.put("Color17", new Color(102, 051, 255));
			
		fillMap = new LinkedHashMap<String, FillProvider>();
		fillMap.put("Static",    new FillProvider());
		fillMap.put("North",     new ClippedFillProvider(AxisClip.Y_AXIS, AxisClip.DECREASING));
		fillMap.put("South",     new ClippedFillProvider(AxisClip.Y_AXIS, AxisClip.INCREASING));
		fillMap.put("East",      new ClippedFillProvider(AxisClip.X_AXIS, AxisClip.INCREASING));
		fillMap.put("West",      new ClippedFillProvider(AxisClip.X_AXIS, AxisClip.DECREASING));
		fillMap.put("Expanding", new ExpandingFillProvider());		
		
		defaultMap = new HashMap<String, String>();		 
		defaultMap.put(GRAPHICAL_SHAPE,            DEFAULT_SHAPE);
		defaultMap.put(GRAPHICAL_BACKGROUND_COLOR, DEFAULT_BACKGROUND_COLOR);
		defaultMap.put(GRAPHICAL_OUTLINE_COLOR,    DEFAULT_OUTLINE_COLOR);
		defaultMap.put(GRAPHICAL_OUTLINE_WEIGHT,   DEFAULT_OUTLINE_WEIGHT);
		defaultMap.put(GRAPHICAL_FOREGROUND_FILL,  DEFAULT_FOREGROUND_FILL);
		defaultMap.put(GRAPHICAL_FOREGROUND_COLOR, DEFAULT_FOREGROUND_COLOR);
		defaultMap.put(GRAPHICAL_FOREGROUND_MIN,   DEFAULT_FOREGROUND_MIN);
		defaultMap.put(GRAPHICAL_FOREGROUND_MAX,   DEFAULT_FOREGROUND_MAX);
		defaultMap.put(GRAPHICAL_EVALUATOR,        DEFAULT_EVALUATOR);
		defaultMap.put(GRAPHICAL_EVALUATOR_MAP,    DEFAULT_EVALUATOR_MAP);
	}
	
	private void initializeEvaluatorMap() {
		evaluatorMap = new LinkedHashMap<String, Object>();
		evaluatorMap.put(NO_EVALUATOR, NO_EVALUATOR);
		
		enumerationMap = new HashMap<Object, List<String>>();
		enumerationMap.put(NO_EVALUATOR, Collections.<String> emptyList());
		
		AbstractComponent component = manifestation.getManifestedComponent();
		
		addEvaluator(component);
	
		if (component.getMasterComponent() != null) {
			component = component.getMasterComponent();
		}
		
		for (AbstractComponent parent : component.getReferencingComponents()) {
			addEvaluator(parent);
		}	
	}
	
	private void addEvaluator(AbstractComponent comp) {
		Evaluator evaluator = comp.getCapability(Evaluator.class);
		if (evaluator == null) return;
		
		// Can only handle enum & imars/enum ...
		String language = evaluator.getLanguage();		
		if (!(language.equals("enum") || language.equals("imars/enum"))) {
			return;
		}
		
		// Track possible enumerations
		List<String> enumerations = new ArrayList<String> ();		
		StringTokenizer codes = new StringTokenizer(evaluator.getCode(), "\t");
		
		while (codes.hasMoreTokens()) {
			String code = codes.nextToken();
			int skip = 0;
			if (language.equals("enum")) skip = 2; // Skip first two terms
			if (language.equals("imars/enum")) skip = 1; // Skip first token only
			while (skip-- > 0) {
				if (code.contains(" ")) 
					code = code.substring(code.indexOf(" ") + 1);
				else
					code = "";
			}
			if (code != "") {
				enumerations.add(code);
			}
		}		
				
		evaluatorMap.put(comp.getDisplayName(), comp);		
		enumerationMap.put(comp, enumerations);		
	}
	
	private void addToReverseMap(Map<String, ?> map) {
		for (Entry<String, ?> e : map.entrySet()) {
			reverseMap.put(e.getValue(), e.getKey());
		}		
	}
	
	/**
	 * Get a named object (for instance, a shape from the settings shape map, or 
	 * color from the settings color map)
	 * @param name the name of the object
	 * @return the object so named
	 */
	public Object getNamedObject(String name) {
		if (shapeMap.containsKey(name)) return shapeMap.get(name);
		if (colorMap.containsKey(name)) return colorMap.get(name);
		if (fillMap.containsKey(name))  return name; // Fill names are mapped only internally
		return null;
	}
	
	/**
	 * Get the list of brushes which, when drawn, will reflect current settings.
	 * @return a list of brushes
	 */
	public List<Brush> getLayers() {
		List<Brush> brushes = new ArrayList<Brush>();
		
		brushes.add(new Fill((Color) getSetting(GRAPHICAL_BACKGROUND_COLOR)));
		FillProvider provider = fillMap.get((String) getSetting(GRAPHICAL_FOREGROUND_FILL));
		Object evaluator = getSetting(GRAPHICAL_EVALUATOR);
		if (evaluator == NO_EVALUATOR) {
			Brush b = provider.getFill((Color) getSetting(GRAPHICAL_FOREGROUND_COLOR));
			Double minimum = Double.parseDouble((String) getSetting(GRAPHICAL_FOREGROUND_MIN));
			Double maximum = Double.parseDouble((String) getSetting(GRAPHICAL_FOREGROUND_MAX));
			if (b instanceof StateSensitive) ((StateSensitive) b).setInterval(minimum, maximum);
			brushes.add(b);
		} else {
			// Build evaluator brushes
			Object evMapSetting = getSetting(GRAPHICAL_EVALUATOR_MAP);
			if (evMapSetting instanceof Map) {
				Map evMap = (Map) evMapSetting;
				for (Object entry : evMap.entrySet()) {
					if (entry instanceof Map.Entry) {
						Object key = ((Map.Entry) entry).getKey();
						Object value = ((Map.Entry) entry).getValue();
						if (value instanceof Color && key instanceof String) {
							Fill b = provider.getFill((Color) value);
							Double minimum = Double.parseDouble((String) getSetting(GRAPHICAL_FOREGROUND_MIN));
							Double maximum = Double.parseDouble((String) getSetting(GRAPHICAL_FOREGROUND_MAX));
							b.setInterval(minimum, maximum);
							brushes.add(new ConditionalBrush(b, (String) key));
						}
					}
				}
			}
		}
		brushes.add(new Outline((Color) getSetting(GRAPHICAL_OUTLINE_COLOR)));

		return brushes;
	}
	
	/**
	 * Apply the current settings to the managed view.
	 */
	public void updateManifestation() {
		manifestation.buildFromSettings();
		manifestation.getManifestedComponent().save(manifestation.getInfo());
	}		

	/**
	 * Get all shapes that are supported by settings
	 * @return a collection of available shapes
	 */
	public Collection<Shape> getSupportedShapes() {
		return shapeMap.values();
	}	
	
	/**
	 * Get all colors which are supported by settings
	 * @return a collection of available colors
	 */
	public Collection<Color> getSupportedColors() {
		return colorMap.values();
	}		

	/**
	 * Get all evaluators which are supported by these settings
	 * @return a collection of evaluators available for the viewed component
	 */
	public Collection<Object> getSupportedEvaluators() {
		return evaluatorMap.values();
	}	
		
	/**
	 * Get the names of all fill types which are supported by these settings
	 * @return a set of names of available fills
	 */
	public Set<String> getSupportedFills() {
		return fillMap.keySet();
	}
	
	/**
	 * 
	 * @param component the component which provides the evaluator
	 * @return a collection of known possible evaluation outputs 
	 */
	public Collection<String> getSupportedEnumerations() {
		return enumerationMap.get(getSetting(GRAPHICAL_EVALUATOR));
	}
	
	/**
	 * Determine whether or not the specified key is the name of a setting
	 * @param key the name to check
	 * @return true if this is the name of a setting, false if not
	 */
	public boolean isValidKey(String key) {
		return defaultMap.containsKey(key);
	}
	
	/**
	 * Gets the current setting associated with the specified key, in its 
	 * String form. If it has not been set in a manifestation, the default will 
	 * be applied and returned.
	 * @param key the name of the setting
	 * @return the current value, as a string
	 */
	private String get(String key) {
		String value = manifestation.getViewProperties().getProperty(key, String.class);
		if (value == null) {
			if (!isValidKey(key)) return null;
			set (key, defaultMap.get(key));
			value = defaultMap.get(key);
		}
		return value;
	}
	
	/**
	 * Set the value for a specific setting. (In its string form - direct 
	 * to the manifestation.) 
	 * @param key the name of the setting
	 * @param value the new value
	 */
	private void set(String key, String value) {
		ExtendedProperties viewProperties = manifestation.getViewProperties();
		viewProperties.setProperty(key, value);
	}
	
	/**
	 * Get the object represented by current settings. 
	 * @param name the name of the settings
	 * @return the object which represents the current choice
	 */
	public Object getSetting (String name) {
		String choice = get(name);
		if (shapeMap.containsKey(choice))         return shapeMap.get(choice);
		if (colorMap.containsKey(choice))         return colorMap.get(choice);
		if (fillMap.containsKey(choice))          return choice; //Not remapped
		if (evaluatorMap.containsKey(choice))     return evaluatorMap.get(choice);		
		if (name.equals(GRAPHICAL_EVALUATOR_MAP)) return decodeMap(choice);
		else                                      return choice;
	}
	
	/**
	 * Set the value of a setting to correspond with the given Object. Note that 
	 * this does nothing if the Object is not supported 
	 * @param key the name of the setting
	 * @param value the object to make the current setting
	 */
	public void setByObject (String key, Object value) {
		if (reverseMap.containsKey(value)) {
			set(key, reverseMap.get(value));
		} else if (value instanceof String) { // Fill just publishes keys
			set(key, (String) value);
		} else if (value instanceof Map) {
			set(key, encodeMap((Map) value));
		}
	}
	
	/**
	 * Turns a string, as stored in settings ("OK\tColor0\nFAIL\tColor1\n", for example)
	 * into a corresponding map.
	 * @param str the string form of the map, as made by encodeMap
	 * @return a Map object representing the string->color mappings
	 */
	private Map<String, Color> decodeMap (String str) {
		Map<String, Color> map = new LinkedHashMap<String, Color>();
		
		StringTokenizer mapTokens = new StringTokenizer(str, "\n");
		while (mapTokens.hasMoreTokens()) {
			String pair = mapTokens.nextToken();
			if (!pair.isEmpty()) {
				StringTokenizer entryTokens = new StringTokenizer(pair, "\t");
				if (entryTokens.countTokens() == 2) {
					String key = entryTokens.nextToken();
					Color  value = colorMap.get(entryTokens.nextToken());
					map.put(key, value);
				}
			}
		}
		
		return map;
	}
	
	/**
	 * Turns a map of Strings to Colors into a string representation 
	 * ("OK=>Color0;FAIL=>Color1;") for storage in settings
	 * @param map the evaluation->color mappings
	 * @return a string describing the same map
	 */
	private String encodeMap (Map map) {
		StringBuilder s = new StringBuilder();
		
		Set keySet = map.keySet();
		for (Object plainKey : keySet) {
			if (plainKey instanceof String) {
				String key   = (String) plainKey;
				String value = reverseMap.get(map.get(key));
				if (value != null) {
					s.append(key);
					s.append("\t");
					s.append(value);
					s.append("\n");
				}
			}
		}
		
		return s.toString();
	}
	
	private static class FillProvider {
		public Fill getFill(Color color) {
			return new Fill(color);
		}
	}
	
	private static class ExpandingFillProvider extends FillProvider {
		@Override
		public Fill getFill(Color color) {
			return new ScalingFill(color);
		}
	}

	private static class ClippedFillProvider extends FillProvider {
		private int axis;
		private int direction;	
				
		public ClippedFillProvider(int axis, int direction) {
			this.axis = axis;
			this.direction = direction;
		}
		
		@Override
		public Fill getFill(Color color) {
			return new ClippedFill(color, axis, direction);
		}
	}
}
