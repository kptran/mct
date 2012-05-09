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
package gov.nasa.arc.mct.limits;

import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.components.FeedProvider.FeedType;
import gov.nasa.arc.mct.limits.data.TimeServiceImpl;

import java.awt.Color;
import java.util.Collections;

import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LimitLineComponentTest {

	private LimitLineComponent component;
	final String id = "Johnny Cash";
	final String opsName = "Man in Black";

	@BeforeMethod
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		component = new LimitLineComponent();
		
		component.setDisplayName(opsName);
	}

	@Test
	public void handleGetCapability() {
		FeedProvider provider = component.getCapability(FeedProvider.class);
		Assert.assertEquals(provider.getMaximumSampleRate(),1);
		Assert.assertEquals(provider.getSubscriptionId(),"limit:"+component.getComponentId());
		Assert.assertEquals(provider.getCanonicalName(), opsName);
		Assert.assertEquals(provider.getLegendText(),opsName);
		Assert.assertEquals(provider.getFeedType(),FeedProvider.FeedType.FLOATING_POINT);
		FeedProvider.RenderingInfo info = provider.getRenderingInfo(Collections.<String,String>singletonMap(FeedProvider.NORMALIZED_VALUE_KEY, "1.23"));
		Assert.assertEquals(info.getStatusText()," ");
		Assert.assertEquals(info.getValueText(), "1.23");
		Assert.assertEquals(info.getValueColor(), Color.green);
		Assert.assertEquals(info.getStatusColor(),  Color.green); // expect this to fail when UI colors are implemented
		Assert.assertSame(provider.getTimeService(), TimeServiceImpl.getInstance());	
	}

	@Test
	public void getterTest() {
		Assert.assertEquals(component.getFeedType(), FeedType.FLOATING_POINT);
		Assert.assertEquals(component.getValidDataExtent(), Long.MAX_VALUE);
		Assert.assertEquals(component.getSubscriptionId(), 	LimitLineComponent.LIMIT_FEED_PREFIX + component.getComponentId());

		Assert.assertTrue(component.isLeaf());
		Assert.assertTrue(component.isPrediction());
		Assert.assertTrue(component.isTwiddleEnabled());
	}

	
}
