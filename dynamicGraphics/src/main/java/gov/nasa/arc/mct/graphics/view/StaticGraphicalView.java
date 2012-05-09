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
import gov.nasa.arc.mct.graphics.component.GraphicalComponent;
import gov.nasa.arc.mct.graphics.component.GraphicalModel;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.services.component.ViewInfo;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


public class StaticGraphicalView extends View {
    private static ResourceBundle bundle = ResourceBundle.getBundle("GraphicsResourceBundle");
    
	private static final long serialVersionUID = -6823838565608622054L;
	private SVGRasterizer rasterizer = null;
	private ImagePanel    imagePanel = new ImagePanel();
	
	public static final String VIEW_ROLE_NAME = bundle.getString("View_Name");
	
	public StaticGraphicalView(AbstractComponent component, ViewInfo vi) {
		super(component, vi);

		setBackground(UIManager.getColor("background"));
		setForeground(UIManager.getColor("foreground"));

		imagePanel.setBackground(getBackground());
		add(imagePanel);
		
		if (component instanceof GraphicalComponent) { 
			GraphicalModel model =  ((GraphicalComponent)component).getModelRole();
			prepareGraphicalView(model.getGraphicURI());
		} else {
			prepareFailureLabel(bundle.getString("Component_Error"));
		}
		
	}	
	
	private void prepareGraphicalView(final String graphicURI) {

		if (graphicURI.endsWith(".svg")) {
			rasterizer = new SVGRasterizer(graphicURI);
			rasterizer.setCallback(new Runnable() {
				public void run() {						
					if (rasterizer.hasFailed()) {
						prepareRasterImage(graphicURI); // Maybe the extension is wrong
					} else {							
						imagePanel.setImage(rasterizer.getLatestImage());
					}
					repaint();					
				}
			});
			
			addComponentListener( new ComponentListener() {
				@Override
				public void componentHidden(ComponentEvent arg0) {
					/* Get a smaller image - less memory use */
					rasterizer.requestRender(50, 50);					
				}

				@Override
				public void componentMoved(ComponentEvent arg0) {
				}

				@Override
				public void componentResized(ComponentEvent arg0) {
					rasterizer.requestRender(getWidth(), getHeight());
				}

				@Override
				public void componentShown(ComponentEvent arg0) {
					rasterizer.requestRender(getWidth(), getHeight());					
				}
				
			});
			
		} else {		
			prepareRasterImage(graphicURI);
		}
					
	}
	
	private void prepareRasterImage(String graphicURI) {
		try {
			BufferedImage img = ImageIO.read(new URL(graphicURI));
			if (img != null) {
				imagePanel.setImage(img);
			} else {
				prepareFailureLabel("Type_Error", graphicURI);
			}
		} catch (IOException ioe) {
			prepareFailureLabel(
					(ioe.getCause() instanceof FileNotFoundException) ? 
							"FileNotFound_Error" : "Location_Error", 
					graphicURI);
		}
	}
		
	private void prepareFailureLabel(String bundleKey, String graphicURI) {
		String failureString = bundle.getString(bundleKey);
		prepareFailureLabel( String.format(failureString, graphicURI) );
	}
	
	private void prepareFailureLabel(final String failureText) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				removeAll();
				setLayout(new BorderLayout());
				JLabel failureLabel = new JLabel(failureText);
				failureLabel.setBackground(getBackground());
				failureLabel.setForeground(getForeground());
				add(failureLabel, BorderLayout.NORTH);
				repaint();
			}
		});
	}
		
	
	private class ImagePanel extends JPanel {
		private static final long serialVersionUID = -2223786881389122841L;
		
		private BufferedImage image = null;
		
		public void setImage(BufferedImage image) {
			this.image = image;
		}

		public void paint(Graphics g) {
			super.paint(g);
			if (image != null) {			
				g.drawImage(image, 0, 0, getWidth(), getHeight(), 
						0, 0, image.getWidth(), image.getHeight(), this);
			} 			
		}
		
	}
}

