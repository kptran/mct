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

import gov.nasa.arc.mct.api.feed.DataProvider;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.components.JAXBModelStatePersistence;
import gov.nasa.arc.mct.components.ModelStatePersistence;
import gov.nasa.arc.mct.components.PropertyDescriptor;
import gov.nasa.arc.mct.components.PropertyDescriptor.VisualControlDescriptor;
import gov.nasa.arc.mct.limits.data.LimitDataProvider;
import gov.nasa.arc.mct.limits.data.TimeServiceImpl;
import gov.nasa.arc.mct.services.activity.TimeService;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.UIManager;

public class LimitLineComponent extends AbstractComponent implements FeedProvider{

	private static final ResourceBundle bundle = ResourceBundle.getBundle("Limits"); 
	public static final String LIMIT_FEED_PREFIX = "limit:"; 
	private final AtomicReference<LimitLineModel> model = new AtomicReference<LimitLineModel>(new LimitLineModel());
	private LimitDataProvider dataProvider = null;

	@Override
	public boolean isLeaf() {
		return true;
	}
	
	@Override
	public boolean isTwiddleEnabled() {
		return true;
	}

	@Override
	protected <T> T handleGetCapability(Class<T> capability) {
		if (FeedProvider.class.isAssignableFrom(capability)) {
			return capability.cast(this);
		}
		
		if (ModelStatePersistence.class.isAssignableFrom(capability)) {
		    JAXBModelStatePersistence<LimitLineModel> persistence = new JAXBModelStatePersistence<LimitLineModel>() {

				@Override
				protected LimitLineModel getStateToPersist() {
					return model.get();
				}

				@Override
				protected void setPersistentState(LimitLineModel modelState) {
					model.set(modelState);
				}

				@Override
				protected Class<LimitLineModel> getJAXBClass() {
					return LimitLineModel.class;
				}
		        
			};
			
			return capability.cast(persistence);
		}
		return null;
	}

	@Override
	public String getLegendText() {
		return this.getDisplayName();
	}
	
	@Override
	public int getMaximumSampleRate() {
		return 1;
	}
	
	@Override
	public String getSubscriptionId() {
		return LIMIT_FEED_PREFIX + getExternalKey();
	}
	
	public LimitLineModel getModel() {
		return model.get();
	}
	
	@Override
	public boolean isPrediction() {
		return true;
	}

	@Override
	public TimeService getTimeService() {
		return TimeServiceImpl.getInstance();
	}

	@Override
	public String getCanonicalName() {
		return getDisplayName();
	}
	
	
	@Override
	public RenderingInfo getRenderingInfo(Map<String, String> data) {
        return getRenderingInfo(data.get(FeedProvider.NORMALIZED_VALUE_KEY));
	}
	
    public static RenderingInfo getRenderingInfo(String stringifiedValue) {
		Color valueColor = UIManager.getColor("ISPColor.ColorOK");
		Color statusColor = UIManager.getColor("ISPColor.ColorOK");

		RenderingInfo ri = new RenderingInfo(
				stringifiedValue,
				valueColor != null ? valueColor : Color.green,
				" ",
				statusColor != null ? statusColor : Color.green,
				true
		);
		return ri;
	}
	
	@Override
	public FeedType getFeedType() {
		return FeedType.FLOATING_POINT;
	}

	@Override
	public long getValidDataExtent() {
		return Long.MAX_VALUE;
	}

	public void setDataProvider(DataProvider dataProvider) {
		this.dataProvider = (LimitDataProvider) dataProvider;
	}
	
	public LimitDataProvider getDataProvider() {
		return this.dataProvider;
	}
	
	@Override
	public List<PropertyDescriptor> getFieldDescriptors()  {
		
        PropertyDescriptor val = new PropertyDescriptor(bundle.getString("VALUE_LABEL"), new LimitLineEditor(this), VisualControlDescriptor.TextField);
        val.setFieldMutable(true);
        
		return Collections.singletonList(val);
	}
}
