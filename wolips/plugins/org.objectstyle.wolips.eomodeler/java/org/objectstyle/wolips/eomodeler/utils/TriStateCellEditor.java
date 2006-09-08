/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.eomodeler.utils;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * A cell editor that manages a checkbox. The cell editor's value is a boolean.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * <p>
 * Note that this implementation simply fakes it and does does not create any
 * new controls. The mere activation of this editor means that the value of the
 * check box is being toggled by the end users; the listener method
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
	 * 
	 * @since 2.1
	 */
	public TriStateCellEditor() {
		setStyle(defaultStyle);
	}

	/**
	 * Creates a new checkbox cell editor parented under the given control. The
	 * cell editor value is a boolean value, which is initially
	 * <code>false</code>. Initially, the cell editor has no cell validator.
	 * 
	 * @param parent
	 *            the parent control
	 */
	public TriStateCellEditor(Composite parent) {
		this(parent, defaultStyle);
	}

	/**
	 * Creates a new checkbox cell editor parented under the given control. The
	 * cell editor value is a boolean value, which is initially
	 * <code>false</code>. Initially, the cell editor has no cell validator.
	 * 
	 * @param parent
	 *            the parent control
	 * @param style
	 *            the style bits
	 * @since 2.1
	 */
	public TriStateCellEditor(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * The <code>CheckboxCellEditor</code> implementation of this
	 * <code>CellEditor</code> framework method simulates the toggling of the
	 * checkbox control and notifies listeners with
	 * <code>ICellEditorListener.applyEditorValue</code>.
	 */
	public void activate() {
		if (value == null) {
			value = Boolean.TRUE;
		} else if (value == Boolean.TRUE) {
			value = Boolean.FALSE;
		} else if (value == Boolean.FALSE) {
			value = null;
		}
		fireApplyEditorValue();
	}

	/**
	 * The <code>CheckboxCellEditor</code> implementation of this
	 * <code>CellEditor</code> framework method does nothing and returns
	 * <code>null</code>.
	 */
	protected Control createControl(Composite parent) {
		return null;
	}

	/**
	 * The <code>CheckboxCellEditor</code> implementation of this
	 * <code>CellEditor</code> framework method returns the checkbox setting
	 * wrapped as a <code>Boolean</code>.
	 * 
	 * @return the Boolean checkbox value
	 */
	protected Object doGetValue() {
		return value;
	}

	/*
	 * (non-Javadoc) Method declared on CellEditor.
	 */
	protected void doSetFocus() {
		// Ignore
	}

	/**
	 * The <code>CheckboxCellEditor</code> implementation of this
	 * <code>CellEditor</code> framework method accepts a value wrapped as a
	 * <code>Boolean</code>.
	 * 
	 * @param value
	 *            a Boolean value
	 */
	protected void doSetValue(Object _value) {
		this.value = (Boolean) _value;
	}
}
