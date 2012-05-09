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
package gov.nasa.arc.mct.fastplot.bridge;

import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.fastplot.utils.AbbreviatingPlotLabelingAlgorithm;
import gov.nasa.arc.mct.fastplot.utils.TruncatingLabel;
import gov.nasa.arc.mct.fastplot.view.LegendEntryPopupMenuFactory;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plotter.xy.LinearXYPlotLine;

/**
 *  Legend entry for a plot line. The class responds to mouse entered events by increasing the brightness of the text labels.
 */
@SuppressWarnings("serial")
public class LegendEntry extends JPanel implements MouseListener {

	private final static Logger logger = LoggerFactory.getLogger(LegendEntry.class);
	
	// Padding around labels to create space between the label text and its outside edge
	// Add a little spacing from the left-hand side
	private static final int    LEFT_PADDING  = 5;
	private static final Border PANEL_PADDING = BorderFactory.createEmptyBorder(0, LEFT_PADDING, 0, 0);
	// Associated plot.
	private LinearXYPlotLine linePlot;

	// Gui widgets
	protected JLabel baseDisplayNameLabel= new TruncatingLabel();
	private Color backgroundColor;
	private Color foregroundColor;
	private Color originalPlotLineColor;
	private Stroke originalPlotLineStroke;
	private Font originalFont;
	private Font boldFont;
	private Font strikeThruFont;
	private Font boldStrikeThruFont;

	private String baseDisplayName = "";
	
	boolean selected=false;
	
	private String currentToolTipTxt = "";
	private ToolTipManager toolTipManager;
	
	private String thisBaseDisplayName = "";	
	private String valueString = "";
	private AbbreviatingPlotLabelingAlgorithm plotLabelingAlgorithm = new AbbreviatingPlotLabelingAlgorithm();
	private String computedBaseDisplayName = "";
	private FeedProvider.RenderingInfo renderingInfo;
	
	private LegendEntryPopupMenuFactory popupManager = null;

	// Default width - will be adjusted to match base display name
	private int baseWidth = PlotConstants.PLOT_LEGEND_WIDTH;
	
	/**
	 * Construct a legend entry
	 * @param theBackgroundColor background color of the entry
	 * @param theForgroundColor text color
	 * @param font text font
	 */
	LegendEntry(Color theBackgroundColor, Color theForgroundColor, Font font, AbbreviatingPlotLabelingAlgorithm thisPlotLabelingAlgorithm) { 
		
		plotLabelingAlgorithm = thisPlotLabelingAlgorithm;
		
		backgroundColor = theBackgroundColor;	
		foregroundColor =  theForgroundColor;
		setForeground(foregroundColor);
		
		// NOTE: Original font size is 10. Decrease by 1 to size 9. 
		// Need to explicitly cast to float from int on derived font size
		// Need to explicitly set to FontName=ArialMT/Arial-BoldMT and FontFamily=Arial to be
		// cross OS platforms L&F between MacOSX and Linux.
		// MacOSX defaults to Arial and Linux defaults to Dialog FontFamily.
		originalFont = font;
		originalFont = originalFont.deriveFont((float)(originalFont.getSize()-1));	
		boldFont = originalFont.deriveFont(Font.BOLD);
		Map<TextAttribute, Object> attributes = new Hashtable<TextAttribute, Object>();
		attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		strikeThruFont = originalFont.deriveFont(attributes);
		boldStrikeThruFont = boldFont.deriveFont(attributes);
	
		// Setup the look of the labels.
		baseDisplayNameLabel.setBackground(backgroundColor);
		baseDisplayNameLabel.setForeground(foregroundColor);
		baseDisplayNameLabel.setFont(originalFont);
		baseDisplayNameLabel.setOpaque(true);
		
		// Sets as the default ToolTipManager
		toolTipManager = ToolTipManager.sharedInstance();
		toolTipManager.setEnabled(true);
		toolTipManager.setLightWeightPopupEnabled(true);
		
		// Defaults: toolTipManager.getDismissDelay()=4000ms, 
		// toolTipManager.getInitialDelay()=750ms, 
		// toolTipManager.getReshowDelay()=500ms 
		toolTipManager.setDismissDelay(PlotConstants.MILLISECONDS_IN_SECOND * 3);
		toolTipManager.setInitialDelay(PlotConstants.MILLISECONDS_IN_SECOND / 2);
		toolTipManager.setReshowDelay(PlotConstants.MILLISECONDS_IN_SECOND / 2);
		
		// Place the labels according to the format specified in the UE spec.
		layoutLabels();

		// Listen to mouse events to drive the highlighting of legends when mouse enters. 
		addMouseListener(this);
	}

	// Data getter and and setters
	void setPlot(LinearXYPlotLine thePlot) {
		linePlot = thePlot;
		linePlot.setForeground(foregroundColor);
	}


	private List<String> getPanelOrWindowContextTitleList() {
		List<String> panelOrWindowContextTitleList = new ArrayList<String>();
		panelOrWindowContextTitleList.clear();
		
		if (plotLabelingAlgorithm != null) {

			if (plotLabelingAlgorithm.getPanelContextTitleList().size() > 0) {
				panelOrWindowContextTitleList.addAll(this.plotLabelingAlgorithm.getPanelContextTitleList());
			}
			
			if (plotLabelingAlgorithm.getCanvasContextTitleList().size() > 0) {
				panelOrWindowContextTitleList.addAll(this.plotLabelingAlgorithm.getCanvasContextTitleList());
			}
			
		} else {
			logger.error("Plot labeling algorithm object is NULL!");
		}
				
		return panelOrWindowContextTitleList;
	}
	
	public void setBaseDisplayName(String theBaseDisplayName) {
		
		thisBaseDisplayName = theBaseDisplayName;
		
		if (thisBaseDisplayName != null) {
			thisBaseDisplayName = thisBaseDisplayName.trim();
		}
		
		baseDisplayName  = thisBaseDisplayName;
					
		// Format the base display name 
		// Split string around newline character.
		 String[] strings = baseDisplayName.split(PlotConstants.LEGEND_NEWLINE_CHARACTER); 
				 
	     if (strings.length <= 1) {
			// Determine if first or second string is null
	    	 if (baseDisplayName.indexOf(PlotConstants.LEGEND_NEWLINE_CHARACTER) == -1) {
				// first string is null.
				baseDisplayNameLabel.setText(baseDisplayName); 
			} else if (theBaseDisplayName.equals(PlotConstants.LEGEND_NEWLINE_CHARACTER)) {
				baseDisplayNameLabel.setText("");	
			} else {
				// second string is empty. Truncate first.
				baseDisplayNameLabel.setText(PlotConstants.LEGEND_ELIPSES);	
			}
		 } else {
			 
			 // Use table labeling algorithm to display base display name.
			 // line1 is base display name; while line2 is PUI name
			 String line1 = strings[0];
			 
			 if (line1 != null) {
				 baseDisplayName = line1.trim();
				 thisBaseDisplayName = baseDisplayName;
			 }
			 
			 List<String> baseDisplayNameList = new ArrayList<String>();
			 baseDisplayNameList.add(line1);
			
			 assert plotLabelingAlgorithm != null : "Plot labeling algorithm should NOT be NULL at this point.";
			 
			 baseDisplayName = plotLabelingAlgorithm.computeLabel(baseDisplayNameList, getPanelOrWindowContextTitleList());
			 
			 // since this name will be used in a legend, it must not be empty so use the initial display name if it would have been empty
			 if (baseDisplayName != null && baseDisplayName.isEmpty()) {
				 baseDisplayName = thisBaseDisplayName;
			 }
			 computedBaseDisplayName = baseDisplayName;

			 updateLabelText();
		 }
			     
	     thisBaseDisplayName = theBaseDisplayName.replaceAll(PlotConstants.WORD_DELIMITERS, " ");
	     currentToolTipTxt = "<HTML>" + thisBaseDisplayName.replaceAll(PlotConstants.LEGEND_NEWLINE_CHARACTER, "<BR>") + "<BR>" + valueString + "<HTML>";
	     this.setToolTipText(currentToolTipTxt);	
	}

	void setData(FeedProvider.RenderingInfo info) {
		this.renderingInfo = info;
		String valueText = info.getValueText();
		if (!"".equals(valueText)) {
			valueString = PlotConstants.DECIMAL_FORMAT.format(Double.parseDouble(valueText));
		}
		updateLabelFont();
		updateLabelText();
		thisBaseDisplayName = thisBaseDisplayName.replaceAll(PlotConstants.WORD_DELIMITERS, " ");
	    currentToolTipTxt = "<HTML>" + thisBaseDisplayName.replaceAll(PlotConstants.LEGEND_NEWLINE_CHARACTER, "<BR>") + "<BR>" + valueString + "<HTML>";
	    this.setToolTipText(currentToolTipTxt);
	     
	}


	private void updateLabelFont() {
		if(selected) {
			if(renderingInfo == null || renderingInfo.isPlottable()) {
				baseDisplayNameLabel.setFont(boldFont);
			} else {
				baseDisplayNameLabel.setFont(boldStrikeThruFont);
			}
		} else {
			if(renderingInfo == null || renderingInfo.isPlottable()) {
				baseDisplayNameLabel.setFont(originalFont);
			} else {
				baseDisplayNameLabel.setFont(strikeThruFont);
			}
		}
	}


	private void updateLabelText() {
		String statusText = renderingInfo == null ? null : renderingInfo.getStatusText();
		if(statusText == null) {
			statusText = "";
		}
		statusText = statusText.trim();
		if(!"".equals(statusText)) {
			baseDisplayNameLabel.setText("(" + statusText + ") " + baseDisplayName);
		} else {
			baseDisplayNameLabel.setText(baseDisplayName);
		}
	}
	
	private void updateLabelWidth() {
		/* Record font & string to restore*/
		Font   f = baseDisplayNameLabel.getFont();
		String s = baseDisplayNameLabel.getText();
		
		if (f == originalFont && s.equals(baseDisplayName) && baseDisplayNameLabel.isValid()) {
			baseWidth = baseDisplayNameLabel.getWidth();
		}
		
	}
	
	/**
	 * Layout the labels within a legend in line with the UE specification.
	 */
	void layoutLabels() {
		setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		panel.setBorder(PANEL_PADDING);
		panel.setBackground(backgroundColor);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JPanel displayNamePanel = new JPanel();
		displayNamePanel.setLayout(new BoxLayout(displayNamePanel, BoxLayout.LINE_AXIS));
		displayNamePanel.add(baseDisplayNameLabel);
		displayNamePanel.setAlignmentX(Component.LEFT_ALIGNMENT);


		panel.add(displayNamePanel);

		add(panel, BorderLayout.CENTER);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// do nothing	
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
		toolTipManager.registerComponent(this);
		
		selected = true;
		// Highlight this legend entry
		baseDisplayNameLabel.setForeground(foregroundColor.brighter());
		updateLabelFont();
		
		// Highlight this entry on the plot.
		originalPlotLineColor = linePlot.getForeground();
		originalPlotLineStroke = linePlot.getStroke();

		linePlot.setForeground(originalPlotLineColor.brighter().brighter());
		BasicStroke stroke = (BasicStroke) originalPlotLineStroke;
		if(stroke == null) {
			linePlot.setStroke(new BasicStroke(PlotConstants.SELECTED_LINE_THICKNESS));
		} else {
			linePlot.setStroke(new BasicStroke(stroke.getLineWidth() * PlotConstants.SELECTED_LINE_THICKNESS, stroke.getEndCap(), stroke
					.getLineJoin(), stroke.getMiterLimit(), stroke.getDashArray(), stroke.getDashPhase()));
		}
				
		this.setToolTipText(currentToolTipTxt);
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
		toolTipManager.unregisterComponent(this);
		
		selected = false;
		// Return this legend entry to its original look. 
		baseDisplayNameLabel.setForeground(foregroundColor);
		updateLabelFont();
		
		// Return this entry on the plot to its original look. 
		linePlot.setForeground(originalPlotLineColor);
		linePlot.setStroke(originalPlotLineStroke);
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// open the color changing popup	
		if (popupManager != null && e.isPopupTrigger()) {
			popupManager.getPopup(this).show(this, e.getX(), e.getY());
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// do nothing
	}
	
	public String getToolTipText() {
		return currentToolTipTxt;
	}

	// Retrieves whatever is set in the label text field
	public String getBaseDisplayNameLabel() {
		return baseDisplayNameLabel.getText();
	}
		
	// Retrieves base display name + PUI name
	public String getFullBaseDisplayName() {
		return thisBaseDisplayName;
	}
	
	// Retrieves only base display name (w/o PUI name)
	// after running thru labeling algorithm
	public String getComputedBaseDisplayName() {
		return computedBaseDisplayName;
	}
	
	// Retrieves truncated with ellipse 
	public String getTruncatedBaseDisplayName() {
		return baseDisplayName;
	}
	
	public void setPlotLabelingAlgorithm(AbbreviatingPlotLabelingAlgorithm plotLabelingAlgorithm) {
		this.plotLabelingAlgorithm = plotLabelingAlgorithm;
	}
	
	public AbbreviatingPlotLabelingAlgorithm getPlotLabelingAlgorithm() {
		return this.plotLabelingAlgorithm;
	}
	
	public int getLabelWidth() {
		updateLabelWidth();
		return baseWidth + LEFT_PADDING;
	}

	@Override
	public void setForeground(Color fg) {
		Color lineColor = fg;
		Color labelColor = fg;
		if (linePlot != null) {
			if (linePlot.getForeground() != foregroundColor) lineColor = fg.brighter().brighter();
			linePlot.setForeground(lineColor);
		}
		
		if (baseDisplayNameLabel != null) {
			if (baseDisplayNameLabel.getForeground() != foregroundColor) labelColor = fg.brighter();
			baseDisplayNameLabel.setForeground(labelColor);
		}
		
		foregroundColor = fg;
		
		super.setForeground(fg);
	}

	public LegendEntryPopupMenuFactory getPopup() {
		return popupManager;
	}

	public void setPopup(LegendEntryPopupMenuFactory popup) {
		this.popupManager = popup;
	}

}
