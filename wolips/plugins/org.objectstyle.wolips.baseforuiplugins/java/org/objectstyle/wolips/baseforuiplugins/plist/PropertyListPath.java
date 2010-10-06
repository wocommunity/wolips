package org.objectstyle.wolips.baseforuiplugins.plist;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectstyle.woenvironment.plist.ParserDataStructureFactory;
import org.objectstyle.woenvironment.plist.PropertyListParserException;
import org.objectstyle.woenvironment.plist.WOLPropertyListSerialization;
import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;

public class PropertyListPath {
	public static enum Type {
		String("String", String.class, null), Number("Number", Number.class), Date("Date", Date.class, Calendar.class), Boolean("Boolean", Boolean.class), Array("Array", List.class, Set.class), Dictionary("Dictionary", Map.class), Data("Data", Object.class);

		private String _name;

		private Class[] _types;

		private Type(String name, Class... types) {
			_name = name;
			_types = types;
		}

		public String getName() {
			return _name;
		}

		public Class[] getTypes() {
			return _types;
		}

		@SuppressWarnings("unchecked")
		public boolean matches(Object obj) {
			Class objType = (obj == null) ? null : obj.getClass();
			for (Class type : _types) {
				if (type == null) {
					if (objType == null) {
						return true;
					}
				} else if (objType != null) {
					if (type.isAssignableFrom(objType)) {
						return true;
					}
				}
			}
			return false;
		}
	}

	private ParserDataStructureFactory _factory;

	private PropertyListPath _parent;

	private int _index;

	private Object _object;

	public PropertyListPath(Object object, ParserDataStructureFactory factory) {
		this(null, 0, object, factory);
	}

	public PropertyListPath(PropertyListPath parent, int index, Object object, ParserDataStructureFactory factory) {
		_parent = parent;
		_index = index;
		_object = object;
		_factory = factory;
	}

	public int getIndex() {
		return _index;
	}

	@Override
	public int hashCode() {
		return getKeyPath().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof PropertyListPath && getKeyPath().equals(((PropertyListPath) obj).getKeyPath());
	}

	public int getIndexOf(PropertyListPath path) {
		return getChildren().indexOf(path);
	}

	public PropertyListPath getChildAtIndex(int index) {
		return getChildren().get(index);
	}

	public PropertyListPath getChildForKey(Object key) {
		for (PropertyListPath child : getChildren()) {
			if (key == child.getKey() || key.equals(child.getKey())) {
				return child;
			}
		}
		return null;
	}

	public List<PropertyListPath> getChildren() {
		return getChildren(this, _object);
	}

	@SuppressWarnings( { "unchecked", "cast" })
	protected List<PropertyListPath> getChildren(PropertyListPath path, Object pathObject) {
		List<PropertyListPath> childrenList;
		if (pathObject instanceof Map) {
			Map lastPathMap = (Map) pathObject;
			childrenList = new LinkedList<PropertyListPath>();
			int index = 0;
			List<Map.Entry<String, Object>> entries = new LinkedList<Map.Entry<String, Object>>((Set<Map.Entry<String, Object>>) lastPathMap.entrySet());
//			Collections.sort(entries, new Comparator<Map.Entry<String, Object>>() {
//				public int compare(Entry<String, Object> o1, Entry<String, Object> o2) {
//					String k1 = o1.getKey();
//					String k2 = o2.getKey();
//					return k1.compareToIgnoreCase(k2);
//				}
//			});
			for (Map.Entry<String, Object> entry : entries) {
				PropertyListPath childPath = path.createChild(index++, entry);
				childrenList.add(childPath);
			}
		} else if (pathObject instanceof Map.Entry) {
			Map.Entry lastPathMapEntry = (Map.Entry) pathObject;
			childrenList = getChildren(path, lastPathMapEntry.getValue());
		} else if (pathObject instanceof List) {
			List lastPathList = (List) pathObject;
			childrenList = new LinkedList<PropertyListPath>();
			int index = 0;
			for (Object child : lastPathList) {
				childrenList.add(path.createChild(index++, child));
			}
		} else if (pathObject instanceof Set) {
			Set lastPathSet = (Set) pathObject;
			childrenList = new LinkedList<PropertyListPath>();
			int index = 0;
			for (Object child : lastPathSet) {
				childrenList.add(path.createChild(index++, child));
			}
		} else {
			childrenList = new LinkedList<PropertyListPath>();
		}
		return childrenList;
	}

	public PropertyListPath getParent() {
		return _parent;
	}

	public Object getRawObject() {
		return _object;
	}

	public PropertyListPath createChild(int index, Object child) {
		return new PropertyListPath(this, index, child, _factory);
	}

	public boolean isCollectionValue() {
		Object object = _object;
		if (object instanceof Map.Entry) {
			object = ((Map.Entry) object).getValue();
		}

		boolean collectionValue;
		if (object instanceof Map) {
			collectionValue = true;
		} else if (object instanceof Collection) {
			collectionValue = true;
		} else {
			collectionValue = false;
		}
		return collectionValue;
	}

	public int getChildCount() {
		int childCount;
		Object value = getValue();
		if (value instanceof Map) {
			childCount = ((Map) value).size();
		} else if (value instanceof Collection) {
			childCount = ((Collection) value).size();
		} else {
			childCount = -1;
		}
		return childCount;
	}

	public String getKeyPath() {
		StringBuffer sb = new StringBuffer();
		getKeyPath(sb);
		return sb.toString();
	}

	protected void getKeyPath(StringBuffer sb) {
		if (_parent != null) {
			_parent.getKeyPath(sb);
			sb.append(".");
		}
		sb.append(getKey());
	}

	@SuppressWarnings("unchecked")
	public boolean setKey(Object key) {
		boolean valueChanged = false;
		Object pathObject = getRawObject();
		PropertyListPath parentPath = getParent();

		if (parentPath == null) {
			// IGNORE
		} else if (pathObject instanceof Map.Entry) {
			Object parentObject = parentPath.getRawObject();
			Map parentMap;
			if (parentObject instanceof Map) {
				parentMap = (Map) parentObject;
			} else {
				parentMap = (Map) ((Map.Entry) parentObject).getValue();
			}

			String oldKey = getKey();
			if (!parentMap.containsKey(key)) {
				// MS: This bit of silliness is because we want to retain the order of the
				// objects in the Map so that rows don't shuffle around as you tab through
				// the editors making changes to keys.  If we just remove and add the new
				// key, the order will change, so instead we remove everything and re-add
				// them in the original order, just replacing the one that we need to.
				if (parentMap instanceof LinkedHashMap) {
					List<Map.Entry> entries = new LinkedList<Map.Entry>(parentMap.entrySet());
					parentMap.clear();
					for (Map.Entry entry : entries) {
						if (ComparisonUtils.equals(entry.getKey(), oldKey)) {
							parentMap.put(key, entry.getValue());
						} else {
							parentMap.put(entry.getKey(), entry.getValue());
						}
					}
				} else {
					Object value = parentMap.remove(oldKey);
					parentMap.put(key, value);
				}
				valueChanged = true;
			}
		}
		return valueChanged;
	}

	public boolean isRealKey() {
		Object pathObject = getRawObject();
		PropertyListPath parentPath = getParent();

		boolean isRealKey;
		if (parentPath == null) {
			isRealKey = false;
		} else if (pathObject instanceof Map.Entry) {
			isRealKey = true;
		} else {
			isRealKey = false;
		}
		return isRealKey;
	}

	public String getKey() {
		Object pathObject = getRawObject();
		PropertyListPath parentPath = getParent();

		String key;
		if (parentPath == null) {
			key = "Root";
		} else if (pathObject instanceof Map.Entry) {
			key = String.valueOf(((Map.Entry) pathObject).getKey());
		} else {
			Object parentPathObject = parentPath.getValue();
			if (parentPathObject instanceof List) {
				key = "Item " + (getIndex() + 1);
			} else {
				key = "Unknown";
			}
		}
		return key;
	}

	@SuppressWarnings("unchecked")
	public boolean setValue(Object value) {
		boolean parentChanged = false;
		PropertyListPath parentPath = getParent();
		if (parentPath == null) {
			_object = value;
		} else {
			Object parentObject = parentPath.getValue();
			if (parentObject instanceof Map) {
				Map parentMap = (Map) parentObject;
				String key = getKey();
				parentMap.put(key, value);
			} else if (parentObject instanceof List) {
				int index = getIndex();
				List parentList = (List) parentObject;
				parentList.set(index, value);
				parentChanged = true;
			} else if (parentObject instanceof Set) {
				Set parentSet = (Set) parentObject;
				parentSet.remove(getValue());
				parentSet.add(value);
				parentChanged = true;
			} else {
				System.out.println("PropertyListPath.getValue: ignoring " + getKeyPath() + "=" + value);
			}
		}
		return parentChanged;
	}

	public Object getValue() {
		Object value = _object;
		if (value instanceof Map.Entry) {
			value = ((Map.Entry) value).getValue();
		}
		return value;
	}

	public PropertyListPath.Type getType() {
		Object value = getValue();
		for (PropertyListPath.Type type : PropertyListPath.Type.values()) {
			if (type.matches(value)) {
				return type;
			}
		}
		return null;
	}

	public boolean setType(PropertyListPath.Type newType) {
		Object newValue = convertValueToType(newType);
		boolean parentChanged = setValue(newValue);
		return parentChanged;
	}

	public Object convertValueToType(PropertyListPath.Type newType) {
		Object newValue = PropertyListPath.convertValueFromTypeToType(getKeyPath(), getValue(), getType(), newType, _factory);
		return newValue;
	}

	public ParserDataStructureFactory getFactory() {
		return _factory;
	}

	@SuppressWarnings( { "unchecked", "cast" })
	public static Object convertValueFromTypeToType(String keyPath, Object oldValue, PropertyListPath.Type oldType, PropertyListPath.Type newType, ParserDataStructureFactory factory) {
		Object newValue;
		try {
			if (oldType == PropertyListPath.Type.Array) {
				Object oldListValue = null;
				List<Object> oldList = (List<Object>) oldValue;
				if (oldList != null && oldList.size() == 1) {
					oldListValue = oldList.get(0);
				}

				if (newType == PropertyListPath.Type.Array) {
					newValue = oldValue;
				} else if (newType == PropertyListPath.Type.Boolean) {
					newValue = Boolean.TRUE;
					if (oldListValue instanceof Boolean) {
						newValue = (Boolean) oldListValue;
					}
				} else if (newType == PropertyListPath.Type.Data) {
					if (oldListValue instanceof byte[]) {
						newValue = (byte[]) oldListValue;
					} else {
						newValue = WOLPropertyListSerialization.stringFromPropertyList(oldValue).getBytes();
					}
				} else if (newType == PropertyListPath.Type.Date) {
					if (oldListValue instanceof Date) {
						newValue = (Date) oldListValue;
					} else if (oldListValue instanceof Calendar) {
						newValue = (Calendar) oldListValue;
					} else {
						newValue = new Date();
					}
				} else if (newType == PropertyListPath.Type.Dictionary) {
					// REVIEW THIS ONE
					Map<Object, Object> newMap = factory.createMap(keyPath);
					int keyNum = 1;
					for (Object obj : (Collection) oldValue) {
						newMap.put("New Key " + (keyNum++), obj);
					}
					newValue = newMap;
				} else if (newType == PropertyListPath.Type.Number) {
					if (oldListValue instanceof Number) {
						newValue = (Number) oldListValue;
					} else if (oldValue != null) {
						newValue = ((Collection) oldValue).size();
					} else {
						newValue = 0;
					}
				} else if (newType == PropertyListPath.Type.String) {
					if (oldListValue instanceof String) {
						newValue = (String) oldListValue;
					} else {
						newValue = WOLPropertyListSerialization.stringFromPropertyList(oldValue);
					}
				} else {
					throw new IllegalArgumentException("Unknown type " + newType);
				}
			} else if (oldType == PropertyListPath.Type.Boolean) {
				if (newType == PropertyListPath.Type.Array) {
					Collection<Object> list = factory.createCollection(keyPath);
					list.add(oldValue);
					newValue = list;
				} else if (newType == PropertyListPath.Type.Boolean) {
					newValue = oldValue;
				} else if (newType == PropertyListPath.Type.Data) {
					newValue = new byte[] { 1 };
				} else if (newType == PropertyListPath.Type.Date) {
					// REVIEW THIS ONE
					newValue = new Date();
				} else if (newType == PropertyListPath.Type.Dictionary) {
					Map<Object, Object> newMap = factory.createMap(keyPath);
					newMap.put("New Key 1", oldValue);
					newValue = newMap;
				} else if (newType == PropertyListPath.Type.Number) {
					newValue = ((Collection) oldValue).size();
				} else if (newType == PropertyListPath.Type.String) {
					newValue = WOLPropertyListSerialization.stringFromPropertyList(oldValue);
				} else {
					throw new IllegalArgumentException("Unknown type " + newType);
				}
			} else if (oldType == PropertyListPath.Type.Data) {
				byte[] oldBytes = (byte[]) oldValue;
				if (newType == PropertyListPath.Type.Array) {
					Collection<Object> list = factory.createCollection(keyPath);
					list.add(oldValue);
					newValue = list;
				} else if (newType == PropertyListPath.Type.Boolean) {
					if (oldBytes != null && oldBytes.length == 1) {
						newValue = oldBytes[0] > 0;
					} else {
						newValue = Boolean.TRUE;
					}
				} else if (newType == PropertyListPath.Type.Data) {
					newValue = oldValue;
				} else if (newType == PropertyListPath.Type.Date) {
					// REVIEW THIS ONE
					newValue = new Date();
				} else if (newType == PropertyListPath.Type.Dictionary) {
					Map<Object, Object> newMap = factory.createMap(keyPath);
					newMap.put("New Key 1", oldValue);
					newValue = newMap;
				} else if (newType == PropertyListPath.Type.Number) {
					if (oldBytes != null) {
						String str = new String(oldBytes);
						try {
							newValue = new BigDecimal(str);
						} catch (NumberFormatException e) {
							newValue = oldBytes.length;
						}
					} else {
						newValue = 0;
					}
				} else if (newType == PropertyListPath.Type.String) {
					if (oldBytes != null) {
						newValue = new String(oldBytes);
					} else {
						newValue = "";
					}
				} else {
					throw new IllegalArgumentException("Unknown type " + newType);
				}
			} else if (oldType == PropertyListPath.Type.Date) {
				if (newType == PropertyListPath.Type.Array) {
					Collection<Object> list = factory.createCollection(keyPath);
					list.add(oldValue);
					newValue = list;
				} else if (newType == PropertyListPath.Type.Boolean) {
					newValue = Boolean.TRUE;
				} else if (newType == PropertyListPath.Type.Data) {
					// REVIEW THIS ONE
					newValue = new byte[0];
				} else if (newType == PropertyListPath.Type.Date) {
					newValue = oldValue;
				} else if (newType == PropertyListPath.Type.Dictionary) {
					Map<Object, Object> newMap = factory.createMap(keyPath);
					newMap.put("New Key 1", oldValue);
					newValue = newMap;
				} else if (newType == PropertyListPath.Type.Number) {
					newValue = System.currentTimeMillis();
				} else if (newType == PropertyListPath.Type.String) {
					DateFormat dateFormat = DateFormat.getDateTimeInstance();
					if (oldValue instanceof Date) {
						newValue = dateFormat.format(oldValue);
					} else if (oldValue instanceof Calendar) {
						newValue = dateFormat.format(((Calendar) oldValue).getTime());
					} else {
						newValue = "";
					}
				} else {
					throw new IllegalArgumentException("Unknown type " + newType);
				}
			} else if (oldType == PropertyListPath.Type.Dictionary) {
				Map<Object, Object> oldMap = (Map<Object, Object>) oldValue;
				Object oldMapValue = null;
				if (oldMap != null && oldMap.size() == 1) {
					Map.Entry<Object, Object> entry = oldMap.entrySet().iterator().next();
					if ("New Key 1".equals(entry.getKey())) {
						oldMapValue = entry.getValue();
					}
				}

				if (newType == PropertyListPath.Type.Array) {
					Collection<Object> list = factory.createCollection(keyPath);
					for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) oldValue).entrySet()) {
						list.add(entry.getValue());
					}
					newValue = list;
				} else if (newType == PropertyListPath.Type.Boolean) {
					if (oldMapValue instanceof Boolean) {
						newValue = (Boolean) oldMapValue;
					} else {
						newValue = Boolean.TRUE;
					}
				} else if (newType == PropertyListPath.Type.Data) {
					if (oldMapValue instanceof byte[]) {
						newValue = (byte[]) oldMapValue;
					} else {
						newValue = WOLPropertyListSerialization.stringFromPropertyList(oldValue).getBytes();
					}
				} else if (newType == PropertyListPath.Type.Date) {
					if (oldMapValue instanceof Date) {
						newValue = (Date) oldMapValue;
					} else if (oldMapValue instanceof Calendar) {
						newValue = (Calendar) oldMapValue;
					} else {
						newValue = new Date();
					}
				} else if (newType == PropertyListPath.Type.Dictionary) {
					newValue = oldValue;
				} else if (newType == PropertyListPath.Type.Number) {
					if (oldMapValue instanceof Number) {
						newValue = (Number) oldMapValue;
					} else if (oldMap != null) {
						newValue = oldMap.size();
					} else {
						newValue = 0;
					}
				} else if (newType == PropertyListPath.Type.String) {
					if (oldMapValue instanceof String) {
						newValue = (String) oldMapValue;
					} else {
						newValue = WOLPropertyListSerialization.stringFromPropertyList(oldValue);
					}
				} else {
					throw new IllegalArgumentException("Unknown type " + newType);
				}
			} else if (oldType == PropertyListPath.Type.Number) {
				Number oldNumber = (Number) oldValue;
				if (newType == PropertyListPath.Type.Array) {
					Collection<Object> list = factory.createCollection(keyPath);
					list.add(oldValue);
					newValue = list;
				} else if (newType == PropertyListPath.Type.Boolean) {
					newValue = (oldNumber != null && oldNumber.intValue() > 0);
				} else if (newType == PropertyListPath.Type.Data) {
					newValue = String.valueOf(oldValue).getBytes();
				} else if (newType == PropertyListPath.Type.Date) {
					newValue = new Date(System.currentTimeMillis());
				} else if (newType == PropertyListPath.Type.Dictionary) {
					Map<Object, Object> newMap = factory.createMap(keyPath);
					newMap.put("New Key 1", oldValue);
					newValue = newMap;
				} else if (newType == PropertyListPath.Type.Number) {
					newValue = oldValue;
				} else if (newType == PropertyListPath.Type.String) {
					newValue = String.valueOf(oldNumber);
				} else {
					throw new IllegalArgumentException("Unknown type " + newType);
				}
			} else if (oldType == PropertyListPath.Type.String) {
				String oldString = (String) oldValue;
				if (newType == PropertyListPath.Type.Array) {
					Collection<Object> list = factory.createCollection(keyPath);
					list.add(oldValue);
					newValue = list;
				} else if (newType == PropertyListPath.Type.Boolean) {
					if (oldString == null) {
						newValue = Boolean.FALSE;
					} else {
						Object newObject;
						try {
							newObject = "y".equalsIgnoreCase(oldString) || "yes".equalsIgnoreCase(oldString) || "true".equalsIgnoreCase(oldString);
							newValue = (newObject instanceof Boolean) ? newObject : Boolean.TRUE;
						} catch (Exception e) {
							newValue = Boolean.FALSE;
						}
					}
				} else if (newType == PropertyListPath.Type.Data) {
					newValue = WOLPropertyListSerialization.stringFromPropertyList(oldValue).getBytes();
				} else if (newType == PropertyListPath.Type.Date) {
					DateFormat dateFormat = DateFormat.getDateTimeInstance();
					if (oldValue == null) {
						newValue = new Date();
					} else {
						try {
							newValue = dateFormat.parseObject((String) oldValue);
						} catch (ParseException e) {
							newValue = new Date();
						}
					}
				} else if (newType == PropertyListPath.Type.Dictionary) {
					Map<Object, Object> newMap = factory.createMap(keyPath);
					newMap.put("New Key 1", oldValue);
					newValue = newMap;
				} else if (newType == PropertyListPath.Type.Number) {
					if (oldValue == null) {
						newValue = 0;
					} else {
						try {
							newValue = new BigDecimal(oldString);
						} catch (NumberFormatException e) {
							newValue = 0;
						}
					}
				} else if (newType == PropertyListPath.Type.String) {
					newValue = oldValue;
				} else {
					throw new IllegalArgumentException("Unknown type " + newType);
				}
			} else {
				throw new IllegalArgumentException("Unknown old type " + oldType);
			}
		} catch (PropertyListParserException e) {
			throw new IllegalArgumentException("Failed to process plist.", e);
		}
		return newValue;
	}

	@SuppressWarnings("unchecked")
	public PropertyListPath addRow() {
		PropertyListPath newPath;
		Object value = getValue();
		if (value instanceof Map) {
			Map<Object, Object> map = (Map<Object, Object>) value;
			int i = 1;
			String key;
			do {
				key = "New Key " + (i++);
			} while (map.containsKey(key));
			map.put(key, "New Value");
			newPath = getChildForKey(key);
		} else if (value instanceof List) {
			List<Object> list = (List<Object>) value;
			list.add("New Value");
			newPath = getChildAtIndex(list.size() - 1);
		} else if (value instanceof Set) {
			Set<Object> set = (Set<Object>) value;
			int i = 1;
			String key;
			do {
				key = "New Value " + (i++);
			} while (set.contains(key));

			newPath = null;
			for (PropertyListPath child : getChildren()) {
				if (key.equals(child.getValue())) {
					newPath = child;
					break;
				}
			}
		} else {
			newPath = null;
		}
		return newPath;
	}

	public boolean delete() {
		boolean deleted = false;
		PropertyListPath parentPath = getParent();
		if (parentPath != null) {
			Object parentValue = parentPath.getValue();
			if (parentValue instanceof List) {
				((List) parentValue).remove(getIndex());
				deleted = true;
			} else if (parentValue instanceof Set) {
				((Set) parentValue).remove(getValue());
				deleted = true;
			} else if (parentValue instanceof Map) {
				((Map) parentValue).remove(getKey());
				deleted = true;
			}
		}
		return deleted;
	}

	@SuppressWarnings("unchecked")
	public boolean moveUp() {
		boolean moved = false;
		PropertyListPath parentPath = getParent();
		if (parentPath != null) {
			Object parentValue = parentPath.getValue();
			if (parentValue instanceof List) {
				int index = getIndex();
				if (index > 0) {
					Object obj = ((List) parentValue).remove(index);
					((List) parentValue).add(index - 1, obj);
					moved = true;
				}
			} else if (parentValue instanceof Set) {
				//((Set) parentValue).remove(getValue());
				//moved = true;
			} else if (parentValue instanceof Map) {
				//((Map) parentValue).remove(getKey());
				//moved = true;
			}
		}
		return moved;
	}

	@SuppressWarnings("unchecked")
	public boolean moveDown() {
		boolean moved = false;
		PropertyListPath parentPath = getParent();
		if (parentPath != null) {
			Object parentValue = parentPath.getValue();
			if (parentValue instanceof List) {
				int index = getIndex();
				if (index < parentPath.getChildCount() - 1) {
					Object obj = ((List) parentValue).remove(index);
					((List) parentValue).add(index + 1, obj);
					moved = true;
				}
			} else if (parentValue instanceof Set) {
				//((Set) parentValue).remove(getValue());
				//moved = true;
			} else if (parentValue instanceof Map) {
				//((Map) parentValue).remove(getKey());
				//moved = true;
			}
		}
		return moved;
	}
}
