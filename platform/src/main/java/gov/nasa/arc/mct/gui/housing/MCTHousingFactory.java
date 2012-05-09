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
package gov.nasa.arc.mct.gui.housing;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.defaults.view.MCTHousingViewManifestation;
import gov.nasa.arc.mct.gui.MCTMutableTreeNode;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.gui.housing.registry.UserEnvironmentRegistry;
import gov.nasa.arc.mct.gui.util.GUIUtil;
import gov.nasa.arc.mct.registry.GlobalComponentRegistry;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.Set;

import javax.swing.JFrame;

public class MCTHousingFactory {
    public static final byte CONTROL_AREA_ENABLE = 0x1;
    public static final byte CONTENT_AREA_ENABLE = 0x2;
    public static final byte INSPECTION_AREA_ENABLE = 0x4;

    // The tree and bookmarks can be separately enabled, since only
    // top-level windows have bookmarks, per issue MODI-216.
    public static final byte DIRECTORY_AREA_TREE_ENABLE = 0x10;
    public static final byte DIRECTORY_AREA_BOOKMARKS_ENABLE = 0x20;
    public static final byte DIRECTORY_AREA_ENABLE = DIRECTORY_AREA_TREE_ENABLE | DIRECTORY_AREA_BOOKMARKS_ENABLE;

    public static final byte STATUS_AREA_ENABLE = 0x40;

    // "All" areas enabled does not normally include bookmarks, per issue
    // MODI-216.
    // Instead, only the top-level user environment includes bookmarks.
    public static final byte ENABLE_ALL_AREA = (CONTROL_AREA_ENABLE | DIRECTORY_AREA_ENABLE | CONTENT_AREA_ENABLE | INSPECTION_AREA_ENABLE | STATUS_AREA_ENABLE)
            & ~DIRECTORY_AREA_BOOKMARKS_ENABLE;

    /**
     * @return a new user environment window.
     */
    public static MCTStandardHousing newUserEnvironment() {
        AbstractComponent rootComponent = GlobalComponentRegistry.getComponent(GlobalComponentRegistry.ROOT_COMPONENT_ID);
        byte enabledAreas = MCTHousingFactory.DIRECTORY_AREA_ENABLE | MCTHousingFactory.INSPECTION_AREA_ENABLE | MCTHousingFactory.CONTROL_AREA_ENABLE
                | MCTHousingFactory.STATUS_AREA_ENABLE;
        MCTStandardHousing housing = newHousing(enabledAreas, JFrame.DO_NOTHING_ON_CLOSE, GUIUtil.cloneTreeNode(rootComponent,rootComponent.getViewInfos(ViewType.NODE)
                .iterator().next()), false, .5, .75, null);
        return housing;
    }

    public static MCTStandardHousing newHousing(byte areaSelection, int housingCloseAction, MCTMutableTreeNode top, boolean rootVisible, double hscale,
            double vscale, Window relativeWindow) {
        Dimension dimension = getWindowDimension(hscale, vscale);

        AbstractComponent housedComponent = ((View) top.getUserObject()).getManifestedComponent();
        Set<ViewInfo> vrs = housedComponent.getViewInfos(ViewType.LAYOUT);
        ViewInfo viewRole = vrs.iterator().next();
        
        MCTStandardHousing housing = new MCTStandardHousing(dimension.width, dimension.height, housingCloseAction, viewRole.createView(housedComponent));
        if (relativeWindow == null) {
            Point location = getWindowDisplayLocation(dimension);
            housing.setLocation(location);
        } else
            housing.setLocationRelativeTo(relativeWindow);

        if (isContentAreaEnabled(areaSelection)) {
            new MCTContentArea(housing, housing.getRootComponent());
        }
        if (isInspectionAreaEnabled(areaSelection)) {
            housing.setInspectionArea(getInspectorArea(housing.getRootComponent()));
        }

        /*
         * The directory area should be initialized at the very last step, since
         * it may rely on other areas to be enabled. If rootVisible, the
         * directory area sets top to be the root tree node and populates its
         * views in the inspector area. [NSHI]
         */
        if (isDirectoryAreaEnabled(areaSelection)) {
            housing.setDirectoryArea(getDirectoryArea(housing.getRootComponent(), top));
            
        }
        if (isControlAreaEnabled(areaSelection)) {
            new MCTControlArea(housing);
        }

        if (isStatusAreaEnabled(areaSelection)) {
            new MCTStatusArea(housing);
        }

        housing.buildGUI();

        UserEnvironmentRegistry.registerHousing(housing);
        return housing;
    }
    
    private static View getDirectoryArea(AbstractComponent component, MCTMutableTreeNode rootNode) {
        Set<ViewInfo> navigatorAreas = component.getViewInfos(ViewType.NAVIGATOR);
        //Set<ViewFactory> navigatorAreas = component.getViewRole(NavigatorViewRole.class);
        ViewInfo navigatorView = navigatorAreas.iterator().next();
        return navigatorView.createView(component);
        //return getMCTViewManifestation(navigatorViewRole);
    }
    
    private static View getInspectorArea(AbstractComponent component) {
        return component.getViewInfos(ViewType.RIGHT).iterator().next().createView(component);
    }
    
    /**
     * Creates a window where a root component is available.
     * 
     * Use cases: - opening the initial user environment window - opening a
     * telemetry group in its own window
     * 
     * @param housingTitle
     *            window title
     * @param areaSelection
     *            areas enabled
     * @param housingCloseAction
     *            window closing action
     * @param top
     *            the root component
     * @param rootVisible
     *            root component visibility
     * @param hscale
     *            relative to the maximum horizontal display bounds.
     * @param vscale
     *            relative to the maximum vertical display bounds.
     * @param relativeWindow
     *            relative window to be located when the window is visible
     * @return the created window
     */
    public static MCTStandardHousing newHousing(String housingTitle, byte areaSelection, int housingCloseAction, MCTMutableTreeNode top, boolean rootVisible,
            double hscale, double vscale, Window relativeWindow) {
        Dimension dimension = getWindowDimension(hscale, vscale);
        
        AbstractComponent housedComponent = ((View) top.getUserObject()).getManifestedComponent();
        Set<ViewInfo> vrs = housedComponent.getViewInfos(ViewType.LAYOUT);
        ViewInfo viewRole = vrs.iterator().next();
        
        MCTStandardHousing housing = new MCTStandardHousing(housingTitle + " - " + viewRole.getViewName(), dimension.width, dimension.height, housingCloseAction, viewRole.createView(housedComponent));

        if (relativeWindow == null) {
            Point location = getWindowDisplayLocation(dimension);
            housing.setLocation(location);
        } else {
             
            if (housing.getGraphicsConfiguration() != relativeWindow.getGraphicsConfiguration()) {
                housing = new MCTStandardHousing(relativeWindow.getGraphicsConfiguration(), 
                    housingTitle + " - " + viewRole.getViewName(), dimension.width, dimension.height, housingCloseAction, viewRole.createView(housedComponent));
            }
            
            housing.setLocationRelativeTo(relativeWindow); 
        }
        
        UserEnvironmentRegistry.registerHousing(housing);
        
        if (isContentAreaEnabled(areaSelection)) {
            new MCTContentArea(housing, housing.getRootComponent());
        }
        if (isInspectionAreaEnabled(areaSelection)) {
            housing.setInspectionArea(getInspectorArea(housing.getRootComponent()));
        }

        /*
         * The directory area should be initialized at the very last step, since
         * it may rely on other areas to be enabled. If rootVisible, the
         * directory area sets top to be the root tree node and populates its
         * views in the inspector area. [NSHI]
         */
        if (isDirectoryAreaEnabled(areaSelection)) {
            housing.setDirectoryArea(getDirectoryArea(housing.getRootComponent(), top));
        }
        if (isControlAreaEnabled(areaSelection)) {
            new MCTControlArea(housing);
        }

        if (isStatusAreaEnabled(areaSelection)) {
            new MCTStatusArea(housing);
        }

        housing.buildGUI();
 
        return housing;
    }

    /**
     * Creates a window where a root component is absent.
     * 
     * Use cases: - opening a telemetry element in its own window
     * 
     * @param housingTitle
     *            window title
     * @param areaSelection
     *            areas to be enabled
     * @param housingCloseAction
     *            window closing action
     * @param initialViewRole
     * @param hscale
     *            relative to the maximum horizontal display bounds.
     * @param vscale
     *            relative to the maximum vertical display bounds.
     * @param relativeWindow
     *            relative window to be located when the window is visible
     * @return the created window
     */
    public static MCTStandardHousing newHousing(String housingTitle, byte areaSelection, int housingCloseAction, View initialView, double hscale,
            double vscale, Window relativeWindow) {
        Dimension dimension = getWindowDimension(hscale, vscale);
        AbstractComponent housedComponent = initialView.getManifestedComponent();
        Set<ViewInfo> vrs = housedComponent.getViewInfos(ViewType.LAYOUT);
        ViewInfo viewRole = vrs.iterator().next();

        
        MCTStandardHousing housing = new MCTStandardHousing(housingTitle, dimension.width, dimension.height, housingCloseAction, viewRole.createView(housedComponent));
        
        if (relativeWindow != null) {
            if (housing.getGraphicsConfiguration() != relativeWindow.getGraphicsConfiguration()) {
                housing = new MCTStandardHousing(relativeWindow.getGraphicsConfiguration(), 
                        housingTitle, dimension.width, dimension.height, housingCloseAction, viewRole.createView(housedComponent));
            }
            
            housing.setLocationRelativeTo(relativeWindow);
        }

        UserEnvironmentRegistry.registerHousing(housing);
        
        if (isControlAreaEnabled(areaSelection)) {
            new MCTControlArea(housing);
        }
        if (isContentAreaEnabled(areaSelection)) {
            new MCTContentArea(housing, initialView);
        }
        if (isInspectionAreaEnabled(areaSelection)) {
            housing.setInspectionArea(getInspectorArea(housing.getRootComponent()));
        }
        housing.buildGUI();

        return housing;
    }

    /**
     * @param housingCloseAction
     * @param component
     *            root component of this new window
     * @param hscale
     * @param vscale
     * @param relativeWindow
     * @return a new window based on component's housing view role. The default
     *         housing view role is the first one registered in a component
     *         loader (e.g., TelemetryComponentLoader).
     */
    public static MCTStandardHousing newHousing(int housingCloseAction, AbstractComponent component, double hscale, double vscale, Window relativeWindow, byte areaSelection) {
        Dimension dimension = getWindowDimension(hscale, vscale);

        Set<ViewInfo> vrs = component.getViewInfos(ViewType.LAYOUT);
        ViewInfo viewRole = vrs.iterator().next();

        MCTStandardHousing housing = new MCTStandardHousing(component.getDisplayName() + " - " + viewRole.getViewName(), dimension.width,
                dimension.height, housingCloseAction, viewRole.createView(component));
        
        if (relativeWindow != null) {
            if (housing.getGraphicsConfiguration() != relativeWindow.getGraphicsConfiguration()) {
                housing = new MCTStandardHousing(relativeWindow.getGraphicsConfiguration(), 
                        component.getDisplayName() + " - " + viewRole.getViewName(), dimension.width, dimension.height, housingCloseAction, viewRole.createView(component));
            }
            
            housing.setLocationRelativeTo(relativeWindow);
        }
        
        UserEnvironmentRegistry.registerHousing(housing);
        
        if (isControlAreaEnabled(areaSelection)) {
            new MCTControlArea(housing);
        }
        if (isContentAreaEnabled(areaSelection)) {
            new MCTContentArea(housing, getDefaultCanvasView(housing.getRootComponent()));
        }
        if (isInspectionAreaEnabled(areaSelection)) {
            housing.setInspectionArea(getInspectorArea(housing.getRootComponent()));
        }
        housing.buildGUI();

        return housing;
    }

    private static View getDefaultCanvasView(AbstractComponent component) {
        Set<ViewInfo> viewInfos = component.getViewInfos(ViewType.CENTER);
        if (!viewInfos.isEmpty())
            return viewInfos.iterator().next().createView(component);
        
        viewInfos = component.getViewInfos(ViewType.OBJECT);
        if (!viewInfos.isEmpty())
            return viewInfos.iterator().next().createView(component);
        
        return null;
    }    
    
    public static void refreshHousing(MCTStandardHousing housing, View newHousingView) {
        // Retrieve the reference to the current housing view manifestation. 
        MCTHousingViewManifestation oldHousingManifestation = (MCTHousingViewManifestation) housing.getHousedViewManifestation(); 
        MCTHousingViewManifestation targetHousingViewManifestation = (MCTHousingViewManifestation) newHousingView;
        targetHousingViewManifestation.setSize(housing.getWidth(), housing.getHeight());
           
        MCTControlArea controlArea = housing.getControlArea();
        controlArea.setParentHousing(housing);
        targetHousingViewManifestation.setControlArea(controlArea);
        targetHousingViewManifestation.setControlAreaVisible(oldHousingManifestation.isControlAreaVisible());
        
        AbstractComponent component = newHousingView.getManifestedComponent();
        if (!component.isLeaf()) {
            View directoryArea = component.getViewInfos(ViewType.NAVIGATOR).iterator().next().createView(component);
            targetHousingViewManifestation.setDirectoryArea(directoryArea);
        }
        if (!component.getId().equals(GlobalComponentRegistry.getComponent(GlobalComponentRegistry.ROOT_COMPONENT_ID))) {
            MCTContentArea contentArea = housing.getContentArea();
            contentArea.clearHousedManifestations();
            contentArea.setParentHousing(housing);
            contentArea.setOwnerComponentCanvasManifestation(component.getViewInfos(ViewType.CENTER).iterator().next().createView(component));
            targetHousingViewManifestation.setContentArea(contentArea);
        }
        if (!component.isLeaf()) {
            View inspectionArea = component.getViewInfos(ViewType.RIGHT).iterator().next().createView(component);
            targetHousingViewManifestation.setInspectionArea(inspectionArea);
        }
        if (!component.isLeaf()) {
            MCTStatusArea statusArea = housing.getStatusArea();
            targetHousingViewManifestation.setStatusArea(statusArea);
        } 
        housing.setHousingViewManifesation(targetHousingViewManifestation);
        housing.setTitle(component.getDisplayName() + " - " + newHousingView.getInfo().getViewName());
        housing.buildGUI();
    }

    private static boolean isControlAreaEnabled(byte areaSelection) {
        return (CONTROL_AREA_ENABLE & areaSelection) != 0;
    }

    private static boolean isDirectoryAreaEnabled(byte areaSelection) {
        // We just look for any bit in DIRECTORY_AREA_ENABLE to be on,
        // either the tree or the bookmarks, or both.
        return (DIRECTORY_AREA_ENABLE & areaSelection) != 0;
    }

    private static boolean isContentAreaEnabled(byte areaSelection) {
        return (CONTENT_AREA_ENABLE & areaSelection) != 0;
    }

    private static boolean isInspectionAreaEnabled(byte areaSelection) {
        return (INSPECTION_AREA_ENABLE & areaSelection) != 0;
    }

    private static boolean isStatusAreaEnabled(byte areaSelection) {
        return (STATUS_AREA_ENABLE & areaSelection) != 0;
    }

    public static Dimension getWindowDimension(double hscale, double vscale) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle maximumWindowBounds = ge.getMaximumWindowBounds();
        int width = (int) (maximumWindowBounds.width * hscale);
        int height = (int) (maximumWindowBounds.height * vscale);
        return new Dimension(width, height);
    }

    private static Point getWindowDisplayLocation(Dimension dimension) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point centerPoint = ge.getCenterPoint();
        int x = (int) (centerPoint.x - dimension.width * .5);
        int y = (int) (centerPoint.y - dimension.height * .5);
        return new Point(x, y);
    }
}
