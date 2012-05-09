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
package gov.nasa.arc.mct.evaluator.view;

import static org.fest.swing.data.TableCell.row;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.evaluator.api.Evaluator;
import gov.nasa.arc.mct.evaluator.api.Executor;
import gov.nasa.arc.mct.evaluator.component.EvaluatorComponent;
import gov.nasa.arc.mct.evaluator.component.EvaluatorData;
import gov.nasa.arc.mct.evaluator.component.EvaluatorProviderRegistry;
import gov.nasa.arc.mct.evaluator.spi.EvaluatorProvider;
import gov.nasa.arc.mct.evaluator.view.InfoViewManifestation.ValueModel;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;
import gov.nasa.arc.mct.test.util.gui.BaseUITest;
import gov.nasa.arc.mct.test.util.gui.Query;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.AncestorListener;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JLabelFixture;
import org.fest.swing.fixture.JRadioButtonFixture;
import org.fest.swing.fixture.JTableCellFixture;
import org.fest.swing.fixture.JTableFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestInfoViewManifestation extends BaseUITest {
	@Mock
	private EvaluatorComponent parentComponent;
	@Mock
	private AbstractComponent childComponent;
	@Mock
	private EvaluatorData mockData;
	private ArrayList<Evaluator> eList;
	
	@Mock
	private FeedProvider provider;
	@Mock
	private EvaluatorProvider evaluatorProvider;
	private static final String feedId = "feed";

	private static class ReturnExecutor implements Executor {
		@Override
		public FeedProvider.RenderingInfo evaluate(Map<String, List<Map<String, String>>> data, List<FeedProvider> feedProviders) {
			List<Map<String, String>> l = data.get(feedId);
			String value = l.get(l.size()-1).get(FeedProvider.NORMALIZED_VALUE_KEY);
			return new FeedProvider.RenderingInfo(value, Color.green, "", Color.black, true);
		}
		
		public boolean requiresMultipleInputs() {
			return false;
		}
	}
	
	@BeforeMethod
	public void setup() {
		final String language = "mskEnum";
		MockitoAnnotations.initMocks(this);
		List<AbstractComponent> feedProviders = Collections.<AbstractComponent>singletonList(childComponent);
		Mockito.when(childComponent.getCapability(FeedProvider.class)).thenReturn(provider);
		Mockito.when(parentComponent.getCapability(FeedProvider.class)).thenReturn(provider);
		Mockito.when(parentComponent.getComponents()).thenReturn(feedProviders);
		Mockito.when(parentComponent.getData()).thenReturn(mockData);
		Mockito.when(provider.getSubscriptionId()).thenReturn(feedId);
		Mockito.when(evaluatorProvider.getLanguage()).thenReturn(language);
		Mockito.when(mockData.getLanguage()).thenReturn(language);
		Mockito.when(evaluatorProvider.compile(Mockito.anyString())).thenReturn(new ReturnExecutor());	
		new EvaluatorProviderRegistry().addProvider(evaluatorProvider);
		
		final Executor executor = 
			EvaluatorProviderRegistry.getExecutor(EvaluatorComponent.class.cast(parentComponent));
		
		final Evaluator e = new Evaluator() {

			@Override
			public String getCode() {
				return "test";
			}

			@Override
			public String getDisplayName() {
				return "testName";
			}

			@Override
			public String getLanguage() {
				return language;
			}
			
			public boolean requiresMultipleInputs() {
				return false;
			}

			@Override
			public FeedProvider.RenderingInfo evaluate(Map<String, List<Map<String, String>>> data,
					List<FeedProvider> providers) {
				return executor.evaluate(data, providers);
			}
			
		};
		
		final Evaluator e2 = new Evaluator() {

			@Override
			public String getCode() {
				return "test2";
			}

			@Override
			public String getDisplayName() {
				return "testName2";
			}

			@Override
			public String getLanguage() {
				return language;
			}
			
			public boolean requiresMultipleInputs() {
				return false;
			}

			@Override
			public FeedProvider.RenderingInfo evaluate(Map<String, List<Map<String, String>>> data,
					List<FeedProvider> providers) {
				return executor.evaluate(data, providers);
			}
			
		};

		eList = new ArrayList<Evaluator>();
		eList.add(e);
		eList.add(e2);
		
	}
	
	@AfterMethod
	public void cleanup() {
		new EvaluatorProviderRegistry().removeProvider(evaluatorProvider);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testInfoView() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

		InfoViewManifestation manifestation = new InfoViewManifestation(parentComponent, new ViewInfo(InfoViewManifestation.class,"info", ViewType.OBJECT));
				
		for (AncestorListener listener : manifestation.getAncestorListeners()) {
			listener.ancestorRemoved(null);
			manifestation.removeAncestorListener(listener);
		}
		
		FrameFixture fixture = showInFrame(manifestation, "test");
		JTableFixture tableFixture = fixture.table();
		FeedProvider.RenderingInfo ri = new FeedProvider.RenderingInfo(
				"1",
				Color.gray,
				"a",
				Color.gray,
				true
		); 
		Mockito.when(provider.getRenderingInfo(Mockito.anyMap())).thenReturn(ri);
		Map<String, String> dataInfo = new HashMap<String,String>();
		dataInfo.put(FeedProvider.NORMALIZED_VALUE_KEY, "1");
		dataInfo.put(FeedProvider.NORMALIZED_RENDERING_INFO, ri.toString());	
		Map<String, List<Map<String, String>>> data = 
			Collections.singletonMap(provider.getSubscriptionId(), 
					Collections.singletonList(dataInfo));
		
		Field f1 = InfoViewManifestation.class.getField("tableModel");
		f1.setAccessible(true);
		Object view = f1.get(manifestation);
		
		Field f2 = ValueModel.class.getField("eList");
		f2.setAccessible(true);
		f2.set(view, eList);

		((ValueModel)view).clearModel(); 
		
		manifestation.updateFromFeed(data);
		JLabelFixture labelFixture = new Query().labelIn(fixture);
		labelFixture.requireText("1");
		
		JRadioButtonFixture buttonA = new Query().accessibleNameMatches("Alpha Value: ").radioButtonIn(fixture);
		buttonA.check();
		manifestation.updateFromFeed(data);
		
		JTableCellFixture cell = tableFixture.cell(row(0).column(2));
		JTableCellFixture cell2 = tableFixture.cell(row(1).column(2));
		cell.requireValue("1");
		cell2.requireValue("1");
		
		buttonA.uncheck();
		JRadioButtonFixture buttonI = new Query().accessibleNameMatches("Test Value: ").radioButtonIn(fixture);
		buttonI.check();
		JTextComponentFixture textFixture = new Query().textBoxIn(fixture);
		textFixture.enterText("2");
		manifestation.updateFromFeed(data);
		cell.requireValue("2");
		cell2.requireValue("2");
		
		
	}
}
