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
package gov.nasa.arc.mct.coreTaxonomyProvider;

import gov.nasa.arc.mct.core.provider.CoreComponentProvider;
import gov.nasa.arc.mct.platform.core.access.PlatformAccess;
import gov.nasa.arc.mct.platform.core.access.PolicyManagerAccess;
import gov.nasa.arc.mct.platform.core.access.TagAccess;
import gov.nasa.arc.mct.platform.spi.DefaultComponentProvider;
import gov.nasa.arc.mct.platform.spi.Platform;
import gov.nasa.arc.mct.services.component.ComponentProvider;
import gov.nasa.arc.mct.services.component.ComponentTagService;
import gov.nasa.arc.mct.services.component.PolicyManager;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

    @Override
    public void start(BundleContext context) {
        ServiceReference sr = context.getServiceReference(Platform.class.getName());
        Platform platform = (Platform) context.getService(sr);
        (new PlatformAccess()).setPlatform(platform);
        context.ungetService(sr);

        sr = context.getServiceReference(PolicyManager.class.getName());
        PolicyManager policyManager = (PolicyManager) context.getService(sr);
        (new PolicyManagerAccess()).setPolciyManager(policyManager);
        context.ungetService(sr);

        sr = context.getServiceReference(ComponentTagService.class.getName());
        ComponentTagService tagService = (ComponentTagService) context.getService(sr);
        (new TagAccess()).setTagService(tagService);
        context.ungetService(sr);

        context.registerService(new String[] { ComponentProvider.class.getName(),
                DefaultComponentProvider.class.getName() }, new CoreComponentProvider(), null);
    }

    @Override
    public void stop(BundleContext context) {
        (new PlatformAccess()).releasePlatform();
        (new PolicyManagerAccess()).releasePolicyManager();
        (new TagAccess()).releaseTagService();
    }

}