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
package gov.nasa.arc.mct.evaluator.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.evaluator.api.Executor;
import gov.nasa.arc.mct.evaluator.spi.EvaluatorProvider;

/**
 * The enum evalutator implementation class.
 *
 */
public class EnumEvaluator  implements EvaluatorProvider {

	/** Language string constant. */
	public static final String LANGUAGE_STRING = "enum";
	
	/** Not equals character. */
	public static final String NOT_EQUALS = "\u2260";
	private static final String ruleMatcher = "([\u2260<>=])[ ]([-]?[\\d]*[\\.]?[\\d]*)[ ]([^\t]*)[\t][|]";
	
	/** Enum expression pattern. */
	public static final Pattern enumExpression = Pattern.compile(ruleMatcher);
	private List<EnumExpression> expressions;
	
	@Override
	public Executor compile(String code) {
		Matcher m = enumExpression.matcher(code);
		expressions = new ArrayList<EnumExpression>();
		while (m.find()){
			assert m.groupCount() == 3 : "only three matching groups should be discovered: found " + m.groupCount();
			String relation = m.group(1);
			String value = m.group(2);
			String display = m.group(3);
			expressions.add(new EnumExpression(relation, value, display));
			
		}
		return new EnumExecutor(expressions);
	}
	
	/** 
	 * Evaluates the expression value.
	 * @param val the value.
	 * @return the calculated evaluation.
	 */
	public String evaluate(String val) {
		if (val != null) {
			try {
				double value = Double.parseDouble(val);
				for (EnumExpression expression : expressions) {
					String expressionValue = expression.execute(value);
					if (expressionValue != null){ 
						return expressionValue;
					}
				}
			} catch (NumberFormatException nfe) {
				
			}
		}
		return val;
	}
	
	@Override
	public String getLanguage(){
		return LANGUAGE_STRING;
	}
	
	private static class EnumExpression {
		private final String display;
		private final double value;
		
		enum Operator {
			EQUALS {
				@Override
				public boolean matches(String operator) {
					return "=".equals(operator);
				}
				
				@Override
				public String evaluate(double lhs, double rhs, String output) {
					return lhs == rhs ? output : null;
				}
			},
			LESS_THAN {
				@Override
				public boolean matches(String operator) {
					return "<".equals(operator);
				}
				
				@Override
				public String evaluate(double lhs, double rhs, String output) {
					return lhs < rhs ? output : null;
				}
			},
			GREATER_THAN {
				@Override
				public boolean matches(String operator) {
					return ">".equals(operator);
				}
				
				@Override
				public String evaluate(double lhs, double rhs, String output) {
					return lhs > rhs ? output : null;
				}
			},
			NOT_EQUALS {
				@Override 
				public boolean matches(String operator) {
					return EnumEvaluator.NOT_EQUALS.equals(operator);
				}
				
				@Override
				public String evaluate (double lhs, double rhs, String output) {
					return lhs != rhs ? output : null;
				}
			};
			
			public abstract boolean matches(String operator);
			public abstract String evaluate(double lhs, double rhs, String output);
		}
		private final Operator operator;
		
		/**
		 * Enum expression contructor.
		 * @param op the operation performed.
		 * @param value the value.
		 * @param display the display name.
		 */
		public EnumExpression (String op, String value, String display) {
			this.value = Double.parseDouble(value);
			this.display = display;
			
			Operator localOperator = null;
			for (Operator o:Operator.values()) {
				if (o.matches(op)) {
					localOperator = o;
					break;
				}
			}
			operator = localOperator;
			assert operator != null : "operator must not be null" ;
		}
		
		/**
		 * Executes the value.
		 * @param aValue the value.
		 * @return the execute string.
		 */
		public String execute(double aValue) {
			return operator.evaluate(aValue, value, display);
		}
	}
	
	private static class EnumExecutor implements Executor {
		private final List <EnumExpression> expressions;
		
		public EnumExecutor(List<EnumExpression> expressionList) {
			expressions = expressionList;
		}
		
		private FeedProvider.RenderingInfo getValueFromFeed(List<FeedProvider> providers, Map<String, List<Map<String, String>>> data) {
			FeedProvider.RenderingInfo result = null;
			FeedProvider provider = providers.get(0);
			String feedId = provider.getSubscriptionId();
			List<Map<String,String>> values = data.get(feedId);
			if (values != null) {
				result = provider.getRenderingInfo(values.get(values.size() - 1));
			}
			
			return result;
		}
		
		/**
		 * Requires multiple inputs.
		 * @return false.
		 */
		public boolean requiresMultipleInputs() {
			return false;
		}
		
		@Override
		public FeedProvider.RenderingInfo evaluate(Map<String, List<Map<String, String>>> data, List<FeedProvider> feedProviders) {
			assert feedProviders.size() == 1 : "only a single feed provider is supported";
			
			FeedProvider.RenderingInfo feedValue = getValueFromFeed(feedProviders,data);
			
			if (feedValue != null) {
				try {
					double value = Double.parseDouble(feedValue.getValueText());
					for (EnumExpression expression : expressions) {
						String expressionValue = expression.execute(value);
						if (expressionValue != null) {
							return new FeedProvider.RenderingInfo(
									expressionValue, feedValue.getValueColor(), feedValue.getStatusText(), feedValue.getStatusColor(), feedValue.isValid()
									);
						}
					}
				} catch (NumberFormatException nfe){
						
				}
			}			
			return feedValue;
		}
	}
}
	

