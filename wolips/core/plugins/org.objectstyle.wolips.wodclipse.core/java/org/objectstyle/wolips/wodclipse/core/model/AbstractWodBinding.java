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
package org.objectstyle.wolips.wodclipse.core.model;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.preferences.PreferenceConstants;

/**
 * @author mschrag
 */
public abstract class AbstractWodBinding implements IWodBinding {
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

  public boolean isKeyPath() {
    String bindingValue = getValue();
    boolean isBindingValueKeyPath;
    if (bindingValue.length() > 0) {
      char ch = bindingValue.charAt(0);
      isBindingValueKeyPath = Character.isJavaIdentifierStart(ch);
    }
    else {
      isBindingValueKeyPath = false;
    }
    return isBindingValueKeyPath;
  }

  public boolean isOGNL() {
    String bindingValue = getValue();
    return bindingValue != null && (bindingValue.startsWith("~") || bindingValue.startsWith("\"~"));
  }

  public abstract int getLineNumber();

  public void fillInBindingProblems(IJavaProject javaProject, IType javaFileType, List<WodProblem> problems, WodParserCache cache) throws JavaModelException {
    boolean warnOnMissingCollectionKey = Activator.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.WARN_ON_MISSING_COLLECTION_KEY);
    boolean errorOnMissingNSKVCKey = Activator.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.ERROR_ON_MISSING_NSKVC_KEY);
    boolean warnOnMissingNSKVCKey = Activator.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.WARN_ON_MISSING_NSKVC_KEY);
    boolean warnOnAmbiguousKey = Activator.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.WARN_ON_AMBIGUOUS_KEY);
    boolean warnOnHelperFunction = Activator.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.WARN_ON_HELPER_FUNCTION_KEY);
    if (shouldValidate()) {
      String bindingName = getName();
      String bindingValue = getValue();
      if (isKeyPath()) {
        int lineNumber = getLineNumber();
        BindingValueKeyPath bindingValueKeyPath = new BindingValueKeyPath(bindingValue, javaFileType, javaProject, cache);
        // NTS: Technically these need to be related to
        // every java file name in the key path
        if (!bindingValueKeyPath.isValid() || (bindingValueKeyPath.isNSKeyValueCoding() && errorOnMissingNSKVCKey && !bindingValueKeyPath.isNSCollection())) {
          String validKeyPath = bindingValueKeyPath.getValidKeyPath();
          if (validKeyPath.length() == 0) {
            problems.add(new WodBindingValueProblem(bindingName, "There is no key '" + bindingValueKeyPath.getInvalidKey() + "' in " + javaFileType.getElementName(), getValuePosition(), lineNumber, false, bindingValueKeyPath.getRelatedToFileNames()));
          }
          else {
            problems.add(new WodBindingValueProblem(bindingName, "There is no key '" + bindingValueKeyPath.getInvalidKey() + "' for the keypath '" + validKeyPath + "' in " + javaFileType.getElementName(), getValuePosition(), lineNumber, false, bindingValueKeyPath.getRelatedToFileNames()));
          }
        }
        else if (bindingValueKeyPath.isNSCollection()) {
          if (warnOnMissingCollectionKey) {
            String validKeyPath = bindingValueKeyPath.getValidKeyPath();
            if (validKeyPath.length() == 0) {
              problems.add(new WodBindingValueProblem(bindingName, "Unable to verify key '" + bindingValueKeyPath.getInvalidKey() + "' because " + javaFileType.getElementName() + " is a collection", getValuePosition(), lineNumber, true, bindingValueKeyPath.getRelatedToFileNames()));
            }
            else {
              problems.add(new WodBindingValueProblem(bindingName, "Unable to verify key '" + bindingValueKeyPath.getInvalidKey() + "' because the keypath '" + validKeyPath + "' in " + javaFileType.getElementName() + " is a collection", getValuePosition(), lineNumber, true, bindingValueKeyPath.getRelatedToFileNames()));
            }
          }
        }
        else if (bindingValueKeyPath.isNSKeyValueCoding()) {
          if (warnOnMissingNSKVCKey) {
            String validKeyPath = bindingValueKeyPath.getValidKeyPath();
            if (validKeyPath.length() == 0) {
              problems.add(new WodBindingValueProblem(bindingName, "Unable to verify the key '" + bindingValueKeyPath.getInvalidKey() + "' because " + javaFileType.getElementName() + " implements NSKeyValueCoding", getValuePosition(), lineNumber, true, bindingValueKeyPath.getRelatedToFileNames()));
            }
            else {
              problems.add(new WodBindingValueProblem(bindingName, "Unable to verify the key '" + bindingValueKeyPath.getInvalidKey() + "' because the keypath '" + validKeyPath + "' in " + javaFileType.getElementName() + " implements NSKeyValueCoding", getValuePosition(), lineNumber, true, bindingValueKeyPath.getRelatedToFileNames()));
            }
          }
        }
        else if (warnOnAmbiguousKey && bindingValueKeyPath.isAmbiguous()) {
          String validKeyPath = bindingValueKeyPath.getValidKeyPath();
          if (validKeyPath.length() == 0) {
            problems.add(new WodBindingValueProblem(bindingName, "Unable to verify the key '" + bindingValueKeyPath.getInvalidKey() + "' in " + javaFileType.getElementName(), getValuePosition(), lineNumber, true, bindingValueKeyPath.getRelatedToFileNames()));
          }
          else {
            problems.add(new WodBindingValueProblem(bindingName, "Unable to verify the key '" + bindingValueKeyPath.getInvalidKey() + "' for the path '" + validKeyPath + "' in " + javaFileType.getElementName(), getValuePosition(), lineNumber, true, bindingValueKeyPath.getRelatedToFileNames()));
          }
        }
        String helperFunction = bindingValueKeyPath.getHelperFunction();
        if (warnOnHelperFunction && helperFunction != null) {
          problems.add(new WodBindingValueProblem(bindingName, "Unable to verify helper function '" + helperFunction + "'", getValuePosition(), lineNumber, true, bindingValueKeyPath.getRelatedToFileNames()));
        }
        // else {
        // String[] validApiValues = WodBindingUtils.getValidValues(elementType, getName(), typeToApiModelWoCache);
        // if (validApiValues != null &&
        // !Arrays.asList(validApiValues).contains(bindingValue))
        // {
        // problems.add(new WodProblem(wodModel, "The .api file for " + wodJavaType.getElementName() + " declares '" + bindingValue + "' to be an invalid value.", getValuePosition(), false));
        // }
        // }
      }

      boolean validateOGNL = Activator.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.VALIDATE_OGNL_KEY);
      if (validateOGNL && isOGNL()) {
        int lineNumber = getLineNumber();
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
            if (!ognlBindingValue.startsWith("@") && !ognlBindingValue.startsWith("#") && !ognlBindingValue.equalsIgnoreCase("null") && !ognlBindingValue.equalsIgnoreCase("this") && !ognlBindingValue.equalsIgnoreCase("new")) {
              // function call
              String nextStr = ognl.substring(i).trim();
              if (!nextStr.startsWith("()")) {
                SimpleWodBinding ognlBinding = new SimpleWodBinding(bindingName, ognlBindingValue, getNamePosition(), getValuePosition(), getLineNumber());
                try {
                  List<WodProblem> ognlProblems = new LinkedList<WodProblem>();
                  ognlBinding.fillInBindingProblems(javaProject, javaFileType, ognlProblems, cache);
                  for (WodProblem ognlProblem : ognlProblems) {
                    problems.add(new WodBindingValueProblem(bindingName, ognlProblem.getMessage(), getValuePosition(), lineNumber, ognlProblem.isWarning(), ognlProblem.getRelatedToFileNames()));
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

  @Override
  public String toString() {
    return "[" + getClass().getName() + ": name = " + getName() + "; value = " + getValue() + "]";
  }
}
