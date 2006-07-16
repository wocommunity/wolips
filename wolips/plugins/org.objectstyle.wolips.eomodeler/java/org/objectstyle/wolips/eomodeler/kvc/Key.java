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
package org.objectstyle.wolips.eomodeler.kvc;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class Key implements IKey {
  private static final int GET = 1;
  private static final int SET = 2;
  private static final String[] GET_METHOD_PREFIXES = { "get", "", "_", "_get", "is", "_is" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
  private static final String[] SET_METHOD_PREFIXES = { "set", "", "_", "_set" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
  private static final String[] FIELD_PREFIXES = { "", "_" }; //$NON-NLS-1$ //$NON-NLS-2$

  private String myName;

  public Key(String _name) {
    myName = _name;
  }

  public ResolvedKey toResolvedKey(Class _declaringClass) {
    return new ResolvedKey(_declaringClass, myName);
  }

  public String getName() {
    return myName;
  }

  public Class getType(Object _instance) {
    Member getMember = getGetMember(_instance);
    Class nextClass;
    if (getMember instanceof Method) {
      nextClass = ((Method) getMember).getReturnType();
    }
    else {
      nextClass = ((Field) getMember).getType();
    }
    return nextClass;
  }

  protected Class getClass(Object _instance) {
    Class clazz;
    if (_instance == null) {
      clazz = null;
    }
    else {
      clazz = _instance.getClass();
    }
    return clazz;
  }

  protected Member getSetMember(Object _instance) {
    Member setMember;
    Class clazz = getClass(_instance);
    if (clazz == null) {
      setMember = null;
    }
    else {
      Map members = new HashMap();
      Class currentClass = clazz;
      while (currentClass != null) {
        hashMembers(currentClass.getDeclaredFields(), members, Key.SET);
        currentClass = currentClass.getSuperclass();
      }
      currentClass = clazz;
      while (currentClass != null) {
        hashMembers(currentClass.getDeclaredMethods(), members, Key.SET);
        currentClass = currentClass.getSuperclass();
      }
      setMember = getMemberWithPrefixes(myName, Key.SET_METHOD_PREFIXES, members);
      if (setMember == null) {
        setMember = getMemberWithPrefixes(myName, Key.FIELD_PREFIXES, members);
        if (setMember == null) {
          throw new IllegalArgumentException("There is no set-method named '" + myName + "' on the class " + clazz.getName() + ".");
        }
      }
    }
    return setMember;
  }

  protected Member getGetMember(Object _instance) {
    Member getMember;
    Class clazz = getClass(_instance);
    if (clazz == null) {
      getMember = null;
    }
    else {
      Map members = new HashMap();
      Class currentClass = clazz;
      while (currentClass != null) {
        hashMembers(currentClass.getDeclaredFields(), members, Key.GET);
        currentClass = currentClass.getSuperclass();
      }
      currentClass = clazz;
      while (currentClass != null) {
        hashMembers(currentClass.getDeclaredMethods(), members, Key.GET);
        currentClass = currentClass.getSuperclass();
      }
      getMember = getMemberWithPrefixes(myName, Key.GET_METHOD_PREFIXES, members);
      if (getMember == null) {
        getMember = getMemberWithPrefixes(myName, Key.FIELD_PREFIXES, members);
        if (getMember == null) {
          throw new IllegalArgumentException("There is no get-method named '" + myName + "' on the class " + clazz.getName() + ".");
        }
      }
    }
    return getMember;
  }

  public void setValue(Object _instance, Object _value) {
    try {
      Member setMember = getSetMember(_instance);
      if (setMember instanceof Method) {
        ((Method) setMember).invoke(_instance, new Object[] { _value });
      }
      else if (setMember instanceof Field) {
        ((Field) setMember).set(_instance, _value);
      }
      else {
        throw new IllegalArgumentException("Unknown type of member '" + setMember + "'.");
      }
    }
    catch (Exception e) {
      throw new RuntimeException("Failed to set value of '" + myName + "' on " + _instance, e);
    }
  }

  public Object getValue(Object _instance) {
    try {
      Object value;
      Member getMember = getGetMember(_instance);
      if (getMember instanceof Method) {
        value = ((Method) getMember).invoke(_instance, null);
      }
      else if (getMember instanceof Field) {
        value = ((Field) getMember).get(_instance);
      }
      else {
        throw new IllegalArgumentException("Unknown type of member '" + getMember + "'.");
      }
      return value;
    }
    catch (Exception e) {
      throw new RuntimeException("Failed to get value of '" + myName + "' on " + _instance, e);
    }
  }

  protected void hashMembers(Member[] _members, Map _membersMap, int _getOrSet) {
    for (int memberNum = 0; memberNum < _members.length; memberNum++) {
      Member member = _members[memberNum];
      int modifiers = member.getModifiers();
      if (Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers)) {
        boolean matches = false;
        if (_getOrSet == Key.GET) {
          if (member instanceof Field) {
            matches = true;
          }
          else {
            Method method = (Method) member;
            matches = method.getReturnType() != null && method.getParameterTypes().length == 0;
          }
        }
        else if (_getOrSet == Key.SET) {
          if (member instanceof Field) {
            matches = true;
          }
          else {
            Method method = (Method) member;
            matches = method.getReturnType() == void.class && method.getParameterTypes().length == 1;
          }
        }
        if (matches) {
          _membersMap.put(member.getName(), member);
        }
      }
    }
  }

  protected Member getMemberWithPrefixes(String _key, String[] _prefixes, Map _membersMap) {
    Member matchingMember = null;
    for (int prefixNum = 0; matchingMember == null && prefixNum < _prefixes.length; prefixNum++) {
      String prefix = _prefixes[prefixNum];
      boolean capitalize = prefix.length() > 1; // Don't capitalize blank and _ prefixes
      String keyWithPrefix = prependToKey(prefix, _key, capitalize);
      matchingMember = (Member) _membersMap.get(keyWithPrefix);
    }
    return matchingMember;
  }

  protected String prependToKey(String _prepend, String _key, boolean _capitalize) {
    StringBuffer sb = new StringBuffer();
    sb.append(_prepend);
    if (_capitalize) {
      sb.append(Character.toUpperCase(_key.charAt(0)));
      sb.append(_key.substring(1));
    }
    else {
      sb.append(_key);
    }
    return sb.toString();
  }

  public String toString() {
    return "[Key: " + myName + "]";
  }
}