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
 * Utility to create alert boxes with LF of MCT
 * 
 * 
 * Created by Blake Arnold
 */
package gov.nasa.arc.mct.util.alert;


import gov.nasa.arc.mct.util.property.MCTProperties;
import gov.nasa.arc.mct.util.resource.*;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;




public class AlertBox {
	public AlertBox() {


	}

	/**
	 * Creates alert box for logging
	 * 
	 * @param title the title of the alert box
	 * @param msg the alert message
	 * @param format the formating for JOptionPane
	 * @return return the option index the user clicked
	 */
	public static Object logBox(String title, String msg, int format) {
		
		
		setLAF();

		String[] options = { RStrings.get("CLOSE")};



		JOptionPane pane = new JOptionPane(msg, format,
				JOptionPane.OK_CANCEL_OPTION, null, options);

		JDialog dialog = pane.createDialog(title);


		dialog.setVisible(true);
		
		
		return pane.getValue();// waiting for user to close

	}
	
	/**
	 * Sets look and feel for alert box if null
	 */
	private static void setLAF()
	{
		final MCTProperties mctProperties = MCTProperties.DEFAULT_MCT_PROPERTIES;

		String laf = mctProperties.getProperty("mct.look.and.feel");
		
		JFrame.setDefaultLookAndFeelDecorated(false);

		try {
			UIManager.setLookAndFeel(laf);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		LookAndFeel current = UIManager.getLookAndFeel();
		
		if (current.getName().equals("Metal")) {

			UIManager.put("swing.boldMetal", Boolean.FALSE);
		}
	}
	


}
