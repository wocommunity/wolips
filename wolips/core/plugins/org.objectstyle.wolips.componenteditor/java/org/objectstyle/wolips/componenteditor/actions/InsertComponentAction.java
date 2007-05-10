package org.objectstyle.wolips.componenteditor.actions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.objectstyle.wolips.componenteditor.ComponenteditorPlugin;
import org.objectstyle.wolips.core.resources.types.api.ApiModelException;
import org.objectstyle.wolips.core.resources.types.api.Binding;
import org.objectstyle.wolips.core.resources.types.api.Wo;
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

	protected Binding[] getRequiredBindings(String componentName) {
		Binding[] requiredBindings = null;
		Wo wo = getWo(componentName);
		if (wo != null) {
			requiredBindings = wo.getRequiredBindings();
		}
		return requiredBindings;
	}
	
	protected boolean canHaveComponentContent(String componentName) {
		boolean componentContent = false;
		Wo wo = getWo(componentName);
		if (wo != null) {
			componentContent = wo.getIsWocomponentcontent();
		}
		return componentContent;
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
		InsertComponentSpecification ics = new InsertComponentSpecification(getComponentName());
		ics.setComponentInstanceNameSuffix(getComponentInstanceNameSuffix());

		IWorkbenchWindow ww = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		InsertComponentDialogue dialog = new InsertComponentDialogue(ww.getShell(), ics);
		dialog.open();

		ics.setRequiredBindings(getRequiredBindings(ics.getComponentName()));

		if (!ics.isInline()) {
			ics.setTagName("webobject");
			Map<String, String> attributes = new HashMap<String, String>();
			attributes.put("name", ics.getComponentInstanceName());
			ics.setAttributes(attributes);
		}

		return ics;
	}
}
