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

	public List<Unsettable> getUnsettables() {
		List<Element> unsetableElements = getChildrenElementsByTagName(Unsettable.UNSETTABLE);
		List<Unsettable> unsettables = new LinkedList<Unsettable>();
		for (int i = 0; i < unsetableElements.size(); i++) {
			Element unsettableElement = unsetableElements.get(i);
			Unsettable validation = new Unsettable(unsettableElement, apiModel);
			unsettables.add(validation);
		}
		return unsettables;
	}

	public List<Settable> getSettables() {
		List<Element> setableElements = getChildrenElementsByTagName(Settable.SETTABLE);
		List<Settable> settables = new LinkedList<Settable>();
		for (int i = 0; i < setableElements.size(); i++) {
			Element settableElement = setableElements.get(i);
			Settable settable = new Settable(settableElement, apiModel);
			settables.add(settable);
		}
		return settables;
	}

	public List<Ungettable> getUngettables() {
		List<Element> ungetableElements = getChildrenElementsByTagName(Ungettable.UNGETTABLE);
		List<Ungettable> ungettables = new LinkedList<Ungettable>();
		for (int i = 0; i < ungetableElements.size(); i++) {
			Element ungettableElement = ungetableElements.get(i);
			Ungettable validation = new Ungettable(ungettableElement, apiModel);
			ungettables.add(validation);
		}
		return ungettables;
	}

	public List<Gettable> getGettables() {
		List<Element> getableElements = getChildrenElementsByTagName(Gettable.GETTABLE);
		List<Gettable> gettables = new LinkedList<Gettable>();
		for (int i = 0; i < getableElements.size(); i++) {
			Element gettableElement = getableElements.get(i);
			Gettable gettable = new Gettable(gettableElement, apiModel);
			gettables.add(gettable);
		}
		return gettables;
	}

	public List<Unbound> getUnbounds() {
		List<Element> unsetableElements = getChildrenElementsByTagName(Unbound.UNBOUND);
		List<Unbound> unsettables = new LinkedList<Unbound>();
		for (int i = 0; i < unsetableElements.size(); i++) {
			Element unboundElement = unsetableElements.get(i);
			Unbound validation = new Unbound(unboundElement, apiModel);
			unsettables.add(validation);
		}
		return unsettables;
	}

	public List<Bound> getBounds() {
		List<Element> setableElements = getChildrenElementsByTagName(Bound.BOUND);
		List<Bound> settables = new LinkedList<Bound>();
		for (int i = 0; i < setableElements.size(); i++) {
			Element boundElement = setableElements.get(i);
			Bound bound = new Bound(boundElement, apiModel);
			settables.add(bound);
		}
		return settables;
	}

	public List<And> getAnds() {
		List<Element> andElements = getChildrenElementsByTagName(And.AND);
		List<And> ands = new LinkedList<And>();
		for (int i = 0; i < andElements.size(); i++) {
			Element andElement = andElements.get(i);
			And and = new And(andElement, apiModel);
			ands.add(and);
		}
		return ands;
	}

	public List<Count> getCounts() {
		List<Element> countElements = getChildrenElementsByTagName(Count.COUNT);
		List<Count> counts = new LinkedList<Count>();
		for (int i = 0; i < countElements.size(); i++) {
			Element countElement = countElements.get(i);
			Count count = new Count(countElement, apiModel);
			counts.add(count);
		}
		return counts;
	}

	public List<Or> getOrs() {
		List<Element> orElements = getChildrenElementsByTagName(Or.OR);
		List<Or> ors = new LinkedList<Or>();
		for (int i = 0; i < orElements.size(); i++) {
			Element orElement = orElements.get(i);
			Or or = new Or(orElement, apiModel);
			ors.add(or);
		}
		return ors;
	}

	public List<Not> getNots() {
		List<Element> notElements = getChildrenElementsByTagName(Not.NOT);
		List<Not> nots = new LinkedList<Not>();
		for (int i = 0; i < notElements.size(); i++) {
			Element notElement = notElements.get(i);
			Not not = new Not(notElement, apiModel);
			nots.add(not);
		}
		return nots;
	}

	public List<IValidation> getValidationChildren() {
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
		return validationChildren;
	}

	protected void addValidationChildren(List<IValidation> allValidationChildren, List<? extends IValidation> validationChildren) {
		if (validationChildren != null) {
		  allValidationChildren.addAll(validationChildren);
		}
	}

	public boolean isAffectedByBindingNamed(String bindingName) {
		boolean isAffectedByBindingName = false;
		List<IValidation> validationChildren = getValidationChildren();
		for (int i = 0; !isAffectedByBindingName && i < validationChildren.size(); i++) {
			isAffectedByBindingName = validationChildren.get(i).isAffectedByBindingNamed(bindingName);
		}
		return isAffectedByBindingName;
	}

	public abstract boolean evaluate(Map<String, String> bindings);
}
