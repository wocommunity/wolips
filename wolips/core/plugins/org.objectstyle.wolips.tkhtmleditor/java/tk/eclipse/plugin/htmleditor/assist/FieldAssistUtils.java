package tk.eclipse.plugin.htmleditor.assist;

import java.util.HashMap;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.swt.layout.GridLayout;

/**
 * Provides utility methods about the field assist.
 * 
 * @author Naoki Takezoe
 */
public class FieldAssistUtils {
	
	private static HashMap<IJavaProject, ICompilationUnit> unitMap = new HashMap<IJavaProject, ICompilationUnit>();
	
	/**
	 * Set contents of the compilation unit to the translated jsp text.
	 *
	 * @param unit the ICompilationUnit on which to set the buffer contents
	 * @param value Java source code
	 */	
	public static void setContentsToCU(ICompilationUnit unit, String value){
		if (unit == null)
			return;

		synchronized (unit) {
			IBuffer buffer;
			try {

				buffer = unit.getBuffer();
			}
			catch (JavaModelException e) {
				e.printStackTrace();
				buffer = null;
			}

			if (buffer != null)
				buffer.setContents(value);
		}
	}
	
	/**
	 * Creates the <code>ICompilationUnit</code> to use temporary.
	 * 
	 * @param project the java project
	 * @return the temporary <code>ICompilationUnit</code>
	 * @throws JavaModelException
	 */
	public synchronized static ICompilationUnit getTemporaryCompilationUnit(
			IJavaProject project) throws JavaModelException {
		
		if(unitMap.get(project) != null){
			return unitMap.get(project);
		}
		
		IPackageFragment root = project.getPackageFragments()[0];
		ICompilationUnit unit = root.getCompilationUnit("_xxx.java").getWorkingCopy(
				new NullProgressMonitor());
		
		unitMap.put(project, unit);
		
		return unit;
	}
	
	public static class ContentProposalImpl implements IContentProposal {
		
		private String content;
		private int position;
		
		public ContentProposalImpl(String content, int position){
			this.content = content;
			this.position = position;
		}
		
		public String getContent() {
			return content.substring(position);
		}

		public int getCursorPosition() {
			return content.length() - position;
		}

		public String getDescription() {
			return null;
		}

		public String getLabel() {
			return content;
		}
	}
	
//	/**
//	 * Creates the <code>Composite</code> for the fields which don't need decoration.
//	 * 
//	 * @param parent the parent composite
//	 * @param white whether it should paint out the background
//	 * @return the created <code>Composite</code> object
//	 */
//	public static Composite createNullDecoratedPanel(Composite parent, boolean white){
//		Composite composite = new Composite(parent, SWT.NULL);
//		GridLayout layout = createNoMarginGridLayout();
//		layout.horizontalSpacing = 0;
//		layout.verticalSpacing = 0;
//		composite.setLayout(layout);
//		if(white){
//			composite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
//		}
//		Label space = new Label(composite, SWT.NULL);
//		GridData gd = new GridData();
//		gd.widthHint = 5;
//		space.setLayoutData(gd);
//		if(white){
//			space.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
//		}
//		return composite;
//	}
	
	/**
	 * Creates <code>GridLayout</code> that has no margin.
	 * 
	 * @return created <code>GridLayout</code> that has no margin
	 */
	public static GridLayout createNoMarginGridLayout(){
		GridLayout layout = new GridLayout(2, false);
		layout.marginBottom = 0;
		layout.marginTop = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		return layout;
	}	
	
}
