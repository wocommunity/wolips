/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2002 - 2006 The ObjectStyle Group and individual authors of the
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

package org.objectstyle.wolips.builder.internal;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.objectstyle.wolips.core.resources.types.project.IProjectPatternsets;

/**
 * @author uli
 */
final class BuildResourceValidator extends DefaultDeltaVisitor {
	private boolean buildRequired = false;

	private IProjectPatternsets projectPatternsets;

	/**
	 * Constructor for ProjectFileResourceValidator.
	 */
	public BuildResourceValidator() {
		super();
	}

	/**
	 * 
	 */
	public void reset() {
		this.buildRequired = false;
	}

	/**
	 * @throws CoreException
	 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(IResourceDelta)
	 */
	public final boolean visit(IResourceDelta delta) throws CoreException {
		if (this.buildRequired) {
			return false;
		}
		if (!super.visit(delta)) {
			return false;
		}
		IResource resource = delta.getResource();
		return examineResource(resource, delta.getKind());
	}

	/**
	 * Method examineResource. Examines changed resources for added and/or
	 * removed webobjects project resources and synchronizes project file. <br>
	 * 
	 * @param resource
	 * @param kindOfChange
	 * @return boolean
	 */
	private final boolean examineResource(IResource resource, int kindOfChange) {
		if (this.buildRequired) {
			return false;
		}
		// see bugreport #708385
		if (!resource.isAccessible() && kindOfChange != IResourceDelta.REMOVED) {
			return false;
		}
		if (resource.isDerived()) {
			return false;
		}
			
		// reset project file to update
		switch (resource.getType()) {
		case IResource.ROOT:
			// further investigation of resource delta needed
			return true;
		case IResource.PROJECT:
			this.projectPatternsets = (IProjectPatternsets) resource.getAdapter(IProjectPatternsets.class);
			return true;
		case IResource.FOLDER:
			String extension = resource.getFileExtension();
			if (extension != null) {
				if (extension.equals("framework") || extension.equals("woa") || extension.equals("xcode") || extension.equals("xcodeproj")) {
					return false;
				}
			}
			if (resource.getName().equals("build") || resource.getName().equals("dist") || resource.getName().equals("target")) {
				return false;
			}
			if (resource.getName().equals(".svn") || resource.getName().equals("CVS")) {
				return false;
			}
			// PJYF May 21 2006 We need to exclude the temp wrappers
			if (resource.getName().endsWith("~")) {
				return false;
			}
			if (this.projectPatternsets.matchesResourcesPattern(resource) || this.projectPatternsets.matchesWOAppResourcesPattern(resource) || this.projectPatternsets.matchesClassesPattern(resource)) {
				this.buildRequired = true;
				return false;
			}
			// further examination of resource delta needed
			return true;
		case IResource.FILE:
			if (needsUpdate(kindOfChange)) {
				if (".project".equals(resource.getName()) || "PB.project".equals(resource.getName()) || ".classpath".equals(resource.getName()) || "Makefile".equals(resource.getName()) || resource.getName().startsWith("ant.")) {
					return false;
				} else if (resource.getName().equals("build.properties")) {
					this.buildRequired = true;
					return false;
				} else if (resource.getName().equals("pom.xml")) {
					this.buildRequired = true;
					return false;
				} else if (resource.getName().endsWith(".java") || this.projectPatternsets.matchesResourcesPattern(resource) || this.projectPatternsets.matchesWOAppResourcesPattern(resource) || this.projectPatternsets.matchesClassesPattern(resource)) {
					this.buildRequired = true;
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * Method needsUpdate. @ param kindOfChange @ return boolean
	 */
	private final boolean needsUpdate(int kindOfChange) {
		return IResourceDelta.ADDED == kindOfChange || IResourceDelta.REMOVED == kindOfChange || IResourceDelta.CHANGED == kindOfChange;
	}

	/**
	 * @return
	 */
	public boolean isBuildRequired() {
		return this.buildRequired;
	}
}