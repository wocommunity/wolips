/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 The ObjectStyle Group and individual authors of the
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
package org.objectstyle.wolips.ruleeditor.editor;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.objectstyle.wolips.ruleeditor.filter.RulesFilter;
import org.objectstyle.wolips.ruleeditor.listener.FilterListener;
import org.objectstyle.wolips.ruleeditor.listener.NumberVerifyListener;
import org.objectstyle.wolips.ruleeditor.listener.TableSortSelectionListener;
import org.objectstyle.wolips.ruleeditor.model.D2WModel;
import org.objectstyle.wolips.ruleeditor.model.LeftHandSide;
import org.objectstyle.wolips.ruleeditor.model.RightHandSide;
import org.objectstyle.wolips.ruleeditor.model.Rule;
import org.objectstyle.wolips.ruleeditor.provider.TableContentProvider;
import org.objectstyle.wolips.ruleeditor.provider.TableLabelProvider;
import org.objectstyle.wolips.ruleeditor.sorter.AbstractInvertableTableSorter;
import org.objectstyle.wolips.ruleeditor.sorter.TextSorter;

/**
 * The UI class for the rule editor.
 * 
 * @author uli
 * @author <a href="mailto:frederico@moleque.com.br">Frederico Lellis</a>
 * @author <a href="mailto:georg@moleque.com.br">Georg von Bülow</a>
 */
public class RuleEditor {
	public static TableSortSelectionListener createTableColumn(final TableViewer viewer, final String text, final String tooltip, final AbstractInvertableTableSorter sorter, final int initialDirection, final boolean keepDirection) {
		TableColumn column = new TableColumn(viewer.getTable(), SWT.LEFT);

		column.setText(text);
		column.setToolTipText(tooltip);

		return new TableSortSelectionListener(viewer, column, sorter, initialDirection, keepDirection);
	}

	private Text classtext;

	private Text lhstext;

	private D2WModel model;

	private Text prioritytext;

	private Text rhstext;

	private Rule rule;

	private Table table;

	private TableViewer tableViewer;

	protected boolean updating;

	private Text valuetext;

	/**
	 * Creates the main window's contents
	 * 
	 * @param shell
	 *            the main window
	 */
	public void createContents(final Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);

		GridLayout layout1 = new GridLayout();
		layout1.numColumns = 2;
		layout1.makeColumnsEqualWidth = true;

		container.setLayout(layout1);

		// Create Button Group
		GridData buttondata = new GridData();
		buttondata.grabExcessHorizontalSpace = true;
		buttondata.horizontalAlignment = GridData.FILL;
		buttondata.verticalAlignment = GridData.CENTER;

		RowLayout buttonlayout = new RowLayout();
		buttonlayout.fill = true;

		Group buttongroup = new Group(container, SWT.NONE);
		buttongroup.setLayout(buttonlayout);
		buttongroup.setLayoutData(buttondata);

		final Button newrulebutton = new Button(buttongroup, SWT.PUSH);

		newrulebutton.setText("New Rule");
		newrulebutton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				rule = model.createEmptyRule();

				updateRules();

				table.select(table.getItemCount());
				updating = false;

				updateBottomDisplay();

			}
		});

		final Button deletebutton = new Button(buttongroup, SWT.PUSH);
		deletebutton.setText("Delete Rule");
		deletebutton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				model.removeRule(selectedRule());

				rule = null;
				tableViewer.refresh();

				updateRules();
				updateBottomDisplay();
			}
		});

		final Button duplicateButton = new Button(buttongroup, SWT.PUSH);
		duplicateButton.setText("Duplicate");
		duplicateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				Rule ruleToCopy = (Rule) tableViewer.getElementAt(table.getSelectionIndex());

				model.copyRule(ruleToCopy);

				updateRules();
				updateBottomDisplay();
			}
		});

		// Search field
		GridData searchdata = new GridData();
		searchdata.grabExcessHorizontalSpace = true;
		searchdata.verticalAlignment = GridData.CENTER;
		searchdata.horizontalAlignment = GridData.FILL;

		final Text searchtext = new Text(container, SWT.BORDER);
		// text.setBounds(100, 50, 70, 20);
		// text.setSize(50, 50);
		searchtext.setLayoutData(searchdata);
		searchtext.setText("Use this field to search any term");
		searchtext.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(final FocusEvent event) {
				if (searchtext.getText().equals("Use this field to search any term")) {
					searchtext.setText("");
				}

			}
		});
		searchtext.addListener(SWT.DefaultSelection, new Listener() {
			public void handleEvent(final Event e) {
				for (ViewerFilter filter : tableViewer.getFilters()) {
					tableViewer.removeFilter(filter);
				}
				String regex = (searchtext.getText());
				if (regex != null && !regex.equals("")) {
					tableViewer.addFilter(new RulesFilter(regex));
				}

			}
		});

		GridData tabledata = new GridData(GridData.FILL_BOTH);
		tabledata.horizontalSpan = 2;
		tabledata.verticalAlignment = GridData.FILL;
		tabledata.horizontalAlignment = GridData.FILL;
		tabledata.grabExcessHorizontalSpace = true;
		tabledata.grabExcessVerticalSpace = true;

		// table = new Table(shell, SWT.SINGLE | SWT.FULL_SELECTION);
		table = new Table(container, SWT.MULTI | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(tabledata);

		// Create the TableViewer to hold content and do update

		tableViewer = new TableViewer(table);
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new TableLabelProvider());
		tableViewer.setInput(model);

		searchtext.addModifyListener(new FilterListener(tableViewer));

		// Create an editor object to use for text editing
		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;

		// Use a mouse listener, not a selection listener, since we're
		// interested
		// in the selected column as well as row
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				rule = (Rule) tableViewer.getElementAt(table.getSelectionIndex());
				updating = true;
				updateBottomDisplay();
			}
		});

		/*
		 * table.addMouseListener(new MouseAdapter() { TableItem item; int
		 * column = -1; public void mouseDown(MouseEvent event) { // Determine
		 * where the mouse was clicked Point pt = new Point(event.x, event.y); //
		 * Determine which row was selected item = table.getItem(pt); if (item !=
		 * null) { // Determine which column was selected for (int i = 0, n =
		 * table.getColumnCount(); i < n; i++) { Rectangle rect =
		 * item.getBounds(i); if (rect.contains(pt)) { // This is the selected
		 * column column = i; break; } } rule = (ERD2WExtendedRule)
		 * item.getData("rule"); if (rule.lhs() == null) lhstext.setText("");
		 * else lhstext.setText(rule.lhs().toString());
		 * classtext.setText(rule.assignmentClassName());
		 * rhstext.setText(rule.rhsKeyPath()); if (rule.rhs().value() == null)
		 * valuetext.setText(""); else
		 * valuetext.setText(rule.rhs().value().toString());
		 * prioritytext.setText((Integer.valueOf(rule.author())).toString()); } }
		 * public void mouseDoubleClick(MouseEvent event) { // Dispose any
		 * existing editor Control old = editor.getEditor(); if (old != null)
		 * old.dispose(); if (item != null) { // Create the Text object for our
		 * editor final Text edittabletext = new Text(table, SWT.NONE);
		 * edittabletext.setForeground(item.getForeground()); // Transfer any
		 * text from the cell to the Text control, // set the color to match
		 * this row, select the text, // and set focus to the control
		 * edittabletext.setText(item.getText(column));
		 * edittabletext.setForeground(item.getForeground());
		 * edittabletext.selectAll(); edittabletext.setFocus(); // Recalculate
		 * the minimum width for the editor editor.minimumWidth =
		 * edittabletext.getBounds().width; // Set the control into the editor
		 * editor.setEditor(edittabletext, item, column); // Add a handler to
		 * transfer the text back to the cell // any time it's modified final
		 * int col = column; edittabletext.addFocusListener(new FocusAdapter() {
		 * public void focusLost(FocusEvent event) { if (col == 0) //LHS
		 * setLHSKeyPath(edittabletext.getText()); else if (col == 1) { //RHS
		 * Key setRHSKeyPath(edittabletext.getText()); } else if (col == 2) {
		 * //RHS Value setRHSValue(edittabletext.getText()); } else if (col ==
		 * 3) { //Priority setPriority(edittabletext.getText()); } Control old =
		 * editor.getEditor(); if (old != null) old.dispose(); } }); } } });
		 */

		// Create the columns
		// FIXME Need a checkbox column for RuleIsDisabled = 'YES'
		RuleEditor.createTableColumn(tableViewer, "Lhs", "Left Hand Side", new TextSorter(0), SWT.DOWN, true);
		RuleEditor.createTableColumn(tableViewer, "Rhs Key", "Right Hand Side Key", new TextSorter(1), SWT.UP, false);
		RuleEditor.createTableColumn(tableViewer, "Rhs Value", "Right Hand Side Value", new TextSorter(2), SWT.UP, false);
		RuleEditor.createTableColumn(tableViewer, "Priority", "Priority", new TextSorter(3), SWT.UP, false).chooseColumnForSorting();

		// TableColumn c1 = new TableColumn(table, SWT.LEFT, 0);
		// c1.setText("Lhs");
		// c1.pack();
		// TableColumn c2 = new TableColumn(table, SWT.LEFT, 1);
		// c2.setText("Rhs Key");
		// c2.pack();
		// TableColumn c3 = new TableColumn(table, SWT.LEFT, 2);
		// c3.setText("Rhs Value");
		// c3.pack();
		// TableColumn c4 = new TableColumn(table, SWT.RIGHT, 3);
		// c4.setText("Priority");
		// c4.pack();

		table.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(final ControlEvent e) {
				if (table.getColumnCount() < 2) {
					return;
				}

				int tblWidth = table.getBounds().width;
				int t0w = (int) (tblWidth * 0.4);
				int t1w = (int) (tblWidth * 0.2);
				int t2w = (int) (tblWidth * 0.3);
				int t3w = tblWidth - (t0w + t1w + t2w + 40);
				table.getColumn(0).setWidth(t0w);
				table.getColumn(1).setWidth(t1w);
				table.getColumn(2).setWidth(t2w);
				table.getColumn(3).setWidth(t3w);
			}
		});

		// Create LHS segment
		GridData lhsdata = new GridData();
		lhsdata.horizontalAlignment = GridData.FILL;
		lhsdata.grabExcessHorizontalSpace = true;
		lhsdata.verticalAlignment = GridData.FILL;
		Group lhsgroup = new Group(container, SWT.NONE);
		lhsgroup.setText("Left-Hand Side");
		lhsgroup.setLayout(new FillLayout());
		lhsgroup.setLayoutData(lhsdata);
		lhstext = new Text(lhsgroup, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		lhstext.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final FocusEvent event) {
				try {
					if (lhstext.getText() != "") {
						setLhsConditions(lhstext.getText());
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		lhstext.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(final FocusEvent event) {

				if (LeftHandSide.EMPTY_LHS_VALUE.equals(lhstext.getText())) {
					lhstext.setText("");

				}

			}
		});
		// Create RHS segment
		GridData rhsdata = new GridData();
		rhsdata.horizontalAlignment = GridData.FILL;
		rhsdata.grabExcessHorizontalSpace = true;
		rhsdata.verticalAlignment = GridData.FILL;

		GridLayout rhslayout = new GridLayout();
		rhslayout.numColumns = 4;
		rhslayout.makeColumnsEqualWidth = false;

		GridData textdata = new GridData();
		textdata.grabExcessHorizontalSpace = true;
		textdata.verticalAlignment = GridData.CENTER;
		textdata.horizontalSpan = 3;
		textdata.horizontalAlignment = GridData.FILL;

		Group rhsgroup = new Group(container, SWT.NONE);
		rhsgroup.setText("Right-Hand Side");
		rhsgroup.setLayout(rhslayout);
		rhsgroup.setLayoutData(rhsdata);

		Label classlabel = new Label(rhsgroup, SWT.NONE);
		classlabel.setText("Class:");
		classtext = new Text(rhsgroup, SWT.BORDER);
		classtext.setLayoutData(textdata);
		classtext.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final FocusEvent event) {
				setClassName(classtext.getText());
			}
		});

		Label keylabel = new Label(rhsgroup, SWT.NONE);
		keylabel.setText("Key:");
		rhstext = new Text(rhsgroup, SWT.BORDER);
		rhstext.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final FocusEvent event) {
				setRHSKeyPath(rhstext.getText());
			}
		});

		GridData keydata = new GridData();
		keydata.grabExcessHorizontalSpace = true;
		keydata.verticalAlignment = GridData.CENTER;
		keydata.horizontalAlignment = GridData.FILL;
		keydata.minimumWidth = 180;
		rhstext.setLayoutData(keydata);
		Label prioritylabel = new Label(rhsgroup, SWT.NONE);
		prioritylabel.setText("Priority:");
		prioritytext = new Text(rhsgroup, SWT.BORDER);
		GridData prioritydata = new GridData();
		prioritydata.grabExcessHorizontalSpace = true;
		prioritydata.verticalAlignment = GridData.CENTER;
		prioritydata.horizontalAlignment = GridData.FILL;
		prioritydata.minimumWidth = 50;
		prioritytext.setLayoutData(prioritydata);
		prioritytext.addVerifyListener(new NumberVerifyListener());
		prioritytext.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final FocusEvent event) {
				setPriority(prioritytext.getText());
			}
		});

		Label valuelabel = new Label(rhsgroup, SWT.NONE);
		valuelabel.setText("Value:");
		valuetext = new Text(rhsgroup, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		GridData valuedata = new GridData(GridData.FILL_BOTH);
		valuedata.grabExcessHorizontalSpace = true;
		valuedata.grabExcessVerticalSpace = true;
		// valuedata.minimumWidth = 150;
		valuedata.minimumHeight = 150;
		valuedata.horizontalSpan = 3;
		valuedata.verticalSpan = 3;

		valuetext.setLayoutData(valuedata);
		valuetext.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final FocusEvent event) {
				setRHSValue(valuetext.getText());
			}
		});

		updateRules();
	}

	public Rule selectedRule() {
		return (Rule) tableViewer.getElementAt(table.getSelectionIndex());
	}

	void setClassName(final String classname) {
		rule.getRightHandSide().setAssignmentClassName(classname);

		updateRules();
	}

	public void setD2WModel(final D2WModel model) {
		this.model = model;

		updateRules();
	}

	public void setFocus() {
		// DO NOTHING
	}

	void setLhsConditions(final String conditions) {
		rule.getLeftHandSide().setConditions(conditions);

		updateRules();
	}

	void setPriority(final String priority) {
		rule.setAuthor(priority);

		updateRules();
	}

	void setRHSKeyPath(final String keypath) {
		rule.getRightHandSide().setKeyPath(keypath);

		updateRules();
	}

	void setRHSValue(final String value) {
		rule.getRightHandSide().setValue(value);

		updateRules();
	}

	void updateBottomDisplay() {
		if (rule != null) {
			LeftHandSide lhs = rule.getLeftHandSide();

			lhstext.setText(lhs.toString());

			RightHandSide rhs = rule.getRightHandSide();

			classtext.setText(rhs.getAssignmentClassName());

			rhstext.setText(rhs.getKeyPath());

			if (rhs.getValue() == null) {
				valuetext.setText("");
			} else {
				valuetext.setText(rhs.getValue());
			}

			prioritytext.setText(rule.getAuthor());

		} else {
			lhstext.setText("");
			classtext.setText("");
			rhstext.setText("");
			prioritytext.setText("");
			valuetext.setText("");
		}
	}

	public void updateRules() {
		if (table == null) {
			return;
		}

		tableViewer.setInput(model);
		tableViewer.refresh();
	}

}