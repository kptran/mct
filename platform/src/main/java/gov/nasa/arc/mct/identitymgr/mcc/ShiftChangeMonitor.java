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

import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.util.StringUtil;
import gov.nasa.arc.mct.util.exception.MCTRuntimeException;
import gov.nasa.arc.mct.util.logging.MCTLogger;
import gov.nasa.arc.mct.util.property.MCTProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Monitor of shift change events.
 */
public class ShiftChangeMonitor implements Runnable, IShiftChangeObservable {
    private static MCTLogger logger = MCTLogger.getLogger(ShiftChangeMonitor.class);

    final static int DEFAULT_PORT = 9999;

    List<IShiftChangeObserver> observers = new ArrayList<IShiftChangeObserver>();
    Thread listenerThread = null;
    private volatile boolean mayRun = false;
    int port;
    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    private ListenerShutdownHook shutHook = new ListenerShutdownHook(this);
    private MCCIdentityManager primaryObserver = null;

    /**
     * Creates a shift change monitor.
     * @param parent ID manager parent 
     */
    public ShiftChangeMonitor(MCCIdentityManager parent) {
        this.primaryObserver = parent;
        this.port = initPort();
        this.listenerThread = new Thread(this);
        listenerThread.setName("ShiftChangeMonitor");

        // this hook ensures thread shutdown upon GUI window closing
        Runtime rt = Runtime.getRuntime();
        Thread hook = new Thread(shutHook);
        rt.addShutdownHook(hook);
    }

    void startMonitor() {
        if (System.getProperty("disableShiftChangeMonitor", "false").equalsIgnoreCase("true")) {
            logger.info("disableShiftChangeMonitor ");
        } else {
            mayRun = true;
            try {
                serverSocket = new ServerSocket();
                serverSocket.setReuseAddress(true);
                InetSocketAddress address = new InetSocketAddress(InetAddress.getByName("localhost"), port);
                serverSocket.bind(address, 10);
                logger
                        .info(
                                "Shift change monitor: user {0} specified at startup. Listening for shift change events on port {1}",
                                this.primaryObserver.getCurrentUser(), port);
            } catch (BindException e1) {
                logger.error("Cannot bind to port: " + port);
                throw new MCTRuntimeException(e1);
            } catch (IOException e1) {
                logger.error(e1.getMessage(), e1);
                mayRun = false;
            }
            if (mayRun) {
                listenerThread.start();
            }
        }
    }

    /**
     * Runs a shift change monitor thread. This thread continually listens for
     * shift change events. When a valid shift change event is received, it
     * notifies observers.
     */
    @Override
    public void run() {
        try {
            while (mayRun) {
                BufferedReader in = null;
                try {
                    clientSocket = serverSocket.accept();
                    logger.debug("accepting..");
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String mccCurrentUser;

                    while ((mccCurrentUser = in.readLine()) != null) {

                        if (StringUtil.isEmpty(mccCurrentUser)) {
                            logger.info("Shift change attempt for empty value.");
                            continue;
                        }
                        if (mccCurrentUser.equals(primaryObserver.getCurrentUser())) {
                            logger.info("Shift change attempt to same user.");
                            continue;
                        }
                        if (GlobalContext.getGlobalContext().getUser().getValidUser(mccCurrentUser) == null) {
                            logger.info("Shift change attempt for an invalid user: {0}", mccCurrentUser);
                            continue;
                        }
                        primaryObserver.setCurrentUser(mccCurrentUser);
                        notifyShiftChangeObservers(mccCurrentUser);
                    }

                } catch (SocketException e) {
                    if (mayRun)
                        logger.info("ShiftChangeMonitor socket {0}", e.getMessage());
                } finally {
                    if (in != null)
                        in.close();
                    if (clientSocket != null)
                        clientSocket.close();
                }
            }
        } catch (IOException e1) {
            logger.error(e1.getMessage(), e1);
            mayRun = false;
        }

    }

    /*
     * let the subscribers know the event happened
     */
    synchronized void notifyShiftChangeObservers(String u) {
        // IdentityManagerSubsystem is notified first, to set currentUser
        primaryObserver.setCurrentUser(u);
        for (IShiftChangeObserver o : observers) {
            o.shiftChangeEvent(u);
        }

    }

    Runnable getShutdownRunnable() {
        return shutHook;
    }

    void shutdownThread() {

        mayRun = false;
        try {
            if (serverSocket != null)
                serverSocket.close();
            if (clientSocket != null)
                clientSocket.close();
        } catch (IOException e) {
            logger.error("error shutting down listener", e);
        }
    }

    /*
     * shutdown Hook is thread that is created, but not started until shutdown
     */

    private static class ListenerShutdownHook implements Runnable {
        private ShiftChangeMonitor listener = null;

        private ListenerShutdownHook(ShiftChangeMonitor listener) {
            this.listener = listener;
        }

        public void run() {
            logger.info("Running " + this.getClass().getName());
            listener.shutdownThread();
        }
    }

    @Override
    public synchronized void addObserver(IShiftChangeObserver o) {
        observers.add(o);

    }

    @Override
    public synchronized void removeObserver(IShiftChangeObserver o) {
        observers.remove(o);

    }

    /*
     * unit test only: for simulation/testing
     */
    protected void simEvent(String u) {
        primaryObserver.setCurrentUser(u);
    }

    protected int getPort() {
        return port;
    }

    /*
     * get port number property, else default
     */
    private int initPort() {
        String evarPort = MCTProperties.DEFAULT_MCT_PROPERTIES.getProperty("mcc.monitor.port");
        if (evarPort != null)
            return new Integer(evarPort);
        else
            return ShiftChangeMonitor.DEFAULT_PORT;
    }

    /**
     * Returns true if the shift change monitor is running.
     * @return true if monitor is up.
     */
    public boolean isMonitorRunning() {
        return mayRun;
    }

}
