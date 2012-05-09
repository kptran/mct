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
package gov.nasa.arc.mct.gui.actions;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.util.CloneUtil;
import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.ContextAwareAction;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.gui.TwiddleView;
import gov.nasa.arc.mct.gui.housing.MCTStandardHousing;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class TwiddleWindowAction extends ContextAwareAction {

    private AbstractComponent selectedComponent;
    private TwiddleView twiddleView;
    
    public TwiddleWindowAction() {
        super("Twiddle Mode");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean twiddleMode = selectedComponent.isTwiddledComponent();
        if (!twiddleMode) {
            AbstractComponent twiddledComponent = CloneUtil.TWIDDLE.clone(selectedComponent);
            twiddleView.enterTwiddleMode(twiddledComponent);
        } else {
            twiddleView.exitTwiddleMode(selectedComponent.getMasterComponent());
        }
    }

    @Override
    public boolean canHandle(ActionContext context) {
        
        View manifestation = context.getWindowManifestation();
        
        TwiddleView twiddleView = (TwiddleView) SwingUtilities.getAncestorOfClass(TwiddleView.class, manifestation);
        assert twiddleView instanceof MCTStandardHousing;
        
        boolean state = false;
        if (twiddleView != null) {
            selectedComponent = manifestation.getManifestedComponent();
            if (selectedComponent.isTwiddleEnabled() && !selectedComponent.isVersionedComponent()) {
                this.twiddleView = twiddleView;
                state = selectedComponent.isTwiddledComponent();
                putValue(Action.SELECTED_KEY, state);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
