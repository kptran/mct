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
package plotter.xy;

import java.text.MessageFormat;

import junit.framework.TestCase;

public class JUnitSlopeLineDisplay extends TestCase {
	private SlopeLineDisplay display;

	private SlopeLine line;


	@Override
	protected void setUp() throws Exception {
		line = new SlopeLine();
		display = new SlopeLineDisplay();
	}


	public void testAdd() {
		display.setText("");
		display.slopeLineAdded(line, null, 0, 0);
		assertEquals("", display.getText());
	}


	public void testRemove() {
		display.setText("test");
		display.slopeLineRemoved(line, null);
		assertEquals("", display.getText());
	}


	public void testUpdate() {
		MessageFormat format = new MessageFormat("{0} {1} {2}");
		display.setFormat(format);
		assertEquals(format, display.getFormat());
		display.slopeLineUpdated(line, null, 1, 2, 3, 10);
		assertEquals("2 8 4", display.getText());
	}


	public void testUpdateSame() {
		MessageFormat format = new MessageFormat("{0} {1} {2}");
		display.setFormat(format);
		assertEquals(format, display.getFormat());
		display.slopeLineUpdated(line, null, 1, 2, 1, 2);
		assertEquals("", display.getText());
	}
}
