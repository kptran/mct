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
package gov.nasa.arc.mct.search;

import gov.nasa.arc.mct.dao.service.QueryResult;
import gov.nasa.arc.mct.dao.specifications.ComponentSpecification;

import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;

import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.core.Robot;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JListFixture;
import org.fest.swing.timing.Condition;
import org.fest.swing.timing.Pause;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestPlatformSearchUI {

    private Robot robot;
    private static final String TITLE = "Test Frame";
    private MockPlatformSearchUI platformSearchUI;

    @BeforeMethod
    public void setup() {
        robot = BasicRobot.robotWithNewAwtHierarchy();
        GuiActionRunner.execute(new GuiTask() {
            
            @Override
            protected void executeInEDT() throws Throwable {
                platformSearchUI = new MockPlatformSearchUI();                
                JFrame frame = new JFrame(TITLE);
                frame.setName(TITLE);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                platformSearchUI.setOpaque(true); // content panes must be opaque
                frame.setContentPane(platformSearchUI.createSearchUI());
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    @AfterMethod
    public void tearDown(){
        robot.cleanUp();
    }
    
    @Test
    public void testSearchClickResults() {
        FrameFixture window = WindowFinder.findFrame(TITLE).using(robot);
        final JListFixture list = window.list(new JListMatcher("Search Result List"));
        ComponentSpecification cs1 = Mockito.mock(ComponentSpecification.class);
        ComponentSpecification cs2 = Mockito.mock(ComponentSpecification.class);
        QueryResult q = new QueryResult(2, Arrays.asList(cs1, cs2));
        platformSearchUI.setQueryResult(q);
        window.button(new JButtonMatcher("Search")).click();
        Condition condition = new Condition("Results Arrived.") {
            
            @Override
            public boolean test() {
                return list.target.getModel().getSize() > 0; 
            }
            
            @Override
            protected void done() {
                Assert.assertEquals(list.target.getModel().getSize(), 2);
            }
        };
        Pause.pause(condition);
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
    
    private static class JListMatcher extends GenericTypeMatcher<JList> {
        private final String label;
        
        public JListMatcher(String label) {
            super(JList.class, true);
            this.label = label;
        }
        
        @Override
        protected boolean isMatching(JList l) {
            return label.equals(l.getAccessibleContext().getAccessibleName());
        }
        
    }
    
    @SuppressWarnings("serial")
    private class MockPlatformSearchUI extends PlatformSearchUI {
        private QueryResult q;
        public void setQueryResult(QueryResult q) {
            this.q = q;
        }
        
        @Override
        QueryResult search(String pattern, boolean isFindObjectsCreatedByMe) {
            return q;
        }
    }

}
