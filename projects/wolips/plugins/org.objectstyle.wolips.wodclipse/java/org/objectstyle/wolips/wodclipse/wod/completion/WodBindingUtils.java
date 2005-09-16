package org.objectstyle.wolips.wodclipse.wod.completion;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.objectstyle.wolips.core.resources.types.api.ApiModel;
import org.objectstyle.wolips.core.resources.types.api.ApiModelException;
import org.objectstyle.wolips.core.resources.types.api.Wo;
import org.objectstyle.wolips.wodclipse.WodclipsePlugin;
import org.objectstyle.wolips.workbenchutilities.WorkbenchUtilitiesPlugin;
import org.osgi.framework.Bundle;

public class WodBindingUtils {
  public static final String[] FIELD_PREFIXES = { "", "_" };
  public static final String[] SET_METHOD_PREFIXES = { "set", "_set" };
  public static final String[] GET_METHOD_PREFIXES = { "get", "", "_get", "is", "_is", "_" };

  public static final int ACCESSORS_ONLY = 0;
  public static final int MUTATORS_ONLY = 1;

  public static String[] getBindingKeyNames(String _keyPath) {
    String[] bindingKeyNames = _keyPath.split("\\.");

    // Split tosses empty tokens, so we check to see if we're on the last "." and fake an empty token in the list
    if (_keyPath.length() > 0 && _keyPath.charAt(_keyPath.length() - 1) == '.') {
      String[] bindingKeyNamesWithFinalBlank = new String[bindingKeyNames.length + 1];
      System.arraycopy(bindingKeyNames, 0, bindingKeyNamesWithFinalBlank, 0, bindingKeyNames.length);
      bindingKeyNamesWithFinalBlank[bindingKeyNamesWithFinalBlank.length - 1] = "";
      bindingKeyNames = bindingKeyNamesWithFinalBlank;
    }

    return bindingKeyNames;
  }

  public static IType getLastType(IJavaProject _project, IType _elementType, String[] _bindingKeyNames) throws JavaModelException {
    IType currentType = _elementType;
    for (int i = 0; currentType != null && i < _bindingKeyNames.length - 1; i++) {
      IBindingKey bindingKey = WodBindingUtils.createMatchingBindingKey(currentType, _bindingKeyNames[i], WodBindingUtils.ACCESSORS_ONLY);
      if (bindingKey != null) {
        String nextTypeName = bindingKey.getNextTypeName();
        currentType = WodBindingUtils.resolveTypeWithName(_project, currentType, nextTypeName);
      }
    }
    return currentType;
  }

  public static IType resolveTypeWithName(IJavaProject _project, IType _typeContext, String _typeName) throws JavaModelException {
    String resolvedTypeName = JavaModelUtil.getResolvedTypeName(_typeName, _typeContext);
    IType resolvedType = JavaModelUtil.findType(_project, resolvedTypeName);
    return resolvedType;
  }

  public static String getShortClassName(String _fullClassName) {
    String shortClassName;
    int lastDotIndex = _fullClassName.lastIndexOf('.');
    if (lastDotIndex == -1) {
      shortClassName = _fullClassName;
    }
    else {
      shortClassName = _fullClassName.substring(lastDotIndex + 1);
    }
    return shortClassName;
  }

  public static IType findElementType(IJavaProject _javaProject, String _elementTypeName, boolean _requireTypeInProject, Map _elementNameToTypeCache) throws JavaModelException {
    // Search the current project for the given element type name
    IType type;
    if (_elementNameToTypeCache != null) {
      type = (IType) _elementNameToTypeCache.get(_elementTypeName);
    }
    else {
      type = null;
    }
    if (type == null) {
      TypeNameCollector typeNameCollector = new TypeNameCollector(_javaProject, _requireTypeInProject);
      WodBindingUtils.findMatchingElementClassNames(_elementTypeName, SearchPattern.R_EXACT_MATCH, typeNameCollector);
      if (typeNameCollector.isExactMatch()) {
        String matchingElementClassName = typeNameCollector.firstTypeName();
        type = typeNameCollector.getTypeForClassName(matchingElementClassName);
      }
      else if (!typeNameCollector.isEmpty()) {
        // there was more than one matching class!  crap!
        String matchingElementClassName = typeNameCollector.firstTypeName();
        type = typeNameCollector.getTypeForClassName(matchingElementClassName);
      }
      else {
        type = null;
      }
      if (type != null) {
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
    }
    else {
      packageName = _elementTypeName.substring(0, lastDotIndex).toCharArray();
      typeName = _elementTypeName.substring(lastDotIndex + 1).toCharArray();
    }
    searchEngine.searchAllTypeNames(packageName, typeName, _matchType, IJavaSearchConstants.CLASS, searchScope, _typeNameCollector, IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, null);
  }

  public static void fillInCompletionProposals(List _bindingKeys, String _token, int _tokenOffset, int _offset, Set _completionProposalsSet) {
    Iterator bindingKeysIter = _bindingKeys.iterator();
    while (bindingKeysIter.hasNext()) {
      IBindingKey bindingKey = (IBindingKey) bindingKeysIter.next();
      WodCompletionProposal completionProposal = new WodCompletionProposal(_token, _tokenOffset, _offset, bindingKey.getBindingName());
      _completionProposalsSet.add(completionProposal);
    }
  }

  public static IBindingKey createMatchingBindingKey(IType _type, String _nameStartingWith, int _accessorsOrMutators) throws JavaModelException {
    List bindingKeys = WodBindingUtils.createMatchingBindingKeys(_type, _nameStartingWith, true, _accessorsOrMutators);
    IBindingKey bindingKey;
    if (bindingKeys.size() == 0) {
      bindingKey = null;
    }
    else {
      bindingKey = (IBindingKey) bindingKeys.get(0);
    }
    return bindingKey;
  }

  public static List createMatchingBindingKeys(IType _type, String _nameStartingWith, boolean _requireExactNameMatch, int _accessorsOrMutators) throws JavaModelException {
    List bindingKeys = new LinkedList();

    if (_type != null) {
      String lowercaseNameStartingWith = _nameStartingWith.toLowerCase();
      ITypeHierarchy typeHierarchy = _type.newSupertypeHierarchy(null);
      IType[] types = typeHierarchy.getAllTypes();
      for (int typeNum = 0; (!_requireExactNameMatch || bindingKeys.size() == 0) && typeNum < types.length; typeNum++) {
        IField[] fields = types[typeNum].getFields();
        for (int fieldNum = 0; (!_requireExactNameMatch || bindingKeys.size() == 0) && fieldNum < fields.length; fieldNum++) {
          IBindingKey bindingKey = WodBindingUtils.createBindingKeyIfMatches(fields[fieldNum], lowercaseNameStartingWith, _requireExactNameMatch, _accessorsOrMutators);
          if (bindingKey != null) {
            bindingKeys.add(bindingKey);
          }
          //System.out.println("WODCompletionProcessor.nextType: field " + fields[fieldNum].getElementName() + "=>" + bindingKey);
        }

        if (!_requireExactNameMatch || bindingKeys.size() == 0) {
          IMethod[] methods = types[typeNum].getMethods();
          for (int methodNum = 0; (!_requireExactNameMatch || bindingKeys.size() == 0) && methodNum < methods.length; methodNum++) {
            IBindingKey bindingKey = WodBindingUtils.createBindingKeyIfMatches(methods[methodNum], lowercaseNameStartingWith, _requireExactNameMatch, _accessorsOrMutators);
            if (bindingKey != null) {
              bindingKeys.add(bindingKey);
            }
            //System.out.println("WODCompletionProcessor.nextType: method " + methods[methodNum].getElementName() + "=>" + bindingKey);
          }
        }
      }
    }

    return bindingKeys;
  }

  public static IBindingKey createBindingKeyIfMatches(IMember _member, String _nameStartingWith, boolean _requireExactNameMatch, int _accessorsOrMutators) throws JavaModelException {
    IBindingKey bindingKey = null;

    int flags = _member.getFlags();
    if (!Flags.isStatic(flags) && !Flags.isPrivate(flags)) {
      String[] possiblePrefixes;
      boolean memberSignatureMatches;
      if (_member instanceof IMethod) {
        IMethod method = (IMethod) _member;
        int parameterCount = method.getParameterNames().length;
        String returnType = method.getReturnType();
        if (_accessorsOrMutators == WodBindingUtils.ACCESSORS_ONLY) {
          memberSignatureMatches = (parameterCount == 0 && !"V".equals(returnType));
          possiblePrefixes = WodBindingUtils.GET_METHOD_PREFIXES;
        }
        else {
          memberSignatureMatches = (parameterCount == 1 && "V".equals(returnType));
          possiblePrefixes = WodBindingUtils.SET_METHOD_PREFIXES;
        }
      }
      else {
        memberSignatureMatches = true;
        possiblePrefixes = WodBindingUtils.FIELD_PREFIXES;
      }

      if (memberSignatureMatches) {
        String memberName = _member.getElementName();
        String lowercaseMemberName = memberName.toLowerCase();

        // Run through our list of valid prefixes and look for a match (i.e. whatever, _whatever, _getWhatever, etc).
        // If we find a match, then turn it into wod-style naming -- lowercase first letter, dropping the prefix
        for (int prefixNum = 0; bindingKey == null && prefixNum < possiblePrefixes.length; prefixNum++) {
          if (lowercaseMemberName.startsWith(possiblePrefixes[prefixNum])) {
            int prefixLength = possiblePrefixes[prefixNum].length();
            String lowercaseMemberNameWithoutPrefix = lowercaseMemberName.substring(prefixLength);
            if ((_requireExactNameMatch && lowercaseMemberNameWithoutPrefix.equals(_nameStartingWith)) || (!_requireExactNameMatch && lowercaseMemberNameWithoutPrefix.startsWith(_nameStartingWith))) {
              String bindingName = WodBindingUtils.toLowercaseFirstLetter(memberName.substring(prefixLength));
              bindingKey = new MemberBindingKey(bindingName, _member);
            }
          }
        }

        // System.out.println("WodBindingUtils.createBindingKeyIfMatches: " + _nameStartingWith + ", " + lowercaseMemberName + ", " + bindingKey);
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
      }
      else {
        lowercaseFirstLetterMemberName = _memberName;
      }
    }
    else {
      lowercaseFirstLetterMemberName = _memberName;
    }
    return lowercaseFirstLetterMemberName;
  }

  public static Wo findApiModelWo(IType _elementType, Map _elementTypeToWoCache) throws JavaModelException, ApiModelException {
    Object cachedWo = _elementTypeToWoCache.get(_elementType);
    Wo wo = null;
    if (cachedWo != null) {
      if (cachedWo instanceof Wo) {
        wo = (Wo)cachedWo;
      }
      else {
        wo = null;
      }
    }
    else if (cachedWo == null) {
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
            //apiFile = new File("/Users/mschrag/Documents/workspace/org.objectstyle.wolips.wodclipse/java/org/objectstyle/wolips/wodclipse/api/WebObjectDefinitions.xml");
            apiModel = new ApiModel(woDefinitionsURL);
          }
          else if (fileExists) {
            apiModel = new ApiModel(apiFile);
          }
        }
      }
      else if (typeContainer instanceof ICompilationUnit) {
        ICompilationUnit cu = (ICompilationUnit) typeContainer;
        IResource resource = cu.getCorrespondingResource();
        String name = resource.getName();
        List apiResources = WorkbenchUtilitiesPlugin.findResourcesInProjectByNameAndExtensions(_elementType.getJavaProject().getProject(), _elementType.getElementName(), new String[] { "api" }, false);
        if (apiResources != null && apiResources.size() > 0) {
          IResource apiResource = (IResource) apiResources.get(0);
          apiModel = new ApiModel(apiResource.getLocation().toFile());
        }
      }

      if (apiModel != null) {
        Wo[] wos = apiModel.getWODefinitions().getWos();
        if (wos.length == 0) {
          wo = null;
        }
        else if (wos.length == 1) {
          wo = wos[0];
        }
        else {
          String className;
          for (int i = 0; wo == null && i < wos.length; i++) {
            if (_elementType.getElementName().equals(wos[i].getClassName())) {
              wo = wos[i];
            }
          }
        }
      }
      
      if (wo == null) {
        _elementTypeToWoCache.put(_elementType, "NOAPI");
      }
      else {
        _elementTypeToWoCache.put(_elementType, wo);
      }
    }
    
    return wo;
  }
}
