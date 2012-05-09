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
package gov.nasa.arc.mct.graphics.brush;

import gov.nasa.arc.mct.graphics.clip.AxisClip;
import gov.nasa.arc.mct.graphics.state.StateSensitive;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class BrushTest {
	private static final Shape ELLIPSE = new Ellipse2D.Double(0,0,1,1);
	
	private static final int WIDTH  = 300;
	private static final int HEIGHT = 300;
	
	private static final Color BACKGROUND   = Color.black;
	private static final Color FOREGROUND   = Color.ORANGE;
	private static final Color CONDITIONAL  = Color.green;
	
	/* For testing conditional fills */
	private static final String CONDITION_A = "Condition A";
	private static final String CONDITION_B = "Condition B";
	
	private BufferedImage image;
	private Graphics2D    g2;
	
	@BeforeTest
	public void setupTest() {
		image = new BufferedImage (WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g2    = image.createGraphics();
		g2.setClip(0, 0, WIDTH, HEIGHT);
		
		g2.setColor(BACKGROUND);
		g2.fillRect(image.getMinX(), image.getMinY(), image.getWidth(), image.getHeight());
		
	}
	
	@Test
	public void testFill() {
		/* Ensure that a fill expands to fit its container */
	
		Brush b = new Fill(FOREGROUND);
		
		b.draw(ELLIPSE, g2, new Rectangle(0,0,WIDTH,HEIGHT));
		
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				/* Determine whether or not point is in expected circle */
				int dx = (WIDTH/2)  - x;
				int dy = (HEIGHT/2) - y;
				double d = Math.sqrt(dx*dx + dy*dy);
				
				/* Check color. Note that for 149.0 <= d <= 151.0 we tolerate uncertainty */
				if (d < 149.0) AssertJUnit.assertEquals(image.getRGB(x, y), FOREGROUND.getRGB());
				if (d > 151.0) AssertJUnit.assertEquals(image.getRGB(x, y), BACKGROUND.getRGB());
				
			}
		}
	}
	
	@Test
	public void testFillColor() {
		Fill b = new Fill(FOREGROUND);
		Assert.assertEquals(b.getColor(), FOREGROUND);
		b.setColor(CONDITIONAL);
		Assert.assertEquals(b.getColor(), CONDITIONAL);
	}
	
	@Test
	public void testStateSensitiveColor() {
		/* Basic fill only stores state if its Color does */
		Fill b = new Fill(FOREGROUND);
		b.setState("1.0");
		Assert.assertEquals(b.getState(), null);
		
		b.setColor(new StateColor());
		b.setState("1.0");
		Assert.assertEquals(b.getState(), "1.0");
		b.setState("2.0");
		Assert.assertEquals(b.getState(), "2.0");		
	}
	
	@Test
	public void testClippedFill() {
		/* Ensure that a fill grows/shrinks appropriately to fit its container */
		
		int[] axes = { AxisClip.X_AXIS, AxisClip.Y_AXIS };
		int[] dirs = { AxisClip.INCREASING, AxisClip.DECREASING };
		
		for (int axis : axes) {
			for (int dir : dirs) {
				
					
				Fill b = new ClippedFill(FOREGROUND, axis, dir);
				b.setInterval(0.0, 1.0);
				
				for (double state = 0.25; state <= 0.75; state += 0.25) {
					b.setState(Double.toString(state));
					Assert.assertEquals(Double.parseDouble(b.getState().toString()), state);
					
					g2.setColor(BACKGROUND);
					g2.fillRect(image.getMinX(), image.getMinY(), image.getWidth(), image.getHeight());
					
					b.draw(ELLIPSE, g2, new Rectangle(0,0,WIDTH,HEIGHT));

				
					for (int x = 0; x < WIDTH; x++) {
						for (int y = 0; y < HEIGHT; y++) {
							/* Determine whether or not we expect to be clipped */
							int i = (axis == AxisClip.X_AXIS) ? x : y;
							int max = (axis == AxisClip.X_AXIS) ? WIDTH : HEIGHT; 
							if (dir == AxisClip.DECREASING) i = max - i;
							
							double expectedState = state * (double) max;
							
							if (i > expectedState + 1) {
								AssertJUnit.assertEquals(image.getRGB(x,y), BACKGROUND.getRGB());
							} else if (i < expectedState - 1){							
								/* Determine whether or not point is in expected circle */
								int dx = (WIDTH/2)  - x;
								int dy = (HEIGHT/2) - y;
								double d = Math.sqrt(dx*dx + dy*dy);
								
								/* Check color. Note that for 149.0 <= d <= 151.0 we tolerate uncertainty */
								if (d < 149.0) AssertJUnit.assertEquals(image.getRGB(x, y), FOREGROUND.getRGB());
								if (d > 151.0) AssertJUnit.assertEquals(image.getRGB(x, y), BACKGROUND.getRGB());
							}
						}
					}
				}
				
				/* Should also clamp to minimum/maximum */
				b.setState(Double.toString(1.5));
				Assert.assertEquals(Double.parseDouble(b.getState().toString()), 1.0);
				b.setState(Double.toString(-0.5));
				Assert.assertEquals(Double.parseDouble(b.getState().toString()), 0.0);
			}
		}
	}

	@Test
	public void testScalingFill() {
		/* Ensure that a fill scales appropriately to fit its container */
							
		Fill b = new ScalingFill(FOREGROUND);
		b.setInterval(0.0, 1.0);
		
		for (double state = 0.25; state <= 0.75; state += 0.25) {
			b.setState(Double.toString(state));
			Assert.assertEquals(Double.parseDouble(b.getState().toString()), state);
			
			g2.setColor(BACKGROUND);
			g2.fillRect(image.getMinX(), image.getMinY(), image.getWidth(), image.getHeight());
			
			b.draw(ELLIPSE, g2, new Rectangle(0,0,WIDTH,HEIGHT));

		
			for (int x = 0; x < WIDTH; x++) {
				for (int y = 0; y < HEIGHT; y++) {
					/* Determine whether or not we expect to be clipped */					
					double expectedRadius = state * (double) WIDTH / 2;
					
					/* Determine whether or not point is in expected circle */
					int dx = (WIDTH/2)  - x;
					int dy = (HEIGHT/2) - y;
					double d = Math.sqrt(dx*dx + dy*dy);
					
					/* Check color. Note that for 149.0 <= d <= 151.0 we tolerate uncertainty */
					if (d < expectedRadius - 1) AssertJUnit.assertEquals(image.getRGB(x, y), FOREGROUND.getRGB());
					if (d > expectedRadius + 1) AssertJUnit.assertEquals(image.getRGB(x, y), BACKGROUND.getRGB());

				}
			}
		}
		
		/* Should also clamp to minimum/maximum */
		b.setState(Double.toString(1.5));
		Assert.assertEquals(Double.parseDouble(b.getState().toString()), 1.0);
		b.setState(Double.toString(-0.5));
		Assert.assertEquals(Double.parseDouble(b.getState().toString()), 0.0);
	}

	@Test
	public void testConditionalFill() {
		/* Ensure that conditional fills correspond to enumerated states */
	
		ConditionalBrush a = new ConditionalBrush(new Fill(FOREGROUND), CONDITION_A);
		ConditionalBrush b = new ConditionalBrush(new Fill(CONDITIONAL), CONDITION_B);
		
		String[] conditions = { CONDITION_A, CONDITION_B, CONDITION_A, CONDITION_B };
		
		AssertJUnit.assertTrue(a instanceof StateSensitive);
		AssertJUnit.assertTrue(b instanceof StateSensitive);
		
		for (String condition : conditions) {
			a.setState(condition);
			b.setState(condition);		
			
			a.draw(ELLIPSE, g2, new Rectangle(0,0,WIDTH,HEIGHT));
			b.draw(ELLIPSE, g2, new Rectangle(0,0,WIDTH,HEIGHT));
			
			for (int x = 0; x < WIDTH; x++) {
				for (int y = 0; y < HEIGHT; y++) {
					/* Determine whether or not point is in expected circle */
					int dx = (WIDTH/2)  - x;
					int dy = (HEIGHT/2) - y;
					double d = Math.sqrt(dx*dx + dy*dy);
					
					Color expected = (condition == CONDITION_A) ? FOREGROUND : CONDITIONAL;
					
					/* Check color. Note that for 149.0 <= d <= 151.0 we tolerate uncertainty */
					if (d < 149.0) AssertJUnit.assertEquals(image.getRGB(x, y), expected.getRGB());
					if (d > 151.0) AssertJUnit.assertEquals(image.getRGB(x, y), BACKGROUND.getRGB());
					
				}
			}
		}
	}
	
	private class StateColor extends Color implements StateSensitive {

		private static final long serialVersionUID = -1060487969568536392L;
		private Object storedState = null;
		
		public StateColor() {
			super(0,0,0);
		}
		
		@Override
		public void setState(Object state) {
			storedState = state;			
		}

		@Override
		public Object getState() {
			return storedState;
		}

		@Override
		public void setInterval(Object minimum, Object maximum) {
			
		}
		
	}
}
