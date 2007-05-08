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
package org.objectstyle.wolips.core.resources.types.api;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public abstract class AbstractValidationChild extends AbstractApiModelElement implements IValidation {

	public AbstractValidationChild(Element element, ApiModel apiModel) {
		super(element, apiModel);
	}

	public Unsettable[] getUnsettables() {
		List unsetableElements = getChildrenElementsByTagName(Unsettable.UNSETTABLE);
		List<Unsettable> unsetables = new LinkedList<Unsettable>();
		for (int i = 0; i < unsetableElements.size(); i++) {
			Element unsettableElement = (Element) unsetableElements.get(i);
			Unsettable validation = new Unsettable(unsettableElement, apiModel);
			unsetables.add(validation);
		}
		return unsetables.toArray(new Unsettable[unsetables.size()]);
	}

	public Settable[] getSettables() {
		List setableElements = getChildrenElementsByTagName(Settable.SETTABLE);
		List<Settable> setables = new LinkedList<Settable>();
		for (int i = 0; i < setableElements.size(); i++) {
			Element settableElement = (Element) setableElements.get(i);
			Settable settable = new Settable(settableElement, apiModel);
			setables.add(settable);
		}
		return setables.toArray(new Settable[setables.size()]);
	}

	public Ungettable[] getUngettables() {
		List ungetableElements = getChildrenElementsByTagName(Ungettable.UNGETTABLE);
		List<Ungettable> ungetables = new LinkedList<Ungettable>();
		for (int i = 0; i < ungetableElements.size(); i++) {
			Element ungettableElement = (Element) ungetableElements.get(i);
			Ungettable validation = new Ungettable(ungettableElement, apiModel);
			ungetables.add(validation);
		}
		return ungetables.toArray(new Ungettable[ungetables.size()]);
	}

	public Gettable[] getGettables() {
		List getableElements = getChildrenElementsByTagName(Gettable.GETTABLE);
		List<Gettable> getables = new LinkedList<Gettable>();
		for (int i = 0; i < getableElements.size(); i++) {
			Element gettableElement = (Element) getableElements.get(i);
			Gettable gettable = new Gettable(gettableElement, apiModel);
			getables.add(gettable);
		}
		return getables.toArray(new Gettable[getables.size()]);
	}

	public Unbound[] getUnbounds() {
		List unsetableElements = getChildrenElementsByTagName(Unbound.UNBOUND);
		List<Unbound> unsetables = new LinkedList<Unbound>();
		for (int i = 0; i < unsetableElements.size(); i++) {
			Element unboundElement = (Element) unsetableElements.get(i);
			Unbound validation = new Unbound(unboundElement, apiModel);
			unsetables.add(validation);
		}
		return unsetables.toArray(new Unbound[unsetables.size()]);
	}

	public Bound[] getBounds() {
		List setableElements = getChildrenElementsByTagName(Bound.BOUND);
		List<Bound> setables = new LinkedList<Bound>();
		for (int i = 0; i < setableElements.size(); i++) {
			Element boundElement = (Element) setableElements.get(i);
			Bound bound = new Bound(boundElement, apiModel);
			setables.add(bound);
		}
		return setables.toArray(new Bound[setables.size()]);
	}

	public And getAnd() {
		NodeList list = element.getChildNodes();
		assert (list.getLength() == 0 || list.getLength() == 1);
		List elements = getChildrenElementsByTagName(And.AND);
		if (elements == null || elements.size() == 0) {
			return null;
		}
		return new And((Element) elements.get(0), apiModel);
	}

	public Count getCount() {
		NodeList list = element.getChildNodes();
		assert (list.getLength() == 0 || list.getLength() == 1);
		List elements = getChildrenElementsByTagName(Count.COUNT);
		if (elements == null || elements.size() == 0) {
			return null;
		}
		return new Count((Element) elements.get(0), apiModel);
	}

	public Or getOr() {
		NodeList list = element.getChildNodes();
		assert (list.getLength() == 0 || list.getLength() == 1);
		List elements = getChildrenElementsByTagName(Or.OR);
		if (elements == null || elements.size() == 0) {
			return null;
		}
		return new Or((Element) elements.get(0), apiModel);
	}

	public Not getNot() {
		NodeList list = element.getChildNodes();
		assert (list.getLength() == 0 || list.getLength() == 1);
		List elements = getChildrenElementsByTagName(Not.NOT);
		if (elements == null || elements.size() == 0) {
			return null;
		}
		return new Not((Element) elements.get(0), apiModel);
	}

	public IValidation[] getValidationChildren() {
		List<IValidation> validationChildren = new LinkedList<IValidation>();
		Count count = this.getCount();
		if (count != null) {
			validationChildren.add(count);
		}
		And and = this.getAnd();
		if (and != null) {
			validationChildren.add(and);
		}
		Or or = this.getOr();
		if (or != null) {
			validationChildren.add(or);
		}
		Not not = this.getNot();
		if (not != null) {
			validationChildren.add(not);
		}
		Unbound[] unbounds = this.getUnbounds();
		for (int i = 0; i < unbounds.length; i++) {
			Unbound unbound = unbounds[i];
			validationChildren.add(unbound);
		}
		Bound[] bounds = this.getBounds();
		for (int i = 0; i < bounds.length; i++) {
			Bound bound = bounds[i];
			validationChildren.add(bound);
		}
		Unsettable[] unsettables = this.getUnsettables();
		for (int i = 0; i < unsettables.length; i++) {
			Unsettable unsettable = unsettables[i];
			validationChildren.add(unsettable);
		}
		Settable[] settables = this.getSettables();
		for (int i = 0; i < settables.length; i++) {
			Settable settable = settables[i];
			validationChildren.add(settable);
		}
		Ungettable[] ungettables = this.getUngettables();
		for (int i = 0; i < ungettables.length; i++) {
			Ungettable ungettable = ungettables[i];
			validationChildren.add(ungettable);
		}
		Gettable[] gettables = this.getGettables();
		for (int i = 0; i < gettables.length; i++) {
			Gettable gettable = gettables[i];
			validationChildren.add(gettable);
		}
		IValidation[] validations = validationChildren.toArray(new IValidation[validationChildren.size()]);
		return validations;
	}

	public boolean isAffectedByBindingNamed(String bindingName) {
		boolean isAffectedByBindingName = false;
		IValidation[] validationChildren = getValidationChildren();
		for (int i = 0; !isAffectedByBindingName && i < validationChildren.length; i++) {
			isAffectedByBindingName = validationChildren[i].isAffectedByBindingNamed(bindingName);
		}
		return isAffectedByBindingName;
	}

	public abstract boolean evaluate(Map _bindings);
}
