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
package gov.nasa.arc.mct.util.condition;

import gov.nasa.arc.mct.util.condition.Condition;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ConditionTest {

	Condition trueCondition = new Condition() {
		public boolean getValue() {
			return true;
		}
	};

	Condition falseCondition = new Condition() {
		public boolean getValue() {
			return false;
		}
	};

	@Test
	public void testGetValue() throws Exception {
		
		assertTrue(trueCondition.getValue());
	}
	
	@Test
	public void testAlwaysTrue() throws Exception {
		Condition.waitForCondition(Long.MAX_VALUE, trueCondition);
		assertTrue(true, "Condition became true");
	}
	
	@Test
	public void testTimeout() throws Exception {
		Condition.waitForCondition(1000L, falseCondition);
		assertTrue(!falseCondition.getValue());
		assertTrue(true, "Condition timed out");
	}
	
	@Test
	public void testChangingCondition() throws Exception {
		Condition cond = new Condition() {
			private int intValue = 10;
			
			public boolean getValue() {
				return (--intValue <= 0);
			}
		};
		
		Condition.waitForCondition(5000L, cond);
		assertTrue(cond.getValue());
	}
	
}
