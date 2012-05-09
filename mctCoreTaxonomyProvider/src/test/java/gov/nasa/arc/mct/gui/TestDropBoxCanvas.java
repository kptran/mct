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
package gov.nasa.arc.mct.gui;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.core.roles.DropboxCanvasView;
import gov.nasa.arc.mct.platform.core.access.PlatformAccess;
import gov.nasa.arc.mct.platform.spi.Platform;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.services.component.PolicyManager;

import java.awt.Component;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.TransferHandler;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestDropBoxCanvas {
    private DropboxCanvasView canvasView;
    
    private AbstractComponent testComponent = new AbstractComponent() {};
    
    private AbstractComponent masterComponent = new AbstractComponent() {};
    private AbstractComponent nonMasterComponent = new AbstractComponent() {

        @Override
        public AbstractComponent getMasterComponent() {
            return masterComponent;
        }

        @Override
        public boolean isVersionedComponent() {
            return true;
        }
        
    };
    
    @Mock TransferHandler mockTransferHandler;
    @Mock DropTargetDropEvent mockDropEvent;
    
    Platform oldPlatform;

    @BeforeClass
    public void setupPlatform() {
        
        oldPlatform = PlatformAccess.getPlatform();
        
        ExecutionResult trueResult = new ExecutionResult(null, true, null);
        PolicyManager mockPolicyManager = Mockito.mock(PolicyManager.class);
        Mockito.when(mockPolicyManager.execute(Mockito.anyString(), Mockito.<PolicyContext>any())).thenReturn(trueResult);
        Platform mockPlatform = Mockito.mock(Platform.class);
        Mockito.when(mockPlatform.getPolicyManager()).thenReturn(mockPolicyManager);
        
        new PlatformAccess().setPlatform(mockPlatform);
        new gov.nasa.arc.mct.platform.spi.PlatformAccess().setPlatform(mockPlatform);
    }
    
    @AfterClass
    public void restorePlatform() {
        new PlatformAccess().setPlatform(oldPlatform);
        new gov.nasa.arc.mct.platform.spi.PlatformAccess().setPlatform(oldPlatform);
    }
    
    @BeforeTest
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        canvasView = new DropboxCanvasView(testComponent, null);
        JFrame f = new JFrame();
        masterComponent.setId("master");
        nonMasterComponent.setId("nonmaster");
        f.getContentPane().add(canvasView);
    }
    
    @Test
    public void testMasterDropTarget() {
        DropTarget target = findDropTarget(canvasView);
        Assert.assertNotNull(target);
        
        // We will drop the master component, to see what happens
        @SuppressWarnings("serial")
        View[] mockViews = { new View() {
            @Override
            public AbstractComponent getManifestedComponent() {
                return masterComponent;
            }
        }};
        Mockito.when(mockDropEvent.getTransferable()).thenReturn(new ViewRoleSelection(mockViews));
        
        target.drop(mockDropEvent);
        
        // Dropping should add the master successfully
        boolean foundMaster = false, foundNonMaster = false;
        for (AbstractComponent child : testComponent.getComponents()) {
            foundMaster    |= child == masterComponent;
            foundNonMaster |= child == nonMasterComponent;
        }
        
        Assert.assertFalse(foundNonMaster);
        Assert.assertTrue(foundMaster);
    }

    @Test
    public void testNonMasterDropTarget() {
        DropTarget target = findDropTarget(canvasView);
        Assert.assertNotNull(target);
        
        // We will drop the non-master component, to see what happens
        @SuppressWarnings("serial")
        View[] mockViews = { new View() {
            @Override
            public AbstractComponent getManifestedComponent() {
                return nonMasterComponent;
            }
        }};
        Mockito.when(mockDropEvent.getTransferable()).thenReturn(new ViewRoleSelection(mockViews));
        
        target.drop(mockDropEvent);
        
        // Dropping should add the master, and not the "clone"
        boolean foundMaster = false, foundNonMaster = false;
        for (AbstractComponent child : testComponent.getComponents()) {
            foundMaster    |= child == masterComponent;
            foundNonMaster |= child == nonMasterComponent;
        }
        Assert.assertFalse(foundNonMaster);
        Assert.assertTrue(foundMaster);
    }

    
     private DropTarget findDropTarget(JComponent component) {
         if (component.getDropTarget() != null) return component.getDropTarget();
         for (Component c : component.getComponents()) {
             if (c instanceof JComponent) {
                 DropTarget target = findDropTarget((JComponent) c);
                 if (target != null) return target;
             }
         }
         return null;
     }
    
}
