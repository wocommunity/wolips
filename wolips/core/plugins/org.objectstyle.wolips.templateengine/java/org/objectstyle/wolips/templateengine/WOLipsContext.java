/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2004 The ObjectStyle Group,
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
 /*Portions of this code are Copyright Apple Inc. 2008 and licensed under the
ObjectStyle Group Software License, version 1.0.  This license from Apple
applies solely to the actual code contributed by Apple and to no other code.
No other license or rights are granted by Apple, explicitly, by implication,
by estoppel, or otherwise.  All rights reserved.*/
package org.objectstyle.wolips.templateengine;

import java.util.GregorianCalendar;

/**
 * @author ulrich
 */
public class WOLipsContext {
	/**
	 * Comment for <code>Key</code>
	 */
	public final static String Key = "WOLipsContext";

	private String projectName;

	private String adaptorName;

	private String componentName;

	private String packageName;

	private boolean createBodyTag = false;

	private String antFolderName;

	private String wooEncoding;

	private int htmlBodyType;

	private String artifactId;

	private String gid;

	private String mavenVersion;

	protected WOLipsContext() {
		super();
	}

	/**
	 * @return Returns the projectName.
	 */
	public String getProjectName() {
		return this.projectName;
	}

	/**
	 * @param projectName
	 *            The projectName to set.
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return plugin name
	 */
	public String getPluginName() {
		return TemplateEnginePlugin.getPluginId();
	}

	/**
	 * @return Returns the adaptorName.
	 */
	public String getAdaptorName() {
		return this.adaptorName;
	}

	/**
	 * @param adaptorName
	 *            The adaptorName to set.
	 */
	public void setAdaptorName(String adaptorName) {
		this.adaptorName = adaptorName;
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

	/**
	 * @return package declaration
	 */
	public boolean getCreatePackageDeclaration() {
		return this.packageName != null && this.packageName.length() > 0;
	}

	/**
	 * @param packageName
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	/**
	 * @return package name
	 */
	public String getPackageName() {
		return this.packageName;
	}

	/**
	 * @return date
	 */
	public String getDate() {
		return new GregorianCalendar().toString();
	}

	/**
	 * @return Returns the antFolderName.
	 */
	public String getAntFolderName() {
		return this.antFolderName;
	}

	/**
	 * @param antFolderName
	 *            The antFolderName to set.
	 */
	public void setAntFolderName(String antFolderName) {
		this.antFolderName = antFolderName;
	}

	/**
	 * Return woo file encoding
	 * @return woo encoding
	 */
	public String getWOOEncoding() {
		return wooEncoding;
	}

	/**
	 * Set the .woo file encoding
	 * @param encoding
	 */
	public void setWOOEncoding(String encoding) {
		this.wooEncoding = encoding;
	}

	/**
	 * @return html doc type
	 */
	public int getHTMLBodyType () {
		return this.htmlBodyType;
	}

	/**
	 * @param type
	 */
	public void setHTMLBodyType(int type) {
		this.htmlBodyType = type;
	}

	/**
	 * @return maven project artifact id
	 */
	public String getArtifactId() {
		return this.artifactId;
	}

	/**
	 * Set  maven project artifact id
	 * @param artifactID
	 */
	public void setArtifactId(String artifactID) {
		this.artifactId = artifactID;
	}

	/**
	 * @return maven project artifact id
	 */
	public String getGroupId() {
		return this.gid;
	}

	/**
	 * Set maven project group id
	 * @param groupId
	 */
	public void setGroupId(String groupId) {
		this.gid = groupId;
	}

	/**
	 * @return maven project artifact id
	 */
	public String getMavenProjectVersion() {
		return this.mavenVersion;
	}

	/**
	 * Set maven project version
	 * @param version
	 */
	public void setMavenProjectVersion(String version) {
		this.mavenVersion = version;
	}

}
