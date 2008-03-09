/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2005 The ObjectStyle Group,
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne"
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */
package org.objectstyle.wolips.bindings.api;

import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.bindings.wod.TypeCache;
import org.w3c.dom.Element;

public class Binding extends AbstractApiModelElement implements IApiBinding {

  private BindingNameChangedListener bindingNameChangedListener;

  public final static String BINDING = "binding";

  public final static String NAME = "name";

  private final static String DEFAULTS = "defaults";

  private Wo parent;

  protected Binding(Element element, ApiModel apiModel, Wo parent) {
    super(element, apiModel);
    this.parent = parent;
  }
  
  public boolean isAction() {
    return ApiUtils.isActionBinding(this);
  }
  
  public int compareTo(IApiBinding o) {
    return (o == null) ? -1 : getName() == null ? -1 : getName().compareTo(o.getName());
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof Binding && ComparisonUtils.equals(((Binding) o).getName(), getName());
  }

  @Override
  public int hashCode() {
    String name = getName();
    return name == null ? 0 : name.hashCode();
  }

  public Wo getElement() {
    return parent;
  }

  public String getName() {
    synchronized (this.apiModel) {
      return element.getAttribute(NAME);
    }
  }

  public void setName(String className) {
    synchronized (this.apiModel) {
      element.setAttribute(NAME, className);
      apiModel.markAsDirty();
      if (bindingNameChangedListener != null) {
        bindingNameChangedListener.namedChanged(this);
      }
    }
  }

  public String getDefaults() {
    synchronized (this.apiModel) {
      return element.getAttribute(DEFAULTS);
    }
  }

  public int getSelectedDefaults() {
    return ApiUtils.getSelectedDefaults(this);
  }

  public void setDefaults(String defaults) {
    synchronized (this.apiModel) {
      element.setAttribute(DEFAULTS, defaults);
    }
  }

  public void setDefaults(int defaults) {
    synchronized (this.apiModel) {
      if (defaults == 0) {
        if (getDefaults() == null) {
          return;
        }
        element.removeAttribute(DEFAULTS);
      }
      else {
        if (getDefaults() != null && getDefaults().equals(ALL_DEFAULTS[defaults])) {
          return;
        }
        this.setDefaults(ALL_DEFAULTS[defaults]);
      }
      apiModel.markAsDirty();
    }
  }

  public boolean isExplicitlyRequired() {
    synchronized (this.apiModel) {
      return "YES".equalsIgnoreCase(element.getAttribute("required"));
    }
  }

  public boolean isRequired() {
    boolean required = isExplicitlyRequired();
    if (!required) {
      List<Validation> validations = parent.getValidations();
      for (int i = 0; !required && i < validations.size(); i++) {
        Validation validation = validations.get(i);
        List<Unbound> unbounds = validation.getUnbounds();
        if (unbounds.size() == 1 && unbounds.get(0).isAffectedByBindingNamed(this.getName())) {
          required = true;
        }
      }
    }
    return required;
  }

  public void setIsRequired(boolean isRequired) {
    synchronized (this.apiModel) {
      if (this.isRequired() == isRequired) {
        return;
      }
      if (element.hasAttribute("required")) {
        if (isRequired) {
          element.setAttribute("required", "YES");
        }
        else {
          element.setAttribute("required", "NO");
        }
      }
      else if (isRequired) {
        Unbound.addToWoWithBinding(parent, this);
      }
      else {
        Unbound.removeFromWoWithBinding(parent, this);
      }
      apiModel.markAsDirty();
    }
  }

  public boolean isExplicitlySettable() {
    synchronized (this.apiModel) {
      return "YES".equalsIgnoreCase(element.getAttribute("settable"));
    }
  }

  public boolean isWillSet() {
    boolean settable = isExplicitlySettable();
    if (!settable) {
      List<Validation> validations = parent.getValidations();
      for (Validation validation : validations) {
        List<Unsettable> unsettables = validation.getUnsettables();
        if (unsettables.size() == 1 && unsettables.get(0).isAffectedByBindingNamed(this.getName())) {
          settable = true;
          break;
        }
      }
    }
    return settable;
  }

  public void setIsWillSet(boolean isWillSet) {
    synchronized (this.apiModel) {
      if (this.isWillSet() == isWillSet) {
        return;
      }
      if (element.hasAttribute("settable")) {
        if (isWillSet) {
          element.setAttribute("settable", "YES");
        }
        else {
          element.setAttribute("settable", "NO");
        }
      }
      else if (isWillSet) {
        Unsettable.addToWoWithBinding(parent, this);
      }
      else {
        Unsettable.removeFromWoWithBinding(parent, this);
      }
      apiModel.markAsDirty();
    }
  }

  public String[] getValidValues(String partialValue, IJavaProject javaProject, IType componentType, TypeCache typeCache) throws JavaModelException {
    return ApiUtils.getValidValues(this, partialValue, javaProject, componentType, typeCache);
  }

  public interface BindingNameChangedListener {
    public abstract void namedChanged(Binding binding);
  }

  public void setBindingNameChangedListener(BindingNameChangedListener bindingNameChangedListener) {
    this.bindingNameChangedListener = bindingNameChangedListener;
  }
}
