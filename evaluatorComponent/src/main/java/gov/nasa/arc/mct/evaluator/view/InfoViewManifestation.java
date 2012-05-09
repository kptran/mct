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
package gov.nasa.arc.mct.evaluator.view;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.components.FeedProvider.RenderingInfo;
import gov.nasa.arc.mct.evaluator.api.Evaluator;
import gov.nasa.arc.mct.evaluator.component.EvaluatorComponent;
import gov.nasa.arc.mct.gui.FeedView;
import gov.nasa.arc.mct.services.component.ViewInfo;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;

public class InfoViewManifestation extends FeedView {
	private static final long serialVersionUID = 1L;
	private static final ResourceBundle bundle = ResourceBundle.getBundle("Bundle"); 
	public static final String VIEW_NAME = bundle.getString("InfoViewRoleName");
	public ValueModel tableModel;
	private int valueType = 1;
	private JLabel valueLabel = new JLabel();
	private JTextField inputTestValue = new JTextField();
	private FeedProvider feed;
	private String f;
	private JRadioButton alphaValue;
	private JRadioButton testValue;
	private Border       componentBorder = null;

	public InfoViewManifestation(AbstractComponent component, ViewInfo info) {
		super(component, info);
		feed = component.getCapability(FeedProvider.class);
        f = feed.getSubscriptionId();
		
		if (getColor("border") != null) {
			componentBorder = BorderFactory.createLineBorder(getColor("border"));
			inputTestValue.setBorder(componentBorder);
		}
       
        buildGUI();
	}
	
	private Color getColor(String name) {
        return UIManager.getColor(name);        
    }
	
	@Override
	public List<FeedProvider> getVisibleFeedProviders() {
		// if this is identified as a performance hotspot, then cache this list and recreate when
		// children are added or deleted
		List<AbstractComponent> components = getManifestedComponent().getComponents();
		
		List<FeedProvider> providers = new ArrayList<FeedProvider>(components.size() + 1);
		FeedProvider fp = getFeedProvider(getManifestedComponent());
		if (fp != null) {
			providers.add(fp);
		}
		for (AbstractComponent component : components) {
			fp = getFeedProvider(component);
			if (fp != null) {
				providers.add(fp);
			}
		}
		return providers;
	}

	@Override
	public void synchronizeTime(Map<String, List<Map<String, String>>> data,
			long syncTime) {
		updateFromFeed(data);
	}
	
	@Override
	public void updateFromFeed(Map<String, List<Map<String, String>>> data) {
		if (!data.isEmpty()) {
			tableModel.updateFromFeed(data);
		}
		
		//Provide alpha value for radio button
        List<Map<String,String>> feedDataForThisComponentCastAsMap = data.get(f);
        if (feedDataForThisComponentCastAsMap == null || feedDataForThisComponentCastAsMap.isEmpty()) {
            return;
        }
        Map<String, String> entry = feedDataForThisComponentCastAsMap.get( feedDataForThisComponentCastAsMap.size() -1 );         

        RenderingInfo ri =  feed.getRenderingInfo(entry); 
        valueLabel.setText(ri.getValueText());
        valueLabel.setForeground(ri.getValueColor()); 
        if (!valueLabel.isVisible()) valueLabel.setVisible(true);	
	}
	
	@Override
	public void updateMonitoredGUI() {
		tableModel.clearModel();
	}
	
	private void buildGUI() {
		// show the language, code, the table, and the evaluated value
		setLayout(new GridBagLayout());
		
		//Buttons to choose alpha and test values
		alphaValue = new JRadioButton();
        alphaValue.setText("Alpha Value: ");
        alphaValue.setActionCommand("1");
        alphaValue.setSelected(true);
       
        testValue = new JRadioButton();
        testValue.setText("Test Value: ");
        testValue.setActionCommand("2");
        
        ButtonGroup selectValue = new ButtonGroup();       
        selectValue.add(alphaValue);
        selectValue.add(testValue);

        alphaValue.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				valueType = 1;
			}	
        });
        testValue.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				valueType = 2;
			}
        });
        
        //Input field for test value
        inputTestValue.getAccessibleContext().setAccessibleName(testValue.getText());
        inputTestValue.setEditable(true);
        inputTestValue.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent arg0) {
				valueType = 2;
				testValue.setSelected(true);	
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
			}
        });
		
		//Table of evaluators associated with given component
		JTable valueTable = new JTable(tableModel = new ValueModel()) {
			private static final long serialVersionUID = 1L;

			@Override
			public String getToolTipText(MouseEvent event) {
				String tip = null;
		        java.awt.Point p = event.getPoint();
		        int colIndex = columnAtPoint(p);
		        int realColumnIndex = convertColumnIndexToModel(colIndex);

		        if (realColumnIndex == 2) { 
		            tip = bundle.getString("ValuesToolTip");
		        } 
		        return tip;

			}
		};
		valueTable.setAutoCreateColumnsFromModel(true);
		valueTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		valueTable.setRowSelectionAllowed(false);
		JScrollPane tableScrollPane = new JScrollPane(valueTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		if (componentBorder != null) {
			tableScrollPane.setBorder(componentBorder);
		}
		
		//add buttons to panel
		GridBagConstraints alphaConstraints = getConstraints(0,0);
        alphaConstraints.gridwidth = 1;
        alphaConstraints.insets = new Insets(3,5,2,5);
        alphaConstraints.weighty = 0;
        alphaConstraints.fill = GridBagConstraints.NONE;
        alphaConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        add(alphaValue, alphaConstraints);
       
        GridBagConstraints valueConstraints = getConstraints(1,0);
        valueConstraints.gridwidth = 1;
        valueConstraints.insets = new Insets(6,5,2,5);
        valueConstraints.weighty = 0;
        valueConstraints.fill = GridBagConstraints.HORIZONTAL;
        valueConstraints.fill = GridBagConstraints.LAST_LINE_START;
        add(valueLabel, valueConstraints);
       
        GridBagConstraints testConstraints = getConstraints(0,1);
        testConstraints.gridwidth = 1;
        testConstraints.insets = new Insets(0,5,5,5);
        testConstraints.weighty = 0;
        testConstraints.fill = GridBagConstraints.NONE;
        testConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        add(testValue, testConstraints);
       
        GridBagConstraints inputConstraints = getConstraints(1,1);
        inputConstraints.gridwidth = 1;
        inputConstraints.insets = new Insets(0,5,5,5);
        inputConstraints.weighty = 0;
        inputConstraints.fill = GridBagConstraints.HORIZONTAL;
        inputConstraints.anchor = GridBagConstraints.LINE_START;
        add(inputTestValue, inputConstraints);
		
		//add evaluators table to panel
		GridBagConstraints tableConstraints = getConstraints(0,2);
		tableConstraints.gridwidth = 2;
		tableConstraints.insets = new Insets(0,5,3,5);
		
		tableConstraints.weighty = 1;
		tableConstraints.weightx = 1;
		tableConstraints.fill = GridBagConstraints.BOTH;
		add(tableScrollPane, tableConstraints);
	}
	
	private GridBagConstraints getConstraints(int x, int y) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.weightx = x == 0 ? 0 : 1;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(y%2==0?5:0, 5, 0, x==1?5:0);
		if (x == 0) {
			gbc.fill = GridBagConstraints.NONE;
		}
		
		gbc.anchor = GridBagConstraints.NORTHWEST;
		
		return gbc;
	}
	
	protected ArrayList<Evaluator> getEvaluatorList(){
        ArrayList<Evaluator> eList = new ArrayList<Evaluator>();
        //Check for imars enumerators
        Evaluator imars = getManifestedComponent().getCapability(Evaluator.class);
        if (imars != null){
        	eList.add(imars);
        }
        //Check for user created enumerators
        Collection<AbstractComponent> parents = getManifestedComponent().getReferencingComponents();
        for (AbstractComponent p : parents){
            Evaluator e = p.getCapability(Evaluator.class);
            if (e!= null && p.getClass().equals(EvaluatorComponent.class)) {
                eList.add(e);
            }
        }
        return eList;
    }
	
	// the data structures could be optimized in this model (perhaps using a Map instead of parallel lists), 
	// but since this is not expected to be used
	// heavily a simple implementation was used
	
	class ValueModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		private List<String> displayValues = new ArrayList<String>();
		private List<String> code = new ArrayList<String>();
		private List<String> name = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		private List<List<String>> lists = Arrays.asList(name,code, displayValues);
		private static final int FEED_VALUE_COLUMN = 2;
		
		public ArrayList<Evaluator> eList = getEvaluatorList();
		private final List<String> columnNames = new ArrayList<String>();
		
		ValueModel() {
			columnNames.add(bundle.getString("EvaluatorNameLabel"));
			columnNames.add(bundle.getString("ExpressionsLabel"));
			columnNames.add(bundle.getString("ResultValueLabel"));
			loadFeeds();
		}

		@Override
		public int getColumnCount() {
			return columnNames.size();
		}

		@Override
		public int getRowCount() {
			return eList.size();
			//return getVisibleFeedProviders().size();
		}
		
		public void updateFromFeed(Map<String, List<Map<String, String>>> data) {
			if (!eList.isEmpty()) {
				// update the live values from the feed values
				String value = "";		
				
				List<Map<String, String>> values = data.get(f);
				if (values != null && !values.isEmpty()) {
					value = values.get(values.size()-1).get(FeedProvider.NORMALIZED_VALUE_KEY);
				}
				
				for (int i = 0; i < eList.size(); i++){
					String lastValue = displayValues.set(i, value);
					if (!value.equals(lastValue)) {
						fireTableCellUpdated(i, FEED_VALUE_COLUMN);
					}		
				}
				updateOutput(data);
			}
		}
		
		private void updateOutput(Map<String, List<Map<String, String>>> originalData) {			
			
			Map<String, List<Map<String, String>>> data = new HashMap<String, List<Map<String,String>>>();
			String val = "";

			if (valueType == 1) {
				val = displayValues.get(0);
			}
			if (valueType == 2) {
				val = inputTestValue.getText();
			}
	
			Evaluator e;

			for (int i = 0; i < eList.size(); i++){
				e = eList.get(i);
				Map<String,String> values = new HashMap<String,String>();
				List<Map<String,String>> origValues = originalData.get(f);
				if (origValues != null && !origValues.isEmpty()) {
					values.putAll(origValues.get(origValues.size()-1));
					values.put(FeedProvider.NORMALIZED_VALUE_KEY, val);
					RenderingInfo originalInfo = RenderingInfo.valueOf(values.get(FeedProvider.NORMALIZED_RENDERING_INFO));
					RenderingInfo ri = new RenderingInfo(
							val,
							originalInfo.getValueColor(),
							originalInfo.getStatusText(),
							originalInfo.getStatusColor(),
							true
					); 
					values.put(FeedProvider.NORMALIZED_RENDERING_INFO, ri.toString());
					data.put(f, Collections.singletonList(values));
				}

				if (e != null) {
					RenderingInfo ri = e.evaluate(data, getVisibleFeedProviders());
					setValueAt(ri == null ? "" : ri.getValueText(), i, FEED_VALUE_COLUMN);
				} else {
					setValueAt("No executor found", i, FEED_VALUE_COLUMN);
				}
			}
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}
		
		@Override
		public String getColumnName(int column) {
			return columnNames.get(column);
		}
		
		private void loadFeeds() {
			Evaluator e;
			for (int i = 0; i < eList.size(); i++){
				e = eList.get(i);
				name.add(e.getDisplayName());
				displayValues.add(null);
				code.add(e.getCode());
			}
		}
		
		public void clearModel() {
			displayValues.clear();
			code.clear();
			name.clear();
			loadFeeds();
			fireTableDataChanged();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			List<String> list = getListForColumn(columnIndex);
			return list.get(rowIndex);
		}
		
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			assert columnIndex == FEED_VALUE_COLUMN : "only result column should be editable";
			displayValues.set(rowIndex, aValue.toString());
		}
		
		private List<String> getListForColumn(int col) {
			return lists.get(col);
		}
		
	}
	
}
