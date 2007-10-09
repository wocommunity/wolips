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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

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

	public And[] getAnds() {
		List andElements = getChildrenElementsByTagName(And.AND);
		List<And> ands = new LinkedList<And>();
		for (int i = 0; i < andElements.size(); i++) {
			Element andElement = (Element) andElements.get(i);
			And and = new And(andElement, apiModel);
			ands.add(and);
		}
		return ands.toArray(new And[ands.size()]);
	}

	public Count[] getCounts() {
		List countElements = getChildrenElementsByTagName(Count.COUNT);
		List<Count> counts = new LinkedList<Count>();
		for (int i = 0; i < countElements.size(); i++) {
			Element countElement = (Element) countElements.get(i);
			Count count = new Count(countElement, apiModel);
			counts.add(count);
		}
		return counts.toArray(new Count[counts.size()]);
	}

	public Or[] getOrs() {
		List orElements = getChildrenElementsByTagName(Or.OR);
		List<Or> ors = new LinkedList<Or>();
		for (int i = 0; i < orElements.size(); i++) {
			Element orElement = (Element) orElements.get(i);
			Or or = new Or(orElement, apiModel);
			ors.add(or);
		}
		return ors.toArray(new Or[ors.size()]);
	}

	public Not[] getNots() {
		List notElements = getChildrenElementsByTagName(Not.NOT);
		List<Not> nots = new LinkedList<Not>();
		for (int i = 0; i < notElements.size(); i++) {
			Element notElement = (Element) notElements.get(i);
			Not not = new Not(notElement, apiModel);
			nots.add(not);
		}
		return nots.toArray(new Not[nots.size()]);
	}

	public IValidation[] getValidationChildren() {
		List<IValidation> validationChildren = new LinkedList<IValidation>();
		addValidationChildren(validationChildren, getCounts());
		addValidationChildren(validationChildren, getAnds());
		addValidationChildren(validationChildren, getOrs());
		addValidationChildren(validationChildren, getNots());
		addValidationChildren(validationChildren, getUnbounds());
		addValidationChildren(validationChildren, getBounds());
		addValidationChildren(validationChildren, getUnsettables());
		addValidationChildren(validationChildren, getSettables());
		addValidationChildren(validationChildren, getUngettables());
		addValidationChildren(validationChildren, getGettables());
		IValidation[] validations = validationChildren.toArray(new IValidation[validationChildren.size()]);
		return validations;
	}

	protected void addValidationChildren(List<IValidation> allValidationChildren, IValidation[] validationChildren) {
		if (validationChildren != null) {
			for (IValidation child : validationChildren) {
				allValidationChildren.add(child);
			}
		}
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
