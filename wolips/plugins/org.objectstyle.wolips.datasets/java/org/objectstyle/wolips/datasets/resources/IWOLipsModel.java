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

package org.objectstyle.wolips.datasets.resources;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;

/**
 * @author ulrich
 * @deprecated Use org.objectstyle.wolips.core.* instead.
 */
public interface IWOLipsModel {
	// flle extensions and resource identifier
	/**
	 * Comment for <code>EXT_PROJECT</code>
	 */
	public static final String EXT_PROJECT = "project";

	/**
	 * Comment for <code>EXT_SUBPROJECT</code>
	 */
	public static final String EXT_SUBPROJECT = "subproj";

	/**
	 * Comment for <code>EXT_LPROJ</code>
	 */
	public static final String EXT_LPROJ = "lproj";

	/**
	 * Comment for <code>EXT_JAVA</code>
	 */
	public static final String EXT_JAVA = "java";

	/**
	 * Comment for <code>EXT_COMPONENT</code>
	 */
	public static final String EXT_COMPONENT = "wo";

	/**
	 * Comment for <code>EXT_API</code>
	 */
	public static final String EXT_API = "api";

	/**
	 * Comment for <code>EXT_WOD</code>
	 */
	public static final String EXT_WOD = "wod";

	/**
	 * Comment for <code>EXT_WOO</code>
	 */
	public static final String EXT_WOO = "woo";

	/**
	 * Comment for <code>EXT_HTML</code>
	 */
	public static final String EXT_HTML = "html";

	/**
	 * Comment for <code>EXT_EOMODEL</code>
	 */
	public static final String EXT_EOMODEL = "eomodeld";

	/**
	 * Comment for <code>EXT_EOMODEL_BACKUP</code>
	 */
	public static final String EXT_EOMODEL_BACKUP = "eomodeld~";

	/**
	 * Comment for <code>EXT_D2WMODEL</code>
	 */
	public static final String EXT_D2WMODEL = "d2wmodel";

	/**
	 * Comment for <code>EXT_PROPERTIES</code>
	 */
	public static final String EXT_PROPERTIES = "properties";

	/**
	 * Comment for <code>EXT_STRINGS</code>
	 */
	public static final String EXT_STRINGS = "strings";

	/**
	 * Comment for <code>EXT_SRC</code>
	 */
	public static final String EXT_SRC = "src";

	/**
	 * Comment for <code>EXT_FRAMEWORK</code>
	 */
	public static final String EXT_FRAMEWORK = "framework";

	/**
	 * Comment for <code>EXT_WOA</code>
	 */
	public static final String EXT_WOA = "woa";

	/**
	 * Comment for <code>EXT_BUILD</code>
	 */
	public static final String EXT_BUILD = "build";

	/**
	 * Comment for <code>EXT_DIST</code>
	 */
	public static final String EXT_DIST = "dist";

	// webobjects project file lists
	/**
	 * Comment for <code>RESOURCES_ID</code>
	 */
	public static final String RESOURCES_ID = "WOAPP_RESOURCES";

	/**
	 * Comment for <code>WS_RESOURCES_ID</code>
	 */
	public static final String WS_RESOURCES_ID = "WEBSERVER_RESOURCES";

	/**
	 * Comment for <code>CLASSES_ID</code>
	 */
	public static final String CLASSES_ID = "CLASSES";

	/**
	 * Comment for <code>COMPONENTS_ID</code>
	 */
	public static final String COMPONENTS_ID = "WO_COMPONENTS";

	/**
	 * Comment for <code>SUBPROJECTS_ID</code>
	 */
	public static final String SUBPROJECTS_ID = "SUBPROJECTS";

	/**
	 * Comment for <code>FRAMEWORKS_ID</code>
	 */
	public static final String FRAMEWORKS_ID = "FRAMEWORKS";

	/**
	 * Comment for <code>PROJECT_FILE_NAME</code>
	 */
	public static final String PROJECT_FILE_NAME = "PB.project";

	/**
	 * Comment for <code>DEFAULT_BUILD_FILENAME</code>
	 */
	public static final String DEFAULT_BUILD_FILENAME = "build.xml";

	/**
	 * Comment for <code>RESOURCE_IDENTIFIERS</code>
	 */
	public static final String[] RESOURCE_IDENTIFIERS = { EXT_JAVA, EXT_COMPONENT, EXT_API, EXT_EOMODEL, EXT_STRINGS, EXT_SUBPROJECT, EXT_FRAMEWORK };

	/**
	 * Comment for <code>LIST_IDENTIFIERS</code>
	 */
	public static final String[] LIST_IDENTIFIERS = { CLASSES_ID, COMPONENTS_ID, RESOURCES_ID, RESOURCES_ID, RESOURCES_ID, SUBPROJECTS_ID, FRAMEWORKS_ID };

	/**
	 * @param resource
	 * @return Returns the IWOLipsResource if the resource is a WOLips resource.
	 *         Otherwise null is returned.
	 */
	public abstract IWOLipsResource getWOLipsResource(IResource resource);

	/**
	 * @param resource
	 * @return Returns true is the resource is a WOLips resource.
	 */
	public abstract boolean isWOLipsResource(IResource resource);

	/**
	 * @param compilationUnit
	 * @return Returns the IWOLipsResource if the resource is a WOLips resource.
	 *         Otherwise null is returned.
	 */
	public abstract IWOLipsCompilationUnit getWOLipsCompilationUnit(ICompilationUnit compilationUnit);

	/**
	 * @param compilationUnit
	 * @return Returns true is the ICompilationUnit is a WOLips CompilationUnit.
	 */
	public abstract boolean isWOLipsCompilationUnit(ICompilationUnit compilationUnit);
}
