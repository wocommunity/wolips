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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

public class AbstractValidationChild extends AbstractApiModelElement {
	
	public AbstractValidationChild(Element element, ApiModel apiModel) {
		super(element, apiModel);
	}

	public Unsettable[] getUnsettables() {
		List unsetableElements = element.getChildren(Unsettable.UNSETTABLE);
		Iterator iterator = unsetableElements.iterator();
		ArrayList unsetables = new ArrayList();
		while (iterator.hasNext()) {
			Element unsettableElement = (Element)iterator.next();
			Unsettable validation = new Unsettable(unsettableElement, apiModel);
			unsetables.add(validation);
		}
		return (Unsettable[])unsetables.toArray(new Unsettable[unsetables.size()]);
	}

	public Settable[] getSettables() {
		List setableElements = element.getChildren(Settable.SETTABLE);
		Iterator iterator = setableElements.iterator();
		ArrayList setables = new ArrayList();
		while (iterator.hasNext()) {
			Element settableElement = (Element)iterator.next();
			Settable settable = new Settable(settableElement, apiModel);
			setables.add(settable);
		}
		return (Settable[])setables.toArray(new Settable[setables.size()]);
	}

	public Unbound[] getUnbounds() {
		List unsetableElements = element.getChildren(Unbound.UNBOUND);
		Iterator iterator = unsetableElements.iterator();
		ArrayList unsetables = new ArrayList();
		while (iterator.hasNext()) {
			Element unboundElement = (Element)iterator.next();
			Unbound validation = new Unbound(unboundElement, apiModel);
			unsetables.add(validation);
		}
		return (Unbound[])unsetables.toArray(new Unbound[unsetables.size()]);
	}

	public Bound[] getBounds() {
		List setableElements = element.getChildren(Bound.BOUND);
		Iterator iterator = setableElements.iterator();
		ArrayList setables = new ArrayList();
		while (iterator.hasNext()) {
			Element boundElement = (Element)iterator.next();
			Bound bound = new Bound(boundElement, apiModel);
			setables.add(bound);
		}
		return (Bound[])setables.toArray(new Bound[setables.size()]);
	}

	

	public And getAnd() {
		List list = element.getChildren();
		assert (list.size() == 0 || list.size() == 1);
		Element child = element.getChild(And.AND);
		if (child == null) {
			return null;
		}
		return new And(child, apiModel);
	}

	public Or getOr() {
		List list = element.getChildren();
		assert (list.size() == 0 || list.size() == 1);
		Element child = element.getChild(Or.OR);
		if (child == null) {
			return null;
		}
		return new Or(child, apiModel);
	}

	public Not getNot() {
		List list = element.getChildren();
		assert (list.size() == 0 || list.size() == 1);
		Element child = element.getChild(Not.NOT);
		if (child == null) {
			return null;
		}
		return new Not(child, apiModel);
	}

	public boolean isAffectedByBindingNamed(String bindingName) {
		And and = this.getAnd();
		if (and != null) {
			return and.isAffectedByBindingNamed(bindingName);
		}
		Or or = this.getOr();
		if (or != null) {
			return or.isAffectedByBindingNamed(bindingName);
		}
		Not not = this.getNot();
		if (not != null) {
			return not.isAffectedByBindingNamed(bindingName);
		}
		Unbound[] unbounds = this.getUnbounds();
		for (int i = 0; i < unbounds.length; i++) {
			Unbound unbound = unbounds[i];
			if(unbound.isAffectedByBindingNamed(bindingName)) {
				return true;
			}
		}
		Bound[] bounds = this.getBounds();
		for (int i = 0; i < bounds.length; i++) {
			Bound bound = bounds[i];
			if(bound.isAffectedByBindingNamed(bindingName)) {
				return true;
			}
		}
		Unsettable[] unsettables = this.getUnsettables();
		for (int i = 0; i < unsettables.length; i++) {
			Unsettable unsettable = unsettables[i];
			if(unsettable.isAffectedByBindingNamed(bindingName)) {
				return true;
			}
		}
		Settable[] settables = this.getSettables();
		for (int i = 0; i < settables.length; i++) {
			Settable settable = settables[i];
			if(settable.isAffectedByBindingNamed(bindingName)) {
				return true;
			}
		}
		return false;
	}

}
