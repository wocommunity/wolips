/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2002 - 2004 The ObjectStyle Group and individual authors of the
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
package org.objectstyle.wolips.jdt.listener;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.jdt.JdtPlugin;

/**
 * @author ulrich
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MasterJavaElementChangeListener implements IElementChangedListener {
	/**
	 * Constructor for MasterResourceChangeListener.
	 */
	public MasterJavaElementChangeListener() {
		super();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.IElementChangedListener#elementChanged(org.eclipse.jdt.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		JavaElementChangeListener javaElementChangeListener = new JavaElementChangeListener();
		javaElementChangeListener.setEvent(event);

		IJavaElement element = event.getDelta().getElement();
		IResource resource = null;
		try {
			resource = element.getCorrespondingResource();
		} catch (JavaModelException e) {
			JdtPlugin.getDefault().getPluginLogger().log(e);
		}
		IResource rule = null;
		if(resource.getType() == IResource.ROOT) {
			IJavaElementDelta[] resourceDelta = event.getDelta().getAffectedChildren();
			if(resourceDelta.length > 1)
			{
				rule = resource;
			}
			if(resourceDelta.length == 1)
			{
				try {
					rule = resourceDelta[0].getElement().getCorrespondingResource();
				} catch (JavaModelException e1) {
					JdtPlugin.getDefault().getPluginLogger().log(e1);
				}
			}
		}
		else
		{
		IProject iProject = null;
		if(resource != null) {
			rule = resource.getProject();
		}
		}
		if(rule != null) {
			javaElementChangeListener.setRule(rule);
			javaElementChangeListener.schedule();
		}
		else {
			JdtPlugin.getDefault().getPluginLogger().log("No rule to create Java Change listener.");
		}
	}
}