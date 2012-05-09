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
package gov.nasa.arc.mct.canvas.layout;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JPanel;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CanvasLayoutManagerTest {
    private JPanel panel;
    private JPanel parentPanel;
    
    @BeforeMethod
    public void setup() {
        panel = new JPanel();
        setupParent(panel);
    }
    
    @Test
    public void testDefaultLayout() {
        CanvasLayoutManager layoutMgr = new CanvasLayoutManager();
        panel.setLayout(layoutMgr);
        
        JPanel childPanel1 = new JPanel();
        JPanel childPanel2 = new JPanel();
        JPanel childPanel3 = new JPanel();
        panel.add(childPanel1, new Rectangle(10, 20, 30, 40));
        panel.add(childPanel2, new Rectangle(20, 30, 40, 50));
        panel.add(childPanel3, new Rectangle(30, 40, 50, 60));
        layoutMgr.invalidateLayout(panel);
        
        Component[] childs = panel.getComponents();
        Assert.assertEquals(childs.length, 3);
        
        Assert.assertEquals(childs[0].getBounds(), new Rectangle(10, 20, 30, 40));
        Assert.assertEquals(childs[1].getBounds(), new Rectangle(20, 30, 40, 50));
        Assert.assertEquals(childs[2].getBounds(), new Rectangle(30, 40, 50, 60));
    }
    
    @Test
    public void testTileLayout() {
        CanvasLayoutManager layoutMgr = new CanvasLayoutManager(CanvasLayoutManager.TILE);
        panel.setLayout(layoutMgr);
        panel.setSize(100, 100);
        
        JPanel childPanel1 = new JPanel();
        JPanel childPanel2 = new JPanel();
        JPanel childPanel3 = new JPanel();
        panel.add(childPanel1, new Rectangle(10, 20, 30, 40));
        panel.add(childPanel2, new Rectangle(20, 30, 40, 50));
        panel.add(childPanel3, new Rectangle(30, 40, 50, 60));
        layoutMgr.invalidateLayout(panel);
        
        Component[] childs = panel.getComponents();
        Assert.assertEquals(childs.length, 3);
        
        Assert.assertEquals(childs[0].getBounds().getLocation(), new Point(0, 0));
        Assert.assertEquals(childs[1].getBounds().getLocation(), new Point(30, 0));
        Assert.assertEquals(childs[2].getBounds().getLocation(), new Point(0, 50));
    }
    
    @Test
    public void snapToGrid() {
        CanvasLayoutManager layoutMgr = new CanvasLayoutManager(CanvasLayoutManager.MIX);
        layoutMgr.setGridSize(7);
        layoutMgr.enableSnap(true);
        panel.setLayout(layoutMgr);
        panel.setSize(100, 100);
        
        JPanel childPanel1 = new JPanel();
        JPanel childPanel2 = new JPanel();
        JPanel childPanel3 = new JPanel();
        panel.add(childPanel1, new Rectangle(0, 0, 30, 40));
        panel.add(childPanel2, new Rectangle(20, 30, 40, 50));
        panel.add(childPanel3, new Rectangle(30, 40, 50, 60));
        layoutMgr.invalidateLayout(panel);
        
        Component[] childs = panel.getComponents();
        Assert.assertEquals(childs.length, 3);
        
        Assert.assertEquals(childs[0].getBounds().getLocation(), new Point(0, 0));
        Assert.assertEquals(childs[1].getBounds().getLocation(), new Point(35, 0));
        Assert.assertEquals(childs[2].getBounds().getLocation(), new Point(0, 56));
    }
    
    @Test
    public void testMixLayout() {
        CanvasLayoutManager layoutMgr = new CanvasLayoutManager(CanvasLayoutManager.MIX);
        panel.setLayout(layoutMgr);
        panel.setSize(100, 100);
        
        JPanel childPanel1 = new JPanel();
        panel.add(childPanel1, new Rectangle(10, 20, 30, 40));
        layoutMgr.invalidateLayout(panel);

        JPanel childPanel2 = new JPanel();
        panel.add(childPanel2, new Rectangle(20, 30, 40, 50));
        layoutMgr.invalidateLayout(panel);
        
        JPanel childPanel3 = new JPanel();
        panel.add(childPanel3, new Rectangle(30, 40, 50, 60));
        layoutMgr.invalidateLayout(panel);
        
        Component[] childs = panel.getComponents();
        Assert.assertEquals(childs.length, 3);
        
        Assert.assertEquals(childs[0].getBounds(), new Rectangle(10, 20, 30, 40));
        Assert.assertEquals(childs[1].getBounds(), new Rectangle(20, 30, 40, 50));
        Assert.assertEquals(childs[2].getBounds(), new Rectangle(30, 40, 50, 60));
        
        panel = new JPanel();
        setupParent(panel);
        panel.setLayout(layoutMgr);
        panel.setSize(100, 100);
        
        childPanel1 = new JPanel();
        childPanel2 = new JPanel();
        childPanel3 = new JPanel();
        panel.add(childPanel1, new Rectangle(0, 0, 30, 40));
        panel.add(childPanel2, new Rectangle(20, 30, 40, 50));
        panel.add(childPanel3, new Rectangle(30, 40, 50, 60));
        layoutMgr.invalidateLayout(panel);
        
        childs = panel.getComponents();
        Assert.assertEquals(childs.length, 3);
        
        Assert.assertEquals(childs[0].getBounds().getLocation(), new Point(0, 0));
        Assert.assertEquals(childs[1].getBounds().getLocation(), new Point(30, 0));
        Assert.assertEquals(childs[2].getBounds().getLocation(), new Point(0, 50));
    }
    
    private void setupParent(JPanel panel) {
        parentPanel = new JPanel();
        parentPanel.add(panel);
        parentPanel.setSize(100, 100);
    }
    
}
