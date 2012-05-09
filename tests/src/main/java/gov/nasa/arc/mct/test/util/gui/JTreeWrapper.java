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
package gov.nasa.arc.mct.test.util.gui;

import static org.testng.Assert.fail;

import javax.swing.JTree;

import org.fest.swing.core.Robot;
import org.fest.swing.fixture.JPopupMenuFixture;
import org.fest.swing.fixture.JTreeFixture;
import org.fest.swing.fixture.JTreeNodeFixture;

public class JTreeWrapper extends JTreeFixture {

	public JTreeWrapper(Robot robot, JTree target) {
		super(robot, target);
		separator(TestUtils.getTreePathSeparator());
	}

	public JTreeWrapper(Robot robot, String treeName) {
		super(robot, treeName);
		separator(TestUtils.getTreePathSeparator());
	}

	public int visibleRowCount() {
		int count = 0;
		
		for (;;) {
			try {
				node(count);
			} catch (Throwable ex) {
				return count;
			}
			++count;
		}
	}

	public void selectPath(Path path) {
		String oldSeparator = separator();
		try {
			separator(TestUtils.getTreePathSeparator());
			selectPath(path.toString(TestUtils.getTreePathSeparator()));
		} finally {
			separator(oldSeparator);
		}
	}
	
	public void drag(Path path) {
		drag(path.toString(separator()));
	}
	
	public void drop(Path path) {
		drop(path.toString(separator()));
	}
	
	public void requireSelection(Path path) {
		requireSelection(path.toString(separator()));
	}
	
	public JTreeNodeFixture node(Path path) {
		return node(path.toString(separator()));
	}
	
	public JPopupMenuFixture showPopupMenuAt(Path path) {
		return showPopupMenuAt(path.toString(separator()));
	}
	
	public void clickPath(Path path) {
		clickPath(path.toString(separator()));
	}
	
	public void expandPath(Path path) {
		expandPath(path.toString(separator()));
	}
	
	public void expandPathSequentially(Path path) {
		expandPathSequentially(path, separator());
	}
	
	private void expandPathSequentially(Path path, String separator) {
		Path newPath = null;
		
		for (String nodeName : path.getPathComponents()) {
			if (newPath == null) {
				newPath = new Path(nodeName);
			} else {
				newPath = new Path(newPath, nodeName);
			}
			expandPath(newPath);
			// Wait for the "please wait..." node to be replaced with the actual children.
			TestUtils.sleep(TestUtils.DEFAULT_TREE_EXPAND_DELAY);
//			showTreeRows();
		}
	}
	
	public void requirePathAbsent(Path path) {
		try {
			node(path);
			fail("Tree path is visible but not expected: <" + path.toString("/") + ">");
		} catch (Throwable ex) {
			// ignore - OK
		}
	}

}
