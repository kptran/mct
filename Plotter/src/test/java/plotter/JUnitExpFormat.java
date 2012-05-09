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
package plotter;

import java.text.DecimalFormat;
import java.text.ParseException;

import junit.framework.TestCase;

public class JUnitExpFormat extends TestCase {
	public void testFormat() {
		DecimalFormat baseFormat = new DecimalFormat("0.0");
		ExpFormat format = new ExpFormat(baseFormat);
		assertEquals("10.0", format.format(1L));
		assertEquals("0.1", format.format(-1L));
		assertEquals("10.0", format.format(1.0));
		assertEquals("0.1", format.format(-1.0));
		assertSame(baseFormat, format.getBaseFormat());
	}


	public void testParse() throws ParseException {
		ExpFormat format = new ExpFormat(new DecimalFormat("0.0"));
		assertEquals(1.0, format.parse("10.0"));
		assertEquals(-1.0, format.parse("0.1"));
	}
}
