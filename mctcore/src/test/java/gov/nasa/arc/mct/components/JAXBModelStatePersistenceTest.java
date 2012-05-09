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
package gov.nasa.arc.mct.components;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.testng.Assert;
import org.testng.annotations.Test;

public class JAXBModelStatePersistenceTest {
    
    @Test
    public void testRoundTripSerialization() {
        final String expectedValue = "abc";
        JAXBExample je = new JAXBExample();
        je.setValue(expectedValue);
        
        ModelStatePersister msp = new ModelStatePersister();
        msp.example = je;
        
        String state = msp.getModelState();
        msp.setModelState(state);
        JAXBExample roundTripValue = msp.example;
        Assert.assertNotSame(roundTripValue, je);
        Assert.assertEquals(roundTripValue.getValue(), expectedValue);
    }
    
    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class JAXBExample {
        private String value;

        /**
         * @return the value
         */
        public String getValue() {
            return value;
        }

        /**
         * @param value the value to set
         */
        public void setValue(String value) {
            this.value = value;
        }
        
    }
    
    public static class ModelStatePersister extends JAXBModelStatePersistence<JAXBExample> {
        public JAXBExample example;
        
        @Override
        protected JAXBExample getStateToPersist() {
            return example;
        }

        @Override
        protected void setPersistentState(JAXBExample modelState) {
            example = modelState;
        }

        @Override
        protected Class<JAXBExample> getJAXBClass() {
            return JAXBExample.class;
        }
        
    }
    
    
}
