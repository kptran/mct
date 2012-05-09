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
package gov.nasa.arc.mct.core.roles;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.OptionBox;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.platform.core.access.PlatformAccess;
import gov.nasa.arc.mct.platform.spi.DuplicateUserException;
import gov.nasa.arc.mct.platform.spi.PersistenceService;
import gov.nasa.arc.mct.services.component.ViewInfo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public final class UsersManifestation extends View {
    private UsersPage usersPage;

    public UsersManifestation(AbstractComponent ac,ViewInfo vi) {
        super(ac,vi);
        usersPage = new UsersPage(getManifestedComponent().getDisplayName());
        add(usersPage);
    }

    @Override
    protected JComponent initializeControlManifestation() {
        return new AddUserView(getManifestedComponent());
    }

    @Override
    public void updateMonitoredGUI() {
        usersPage.refresh();
    }

    private static final class AddUserView extends JPanel {

        private static final String PROMPT = "To add a user, type user ID and press Enter";
        private static final String EM_STR = "";
        private static final long serialVersionUID = 7516917053393171272L;

        public AddUserView(final AbstractComponent disciplineComponent) {
            setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
            final JTextField textField = new JTextField(PROMPT);
            textField.setOpaque(false);
            textField.setBorder(new RoundedBorder(textField));
            textField.setMargin(new Insets(5, 10, 5, 10));
            textField.setColumns(25);
            textField.setForeground(Color.GRAY);
            textField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    clear();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    textField.setText(PROMPT);
                }

                private void clear() {
                    if (textField.getText().trim().equals(PROMPT)) {
                        textField.setText(EM_STR);
                    }
                }
            });
            textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    submitWhenDone(e);
                }

                private void submitWhenDone(KeyEvent e) {
                    if (isEnter(e)) {
                        String userId = textField.getText().trim();

                        if (userId.length() == 0)
                            return;

                        // Check userId length
                        if (userId.length() > 20) {
                            SwingUtilities.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    OptionBox.showMessageDialog(textField, "User ID cannot exceed 20 characters",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            });
                            return;
                        }

                        PersistenceService persistenceService = PlatformAccess.getPlatform().getPersistenceService();
                        try {
                            persistenceService.addNewUser(userId, disciplineComponent.getDisplayName());
                            disciplineComponent.refreshViewManifestations();
                            textField.setText(EM_STR);
                        } catch (DuplicateUserException ex) {
                            OptionBox.showMessageDialog(textField, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        } catch (InterruptedException ex) {
                            OptionBox.showMessageDialog(textField, "Cannot create new users now, please try again.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }

                private boolean isEnter(KeyEvent e) {
                    return e.getKeyCode() == KeyEvent.VK_ENTER;
                }
            });
            add(textField);
        }

        private final class RoundedBorder implements Border {

            private JTextField textField;

            public RoundedBorder(JTextField textField) {
                this.textField = textField;
            }

            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                RenderingHints renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHints(renderHints);
                g2.setStroke(new BasicStroke(2f));
                int arcWidthAndHeight = height / 2;
                g2.drawRoundRect(x + 2, y + 2, width - 4, height - 4, arcWidthAndHeight, arcWidthAndHeight);
            }

            @Override
            public Insets getBorderInsets(Component c) {
                return textField.getMargin();
            }

            @Override
            public boolean isBorderOpaque() {
                return false;
            }

        }

    }
}