/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowlegement: "This product includes software developed by the ObjectStyle
 * Group (http://objectstyle.org/)." Alternately, this acknowlegement may
 * appear in the software itself, if and wherever such third-party
 * acknowlegements normally appear. 4. The names "ObjectStyle Group" and
 * "Cayenne" must not be used to endorse or promote products derived from this
 * software without prior written permission. For written permission, please
 * contact andrus@objectstyle.org. 5. Products derived from this software may
 * not be called "ObjectStyle" nor may "ObjectStyle" appear in their names
 * without prior written permission of the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/> .
 *  
 */
package org.objectstyle.wolips.bindings.wod;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.objectstyle.wolips.bindings.Activator;
import org.objectstyle.wolips.bindings.api.ApiCache;
import org.objectstyle.wolips.bindings.api.ApiModelException;
import org.objectstyle.wolips.bindings.api.ApiUtils;
import org.objectstyle.wolips.bindings.api.IApiBinding;
import org.objectstyle.wolips.bindings.api.Wo;
import org.objectstyle.wolips.bindings.preferences.PreferenceConstants;
import org.objectstyle.wolips.bindings.utils.BindingReflectionUtils;

/**
 * @author mschrag
 */
public abstract class AbstractWodBinding implements IWodBinding {
  private static Set<String> VALID_OGNL_VALUES = new HashSet<String>();
  static {
    AbstractWodBinding.VALID_OGNL_VALUES.add("and");
    AbstractWodBinding.VALID_OGNL_VALUES.add("band");
    AbstractWodBinding.VALID_OGNL_VALUES.add("bor");
    AbstractWodBinding.VALID_OGNL_VALUES.add("eq");
    AbstractWodBinding.VALID_OGNL_VALUES.add("gt");
    AbstractWodBinding.VALID_OGNL_VALUES.add("gte");
    AbstractWodBinding.VALID_OGNL_VALUES.add("in");
    AbstractWodBinding.VALID_OGNL_VALUES.add("instanceof");
    AbstractWodBinding.VALID_OGNL_VALUES.add("lt");
    AbstractWodBinding.VALID_OGNL_VALUES.add("lte");
    AbstractWodBinding.VALID_OGNL_VALUES.add("neq");
    AbstractWodBinding.VALID_OGNL_VALUES.add("not");
    AbstractWodBinding.VALID_OGNL_VALUES.add("null");
    AbstractWodBinding.VALID_OGNL_VALUES.add("neq");
    AbstractWodBinding.VALID_OGNL_VALUES.add("new");
    AbstractWodBinding.VALID_OGNL_VALUES.add("or");
    AbstractWodBinding.VALID_OGNL_VALUES.add("shl");
    AbstractWodBinding.VALID_OGNL_VALUES.add("shr");
    AbstractWodBinding.VALID_OGNL_VALUES.add("this");
    AbstractWodBinding.VALID_OGNL_VALUES.add("ushr");
    AbstractWodBinding.VALID_OGNL_VALUES.add("xor");
  }
  private boolean _validate;

  public AbstractWodBinding() {
    _validate = true;
  }

  public void setValidate(boolean validate) {
    _validate = validate;
  }

  public boolean shouldValidate() {
    return _validate;
  }

  public void writeWodFormat(Writer writer) throws IOException {
    writer.write("  ");
    writer.write(getName());
    writer.write(" = ");
    writer.write(getValue());
    writer.write(";");
  }

  public void writeInlineFormat(Writer writer, String prefix, String suffix) throws IOException {
    writer.write(" ");
    writer.write(getName());
    writer.write(" = ");
    if (isLiteral()) {
      writer.write(getValue());
    }
    else if (isOGNL()) {
      writer.write("\"");
      writer.write(getValue());
      writer.write("\"");
    }
    else {
      writer.write("\"");
      writer.write(prefix);
      writer.write(getValue());
      writer.write(suffix);
      writer.write("\"");
    }
  }

  public boolean isTrueValue() {
    String bindingValue = getValue();
    return "true".equalsIgnoreCase(bindingValue) || "yes".equalsIgnoreCase(bindingValue);
  }

  public boolean isCaret() {
    String bindingValue = getValue();
    return bindingValue != null && bindingValue.startsWith("^");   
  }
  
  public boolean isKeyPath() {
    String bindingValue = getValue();
    boolean isBindingValueKeyPath;
    if (bindingValue != null && bindingValue.length() > 0) {
      char ch = bindingValue.charAt(0);
      isBindingValueKeyPath = Character.isJavaIdentifierStart(ch);
    }
    else {
      isBindingValueKeyPath = false;
    }
    return isBindingValueKeyPath;
  }

  public boolean isLiteral() {
    String bindingValue = getValue();
    return bindingValue != null && bindingValue.startsWith("\"");
  }

  public boolean isOGNL() {
    String bindingValue = getValue();
    return bindingValue != null && (bindingValue.startsWith("~") || bindingValue.startsWith("\"~"));
  }

  public abstract int getLineNumber();

  public static List<WodProblem> getBindingProblems(String elementType, String keypath, IType javaFileType, TypeCache typeCache) throws JavaModelException, ApiModelException {
    SimpleWodBinding binding = new SimpleWodBinding("_temp", keypath);
    return binding.getBindingProblems(elementType, javaFileType, typeCache);
  }

  public void fillInBindingProblems(IApiBinding apiBinding, IJavaProject javaProject, IType javaFileType, List<WodProblem> problems, TypeCache cache) throws JavaModelException {
    boolean warnOnMissingCollectionKey = Activator.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.WARN_ON_MISSING_COLLECTION_KEY);
    boolean errorOnMissingComponentKey = Activator.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.ERROR_ON_MISSING_COMPONENT_KEY);
    boolean warnOnMissingComponentKey = Activator.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.WARN_ON_MISSING_COMPONENT_KEY);
    boolean errorOnMissingNSKVCKey = Activator.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.ERROR_ON_MISSING_NSKVC_KEY);
    boolean warnOnMissingNSKVCKey = Activator.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.WARN_ON_MISSING_NSKVC_KEY);
    boolean warnOnAmbiguousKey = Activator.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.WARN_ON_AMBIGUOUS_KEY);
    boolean warnOnOperator = Activator.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.WARN_ON_OPERATOR_KEY);
    boolean warnOnHelperFunction = Activator.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.WARN_ON_HELPER_FUNCTION_KEY);
    if (shouldValidate()) {
      String bindingName = getName();
      String bindingValue = getValue();

      boolean explicitlyValid = false;
      String javaFileTypeName = javaFileType.getElementName();
      if (javaFileTypeName != null) {
        List<BindingValidationRule> bindingValidationRules = ApiCache.getBindingValidationRules();
        for (int i = 0; !explicitlyValid && i < bindingValidationRules.size(); i++) {
          BindingValidationRule bindingValidationRule = bindingValidationRules.get(i);
          if (javaFileTypeName.matches(bindingValidationRule.getTypeRegex())) {
            explicitlyValid = bindingValue != null && bindingValue.matches(bindingValidationRule.getValidBindingRegex());
          }
        }
      }

      if (!explicitlyValid) {
        int lineNumber = getLineNumber();
        if (isKeyPath()) {
          BindingValueKeyPath bindingValueKeyPath = new BindingValueKeyPath(bindingValue, javaFileType, javaProject, cache);
          // NTS: Technically these need to be related to
          // every java file name in the key path
          if (!bindingValueKeyPath.isValid() || (bindingValueKeyPath.isWOComponent() && errorOnMissingComponentKey) || (bindingValueKeyPath.isNSKeyValueCoding() && errorOnMissingNSKVCKey && !bindingValueKeyPath.isNSCollection())) {
            String validKeyPath = bindingValueKeyPath.getValidKeyPath();
            if (validKeyPath != null) {
              if (validKeyPath.length() == 0) {
                problems.add(new WodBindingValueProblem(bindingName, "There is no key '" + bindingValueKeyPath.getInvalidKey() + "' in " + javaFileType.getElementName(), getValuePosition(), lineNumber, false));
              }
              else {
                problems.add(new WodBindingValueProblem(bindingName, "There is no key '" + bindingValueKeyPath.getInvalidKey() + "' for the keypath '" + validKeyPath + "' in " + javaFileType.getElementName(), getValuePosition(), lineNumber, false));
              }
            }
          }
          else if (bindingValueKeyPath.isNSCollection()) {
            if (warnOnMissingCollectionKey) {
              String validKeyPath = bindingValueKeyPath.getValidKeyPath();
              if (validKeyPath != null) {
                if (validKeyPath.length() == 0) {
                  problems.add(new WodBindingValueProblem(bindingName, "Unable to verify key '" + bindingValueKeyPath.getInvalidKey() + "' because " + javaFileType.getElementName() + " is a collection", getValuePosition(), lineNumber, true));
                }
                else {
                  problems.add(new WodBindingValueProblem(bindingName, "Unable to verify key '" + bindingValueKeyPath.getInvalidKey() + "' because the keypath '" + validKeyPath + "' in " + javaFileType.getElementName() + " is a collection", getValuePosition(), lineNumber, true));
                }
              }
            }
          }
          else if (bindingValueKeyPath.isWOComponent()) {
            if (warnOnMissingComponentKey) {
              String validKeyPath = bindingValueKeyPath.getValidKeyPath();
              if (validKeyPath != null) {
                if (validKeyPath.length() == 0) {
                  problems.add(new WodBindingValueProblem(bindingName, "Unable to verify the key '" + bindingValueKeyPath.getInvalidKey() + "' because " + javaFileType.getElementName() + " is a component.", getValuePosition(), lineNumber, true));
                }
                else {
                  problems.add(new WodBindingValueProblem(bindingName, "Unable to verify the key '" + bindingValueKeyPath.getInvalidKey() + "' because the keypath '" + validKeyPath + "' in " + javaFileType.getElementName() + " is a component.", getValuePosition(), lineNumber, true));
                }
              }
            }
          }
          else if (bindingValueKeyPath.isNSKeyValueCoding()) {
            if (warnOnMissingNSKVCKey) {
              String validKeyPath = bindingValueKeyPath.getValidKeyPath();
              if (validKeyPath != null) {
                if (validKeyPath.length() == 0) {
                  problems.add(new WodBindingValueProblem(bindingName, "Unable to verify the key '" + bindingValueKeyPath.getInvalidKey() + "' because " + javaFileType.getElementName() + " implements NSKeyValueCoding", getValuePosition(), lineNumber, true));
                }
                else {
                  problems.add(new WodBindingValueProblem(bindingName, "Unable to verify the key '" + bindingValueKeyPath.getInvalidKey() + "' because the keypath '" + validKeyPath + "' in " + javaFileType.getElementName() + " implements NSKeyValueCoding", getValuePosition(), lineNumber, true));
                }
              }
            }
          }
          else if (warnOnAmbiguousKey && bindingValueKeyPath.isAmbiguous()) {
            String validKeyPath = bindingValueKeyPath.getValidKeyPath();
            if (validKeyPath != null) {
              if (validKeyPath.length() == 0) {
                problems.add(new WodBindingValueProblem(bindingName, "Unable to verify the key '" + bindingValueKeyPath.getInvalidKey() + "' in " + javaFileType.getElementName(), getValuePosition(), lineNumber, true));
              }
              else {
                problems.add(new WodBindingValueProblem(bindingName, "Unable to verify the key '" + bindingValueKeyPath.getInvalidKey() + "' for the path '" + validKeyPath + "' in " + javaFileType.getElementName(), getValuePosition(), lineNumber, true));
              }
            }
          }

          String operator = bindingValueKeyPath.getOperator();
          if (warnOnOperator && operator != null && !BindingReflectionUtils.getArrayOperators().contains(operator)) {
            problems.add(new WodBindingValueProblem(bindingName, "Unable to verify operator '" + operator + "'", getValuePosition(), lineNumber, true));
          }

          String helperFunction = bindingValueKeyPath.getHelperFunction();
          if (warnOnHelperFunction && helperFunction != null) {
            problems.add(new WodBindingValueProblem(bindingName, "Unable to verify helper function '" + helperFunction + "'", getValuePosition(), lineNumber, true));
          }
          // else {
          // String[] validApiValues = WodBindingUtils.getValidValues(elementType, getName(), typeToApiModelWoCache);
          // if (validApiValues != null &&
          // !Arrays.asList(validApiValues).contains(bindingValue))
          // {
          // problems.add(new WodProblem(wodModel, "The .api file for " + wodJavaType.getElementName() + " declares '" + bindingValue + "' to be an invalid value.", getValuePosition(), false));
          // }
          // }

          if (apiBinding != null) {
            if (apiBinding.isWillSet()) {
              if (!bindingValueKeyPath.isSettable()) {
                problems.add(new WodBindingValueProblem(bindingName, "The key '" + getName() + "' must have a 'set' method.", getValuePosition(), lineNumber, false));
              }
            }
          }
        }
        else if (apiBinding != null) {
          if (apiBinding.isWillSet() && !isCaret()) {
            problems.add(new WodBindingValueProblem(bindingName, "The key '" + getName() + "' cannot be a constant value.", getValuePosition(), lineNumber, false));
          }
        }

        boolean validateOGNL = Activator.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.VALIDATE_OGNL_KEY);
        if (validateOGNL && isOGNL()) {
          boolean inQuotes = bindingValue.startsWith("\"");
          if (inQuotes) {
            bindingValue = bindingValue.substring(1, bindingValue.length() - 1);
          }
          String ognl = bindingValue.substring(1);
          ognl = ognl.replaceAll("\\\\'", " ");
          ognl = ognl.replaceAll("'[^']*'", "''");
          if (inQuotes) {
            ognl = ognl.replaceAll("\\\\\"[^\"]*\\\\\"", "\\\"\\\"");
          }
          else {
            ognl = ognl.replaceAll("\\\\\"", " ");
            ognl = ognl.replaceAll("\"[^\"]*\"", "\"\"");
          }
          ognl = ognl + " ";
          int identifierStartChar = -1;
          for (int i = 0; i < ognl.length(); i++) {
            char ch = ognl.charAt(i);
            if (identifierStartChar == -1) {
              if (Character.isJavaIdentifierStart(ch) || ch == '@' || ch == '#') {
                identifierStartChar = i;
              }
            }
            else if (!Character.isJavaIdentifierPart(ch) && ch != '.' && ch != '@' && ch != '#') {
              String ognlBindingValue = ognl.substring(identifierStartChar, i);
              // null
              if (!ognlBindingValue.startsWith("@") && !ognlBindingValue.startsWith("#") && !AbstractWodBinding.VALID_OGNL_VALUES.contains(ognlBindingValue.toLowerCase())) {
                // function call
                String nextStr = ognl.substring(i).trim();
                if (!nextStr.startsWith("(")) {
                  SimpleWodBinding ognlBinding = new SimpleWodBinding(bindingName, ognlBindingValue, getNamePosition(), getValuePosition(), getLineNumber());
                  try {
                    List<WodProblem> ognlProblems = new LinkedList<WodProblem>();
                    ognlBinding.fillInBindingProblems(apiBinding, javaProject, javaFileType, ognlProblems, cache);
                    for (WodProblem ognlProblem : ognlProblems) {
                      problems.add(new WodBindingValueProblem(bindingName, ognlProblem.getMessage(), getValuePosition(), lineNumber, ognlProblem.isWarning()));
                    }
                  }
                  catch (Exception e) {
                    Activator.getDefault().log(e);
                  }
                }
              }
              identifierStartChar = -1;
            }
          }
        }
      }
    }
  }

  // IApiBinding stub impl
  public String getDefaults() {
    return null;
  }

  public int getSelectedDefaults() {
    return ApiUtils.getSelectedDefaults(this);
  }

  public String[] getValidValues(String partialValue, IJavaProject javaProject, IType componentType, TypeCache typeCache) {
    return new String[0];
  }

  public boolean isRequired() {
    return false;
  }

  public boolean isWillSet() {
    return false;
  }

  public boolean isValueWithin(IRegion region) {
    Position valuePosition = getValuePosition();
    return valuePosition != null && valuePosition.getOffset() <= region.getOffset() && valuePosition.getOffset() + valuePosition.getLength() > region.getOffset();
  }

  public boolean isNameWithin(IRegion region) {
    Position namePosition = getNamePosition();
    return namePosition != null && namePosition.getOffset() <= region.getOffset() && namePosition.getOffset() + namePosition.getLength() > region.getOffset();
  }

  @Override
  public String toString() {
    return "[" + getClass().getName() + ": name = " + getName() + "; value = " + getValue() + "]";
  }

  public List<WodProblem> getBindingProblems(String elementType, IType javaFileType, TypeCache typeCache) throws JavaModelException, ApiModelException {
    List<WodProblem> problems = new LinkedList<WodProblem>();
    IApiBinding apiBinding = null;
    if (elementType != null) {
      SimpleWodElement element = new SimpleWodElement("_temp", elementType);
      Wo wo = element.getApi(javaFileType.getJavaProject(), typeCache);
      if (wo != null) {
        apiBinding = wo.getBinding(getName());
      }
    }
    fillInBindingProblems(apiBinding, javaFileType.getJavaProject(), javaFileType, problems, new TypeCache());
    return problems;
  }
}
