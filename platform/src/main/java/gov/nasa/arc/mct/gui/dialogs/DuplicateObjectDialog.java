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
/**
 * DuplicateTelemetryGroupDialog.java  June 04, 2009
 * 
 * This code is the property of the National Aeronautics and Space
 * Administration and was produced for the Mission Control Technologies (MCT)
 * project.
 * 
 */

package gov.nasa.arc.mct.gui.dialogs;

import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.policy.PolicyInfo;
import gov.nasa.arc.mct.policymgr.PolicyManagerImpl;
import gov.nasa.arc.mct.util.DataValidation;
import gov.nasa.arc.mct.util.MCTIcons;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import gov.nasa.arc.mct.gui.OptionBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Show the dialog box for "Duplicate Object...".
 * Dialog basis taken from Nija's CreateTelemetryGroupDialog.java.
 * @author jjupin
 *
 */
@SuppressWarnings("serial")
public class DuplicateObjectDialog extends JDialog {

    private static ResourceBundle bundle;
    
    private static final int ICON_HEIGHT = 16;
    private static final int ICON_WIDTH = 16;
    private static final int DIALOG_HEIGHT = 150;
    private static final int DIALOG_WIDTH = 800;
    private static int MIN_LENGTH, MAX_LENGTH;
    private static final int COL_SIZE = 60;
    
    private static final String ERRORMSG;

    static {
        bundle = ResourceBundle.getBundle("DuplicateObjectResource"); //NOI18N
        MIN_LENGTH = Integer.parseInt(bundle.getString("MIN_LENGTH")); //NOI18N
        MAX_LENGTH = Integer.parseInt(bundle.getString("MAX_LENGTH")); //NOI18N
        ERRORMSG = String.format(bundle.getString("ERRMSG_LENGTH"), MIN_LENGTH, MAX_LENGTH); //NOI18N
    }
    private boolean confirmed = false;
    private final JButton duplicate = new JButton(bundle.getString("DUPLICATE_BUTTON")); //NOI18N
    private final JLabel message = new JLabel();
    private final JTextField name = new JTextField();

    /**
     * The constructor that creates the dialog.
     * @param frame the owner of the {@link Frame} of which this
     * dialog is displayed.
     */
    public DuplicateObjectDialog(JFrame frame, String objectName) {
        super(frame, ModalityType.DOCUMENT_MODAL);

        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setTitle(bundle.getString("DIALOG_TITLE") + " - " + frame.getTitle()); //NOI18N

        JPanel contentPanel = new JPanel();
        JLabel prompt = new JLabel(bundle.getString("TEXT_FIELD_LABEL")); //NOI18N
        name.setText(objectName + " " + bundle.getString("TRAILING_COPY_KEYWORD")); //NOI18N
        name.selectAll();
        name.setColumns(COL_SIZE);
        name.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void changedUpdate(DocumentEvent e) {
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                doAction();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                doAction();
            }

            private boolean verify(String input) {
                return DataValidation.validateLength(input, MIN_LENGTH, MAX_LENGTH);
            }

            private void doAction() {

                boolean flag = verify(name.getText().trim());
                
                duplicate.setEnabled(flag);
                message.setIcon((flag) ? null : MCTIcons.getErrorIcon(ICON_WIDTH, ICON_HEIGHT));
                message.setText((flag) ? "" : ERRORMSG);
                
                ExecutionResult exResult = checkReservedWordsNamingPolicy(name.getText().trim());
                if (!exResult.getStatus()) {
                    duplicate.setEnabled(false);
                    message.setIcon(MCTIcons.getErrorIcon(ICON_WIDTH, ICON_HEIGHT));
                    message.setText(exResult.getMessage());     
                }

            }

        });
        
        name.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (!checkReservedWordsNamingPolicy(name.getText().trim()).getStatus()) {
                    name.setForeground(Color.RED);
                } else {
                    name.setForeground(Color.BLACK);
                }
            }

        });
        contentPanel.add(prompt);
        contentPanel.add(name);

        JPanel controlPanel = new JPanel();
        duplicate.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                
               // now, make sure the name selected does not have any reserved words.
                
              ExecutionResult exResult = checkReservedWordsNamingPolicy(name.getText().trim());
                
                if (!exResult.getStatus()) {
                    DuplicateObjectDialog.this.setVisible(true);
                    
                    OptionBox.showMessageDialog(null, 
                            String.format(bundle.getString("ERRMSG_RESERVED"), 
                                      name.getText().trim(),
                                      exResult.getMessage()),
                            bundle.getString("ERRTITLE_RESERVED"),          
                            OptionBox.ERROR_MESSAGE);
                    
                     confirmed = false;
                     
                } else {
                    DuplicateObjectDialog.this.dispose();
                    confirmed = true;
                }
            }

        });
        JButton cancel = new JButton(bundle.getString("CANCEL_BUTTON")); //NOI18N
        cancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                DuplicateObjectDialog.this.dispose();
            }

        });
        controlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.add(duplicate);
        controlPanel.add(cancel);

        JPanel messagePanel = new JPanel();
        messagePanel.add(message);

        setLayout(new GridLayout(3, 1));
        add(contentPanel);
        add(messagePanel);
        add(controlPanel);

        // Set Create button as default to respond to enter key
        this.getRootPane().setDefaultButton(duplicate);

        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setLocationRelativeTo(frame);
        setVisible(true);

    }
    
    private ExecutionResult checkReservedWordsNamingPolicy(String name) {
        
        PolicyContext context = new  PolicyContext();
        context.setProperty("NAME", name);
        String namingKey = PolicyInfo.CategoryType.COMPONENT_NAMING_POLICY_CATEGORY.getKey();
        ExecutionResult exResult = PolicyManagerImpl.getInstance().execute(namingKey, context);
        
        return exResult;            
    }
    
    /**
     * If the dialog exits normally, this method returns the new name for 
     * the component. Otherwise, the method returns an empty {@link String}.
     * @return a non-null {@link String}.
     */
    public String getConfirmedTelemetryGroupName() {
        return confirmed ? name.getText().trim() : "";
    }
}
