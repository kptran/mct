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
package gov.nasa.jsc.mct.executable.buttons;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.JAXBModelStatePersistence;
import gov.nasa.arc.mct.components.ModelStatePersistence;
import gov.nasa.arc.mct.components.PropertyDescriptor;
import gov.nasa.arc.mct.components.PropertyDescriptor.VisualControlDescriptor;

import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class ExecutableButtonComponent extends AbstractComponent {
	private static final ResourceBundle bundle  = ResourceBundle.getBundle("ResourceBundle"); //NOI18N
	private final AtomicReference<ExecutableButtonModel> model = new AtomicReference<ExecutableButtonModel>(new ExecutableButtonModel());
	
	@Override
	protected <T> T handleGetCapability(Class<T> capability) {
		if (ModelStatePersistence.class.isAssignableFrom(capability)) {
		    JAXBModelStatePersistence<ExecutableButtonModel> persistence = new JAXBModelStatePersistence<ExecutableButtonModel>() {

				@Override
				protected ExecutableButtonModel getStateToPersist() {
					return model.get();
				}

				@Override
				protected void setPersistentState(ExecutableButtonModel modelState) {
					model.set(modelState);
				}

				@Override
				protected Class<ExecutableButtonModel> getJAXBClass() {
					return ExecutableButtonModel.class;
				}
		        
			};
			
			return capability.cast(persistence);
		}
		
		return null;
	}
	
	@Override
	public boolean isLeaf() {
		return true;
	}
	
	public ExecutableButtonModel getModel() {
		return model.get();
	}
	
	@Override
	public List<PropertyDescriptor> getFieldDescriptors()  {

        PropertyDescriptor p = new PropertyDescriptor(bundle.getString("EXEC_COMMAND_LABEL"), new ExecutableButtonEditor(this), VisualControlDescriptor.TextField);
        p.setFieldMutable(true);
        
		return Collections.singletonList(p);
	}
}
