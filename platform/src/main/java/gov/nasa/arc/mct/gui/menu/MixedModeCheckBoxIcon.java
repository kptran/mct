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
package gov.nasa.arc.mct.gui.menu;

import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;

import javax.swing.Icon;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;


class MixedModeCheckBoxIcon implements Icon, UIResource,  
    Serializable {  
        private static final long serialVersionUID = 1L;

        private int getControlSize() {  
            return 13;  
        }  

        public void paintIcon(Component c, Graphics g, int x, int y) {  
            int controlSize = getControlSize();  

            drawFlush3DBorder(g, x, y, controlSize, controlSize);  
            g.setColor(MetalLookAndFeel.getControlInfo());   
            drawDash(c, g, x, y);  
        }

        private void drawFlush3DBorder(Graphics g, int x, int y, int w, int h) {  
            g.translate(x, y);  
            g.setColor(MetalLookAndFeel.getControlDarkShadow());  
            g.drawRect(0, 0, w - 2, h - 2);  
            g.setColor(MetalLookAndFeel.getControlHighlight());  
            g.drawRect(1, 1, w - 2, h - 2);  
            g.setColor(MetalLookAndFeel.getControl());  
            g.drawLine(0, h - 1, 1, h - 2);  
            g.drawLine(w - 1, 0, w - 2, 1);  
            g.translate(-x, -y);  
        }  
        
        private void drawDash(Component c, Graphics g, int x, int y) {  
            int controlSize = getControlSize();  
            int yPos = y+(controlSize/2);
            g.drawLine(x+3, yPos, x+controlSize-4, yPos);
        }  

        public int getIconWidth() {  
            return getControlSize();  
        }  

        public int getIconHeight() {  
            return getControlSize();  
        }  
    }  