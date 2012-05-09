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
package gov.nasa.arc.mct.util.condition;


/**
 * An arbitrary boolean condition, expressed as a Java object with a method
 * returning whether the value is true or not.
 * 
 * @author mrose
 *
 */
public abstract class Condition {

    /**
     * Check whether the condition is satisfied.
     * 
     * @return true, if the condition is satisfied
     */
    public abstract boolean getValue();

    /**
     * Wait until a given condition becomes true.
     * 
     * @param maxWait the maximum time to wait, in milliseconds
     * @param cond the condition we want to wait on
     * @return true, if the condition is satisfied within the maximum wait time, else false
     */
    public static boolean waitForCondition(long maxWait, Condition cond) {
        Object signalObject = new Object();
        new Condition.PollingThread(cond, maxWait, signalObject).start();
        
        synchronized (signalObject) {
            try {
                signalObject.wait();
            } catch (InterruptedException e) {
                // ignore
            }
        }
        
        return cond.getValue();
    }
    
    /**
     * A thread that polls for a condition to be true. Once the condition is satisfied,
     * notify a specified object that another thread will be waitin on.
     * 
     * @author mrose
     *
     */
    public static class PollingThread extends Thread {
        
        private Condition cond;
        long maxWait;
        Object signalObject;
        
        public PollingThread(Condition cond, long maxWait, Object signalObject) {
            this.cond = cond;
            this.maxWait = maxWait;
            this.signalObject = signalObject;
        }
        
        public void run() {
            long start = System.currentTimeMillis();
            
            for (;;) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    // ignore
                }
    
                long now = System.currentTimeMillis();
                if (now-start > maxWait) {
                    break;
                }
                if (cond.getValue()) {
                    break;
                }
            }
            
            synchronized (signalObject) {
                signalObject.notify();
            }
        }
    
    }

}
