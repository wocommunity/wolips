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
package org.objectstyle.wolips.core.plugin;
/**
 * @author mnolte
 */
public interface IWOLipsPluginConstants {
	//preferences
	/*
	public static final String PREF_MODEL_NAVIGATOR_FILTER =
		"org.objectstyle.wolips.preference.ModelNavigatorFilter";
	public static final String PREF_WO_NAVIGATOR_FILTER =
		"org.objectstyle.wolips.preference.WONavigatorFilter";
	public static final String PREF_PRODUCT_NAVIGATOR_FILTER =
		"org.objectstyle.wolips.preference.ProductNavigatorFilter";
	public static final String PREF_ANT_BUILD_FILE =
		"org.objectstyle.wolips.preference.AntBuildFile";
	public static final String PREF_RUN_WOBUILDER_ON_BUILD =
		"org.objectstyle.wolips.preference.RunWOBuilderOnBuild";
	public static final String PREF_OPEN_WOCOMPONENT_ACTION_INCLUDES_OPEN_HTML =
		"org.objectstyle.wolips.Preference.OpenWOComponentActionIncludesOpenHTML";
	public static final String PREF_SHOW_BUILD_OUTPUT =
		"org.objectstyle.wolips.Preference.ShowBuildOutput";
	public static final String PREF_RUN_ANT_AS_EXTERNAL_TOOL =
		"org.objectstyle.wolips.Preference.RunAntAsExternalTool";
	public static final String PREF_NS_PROJECT_SEARCH_PATH =
		"org.objectstyle.wolips.Preference.NSProjectSearch";
	public static final String PREF_REBUILD_WOBUILD_PROPERTIES_ON_NEXT_LAUNCH =
		"org.objectstyle.wolips.Preference.RebuildWOBuildPropertiesOnNextLaunch";
	public static final String PREF_LOG_LEVEL =
		"org.objectstyle.wolips.Preference.LogLevel";
	public static final String PREF_WOLIPS_VERSION_EARLY_STARTUP =
		"org.objectstyle.wolips.Preference.WOLipsVersionEarlyStartup";
	public static final String PREF_PBWO_PROJECT_UPDATE =
		"org.objectstyle.wolips.Preference.Update_PBWO_Project";
	public static final String PREF_PBWO_PROJECT_INCLUDED_WOAPP_RESOURCES =
		"org.objectstyle.wolips.Preference.PBWO_Project_Included_WOAPP_Resources";
	public static final String PREF_PBWO_PROJECT_EXCLUDED_WOAPP_RESOURCES =
		"org.objectstyle.wolips.Preference.PBWO_Project_Excluded_WOAPP_Resources";
	public static final String PREF_PBWO_PROJECT_INCLUDED_CLASSES =
		"org.objectstyle.wolips.Preference.PBWO_Project_Included_Classes";
	public static final String PREF_PBWO_PROJECT_EXCLUDED_CLASSES =
		"org.objectstyle.wolips.Preference.PBWO_Project_Excluded_Classes";
	public static final String PREF_LAUNCH_GLOBAL = "org.objectstyle.wolips.Preference.Launch_Global";
	*/
	// flle extensions and resource identifier
	public static final String EXT_PROJECT = "project";
	public static final String EXT_SUBPROJECT = "subproj";
	public static final String EXT_JAVA = "java";
	public static final String EXT_COMPONENT = "wo";
	public static final String EXT_API = "api";
	public static final String EXT_WOD = "wod";
	public static final String EXT_WOO = "woo";
	public static final String EXT_HTML = "html";
	public static final String EXT_EOMODEL = "eomodeld";
	public static final String EXT_EOMODEL_BACKUP = "eomodeld~";
	public static final String EXT_D2WMODEL = "d2wmodel";
	public static final String EXT_PROPERTIES = "properties";
	public static final String EXT_STRINGS = "strings";
	public static final String EXT_SRC = "src";
	public static final String EXT_FRAMEWORK = "framework";
	public static final String EXT_WOA = "woa";
	public static final String EXT_BUILD = "build";
	public static final String EXT_DIST = "dist";
	// webobjects project file lists
	public static final String RESOURCES_ID = "WOAPP_RESOURCES";
	public static final String CLASSES_ID = "CLASSES";
	public static final String COMPONENTS_ID = "WO_COMPONENTS";
	public static final String SUBPROJECTS_ID = "SUBPROJECTS";
	public static final String FRAMEWORKS_ID = "FRAMEWORKS";
	public static final String[] RESOURCE_IDENTIFIERS =
		{
			EXT_JAVA,
			EXT_COMPONENT,
			EXT_API,
			EXT_EOMODEL,
			EXT_STRINGS,
			EXT_SUBPROJECT,
			EXT_FRAMEWORK };
	public static final String[] LIST_IDENTIFIERS =
		{
			CLASSES_ID,
			COMPONENTS_ID,
			RESOURCES_ID,
			RESOURCES_ID,
			RESOURCES_ID,
			SUBPROJECTS_ID,
			FRAMEWORKS_ID };
	public static final String PROJECT_FILE_NAME = "PB.project";
	public static final String INCREMENTAL_BUILDER_ID =
		"org.objectstyle.wolips.incrementalbuilder";
	public static final String ANT_BUILDER_ID =
		"org.objectstyle.wolips.antbuilder";
	public static final String INCREMENTAL_FRAMEWORK_NATURE_ID =
		"org.objectstyle.wolips.incrementalframeworknature";
	public static final String ANT_FRAMEWORK_NATURE_ID =
		"org.objectstyle.wolips.antframeworknature";
	public static final String INCREMENTAL_APPLICATION_NATURE_ID =
		"org.objectstyle.wolips.incrementalapplicationnature";
	public static final String ANT_APPLICATION_NATURE_ID =
		"org.objectstyle.wolips.antapplicationnature";
	public static String WOGENERATOR_ID = "org.objectstyle.wolips.wogenerator";
	public static final String WO_APPLICATION_NATURE_OLD =
		"org.objectstyle.wolips.applicationnature";
	public static final String WO_FRAMEWORK_NATURE_OLD =
		"org.objectstyle.wolips.frameworknature";
	public static String WOFRAMEWORK_BUILDER_ID =
		"org.objectstyle.wolips.woframeworkbuilder";
	public static String WOAPPLICATION_BUILDER_ID =
		"org.objectstyle.wolips.woapplicationbuilder";
	// mandatory eclipse classpath variables
	public static final String[] WOLIPS_NATURES =
		{
			INCREMENTAL_FRAMEWORK_NATURE_ID,
			ANT_FRAMEWORK_NATURE_ID,
			INCREMENTAL_APPLICATION_NATURE_ID,
			ANT_APPLICATION_NATURE_ID,
			WO_APPLICATION_NATURE_OLD,
			WO_FRAMEWORK_NATURE_OLD };
	public static final String PLUGIN_ID = "org.objectstyle.wolips";
	public static final String ID_ELEMENT_CREATION_ACTION_SET =
		"org.objectstyle.wolips.wizards.ElementCreationActionSet";
	public static final String ID_BUILD_ACTION_SET =
		"org.objectstyle.wolips.ui.BuildActionSet";
	public static final String ID_Navigator = "org.objectstyle.wolips.ui.Navigator"; //$NON-NLS-1$
	public static final String ID_WONavigator = "org.objectstyle.wolips.ui.WONavigator"; //$NON-NLS-1$
	public static final String ID_ModelNavigator = "org.objectstyle.wolips.ui.ModelNavigator"; //$NON-NLS-1$
	public static final String ID_ProductNavigator = "org.objectstyle.wolips.ui.ProductNavigator"; //$NON-NLS-1$
	public static final String ID_AntNavigator = "org.eclipse.ui.externaltools.AntView"; //$NON-NLS-1$
	public static final String build_file_wounit = "build-wounit.xml";
	//local framework search for PB.project
	public static final String DefaultLocalFrameworkSearch =
		"$(NEXT_ROOT)$(LOCAL_LIBRARY_DIR)/Frameworks";
	public static String UserHomeClasspathVariable = "USER.HOME";
	public static String ProjectWonderHomeClasspathVariable = "PROJECT.WONDER.HOME";	
	
}
