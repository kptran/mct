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
package gov.nasa.arc.mct.fastplot.utils;

import gov.nasa.arc.mct.fastplot.bridge.PlotConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a plot labeling algorithm that tries to abbreviate base display name as Jlabel 
 * based on not repeating words that appear in other labels.
 */
public class AbbreviatingPlotLabelingAlgorithm { 
	
	private final static Logger logger = LoggerFactory.getLogger(AbbreviatingPlotLabelingAlgorithm.class);
	
	public static List<String> globalContextLabels = new ArrayList<String>();
	
	private List<String> panelContextTitleList = new ArrayList<String>();
	private List<String> canvasContextTitleList = new ArrayList<String>();
	
	private String panelOrWindowTitle = "";
	private String canvasPanelTitle = "";
	
	private String name = "";
	
	/**
	 * Creates a new instance of the labeling algorithm.
	 */
	public AbbreviatingPlotLabelingAlgorithm() { }
	

	/**
	 * Computes a label for a row or column with a set of cell identifiers.
	 * The resulting label should have all words that are common among the
	 * cells, excluding any words that are in the surrounding context labels.
	 * Labels from the surrounding context could be from an existing
	 * row or column label or from a surrounding panel or window, for example.
	 * 
	 * @param identifiers the cell identifiers for the row or column
	 * @param contextLabels the labels for the surrounding context
	 * @return the label for the row or column
	 */
	public String computeLabel(List<String> identifiers, List<String> contextLabels) {
		List<String> labelWords = null;
		
		for (int i=0; i < identifiers.size(); i++) {				
			List<String> cellLabelWords = StringUtils.split(identifiers.get(i), PlotConstants.WORD_DELIMITER_PATTERN);
			if (cellLabelWords.size() > 0) {
				if (labelWords == null) {
					labelWords = cellLabelWords;
				} else {
					labelWords.retainAll(cellLabelWords);
				}
			}
		}
		
		
		if (labelWords == null) {
			return "";
		} else {
			for (String label : contextLabels) {
				logger.debug("label.trim()={}", label.trim());
				labelWords.removeAll(StringUtils.split(label.trim(), PlotConstants.WORD_DELIMITER_PATTERN));
			}
			return StringUtils.join(labelWords, PlotConstants.WORD_SEPARATOR);
		}
	}

	/** The array return type of {@link #getContextLabels()}, used to
	 * convert a list into an array. 
	 */
	private static final String[] CONTEXT_LABEL_ARRAY_TYPE = new String[0];
	
	/**
	 * Gets the labels describing the surrounding context of the table.
	 * The words in these labels will be stripped during the label
	 * computation, so that they never appear as row, column, nor cell
	 * labels.
	 * 
	 * @return an array containing the context labels, which may be empty
	 */
	public String[] getContextLabels() {
		return globalContextLabels.toArray(CONTEXT_LABEL_ARRAY_TYPE);
	}

	/**
	 * Sets the labels describing the surrounding context of the table.
	 * The words in these labels will be stripped during the label
	 * computation, so that they never appear as row, column, nor cell
	 * labels.
	 * 
	 * @param labels zero or more labels from the surrounding context
	 */
	public void setContextLabels(String... labels) {
		globalContextLabels.clear();
		globalContextLabels.addAll(Arrays.asList(labels));
	}

	public void setPanelContextTitleList(List<String> thePanelContextTitleList) {
		panelContextTitleList.clear();
		panelContextTitleList.addAll(thePanelContextTitleList);
	}
	
	public List<String> getPanelContextTitleList() {	
		return panelContextTitleList;
	}
	
	public void setCanvasContextTitleList(List<String> theCanvasContextTitleList) {
		canvasContextTitleList.clear();
		canvasContextTitleList.addAll(theCanvasContextTitleList);
	}
	
	public List<String> getCanvasContextTitleList() {	
		return canvasContextTitleList;
	}
	
	public void setPanelOrWindowTitle(String panelOrWindowTitle) {
		this.panelOrWindowTitle = panelOrWindowTitle;
	}
	
	public String getPanelOrWindowTitle() {
		return panelOrWindowTitle;
	}
	
	public void setCanvasPanelTitle(String canvasPanelTitle) {
		this.canvasPanelTitle = canvasPanelTitle;
	}
	
	public String getCanvasPanelTitle() {
		return canvasPanelTitle;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
}
