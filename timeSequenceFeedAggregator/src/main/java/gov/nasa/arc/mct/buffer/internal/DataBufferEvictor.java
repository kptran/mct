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
package gov.nasa.arc.mct.buffer.internal;


import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class DataBufferEvictor extends Timer {
    private static final Logger logger = LoggerFactory.getLogger(DataBufferEvictor.class);
    
    private final long evictMillis;
    private final long switchMillis;
    private final DataBuffer dataBuffer;
    
    DataBufferEvictor(DataBuffer dataBuffer, long evictMillis, long switchMillis) {
        super("DataBuffer Evictor", true);
        this.evictMillis = evictMillis;
        this.switchMillis = switchMillis;
        this.dataBuffer = dataBuffer;
    }
    

    void schedule() {
        super.schedule(newPrepareTask(), evictMillis);
    }

    
    private TimerTask newPrepareTask() {
        return new TimerTask() {
            
            @Override
            public void run() {
                try {
                    dataBuffer.prepareForNextPartition();
                    schedule(newMoveTask(), switchMillis);
                } catch(Exception e) {
                    logger.error(e.toString(), e);
                    schedule(newPrepareTask(), evictMillis);
                }
            }
        };
    }
    
    private TimerTask newMoveTask() {
        return new TimerTask() {
            
            @Override
            public void run() {
                try {
                    dataBuffer.moveToNextPartition();
                    schedule(newPrepareTask(), evictMillis);
                } catch(Exception e) {
                    logger.error(e.toString(), e);
                    schedule(newMoveTask(), switchMillis);
                }
            }
        };
    }
}
