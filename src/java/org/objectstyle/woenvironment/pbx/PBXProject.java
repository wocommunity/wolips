/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 The ObjectStyle Group 
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
package org.objectstyle.woenvironment.pbx;

import java.util.Vector;

/**
 * @author tlg
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
@SuppressWarnings("unchecked")
public class PBXProject extends PBXItem {
	public final static String _KBUILDSTYLES = "buildStyles";

	// public final static String _KBUILDSETTINGS = "buildSettings";
	public final static String _KMAINGROUP = "mainGroup";

	public final static String _KPRODUCTREFGROUP = "productRefGroup";

	public final static String _KPROJECTDIRPATH = "projectDirPath";

	public final static String _KTARGETS = "targets";

	protected Vector buildStyles;

	protected Vector buildSettings;

	protected PBXGroup mainGroup;

	protected PBXGroup productRefGroup;

	protected String projectDirPath;

	protected Vector targets;

	public PBXProject(Object ref) {
		super(ref);
		buildStyles = new Vector();
		buildSettings = new Vector();
		targets = new Vector();
	}

	public void addBuildStyles(Object buildStyle) {
		this.buildStyles.add(buildStyle);
	}

	public Vector getBuildStyles() {
		return this.buildStyles;
	}

	public void addBuildSettings(Object buildSetting) {
		this.buildSettings.add(buildSetting);
	}

	public Vector getBuildSettings() {
		return this.buildSettings;
	}

	public void setMainGroup(Object mainGroup) {
		this.mainGroup = (PBXGroup) mainGroup;
	}

	public PBXGroup getMainGroup() {
		return this.mainGroup;
	}

	public void setProductRefGroup(Object productRefGroup) {
		this.productRefGroup = (PBXGroup) productRefGroup;
	}

	public PBXGroup getProductRefGroup() {
		return this.productRefGroup;
	}

	public void setProjectDirPath(Object projectDirPath) {
		this.projectDirPath = (String) projectDirPath;
	}

	public String getProjectDirPath() {
		return this.projectDirPath;
	}

	public String projectDirectoryPath() {
		return this.projectDirPath;
	}

	public void addTargets(Object target) {
		this.targets.add(target);
	}

	public Vector getTargets() {
		return this.targets;
	}
}