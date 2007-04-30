package tk.eclipse.plugin.htmleditor.gefutils;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * The property descriptor that edits the text by the textarea dialog.
 * 
 * @author Naoki Takezoe
 */
public class TextAreaPropertyDescriptor extends
		AbstractDialogPropertyDescriptor {
	
	public TextAreaPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}

	protected Object openDialogBox(Object obj, Control cellEditorWindow) {
		String value = (String)obj;
		TextAreaDialog dialog = new TextAreaDialog(cellEditorWindow.getShell(), value);
		if(dialog.open()==TextAreaDialog.OK){
			return dialog.getResult();
		}
		return null;
	}
	
	private class TextAreaDialog extends Dialog {
	
		private Text text;
		private String result;
		private String initValue;
		
		public TextAreaDialog(Shell parent, String initValue){
			super(parent);
			this.initValue = initValue;
			setShellStyle(getShellStyle()|SWT.RESIZE);
		}
		
		protected Point getInitialSize() {
			return new Point(350, 300);
		}
		
		protected Control createDialogArea(Composite parent) {
			getShell().setText(getDisplayName());
			
			Composite composite = new Composite(parent, SWT.NULL);
			composite.setLayout(new GridLayout());
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			text = new Text(composite, SWT.MULTI|SWT.BORDER|SWT.H_SCROLL|SWT.V_SCROLL);
			text.setLayoutData(new GridData(GridData.FILL_BOTH));
			text.setText(this.initValue);
			
			return composite;
		}

		protected void okPressed() {
			result = this.text.getText();
			super.okPressed();
		}
		
		public String getResult(){
			return this.result;
		}
		
	}

}
