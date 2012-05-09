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
package gov.nasa.arc.mct.core.policy;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.core.components.TelemetryDataTaxonomyComponent;
import gov.nasa.arc.mct.gui.FeedView;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestDefaultViewForTaxonomy {
    
    @DataProvider(name="policies")
    Object[][] generateData() {
        AbstractComponent[] components = new AbstractComponent[] {
                new AbstractComponent() {
                },
                new TelemetryDataTaxonomyComponent()
        };
        
        ViewInfo[] infos = new ViewInfo[] {
                new ViewInfo(FeedViewDerived.class,"view",ViewType.OBJECT),
                new ViewInfo(FeedViewDerived.class, "view", ViewType.EMBEDDED),
                new ViewInfo(NormalView.class, "view", ViewType.OBJECT)
        };
        
        Object[][] outer = new Object[components.length*infos.length][];
        
        for (int i=0; i < components.length; i++) {
            for (int j = 0; j < infos.length; j++) {
                outer[(i*infos.length)+j] = new Object[] {components[i], infos[j], !(components[i].getClass().equals(TelemetryDataTaxonomyComponent.class)&&
                                                                    !infos[j].getViewClass().equals(FeedViewDerived.class)&&
                                                                    infos[j].getViewType()==ViewType.OBJECT)};
            }
        }
        
        return outer;
    }
    
    private static final class FeedViewDerived extends FeedView {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public FeedViewDerived(AbstractComponent ac, ViewInfo vi) {
            super(null);
        }
        
        @Override
        public void updateFromFeed(Map<String, List<Map<String, String>>> data) {
            
        }

        @Override
        public void synchronizeTime(Map<String, List<Map<String, String>>> data, long syncTime) {
            
        }

        @Override
        public Collection<FeedProvider> getVisibleFeedProviders() {
            return null;
        }
        
    }
    
    public static final class NormalView extends View{
        public NormalView(AbstractComponent ac, ViewInfo vi) {}
        
        private static final long serialVersionUID = 1L;
    }
    
    @Test(dataProvider="policies")
    public void testPolicy(AbstractComponent ac, ViewInfo vi, boolean expectedResult) {
        DefaultViewForTaxonomyNode policy = new DefaultViewForTaxonomyNode();
        PolicyContext context = new PolicyContext();
        context.setProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName(), ac);
        context.setProperty(PolicyContext.PropertyName.TARGET_VIEW_INFO.getName(), vi);
        Assert.assertEquals(policy.execute(context).getStatus(), expectedResult);
    }
}
