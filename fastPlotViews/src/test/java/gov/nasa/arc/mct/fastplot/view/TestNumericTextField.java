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

import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestNumericTextField {

	@Test
	public void testCreation() {
		// Try main constructor
		DecimalFormat myFormat = new DecimalFormat("###");
		NumericTextField textField = new NumericTextField("11", 10, myFormat);
		Assert.assertEquals(textField.getFormat(), myFormat);
		DecimalFormat otherFormat = new DecimalFormat("###.###");
		textField.setFormat(otherFormat);
		textField.formatChanged();
		Assert.assertEquals(textField.getFormat(), otherFormat);

		// Other constructors
		NumericTextField tField1 = new NumericTextField("99");
		Assert.assertEquals(tField1.getColumns(), 0);
		NumericTextField tField2 = new NumericTextField("99", 7);
		Assert.assertEquals(tField2.getColumns(), 7);
		NumericTextField tField3 = new NumericTextField();
		Assert.assertEquals(tField3.getColumns(), 0);
		NumericTextField tField4 = new NumericTextField(1, otherFormat);
		Assert.assertEquals(tField4.getColumns(), 1);

		
		/*public NumericTextField(int columns, DecimalFormat format) {
			this(null, columns, format);
		}*/
		
		// Try setValue(*)s
		double valueDouble = 22.;
		double shadowDouble = 22.;
		textField.setValue(valueDouble);
		try {
			Assert.assertEquals(textField.getDoubleValue(), shadowDouble);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long valueLong = 33;
		Long shadowLong = Long.valueOf(33);
		textField.setValue(valueLong);
		try {
			Assert.assertEquals(textField.getLongValue(), shadowLong);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Number valueNumber = Long.valueOf(44);
		Number shadowNumber = Long.valueOf(44);
		textField.setValue(valueNumber);
		try {
			Assert.assertEquals(textField.getNumberValue(), shadowNumber);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
			textField.normalize();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		textField.insertFailed(null, 0, null, null);

		// NumericPlainDocument
		NumericPlainDocument document = (NumericPlainDocument) textField.getDocument();

		// Try insertString()
		AttributeSet a = null;
		String sampleString = "-76";
		try {
			document.insertString(0, sampleString, a);
			document.insertString(0, "+98", a);
			document.insertString(0, "-+888", a);
			document.insertString(0, "ewr90", a);
			document.insertString(0, "-85.0", a);
			document.insertString(0, "-", a);
			document.insertString(0, "(85.0$", a);
			document.insertString(0, "85.", a);
			document.insertString(0, ".3426", a);
			document.insertString(0, "34,26", a);
			document.insertString(0, "34,260.3", a);
			document.insertString(0, "", a);
			document.insertString(0, null, a);
			document.insertString(0, "2397,", a);
			document.insertString(0, "2,397,", a);
			document.insertString(0, "2.397,", a);
			DecimalFormat intFormat = new DecimalFormat("###");
			intFormat.setParseIntegerOnly(false);
			document.setFormat(intFormat);
			document.insertString(0, "123.", a);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		document.removeInsertErrorListener(textField);
		Assert.assertNull(document.getInsertErrorListener());
	}
}
