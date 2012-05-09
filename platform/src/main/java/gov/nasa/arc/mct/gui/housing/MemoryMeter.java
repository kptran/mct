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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * This class contains the state of memory meters in the MCT instance.
 *
 */
public class MemoryMeter {
    private static final MemoryMeter INSTANCE = new MemoryMeter();
    private final List<WeakReference<JProgressBar>> activeWidgets = new LinkedList<WeakReference<JProgressBar>>();
    private static final int MEMORY_UPDATE_RATE_MILLIS = 5000;
    private static final int MEGABYTES = 1048576;
    private static final ResourceBundle BUNDLE = 
        ResourceBundle.getBundle(
                MemoryMeter.class.getName().substring(0, 
                        MemoryMeter.class.getName().lastIndexOf("."))+".Bundle");
    private boolean visible = false;
    private final Timer memoryUpdateTimer =  new Timer(MEMORY_UPDATE_RATE_MILLIS, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
           updateMemoryMeters();
        }
    });
    
    private MemoryMeter() {
    }
    
    private void updateMemoryMeters() {
        if (isVisible()) {
            MemoryUsage usage =  ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
            String tooltip = 
                MessageFormat.format(
                    BUNDLE.getString("MemoryMeter.Tooltip"),usage.getUsed()/MEGABYTES, usage.getMax()/MEGABYTES);
            int used = (int) usage.getUsed()/MEGABYTES;
            int max = (int) usage.getMax()/MEGABYTES;
            Iterator<WeakReference<JProgressBar>> it = activeWidgets.iterator();
            while (it.hasNext()) {
                JProgressBar bar = it.next().get();
                if (bar == null) {
                    it.remove();
                    break;
                }
                bar.setToolTipText(tooltip);
                bar.setValue((int) ((used/(double)max)*100));
            }
        }
    }
    
    /**
     * Returns the instance of the memory meter. 
     */
    public static MemoryMeter getInstance() {
        return INSTANCE;
    }
    
    /**
     * Iterate through all the meters to set the visibility. 
     * @param visible true if meters should be visible false otherwise
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
        Iterator<WeakReference<JProgressBar>> it = activeWidgets.iterator();
        while (it.hasNext()) {
            JProgressBar bar = it.next().get();
            if (bar == null) {
                it.remove();
                break;
            }
            bar.setVisible(visible);
        }
        updateMemoryMeters();
        if (visible) {
            memoryUpdateTimer.start();
        } else {
            memoryUpdateTimer.stop();
        }
    }
    
    /**
     * Return true if the memory meter should be visible, false otherwise
     */
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Creates an instance of a memory meter widget. This will be managed by this component.
     * @return instance of a memory meter widget for inclusion in a UI. The visibility of the widget will
     * be controlled through an action.
     */
    public JComponent getMemoryMeterWidget() {
        final JProgressBar bar = new JProgressBar(JProgressBar.HORIZONTAL);
        bar.setIndeterminate(false);
        bar.setMaximum(100);
        bar.setMinimum(0);
        bar.setBorderPainted(true);
        bar.setStringPainted(true);
        bar.addAncestorListener(new AncestorListener() {

            @Override
            public void ancestorAdded(AncestorEvent event) {
                // remove border from my parent
                ((JComponent) bar.getParent()).setBorder(null);
                bar.setVisible(visible);
                activeWidgets.add(new WeakReference<JProgressBar>(bar));
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
            }

        });

        return bar;
    }
    
    
}
