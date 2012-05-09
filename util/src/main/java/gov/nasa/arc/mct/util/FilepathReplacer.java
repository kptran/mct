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
package gov.nasa.arc.mct.util;

import gov.nasa.arc.mct.util.logging.MCTLogger;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Replaces variables in an expression.
 *
 */
public class FilepathReplacer {
    private static MCTLogger logger = MCTLogger.getLogger(FilepathReplacer.class);
    private static final String regex = "([^\\)]*)(%\\()([\\w]+)(\\))"; // four groups
    private static final Pattern pattern = Pattern.compile(regex);

    /**
     * Substitutes evar values in an expression. Evar names are delineated with %() markers.
     * For example, when evar cat evaluates to dog, the expression "A %(cat) in the yard" 
     * results in "A dog in the yard". If evar cat is unset the empty string is substituted.
     * A legal evar name is one or more regex word chars, that is [a-zA-Z_0-9]+.
     * 
     * @param expression the expression
     * @return the expression with values substituted.
     */
    public static String substitute(String expression) {
        return substitute(expression, System.getProperties());
    }

    /**
     * Substitutes expression.
     * @param expression - the string.
     * @param evars - environment variables.
     * @return the substitute string.
     */
    static String substitute(String expression, Properties evars) {
        assert evars != null && expression != null;
        Matcher m = pattern.matcher(expression);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String evarReplacement = evars.getProperty(m.group(3));
            if (evarReplacement == null) {
                logger.error(String.format("Expression: %s substituting empty string because evar %s is undefined", expression, m.group(3)));
                evarReplacement = "";
            } 
            String beforeReplacement = m.group(1);
            sb.append(beforeReplacement + evarReplacement);
            expression = expression.substring(m.end());
            m = pattern.matcher(expression);
        }
        return sb.toString() + expression;
    }
}
