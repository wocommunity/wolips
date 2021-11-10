package org.objectstyle.wolips.eomodeler.core.sql;

import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EOFSQLUtils53 {

	public static Object toWOCollections(Object obj) {
		try {
			return toWOCollectionsReflect(obj);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	private static Object toWOCollectionsReflect(Object obj) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<?> nsmutarray = Class.forName("com.webobjects.foundation.NSMutableArray");
		Class<?> nsmutdictionary = Class.forName("com.webobjects.foundation.NSMutableDictionary");
		Class<?> nsmutset = Class.forName("com.webobjects.foundation.NSMutableSet");
		Object result;
		if (obj instanceof Map) {
			Object nsDict = nsmutdictionary.getConstructor().newInstance();
			Map map = (Map) obj;
			Iterator entriesIter = map.entrySet().iterator();
			while (entriesIter.hasNext()) {
				Map.Entry entry = (Map.Entry) entriesIter.next();
				Object key = entry.getKey();
				Object value = entry.getValue();
				if (key != null && value != null) {
					key = toWOCollections(key);
					value = toWOCollections(value);
					nsmutdictionary.getMethod("setObjectForKey", Object.class, Object.class).invoke(nsDict, value, key);
				}
			}
			result = nsDict;
		} else if (obj instanceof List) {
			Object nsArray = nsmutarray.getConstructor().newInstance();
			List list = (List) obj;
			Iterator valuesEnum = list.iterator();
			while (valuesEnum.hasNext()) {
				Object value = valuesEnum.next();
				if (value != null) {
					value = toWOCollections(value);
					nsmutarray.getMethod("addObject", Object.class).invoke(nsArray, value);
				}
			}
			result = nsArray;
		} else if (obj instanceof Set) {
			Set set = (Set) obj;
			Object nsSet = nsmutset.getConstructor().newInstance();
			Iterator valuesEnum = set.iterator();
			while (valuesEnum.hasNext()) {
				Object value = valuesEnum.next();
				if (value != null) {
					value = toWOCollections(value);
					nsmutset.getMethod("addObject", Object.class).invoke(nsSet, value);
				}
			}
			result = nsSet;
		} else {
			result = obj;
		}
		return result;
	}

	public static Object toJavaCollections(Object obj) {
		try {
			return toJavaCollectionsReflect(obj);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	private static Object toJavaCollectionsReflect(Object obj) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<?> nsarray = Class.forName("com.webobjects.foundation.NSArray");
		Class<?> nsdictionary = Class.forName("com.webobjects.foundation.NSDictionary");
		Class<?> nsset = Class.forName("com.webobjects.foundation.NSSet");
		Object result;
		if (nsdictionary.isInstance(obj)) {
			Map map = new HashMap();
			Object arr = nsdictionary.getMethod("allKeys").invoke(obj);
			Enumeration keysEnum = (Enumeration) nsarray.getMethod("objectEnumerator").invoke(arr);
			while (keysEnum.hasMoreElements()) {
				Object key = keysEnum.nextElement();
				Object value = nsdictionary.getMethod("objectForKey", Object.class).invoke(obj, key);
				key = toJavaCollections(key);
				value = toJavaCollections(value);
				map.put(key, value);
			}
			result = map;
		} else if (nsarray.isInstance(obj)) {
			List list = new LinkedList();
			Enumeration valuesEnum = (Enumeration) nsarray.getMethod("objectEnumerator").invoke(obj);
			while (valuesEnum.hasMoreElements()) {
				Object value = valuesEnum.nextElement();
				value = toJavaCollections(value);
				list.add(value);
			}
			result = list;
		} else if (nsset.isInstance(obj)) {
			Set set = new HashSet();
			Enumeration valuesEnum = (Enumeration) nsset.getMethod("objectEnumerator").invoke(obj);
			while (valuesEnum.hasMoreElements()) {
				Object value = valuesEnum.nextElement();
				value = toJavaCollections(value);
				set.add(value);
			}
			result = set;
		} else {
			result = obj;
		}
		return result;
	}
}
