package org.objectstyle.wolips.componenteditor.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.objectstyle.wolips.bindings.api.ApiModelException;
import org.objectstyle.wolips.bindings.api.Binding;
import org.objectstyle.wolips.bindings.api.Wo;
import org.objectstyle.wolips.componenteditor.ComponenteditorPlugin;
import org.objectstyle.wolips.locate.LocateException;
import org.objectstyle.wolips.templateeditor.TemplateEditor;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;

/**
 * <P>
 * This is the superclass of the actions that insert new components into the
 * component. Most of the guts of it are in the superclass here with the
 * configuration of the actions in the subclasses.
 * </P>
 * 
 * @author apl
 * 
 */

public abstract class InsertComponentAction extends InsertHtmlAndWodAction {
	private Wo _wo;
	
	public Wo getWo() {
		String componentName = getComponentName();
		if (_wo == null) {
			_wo = getWo(componentName);
		}
		return _wo;
	}
	
	/**
	 * <P>
	 * This method will return the Wo file from which parsed information can be
	 * derived about components to be inserted. Otherwise it will reutrn null.
	 * </P>
	 */
	protected Wo getWo(String componentName) {
		Wo wo = null;
		if (componentName != null) {
			TemplateEditor te = getTemplateEditor();
			if (null != te) {
				IFileEditorInput input = (IFileEditorInput) te.getEditorInput();
				IFile file = input.getFile();

				try {
					WodParserCache cache = WodParserCache.parser(file);
					wo = cache.getWo(componentName);
				} catch (LocateException le) {
					ComponenteditorPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, ComponenteditorPlugin.PLUGIN_ID, IStatus.OK, "unable to get the Wo for an edited component", le));
				} catch (CoreException ce) {
					ComponenteditorPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, ComponenteditorPlugin.PLUGIN_ID, IStatus.OK, "unable to get the Wo for an edited component", ce));
				} catch (ApiModelException ame) {
					ComponenteditorPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, ComponenteditorPlugin.PLUGIN_ID, IStatus.OK, "unable to get the Wo for an edited component", ame));
				}
			}
		}
		return wo;
	}

	protected IJavaProject getJavaProject() {
		IJavaProject javaProject = null;
		TemplateEditor te = getTemplateEditor();
		if (te != null) {
			IFileEditorInput input = (IFileEditorInput) te.getEditorInput();
			IFile file = input.getFile();
			if (file != null) {
				javaProject = JavaCore.create(file.getProject());
			}
		}
		return javaProject;
	}

	protected List<Binding> getRequiredBindings(String componentName) {
		List<Binding> requiredBindings = null;
		Wo wo = getWo(componentName);
		if (wo != null) {
			requiredBindings = wo.getRequiredBindings();
		}
		return requiredBindings;
	}

	/**
	 * <P>
	 * This is a standard suffix for the component names. For example, you might
	 * like your string components to generally have "String" at the end.
	 * </P>
	 */

	public abstract String getComponentInstanceNameSuffix();

	/**
	 * <P>
	 * This is the name of the component that will be inserted. Some examples of
	 * standard component named might be <TT>WOString</TT>, <TT>WOForm</TT>
	 * etc...
	 * </P>
	 */
	public abstract String getComponentName();

	protected InsertComponentSpecification getComponentSpecification() {
		InsertComponentSpecification ics = _componentSpecification;

		int results;
		if (ics == null) {
			ics = new InsertComponentSpecification(getComponentName());
			ics.setComponentInstanceNameSuffix(getComponentInstanceNameSuffix());

			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			InsertComponentDialogue dialog = new InsertComponentDialogue(window.getShell(), getJavaProject(), ics);
			results = dialog.open();
		} else {
			results = Window.OK;
		}

		if (results == Window.OK) {
			ics.setRequiredBindings(getRequiredBindings(ics.getComponentName()));

			if (!ics.isInline()) {
				ics.setTagName("webobject");
				Map<String, String> attributes = new HashMap<String, String>();
				attributes.put("name", ics.getComponentInstanceName());
				ics.setAttributes(attributes);
			}

			Wo wo = getWo(ics.getComponentName());
			if (wo != null) {
				ics.setComponentContent(wo.isComponentContent());
			}
		} else {
			ics = null;
		}

		return ics;
	}
}
