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
package gov.nasa.arc.mct.core.components;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.core.util.ComponentSecurityProperties;
import gov.nasa.arc.mct.platform.core.access.PlatformAccess;
import gov.nasa.arc.mct.services.internal.component.ComponentInitializer;

import java.util.Collection;
import java.util.Collections;

/**
 * This component implements the All Discipline Drop Box.
 * 
 * @author nshi
 * 
 */
public final class TelemetryAllDropBoxComponent extends AbstractComponent {
    private static final String OWNER = ComponentSecurityProperties.parseNameValuefromPolicy("discipline.owner");

    private String disciplineId;

    /**
     * For internal use only.
     */
    public TelemetryAllDropBoxComponent() {
        setShared(true);
        setOwner(OWNER);
        this.disciplineId = null;
        this.getCapability(ComponentInitializer.class).setComponentReferences(
                Collections.<AbstractComponent> singleton(AbstractComponent.NULL_COMPONENT)
        );
    }
    
    @Override
    protected void initialize() {
        super.initialize();
        PlatformAccess.getPlatform().getLockManager().lockForAllUser(getId());
    }
  
    /**
     * Set the discipline ID for the component.
     * disciplineId the discipline ID.
     */
    public void setDisciplineId(String disciplineId) {
        this.disciplineId = disciplineId;
    }
    
    @Override
    protected void addDelegateComponentsCallback(Collection<AbstractComponent> childComponents) {
        if (isEmptyDropBoxes())
            return;

        for (AbstractComponent userDropBox : getComponents()) {            
            ((TelemetryUserDropBoxComponent)userDropBox).dropDelegateComponents(childComponents);
        }

    }
    
    @Override
    protected void additionalRefresh() {
        // refresh all related drop boxes also
        if (isEmptyDropBoxes())
            return;

        for (AbstractComponent userDropBox : getComponents()) {            
            ((TelemetryUserDropBoxComponent)userDropBox).refreshViewManifestations();
        }
    }

    private boolean isEmptyDropBoxes() {
        return getComponents().isEmpty();
    }

    public String getDisciplineId() {
        return disciplineId;
    }
    
    @Override
    public boolean isLeaf() {
        return true;
    }
    
    /**
     * Internal Use.
     * @param component
     */
    public void addNewAndInitializeUserDropboxes(Collection<AbstractComponent> dropboxes) {
        int index = getComponents().size();
        saveComponentsToDatabase(index, dropboxes);
        
        if (dropboxes.isEmpty()) {
            refreshViewManifestations();
        } else {
            for (AbstractComponent dropbox : dropboxes)
                addDelegateComponent(dropbox);
            refreshManifestationFromInsert(index, dropboxes);
        }

    }

}
