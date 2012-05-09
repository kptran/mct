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
package gov.nasa.arc.mct.platform;

import gov.nasa.arc.mct.services.component.PluginStartupStatus;
import gov.nasa.arc.mct.services.component.PluginStartupStatus.Severity;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

/**
 * Provides a dialog that shows issues reported by the installed plugin bundles
 * during MCT startup. Each issue contains a severity level: error or warning.
 * The dialog allows users to choose either to exit or continue launching MCT.
 */

@SuppressWarnings("serial")
public class PluginStartupStatusDialog extends JDialog {
    
    private static final int DIALOG_HEIGHT = 300;
    private static final int DIALOG_WIDTH = 450;

    /** Creates the reusable dialog. */
    public PluginStartupStatusDialog(List<PluginStartupStatus> statuses) {
        setModalityType(ModalityType.APPLICATION_MODAL);
        
        JPanel controlPanel = new JPanel();
        JButton continueButton = new JButton("Continue");
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                System.exit(0);
            }

        });
        continueButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                PluginStartupStatusDialog.this.dispose();
            }

        });
        
        controlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.add(exitButton);
        controlPanel.add(continueButton);


        JLabel description = new JLabel("<html>The following issues were reported by the plugin bundles<br>during MCT startup. You can choose to continue or exit MCT.</html>");
        description.setFont(description.getFont().deriveFont(Font.BOLD));
        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        messagePanel.add(description);
        
        JList list = new JList(statuses.toArray());
        list.setOpaque(false);
        list.setCellRenderer(new DefaultListCellRenderer() {
            
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                PluginStartupStatus status = (PluginStartupStatus) value;
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (status.getSeverity() == Severity.ERROR)
                    label.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
                if (status.getSeverity() == Severity.WARNING)
                    label.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
                label.setText(status.getMessage());
                label.setOpaque(false);
                return label;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 20, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        
        c.gridy = 0;
        c.weightx = 1;
        add(messagePanel, c);
                        
        c.insets = new Insets(0, 10, 0, 10);
        c.gridy = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        add(scrollPane, c);
        
        c.gridy = 2;
        c.insets = new Insets(0, 0, 0, 5);
        c.anchor = GridBagConstraints.LAST_LINE_END;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(controlPanel, c);
        
        // Set Create button as default to respond to enter key
        this.getRootPane().setDefaultButton(continueButton);

        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point centerPoint = ge.getCenterPoint();
        int x = (int) (centerPoint.x - DIALOG_WIDTH * .5);
        int y = (int) (centerPoint.y - DIALOG_HEIGHT * .5);
        setLocation(x, y);
        setVisible(true);
    }

}


