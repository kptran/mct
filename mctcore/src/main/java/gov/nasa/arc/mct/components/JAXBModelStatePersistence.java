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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * This class provides a JAXB based serialized model state. 
 *
 * @param <C> class that will be serialized using JAXB, this class should be annotated for JAXB. 
 */
public abstract class JAXBModelStatePersistence<C> implements ModelStatePersistence {
    private static final Map<Class<?>, JAXBContext> marshalCache = new ConcurrentHashMap<Class<?>, JAXBContext>();
    
    @Override
    public final String getModelState() {
        try {
            return marshal(getStateToPersist());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }
    }

    @Override
    public final void setModelState(String state) {
        try {
            setPersistentState(unmarshal(getJAXBClass(), state));
        } catch (DataBindingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Gets the current model state to persist. 
     * @return the current model state
     */
    protected abstract C getStateToPersist();
    
    /**
     * Sets the current model state. 
     * @param modelState to reference as the current model state. 
     */
    protected abstract void setPersistentState(C modelState);
    
    /**
     * Gets the class used for JAXB serialization. 
     * @return class used as JAXBContext
     */
    protected abstract Class<C> getJAXBClass();
    
    private JAXBContext getFromCache(Class<?> unMarshalledClazz) throws JAXBException {
        JAXBContext jc = marshalCache.get(unMarshalledClazz);
        if (jc == null) {
            jc = JAXBContext.newInstance(unMarshalledClazz);
            marshalCache.put(unMarshalledClazz, jc);
        }
        return jc;
    }
    
    /**
     * Unmarshals the supplied bytes and return the unmarshalled object.
     * 
     * @param unMarshalledClazz
     *            the class of the object to be created from the unmarshalling
     *            operation
     * @param state
     *            the string that contains the marshalled data.
     * @return the object created from unmarshalling the model role data.
     */
    private C unmarshal(Class<C> unMarshalledClazz, String state) throws DataBindingException, JAXBException, UnsupportedEncodingException {
        InputStream is = new ByteArrayInputStream(state.getBytes("ASCII"));
        JAXBContext jc = getFromCache(unMarshalledClazz);
        Unmarshaller u = jc.createUnmarshaller();
        return unMarshalledClazz.cast(u.unmarshal(is));
    }
    
    /**
     * Marshals the data and return the marshalled String. The object is
     * converted between byte and Unicode characters using the named encoding
     * "UTF-8".
     * 
     * @param toBeMarshalled
     *            the object whose data is to be marshalled.
     */
    private String marshal(C toBeMarshalled) throws JAXBException, UnsupportedEncodingException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Class<?> clazz = toBeMarshalled.getClass();
        JAXBContext ctxt = getFromCache(clazz);
        
        Marshaller marshaller = ctxt.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "ASCII");
        marshaller.marshal(toBeMarshalled, out);
        return out.toString("ASCII");
    }

}
