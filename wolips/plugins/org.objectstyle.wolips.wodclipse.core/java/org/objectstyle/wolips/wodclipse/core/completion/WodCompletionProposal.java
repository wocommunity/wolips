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
package org.objectstyle.wolips.wodclipse.core.completion;

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

/**
 * @author mike
 */
public class WodCompletionProposal implements Comparable, ICompletionProposal {
  private String _token;

  private int _replacementOffset;

  private int _offset;

  private String _replacementString;

  private int _replacementLength;

  private String _displayString;

  private int _cursorPosition;

  private IType _correspondingType;

  private Image _image;

  private IContextInformation _contextInformation;

  private String _additionalProposalInfo;

  public WodCompletionProposal(String token, int replacementOffset, int offset, String replacementString) {
    this(token, replacementOffset, offset, replacementString, null, replacementString.length());
  }

  public WodCompletionProposal(String token, int replacementOffset, int offset, String replacementString, String displayString, int cursorPosition) {
    this(token, replacementOffset, token.length(), offset, replacementString, null, cursorPosition, null);
  }

  public WodCompletionProposal(String token, int replacementOffset, int replacementLength, int offset, String replacementString, String displayString, int cursorPosition, Image image) {
    _token = token;
    _offset = offset;
    _replacementOffset = replacementOffset;
    _replacementString = replacementString;
    _replacementLength = replacementLength;
    _displayString = displayString;
    _cursorPosition = cursorPosition;

    _image = image;
    _contextInformation = null;
    _additionalProposalInfo = null;
  }

  public void setCorrespondingType(IType correspondingType) {
    _correspondingType = correspondingType;
  }

  public IType getCorrespondingType() {
    return _correspondingType;
  }

  public String getProposal() {
    return _replacementString;
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof WodCompletionProposal && ((WodCompletionProposal) obj)._replacementString.equals(_replacementString));
  }

  @Override
  public int hashCode() {
    return _replacementString.hashCode();
  }

  public int compareTo(Object obj) {
    int comparison;
    if (obj instanceof WodCompletionProposal) {
      String proposal = _replacementString;
      String otherProposal = ((WodCompletionProposal) obj)._replacementString;
      if (proposal.startsWith("_")) {
        if (otherProposal.startsWith("_")) {
          comparison = _replacementString.compareTo(((WodCompletionProposal) obj)._replacementString);
        }
        else {
          comparison = 1;
        }
      }
      else {
        if (otherProposal.startsWith("_")) {
          comparison = -1;
        }
        else {
          comparison = proposal.compareTo(otherProposal);
        }
      }
    }
    else {
      comparison = -1;
    }
    return comparison;
  }

  /*
   * @see ICompletionProposal#apply(IDocument)
   */
  public void apply(IDocument document) {
    try {
      document.replace(_replacementOffset, _replacementLength, _replacementString);
    }
    catch (BadLocationException x) {
      // ignore
    }
  }

  /*
   * @see ICompletionProposal#getSelection(IDocument)
   */
  public Point getSelection(IDocument document) {
    return new Point(_replacementOffset + _cursorPosition, 0);
  }

  /*
   * @see ICompletionProposal#getContextInformation()
   */
  public IContextInformation getContextInformation() {
    return _contextInformation;
  }

  /*
   * @see ICompletionProposal#getImage()
   */
  public Image getImage() {
    return _image;
  }

  /*
   * @see ICompletionProposal#getDisplayString()
   */
  public String getDisplayString() {
    if (_displayString != null)
      return _displayString;
    return _replacementString;
  }

  /*
   * @see ICompletionProposal#getAdditionalProposalInfo()
   */
  public String getAdditionalProposalInfo() {
    return _additionalProposalInfo;
  }
}
