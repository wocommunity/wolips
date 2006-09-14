package org.objectstyle.wolips.wodclipse.wod.completion;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.internal.corext.util.SuperTypeHierarchyCache;
import org.objectstyle.wolips.core.resources.types.api.ApiModel;
import org.objectstyle.wolips.core.resources.types.api.ApiModelException;
import org.objectstyle.wolips.core.resources.types.api.Binding;
import org.objectstyle.wolips.core.resources.types.api.Wo;
import org.objectstyle.wolips.wodclipse.WodclipsePlugin;
import org.objectstyle.wolips.wodclipse.wod.model.BindingValueKey;
import org.objectstyle.wolips.workbenchutilities.WorkbenchUtilitiesPlugin;
import org.osgi.framework.Bundle;

public class WodBindingUtils {
	public static final String[] FIELD_PREFIXES = { "", "_" };

	public static final String[] SET_METHOD_PREFIXES = { "set", "_set" };

	public static final String[] GET_METHOD_PREFIXES = { "get", "", "_get", "is", "_is", "_" };

	public static final int ACCESSORS_ONLY = 0;

	public static final int MUTATORS_ONLY = 1;

	public static final int ACCESSORS_OR_VOID = 2;

	public static final int VOID_ONLY = 3;

	public static String getShortClassName(String _fullClassName) {
		String shortClassName;
		int lastDotIndex = _fullClassName.lastIndexOf('.');
		if (lastDotIndex == -1) {
			shortClassName = _fullClassName;
		} else {
			shortClassName = _fullClassName.substring(lastDotIndex + 1);
		}
		return shortClassName;
	}

	public static IType findElementType(IJavaProject _javaProject, String _elementTypeName, boolean _requireTypeInProject, Map _elementNameToTypeCache) throws JavaModelException {
		// Search the current project for the given element type name
		IType type;
		if (_elementNameToTypeCache != null) {
			type = (IType) _elementNameToTypeCache.get(_elementTypeName);
		} else {
			type = null;
		}
		if (type == null) {
			TypeNameCollector typeNameCollector = new TypeNameCollector(_javaProject, _requireTypeInProject);
			WodBindingUtils.findMatchingElementClassNames(_elementTypeName, SearchPattern.R_EXACT_MATCH, typeNameCollector);
			if (typeNameCollector.isExactMatch()) {
				String matchingElementClassName = typeNameCollector.firstTypeName();
				type = typeNameCollector.getTypeForClassName(matchingElementClassName);
			} else if (!typeNameCollector.isEmpty()) {
				// there was more than one matching class! crap!
				String matchingElementClassName = typeNameCollector.firstTypeName();
				type = typeNameCollector.getTypeForClassName(matchingElementClassName);
			}
			if (type != null && _elementNameToTypeCache != null) {
				_elementNameToTypeCache.put(_elementTypeName, type);
			}
		}
		return type;
	}

	public static void findMatchingElementClassNames(String _elementTypeName, int _matchType, TypeNameCollector _typeNameCollector) throws JavaModelException {
		SearchEngine searchEngine = new SearchEngine();
		IJavaSearchScope searchScope = SearchEngine.createWorkspaceScope();
		int lastDotIndex = _elementTypeName.lastIndexOf('.');
		char[] packageName;
		char[] typeName;
		if (lastDotIndex == -1) {
			packageName = null;
			typeName = _elementTypeName.toCharArray();
		} else {
			packageName = _elementTypeName.substring(0, lastDotIndex).toCharArray();
			typeName = _elementTypeName.substring(lastDotIndex + 1).toCharArray();
		}
		searchEngine.searchAllTypeNames(packageName, typeName, _matchType, IJavaSearchConstants.CLASS, searchScope, _typeNameCollector, IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, null);
	}

	public static List createMatchingBindingKeys(IJavaProject _javaProject, IType _type, String _nameStartingWith, boolean _requireExactNameMatch, int _accessorsOrMutators) throws JavaModelException {
		List bindingKeys = new LinkedList();

		if (_type != null) {
			String lowercaseNameStartingWith = _nameStartingWith.toLowerCase();
			ITypeHierarchy typeHierarchy;
			String typeName = _type.getElementName();
			// We want to show fields from your WOApplication, WOSession, and
			// WODirectAction subclasses ...
			if ("WOApplication".equals(typeName) || "WOSession".equals(typeName) || "WODirectAction".equals(typeName)) {
				typeHierarchy = _type.newTypeHierarchy(_javaProject, null);
			} else {
				typeHierarchy = SuperTypeHierarchyCache.getTypeHierarchy(_type);
			}
			IType[] types = typeHierarchy.getAllTypes();
			for (int typeNum = 0; (!_requireExactNameMatch || bindingKeys.size() == 0) && typeNum < types.length; typeNum++) {
				IField[] fields = types[typeNum].getFields();
				for (int fieldNum = 0; (!_requireExactNameMatch || bindingKeys.size() == 0) && fieldNum < fields.length; fieldNum++) {
					BindingValueKey bindingKey = WodBindingUtils.createBindingKeyIfMatches(_javaProject, fields[fieldNum], lowercaseNameStartingWith, _requireExactNameMatch, _accessorsOrMutators);
					if (bindingKey != null) {
						bindingKeys.add(bindingKey);
					}
					// System.out.println("WODCompletionProcessor.nextType:
					// field " + fields[fieldNum].getElementName() + "=>" +
					// bindingKey);
				}

				if (!_requireExactNameMatch || bindingKeys.size() == 0) {
					IMethod[] methods = types[typeNum].getMethods();
					for (int methodNum = 0; (!_requireExactNameMatch || bindingKeys.size() == 0) && methodNum < methods.length; methodNum++) {
						BindingValueKey bindingKey = WodBindingUtils.createBindingKeyIfMatches(_javaProject, methods[methodNum], lowercaseNameStartingWith, _requireExactNameMatch, _accessorsOrMutators);
						if (bindingKey != null) {
							bindingKeys.add(bindingKey);
						}
						// System.out.println("WODCompletionProcessor.nextType:
						// method " + methods[methodNum].getElementName() + "=>"
						// + bindingKey);
					}
				}
			}
		}

		return bindingKeys;
	}

	public static BindingValueKey createBindingKeyIfMatches(IJavaProject _javaProject, IMember _member, String _nameStartingWith, boolean _requireExactNameMatch, int _accessorsOrMutators) throws JavaModelException {
		BindingValueKey bindingKey = null;

		int flags = _member.getFlags();
		if (!Flags.isStatic(flags) && Flags.isPublic(flags)) {
			String[] possiblePrefixes;
			boolean memberSignatureMatches;
			if (_member instanceof IMethod) {
				IMethod method = (IMethod) _member;
				int parameterCount = method.getParameterNames().length;
				String returnType = method.getReturnType();
				if (_accessorsOrMutators == WodBindingUtils.ACCESSORS_ONLY) {
					memberSignatureMatches = (parameterCount == 0 && !"V".equals(returnType));
					possiblePrefixes = WodBindingUtils.GET_METHOD_PREFIXES;
				} else if (_accessorsOrMutators == WodBindingUtils.ACCESSORS_OR_VOID) {
					memberSignatureMatches = (parameterCount == 0);
					possiblePrefixes = WodBindingUtils.GET_METHOD_PREFIXES;
				} else if (_accessorsOrMutators == WodBindingUtils.VOID_ONLY) {
					memberSignatureMatches = (parameterCount == 0 && "V".equals(returnType));
					possiblePrefixes = WodBindingUtils.GET_METHOD_PREFIXES;
				} else {
					memberSignatureMatches = (parameterCount == 1 && "V".equals(returnType));
					possiblePrefixes = WodBindingUtils.SET_METHOD_PREFIXES;
				}
			} else {
				memberSignatureMatches = true;
				possiblePrefixes = WodBindingUtils.FIELD_PREFIXES;
			}

			if (memberSignatureMatches) {
				String memberName = _member.getElementName();
				String lowercaseMemberName = memberName.toLowerCase();

				// Run through our list of valid prefixes and look for a match
				// (i.e. whatever, _whatever, _getWhatever, etc).
				// If we find a match, then turn it into wod-style naming --
				// lowercase first letter, dropping the prefix
				for (int prefixNum = 0; bindingKey == null && prefixNum < possiblePrefixes.length; prefixNum++) {
					if (lowercaseMemberName.startsWith(possiblePrefixes[prefixNum])) {
						int prefixLength = possiblePrefixes[prefixNum].length();
						String lowercaseMemberNameWithoutPrefix = lowercaseMemberName.substring(prefixLength);
						if ((_requireExactNameMatch && lowercaseMemberNameWithoutPrefix.equals(_nameStartingWith)) || (!_requireExactNameMatch && lowercaseMemberNameWithoutPrefix.startsWith(_nameStartingWith))) {
							String bindingName = WodBindingUtils.toLowercaseFirstLetter(memberName.substring(prefixLength));
							if (_nameStartingWith.length() > 0 || !bindingName.startsWith("_")) {
								bindingKey = new BindingValueKey(bindingName, _member, _javaProject);
							}
						}
					}
				}

				// System.out.println("WodBindingUtils.createBindingKeyIfMatches:
				// " + _nameStartingWith + ", " + lowercaseMemberName + ", " +
				// bindingKey);
			}
		}

		return bindingKey;
	}

	public static String toLowercaseFirstLetter(String _memberName) {
		String lowercaseFirstLetterMemberName;
		if (_memberName.length() > 0) {
			char firstChar = _memberName.charAt(0);
			if (Character.isUpperCase(firstChar)) {
				lowercaseFirstLetterMemberName = Character.toLowerCase(firstChar) + _memberName.substring(1);
			} else {
				lowercaseFirstLetterMemberName = _memberName;
			}
		} else {
			lowercaseFirstLetterMemberName = _memberName;
		}
		return lowercaseFirstLetterMemberName;
	}

	public static Wo findApiModelWo(IType _elementType, Map _elementTypeToWoCache) throws JavaModelException, ApiModelException {
		Object cachedWo = _elementTypeToWoCache.get(_elementType);
		Wo wo = null;
		if (cachedWo != null) {
			if (cachedWo instanceof Wo) {
				wo = (Wo) cachedWo;
			}
		} else {
			ApiModel apiModel = null;
			IOpenable typeContainer = _elementType.getOpenable();
			if (typeContainer instanceof IClassFile) {
				IClassFile classFile = (IClassFile) typeContainer;
				IJavaElement parent = classFile.getParent();
				if (parent instanceof IPackageFragment) {
					IPackageFragment parentPackage = (IPackageFragment) parent;
					IPath packagePath = parentPackage.getPath();
					IPath apiPath = packagePath.removeLastSegments(2).append(_elementType.getElementName()).addFileExtension("api");
					File apiFile = apiPath.toFile();
					boolean fileExists = apiFile.exists();
					if (!fileExists && parentPackage.getElementName().startsWith("com.webobjects")) {
						Bundle bundle = WodclipsePlugin.getDefault().getBundle();
						URL woDefinitionsURL = bundle.getEntry("/WebObjectDefinitions.xml");
						// apiFile = new
						// File("/Users/mschrag/Documents/workspace/org.objectstyle.wolips.wodclipse/java/org/objectstyle/wolips/wodclipse/api/WebObjectDefinitions.xml");
						apiModel = new ApiModel(woDefinitionsURL);
					} else if (fileExists) {
						apiModel = new ApiModel(apiFile);
					}
				}
			} else if (typeContainer instanceof ICompilationUnit) {
				//ICompilationUnit cu = (ICompilationUnit) typeContainer;
				//IResource resource = cu.getCorrespondingResource();
				//String name = resource.getName();
				List apiResources = WorkbenchUtilitiesPlugin.findResourcesInProjectByNameAndExtensions(_elementType.getJavaProject().getProject(), _elementType.getElementName(), new String[] { "api" }, false);
				if (apiResources != null && apiResources.size() > 0) {
					IResource apiResource = (IResource) apiResources.get(0);
					apiModel = new ApiModel(apiResource.getLocation().toFile());
				}
			}

			if (apiModel != null) {
				Wo[] wos = apiModel.getWODefinitions().getWos();
				if (wos.length == 0) {
					// leave it alone
				} else if (wos.length == 1) {
					wo = wos[0];
				} else {
					for (int i = 0; wo == null && i < wos.length; i++) {
						if (_elementType.getElementName().equals(wos[i].getClassName())) {
							wo = wos[i];
						}
					}
				}
			}

			if (wo == null) {
				_elementTypeToWoCache.put(_elementType, "NOAPI");
			} else {
				_elementTypeToWoCache.put(_elementType, wo);
			}
		}

		return wo;
	}

	public static String[] getValidValues(IJavaProject _javaProject, IType _wodJavaFileType, IType _elementType, String _bindingName, Map _elementTypeToWoCache) throws JavaModelException, ApiModelException {
		String[] validValues = null;
		Wo wo = WodBindingUtils.findApiModelWo(_elementType, _elementTypeToWoCache);
		if (wo != null) {
			Binding matchingBinding = null;
			Binding[] bindings = wo.getBindings();
			for (int i = 0; matchingBinding == null && i < bindings.length; i++) {
				String apiBindingName = bindings[i].getName();
				if (apiBindingName.equals(_bindingName)) {
					matchingBinding = bindings[i];
				}
			}
			if (matchingBinding != null) {
				int selectedDefaults = matchingBinding.getSelectedDefaults();
				String defaultsName = Binding.ALL_DEFAULTS[selectedDefaults];
				if ("Boolean".equals(defaultsName)) {
					validValues = new String[] { "true", "false" };
				} else if ("YES/NO".equals(defaultsName)) {
					validValues = new String[] { "true", "false" };
				} else if ("Date Format Strings".equals(defaultsName)) {
					validValues = new String[] { "\"%m/%d/%y\"", "\"%B %d, %Y\"", "\"%b %d, %Y\"", "\"%A, %B %d, %Y\"", "\"%A, %b %d, %Y\"", "\"%d.%m.%y\"", "\"%d %B %y\"", "\"%d %b %y\"", "\"%A %d %B %Y\"", "\"%A %d %b %Y\"", "\"%x\"", "\"%H:%M:%S\"", "\"%I:%M:%S %p\"", "\"%H:%M\"", "\"%I:%M %p\"", "\"%X\"" };
				} else if ("Number Format Strings".equals(defaultsName)) {
					validValues = new String[] { "\"0\"", "\"0.00\"", "\"0.##\"", "\"#,##0\"", "\"_,__0\"", "\"#,##0.00\"", "\"$#,##0\"", "\"$#,##0.00\"", "\"$#,##0.##\"" };
				} else if ("MIME Types".equals(defaultsName)) {
					validValues = new String[] { "\"image/gif\"", "\"image/jpeg\"", "\"image/png\"" };
				} else if ("Direct Actions".equals(defaultsName)) {
					// do nothing for now
				} else if ("Direct Action Classes".equals(defaultsName)) {
					// do nothing for now
				} else if ("Page Names".equals(defaultsName)) {
					// do nothing for now
				} else if ("Frameworks".equals(defaultsName)) {
					// do nothing for now
				} else if ("Resources".equals(defaultsName)) {
					// do nothing for now
				} else if ("Actions".equals(defaultsName)) {
					List bindingKeysList = WodBindingUtils.createMatchingBindingKeys(_javaProject, _wodJavaFileType, "", false, WodBindingUtils.VOID_ONLY);
					validValues = new String[bindingKeysList.size()];
					for (int i = 0; i < validValues.length; i++) {
						validValues[i] = ((BindingValueKey) bindingKeysList.get(i)).getBindingName();
					}
				}
			}
		}
		return validValues;
	}
}
