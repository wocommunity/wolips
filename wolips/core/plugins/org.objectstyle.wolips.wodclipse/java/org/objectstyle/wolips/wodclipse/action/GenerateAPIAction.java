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
package org.objectstyle.wolips.wodclipse.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.objectstyle.wolips.core.resources.types.api.ApiModel;
import org.objectstyle.wolips.core.resources.types.api.ApiModelException;
import org.objectstyle.wolips.core.resources.types.api.Wo;
import org.objectstyle.wolips.locate.LocateException;
import org.objectstyle.wolips.locate.LocatePlugin;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;
import org.objectstyle.wolips.wodclipse.wod.completion.WodBindingUtils;
import org.objectstyle.wolips.wodclipse.wod.model.BindingValueKey;

public class GenerateAPIAction implements IObjectActionDelegate {
	private ISelection _selection;

	public void dispose() {
		// do nothing
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// do nothing
	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		_selection = selection;
	}

	public void run() {
		try {
			if (_selection instanceof IStructuredSelection) {
				Map typeContextCache = new HashMap();
				Object[] selectedObjects = ((IStructuredSelection) _selection).toArray();
				for (int i = 0; i < selectedObjects.length; i++) {
					IResource resource = (IResource) selectedObjects[i];
					LocalizedComponentsLocateResult componentsLocateResults = LocatePlugin.getDefault().getLocalizedComponentsLocateResult(resource);
					List bindingKeys = WodBindingUtils.createMatchingBindingKeys(JavaCore.create(resource.getProject()), componentsLocateResults.getDotJavaType(), "", false, WodBindingUtils.MUTATORS_ONLY, typeContextCache);
					IFile apiFile = componentsLocateResults.getDotApi(true);
					ApiModel apiModel = new ApiModel(apiFile);
					Wo wo = apiModel.getWo();
					Iterator bindingKeysIter = bindingKeys.iterator();
					while (bindingKeysIter.hasNext()) {
						BindingValueKey bindingKey = (BindingValueKey) bindingKeysIter.next();
						if (!WodBindingUtils.isSystemBindingValueKey(bindingKey, true)) {
							String bindingName = bindingKey.getBindingName();
							wo.createBinding(bindingName);
						}
					}
					apiModel.saveChanges();
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (LocateException e) {
			e.printStackTrace();
		} catch (ApiModelException e) {
			e.printStackTrace();
		}
	}
}
