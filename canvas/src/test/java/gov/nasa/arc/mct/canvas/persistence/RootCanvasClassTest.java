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
package gov.nasa.arc.mct.canvas.persistence;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.testng.Assert;
import org.testng.annotations.Test;

public class RootCanvasClassTest {
    
    
    @Test
    public void testBackwardCompatibility() throws Exception {
        JAXBContext jc = JAXBContext.newInstance(CanvasPersistentState.class);
        Unmarshaller u = jc.createUnmarshaller();
        
        InputStream is = getClass().getResourceAsStream("/JAXBTest.xml");
        CanvasPersistentState info = (CanvasPersistentState) u.unmarshal(is);
        is.close();
        List<MCTViewManifestationInfo> infos = info.getInfos();
        Assert.assertEquals(infos.size(), 3);
        List<String> expected = Arrays.asList("1","2","0");
        for (int i = 0; i < infos.size(); i++) {
            // verify panel order
            MCTViewManifestationInfo curInfo = infos.get(i);
            Assert.assertEquals(curInfo.getInfoProperty("PANEL_ORDER"),expected.get(i));
        }
    }
}

