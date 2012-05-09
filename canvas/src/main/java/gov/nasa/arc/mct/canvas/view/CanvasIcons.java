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
package gov.nasa.arc.mct.canvas.view;

import javax.swing.ImageIcon;

public class CanvasIcons {
    private static ImageIcon jlsAlignTableLeftImage = null;
    private static ImageIcon jlsAlignTableHCenterImage = null;
    private static ImageIcon jlsAlignTableRightImage = null;
    private static ImageIcon jlsAlignTableBottomImage = null;
    private static ImageIcon jlsAlignTableVCenterImage = null;
    private static ImageIcon jlsAlignTableTopImage = null;
    
    private static ImageIcon jlsLeftBorderSelectedImage =  null;
    private static ImageIcon jlsRightBorderSelectedImage = null;
    private static ImageIcon jlsTopBorderSelectedImage = null;
    private static ImageIcon jlsBottomBorderSelectedImage = null;
    private static ImageIcon jlsAllBorderSelectedImage = null;
    private static ImageIcon jlsNoBorderSelectedImage = null;
    
    private static ImageIcon jlsLeftBorderImage = null;
    private static ImageIcon jlsRightBorderImage = null;
    private static ImageIcon jlsTopBorderImage = null;
    private static ImageIcon jlsBottomBorderImage = null;
    private static ImageIcon jlsAllBorderImage = null;
    private static ImageIcon jlsNoBorderImage = null;
    
    private static ImageIcon panelWidthImage = null;
    private static ImageIcon panelHeightImage = null;
     
    public static enum Icons { 
                
        JLS_LEFT_BORDER_SELECTED_ICON,
        JLS_RIGHT_BORDER_SELECTED_ICON,
        JLS_TOP_BORDER_SELECTED_ICON,
        JLS_BOTTOM_BORDER_SELECTED_ICON,
        JLS_ALL_BORDER_SELECTED_ICON,
        JLS_NO_BORDER_SELECTED_ICON,
        
        JLS_LEFT_BORDER_ICON,
        JLS_RIGHT_BORDER_ICON,
        JLS_TOP_BORDER_ICON,
        JLS_BOTTOM_BORDER_ICON,
        JLS_ALL_BORDER_ICON,
        JLS_NO_BORDER_ICON,
        
        JLS_ALIGN_TABLE_LEFT_ICON,
        JLS_ALIGN_TABLE_HCENTER_ICON,
        JLS_ALIGN_TABLE_RIGHT_ICON,
        JLS_ALIGN_TABLE_BOTTOM_ICON,
        JLS_ALIGN_TABLE_VCENTER_ICON,
        JLS_ALIGN_TABLE_TOP_ICON,
        
        PANEL_WIDTH_ICON,
        PANEL_HEIGHT_ICON,
    };
    
    public static ImageIcon getIcon(Icons anIconEnum) {
        switch(anIconEnum) {
       case JLS_LEFT_BORDER_SELECTED_ICON:
            if (jlsLeftBorderSelectedImage == null)
                jlsLeftBorderSelectedImage = new ImageIcon(CanvasIcons.class.getClassLoader().getResource("images/LeftBorder_on.png"));
            return jlsLeftBorderSelectedImage;
            
        case JLS_RIGHT_BORDER_SELECTED_ICON:
            if (jlsRightBorderSelectedImage == null)
                jlsRightBorderSelectedImage = new ImageIcon(CanvasIcons.class.getClassLoader().getResource("images/RightBorder_on.png"));
            return jlsRightBorderSelectedImage;
            
        case JLS_TOP_BORDER_SELECTED_ICON:
            if (jlsTopBorderSelectedImage == null)
                jlsTopBorderSelectedImage = new ImageIcon(CanvasIcons.class.getClassLoader().getResource("images/TopBorder_on.png"));
            return jlsTopBorderSelectedImage;
            
        case JLS_BOTTOM_BORDER_SELECTED_ICON:
            if (jlsBottomBorderSelectedImage == null)
                jlsBottomBorderSelectedImage = new ImageIcon(CanvasIcons.class.getClassLoader().getResource("images/BottomBorder_on.png"));
            return jlsBottomBorderSelectedImage;
            
        case JLS_ALL_BORDER_SELECTED_ICON:
            if (jlsAllBorderSelectedImage == null)
                jlsAllBorderSelectedImage = new ImageIcon(CanvasIcons.class.getClassLoader().getResource("images/AllBorder_on.png"));
            return jlsAllBorderSelectedImage;
            
        case JLS_NO_BORDER_SELECTED_ICON:
            if (jlsNoBorderSelectedImage == null)
                jlsNoBorderSelectedImage = new ImageIcon(CanvasIcons.class.getClassLoader().getResource("images/NoBorder_on.png"));
            return jlsNoBorderSelectedImage;
                  
        case JLS_LEFT_BORDER_ICON:
            if (jlsLeftBorderImage == null) 
                jlsLeftBorderImage = new ImageIcon(CanvasIcons.class.getClassLoader().getResource("images/LeftBorder_off.png"));
            return jlsLeftBorderImage;
            
        case JLS_RIGHT_BORDER_ICON:
            if (jlsRightBorderImage == null)
                jlsRightBorderImage = new ImageIcon(CanvasIcons.class.getClassLoader().getResource("images/RightBorder_off.png"));
            return jlsRightBorderImage;
            
        case JLS_TOP_BORDER_ICON:
            if (jlsTopBorderImage == null)
                jlsTopBorderImage = new ImageIcon(CanvasIcons.class.getClassLoader().getResource("images/TopBorder_off.png"));
            return jlsTopBorderImage;
            
        case JLS_BOTTOM_BORDER_ICON:
            if (jlsBottomBorderImage == null)
                jlsBottomBorderImage = new ImageIcon(CanvasIcons.class.getClassLoader().getResource("images/BottomBorder_off.png"));
            return jlsBottomBorderImage;
            
        case JLS_ALL_BORDER_ICON:
            if (jlsAllBorderImage == null)
                jlsAllBorderImage = new ImageIcon(CanvasIcons.class.getClassLoader().getResource("images/AllBorder_off.png"));
            return jlsAllBorderImage;

        case JLS_NO_BORDER_ICON:
            if (jlsNoBorderImage == null)
                jlsNoBorderImage = new ImageIcon(CanvasIcons.class.getClassLoader().getResource("images/NoBorder_off.png"));
            return jlsNoBorderImage; 
            
        case JLS_ALIGN_TABLE_LEFT_ICON:
            if (jlsAlignTableLeftImage == null)
                jlsAlignTableLeftImage = new ImageIcon(CanvasIcons.class.getClassLoader().getResource("images/TableAlignLeft_off.png"));
            return jlsAlignTableLeftImage;
            
        case JLS_ALIGN_TABLE_HCENTER_ICON:
            if (jlsAlignTableHCenterImage == null)
                jlsAlignTableHCenterImage = new ImageIcon(CanvasIcons.class.getClassLoader().getResource("images/TableAlignCenter_off.png"));
            return jlsAlignTableHCenterImage;
            
        case JLS_ALIGN_TABLE_RIGHT_ICON:
              if (jlsAlignTableRightImage == null)
                    jlsAlignTableRightImage = new ImageIcon(CanvasIcons.class.getClassLoader().getResource("images/TableAlignRight_off.png"));
                return jlsAlignTableRightImage;
            
        case JLS_ALIGN_TABLE_BOTTOM_ICON:
            if (jlsAlignTableBottomImage == null)
                jlsAlignTableBottomImage = new ImageIcon(CanvasIcons.class.getClassLoader().getResource("images/TableAlignBottom_off.png"));
            return jlsAlignTableBottomImage;
            
        case JLS_ALIGN_TABLE_VCENTER_ICON:
            if (jlsAlignTableVCenterImage == null)
                jlsAlignTableVCenterImage = new ImageIcon(CanvasIcons.class.getClassLoader().getResource("images/TableAlignVerticalCenter_off.png"));
            return jlsAlignTableVCenterImage;
            
        case JLS_ALIGN_TABLE_TOP_ICON:
            if (jlsAlignTableTopImage == null)
                jlsAlignTableTopImage = new ImageIcon(CanvasIcons.class.getClassLoader().getResource("images/TableAlignTop_off.png"));
            return jlsAlignTableTopImage;    

        case PANEL_WIDTH_ICON:
            if (panelWidthImage == null)
                panelWidthImage = new ImageIcon(CanvasIcons.class.getClassLoader().getResource("images/button_panelWidth_on.gif"));
            return panelWidthImage;
            
        case PANEL_HEIGHT_ICON:
            if (panelHeightImage == null)
                panelHeightImage = new ImageIcon(CanvasIcons.class.getClassLoader().getResource("images/button_panelHeight_on.gif"));
            return panelHeightImage;
            
        default:
            return null;
        }
        
    }


    private CanvasIcons() {
        // no instantiation 
    }

}

