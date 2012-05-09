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
import gov.nasa.arc.mct.graphics.brush.ConditionalBrush;
import gov.nasa.arc.mct.graphics.brush.Fill;
import gov.nasa.arc.mct.graphics.brush.Outline;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;



public class GraphicalSettingsTest {
	private static final String ENUMERATOR_NAME = "Enumerator";

	private GraphicalManifestation mockView;
	private AbstractComponent mockEnumerator;
	@Mock private AbstractComponent mockComponent;
	@Mock private ExtendedProperties mockViewProperties;
	@Mock private Evaluator mockEvaluator;

	
	private GraphicalSettings settings;
	
	@BeforeMethod
	public void setup() {
		MockitoAnnotations.initMocks(this);

		mockView = new GraphicalManifestation(mockComponent, 
				new ViewInfo(GraphicalManifestation.class,"", ViewType.OBJECT)) {
			private static final long serialVersionUID = 6036948129496070106L;

			public ExtendedProperties getViewProperties() {
				return mockViewProperties;
			}
		};
		
		Mockito.when(mockComponent.getCapability(Evaluator.class)).thenReturn(null);
		Mockito.when(mockComponent.getMasterComponent()).thenReturn(null);
		Mockito.when(mockComponent.getReferencingComponents()).thenReturn(Collections.<AbstractComponent>emptyList());

		Mockito.when(mockEvaluator.getDisplayName()).thenReturn("Phony evaluator");
		Mockito.when(mockEvaluator.getLanguage()).thenReturn("enum");
		Mockito.when(mockEvaluator.getCode()).thenReturn("= 0 abc\t> 0 xyz");
		
		mockEnumerator = new AbstractComponent() {

			@SuppressWarnings("unchecked")
			@Override
			protected <T> T handleGetCapability(Class<T> capability) {
				if (capability.isAssignableFrom(mockEvaluator.getClass()))
						return (T) mockEvaluator;
				else
					return null;
			}
			
		};
		mockEnumerator.setDisplayName(ENUMERATOR_NAME);
		
		settings = new GraphicalSettings(mockView);
	}
	
	@SuppressWarnings("unchecked") /* Unchecked is suppressing so we can mock view properties */
	@Test
	public void testDefaults() {
		Mockito.when(mockViewProperties.getProperty(Mockito.anyString(), Mockito.any(Class.class))).thenReturn(null);
		
		Assert.assertEquals(settings.getSetting(GraphicalSettings.GRAPHICAL_BACKGROUND_COLOR), 
							settings.getNamedObject(GraphicalSettings.DEFAULT_BACKGROUND_COLOR));
		Assert.assertEquals(settings.getSetting(GraphicalSettings.GRAPHICAL_FOREGROUND_COLOR), 
				settings.getNamedObject(GraphicalSettings.DEFAULT_FOREGROUND_COLOR));
		Assert.assertEquals(settings.getSetting(GraphicalSettings.GRAPHICAL_FOREGROUND_FILL), 
				settings.getNamedObject(GraphicalSettings.DEFAULT_FOREGROUND_FILL));
		Assert.assertEquals(settings.getSetting(GraphicalSettings.GRAPHICAL_OUTLINE_COLOR), 
				settings.getNamedObject(GraphicalSettings.DEFAULT_OUTLINE_COLOR));
		Assert.assertEquals(settings.getSetting(GraphicalSettings.GRAPHICAL_SHAPE), 
				settings.getNamedObject(GraphicalSettings.DEFAULT_SHAPE));
	}
	
	@SuppressWarnings("unchecked") /* Unchecked is suppressing so we can mock view properties */
	@Test
	public void testLayerProduction() {
		List<Brush> layers;
		
		/* Does it produce regular fills? */ 
		Mockito.when(mockViewProperties.getProperty(Mockito.anyString(), Mockito.any(Class.class))).thenReturn(null);
		
		layers = settings.getLayers();		
		Assert.assertEquals(layers.size(), 3); // Background, foreground, outline
		
		Assert.assertTrue(layers.get(0) instanceof Fill);
		Assert.assertTrue(layers.get(1) instanceof Fill);
		Assert.assertTrue(layers.get(2) instanceof Outline);
		
		/* Does it produce evaluator views? */
		Mockito.when(mockViewProperties.getProperty(GraphicalSettings.GRAPHICAL_EVALUATOR_MAP, String.class))
			.thenReturn("A\tColor1\nB\tColor2\n");		
		Mockito.when(mockViewProperties.getProperty(GraphicalSettings.GRAPHICAL_EVALUATOR, String.class))
		.thenReturn("Mock Evaluator");		
	
		layers = settings.getLayers();		
		Assert.assertEquals(layers.size(), 4);
		
		Assert.assertTrue(layers.get(0) instanceof Fill);
		Assert.assertTrue(layers.get(1) instanceof ConditionalBrush);
		Assert.assertTrue(layers.get(2) instanceof ConditionalBrush);
		Assert.assertTrue(layers.get(3) instanceof Outline);
		
	}
	
	/* Should be able to parse the mock evaluator */
	@Test
	public void testEnumerations() {
		Assert.assertTrue(settings.getSupportedEnumerations().isEmpty());
		
		Mockito.when(mockComponent.getReferencingComponents()).thenReturn(Collections.<AbstractComponent>singletonList(mockEnumerator));
		Mockito.when(mockViewProperties.getProperty(GraphicalSettings.GRAPHICAL_EVALUATOR, String.class))
			.thenReturn(ENUMERATOR_NAME);
		
		settings = new GraphicalSettings(mockView);
		
		Collection<String> enumerations = settings.getSupportedEnumerations();
		Assert.assertNotNull(enumerations);
		Assert.assertEquals(enumerations.size(), 2);
		Iterator<String> it = enumerations.iterator();
		Assert.assertEquals(it.next(), "abc");
		Assert.assertEquals(it.next(), "xyz");
	}

}
