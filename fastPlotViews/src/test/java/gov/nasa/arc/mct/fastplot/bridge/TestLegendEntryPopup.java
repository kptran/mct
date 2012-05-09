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
package gov.nasa.arc.mct.fastplot.bridge;

import gov.nasa.arc.mct.fastplot.utils.AbbreviatingPlotLabelingAlgorithm;
import gov.nasa.arc.mct.fastplot.view.LegendEntryPopupMenuFactory;
import gov.nasa.arc.mct.fastplot.view.PlotViewManifestation;
import gov.nasa.arc.mct.platform.spi.Platform;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.services.component.PolicyManager;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestLegendEntryPopup {

	@Mock AbbreviatingPlotLabelingAlgorithm mockLabelingAlgorithm;
	@Mock LegendEntryPopupMenuFactory       mockPopupManager; 
	@Mock JPopupMenu                        mockPopup;
	@Mock LegendEntry                       mockLegendEntry;
	@Mock PlotViewManifestation             mockPlotView;
	
	@Mock Platform                          mockPlatform;
	@Mock PolicyManager                     mockPolicyManager;
	
	ExecutionResult lockedResult   = new ExecutionResult(null, true, null);
	ExecutionResult unlockedResult = new ExecutionResult(null, false, null);
	
	Platform oldPlatform;
	
	@BeforeClass
	public void setupClass() {
		oldPlatform = PlatformAccess.getPlatform();
		
		new PlatformAccess().setPlatform(mockPlatform);		
	}
	
	@AfterClass
	public void teardownClass() {
		new PlatformAccess().setPlatform(oldPlatform);		
	}
	
	@BeforeTest
	public void setupTest() {
		
		MockitoAnnotations.initMocks(this);
		Mockito.when(mockPopupManager.getPopup(Mockito.<LegendEntry> any())).thenReturn(mockPopup);
				
		/* View's isLocked() method is final - it can't be mocked & needs to reference the platform */
		/* So, provide a mock platform */
		Mockito.when(mockPlatform.getPolicyManager())
			.thenReturn(mockPolicyManager);
		Mockito.when(mockPolicyManager.execute(Mockito.anyString(), Mockito.<PolicyContext> any()))
			.thenReturn(new ExecutionResult(null, false, null));
		
		Mockito.when(mockLegendEntry.getComputedBaseDisplayName()).thenReturn("test");
		Mockito.when(mockLegendEntry.getFullBaseDisplayName()).thenReturn("test");
		
	}
	
	private JPopupMenu getSubMenu(JPopupMenu menu) {
		return ((JMenu) (menu.getComponent(0))).getPopupMenu();
	}
	
	/* Tests for LegendEntry's triggering of popup manager behaviors */	
	@Test
	public void testLegendEntryTriggersPopup() {		
		LegendEntry entry = new LegendEntry(Color.WHITE, Color.BLACK, new JLabel().getFont(), mockLabelingAlgorithm);
		entry.setPopup(mockPopupManager);
		entry.mousePressed(new MouseEvent(entry, 0, 0, 0, 0, 0, 0, true ));
		Mockito.verify(mockPopupManager).getPopup(entry);
		Mockito.verify(mockPopup).show(entry, 0, 0);
	}
	
	@Test
	public void testLegendEntryTriggersPopupAtLocation() {
		int x = 100; int y = 120;
		LegendEntry entry = new LegendEntry(Color.WHITE, Color.BLACK, new JLabel().getFont(), mockLabelingAlgorithm);
		entry.setPopup(mockPopupManager);
		entry.mousePressed(new MouseEvent(entry, 0, 0, 0, x, y, 0, true ));
		Mockito.verify(mockPopupManager).getPopup(entry);
		Mockito.verify(mockPopup).show(entry, x, y);
	}

	@Test
	public void testLegendEntryIgnoresNonPopupTriggers () {		
		LegendEntry entry = new LegendEntry(Color.WHITE, Color.BLACK, new JLabel().getFont(), mockLabelingAlgorithm);
		entry.setPopup(mockPopupManager);
		entry.mousePressed(new MouseEvent(entry, 0, 0, 0, 0, 0, 0, false ));		
		Mockito.verify(mockPopupManager, Mockito.never()).getPopup(entry);		
	}
	
	
	/* Tests for LegendEntryPopupMenuManager behavior */
	@Test
	public void testLegendEntryPopupMenuSize() {
		Mockito.when(mockPolicyManager.execute(Mockito.anyString(), Mockito.<PolicyContext> any()))
			.thenReturn(new ExecutionResult(null, false, null));	
		
		LegendEntryPopupMenuFactory manager = new LegendEntryPopupMenuFactory(mockPlotView);
		JPopupMenu menu = getSubMenu(manager.getPopup(mockLegendEntry));
		
		Assert.assertEquals(menu.getComponentCount(), PlotConstants.MAX_NUMBER_OF_DATA_ITEMS_ON_A_PLOT);		
	}

	@Test 
	public void testLegendEntryMenuEmptyWhenLocked() {
		/* Simulate locking by having policy manager return true for everything */
		/* (including locking check) */
		Mockito.when(mockPolicyManager.execute(Mockito.anyString(), Mockito.<PolicyContext> any()))
			.thenReturn(new ExecutionResult(null, true, null));	
		
		LegendEntryPopupMenuFactory manager = new LegendEntryPopupMenuFactory(mockPlotView);
		JPopupMenu menu = manager.getPopup(mockLegendEntry);
		Assert.assertEquals(menu.getComponentCount(), 0);		
	}
	
	@Test 
	public void testLegendEntryPopupMenuColors() {
		Mockito.when(mockPolicyManager.execute(Mockito.anyString(), Mockito.<PolicyContext> any()))
		.thenReturn(new ExecutionResult(null, false, null));	
	
		LegendEntryPopupMenuFactory manager = new LegendEntryPopupMenuFactory(mockPlotView);
		JPopupMenu menu = getSubMenu(manager.getPopup(mockLegendEntry));
		
		/* Draw icons to this to test for color correctness */
		BufferedImage image = new BufferedImage(12, 12, BufferedImage.TYPE_INT_RGB);
		Graphics      graphics = image.getGraphics();
		
		for (int i = 0; i < PlotConstants.MAX_NUMBER_OF_DATA_ITEMS_ON_A_PLOT; i++) {
			JMenuItem item = (JMenuItem) menu.getComponent(i);
			Icon      icon = item.getIcon();
			icon.paintIcon(item, graphics, 0, 0);
			int drawnRGB = image.getRGB(2, 2); // Go a couple pixels in, in case icon has border
			Assert.assertEquals(PlotLineColorPalette.getColor(i).getRGB(), drawnRGB);
		}
	}
	
	@Test 
	public void testLegendEntryPopupMenuSelection() {
		Mockito.when(mockPolicyManager.execute(Mockito.anyString(), Mockito.<PolicyContext> any()))
		.thenReturn(new ExecutionResult(null, false, null));	
	
		LegendEntryPopupMenuFactory manager = new LegendEntryPopupMenuFactory(mockPlotView);
		for (int i = 0; i < PlotConstants.MAX_NUMBER_OF_DATA_ITEMS_ON_A_PLOT; i++) {
			Mockito.when(mockLegendEntry.getForeground()).thenReturn(PlotLineColorPalette.getColor(i));
	
			JPopupMenu menu = getSubMenu(manager.getPopup(mockLegendEntry));
			
			JRadioButtonMenuItem  item = (JRadioButtonMenuItem) menu.getComponent(i);
			Assert.assertTrue(item.isSelected());
			for (int j = 0; j < PlotConstants.MAX_NUMBER_OF_DATA_ITEMS_ON_A_PLOT; j++) {
				if (j != i) {
					item = (JRadioButtonMenuItem) menu.getComponent(j);
					Assert.assertFalse(item.isSelected());
				}
			}
		}
	}

}
