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
import org.eclipse.jface.text.contentassist.CompletionProposal;

/**
 * @author mike
 */
public class WodCompletionProposal implements Comparable {
	private String _token;

	private int _tokenOffset;

	private int _offset;

	private String _proposal;

	private String _display;

	private int _cursorOffset;

	private IType _correspondingType;

	public WodCompletionProposal(String token, int tokenOffset, int offset, String proposal) {
		this(token, tokenOffset, offset, proposal, null, proposal.length());
	}

	public WodCompletionProposal(String token, int tokenOffset, int offset, String proposal, String display, int cursorOffset) {
		_token = token;
		_tokenOffset = tokenOffset;
		_offset = offset;
		_proposal = proposal;
		_display = display;
		_cursorOffset = cursorOffset;
	}

	public void setCorrespondingType(IType correspondingType) {
		_correspondingType = correspondingType;
	}

	public IType getCorrespondingType() {
		return _correspondingType;
	}

	public String getProposal() {
		return _proposal;
	}

	public CompletionProposal toCompletionProposal() {
		CompletionProposal completionProposal = new CompletionProposal(_proposal, _tokenOffset, _token.length(), _cursorOffset, null, _display, null, null);
		return completionProposal;
	}

	@Override
  public boolean equals(Object obj) {
		return (obj instanceof WodCompletionProposal && ((WodCompletionProposal) obj)._proposal.equals(_proposal));
	}

	@Override
  public int hashCode() {
		return _proposal.hashCode();
	}

	public int compareTo(Object obj) {
		int comparison;
		if (obj instanceof WodCompletionProposal) {
			String proposal = _proposal;
			String otherProposal = ((WodCompletionProposal) obj)._proposal;
			if (proposal.startsWith("_")) {
				if (otherProposal.startsWith("_")) {
					comparison = _proposal.compareTo(((WodCompletionProposal) obj)._proposal);
				} else {
					comparison = 1;
				}
			} else {
				if (otherProposal.startsWith("_")) {
					comparison = -1;
				} else {
					comparison = proposal.compareTo(otherProposal);
				}
			}
		} else {
			comparison = -1;
		}
		return comparison;
	}
}
