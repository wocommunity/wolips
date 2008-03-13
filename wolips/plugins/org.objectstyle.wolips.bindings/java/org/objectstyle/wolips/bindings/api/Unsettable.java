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
import java.util.Map;

import org.w3c.dom.Element;

public class Unsettable extends AbstractNamedValidation {

  protected final static String UNSETTABLE = "unsettable";

  protected Unsettable(Element element, ApiModel apiModel) {
    super(element, apiModel);
  }

  public static void addToWoWithBinding(Wo wo, Binding binding) {
    synchronized (wo.apiModel) {
      Element newValidationElement = wo.element.getOwnerDocument().createElement(Validation.VALIDATION);
      wo.element.appendChild(newValidationElement);
      newValidationElement.setAttribute(Validation.MESSAGE, "'" + binding.getName() + "' must be bound to a settable value");
      Element newUnsettableElement = wo.element.getOwnerDocument().createElement(UNSETTABLE);
      newValidationElement.appendChild(newUnsettableElement);
      newUnsettableElement.setAttribute(NAME, binding.getName());
    }
  }

  public static void removeFromWoWithBinding(Wo wo, Binding binding) {
    synchronized (wo.apiModel) {
      List<Validation> validations = wo.getValidations();
      for (int i = validations.size() - 1; i > 0; i--) {
        Validation validation = validations.get(i);
        List<Unsettable> unsettables = validation.getUnsettables();
        if (unsettables.size() == 1 && unsettables.get(0).isAffectedByBindingNamed(binding.getName())) {
          validation.element.removeChild(unsettables.get(0).element);
        }
      }
    }
  }

  public boolean evaluate(Map<String, String> bindings) {
    String bindingName = getName();
    String bindingValue = bindings.get(bindingName);
    boolean evaluation = (bindingValue != null && bindingValue.startsWith("\"") && !bindingValue.startsWith("\"~"));
    return evaluation;
  }
}
