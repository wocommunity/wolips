package org.objectstyle.wolips.actions;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;

/**
 * @author uli
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class OpenComponentAction extends ActionOnIResource {

	public void run(IAction action) {
		if (actionResource() != null) {
			String fileName = actionResource().getName();
			fileName = fileName.substring(0, fileName.length() - 5);
			ArrayList list = new ArrayList();
			this.findFilesInResourceByName(list, project(), fileName + ".wod");
			for (int i = 0; i < list.size(); i++) {
				IResource resource = (IResource) list.get(i);
				if ((resource != null)
					&& (resource.getType() == IResource.FILE))
					open((IFile) resource);
			}
		}
	}
}