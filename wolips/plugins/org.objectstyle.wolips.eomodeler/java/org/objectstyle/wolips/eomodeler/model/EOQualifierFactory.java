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

public class EOQualifierFactory {
  public static Node createNodeFromQualifierMap(EOModelMap _qualifierMap) {
    Node node = null;
    if (_qualifierMap != null) {
      String className = _qualifierMap.getString("class", true); //$NON-NLS-1$
      if ("EOAndQualifier".equals(className)) { //$NON-NLS-1$
        node = new ASTAnd(EOQualifierFactory.createNodesFromQualifierMaps(_qualifierMap.getList("qualifiers"))); //$NON-NLS-1$
      }
      else if ("EOOrQualifier".equals(className)) { //$NON-NLS-1$
        node = new ASTOr(EOQualifierFactory.createNodesFromQualifierMaps(_qualifierMap.getList("qualifiers"))); //$NON-NLS-1$
      }
      else if ("EONotQualifier".equals(className)) { //$NON-NLS-1$
        node = new ASTNot(EOQualifierFactory.createNodeFromQualifierMap(new EOModelMap(_qualifierMap.getMap("qualifier")))); //$NON-NLS-1$
      }
      else if ("EOKeyValueQualifier".equals(className)) { //$NON-NLS-1$
        String key = _qualifierMap.getString("key", true); //$NON-NLS-1$
        Object rawValue = _qualifierMap.get("value"); //$NON-NLS-1$
        Object value;
        if (rawValue instanceof Map) {
          EOModelMap valueMap = new EOModelMap((Map) rawValue);
          String valueClass = valueMap.getString("class", true); //$NON-NLS-1$
          if ("EONull".equals(valueClass)) { //$NON-NLS-1$
            value = null;
          }
          else if ("EOQualifierVariable".equals(valueClass)) { //$NON-NLS-1$
            String variableKey = valueMap.getString("_key", true); //$NON-NLS-1$
            value = new ASTNamedParameter(variableKey);
          }
          else if ("NSNumber".equals(valueClass)) { //$NON-NLS-1$
            value = valueMap.get("value"); //$NON-NLS-1$
          }
          else {
            throw new IllegalArgumentException("Unknown EOKeyValueQualifier value class " + valueClass);
          }
        }
        else {
          value = rawValue;
        }
        String selectorName = _qualifierMap.getString("selectorName", true); //$NON-NLS-1$
        ASTObjPath objPath = new ASTObjPath(key);
        if ("isEqualTo:".equals(selectorName)) { //$NON-NLS-1$
          node = new ASTEqual(objPath, value);
        }
        else if ("isNotEqualTo:".equals(selectorName)) { //$NON-NLS-1$
          node = new ASTNotEqual(objPath, value);
        }
        else if ("isLessThan:".equals(selectorName)) { //$NON-NLS-1$
          node = new ASTLess(objPath, value);
        }
        else if ("isGreaterThan:".equals(selectorName)) { //$NON-NLS-1$
          node = new ASTGreater(objPath, value);
        }
        else if ("isLessThanOrEqualTo:".equals(selectorName)) { //$NON-NLS-1$
          node = new ASTLessOrEqual(objPath, value);
        }
        else if ("isGreaterThanOrEqualTo:".equals(selectorName)) { //$NON-NLS-1$
          node = new ASTGreaterOrEqual(objPath, value);
        }
        else if ("doesContain:".equals(selectorName)) { //$NON-NLS-1$
          //node = new ASTEqual(objPath, value);
          throw new IllegalArgumentException("Not sure what 'doesContain:' maps onto.");
        }
        else if ("isLike:".equals(selectorName)) { //$NON-NLS-1$
          node = new ASTLike(objPath, value);
        }
        else if ("isCaseInsensitiveLike:".equals(selectorName)) { //$NON-NLS-1$
          node = new ASTLikeIgnoreCase(objPath, value);
        }
        else {
          throw new IllegalArgumentException("Unknown selectorName '" + selectorName + "'.");
        }
      }
      else {
        throw new IllegalArgumentException("Unknown qualifier className '" + className + "'.");
      }
    }
    return node;
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
      map.setString("class", "EONull", false); //$NON-NLS-1$ //$NON-NLS-2$
      value = map;
    }
    else if (_value instanceof ASTNamedParameter) {
      EOModelMap map = new EOModelMap();
      String name = ((ExpressionParameter) ((ASTNamedParameter) _value).getValue()).getName();
      map.setString("_key", name, true); //$NON-NLS-1$
      map.setString("class", "EOQualifierVariable", false); //$NON-NLS-1$ //$NON-NLS-2$
      value = map;
    }
    else if (_value instanceof Number) {
      EOModelMap map = new EOModelMap();
      map.setString("class", "NSNumber", false); //$NON-NLS-1$ //$NON-NLS-2$
      map.put("value", _value); //$NON-NLS-1$
      value = map;
    }
    else {
      value = _value;
    }
    return value;
  }

  private static EOModelMap createQualifierMapFromConditionNode(ConditionNode _node, String _selectorName) {
    ASTPath path = (ASTPath) _node.getOperand(0);
    EOModelMap map = new EOModelMap();
    map.setString("class", "EOKeyValueQualifier", false); //$NON-NLS-1$ //$NON-NLS-2$
    map.setString("key", (String) path.getOperand(0), false); //$NON-NLS-1$
    map.setString("selectorName", _selectorName, false); //$NON-NLS-1$
    map.put("value", createQualifierValue(_node.getOperand(1))); //$NON-NLS-1$
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
      map = EOQualifierFactory.createQualifierMapFromConditionNode((ASTEqual) _node, "isEqualTo:"); //$NON-NLS-1$
    }
    else if (_node instanceof ASTNotEqual) {
      map = EOQualifierFactory.createQualifierMapFromConditionNode((ASTNotEqual) _node, "isNotEqual:"); //$NON-NLS-1$
    }
    else if (_node instanceof ASTLess) {
      map = EOQualifierFactory.createQualifierMapFromConditionNode((ASTLess) _node, "isLessThan:"); //$NON-NLS-1$
    }
    else if (_node instanceof ASTGreater) {
      map = EOQualifierFactory.createQualifierMapFromConditionNode((ASTGreater) _node, "isGreaterThan:"); //$NON-NLS-1$
    }
    else if (_node instanceof ASTLessOrEqual) {
      map = EOQualifierFactory.createQualifierMapFromConditionNode((ASTLessOrEqual) _node, "isLessThanOrEqualTo:"); //$NON-NLS-1$
    }
    else if (_node instanceof ASTGreaterOrEqual) {
      map = EOQualifierFactory.createQualifierMapFromConditionNode((ASTGreaterOrEqual) _node, "isGreaterThanOrEqualTo:"); //$NON-NLS-1$
    }
    else if (_node instanceof ASTLike) {
      map = EOQualifierFactory.createQualifierMapFromConditionNode((ASTLike) _node, "isLike:"); //$NON-NLS-1$
    }
    else if (_node instanceof ASTLikeIgnoreCase) {
      map = EOQualifierFactory.createQualifierMapFromConditionNode((ASTLikeIgnoreCase) _node, "isCaseInsensitiveLike:"); //$NON-NLS-1$
    }
    else if (_node instanceof ASTAnd) {
      map = new EOModelMap();
      map.setString("class", "EOAndQualifier", false); //$NON-NLS-1$//$NON-NLS-2$
      map.setList("qualifiers", createQualifierMapsFromNode((ASTAnd) _node), true); //$NON-NLS-1$
    }
    else if (_node instanceof ASTOr) {
      map = new EOModelMap();
      map.setString("class", "EOOrQualifier", false); //$NON-NLS-1$ //$NON-NLS-2$
      map.setList("qualifiers", createQualifierMapsFromNode((ASTOr) _node), true); //$NON-NLS-1$
    }
    else if (_node instanceof ASTNot) {
      map = new EOModelMap();
      map.setString("class", "EONotQualifier", false); //$NON-NLS-1$//$NON-NLS-2$
      map.setMap("qualifier", createQualifierMapFromNode((Node) ((ASTNot) _node).getOperand(0)), true); //$NON-NLS-1$
    }
    else {
      throw new IllegalArgumentException("Unknown node " + _node + ".");
    }
    return map;
  }
}