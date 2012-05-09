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
package gov.nasa.arc.mct.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common standard combo box colors definitions and helper utility.
 *
 */
public class StandardComboBoxColors {

    private static final Logger logger = LoggerFactory.getLogger(StandardComboBoxColors.class);
    private Map<String, Color> colorMap;
    private Map<String, String> defaultSettingsMap;
    
    /** Default combo box dimension. */
    public static final Dimension COMBO_BOX_DIMENSION = new Dimension(100, 20);
    
    /** Default background color name. */
    public static final String  DEFAULT_BACKGROUND_COLOR = "ColorBG"; 
    
    /** Button background color. */
    public static final String  BACKGROUND_COLOR = "ButtonBGColor";
    
    /** Button label foreground color. */
    public static final String  FOREGROUND_COLOR = "ButtonLabelColor";
    
    /** Text label color. */
    public static final String  TEXT_LABEL_COLOR = "TextColor";
    
    /** Default foreground color name. */
    public static final String  DEFAULT_FOREGROUND_COLOR   = "ColorFG";
    
    /** Default text label color. */
    public static final String  DEFAULT_TEXT_LABEL_COLOR = "Color1";
    
    /** Default executables button label text. */
    public static final String  EXEC_BUTTON_LABEL_TEXT = "LabelText";
    
    /**
     * Initializes the standard combo box colors maps.
     */
    public StandardComboBoxColors() {
        initializeColorMap();
        initializeDefaultSettingsMap();
        
        logger.debug("new Color(UIManager.getColor(\"Button.background\").getRGB())): {}", new Color(UIManager.getColor("Button.background").getRGB()));
        logger.debug("new Color(UIManager.getColor(\"Button.foreground\").getRGB())): {}", new Color(UIManager.getColor("Button.foreground").getRGB()));
    }
    
    /**
     * Creates a new button component.
     * @param text - the button text label to display.
     * @return JButton component
     */
    public JButton createJButton(String text) {
        JButton button = new JButton(text);
        return button;
    }
    
    /**
     * Gets the JComponent color properties.
     * @param comp - the JComponent.
     * @param colorProperty - The string color property.
     * @return Color - the color.
     */
    public Color getJComponentColorProps(JComponent comp, String colorProperty) {
        
        Color defaultColor = colorMap.get(DEFAULT_BACKGROUND_COLOR);
        
        if (colorProperty.equals("Button.background")) { 
            return comp.getBackground();
        } else if (colorProperty.equals("Button.foreground")) {
            return comp.getForeground();
        } 
        return defaultColor;
    }
    
    
    /**
     * Gets the supported colors.
     * @return Collection of color.
     */
    public Collection<Color> getSupportedColors() {
        return colorMap.values();
    }       
    
    /**
     * Initializes the common color map.
     */
    private void initializeColorMap() {
        colorMap = new LinkedHashMap<String, Color>();
        colorMap.put("Color0", Color.DARK_GRAY.darker());
        colorMap.put("Color1", Color.DARK_GRAY);
        colorMap.put("Color2", Color.BLUE);
        colorMap.put("Color3", Color.RED);
        colorMap.put("Color4", Color.green);
        colorMap.put("Color5", new Color(000, 128, 000));
        colorMap.put("Color6", new Color(032, 179, 170));
        colorMap.put("Color7", new Color(152, 251, 152));
        colorMap.put("Color8", new Color(255, 140, 000));
        colorMap.put("Color9", new Color(255, 000, 255));
        colorMap.put("Color10", new Color(255, 69, 000));
        colorMap.put("Color11", new Color(255, 215, 000));
        colorMap.put("Color12", new Color(047, 79, 79));
        colorMap.put("Color13", new Color(128, 128, 128));
        colorMap.put("Color14", new Color(100, 149, 237));
        colorMap.put("Color15", new Color(000, 49, 042));
        colorMap.put("Color16", new Color(000, 176, 176));
        colorMap.put("Color17", new Color(102, 051, 255));
        
        colorMap.put("ColorBG", new Color(UIManager.getColor("Button.background").getRGB()));
        colorMap.put("ColorFG", new Color(UIManager.getColor("Button.foreground").getRGB()));
    }
   
    private void initializeDefaultSettingsMap() {
        defaultSettingsMap = new HashMap<String, String>();      
        defaultSettingsMap.put(BACKGROUND_COLOR, DEFAULT_BACKGROUND_COLOR);
        defaultSettingsMap.put(FOREGROUND_COLOR, DEFAULT_FOREGROUND_COLOR);
        defaultSettingsMap.put(TEXT_LABEL_COLOR, DEFAULT_TEXT_LABEL_COLOR);
    }
   
    /**
     * Checks whether key is valid or not.
     * @param key - the key.
     * @return boolean - flag to check whether key is valid or not.
     */
    public boolean isValidKey(String key) {
        return defaultSettingsMap.containsKey(key);
    }
    
    /**
     * Gets the color map.
     * @return map of colors.
     */
    public Map<String, Color> getColorMap() {
        return this.colorMap;
    }
    
    /**
     * Gets the named object.
     * @param name - the name of the object to fetch.
     * @return the object to fetch; otherwise null.
     */
    public Object getNamedObject(String name) {
        if (colorMap.containsKey(name)) {
            return colorMap.get(name);
        }
        return null;
    }
    
    /**
     * Add key/value pair to color map.
     * @param colorKey - the color key.
     * @param colorValue - the color value.
     */
    public void addToColorMap(String colorKey, Color colorValue) {
        this.colorMap.put(colorKey, colorValue);
    }
    
    /**
     * Removes the key from the color map.
     * @param colorKey - the color key.
     */
    public void removeFromColorMap(String colorKey) {
        this.colorMap.remove(colorKey);
    }
    
    /**
     * Checks the color key from the default map.
     * @param colorKey - the color key.
     */
    public void containsColorMapKey(String colorKey) {
        this.colorMap.containsKey(colorKey);
    }
    
    /**
     * Checks the color value from the default map.
     * @param colorValue - the color value.
     */
    public void containsColorMapValue(Color colorValue) {
        this.colorMap.containsValue(colorValue);
    }
    
    /**
     * Gets the default setting map.
     * @return defaultSettingMap - the default setting map.
     */
    public Map<String, String> getDefaultSettingsMap() {
        return this.defaultSettingsMap;
    }
    
    /**
     * Adds to default color map settings.
     * @param key - the color key.
     * @param value - the color value.
     */
    public void addToDefaultSettingsMap(String key, String value) {
        this.defaultSettingsMap.put(key, value);
    }
    
    /**
     * Removes key from the default map settings.
     * @param key - the color key.
     */
    public void removeFromDefaultSettingsMap(String key) {
        this.defaultSettingsMap.remove(key);
    }
    
    /**
     * Checks whether the key is contained in the default map settings.
     * @param key - the color key.
     */
    public void containsDefaultSettingsMapKey(String key) {
        this.defaultSettingsMap.containsKey(key);
    }
    
    /**
     * Checks whether the value is contained in the default map settings. 
     * @param value - the color value.
     */
    public void containsDefaultSettingsMapValue(String value) {
        this.defaultSettingsMap.containsValue(value);
    }
    
    /**
     * Color panel layout.
     *
     */
    public static class ColorPanel extends JPanel {
        private static final long serialVersionUID = -4129432361356154082L;

        /** The color instance. */
        Color color;
        
        /**
         * Initializes a color panel.
         * @param c - the color to set to.
         */
        public ColorPanel(Color c) {
            color = c;
            setBackground(c);
            this.setPreferredSize(COMBO_BOX_DIMENSION);         
        }
        
        /**
         * Paints the component.
         * @param g - Graphics.
         */
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(color);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
    
    /**
     * Implements the gridbag layout with designated constraints.
     * @param y - integer grid y-coordinates.
     * @param x - integer grid x-coordinates.
     * @return GridBagConstraints - gbc 
     */
    public GridBagConstraints getConstraints(int y, int x) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Insets i = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = i;
        
        return gbc;
    }
}
