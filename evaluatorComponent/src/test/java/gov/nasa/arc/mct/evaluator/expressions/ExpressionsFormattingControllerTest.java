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

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ExpressionsFormattingControllerTest {
	private Expression e1, e2, e3;
	private ExpressionList eList;
	private ArrayList<AbstractComponent> tList;
	@Mock 
	private AbstractComponent t; 
	
	@BeforeMethod
	public void setup(){
        MockitoAnnotations.initMocks(this);

		eList = new ExpressionList("");
		e1 = new Expression("=", "1", "test1");
		e2 = new Expression("=", "2", "test2");
		e3 = new Expression("=", "3", "test3");
		tList = new ArrayList<AbstractComponent>();
	}
	
	@AfterMethod
	public void tearDown() {
		this.eList = new ExpressionList("");
	}
	
	@Test
	public void notifyExpressionAddedTest(){
		ExpressionsFormattingController.notifyExpressionAdded(e1, eList);
		Assert.assertEquals(eList.size(), 1);
		Assert.assertEquals(eList.getExp(0), e1);
	}
	
	@Test
	public void notifyExpressionDeletedTest(){
		eList.addExp(e1);
		eList.addExp(e2);
		ExpressionsFormattingController.notifyExpressionDeleted(e2, eList);
		Assert.assertEquals(eList.size(), 1);
		Assert.assertEquals(eList.getExp(0), e1);
	}
	
	@Test 
	public void notifyExpressionAddedAboveTest(){
		eList.addExp(e1);
		ExpressionsFormattingController.notifyExpressionAddedAbove(e2, e1, eList);
		Assert.assertEquals(eList.size(), 2);
		Assert.assertEquals(eList.getExp(0), e2);
	}
	
	@Test 
	public void notifyExpressionAddedBelowTest(){
		eList.addExp(e1);
		ExpressionsFormattingController.notifyExpressionAddedBelow(e2, e1, eList);
		Assert.assertEquals(eList.size(), 2);
		Assert.assertEquals(eList.getExp(0), e1);
	}
	
	@Test
	public void notifyMovedUpOneTest(){
		eList.addExp(e1);
		eList.addExp(e2);
		ExpressionsFormattingController.notifyMovedUpOne(e2, eList);
		Assert.assertEquals(eList.getExp(0), e2);
	}
	
	@Test
	public void notifyMovedDownOneTest(){
		eList.addExp(e1);
		eList.addExp(e2);
		ExpressionsFormattingController.notifyMovedDownOne(e1, eList);
		Assert.assertEquals(eList.getExp(0), e2);
	}
	
	@Test
	public void notifyMoveToTopTest(){
		eList.addExp(e1);
		eList.addExp(e2);
		eList.addExp(e3);
		ExpressionsFormattingController.notifyMoveToTop(e3, eList);
		Assert.assertEquals(eList.getExp(0), e3);
	}
	
	@Test
	public void notifyMoveToBottomTest(){
		eList.addExp(e1);
		eList.addExp(e2);
		eList.addExp(e3);
		ExpressionsFormattingController.notifyMoveToBottom(e1, eList);
		Assert.assertEquals(eList.getExp(2), e1);
		Assert.assertEquals(eList.getExp(0), e2);
	}
	
	@Test
	public void notifyRemoveTelemTest(){
		tList.add(t);
		Assert.assertEquals(tList.size(), 1);
		ExpressionsFormattingController.notifyRemoveTelem(t, tList);
		Assert.assertEquals(tList.size(), 0);
	}
}
