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
public class OpenSourceAction extends ActionOnIResource {

	public void run(IAction action) {
		if (actionResource() != null) {
			String fileName = actionResource().getName();
			if(fileName.endsWith(".wod"))
			fileName = fileName.substring(0, fileName.length() - 4);
			if(fileName.endsWith(".wo"))
						fileName = fileName.substring(0, fileName.length() - 3);

			ArrayList list = new ArrayList();
			this.findFilesInResourceByName(list, project(), fileName + ".java");
			for (int i = 0; i < list.size(); i++) {
				IResource resource = (IResource) list.get(i);
				if ((resource != null)
					&& (resource.getType() == IResource.FILE))
					open((IFile) resource);
			}
		}
	}
}
