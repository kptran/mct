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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
public class AlternateRowColorTable extends JTable {

    static final Color ALTERNATE_ROW_COLOR = new Color (55, 105, 255, 25);
    
    public AlternateRowColorTable() {
        super();
        getTableHeader().setDefaultRenderer(new BottomBorderHeaderRenderer());
        setFillsViewportHeight(true);
        setIntercellSpacing(new Dimension(0, 0));
        setShowGrid(false);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        paintEmptyRows(g);
    }
    
    @Override
    public void setModel(TableModel dataModel) {
        super.setModel(dataModel);
        
        CellRenderer cellRenderer = new CellRenderer();
        for (int col = 0; col < dataModel.getColumnCount(); col++) {
            getColumnModel().getColumn(col).setCellRenderer(cellRenderer);
        }
    }
    
    private void paintEmptyRows(Graphics g) {
        final int rowCount = getRowCount();        
        final Rectangle clip = g.getClipBounds();
        if (rowCount * rowHeight < clip.height) {
            for (int i = rowCount; i <= clip.height/rowHeight; ++i) {
                if (i % 2 == 1) {
                    g.setColor(ALTERNATE_ROW_COLOR);
                    g.fillRect(clip.x, i * rowHeight, clip.width, rowHeight);
                }
            }
        }
    }

    public static class CellRenderer extends DefaultTableCellRenderer {

        private static final String TABLE_BACKGROUND = "Table.background";
        private static final String TABLE_SELECTION_BACKGROUND = "Table.selectionBackground";

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setBackground(row % 2 == 1 ? ALTERNATE_ROW_COLOR : UIManager.getColor(TABLE_BACKGROUND));
            if (isSelected)
                label.setBackground(UIManager.getColor(TABLE_SELECTION_BACKGROUND));
            return label;
        }
        
    }

}
