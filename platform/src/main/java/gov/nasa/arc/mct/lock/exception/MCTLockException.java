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
 * MCTLockException.java Dec 1, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.lock.exception;

import gov.nasa.arc.mct.util.exception.MCTRuntimeException;

/**
 * MCT lock exception wrapper for runtime exception handling.
 *
 */
@SuppressWarnings("serial")
public class MCTLockException extends MCTRuntimeException {
	
    /**
     * Constructor overloaded with exception message and throwable cause.
     * @param msg exception 
     * @param t throwable exception cause
     */
    public MCTLockException(String msg, Throwable t) {
        super(msg, t);
    }
    
    /**
     * Constructor with just exception message.
     * @param msg exception
     */
    public MCTLockException(String msg) {
        super(msg);
    }
    
    /**
     * Constructor with just throwable cause.
     * @param t throwable exception cause
     */
	public MCTLockException(Throwable t) {
		super(t);
	}

}
