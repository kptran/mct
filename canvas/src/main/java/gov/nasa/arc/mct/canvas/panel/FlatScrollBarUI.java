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
package gov.nasa.arc.mct.canvas.panel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class FlatScrollBarUI extends BasicScrollBarUI {
    private Color foreground, background;
    
    public FlatScrollBarUI (Color fg, Color bg) {
        this.thumbColor = fg;
        this.thumbDarkShadowColor = fg;
        this.thumbHighlightColor  = fg;
        this.thumbLightShadowColor = fg;
        this.trackColor = bg;
        
        foreground = fg;
        background = bg;
    }

    @Override
    protected void paintDecreaseHighlight(Graphics g) {

    }

    @Override
    protected void paintIncreaseHighlight(Graphics g) {

    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(foreground);
        g2.fill(thumbBounds);

        
        if (c instanceof JScrollBar) {
            int orientation = ((JScrollBar) c).getOrientation();
            g2.setColor(background);                
            // Center position
            int x = (thumbBounds.x)+ thumbBounds.width / 2;
            int y = (thumbBounds.y) + thumbBounds.height / 2;
            int w = thumbBounds.width / 4;
            int h = thumbBounds.height / 4;
            for (int i = -2; i <= 2; i += 2) {
                switch (orientation) {
                case JScrollBar.VERTICAL:
                    g2.drawLine(x-w, y+i, x+w, y+i);
                    break;
                case JScrollBar.HORIZONTAL:
                    g2.drawLine(x+i, y-h, x+i, y+h);
                    break;
                }
            }
        }
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(background);
        g2.fill(trackBounds);
        g2.setColor(foreground);
        g2.draw(trackBounds);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        JButton b = new BasicArrowButton(orientation, 
                        background, foreground, foreground, foreground);
        b.setBorder(BorderFactory.createLineBorder(foreground, 1));
        return b;
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        // TODO Auto-generated method stub
        //return super.createIncreaseButton(orientation);
        JButton b = new BasicArrowButton(orientation, 
                        background, foreground, foreground, foreground);
        b.setBorder(BorderFactory.createLineBorder(foreground, 1));
        return b;
    }
    
    
    
    
}
