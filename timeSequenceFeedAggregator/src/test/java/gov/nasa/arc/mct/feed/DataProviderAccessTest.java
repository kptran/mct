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
package gov.nasa.arc.mct.feed;

import gov.nasa.arc.mct.api.feed.DataProvider;
import gov.nasa.arc.mct.api.feed.DataProvider.LOS;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.Vector;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DataProviderAccessTest {
    @Mock
    private DataProvider dataProvider;
    private FeedAggregatorService service;
    private File bufferLocation;
    
    @BeforeMethod
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        Mockito.when(dataProvider.getLOS()).thenReturn(LOS.fast);
        Properties prop = new Properties();
        prop.load(ClassLoader.getSystemResourceAsStream("properties/testFeed2.properties"));
        prop.put("buffer.partitions", "2");
        prop.put("buffer.time.millis", "-1");
        bufferLocation = File.createTempFile("mct-buffer", "");
        bufferLocation.delete();
        bufferLocation.mkdir();
        prop.put("buffer.disk.loc", bufferLocation.toString());
        service = new FeedAggregatorService(prop);
    }

    @AfterMethod
    public void reset() {
        delete(bufferLocation);
    }

    private void delete(File f) {
        if (f.isDirectory()) {
            for (File f2 : f.listFiles()) {
                delete(f2);
            }
        }
        f.delete();
    }

    @Test
    public void setAndRemoveProviderTest() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        Vector<DataProvider> providers = getProviders();
        int numOfProvidersBefore = providers.size();
        service.addDataProvider(dataProvider);
        providers = getProviders();
        Assert.assertEquals(providers.size(), numOfProvidersBefore+1);
        
        boolean found = false;
        for (DataProvider provider: providers) {
            if (provider == dataProvider) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(found);
        
        service.removeDataProvider(dataProvider);
        providers = getProviders();
        Assert.assertEquals(providers.size(), numOfProvidersBefore);
    }
    
    @SuppressWarnings("unchecked")
    private Vector<DataProvider> getProviders() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field field = FeedAggregatorService.class.getDeclaredField("dataProviders");
        field.setAccessible(true);
        return (Vector<DataProvider>)field.get(service);
    }
}
