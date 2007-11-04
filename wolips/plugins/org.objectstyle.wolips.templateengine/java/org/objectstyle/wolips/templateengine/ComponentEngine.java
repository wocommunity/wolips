/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2004 The ObjectStyle Group
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

package org.objectstyle.wolips.templateengine;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.objectstyle.wolips.datasets.resources.IWOLipsModel;

/**
 * @author ulrich
 */
public class ComponentEngine extends AbstractEngine {
	private IPath componentPath;

	private IPath apiPath;

	private IPath javaPath;

	private String componentName;

	private String packageName;

	private String superclassName;

	private boolean createBodyTag = false;

	private boolean createWooFile = false;

	private boolean createApiFile = false;

	private String wooEncoding;

	private int htmlBodyType;

	/**
	 * @return Returns the apiPath.
	 */
	public IPath getApiPath() {
		return this.apiPath;
	}

	/**
	 * @param apiPath
	 *            The apiPath to set.
	 */
	public void setApiPath(IPath apiPath) {
		this.apiPath = apiPath;
	}

	/**
	 * @return Returns the componentPath.
	 */
	public IPath getComponentPath() {
		return this.componentPath;
	}

	/**
	 * @param componentPath
	 *            The componentPath to set.
	 */
	public void setComponentPath(IPath componentPath) {
		this.componentPath = componentPath;
	}

	/**
	 * @return Returns the javaPath.
	 */
	public IPath getJavaPath() {
		return this.javaPath;
	}

	/**
	 * @param javaPath
	 *            The javaPath to set.
	 */
	public void setJavaPath(IPath javaPath) {
		this.javaPath = javaPath;
	}

	/**
	 * @return Returns the createBodyTag.
	 */
	public boolean getCreateBodyTag() {
		return this.createBodyTag;
	}

	/**
	 * @param createBodyTag
	 *            The createBodyTag to set.
	 */
	public void setCreateBodyTag(boolean createBodyTag) {
		this.createBodyTag = createBodyTag;
	}

	/**
	 * @return Returns the componentName.
	 */
	public String getComponentName() {
		return this.componentName;
	}

	/**
	 * @param componentName
	 *            The componentName to set.
	 */
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public boolean getCreatePackageDeclaration() {
		return this.packageName != null && this.packageName.length() > 0;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPackageName() {
		return this.packageName;
	}
	
	public void setSuperclassName(String superclassName) {
		this.superclassName = superclassName;
	}
	
	public String getSuperclassName() {
		return superclassName;
	}

	public String getWOOEncoding() {
		return this.wooEncoding;
	}

	public void setWOOEncoding(String stringEncoding) {
		this.wooEncoding = stringEncoding;
	}

	public int getHTMLBodyType() {
		return this.htmlBodyType;
	}

	public void setHTMLBodyType(int type) {
		this.htmlBodyType = type;
	}

	/**
	 * inits the engine
	 */
	public void init() throws Exception {
		super.init();
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException {
		if (this.getCreateBodyTag()) {
			this.setPropertyForKey(this.getCreateBodyTag() + "", "CreateBodyTag");
			this.setPropertyForKey(this.getHTMLBodyType(), "HTMLBodyType");
		}

		if (this.getWOOEncoding() != null) {
			this.setPropertyForKey(this.getWOOEncoding(), "WOOEncoding");
		}

		setDateInContext();
		this.addTemplate(new TemplateDefinition("wocomponent/wocomponent.html.vm", this.getComponentPath().toOSString(), this.componentName + "." + IWOLipsModel.EXT_HTML, IWOLipsModel.EXT_HTML));
		this.addTemplate(new TemplateDefinition("wocomponent/wocomponent.wod.vm", this.getComponentPath().toOSString(), this.componentName + "." + IWOLipsModel.EXT_WOD, IWOLipsModel.EXT_WOD));
		if (this.createWooFile) {
			this.addTemplate(new TemplateDefinition("wocomponent/wocomponent.woo.vm", this.getComponentPath().toOSString(), this.componentName + "." + IWOLipsModel.EXT_WOO, IWOLipsModel.EXT_WOO));
		}
		this.addTemplate(new TemplateDefinition("wocomponent/wocomponent.java.vm", this.getJavaPath().toOSString(), this.componentName + "." + IWOLipsModel.EXT_JAVA, IWOLipsModel.EXT_JAVA));
		if (this.createApiFile) {
			this.addTemplate(new TemplateDefinition("wocomponent/wocomponent.api.vm", this.getApiPath().toOSString(), this.componentName + "." + IWOLipsModel.EXT_API, IWOLipsModel.EXT_API));
		}
		try {
			super.run(monitor);
		} catch (Exception e) {
			TemplateEnginePlugin.getDefault().getPluginLogger().log(e);
			throw new InvocationTargetException(e);
		}
	}

	/**
	 * @return Returns the createApiFile.
	 */
	public boolean isCreateApiFile() {
		return this.createApiFile;
	}

	/**
	 * @param createApiFile
	 *            The createApiFile to set.
	 */
	public void setCreateApiFile(boolean createApiFile) {
		this.createApiFile = createApiFile;
	}

	/**
	 * @return Returns the createWooFile.
	 */
	public boolean isCreateWooFile() {
		return this.createWooFile;
	}

	/**
	 * @param createWooFile
	 *            The createWooFile to set.
	 */
	public void setCreateWooFile(boolean createWooFile) {
		this.createWooFile = createWooFile;
	}

}
