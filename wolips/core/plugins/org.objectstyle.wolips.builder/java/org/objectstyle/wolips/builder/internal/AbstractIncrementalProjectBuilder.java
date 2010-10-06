/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 - 2006 The ObjectStyle Group 
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
package org.objectstyle.wolips.builder.internal;

import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.objectstyle.wolips.builder.BuilderPlugin;
import org.objectstyle.wolips.core.resources.builder.AbstractOldBuilder;

/**
 * @author ulrich
 */
public abstract class AbstractIncrementalProjectBuilder extends AbstractOldBuilder {

	private ThreadLocal<BuildResourceValidator> _buildResourceValidator = new ThreadLocal<BuildResourceValidator>();

	private ThreadLocal<PatternsetDeltaVisitor> _patternsetDeltaVisitor = new ThreadLocal<PatternsetDeltaVisitor>();

	public PatternsetDeltaVisitor patternsetDeltaVisitor() {
		PatternsetDeltaVisitor patternsetDeltaVisitor = _patternsetDeltaVisitor.get();
		if (patternsetDeltaVisitor == null) {
			patternsetDeltaVisitor = new PatternsetDeltaVisitor();
			_patternsetDeltaVisitor.set(patternsetDeltaVisitor);
		}
		return patternsetDeltaVisitor;
	}
	
	public BuildResourceValidator buildResourceValidator() {
		BuildResourceValidator buildResourceValidator = _buildResourceValidator.get();
		if (buildResourceValidator == null) {
			buildResourceValidator = new BuildResourceValidator();
			_buildResourceValidator.set(buildResourceValidator);
		}
		return buildResourceValidator;
	}
	
	/**
	 * Method projectNeedsAnUpdate.
	 * 
	 * @return boolean
	 */
	protected boolean projectNeedsAnUpdate(IResourceDelta delta) {
		BuildResourceValidator buildResourceValidator = buildResourceValidator();
		buildResourceValidator.reset();
		PatternsetDeltaVisitor patternsetDeltaVisitor = patternsetDeltaVisitor();
		patternsetDeltaVisitor.reset();
		if (delta == null)
			return false;
		try {
			delta.accept(patternsetDeltaVisitor);
			if (patternsetDeltaVisitor.isFullBuildRequired()) {
				return true;
			}
			delta.accept(buildResourceValidator);
		} catch (CoreException e) {
			BuilderPlugin.getDefault().log(e);
			return false;
		}
		return buildResourceValidator.isBuildRequired();
	}
}
