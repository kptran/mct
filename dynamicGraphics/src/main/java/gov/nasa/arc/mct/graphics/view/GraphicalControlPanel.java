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
import gov.nasa.arc.mct.graphics.brush.Outline;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

/**
 * A control panel for editing properties of Dynamic Graphics. Works hand-in-hand 
 * with GraphicalSettings 
 * 
 * @author vwoeltje
 *
 */
public class GraphicalControlPanel extends JPanel implements ActionListener {
    private static ResourceBundle bundle = ResourceBundle.getBundle("GraphicsResourceBundle");
	private static final long serialVersionUID = -3596274745885119104L;
	
	private GraphicalSettings   settings;
	
	private JPanel              mappingPanel;
	private JComboBox           enumerationBox;
	private JComboBox           mappingBox;
	private JTextField          minField;
	private JTextField          maxField;
	private JLabel              intervalLabel;
	private Map<String, JPanel> mappingPanelNames = new LinkedHashMap<String, JPanel>();
	private Map<String, Color>  mappingColors     = new LinkedHashMap<String, Color> ();
	
	private static final String    ADD_MAPPING_BUTTON = "AddMappingButton";
	private static final String    REMOVE_BUTTON      = "RemoveMappingButton";
	private static final String    NEXT_COLOR         = "NextColor";
	private static final String    NEXT_EVALUATION    = "NextEvaluation";
	
	private static final Dimension BUTTON_DIMENSION = new Dimension (20, 20);
	private static final Dimension COMBO_BOX_DIMENSION = new Dimension (70, 20);
	private static final Dimension WIDE_COMBO_BOX_DIMENSION = new Dimension (160, 20);

	/**
	 * Construct a new control panel to manage the settings of a GraphicalManifestation
	 * @param manifestation the graphical view of the component
	 */
	public GraphicalControlPanel(GraphicalManifestation manifestation) {
		JLabel label, dirLabel, leftLabel, rightLabel;
		settings = manifestation.getSettings();
				
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
				
		/* Shape subpanel */
		label = makeLabel(bundle.getString("Shape_Label"), 
						  makeComboBox(GraphicalSettings.GRAPHICAL_SHAPE,
						    	  settings.getSupportedShapes()));		
		addSubPanel(label, label.getLabelFor());
		
		/* Background subpanel */
		label = makeLabel(bundle.getString("Background_Label"),
						  makeComboBox(GraphicalSettings.GRAPHICAL_BACKGROUND_COLOR,
								  settings.getSupportedColors()));
		addSubPanel(label, label.getLabelFor());
		
		/* Outline subpanel */
		label = makeLabel(bundle.getString("Outline_Label"),
						  makeComboBox(GraphicalSettings.GRAPHICAL_OUTLINE_COLOR,
								  settings.getSupportedColors()));
		addSubPanel(label, label.getLabelFor());

		
		/* Fill subpanel */
		label = makeLabel(bundle.getString("Foreground_Label"),
						  makeComboBox(GraphicalSettings.GRAPHICAL_FOREGROUND_COLOR,
									   settings.getSupportedColors()));
		dirLabel = makeLabel(bundle.getString("Direction_Label") + ":",
				  			 makeComboBox(GraphicalSettings.GRAPHICAL_FOREGROUND_FILL,
				  					      settings.getSupportedFills()));
		minField = makeTextField(GraphicalSettings.GRAPHICAL_FOREGROUND_MIN);
		maxField = makeTextField(GraphicalSettings.GRAPHICAL_FOREGROUND_MAX);
		leftLabel  = makeLabel(bundle.getString("Min_Label") + ":", minField);
		rightLabel = makeLabel(bundle.getString("Max_Label") + ":", maxField);
		intervalLabel = new JLabel();
		addSubPanel(label, label.getLabelFor(),
				    dirLabel, dirLabel.getLabelFor(),
				    makeEqualPair(leftLabel, rightLabel),
				    makeEqualPair(leftLabel.getLabelFor(), rightLabel.getLabelFor()),
				    intervalLabel);
	
		/* Evaluator subpanel */
		label        = makeLabel(bundle.getString("Evaluator_Label"),
				                 makeComboBox(GraphicalSettings.GRAPHICAL_EVALUATOR,
				                              settings.getSupportedEvaluators()));
		enumerationBox = makeComboBox(NEXT_EVALUATION, settings.getSupportedEnumerations());
		mappingBox   = makeComboBox(NEXT_COLOR, settings.getSupportedColors());
		mappingPanel = addSubPanel(WIDE_COMBO_BOX_DIMENSION,
				label, label.getLabelFor(),
				makeInequalPair(makeEqualPair(makeLabel(bundle.getString("Value_Label") + ":", enumerationBox), 
						                      makeLabel(bundle.getString("Color_Label") + ":", mappingBox)), 
								new JPanel(), 
								BUTTON_DIMENSION),						
				makeInequalPair(makeEqualPair(enumerationBox, mappingBox), 
								makeButton(ADD_MAPPING_BUTTON, "+"), 
								BUTTON_DIMENSION),
				new JPanel());

		
		/* Load the existing Evaluation->Color mappings */
		Object map = settings.getSetting(GraphicalSettings.GRAPHICAL_EVALUATOR_MAP);
		if (map instanceof Map) loadMappings((Map) map);
		

	}
	
	private JLabel makeLabel(String text, Component labelFor) {
		JLabel label = new JLabel(text);
		label.setLabelFor(labelFor);
		return label;
	}

	/**
	 * Creates a button, with this control panel designated as a listener.
	 * @param name the name of the button
	 * @param text the text to display on the button
	 * @return the created button
	 */
	private JButton makeButton(String name, String text) {
		JButton button = new JButton(text);
		button.setName(name);
		button.setMargin(new Insets(0,0,0,0));
		button.addActionListener(this);
		return button;
	}
	
	/**
	 * Create a text field, with this control panel designated as a listener.
	 * Additionally, settings will be consulted for an initial value.
	 * @param name the name for the JTextField
	 * @return the JTextField created
	 */
	private JTextField makeTextField(String name) {
		JTextField field = new JTextField();
		field.setName(name);		

		Object selected = settings.getSetting(name);
		if (selected != null && selected instanceof String) {
			field.setText((String) selected);
		}
		field.addActionListener(this);
		
		return field;
	}
	
	/**
	 * Create a combo box for this collection of items. Each item will be rendered 
	 * according to its type (Shape, Color, String, and AbstractComponent are supported)
	 * The control panel will also be added as a listener for this JComboBox.
	 * Additionally, settings will be consulted for an initial value.
	 * @param name the name to give the resulting JComboBox
	 * @param items the items to place in the combo box
	 * @return the JComboBox
	 */
	private JComboBox makeComboBox(String name, Collection<?> items) {
		JComboBox box = new JComboBox (items.toArray());
		box.setName(name);
		
		box.setRenderer(new ListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list,
					Object obj, int arg2, boolean arg3, boolean arg4) {		
				if (obj instanceof Shape)  return new ShapePanel((Shape) obj);
				if (obj instanceof Color)  return new ColorPanel((Color) obj);
				if (obj instanceof String) return new JLabel((String) obj);
				if (obj instanceof AbstractComponent) return new JLabel(((AbstractComponent) obj).getDisplayName());
				return new JPanel();
			}			
		});
		
		Object selected = settings.getSetting(name);
		if (selected != null) 		box.setSelectedItem(selected);
		else if (items.size() >0)   box.setSelectedIndex(0);
		box.addActionListener(this);
		
		return box;
	}
	
	/**
	 * Load a set of existing Evaluation->Color mappings, and display them in the Control Panel
	 * @param map
	 */
	private void loadMappings(Map map) {
		for (Object key : map.keySet()) {
			if (key instanceof String && map.get(key) instanceof Color) {
				addMapping ((String) key, (Color) map.get(key));
			}
		}
	}
	
	/**
	 * Associate a given evaluation with a given color
	 * @param key the evaluation to watch for
	 * @param value the color to associate with the evaluation
	 * @return the JPanel that shows this mapping
	 */
	private JPanel addMapping(String key, Color value) {		
		String buttonName = REMOVE_BUTTON + key;
		Color  background = mappingPanel.getBackground().darker(); 
		
		JLabel keyLabel   = new JLabel(key);	
		
		JPanel colorPanel = new JPanel();
		colorPanel.setBackground(value);
		JPanel pairPanel = makeEqualPair(keyLabel, colorPanel);
		pairPanel.setBorder(BorderFactory.createLineBorder(background, 2));
		pairPanel.setBackground(background);
		JPanel panel = makeInequalPair(
				pairPanel,
				makeButton(buttonName, "-"),
				BUTTON_DIMENSION
				);
		
		panel.setAlignmentX(LEFT_ALIGNMENT);
		panel.setMinimumSize(WIDE_COMBO_BOX_DIMENSION);
		panel.setPreferredSize(WIDE_COMBO_BOX_DIMENSION);
		panel.setMaximumSize(WIDE_COMBO_BOX_DIMENSION);	
		
		mappingPanel.add(panel);
		mappingPanel.revalidate();
		
		if (mappingPanelNames.containsKey(key)) {
			removeMapping(key);
		}
		mappingPanelNames.put(key, panel);
		mappingColors.put(key, value);
		
		return panel;
	}
	
	/**
	 * Remove the Evaluation->Color association for a given key.
	 * @param key the Evaluation to disassociate from its color
	 */
	private void removeMapping(String key) {
		JPanel panel = mappingPanelNames.get(key);
		if (panel != null) {
			mappingPanelNames.remove(key);
			mappingColors.remove(key);
			mappingPanel.remove(panel);
			mappingPanel.revalidate();
		}		
	}
	
	/**
	 * Creates and adds a new sub panel (vertical set of components) to 
	 * this Control Panel. Adds separators as appropriate.
	 * Note that this will modify the preferredSize of all components in the 
	 * list (using COMBO_BOX_DIMENSION as a standard size)
	 * @param comps the components to place in this subpanel
	 * @return the panel that was added
	 */
	private JPanel addSubPanel(Component... comps) {
		return addSubPanel (COMBO_BOX_DIMENSION, comps);
	}
	
	/**
	 * Creates and adds a new sub panel (vertical set of components) to 
	 * this Control Panel. Adds separators as appropriate.
	 * Note that this will modify the preferredSize of all components in the 
	 * list (using the specified Dimension)
	 * @param standardDimension the standard size for all components in this subpanel
	 * @param comps the components to place in this subpanel
	 * @return the panel that was added
	 */
	private JPanel addSubPanel(Dimension standardDimension, Component... comps) {
		
		if (getComponentCount() > 0) {			
			JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
			separator.setMaximumSize(new Dimension(4, Integer.MAX_VALUE));
			add (separator);			
		}
		
		add (Box.createHorizontalStrut(8));

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		for (Component c : comps) {
			if (c instanceof JComponent) {
				((JComponent) c).setAlignmentX(LEFT_ALIGNMENT);
				c.setMinimumSize(standardDimension);
				c.setPreferredSize(standardDimension);
				c.setMaximumSize(standardDimension);
			}
			panel.add(c);			
		}

		panel.setAlignmentY(TOP_ALIGNMENT);
		panel.setAlignmentX(LEFT_ALIGNMENT);
		add(panel);
		
		add (Box.createHorizontalStrut(8));

		return panel;
	}
	
	/**
	 * Makes a JPanel in which two components are next to each other horizontally, 
	 * and where each takes up equal space.
	 * @param left the component on the left
	 * @param right the component on the right
	 * @return a JPanel containing the above components
	 */
	private JPanel makeEqualPair(Component left, Component right) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1,2,4,0));		
		panel.add(left);
		panel.add(right);
		return panel;
	}

	/**
	 * Makes a JPanel where the component on the left expands to fill available 
	 * space, while the component on the right is of a fixed size.
	 * Note that right's preferredSize will be modified by this method.
	 * @param left the component on the left, which expands
	 * @param right the component on the right, which will stay the same size
	 * @param rightDim the dimension for the right-hand component
	 * @return a JPanel containing the specified components
	 */
	private JPanel makeInequalPair(Component left, Component right, Dimension rightDim) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(4, 0));
		panel.add(left, BorderLayout.CENTER);
		right.setPreferredSize(rightDim);
		panel.add(right, BorderLayout.EAST);
		return panel;
	}

	@Override
	public void actionPerformed(ActionEvent event) {		
		JComponent comp = (JComponent) event.getSource();		
		
		/* name is presumably the setting name, but may be ADD_MAPPING_BUTTON 
		 * or REMOVE_BUTTON, which are handled separately. Value is the incoming 
		 * value which will be used to update settings. */ 
		Object value = null;
		String name  = comp.getName();
		
		/* Add a new Evaluation->Color mapping */
		if (name.equals(ADD_MAPPING_BUTTON)) {
			String nextEvaluation = enumerationBox.getSelectedItem() == null ? "" : enumerationBox.getSelectedItem().toString();
			Color  nextColor      = (Color) mappingBox.getSelectedItem();
			if (!nextEvaluation.isEmpty()) addMapping(nextEvaluation, nextColor);
			name  = GraphicalSettings.GRAPHICAL_EVALUATOR_MAP; // Set name/value
			value = mappingColors;                             // for settings update
		}
		
		/* Remove an existing Evaluation->Color mapping */
		if (name.startsWith(REMOVE_BUTTON)) {
			String key = name.replaceFirst(REMOVE_BUTTON, "");		
			removeMapping(key);
			name  = GraphicalSettings.GRAPHICAL_EVALUATOR_MAP; // Set name/value
			value = mappingColors;                             // for settings update
		}
		
		/* Pull the value from the ComboBox */
		if (comp instanceof JComboBox) {
			value = ((JComboBox) comp).getSelectedItem();
		}
		
		/* Get the entry from the text field. Note that we presume 
		 * text fields are for Doubles in this context */
		if (comp instanceof JTextField) try {
			Double minValue = Double.parseDouble(minField.getText());
			Double maxValue = Double.parseDouble(maxField.getText());
			if (minValue < maxValue) {
				settings.setByObject(minField.getName(), minField.getText());
				settings.setByObject(maxField.getName(), maxField.getText());
				settings.updateManifestation();
				intervalLabel.setText("");
			}  else {
				// Restore fields to their stored settings - min & max not valid
				minField.setText((String) settings.getSetting(minField.getName()));
				maxField.setText((String) settings.getSetting(maxField.getName()));
				intervalLabel.setText("<html><center>" + bundle.getString("MinMax_Error") + "</center></html>");
				// wrapping in html ensures word wrap
				intervalLabel.setFont(intervalLabel.getFont().deriveFont(9.0f));
				intervalLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0)); // For padding
			}		
		} catch (NumberFormatException nfe) {
			value = settings.getSetting(name);
			((JTextField) comp).setText((String) value);
		}
		
		/* Update the settings with the incoming change */
		if (value != null && settings.isValidKey(name))  {
			settings.setByObject(name, value);	
			settings.updateManifestation();
		}
		
		/* Update enumeration options if evaluator changed */
		if (comp.getName().equals(GraphicalSettings.GRAPHICAL_EVALUATOR)) {
			if (enumerationBox != null) {
				enumerationBox.removeAllItems();
				for (Object o : settings.getSupportedEnumerations())
					enumerationBox.addItem(o);
			}
		}
				
	}

	/**
	 * A JPanel that draws a shape, for shape dropdowns.
	 * @author vwoeltje
	 */
	private static class ShapePanel extends JPanel {
		private static final long serialVersionUID = -9099499671031757612L;
		
		private static final Outline SHAPE_OUTLINE = new Outline (Color.BLACK);
		private Shape shape;
		
		public ShapePanel(Shape s) {
			shape = s;
			setBackground(Color.WHITE);
			setPreferredSize(COMBO_BOX_DIMENSION);			
		}
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Rectangle b = getBounds().getBounds();
			b.x = 2;
			b.y = 2;
			b.width  -= 5;
			b.height -= 4;
			SHAPE_OUTLINE.draw(shape, g, b);
		}		
	}
	
	/**
	 * A JPanel that draws a color, for color dropdowns.
	 * @author vwoeltje	 
	 */
	private static class ColorPanel extends JPanel {
		private static final long serialVersionUID = -4129432361356154082L;

		Color color;
		public ColorPanel(Color c) {
			color = c;
			setBackground(c);
			this.setPreferredSize(COMBO_BOX_DIMENSION);			
		}
		
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(color);
			g.fillRect(0, 0, getWidth(), getHeight());
		}

		
	}
}
