/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
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
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.eomodeler.core.model;

import org.objectstyle.wolips.eomodeler.core.Messages;

public class EOJoinSemantic {
	public static final EOJoinSemantic INNER = new EOJoinSemantic("EOInnerJoin", Messages.getString("EOJoinSemantic.inner"));

	public static final EOJoinSemantic FULL_OUTER = new EOJoinSemantic("EOFullOuterJoin", Messages.getString("EOJoinSemantic.fullOuter"));

	public static final EOJoinSemantic LEFT_OUTER = new EOJoinSemantic("EOLeftOuterJoin", Messages.getString("EOJoinSemantic.leftOuter"));

	public static final EOJoinSemantic RIGHT_OUTER = new EOJoinSemantic("EORightOuterJoin", Messages.getString("EOJoinSemantic.rightOuter"));

	public static final EOJoinSemantic[] JOIN_SEMANTICS = new EOJoinSemantic[] { EOJoinSemantic.INNER, EOJoinSemantic.FULL_OUTER, EOJoinSemantic.LEFT_OUTER, EOJoinSemantic.RIGHT_OUTER };

	private String myID;

	private String myName;

	public EOJoinSemantic(String _id, String _name) {
		myID = _id;
		myName = _name;
	}

	public String getID() {
		return myID;
	}

	public String getName() {
		return myName;
	}

	public String toString() {
		return "[EOJoinSemantic: name = " + myName + "]";
	}

	public static EOJoinSemantic getJoinSemanticByID(String _id) {
		EOJoinSemantic matchingJoinSemantic = null;
		for (int joinSemanticNum = 0; matchingJoinSemantic == null && joinSemanticNum < EOJoinSemantic.JOIN_SEMANTICS.length; joinSemanticNum++) {
			if (EOJoinSemantic.JOIN_SEMANTICS[joinSemanticNum].myID.equals(_id)) {
				matchingJoinSemantic = EOJoinSemantic.JOIN_SEMANTICS[joinSemanticNum];
			}
		}
		if (matchingJoinSemantic == null) {
			matchingJoinSemantic = EOJoinSemantic.INNER;
		}
		return matchingJoinSemantic;
	}
}
