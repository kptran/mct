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
// Copyright (C) 2005 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.

/**
 * Provides definitions of interfaces and classes for component roles. There
 * are two types of roles in the MCT system:
 * 
 * <dl>
 *   <dt>Model Roles
 *   <dd>Roles defining the underlying component state, or data. These normally
 *   extend <code>AbstractModelRole</code>.
 *   
 *   <dt>View Roles
 *   <dd>Roles implementing the various visual views of the component that
 *   can be displayed in the UI. These normally extend <code>AbstractViewRole</code>
 * </dl>
 * 
 * <p>View role classes also implement one or more interfaces that indicate the
 * uses for that view within the user interface. For example, <code>InspectableViewRole</code>
 * indicates that the view can be shown in the inspector pane. Components should provide
 * a set of views that collectively provide at least one view implementing each of these
 * interfaces:</p>
 * <ul>
 *   <li><code>NodeViewRole</code> &#x2013; for displaying within the directory tree</li>
 *   <li><code>InspectableViewRole</code> &#x2013; for displaying in the inspector pane</li>
 *   <li><code>CanvasSwitchableViewRole</code> &#x2013; for displaying the canvas area</li>
 *   <li><code>HousingViewRole</code> &#x2013; for displaying the housing layout in a new window</li>
 * </ul>
 */
package gov.nasa.arc.mct.roles;
