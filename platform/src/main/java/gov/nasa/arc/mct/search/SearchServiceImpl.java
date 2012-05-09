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
package gov.nasa.arc.mct.search;

import gov.nasa.arc.mct.registry.ExternalComponentRegistryImpl.ExtendedComponentProvider;
import gov.nasa.arc.mct.services.component.SearchProvider;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

public class SearchServiceImpl {
    
    private Collection<SearchProvider> searchProviders = new LinkedHashSet<SearchProvider>();
    
    private static final SearchServiceImpl INSTANCE = new SearchServiceImpl();
    
    public static final SearchServiceImpl getInstance() {
        return INSTANCE;
    }
    
    private SearchServiceImpl() {        
    }
    
    public void refresh(List<ExtendedComponentProvider> providers) {
        searchProviders.clear();
        
        for (ExtendedComponentProvider provider : providers) {
            SearchProvider pluginSearchProvider = provider.getSearchProvider();
            if (pluginSearchProvider != null) {
                searchProviders.add(pluginSearchProvider);
            }
        }
    }

    public Collection<SearchProvider> getExtendedSearchProviders() {
        return Collections.unmodifiableCollection(searchProviders);
    }
    
    public SearchProvider getPlatformSearchProvider() {
        return new PlatformSearchUI();
    }
}
