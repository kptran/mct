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
package gov.nasa.arc.mct.gui.layout;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class TestEnhancedFlowLayout {
    
    JPanel container, one, two;
    
    @BeforeTest
    public void init() {
        container = new JPanel();
        container.setSize(200, 100);
        
        one = new JPanel();
        one.setPreferredSize(new Dimension(20, 10));
        one.setSize(20, 10);
        two = new JPanel();
        two.setPreferredSize(new Dimension(15, 5));
        two.setSize(15, 5);
    }

    @Test
    public void testConstructors() throws Exception {
        testNoGap(FlowLayout.LEFT, EnhancedFlowLayout.TOP);
        testNoGap(FlowLayout.RIGHT, EnhancedFlowLayout.TOP);
        testNoGap(FlowLayout.LEFT, EnhancedFlowLayout.BOTTOM);
        
        testGap(FlowLayout.LEFT, EnhancedFlowLayout.TOP, 10, 11);
        testGap(FlowLayout.RIGHT, EnhancedFlowLayout.BOTTOM, 10, 11);
    }
    
    protected void testNoGap(int hAlign, int vAlign) {
        EnhancedFlowLayout flow = new EnhancedFlowLayout(hAlign, vAlign);
        assertEquals(hAlign, flow.getAlignment());
        assertEquals(vAlign, flow.getVerticalAlignment());
        assertEquals(0, flow.getHgap());
        assertEquals(0, flow.getVgap());
    }
    
    protected void testGap(int hAlign, int vAlign, int hGap, int vGap) {
        EnhancedFlowLayout flow = new EnhancedFlowLayout(hAlign, vAlign, hGap, vGap);
        assertEquals(hAlign, flow.getAlignment());
        assertEquals(vAlign, flow.getVerticalAlignment());
        assertEquals(flow.getHgap(), hGap);
        assertEquals(flow.getVgap(), vGap);
    }
    
    @Test
    public void testEmptyLayout() {
        EnhancedFlowLayout flow = new EnhancedFlowLayout(EnhancedFlowLayout.LEFT, EnhancedFlowLayout.TOP);
        container.setLayout(flow);
        flow.layoutContainer(container);
        
        assertEquals(container.getWidth(), 200);
        assertEquals(container.getHeight(), 100);
    }
    
    @Test
    public void testTopAlignment() {
        EnhancedFlowLayout flow = new EnhancedFlowLayout(EnhancedFlowLayout.LEFT, EnhancedFlowLayout.TOP);
        container.setLayout(flow);
        
        container.add(one);
        container.add(two);
        flow.layoutContainer(container);
        
        // The first component, the largest, will be top aligned.
        assertEquals(one.getWidth(), 20);
        assertEquals(one.getHeight(), 10);
        assertEquals(one.getX(), 0);
        assertEquals(one.getY(), 0);
        
        // The 2nd component will be near the top, but will have its midpoint
        // aligned to the midpoint of its larger neighbor to the left.
        assertEquals(two.getWidth(), 15);
        assertEquals(two.getHeight(), 5);
        assertEquals(two.getX(), 20);
        assertEquals(getYCenter(two), getYCenter(one), 0.5);
    }

    @Test
    public void testBottomAlignment() {
        EnhancedFlowLayout flow = new EnhancedFlowLayout(EnhancedFlowLayout.LEFT, EnhancedFlowLayout.BOTTOM);
        container.setLayout(flow);
        
        container.add(one);
        container.add(two);
        flow.layoutContainer(container);
        
        // The first component, the largest, will be bottom-aligned within
        // its container.
        assertEquals(one.getWidth(), 20);
        assertEquals(one.getHeight(), 10);
        assertEquals(one.getX(), 0);
        assertEquals((double) one.getY(), container.getSize().getHeight() - one.getSize().getHeight());

        // The second component will be near the bottom, but will have its midpoint
        // aligned to the midpoint of its larger neighbor to the left.
        assertEquals(two.getWidth(), 15);
        assertEquals(two.getHeight(), 5);
        assertEquals(two.getX(), 20);
        assertEquals(getYCenter(two), getYCenter(one), 0.5);
    }
    
    @Test
    public void testCenterAlignment() {
        EnhancedFlowLayout flow = new EnhancedFlowLayout(EnhancedFlowLayout.LEFT, EnhancedFlowLayout.CENTER);
        container.setLayout(flow);
        
        container.add(one);
        container.add(two);
        flow.layoutContainer(container);
        
        assertEquals(one.getWidth(), 20);
        assertEquals(one.getHeight(), 10);
        assertEquals(one.getX(), 0);
        assertEquals(getYCenter(one), container.getSize().getHeight() / 2.0, 0.5);

        assertEquals(two.getWidth(), 15);
        assertEquals(two.getHeight(), 5);
        assertEquals(two.getX(), 20);
        assertEquals(getYCenter(two), container.getSize().getHeight() / 2.0, 0.5);
    }
    
    protected double getYCenter(Component comp) {
        return comp.getY() + (comp.getHeight() / 2.0);
    }

}
