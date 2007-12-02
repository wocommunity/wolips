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
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.util.SuperTypeHierarchyCache;
import org.objectstyle.wolips.datasets.DataSetsPlugin;

/**
 * @author ulrich
 * @deprecated Use org.objectstyle.wolips.core.* instead.
 */
public final class WOLipsModel implements IWOLipsModel {
	private static WOLipsModel wolipsModel;

	private final static int UNKNOWN_RESOURCE_TYPE = -1;

	private final static int UNKNOWN_COMPILATION_UNIT_TYPE = -1;

	private final static String[] RESOURCE_TYPES = new String[] { WOComponentBundle.class.getName(), WOComponentDefinition.class.getName(), WOComponentHtml.class.getName(), WOComponentWoo.class.getName(), WOComponentApi.class.getName(), EOModel.class.getName() };

	/**
	 * Comment for <code>WOCOMPONENT_BUNDLE_EXTENSION</code>
	 */
	public final static String WOCOMPONENT_BUNDLE_EXTENSION = "wo";

	/**
	 * Comment for <code>WOCOMPONENT_WOD_EXTENSION</code>
	 */
	public final static String WOCOMPONENT_WOD_EXTENSION = "wod";

	/**
	 * Comment for <code>WOCOMPONENT_HTML_EXTENSION</code>
	 */
	public final static String WOCOMPONENT_HTML_EXTENSION = "html";

	/**
	 * Comment for <code>WOCOMPONENT_WOO_EXTENSION</code>
	 */
	public final static String WOCOMPONENT_WOO_EXTENSION = "woo";

	/**
	 * Comment for <code>WOCOMPONENT_API_EXTENSION</code>
	 */
	public final static String WOCOMPONENT_API_EXTENSION = "api";

	/**
	 * Comment for <code>EOMODEL_EXTENSION</code>
	 */
	public final static String EOMODEL_EXTENSION = "eomodeld";

	/**
	 * Comment for <code>EOENTITY_PLIST_EXTENSION</code>
	 */
	public final static String EOENTITY_PLIST_EXTENSION = "plist";

	/**
	 * Comment for <code>BUNDLE_TYPES</code>
	 */
	public final static String[] BUNDLE_TYPES = new String[] { WOLipsModel.WOCOMPONENT_BUNDLE_EXTENSION, WOLipsModel.EOMODEL_EXTENSION };

	/**
	 * Comment for <code>BUNDLE_TYPES_TO_RESOURCE_TYPE_MAPPING</code>
	 */
	public final static int[] BUNDLE_TYPES_TO_RESOURCE_TYPE_MAPPING = new int[] { IWOLipsResource.WOCOMPONENT_BUNDLE, IWOLipsResource.EOMODEL };

	/**
	 * Comment for <code>FILE_TYPES</code>
	 */
	public final static String[] FILE_TYPES = new String[] { WOLipsModel.WOCOMPONENT_WOD_EXTENSION, WOLipsModel.WOCOMPONENT_HTML_EXTENSION, WOLipsModel.WOCOMPONENT_WOO_EXTENSION, WOLipsModel.WOCOMPONENT_API_EXTENSION, WOLipsModel.EOMODEL_EXTENSION };

	/**
	 * Comment for <code>FILE_TYPES_TO_RESOURCE_TYPE_MAPPING</code>
	 */
	public final static int[] FILE_TYPES_TO_RESOURCE_TYPE_MAPPING = new int[] { IWOLipsResource.WOCOMPONENT_WOD, IWOLipsResource.WOCOMPONENT_HTML, IWOLipsResource.WOCOMPONENT_WOO, IWOLipsResource.WOCOMPONENT_API, IWOLipsResource.EOMODEL };

	private static final String[] COMPILATION_UNITS = new String[] { WOComponentJava.class.getName(), EOEntityJava.class.getName(), EOEntityJava.class.getName(), EOEntityJava.class.getName() };

	private static final String[] COMPILATION_UNIT_SUPER_TYPES = new String[] { "WOComponent", "EOGenericRecord", "EOEnterpriseObject", "EOCustomObject" };

	private WOLipsModel() {
		super();
	}

	public final IWOLipsResource getWOLipsResource(IResource resource) {
		if (resource == null || !resource.isAccessible())
			return null;
		final int resourceType = this.getWOLipsResourceType(resource);
		if (resourceType == WOLipsModel.UNKNOWN_RESOURCE_TYPE)
			return null;
		Class clazz;
		WOLipsResource wolipsResource;
		try {
			clazz = this.getClass().getClassLoader().loadClass(RESOURCE_TYPES[resourceType]);
			wolipsResource = (WOLipsResource) clazz.newInstance();
			wolipsResource.setCorrespondingResource(resource);
		} catch (InstantiationException e) {
			wolipsResource = null;
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		} catch (IllegalAccessException e) {
			wolipsResource = null;
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		} catch (ClassNotFoundException e) {
			wolipsResource = null;
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		} finally {
			clazz = null;
		}
		return wolipsResource;
	}

	/**
	 * @param resource
	 * @return Returns true is the resource is a WOLips resource.
	 */
	public final boolean isWOLipsResource(IResource resource) {
		return this.getWOLipsResourceType(resource) == WOLipsModel.UNKNOWN_RESOURCE_TYPE;
	}

	public final IWOLipsCompilationUnit getWOLipsCompilationUnit(ICompilationUnit compilationUnit) {
		try {
			if (compilationUnit == null || compilationUnit.getCorrespondingResource() == null || !compilationUnit.getCorrespondingResource().isAccessible())
				return null;
		} catch (JavaModelException e) {
			// ak: this can happen a lot during refactoring and we can't use the error anyway
			// DataSetsPlugin.getDefault().getPluginLogger().log(e);
			return null;
		}
		final int compilationUnitType = this.getWOLipsCompilationUnitType(compilationUnit);
		if (compilationUnitType == WOLipsModel.UNKNOWN_COMPILATION_UNIT_TYPE)
			return null;
		Class clazz;
		WOLipsCompilationUnit wolipsCompilationUnit;
		try {
			clazz = this.getClass().getClassLoader().loadClass(COMPILATION_UNITS[compilationUnitType]);
			wolipsCompilationUnit = (WOLipsCompilationUnit) clazz.newInstance();
			wolipsCompilationUnit.setCorrespondingCompilationUnit(compilationUnit);
		} catch (InstantiationException e) {
			wolipsCompilationUnit = null;
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		} catch (IllegalAccessException e) {
			wolipsCompilationUnit = null;
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		} catch (ClassNotFoundException e) {
			wolipsCompilationUnit = null;
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		} finally {
			clazz = null;
		}
		return wolipsCompilationUnit;
	}

	public final boolean isWOLipsCompilationUnit(ICompilationUnit compilationUnit) {
		return this.getWOLipsCompilationUnitType(compilationUnit) == WOLipsModel.UNKNOWN_COMPILATION_UNIT_TYPE;
	}

	private final int getWOLipsResourceType(IResource resource) {
		int resourceType = WOLipsModel.UNKNOWN_RESOURCE_TYPE;
		if (resource.getType() == IResource.FOLDER) {
			for (int i = 0; i < WOLipsModel.BUNDLE_TYPES.length; i++) {
				if (resource.getName().endsWith(WOLipsModel.BUNDLE_TYPES[i])) {
					resourceType = WOLipsModel.BUNDLE_TYPES_TO_RESOURCE_TYPE_MAPPING[i];
					break;
				}
			}
		} else if (resource.getType() == IResource.FILE) {
			for (int i = 0; i < WOLipsModel.FILE_TYPES.length; i++) {
				if (resource.getName().endsWith(WOLipsModel.FILE_TYPES[i])) {
					resourceType = WOLipsModel.FILE_TYPES_TO_RESOURCE_TYPE_MAPPING[i];
					break;
				}
			}
		}
		return resourceType;
	}

	private final int getWOLipsCompilationUnitType(ICompilationUnit compilationUnit) {
		IType[] types = null;
		int compilationUnitType = WOLipsModel.UNKNOWN_COMPILATION_UNIT_TYPE;
		try {
			IType type = compilationUnit.findPrimaryType();
			if (type != null) {
				ITypeHierarchy typeHierarchy = SuperTypeHierarchyCache.getTypeHierarchy(type);
				types = typeHierarchy.getAllSupertypes(type);
				compilationUnitType = this.getWOLipsCompilationUnitType(types);
			}
		} catch (JavaModelException e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		}
		return compilationUnitType;
	}

	/**
	 * @param types
	 * @return
	 */
	public final int getWOLipsCompilationUnitType(IType[] types) {
		int compilationUnitType = WOLipsModel.UNKNOWN_COMPILATION_UNIT_TYPE;
		for (int i = 0; i < types.length; i++) {
			compilationUnitType = this.getWOLipsCompilationUnitType(types[i]);
			if (compilationUnitType != WOLipsModel.UNKNOWN_COMPILATION_UNIT_TYPE)
				break;
		}
		return compilationUnitType;
	}

	/**
	 * @param type
	 * @return
	 */
	public final int getWOLipsCompilationUnitType(IType type) {
		int compilationUnitType = WOLipsModel.UNKNOWN_COMPILATION_UNIT_TYPE;
		for (int i = 0; i < WOLipsModel.COMPILATION_UNIT_SUPER_TYPES.length; i++) {
			String elementName = type.getElementName();
			String supertypeName = WOLipsModel.COMPILATION_UNIT_SUPER_TYPES[i];
			if (supertypeName.equals(elementName)) {
				compilationUnitType = i;
				break;
			}
		}
		return compilationUnitType;
	}

	/**
	 * @return Returns the shared instance.
	 */
	public static IWOLipsModel getSharedWOLipsModel() {
		if (wolipsModel == null)
			wolipsModel = new WOLipsModel();
		return wolipsModel;
	}

}
