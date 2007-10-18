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

import org.objectstyle.wolips.eomodeler.core.utils.StringUtils;

public class EOModelVerificationFailure implements Comparable<EOModelVerificationFailure> {
	private EOModel _model;

	private EOModelObject _failedObject;

	private String _message;

	private Throwable _rootCause;

	private boolean _warning;

	public EOModelVerificationFailure(EOModel model, String message, boolean warning) {
		this(model, model, message, warning, null);
	}

	public EOModelVerificationFailure(EOModel model, String message, boolean warning, Throwable rootCause) {
		this(model, model, message, warning, rootCause);
	}

	public EOModelVerificationFailure(EOModel model, EOModelObject failedObject, String message, boolean warning) {
		this(model, failedObject, message, warning, null);
	}

	public EOModelVerificationFailure(EOModel model, EOModelObject failedObject, String message, boolean warning, Throwable rootCause) {
		_model = model;
		_failedObject = failedObject;
		_message = message;
		_rootCause = rootCause;
		_warning = warning;
	}

	public EOModelObject getFailedObject() {
		return _failedObject;
	}

	public EOModel getModel() {
		return _model;
	}

	public boolean isWarning() {
		return _warning;
	}

	public int hashCode() {
		int hashCode = _message.hashCode();
		if (_rootCause != null) {
			hashCode *= _rootCause.hashCode();
		}
		return hashCode;
	}

	public boolean equals(Object obj) {
		return (obj instanceof EOModelVerificationFailure && (obj == this || (((EOModelVerificationFailure) obj)._message.equals(_message)) && _rootCause == null && ((EOModelVerificationFailure) obj)._rootCause == null));
	}

	public int compareTo(EOModelVerificationFailure obj) {
		return (obj != null) ? obj.getMessage().compareTo(getMessage()) : -1;
	}

	public String getMessage() {
		return StringUtils.getErrorMessage(_message, _rootCause);
	}

	public Throwable getRootCause() {
		return _rootCause;
	}

	public String toString() {
		return "[EOModelVerificationFailure: " + _message + "]";
	}
}
