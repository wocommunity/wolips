package org.objectstyle.wolips.wodclipse.core.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.util.WodReflectionUtils;

public class BindingValueKeyPath {
  private IJavaProject _javaProject;

  private IType _contextType;

  private String[] _bindingKeyNames;

  private BindingValueKey[] _bindingKeys;

  private String _validKeyPath;

  private String _invalidKey;

  private boolean _valid;

  private boolean _nsKVC;

  private boolean _ambiguous;

  private String _helperFunction;

  private WodParserCache _cache;

  public BindingValueKeyPath(String keyPath, IType contextType, IJavaProject javaProject, WodParserCache cache) throws JavaModelException {
    _cache = cache;
    _javaProject = javaProject;
    _contextType = contextType;
    _valid = true;

    boolean isKeyPath = true;
    // short-circuit for booleans
    if ("true".equalsIgnoreCase(keyPath) || "false".equalsIgnoreCase(keyPath) || "yes".equalsIgnoreCase(keyPath) || "no".equalsIgnoreCase(keyPath)) {
      isKeyPath = false;
    }
    else if (keyPath.length() > 0) {
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
      String[] bindingKeyNames;
      int pipeIndex = keyPath.indexOf('|');
      if (pipeIndex == -1) {
        bindingKeyNames = keyPath.split("\\.");
      }
      else {
        bindingKeyNames = keyPath.substring(0, pipeIndex).split("\\.");
        _helperFunction = keyPath.substring(pipeIndex + 1);
      }

      // Split tosses empty tokens, so we check to see if we're on the last
      // "." and fake an empty token in the list
      if (keyPath.length() > 0 && keyPath.charAt(keyPath.length() - 1) == '.') {
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
        if (_bindingKeyNames[keyNum].startsWith("@") || _bindingKeyNames[keyNum].startsWith("|")) {
          _ambiguous = true;
          invalidKeyNum = keyNum;
          currentType = null;
        }
        else {
          List bindingKeys = WodReflectionUtils.getBindingKeys(_javaProject, currentType, _bindingKeyNames[keyNum], true, WodReflectionUtils.ACCESSORS_OR_VOID, cache);
          if (!bindingKeys.isEmpty()) {
            // NTS: Deal with multiple matches ...
            BindingValueKey bindingKey = (BindingValueKey) bindingKeys.get(0);
            bindingKeysList.add(bindingKey);
            currentType = bindingKey.getNextType();
          }
          else {
            if (WodReflectionUtils.isNSKeyValueCoding(currentType)) {
              _nsKVC = true;
              _ambiguous = true;
            }
            else {
              _valid = false;
            }
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

      // ... I have no idea why this was here.  I wish I had commented it originally
      //if (!_valid) {
      //_valid = _bindingKeyNames.length == 1;
      //}
    }
  }

  public boolean isNSKeyValueCoding() {
    return _nsKVC;
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
    if (_bindingKeyNames.length > 0) {
      lastBindingKeyName = _bindingKeyNames[_bindingKeyNames.length - 1];
    }
    else {
      lastBindingKeyName = null;
    }
    return lastBindingKeyName;
  }

  public BindingValueKey getLastBindingKey() {
    BindingValueKey bindingKey;
    if (_bindingKeys.length > 0) {
      bindingKey = _bindingKeys[_bindingKeys.length - 1];
    }
    else {
      bindingKey = null;
    }
    return bindingKey;
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
    List<BindingValueKey> bindingKeysList;
    IType lastType = getLastType();
    if (lastType != null) {
      // Jump forward to the last '.' and look for valid "get" method
      // completion
      // proposals based on the partial token
      String bindingKeyName = getLastBindingKeyName();
      bindingKeysList = WodReflectionUtils.getBindingKeys(_javaProject, lastType, bindingKeyName, false, WodReflectionUtils.ACCESSORS_ONLY, _cache);
    }
    else {
      bindingKeysList = null;
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
    return _bindingKeyNames.length;
  }
}
