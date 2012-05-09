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
package gov.nasa.arc.mct.persistence.strategy;

/**
 * This exception is thrown when an optimistic lock collision occurs. For example, when attempting to persist
 * while the underlying database has been updated by another client. This exception should be handled in one of
 * two ways: 
 * <ul>
 *     <li>If the change is similar to an append, where previous changes are not lost, then replay the change.</li>
 *     <li>Let the user know there was a problem and provide the opportunity to get the new changes and overwrite 
 *     possibly by merging (like a source code control merge) or creating a new instance that contains the changes if desired.</li>
 * </ul>
 *
 */
public class OptimisticLockException extends RuntimeException {

    private static final long serialVersionUID = -618957107961134329L;

    public OptimisticLockException(Exception cause) {
        super(cause);
    }
    
    public OptimisticLockException(String message, Exception cause) {
        super(message, cause);
    }
    
}
