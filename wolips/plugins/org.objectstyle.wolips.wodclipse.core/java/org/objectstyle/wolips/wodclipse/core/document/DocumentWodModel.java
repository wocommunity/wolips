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
package org.objectstyle.wolips.wodclipse.core.document;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.objectstyle.wolips.bindings.wod.AbstractWodModel;
import org.objectstyle.wolips.bindings.wod.IWodBinding;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.bindings.wod.IWodUnit;
import org.objectstyle.wolips.bindings.wod.WodProblem;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.parser.AssignmentOperatorWordDetector;
import org.objectstyle.wolips.wodclipse.core.parser.BindingNameRule;
import org.objectstyle.wolips.wodclipse.core.parser.BindingValueRule;
import org.objectstyle.wolips.wodclipse.core.parser.CloseDefinitionWordDetector;
import org.objectstyle.wolips.wodclipse.core.parser.ElementNameRule;
import org.objectstyle.wolips.wodclipse.core.parser.ElementTypeOperatorWordDetector;
import org.objectstyle.wolips.wodclipse.core.parser.ElementTypeRule;
import org.objectstyle.wolips.wodclipse.core.parser.EndAssignmentWordDetector;
import org.objectstyle.wolips.wodclipse.core.parser.ICommentRule;
import org.objectstyle.wolips.wodclipse.core.parser.OpenDefinitionWordDetector;
import org.objectstyle.wolips.wodclipse.core.parser.RulePosition;
import org.objectstyle.wolips.wodclipse.core.parser.StringLiteralRule;
import org.objectstyle.wolips.wodclipse.core.parser.WOOGNLRule;
import org.objectstyle.wolips.wodclipse.core.parser.WodScanner;

/**
 * @author mschrag
 */
public class DocumentWodModel extends AbstractWodModel {
  private IFile _wodFile;

  private IDocument _document;

  public DocumentWodModel(IFile wodFile, IDocument document) {
    _wodFile = wodFile;
    _document = document;
    parse();
  }

  public IDocument getDocument() {
    return _document;
  }

  public void addParseProblem(String message, RulePosition rulePosition, boolean warning) {
    Position position = rulePosition.getPosition();
    try {
      int lineNumber = _document.getLineOfOffset(position.getOffset());
      WodProblem problem = new WodProblem(message, position, lineNumber, warning);
      addParseProblem(problem);
    }
    catch (BadLocationException e) {
      Activator.getDefault().log(e);
    }
  }

  protected synchronized void parse() {
    clear();

    WodScanner scanner = WodScanner.wodScannerForDocument(_document);
    DocumentWodBinding lastBinding = null;
    DocumentWodElement element = null;
    RulePosition savedRulePosition = null;
    RulePosition lastRulePosition = null;
    // boolean stringLiteralIsABindingName = false;
    RulePosition rulePosition;
    while ((rulePosition = scanner.nextRulePosition()) != null) {
      boolean whitespace = false;
      boolean comment = false;
      if (RulePosition.isRulePositionOfType(rulePosition, WhitespaceRule.class)) {
        whitespace = true;
      }
      else if (RulePosition.isRulePositionOfType(rulePosition, ICommentRule.class)) {
        comment = true;
        if (lastBinding != null) {
          try {
            String commentText = rulePosition.getText();
            if (commentText.startsWith("//")) {
              commentText = commentText.substring(2).trim();
              if ("VALID".equalsIgnoreCase(commentText)) {
                lastBinding.setValidate(false);
              }
            }
          }
          catch (BadLocationException e) {
            e.printStackTrace();
          }
        }
      }
      else if (RulePosition.isRulePositionOfType(rulePosition, ElementNameRule.class)) {
        if (lastRulePosition != null && !RulePosition.isOperatorOfType(lastRulePosition, CloseDefinitionWordDetector.class)) {
          addParseProblem("The element name '" + rulePosition._getTextWithoutException() + "' can only appear at the beginning of the document or after a '}'", rulePosition, false);
        }
        savedRulePosition = rulePosition;
        element = null;
      }
      else if (RulePosition.isOperatorOfType(rulePosition, ElementTypeOperatorWordDetector.class)) {
        if (!RulePosition.isRulePositionOfType(lastRulePosition, ElementNameRule.class)) {
          addParseProblem("A ':' can only appear after an element name", rulePosition, false);
        }
      }
      else if (RulePosition.isRulePositionOfType(rulePosition, ElementTypeRule.class)) {
        if (!RulePosition.isOperatorOfType(lastRulePosition, ElementTypeOperatorWordDetector.class)) {
          addParseProblem("The element type '" + rulePosition._getTextWithoutException() + "' can only appear after a ':'", rulePosition, false);
        }
        else {
          element = new DocumentWodElement(savedRulePosition, rulePosition);
          addElement(element);
          savedRulePosition = null;
        }
      }
      else if (RulePosition.isOperatorOfType(rulePosition, OpenDefinitionWordDetector.class)) {
        if (!RulePosition.isRulePositionOfType(lastRulePosition, ElementTypeRule.class)) {
          addParseProblem("A '{' can only appear after an element type", rulePosition, false);
        }
      }
      else if (RulePosition.isRulePositionOfType(rulePosition, WOOGNLRule.class)) {
        boolean ognlIsValue = RulePosition.isOperatorOfType(lastRulePosition, AssignmentOperatorWordDetector.class);
        boolean ognlIsName = !ognlIsValue && (RulePosition.isOperatorOfType(lastRulePosition, EndAssignmentWordDetector.class) || RulePosition.isOperatorOfType(lastRulePosition, OpenDefinitionWordDetector.class));
        if (!ognlIsValue && !ognlIsName) {
          addParseProblem("The OGNL value " + rulePosition._getTextWithoutException() + " can only appear after a '{', '=', or ';'.", rulePosition, false);
          savedRulePosition = null;
        }
        else if (ognlIsName) {
          savedRulePosition = rulePosition;
        }
        else if (ognlIsValue) {
          lastBinding = addBinding(element, savedRulePosition, rulePosition);
          savedRulePosition = null;
        }
      }
      else if (RulePosition.isRulePositionOfType(rulePosition, StringLiteralRule.class)) {
        boolean literalIsValue = RulePosition.isOperatorOfType(lastRulePosition, AssignmentOperatorWordDetector.class);
        boolean literalIsName = !literalIsValue && (RulePosition.isOperatorOfType(lastRulePosition, EndAssignmentWordDetector.class) || RulePosition.isOperatorOfType(lastRulePosition, OpenDefinitionWordDetector.class));
        if (!literalIsValue && !literalIsName) {
          addParseProblem("The string literal '" + rulePosition._getTextWithoutException() + "' can only appear after a '{', '=', or ';'.", rulePosition, false);
          savedRulePosition = null;
        }
        else if (literalIsName) {
          savedRulePosition = rulePosition;
        }
        else if (literalIsValue) {
          lastBinding = addBinding(element, savedRulePosition, rulePosition);
          savedRulePosition = null;
        }
      }
      else if (RulePosition.isRulePositionOfType(rulePosition, BindingNameRule.class)) {
        if (!RulePosition.isOperatorOfType(lastRulePosition, OpenDefinitionWordDetector.class) && !RulePosition.isOperatorOfType(lastRulePosition, EndAssignmentWordDetector.class)) {
          addParseProblem("The binding name '" + rulePosition._getTextWithoutException() + "' can only appear after a '{' or a ';'", rulePosition, false);
        }
        savedRulePosition = rulePosition;
        lastBinding = null;
      }
      else if (RulePosition.isOperatorOfType(rulePosition, AssignmentOperatorWordDetector.class)) {
        if (!RulePosition.isRulePositionOfType(lastRulePosition, BindingNameRule.class) && !RulePosition.isRulePositionOfType(lastRulePosition, StringLiteralRule.class)) {
          addParseProblem("An '=' can only appear after a binding name", rulePosition, false);
        }
      }
      else if (RulePosition.isRulePositionOfType(rulePosition, BindingValueRule.class)) {
        if (!RulePosition.isOperatorOfType(lastRulePosition, AssignmentOperatorWordDetector.class)) {
          addParseProblem("The binding value '" + rulePosition._getTextWithoutException() + "' can only appear after an '='", rulePosition, false);
        }
        else {
          lastBinding = addBinding(element, savedRulePosition, rulePosition);
        }
        savedRulePosition = null;
      }
      else if (RulePosition.isOperatorOfType(rulePosition, EndAssignmentWordDetector.class)) {
        if (!RulePosition.isRulePositionOfType(lastRulePosition, BindingValueRule.class) && !RulePosition.isRulePositionOfType(lastRulePosition, StringLiteralRule.class) && !RulePosition.isRulePositionOfType(lastRulePosition, WOOGNLRule.class)) {
          addParseProblem("A ';' can only appear after a binding value", rulePosition, false);
        }
      }
      else if (RulePosition.isOperatorOfType(rulePosition, CloseDefinitionWordDetector.class)) {
        if (element != null) {
          element.setEndOffset(rulePosition.getTokenOffset() + 1);
        }
        if (!RulePosition.isOperatorOfType(lastRulePosition, OpenDefinitionWordDetector.class) && !RulePosition.isOperatorOfType(lastRulePosition, EndAssignmentWordDetector.class)) {
          addParseProblem("A '}' can only appear after a ';' or a '{'", rulePosition, false);
        }
        else {
          element = null;
        }
        lastBinding = null;
      }
      else {
        addParseProblem("'" + rulePosition._getTextWithoutException() + "' is an unknown keyword", rulePosition, false);
      }

      if (!whitespace && !comment) {
        lastRulePosition = rulePosition;
      }
    }

    if (lastRulePosition != null && !RulePosition.isOperatorOfType(lastRulePosition, CloseDefinitionWordDetector.class)) {
      addParseProblem("The last entry in a WOD file must be a '}'.", lastRulePosition, false);
    }
  }

  protected DocumentWodBinding addBinding(DocumentWodElement _element, RulePosition _nameRulePosition, RulePosition _valueRulePosition) {
    DocumentWodBinding binding = null;
    if (_element == null) {
      addParseProblem("A binding must appear in a declaration", _valueRulePosition, false);
    }
    else if (_nameRulePosition == null) {
      addParseProblem("A binding must have a name", _valueRulePosition, false);
    }
    else if (_valueRulePosition == null) {
      addParseProblem("A binding must have a value", _valueRulePosition, false);
    }
    else {
      binding = new DocumentWodBinding(_nameRulePosition, _valueRulePosition);
      _element.addBinding(binding);
    }
    return binding;
  }

  public String getName() {
    return _wodFile.getName();
  }

  public IFile getWodFile() {
    return _wodFile;
  }

  public int getStartOffset() {
    return 0;
  }

  public int getEndOffset() {
    return _document.getLength();
  }

  public IWodElement getWodElementAtIndex(int index) {
    IWodElement elementAtIndex = null;
    Iterator<IWodElement> elementsIter = getElements().iterator();
    while (elementAtIndex == null && elementsIter.hasNext()) {
      IWodElement element = elementsIter.next();
      if (isIndexContainedByWodUnit(index, element)) {
        elementAtIndex = element;
      }
    }
    return elementAtIndex;
  }

  public IWodUnit getWodUnitAtIndex(int index) {
    IWodUnit wodUnit = null;

    IWodElement elementAtIndex = getWodElementAtIndex(index);
    if (elementAtIndex != null) {
      Iterator<IWodBinding> bindingsIter = elementAtIndex.getBindings().iterator();
      while (wodUnit == null && bindingsIter.hasNext()) {
        IWodBinding binding = bindingsIter.next();
        if (isIndexContainedByWodUnit(index, binding)) {
          wodUnit = binding;
        }
      }
      if (wodUnit == null) {
        wodUnit = elementAtIndex;
      }
    }
    
    if (wodUnit == null) {
      wodUnit = this;
    }
    
    return wodUnit;
  }

  protected boolean isIndexContainedByWodUnit(int index, IWodUnit wodUnit) {
    return index >= wodUnit.getStartOffset() && index <= wodUnit.getEndOffset();
  }

}
