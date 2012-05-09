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
import gov.nasa.arc.mct.services.component.ComponentRegistry;
import gov.nasa.arc.mct.table.access.ServiceAccess;
import gov.nasa.arc.mct.table.policy.TableViewPolicy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements a data structure that maintains the structure of a tabular
 * array of objects.
 */
public class TableStructure {
	
	private static final Logger logger = LoggerFactory.getLogger(TableStructure.class);
	
	/** The type of table, 0-dimensional, 1-dimensional, or 2-dimensional. */
	private TableType type;
	
	/** The root component. */
	private AbstractComponent rootComponent;

	/** The maximum number of columns of any row. */
	private int columnCount = 0;
	
	/**
	 * Creates a tabular structure of a given dimensionality.
	 * 
	 * @param type the type of tabular structure, 0-dimensional, 1-dimensional,
	 *   or 2-dimensional
	 * @param rootComponent the root of the table structure
	 */
	public TableStructure(TableType type, AbstractComponent rootComponent) {
		this.type = type;
		this.rootComponent = rootComponent;
		buildTable();
		updateColumnCount();
	}
	
	/**
	 * Tells the structure that children have been added to or removed from
	 * the root component of the structure. 
	 */
	public void notifyTableStructureChanged() {
		updateColumnCount();
	}
	
	private void updateColumnCount() {
		if (type != TableType.TWO_DIMENSIONAL) {
			columnCount = 1;
		} else {
			columnCount = 1; // We always pretend to have at least one column, so that we can drag-and-drop.
			for (List<TableCell> row : rowList) {
				if (row.size() > columnCount) { 
					columnCount = row.size();
				}
			}
		}
	}
	
	/**
	 * Gets the number of rows in the tabular structure.
	 * 
	 * @return the number of rows
	 */
	public int getRowCount() {
		if (type == TableType.ZERO_DIMENSIONAL) {
			return 1;
		} else {
			return Math.max(1, rowList.size());
		}
	}
	
	/**
	 * Gets the number of columns in the tabular structure. The number
	 * of columns is the maximum number of elements in any row.
	 * 
	 * @return the number of columns
	 */
	public int getColumnCount() {
		return Math.max(1, columnCount);
	}
		
	/**
	 * Tests whether the table structure is a skeleton, ready for values to be
	 * dropped in to flesh out the table cells and columns. A skeleton table
	 * structure is a 2-D table that doesn't yet have any values.
	 * 
	 * @return true, if the table is a skeleton
	 */
	public boolean isSkeleton() {
		return type==TableType.TWO_DIMENSIONAL && rowList.size() == 0;
	}
	
	/**
	 * Gets a value in the tabular array.
	 * 
	 * @param rowIndex the row for which to get the value
	 * @param columnIndex the column for which to get the value
	 * @return the value at the row and column position, or null if there is no value at that position
	 * @throws ArrayIndexOutOfBoundsException
	 */
	
	public AbstractComponent getValue(int rowIndex, int columnIndex) {
		
		try {
			
			if (rowIndex < 0 || rowIndex >= getRowCount()) {
				logger.warn("Row index " + rowIndex + " > " + (getRowCount() - 1));
			}
		
			if (columnIndex < 0 || columnIndex >= columnCount) {
				logger.warn("Column index " + columnIndex + " > " + (columnCount - 1));
			}
		
			if (rowList.size() > 0) {
				TableRow row = rowList.get(rowIndex);
				if (row.size() > 0 && columnIndex < row.size()) {
					return row.get(columnIndex).getValue();
				} else {
					return null; // Not all rows are same width, so pad with nulls
				}
			} else {
				return null;     // Not all rows are same width, so pad with nulls
			}
		} catch (IndexOutOfBoundsException outOfBoundsException) {
			logger.warn("IndexOutOfBoundsException: {0}", outOfBoundsException);
			return null; 
		}
	}
	
	/**
	 * Sets a value at a row and column position in the tabular
	 * array.
	 * 
	 * @param rowIndex the row for which to set the value
	 * @param columnIndex the column for which to set the value
	 * @param isInsertRow true, if the value should be inserted in a new row prior to the indicated row
	 * @param isInsertColumn true, if the value should be inserted in a new column prior to the indicated row
	 * @param value the new value
	 */
	public void setValue(int rowIndex, int columnIndex, boolean isInsertRow, boolean isInsertColumn, AbstractComponent value) {
		assert canSetValue(rowIndex, columnIndex, isInsertRow, isInsertColumn);
		assert type != TableType.ZERO_DIMENSIONAL;
		
		// Refuse values which cannot be placed in tables
		// TODO: User notification? Or disallow drop to begin with?
		if (!canEmbedInTable(value)) return;
		
	
		if (type == TableType.ONE_DIMENSIONAL) {
			
			// Determine where in the root component we want to add the component
			int componentIndex;
			if (rowIndex < rowList.size()) {
				componentIndex = rowList.get(rowIndex).get(0).getIndex();
			} else {
				componentIndex = rootComponent.getComponents().size(); //Add it to the end
			}
			
			// Remove the old component, if there is one
			if (!isInsertRow) {
				TableCell cell = rowList.get(rowIndex).get(columnIndex);
				AbstractComponent toBeRemoved = cell.getValue();
				rootComponent.removeDelegateComponent(toBeRemoved);
			}
			rootComponent.addDelegateComponents(componentIndex, Collections.<AbstractComponent> singletonList(value));
			
		} else {
			
			if (isInsertRow) {
				// Should insert new child. Create a collection of the same type as
				// the root object.
				int componentIndex;
				if (rowIndex < rowList.size()) {
					componentIndex = rowList.get(rowIndex).getIndex();
				} else {
					componentIndex = rootComponent.getComponents().size();
				}
				AbstractComponent newChild = ServiceAccess.getService(ComponentRegistry.class).newCollection(Collections.singleton(value));
				rootComponent.addDelegateComponents(componentIndex, Collections.singleton(newChild));
			} else { //Insert column, or overwrite cell
				TableRow row = rowList.get(rowIndex);
				
				int componentIndex;
				AbstractComponent targetComponent;
				if (!isInsertColumn && columnIndex < row.size()) {
				}
				if (columnIndex < row.size()) {
					targetComponent = row.get(columnIndex).getParent();
					componentIndex  = row.get(columnIndex).getIndex();
					if (!isInsertColumn) {
						targetComponent.removeDelegateComponent(row.get(columnIndex).getValue());
					}
				} else {
					targetComponent = row.get(row.size()-1).getParent();
					componentIndex  = targetComponent.getComponents().size();
				}
				targetComponent.addDelegateComponents(componentIndex, Collections.singleton(value));
				
			}
			
			updateColumnCount(); // in case we added a new column
		}
		
		buildTable();
	}
	
	/**
	 * Tests whether a value can be set at a position. We can only set values
	 * where existing values already exist, or where they are within the bounds
	 * of the matrix and would extend a row or column. That is, we cannot allow gaps to
	 * exist within an object.
	 * 
	 * @param rowIndex the row index at which to set the value
	 * @param columnIndex the column index at which to set the value
	 * @param isInsertRow true, if the value should be inserted in a new row prior to the indicated row
	 * @param isInsertColumn true, if the value should be inserted in a new column prior to the indicated row
	 * @return true, if the value can be set at the position
	 */
	public boolean canSetValue(int rowIndex, int columnIndex, boolean isInsertRow, boolean isInsertColumn) {
		if (isInsertRow && isInsertColumn) {
			return false;
		}
		if (rowIndex < 0 || columnIndex < 0) {
			return false;
		}
		if (rowIndex > rowList.size() || columnIndex > columnCount) {
			return false;
		}
		
		// Cannot modify a single element table.
		if (type == TableType.ZERO_DIMENSIONAL) {
			return false;
		}
		
		// Can only set values in column zero for a one dimensional table.
		if (type == TableType.ONE_DIMENSIONAL) {
			if (columnIndex != 0) {
				return false;
			}
			if (isInsertRow) {
				return 0<=rowIndex && rowIndex <= rowList.size();
			} else {
				return 0<=rowIndex && rowIndex < rowList.size();
			}
		}
		
		// Two dimensional case. We have to ensure there is an existing value at the position, or
		// that the position is within the bounds of the matrix and just past the last value.
		
		// Special case of an empty table. We can only insert a row into 0, 0.
		if (rowList.size() == 0) {
			return rowIndex==0 && columnIndex==0 && isInsertRow;
		}
		
		// Can only insert rows at column zero.
		if (isInsertRow) {
			return columnIndex==0 && 0<=rowIndex && rowIndex <= getRowCount();
		}
		
		// Otherwise must set value in an existing row.
		if (rowIndex < 0 || rowIndex >= getRowCount()) {
			return false;
		}

		List<TableCell> row = rowList.get(rowIndex);
		if (isInsertColumn) {
			return 0<=columnIndex && columnIndex <= row.size();
		} else {
			return 0<=columnIndex && columnIndex <= row.size() && columnIndex < columnCount;
		}
	}
	
	/**
	 * Get the component that will be modified by insertion at the given location.
	 * @param rowIndex the row index.
	 * @param columnIndex the column index.
	 * @param isInsertRow boolean flag to check for whether can insert row.
	 * @param isInsertColumn boolean flag to check for whether can insert column.
	 * @return the component whose children will change by this insertion.
	 */
	public AbstractComponent getModifiedComponent(int rowIndex, int columnIndex, boolean isInsertRow, boolean isInsertColumn) {
		assert canSetValue(rowIndex, columnIndex, isInsertRow, isInsertColumn);
		assert type != TableType.ZERO_DIMENSIONAL;
		
		if (rowIndex == rowList.size()) {
			return rootComponent;
		}
		
		if (columnIndex == rowList.get(rowIndex).size()) {
			return rowList.get(rowIndex).get(columnIndex - 1).getParent();
		}
		
		// Otherwise, we'll modify the parent of a cell
		return rowList.get(rowIndex).get(columnIndex).getParent();

	}

	/**
	 * Gets the type of tabular structure.
	 * 
	 * @return the type of tabular structure.
	 */
	public TableType getType() {
		return type;
	}
	
	/**
	 * Determine if this component is displayable as a cell within a table.
	 * @param comp the abstract component.
	 * @return true if the component can be displayed in a table, false if it should be skipped.
	 */
	private boolean canEmbedInTable(AbstractComponent comp) {
		return TableViewPolicy.canEmbedInTable(comp);
	}

	private ArrayList<TableRow> rowList;

	private void buildTable () {
		rowList = new ArrayList<TableRow>();

		if (type == TableType.ZERO_DIMENSIONAL) {

			TableRow row = new TableRow();
			row.add(new TableCell(rootComponent, null, 0));
			rowList.add(row);

		} else if (type == TableType.ONE_DIMENSIONAL) {

			int index = 0;
			for (AbstractComponent child : rootComponent.getComponents()) {
				if (canEmbedInTable(child)) {
					TableRow row = new TableRow();
					row.setIndex(index);
					row.add(new TableCell(child, rootComponent, index));
					rowList.add(row);
				}
				index++;
			}

		} else if (type == TableType.TWO_DIMENSIONAL) { 

			int parentIndex = 0;
			for (AbstractComponent child : rootComponent.getComponents()) {
				int index = 0;
				TableRow row = new TableRow();
				row.setIndex(parentIndex);
				for (AbstractComponent grandChild : child.getComponents()) {
					if (canEmbedInTable(grandChild)) {
						row.add(new TableCell(grandChild, child, index));
					}
					index++;
				}
				if (!row.isEmpty()) rowList.add(row);
				parentIndex++;
			}

		} 
	}
	
	private class TableRow extends ArrayList<TableCell> {
		private static final long serialVersionUID = 6852306825313602935L;
		private int  index = -1;
		public  void setIndex(int index) { this.index=index; }
		public  int  getIndex()          { return index;     }
	}
	
	private class TableCell {
		AbstractComponent value;
		AbstractComponent parent;
		int               index;
		
		public TableCell(AbstractComponent value, AbstractComponent parent, int index) {
			this.value  = value;
			this.parent = parent;
			this.index  = index;
		}
		
		public AbstractComponent getValue() { return value; }
		public int               getIndex() { return index; }
		public AbstractComponent getParent(){ return parent;}
		
	}
	
}
