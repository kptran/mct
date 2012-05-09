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


import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.text.JTextComponent;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.finder.DialogFinder;
import org.fest.swing.finder.FrameFinder;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.ComponentContainerFixture;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JCheckBoxFixture;
import org.fest.swing.fixture.JComboBoxFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JLabelFixture;
import org.fest.swing.fixture.JListFixture;
import org.fest.swing.fixture.JMenuItemFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JPopupMenuFixture;
import org.fest.swing.fixture.JProgressBarFixture;
import org.fest.swing.fixture.JRadioButtonFixture;
import org.fest.swing.fixture.JScrollBarFixture;
import org.fest.swing.fixture.JScrollPaneFixture;
import org.fest.swing.fixture.JSliderFixture;
import org.fest.swing.fixture.JSpinnerFixture;
import org.fest.swing.fixture.JSplitPaneFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.fixture.JTableFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.fest.swing.fixture.JToggleButtonFixture;
import org.fest.swing.fixture.JToolBarFixture;
import org.fest.swing.fixture.JTreeFixture;

public class Query extends GenericTypeMatcher<Component> {
	
	List<Criterion> criteria = new ArrayList<Criterion>();
	Integer timeoutInSeconds;

	public Query() {
		super(Component.class);
	}
	
	public Query withTimeout(int timeoutInSeconds) {
		this.timeoutInSeconds = Integer.valueOf(timeoutInSeconds);
		return this;
	}
	
	public Query text(String text) {
		criteria.add(new PropertyCriterion("text", text, false));
		return this;
	}
	
	public Query textMatches(String text) {
		criteria.add(new PropertyCriterion("text", text, true));
		return this;
	}
	
	public Query title(String text) {
		criteria.add(new PropertyCriterion("title", text, false));
		return this;
	}
	
	public Query titleMatches(String text) {
		criteria.add(new PropertyCriterion("title", text, true));
		return this;
	}
	
	public Query name(String text) {
		criteria.add(new PropertyCriterion("name", text, false));
		return this;
	}
	
	public Query visible(boolean b) {
		criteria.add(new VisibilityCriterion(b));
		return this;
	}
	
	public Query enabled(boolean b) {
		criteria.add(new EnabledCriterion(b));
		return this;
	}
	
	public Query toolTip(String text) {
		criteria.add(new PropertyCriterion("toolTipText", text, false));
		return this;
	}
	
	public Query toolTipMatches(String text) {
		criteria.add(new PropertyCriterion("toolTipText", text, true));
		return this;
	}
	
	public Query accessibleName(String text) {
		criteria.add(new AccessiblePropertyCriterion("accessibleName", text, false));
		return this;
	}
	
	public Query accessibleNameMatches(String text) {
		criteria.add(new AccessiblePropertyCriterion("accessibleName", text, true));
		return this;
	}
	
	public Query accessibleDescription(String text) {
		criteria.add(new AccessiblePropertyCriterion("accessibleDescription", text, false));
		return this;
	}
	
	public Query accessibleDescriptionMatches(String text) {
		criteria.add(new AccessiblePropertyCriterion("accessibleDescription", text, true));
		return this;
	}
	
	public Query property(String propertyName, String value) {
		criteria.add(new PropertyCriterion(propertyName, value, false));
		return this;
	}
	
	public Query propertyMatches(String propertyName, String value) {
		criteria.add(new PropertyCriterion(propertyName, value, true));
		return this;
	}
	
	public <T extends Component> Query when(GenericTypeMatcher<T> matcher) {
		criteria.add(new GenericMatcherCriterion<T>(matcher));
		return this;
	}
	
	public JTabbedPaneWrapper tabbedPaneIn(ComponentContainerFixture fixture) {
		assert fixture != null;
		JTabbedPaneFixture tabbedPaneFixture = fixture.tabbedPane(new QueryMatcher<JTabbedPane>(JTabbedPane.class, this));
		return new JTabbedPaneWrapper(tabbedPaneFixture.robot, tabbedPaneFixture.component());
	}
	
	public JTreeWrapper treeIn(ComponentContainerFixture fixture) {
		assert fixture != null;
		JTreeFixture treeFixture = fixture.tree(new QueryMatcher<JTree>(JTree.class, this));
		return new JTreeWrapper(treeFixture.robot, treeFixture.component());
	}
	
	public JTableFixture tableIn(ComponentContainerFixture fixture) {
		assert fixture != null;
		return fixture.table(new QueryMatcher<JTable>(JTable.class, this));
	}
	
	public JListFixture listIn(ComponentContainerFixture fixture) {
		assert fixture != null;
		return fixture.list(new QueryMatcher<JList>(JList.class, this));
	}
	
	public JLabelFixture labelIn(ComponentContainerFixture fixture) {
		assert fixture != null;
		return fixture.label(new QueryMatcher<JLabel>(JLabel.class, this));
	}
	
	public JButtonFixture buttonIn(ComponentContainerFixture fixture) {
		assert fixture != null;
		return fixture.button(new QueryMatcher<JButton>(JButton.class, this));
	}
	
	public JCheckBoxFixture checkBoxIn(ComponentContainerFixture fixture) {
		assert fixture != null;
		return fixture.checkBox(new QueryMatcher<JCheckBox>(JCheckBox.class, this));
	}
	
	public JRadioButtonFixture radioButtonIn(ComponentContainerFixture fixture) {
		assert fixture != null;
		return fixture.radioButton(new QueryMatcher<JRadioButton>(JRadioButton.class, this));
	}
	
	public JToggleButtonFixture toggleButtonIn(ComponentContainerFixture fixture) {
		assert fixture != null;
		return fixture.toggleButton(new QueryMatcher<JToggleButton>(JToggleButton.class, this));
	}
	
	public JToolBarFixture toolBarIn(ComponentContainerFixture fixture) {
		assert fixture != null;
		return fixture.toolBar(new QueryMatcher<JToolBar>(JToolBar.class, this));
	}
	
	public JComboBoxFixture comboBoxIn(ComponentContainerFixture fixture) {
		assert fixture != null;
		return fixture.comboBox(new QueryMatcher<JComboBox>(JComboBox.class, this));
	}
	
	public JTextComponentFixture textBoxIn(ComponentContainerFixture fixture) {
		assert fixture != null;	
		return fixture.textBox(new QueryMatcher<JTextComponent>(JTextComponent.class, this));
	}
	
	public JPanelFixture panelIn(ComponentContainerFixture fixture) {
		assert fixture != null;
		return fixture.panel(new QueryMatcher<JPanel>(JPanel.class, this));
	}
	
	public JProgressBarFixture progressBarIn(ComponentContainerFixture fixture) {
		assert fixture != null;
		return fixture.progressBar(new QueryMatcher<JProgressBar>(JProgressBar.class, this));
	}
	
	public JScrollBarFixture scrollBarIn(ComponentContainerFixture fixture) {
		assert fixture != null;
		return fixture.scrollBar(new QueryMatcher<JScrollBar>(JScrollBar.class, this));
	}
	
	public JScrollPaneFixture scrollPaneIn(ComponentContainerFixture fixture) {
		assert fixture != null;
		return fixture.scrollPane(new QueryMatcher<JScrollPane>(JScrollPane.class, this));
	}
	
	public JSliderFixture sliderIn(ComponentContainerFixture fixture) {
		assert fixture != null;
		return fixture.slider(new QueryMatcher<JSlider>(JSlider.class, this));
	}
	
	public JSpinnerFixture spinnerIn(ComponentContainerFixture fixture) {
		assert fixture != null;
		return fixture.spinner(new QueryMatcher<JSpinner>(JSpinner.class, this));
	}
	
	public JSplitPaneFixture splitPaneIn(ComponentContainerFixture fixture) {
		assert fixture != null;
		return fixture.splitPane(new QueryMatcher<JSplitPane>(JSplitPane.class, this));
	}
	
	public JFileChooserFixture fileChooserIn(ComponentContainerFixture fixture) {
		assert fixture != null;
		return fixture.fileChooser(new QueryMatcher<JFileChooser>(JFileChooser.class, this));
	}
	
	public JMenuItemFixture menuItemIn(ComponentContainerFixture fixture) {
		assert fixture != null;
		return fixture.menuItem(new QueryMatcher<JMenuItem>(JMenuItem.class, this));
	}
	
	public JMenuItemFixture menuItemIn(JPopupMenuFixture fixture) {
		assert fixture != null;
		return fixture.menuItem(new QueryMatcher<JMenuItem>(JMenuItem.class, this));
	}
	
	public FrameFixture findFrame() {
		FrameFinder finder = WindowFinder.findFrame(new QueryMatcher<Frame>(Frame.class, this));
		if (timeoutInSeconds != null) {
			finder = finder.withTimeout(timeoutInSeconds.intValue(), TimeUnit.SECONDS);
		}
		return finder.using(TestUtils.getRobot());
	}
	
	public DialogFixture findDialog() {
		DialogFinder finder = WindowFinder.findDialog(new QueryMatcher<Dialog>(Dialog.class, this));
		if (timeoutInSeconds != null) {
			finder = finder.withTimeout(timeoutInSeconds.intValue(), TimeUnit.SECONDS);
		}
		return finder.using(TestUtils.getRobot());
	}
	
	@Override
	protected boolean isMatching(Component component) {
		for (Criterion criterion : criteria) {
			if (!criterion.satisfies(component)) {
				return false;
			}
		}
		
		return true;
	}
	
	public String criteriaToString() {
		StringBuffer s = new StringBuffer();
		for (Criterion criterion : criteria) {
			if (s.length() > 0) {
				s.append(',');
			}
			s.append(criterion.toString());
		}
		
		return s.toString();
	}
	
}
