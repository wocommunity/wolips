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
package org.objectstyle.wolips.wodclipse.wod.completion;

import org.eclipse.jface.text.contentassist.CompletionProposal;

/**
 * @author mike
 */
public class WodCompletionProposal implements Comparable {
  private String myToken;
  private int myTokenOffset;
  private int myOffset;
  private String myProposal;
  private String myDisplay;
  private int myCursorOffset;

  public WodCompletionProposal(String _token, int _tokenOffset, int _offset, String _proposal) {
    this(_token, _tokenOffset, _offset, _proposal, null, _proposal.length());
  }

  public WodCompletionProposal(String _token, int _tokenOffset, int _offset, String _proposal, String _display, int _cursorOffset) {
    myToken = _token;
    myTokenOffset = _tokenOffset;
    myOffset = _offset;
    myProposal = _proposal;
    myDisplay = _display;
    myCursorOffset = _cursorOffset;
  }

  public CompletionProposal toCompletionProposal() {
    CompletionProposal completionProposal = new CompletionProposal(myProposal, myTokenOffset, myToken.length(), myCursorOffset, null, myDisplay, null, null);
    return completionProposal;
  }

  public boolean equals(Object _obj) {
    return (_obj instanceof WodCompletionProposal && ((WodCompletionProposal) _obj).myProposal.equals(myProposal));
  }

  public int hashCode() {
    return myProposal.hashCode();
  }

  public int compareTo(Object _obj) {
    int comparison;
    if (_obj instanceof WodCompletionProposal) {
      String proposal = myProposal;
      String otherProposal = ((WodCompletionProposal)_obj).myProposal;
      if (proposal.startsWith("_")) {
        if (otherProposal.startsWith("_")) {
          comparison = myProposal.compareTo(((WodCompletionProposal) _obj).myProposal);
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
}
