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

import gov.nasa.arc.mct.fastplot.view.TimeSpanTextField.DurationVerifier;

import java.text.ParseException;

import javax.swing.text.MaskFormatter;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestTimeSpanTextField {

	private MaskFormatter formatter;

	@BeforeClass
	public void setup() {
        formatter = null;
		try {
			formatter = new MaskFormatter("###/##:##:##");
			formatter.setPlaceholderCharacter('0');
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testTextFieldValues() {
		TimeSpanTextField field = new TimeSpanTextField(formatter);
		TimeDuration duration = new TimeDuration(1, 2, 3, 4);
		field.setTime(duration);
		Assert.assertEquals(field.getDayOfYear(), 1);
		Assert.assertEquals(field.getHourOfDay(), 2);
		Assert.assertEquals(field.getMinute(), 3);
		Assert.assertEquals(field.getSecond(), 4);
	}

	@Test
	public void testVerifier() {
		TimeSpanTextField field = new TimeSpanTextField(formatter);
		DurationVerifier verifier = field.new DurationVerifier();

		field.setText("000/08:22:51");
		Assert.assertTrue(verifier.verify(field));
		field.setText("020/00:59:59");
		Assert.assertTrue(verifier.verify(field));
		field.setText("365/23:59:00");
		Assert.assertTrue(verifier.verify(field));
		field.setText("365/24:59:00");
		Assert.assertTrue(verifier.verify(field));
		Assert.assertEquals(field.getValue().toString(), "366/00:59:00");		
		field.setText("365/23:60:00");
		Assert.assertTrue(verifier.verify(field));
		Assert.assertEquals(field.getValue().toString(), "366/00:00:00");
		field.setText("365/23:59:60");
		Assert.assertTrue(verifier.verify(field));
		Assert.assertEquals(field.getValue().toString(), "366/00:00:00");
		field.setText("366/23:59:60");
		Assert.assertFalse(verifier.verify(field));
		field.setText("500/08:22:51");
		Assert.assertFalse(verifier.verify(field));
		field.setText("000/08:22:51");
		Assert.assertTrue(verifier.verify(field));
		Assert.assertEquals(field.getValue().toString(), "000/08:22:51");
		Assert.assertEquals(field.getDayOfYear(), 0);
		Assert.assertEquals(field.getHourOfDay(), 8);
		Assert.assertEquals(field.getMinute(), 22);
		Assert.assertEquals(field.getSecond(), 51);
		Assert.assertTrue(field.getSubYearValue() > 0);
	}
}