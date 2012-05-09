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
package gov.nasa.arc.mct.table.model;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.components.Placeholder;
import gov.nasa.arc.mct.evaluator.api.Evaluator;
import gov.nasa.arc.mct.table.utils.NoSizeList;
import gov.nasa.arc.mct.table.view.DisplayedValue;
import gov.nasa.arc.mct.table.view.LabelAbbreviations;
import gov.nasa.arc.mct.table.view.TableCellSettings;
import gov.nasa.arc.mct.table.view.TableViewManifestation;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements a labeled table model that represents a nested object
 * structure of MCT components.
 */
public class ComponentTableModel extends LabeledTableModel {
	
	private static final long serialVersionUID = 1L;

	/** The 2-dimensional matrix of components making up this table model. */
	private TableStructure structure;
	private Map<String,List<Point>> componentLocations;
	
	private Map<String, Object> values = new HashMap<String, Object>(); 
	private Map<String, TableCellSettings> cellSettings = new HashMap<String, TableCellSettings>();
	private Map<String, LabelAbbreviations> cellLabelAbbreviations = new HashMap<String, LabelAbbreviations>();
	
	private NoSizeList<LabelAbbreviations> rowLabelAbbreviations = new NoSizeList<LabelAbbreviations>();
	private NoSizeList<LabelAbbreviations> columnLabelAbbreviations = new NoSizeList<LabelAbbreviations>();

	/** The maximum number of decimals for each column, if known. */
	private NoSizeList<Integer> maxDecimalsForColumn = new NoSizeList<Integer>();
	
	private TableViewManifestation tableViewManifestation;
	
	/**
	 * Creates a new table model from a table structure object and a
	 * labeling algorithm.
	 * 
	 * @param structure the object representing the underlying MCT component structure.
	 * @param algorithm the labeling algorithm.
	 * @param tableViewManifestation the table view manifestation.
	 */
	public ComponentTableModel(
			TableStructure structure,
			TableLabelingAlgorithm algorithm,
			TableViewManifestation tableViewManifestation
	) {
		super(algorithm, TableOrientation.ROW_MAJOR);
		this.structure = structure;
		this.tableViewManifestation = tableViewManifestation;
		updateLocations();
	}
	
	/**
	 * Updates the table structure in response to knowledge that the structure
	 * of the root component in the table has changed. Users of the table model
	 * should call this when they receive notification that children have been
	 * added to or removed from the component that is backing up this model.
	 */
	public void notifyTableStructureChanged() {
		structure.notifyTableStructureChanged();
	}
	
	/**
	 * Returns a unique key for a given component. This key is used by the
	 * code that responds to a feed update to pass along changes to the
	 * component's value.
	 * 
	 * @param component the component for which to determine the key
	 * @return a unique key for the component
	 */
	public String getKey(AbstractComponent component) {
		AbstractComponent delegate = component;
		Evaluator e = component.getCapability(Evaluator.class);
		if (e != null && component.getComponents().size() > 1) {
			return component.getComponentId();
		}
		FeedProvider fp = component.getCapability(FeedProvider.class);
		if (fp != null) {
			return fp.getSubscriptionId();
		}
		
		return delegate.getComponentId();
	}
	
	private void updateLocations() {
		componentLocations = new HashMap<String,List<Point>>();
		for (int row=0; row < structure.getRowCount(); ++row) {
			for (int col=0; col < structure.getColumnCount(); ++col) {
				AbstractComponent component = structure.getValue(row, col);
				if (component != null) {
					component.addViewManifestation(tableViewManifestation);
					List<Point> locations = componentLocations.get(getKey(component));
					if (locations == null) {
						locations = new ArrayList<Point>();
						componentLocations.put(getKey(component), locations);
					}
					locations.add(new Point(col,row));
				}
			}
		}
	}

	@Override
	protected int getObjectCount() {
		return structure.getRowCount();
	}
	
	@Override
	protected int getAttributeCount() {
		return structure.getColumnCount();
	}

	@Override
	protected String getObjectIdentifierAt(int rowIndex, int columnIndex) {
		AbstractComponent component = structure.getValue(rowIndex, columnIndex);
		
		if (component == null) {
			return "";
		} else if  (component == AbstractComponent.NULL_COMPONENT) {
			return "\u00A0";
		} else {
			return getCanonicalName(component);
		}
	}

	/**
	 * Gets a complete identifier labeling the component. These <em>canonical
	 * names</em> for each table cell are used in the table labeling algorithm
	 * to calculate labels for columns, rows, and cells. The canonical name
	 * is the canonical name of the feed provider, if it exists and is not
	 * empty. Otherwise it is the display name for the component.
	 * 
	 * @param component the component for which we need the canonical name
	 * @return the canonical name for the component
	 */
	private String getCanonicalName(AbstractComponent component) {
		String canonicalName = null;
		
		// Try to get the canonical name for the feed provider.
		FeedProvider feedProvider = component.getCapability(FeedProvider.class);
		if (feedProvider != null) {
			canonicalName = feedProvider.getCanonicalName();
		}
		
		// If the feed provider doesn't have a canonical name, use the component display name.
		if (canonicalName==null || canonicalName.isEmpty()) {
			canonicalName = component.getDisplayName();
		}
		
		return canonicalName;
	}
	
	@Override
	public Object getObjectAt(int rowIndex, int columnIndex) {
		AbstractComponent component = structure.getValue(rowIndex, columnIndex);
		
		if (component == null) {
			return null;
		} else if  (component == AbstractComponent.NULL_COMPONENT) {
			return "";
		} else {
			String cellName = getObjectName(rowIndex, columnIndex);

			Object value = getValueForComponent(component);
			if (value instanceof DisplayedValue) {
				DisplayedValue dv = (DisplayedValue) value;
				TableCellSettings cellSettings = getCellSettings(getKey(component));
				dv.setLabel(cellName!=null ? cellName : "");
				dv.setAlignment(cellSettings.getAlignment());
				dv.setNumberOfDecimals(cellSettings.getNumberOfDecimals());
			}
			return value;
		}
	}
	
	private Object getValueForComponent(AbstractComponent component) {
		Object value = values.get(getKey(component));
		if (value == null) {
			DisplayedValue displayedValue = new DisplayedValue();
			if (component.getCapability(Placeholder.class) != null) {
			    displayedValue.setValue(component.getCapability(Placeholder.class).getPlaceholderValue());
			} else {
				displayedValue.setValue(component.getDisplayName());
			}
			return displayedValue;
		} else {
			return value;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	/**
	 * Sets the value of an object updated by a data feed. This change
	 * is propogated to all table cells displaying that object.
	 * 
	 * @param id the identifier for the object updated
	 * @param value the new value to display
	 */
	public void setValue(String id, Object value) {
		values.put(id, value);
		List<Point> locations = componentLocations.get(id);
		if (locations != null) {
			for (Point p : locations) {
				fireTableCellUpdated(p.y, p.x);
			}
		}
	}

	@Override
	public TableType getTableType() {
		return structure.getType();
	}

	@Override
	protected boolean canSetObjectAt(int rowIndex, int columnIndex, boolean isInsertRow, boolean isInsertColumn) {
		return structure.canSetValue(rowIndex, columnIndex, isInsertRow, isInsertColumn);
	}

	@Override
	protected void setObjectAt(Object value, int rowIndex, int columnIndex, boolean isInsertRow, boolean isInsertColumn) {
		structure.setValue(rowIndex, columnIndex, isInsertRow, isInsertColumn, (AbstractComponent) value);
		updateLocations();
	}

	@Override
	protected Object getStoredObjectAt(int objectIndex, int attributeIndex) {
		return structure.getValue(objectIndex, attributeIndex);
	}
	
	/**
	 * Gets the component that will be modified to complete set a value at a particular
	 * location. The new value may be inserted into a new row or column.
	 * 
	 * @param rowIndex the row at which to place the new item
	 * @param columnIndex the column at which to place the new item
	 * @param isInsertRow true, if a new row should be inserted above the position
	 * @param isInsertColumn true, if a new column should be inserted to the left of the position
	 * @return the component that must be modified to set the value at the indicated position
	 */
	public AbstractComponent getModifiedComponentAt(int rowIndex, int columnIndex, boolean isInsertRow, boolean isInsertColumn) {
		if (getOrientation() == TableOrientation.ROW_MAJOR) {
			return structure.getModifiedComponent(rowIndex, columnIndex, isInsertRow, isInsertColumn);
		} else {
			return structure.getModifiedComponent(columnIndex, rowIndex, isInsertColumn, isInsertRow);
		}
	}
	
	/**
	 * Gets the cell settings for a particular component ID.
	 * 
	 * @param id the identifier of the component in the cell
	 * @return the cell settings, or a new set of default settings if no settings have been stored
	 */
	public TableCellSettings getCellSettings(String id) {
		TableCellSettings settings = cellSettings.get(id);
		if (settings == null) {
			settings = new TableCellSettings();
			cellSettings.put(id, settings);
		}
		return settings;
	}
	
	/**
	 * Notifies listeners that a table cell should be redrawn because its settings have changed.
	 * 
	 * @param id the identifier for the component whose settings have changed
	 */
	public void fireCellSettingsChanged(String id) {
		List<Point> locations = componentLocations.get(id);
		if (locations != null) {
			for (Point p : locations) {
				fireTableCellUpdated(p.y, p.x);
			}
		}
	}

	/**
	 * Gets the row label abbreviations for a specified row.
	 *  
	 * @param row the row to retrieve abbreviations for
	 * @return the abbreviations
	 */
	public LabelAbbreviations getRowLabelAbbreviations(int row) {
		LabelAbbreviations abbrevs = rowLabelAbbreviations.get(row);
		if (abbrevs != null) {
			return abbrevs;
		} else {
			return new LabelAbbreviations();
		}
	}
	
	/**
	 * Sets the row label abbreviations for a specified row.
	 *  
	 * @param rowIndex the row to set abbreviations for
	 * @param abbreviations the new abbreviations
	 */
	public void setRowLabelAbbreviations(int rowIndex, LabelAbbreviations abbreviations) {
		rowLabelAbbreviations.set(rowIndex, abbreviations);
		fireLabelsChanged();
	}
	
	/**
	 * Gets column label abbreviations for a specified column.
	 *  
	 * @param column the column index to retrieve abbreviations for
	 * @return the abbreviations
	 */
	public LabelAbbreviations getColumnLabelAbbreviations(int column) {
		LabelAbbreviations abbrevs = columnLabelAbbreviations.get(column);
		if (abbrevs != null) {
			return abbrevs;
		} else {
			return new LabelAbbreviations();
		}
	}
	
	/**
	 * Sets the column label abbreviations for a specified column.
	 *  
	 * @param columnIndex the column to set abbreviations for
	 * @param abbreviations the new abbreviations
	 */
	public void setColumnLabelAbbreviations(int columnIndex, LabelAbbreviations abbreviations) {
		columnLabelAbbreviations.set(columnIndex, abbreviations);
		fireLabelsChanged();
	}
	
	/**
	 * Indicates that the labels of the rows or columns have changed.
	 */
	public void fireHeaderLabelsChanged() {
		fireTableStructureChanged();
	}
	
	/**
	 * Gets the cell label abbreviations for a specific feed provider ID.
	 *  
	 * @param id the feed provider ID (a PUI, if using ISP)
	 * @return the cell label abbreviations
	 */
	public LabelAbbreviations getCellLabelAbbreviations(String id) {
		LabelAbbreviations abbrevs = cellLabelAbbreviations.get(id);
		if (abbrevs == null) {
			abbrevs = new LabelAbbreviations();
			cellLabelAbbreviations.put(id, abbrevs);
		}
		return abbrevs;
	}
	
	/**
	 * Sets the cell label abbreviations for a specific feed provider ID.
	 *  
	 * @param id the feed provider ID (a PUI, if using ISP)
	 * @param abbreviations the new cell label abbreviations
	 */
	public void setCellLabelAbbreviations(String id, LabelAbbreviations abbreviations) {
		cellLabelAbbreviations.put(id, abbreviations);
	}

	@Override
	public String getRowName(int rowIndex) {
		return abbreviateLabel(getFullRowName(rowIndex), getRowLabelAbbreviations(rowIndex));
	}
	
	private String abbreviateLabel(String fullLabel, LabelAbbreviations abbrevs) {
		if (abbrevs == null) {
			return fullLabel;
		} else {
			return abbrevs.applyAbbreviations(fullLabel);
		}
	}

	@Override
	public String getColumnName(int columnIndex) {
		LabelAbbreviations x = getColumnLabelAbbreviations(columnIndex);
		return abbreviateLabel(getFullColumnName(columnIndex), getColumnLabelAbbreviations(columnIndex));
	}

	@Override
	public String getCellName(int rowIndex, int columnIndex) {
		String fullLabel = super.getCellName(rowIndex, columnIndex);
		AbstractComponent component = (AbstractComponent) getStoredValueAt(rowIndex, columnIndex);
		if (component == null) {
			return fullLabel;
		} else {
			LabelAbbreviations abbrevs = getCellLabelAbbreviations(getKey(component));
			return abbrevs.applyAbbreviations(fullLabel);
		}
	}
	
	@Override
	public void updateLabels() {
		super.updateLabels();

		// Forget about any abbreviations we're no longer using.
		rowLabelAbbreviations.truncate(getRowCount());
		columnLabelAbbreviations.truncate(getColumnCount());
	}

	@Override
	public boolean isSkeleton() {
		return structure.isSkeleton();
	}
	
	/**
	 * Gets the maximum number of decimals shown in any cell in a column.
	 * If we have already calculated the maximum for a column, returns it.
	 * Otherwise iterates over all cells in the column to determine the
	 * maximum, and remembers that value.
	 * 
	 * @param columnIndex the column for which we want the max decimals setting
	 * @return the maximum number of decimals shown in the column.
	 */
	public int getMaxDecimalsForColumn(int columnIndex) {
		Integer decimals = maxDecimalsForColumn.get(columnIndex);
		
		if (decimals != null) {
			return decimals;
		}
		
		int maxDecimals = 0;
		for (int rowIndex=0; rowIndex < getRowCount(); ++rowIndex) {
			AbstractComponent component = (AbstractComponent) getStoredValueAt(rowIndex, columnIndex);
			if (component != null) {
				TableCellSettings cellSettings = getCellSettings(getKey(component));
				int cellDecimals = cellSettings.getNumberOfDecimals();
				if (cellDecimals < 0) {
					cellDecimals = TableCellSettings.DEFAULT_DECIMALS;
				}
				
				maxDecimals = Math.max(maxDecimals, cellDecimals);
			}
		}
		
		maxDecimalsForColumn.set(columnIndex, maxDecimals);
		return maxDecimals;
	}

	/**
	 * Updates the maximumd decimals for a column. For simplicity,
	 * just forgets all maximum decimal settings. The max for each
	 * column will be updated when {@link #getMaxDecimalsForColumn(int)} is
	 * called by the table cell renderer.
	 */
	public void updateDecimalsForColumns() {
		maxDecimalsForColumn.clear();
	}
	
}
