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
package gov.nasa.arc.mct.util.ext.commands;

import gov.nasa.arc.mct.util.ext.commands.CmdProcessBuilderStreamHelper;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestCmdProcessBuilderStreamHelper {

	private CmdProcessBuilderStreamHelper cmdPBSHelper;
    private Runtime runTime; 
    private Process process;

	private int exitValueWaitFor = 1;
	private int exitValue = 2;
	private static final String testPUI = "testCOMMANDPUIT";
	private CmdProcessBuilder cmdProcessBuilder;
	
	@BeforeMethod
	void setup() {
	    cmdProcessBuilder = new CmdProcessBuilder();
		System.setProperty("ExecLimitManagerPath", "src/test/resources/");
		System.setProperty("ExecLimitManagerScript", "launchLimitMgrTest.sh");
	}
	
	@Test
    public void testGetIOStreamMsgs() {
        
        final String execCmd = System.getProperty("ExecLimitManagerPath") 
            +  System.getProperty("ExecLimitManagerScript") + " " + testPUI;
 
        try {
            if (cmdProcessBuilder.isMacOS() || cmdProcessBuilder.isUNIXLinux()) {
                
                initializeRuntimeProcess(execCmd);
        
                cmdPBSHelper = new CmdProcessBuilderStreamHelper(process.getInputStream(), "OUTPUT");
                cmdPBSHelper.start();
                Assert.assertNotNull(cmdPBSHelper);
        
                exitValueWaitFor = process.waitFor();
                Assert.assertEquals(0, exitValueWaitFor);
        
                exitValue = process.exitValue();
                Assert.assertEquals(0, exitValue);
            }
        
            if ((cmdPBSHelper != null) && !cmdPBSHelper.getIOStreamMessages().isEmpty()) {
                System.out.println("*** " + cmdPBSHelper.getIOStreamMessages());
                Assert.assertTrue(cmdPBSHelper.getIOStreamMessages().contains(testPUI));
            } 
            
        } catch (InterruptedException e2) {
            System.err.println("InterruptedException: " + e2.getMessage());
            e2.printStackTrace();
        }
    }
	
	@Test
	public void testIOStreamBuilderHelper() {

		final String execCmd = System.getProperty("ExecLimitManagerPath") 
			+  System.getProperty("ExecLimitManagerScript") + " " + testPUI;
		
		try {
		    if (cmdProcessBuilder.isMacOS() || cmdProcessBuilder.isUNIXLinux()) {
		        initializeRuntimeProcess(execCmd);
		    
		        // Prints out any stderror msgs
		        cmdPBSHelper = new CmdProcessBuilderStreamHelper(process.getErrorStream(), "ERROR");
		        cmdPBSHelper.start();
		        Assert.assertNotNull(cmdPBSHelper);
		
			    exitValueWaitFor = process.waitFor();
			    Assert.assertEquals(0, exitValueWaitFor);
			
			    exitValue = process.exitValue();
			    Assert.assertEquals(0, exitValue);
			}
			
			if ((cmdPBSHelper != null) && !cmdPBSHelper.getIOStreamMessages().isEmpty()) {
			    System.err.println(">>> " + cmdPBSHelper.getIOStreamMessages());
			    Assert.assertTrue(cmdPBSHelper.getIOStreamMessages().contains("ERROR"));
			} 
					
		} catch (InterruptedException e2) {
		    System.err.println("InterruptedException: " + e2.getMessage());
			e2.printStackTrace();
		}
		
	}
	
	private void initializeRuntimeProcess(String execCmd) {
	    
	    try {
	        
	        runTime = Runtime.getRuntime();
	        Assert.assertNotNull(runTime);
	        process = runTime.exec(execCmd);
            Assert.assertNotNull(process);
            
	    } catch (IOException e1) {
            System.err.println("IOException: " + e1.getMessage());
            e1.printStackTrace();
        }
	}
	
}
