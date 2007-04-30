package tk.eclipse.plugin.htmleditor.gefutils;

import java.text.MessageFormat;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * The base class for selection dialog property descriptors.
 * 
 * @author Naoki Takezoe
 */
public abstract class AbstractDialogPropertyDescriptor extends PropertyDescriptor {

	public AbstractDialogPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}
	
	public CellEditor createPropertyEditor(Composite parent) {
		ValueCellEditor editor = new ValueCellEditor(parent);
		if (getValidator() != null){
			editor.setValidator(getValidator());
		}
		return editor;
	}
	
	protected abstract Object openDialogBox(Object value, Control cellEditorWindow);
	
	protected class ValueCellEditor extends CellEditor {

		private Text text;
		private Composite editor;
		private Button button;
		private boolean isSelection = false;
		private boolean isDeleteable = false;
		private boolean isSelectable = false;
		private Object value = null;
		
		private class DialogCellLayout extends Layout {
			public void layout(Composite editor, boolean force) {
				Rectangle bounds = editor.getClientArea();
				Point size = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
				if (text != null){
					text.setBounds(0, 0, bounds.width-size.x, bounds.height);
				}
				button.setBounds(bounds.width-size.x, 0, size.x, bounds.height);
			}
			public Point computeSize(Composite editor, int wHint, int hHint, boolean force) {
				if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT){
					return new Point(wHint, hHint);
				}
				Point contentsSize = text.computeSize(SWT.DEFAULT, SWT.DEFAULT, force); 
				Point buttonSize   = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
				
				Point result = new Point(buttonSize.x,Math.max(contentsSize.y, buttonSize.y));
				return result;
			}
		}
		
		public ValueCellEditor(Composite parent) {
			super(parent, SWT.NONE);
		}
		
		protected Control createControl(Composite parent) {
	
			Font font = parent.getFont();
			Color bg  = parent.getBackground();
	
			editor = new Composite(parent, getStyle());
			editor.setFont(font);
			editor.setBackground(bg);
			editor.setLayout(new DialogCellLayout());
			
			text = new Text(editor,SWT.NULL);
		    text.addKeyListener(new KeyAdapter() {
		    	public void keyReleased(KeyEvent e) {
		    		if (e.character == '\u001b') { // Escape
						fireCancelEditor();
					} else if (e.character == '\r'){ // Enter
						Object newValue = text.getText();
						updateValue(newValue);
					}
		    	}
		    	public void keyPressed(KeyEvent e) {
					checkSelection();
					checkDeleteable();
					checkSelectable();
		    	}
		    });
			text.addMouseListener(new MouseAdapter() {
				public void mouseUp(MouseEvent e) {
					checkSelection();
					checkDeleteable();
					checkSelectable();
				}
			});
			text.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					Object newValue = text.getText();
					updateValue(newValue);
				}
			});
			text.setFont(parent.getFont());
			text.setBackground(parent.getBackground());
			
			button = new Button(editor, SWT.DOWN);
			button.setText("...");
			button.setFont(font);
			button.addKeyListener(new KeyAdapter() {
				public void keyReleased(KeyEvent e) {
					if (e.character == '\u001b') { // Escape
						fireCancelEditor();
					}
				}
			});
			button.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					Object newValue = openDialogBox(editor);
					if (newValue != null) {
						updateValue(newValue);
					}
				}
			});
			
			setValueValid(true);
			updateContents(value);
			
			return editor;
		}
		
		private void updateValue(Object newValue){
			if (newValue != null) {
				boolean newValidState = isCorrect(newValue);
				if (newValidState) {
					markDirty();
					doSetValue(newValue);
				} else {
					setErrorMessage(MessageFormat.format(getErrorMessage(), new Object[] { newValue.toString()}));
				}
				fireApplyEditorValue();
			}
		}
		
		protected Object doGetValue() {
			return value;
		}

		protected void doSetFocus() {
		    button.setFocus();
//			text.setFocus();
//			text.selectAll();
			checkSelection();
			checkDeleteable();
			checkSelectable();
		}

		protected void doSetValue(Object value) {
			this.value = value;
			updateContents(value);
		}
		
		
		private void checkDeleteable() {
			boolean oldIsDeleteable = isDeleteable;
			isDeleteable = isDeleteEnabled();
			if (oldIsDeleteable != isDeleteable) {
				fireEnablementChanged(DELETE);
			}
		}

		private void checkSelectable() {
			boolean oldIsSelectable = isSelectable;
			isSelectable = isSelectAllEnabled();
			if (oldIsSelectable != isSelectable) {
				fireEnablementChanged(SELECT_ALL);
			}
		}

		private void checkSelection() {
			boolean oldIsSelection = isSelection;
			isSelection = text.getSelectionCount() > 0;
			if (oldIsSelection != isSelection) {
				fireEnablementChanged(COPY);
				fireEnablementChanged(CUT);
			}
		}
		
		public boolean isCopyEnabled() {
			if (text == null || text.isDisposed())
				return false;
			return text.getSelectionCount() > 0;
		}

		public boolean isCutEnabled() {
			if (text == null || text.isDisposed())
				return false;
			return text.getSelectionCount() > 0;
		}

		public boolean isDeleteEnabled() {
			if (text == null || text.isDisposed())
				return false;
			return text.getSelectionCount() > 0 || text.getCaretPosition() < text.getCharCount();
		}

		public boolean isPasteEnabled() {
			if (text == null || text.isDisposed())
				return false;
			return true;
		}

		public boolean isSaveAllEnabled() {
			if (text == null || text.isDisposed())
				return false;
			return true;
		}

		public boolean isSelectAllEnabled() {
			if (text == null || text.isDisposed())
				return false;
			return text.getCharCount() > 0;
		}

		public void performCopy() {
			text.copy();
		}

		public void performCut() {
			text.cut();
			checkSelection(); 
			checkDeleteable();
			checkSelectable();
		}

		public void performDelete() {
			if (text.getSelectionCount() > 0)
				// remove the contents of the current selection
				text.insert(""); //$NON-NLS-1$
			else {
				// remove the next character
				int pos = text.getCaretPosition();
				if (pos < text.getCharCount()) {
					text.setSelection(pos, pos + 1);
					text.insert(""); //$NON-NLS-1$
				}
			}
			checkSelection(); 
			checkDeleteable();
			checkSelectable();
		}

		public void performPaste() {
			text.paste();
			checkSelection(); 
			checkDeleteable();
			checkSelectable();
		}

		public void performSelectAll() {
			text.selectAll();
			checkSelection(); 
			checkDeleteable();
		}
		
		protected Object openDialogBox(Control cellEditorWindow){
			return AbstractDialogPropertyDescriptor.this.openDialogBox(value, cellEditorWindow);
		}
		
		protected void updateContents(Object value) {
			if (this.text == null){
				return;
			}	
			String text = "";//$NON-NLS-1$
			if (value != null){
				text = value.toString();
			}
			this.text.setText(text);
		}
	}

}
