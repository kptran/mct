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

import gov.nasa.arc.mct.components.AbstractComponent;

import java.util.ArrayList;

/**
 * Expressions format controller.
 */
public class ExpressionsFormattingController {

	private ExpressionsFormattingController() {
		
	}
	
	/**
	 * Notifies that a new expression has been created.
	 * @param newExp the new expression to add.
	 * @param expList the expression list to add.
	 */
	public static void notifyExpressionAdded(Expression newExp, ExpressionList expList){
		if (expList != null) {
			expList.addExp(newExp);
		}
	}
	
	/**
	 * Notifies the an existing expression has been deleted.
	 * @param selectedExp the selected expression to delete.
	 * @param expList the expression list to delete.
	 */
	public static void notifyExpressionDeleted(Expression selectedExp, ExpressionList expList){
		if (selectedExp != null && expList != null){
			expList.deleteExp(selectedExp);
		}
	}
	
	/**
	 * Notifies that an existing expression has been added on top.
	 * @param newExp the new expression to add above.
	 * @param selectedExp the selected expression to add from.
	 * @param expList the expression list to add to.
	 */
	public static void notifyExpressionAddedAbove(Expression newExp, Expression selectedExp, ExpressionList expList){
		if (newExp != null && selectedExp != null && expList != null) {
			int index = expList.indexOf(selectedExp);
			expList.addExp(index, newExp);
		}
	}
	
	/**
	 * Notifies than an existing expression has been added below.
	 * @param newExp the new expression to add below.
	 * @param selectedExp the selected expression to add to.
	 * @param expList the expression list to add to.
	 */
	public static void notifyExpressionAddedBelow(Expression newExp, Expression selectedExp, ExpressionList expList){
		if (newExp != null && selectedExp != null && expList != null) {	
			int index = expList.indexOf(selectedExp);
			expList.addExp(index+1, newExp);
		}
	}
	
	/**
	 * Notifies to move expression up one.
	 * @param exp the expression to move up one.
	 * @param expList the expression list.
	 */
	public static void notifyMovedUpOne(Expression exp, ExpressionList expList){
		if (exp != null && expList != null){
			int index = expList.indexOf(exp);
			expList.move(index-1, exp);
		}
	}
	
	/**
	 * Notifies to move expression down one.
	 * @param exp the expression to move down one.
	 * @param expList the expression list. 
	 */
	public static void notifyMovedDownOne(Expression exp, ExpressionList expList){
		if (exp != null && expList != null){
			int index = expList.indexOf(exp);
			expList.move(index+1, exp);
		}
	}
	
	/**
	 * Notifies to move expression to top level.
	 * @param exp the expression to move to top level.
	 * @param expList the expression list.
	 */
	public static void notifyMoveToTop(Expression exp, ExpressionList expList){
		if (exp != null && expList != null){
			expList.move(0, exp);
		}
	}
	
	/**
	 * Notifies to move expression to the bottom level. 
	 * @param exp the expression to move expression to the bottom level.
	 * @param expList the expression list.
	 */
	public static void notifyMoveToBottom(Expression exp, ExpressionList expList){
		if (exp != null && expList != null){
			expList.move(expList.size()-1, exp);
		}
	}
	
	/**
	 * Notifies to remove telemetry item from the expression.
	 * @param telem the telemetry component.
	 * @param telemList the array list of telemetry components.
	 */
	public static void notifyRemoveTelem(AbstractComponent telem, ArrayList<AbstractComponent> telemList) {
		int index = telemList.indexOf(telem);
		telemList.remove(index);
	}
}
