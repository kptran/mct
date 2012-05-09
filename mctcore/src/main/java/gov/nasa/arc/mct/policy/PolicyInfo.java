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
package gov.nasa.arc.mct.policy;

/**
 * Provides information for registering policies.
 * 
 * @author nija.shi@nasa.gov
 */
public final class PolicyInfo {

    /**
     * Defines an enumeration of category types.
     * 
     * @author nija.shi@nasa.gov
     */
    public enum CategoryType {
        /**
         * This category is triggered upon modification of a component. For
         * example, a drag n' drop onto a component triggers this policy
         * category.
         */
        COMPOSITION_POLICY_CATEGORY("COMPOSITION_POLICY_CATEGORY"),
        /**
         * This category is triggered upon
         * {@link gov.nasa.arc.mct.components.AbstractComponent#getViewInfos(ViewType)}. For example, inspecting a
         * component or opening it in its own window.
         */
        FILTER_VIEW_ROLE("FILTER_VIEW_ROLE"),

        /**
         * Beta.
         */
        LOCKING_ENABLE_POLICY_CATEGORY("LOCKING_ENABLE_POLICY_CATEGORY"),
        /**
         * Beta.
         */
        NEED_TO_LOCK_CATEGORY("NEED_TO_LOCK_CATEGORY"),
        /**
         * Beta. This policy can be used to select the preferred view for a component. 
         */
        PREFERRED_VIEW("PREFERRED_VIEW"),
        /**
         * Beta.
         */
        PRIVACY_POLICY_CATEGORY("PRIVACY_POLICY_CATEGORY"),
        /**
         * Beta.
         */
        OBJECT_INSPECTION_POLICY_CATEGORY("OBJECT_INSPECTION_POLICY_CATEGORY"),
        /**
         * Beta.
         */
        OBJECT_VISIBILITY_POLICY_CATEGORY("OBJECT_VISIBILITY_POLICY_CATEGORY"),
        /**
         * Beta.
         */
        COMPONENT_NAMING_POLICY_CATEGORY("COMPONENT_NAMING_POLICY_CATEGORY"),
        /**
         * Beta.
         */
        ALLOW_COMPONENT_RENAME_POLICY_CATEGORY("ALLOW_COMPONENT_RENAME_POLICY_CATEGORY"),

        /**
         * Beta.
         */
        ACCEPT_DELEGATE_MODEL_CATEGORY("ACCEPT_DELEGATE_MODEL_CATEGORY"),
        
        /**
         * Beta.
         */
        CAN_DELETE_COMPONENT_POLICY_CATEGORY("CAN_DELETE_COMPONENT_POLICY_CATEGORY"),
        
        /**
         * Beta.
         */
        CAN_REMOVE_MANIFESTATION_CATEGORY("CAN_REMOVE_MANIFESTATION_CATEGORY"),
        
        /**
         * This category is used to determine whether an object can be contained in another
         * object. This category should only rarely be used and it is likely that you should 
         * not be using this category.  
         */
        CAN_OBJECT_BE_CONTAINED_CATEGORY("CAN_OBJECT_BE_CONTAINED_CATEGORY"),
        
        /**
         * Beta.
         */
        CAN_DUPLICATE_OBJECT("CAN_DUPLICATE_OBJECT"),
        
        /**
         * Each <code>MCTViewManifestation</code> may be associated with a controller,
         * which can be used when the manifestation is shown in the center pane and
         * the inspector area. Initially, the controller is hidden and can be expanded
         * by clicking on the twistie. This policy category controls whether the 
         * twistie is available. 
         */
        SHOW_HIDE_CTRL_MANIFESTATION("SHOW_HIDE_CTRL_MANIFESTATION");
        

        private String key;

        CategoryType(String key) {
            this.key = key;
        }

        /**
         * Returns the <code>String</code> of this category type.
         * 
         * @return the category type as a <code>String</code>.
         */
        public String getKey() {
            return key;
        }
    }

    private String categoryKey;
    private Class<?>[] policyClasses;

    /**
     * Creates a <code>PolicyInfo</code> with an array of policy classes to the
     * given category key. Each policy class must implement the {@link Policy}
     * interface. If not, that policy will not be registered to the policy
     * manager.
     * 
     * @param categoryKey
     *            the policy category key
     * @param policyClasses
     *            the array of policy class instances
     */
    public PolicyInfo(String categoryKey, Class<?>... policyClasses) {
        this.categoryKey = categoryKey;
        this.policyClasses = policyClasses;
    }

    /**
     * Returns the category key provided in this <code>PolicyInfo</code>.
     * 
     * @return the category key
     */
    public String getCategoryKey() {
        return categoryKey;
    }

    /**
     * Returns an array of policy class instances provided in this
     * <code>PolicyInfo</code>.
     * 
     * @return an array of policy class instances
     */
    public Class<?>[] getPolicyClasses() {
        return policyClasses;
    }

}
