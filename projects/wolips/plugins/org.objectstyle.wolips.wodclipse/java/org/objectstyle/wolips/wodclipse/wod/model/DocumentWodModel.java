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
package org.objectstyle.wolips.wodclipse.wod.model;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.objectstyle.wolips.wodclipse.wod.parser.AssignmentOperatorWordDetector;
import org.objectstyle.wolips.wodclipse.wod.parser.BindingNameRule;
import org.objectstyle.wolips.wodclipse.wod.parser.BindingValueRule;
import org.objectstyle.wolips.wodclipse.wod.parser.CloseDefinitionWordDetector;
import org.objectstyle.wolips.wodclipse.wod.parser.ConstantBindingValueRule;
import org.objectstyle.wolips.wodclipse.wod.parser.ElementNameRule;
import org.objectstyle.wolips.wodclipse.wod.parser.ElementTypeOperatorWordDetector;
import org.objectstyle.wolips.wodclipse.wod.parser.ElementTypeRule;
import org.objectstyle.wolips.wodclipse.wod.parser.EndAssignmentWordDetector;
import org.objectstyle.wolips.wodclipse.wod.parser.OpenDefinitionWordDetector;
import org.objectstyle.wolips.wodclipse.wod.parser.RulePosition;
import org.objectstyle.wolips.wodclipse.wod.parser.WodScanner;

/**
 * @author mschrag
 */
public class DocumentWodModel implements IWodModel {
  private IFile myWodFile;
  private IDocument myDocument;
  private List myElements;
  private List myProblems;

  public DocumentWodModel(IFile _wodFile, IDocument _document) {
    myWodFile = _wodFile;
    myDocument = _document;
    myElements = new LinkedList();
    myProblems = new LinkedList();
    parse();
  }

  protected synchronized void parse() {
    myElements.clear();
    myProblems.clear();

    WodScanner scanner = WodScanner.wodScannerForDocument(myDocument);
    DocumentWodElement element = null;
    RulePosition savedRulePosition = null;
    RulePosition lastRulePosition = null;
    RulePosition rulePosition;
    while ((rulePosition = scanner.nextRulePosition()) != null) {
      boolean whitespace = false;
      boolean syntaxError = false;
      if (RulePosition.isRulePositionOfType(rulePosition, WhitespaceRule.class)) {
        whitespace = true;
      }
      else if (RulePosition.isRulePositionOfType(rulePosition, ElementNameRule.class)) {
        if (lastRulePosition != null && !RulePosition.isOperatorOfType(lastRulePosition, CloseDefinitionWordDetector.class)) {
          addProblem("The element name '" + rulePosition._getTextWithoutException() + "' can only appear at the beginning of the document or after a '}'", rulePosition);
          syntaxError = true;
        }
        savedRulePosition = rulePosition;
        element = null;
        syntaxError = false;
      }
      else if (RulePosition.isOperatorOfType(rulePosition, ElementTypeOperatorWordDetector.class)) {
        if (!RulePosition.isRulePositionOfType(lastRulePosition, ElementNameRule.class)) {
          addProblem("A ':' can only appear after an element name", rulePosition);
          syntaxError = true;
        }
      }
      else if (RulePosition.isRulePositionOfType(rulePosition, ElementTypeRule.class)) {
        if (!RulePosition.isOperatorOfType(lastRulePosition, ElementTypeOperatorWordDetector.class)) {
          addProblem("The element type '" + rulePosition._getTextWithoutException() + "' can only appear after a ':'", rulePosition);
          syntaxError = true;
        }
        else if (!syntaxError) {
          element = new DocumentWodElement(savedRulePosition, rulePosition, this);
          addElement(element);
        }
      }
      else if (RulePosition.isOperatorOfType(rulePosition, OpenDefinitionWordDetector.class)) {
        if (!RulePosition.isRulePositionOfType(lastRulePosition, ElementTypeRule.class)) {
          addProblem("A '{' can only appear after an element type", rulePosition);
          syntaxError = true;
        }
      }
      else if (RulePosition.isRulePositionOfType(rulePosition, BindingNameRule.class)) {
        if (!RulePosition.isOperatorOfType(lastRulePosition, OpenDefinitionWordDetector.class) && !RulePosition.isOperatorOfType(lastRulePosition, EndAssignmentWordDetector.class)) {
          addProblem("The binding name '" + rulePosition._getTextWithoutException() + "' can only appear after a '{' or a ';'", rulePosition);
          syntaxError = true;
        }
        else if (!syntaxError) {
          savedRulePosition = rulePosition;
        }
      }
      else if (RulePosition.isOperatorOfType(rulePosition, AssignmentOperatorWordDetector.class)) {
        if (!RulePosition.isRulePositionOfType(lastRulePosition, BindingNameRule.class)) {
          addProblem("An '=' can only appear after a binding name", rulePosition);
          syntaxError = true;
        }
      }
      else if (RulePosition.isRulePositionOfType(rulePosition, BindingValueRule.class) || RulePosition.isRulePositionOfType(rulePosition, ConstantBindingValueRule.class)) {
        if (!RulePosition.isOperatorOfType(lastRulePosition, AssignmentOperatorWordDetector.class)) {
          addProblem("The binding value '" + rulePosition._getTextWithoutException() + "' can only appear after an '='", rulePosition);
          syntaxError = true;
        }
        else if (!syntaxError) {
          if (element == null) {
            syntaxError = true;
          }
          else {
            DocumentWodBinding binding = new DocumentWodBinding(savedRulePosition, rulePosition, element);
            element.addBinding(binding);
          }
        }
      }
      else if (RulePosition.isOperatorOfType(rulePosition, EndAssignmentWordDetector.class)) {
        if (!RulePosition.isRulePositionOfType(lastRulePosition, BindingValueRule.class) && !RulePosition.isRulePositionOfType(lastRulePosition, ConstantBindingValueRule.class)) {
          addProblem("A ';' can only appear after a binding value", rulePosition);
          syntaxError = true;
        }
      }
      else if (RulePosition.isOperatorOfType(rulePosition, CloseDefinitionWordDetector.class)) {
        if (!RulePosition.isOperatorOfType(lastRulePosition, OpenDefinitionWordDetector.class) && !RulePosition.isOperatorOfType(lastRulePosition, EndAssignmentWordDetector.class)) {
          addProblem("A '}' can only appear after a ';' or a '{'", rulePosition);
          syntaxError = true;
        }
        else {
          element = null;
        }
      }
      else {
        addProblem("'" + rulePosition._getTextWithoutException() + "' is an unknown keyword", rulePosition);
        syntaxError = true;
      }

      if (!whitespace) {
        lastRulePosition = rulePosition;
      }
    }

    if (lastRulePosition != null && !RulePosition.isOperatorOfType(lastRulePosition, CloseDefinitionWordDetector.class)) {
      addProblem("The last entry in a WOD file must be a '}'.", lastRulePosition);
    }
  }

  public synchronized void addProblem(String _message, RulePosition _found) {
    myProblems.add(new WodProblem(this, _message, _found.getPosition()));
  }

  public synchronized void addElement(DocumentWodElement _element) {
    myElements.add(_element);
  }

  public String getName() {
    return myWodFile.getName();
  }

  public IFile getWodFile() {
    return myWodFile;
  }

  public synchronized List getElements() {
    return myElements;
  }

  public synchronized List getSyntacticProblems() {
    return myProblems;
  }

  public String toString() {
    return "[DocumentWodModel: elements = " + myElements + "]";
  }
}
