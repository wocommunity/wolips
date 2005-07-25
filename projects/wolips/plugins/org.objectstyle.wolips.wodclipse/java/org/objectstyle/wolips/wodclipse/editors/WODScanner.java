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
package org.objectstyle.wolips.wodclipse.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.text.AbstractJavaScanner;
import org.eclipse.jdt.internal.ui.text.JavaWhitespaceDetector;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.objectstyle.wolips.wodclipse.WodclipsePlugin;
import org.objectstyle.wolips.wodclipse.preferences.PreferenceConstants;

/**
 * @author mike
 */
public class WODScanner extends AbstractJavaScanner {
  private static String[] WOD_TOKENS = { PreferenceConstants.ELEMENT_NAME, PreferenceConstants.ELEMENT_TYPE, PreferenceConstants.ASSOCIATION_NAME, PreferenceConstants.ASSOCIATION_VALUE, PreferenceConstants.CONSTANT_ASSOCIATION_VALUE, PreferenceConstants.OPERATOR, PreferenceConstants.UNKNOWN };

  public static WODScanner newWODScanner() {
    IColorManager colorManager = JavaPlugin.getDefault().getJavaTextTools().getColorManager();
    IPreferenceStore preferenceStore = WodclipsePlugin.getDefault().getPreferenceStore();
    WODScanner scanner = new WODScanner(colorManager, preferenceStore);
    return scanner;
  }

  public WODScanner(IColorManager _manager, IPreferenceStore _store) {
    super(_manager, _store);
    initialize();
  }

  protected String[] getTokenProperties() {
    return WODScanner.WOD_TOKENS;
  }

  protected List createRules() {
    List rules = new ArrayList();
    rules.add(new SingleLineRule("\"", "\"", getToken(PreferenceConstants.CONSTANT_ASSOCIATION_VALUE), '\\'));
    rules.add(new SingleLineRule("'", "'", getToken(PreferenceConstants.CONSTANT_ASSOCIATION_VALUE), '\\'));
    rules.add(new WhitespaceRule(new JavaWhitespaceDetector()));
    rules.add(new OperatorRule(new ElementTypeOperatorWordDetector(), getToken(PreferenceConstants.OPERATOR)));
    rules.add(new OperatorRule(new OpenDefinitionWordDetector(), getToken(PreferenceConstants.OPERATOR)));
    rules.add(new OperatorRule(new AssignmentOperatorWordDetector(), getToken(PreferenceConstants.OPERATOR)));
    rules.add(new OperatorRule(new EndAssignmentWordDetector(), getToken(PreferenceConstants.OPERATOR)));
    rules.add(new OperatorRule(new CloseDefinitionWordDetector(), getToken(PreferenceConstants.OPERATOR)));
    rules.add(new ElementNameRule(getToken(PreferenceConstants.ELEMENT_NAME)));
    rules.add(new ElementTypeRule(getToken(PreferenceConstants.ELEMENT_TYPE)));
    rules.add(new AssociationNameRule(getToken(PreferenceConstants.ASSOCIATION_NAME)));
    rules.add(new AssociationValueRule(getToken(PreferenceConstants.ASSOCIATION_VALUE)));
    rules.add(new WordPredicateRule(new UnknownWordDetector(), getToken(PreferenceConstants.UNKNOWN)));
    //setDefaultReturnToken(getToken("Default"));
    return rules;
  }

  public Token getToken(String _key) {
    return super.getToken(_key);
  }

  public IRule nextMatchingRule() {
    fTokenOffset = fOffset;
    fColumn = UNDEFINED;

    if (fRules != null) {
      for (int i = 0; i < fRules.length; i++) {
        IToken token = fRules[i].evaluate(this);
        if (!token.isUndefined()) {
          return fRules[i];
        }
      }
    }

    if (read() == EOF) {
      return null;
    }

    return null;
  }

  public IToken nextToken() {
    IToken token = super.nextToken();
    /**
     try {
     System.out.println("WODScanner.nextToken: '" + fDocument.get(getTokenOffset(), getTokenLength()) + "'");
     }
     catch (Throwable t) {
     System.out.println("WODScanner.nextToken: " + t);
     }
     /**/
    return token;
  }
}
