package org.objectstyle.wolips.eomodeler.core.kvc;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class MemberContainer {
	public static final int GET = 1;

	public static final int SET = 2;

	private static final String[] GET_METHOD_PREFIXES = { "get", "", "_", "_get", "is", "_is" }; //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

	private static final String[] SET_METHOD_PREFIXES = { "set", "", "_", "_set" }; //$NON-NLS-4$

	private static final String[] FIELD_PREFIXES = { "", "_" };

	private int myMemberType;

	private Class myClass;

	private Map<String, IKey> myMembers;

	public MemberContainer(Class _class, int _memberType) {
		myMembers = new HashMap<String, IKey>();
		myClass = _class;
		myMemberType = _memberType;
		Class currentClass = _class;
		while (currentClass != null) {
			hashMembers(currentClass.getDeclaredFields());
			currentClass = currentClass.getSuperclass();
		}
		currentClass = _class;
		while (currentClass != null) {
			hashMembers(currentClass.getDeclaredMethods());
			currentClass = currentClass.getSuperclass();
		}
	}

	public IKey getMember(Object _instance, String _name) {
		String[] prefixes = (myMemberType == MemberContainer.GET) ? MemberContainer.GET_METHOD_PREFIXES : MemberContainer.SET_METHOD_PREFIXES;
		IKey setMember = getMemberWithPrefixes(_name, prefixes);
		if (setMember == null) {
			setMember = getMemberWithPrefixes(_name, MemberContainer.FIELD_PREFIXES);
			if (setMember == null) {
				if (_instance instanceof Map) {
					setMember = new MapKey(_name);
				} else {
					throw new IllegalArgumentException("There is no set-method named '" + _name + "' on the class " + myClass.getName() + ".");
				}
			}
		}
		return setMember;
	}

	protected void hashMembers(Member[] _members) {
		for (int memberNum = 0; memberNum < _members.length; memberNum++) {
			Member member = _members[memberNum];
			int modifiers = member.getModifiers();
			if (Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers)) {
				IKey matchingMember = null;
				if (myMemberType == MemberContainer.GET) {
					if (member instanceof Field) {
						matchingMember = new FieldKey((Field) member);
					} else {
						Method method = (Method) member;
						if (method.getReturnType() != null && method.getParameterTypes().length == 0) {
							matchingMember = new MethodKey(method);
						}
					}
				} else if (myMemberType == MemberContainer.SET) {
					if (member instanceof Field) {
						matchingMember = new FieldKey((Field) member);
					} else {
						Method method = (Method) member;
						if (method.getReturnType() == void.class && method.getParameterTypes().length == 1) {
							matchingMember = new MethodKey(method);
						}
					}
				}
				if (matchingMember != null) {
					myMembers.put(member.getName(), matchingMember);
				}
			}
		}
	}

	protected IKey getMemberWithPrefixes(String _key, String[] _prefixes) {
		IKey matchingMember = null;
		for (int prefixNum = 0; matchingMember == null && prefixNum < _prefixes.length; prefixNum++) {
			String prefix = _prefixes[prefixNum];
			boolean capitalize = prefix.length() > 1; // Don't capitalize
														// blank and _ prefixes
			String keyWithPrefix = prependToKey(prefix, _key, capitalize);
			matchingMember = myMembers.get(keyWithPrefix);
		}
		return matchingMember;
	}

	protected String prependToKey(String _prepend, String _key, boolean _capitalize) {
		StringBuffer sb = new StringBuffer();
		sb.append(_prepend);
		if (_capitalize) {
			sb.append(Character.toUpperCase(_key.charAt(0)));
			sb.append(_key.substring(1));
		} else {
			sb.append(_key);
		}
		return sb.toString();
	}

}
