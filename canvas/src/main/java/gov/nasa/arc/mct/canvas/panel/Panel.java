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
package gov.nasa.arc.mct.canvas.panel;

import gov.nasa.arc.mct.canvas.formatting.ControlAreaFormattingConstants;
import gov.nasa.arc.mct.canvas.formatting.ControlAreaFormattingConstants.PANEL_ZORDER;
import gov.nasa.arc.mct.canvas.layout.CanvasLayoutManager;
import gov.nasa.arc.mct.canvas.view.CanvasManifestation;
import gov.nasa.arc.mct.canvas.view.PanelFocusSelectionProvider;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.ExtendedProperties;
import gov.nasa.arc.mct.gui.MCTViewManifestationInfo;
import gov.nasa.arc.mct.gui.NamingContext;
import gov.nasa.arc.mct.gui.SelectionProvider;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.roles.events.PropertyChangeEvent;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.TextAttribute;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class Panel extends JPanel implements SelectionProvider, NamingContext {
    private static Color DEFAULT_COLOR = Color.LIGHT_GRAY;
    private static Insets TITLE_INSETS = new Insets(0, 5, 0, 0);
    private static final Logger LOGGER = LoggerFactory.getLogger(Panel.class);

    
    private JPanel titlePanel;
    private JLabel titleLabel;
    private JComponent titleManifestation;
    private final Rectangle titleBounds = new Rectangle(); // Bounds relative to the containing canvas manifestation
    private View wrappedManifestation;
    private PanelBorder panelBorder;
    private int panelId;
    private PanelFocusSelectionProvider panelSelectionProvider;
    private JScrollPane scrollPane;
    private boolean hasTitle;
    private final Rectangle iconBounds = new Rectangle(); // Bounds relative to the containing canvas manifestation
    private JComponent icon;
    private JPanel statusBar;

    
    public Panel(View manifestation, PanelFocusSelectionProvider panelSelectionProvider) {
        this(manifestation, -1, panelSelectionProvider);
    }
    
    public Panel(View manifestation, int panelId, PanelFocusSelectionProvider panelSelectionProvider) {
        setLayout(new BorderLayout());
        assert CanvasManifestation.getManifestationInfo(manifestation) != null : "manifestation info must be added";
        this.wrappedManifestation = manifestation;
        this.panelId = panelId;
        this.panelSelectionProvider = panelSelectionProvider;
        
        setBorder(BorderFactory.createLineBorder(DEFAULT_COLOR, 1));                        
        
        init();
        
        manifestation.setAutoscrolls(true);
        scrollPane = new JScrollPane(manifestation, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
                
        add(scrollPane, BorderLayout.CENTER);
        add(titlePanel, BorderLayout.NORTH);
        hideTitle(CanvasManifestation.getManifestationInfo(wrappedManifestation).hasTitlePanel());
        
        statusBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        add(statusBar, BorderLayout.SOUTH);
        statusBar.setVisible(false);
        addStatusWidgetsIfApplicable();
        
        this.wrappedManifestation.getSelectionProvider().addSelectionChangeListener(selectionListener);
    }
    
    public void setScrollColor(Color scrollColor, Color bgColor) {
        scrollPane.getHorizontalScrollBar().setUI( new FlatScrollBarUI(scrollColor,bgColor) );
        scrollPane.getVerticalScrollBar().setUI( new FlatScrollBarUI(scrollColor,bgColor) );   
        scrollPane.setBackground(bgColor);

    
    }
    
    public void setTitleBarColor(Color background, Color foreground) {
        if (background != null) {
            titlePanel.setBackground(background);
            titleManifestation.setBackground(background);
        }
        if (foreground != null) {
            titleLabel.setForeground(foreground);
            titleManifestation.setForeground(foreground); //...manifestation may not use this
        }
    }
    
    public void setDefaultBorderColor(Color borderColor) {
        panelBorder.setDefaultBorderColor(borderColor);
    }

    private void addStatusWidgetsIfApplicable() {
        List<? extends JComponent> statusWidgets = wrappedManifestation.getStatusWidgets();
        if (!statusWidgets.isEmpty()) {
            for (JComponent widget : statusWidgets) {
                statusBar.add(widget);
            }
            statusBar.setVisible(true);
        } else {
            statusBar.removeAll();
            statusBar.setVisible(false);
        }
    }
    
    private void setupTitlePanel() {
        MCTViewManifestationInfo manifestationInfo = CanvasManifestation.getManifestationInfo(this.wrappedManifestation);
        AbstractComponent wrappedComponent = wrappedManifestation.getManifestedComponent();
        icon = new TransferableIcon(wrappedComponent, new TransferableIcon.ViewTransferCallback() {
            @Override
            public List<View> getViewsToTransfer() {
                return Collections.singletonList(getWrappedManifestation());
            }
        });
        titleLabel = new JLabel();
        titleLabel.setOpaque(false);
        
        titlePanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = .01;
        titlePanel.add(icon);

        titleManifestation = wrappedComponent.getViewInfos(ViewType.TITLE).iterator().next().createView(wrappedComponent);
        titleManifestation.setBackground(DEFAULT_COLOR);      
        titlePanel.setBackground(DEFAULT_COLOR);
        setTitleFromManifestation(manifestationInfo);        
      

    }
    
    private void init() {
        MCTViewManifestationInfo manifestationInfo = CanvasManifestation.getManifestationInfo(this.wrappedManifestation);
        byte border = PanelBorder.ALL_BORDERS;
        String borderStr = manifestationInfo.getInfoProperty(ControlAreaFormattingConstants.PANEL_BORDER_PROPERTY);
        if (borderStr != null) {
            border = Byte.parseByte(borderStr);
        }
        int borderStyle = manifestationInfo.getBorderStyle();
        Color borderColor = manifestationInfo.getBorderColor();
        this.panelBorder = new PanelBorder(border);
        panelBorder.setBorderStyle(borderStyle);
        panelBorder.setBorderColor(borderColor);
        setBorder(this.panelBorder);
        setupTitlePanel();
    }
    
    public Rectangle marshalBound(Rectangle origBound) {
        Container c = getParent();
        if (c != null) {
            LayoutManager layoutManager = c.getLayout();
            assert layoutManager instanceof CanvasLayoutManager;
            origBound = CanvasLayoutManager.class.cast(layoutManager).marshalLocation(origBound);
        }
        return origBound;
    }
    
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        revalidate();
        MCTViewManifestationInfo manifestInfo = CanvasManifestation.getManifestationInfo(this.wrappedManifestation);
        manifestInfo.setStartPoint(new Point(x, y));
        manifestInfo.setDimension(new Dimension(width, height));
    }
    
    @Override
    public void setBounds(Rectangle r) {
        super.setBounds(r);
        revalidate();
        MCTViewManifestationInfo manifestInfo = CanvasManifestation.getManifestationInfo(this.wrappedManifestation);
        manifestInfo.setStartPoint(r.getLocation());
        manifestInfo.setDimension(r.getSize());
    }
    
    public void setTitleBounds() {
        if (titlePanel.getParent() == this && titlePanel.isVisible()) {
            titleBounds.setLocation(getX() + titlePanel.getX(), getY() + titlePanel.getY());
            titleBounds.setSize(titlePanel.getWidth(), titlePanel.getHeight());
            iconBounds.setLocation(getX() + icon.getX() + titlePanel.getX(), getY() + icon.getY() + titlePanel.getY());
            iconBounds.setSize(icon.getWidth(), icon.getHeight());
        } else {
            titleBounds.setLocation(0, 0);
            titleBounds.setSize(0, 0);            
            iconBounds.setLocation(0, 0);
            iconBounds.setSize(0, 0);
        }
    }
    
    public void setId(int panelId) {
        this.panelId = panelId;
    }
    
    public int getId() {
        assert panelId > 0 : "Panel ID must be set.";
        return panelId;
    }
    
    public void addPanelBorder(byte newBorderState) {
        this.panelBorder.addBorderState(newBorderState);
        MCTViewManifestationInfo manifestInfo = CanvasManifestation.getManifestationInfo(this.wrappedManifestation);
        manifestInfo.addInfoProperty(ControlAreaFormattingConstants.PANEL_BORDER_PROPERTY, String.valueOf(this.panelBorder.getBorders()));
        setBorder(null);
        setBorder(panelBorder); // We need to reset the border so that the border will repaint.
    }
    
    public void removePanelBorder(byte removeBorderState) {
        this.panelBorder.removeBorderState(removeBorderState);
        MCTViewManifestationInfo manifestInfo = CanvasManifestation.getManifestationInfo(this.wrappedManifestation);
        manifestInfo.addInfoProperty(ControlAreaFormattingConstants.PANEL_BORDER_PROPERTY, String.valueOf(this.panelBorder.getBorders()));
        setBorder(null);
        setBorder(panelBorder); // We need to reset the border so that the bord
    }
    
    public void setBorderStyle(int borderStyle) {
        MCTViewManifestationInfo manifestInfo = CanvasManifestation.getManifestationInfo(this.wrappedManifestation);
        manifestInfo.setBorderStyle(borderStyle);
        this.panelBorder.setBorderStyle(borderStyle);
        setBorder(null);
        setBorder(panelBorder); // We need to reset the border so that the bord
    }
    
    public byte getBorderState() {
        return this.panelBorder.getBorders();
    }
    
    public int getBorderStyle() {
        return this.panelBorder.getBorderStyle();
    }
    
    public void setBorderColor(Color borderColor) {
        MCTViewManifestationInfo manifestInfo = CanvasManifestation.getManifestationInfo(this.wrappedManifestation);
        manifestInfo.setBorderColor(borderColor);
        this.panelBorder.setBorderColor(borderColor);
        setBorder(null);
        setBorder(panelBorder);
    }
    
    public Color getBorderColor() {
        return this.panelBorder.getBorderColor();
    }
    
    public View getWrappedManifestation() {
        return wrappedManifestation;
    }
    
    public boolean containsPoint(Point p) {
        Point locationOnScreen = getLocationOnScreen();
        return (p.x >= locationOnScreen.x) && (p.x < locationOnScreen.x + getWidth()) 
            && (p.y >= locationOnScreen.y) && (p.y < locationOnScreen.y + getHeight());
    }
    
    public View changeToView(ViewInfo view, MCTViewManifestationInfo newInfo) {
        AbstractComponent comp = wrappedManifestation.getManifestedComponent();
        comp = comp.getMasterComponent() == null ? comp : comp.getMasterComponent();
        MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(wrappedManifestation);
        
        // Compare the list of owned view properties and determine if a new view needs to be created.
        if (viewChanged(wrappedManifestation.getInfo(), info, newInfo)) {
            View manifestation = CanvasViewStrategy.CANVAS_OWNED.createViewFromManifestInfo(view, comp, ((View) panelSelectionProvider).getManifestedComponent(), info);
            attachManifestationToPanel(manifestation);  
            addStatusWidgetsIfApplicable();
            return manifestation;
        }
        
        return wrappedManifestation;
    }
    
    public View changeToView(ViewInfo view) {
        AbstractComponent comp = wrappedManifestation.getManifestedComponent();
        comp = comp.getMasterComponent() == null ? comp : comp.getMasterComponent();
        MCTViewManifestationInfo info = CanvasManifestation.getManifestationInfo(wrappedManifestation);
        View manifestation = CanvasViewStrategy.CANVAS_OWNED.createViewFromManifestInfo(view, comp, ((View) panelSelectionProvider).getManifestedComponent(), info);
        manifestation.setNamingContext(this);
        attachManifestationToPanel(manifestation);

        addStatusWidgetsIfApplicable();
        panelSelectionProvider.fireManifestationChanged();
        return manifestation;
    }
    
    private boolean viewChanged(ViewInfo viewInfo, MCTViewManifestationInfo oldManifestationInfo, MCTViewManifestationInfo newManifestationInfo) {
        ExtendedProperties oldExtProps = CanvasViewStrategy.CANVAS_OWNED.getExistingProperties(oldManifestationInfo, viewInfo);
        ExtendedProperties newExtProps = CanvasViewStrategy.CANVAS_OWNED.getExistingProperties(newManifestationInfo, viewInfo);
        byte[] existingMD5 = getMessageDigest(oldExtProps);
        byte[] newMD5 = getMessageDigest(newExtProps);
        
        if (existingMD5 == null || newExtProps == null)
            return true;
        
        return !Arrays.equals(existingMD5, newMD5);        
    }
    
    private byte[] getMessageDigest(ExtendedProperties props) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(marshal(props));
            byte messageDigest[] = algorithm.digest();
            return messageDigest;
        } catch (UnsupportedEncodingException e) {
        	LOGGER.debug(e.getMessage(), e);
        } catch (JAXBException e) {
        	LOGGER.debug(e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
        	LOGGER.debug(e.getMessage(), e);
        }
        return null;
    }
    
    private byte[] marshal(ExtendedProperties toBeMarshalled) throws JAXBException, UnsupportedEncodingException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JAXBContext ctxt = JAXBContext.newInstance(ExtendedProperties.class);
        Marshaller marshaller = ctxt.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "ASCII");
        marshaller.marshal(toBeMarshalled, out);
        return out.toByteArray();
    }
    
    
    private void attachManifestationToPanel(View manifestation) {
        manifestation.putClientProperty(CanvasManifestation.MANIFEST_INFO, CanvasManifestation.getManifestationInfo(wrappedManifestation));
        wrappedManifestation = manifestation;
        MCTViewManifestationInfo manifestationInfo = CanvasManifestation.getManifestationInfo(wrappedManifestation);
        manifestationInfo.setManifestedViewType(wrappedManifestation.getInfo().getType());
        
        scrollPane.setViewportView(wrappedManifestation);
        revalidate();
        repaint();
        
        // Update selection provider listeners.
        manifestation.getSelectionProvider().removeSelectionChangeListener(selectionListener);
        wrappedManifestation.getSelectionProvider().addSelectionChangeListener(selectionListener);        
    }
    
    public void hideTitle(boolean flag) {
        this.hasTitle = flag;

        if (!flag) {
            remove(titlePanel);
        } else {
            add(titlePanel, BorderLayout.NORTH);
        }
        MCTViewManifestationInfo manifestInfo = CanvasManifestation.getManifestationInfo(this.wrappedManifestation);
        manifestInfo.setHasTitlePanel(flag);
        if (!flag) {
            manifestInfo.setPanelTitle("");
        } else {
            String title = titleLabel.getText();
            if (!this.wrappedManifestation.getManifestedComponent().getDisplayName().equals(title)) {
                manifestInfo.setPanelTitle(titleLabel.getText());
            }
        }
        revalidate();
        notifyTitleChanged();
    }
    
    /**
     * Notifies view roles for the manifested component that the panel title
     * display has changed.
     */
    private void notifyTitleChanged() {
        AbstractComponent component = wrappedManifestation.getManifestedComponent();
        PropertyChangeEvent event = new PropertyChangeEvent(component);
        event.setProperty(PropertyChangeEvent.DISPLAY_NAME, component.getDisplayName());
        event.setProperty(PropertyChangeEvent.PANEL_TITLE, getTitle());
        // Avoids refreshing other manifestations when changing the panel title of 
        // one manifestation.
        wrappedManifestation.updateMonitoredGUI(event);
    }
    
    /**
     * Returns the panel title's bounds relative to its container. In this case,
     * the container is the containing <code>CanvasManifestation</code>.
     * @return bounds of the panel title relative to its container
     */
    public Rectangle getTitleBounds() {
        return titleBounds;
    }
    
    /**
     * Returns whether the point is part of the title area
     * @param point to determine whether it is part of the title area, in screen coordinates
     * @return true if the point is part of the title area false otherwise
     */
    public boolean pointInTitle(Point point) {
        Point adjustedPoint = (Point) point.clone();
        SwingUtilities.convertPointFromScreen(adjustedPoint, titlePanel);
        return hasTitle() && titlePanel.contains(adjustedPoint);
    }

    /**
     * Returns whether the point is in the panel border (inside the panel but outside the wrapped manifestation)
     * @param point to determine whether it is part of the panel, in screen coordinates
     * @return true if the point is part of the panel false otherwise
     */
    public boolean pointOnBorder(Point point) {
        Point panelAdjustedPoint = (Point) point.clone();
        SwingUtilities.convertPointFromScreen(panelAdjustedPoint, this);

        Point paneAdjustedPoint = (Point) point.clone();
        SwingUtilities.convertPointFromScreen(paneAdjustedPoint, scrollPane);
        return contains(panelAdjustedPoint) && !scrollPane.contains(paneAdjustedPoint);        
    }
    
    /**
     * Returns the panel icon's bounds relative to its container. In this case,
     * the container is the containing <code>CanvasManifestation</code>.
     * @return bounds of the panel icon relative to its container
     */
    public Rectangle getIconBounds() {
        return iconBounds;
    }

    public boolean hasTitle() {
        return hasTitle;
    }
    
    private void setTitleFromManifestation(MCTViewManifestationInfo manifestationInfo) {
        String panelTitle = manifestationInfo.getPanelTitle();
        String panelTitleFont = manifestationInfo.getPanelTitleFont();
        Integer panelTitleFontSize = manifestationInfo.getPanelTitleFontSize();
        Integer panelTitleFontStyle = manifestationInfo.getPanelTitleFontStyle();
        Integer panelTitleFontUnderline = manifestationInfo.getPanelTitleFontUnderline();
        Integer panelTitleForegroundColor = manifestationInfo.getPanelTitleFontForegroundColor();
        Integer panelTitleBackgroundColor = manifestationInfo.getPanelTitleFontBackgroundColor();
        if (panelTitleFontSize == null) {
            panelTitleFontSize = 12;
        }
        if (panelTitleFontStyle == null) {
            panelTitleFontStyle = Font.PLAIN;
        }
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.weightx = .99;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;            
        c.insets = TITLE_INSETS;
        if (panelTitle == null || panelTitle.isEmpty()) {
            titlePanel.remove(titleLabel);
            titlePanel.add(titleManifestation, c);
            titleLabel.setText("");
        } else {
            titlePanel.remove(titleManifestation);
            titlePanel.add(titleLabel, c);
            Font titleFont =new Font(panelTitleFont, panelTitleFontStyle.intValue(), 
                            panelTitleFontSize.intValue());
            if (panelTitleFontUnderline != null && panelTitleFontUnderline.equals(TextAttribute.UNDERLINE_ON)) {
                titleFont = titleFont.deriveFont(ControlAreaFormattingConstants.underlineMap);
            }
            titleLabel.setFont(titleFont);
            if (panelTitleForegroundColor != null) {
                titleLabel.setForeground(new Color(panelTitleForegroundColor.intValue()));
            }
            if (panelTitleBackgroundColor != null) {
                titleLabel.setOpaque(true);
                titleLabel.setBackground(new Color(panelTitleBackgroundColor.intValue()));
            } else {
                titleLabel.setOpaque(false);
            }
            titleLabel.setText(panelTitle);
            titleLabel.repaint();
        }
    }
    
    public void setTitle(String newTitle) {
        if (this.titlePanel.isVisible()) {
            MCTViewManifestationInfo manifestInfo = CanvasManifestation.getManifestationInfo(this.wrappedManifestation);
            manifestInfo.setPanelTitle(newTitle);
            setTitleFromManifestation(manifestInfo);
            revalidate();
            notifyTitleChanged();
        }
    }
    /** Set the panel title font from the manifestation info
     * @param newFont the string of the JVM Font Family to set
     */
    public void setTitleFont(String newFont) {
        if (this.titlePanel.isVisible()) {
            MCTViewManifestationInfo manifestInfo = CanvasManifestation.getManifestationInfo(this.wrappedManifestation);
            manifestInfo.setPanelTitleFont(newFont);
            setTitleFromManifestation(manifestInfo);
            revalidate();
            notifyTitleChanged();
        }
    }
    
    /** Set the panel title font size from the manifestation info
     * @param newFontSize the new font size
     */
    public void setTitleFontSize(Integer newFontSize) {
        if (this.titlePanel.isVisible()) {
            MCTViewManifestationInfo manifestInfo = CanvasManifestation.getManifestationInfo(this.wrappedManifestation);
            manifestInfo.setPanelTitleFontSize(newFontSize);
            setTitleFromManifestation(manifestInfo);
            revalidate();
            notifyTitleChanged();
        }
    }
    
    /** Set the panel title font style from the manifestation info
     * @param newFontStyle the new font style
     */
    public void setTitleFontStyle(Integer newFontStyle) {
        if (this.titlePanel.isVisible()) {
            MCTViewManifestationInfo manifestInfo = CanvasManifestation.getManifestationInfo(this.wrappedManifestation);
            manifestInfo.setPanelTitleFontStyle(newFontStyle);
            setTitleFromManifestation(manifestInfo);
            revalidate();
            notifyTitleChanged();
        }
    }
    
    /** Set the panel title font underline from the manifestation info
     * @param newFontStyle the title font underline style
     */
    public void setTitleFontUnderline(Integer newFontStyle) {
        if (this.titlePanel.isVisible()) {
            MCTViewManifestationInfo manifestInfo = CanvasManifestation.getManifestationInfo(this.wrappedManifestation);
            manifestInfo.setPanelTitleFontUnderline(newFontStyle);
            setTitleFromManifestation(manifestInfo);
            revalidate();
            notifyTitleChanged();
        }
    }
    
    /** Set the panel title font color from the manifestation info
     * @param newTitleFontForegroundColor
     */
    public void setTitleFontForegroundColor(Integer newTitleFontForegroundColor) {
        if (this.titlePanel.isVisible()) {
            MCTViewManifestationInfo manifestInfo = CanvasManifestation.getManifestationInfo(this.wrappedManifestation);
            manifestInfo.setPanelTitleFontForegroundColor(newTitleFontForegroundColor);
            setTitleFromManifestation(manifestInfo);
            revalidate();
            notifyTitleChanged();
        }
    }
    
    /** Set the panel title background color from the manifestation info
     * @param newTitleFontBackgroundColor
     */
    public void setTitleFontBackgroundColor(Integer newTitleFontBackgroundColor) {
        if (this.titlePanel.isVisible()) {
            MCTViewManifestationInfo manifestInfo = CanvasManifestation.getManifestationInfo(this.wrappedManifestation);
            manifestInfo.setPanelTitleFontBackgroundColor(newTitleFontBackgroundColor);
            setTitleFromManifestation(manifestInfo);
            revalidate();
            notifyTitleChanged();
        }
    }
    
    public String getTitle() {
        MCTViewManifestationInfo manifestInfo = CanvasManifestation.getManifestationInfo(this.wrappedManifestation);
        return manifestInfo.getPanelTitle();
    }
    
    /** Get the panel title font from the manifestation info
     * @return the panel title font from the manifestation info
     */
    public String getTitleFont() {
        MCTViewManifestationInfo manifestInfo = CanvasManifestation.getManifestationInfo(this.wrappedManifestation);
        return manifestInfo.getPanelTitleFont();
    }
    
    /** Get the panel title font size from the manifestation info
     * @return the panel title font size from the manifestation info
     */
    public Integer getTitleFontSize() {
        MCTViewManifestationInfo manifestInfo = CanvasManifestation.getManifestationInfo(this.wrappedManifestation);
        return manifestInfo.getPanelTitleFontSize();
    }
    
    /** Get the panel title font style from the manifestation info
     * @return the panel title font style from the manifestation info
     */
    public Integer getTitleFontStyle() {
        MCTViewManifestationInfo manifestInfo = CanvasManifestation.getManifestationInfo(this.wrappedManifestation);
        return manifestInfo.getPanelTitleFontStyle();
    }
    
    /** Get the panel title font underline style from the manifestation info
     * @return the panel title font underline style from the manifestation info
     */
    public Integer getTitleFontUnderline() {
        MCTViewManifestationInfo manifestInfo = CanvasManifestation.getManifestationInfo(this.wrappedManifestation);
        return manifestInfo.getPanelTitleFontUnderline();
    }
    
    /** Get the panel title font  color from the manifestation info
     * @return the panel title font color from the manifestation info
     */
    public Integer getTitleFontForegroundColor() {
        MCTViewManifestationInfo manifestInfo = CanvasManifestation.getManifestationInfo(this.wrappedManifestation);
        return manifestInfo.getPanelTitleFontForegroundColor();
    }
    
    /** Get the panel title background color from the manifestation info
     * @return the panel title background color from the manifestation info
     */
    public Integer getTitleFontBackgroundColor() {
        MCTViewManifestationInfo manifestInfo = CanvasManifestation.getManifestationInfo(this.wrappedManifestation);
        return manifestInfo.getPanelTitleFontBackgroundColor();
    }
    
    public void bringToFront() {
        this.panelSelectionProvider.fireOrderChange(this, PANEL_ZORDER.FRONT);
    }
    
    public void sendToBack() {
        this.panelSelectionProvider.fireOrderChange(this, PANEL_ZORDER.BACK);
    }
    
    public PanelFocusSelectionProvider getPanelFocusSelectionProvider() {
        return this.panelSelectionProvider;
    }
    
    public void save() {
        this.panelSelectionProvider.fireFocusPersist();
    }
    
    public void removeFromCanvas() {
        panelSelectionProvider.fireSelectionRemoved(this);
        getSelectionProvider().removeSelectionChangeListener(selectionListener);
    }
    
    public void update(MCTViewManifestationInfo manifestationInfo) {
        Rectangle existingBound = getBounds();
        Point newLocation = manifestationInfo.getStartPoint();
        Dimension newSize = manifestationInfo.getDimension();
        if (!existingBound.getLocation().equals(newLocation)
                        || !existingBound.getSize().equals(newSize)) {
            setBounds(new Rectangle(newLocation, newSize));
        }
        
        String borderStr = manifestationInfo.getInfoProperty(ControlAreaFormattingConstants.PANEL_BORDER_PROPERTY);
        if (borderStr != null) {
            byte border = Byte.parseByte(borderStr);
            this.panelBorder.setBorderState(border);
        }
        Color borderColor = manifestationInfo.getBorderColor();
        int borderStyle = manifestationInfo.getBorderStyle();
        this.panelBorder.setBorderStyle(borderStyle);
        this.panelBorder.setBorderColor(borderColor);
        setBorder(null);
        setBorder(panelBorder);
        
        titlePanel.setVisible(manifestationInfo.hasBorder());
        setTitleFromManifestation(manifestationInfo);
        
        String manifestedViewType = manifestationInfo.getManifestedViewType();
        AbstractComponent comp = wrappedManifestation.getManifestedComponent();
        Collection<ViewInfo> embeddedViews = comp.getViewInfos(ViewType.EMBEDDED);
        ViewInfo matchedVi = null;
        for (ViewInfo vi : embeddedViews) {
            if (manifestedViewType.equals(vi.getType())) {
                matchedVi = vi;
                break;
            }
        }
        assert matchedVi != null : "unable to find view for manifestation info " + manifestedViewType;
        changeToView(matchedVi, manifestationInfo);

        wrappedManifestation.putClientProperty(CanvasManifestation.MANIFEST_INFO,manifestationInfo);
    }
    
    private final PropertyChangeListener selectionListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(java.beans.PropertyChangeEvent evt) {
            firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
        }
    }; 

    public SelectionProvider getSelectionProvider() {
        return this;
    }
    
    @Override
    public void addSelectionChangeListener(PropertyChangeListener listener) {
        addPropertyChangeListener(SelectionProvider.SELECTION_CHANGED_PROP, listener);
    }

    @Override
    public void removeSelectionChangeListener(PropertyChangeListener listener) {
        removePropertyChangeListener(SelectionProvider.SELECTION_CHANGED_PROP, listener);
    }

    @Override
    public Collection<View> getSelectedManifestations() {
        return wrappedManifestation.getSelectionProvider().getSelectedManifestations();
    }

    @Override
    public void clearCurrentSelections() {
        wrappedManifestation.getSelectionProvider().clearCurrentSelections();
        Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (focusOwner != null && SwingUtilities.isDescendingFrom(focusOwner, wrappedManifestation)) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
        }
    }

    @Override
    public NamingContext getParentContext() {
        if (titleManifestation instanceof NamingContext) {
            return (NamingContext) titleManifestation;
        } else {
            return null;
        }
    }

    @Override
    public String getContextualName() {
        if (!hasTitle) return null;
        if (!titleLabel.getText().isEmpty()) return titleLabel.getText();
        if (getParentContext() != null) return getParentContext().getContextualName();
        return null;
    }
    

}
