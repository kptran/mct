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

import gov.nasa.arc.mct.util.ext.commands.CmdProcessBuilder;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestCmdProcessBuilder {
    
	private CmdProcessBuilder cmdProcessBuilder;
	
	@BeforeMethod
	void setup() {    
	    cmdProcessBuilder = new CmdProcessBuilder();
	    
		System.setProperty("ExecLimitManagerPath", "src/test/resources/");
		System.setProperty("ExecLimitManagerScript", "launchLimitMgrTest.sh");
	}
	
	@Test
	public void testOSPlatformSupported() {
	    
		Assert.assertNotNull(cmdProcessBuilder.checkOSPlatform());
		
		if (cmdProcessBuilder.isWindows()) {
		    cmdProcessBuilder.setOSPlatform("Windows XP");
		    Assert.assertTrue(cmdProcessBuilder.isWindows());
		}
		
		if (cmdProcessBuilder.isMacOS()) {
		    cmdProcessBuilder.setOSPlatform("Mac OS X");
		    Assert.assertTrue(cmdProcessBuilder.isMacOS());
		}
		
		if (cmdProcessBuilder.isUNIXLinux()) {
		    cmdProcessBuilder.setOSPlatform("Linux");
		    Assert.assertTrue(cmdProcessBuilder.isUNIXLinux());
		}
		
	}
	
	@Test(dependsOnMethods="testOSPlatformSupported")
	public void testCmdExecSuccessful() {
		final String PUI = "TESTCOMMANDPUI";
		final List<String> commandList = new ArrayList<String>();
		
		if (cmdProcessBuilder.isMacOS()) {
		    Assert.assertEquals(cmdProcessBuilder.checkOSPlatform(), "Mac OS X");
		    commandList.add(System.getProperty("ExecLimitManagerScript") + " " + PUI);
		} else if (cmdProcessBuilder.isUNIXLinux()) {
		    commandList.add(System.getProperty("ExecLimitManagerScript") + " " + PUI);
		}
		
		if (cmdProcessBuilder.isMacOS() || cmdProcessBuilder.isUNIXLinux()) {
		    Assert.assertTrue(cmdProcessBuilder.execMultipleCommands(System.getProperty("ExecLimitManagerPath"), commandList));
		}
	}
	
	@Test(dependsOnMethods="testOSPlatformSupported")
	public void testCmdExecFailed() {
		final String PUI = "XYZ123abc456";
		final List<String> commandList = new ArrayList<String>();
		
		if (cmdProcessBuilder.isUNIXLinux()) {
		    Assert.assertEquals(cmdProcessBuilder.checkOSPlatform(), "Linux");
		    commandList.add(System.getProperty("ExecLimitManagerScript") + " " + PUI);
		} else if (cmdProcessBuilder.isMacOS()) {
		    commandList.add(System.getProperty("ExecLimitManagerScript") + " " + PUI);
		}
		
		if (cmdProcessBuilder.isMacOS() || cmdProcessBuilder.isUNIXLinux()) {
		    Assert.assertFalse(cmdProcessBuilder.execMultipleCommands("/wrong/path/", commandList));
		}
	}
	
}
