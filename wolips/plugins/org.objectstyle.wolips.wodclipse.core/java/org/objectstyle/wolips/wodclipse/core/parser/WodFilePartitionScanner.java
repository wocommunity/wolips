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
package org.objectstyle.wolips.wodclipse.core.parser;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;

/**
 * @author mike
 */
public class WodFilePartitionScanner extends RuleBasedPartitionScanner implements IWodFilePartitions {
	public WodFilePartitionScanner() {
		List<IRule> rules = new LinkedList<IRule>();
		rules.add(new DefinitionRule());
		/*
		 * Token constant = new
		 * Token(IWODFilePartitions.CONSTANT_ASSOCIATION_VALUE); Token operator =
		 * new Token(IWODFilePartitions.OPERATOR); Token componentName = new
		 * Token(IWODFilePartitions.COMPONENT_NAME); Token componentType = new
		 * Token(IWODFilePartitions.COMPONENT_TYPE); Token associationName = new
		 * Token(IWODFilePartitions.ASSOCIATION_NAME); Token associationValue =
		 * new Token(IWODFilePartitions.ASSOCIATION_VALUE); rules.add(new
		 * SingleLineRule("\"", "\"", constant, '\\')); rules.add(new
		 * SingleLineRule("'", "'", constant, '\\')); rules.add(new
		 * WhitespacePredicateRule(new JavaWhitespaceDetector())); rules.add(new
		 * WordPredicateRule(new ComponentTypeOperatorWordDetector(),
		 * operator)); rules.add(new WordPredicateRule(new
		 * OpenDefinitionWordDetector(), operator)); rules.add(new
		 * WordPredicateRule(new AssignmentOperatorWordDetector(), operator));
		 * rules.add(new WordPredicateRule(new EndAssignmentWordDetector(),
		 * operator)); rules.add(new WordPredicateRule(new
		 * CloseDefinitionWordDetector(), operator)); rules.add(new
		 * ComponentNameRule(componentName)); rules.add(new
		 * ComponentTypeRule(componentType)); rules.add(new
		 * KeyBindingNameRule(associationName)); rules.add(new
		 * KeyBindingValueRule(associationValue));
		 */
		IPredicateRule[] rulesArray = new IPredicateRule[rules.size()];
		rules.toArray(rulesArray);
		setPredicateRules(rulesArray);
	}

	@Override
  public IToken nextToken() {
		IToken token = super.nextToken();
		return token;
	}
}
