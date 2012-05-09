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
package gov.nasa.arc.mct.graphics.view;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.components.FeedProvider.RenderingInfo;
import gov.nasa.arc.mct.evaluator.api.Evaluator;
import gov.nasa.arc.mct.graphics.brush.Brush;
import gov.nasa.arc.mct.graphics.state.StateSensitive;
import gov.nasa.arc.mct.gui.FeedView;
import gov.nasa.arc.mct.gui.FeedView.RenderingCallback;
import gov.nasa.arc.mct.gui.NamingContext;
import gov.nasa.arc.mct.gui.Request;
import gov.nasa.arc.mct.roles.events.PropertyChangeEvent;
import gov.nasa.arc.mct.services.component.ViewInfo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

/**
 * A GraphicalManifestation provides a simple graphical view of an object 
 * (for instance, with a thermometer-style fill, or enumerated fill) 
 * @author vwoeltje
 */
public class GraphicalManifestation extends FeedView implements RenderingCallback {
    private static ResourceBundle bundle = ResourceBundle.getBundle("GraphicsResourceBundle");

	private static final long serialVersionUID = -5934962679343158613L;

	private final List<FeedProvider> feedProviderList;
	
	private GraphicalSettings settings;
	
	private List<Brush>              layers; 
	private Shape             		 shape;

	public static final String VIEW_ROLE_NAME = bundle.getString("View_Name");
	
	public GraphicalManifestation(AbstractComponent component, ViewInfo vi) {
		super(component,vi);
			
		layers = new ArrayList<Brush>();
		
		settings = new GraphicalSettings(this);
		
		setBackground(UIManager.getColor("background"));
		
		this.setOpaque(true);	
		
		feedProviderList = Collections.singletonList(getFeedProvider(getManifestedComponent()));
		
		buildFromSettings();	
	}

	@Override
	protected JComponent initializeControlManifestation() {
		return new JScrollPane(new GraphicalControlPanel(this),
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);		
	}
	
	
	@Override
	public void paint(Graphics g) {
		boolean hasTitle = true;
		NamingContext nc = getNamingContext();
		if (nc != null && nc.getContextualName() == null) hasTitle = false;
		
		/* Only paint background if we are on a panel or similar */
		if (hasTitle) super.paint(g);
			
		/* Pad for border, or other stuff */
		Rectangle bounds = getBounds().getBounds();
		int padding = Math.min(bounds.width, bounds.height) / 20;
		bounds.grow(-padding, -padding);
		
		/* Paint all layers in order */
		for (Brush b : layers) {
			b.draw(shape, g, bounds);
		}	
		
		if (!hasTitle) paintDisplayName(g);		
			
	}
	

	private void paintDisplayName(Graphics g) {
		g.setFont(g.getFont().deriveFont(9.0f));
		
		String name = getManifestedComponent().getDisplayName();
		int width  = g.getFontMetrics().stringWidth(name);
		int height = g.getFontMetrics().getAscent() - 2;
		int x      = (getBounds().width   - width) / 2;
		int y      = (getBounds().height) - height * 2;
		
		
		g.setColor(new Color(255,255,255,160));
		g.fillRoundRect(x - 4, y - 4, width + 8, height + 8, height / 2 , height / 2);
		g.setColor(Color.DARK_GRAY);
		g.drawString(name, x, y + height);
		
	}
	
	/**
	 * Rebuild display to reflect settings
	 */
	public void buildFromSettings() {
		layers = settings.getLayers();
		shape  = (Shape) settings.getSetting(GraphicalSettings.GRAPHICAL_SHAPE);		
		requestPredictiveData();
	}
	
	/**
	 * Get the settings for this manifestation
	 * @return the settings for this manifestation
	 */
	public GraphicalSettings getSettings() {
		return settings;
	}
		
	@Override
	public Collection<FeedProvider> getVisibleFeedProviders() {		
		return feedProviderList;
	}

	@Override
	public void synchronizeTime(Map<String, List<Map<String, String>>> data,
			long syncTime) {
		updateFromFeed(data);
	}

	@Override
	public void updateFromFeed(Map<String, List<Map<String, String>>> data) {		
		AbstractComponent component = getManifestedComponent();
		FeedProvider provider = getFeedProvider(component);
			
		if (provider != null) {
			List<Map<String, String>> feedData = data.get(provider.getSubscriptionId());
			if (feedData != null && !feedData.isEmpty()) {
				Map<String, String> feedDataItem = feedData.get(feedData.size() - 1);

				List<RenderingInfo> riList = new ArrayList<RenderingInfo>();
				riList.add(provider.getRenderingInfo(feedDataItem));
				
				/* Get updates from the appropriate evaluator */
				if (settings.getSetting(GraphicalSettings.GRAPHICAL_EVALUATOR) instanceof AbstractComponent) {
					AbstractComponent comp = (AbstractComponent) settings.getSetting(GraphicalSettings.GRAPHICAL_EVALUATOR);
					riList.add(comp.getCapability(Evaluator.class).evaluate(data, Collections.singletonList(provider)));
				}
				
				/* Send both numeric state and evaluator state to brushes */
				if (riList.get(0).isPlottable()) { // ...only if we have new feed data
					for (RenderingInfo ri : riList) {
						for (Brush b : layers) {
							if (b instanceof StateSensitive) {
								((StateSensitive) b).setState(ri.getValueText());
							}
						}			
					}
				} 
				// TODO: Show status character on LOS
				
				
			}
		}		
		
		repaint(); // Update the graphical representation
		           // TODO: Only repaint on change?
	}

	private void requestPredictiveData() {
		/* Explicitly request data if we're using a predictive feed */
		boolean predictive = false;
		
		for (FeedProvider fp : feedProviderList) {
			if (fp != null && fp.isPrediction()) {
				predictive = true;
				break;
			}			
		}
		
		if (predictive) {
			requestData(feedProviderList, System.currentTimeMillis(), System.currentTimeMillis(), 
					new DataTransformation() {
						@Override
						public void transform(
								Map<String, List<Map<String, String>>> data,
								long startTime, long endTime) {}
					},
					this, 
					true);
		}
		
		/* We expect non-predictive feeds to push their data */
	}
	
	
	@Override
	public void updateMonitoredGUI() {
		this.buildFromSettings();
	}
	
	@Override
	public void updateMonitoredGUI(PropertyChangeEvent evt) {
		this.buildFromSettings();
		// TODO: This may be where to catch naming context changes
	}

	@Override
	public void render(Map<String, List<Map<String, String>>> data) {
		updateFromFeed(data);		
	}


}
