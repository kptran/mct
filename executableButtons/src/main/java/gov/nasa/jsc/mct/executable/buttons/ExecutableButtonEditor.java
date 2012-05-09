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

import gov.nasa.arc.mct.components.PropertyEditor;

import java.util.List;


public final class ExecutableButtonEditor implements PropertyEditor<Object> {

	ExecutableButtonComponent executableButtonComponent = null;

	public ExecutableButtonEditor(ExecutableButtonComponent comp) {
        executableButtonComponent = comp;
	}

	@Override
	public String getAsText() {
		return executableButtonComponent.getModel().getData().getExecCmd();
	}

	/**
	 * Edit the value of a limit line. 
	 * Refresh the value of the cache, so that GUIs like plot and alpha are redrawn.
	 * 
	 * @param newValue the new limit line value
	 * @throws exception if the new value is invalid.  The consumer must handle this exception and
	 * disallow the prospective edit.
	 */
	@Override
	public void setAsText(String newValue) throws IllegalArgumentException {
		if (newValue == null || newValue.isEmpty()) {
			throw new IllegalArgumentException("Cannot be empty.");
		}
		executableButtonComponent.getModel().getData().setExecCmd(newValue);
	}

	@Override
	public Object getValue() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Object> getTags() {
		throw new UnsupportedOperationException();
	}
}  
