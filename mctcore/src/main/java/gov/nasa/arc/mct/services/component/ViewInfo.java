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
package gov.nasa.arc.mct.services.component;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.util.LookAndFeelSettings;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import javax.swing.ImageIcon;

/**
 * The ViewInfo class describes metadata about a view. This class is used to create new view instances. 
 *
 */
public class ViewInfo {
    private final Constructor<? extends View> viewConstructor;
    private final String type;
    private final String viewName;
    private final ViewType viewType;
    private final ImageIcon icon;
    private final ImageIcon selectedIcon;
   
    /**
     * Creates a new instance of ViewInfo.
     * @param aViewClass class representing a view. 
     * @param viewName name of the view
     * @param viewType for this view
     * @throws IllegalArgumentException if the view type is null
     */
    public ViewInfo(Class<? extends View> aViewClass, String viewName, ViewType viewType) throws IllegalArgumentException {
        this(aViewClass, viewName, aViewClass.getName(), viewType);
    }
    
    /**
     * Creates a new instance of ViewInfo. This constructor should only be used when
     * attempting to provide backward compatibility for views which have already been serialized. The
     * serialized mapping uses the type to determine how to map the state to a view type. 
     * @param aViewClass representing a view.
     * @param name human readable name of the view
     * @param aType representing the type used when serializing the view state. The type must be unique across all serialized view
     * states so the default type used is the fully qualified class name. 
     * @param viewType for this view
     * @throws IllegalArgumentException if the view type is null or the class doesn't have the right type of constructor
     */
    public ViewInfo(Class<? extends View> aViewClass, String name, String aType, ViewType viewType) throws IllegalArgumentException {
        this(aViewClass, name, aType, viewType, null, null);
    }

    /**
     * Creates a new instance of ViewInfo. This constructor should only be used when
     * attempting to provide backward compatibility for views which have already been serialized. The
     * serialized mapping uses the type to determine how to map the state to a view type. 
     * @param aViewClass representing a view.
     * @param name human readable name of the view
     * @param aType representing the type used when serializing the view state. The type must be unique across all serialized view
     * states so the default type used is the fully qualified class name. 
     * @param viewType for this view
     * @param icon to be placed in a button for this view. This icon is typically used for button showing in the inspector.
     * @param selectedIcon icon to be placed in a button for this view. This icon is typically used for button showing in the inspector when the button is selected.
     * @throws IllegalArgumentException if the view type is null or the class doesn't have the right type of constructor
     */
    public ViewInfo(Class<? extends View> aViewClass, String name, String aType, ViewType viewType, ImageIcon icon, ImageIcon selectedIcon) throws IllegalArgumentException {
        type = aType;
        viewName = name;
        this.icon = icon;
        this.selectedIcon = selectedIcon;
        if (name == null) {
            throw new IllegalArgumentException("name must be specified for " + aViewClass);
        }
        if (viewType == null) {
            throw new IllegalArgumentException("view type must be specified for " + name);
        }
        this.viewType = viewType;
        viewConstructor = getConstructor(aViewClass);
        if (viewConstructor == null) {
            throw new IllegalArgumentException("a constructor must be defined that has AbstractComponent and ViewInfo as the parameters for " + aViewClass);
        }
    }

    /**
     * Gets the class representing the view. 
     * @return the viewClass
     */
    public Class<? extends View> getViewClass() {
        return viewConstructor.getDeclaringClass();
    }

    /**
     * Gets the type used to represent this view. 
     * @return the type
     */
    public String getType() {
        return type;
    }
    
    /**
     * Gets the human readable name for this view.
     * @return String representing the view name
     */
    public String getViewName() {
         return viewName;
    }
    
    /**
     * Returns the view type supported by the view.
     * @return the view type for this view
     */
    public ViewType getViewType() {
        return viewType;
    }
    
    @Override
    public int hashCode() {
        return getType().hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof ViewInfo && ((ViewInfo)obj).getType().equals(type);
    }
    
    @Override
    public String toString() {
        return "class " + getViewClass();
    }
    
    /**
     * Creates a new instance of this for the selected view type. 
     * @param component to use to attach view to
     * @return new instance of view
     */
    public View createView(final AbstractComponent component) {
        View v;
        /* Try to build under MCT's color model */
        try {
            v = LookAndFeelSettings.getColorProperties().getColorSchemeFor(getViewClass().getSimpleName()).callUnderColorScheme( new Callable<View>() {
                public View call() throws Exception {
                    return newView(component);                
                };
            });   
        } catch (Exception e) {
            /* Don't swallow runtime exceptions */
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            
            /* If that fails, just build it. */
            v = newView(component);
        }
        component.addViewManifestation(v);
        
        return v;
    }
    
    /**
     * Returns the icon to be placed in a button for this view.
     * @return icon; null if no provided
     */
    public ImageIcon getIcon() {
        return icon;
    }
    
    /**
     * Returns the icon to be placed in a button for this view when the button is selected.
     * @return an icon; null if not provided
     */
    public ImageIcon getSelectedIcon() {
        return selectedIcon;
    }
    
    private Constructor<? extends View> getConstructor(Class<? extends View> viewClass) {
        Constructor<? extends View> c = null;
        try {
            c = viewClass.getConstructor(AbstractComponent.class, ViewInfo.class);
        } catch (SecurityException e) {
        } catch (NoSuchMethodException e) {
        }

        return c;
    }
    
    private View newView(AbstractComponent ac) {
        View v = null;
        try {
            v = viewConstructor.newInstance(ac, this);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        
        return v;
    }
    
}
