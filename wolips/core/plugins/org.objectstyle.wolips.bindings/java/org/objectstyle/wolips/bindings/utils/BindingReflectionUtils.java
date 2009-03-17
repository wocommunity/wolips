package org.objectstyle.wolips.bindings.utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.internal.core.DefaultWorkingCopyOwner;
import org.eclipse.jdt.internal.ui.search.JavaSearchScopeFactory;
import org.objectstyle.wolips.bindings.wod.BindingValueKey;
import org.objectstyle.wolips.bindings.wod.BindingValueKeyPath;
import org.objectstyle.wolips.bindings.wod.TypeCache;
import org.objectstyle.wolips.core.resources.types.TypeNameCollector;
import org.objectstyle.wolips.core.resources.types.WOHierarchyScope;

public class BindingReflectionUtils {
  public static final String[] FIELD_PREFIXES = { "", "_" };

  public static final String[] SET_METHOD_PREFIXES = { "set", "_set" };

  public static final String[] GET_METHOD_PREFIXES = { "get", "", "_get", "is", "_is", "_" };

  public static final int ACCESSORS_ONLY = 0;

  public static final int MUTATORS_ONLY = 1;

  public static final int ACCESSORS_OR_VOID = 2;

  public static final int VOID_ONLY = 3;

  public static boolean isBoolean(String typeName) {
    return "boolean".equals(typeName) || "Boolean".equals(typeName) || "java.lang.Boolean".equals(typeName);
  }

  public static boolean isImportRequired(String typeName) {
    return typeName != null && !isPrimitive(typeName) && !typeName.equals("java.lang." + Signature.getSimpleName(typeName));
  }

  public static boolean isPrimitive(String typeName) {
    return ("boolean".equals(typeName) || "byte".equals(typeName) || "char".equals(typeName) || "int".equals(typeName) || "short".equals(typeName) || "float".equals(typeName) || "double".equals(typeName));
  }

  public static String getFullClassName(IJavaProject javaProject, String shortClassName) throws JavaModelException {
    String expandedClassName = shortClassName;
    if (expandedClassName != null && expandedClassName.indexOf('.') == -1) {
      if ("String".equals(shortClassName)) {
        expandedClassName = "java.lang.String";
      }
      else if ("Object".equals(shortClassName)) {
        expandedClassName = "java.lang.Object";
      }
      else if (isPrimitive(expandedClassName)) {
        // ignore primitives
      }
      else {
        SearchEngine searchEngine = new SearchEngine();
        IJavaSearchScope searchScope = JavaSearchScopeFactory.getInstance().createJavaProjectSearchScope(javaProject, JavaSearchScopeFactory.ALL);
        TypeNameCollector typeNameCollector = new TypeNameCollector(javaProject, false);
        searchEngine.searchAllTypeNames(null, SearchPattern.R_EXACT_MATCH, shortClassName.toCharArray(), IJavaSearchConstants.TYPE, IJavaSearchConstants.TYPE, searchScope, typeNameCollector, IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, new NullProgressMonitor());
        Set<String> typeNames = typeNameCollector.getTypeNames();
        if (typeNames.size() == 1) {
          expandedClassName = typeNames.iterator().next();
        }
        else if (typeNames.size() == 0) {
          System.out.println("BindingReflectionUtils.getFullClassName: Unknown type name " + shortClassName);
        }
        else {
          System.out.println("BindingReflectionUtils.getFullClassName: Ambiguous type name " + shortClassName + " (" + typeNames + ")");
        }
      }
    }
    return expandedClassName;
  }

  public static String getShortClassName(String fullClassName) {
    String shortClassName;
    int lastDotIndex = fullClassName.lastIndexOf('.');
    if (lastDotIndex == -1) {
      shortClassName = fullClassName;
    }
    else {
      shortClassName = fullClassName.substring(lastDotIndex + 1);
    }
    return shortClassName;
  }

  public static IType findElementType(IJavaProject javaProject, String elementTypeName, boolean requireTypeInProject, TypeCache cache) throws JavaModelException {
    // Search the current project for the given element type name
    String typeName = cache.getApiCache(javaProject).getElementTypeNamed(elementTypeName);
    IType type = null;
    if (typeName != null) {
      type = javaProject.findType(typeName);
    }
    else {
      TypeNameCollector typeNameCollector = new TypeNameCollector(javaProject, requireTypeInProject);
      BindingReflectionUtils.findMatchingElementClassNames(elementTypeName, SearchPattern.R_EXACT_MATCH, typeNameCollector, null);
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
        cache.getApiCache(javaProject).setElementTypeForName(type, elementTypeName);
      }
    }
    return type;
  }

  public static void findMatchingElementClassNames(String elementTypeName, int matchType, TypeNameCollector typeNameCollector, IProgressMonitor progressMonitor) throws JavaModelException {
    if (elementTypeName != null) {
      SearchEngine searchEngine = new SearchEngine();
      IJavaSearchScope searchScope = new WOHierarchyScope(typeNameCollector.getSuperclassType(), typeNameCollector.getProject());
      int lastDotIndex = elementTypeName.lastIndexOf('.');
      char[] packageName;
      char[] typeName;
      if (lastDotIndex == -1) {
        packageName = null;
        typeName = elementTypeName.toCharArray();
      }
      else {
        packageName = elementTypeName.substring(0, lastDotIndex).toCharArray();
        typeName = elementTypeName.substring(lastDotIndex + 1).toCharArray();
      }
      searchEngine.searchAllTypeNames(packageName, SearchPattern.R_EXACT_MATCH, typeName, matchType, IJavaSearchConstants.CLASS, searchScope, typeNameCollector, IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, progressMonitor);
    }
  }

  public static boolean isWOComponent(IType type, TypeCache cache) throws JavaModelException {
    return BindingReflectionUtils.isType(type, new String[] { "com.webobjects.appserver.WOComponent" }, cache);
  }

  public static boolean isNSKeyValueCoding(IType type, TypeCache cache) throws JavaModelException {
    return BindingReflectionUtils.isType(type, new String[] { "com.webobjects.foundation.NSKeyValueCoding" }, cache);
  }

  public static boolean isNSCollection(IType type, TypeCache cache) throws JavaModelException {
    return BindingReflectionUtils.isType(type, new String[] { "com.webobjects.foundation.NSDictionary", "com.webobjects.foundation.NSArray", "com.webobjects.foundation.NSSet", "er.extensions.ERXLocalizer" }, cache);
  }

  public static boolean isType(IType type, String[] possibleTypes, TypeCache cache) throws JavaModelException {
    boolean isType = false;
    //ITypeHierarchy typeHierarchy = SuperTypeHierarchyCache.getTypeHierarchy(type);
    List<IType> types = cache.getSupertypesOf(type);
    for (int typeNum = 0; !isType && typeNum < types.size(); typeNum++) {
      String name = types.get(typeNum).getFullyQualifiedName();
      for (int possibleTypeNum = 0; !isType && possibleTypeNum < possibleTypes.length; possibleTypeNum++) {
        if (possibleTypes[possibleTypeNum].equals(name)) {
          isType = true;
        }
      }
    }
    return isType;
  }

  public static Set<String> getArrayOperators() {
    Set<String> operators = new HashSet<String>();
    operators.add("avg");
    operators.add("count");
    operators.add("min");
    operators.add("max");
    operators.add("sum");
    operators.add("sort");
    operators.add("sortAsc");
    operators.add("sortDesc");
    return operators;
  }

  public static List<BindingValueKey> getBindingKeys(IJavaProject javaProject, IType type, String nameStartingWith, boolean requireExactNameMatch, int accessorsOrMutators, boolean allowInheritanceDuplicates, TypeCache cache) throws JavaModelException {
    List<BindingValueKey> bindingKeys = new LinkedList<BindingValueKey>();
//    if (_requireExactNameMatch && BindingReflectionUtils.isBooleanValue(_nameStartingWith)) {
//      return bindingKeys;
//    }

    //System.out.println("BindingReflectionUtils.getBindingKeys: " + _type.getElementName() + ", " + _nameStartingWith + ", " + _requireExactNameMatch + ", " + _accessorsOrMutators);
    if (type != null) {
      String lowercaseNameStartingWith = nameStartingWith.toLowerCase();

      Set<String> additionalProposals = new HashSet<String>();

      // We want to show fields from your WOApplication, WOSession, and
      // WODirectAction subclasses ...
      //ITypeHierarchy typeHierarchy = SuperTypeHierarchyCache.getTypeHierarchy(_type);
      List<IType> types = cache.getSupertypesOf(type);
      if (types != null) {
        IType usuallySubclassedSupertype = null;
        IType nextType = null;
        for (int typeNum = 0; usuallySubclassedSupertype == null && typeNum < types.size(); typeNum++) {
          String typeName = types.get(typeNum).getElementName();
          if ("WOApplication".equals(typeName) || "WOSession".equals(typeName) || "WODirectAction".equals(typeName)) {
            usuallySubclassedSupertype = types.get(typeNum);
          }
          else if ("NSArray".equals(typeName)) {
            for (String operator : BindingReflectionUtils.getArrayOperators()) {
              additionalProposals.add("@" + operator);
            }
            nextType = types.get(typeNum);
          }
        }

        for (String additionalProposal : additionalProposals) {
          if (additionalProposal.startsWith(nameStartingWith)) {
            BindingValueKey additionalKey = new BindingValueKey(additionalProposal, null, null, javaProject, cache);
            // MS: this is a hack to prevent NPE's because we don't know the next type right now ...
            //additionalKey.setNextType(nextType);
            bindingKeys.add(additionalKey);
          }
        }

        if (usuallySubclassedSupertype != null) {
          //typeHierarchy = _type.newTypeHierarchy(_javaProject, null);
          //typeHierarchy = SubTypeHierachyCache.getTypeHierarchy(_type);
          types = cache.getSubtypesOfInProject(usuallySubclassedSupertype, javaProject);
        }
      }

      if (types != null) {
        for (int typeNum = 0; (!requireExactNameMatch || bindingKeys.size() == 0) && typeNum < types.size(); typeNum++) {
          //for (int typeNum = types.length - 1; (!_requireExactNameMatch || bindingKeys.size() == 0) && typeNum >= 0; typeNum --) {
          BindingReflectionUtils.fillInBindingKeys(type, types.get(typeNum), lowercaseNameStartingWith, requireExactNameMatch, accessorsOrMutators, allowInheritanceDuplicates, javaProject, bindingKeys, cache);
        }
      }
    }

    return bindingKeys;
  }

  protected static void fillInBindingKeys(IType declaringType, IType type, String lowercaseNameStartingWith, boolean requireExactNameMatch, int accessorsOrMutators, boolean allowInheritanceDuplicates, IJavaProject javaProject, List<BindingValueKey> bindingKeys, TypeCache cache) throws JavaModelException {
    //System.out.println("BindingReflectionUtils.getBindingKeys: a " + type.getFullyQualifiedName());

    IField[] fields = type.getFields();
    for (int fieldNum = 0; (!requireExactNameMatch || bindingKeys.size() == 0) && fieldNum < fields.length; fieldNum++) {
      for (String prefix : BindingReflectionUtils.FIELD_PREFIXES) {
        BindingValueKey bindingKey = BindingReflectionUtils.getBindingKeyIfMatches(javaProject, declaringType, fields[fieldNum], prefix + lowercaseNameStartingWith, prefix, requireExactNameMatch, accessorsOrMutators, cache);
        if (bindingKey != null) {
          bindingKeys.add(bindingKey);
          break;
        }
      }
    }

    if (!requireExactNameMatch || bindingKeys.size() == 0) {
      IMethod[] methods = type.getMethods();
      String[] prefixes;
      if (accessorsOrMutators == BindingReflectionUtils.ACCESSORS_ONLY) {
        prefixes = BindingReflectionUtils.GET_METHOD_PREFIXES;
      }
      else if (accessorsOrMutators == BindingReflectionUtils.ACCESSORS_OR_VOID) {
        prefixes = BindingReflectionUtils.GET_METHOD_PREFIXES;
      }
      else if (accessorsOrMutators == BindingReflectionUtils.MUTATORS_ONLY) {
        prefixes = BindingReflectionUtils.SET_METHOD_PREFIXES;
      }
      else {
        prefixes = new String[0];
      }

      for (int methodNum = 0; (!requireExactNameMatch || bindingKeys.size() == 0) && methodNum < methods.length; methodNum++) {
        for (String prefix : prefixes) {
          //System.out.println("BindingReflectionUtils.getBindingKeys: checking for " + prefix + methods[methodNum].getElementName());
          BindingValueKey bindingKey = BindingReflectionUtils.getBindingKeyIfMatches(javaProject, declaringType, methods[methodNum], prefix + lowercaseNameStartingWith, prefix, requireExactNameMatch, accessorsOrMutators, cache);
          if (bindingKey != null) {
            if (allowInheritanceDuplicates || !bindingKeys.contains(bindingKey)) {
              bindingKeys.add(bindingKey);
            }
            break;
          }
        }
      }
    }
  }

  public static boolean isDefaultPackage(IMember member) {
    IType declaringType = member.getDeclaringType();
    String declaringTypePackageName = declaringType.getPackageFragment().getElementName();
    return declaringTypePackageName == null || declaringTypePackageName.length() == 0;
  }

  public static BindingValueKey getBindingKeyIfMatches(IJavaProject javaProject, IType type, IMember member, String nameStartingWith, String prefix, boolean requireExactNameMatch, int accessorsOrMutators, TypeCache cache) throws JavaModelException {
    //System.out.println("BindingReflectionUtils.getBindingKeyIfMatches: " + member.getElementName() + " starts with " + nameStartingWith);
    BindingValueKey bindingKey = null;

    int flags = member.getFlags();
    boolean visible = false;
    // Private is never an option
    if (Flags.isPrivate(flags)) {
      visible = false;
    }
    // Don't show static methods and fields
    else if (Flags.isStatic(flags)) {
      visible = false;
    }
    // Public bindings are always visible
    else if (Flags.isPublic(flags)) {
      visible = true;
    }
    // Components that are not in a package can have bindings to protected fields
    else if ((Flags.isProtected(flags) || Flags.isPackageDefault(flags)) && BindingReflectionUtils.isDefaultPackage(member)) {
      visible = true;
    }
    if (visible) {
      boolean memberSignatureMatches;
      if (member instanceof IMethod) {
        IMethod method = (IMethod) member;
        if (method.isConstructor()) {
          memberSignatureMatches = false;
        }
        else {
          int parameterCount = method.getParameterTypes().length;
          String returnType = method.getReturnType();
          if (accessorsOrMutators == BindingReflectionUtils.ACCESSORS_ONLY) {
            memberSignatureMatches = (parameterCount == 0 && !"V".equals(returnType));
          }
          else if (accessorsOrMutators == BindingReflectionUtils.ACCESSORS_OR_VOID) {
            memberSignatureMatches = (parameterCount == 0);
          }
          else if (accessorsOrMutators == BindingReflectionUtils.VOID_ONLY) {
            memberSignatureMatches = (parameterCount == 0 && "V".equals(returnType));
          }
          else {
            memberSignatureMatches = (parameterCount == 1 && "V".equals(returnType));
          }
        }
      }
      else {
        memberSignatureMatches = true;
      }

      if (memberSignatureMatches) {
        String memberName = member.getElementName();
        String lowercaseMemberName = memberName.toLowerCase();

        int prefixLength = prefix.length();
        if ((requireExactNameMatch && lowercaseMemberName.equals(nameStartingWith)) || (!requireExactNameMatch && lowercaseMemberName.startsWith(nameStartingWith))) {
          String bindingName = BindingReflectionUtils.toLowercaseFirstLetter(memberName.substring(prefixLength));
          //System.out.println("BindingReflectionUtils.getBindingKeyIfMatches:   bindingName = " + bindingName);
          if (nameStartingWith.length() > 0 || !bindingName.startsWith("_")) {
            bindingKey = new BindingValueKey(bindingName, type, member, javaProject, cache);
          }
        }
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

  public static Set<String> _systemTypeNames;
  public static Set<String> _uselessSystemBindings;
  public static Set<String> _usefulSystemBindings;
  static {
    _systemTypeNames = new HashSet<String>();
    _systemTypeNames.add("Object");
    _systemTypeNames.add("WOElement");
    _systemTypeNames.add("WOActionResults");
    _systemTypeNames.add("WOComponent");

    _uselessSystemBindings = new HashSet<String>();
    _uselessSystemBindings.add("baseURL");
    _uselessSystemBindings.add("bindingKeys");
    _uselessSystemBindings.add("cachingEnabled");
    _uselessSystemBindings.add("childTemplate");
    _uselessSystemBindings.add("getClass");
    _uselessSystemBindings.add("class");
    _uselessSystemBindings.add("clone");
    _uselessSystemBindings.add("componentDefinition");
    _uselessSystemBindings.add("componentUnroll");
    _uselessSystemBindings.add("frameworkName");
    _uselessSystemBindings.add("generateResponse");
    _uselessSystemBindings.add("hashCode");
    _uselessSystemBindings.add("isCachingEnabled");
    _uselessSystemBindings.add("eventLoggingEnabled");
    _uselessSystemBindings.add("isEventLoggingEnabled");
    _uselessSystemBindings.add("isPage");
    _uselessSystemBindings.add("isStateless");
    _uselessSystemBindings.add("stateless");
    _uselessSystemBindings.add("keyAssociations");
    _uselessSystemBindings.add("name");
    _uselessSystemBindings.add("page");
    _uselessSystemBindings.add("parent");
    _uselessSystemBindings.add("path");
    _uselessSystemBindings.add("pathURL");
    _uselessSystemBindings.add("synchronizesVariablesWithBindings");
    _uselessSystemBindings.add("template");
    _uselessSystemBindings.add("toString");
    _uselessSystemBindings.add("unroll");

    _usefulSystemBindings = new HashSet<String>();
    _usefulSystemBindings.add("application");
    _usefulSystemBindings.add("context");
    _usefulSystemBindings.add("hasSession");
    _usefulSystemBindings.add("session");
  }

  public static boolean isSystemBindingValueKey(BindingValueKey bindingValueKey, boolean showUsefulSystemBindings) {
    boolean isSystemBinding = false;
    if (bindingValueKey != null && bindingValueKey.getDeclaringType() != null) {
      String declaringTypeName = bindingValueKey.getDeclaringType().getElementName();
      String bindingName = bindingValueKey.getBindingName();
      if (BindingReflectionUtils._systemTypeNames.contains(declaringTypeName)) {
        if (!showUsefulSystemBindings && BindingReflectionUtils._usefulSystemBindings.contains(bindingName)) {
          isSystemBinding = true;
        }
        else if (BindingReflectionUtils._uselessSystemBindings.contains(bindingName)) {
          isSystemBinding = true;
        }
      }
      else if (bindingName.startsWith("_")) {
        isSystemBinding = true;
      }
    }
    return isSystemBinding;
  }

  public static boolean isBooleanValue(String keyPath) {
    return "true".equalsIgnoreCase(keyPath) || "false".equalsIgnoreCase(keyPath) || "yes".equalsIgnoreCase(keyPath) || "no".equalsIgnoreCase(keyPath);
  }

  public static List<BindingValueKey> filterSystemBindingValueKeys(List<BindingValueKey> bindingKeys, boolean showUsefulSystemBindings) {
    Set<BindingValueKey> systemBindingValueKeys = new HashSet<BindingValueKey>();
    for (BindingValueKey bindingKey : bindingKeys) {
      if (BindingReflectionUtils.isSystemBindingValueKey(bindingKey, showUsefulSystemBindings)) {
        systemBindingValueKeys.add(bindingKey);
      }
    }

    List<BindingValueKey> filteredBindingValueKeys;
    if (systemBindingValueKeys.isEmpty()) {
      filteredBindingValueKeys = bindingKeys;
    }
    else {
      filteredBindingValueKeys = new LinkedList<BindingValueKey>();
      for (BindingValueKey bindingKey : bindingKeys) {
//        System.out.println("BindingReflectionUtils.filterSystemBindingValueKeys: FILTERING? " + bindingKey.getBindingName());
        if (!systemBindingValueKeys.contains(bindingKey)) {
          filteredBindingValueKeys.add(bindingKey);
        }
//        else {
//          System.out.println("BindingReflectionUtils.filterSystemBindingValueKeys:   SKIPPED");
//        }
      }
    }

    return filteredBindingValueKeys;
  }

  /**
   * Returns the BindingValueKeys for the given type grouped by which class the
   * keys were contributed from.
   * 
   * @param startingWith a partial keypath or "" for all
   * @param type the starting type
   * @param cache the TypeCache
   * @return a map of supertypes to keys
   * @throws JavaModelException if the type information cannot be loaded
   */
  public static Map<IType, Set<BindingValueKey>> getGroupedBindingValueKeys(String startingWith, IType type, TypeCache cache) throws JavaModelException {
    BindingValueKeyPath bindingValueKeyPath = new BindingValueKeyPath(startingWith, type, type.getJavaProject(), cache);
    List<BindingValueKey> bindingValueKeys = bindingValueKeyPath.getPartialMatchesForLastBindingKey(true);
    List<BindingValueKey> filteredBindingValueKeys = BindingReflectionUtils.filterSystemBindingValueKeys(bindingValueKeys, true);
    Set<BindingValueKey> uniqueBingingValueKeys = new TreeSet<BindingValueKey>(filteredBindingValueKeys);

    Map<IType, Set<BindingValueKey>> typeKeys = new TreeMap<IType, Set<BindingValueKey>>(new TypeDepthComparator(cache));
    for (BindingValueKey key : uniqueBingingValueKeys) {
      IType declaringType = key.getDeclaringType();
      Set<BindingValueKey> typeKeysSet = typeKeys.get(declaringType);
      if (typeKeysSet == null) {
        typeKeysSet = new TreeSet<BindingValueKey>();
        typeKeys.put(declaringType, typeKeysSet);
      }
      typeKeysSet.add(key);
    }

    return typeKeys;
  }
}
