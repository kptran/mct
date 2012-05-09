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
package gov.nasa.arc.mct.services.component;

/**
 * Implements an OSGi service that is used to define
 * Module 1-style plugin components. New instances of
 * this service are created to define a new plugin
 * component type. However, there are no methods
 * defined by this service. Instead, it only services
 * to hold service properties, when registered with
 * the OSGi service registry.
 * 
 * <p>Constants within the class define service property
 * names used to store and retrieve attributes of the
 * component type being registered.
 */
public class ComponentType {

    /** A service property indicating the unique ID of the new component type. */
    public static String COMPONENT_ID = "mct.component.id";
    /** A service property indicating the display name of the new component type. */
	public static String NAME = "mct.component.name";
	/** A service property indicating the service PID of the model class. */
	public static String MODEL_ID = "mct.model.id";
	/** A service property indicating an array of service PIDs of the view role classes. */
	public static String VIEW_IDS = "mct.view.ids";

}
