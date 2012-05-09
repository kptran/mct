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
package gov.nasa.arc.mct.core.policy;

import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.Policy;
import gov.nasa.arc.mct.policy.PolicyContext;

/**
 * This class implements the reserved words name policy.
 * Currently, the only words not allowed in the name of a
 * group is "Drop Box" or "DropBox".
 * 
 * Also, the matching will occur regardless of capitalization
 * (ie, Drop Box == drop box)
 * 
 * To expand this, add more words to the RESERVED_WORDS strings...
 * 
 * @author jjupin
 *
 */

public class ReservedWordsNamingPolicy implements Policy {
    
    static final String[] RESERVED_WORDS = {"drop box", "dropbox"};

    @Override
    public ExecutionResult execute(PolicyContext context) {
      ExecutionResult result = new ExecutionResult(context, true, "");
        
        String name = context.getProperty("NAME", String.class);
        String found = "";
        
        for (int i=0; i<RESERVED_WORDS.length; i++) {
        
            if (name.toLowerCase().indexOf(RESERVED_WORDS[i].toLowerCase()) > -1) {
                result.setStatus(false);
                found = RESERVED_WORDS[i];              
            }
        }
        
        if (!result.getStatus())
            result.setMessage("Name must not contain the words \"" + found + "\"");
        
        return result;
    }
}
