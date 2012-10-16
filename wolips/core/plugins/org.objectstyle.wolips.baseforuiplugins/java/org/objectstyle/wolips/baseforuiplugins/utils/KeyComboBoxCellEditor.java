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
package org.objectstyle.wolips.baseforuiplugins.utils;

import java.text.MessageFormat;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * A cell editor that presents a list of items in a combo box. The cell editor's
 * value is the zero-based index of the selected item.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 */
public class KeyComboBoxCellEditor extends CellEditor {

	/**
	 * The list of items to present in the combo box.
	 */
	private String[] items;

	/**
	 * The zero-based index of the selected item.
	 */
	int selection;

	/**
	 * The custom combo box control.
	 */
	IHateCCombo comboBox;

	/**
	 * Default ComboBoxCellEditor style
	 */
	private static final int defaultStyle = SWT.NONE;

	private long lastKeyTime;

	private StringBuffer searchStringBuffer;

	/**
	 * Creates a new cell editor with no control and no st of choices.
	 * Initially, the cell editor has no cell validator.
	 * 
	 * @since 2.1
	 * @see CellEditor#setStyle
	 * @see CellEditor#create
	 * @see ComboBoxCellEditor#setItems
	 * @see CellEditor#dispose
	 */
	public KeyComboBoxCellEditor() {
		setStyle(defaultStyle | SWT.FLAT);
		searchStringBuffer = new StringBuffer();
	}

	/**
	 * Creates a new cell editor with a combo containing the given list of
	 * choices and parented under the given control. The cell editor value is
	 * the zero-based index of the selected item. Initially, the cell editor has
	 * no cell validator and the first item in the list is selected.
	 * 
	 * @param parent
	 *            the parent control
	 * @param items
	 *            the list of strings for the combo box
	 */
	public KeyComboBoxCellEditor(Composite parent, String[] items) {
		this(parent, items, defaultStyle);
	}

	/**
	 * Creates a new cell editor with a combo containing the given list of
	 * choices and parented under the given control. The cell editor value is
	 * the zero-based index of the selected item. Initially, the cell editor has
	 * no cell validator and the first item in the list is selected.
	 * 
	 * @param parent
	 *            the parent control
	 * @param items
	 *            the list of strings for the combo box
	 * @param style
	 *            the style bits
	 * @since 2.1
	 */
	public KeyComboBoxCellEditor(Composite parent, String[] items, int style) {
		super(parent, style);
		setItems(items);
		searchStringBuffer = new StringBuffer();
	}

	/**
	 * Returns the list of choices for the combo box
	 * 
	 * @return the list of choices for the combo box
	 */
	public String[] getItems() {
		return this.items;
	}

	/**
	 * Sets the list of choices for the combo box
	 * 
	 * @param items
	 *            the list of choices for the combo box
	 */
	public void setItems(String[] items) {
		Assert.isNotNull(items);
		this.items = items;
		populateComboBoxItems();
	}

	public IHateCCombo getComboBox() {
		return comboBox;
	}
	
	private boolean _moving;
	private boolean _resizing;
	/*
	 * (non-Javadoc) Method declared on CellEditor.
	 */
	protected Control createControl(Composite parent) {
		comboBox = new IHateCCombo(parent, getStyle());
		comboBox.setBackground(parent.getBackground());
		comboBox.setFont(parent.getFont());

		if ("carbon".equals(SWT.getPlatform()) || "cocoa".equals(SWT.getPlatform())) {
			comboBox.addControlListener(new ControlListener() {
				public void controlMoved(ControlEvent e) {
					if (!_moving) {
						_moving = true;
						Point location = comboBox.getLocation();
						if (System.getProperty("org.eclipse.swt.internal.carbon.smallFonts") != null) {
							comboBox.setLocation(location.x - 3, location.y - 1);
						}
						else {
							comboBox.setLocation(location.x - 3, location.y - 2);
						}
						_moving = false;
					}
				}
				
				public void controlResized(ControlEvent e) {
					if (!_resizing) {
						_resizing = true;
						Point size = comboBox.getSize();
						if (System.getProperty("org.eclipse.swt.internal.carbon.smallFonts") != null) {
							comboBox.setSize(size.x + 3, size.y + 2);
						}
						else {
							comboBox.setSize(size.x + 3, size.y + 3);
						}
						if (isActivated()) {
							if (!comboBox.getEditable()) {
								//comboBox.dropDown(true);
							}
						}
						_resizing = false;
					}
				}
			});
		}
		
		comboBox.addKeyListener(new KeyAdapter() {
			// hook key pressed - see PR 14201
			public void keyPressed(KeyEvent e) {
				keyReleaseOccured(e);
			}
		});

		comboBox.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent event) {
				applyEditorValue(false);
			}

			public void widgetSelected(SelectionEvent event) {
				selection = comboBox.getSelectionIndex();
			}
		});

		comboBox.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE || e.detail == SWT.TRAVERSE_RETURN) {
					e.doit = false;
				}
			}
		});

		comboBox.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				KeyComboBoxCellEditor.this.focusLost();
			}
		});

		return comboBox;
	}

	/**
	 * The <code>ComboBoxCellEditor</code> implementation of this
	 * <code>CellEditor</code> framework method returns the zero-based index
	 * of the current selection.
	 * 
	 * @return the zero-based index of the current selection wrapped as an
	 *         <code>Integer</code>
	 */
	protected Object doGetValue() {
		return Integer.valueOf(selection);
	}

	/*
	 * (non-Javadoc) Method declared on CellEditor.
	 */
	protected void doSetFocus() {
		comboBox.setFocus();
	}

	/**
	 * The <code>ComboBoxCellEditor</code> implementation of this
	 * <code>CellEditor</code> framework method sets the minimum width of the
	 * cell. The minimum width is 10 characters if <code>comboBox</code> is
	 * not <code>null</code> or <code>disposed</code> eles it is 60 pixels
	 * to make sure the arrow button and some text is visible. The list of
	 * CCombo will be wide enough to show its longest item.
	 */
	public LayoutData getLayoutData() {
		LayoutData layoutData = new LayoutData();
		return layoutData;
		/*
		LayoutData layoutData = super.getLayoutData();
		if ((comboBox == null) || comboBox.isDisposed()) {
			layoutData.minimumWidth = 60;
		} else {
			// make the comboBox 10 characters wide
			GC gc = new GC(comboBox);
			layoutData.minimumWidth = (gc.getFontMetrics().getAverageCharWidth() * 10) + 10 + 300;
			gc.dispose();
		}
		return layoutData;
		*/
	}

	/**
	 * The <code>ComboBoxCellEditor</code> implementation of this
	 * <code>CellEditor</code> framework method accepts a zero-based index of
	 * a selection.
	 * 
	 * @param value
	 *            the zero-based index of the selection wrapped as an
	 *            <code>Integer</code>
	 */
	protected void doSetValue(Object value) {
		if (value == null) {
			comboBox.select(-1);
		} else {
			// Assert.isTrue(comboBox != null && (value instanceof Integer));
			selection = ((Integer) value).intValue();
			comboBox.select(selection);
		}
	}

	/**
	 * Updates the list of choices for the combo box for the current control.
	 */
	private void populateComboBoxItems() {
		if (comboBox != null && items != null) {
			comboBox.removeAll();
			for (int i = 0; i < items.length; i++) {
				comboBox.add(items[i], i);
			}

			setValueValid(true);
			selection = 0;
		}
	}
	
	@Override
	public void activate() {
		super.activate();
	}
	
	/**
	 * Applies the currently selected value and deactiavates the cell editor
	 */
	void applyEditorValue(boolean deactivate) {
		// must set the selection before getting value
		selection = comboBox.getSelectionIndex();
		Object newValue = doGetValue();
		markDirty();
		boolean isValid = isCorrect(newValue);
		setValueValid(isValid);

		if (!isValid) {
			// Only format if the 'index' is valid
			if (items.length > 0 && selection >= 0 && selection < items.length) {
				// try to insert the current value into the error message.
				setErrorMessage(MessageFormat.format(getErrorMessage(), new Object[] { items[selection] }));
			} else {
				// Since we don't have a valid index, assume we're using an
				// 'edit'
				// combo so format using its text value
				setErrorMessage(MessageFormat.format(getErrorMessage(), new Object[] { comboBox.getText() }));
			}
		}

		fireApplyEditorValue();
		comboBox.dropDown(false);
		if (deactivate) {
			deactivate();
		}
	}
	
	@Override
	public void deactivate() {
		super.deactivate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.CellEditor#focusLost()
	 */
	protected void focusLost() {
		if (isActivated()) {
			applyEditorValue(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.CellEditor#keyReleaseOccured(org.eclipse.swt.events.KeyEvent)
	 */
	protected void keyReleaseOccured(KeyEvent keyEvent) {
		if (keyEvent.character == '\u001b') { // Escape character
			fireCancelEditor();
		} else if (keyEvent.character == '\t') { // tab key
			applyEditorValue(true);
		} else if (keyEvent.keyCode == SWT.ARROW_DOWN) {
			comboBox.dropDown(true);
		} else if (Character.isLetterOrDigit(keyEvent.character)) {
			long now = System.currentTimeMillis();
			if ((now - lastKeyTime) > 1000) {
				lastKeyTime = now;
				searchStringBuffer.setLength(0);
			}
			searchStringBuffer.append(Character.toLowerCase(keyEvent.character));
			int matchingIndex = -1;
			String searchString = searchStringBuffer.toString();
			for (int itemNum = 0; matchingIndex == -1 && itemNum < items.length; itemNum++) {
				if (items[itemNum].toLowerCase().startsWith(searchString)) {
					matchingIndex = itemNum;
				}
			}
			if (matchingIndex != -1) {
				comboBox.select(matchingIndex);
				selection = matchingIndex;
			} else {
				comboBox.clearSelection();
				selection = 0;
				comboBox.select(-1);
			}
		}
	}
}
