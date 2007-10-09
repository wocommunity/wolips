package org.objectstyle.wolips.bindings.wod;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.bindings.utils.BindingReflectionUtils;
import org.objectstyle.wolips.bindings.wod.TypeCache;

public class BindingValueKeyPath {
  private IJavaProject _javaProject;

  private IType _contextType;

  private String[] _bindingKeyNames;

  private BindingValueKey[] _bindingKeys;

  private String _originalKeyPath;

  private String _validKeyPath;

  private String _operator;

  private String _invalidKey;

  private boolean _valid;

  private boolean _nsCollection;

  private boolean _nsKVC;

  private boolean _ambiguous;

  private String _helperFunction;

  private TypeCache _cache;

  public BindingValueKeyPath(String keyPath, IType contextType, IJavaProject javaProject, TypeCache cache) throws JavaModelException {
    _cache = cache;
    _javaProject = javaProject;
    _contextType = contextType;
    _valid = true;
    _originalKeyPath = keyPath;

    boolean isKeyPath = true;
    // short-circuit for booleans
    if (keyPath.length() > 0) {
      char ch = keyPath.charAt(0);
      // short-circuit for quoted strings
      if (ch == '\"' || ch == '\'') {
        isKeyPath = false;
      }
      // short-circuit for numbers
      else if (Character.isDigit(ch)) {
        isKeyPath = false;
      }
    }

    if (isKeyPath) {
      String partialKeyPath;
      int pipeIndex = keyPath.indexOf('|');
      if (pipeIndex == -1) {
        partialKeyPath = keyPath;
      }
      else {
        partialKeyPath = keyPath.substring(0, pipeIndex);
        _helperFunction = keyPath.substring(pipeIndex + 1);
      }

      String[] bindingKeyNames;
      int atIndex = partialKeyPath.indexOf('@');
      if (atIndex != -1) {
        _operator = partialKeyPath.substring(atIndex + 1);
        partialKeyPath = partialKeyPath.substring(0, atIndex);
      }
      bindingKeyNames = partialKeyPath.split("\\.");

      // Split tosses empty tokens, so we check to see if we're on the last
      // "." and fake an empty token in the list
      if (keyPath.length() > 0 && keyPath.charAt(keyPath.length() - 1) == '.' && _operator == null && _helperFunction == null) {
        String[] bindingKeyNamesWithFinalBlank = new String[bindingKeyNames.length + 1];
        System.arraycopy(bindingKeyNames, 0, bindingKeyNamesWithFinalBlank, 0, bindingKeyNames.length);
        bindingKeyNamesWithFinalBlank[bindingKeyNamesWithFinalBlank.length - 1] = "";
        _bindingKeyNames = bindingKeyNamesWithFinalBlank;
      }
      else {
        _bindingKeyNames = bindingKeyNames;
      }

      int invalidKeyNum = -1;
      IType currentType = _contextType;
      List<BindingValueKey> bindingKeysList = new LinkedList<BindingValueKey>();
      for (int keyNum = 0; currentType != null && keyNum < _bindingKeyNames.length; keyNum++) {
        // we can't verify helper functions or @arrayOps
        List<BindingValueKey> bindingKeys = BindingReflectionUtils.getBindingKeys(_javaProject, currentType, _bindingKeyNames[keyNum], true, BindingReflectionUtils.ACCESSORS_OR_VOID, cache);
        if (!bindingKeys.isEmpty()) {
          // NTS: Deal with multiple matches ...
          BindingValueKey bindingKey = bindingKeys.get(0);
          bindingKeysList.add(bindingKey);
          currentType = bindingKey.getNextType();
        }
        else {
          if (BindingReflectionUtils.isNSKeyValueCoding(currentType) || "java.lang.Object".equals(currentType.getFullyQualifiedName())) {
            _nsKVC = true;
            if (BindingReflectionUtils.isNSCollection(currentType)) {
              _nsCollection = true;
              _ambiguous = true;
              invalidKeyNum = keyNum;
              currentType = null;
            }
            else {
              _ambiguous = true;
              invalidKeyNum = keyNum;
              currentType = null;
            }
          }
          else {
            _valid = false;
            invalidKeyNum = keyNum;
            currentType = null;
          }
        }
      }

      // Build the part of the keypath that is valid and the key that is invalid for error reporting ...
      if (invalidKeyNum != -1) {
        StringBuffer validKeyPathBuffer = new StringBuffer();
        if (invalidKeyNum > 0) {
          for (int keyNum = 0; keyNum < invalidKeyNum; keyNum++) {
            validKeyPathBuffer.append(_bindingKeyNames[keyNum]);
            validKeyPathBuffer.append(".");
          }
          validKeyPathBuffer.setLength(validKeyPathBuffer.length() - 1);
        }
        _validKeyPath = validKeyPathBuffer.toString();
        _invalidKey = _bindingKeyNames[invalidKeyNum];
      }

      _bindingKeys = bindingKeysList.toArray(new BindingValueKey[bindingKeysList.size()]);

      if ("true".equalsIgnoreCase(keyPath) || "false".equalsIgnoreCase(keyPath) || "yes".equalsIgnoreCase(keyPath) || "no".equalsIgnoreCase(keyPath)) {
        _ambiguous = false;
        _valid = true;
        _nsKVC = false;
        _nsCollection = false;
      }
      // ... I have no idea why this was here.  I wish I had commented it originally
      //if (!_valid) {
      //_valid = _bindingKeyNames.length == 1;
      //}
    }
  }

  public String getOriginalKeyPath() {
    return _originalKeyPath;
  }

  public String getOperator() {
    return _operator;
  }

  public boolean isNSKeyValueCoding() {
    return _nsKVC;
  }

  public boolean isNSCollection() {
    return _nsCollection;
  }

  public String getValidKeyPath() {
    return _validKeyPath;
  }

  public String getInvalidKey() {
    return _invalidKey;
  }

  public String getHelperFunction() {
    return _helperFunction;
  }

  public boolean isAmbiguous() {
    return _ambiguous;
  }

  public boolean isValid() {
    return _valid;
  }

  public String getLastBindingKeyName() {
    String lastBindingKeyName;
    if (_bindingKeyNames != null && _bindingKeyNames.length > 0) {
      lastBindingKeyName = _bindingKeyNames[_bindingKeyNames.length - 1];
    }
    else {
      lastBindingKeyName = null;
    }
    return lastBindingKeyName;
  }

  public String getNextToLastBindingKeyName() {
    String nextToLastBindingKeyName;
    if (_bindingKeyNames.length > 1) {
      nextToLastBindingKeyName = _bindingKeyNames[_bindingKeyNames.length - 2];
    }
    else {
      nextToLastBindingKeyName = null;
    }
    return nextToLastBindingKeyName;
  }

  public BindingValueKey getLastBindingKey() {
    BindingValueKey lastBindingKey;
    if (_bindingKeys != null && _bindingKeys.length > 0) {
      lastBindingKey = _bindingKeys[_bindingKeys.length - 1];
    }
    else {
      lastBindingKey = null;
    }
    return lastBindingKey;
  }

  public BindingValueKey getNextToLastBindingKey() {
    BindingValueKey nextToLastBindingKey;
    if (_bindingKeys.length > 1) {
      nextToLastBindingKey = _bindingKeys[_bindingKeys.length - 2];
    }
    else {
      nextToLastBindingKey = null;
    }
    return nextToLastBindingKey;
  }

  public IType getLastType() throws JavaModelException {
    BindingValueKey lastBindingKey = getLastBindingKey();
    IType lastType;
    if (lastBindingKey != null) {
      lastType = lastBindingKey.getNextType();
    }
    else {
      lastType = _contextType;
    }
    return lastType;
  }

  public List<BindingValueKey> getPartialMatchesForLastBindingKey() throws JavaModelException {
    List<BindingValueKey> bindingKeysList = null;
    if (_helperFunction != null) {
      // if there's a helper function, we can't do completion
    }
    else if (_operator != null) {
      bindingKeysList = BindingReflectionUtils.getBindingKeys(_javaProject, getLastType(), "@" + _operator, false, BindingReflectionUtils.ACCESSORS_ONLY, _cache);
    }
    else if (_bindingKeys == null) {
      bindingKeysList = BindingReflectionUtils.getBindingKeys(_javaProject, getLastType(), _originalKeyPath, false, BindingReflectionUtils.ACCESSORS_ONLY, _cache);
    }
    else {
      String partialBindingKeyName;
      BindingValueKey lastBindingKey;
      if (_bindingKeyNames.length == _bindingKeys.length) {
        partialBindingKeyName = getLastBindingKeyName();
        lastBindingKey = getNextToLastBindingKey();
      }
      else {
        partialBindingKeyName = getLastBindingKeyName();
        lastBindingKey = getLastBindingKey();
      }

      IType lastType;
      if (lastBindingKey == null) {
        lastType = _contextType;
      }
      else {
        lastType = lastBindingKey.getNextType();
      }

      if (lastType != null) {
        // Jump forward to the last '.' and look for valid "get" method
        // completion proposals based on the partial token
        bindingKeysList = BindingReflectionUtils.getBindingKeys(_javaProject, lastType, partialBindingKeyName, false, BindingReflectionUtils.ACCESSORS_ONLY, _cache);
      }
    }
    return bindingKeysList;
  }

  public String[] getRelatedToFileNames() {
    Set<String> relatedToFileNamesSet = new HashSet<String>();
    relatedToFileNamesSet.add(_contextType.getResource().getName());
    for (int i = 0; i < _bindingKeys.length; i++) {
      IResource resource = _bindingKeys[i].getDeclaringType().getResource();
      if (resource != null) {
        relatedToFileNamesSet.add(resource.getName());
      }
    }
    // System.out.println("BindingValueKeyPath.getRelatedToFileNames: " +
    // relatedToFileNamesSet);
    String[] relatedToFileNames = relatedToFileNamesSet.toArray(new String[relatedToFileNamesSet.size()]);
    return relatedToFileNames;
  }

  public int getLength() {
    return _bindingKeyNames == null ? 1 : _bindingKeyNames.length;
  }
}
