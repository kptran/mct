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

import gov.nasa.arc.mct.canvas.formatting.ControlAreaFormattingConstants;
import gov.nasa.arc.mct.canvas.formatting.ControlAreaFormattingConstants.BorderStyle;
import gov.nasa.arc.mct.canvas.panel.Panel;
import gov.nasa.arc.mct.canvas.panel.PanelBorder;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.ExtendedProperties;
import gov.nasa.arc.mct.gui.MCTViewManifestationInfo;
import gov.nasa.arc.mct.gui.MCTViewManifestationInfoImpl;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CanvasFormattingControllerTest {
    private List<Panel> selectedPanels = new ArrayList<Panel>();
    @Mock
    private AbstractComponent mockComponent;
    @Mock
    private PanelFocusSelectionProvider panelFocusSelectionProvider;
    @Mock private ViewInfo mockIconInfo;
    @Mock private ViewInfo mockTitleInfo;
    @Mock private View mockIconView;
    @Mock private View mockTitleView;
    private ViewInfo canvasViewInfo;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        canvasViewInfo = new ViewInfo(CanvasManifestation.class, "", ViewType.OBJECT);
        Mockito.when(mockIconInfo.createView(Mockito.any(AbstractComponent.class))).thenReturn(mockIconView);
        Mockito.when(mockTitleInfo.createView(Mockito.any(AbstractComponent.class))).thenReturn(mockTitleView);
        Mockito.when(mockComponent.getViewInfos(ViewType.TITLE)).thenReturn(Collections.singleton(mockTitleInfo));
        Mockito.when(mockComponent.getDisplayName()).thenReturn("test comp");
        Mockito.when(mockComponent.getComponents()).thenReturn(
                        Collections.<AbstractComponent> emptyList());
    }

    @AfterMethod
    public void tearDown() {
        this.selectedPanels.clear();
    }

    private View addManifestInfo(View v) {
        v.putClientProperty(CanvasManifestation.MANIFEST_INFO, new MCTViewManifestationInfoImpl());
        return v;
    }
    
    @SuppressWarnings("serial")
    @Test
    public void notifyXPropertyChangeTest() {
        final ExtendedProperties viewProps = new ExtendedProperties();
        CanvasManifestation canvas1 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };

        Panel panel = new Panel(addManifestInfo(canvas1),
                        panelFocusSelectionProvider);
        int origX = panel.getBounds().x;
        CanvasFormattingController.notifyXPropertyChange(origX + 1000, panel);
        int newX = panel.getBounds().x;
        Assert.assertFalse(origX == newX);
        Assert.assertEquals(newX, origX + 1000);

        MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
        Assert.assertEquals(info.getStartPoint().x, newX);
    }

    @SuppressWarnings("serial")
    @Test
    public void notifyYPropertyChangeTest() {
        final ExtendedProperties viewProps = new ExtendedProperties();
        CanvasManifestation canvas1 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };

        Panel panel = new Panel(addManifestInfo(canvas1),
                        panelFocusSelectionProvider);
        int origY = panel.getBounds().y;
        CanvasFormattingController.notifyYPropertyChange(origY + 1000, panel);
        int newY = panel.getBounds().y;
        Assert.assertFalse(origY == newY);
        Assert.assertEquals(newY, origY + 1000);

        MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
        Assert.assertEquals(info.getStartPoint().y, newY);
    }

    @SuppressWarnings("serial")
    @Test
    public void notifyWidthPropertyChangeTest() {
        final ExtendedProperties viewProps = new ExtendedProperties();
        CanvasManifestation canvas1 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };

        TestPanel panel = new TestPanel(addManifestInfo(canvas1), panelFocusSelectionProvider);
        selectedPanels.add(panel);
        CanvasManifestation canvas2 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };

        panel = new TestPanel(addManifestInfo(canvas2), panelFocusSelectionProvider);
        selectedPanels.add(panel);

        CanvasFormattingController.notifyWidthPropertyChange(1000, selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            Rectangle newBound = panel.getBounds();
            Rectangle origBound = panel.getOrigBound();

            Assert.assertFalse(origBound.width == newBound.width);
            Assert.assertEquals(newBound.width, 1000);

            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            Assert.assertEquals(info.getDimension().width, 1000);
        }
    }

    @SuppressWarnings("serial")
    @Test
    public void notifyHeightPropertyChangeTest() {
        final ExtendedProperties viewProps = new ExtendedProperties();
        CanvasManifestation canvas1 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };
        TestPanel panel = new TestPanel(addManifestInfo(canvas1), panelFocusSelectionProvider);
        selectedPanels.add(panel);
        CanvasManifestation canvas2 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };

        panel = new TestPanel(addManifestInfo(canvas2), panelFocusSelectionProvider);
        selectedPanels.add(panel);

        CanvasFormattingController.notifyHeightPropertyChange(2000, selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            Rectangle newBound = panel.getBounds();
            Rectangle origBound = panel.getOrigBound();

            Assert.assertFalse(origBound.height == newBound.height);
            Assert.assertEquals(newBound.height, 2000);

            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            Assert.assertEquals(info.getDimension().height, 2000);
        }
    }

    @SuppressWarnings("serial")
    @Test
    public void notifyAlignLeftSelectedTest() {
        final ExtendedProperties viewProps = new ExtendedProperties();
        CanvasManifestation canvas1 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            @Override
            public ExtendedProperties getViewProperties() {
                return viewProps;
            }
        };
        TestPanel panel = new TestPanel(addManifestInfo(canvas1), panelFocusSelectionProvider);
        panel.setBounds(100, 200, 100, 100);
        selectedPanels.add(panel);
        CanvasManifestation canvas2 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            @Override
            public ExtendedProperties getViewProperties() {
                return viewProps;
            }
        };
        panel = new TestPanel(addManifestInfo(canvas2), panelFocusSelectionProvider);
        panel.setBounds(150, 200, 100, 100);
        selectedPanels.add(panel);

        CanvasFormattingController.notifyAlignLeftSelected(selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            Rectangle newBound = panel.getBounds();

            Assert.assertEquals(newBound.x, 100);

            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            Assert.assertEquals(info.getStartPoint().x, 100);
        }
    }

    @SuppressWarnings("serial")
    @Test
    public void notifyAlignCenterHSelectedTest() {
        final ExtendedProperties viewProps = new ExtendedProperties();
        CanvasManifestation canvas1 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };
        TestPanel panel = new TestPanel(addManifestInfo(canvas1), panelFocusSelectionProvider);
        panel.setBounds(100, 200, 100, 100);
        selectedPanels.add(panel);
        CanvasManifestation canvas2 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };
        panel = new TestPanel(addManifestInfo(canvas2), panelFocusSelectionProvider);
        panel.setBounds(150, 100, 100, 100);
        selectedPanels.add(panel);

        CanvasFormattingController.notifyAlignCenterHSelected(selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            Rectangle newBound = panel.getBounds();

            Assert.assertEquals(newBound.x, 125);

            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            Assert.assertEquals(info.getStartPoint().x, 125);
        }
    }

    @SuppressWarnings("serial")
    @Test
    public void notifyAlignRightSelectedTest() {
        final ExtendedProperties viewProps = new ExtendedProperties();
        CanvasManifestation canvas1 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };
        TestPanel panel = new TestPanel(addManifestInfo(canvas1), panelFocusSelectionProvider);
        panel.setBounds(100, 200, 100, 100);
        selectedPanels.add(panel);
        CanvasManifestation canvas2 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };        
        panel = new TestPanel(addManifestInfo(canvas2), panelFocusSelectionProvider);
        panel.setBounds(100, 100, 50, 100);
        selectedPanels.add(panel);

        CanvasFormattingController.notifyAlignRightSelected(selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            Rectangle newBound = panel.getBounds();

            Assert.assertEquals(newBound.x + newBound.width, 200);

            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            Assert.assertEquals(info.getStartPoint().x + info.getDimension().width, 200);
        }
        
        // try a single panel
        CanvasManifestation canvas3 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };        
        panel = new TestPanel(addManifestInfo(canvas3), panelFocusSelectionProvider);
        panel.setBounds(100, 200, 100, 100);
        Rectangle orig = panel.getBounds();
        selectedPanels.clear();
        selectedPanels.add(panel);
        CanvasFormattingController.notifyAlignRightSelected(selectedPanels);
        Assert.assertEquals(panel.getBounds(), orig);
    }

    @SuppressWarnings("serial")
    @Test
    public void notifyAlignTopSelectedTest() {
        final ExtendedProperties viewProps = new ExtendedProperties();
        CanvasManifestation canvas1 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };
        TestPanel panel = new TestPanel(addManifestInfo(canvas1), panelFocusSelectionProvider);
        panel.setBounds(100, 200, 100, 100);
        selectedPanels.add(panel);
        CanvasManifestation canvas2 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };
        panel = new TestPanel(addManifestInfo(canvas2), panelFocusSelectionProvider);
        panel.setBounds(100, 100, 50, 100);
        selectedPanels.add(panel);

        CanvasFormattingController.notifyAlignTopSelected(selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            Rectangle newBound = panel.getBounds();

            Assert.assertEquals(newBound.y, 100);

            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            Assert.assertEquals(info.getStartPoint().y, 100);
        }
    }

    @SuppressWarnings("serial")
    @Test
    public void notifyAlignBottomSelectedTest() {
        final ExtendedProperties viewProps = new ExtendedProperties();
        CanvasManifestation canvasManifestation1 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            @Override
            public ExtendedProperties getViewProperties() {
                return viewProps;
            }
        };
        TestPanel panel = new TestPanel(addManifestInfo(canvasManifestation1), panelFocusSelectionProvider);
        panel.setBounds(100, 200, 100, 100);
        selectedPanels.add(panel);
        CanvasManifestation canvasManifestation2 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            @Override
            public ExtendedProperties getViewProperties() {
                return viewProps;
            }            
        };
        panel = new TestPanel(addManifestInfo(canvasManifestation2), panelFocusSelectionProvider);
        panel.setBounds(100, 100, 50, 100);
        selectedPanels.add(panel);

        CanvasFormattingController.notifyAlignBottomSelected(selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            Rectangle newBound = panel.getBounds();

            Assert.assertEquals(newBound.y + newBound.height, 300);

            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            Assert.assertEquals(info.getStartPoint().y + info.getDimension().height, 300);
        }
        
        // try a single panel
        selectedPanels.clear();
        panel = new TestPanel(addManifestInfo(canvasManifestation2), panelFocusSelectionProvider);
        panel.setBounds(100, 200, 100, 100);
        Rectangle orig = panel.getBounds();
        selectedPanels.add(panel);
        CanvasFormattingController.notifyAlignBottomSelected(selectedPanels);
        Assert.assertEquals(panel.getBounds(), orig);
        
    }

    @SuppressWarnings("serial")
    @Test
    public void notifyAlignVCenterSelected() {
        final ExtendedProperties viewProps = new ExtendedProperties();
        CanvasManifestation canvas1 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };
        TestPanel panel = new TestPanel(addManifestInfo(canvas1), panelFocusSelectionProvider);
        panel.setBounds(100, 200, 100, 100);
        selectedPanels.add(panel);
        CanvasManifestation canvas2 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };        
        panel = new TestPanel(addManifestInfo(canvas2),
                        panelFocusSelectionProvider);
        panel.setBounds(100, 100, 50, 50);
        selectedPanels.add(panel);

        CanvasFormattingController.notifyAlignVCenterSelected(selectedPanels);

        panel = (TestPanel) selectedPanels.get(0);
        Rectangle newBound = panel.getBounds();

        Assert.assertEquals(newBound.y, 150);

        MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
        Assert.assertEquals(info.getStartPoint().y, 150);

        panel = (TestPanel) selectedPanels.get(1);
        newBound = panel.getBounds();

        Assert.assertEquals(newBound.y, 175);

        info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
        Assert.assertEquals(info.getStartPoint().y, 175);

    }
    
    @SuppressWarnings("serial")
    @Test
    public void notifyWestBorderStatusTest() {
        final ExtendedProperties viewProps = new ExtendedProperties();
        CanvasManifestation canvas1 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };

        TestPanel panel = new TestPanel(addManifestInfo(canvas1), panelFocusSelectionProvider);
        panel.setBounds(100, 200, 100, 100);
        selectedPanels.add(panel);
        CanvasManifestation canvas2 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };

        panel = new TestPanel(addManifestInfo(canvas2), panelFocusSelectionProvider);
        panel.setBounds(100, 100, 50, 100);
        selectedPanels.add(panel);

        CanvasFormattingController.notifyWestBorderStatus(true, selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            
            byte panelBorder = panel.getBorderState();
            Assert.assertTrue(PanelBorder.hasWestBorder(panelBorder));
            
            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            String panelBorderStr = info.getInfoProperty(ControlAreaFormattingConstants.PANEL_BORDER_PROPERTY);
            Assert.assertTrue(PanelBorder.hasWestBorder(Byte.parseByte(panelBorderStr)));
        }
        
        CanvasFormattingController.notifyWestBorderStatus(false, selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            
            byte panelBorder = panel.getBorderState();
            Assert.assertFalse(PanelBorder.hasWestBorder(panelBorder));
            
            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            String panelBorderStr = info.getInfoProperty(ControlAreaFormattingConstants.PANEL_BORDER_PROPERTY);
            Assert.assertFalse(PanelBorder.hasWestBorder(Byte.parseByte(panelBorderStr)));
        }
    }
    
    @SuppressWarnings("serial")
    @Test
    public void notifyEastBorderStatusTest() {
        final ExtendedProperties viewProps = new ExtendedProperties();
        CanvasManifestation canvas1 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };
        TestPanel panel = new TestPanel(addManifestInfo(canvas1), panelFocusSelectionProvider);
        panel.setBounds(100, 200, 100, 100);
        selectedPanels.add(panel);
        CanvasManifestation canvas2 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };
        panel = new TestPanel(addManifestInfo(canvas2), panelFocusSelectionProvider);
        panel.setBounds(100, 100, 50, 100);
        selectedPanels.add(panel);

        CanvasFormattingController.notifyEastBorderStatus(true, selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            
            byte panelBorder = panel.getBorderState();
            Assert.assertTrue(PanelBorder.hasEastBorder(panelBorder));
            
            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            String panelBorderStr = info.getInfoProperty(ControlAreaFormattingConstants.PANEL_BORDER_PROPERTY);
            Assert.assertTrue(PanelBorder.hasEastBorder(Byte.parseByte(panelBorderStr)));
        }
        
        CanvasFormattingController.notifyEastBorderStatus(false, selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            
            byte panelBorder = panel.getBorderState();
            Assert.assertFalse(PanelBorder.hasEastBorder(panelBorder));
            
            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            String panelBorderStr = info.getInfoProperty(ControlAreaFormattingConstants.PANEL_BORDER_PROPERTY);
            Assert.assertFalse(PanelBorder.hasEastBorder(Byte.parseByte(panelBorderStr)));
        }
    }
    
    @SuppressWarnings("serial")
    @Test
    public void notifyNorthBorderStatusTest() {
        final ExtendedProperties viewProps = new ExtendedProperties();
        CanvasManifestation canvas1 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };
        TestPanel panel = new TestPanel(addManifestInfo(canvas1), panelFocusSelectionProvider);
        panel.setBounds(100, 200, 100, 100);
        selectedPanels.add(panel);
        CanvasManifestation canvas2 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };

        panel = new TestPanel(addManifestInfo(canvas2),
                        panelFocusSelectionProvider);
        panel.setBounds(100, 100, 50, 100);
        selectedPanels.add(panel);

        CanvasFormattingController.notifyNorthBorderStatus(true, selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            
            byte panelBorder = panel.getBorderState();
            Assert.assertTrue(PanelBorder.hasNorthBorder(panelBorder));
            
            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            String panelBorderStr = info.getInfoProperty(ControlAreaFormattingConstants.PANEL_BORDER_PROPERTY);
            Assert.assertTrue(PanelBorder.hasNorthBorder(Byte.parseByte(panelBorderStr)));
        }
        
        CanvasFormattingController.notifyNorthBorderStatus(false, selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            
            byte panelBorder = panel.getBorderState();
            Assert.assertFalse(PanelBorder.hasNorthBorder(panelBorder));
            
            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            String panelBorderStr = info.getInfoProperty(ControlAreaFormattingConstants.PANEL_BORDER_PROPERTY);
            Assert.assertFalse(PanelBorder.hasNorthBorder(Byte.parseByte(panelBorderStr)));
        }
    }
    
    @SuppressWarnings("serial")
    @Test
    public void notifySouthBorderStatusTest() {
        final ExtendedProperties viewProps = new ExtendedProperties();
        CanvasManifestation canvas1 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };

        TestPanel panel = new TestPanel(addManifestInfo(canvas1), panelFocusSelectionProvider);
        panel.setBounds(100, 200, 100, 100);
        selectedPanels.add(panel);
        CanvasManifestation canvas2 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };

        panel = new TestPanel(addManifestInfo(canvas2), panelFocusSelectionProvider);
        panel.setBounds(100, 100, 50, 100);
        selectedPanels.add(panel);

        CanvasFormattingController.notifySouthBorderStatus(true, selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            
            byte panelBorder = panel.getBorderState();
            Assert.assertTrue(PanelBorder.hasSouthBorder(panelBorder));
            
            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            String panelBorderStr = info.getInfoProperty(ControlAreaFormattingConstants.PANEL_BORDER_PROPERTY);
            Assert.assertTrue(PanelBorder.hasSouthBorder(Byte.parseByte(panelBorderStr)));
        }
        
        CanvasFormattingController.notifySouthBorderStatus(false, selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            
            byte panelBorder = panel.getBorderState();
            Assert.assertFalse(PanelBorder.hasSouthBorder(panelBorder));
            
            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            String panelBorderStr = info.getInfoProperty(ControlAreaFormattingConstants.PANEL_BORDER_PROPERTY);
            Assert.assertFalse(PanelBorder.hasSouthBorder(Byte.parseByte(panelBorderStr)));
        }
    }
    
    @SuppressWarnings("serial")
    @Test
    public void notifyAllBorderStatusTest() {
        final ExtendedProperties viewProps = new ExtendedProperties();
        CanvasManifestation canvas1 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };
        TestPanel panel = new TestPanel(addManifestInfo(canvas1), panelFocusSelectionProvider);
        panel.setBounds(100, 200, 100, 100);
        selectedPanels.add(panel);
        CanvasManifestation canvas2 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };
        panel = new TestPanel(addManifestInfo(canvas2),
                        panelFocusSelectionProvider);
        panel.setBounds(100, 100, 50, 100);
        selectedPanels.add(panel);

        CanvasFormattingController.notifyAllBorderStatus(true, selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            
            byte panelBorder = panel.getBorderState();
            Assert.assertEquals(panelBorder, PanelBorder.ALL_BORDERS);
            
            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            String panelBorderStr = info.getInfoProperty(ControlAreaFormattingConstants.PANEL_BORDER_PROPERTY);
            Assert.assertEquals(Byte.parseByte(panelBorderStr), PanelBorder.ALL_BORDERS);
        }
        
        CanvasFormattingController.notifyAllBorderStatus(false, selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            
            byte panelBorder = panel.getBorderState();
            Assert.assertEquals(panelBorder, PanelBorder.NO_BORDER);
            
            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            String panelBorderStr = info.getInfoProperty(ControlAreaFormattingConstants.PANEL_BORDER_PROPERTY);
            Assert.assertEquals(Byte.parseByte(panelBorderStr), PanelBorder.NO_BORDER);
        }
    }
    
    @SuppressWarnings("serial")
    @Test
    public void notifyBorderColorSelectedTest() {
        final ExtendedProperties viewProps = new ExtendedProperties();
        CanvasManifestation canvas1 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };
        TestPanel panel = new TestPanel(addManifestInfo(canvas1), panelFocusSelectionProvider);
        panel.setBounds(100, 200, 100, 100);
        selectedPanels.add(panel);
        CanvasManifestation canvas2 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };
        panel = new TestPanel(addManifestInfo(canvas2), panelFocusSelectionProvider);
        panel.setBounds(100, 100, 50, 100);
        selectedPanels.add(panel);

        CanvasFormattingController.notifyBorderColorSelected(Color.red, selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            
            Color borderColor = panel.getBorderColor();
            Assert.assertEquals(borderColor, Color.red);
            
            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            Assert.assertEquals(info.getBorderColor(), Color.red);
        }
    }
    
    @SuppressWarnings("serial")
    @Test
    public void notifyBorderFormattingStyleTest() {
        final ExtendedProperties viewProps = new ExtendedProperties();
        CanvasManifestation canvas1 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };
        TestPanel panel = new TestPanel(addManifestInfo(canvas1), panelFocusSelectionProvider);
        panel.setBounds(100, 200, 100, 100);
        selectedPanels.add(panel);
        CanvasManifestation canvas2 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };
        panel = new TestPanel(addManifestInfo(canvas2),
                        panelFocusSelectionProvider);
        panel.setBounds(100, 100, 50, 100);
        selectedPanels.add(panel);

        CanvasFormattingController.notifyBorderFormattingStyle(BorderStyle.MIXED.ordinal(), selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            
            int borderStyle = panel.getBorderStyle();
            Assert.assertEquals(borderStyle, BorderStyle.MIXED.ordinal());
            
            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            Assert.assertEquals(info.getBorderStyle(), BorderStyle.MIXED.ordinal());
        }
    }
    
    @SuppressWarnings("serial")
    @Test
    public void notifyTitleBarStatusTest() {
        final ExtendedProperties viewProps = new ExtendedProperties();
        CanvasManifestation canvas1 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };

        TestPanel panel = new TestPanel(addManifestInfo(canvas1), panelFocusSelectionProvider);
        panel.setBounds(100, 200, 100, 100);
        selectedPanels.add(panel);
        CanvasManifestation canvas2 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };

        panel = new TestPanel(addManifestInfo(canvas2),
                        panelFocusSelectionProvider);
        panel.setBounds(100, 100, 50, 100);
        selectedPanels.add(panel);

        CanvasFormattingController.notifyTitleBarStatus(true, selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            
            Assert.assertTrue(panel.hasTitle());
            
            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            Assert.assertTrue(info.hasTitlePanel());
        }
        
        CanvasFormattingController.notifyTitleBarStatus(false, selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            
            Assert.assertFalse(panel.hasTitle());
            
            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            Assert.assertFalse(info.hasTitlePanel());
        }
    }
    
    @SuppressWarnings("serial")
    @Test
    public void notifyNewTitleTest() {
        final ExtendedProperties viewProps = new ExtendedProperties();
        CanvasManifestation canvas1 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };

        TestPanel panel = new TestPanel(addManifestInfo(canvas1), panelFocusSelectionProvider);
        panel.setBounds(100, 200, 100, 100);
        selectedPanels.add(panel);
        CanvasManifestation canvas2 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };

        panel = new TestPanel(addManifestInfo(canvas2),
                        panelFocusSelectionProvider);
        panel.setBounds(100, 100, 50, 100);
        selectedPanels.add(panel);

        CanvasFormattingController.notifyNewTitle("new Title", selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            Assert.assertEquals(panel.getTitle(), "new Title");
            
            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            Assert.assertEquals(info.getPanelTitle(), "new Title");
        }
    }
    
    @SuppressWarnings("serial")
    @Test
    public void notifyNewTitleFontTest() {
        final ExtendedProperties viewProps = new ExtendedProperties();
        CanvasManifestation canvas1 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };

        TestPanel panel = new TestPanel(addManifestInfo(canvas1), panelFocusSelectionProvider);
        panel.setBounds(100, 200, 100, 100);
        selectedPanels.add(panel);
        CanvasManifestation canvas2 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };

        panel = new TestPanel(addManifestInfo(canvas2),
                        panelFocusSelectionProvider);
        panel.setBounds(100, 100, 50, 100);
        selectedPanels.add(panel);

        CanvasFormattingController.notifyTitleBarFontSelected("Monospaced", selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            Assert.assertEquals(panel.getTitleFont(), "Monospaced");
            
            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            Assert.assertEquals(info.getPanelTitleFont(), "Monospaced");
        }
    }
    
    @SuppressWarnings("serial")
    @Test
    public void notifyNewTitleFontSizeTest() {
        final ExtendedProperties viewProps = new ExtendedProperties();
        CanvasManifestation canvas1 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };

        TestPanel panel = new TestPanel(addManifestInfo(canvas1), panelFocusSelectionProvider);
        panel.setBounds(100, 200, 100, 100);
        selectedPanels.add(panel);
        CanvasManifestation canvas2 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };

        panel = new TestPanel(addManifestInfo(canvas2),
                        panelFocusSelectionProvider);
        panel.setBounds(100, 100, 50, 100);
        selectedPanels.add(panel);

        CanvasFormattingController.notifyTitleBarFontSizeSelected(20, selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            Assert.assertEquals(panel.getTitleFontSize().intValue(), 20);
            
            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            Assert.assertEquals(info.getPanelTitleFontSize().intValue(), 20);
        }
    }
    
    @SuppressWarnings("serial")
    @Test
    public void notifyNewTitleFontStyleTest() {
        final ExtendedProperties viewProps = new ExtendedProperties();
        CanvasManifestation canvas1 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };

        TestPanel panel = new TestPanel(addManifestInfo(canvas1), panelFocusSelectionProvider);
        panel.setBounds(100, 200, 100, 100);
        selectedPanels.add(panel);
        CanvasManifestation canvas2 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };

        panel = new TestPanel(addManifestInfo(canvas2),
                        panelFocusSelectionProvider);
        panel.setBounds(100, 100, 50, 100);
        selectedPanels.add(panel);

        CanvasFormattingController.notifyTitleBarFontStyleSelected(Font.BOLD, selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            Assert.assertEquals(panel.getTitleFontStyle().intValue(), Font.BOLD);
            
            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            Assert.assertEquals(info.getPanelTitleFontStyle().intValue(), Font.BOLD);
        }
        CanvasFormattingController.notifyTitleBarFontStyleSelected(Font.ITALIC, selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            Assert.assertEquals(panel.getTitleFontStyle().intValue(), Font.ITALIC);
            
            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            Assert.assertEquals(info.getPanelTitleFontStyle().intValue(), Font.ITALIC);
        }
        
        CanvasFormattingController.notifyTitleBarFontStyleSelected(Font.BOLD + Font.ITALIC, selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            Assert.assertEquals(panel.getTitleFontStyle().intValue(), Font.BOLD+ Font.ITALIC);
            
            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            Assert.assertEquals(info.getPanelTitleFontStyle().intValue(), Font.BOLD+ Font.ITALIC);
        }
    }
    
    @SuppressWarnings("serial")
    @Test
    public void notifyNewTitleFontUnderlineTest() {
        final ExtendedProperties viewProps = new ExtendedProperties();
        CanvasManifestation canvas1 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };

        TestPanel panel = new TestPanel(addManifestInfo(canvas1), panelFocusSelectionProvider);
        panel.setBounds(100, 200, 100, 100);
        selectedPanels.add(panel);
        CanvasManifestation canvas2 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };

        panel = new TestPanel(addManifestInfo(canvas2),
                        panelFocusSelectionProvider);
        panel.setBounds(100, 100, 50, 100);
        selectedPanels.add(panel);

        CanvasFormattingController.notifyTitleBarFontUnderlineSelected(TextAttribute.UNDERLINE_ON, selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            Assert.assertEquals(panel.getTitleFontUnderline().intValue(), TextAttribute.UNDERLINE_ON.intValue());
            
            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            Assert.assertEquals(info.getPanelTitleFontUnderline().intValue(), TextAttribute.UNDERLINE_ON.intValue());
        }
        
        CanvasFormattingController.notifyTitleBarFontUnderlineSelected(ControlAreaFormattingConstants.UNDERLINE_OFF, selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            Assert.assertEquals(panel.getTitleFontUnderline().intValue(), ControlAreaFormattingConstants.UNDERLINE_OFF);
            
            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            Assert.assertEquals(info.getPanelTitleFontUnderline().intValue(), ControlAreaFormattingConstants.UNDERLINE_OFF);
        }
    }
    
    @SuppressWarnings("serial")
    @Test
    public void notifyNewTitleFontForegroundColorTest() {
        final ExtendedProperties viewProps = new ExtendedProperties();
        CanvasManifestation canvas1 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };

        TestPanel panel = new TestPanel(addManifestInfo(canvas1), panelFocusSelectionProvider);
        panel.setBounds(100, 200, 100, 100);
        selectedPanels.add(panel);
        CanvasManifestation canvas2 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };

        panel = new TestPanel(addManifestInfo(canvas2),
                        panelFocusSelectionProvider);
        panel.setBounds(100, 100, 50, 100);
        selectedPanels.add(panel);

        CanvasFormattingController.notifyTitleBarFontForegroundColorSelected(Color.decode("#0000FF").getRGB(), selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            Assert.assertEquals(panel.getTitleFontForegroundColor().intValue(), Color.decode("#0000FF").getRGB());
            
            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            Assert.assertEquals(info.getPanelTitleFontForegroundColor().intValue(), Color.decode("#0000FF").getRGB());
        }
    }
    
    @SuppressWarnings("serial")
    @Test
    public void notifyNewTitleFontBackgroundColorTest() {
        final ExtendedProperties viewProps = new ExtendedProperties();
        CanvasManifestation canvas1 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };

        TestPanel panel = new TestPanel(addManifestInfo(canvas1), panelFocusSelectionProvider);
        panel.setBounds(100, 200, 100, 100);
        selectedPanels.add(panel);
        CanvasManifestation canvas2 = new CanvasManifestation(mockComponent,canvasViewInfo) {
            public ExtendedProperties getViewProperties() {
                return viewProps;
            };
        };

        panel = new TestPanel(addManifestInfo(canvas2),
                        panelFocusSelectionProvider);
        panel.setBounds(100, 100, 50, 100);
        selectedPanels.add(panel);

        CanvasFormattingController.notifyTitleBarFontBackgroundColorSelected(Color.decode("#0000FF").getRGB(), selectedPanels);

        for (Panel p : selectedPanels) {
            panel = (TestPanel) p;
            Assert.assertEquals(panel.getTitleFontBackgroundColor().intValue(), Color.decode("#0000FF").getRGB());
            
            MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(panel.getWrappedManifestation());
            Assert.assertEquals(info.getPanelTitleFontBackgroundColor().intValue(), Color.decode("#0000FF").getRGB());
        }
    }


    private static final class TestPanel extends Panel {
        private static final long serialVersionUID = 1L;

        private Rectangle origBound;

        public TestPanel(View manifestation,
                        PanelFocusSelectionProvider panelSelectionProvider) {
            super(manifestation, panelSelectionProvider);
            this.origBound = getBounds();
        }

        public Rectangle getOrigBound() {
            return this.origBound;
        }
    }
}
