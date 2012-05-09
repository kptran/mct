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

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

/**
 * Acts as a proxy for a {@link Graphics2D} object.
 * All calls are passed directly to the delegate except {@link #create()} and {@link #create(int, int, int, int)}.
 * @author Adam Crume
 */
public class GraphicsProxy extends Graphics2D {
	/** The delegate that all painting calls are forwarded to. */
	protected final Graphics2D base;


	/**
	 * Creates a graphics proxy.
	 * @param base base graphics object, a.k.a. the delegate 
	 */
	public GraphicsProxy(Graphics2D base) {
		this.base = base;
	}


	public void addRenderingHints(Map<?, ?> hints) {
		base.addRenderingHints(hints);
	}


	public void clearRect(int x, int y, int width, int height) {
		base.clearRect(x, y, width, height);
	}


	public void clip(Shape s) {
		base.clip(s);
	}


	public void clipRect(int x, int y, int width, int height) {
		base.clipRect(x, y, width, height);
	}


	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		base.copyArea(x, y, width, height, dx, dy);
	}


	public void draw(Shape s) {
		base.draw(s);
	}


	public void draw3DRect(int x, int y, int width, int height, boolean raised) {
		base.draw3DRect(x, y, width, height, raised);
	}


	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		base.drawArc(x, y, width, height, startAngle, arcAngle);
	}


	public void drawBytes(byte[] data, int offset, int length, int x, int y) {
		base.drawBytes(data, offset, length, x, y);
	}


	public void drawChars(char[] data, int offset, int length, int x, int y) {
		base.drawChars(data, offset, length, x, y);
	}


	public void drawGlyphVector(GlyphVector g, float x, float y) {
		base.drawGlyphVector(g, x, y);
	}


	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		base.drawImage(img, op, x, y);
	}


	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		return base.drawImage(img, xform, obs);
	}


	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
		return base.drawImage(img, x, y, bgcolor, observer);
	}


	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		return base.drawImage(img, x, y, observer);
	}


	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
		return base.drawImage(img, x, y, width, height, bgcolor, observer);
	}


	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
		return base.drawImage(img, x, y, width, height, observer);
	}


	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
		return base.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
	}


	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		return base.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
	}


	public void drawLine(int x1, int y1, int x2, int y2) {
		base.drawLine(x1, y1, x2, y2);
	}


	public void drawOval(int x, int y, int width, int height) {
		base.drawOval(x, y, width, height);
	}


	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		base.drawPolygon(xPoints, yPoints, nPoints);
	}


	public void drawPolygon(Polygon p) {
		base.drawPolygon(p);
	}


	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		base.drawPolyline(xPoints, yPoints, nPoints);
	}


	public void drawRect(int x, int y, int width, int height) {
		base.drawRect(x, y, width, height);
	}


	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		base.drawRenderableImage(img, xform);
	}


	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		base.drawRenderedImage(img, xform);
	}


	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		base.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
	}


	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		base.drawString(iterator, x, y);
	}


	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		base.drawString(iterator, x, y);
	}


	public void drawString(String str, float x, float y) {
		base.drawString(str, x, y);
	}


	public void drawString(String str, int x, int y) {
		base.drawString(str, x, y);
	}


	public void fill(Shape s) {
		base.fill(s);
	}


	public void fill3DRect(int x, int y, int width, int height, boolean raised) {
		base.fill3DRect(x, y, width, height, raised);
	}


	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		base.fillArc(x, y, width, height, startAngle, arcAngle);
	}


	public void fillOval(int x, int y, int width, int height) {
		base.fillOval(x, y, width, height);
	}


	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		base.fillPolygon(xPoints, yPoints, nPoints);
	}


	public void fillPolygon(Polygon p) {
		base.fillPolygon(p);
	}


	public void fillRect(int x, int y, int width, int height) {
		base.fillRect(x, y, width, height);
	}


	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		base.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
	}


	public Color getBackground() {
		return base.getBackground();
	}


	public Shape getClip() {
		return base.getClip();
	}


	public Rectangle getClipBounds() {
		return base.getClipBounds();
	}


	public Rectangle getClipBounds(Rectangle r) {
		return base.getClipBounds(r);
	}


	@Deprecated
	public Rectangle getClipRect() {
		return base.getClipRect();
	}


	public Color getColor() {
		return base.getColor();
	}


	public Composite getComposite() {
		return base.getComposite();
	}


	public GraphicsConfiguration getDeviceConfiguration() {
		return base.getDeviceConfiguration();
	}


	public Font getFont() {
		return base.getFont();
	}


	public FontMetrics getFontMetrics() {
		return base.getFontMetrics();
	}


	public FontMetrics getFontMetrics(Font f) {
		return base.getFontMetrics(f);
	}


	public FontRenderContext getFontRenderContext() {
		return base.getFontRenderContext();
	}


	public Paint getPaint() {
		return base.getPaint();
	}


	public Object getRenderingHint(Key hintKey) {
		return base.getRenderingHint(hintKey);
	}


	public RenderingHints getRenderingHints() {
		return base.getRenderingHints();
	}


	public Stroke getStroke() {
		return base.getStroke();
	}


	public AffineTransform getTransform() {
		return base.getTransform();
	}


	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		return base.hit(rect, s, onStroke);
	}


	public boolean hitClip(int x, int y, int width, int height) {
		return base.hitClip(x, y, width, height);
	}


	public void rotate(double theta, double x, double y) {
		base.rotate(theta, x, y);
	}


	public void rotate(double theta) {
		base.rotate(theta);
	}


	public void scale(double sx, double sy) {
		base.scale(sx, sy);
	}


	public void setBackground(Color color) {
		base.setBackground(color);
	}


	public void setClip(int x, int y, int width, int height) {
		base.setClip(x, y, width, height);
	}


	public void setClip(Shape clip) {
		base.setClip(clip);
	}


	public void setColor(Color c) {
		base.setColor(c);
	}


	public void setComposite(Composite comp) {
		base.setComposite(comp);
	}


	public void setFont(Font font) {
		base.setFont(font);
	}


	public void setPaint(Paint paint) {
		base.setPaint(paint);
	}


	public void setPaintMode() {
		base.setPaintMode();
	}


	public void setRenderingHint(Key hintKey, Object hintValue) {
		base.setRenderingHint(hintKey, hintValue);
	}


	public void setRenderingHints(Map<?, ?> hints) {
		base.setRenderingHints(hints);
	}


	public void setStroke(Stroke s) {
		base.setStroke(s);
	}


	public void setTransform(AffineTransform Tx) {
		base.setTransform(Tx);
	}


	public void setXORMode(Color c1) {
		base.setXORMode(c1);
	}


	public void shear(double shx, double shy) {
		base.shear(shx, shy);
	}


	public void transform(AffineTransform Tx) {
		base.transform(Tx);
	}


	public void translate(double tx, double ty) {
		base.translate(tx, ty);
	}


	public void translate(int x, int y) {
		base.translate(x, y);
	}


	/**
	 * Returns a new GraphicsProxy which wraps the return value of <code>base.create()</code>.
	 * @return new proxy graphics object
	 */
	@Override
	public Graphics create() {
		return new GraphicsProxy((Graphics2D) base.create());
	}


	/**
	 * Returns a new GraphicsProxy which wraps the return value of <code>base.create(x, y, width, height)</code>.
	 * @return new proxy graphics object
	 */
	@Override
	public Graphics create(int x, int y, int width, int height) {
		return new GraphicsProxy((Graphics2D) base.create(x, y, width, height));
	}


	@Override
	public void dispose() {
		base.dispose();
	}
}
