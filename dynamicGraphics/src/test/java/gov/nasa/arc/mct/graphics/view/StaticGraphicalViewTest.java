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
package gov.nasa.arc.mct.graphics.view;

import gov.nasa.arc.mct.graphics.component.GraphicalComponent;
import gov.nasa.arc.mct.graphics.component.GraphicalModel;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JPanel;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class StaticGraphicalViewTest {
	private static final String CHECKER_SVG  = new File("src/test/resources/checker.svg").toURI().toString();
	private static final String CHECKER_PNG  = new File("src/test/resources/checker.png").toURI().toString();
	private static final String BAD_FILE = "src/test/resources/DOES_NOT_EXIST.svg";
	
	private static final int PAINT_SIZE = 200;
	
	@Mock private GraphicalComponent mockComponent;
	
	private GraphicalModel checkersModelSVG = new GraphicalModel();
	private GraphicalModel checkersModelPNG = new GraphicalModel();
	private GraphicalModel badModel      = new GraphicalModel();
	
	
	
	@BeforeTest
	public void setup() {
		checkersModelSVG.setGraphicURI(CHECKER_SVG);
		checkersModelPNG.setGraphicURI(CHECKER_PNG);
		badModel.setGraphicURI(BAD_FILE);
		
		MockitoAnnotations.initMocks(this);
	
	}
	
	@Test
	public void testViewConstruction() throws Exception {
		Mockito.when(mockComponent.getModelRole()).thenReturn(checkersModelPNG);
		
		/* Create a new static graphical view */
		StaticGraphicalView view = new StaticGraphicalView(mockComponent, 
				new ViewInfo(StaticGraphicalView.class, "", ViewType.OBJECT));;	

		/* Give image time to load or fail */
		Thread.sleep(1000);
				
		/* Should have one component (a JPanel) */
		Assert.assertEquals(view.getComponentCount(), 1);
		Assert.assertTrue(JPanel.class.isAssignableFrom(view.getComponent(0).getClass()));		
	}

	@Test
	public void testViewPaintingSVG() throws Exception {
		Mockito.when(mockComponent.getModelRole()).thenReturn(checkersModelSVG);
		
		/* Create a new static graphical view for the sample SVG */
		StaticGraphicalView view = new StaticGraphicalView(mockComponent, 
				new ViewInfo(StaticGraphicalView.class, "", ViewType.OBJECT));;	
		
		/* Should force rendering */
	    view.setSize(PAINT_SIZE, PAINT_SIZE);	    
	    view.setVisible(true);
	    view.doLayout();
	    for (ComponentListener l : view.getComponentListeners()) {
	    	l.componentResized(null);
	    }
		Thread.sleep(1000);
		
		/* We should be able to draw the checker pattern now */
		BufferedImage testImage = new BufferedImage(PAINT_SIZE, PAINT_SIZE, BufferedImage.TYPE_INT_RGB);
		view.paint(testImage.getGraphics());
		checkForCheckers(testImage);	
	}
	
	@Test 
	public void testViewPaintingPNG() throws Exception {
		Mockito.when(mockComponent.getModelRole()).thenReturn(checkersModelPNG);
		
		/* Create a new static graphical view for the sample PNG */
		StaticGraphicalView view = new StaticGraphicalView(mockComponent, 
				new ViewInfo(StaticGraphicalView.class, "", ViewType.OBJECT));;	
		
		/* Should force rendering */
	    view.setSize(PAINT_SIZE, PAINT_SIZE);	    
	    view.setVisible(true);
	    view.doLayout();
	    for (ComponentListener l : view.getComponentListeners()) {
	    	l.componentResized(null);
	    }
		Thread.sleep(1000);
		
		/* We should be able to draw the checker pattern now */
		BufferedImage testImage = new BufferedImage(PAINT_SIZE, PAINT_SIZE, BufferedImage.TYPE_INT_RGB);
		view.paint(testImage.getGraphics());
		checkForCheckers(testImage);	
	}

	private void checkForCheckers(BufferedImage img) {
		int w = img.getWidth();
		int h = img.getHeight();
		
		// Start at 2 to give a couple of pixels of leeway
		for (int x = 2; x < w / 2 - 2; x++) {
			for (int y = 2; y < h / 2 - 2; y++) {
				/* Test for checkerboard pattern, or similar */
				
				// Get colors from four quadrants
				int[][] rgb = new int[2][2];
				rgb[0][0] = img.getRGB(  x,   y);
				rgb[0][1] = img.getRGB(  x, h-y);
				rgb[1][0] = img.getRGB(w-x,   y);
				rgb[1][1] = img.getRGB(w-x, h-y);
				
				//Not reflected across x	
				Assert.assertFalse(rgb[0][0] == rgb[1][0]);
				Assert.assertFalse(rgb[0][1] == rgb[1][1]);
				
				//Not reflected across y
				Assert.assertFalse(rgb[0][0] == rgb[0][1]);
				Assert.assertFalse(rgb[1][0] == rgb[1][1]);
				
				//But reflected diagonally
				Assert.assertTrue (rgb[0][0] == rgb[1][1]);
				Assert.assertTrue (rgb[1][0] == rgb[0][1]);
				
			}
		}
	}
	

}
