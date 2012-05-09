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
/**
 * 
 */
package plotter;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

/**
 * Used to verify that lines drawn match expectations.
 * Endpoints of individual line segments may be switched (e.g. the lines (0,0)-(1,1) and (1,1)-(0,0) are equivalent),
 * and lines may be drawn in any order.
 * Example usage:
 * <pre>
 * {@link CountingGraphics} g = new CountingGraphics(...);
 * // draw on g
 * LineChecker c = new LineChecker();
 * c.require(19, 180, 39, 160);
 * c.require(159, 40, 179, 20);
 * c.check(g.getLines());
 * </pre> 
 * @author Adam Crume
 */
public class LineChecker {
	/** Maximum allowable distance between two points that count as a match. */
	private double error = .00001;

	/** Required line segments. */
	private List<Line2D> required = new ArrayList<Line2D>();

	/** Allowed but not required line segments. */
	private List<Line2D> allowed = new ArrayList<Line2D>();


	/**
	 * Adds a required line segment.
	 * @param x1 X coordinate of end 1
	 * @param y1 Y coordinate of end 1
	 * @param x2 X coordinate of end 2
	 * @param y2 Y coordinate of end 2
	 */
	public void require(double x1, double y1, double x2, double y2) {
		required.add(new Line2D.Double(x1, y1, x2, y2));
	}


	/**
	 * Adds an allowed but not required line segment.
	 * @param x1 X coordinate of end 1
	 * @param y1 Y coordinate of end 1
	 * @param x2 X coordinate of end 2
	 * @param y2 Y coordinate of end 2
	 */
	public void allow(double x1, double y1, double x2, double y2) {
		allowed.add(new Line2D.Double(x1, y1, x2, y2));
	}


	/**
	 * Verifies that the argument matches the requirements.
	 * In other words, all required lines must be present, and no extra lines may be present.
	 * @param lines lines to check
	 * @throws AssertionFailedError if the lines do not match the requirements
	 */
	public void check(List<Line2D> lines) {
		lines=new ArrayList<Line2D>(lines);
		List<Line2D> required=new ArrayList<Line2D>(this.required);
		for(Iterator<Line2D> reqItr = required.iterator(); reqItr.hasNext();) {
			Line2D requiredLine=reqItr.next();
			for(Iterator<Line2D> itr = lines.iterator(); itr.hasNext();) {
				Line2D line = itr.next();
				if(linesMatch(requiredLine, line)) {
					itr.remove();
					reqItr.remove();
					break;
				}
			}
		}

		for(Line2D allowedLine : allowed) {
			for(Iterator<Line2D> itr = lines.iterator(); itr.hasNext();) {
				Line2D line = itr.next();
				if(linesMatch(allowedLine, line)) {
					itr.remove();
					break;
				}
			}
		}

		if(!required.isEmpty() || !lines.isEmpty()) {
			StringBuffer msg = new StringBuffer();
			msg.append("Missing lines:\n");
			formatLines(msg, required);
			msg.append("Extra lines:\n");
			formatLines(msg, lines);
			Assert.fail(msg.toString());
		}
	}


	private void formatLines(StringBuffer msg, List<Line2D> lines) {
		for(Line2D line : lines) {
			msg.append("(");
			msg.append(line.getX1());
			msg.append(", ");
			msg.append(line.getY1());
			msg.append(") - (");
			msg.append(line.getX2());
			msg.append(", ");
			msg.append(line.getY2());
			msg.append(")\n");
		}
	}


	private boolean pointsMatch(double x1, double y1, double x2, double y2) {
		return Math.abs(x1 - x2) < error && Math.abs(y1 - y2) < error;
	}


	private boolean linesMatch(Line2D requiredLine, Line2D line) {
		double x1 = requiredLine.getX1();
		double y1 = requiredLine.getY1();
		double x2 = requiredLine.getX2();
		double y2 = requiredLine.getY2();
		double lineX1 = line.getX1();
		double lineY1 = line.getY1();
		double lineX2 = line.getX2();
		double lineY2 = line.getY2();
		// Check forward and backward
		if(pointsMatch(lineX1, lineY1, x1, y1) && pointsMatch(lineX2, lineY2, x2, y2)) {
			return true;
		}
		if(pointsMatch(lineX1, lineY1, x2, y2) && pointsMatch(lineX2, lineY2, x1, y1)) {
			return true;
		}
		return false;
	}
}
