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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import junit.framework.TestCase;

public class JUnitDateNumberFormat extends TestCase {
	public void testFormat() {
		DateFormat baseFormat = new SimpleDateFormat("yyyy-MM-dd");
		baseFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		DateNumberFormat format = new DateNumberFormat(baseFormat);
		assertEquals("1970-01-01", format.format(0L));
		assertEquals("2001-09-09", format.format(1000000000000L));
		assertEquals("1970-01-01", format.format(0.0));
		assertEquals("2001-09-09", format.format(1000000000000.0));
		assertSame(baseFormat, format.getBaseFormat());
	}


	public void testParse() throws ParseException {
		DateFormat baseFormat = new SimpleDateFormat("yyyy-MM-dd");
		baseFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		DateNumberFormat format = new DateNumberFormat(baseFormat);
		assertEquals(946684800000L, format.parse("2000-01-01"));
		assertEquals(1317340800000L, format.parse("2011-09-30"));
	}
}
