/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.eomodeler.core.model;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectstyle.cayenne.exp.Expression;
import org.objectstyle.cayenne.exp.ExpressionParameter;
import org.objectstyle.cayenne.exp.parser.ASTAnd;
import org.objectstyle.cayenne.exp.parser.ASTEqual;
import org.objectstyle.cayenne.exp.parser.ASTGreater;
import org.objectstyle.cayenne.exp.parser.ASTGreaterOrEqual;
import org.objectstyle.cayenne.exp.parser.ASTLess;
import org.objectstyle.cayenne.exp.parser.ASTLessOrEqual;
import org.objectstyle.cayenne.exp.parser.ASTLike;
import org.objectstyle.cayenne.exp.parser.ASTLikeIgnoreCase;
import org.objectstyle.cayenne.exp.parser.ASTNamedParameter;
import org.objectstyle.cayenne.exp.parser.ASTNegate;
import org.objectstyle.cayenne.exp.parser.ASTNot;
import org.objectstyle.cayenne.exp.parser.ASTNotEqual;
import org.objectstyle.cayenne.exp.parser.ASTObjPath;
import org.objectstyle.cayenne.exp.parser.ASTOr;
import org.objectstyle.cayenne.exp.parser.ASTPath;
import org.objectstyle.cayenne.exp.parser.AggregateConditionNode;
import org.objectstyle.cayenne.exp.parser.ConditionNode;
import org.objectstyle.cayenne.exp.parser.Node;
import org.objectstyle.wolips.baseforplugins.util.StringUtils;
import org.objectstyle.wolips.eomodeler.core.model.qualifier.EOQualifierBinding;
import org.objectstyle.wolips.eomodeler.core.utils.BooleanUtils;

public class EOQualifierFactory {
	public static Expression fromString(String _str) {
		Expression exp;
		if (_str == null || _str.trim().length() == 0) {
			exp = null;
		} else {
			exp = Expression.fromString(_str);
		}
		return exp;
	}

	public static String toString(Expression _exp) {
		String qualifierString;
		if (_exp == null) {
			qualifierString = null;
		} else {
			StringWriter sw = new StringWriter();
			_exp.encodeAsString(new PrintWriter(sw));
			qualifierString = sw.getBuffer().toString();
		}
		return qualifierString;
	}

	public static Expression createExpressionFromQualifierMap(EOModelMap _qualifierMap) {
		Expression exp = null;
		if (_qualifierMap != null) {
			String className = _qualifierMap.getString("class", true);
			if ("EOAndQualifier".equals(className) || "com.webobjects.eocontrol.EOAndQualifier".equals(className)) {
				exp = new ASTAnd(EOQualifierFactory.createExpressionsFromQualifierMaps(_qualifierMap.getList("qualifiers")));
			} else if ("EOOrQualifier".equals(className) || "com.webobjects.eocontrol.EOOrQualifier".equals(className)) {
				exp = new ASTOr(EOQualifierFactory.createExpressionsFromQualifierMaps(_qualifierMap.getList("qualifiers")));
			} else if ("EONotQualifier".equals(className) || "com.webobjects.eocontrol.EONotQualifier".equals(className)) {
				exp = EOQualifierFactory.createExpressionFromQualifierMap(new EOModelMap(_qualifierMap.getMap("qualifier"))).notExp();
			} else if ("EOKeyValueQualifier".equals(className) || "com.webobjects.eocontrol.EOKeyValueQualifier".equals(className)) {
				String key = _qualifierMap.getString("key", true);
				Object value = EOQualifierFactory.createValue(_qualifierMap.get("value"));
				String selectorName = _qualifierMap.getString("selectorName", true);
				exp = EOQualifierFactory.createKeyValueExpression(key, selectorName, value);
			} else if ("EOKeyComparisonQualifier".equals(className) || "com.webobjects.eocontrol.EOKeyComparisonQualifier".equals(className)) {
				String leftKey = _qualifierMap.getString("leftKey", true);
				String rightKey = _qualifierMap.getString("rightKey", true);
				String selectorName = _qualifierMap.getString("selectorName", true);
				exp = EOQualifierFactory.createKeyValueExpression(leftKey, selectorName, new ASTObjPath(rightKey));
			} else {
				throw new IllegalArgumentException("Unknown qualifier className '" + className + "'.");
			}
			if (exp instanceof Node) {
				int operandCount = exp.getOperandCount();
				for (int operand = 0; operand < operandCount; operand++) {
					Object obj = exp.getOperand(operand);
					if (obj instanceof Node) {
						((Node) obj).jjtSetParent((Node) exp);
					}
				}
			}
		}
		return exp;
	}

	private static Expression createKeyValueExpression(Object _key, String _selectorName, Object _value) {
		Expression expression = null;
		ASTObjPath objPath = new ASTObjPath(_key);
		if (StringUtils.isSelectorNameEqual("isEqualTo", _selectorName)) {
			expression = new ASTEqual(objPath, _value);
		} else if (StringUtils.isSelectorNameEqual("isNotEqualTo", _selectorName)) {
			expression = new ASTNotEqual(objPath, _value);
		} else if (StringUtils.isSelectorNameEqual("isLessThan", _selectorName)) {
			expression = new ASTLess(objPath, _value);
		} else if (StringUtils.isSelectorNameEqual("isGreaterThan", _selectorName)) {
			expression = new ASTGreater(objPath, _value);
		} else if (StringUtils.isSelectorNameEqual("isLessThanOrEqualTo", _selectorName)) {
			expression = new ASTLessOrEqual(objPath, _value);
		} else if (StringUtils.isSelectorNameEqual("isGreaterThanOrEqualTo", _selectorName)) {
			expression = new ASTGreaterOrEqual(objPath, _value);
		} else if (StringUtils.isSelectorNameEqual("doesContain", _selectorName)) {
			// node = new ASTEqual(objPath, value);
			throw new IllegalArgumentException("Not sure what 'doesContain:' maps onto.");
		} else if (StringUtils.isSelectorNameEqual("isLike", _selectorName)) {
			expression = new ASTLike(objPath, _value);
		} else if (StringUtils.isSelectorNameEqual("isCaseInsensitiveLike", _selectorName)) {
			expression = new ASTLikeIgnoreCase(objPath, _value);
		} else {
			throw new IllegalArgumentException("Unknown selectorName '" + _selectorName + "'.");
		}
		return expression;
	}

	private static Object createValue(Object _rawValue) {
		Object value;
		if (_rawValue instanceof Map) {
			EOModelMap valueMap = new EOModelMap((Map) _rawValue);
			String valueClass = valueMap.getString("class", true);
			if ("EONull".equals(valueClass) || "com.webobjects.eocontrol.EONull".equals(valueClass)) {
				value = null;
			} else if ("EOQualifierVariable".equals(valueClass) || "com.webobjects.eocontrol.EOQualifierVariable".equals(valueClass)) {
				String variableKey = valueMap.getString("_key", true);
				value = new ASTNamedParameter(variableKey);
			} else if ("NSNumber".equals(valueClass)) {
				value = valueMap.get("value");
				if (value instanceof String) {
					String valueStr = (String) value;
					if (valueStr.indexOf('.') == -1) {
						value = Integer.parseInt(valueStr);
						if (!String.valueOf(value).equals(valueStr)) {
							value = Long.parseLong(valueStr);
						}
					} else {
						value = Float.parseFloat(valueStr);
					}
				}
			} else {
				throw new IllegalArgumentException("Unknown EOKeyValueQualifier value class " + valueClass);
			}
		} else {
			value = _rawValue;
		}
		return value;
	}

	private static Collection<Expression> createExpressionsFromQualifierMaps(Collection<Map<Object, Object>> _qualifiers) {
		List<Expression> expressions = new LinkedList<Expression>();
		if (_qualifiers != null) {
			for (Map<Object, Object> qualifierMap : _qualifiers) {
				Expression exp = EOQualifierFactory.createExpressionFromQualifierMap(new EOModelMap(qualifierMap));
				expressions.add(exp);
			}
		}
		return expressions;
	}

	private static Object createQualifierValue(Object _value) {
		Object value;
		if (_value == null) {
			EOModelMap map = new EOModelMap();
			map.setString("class", "EONull", false);
			value = map;
		} else if (_value instanceof ASTNamedParameter) {
			EOModelMap map = new EOModelMap();
			String name = ((ExpressionParameter) ((ASTNamedParameter) _value).getValue()).getName();
			map.setString("_key", name, true);
			map.setString("class", "EOQualifierVariable", false);
			value = map;
		} else if (_value instanceof Number) {
			EOModelMap map = new EOModelMap();
			map.setString("class", "NSNumber", false);
			map.put("value", _value);
			value = map;
		} else if (_value instanceof Boolean) {
			EOModelMap map = new EOModelMap();
			map.setString("class", "NSNumber", false);
			map.put("value", _value);
			value = map;
		} else if (_value instanceof ExpressionParameter) {
			EOModelMap map = new EOModelMap();
			String name = ((ExpressionParameter) _value).getName();
			map.setString("_key", name, true);
			map.setString("class", "EOQualifierVariable", false);
			value = map;
		} else if (_value instanceof String) {
			value = _value;
		} else if (_value instanceof ASTNegate) {
			Object operand = ((ASTNegate) _value).getOperand(0);
			EOModelMap map = new EOModelMap();
			map.setString("class", "NSNumber", false);
			if (operand instanceof Integer) {
				map.put("value", new Integer(((Integer) operand).intValue() * -1));
			} else if (operand instanceof Float) {
				map.put("value", new Float(((Float) operand).floatValue() * -1.0f));
			} else if (operand instanceof Double) {
				map.put("value", new Double(((Double) operand).doubleValue() * -1.0));
			} else if (operand instanceof Long) {
				map.put("value", new Long(((Long) operand).longValue() * -1L));
			} else {
				throw new IllegalArgumentException("Unknown qualifier value type: negate " + operand + " (type = " + operand.getClass().getName() + ")");
			}
			value = map;
		} else {
			throw new IllegalArgumentException("Unknown qualifier value type: " + _value + " (type = " + _value.getClass().getName() + ")");
		}
		return value;
	}

	private static EOModelMap createQualifierMapFromConditionNode(ConditionNode _node, String _selectorName) {
		Object leftValue = _node.getOperand(0);
		Object rightValue = _node.getOperand(1);

		boolean keyValueQualifier = true;
		EOModelMap map = new EOModelMap();
		if (leftValue instanceof ASTPath && rightValue instanceof ASTPath) {
			String leftKey = (String) ((ASTPath) leftValue).getOperand(0);
			String rightKey = (String) ((ASTPath) rightValue).getOperand(0);

			if (BooleanUtils.isTrue(leftKey)) {
				leftValue = Integer.valueOf(1);
				keyValueQualifier = true;
			} else if (BooleanUtils.isFalse(leftKey)) {
				leftValue = Integer.valueOf(0);
				keyValueQualifier = true;
			} else if (BooleanUtils.isTrue(rightKey)) {
				rightValue = Integer.valueOf(1);
				keyValueQualifier = true;
			} else if (BooleanUtils.isFalse(rightKey)) {
				rightValue = Integer.valueOf(0);
				keyValueQualifier = true;
			} else {
				keyValueQualifier = false;
			}

			if (!keyValueQualifier) {
				map.setString("class", "EOKeyComparisonQualifier", false);
				map.setString("leftKey", leftKey, false);
				map.setString("rightKey", rightKey, false);
				map.setString("selectorName", _selectorName, false);
			}
		}

		if (keyValueQualifier) {
			String key;
			Object value;
			if (leftValue instanceof ASTPath) {
				key = (String) ((ASTPath) leftValue).getOperand(0);
				value = rightValue;
			} else if (rightValue instanceof ASTPath) {
				key = (String) ((ASTPath) rightValue).getOperand(0);
				value = leftValue;
			} else {
				throw new IllegalArgumentException("There is no known qualifier that can compare " + leftValue + " and " + rightValue + ".");
			}

			map.setString("class", "EOKeyValueQualifier", false);
			Object processedValue = createQualifierValue(value);
			map.setString("key", key, false);
			map.setString("selectorName", _selectorName, false);
			map.put("value", processedValue);
		}
		return map;
	}

	private static List<EOModelMap> createQualifierMapsFromAggregateConditionNode(AggregateConditionNode _node) {
		List<EOModelMap> qualifierMaps = new LinkedList<EOModelMap>();
		int operandCount = _node.getOperandCount();
		for (int operand = 0; operand < operandCount; operand++) {
			qualifierMaps.add(EOQualifierFactory.createQualifierMapFromExpression((Expression) _node.getOperand(operand)));
		}
		return qualifierMaps;
	}

	public static EOModelMap createQualifierMapFromExpression(Expression _expression) {
		EOModelMap map;
		if (_expression instanceof ASTEqual) {
			map = EOQualifierFactory.createQualifierMapFromConditionNode((ASTEqual) _expression, "isEqualTo:");
		} else if (_expression instanceof ASTNotEqual) {
			map = EOQualifierFactory.createQualifierMapFromConditionNode((ASTNotEqual) _expression, "isNotEqualTo:");
		} else if (_expression instanceof ASTLess) {
			map = EOQualifierFactory.createQualifierMapFromConditionNode((ASTLess) _expression, "isLessThan:");
		} else if (_expression instanceof ASTGreater) {
			map = EOQualifierFactory.createQualifierMapFromConditionNode((ASTGreater) _expression, "isGreaterThan:");
		} else if (_expression instanceof ASTLessOrEqual) {
			map = EOQualifierFactory.createQualifierMapFromConditionNode((ASTLessOrEqual) _expression, "isLessThanOrEqualTo:");
		} else if (_expression instanceof ASTGreaterOrEqual) {
			map = EOQualifierFactory.createQualifierMapFromConditionNode((ASTGreaterOrEqual) _expression, "isGreaterThanOrEqualTo:");
		} else if (_expression instanceof ASTLike) {
			map = EOQualifierFactory.createQualifierMapFromConditionNode((ASTLike) _expression, "isLike:");
		} else if (_expression instanceof ASTLikeIgnoreCase) {
			map = EOQualifierFactory.createQualifierMapFromConditionNode((ASTLikeIgnoreCase) _expression, "isCaseInsensitiveLike:");
		} else if (_expression instanceof ASTAnd) {
			map = new EOModelMap();
			map.setString("class", "EOAndQualifier", false);//$NON-NLS-2$
			map.setList("qualifiers", createQualifierMapsFromAggregateConditionNode((ASTAnd) _expression), true);
		} else if (_expression instanceof ASTOr) {
			map = new EOModelMap();
			map.setString("class", "EOOrQualifier", false);
			map.setList("qualifiers", createQualifierMapsFromAggregateConditionNode((ASTOr) _expression), true);
		} else if (_expression instanceof ASTNot) {
			map = new EOModelMap();
			map.setString("class", "EONotQualifier", false);//$NON-NLS-2$
			map.setMap("qualifier", createQualifierMapFromExpression((Expression) ((ASTNot) _expression).getOperand(0)), true);
		} else {
			throw new IllegalArgumentException("Unknown expression " + _expression + ".");
		}
		return map;
	}

	public static Set<String> getQualifierKeysFromQualifierString(String qualifierString) {
		Expression expression = EOQualifierFactory.fromString(qualifierString);
		return EOQualifierFactory.getQualifierKeysFromExpression(expression);
	}

	public static Set<String> getQualifierKeysFromExpression(Expression expression) {
		Set<String> keys = new HashSet<String>();
		try {
			EOQualifierFactory.fillInQualifierKeysFromExpression(expression, keys);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return keys;
	}

	public static void fillInQualifierKeysFromExpression(Expression expression, Set<String> keys) {
		if (expression instanceof ConditionNode) {
			String key = (String) ((ASTPath) expression.getOperand(0)).getOperand(0);
			Object value = expression.getOperand(1);
			System.out.println("EOQualifierFactory.fillInQualifierKeysFromExpression: " + value);

			// String name = ((ExpressionParameter) ((ASTNamedParameter)
			// _value).getValue()).getName();
			keys.add(key);
		} else if (expression instanceof AggregateConditionNode) {
			int operandCount = expression.getOperandCount();
			for (int operand = 0; operand < operandCount; operand++) {
				EOQualifierFactory.fillInQualifierKeysFromExpression((Expression) expression.getOperand(operand), keys);
			}
		} else if (expression instanceof ASTNot) {
			EOQualifierFactory.fillInQualifierKeysFromExpression((Expression) ((ASTNot) expression).getOperand(0), keys);
		} else {
			throw new IllegalArgumentException("Unknown expression " + expression + ".");
		}
	}

	public static List<EOQualifierBinding> getQualifierBindingsFromQualifierString(EOEntity entity, String qualifierString) {
		Expression expression = EOQualifierFactory.fromString(qualifierString);
		return EOQualifierFactory.getQualifierBindingsFromExpression(entity, expression);
	}

	public static List<EOQualifierBinding> getQualifierBindingsFromExpression(EOEntity entity, Expression expression) {
		List<EOQualifierBinding> bindings = new LinkedList<EOQualifierBinding>();
		try {
			EOQualifierFactory.fillInQualifierBindingsFromExpression(entity, expression, bindings);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return bindings;
	}

	public static void fillInQualifierBindingsFromExpression(EOEntity entity, Expression expression, List<EOQualifierBinding> bindings) {
		if (expression instanceof ConditionNode) {
			String key = (String) ((ASTPath) expression.getOperand(0)).getOperand(0);
			Object value = expression.getOperand(1);
			if (value instanceof ASTNamedParameter) {
				value = ((ASTNamedParameter) value).getValue();
			}

			if (value instanceof ExpressionParameter) {
				String bindingName = ((ExpressionParameter) value).getName();
				EOQualifierBinding binding = new EOQualifierBinding(entity, bindingName, key);
				bindings.add(binding);
			}
		} else if (expression instanceof AggregateConditionNode) {
			int operandCount = expression.getOperandCount();
			for (int operand = 0; operand < operandCount; operand++) {
				EOQualifierFactory.fillInQualifierBindingsFromExpression(entity, (Expression) expression.getOperand(operand), bindings);
			}
		} else if (expression instanceof ASTNot) {
			EOQualifierFactory.fillInQualifierBindingsFromExpression(entity, (Expression) ((ASTNot) expression).getOperand(0), bindings);
		} else {
			throw new IllegalArgumentException("Unknown expression " + expression + ".");
		}
	}

}