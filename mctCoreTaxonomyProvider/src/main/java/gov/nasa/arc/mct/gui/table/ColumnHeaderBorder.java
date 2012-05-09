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
package gov.nasa.arc.mct.gui.table;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.UIManager;
import javax.swing.border.Border;

public class ColumnHeaderBorder implements Border {
    private static final String TABLE_HEADER_FOREGROUND = "TableHeader.foreground";
    private int inset = 2;
    private static final ColumnHeaderBorder border = new ColumnHeaderBorder();
    
    private ColumnHeaderBorder() {
        
    }
    
    public static ColumnHeaderBorder getInstance() {
        return border;
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(inset, inset, inset, inset);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(UIManager.getColor(TABLE_HEADER_FOREGROUND));
        g2.fillRect(x, y + height - inset, width, inset);
    }

}
