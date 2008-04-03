package org.objectstyle.wolips.eomodeler.core.utils;

import java.util.Set;

import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.baseforplugins.util.StringUtils;

public class NameSyncUtils {
	public static class NamePair {
		private String _name;

		private String _dependentName;

		public NamePair(String name, String dependentName) {
			_name = name;
			_dependentName = dependentName;
		}

		public String getName() {
			return _name;
		}

		public String getDependentName() {
			return _dependentName;
		}
		
		public String getPrefix() {
			return NameSyncUtils.getPrefix(_name, _dependentName);
		}
	}

	public static String getCommonPrefix(Set<NamePair> namePairs) {
		String commonPrefix = null;
		if (namePairs != null) {
			for (NamePair namePair : namePairs) {
				if (namePair.getName() != null && namePair.getName().length() > 0 && namePair.getDependentName() != null && namePair.getDependentName().length() > 0) {
					String prefix = namePair.getPrefix();
					if (commonPrefix == null) {
						commonPrefix = prefix;
					}
					else if (!ComparisonUtils.equals(commonPrefix, prefix)) {
						commonPrefix = null;
						break;
					}
				}
			}
		}
		if (commonPrefix == null) {
			commonPrefix = "";
		}
		return commonPrefix;
	}

	public static boolean isSeparatorCharacter(char ch) {
		return Character.isUpperCase(ch) || ch == '_';
	}

	public static String getPrefix(String name, String dependentName) {
		String prefix;
		if (name == null || dependentName == null) {
			prefix = "";
		} else if (ComparisonUtils.equals(name, dependentName, true)) {
			prefix = "";
		} else if (dependentName.endsWith(name)) {
			prefix = dependentName.substring(0, dependentName.length() - name.length());
		} else if (dependentName.endsWith(name.toUpperCase())) {
			prefix = dependentName.substring(0, dependentName.length() - name.length());
		} else if (dependentName.endsWith(name.toLowerCase())) {
			prefix = dependentName.substring(0, dependentName.length() - name.length());
		} else if (dependentName.indexOf('_') != -1) {
			if (ComparisonUtils.equals(StringUtils.camelCaseToUnderscore(name), dependentName)) {
				prefix = "";
			} else if (ComparisonUtils.equals(StringUtils.camelCaseToUnderscore(name).toUpperCase(), dependentName)) {
				prefix = "";
			} else if (dependentName.endsWith(StringUtils.camelCaseToUnderscore(name))) {
				prefix = dependentName.substring(0, dependentName.length() - StringUtils.camelCaseToUnderscore(name).length());
			} else if (dependentName.endsWith(StringUtils.camelCaseToUnderscore(name).toUpperCase())) {
				prefix = dependentName.substring(0, dependentName.length() - StringUtils.camelCaseToUnderscore(name).length());
			} else if (ComparisonUtils.equals(StringUtils.camelCaseToUnderscore(name, false), dependentName)) {
				prefix = "";
			} else if (dependentName.endsWith(StringUtils.camelCaseToUnderscore(name, false))) {
				prefix = dependentName.substring(0, dependentName.length() - StringUtils.camelCaseToUnderscore(name, false).length());
			} else {
				prefix = "";
			}
		} else {
			prefix = "";
		}
		return prefix;
	}

	public static String newDependentName(String oldName, String newName, String oldDependentName, Set<NamePair> otherDependentNames) {
		String commonPrefix = NameSyncUtils.getCommonPrefix(otherDependentNames);
		String newDependentName;
		if (oldName == null || newName == null) {
			newDependentName = oldDependentName;
		} else if (oldDependentName == null) {
			newDependentName = newName;
		} else if (ComparisonUtils.equals(oldName, oldDependentName, true)) {
			newDependentName = newName;
		} else if (ComparisonUtils.equals(oldName.toUpperCase(), oldDependentName, true)) {
			newDependentName = newName.toUpperCase();
		} else if (ComparisonUtils.equals(oldName.toLowerCase(), oldDependentName, true)) {
			newDependentName = newName.toLowerCase();
		} else if (ComparisonUtils.equals(commonPrefix + oldName, oldDependentName, true)) {
			newDependentName = commonPrefix + newName;
		} else if (ComparisonUtils.equals(commonPrefix + oldName.toUpperCase(), oldDependentName, true)) {
			newDependentName = commonPrefix + newName.toUpperCase();
		} else if (ComparisonUtils.equals(commonPrefix + oldName.toLowerCase(), oldDependentName, true)) {
			newDependentName = commonPrefix + newName.toLowerCase();
		} else if (oldDependentName.indexOf('_') != -1) {
			if (ComparisonUtils.equals(StringUtils.camelCaseToUnderscore(oldName), oldDependentName)) {
				newDependentName = StringUtils.camelCaseToUnderscore(newName);
			} else if (ComparisonUtils.equals(StringUtils.camelCaseToUnderscore(oldName).toUpperCase(), oldDependentName)) {
				newDependentName = StringUtils.camelCaseToUnderscore(newName).toUpperCase();
			} else if (ComparisonUtils.equals(commonPrefix + StringUtils.camelCaseToUnderscore(oldName), oldDependentName)) {
				newDependentName = commonPrefix + StringUtils.camelCaseToUnderscore(newName);
			} else if (ComparisonUtils.equals(commonPrefix + StringUtils.camelCaseToUnderscore(oldName).toUpperCase(), oldDependentName)) {
				newDependentName = commonPrefix + StringUtils.camelCaseToUnderscore(newName).toUpperCase();
			} else if (ComparisonUtils.equals(StringUtils.camelCaseToUnderscore(oldName, false), oldDependentName)) {
				newDependentName = StringUtils.camelCaseToUnderscore(newName, false);
			} else if (ComparisonUtils.equals(commonPrefix + StringUtils.camelCaseToUnderscore(oldName, false), oldDependentName)) {
				newDependentName = commonPrefix + StringUtils.camelCaseToUnderscore(newName, false);
			} else {
				newDependentName = oldDependentName;
			}
		} else {
			newDependentName = oldDependentName;
		}
		return newDependentName;
	}

	public static String newClassName(String oldName, String newName, String oldClassName) {
		String newClassName;
		if (ComparisonUtils.equals(oldName, oldClassName, true)) {
			newClassName = newName;
		} else if (oldClassName != null && oldClassName.endsWith("." + oldName)) {
			String oldPackage = oldClassName.substring(0, oldClassName.lastIndexOf('.') + 1);
			newClassName = oldPackage + newName;
		} else {
			newClassName = oldClassName;
		}
		return newClassName;
	}

}
