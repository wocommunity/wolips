package org.objectstyle.wolips;

/**
 * @author mnolte
 */
public interface IWOLipsPluginConstants {

	// file extensions and resource identifier
	public static final String SUBPROJECT = "subproj";
	public static final String CLASS = "java";
	public static final String COMPONENT = "wo";
	public static final String API = "api";
	public static final String WOD = "wod";
	public static final String HTML = "html";
	public static final String EOMODEL = "eomodeld";
	public static final String PROPERTIES = "properties";
	public static final String FRAMEWORK = "framework";
	public static final String SRC = "src";

	// webobjects project file lists
	public static final String RESOURCES_ID = "WOAPP_RESOURCES";
	public static final String CLASSES_ID = "CLASSES";
	public static final String COMPONENTS_ID = "WO_COMPONENTS";
	public static final String SUBPROJECTS_ID = "SUBPROJECTS";
	public static final String FRAMEWORKS_ID = "FRAMEWORKS";


	
	public static final String[] RESOURCE_IDENTIFIERS =
		{ CLASS, COMPONENT, API, EOMODEL, PROPERTIES, SUBPROJECT, FRAMEWORK };
		
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
	public static final String WO_APPLICATION_NATURE = "org.objectstyle.wolips.applicationnature";
	public static final String WO_FRAMEWORK_NATURE = "org.objectstyle.wolips.frameworknature";
	
}
