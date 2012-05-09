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
package gov.nasa.arc.mct.platform.spi;

import gov.nasa.arc.mct.api.feed.FeedAggregator;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.lock.manager.LockManager;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.services.activity.TimeService;
import gov.nasa.arc.mct.services.component.ComponentTagService;
import gov.nasa.arc.mct.services.component.ComponentTypeInfo;
import gov.nasa.arc.mct.services.component.MenuManager;
import gov.nasa.arc.mct.services.component.PolicyManager;
import gov.nasa.arc.mct.services.component.ProviderDelegateService;
import gov.nasa.arc.mct.services.component.TagService;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;
import gov.nasa.arc.mct.services.internal.component.CoreComponentRegistry;
import gov.nasa.arc.mct.services.internal.component.User;

import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Set;

public class MockPlatform implements Platform {

	@Override
	public PolicyManager getPolicyManager() {
		return new PolicyManager() {
			
			@Override
			public ExecutionResult execute(String categoryKey, PolicyContext context) {
				return new ExecutionResult(context, true, "");
			}
		};
	}

	@Override
	public WindowManager getWindowManager() {
		return new WindowManager() {
			
			@Override
			public void openInNewWindow(AbstractComponent component) {
			}

			@Override
			public AbstractComponent getWindowRootComponent(Component component) {
				return null;
			}

			@Override
			public View getWindowRootManifestation(
					Component component) {
				return null;
			}
			
			@Override
			public void refreshWindows() {
				//
			}
			
			@Override
			public void closeWindows(String componentId) {
				// 
			}

			@Override
			public void openInNewWindow(AbstractComponent component,
					GraphicsConfiguration graphicsConfig) {
				// TODO Auto-generated method stub
				
			}
		};
	}

	@Override
	public CoreComponentRegistry getComponentRegistry() {
		return new CoreComponentRegistry() {
			
			@Override
			public <T extends AbstractComponent> T newInstance(Class<T> componentClass, AbstractComponent parent) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public AbstractComponent newInstance(ComponentTypeInfo componentTypeInfo) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public AbstractComponent getComponent(String id) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public AbstractComponent newCollection(
					Collection<AbstractComponent> components) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getRootComponentId() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void unregister(Collection<AbstractComponent> components) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Set<ViewInfo> getViewInfos(String componentTypeId,
					ViewType type) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
	}

	@Override
	public User getCurrentUser() {
		return GlobalContext.getGlobalContext().getUser();
	}

	@Override
	public PersistenceService getPersistenceService() {
		return new PersistenceService() {
			
			@Override
			public Collection<String> getAllUsersOfDiscipline(String disciplineId) {
				return Collections.emptyList();
			}
			
			@Override
			public Collection<String> getAllDisciplines() {
				return Collections.emptyList();
			}
			
			@Override
			public AbstractComponent loadComponent(String componentId) {
				return null;
			}
			
			@Override
			public void setComponentDaoStrategy(AbstractComponent mctComp) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public AbstractComponent findPUI(String pui) {
				AbstractComponent b = new AbstractComponent() {}; // mock always finding pui
				b.setId(pui);
				return b;
			}
			
			@Override
			public Collection<AbstractComponent> getReferences(
					AbstractComponent component) {
				return Collections.emptyList();
			}

			@Override
			public void addNewUser(String userId, String group)
					throws DuplicateUserException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void associateSessions(String session, String target) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void disassociateSession(String session) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Collection<AbstractComponent> findComponentByName(
					String session, String name) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void updateComponentsFromDatabase() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void updateComponentFromDatabase(AbstractComponent component) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Set<String> getAllUsers() {
				return null;
			}
		};
	}

	@Override
	public LockManager getLockManager() {
		return GlobalContext.getGlobalContext().getLockManager();
	}
	
	@Override
	public DefaultComponentProvider getDefaultComponentProvider() {
		return null;
	}
	
	@Override
	public SubscriptionManager getSubscriptionManager() {
		return null;
	}

	@Override
	public void registerService(Class<?> serviceClass, Object serviceObject,
			Dictionary<String, Object> props) throws IllegalArgumentException {
		// do nothing
	}

	@Override
	public void unregisterService(Object serviceObject) {
		// do nothing
	}
	
	@Override
	public TimeService getTimeService() {
		return null;
	}

	@Override
	public MenuManager getMenuManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TagService getTagService() {
		return null;
	}

	@Override
	public ComponentTagService getComponentTagService() {
		return null;
	}

	@Override
	public ProviderDelegateService getProviderDelegateService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FeedAggregator getFeedAggregator() {
		return null;
	}
}
