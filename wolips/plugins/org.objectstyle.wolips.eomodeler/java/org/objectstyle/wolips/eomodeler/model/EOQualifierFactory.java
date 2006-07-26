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
package org.objectstyle.wolips.eomodeler.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import org.objectstyle.cayenne.exp.parser.ASTNot;
import org.objectstyle.cayenne.exp.parser.ASTNotEqual;
import org.objectstyle.cayenne.exp.parser.ASTObjPath;
import org.objectstyle.cayenne.exp.parser.ASTOr;
import org.objectstyle.cayenne.exp.parser.ASTPath;
import org.objectstyle.cayenne.exp.parser.AggregateConditionNode;
import org.objectstyle.cayenne.exp.parser.ConditionNode;
import org.objectstyle.cayenne.exp.parser.Node;
import org.objectstyle.wolips.eomodeler.utils.StringUtils;

public class EOQualifierFactory {
  public static Node createNodeFromQualifierMap(EOModelMap _qualifierMap) {
    Node node = null;
    if (_qualifierMap != null) {
      String className = _qualifierMap.getString("class", true);
      if ("EOAndQualifier".equals(className) || "com.webobjects.eocontrol.EOAndQualifier".equals(className)) {
        node = new ASTAnd(EOQualifierFactory.createNodesFromQualifierMaps(_qualifierMap.getList("qualifiers")));
      }
      else if ("EOOrQualifier".equals(className) || "com.webobjects.eocontrol.EOOrQualifier".equals(className)) {
        node = new ASTOr(EOQualifierFactory.createNodesFromQualifierMaps(_qualifierMap.getList("qualifiers")));
      }
      else if ("EONotQualifier".equals(className) || "com.webobjects.eocontrol.EONotQualifier".equals(className)) {
        node = new ASTNot(EOQualifierFactory.createNodeFromQualifierMap(new EOModelMap(_qualifierMap.getMap("qualifier"))));
      }
      else if ("EOKeyValueQualifier".equals(className) || "com.webobjects.eocontrol.EOKeyValueQualifier".equals(className)) {
        String key = _qualifierMap.getString("key", true);
        Object value = EOQualifierFactory.createValue(_qualifierMap.get("value"));
        String selectorName = _qualifierMap.getString("selectorName", true);
        node = EOQualifierFactory.createKeyValueQualifierNode(key, selectorName, value);
      }
      else if ("EOKeyComparisonQualifier".equals(className) || "com.webobjects.eocontrol.EOKeyComparisonQualifier".equals(className)) {
        String leftKey = _qualifierMap.getString("leftKey", true);
        String rightKey = _qualifierMap.getString("rightKey", true);
        String selectorName = _qualifierMap.getString("selectorName", true);
        node = EOQualifierFactory.createKeyValueQualifierNode(leftKey, selectorName, new ASTObjPath(rightKey));
      }
      else {
        throw new IllegalArgumentException("Unknown qualifier className '" + className + "'.");
      }
    }
    return node;
  }

  private static Node createKeyValueQualifierNode(Object _key, String _selectorName, Object _value) {
    Node node = null;
    ASTObjPath objPath = new ASTObjPath(_key);
    if (StringUtils.isSelectorNameEqual("isEqualTo", _selectorName)) {
      node = new ASTEqual(objPath, _value);
    }
    else if (StringUtils.isSelectorNameEqual("isNotEqualTo", _selectorName)) {
      node = new ASTNotEqual(objPath, _value);
    }
    else if (StringUtils.isSelectorNameEqual("isLessThan", _selectorName)) {
      node = new ASTLess(objPath, _value);
    }
    else if (StringUtils.isSelectorNameEqual("isGreaterThan", _selectorName)) {
      node = new ASTGreater(objPath, _value);
    }
    else if (StringUtils.isSelectorNameEqual("isLessThanOrEqualTo", _selectorName)) {
      node = new ASTLessOrEqual(objPath, _value);
    }
    else if (StringUtils.isSelectorNameEqual("isGreaterThanOrEqualTo", _selectorName)) {
      node = new ASTGreaterOrEqual(objPath, _value);
    }
    else if (StringUtils.isSelectorNameEqual("doesContain", _selectorName)) {
      //node = new ASTEqual(objPath, value);
      throw new IllegalArgumentException("Not sure what 'doesContain:' maps onto.");
    }
    else if (StringUtils.isSelectorNameEqual("isLike", _selectorName)) {
      node = new ASTLike(objPath, _value);
    }
    else if (StringUtils.isSelectorNameEqual("isCaseInsensitiveLike", _selectorName)) {
      node = new ASTLikeIgnoreCase(objPath, _value);
    }
    else {
      throw new IllegalArgumentException("Unknown selectorName '" + _selectorName + "'.");
    }
    return node;
  }

  private static Object createValue(Object _rawValue) {
    Object value;
    if (_rawValue instanceof Map) {
      EOModelMap valueMap = new EOModelMap((Map) _rawValue);
      String valueClass = valueMap.getString("class", true);
      if ("EONull".equals(valueClass) || "com.webobjects.eocontrol.EONull".equals(valueClass)) {
        value = null;
      }
      else if ("EOQualifierVariable".equals(valueClass) || "com.webobjects.eocontrol.EOQualifierVariable".equals(valueClass)) {
        String variableKey = valueMap.getString("_key", true);
        value = new ASTNamedParameter(variableKey);
      }
      else if ("NSNumber".equals(valueClass)) {
        value = valueMap.get("value");
      }
      else {
        throw new IllegalArgumentException("Unknown EOKeyValueQualifier value class " + valueClass);
      }
    }
    else {
      value = _rawValue;
    }
    return value;
  }

  private static Collection createNodesFromQualifierMaps(Collection _qualifiers) {
    List nodes = new LinkedList();
    if (_qualifiers != null) {
      Iterator qualifiersIter = _qualifiers.iterator();
      while (qualifiersIter.hasNext()) {
        Map qualifierMap = (Map) qualifiersIter.next();
        Node node = EOQualifierFactory.createNodeFromQualifierMap(new EOModelMap(qualifierMap));
        nodes.add(node);
      }
    }
    return nodes;
  }

  private static Object createQualifierValue(Object _value) {
    Object value;
    if (_value == null) {
      EOModelMap map = new EOModelMap();
      map.setString("class", "EONull", false);
      value = map;
    }
    else if (_value instanceof ASTNamedParameter) {
      EOModelMap map = new EOModelMap();
      String name = ((ExpressionParameter) ((ASTNamedParameter) _value).getValue()).getName();
      map.setString("_key", name, true);
      map.setString("class", "EOQualifierVariable", false);
      value = map;
    }
    else if (_value instanceof Number) {
      EOModelMap map = new EOModelMap();
      map.setString("class", "NSNumber", false);
      map.put("value", _value);
      value = map;
    }
    else if (_value instanceof ExpressionParameter) {
      EOModelMap map = new EOModelMap();
      String name = ((ExpressionParameter) _value).getName();
      map.setString("_key", name, true);
      map.setString("class", "EOQualifierVariable", false);
      value = map;
    }
    else if (_value instanceof String) {
      value = _value;
    }
    else {
      throw new IllegalArgumentException("Unknown qualifier value type: " + _value + " (type = " + _value.getClass().getName() + ")");
    }
    return value;
  }

  private static EOModelMap createQualifierMapFromConditionNode(ConditionNode _node, String _selectorName) {
    ASTPath path = (ASTPath) _node.getOperand(0);
    EOModelMap map = new EOModelMap();
    Object valueObj = _node.getOperand(1);
    if (valueObj instanceof ASTPath) {
      map.setString("class", "EOKeyComparisonQualifier", false);
      map.setString("leftKey", (String) path.getOperand(0), false);
      map.setString("rightKey", (String) ((ASTPath) valueObj).getOperand(0), false);
      map.setString("selectorName", _selectorName, false);
    }
    else {
      map.setString("class", "EOKeyValueQualifier", false);
      Object value = createQualifierValue(valueObj);
      map.setString("key", (String) path.getOperand(0), false);
      map.setString("selectorName", _selectorName, false);
      map.put("value", value);
    }
    return map;
  }

  private static List createQualifierMapsFromNode(AggregateConditionNode _node) {
    List qualifierMaps = new LinkedList();
    int operandCount = _node.getOperandCount();
    for (int operand = 0; operand < operandCount; operand++) {
      qualifierMaps.add(EOQualifierFactory.createQualifierMapFromNode((Node) _node.getOperand(operand)));
    }
    return qualifierMaps;
  }

  public static EOModelMap createQualifierMapFromNode(Node _node) {
    EOModelMap map;
    if (_node instanceof ASTEqual) {
      map = EOQualifierFactory.createQualifierMapFromConditionNode((ASTEqual) _node, "isEqualTo:");
    }
    else if (_node instanceof ASTNotEqual) {
      map = EOQualifierFactory.createQualifierMapFromConditionNode((ASTNotEqual) _node, "isNotEqualTo:");
    }
    else if (_node instanceof ASTLess) {
      map = EOQualifierFactory.createQualifierMapFromConditionNode((ASTLess) _node, "isLessThan:");
    }
    else if (_node instanceof ASTGreater) {
      map = EOQualifierFactory.createQualifierMapFromConditionNode((ASTGreater) _node, "isGreaterThan:");
    }
    else if (_node instanceof ASTLessOrEqual) {
      map = EOQualifierFactory.createQualifierMapFromConditionNode((ASTLessOrEqual) _node, "isLessThanOrEqualTo:");
    }
    else if (_node instanceof ASTGreaterOrEqual) {
      map = EOQualifierFactory.createQualifierMapFromConditionNode((ASTGreaterOrEqual) _node, "isGreaterThanOrEqualTo:");
    }
    else if (_node instanceof ASTLike) {
      map = EOQualifierFactory.createQualifierMapFromConditionNode((ASTLike) _node, "isLike:");
    }
    else if (_node instanceof ASTLikeIgnoreCase) {
      map = EOQualifierFactory.createQualifierMapFromConditionNode((ASTLikeIgnoreCase) _node, "isCaseInsensitiveLike:");
    }
    else if (_node instanceof ASTAnd) {
      map = new EOModelMap();
      map.setString("class", "EOAndQualifier", false);//$NON-NLS-2$
      map.setList("qualifiers", createQualifierMapsFromNode((ASTAnd) _node), true);
    }
    else if (_node instanceof ASTOr) {
      map = new EOModelMap();
      map.setString("class", "EOOrQualifier", false);
      map.setList("qualifiers", createQualifierMapsFromNode((ASTOr) _node), true);
    }
    else if (_node instanceof ASTNot) {
      map = new EOModelMap();
      map.setString("class", "EONotQualifier", false);//$NON-NLS-2$
      map.setMap("qualifier", createQualifierMapFromNode((Node) ((ASTNot) _node).getOperand(0)), true);
    }
    else {
      throw new IllegalArgumentException("Unknown node " + _node + ".");
    }
    return map;
  }
}