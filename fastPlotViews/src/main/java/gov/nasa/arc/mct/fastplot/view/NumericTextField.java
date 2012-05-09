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
import java.text.ParsePosition;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class subclasses JTextField to provide numeric verification. It does not display invalid keystrokes.
 *
 */
public class NumericTextField extends JTextField implements NumericPlainDocument.InsertErrorListener {
    private static final long serialVersionUID = 1L;
    
	public NumericTextField() {
		this(null, 0, null);
	}

	public NumericTextField(String text, int columns, DecimalFormat format) {
		super(null, text, columns);
		setHorizontalAlignment(JTextField.RIGHT);

		NumericPlainDocument numericDoc = (NumericPlainDocument) getDocument();
		if (format != null) {
			numericDoc.setFormat(format);
		}

		numericDoc.addInsertErrorListener(this);
	}

	public NumericTextField(int columns, DecimalFormat format) {
		this(null, columns, format);
	}

	public NumericTextField(String text) {
		this(text, 0, null);
	}

	public NumericTextField(String text, int columns) {
		this(text, columns, null);
	}

	public void setFormat(DecimalFormat format) {
		((NumericPlainDocument) getDocument()).setFormat(format);
	}

	public DecimalFormat getFormat() {
		return ((NumericPlainDocument) getDocument()).getFormat();
	}

	public void formatChanged() {
		// Notify change of format attributes.
		setFormat(getFormat());
	}

	// Methods to get the field value
	public Long getLongValue() throws ParseException {
		return ((NumericPlainDocument) getDocument()).getLongValue();
	}

	public Double getDoubleValue() throws ParseException {
		return ((NumericPlainDocument) getDocument()).getDoubleValue();
	}

	public Number getNumberValue() throws ParseException {
		return ((NumericPlainDocument) getDocument()).getNumberValue();
	}

	// Methods to install numeric values
	public void setValue(Number number) {
		setText(getFormat().format(number));
	}

	public void setValue(long l) {
		setText(getFormat().format(l));
	}

	public void setValue(double d) {
		setText(getFormat().format(d));
	}

	public void normalize() throws ParseException {
		// format the value according to the format string
		setText(getFormat().format(getNumberValue()));
	}

	// Override to handle insertion error
	public void insertFailed(NumericPlainDocument doc, int offset, String str,
			AttributeSet a) {
	}

	// Method to create default model
	protected Document createDefaultModel() {
		return new NumericPlainDocument();
	}

}


class NumericPlainDocument extends PlainDocument {
    private static final long serialVersionUID = 1L;

    private final static Logger logger = LoggerFactory.getLogger(NumericPlainDocument.class);
    
	public NumericPlainDocument() {
		setFormat(null);
	}

	public void setFormat(DecimalFormat fmt) {
		this.format = fmt != null ? fmt : (DecimalFormat) defaultFormat.clone();

		decimalSeparator = format.getDecimalFormatSymbols().getDecimalSeparator();
		groupingSeparator = format.getDecimalFormatSymbols().getGroupingSeparator();
		positivePrefix = format.getPositivePrefix();
		positivePrefixLen = positivePrefix.length();
		negativePrefix = format.getNegativePrefix();
		negativePrefixLen = negativePrefix.length();
		positiveSuffix = format.getPositiveSuffix();
		positiveSuffixLen = positiveSuffix.length();
		negativeSuffix = format.getNegativeSuffix();
		negativeSuffixLen = negativeSuffix.length();
	}

	public DecimalFormat getFormat() {
		return format;
	}

	public Number getNumberValue() throws ParseException {
		try {
			String content = getText(0, getLength());
			parsePos.setIndex(0);
			Number result = format.parse(content, parsePos);
			if (parsePos.getIndex() != getLength()) {
				throw new ParseException("Not a valid number: " + content, 0);
			}

			return result;
		} catch (BadLocationException e) {
			throw new ParseException("Not a valid number", 0);
		}
	}

	public Long getLongValue() throws ParseException {
		Number result = getNumberValue();
		if ((result instanceof Long) == false) {
			throw new ParseException("Not a valid long", 0);
		}

		return (Long) result;
	}

	public Double getDoubleValue() throws ParseException {
		Number result = getNumberValue();
		if ((result instanceof Long) == false
				&& (result instanceof Double) == false) {
			throw new ParseException("Not a valid double", 0);
		}

		if (result instanceof Long) {
			result = Double.valueOf(result.doubleValue());
		}

		return (Double) result;
	}

	public void insertString(int offset, String str, AttributeSet a)
			throws BadLocationException {
		if (str == null || str.length() == 0) {
			return;
		}

		Content content = getContent();
		int length = content.length();
		int originalLength = length;

		parsePos.setIndex(0);

		// Create the result of inserting the new data,
		// but ignore the trailing newline
		String targetString = content.getString(0, offset) + str
				+ content.getString(offset, length - offset - 1);

		// Parse the input string and check for errors
		do {
			boolean gotPositive = targetString.startsWith(positivePrefix);
			boolean gotNegative = targetString.startsWith(negativePrefix);

			length = targetString.length();

			// If we have a valid prefix, the parse fails if the
			// suffix is not present and the error is reported
			// at index 0. So, we need to add the appropriate
			// suffix if it is not present at this point.
			if (gotPositive == true || gotNegative == true) {
				int prefixLength;

				if (gotPositive == true && gotNegative == true) {
					// This happens if one is the leading part of
					// the other - e.g. if one is "(" and the other "(("
					if (positivePrefixLen > negativePrefixLen) {
						gotNegative = false;
					} else {
						gotPositive = false;
					}
				}

				if (gotPositive == true) {
					prefixLength = positivePrefixLen;
				} else {
					// Must have the negative prefix
					prefixLength = negativePrefixLen;
				}

				// If the string consists of the prefix alone,
				// do nothing, or the result won't parse.
				if (length == prefixLength) {
					break;
				}
			}

            try {			
			    format.parse(targetString, parsePos);
            } catch (NumberFormatException e) {
			    logger.error("Number format exception when parsing \n" +
			                 "   targetString = " + targetString + "\n" +
			                 "    parsePos = " + parsePos.toString());
			}

			int endIndex = parsePos.getIndex();
			if (endIndex == length) {
				break; // Number is acceptable
			}

			// Parse ended early
			// Since incomplete numbers don't always parse, try
			// to work out what went wrong.
			// First check for an incomplete positive prefix
			if (positivePrefixLen > 0 && endIndex < positivePrefixLen
					&& length <= positivePrefixLen
					&& targetString.regionMatches(0, positivePrefix, 0, length)) {
				break; // Accept for now
			}

			// Next check for an incomplete negative prefix
			if (negativePrefixLen > 0 && endIndex < negativePrefixLen
					&& length <= negativePrefixLen
					&& targetString.regionMatches(0, negativePrefix, 0, length)) {
				break; // Accept for now
			}

			// Allow a number that ends with the group
			// or decimal separator, if these are in use
			char lastChar = targetString.charAt(originalLength - 1);
			int decimalIndex = targetString.indexOf(decimalSeparator);
			if (format.isGroupingUsed() && lastChar == groupingSeparator
					&& decimalIndex == -1) {
				// Allow a "," but only in integer part
				break;
			}

			if (format.isParseIntegerOnly() == false
					&& lastChar == decimalSeparator
					&& decimalIndex == originalLength - 1) {
				// Allow a ".", but only one
				break;
			}

			// No more corrections to make: must be an error
			if (errorListener != null) {
				errorListener.insertFailed(this, offset, str, a);
			}
			return;
		} while (false);

		// Finally, add to the model
		super.insertString(offset, str, a);
	}

	public void addInsertErrorListener(InsertErrorListener l) {
		if (errorListener == null) {
			errorListener = l;
			return;
		}
		throw new IllegalArgumentException("InsertErrorListener already registered");
	}

	public void removeInsertErrorListener(InsertErrorListener l) {
		if (errorListener == l) {
			errorListener = null;
		}
	}

	InsertErrorListener getInsertErrorListener() {
		return errorListener;
	}

	public interface InsertErrorListener {
		public abstract void insertFailed(
				NumericPlainDocument doc, int offset, String str, AttributeSet a);
	}

	protected InsertErrorListener errorListener;

	protected DecimalFormat format;

	protected char decimalSeparator;

	protected char groupingSeparator;

	protected String positivePrefix;

	protected String negativePrefix;

	protected int positivePrefixLen;

	protected int negativePrefixLen;

	protected String positiveSuffix;

	protected String negativeSuffix;

	protected int positiveSuffixLen;

	protected int negativeSuffixLen;

	protected ParsePosition parsePos = new ParsePosition(0);

	protected static DecimalFormat defaultFormat = new DecimalFormat();
}

