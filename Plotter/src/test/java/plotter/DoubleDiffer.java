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

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

/**
 * Helper class for asserting that doubles are nearly equal.
 * @author Adam Crume
 */
public class DoubleDiffer {
	/** Allowable difference between doubles. */
	double error;


	/**
	 * Creates a {@link DoubleDiffer} with the given tolerance
	 * @param error maximum allowable difference between doubles
	 */
	public DoubleDiffer(double error) {
		this.error = error;
	}


	/**
	 * Asserts that the absolute value of the difference between the doubles is less than the tolerance.
	 * @param expected expected value
	 * @param actual actual value
	 * @throws AssertionFailedError if the doubles are farther apart than the tolerance allows
	 */
	public void assertClose(double expected, double actual) {
		Assert.assertTrue("Expected: " + expected + ", actual: " + actual, Math.abs(expected - actual) < error);
	}
}
