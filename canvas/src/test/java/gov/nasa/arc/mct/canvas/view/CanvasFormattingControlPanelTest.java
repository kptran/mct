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
package gov.nasa.arc.mct.canvas.view;

import gov.nasa.arc.mct.canvas.panel.CanvasViewStrategy;
import gov.nasa.arc.mct.canvas.panel.Panel;
import gov.nasa.arc.mct.component.MockComponent;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.MCTViewManifestationInfo;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;
import gov.nasa.arc.mct.test.util.gui.Query;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JSpinner;
import javax.swing.RepaintManager;

import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.core.Robot;
import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JCheckBoxFixture;
import org.fest.swing.fixture.JComboBoxFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CanvasFormattingControlPanelTest {

    private Robot robot;
    private static final ResourceBundle bundle = ResourceBundle.getBundle("CanvasResourceBundle"); //NOI18N
    @Mock
    private CanvasManifestation mockCanvasManifestation;
    @Mock 
    private Panel mockPanel;
    private static final String TITLE = "Test Frame";
    private CanvasFormattingControlsPanel controlPanel;
    private static Query PANEL_TITLE_FONT_SIZE_SPINNER = new Query().accessibleName(bundle.getString("PANEL_TITLE_FONT_SIZE_SPINNER"));
    
    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        robot = BasicRobot.robotWithCurrentAwtHierarchy();
        Mockito.when(mockPanel.getBounds()).thenReturn(new Rectangle(0,0,50,50));
        Mockito.when(mockPanel.getBorderColor()).thenReturn(Color.gray);
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                controlPanel = new CanvasFormattingControlsPanel(
                                mockCanvasManifestation);
                controlPanel.informOnePanelSelected(Collections.singletonList(mockPanel));
                JFrame frame = new JFrame(TITLE);
                frame.setName(TITLE);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                controlPanel.setOpaque(true); // content panes must be opaque
                frame.setContentPane(controlPanel);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    @AfterMethod
    public void tearDown() {
        robot.cleanUp();
    }

    public static class DerivedMock extends MockComponent {
        @Override
        public Set<ViewInfo> getViewInfos(ViewType type) {
            return Collections.emptySet();
        }
    }
    
    @Test
    public void testClone() {     
        AbstractComponent canvasComponent = Mockito.mock(AbstractComponent.class);
        AbstractComponent component = new DerivedMock();
        ViewInfo viewInfo = new ViewInfo(CanvasManifestation.class, "", "", ViewType.OBJECT) {
            @Override
            public View createView(final AbstractComponent component) {
                View view = Mockito.mock(View.class);
                Mockito.when(view.getManifestedComponent()).thenReturn(component);
                return view;
            }
        };
        
        MCTViewManifestationInfo manifInfo = Mockito.mock(MCTViewManifestationInfo.class);        
        View view = CanvasViewStrategy.CANVAS_OWNED.createViewFromManifestInfo(viewInfo, component, canvasComponent, manifInfo);
        Assert.assertTrue(view.getManifestedComponent().getMasterComponent() == component);
        Assert.assertEquals(view.getManifestedComponent().getComponentId(), canvasComponent.getComponentId());
    }    

    @Test
    public void testPersistenceIsCalledDuringActions() {
        int persistentCount = 0;
        FrameFixture window = WindowFinder.findFrame(TITLE).using(robot);
        JCheckBoxFixture titleCheckBox = window.checkBox(new CheckBoxMatcher("Panel Title Bar"));
        titleCheckBox.click();
        Mockito.verify(mockCanvasManifestation,Mockito.times(++persistentCount)).fireFocusPersist();
        
        // check that both focus lost and enter will trigger focus for text fields
        JTextComponentFixture textFixture = window.textBox(new TextFieldMatcher("Panel Title:"));
        textFixture.enterText("abc").pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_ENTER));
        Mockito.verify(mockCanvasManifestation,Mockito.times(++persistentCount)).fireFocusPersist();
        titleCheckBox.focus();
        // should not fire persistence event if the text hasn't changed
        Mockito.verify(mockCanvasManifestation,Mockito.times(persistentCount)).fireFocusPersist();
        
        textFixture.focus();
        textFixture.enterText("123");
        titleCheckBox.focus();
        Mockito.verify(mockCanvasManifestation,Mockito.times(persistentCount)).fireFocusPersist();

        // check that selecting the border color call persistence
        JComboBoxFixture colorFixture = window.comboBox(new ComboBoxMatcher("Color:"));
        colorFixture.selectItem(2);
        Mockito.verify(mockCanvasManifestation,Mockito.times(++persistentCount)).fireFocusPersist();

        window.toggleButton(new JToggleButtonMatcher("All borders")).click();
        Mockito.verify(mockCanvasManifestation,Mockito.times(++persistentCount)).fireFocusPersist();
        
        // check the no border button state
        window.toggleButton(new JToggleButtonMatcher("No borders")).click();
        Mockito.verify(mockCanvasManifestation,Mockito.times(++persistentCount)).fireFocusPersist();
        
        window.comboBox(new ComboBoxMatcher("panelTitleFontComboBox")).selectItem("Serif");
        Mockito.verify(mockCanvasManifestation,Mockito.times(++persistentCount)).fireFocusPersist();
        
        PANEL_TITLE_FONT_SIZE_SPINNER.spinnerIn(window).select(18);
        Mockito.verify(mockCanvasManifestation,Mockito.times(++persistentCount)).fireFocusPersist();
        persistentCount++;
        window.toggleButton(new JToggleButtonMatcher("panelTitleFontStyleBold")).click();
        Mockito.verify(mockCanvasManifestation,Mockito.times(++persistentCount)).fireFocusPersist();
        persistentCount++;
        
        window.toggleButton(new JToggleButtonMatcher("panelTitleFontStyleItalic")).click();
        Mockito.verify(mockCanvasManifestation,Mockito.times(++persistentCount)).fireFocusPersist(); 
       
        window.toggleButton(new JToggleButtonMatcher("panelTitleFontStyleUnderline")).click();
        Mockito.verify(mockCanvasManifestation,Mockito.times(++persistentCount)).fireFocusPersist();
        
        window.comboBox(new ComboBoxMatcher("panelTitleFontColorComboBox")).selectItem(2);
        Mockito.verify(mockCanvasManifestation,Mockito.times(++persistentCount)).fireFocusPersist();
        
        window.comboBox(new ComboBoxMatcher("panelTitleBackgroundColorComboBox")).selectItem(2);
        Mockito.verify(mockCanvasManifestation,Mockito.times(++persistentCount)).fireFocusPersist();
        
        controlPanel.informZeroPanelsSelected();
        titleCheckBox.requireDisabled();
        textFixture.requireDisabled();
        window.toggleButton(new JToggleButtonMatcher("All borders")).requireDisabled();
        window.button(new JButtonMatcher("Align to bottom edge")).requireDisabled();
        window.comboBox(new ComboBoxMatcher("panelTitleFontComboBox")).requireDisabled();
        PANEL_TITLE_FONT_SIZE_SPINNER.spinnerIn(window).requireDisabled();
        window.toggleButton(new JToggleButtonMatcher("panelTitleFontStyleBold")).requireDisabled();
        window.toggleButton(new JToggleButtonMatcher("panelTitleFontStyleItalic")).requireDisabled();
        window.toggleButton(new JToggleButtonMatcher("panelTitleFontStyleUnderline")).requireDisabled();
        window.comboBox(new ComboBoxMatcher("panelTitleFontColorComboBox")).requireDisabled();
        window.comboBox(new ComboBoxMatcher("panelTitleBackgroundColorComboBox")).requireDisabled();
        
        controlPanel.informMultipleViewPanelsSelected(Collections.<Panel>emptyList());
        titleCheckBox.requireEnabled();
        textFixture.requireDisabled();
        window.toggleButton(new JToggleButtonMatcher("All borders")).requireEnabled();
        window.button(new JButtonMatcher("Align to bottom edge")).requireEnabled();
        window.comboBox(new ComboBoxMatcher("panelTitleFontComboBox")).requireEnabled();
        PANEL_TITLE_FONT_SIZE_SPINNER.spinnerIn(window).requireEnabled();
        window.toggleButton(new JToggleButtonMatcher("panelTitleFontStyleBold")).requireEnabled();
        window.toggleButton(new JToggleButtonMatcher("panelTitleFontStyleItalic")).requireEnabled();
        window.toggleButton(new JToggleButtonMatcher("panelTitleFontStyleUnderline")).requireEnabled();
        window.comboBox(new ComboBoxMatcher("panelTitleFontColorComboBox")).requireEnabled();
        window.comboBox(new ComboBoxMatcher("panelTitleBackgroundColorComboBox")).requireEnabled();

        
    }
    
    private static class JButtonMatcher extends GenericTypeMatcher<JButton> {
        private final String label;
        
        public JButtonMatcher(String label) {
            super(JButton.class, true);
            this.label = label;
        }
        
        @Override
        protected boolean isMatching(JButton cb) {
            return label.equals(cb.getAccessibleContext().getAccessibleName()) ||
                   label.equals(cb.getToolTipText());
        }
        
    }
    
    private static class JToggleButtonMatcher extends GenericTypeMatcher<JToggleButton> {
        private final String label;
        
        public JToggleButtonMatcher(String label) {
            super(JToggleButton.class, true);
            this.label = label;
        }
        
        @Override
        protected boolean isMatching(JToggleButton cb) {
            return label.equals(cb.getAccessibleContext().getAccessibleName()) ||
                   label.equals(cb.getToolTipText());
        }
        
    }
    
    
    
    private static class ComboBoxMatcher extends GenericTypeMatcher<JComboBox> {
        private final String label;
        
        public ComboBoxMatcher(String label) {
            super(JComboBox.class, true);
            this.label = label;
        }
        
        @Override
        protected boolean isMatching(JComboBox cb) {
            return label.equals(cb.getAccessibleContext().getAccessibleName());
        }
        
    }
    
    private static class TextFieldMatcher extends GenericTypeMatcher<JTextField> {
        private final String label;
        
        public TextFieldMatcher(String label) {
            super(JTextField.class, true);
            this.label = label;
        }
        
        @Override
        protected boolean isMatching(JTextField cb) {
            return label.equals(cb.getAccessibleContext().getAccessibleName());
        }
        
    }
    
    private static class CheckBoxMatcher extends GenericTypeMatcher<JCheckBox> {
        private final String buttonLabel;
        
        public CheckBoxMatcher(String label) {
            super(JCheckBox.class, true);
            buttonLabel = label;
        }
        
        @Override
        protected boolean isMatching(JCheckBox cb) {
            return buttonLabel.equals(cb.getText());
        }
        
    }

}
