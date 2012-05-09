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
 * ShiftChangeReporter.java
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 */

package gov.nasa.arc.mct.identitymgr.mcc;

import gov.nasa.arc.mct.util.StringUtil;
import gov.nasa.arc.mct.util.exception.MCTException;
import gov.nasa.arc.mct.util.logging.MCTLogger;
import gov.nasa.arc.mct.util.property.MCTProperties;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * Reporter of shift change events.
 *
 */
public class ShiftChangeReporter {
    private static MCTLogger logger = MCTLogger.getLogger(ShiftChangeReporter.class);
    private volatile boolean mayRun = false;

    enum ConnectionStatus {
        STATUS_CONNECTED, STATUS_DISCONNECTED
    };

    ConnectionStatus connectionStatus = ConnectionStatus.STATUS_DISCONNECTED;
    private int monitorPort = -1;
    private int interval;
    static final int DEFAULT_INTERVAL = 20;
    static final int DEFAULT_EXPIRATION = 0;
    private String execCommand = null;
    private String currentUser;
    private int reporterExpirationSeconds = 0;

    /**
     * Creates a shift change reporter.
     * @param initialUser the current user ID
     * @throws MCTException
     * @throws IOException
     */
    public ShiftChangeReporter(String initialUser) throws MCTException, IOException {
        final MCTProperties mctProperties = MCTProperties.DEFAULT_MCT_PROPERTIES;

        if (StringUtil.isEmpty(initialUser))
            throw new MCTException("empty or null initial user");
        currentUser = initialUser;

        String portProp = mctProperties.getProperty("mcc.monitor.port");
        if (StringUtil.isEmpty(portProp)) {
            throw new MCTException("property mcc.monitor.port is not set or empty.");
        }
        try {
            this.monitorPort = new Integer(portProp);
        } catch (NumberFormatException e) {
            throw new MCTException(
                    "Could not convert mcc.monitor.port to a valid port. Check that mcc.monitor.port is set.", e);
        }

        reporterExpirationSeconds = DEFAULT_EXPIRATION;
        String expire = mctProperties.getProperty("mcc.reporter.expiration", "0");
        try {
            reporterExpirationSeconds = new Integer(expire);
        } catch (NumberFormatException e) {
            logger.warn("Could not convert mcc.reporter.expiration; using default");
        }

        interval = DEFAULT_INTERVAL;
        String intervalProp = mctProperties.getProperty("mcc.monitor.interval");
        if (!StringUtil.isEmpty(intervalProp)) {
            try {
                this.interval = new Integer(intervalProp);
            } catch (NumberFormatException e) {
                logger.warn("Could not convert mcc.monitor.interval; using default");
            }
        }

        execCommand = mctProperties.getProperty("mcc.monitor.command");
        if (StringUtil.isEmpty(execCommand)) {
            throw new MCTException("property mcc.monitor.command is not set or invalid.");
        }
    }

    private Socket openMonitor() throws MCTException {
        Socket s = null;
        boolean stopTrying = false;
        int connectCounter = 1;

        while (!stopTrying && connectionStatus == ConnectionStatus.STATUS_DISCONNECTED) {
            try {
                s = new Socket("localhost", monitorPort);
                connectionStatus = ConnectionStatus.STATUS_CONNECTED;
                stopTrying = true;
            } catch (ConnectException e) {
                if (connectCounter >= 5) {
                    stopTrying = true;
                    throw new MCTException("Could not open socket to port (gave up):" + monitorPort, e);
                } else {
                    logger.debug("retry {0}", connectionStatus);
                    connectCounter++;
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e1) {
                    }
                }
            } catch (IOException e) {
                stopTrying = true;
                throw new MCTException("Exception trying to open socket to port:" + monitorPort, e);
            }
        }
        logger.info("connectionStatus: {0}", connectionStatus);
        return s;
    }

    void closeMonitor(Socket s) {
        if (s != null)
            try {
                s.close();
            } catch (IOException e) {
            }
        connectionStatus = ConnectionStatus.STATUS_DISCONNECTED;
    }

    /*
     * Continually look for a change in user. If detected, report it to shift
     * change monitor.
     */
    void detectShiftChange() throws MCTException {

        if (this.reporterExpirationSeconds > 0) {
            logger.info("Shift change reporter expiration (seconds):{0}", this.reporterExpirationSeconds);
            new ExpirationTask(this.reporterExpirationSeconds);
        }

        mayRun = true;
        while (mayRun) {

            Runtime rt = Runtime.getRuntime();

            Process proc = null;
            try {
                proc = rt.exec(execCommand);
            } catch (IOException e1) {
                throw new MCTException("Could not execute command: " + execCommand);
            }

            // any error message?
            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERR");

            // output from stdout
            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUT", this);

            // kick them off
            errorGobbler.start();
            outputGobbler.start();

            // any error???
            int exitVal = 0;
            try {
                exitVal = proc.waitFor();
            } catch (InterruptedException e) {
                logger.warn("Exec command interrupted.");
            }
            if (exitVal == 0) {
                try {
                    Thread.sleep(interval * 1000);
                } catch (InterruptedException e) {
                    logger.warn("sleep interrupted");
                }
            } else {
                throw new MCTException("exitVal " + exitVal + " returned from command: " + execCommand);
            }
        }
    }

    /**
     * The shift change reporter application entry.
     * @param args command line arguments
     * @throws Throwable
     */
    public static void main(String args[]) throws Throwable {
        String initialUser = null;
        if (args.length != 1 || StringUtil.isEmpty(args[0])) {
            logger.error("You must specify initial user. Exiting");
            return;
        } else {
            initialUser = args[0];
            logger.debug("initial User: {0}", initialUser);
        }
        try {
            ShiftChangeReporter shiftChangeReporter = new ShiftChangeReporter(initialUser);
            shiftChangeReporter.sanityCheck();
            shiftChangeReporter.detectShiftChange();

        } catch (Throwable e) {
            logger.error("Shift Change Reporter Error", e);
            throw e;
        }
    }

    /*
     * Ensures the open socket is ok. We want to discover this now, not at the
     * end of a shift.
     */
    void sanityCheck() throws MCTException {
        Socket s = openMonitor();
        closeMonitor(s);
    }

    int getMonitorPort() {
        return monitorPort;
    }

    String getExecCommand() {
        return execCommand;
    }

    String getCurrentUser() {
        return currentUser;
    }

    int getInterval() {
        return interval;
    }

    /*
     * If the current value changed from the previous, report. @returns true if
     * reported, false upon error or non reporting
     */
    boolean reportIfValueChanged(String newUser) {
        if (currentUser.equalsIgnoreCase(newUser)) {
            logger.debug("no change from current: {0}", currentUser);
            return false;
        }
        Socket socket = null;
        PrintWriter pw = null;
        OutputStream os = null;
        try {
            socket = openMonitor();

            if (socket != null)
                os = socket.getOutputStream();

            if (os != null)
                pw = new PrintWriter(os);

            if (pw != null)
                pw.println(newUser);

        } catch (IOException e) {
            logger.error("Could not get output stream ", e);
        } catch (MCTException e) {
            logger.error(e);
        } finally {
            if (pw != null) {
                pw.flush();
                pw.close(); // else servers get Connection Reset
            }
            closeMonitor(socket);
        }
        currentUser = newUser;
        logger.info("reported: {0}", newUser);
        return true;
    }

    void setReporterExpiration(int reporterExpiration) {
        this.reporterExpirationSeconds = reporterExpiration;
    }

    class ExpirationTask {
        final Timer timer;
        private int expireSeconds;

        public ExpirationTask(int seconds) {
            expireSeconds = seconds;
            timer = new Timer();
            timer.schedule(new StopTask(), seconds * 1000);
        }

        class StopTask extends TimerTask {
            public void run() {
                mayRun = false;
                logger.info("Expiration of Reporter after {0} seconds", expireSeconds);
                timer.cancel();
            }
        }
    }
}
