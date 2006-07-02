package org.objectstyle.wolips.eomodeler.editors;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * A cell editor that manages a checkbox.
 * The cell editor's value is a boolean.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * <p>
 * Note that this implementation simply fakes it and does does not create
 * any new controls. The mere activation of this editor means that the value
 * of the check box is being toggled by the end users; the listener method
 * <code>applyEditorValue</code> is immediately called to signal the change.
 * </p>
 */
public class TriStateCellEditor extends CellEditor {

  /**
   * The checkbox value.
   */
  /* package */
  private Boolean value = null;

  /**
   * Default CheckboxCellEditor style
   */
  private static final int defaultStyle = SWT.NONE;

  /**
   * Creates a new checkbox cell editor with no control
   * @since 2.1
   */
  public TriStateCellEditor() {
    setStyle(defaultStyle);
  }

  /**
   * Creates a new checkbox cell editor parented under the given control.
   * The cell editor value is a boolean value, which is initially <code>false</code>. 
   * Initially, the cell editor has no cell validator.
   *
   * @param parent the parent control
   */
  public TriStateCellEditor(Composite parent) {
    this(parent, defaultStyle);
  }

  /**
   * Creates a new checkbox cell editor parented under the given control.
   * The cell editor value is a boolean value, which is initially <code>false</code>. 
   * Initially, the cell editor has no cell validator.
   *
   * @param parent the parent control
   * @param style the style bits
   * @since 2.1
   */
  public TriStateCellEditor(Composite parent, int style) {
    super(parent, style);
  }

  /**
   * The <code>CheckboxCellEditor</code> implementation of
   * this <code>CellEditor</code> framework method simulates
   * the toggling of the checkbox control and notifies
   * listeners with <code>ICellEditorListener.applyEditorValue</code>.
   */
  public void activate() {
    if (value == null) {
      value = Boolean.TRUE;
    }
    else if (value == Boolean.TRUE) {
      value = Boolean.FALSE;
    }
    else if (value == Boolean.FALSE) {
      value = null;
    }
    fireApplyEditorValue();
  }

  /**
   * The <code>CheckboxCellEditor</code> implementation of
   * this <code>CellEditor</code> framework method does
   * nothing and returns <code>null</code>.
   */
  protected Control createControl(Composite parent) {
    return null;
  }

  /**
   * The <code>CheckboxCellEditor</code> implementation of
   * this <code>CellEditor</code> framework method returns
   * the checkbox setting wrapped as a <code>Boolean</code>.
   *
   * @return the Boolean checkbox value
   */
  protected Object doGetValue() {
    return value;
  }

  /* (non-Javadoc)
   * Method declared on CellEditor.
   */
  protected void doSetFocus() {
    // Ignore
  }

  /**
   * The <code>CheckboxCellEditor</code> implementation of
   * this <code>CellEditor</code> framework method accepts
   * a value wrapped as a <code>Boolean</code>.
   *
   * @param value a Boolean value
   */
  protected void doSetValue(Object _value) {
    this.value = (Boolean) _value;
  }
}
