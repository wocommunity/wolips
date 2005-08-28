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

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AbstractValidationChild extends AbstractApiModelElement {
	
	public AbstractValidationChild(Element element, ApiModel apiModel) {
		super(element, apiModel);
	}

	public Unsettable[] getUnsettables() {
		NodeList unsetableElements = element.getElementsByTagName(Unsettable.UNSETTABLE);
		ArrayList unsetables = new ArrayList();
		for (int i = 0; i < unsetableElements.getLength(); i++) {
			Element unsettableElement = (Element)unsetableElements.item(i);
			Unsettable validation = new Unsettable(unsettableElement, apiModel);
			unsetables.add(validation);
		}
		return (Unsettable[])unsetables.toArray(new Unsettable[unsetables.size()]);
	}

	public Settable[] getSettables() {
		NodeList setableElements = element.getElementsByTagName(Settable.SETTABLE);
		ArrayList setables = new ArrayList();
		for (int i = 0; i < setableElements.getLength(); i++) {
			Element settableElement = (Element)setableElements.item(i);
			Settable settable = new Settable(settableElement, apiModel);
			setables.add(settable);
		}
		return (Settable[])setables.toArray(new Settable[setables.size()]);
	}

	public Unbound[] getUnbounds() {
		NodeList unsetableElements = element.getElementsByTagName(Unbound.UNBOUND);
		ArrayList unsetables = new ArrayList();
		for (int i = 0; i < unsetableElements.getLength(); i++) {
			Element unboundElement = (Element)unsetableElements.item(i);
			Unbound validation = new Unbound(unboundElement, apiModel);
			unsetables.add(validation);
		}
		return (Unbound[])unsetables.toArray(new Unbound[unsetables.size()]);
	}

	public Bound[] getBounds() {
		NodeList setableElements = element.getElementsByTagName(Bound.BOUND);
		ArrayList setables = new ArrayList();
		for (int i = 0; i < setableElements.getLength(); i++) {
			Element boundElement = (Element)setableElements.item(i);
			Bound bound = new Bound(boundElement, apiModel);
			setables.add(bound);
		}
		return (Bound[])setables.toArray(new Bound[setables.size()]);
	}

	

	public And getAnd() {
		NodeList list = element.getChildNodes();
		assert (list.getLength()== 0 || list.getLength() == 1);
		NodeList elements = element.getElementsByTagName(And.AND);
		if (elements == null || elements.getLength() == 0) {
			return null;
		}
		return new And((Element)elements.item(0), apiModel);
	}

	public Or getOr() {
		NodeList list = element.getChildNodes();
		assert (list.getLength()== 0 || list.getLength() == 1);
		NodeList elements = element.getElementsByTagName(Or.OR);
		if (elements == null || elements.getLength() == 0) {
			return null;
		}
		return new Or((Element)elements.item(0), apiModel);
	}

	public Not getNot() {
		NodeList list = element.getChildNodes();
		assert (list.getLength()== 0 || list.getLength() == 1);
		NodeList elements = element.getElementsByTagName(Not.NOT);
		if (elements == null || elements.getLength() == 0) {
			return null;
		}
		return new Not((Element)elements.item(0), apiModel);
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
