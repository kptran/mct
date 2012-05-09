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


import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.View;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class PlaceObjectsInEnumActionTest {
	@Mock
	private AbstractComponent componentA, componentB;
	@Mock
	private AbstractComponent enumerator;
	@Mock 
	private View viewManifestation1, viewManifestation2;
	@Mock
	private ActionContext ac;
	private ArrayList<View> selected;
	@Mock
	private FeedProvider feed;
	private PlaceObjectsInEnumAction action;
	
	private int success, fail;
	private Collection<AbstractComponent> sourceComponents;
	private boolean setResult;
	
	@SuppressWarnings("serial")
	@BeforeClass
	public void setup() {
		MockitoAnnotations.initMocks(this);
		action = new PlaceObjectsInEnumAction() {
			@Override
			protected String getNewEnum(Collection<AbstractComponent> sourceComponents) {
				PlaceObjectsInEnumActionTest.this.sourceComponents = sourceComponents;
				return "test";
			}
			
			@Override
		    AbstractComponent createNewEnum(Collection<AbstractComponent> sourceComponents) {
				return setResult ? enumerator : null;
			}
			
			@Override
			void openNewEnum(String name, AbstractComponent collection) {
				success++;
			}
			
			@Override 
			void showErrorInCreateEnum() {
				fail++;
			}
		};
		
		
		Mockito.when(viewManifestation1.getManifestedComponent()).thenReturn(componentA);
		Mockito.when(viewManifestation2.getManifestedComponent()).thenReturn(componentB);

		Mockito.when(componentA.getCapability(FeedProvider.class)).thenReturn(feed);
		Mockito.when(componentB.getCapability(FeedProvider.class)).thenReturn(feed);
		
		selected = new ArrayList<View> ();
		selected.add(viewManifestation1);
		selected.add(viewManifestation2);

		Mockito.when(ac.getSelectedManifestations()).thenReturn(selected);	
	}
	
	@Test
	public void testCanHandle() {
		Assert.assertTrue(action.canHandle(ac));
	}
	
	@Test (dependsOnMethods = {"testCanHandle"})
	public void testIsEnabled() {
		Assert.assertTrue(action.isEnabled());
	}
	
	@Test(dependsOnMethods = {"testIsEnabled"})
	public void testActionPerformedSuccessfulCase() {
		reset();
		
		setResult = true;
		action.actionPerformed(new ActionEvent(viewManifestation1, 0, ""));
		Assert.assertEquals(success, 1);
		Assert.assertEquals(fail, 0);
		Assert.assertNotNull(sourceComponents);
		Assert.assertEquals(sourceComponents.size(), 2);
        Assert.assertTrue(sourceComponents.contains(componentA));
        Assert.assertTrue(sourceComponents.contains(componentB));
	}
	
	@Test(dependsOnMethods = {"testIsEnabled"})
	public void testActionPerformedFailedCase() {
		reset();
		
		setResult = false;
		action.actionPerformed(new ActionEvent(viewManifestation1, 0,""));
		Assert.assertEquals(success,0);
		Assert.assertEquals(fail,1);
		Assert.assertEquals(sourceComponents.size(), 2);
	}
	
	
	private void reset(){
		success = fail = 0;
		sourceComponents = null;
	}
	

}
