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

import static javax.swing.SwingConstants.CENTER;
import static javax.swing.SwingConstants.LEADING;
import static javax.swing.SwingConstants.LEFT;
import static javax.swing.SwingConstants.RIGHT;
import static javax.swing.SwingConstants.TOP;
import static javax.swing.SwingConstants.TRAILING;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Component.BaselineResizeBehavior;
import java.awt.geom.AffineTransform;
import java.util.StringTokenizer;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicLabelUI;

/**
 * This code was found at http://codeguru.earthweb.com/java/articles/198.shtml.
 * This was posted by Zafir Anjum on 28-Jul-1999.
 * Modified by Adam Crume.
 */
public class MultiLineLabelUI extends BasicLabelUI {
	public static final String ROTATION_KEY = Rotation.class.getName();

	public static final MultiLineLabelUI labelUI = new MultiLineLabelUI();


	protected String layoutCL(JLabel label, FontMetrics fontMetrics, String text, Icon icon, Rectangle viewR,
			Rectangle iconR, Rectangle textR) {
		String s = layoutCompoundLabel((JComponent) label, fontMetrics, splitStringByLines(text), icon, label
				.getVerticalAlignment(), label.getHorizontalAlignment(), label.getVerticalTextPosition(), label
				.getHorizontalTextPosition(), viewR, iconR, textR, label.getIconTextGap());

		if(s.equals(""))
			return text;
		return s;
	}


	/**
	 * Compute and return the location of the icons origin, the
	 * location of origin of the text baseline, and a possibly clipped
	 * version of the compound labels string.  Locations are computed
	 * relative to the viewR rectangle.
	 * The JComponents orientation (LEADING/TRAILING) will also be taken
	 * into account and translated into LEFT/RIGHT values accordingly.
	 */
	public static String layoutCompoundLabel(JComponent c, FontMetrics fm, String[] text, Icon icon,
			int verticalAlignment, int horizontalAlignment, int verticalTextPosition, int horizontalTextPosition,
			Rectangle viewR, Rectangle iconR, Rectangle textR, int textIconGap) {
		boolean orientationIsLeftToRight = true;
		int hAlign = horizontalAlignment;
		int hTextPos = horizontalTextPosition;


		if(c != null) {
			if(!(c.getComponentOrientation().isLeftToRight())) {
				orientationIsLeftToRight = false;
			}
		}


		// Translate LEADING/TRAILING values in horizontalAlignment
		// to LEFT/RIGHT values depending on the components orientation
		switch(horizontalAlignment) {
		case LEADING:
			hAlign = (orientationIsLeftToRight) ? LEFT : RIGHT;
			break;
		case TRAILING:
			hAlign = (orientationIsLeftToRight) ? RIGHT : LEFT;
			break;
		}

		// Translate LEADING/TRAILING values in horizontalTextPosition
		// to LEFT/RIGHT values depending on the components orientation
		switch(horizontalTextPosition) {
		case LEADING:
			hTextPos = (orientationIsLeftToRight) ? LEFT : RIGHT;
			break;
		case TRAILING:
			hTextPos = (orientationIsLeftToRight) ? RIGHT : LEFT;
			break;
		}

		return layoutCompoundLabel(fm, text, icon, verticalAlignment, hAlign, verticalTextPosition, hTextPos, viewR,
				iconR, textR, textIconGap);
	}


	/**
	 * Compute and return the location of the icons origin, the
	 * location of origin of the text baseline, and a possibly clipped
	 * version of the compound labels string.  Locations are computed
	 * relative to the viewR rectangle.
	 * This layoutCompoundLabel() does not know how to handle LEADING/TRAILING
	 * values in horizontalTextPosition (they will default to RIGHT) and in
	 * horizontalAlignment (they will default to CENTER).
	 * Use the other version of layoutCompoundLabel() instead.
	 */
	public static String layoutCompoundLabel(FontMetrics fm, String[] text, Icon icon, int verticalAlignment,
			int horizontalAlignment, int verticalTextPosition, int horizontalTextPosition, Rectangle viewR,
			Rectangle iconR, Rectangle textR, int textIconGap) {
		/* Initialize the icon bounds rectangle iconR.
		 */

		if(icon != null) {
			iconR.width = icon.getIconWidth();
			iconR.height = icon.getIconHeight();
		} else {
			iconR.width = iconR.height = 0;
		}

		/* Initialize the text bounds rectangle textR.  If a null
		 * or and empty String was specified we substitute "" here
		 * and use 0,0,0,0 for textR.
		 */

		// Fix for textIsEmpty sent by Paulo Santos
		boolean textIsEmpty = (text == null) || (text.length == 0)
				|| (text.length == 1 && ((text[0] == null) || text[0].equals("")));

		String rettext = "";
		if(textIsEmpty) {
			textR.width = textR.height = 0;
		} else {
			Dimension dim = computeMultiLineDimension(fm, text);
			textR.width = dim.width;
			textR.height = dim.height;
		}

		/* Unless both text and icon are non-null, we effectively ignore
		 * the value of textIconGap.  The code that follows uses the
		 * value of gap instead of textIconGap.
		 */

		int gap = (textIsEmpty || (icon == null)) ? 0 : textIconGap;

		if(!textIsEmpty) {

			/* If the label text string is too wide to fit within the available
			 * space "..." and as many characters as will fit will be
			 * displayed instead.
			 */

			int availTextWidth;

			if(horizontalTextPosition == CENTER) {
				availTextWidth = viewR.width;
			} else {
				availTextWidth = viewR.width - (iconR.width + gap);
			}


			if(textR.width > availTextWidth && text.length == 1) {
				String clipString = "...";
				int totalWidth = SwingUtilities.computeStringWidth(fm, clipString);
				int nChars;
				for(nChars = 0; nChars < text[0].length(); nChars++) {
					totalWidth += fm.charWidth(text[0].charAt(nChars));
					if(totalWidth > availTextWidth) {
						break;
					}
				}
				rettext = text[0].substring(0, nChars) + clipString;
				textR.width = SwingUtilities.computeStringWidth(fm, rettext);
			}
		}


		/* Compute textR.x,y given the verticalTextPosition and
		 * horizontalTextPosition properties
		 */

		if(verticalTextPosition == TOP) {
			if(horizontalTextPosition != CENTER) {
				textR.y = 0;
			} else {
				textR.y = -(textR.height + gap);
			}
		} else if(verticalTextPosition == CENTER) {
			textR.y = (iconR.height / 2) - (textR.height / 2);
		} else { // (verticalTextPosition == BOTTOM)
			if(horizontalTextPosition != CENTER) {
				textR.y = iconR.height - textR.height;
			} else {
				textR.y = (iconR.height + gap);
			}
		}

		if(horizontalTextPosition == LEFT) {
			textR.x = -(textR.width + gap);
		} else if(horizontalTextPosition == CENTER) {
			textR.x = (iconR.width / 2) - (textR.width / 2);
		} else { // (horizontalTextPosition == RIGHT)
			textR.x = (iconR.width + gap);
		}

		/* labelR is the rectangle that contains iconR and textR.
		 * Move it to its proper position given the labelAlignment
		 * properties.
		 *
		 * To avoid actually allocating a Rectangle, Rectangle.union
		 * has been inlined below.
		 */
		int labelR_x = Math.min(iconR.x, textR.x);
		int labelR_width = Math.max(iconR.x + iconR.width, textR.x + textR.width) - labelR_x;
		int labelR_y = Math.min(iconR.y, textR.y);
		int labelR_height = Math.max(iconR.y + iconR.height, textR.y + textR.height) - labelR_y;

		int dx, dy;

		if(verticalAlignment == TOP) {
			dy = viewR.y - labelR_y;
		} else if(verticalAlignment == CENTER) {
			dy = (viewR.y + (viewR.height / 2)) - (labelR_y + (labelR_height / 2));
		} else { // (verticalAlignment == BOTTOM)
			dy = (viewR.y + viewR.height) - (labelR_y + labelR_height);
		}

		if(horizontalAlignment == LEFT) {
			dx = viewR.x - labelR_x;
		} else if(horizontalAlignment == RIGHT) {
			dx = (viewR.x + viewR.width) - (labelR_x + labelR_width);
		} else { // (horizontalAlignment == CENTER)
			dx = (viewR.x + (viewR.width / 2)) - (labelR_x + (labelR_width / 2));
		}

		/* Translate textR and glypyR by dx,dy.
		 */

		textR.x += dx;
		textR.y += dy;

		iconR.x += dx;
		iconR.y += dy;

		return rettext;
	}


	protected void paintEnabledText(JLabel l, Graphics g, String s, int textX, int textY) {
		int accChar = l.getDisplayedMnemonic();
		g.setColor(l.getForeground());
		drawString(g, s, accChar, textX, textY);
	}


	protected void paintDisabledText(JLabel l, Graphics g, String s, int textX, int textY) {
		int accChar = l.getDisplayedMnemonic();
		g.setColor(l.getBackground());
		drawString(g, s, accChar, textX, textY);
	}


	protected void drawString(Graphics g, String s, int accChar, int textX, int textY) {
		if(s.indexOf('\n') == -1)
			BasicGraphicsUtils.drawString(g, s, accChar, textX, textY);
		else {
			String[] strs = splitStringByLines(s);
			int height = g.getFontMetrics().getHeight();
			// Only the first line can have the accel char
			BasicGraphicsUtils.drawString(g, strs[0], accChar, textX, textY);
			for(int i = 1; i < strs.length; i++)
				g.drawString(strs[i], textX, textY + (height * i));
		}
	}


	public static Dimension computeMultiLineDimension(FontMetrics fm, String[] strs) {
		int i, c, width = 0;
		for(i = 0, c = strs.length; i < c; i++)
			width = Math.max(width, SwingUtilities.computeStringWidth(fm, strs[i]));
		return new Dimension(width, fm.getHeight() * strs.length);
	}


	protected String str;

	protected String[] strs;


	public String[] splitStringByLines(String str) {
		if(str.equals(this.str))
			return strs;

		this.str = str;

		int lines = 1;
		int i, c;
		for(i = 0, c = str.length(); i < c; i++) {
			if(str.charAt(i) == '\n')
				lines++;
		}
		strs = new String[lines];
		StringTokenizer st = new StringTokenizer(str, "\n");

		int line = 0;
		while(st.hasMoreTokens())
			strs[line++] = st.nextToken();

		return strs;
	}


	@Override
	public int getBaseline(JComponent c, int width, int height) {
		Rotation rotation = (Rotation) c.getClientProperty(ROTATION_KEY);
		if(rotation == Rotation.CCW || rotation == Rotation.CW) {
			return -1;
		} else if(rotation == Rotation.HALF) {
			int baseline = super.getBaseline(c, width, height);
			if(baseline < 0) {
				return baseline;
			} else {
				return height - baseline;
			}
		}
		return super.getBaseline(c, width, height);
	}


	@Override
	public BaselineResizeBehavior getBaselineResizeBehavior(JComponent c) {
		Rotation rotation = (Rotation) c.getClientProperty(ROTATION_KEY);
		if(rotation == Rotation.CCW || rotation == Rotation.CW) {
			return BaselineResizeBehavior.OTHER;
		} else if(rotation == Rotation.HALF) {
			BaselineResizeBehavior b = super.getBaselineResizeBehavior(c);
			if(b == BaselineResizeBehavior.CONSTANT_ASCENT) {
				return BaselineResizeBehavior.CONSTANT_DESCENT;
			} else if(b == BaselineResizeBehavior.CONSTANT_DESCENT) {
				return BaselineResizeBehavior.CONSTANT_ASCENT;
			} else {
				return b;
			}
		}
		return super.getBaselineResizeBehavior(c);
	}

	private static Rectangle paintIconR = new Rectangle();

	private static Rectangle paintTextR = new Rectangle();

	private static Rectangle paintViewR = new Rectangle();

	private static Insets paintViewInsets = new Insets(0, 0, 0, 0);


	@Override
	public void paint(Graphics g, JComponent c) {
		// This method adapted from http://tech.chitgoks.com/2009/11/13/rotate-jlabel-vertically/
		JLabel label = (JLabel) c;
		String text = label.getText();
		Icon icon = (label.isEnabled()) ? label.getIcon() : label.getDisabledIcon();

		if((icon == null) && (text == null)) {
			return;
		}

		Rotation rotation = getRotation(c);
		FontMetrics fm = g.getFontMetrics();
		paintViewInsets = c.getInsets(paintViewInsets);

		paintViewR.x = paintViewInsets.left;
		paintViewR.y = paintViewInsets.top;

		if(rotation.isXYSwitched()) {
			paintViewR.height = c.getWidth() - (paintViewInsets.left + paintViewInsets.right);
			paintViewR.width = c.getHeight() - (paintViewInsets.top + paintViewInsets.bottom);
		} else {
			paintViewR.width = c.getWidth() - (paintViewInsets.left + paintViewInsets.right);
			paintViewR.height = c.getHeight() - (paintViewInsets.top + paintViewInsets.bottom);
		}

		paintIconR.x = paintIconR.y = paintIconR.width = paintIconR.height = 0;
		paintTextR.x = paintTextR.y = paintTextR.width = paintTextR.height = 0;

		String clippedText = layoutCL(label, fm, text, icon, paintViewR, paintIconR, paintTextR);

		Graphics2D g2 = (Graphics2D) g;
		AffineTransform tr = g2.getTransform();
		switch(rotation) {
		case NONE:
			break;
		case CW:
			g2.rotate(Math.PI / 2);
			g2.translate(0, -c.getWidth());
			break;
		case CCW:
			g2.rotate(-Math.PI / 2);
			g2.translate(-c.getHeight(), 0);
			break;
		case HALF:
			g2.rotate(Math.PI);
			g2.translate(-c.getWidth(), -c.getHeight());
			break;
		}

		if(icon != null) {
			icon.paintIcon(c, g, paintIconR.x, paintIconR.y);
		}

		if(text != null) {
			int textX = paintTextR.x;
			int textY = paintTextR.y + fm.getAscent();

			if(label.isEnabled()) {
				paintEnabledText(label, g, clippedText, textX, textY);
			} else {
				paintDisabledText(label, g, clippedText, textX, textY);
			}
		}
		g2.setTransform(tr);
	}


	private Rotation getRotation(JComponent c) {
		Rotation rotation = (Rotation) c.getClientProperty(ROTATION_KEY);
		if(rotation == null) {
			rotation = Rotation.NONE;
		}
		return rotation;
	}


	@Override
	public Dimension getPreferredSize(JComponent c) {
		Dimension d = super.getPreferredSize(c);
		if(getRotation(c).isXYSwitched()) {
			d.setSize(d.getHeight(), d.getWidth());
		}
		return d;
	}
}
