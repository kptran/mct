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
package gov.nasa.arc.mct.evaluator.expressions;

/**
 * Enum expression.
 *
 */
public class Expression {

	private String operator;
	private Double val= Double.valueOf(0);
	private String display;
	
	/**
	 * Expression default initialization constructor.
	 */
	public Expression() {
		this.operator = "=";
		this.display = "";
	}
	
	/**
	 * Expression initialization based upon operation, value, and display.
	 * @param op operation.
	 * @param val value.
	 * @param dis display name.
	 */
	public Expression(String op, String val, String dis){
		this.operator = op;
		if (val != null) {
			try {
				this.val = Double.parseDouble(val);
			} catch (NumberFormatException nfe) {
				// ignore the default value of zero will be used
			}
		}
		this.display = dis;
	}
	
	/**
	 * Sets the operator.
	 * @param op operation.
	 */
	public void setOperator(String op) {
		this.operator = op;
	}
	
	/**
	 * Sets the operator.
	 * @return operator the operator.
	 */
	public String getOperator() {
		return this.operator;
	}
	
	/**
	 * Sets the value.
	 * @param val the value.
	 */
	public void setVal(Double val) {
		this.val = val;
	}
	
	/**
	 * Gets the value.
	 * @return val the value.
	 */
	public Double getVal() { 
		return this.val;
	}
	
	/**
	 * Sets the display name.
	 * @param display name.
	 */
	public void setDisplay(String display) {
		this.display = display;
	}
	
	/**
	 * Gets the display name.
	 * @return display name.
	 */
	public String getDisplay() {
		return this.display;
	}
}
