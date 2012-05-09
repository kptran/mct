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
import java.io.ByteArrayInputStream;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * 
 * @author vwoeltje
 *
 */

public class ColorPropertiesTest {
    
    private static String TEST_PREFIX  = "*cat";
    private static String TEST_COLOR   = "#F00BA2";
    private static String TEST_COLOR2  = "#BA2F00";
    private static String TEST_COLOR3  = "#F0000D";
    private static String TEST_KEY     = "Label.foreground";
    private static String TEST_NEWKEY  = "nonswing";
    private static String TEST_PREFIX2 = "dog";

    private InputStream testInputStream() {
        String testProperty = TEST_PREFIX  + "." + TEST_KEY +    " = " + TEST_COLOR  + "\n";
        testProperty       += TEST_PREFIX  + "." + TEST_NEWKEY + " = " + TEST_COLOR2 + "\n";
        testProperty       +=                      TEST_NEWKEY + " = " + TEST_COLOR  + "\n";
        testProperty       += TEST_PREFIX2 + "." + TEST_NEWKEY + " = " + TEST_COLOR3 + "\n";
        return new ByteArrayInputStream(testProperty.getBytes());
    }
    
    @Test
    public void testColorPropertiesLoad() throws Exception {
        ColorScheme c = null;
        
        System.setProperty(LookAndFeelSettings.viewColor, "src/test/resources/properties/anyFolder/anyFile.properties");
        LookAndFeelSettings.INSTANCE.setLAF("lookAndFeelStr") ;
        c = LookAndFeelSettings.getColorProperties().getColorSchemeFor("Thing");
        c.applyColorScheme();
        Assert.assertEquals( UIManager.get("Thing.Panel.background"), "anyCustomerDefined");
        
        System.setProperty(LookAndFeelSettings.viewColor, "src/test/resources/properties/viewColor.properties");
        LookAndFeelSettings.INSTANCE.setLAF("lookAndFeelStr") ;
        c = LookAndFeelSettings.getColorProperties().getColorSchemeFor("Thing");
        c.applyColorScheme();
        Assert.assertEquals( UIManager.get("Thing.Panel.background"), "default");
       
        System.setProperty(LookAndFeelSettings.viewColor, "");
        LookAndFeelSettings.INSTANCE.setLAF("lookAndFeelStr") ;
        c = LookAndFeelSettings.getColorProperties().getColorSchemeFor("Thing");
        c.applyColorScheme();
        Assert.assertEquals( UIManager.get("Thing.Panel.background"), "default");
    }
    
    @Test
    public void testColorProperties() throws Exception {
        Properties p = new Properties();
        p.load(testInputStream());
        ColorScheme cp = new ColorScheme ( p );
        Callable<Object> getLabelForeground = new Callable<Object>() {
            public Object call() {
                return (new JLabel()).getForeground();
            }
        };
        
        UIManager.setLookAndFeel(new MetalLookAndFeel());
                
        Color newColor = Color.decode(TEST_COLOR);
        Color oldColor = (Color) UIManager.getLookAndFeelDefaults().get(TEST_KEY);
        
        Assert.assertNotSame(newColor, oldColor);
        Assert.assertNotNull(oldColor);
        Assert.assertNotNull(newColor);
      
        Color c;

        c = (Color) getLabelForeground.call();
        Assert.assertNotNull(c);
        Assert.assertEquals(c, oldColor);
        
        c = (Color) cp.callUnderColorScheme( getLabelForeground );
        Assert.assertNotNull(c);
        Assert.assertEquals(c, oldColor);

        c = (Color) cp.getColorSchemeFor("cat").callUnderColorScheme( getLabelForeground );
        Assert.assertNotNull(c);
        Assert.assertEquals(c, newColor);

        c = (Color) cp.getColorSchemeFor("birdcat").callUnderColorScheme( getLabelForeground );
        Assert.assertNotNull(c);
        Assert.assertEquals(c, newColor);                

        c = (Color) cp.getColorSchemeFor("dog").callUnderColorScheme( getLabelForeground );
        Assert.assertNotNull(c);
        Assert.assertEquals(c, oldColor);                
      
        c = (Color) getLabelForeground.call();
        Assert.assertNotNull(c);
        Assert.assertEquals(c, oldColor);              
               
    }
    
    @Test
    public void testKeyAvailability() throws Exception {
        Properties p = new Properties();
        p.load(testInputStream());
        ColorScheme cp = new ColorScheme (p);
        
        Color color1 = Color.decode(TEST_COLOR);
        Color color2 = Color.decode(TEST_COLOR2);
        Color color3 = Color.decode(TEST_COLOR3);
        
        Color c;
        
        c = (Color) cp.callUnderColorScheme( KeyGrabber.grab ( TEST_NEWKEY ) );
        Assert.assertNotNull(c);
        Assert.assertEquals(c, color1);
        
        c = (Color) cp.getColorSchemeFor("cat").callUnderColorScheme( KeyGrabber.grab ( TEST_NEWKEY ));
        Assert.assertNotNull(c);
        Assert.assertEquals(c, color2);
     
        c = (Color) cp.callUnderColorScheme( KeyGrabber.grab ( TEST_PREFIX + "." + TEST_NEWKEY ) );
        Assert.assertNull(c);
        
        c = (Color) cp.callUnderColorScheme( KeyGrabber.grab ( TEST_PREFIX2 + "." + TEST_NEWKEY));
        Assert.assertNotNull(c);
        Assert.assertEquals(c, color3);
        
        c = (Color) cp.getColorSchemeFor("cat").callUnderColorScheme( KeyGrabber.grab ( TEST_PREFIX2 + "." + TEST_NEWKEY));
        Assert.assertNotNull(c);
        Assert.assertEquals(c, color3);       
        
                
    }
    
    private static class KeyGrabber implements Callable<Object> {
        private String key;
        private KeyGrabber(String key) { 
            this.key = key; 
        }
        @Override
        public Object call() throws Exception {
            return UIManager.get(key);
        }
        public static KeyGrabber grab ( String key ) {
            return new KeyGrabber(key);
        }
    }
}
