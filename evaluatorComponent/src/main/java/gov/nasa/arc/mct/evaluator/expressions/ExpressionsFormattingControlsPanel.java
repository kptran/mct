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
package gov.nasa.arc.mct.evaluator.expressions;

import gov.nasa.arc.mct.components.AbstractComponent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * Expression formating control panel class.
 */
@SuppressWarnings("serial")
public class ExpressionsFormattingControlsPanel extends JPanel{
	private static final ResourceBundle bundle = ResourceBundle.getBundle("Enumerator");

	//Creation Panel
	private JButton addExpButton = null;
	private JButton deleteExpButton = null;
	private JButton addAboveButton = null;
	private JButton addBelowButton = null;
	
	//Expression Order Editing Panel
	private JButton upOneButton = null;
	private JButton downOneButton = null;
	private JButton upTopButton = null;
	private JButton downBottomButton = null;
	
	//Telemetry Association Editing Panel
	private JButton deleteTelemButton = null;
	
	private boolean listenersEnabled = true;
	
	private final ExpressionsViewManifestation managedExpression;
	
	/**
	 * Initialize the expressions format control panel.
	 * @param managedExpression the view manifestation.
	 */
	ExpressionsFormattingControlsPanel (ExpressionsViewManifestation managedExpression) {
		this.managedExpression = managedExpression;
		createExpressionsFormattingControlsPanel();
	}
	
	private void createExpressionsFormattingControlsPanel() {
		JPanel controlPanel = new JPanel (new GridBagLayout());
		JPanel creationPanel = createCreationPanel();
		JPanel orderingPanel = createOrderingPanel();
		JPanel telemetryPanel = createTelemetryPanel();
		
		//Creation Panel
		GridBagConstraints creationPanelConstraints = new GridBagConstraints();
		creationPanelConstraints.fill = GridBagConstraints.NONE;
		creationPanelConstraints.anchor = GridBagConstraints.NORTHWEST;
		creationPanelConstraints.weighty = 0;
		creationPanelConstraints.weightx = 1;
		creationPanelConstraints.insets = new Insets(3,7,3,7);
		creationPanelConstraints.gridx = 0;
		creationPanelConstraints.gridy = 0;
		controlPanel.add(creationPanel, creationPanelConstraints);

		//Ordering Panel
		GridBagConstraints orderingPanelConstraints = new GridBagConstraints();
		orderingPanelConstraints.fill = GridBagConstraints.NONE;
		orderingPanelConstraints.anchor = GridBagConstraints.NORTHWEST;
		orderingPanelConstraints.weighty = 0;
		orderingPanelConstraints.weightx = 1;
		orderingPanelConstraints.insets = new Insets(3,7,3,7);
		orderingPanelConstraints.gridx = 0;
		orderingPanelConstraints.gridy = 1;
		controlPanel.add(orderingPanel, orderingPanelConstraints);
			
		//Telemetry Panel
		GridBagConstraints telemetryPanelConstraints = new GridBagConstraints();
		telemetryPanelConstraints.fill = GridBagConstraints.NONE;
		telemetryPanelConstraints.anchor = GridBagConstraints.NORTHWEST;
		telemetryPanelConstraints.weighty = 1;
		telemetryPanelConstraints.weightx = 1;
		telemetryPanelConstraints.insets = new Insets(3,7,3,7);
		telemetryPanelConstraints.gridx = 0;
		telemetryPanelConstraints.gridy = 2;
		controlPanel.add(telemetryPanel, telemetryPanelConstraints);
		
		controlPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		
		this.setLayout(new GridLayout(1,0));
		this.add(controlPanel);
	}
	
	private JPanel createCreationPanel() {
		JPanel creationPanel = new JPanel();
		creationPanel.setLayout(new BorderLayout());
		
		//Add title
		creationPanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("CreateDeleteTitle")));
		
		JPanel creationInnerPanel = new JPanel();
		creationPanel.add(creationInnerPanel, BorderLayout.WEST);
		
		creationInnerPanel.setLayout(new GridBagLayout());
		
		//Creation controls
		addExpButton = new JButton(bundle.getString("AddExpressionTitle"));
		deleteExpButton = new JButton(bundle.getString("DeleteExpressionTitle"));
		addAboveButton = new JButton(bundle.getString("AddAboveTitle"));
		addBelowButton = new JButton(bundle.getString("AddBelowTitle"));
		
		addExpButton.setName(bundle.getString("AddExpressionTitle"));
		deleteExpButton.setName(bundle.getString("DeleteExpressionTitle"));
		addAboveButton.setName(bundle.getString("AddAboveTitle"));
		addBelowButton.setName(bundle.getString("AddBelowTitle"));
		
		addExpButton.setToolTipText(bundle.getString("AddExpressionToolTip"));
		deleteExpButton.setToolTipText(bundle.getString("DeleteExpressionToolTip"));
		addAboveButton.setToolTipText(bundle.getString("AddAboveToolTip"));
		addBelowButton.setToolTipText(bundle.getString("AddBelowToolTip"));
		
		addExpButton.setOpaque(false);
		deleteExpButton.setOpaque(false);
		addAboveButton.setOpaque(false);
		addBelowButton.setOpaque(false);
		
		addExpButton.setFocusPainted(false);
		deleteExpButton.setFocusPainted(false);
		addAboveButton.setFocusPainted(false);
		addBelowButton.setFocusPainted(false);
		
		addExpButton.setSize(200,200);
		addExpButton.setContentAreaFilled(true);
		deleteExpButton.setSize(200,200);
		deleteExpButton.setContentAreaFilled(true);
		addAboveButton.setSize(200,200);
		addAboveButton.setContentAreaFilled(true);
		addBelowButton.setSize(200,200);
		addBelowButton.setContentAreaFilled(true);
		
		addExpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (listenersEnabled){
					ExpressionsFormattingController.notifyExpressionAdded(new Expression(), managedExpression.getExpressions());
					managedExpression.getEnum().getData().setCode(managedExpression.getExpressions().toString());
					managedExpression.fireFocusPersist();
				}
			}
		});
		
		deleteExpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Expression selectedExpression = managedExpression.getSelectedExpression();
				if (listenersEnabled){
					ExpressionsFormattingController.notifyExpressionDeleted(selectedExpression, managedExpression.getExpressions());
					managedExpression.getEnum().getData().setCode(managedExpression.getExpressions().toString());
					managedExpression.fireFocusPersist();
				}
			}
		});
		
		addAboveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Expression selectedExpression = managedExpression.getSelectedExpression();
				if (listenersEnabled){
					ExpressionsFormattingController.notifyExpressionAddedAbove(new Expression(), selectedExpression, managedExpression.getExpressions());
					managedExpression.getEnum().getData().setCode(managedExpression.getExpressions().toString());
					managedExpression.fireFocusPersist();
				}
			}
		});
		
		addBelowButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Expression selectedExpression = managedExpression.getSelectedExpression();
				if (listenersEnabled){
					ExpressionsFormattingController.notifyExpressionAddedBelow(new Expression(), selectedExpression, managedExpression.getExpressions());
					managedExpression.fireFocusPersist();
				}
			}
		});
		
		GridBagConstraints addExpButtonConstraints = new GridBagConstraints();
		addExpButtonConstraints.fill = GridBagConstraints.NONE;
		addExpButtonConstraints.anchor = GridBagConstraints.LINE_START;
		addExpButtonConstraints.weighty = 1;
		addExpButtonConstraints.weightx = 0;
		addExpButtonConstraints.ipadx = 1;
		addExpButtonConstraints.gridx = 0;
		addExpButtonConstraints.gridy = 0;
		creationInnerPanel.add(addExpButton, addExpButtonConstraints);
		
		GridBagConstraints deleteExpButtonConstraints = new GridBagConstraints();
		deleteExpButtonConstraints.fill = GridBagConstraints.NONE;
		deleteExpButtonConstraints.anchor = GridBagConstraints.LINE_START;
		deleteExpButtonConstraints.weighty = 1;
		deleteExpButtonConstraints.weightx = 0;
		deleteExpButtonConstraints.ipadx = 1;
		deleteExpButtonConstraints.gridx = 1;
		deleteExpButtonConstraints.gridy = 0;
		creationInnerPanel.add(deleteExpButton, deleteExpButtonConstraints);
		
		GridBagConstraints addAboveButtonConstraints = new GridBagConstraints();
		addAboveButtonConstraints.fill = GridBagConstraints.NONE;
		addAboveButtonConstraints.anchor = GridBagConstraints.LINE_START;
		addAboveButtonConstraints.weighty = 1;
		addAboveButtonConstraints.weightx = 0;
		addAboveButtonConstraints.ipadx = 1;
		addAboveButtonConstraints.gridx = 2;
		addAboveButtonConstraints.gridy = 0;
		creationInnerPanel.add(addAboveButton, addAboveButtonConstraints);
		
		GridBagConstraints addBelowButtonConstraints = new GridBagConstraints();
		addBelowButtonConstraints.fill = GridBagConstraints.NONE;
		addBelowButtonConstraints.anchor = GridBagConstraints.LINE_START;
		addBelowButtonConstraints.weighty = 1;
		addBelowButtonConstraints.weightx = 1;
		addBelowButtonConstraints.ipadx = 1;
		addBelowButtonConstraints.gridx = 3;
		addBelowButtonConstraints.gridy = 0;
		creationInnerPanel.add(addBelowButton, addBelowButtonConstraints);
		
		return creationPanel;
	}
	

	
	private JPanel createOrderingPanel() {
		JPanel orderingPanel = new JPanel();
		orderingPanel.setLayout(new BorderLayout());
		
		//Add title
		orderingPanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("EditOrderTitle")));
		
		JPanel orderingInnerPanel = new JPanel();
		orderingPanel.add(orderingInnerPanel, BorderLayout.WEST);
		
		orderingInnerPanel.setLayout(new GridBagLayout());
		
		
		upOneButton = new JButton(bundle.getString("MoveUpOneTitle"));
		downOneButton = new JButton(bundle.getString("MoveDownOneTitle"));
		upTopButton = new JButton(bundle.getString("MoveToTopTitle"));
		downBottomButton = new JButton(bundle.getString("MoveToBottomTitle"));
		
		upOneButton.setName(bundle.getString("MoveUpOneTitle"));
		downOneButton.setName(bundle.getString("MoveDownOneTitle"));
		upTopButton.setName(bundle.getString("MoveToTopTitle"));
		downBottomButton.setName(bundle.getString("MoveToBottomTitle"));
		
		upOneButton.setToolTipText(bundle.getString("MoveUpOneToolTip"));
		downOneButton.setToolTipText(bundle.getString("MoveDownOneToolTip"));
		upTopButton.setToolTipText(bundle.getString("MoveToTopToolTip"));
		downBottomButton.setToolTipText(bundle.getString("MoveToBottomToolTip"));
		
		upOneButton.setOpaque(false);
		downOneButton.setOpaque(false);
		upTopButton.setOpaque(false);
		downBottomButton.setOpaque(false);
		
		upOneButton.setFocusPainted(false);
		downOneButton.setFocusPainted(false);
		upTopButton.setFocusPainted(false);
		downBottomButton.setFocusPainted(false);
		
		upOneButton.setSize(400,10);
		upOneButton.setContentAreaFilled(true);
		downOneButton.setSize(400,10);
		downOneButton.setContentAreaFilled(true);
		upTopButton.setSize(400,10);
		upTopButton.setContentAreaFilled(true);
		downBottomButton.setSize(400,10);
		downBottomButton.setContentAreaFilled(true);
		
		upOneButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Expression selectedExpression = managedExpression.getSelectedExpression();
				if (listenersEnabled){
					ExpressionsFormattingController.notifyMovedUpOne(selectedExpression, managedExpression.getExpressions());
					managedExpression.getEnum().getData().setCode(managedExpression.getExpressions().toString());
					managedExpression.fireFocusPersist();
				}
			}
		});
		
		downOneButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Expression selectedExpression = managedExpression.getSelectedExpression();
				if (listenersEnabled){
					ExpressionsFormattingController.notifyMovedDownOne(selectedExpression, managedExpression.getExpressions());
					managedExpression.getEnum().getData().setCode(managedExpression.getExpressions().toString());
					managedExpression.fireFocusPersist();
				}
			}
		});
		
		upTopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Expression selectedExpression = managedExpression.getSelectedExpression();
				if (listenersEnabled){
					ExpressionsFormattingController.notifyMoveToTop(selectedExpression, managedExpression.getExpressions());
					managedExpression.getEnum().getData().setCode(managedExpression.getExpressions().toString());
					managedExpression.fireFocusPersist();
				}
			}
		});
		
		downBottomButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Expression selectedExpression = managedExpression.getSelectedExpression();
				if (listenersEnabled){
					ExpressionsFormattingController.notifyMoveToBottom(selectedExpression, managedExpression.getExpressions());
					managedExpression.getEnum().getData().setCode(managedExpression.getExpressions().toString());
					managedExpression.fireFocusPersist();
				}
			}
		});
		
		GridBagConstraints upOneButtonConstraints = new GridBagConstraints();
		upOneButtonConstraints.fill = GridBagConstraints.NONE;
		upOneButtonConstraints.anchor = GridBagConstraints.LINE_START;
		upOneButtonConstraints.weighty = 1;
		upOneButtonConstraints.ipadx = 1;
		upOneButtonConstraints.gridx = 0;
		upOneButtonConstraints.gridy = 0;
		orderingInnerPanel.add(upOneButton, upOneButtonConstraints);
		
		GridBagConstraints downOneButtonConstraints = new GridBagConstraints();
		downOneButtonConstraints.fill = GridBagConstraints.NONE;
		downOneButtonConstraints.anchor = GridBagConstraints.LINE_START;
		downOneButtonConstraints.weighty = 1;
		downOneButtonConstraints.ipadx = 1;
		downOneButtonConstraints.gridx = 1;
		downOneButtonConstraints.gridy = 0;
		orderingInnerPanel.add(downOneButton, downOneButtonConstraints);
		
		GridBagConstraints upTopButtonConstraints = new GridBagConstraints();
		upTopButtonConstraints.fill = GridBagConstraints.NONE;
		upTopButtonConstraints.anchor = GridBagConstraints.LINE_START;
		upTopButtonConstraints.weighty = 1;
		upTopButtonConstraints.ipadx = 1;
		upTopButtonConstraints.gridx = 2;
		upTopButtonConstraints.gridy = 0;
		orderingInnerPanel.add(upTopButton, upTopButtonConstraints);
		
		GridBagConstraints downBottomButtonConstraints = new GridBagConstraints();
		downBottomButtonConstraints.fill = GridBagConstraints.NONE;
		downBottomButtonConstraints.anchor = GridBagConstraints.LINE_START;
		downBottomButtonConstraints.weighty = 1;
		downBottomButtonConstraints.ipadx = 1;
		downBottomButtonConstraints.gridx = 3;
		downBottomButtonConstraints.gridy = 0;
		orderingInnerPanel.add(downBottomButton, downBottomButtonConstraints);
		
		return orderingPanel;
	}
	
	private JPanel createTelemetryPanel(){
		JPanel telemetryPanel = new JPanel();
		telemetryPanel.setLayout(new BorderLayout());
		
		//Add title
		telemetryPanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("EditTelemetryTitle")));
		
		JPanel telemetryInnerPanel = new JPanel();
		telemetryPanel.add(telemetryInnerPanel, BorderLayout.WEST);
		
		telemetryInnerPanel.setLayout(new GridBagLayout());
		
		deleteTelemButton = new JButton(bundle.getString("RemoveTelemetryTitle"));
		JRadioButton PUI = new JRadioButton();
		PUI.setText("PUI");
		JRadioButton baseDisplay = new JRadioButton();
		baseDisplay.setText("Base Display Name");
		
		deleteTelemButton.setName(bundle.getString("RemoveTelemetryTitle"));
		
		deleteTelemButton.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AbstractComponent t = managedExpression.getSelectedTelemetry();
				if (t != null){
					ExpressionsFormattingController.notifyRemoveTelem(t, managedExpression.getTelemetry());
					managedExpression.getEnum().removeDelegateComponent(t);
					managedExpression.fireFocusPersist();
				}
			}	
		});
		
		GridBagConstraints deleteTelemButtonConstraints = new GridBagConstraints();
		deleteTelemButtonConstraints.fill = GridBagConstraints.NONE;
		deleteTelemButtonConstraints.anchor = GridBagConstraints.LINE_START;
		deleteTelemButtonConstraints.weighty = .25;
		deleteTelemButtonConstraints.ipadx = 1;
		deleteTelemButtonConstraints.gridx = 0;
		deleteTelemButtonConstraints.gridy = 0;
		telemetryInnerPanel.add(deleteTelemButton, deleteTelemButtonConstraints);
		
		return telemetryPanel;
	}
}
