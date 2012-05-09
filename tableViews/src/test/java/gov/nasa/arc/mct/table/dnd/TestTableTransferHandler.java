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
package gov.nasa.arc.mct.table.dnd;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.table.view.TableViewManifestation;

import java.awt.datatransfer.DataFlavor;
import java.lang.reflect.Method;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@SuppressWarnings("javadoc")
public class TestTableTransferHandler {
	
	@DataProvider(name="components")
	Object[][] generateSharedComponents() {
		return new Object[][] {
			new Object[] {createVersionedComponent()},
			new Object[] {createSharedComponent()},
		};
	}
	
	private AbstractComponent createVersionedComponent() {
		AbstractComponent ac = Mockito.mock(AbstractComponent.class);
		AbstractComponent masterMock = Mockito.mock(AbstractComponent.class);
		Mockito.when(ac.getMasterComponent()).thenReturn(masterMock);
		return ac;
	}
	
	private AbstractComponent createSharedComponent() {
		AbstractComponent ac = Mockito.mock(AbstractComponent.class);
		Mockito.when(ac.isShared()).thenReturn(true);
		return ac;
	}
	
	@Test(dataProvider="components")
	public void testCannotDropWhenComponentIsShared(AbstractComponent ac) throws Exception {
		Mockito.when(ac.getDisplayName()).thenReturn("abc");
		TableViewManifestation tvm = Mockito.mock(TableViewManifestation.class);
		Mockito.when(tvm.getManifestedComponent()).thenReturn(ac);
		TableTransferHandler handler = new TableTransferHandler(tvm, null);
		Method internalCanHandle = TableTransferHandler.class.getDeclaredMethod("internalCanImport", DataFlavor[].class, Integer.TYPE, Integer.TYPE, Boolean.TYPE, Boolean.TYPE);
		internalCanHandle.setAccessible(true);
		ViewRoleSelection selector = new ViewRoleSelection(new View[]{tvm});
		Assert.assertTrue(selector.getTransferDataFlavors().length > 0);
		Assert.assertTrue(selector.getTransferDataFlavors()[0].getRepresentationClass().equals(View.class));
		Assert.assertFalse(Boolean.class.cast(internalCanHandle.invoke(handler, selector.getTransferDataFlavors(), 1,1,true,true)).booleanValue());
	}
}
