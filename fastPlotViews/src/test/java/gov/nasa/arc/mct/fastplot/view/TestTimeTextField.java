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

import gov.nasa.arc.mct.fastplot.bridge.PlotConstants;
import gov.nasa.arc.mct.fastplot.view.TimeTextField.TimeVerifier;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.swing.text.MaskFormatter;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestTimeTextField {

	private MaskFormatter formatter;
	private DecimalFormat decimalFormat = new DecimalFormat("00");
	private DecimalFormat dayFormat = new DecimalFormat("000");
	private GregorianCalendar calendar;

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
		TimeTextField field = new TimeTextField(formatter);
		calendar = new GregorianCalendar();
		calendar.setTimeZone(TimeZone.getTimeZone(PlotConstants.DEFAULT_TIME_ZONE));
		int day = calendar.get(Calendar.DAY_OF_YEAR);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		field.setTime(calendar);
		Assert.assertEquals(field.getValue(), dayFormat.format(day) + "/" +
				decimalFormat.format(hour) + ":" + decimalFormat.format(minute) + ":" +
				decimalFormat.format(second));
		long difference = calendar.getTimeInMillis() - field.getValueInMillis();
		Assert.assertTrue(Math.abs(difference) < 1000);
	}

	@Test
	public void testVerifier() {
		TimeTextField field = new TimeTextField(formatter);
		TimeVerifier verifier = field.new TimeVerifier();

		field.setText("000/08:22:51");
		Assert.assertFalse(verifier.verify(field));
		field.setText("001/08:22:51");
		Assert.assertTrue(verifier.verify(field));
		field.setText("020/00:59:59");
		Assert.assertTrue(verifier.verify(field));
		field.setText("365/23:59:00");
		Assert.assertTrue(verifier.verify(field));
		field.setText("365/23:60:00");
		Assert.assertTrue(verifier.verify(field));
		if (calendar.isLeapYear(calendar.get(Calendar.YEAR))) {
			Assert.assertEquals("366/00:00:00", field.getValue().toString());
		} else {
			Assert.assertEquals("001/00:00:00", field.getValue().toString());
		}
		field.setText("365/23:59:60");
		Assert.assertTrue(verifier.verify(field));
		if (calendar.isLeapYear(calendar.get(Calendar.YEAR))) {
			Assert.assertEquals("366/00:00:00", field.getValue().toString());
		} else {
			Assert.assertEquals("001/00:00:00", field.getValue().toString());
		}
		field.setText("500/08:22:51");
		Assert.assertTrue(verifier.verify(field));
	}
}
