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
 * IdentityManager
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */

package gov.nasa.arc.mct.identitymgr.mcc;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import org.testng.Assert;

public class IdTestUtils {

    /*
     * IANA Unassigned ports 36866-37474 37476-37653 37655-38200
     */
    private static Integer getUnusedPort() {
        ServerSocket serverSocket = null;
        int ret = 0;
        for (int port = 36866; port <= 38200; port++) {
            try {
                serverSocket = new ServerSocket();
                serverSocket.setReuseAddress(true);
                InetSocketAddress address = new InetSocketAddress(InetAddress.getByName("localhost"), port);
                serverSocket.bind(address, 10);
            } catch (BindException e1) {
                continue;
            } catch (Exception e) {
                Assert.fail("Could not find an unused port");
            } finally {
                if (serverSocket != null) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        Assert.fail("could not close socket");
                    }
                }
            }
            ret = port;
            break;
        }
        System.err.println("monitor port:" + ret);
        return ret;
    }

    public static void setMctProperties() {
        System.setProperty("mcc.monitor.port", IdTestUtils.getUnusedPort().toString());
        System.setProperty("mcc.monitor.interval", "14");
        System.setProperty("mcc.monitor.command", "echo hello");
    }

    public static void restoreMctProperties() {
        System.setProperty("mcc.monitor.port", IdTestUtils.getUnusedPort().toString());
    }
}
