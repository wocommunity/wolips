package tk.eclipse.plugin.htmleditor.gefutils;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import tk.eclipse.plugin.htmleditor.HTMLUtil;

/**
 * This is PropertyDescriptor which can input a class name directry
 * and select from JDT class selection dialog.
 * 
 * @author takezoe
 */
public class ClassSelectPropertyDescriptor extends AbstractDialogPropertyDescriptor {

	/**
	 * @param id
	 * @param displayName
	 */
	public ClassSelectPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}
	
	@Override
  protected Object openDialogBox(Object obj, Control cellEditorWindow) {
		IEditorPart editorPart = HTMLUtil.getActiveEditor();
		
		IFileEditorInput input = (IFileEditorInput)editorPart.getEditorInput();
		IJavaProject project = JavaCore.create(input.getFile().getProject());
		
		return HTMLUtil.openClassSelectDialog(project, cellEditorWindow);
	}
}
