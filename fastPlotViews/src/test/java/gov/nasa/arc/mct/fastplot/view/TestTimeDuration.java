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
package gov.nasa.arc.mct.fastplot.view;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestTimeDuration {

	@Test
	public void testValues() {
		TimeDuration duration = new TimeDuration(1, 2, 3, 4);
		Assert.assertEquals(duration.getDays(), 1);
		Assert.assertEquals(duration.getHours(), 2);
		Assert.assertEquals(duration.getMinutes(), 3);
		Assert.assertEquals(duration.getSeconds(), 4);
		duration.setDays(5);
		duration.setHours(6);
		duration.setMinutes(7);
		duration.setSeconds(8);
		Assert.assertEquals(duration.getDays(), 5);
		Assert.assertEquals(duration.getHours(), 6);
		Assert.assertEquals(duration.getMinutes(), 7);
		Assert.assertEquals(duration.getSeconds(), 8);
	}
}
