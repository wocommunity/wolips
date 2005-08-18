package org.objectstyle.wolips.wodclipse.mpe;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiEditorInput;
import org.objectstyle.wolips.wodclipse.WodclipsePlugin;
import org.objectstyle.wolips.wodclipse.api.ApiEditorInput;
import org.objectstyle.wolips.workbenchutilities.WorkbenchUtilitiesPlugin;

public class ComponentEditorInput extends MultiEditorInput {

	public ComponentEditorInput(String[] editorIDs, IEditorInput[] innerEditors) {
		super(editorIDs, innerEditors);
	}

	/*
	 * may return null
	 */
	public static ComponentEditorInput createWithDotJava(IFile file) {
		IProject project = file.getProject();
		String ids[] = null;
		IEditorInput allInput[] = null;
		String fileName = file.getName().substring(0,
				file.getName().length() - 5);
		List htmlResources = WorkbenchUtilitiesPlugin
				.findResourcesInProjectByNameAndExtensions(project, fileName,
						new String[] { "html" }, false);
		if (htmlResources == null || htmlResources.size() != 1) {
			return null;
		}
		List wodResources = WorkbenchUtilitiesPlugin
				.findResourcesInProjectByNameAndExtensions(project, fileName,
						new String[] { "wod" }, false);

		if (wodResources == null || wodResources.size() != 1) {
			return null;
		}
		List apiResources = WorkbenchUtilitiesPlugin
				.findResourcesInProjectByNameAndExtensions(project, fileName,
						new String[] { "api" }, false);

		if (apiResources == null || apiResources.size() != 1) {
			ids = new String[3];
			allInput = new IEditorInput[3];
		} else {
			ids = new String[4];
			allInput = new IEditorInput[4];
		}
		ids[0] = JavaUI.ID_CU_EDITOR;
		allInput[0] = new FileEditorInput(file);
		ids[1] = WodclipsePlugin.HTMLEditorID;
		allInput[1] = new FileEditorInput(((IFile) htmlResources.get(0)));
		ids[2] = WodclipsePlugin.WODEditorID;
		allInput[2] = new FileEditorInput(((IFile) wodResources.get(0)));
		if (apiResources != null && apiResources.size() == 1) {
			ids[3] = WodclipsePlugin.ApiEditorID;
			allInput[3] = new ApiEditorInput(((IFile) apiResources.get(0)));
		}
		ComponentEditorInput input = new ComponentEditorInput(ids, allInput);
		
		return input;
	}
	
	public static ComponentEditorInput createWithDotHtml(IFile file) {
		IProject project = file.getProject();
		String javaFileName = file.getName().substring(0,
				file.getName().length() - 5);
		List javaResources = WorkbenchUtilitiesPlugin
				.findResourcesInProjectByNameAndExtensions(project, javaFileName,
						new String[] { "java" }, false);
		if (javaResources == null || javaResources.size() != 1) {
			return null;
		}
		IFile javaFile = (IFile)javaResources.get(0);
		String htmlFileName = javaFile.getName().substring(0,
				javaFile.getName().length() - 5);
		List htmlResources = WorkbenchUtilitiesPlugin
				.findResourcesInProjectByNameAndExtensions(project, htmlFileName,
						new String[] { "html" }, false);
		if (htmlResources == null || htmlResources.size() != 1) {
			return null;
		}
		IFile htmlFile = (IFile)htmlResources.get(0);
		if(htmlFile.getLocation().equals(file.getLocation())) {
			return createWithDotJava(javaFile);
		}
		return null;
	}
	public static ComponentEditorInput createWithDotWod(IFile file) {
		IProject project = file.getProject();
		String javaFileName = file.getName().substring(0,
				file.getName().length() - 4);
		List javaResources = WorkbenchUtilitiesPlugin
				.findResourcesInProjectByNameAndExtensions(project, javaFileName,
						new String[] { "java" }, false);
		if (javaResources == null || javaResources.size() != 1) {
			return null;
		}
		IFile javaFile = (IFile)javaResources.get(0);
		String htmlFileName = javaFile.getName().substring(0,
				javaFile.getName().length() - 5);
		List htmlResources = WorkbenchUtilitiesPlugin
				.findResourcesInProjectByNameAndExtensions(project, htmlFileName,
						new String[] { "wod" }, false);
		if (htmlResources == null || htmlResources.size() != 1) {
			return null;
		}
		IFile htmlFile = (IFile)htmlResources.get(0);
		if(htmlFile.getLocation().equals(file.getLocation())) {
			return createWithDotJava(javaFile);
		}
		return null;
	}
	public static ComponentEditorInput createWithDotApi(IFile file) {
		IProject project = file.getProject();
		String javaFileName = file.getName().substring(0,
				file.getName().length() - 4);
		List javaResources = WorkbenchUtilitiesPlugin
				.findResourcesInProjectByNameAndExtensions(project, javaFileName,
						new String[] { "java" }, false);
		if (javaResources == null || javaResources.size() != 1) {
			return null;
		}
		IFile javaFile = (IFile)javaResources.get(0);
		String htmlFileName = javaFile.getName().substring(0,
				javaFile.getName().length() - 5);
		List htmlResources = WorkbenchUtilitiesPlugin
				.findResourcesInProjectByNameAndExtensions(project, htmlFileName,
						new String[] { "api" }, false);
		if (htmlResources == null || htmlResources.size() != 1) {
			return null;
		}
		IFile htmlFile = (IFile)htmlResources.get(0);
		if(htmlFile.getLocation().equals(file.getLocation())) {
			return createWithDotJava(javaFile);
		}
		return null;
	}
	public static ComponentEditorInput createWithDotWoo(IFile file) {
		IProject project = file.getProject();
		String javaFileName = file.getName().substring(0,
				file.getName().length() - 4);
		List javaResources = WorkbenchUtilitiesPlugin
				.findResourcesInProjectByNameAndExtensions(project, javaFileName,
						new String[] { "java" }, false);
		if (javaResources == null || javaResources.size() != 1) {
			return null;
		}
		IFile javaFile = (IFile)javaResources.get(0);
		String htmlFileName = javaFile.getName().substring(0,
				javaFile.getName().length() - 5);
		List htmlResources = WorkbenchUtilitiesPlugin
				.findResourcesInProjectByNameAndExtensions(project, htmlFileName,
						new String[] { "woo" }, false);
		if (htmlResources == null || htmlResources.size() != 1) {
			return null;
		}
		IFile htmlFile = (IFile)htmlResources.get(0);
		if(htmlFile.getLocation().equals(file.getLocation())) {
			return createWithDotJava(javaFile);
		}
		return null;
	}
}
