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
package gov.nasa.arc.mct.gui.dialogs;

import gov.nasa.arc.mct.component.MockDaoObject;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.persistence.strategy.DaoStrategy;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MockDaoStrategy implements DaoStrategy<AbstractComponent, MockDaoObject> {
	private MockComponent component;
	private MockDaoObject daoObject;
	
	public MockDaoStrategy(MockComponent component, MockDaoObject daoObject) {
		this.component = component;
		this.daoObject = daoObject;
	}

	@Override
	public void saveObject() {};
	
	@Override
	public MockDaoObject getDaoObject() {
		return daoObject;
	}
	
	@Override
    public MockDaoObject getDaoObject(String sessionId) {
        return daoObject;
    }

	@Override
	public MockComponent getMCTComp() {
		return component;
	}

	@Override
	public void load() {
	}

	@Override
	public void refreshDAO() {
	}

	@Override
	public void removeObject(AbstractComponent mctComp) {
	}

	@Override
	public void removeObjects(Collection<AbstractComponent> mctComps) {
	}

	@Override
	public void saveObject(int childIndex, AbstractComponent mctComp) {
	}

	@Override
	public void saveObjects(int childIndex, Collection<AbstractComponent> mctComps) {
	}

    @Override
    public void refreshDAO(AbstractComponent mctComp) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void deleteObject(AbstractComponent comp) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public Map<String, MockDaoObject> getDaoObjects(List<AbstractComponent> comps) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void associateDelegateSessionId(String sessionId, String delegateSessionId) {
        // TODO Auto-generated method stub
        
    }

}
