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
 * TreeUtil.java Jan 6, 2009
 *
 * This code is property of the National Aeronautics and Space Administration and was
 * produced for the Mission Control Technologies (MCT) Project.
 *
 */
package gov.nasa.arc.mct.util;

import java.awt.Point;

import javax.swing.JTree;

/**
 * Tree utility helper.
 * @author atomo
 *
 */
public class TreeUtil {

	/**
	 * Determines whether a point is on a row in a tree, or is outside the tree rows.
	 * @param tree - the JTree.
	 * @param location - Point location.
	 * @return true if the point is on a row.
	 */
	public static boolean isPointOnTree(JTree tree, Point location) {
		return (tree.getRowForLocation(location.x, location.y) != -1);
	}

	/**
	 * Checks whether there's a point on the tree.
	 * @param tree - the JTree.
	 * @param x - number.
	 * @param y - number.
	 * @return boolean - flag to check whether the point is on the tree.
	 */
	public static boolean isPointOnTree(JTree tree, int x, int y) {
		return (tree.getRowForLocation(x, y) != -1);
	}
}
