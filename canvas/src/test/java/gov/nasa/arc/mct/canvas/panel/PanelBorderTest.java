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
package gov.nasa.arc.mct.canvas.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PanelBorderTest {
  
    @Mock
    private Component c;
    @Mock
    private Graphics2D g1,g2;
    

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGraphicsContextNotReset() {
       PanelBorder panelBorder = new PanelBorder(Color.BLUE);
       Mockito.when(g1.create()).thenReturn(g2);
       panelBorder.paintBorder(c, g1, 0, 0, 10, 10);
       Mockito.verify(g1).create();
       Mockito.verify(g1, Mockito.times(0)).setColor((Color)Mockito.anyObject());
       Mockito.verify(g2,Mockito.atLeastOnce()).setColor(Color.BLUE);
       Mockito.verify(g2, Mockito.times(1)).dispose();
    }
    
}
