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
	
	@Override
  public CellEditor createPropertyEditor(Composite parent) {
		ValueCellEditor editor = new ValueCellEditor(parent);
		if (getValidator() != null){
			editor.setValidator(getValidator());
		}
		return editor;
	}
	
	protected abstract Object openDialogBox(Object value, Control cellEditorWindow);
	
	protected class ValueCellEditor extends CellEditor {

		private Text _text;
		private Composite _editor;
		private Button _button;
		private boolean _isSelection = false;
		private boolean _isDeleteable = false;
		private boolean _isSelectable = false;
		private Object _value = null;
		
		private class DialogCellLayout extends Layout {
			@Override
      public void layout(Composite editor, boolean force) {
				Rectangle bounds = editor.getClientArea();
				Point size = _button.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
				if (_text != null){
					_text.setBounds(0, 0, bounds.width-size.x, bounds.height);
				}
				_button.setBounds(bounds.width-size.x, 0, size.x, bounds.height);
			}
			@Override
      public Point computeSize(Composite editor, int wHint, int hHint, boolean force) {
				if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT){
					return new Point(wHint, hHint);
				}
				Point contentsSize = _text.computeSize(SWT.DEFAULT, SWT.DEFAULT, force); 
				Point buttonSize   = _button.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
				
				Point result = new Point(buttonSize.x,Math.max(contentsSize.y, buttonSize.y));
				return result;
			}
		}
		
		public ValueCellEditor(Composite parent) {
			super(parent, SWT.NONE);
		}
		
		@Override
    protected Control createControl(Composite parent) {
	
			Font font = parent.getFont();
			Color bg  = parent.getBackground();
	
			_editor = new Composite(parent, getStyle());
			_editor.setFont(font);
			_editor.setBackground(bg);
			_editor.setLayout(new DialogCellLayout());
			
			_text = new Text(_editor,SWT.NULL);
		    _text.addKeyListener(new KeyAdapter() {
		    	@Override
          public void keyReleased(KeyEvent e) {
		    		if (e.character == '\u001b') { // Escape
						fireCancelEditor();
					} else if (e.character == '\r'){ // Enter
						Object newValue = _text.getText();
						updateValue(newValue);
					}
		    	}
		    	@Override
          public void keyPressed(KeyEvent e) {
					checkSelection();
					checkDeleteable();
					checkSelectable();
		    	}
		    });
			_text.addMouseListener(new MouseAdapter() {
				@Override
        public void mouseUp(MouseEvent e) {
					checkSelection();
					checkDeleteable();
					checkSelectable();
				}
			});
			_text.addFocusListener(new FocusAdapter() {
				@Override
        public void focusLost(FocusEvent e) {
					Object newValue = _text.getText();
					updateValue(newValue);
				}
			});
			_text.setFont(parent.getFont());
			_text.setBackground(parent.getBackground());
			
			_button = new Button(_editor, SWT.DOWN);
			_button.setText("...");
			_button.setFont(font);
			_button.addKeyListener(new KeyAdapter() {
				@Override
        public void keyReleased(KeyEvent e) {
					if (e.character == '\u001b') { // Escape
						fireCancelEditor();
					}
				}
			});
			_button.addSelectionListener(new SelectionAdapter() {
				@Override
        public void widgetSelected(SelectionEvent event) {
					Object newValue = openDialogBox(_editor);
					if (newValue != null) {
						updateValue(newValue);
					}
				}
			});
			
			setValueValid(true);
			updateContents(_value);
			
			return _editor;
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
		
		@Override
    protected Object doGetValue() {
			return _value;
		}

		@Override
    protected void doSetFocus() {
		    _button.setFocus();
//			text.setFocus();
//			text.selectAll();
			checkSelection();
			checkDeleteable();
			checkSelectable();
		}

		@Override
    protected void doSetValue(Object value) {
			this._value = value;
			updateContents(value);
		}
		
		
		private void checkDeleteable() {
			boolean oldIsDeleteable = _isDeleteable;
			_isDeleteable = isDeleteEnabled();
			if (oldIsDeleteable != _isDeleteable) {
				fireEnablementChanged(DELETE);
			}
		}

		private void checkSelectable() {
			boolean oldIsSelectable = _isSelectable;
			_isSelectable = isSelectAllEnabled();
			if (oldIsSelectable != _isSelectable) {
				fireEnablementChanged(SELECT_ALL);
			}
		}

		private void checkSelection() {
			boolean oldIsSelection = _isSelection;
			_isSelection = _text.getSelectionCount() > 0;
			if (oldIsSelection != _isSelection) {
				fireEnablementChanged(COPY);
				fireEnablementChanged(CUT);
			}
		}
		
		@Override
    public boolean isCopyEnabled() {
			if (_text == null || _text.isDisposed())
				return false;
			return _text.getSelectionCount() > 0;
		}

		@Override
    public boolean isCutEnabled() {
			if (_text == null || _text.isDisposed())
				return false;
			return _text.getSelectionCount() > 0;
		}

		@Override
    public boolean isDeleteEnabled() {
			if (_text == null || _text.isDisposed())
				return false;
			return _text.getSelectionCount() > 0 || _text.getCaretPosition() < _text.getCharCount();
		}

		@Override
    public boolean isPasteEnabled() {
			if (_text == null || _text.isDisposed())
				return false;
			return true;
		}

		public boolean isSaveAllEnabled() {
			if (_text == null || _text.isDisposed())
				return false;
			return true;
		}

		@Override
    public boolean isSelectAllEnabled() {
			if (_text == null || _text.isDisposed())
				return false;
			return _text.getCharCount() > 0;
		}

		@Override
    public void performCopy() {
			_text.copy();
		}

		@Override
    public void performCut() {
			_text.cut();
			checkSelection(); 
			checkDeleteable();
			checkSelectable();
		}

		@Override
    public void performDelete() {
			if (_text.getSelectionCount() > 0)
				// remove the contents of the current selection
				_text.insert(""); //$NON-NLS-1$
			else {
				// remove the next character
				int pos = _text.getCaretPosition();
				if (pos < _text.getCharCount()) {
					_text.setSelection(pos, pos + 1);
					_text.insert(""); //$NON-NLS-1$
				}
			}
			checkSelection(); 
			checkDeleteable();
			checkSelectable();
		}

		@Override
    public void performPaste() {
			_text.paste();
			checkSelection(); 
			checkDeleteable();
			checkSelectable();
		}

		@Override
    public void performSelectAll() {
			_text.selectAll();
			checkSelection(); 
			checkDeleteable();
		}
		
		protected Object openDialogBox(Control cellEditorWindow){
			return AbstractDialogPropertyDescriptor.this.openDialogBox(_value, cellEditorWindow);
		}
		
		protected void updateContents(Object value) {
			if (this._text == null){
				return;
			}	
			String text = "";//$NON-NLS-1$
			if (value != null){
				text = value.toString();
			}
			this._text.setText(text);
		}
	}

}
