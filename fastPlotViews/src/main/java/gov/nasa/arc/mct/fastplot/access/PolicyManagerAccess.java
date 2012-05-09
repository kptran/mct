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
package gov.nasa.arc.mct.fastplot.access;

import gov.nasa.arc.mct.services.component.PolicyManager;

import java.util.concurrent.atomic.AtomicReference;

/**
 * The <code>PolicyManagerAccess</code> class is used to inject an instance of the <code>PolicyManager</code> 
 * using declarative services. This OSGi component does not expose an interface (see OSGI-INF/component.xml) 
 * and thus will be usable from other bundles (the class is not exported from this bundle). This class is 
 * thread safe as this may be accessed from multiple threads and the registry instance must be visible across 
 * all threads. 
 */
public final class PolicyManagerAccess {
    private static AtomicReference<PolicyManager> manager = new AtomicReference<PolicyManager>();
    
    public void setPolicyManager(PolicyManager policyManager) {
        manager.set(policyManager);
    }
    
    public void unsetPolicyManager(PolicyManager pm) {
        manager.set(null);
    }

    public static PolicyManager getPolicyManager() {
        return manager.get();
    }
}
