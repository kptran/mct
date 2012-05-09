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
package gov.nasa.arc.mct.gui;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.HeadlessException;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * Implements static methods to show dialog boxes to show messages
 * or get user input. Each of these dialogs is document-modal, rather
 * than the default Swing behavior, application-modal.
 * 
 * <p>Some of this code is modeled after methods in {@link javax.swing.JOptionPane}.
 */
public class OptionBox {
    
    // Values copied from JOptionPane, so that code which uses this class doesn't
    // also need to import JOptionPane.
    
    /** No icon is used. */
    public static final int PLAIN_MESAGE = JOptionPane.PLAIN_MESSAGE;
    
    /** Used for error messages. */
    public static final int ERROR_MESSAGE = JOptionPane.ERROR_MESSAGE;
    
    /** Used for warning messages. */
    public static final int WARNING_MESSAGE = JOptionPane.WARNING_MESSAGE;
    
    /** Used for questions. */
    public static final int QUESTION_MESSAGE = JOptionPane.QUESTION_MESSAGE;

    /** Type used for <code>showConfirmDialog</code>. */
    public static final int OK_CANCEL_OPTION = JOptionPane.OK_CANCEL_OPTION;
    
    /** Return value from class method if YES is chosen. */
    public static final int YES_OPTION = JOptionPane.YES_OPTION;
    
    /** Type used for <code>showConfirmDialog</code>. */
    public static final int YES_NO_OPTION = JOptionPane.YES_NO_OPTION;

    /** Type used for <code>showConfirmDialog</code>. */
    public static final int YES_NO_CANCEL_OPTION = JOptionPane.YES_NO_CANCEL_OPTION;

    /** Return value from class method if user closes window without selecting anything,
     * more than likely this should be treated as either a <code>CANCEL_OPTION</code>
     * or <code>NO_OPTION</code>. */
    public static final int CLOSED_OPTION = JOptionPane.CLOSED_OPTION;
    
    /** Return value from class method if NO is chosen. */
    public static final int NO_OPTION = JOptionPane.NO_OPTION;
    
    /** Type meaning Look and Feel should not supply any options --
     * only use the options from the <code>JOptionPane</code>.
     */
    public static final int DEFAULT_OPTION = JOptionPane.DEFAULT_OPTION;

    /**
     * Brings up an information-message dialog titled "Message".
     *
     * @param parentComponent determines the <code>Frame</code> in
     *      which the dialog is displayed; if <code>null</code>,
     *      or if the <code>parentComponent</code> has no
     *      <code>Frame</code>, a default <code>Frame</code> is used
     * @param message   the <code>Object</code> to display
     * @exception HeadlessException if
     *   <code>GraphicsEnvironment.isHeadless</code> returns
     *   <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static void showMessageDialog(Component parentComponent,
        Object message) throws HeadlessException {
        showMessageDialog(parentComponent, message, "Message",
                    JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Brings up a dialog that displays a message using a default
     * icon determined by the <code>messageType</code> parameter.
     *
     * @param parentComponent determines the <code>Frame</code>
     *      in which the dialog is displayed; if <code>null</code>,
     *      or if the <code>parentComponent</code> has no
     *      <code>Frame</code>, a default <code>Frame</code> is used
     * @param message   the <code>Object</code> to display
     * @param title     the title string for the dialog
     * @param messageType the type of message to be displayed:
     *                  <code>ERROR_MESSAGE</code>,
     *          <code>INFORMATION_MESSAGE</code>,
     *          <code>WARNING_MESSAGE</code>,
     *                  <code>QUESTION_MESSAGE</code>,
     *          or <code>PLAIN_MESSAGE</code>
     * @exception HeadlessException if
     *   <code>GraphicsEnvironment.isHeadless</code> returns
     *   <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static void showMessageDialog(Component parentComponent,
            Object message, String title, int messageType) 
    throws HeadlessException {
        showMessageDialog(parentComponent, message, title, messageType, null);
    }

    /**
     * Brings up a dialog displaying a message, specifying all parameters.
     *
     * @param parentComponent determines the <code>Frame</code> in which the
     *          dialog is displayed; if <code>null</code>,
     *          or if the <code>parentComponent</code> has no
     *          <code>Frame</code>, a 
     *                  default <code>Frame</code> is used
     * @param message   the <code>Object</code> to display
     * @param title     the title string for the dialog
     * @param messageType the type of message to be displayed:
     *                  <code>ERROR_MESSAGE</code>,
     *          <code>INFORMATION_MESSAGE</code>,
     *          <code>WARNING_MESSAGE</code>,
     *                  <code>QUESTION_MESSAGE</code>,
     *          or <code>PLAIN_MESSAGE</code>
     * @param icon      an icon to display in the dialog that helps the user
     *                  identify the kind of message that is being displayed
     * @exception HeadlessException if
     *   <code>GraphicsEnvironment.isHeadless</code> returns
     *   <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    private static void showMessageDialog(Component parentComponent,
            Object message, String title, int messageType, Icon icon) 
    throws HeadlessException {
        showOptionDialog(parentComponent, message, title, JOptionPane.DEFAULT_OPTION, 
                messageType, icon, null, null);
    }

    /**
     * Brings up a dialog with a specified icon, where the initial
     * choice is determined by the <code>initialValue</code> parameter and
     * the number of choices is determined by the <code>optionType</code> 
     * parameter.
     * <p>
     * If <code>optionType</code> is <code>YES_NO_OPTION</code>,
     * or <code>YES_NO_CANCEL_OPTION</code>
     * and the <code>options</code> parameter is <code>null</code>,
     * then the options are
     * supplied by the look and feel. 
     * <p>
     * The <code>messageType</code> parameter is primarily used to supply
     * a default icon from the look and feel.
     *
     * @param parentComponent determines the <code>Frame</code>
     *          in which the dialog is displayed;  if 
     *                  <code>null</code>, or if the
     *          <code>parentComponent</code> has no
     *          <code>Frame</code>, a 
     *                  default <code>Frame</code> is used
     * @param message   the <code>Object</code> to display
     * @param title     the title string for the dialog
     * @param optionType an integer designating the options available on the
     *                  dialog: <code>DEFAULT_OPTION</code>,
     *                  <code>YES_NO_OPTION</code>,
     *                  <code>YES_NO_CANCEL_OPTION</code>,
     *                  or <code>OK_CANCEL_OPTION</code>
     * @param messageType an integer designating the kind of message this is, 
     *                  primarily used to determine the icon from the
     *          pluggable Look and Feel: <code>ERROR_MESSAGE</code>,
     *          <code>INFORMATION_MESSAGE</code>, 
     *                  <code>WARNING_MESSAGE</code>,
     *                  <code>QUESTION_MESSAGE</code>,
     *          or <code>PLAIN_MESSAGE</code>
     * @param icon      the icon to display in the dialog
     * @param options   an array of objects indicating the possible choices
     *                  the user can make; if the objects are components, they
     *                  are rendered properly; non-<code>String</code>
     *          objects are
     *                  rendered using their <code>toString</code> methods;
     *                  if this parameter is <code>null</code>,
     *          the options are determined by the Look and Feel
     * @param initialValue the object that represents the default selection
     *                  for the dialog; only meaningful if <code>options</code>
     *          is used; can be <code>null</code>
     * @return an integer indicating the option chosen by the user, 
     *              or <code>CLOSED_OPTION</code> if the user closed
     *                  the dialog
     * @exception HeadlessException if
     *   <code>GraphicsEnvironment.isHeadless</code> returns
     *   <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static int showOptionDialog(Component parentComponent,
            Object message, String title, int optionType, int messageType,
            Icon icon, Object[] options, Object initialValue) 
    throws HeadlessException {
        JOptionPane pane = new JOptionPane(message, messageType,
                optionType, icon,
                options, initialValue
        );

        pane.setInitialValue(initialValue);
        pane.setComponentOrientation(((parentComponent == null) ?
                JOptionPane.getRootFrame() : parentComponent).getComponentOrientation());

        pane.setMessageType(messageType);
        JDialog dialog = pane.createDialog(parentComponent, title);

        pane.selectInitialValue();
        
        // These two lines are different from JOptionPane.showOptionDialog().
        dialog.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
        dialog.setLocationRelativeTo(parentComponent);
        
        dialog.setVisible(true);
        dialog.dispose();

        Object selectedValue = pane.getValue();

        if(selectedValue == null)
            return JOptionPane.CLOSED_OPTION;
        if(options == null) {
            if(selectedValue instanceof Integer)
                return ((Integer)selectedValue).intValue();
            return JOptionPane.CLOSED_OPTION;
        }
        for(int counter = 0, maxCounter = options.length;
        counter < maxCounter; counter++) {
            if(options[counter].equals(selectedValue))
                return counter;
        }
        return JOptionPane.CLOSED_OPTION;
    }

}
