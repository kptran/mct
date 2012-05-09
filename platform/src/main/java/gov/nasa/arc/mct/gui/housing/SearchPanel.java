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
package gov.nasa.arc.mct.gui.housing;

import gov.nasa.arc.mct.search.SearchServiceImpl;
import gov.nasa.arc.mct.services.component.SearchProvider;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class SearchPanel extends JPanel   {
    JComponent platformSearchUI = null;
    List<SearchProvider> providerSearchUIs = new ArrayList<SearchProvider>();
    
    public SearchPanel( ) {

        setLayout(new BorderLayout());

        // Get all search providers
        SearchProvider platformSearchProvider = SearchServiceImpl.getInstance().getPlatformSearchProvider();        
        platformSearchUI = platformSearchProvider.createSearchUI();        
        for (SearchProvider provider : SearchServiceImpl.getInstance().getExtendedSearchProviders()) {
            providerSearchUIs.add(provider);
        }
    }

    /** Gets the platform search component. */
    public JComponent getPlatformSearchUI() {
        assert platformSearchUI != null;
        return platformSearchUI;
    }

    /** Gets a list of plugin search providers GUIs. */
    public List<SearchProvider> getProviderSearchUIs() {
        return providerSearchUIs;
    }

}
