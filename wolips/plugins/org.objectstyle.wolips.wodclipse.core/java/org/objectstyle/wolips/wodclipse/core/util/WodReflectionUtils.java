package org.objectstyle.wolips.wodclipse.core.util;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.internal.corext.util.SuperTypeHierarchyCache;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.model.BindingValueKey;

public class WodReflectionUtils {
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
    }
    else {
      shortClassName = _fullClassName.substring(lastDotIndex + 1);
    }
    return shortClassName;
  }

  public static IType findElementType(IJavaProject _javaProject, String _elementTypeName, boolean _requireTypeInProject, WodParserCache cache) throws JavaModelException {
    // Search the current project for the given element type name
    String typeName = cache.getContext().getElementTypeNamed(_elementTypeName);
    IType type = null;
    if (typeName != null) {
      type = _javaProject.findType(typeName);
    }
    else if (typeName == null) {
      TypeNameCollector typeNameCollector = new TypeNameCollector(_javaProject, _requireTypeInProject);
      WodReflectionUtils.findMatchingElementClassNames(_elementTypeName, SearchPattern.R_EXACT_MATCH, typeNameCollector, null);
      if (typeNameCollector.isExactMatch()) {
        String matchingElementClassName = typeNameCollector.firstTypeName();
        type = typeNameCollector.getTypeForClassName(matchingElementClassName);
      }
      else if (!typeNameCollector.isEmpty()) {
        // there was more than one matching class! crap!
        String matchingElementClassName = typeNameCollector.firstTypeName();
        type = typeNameCollector.getTypeForClassName(matchingElementClassName);
      }
      if (type != null) {
        cache.getContext().setElementTypeForName(type, _elementTypeName);
      }
    }
    return type;
  }

  public static void findMatchingElementClassNames(String _elementTypeName, int _matchType, TypeNameCollector _typeNameCollector, IProgressMonitor progressMonitor) throws JavaModelException {
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
    searchEngine.searchAllTypeNames(packageName, typeName, _matchType, IJavaSearchConstants.CLASS, searchScope, _typeNameCollector, IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, progressMonitor);
  }

  public static boolean isNSKeyValueCoding(IType type) throws JavaModelException {
    return WodReflectionUtils.isType(type, new String[] { "com.webobjects.foundation.NSKeyValueCoding" });
  }

  public static boolean isNSCollection(IType type) throws JavaModelException {
    return WodReflectionUtils.isType(type, new String[] { "com.webobjects.foundation.NSDictionary", "com.webobjects.foundation.NSArray", "com.webobjects.foundation.NSSet", "er.extensions.ERXLocalizer" });
  }

  public static boolean isType(IType type, String[] possibleTypes) throws JavaModelException {
    boolean isType = false;
    ITypeHierarchy typeHierarchy = SuperTypeHierarchyCache.getTypeHierarchy(type);
    IType[] types = typeHierarchy.getAllTypes();
    for (int typeNum = 0; !isType && typeNum < types.length; typeNum++) {
      String name = types[typeNum].getFullyQualifiedName();
      for (int possibleTypeNum = 0; !isType && possibleTypeNum < possibleTypes.length; possibleTypeNum++) {
        if (possibleTypes[possibleTypeNum].equals(name)) {
          isType = true;
        }
      }
    }
    return isType;
  }

  public static List<BindingValueKey> getBindingKeys(IJavaProject _javaProject, IType _type, String _nameStartingWith, boolean _requireExactNameMatch, int _accessorsOrMutators, WodParserCache cache) throws JavaModelException {
    List<BindingValueKey> bindingKeys = new LinkedList<BindingValueKey>();

    String nameStartingWith = _nameStartingWith;
    if (_type != null) {
      String lowercaseNameStartingWith = nameStartingWith.toLowerCase();

      ITypeHierarchy typeHierarchy;

      Set<String> additionalProposals = new HashSet<String>();
      
      // We want to show fields from your WOApplication, WOSession, and
      // WODirectAction subclasses ...
      typeHierarchy = SuperTypeHierarchyCache.getTypeHierarchy(_type);
      IType[] types = typeHierarchy.getAllTypes();
      if (types != null) {
        IType nextType = null;
        boolean isUsuallySubclassed = false;
        for (int typeNum = 0; !isUsuallySubclassed && typeNum < types.length; typeNum++) {
          String typeName = types[typeNum].getElementName();
          if ("WOApplication".equals(typeName) || "WOSession".equals(typeName) || "WODirectAction".equals(typeName)) {
            isUsuallySubclassed = true;
          }
          else if ("NSArray".equals(typeName)) {
            additionalProposals.add("@avg");
            additionalProposals.add("@count");
            additionalProposals.add("@min");
            additionalProposals.add("@max");
            additionalProposals.add("@sum");
            additionalProposals.add("@sort");
            additionalProposals.add("@sortAsc");
            additionalProposals.add("@sortDesc");
            nextType = types[typeNum];
          }
        }
        
        for (String additionalProposal : additionalProposals) {
          if (additionalProposal.startsWith(_nameStartingWith)) {
            BindingValueKey additionalKey = new BindingValueKey(additionalProposal, null, _javaProject, cache);
            // MS: this is a hack to prevent NPE's because we don't know the next type right now ...
            //additionalKey.setNextType(nextType);
            bindingKeys.add(additionalKey);
          }
        }
        
        if (isUsuallySubclassed) {
          //typeHierarchy = _type.newTypeHierarchy(_javaProject, null);
          typeHierarchy = SubTypeHierachyCache.getTypeHierarchy(_type);
          types = typeHierarchy.getAllTypes();
        }
      }
      
      if (types != null) {
        for (int typeNum = 0; (!_requireExactNameMatch || bindingKeys.size() == 0) && typeNum < types.length; typeNum++) {
          IField[] fields = types[typeNum].getFields();
          for (int fieldNum = 0; (!_requireExactNameMatch || bindingKeys.size() == 0) && fieldNum < fields.length; fieldNum++) {
            BindingValueKey bindingKey = WodReflectionUtils.getBindingKeyIfMatches(_javaProject, fields[fieldNum], lowercaseNameStartingWith, _requireExactNameMatch, _accessorsOrMutators, cache);
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
              BindingValueKey bindingKey = WodReflectionUtils.getBindingKeyIfMatches(_javaProject, methods[methodNum], lowercaseNameStartingWith, _requireExactNameMatch, _accessorsOrMutators, cache);
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
    }

    return bindingKeys;
  }

  public static BindingValueKey getBindingKeyIfMatches(IJavaProject javaProject, IMember member, String nameStartingWith, boolean requireExactNameMatch, int accessorsOrMutators, WodParserCache cache) throws JavaModelException {
    BindingValueKey bindingKey = null;

    int flags = member.getFlags();
    if (!Flags.isStatic(flags) && Flags.isPublic(flags)) {
      String[] possiblePrefixes;
      boolean memberSignatureMatches;
      if (member instanceof IMethod) {
        IMethod method = (IMethod) member;
        if (method.isConstructor()) {
          possiblePrefixes = WodReflectionUtils.GET_METHOD_PREFIXES;
          memberSignatureMatches = false;
        }
        else {
          int parameterCount = method.getParameterNames().length;
          String returnType = method.getReturnType();
          if (accessorsOrMutators == WodReflectionUtils.ACCESSORS_ONLY) {
            memberSignatureMatches = (parameterCount == 0 && !"V".equals(returnType));
            possiblePrefixes = WodReflectionUtils.GET_METHOD_PREFIXES;
          }
          else if (accessorsOrMutators == WodReflectionUtils.ACCESSORS_OR_VOID) {
            memberSignatureMatches = (parameterCount == 0);
            possiblePrefixes = WodReflectionUtils.GET_METHOD_PREFIXES;
          }
          else if (accessorsOrMutators == WodReflectionUtils.VOID_ONLY) {
            memberSignatureMatches = (parameterCount == 0 && "V".equals(returnType));
            possiblePrefixes = WodReflectionUtils.GET_METHOD_PREFIXES;
          }
          else {
            memberSignatureMatches = (parameterCount == 1 && "V".equals(returnType));
            possiblePrefixes = WodReflectionUtils.SET_METHOD_PREFIXES;
          }
        }
      }
      else {
        memberSignatureMatches = true;
        possiblePrefixes = WodReflectionUtils.FIELD_PREFIXES;
      }

      if (memberSignatureMatches) {
        String memberName = member.getElementName();
        String lowercaseMemberName = memberName.toLowerCase();

        // Run through our list of valid prefixes and look for a match
        // (i.e. whatever, _whatever, _getWhatever, etc).
        // If we find a match, then turn it into wod-style naming --
        // lowercase first letter, dropping the prefix
        for (int prefixNum = 0; bindingKey == null && prefixNum < possiblePrefixes.length; prefixNum++) {
          if (lowercaseMemberName.startsWith(possiblePrefixes[prefixNum])) {
            int prefixLength = possiblePrefixes[prefixNum].length();
            String lowercaseMemberNameWithoutPrefix = lowercaseMemberName.substring(prefixLength);
            if ((requireExactNameMatch && lowercaseMemberNameWithoutPrefix.equals(nameStartingWith)) || (!requireExactNameMatch && lowercaseMemberNameWithoutPrefix.startsWith(nameStartingWith))) {
              String bindingName = WodReflectionUtils.toLowercaseFirstLetter(memberName.substring(prefixLength));
              if (nameStartingWith.length() > 0 || !bindingName.startsWith("_")) {
                bindingKey = new BindingValueKey(bindingName, member, javaProject, cache);
              }
            }
          }
        }

        // System.out.println("WodBindingUtils.getBindingKeyIfMatches:
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

  public static boolean isSystemBindingValueKey(BindingValueKey bindingValueKey, boolean includeCommonKeys) {
    boolean isSystemBinding = false;
    if (bindingValueKey != null && bindingValueKey.getDeclaringType() != null && "WOComponent".equals(bindingValueKey.getDeclaringType().getElementName())) {
      String bindingName = bindingValueKey.getBindingName();
      if ("cachingEnabled".equals(bindingName)) {
        isSystemBinding = true;
      }
      else if ("isPage".equals(bindingName)) {
        isSystemBinding = true;
      }
      else if ("keyAssociations".equals(bindingName)) {
        isSystemBinding = true;
      }
      else if (includeCommonKeys && "context".equals(bindingName)) {
        isSystemBinding = true;
      }
    }
    return isSystemBinding;
  }
}
