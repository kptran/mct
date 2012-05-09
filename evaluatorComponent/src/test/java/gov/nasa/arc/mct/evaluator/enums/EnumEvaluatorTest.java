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
package gov.nasa.arc.mct.evaluator.enums;

import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.evaluator.api.Executor;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class EnumEvaluatorTest {
	
	@DataProvider(name="evalTestData")
	protected Object[][] testData() {
		return new Object[][] {
			new Object[] {
					// code , input, String expectedValue
					"< -3.3 low\t|> -3.3 normal\t|", "-3.7", "low"
			},
			new Object[] {
					"< -3.3 low\t|> -3.3 normal\t|", "-3.0", "normal"
			},
			new Object[] {
					"= 3.3 equals\t|\u2260 -3.3 not equals\t|", "3.3", "equals"
			},
			new Object[] {
					"= 3.3 equals\t|\u2260 -3.3 not equals\t|", "3", "not equals"
			},
			new Object[] {
					"< -3.3 low\t|> -3.3 normal\t|", "3", "normal"
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	@Test(dataProvider="evalTestData")
	public void testEvalutor(String code, String inputValue, String expectedValue) {
		EnumEvaluator enumEvaluator = new EnumEvaluator();
		Executor e = enumEvaluator.compile(code);
		FeedProvider fp = Mockito.mock(FeedProvider.class);
		Mockito.when(fp.getSubscriptionId()).thenReturn("abc");
		FeedProvider.RenderingInfo data = new FeedProvider.RenderingInfo(inputValue, Color.red, "x", Color.red, true);
		Mockito.when(fp.getRenderingInfo(Mockito.anyMap())).thenReturn(data);
		Map<String, String> value = Collections.singletonMap(FeedProvider.NORMALIZED_RENDERING_INFO, data.toString());
		List<Map<String, String>> values = Collections.singletonList(value);
		Map<String, List<Map<String, String>>> dataSet = Collections.singletonMap(fp.getSubscriptionId(), values);
		FeedProvider.RenderingInfo info = e.evaluate(dataSet, Collections.singletonList(fp));
		Assert.assertTrue(info.isValid());
		Assert.assertEquals(info.getValueText(), expectedValue);
	}
}
