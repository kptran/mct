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
package gov.nasa.arc.mct.gui.actions;

import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.ContextAwareAction;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.arc.mct.gui.OptionBox;

@SuppressWarnings("serial")
public class HelpMCTAction extends ContextAwareAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelpMCTAction.class);
    
    public static final String TEXT = "MCT User Documents";
    private boolean browserReturned = false;
    private String filePath = "";
    private URI uri = null;
    private String sysUserDir = "";
    private String totalFilePath = "";
    
    public HelpMCTAction() {
        super(TEXT);
    }

    @Override
    public boolean isEnabled() {
        return doesHelpDirExists();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if (Desktop.isDesktopSupported()) {
           try {
                
               if ( (totalFilePath == null) || totalFilePath.isEmpty()) {
                   OptionBox.showMessageDialog(null, "<HTML>Unable to open MCT Help file.  <BR>" +
                           "File does not exist! <BR> " +
                           "Looking for file in: <B> " + sysUserDir + "/" + filePath + "</B><BR>",
                           "Error opening Help File!",
                           OptionBox.ERROR_MESSAGE);  
                   return;
               }
               
               
                if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE) && doesHelpDirExists()) {
                    Desktop.getDesktop().browse(uri);
                        
                } else {
                        OptionBox.showMessageDialog(null, "A Browser is not supported for this Desktop", 
                                "Error opening HTML File!", OptionBox.ERROR_MESSAGE);
                        return;
                } 
                
            } catch (Exception exp) {
                
                OptionBox.showMessageDialog(null, "<HTML>MCT Help files do not exist for Generic Platform. <BR>Unable to open MCT Help file.  <BR>" +
                       "Looking for help.html file in: <B> " + totalFilePath + "</B><BR>" +
                        "Error as follows: " + exp.toString(), "Error opening Help File!",
                        OptionBox.ERROR_MESSAGE);
            }
            
        } else {
            OptionBox.showMessageDialog(null, "Cannot find a supported Desktop - aborting...", 
                    "Error opening HTML File!", OptionBox.ERROR_MESSAGE);
            return;

        }
        browserReturned = true;
    }

    @Override
    public boolean canHandle(ActionContext context) {
        return doesHelpDirExists();
    }

    public boolean getBrowserReturned() {
        return browserReturned;
    }
    
    public boolean doesHelpDirExists() {
        
        boolean dirExists = false;
        sysUserDir = System.getProperty("user.dir");
        totalFilePath = sysUserDir + "/" + filePath;
        
         try {
             
             String os = System.getProperty("os.name");
                          
             if (os.indexOf("Mac") >= 0) {
                 filePath = "help/mct_help.html";
                 uri = ClassLoader.getSystemResource(filePath).toURI();
                 
                 if (uri != null) {
                     dirExists = true;
                 }
                 
             } else {
                 // Assumes it's UNIX/Linux
                 // Checks that for MCT generic platform the help docs directory
                 // are removed for now b/c the docs are only tailored for JSC
                 filePath = "resources/help/mct_help.html";
                 File f = new File(sysUserDir + "/" + filePath);
                 if (f.exists()) {
                     uri = f.toURI();
                     dirExists = true;
                 } else {
                     
                     LOGGER.warn("MCT Help files do not exist for Generic Platform." +
                             totalFilePath);    
                 }
             }

         } catch (Exception exp) {
             LOGGER.warn("MCT Generic Platform does not have HELP files. Unable to open MCT Help file. Looking for help.html file in: "
                     + totalFilePath);
         }
         
         return dirExists;
    }
}
