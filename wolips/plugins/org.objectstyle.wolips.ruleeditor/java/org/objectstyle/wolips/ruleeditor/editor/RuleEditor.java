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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.dnd.Clipboard;
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
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.objectstyle.wolips.ruleeditor.RuleEditorPlugin;
import org.objectstyle.wolips.ruleeditor.model.D2WModel;
import org.objectstyle.wolips.ruleeditor.model.Rule;

/**
 * @author uli
 */
public class RuleEditor {
	D2WModel model;

	Table table;

	Text lhstext;

	Text classtext;

	Text rhstext;

	Rule rule;

	Text valuetext;

	Text prioritytext;

	void setLHSKeyPath(String keypath) {
		// rule.setLeftHandSide(leftHandSide)(EOQualifier.qualifierWithQualifierFormat(keypath,
		// null));
		updateRules();
	}

	void setRHSKeyPath(String keypath) {
		rule.getRightHandSide().setKeyPath(keypath);
		updateRules();
	}

	void setRHSValue(String value) {
		rule.getRightHandSide().setValue(value);
		updateRules();
	}

	void setPriority(String priority) {
		rule.setPriority(priority);
		updateRules();
	}

	void setClassName(String classname) {
		rule.setAssignmentClassName(classname);
		updateRules();
	}

	void updateBottomDisplay() {
		if (rule.getLeftHandSide() == null)
			lhstext.setText("");
		else
			lhstext.setText(rule.getLeftHandSide().toString());
		classtext.setText(rule.getAssignmentClassName());
		rhstext.setText(rule.getRightHandSide().getKeyPath());
		if (rule.getRightHandSide().getValue() == null)
			valuetext.setText("");
		else
			valuetext.setText(rule.getRightHandSide().getValue());
		prioritytext.setText(rule.getPriority().toString());
	}

	/**
	 * Creates the main window's contents
	 * 
	 * @param shell
	 *            the main window
	 */
	public void createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		// Shell shell = container.getShell();
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
		// buttonayout.justify = false;
		Group buttongroup = new Group(container, SWT.NONE);
		buttongroup.setLayout(buttonlayout);
		buttongroup.setLayoutData(buttondata);

		final Button newrulebutton = new Button(buttongroup, SWT.PUSH);
		newrulebutton.setText("New Rule");
		newrulebutton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// rule = new ERD2WExtendedRule();
				// rule.setAuthor(100);
				// rule.setRhs(new Assignment("", null));
				// model.addRule(rule);
				// displaygroup.setObjectArray(model.publicRules());

				updateRules();
				table.setSelection(table.getItemCount() - 1);
				updateBottomDisplay();
			}
		});

		final Button deletebutton = new Button(buttongroup, SWT.PUSH);
		deletebutton.setText("Delete Rule");
		deletebutton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// NSMutableArray publicrules =
				// model.publicRules().mutableClone();
				// for (int i = 0; i < table.getSelectionCount(); i++) {
				// ERD2WExtendedRule deleterule = (ERD2WExtendedRule)
				// table.getSelection()[i].getData("rule");
				// publicrules.removeObject(deleterule);
				// }
				// model.setPublicRules(publicrules);
				// displaygroup.setObjectArray(model.publicRules());
				updateRules();
				updateBottomDisplay();
			}
		});

		final Button copybutton = new Button(buttongroup, SWT.PUSH);
		copybutton.setText("Copy");
		copybutton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// Clipboard cb = new Clipboard(copybutton.getDisplay());
				// NSMutableArray rules = new NSMutableArray();
				// for (int i = 0; i < table.getSelectionCount(); i++) {
				// ERD2WExtendedRule copyrule = (ERD2WExtendedRule) table
				// .getSelection()[i].getData("rule");
				// EOKeyValueArchiver rulearchive = new EOKeyValueArchiver();
				//
				// copyrule.encodeWithKeyValueArchiver(rulearchive);
				// rules.addObject(copyrule);
				// }
				// EOKeyValueArchiver rulesarchive = new EOKeyValueArchiver();
				// rulesarchive.encodeObject(rules, "rules");
				//
				// cb.setContents(new Object[] { rulesarchive.dictionary()
				// .toString() }, new Transfer[] { TextTransfer
				// .getInstance() });
				// cb.dispose();
			}
		});

		final Button pastebutton = new Button(buttongroup, SWT.PUSH);
		pastebutton.setText("Paste");
		pastebutton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Clipboard cb = new Clipboard(pastebutton.getDisplay());

				try {
					// NSMutableDictionary rulesdictionary =
					// (NSMutableDictionary) NSPropertyListSerialization
					// .dictionaryForString((String) cb
					// .getContents(TextTransfer.getInstance()));
					// NSArray ruleschange = (NSArray) rulesdictionary
					// .objectForKey("rules");
					// Enumeration e = ruleschange.objectEnumerator();
					// while (e.hasMoreElements()) {
					// NSMutableDictionary dict = (NSMutableDictionary) e
					// .nextElement();
					// if ("com.webobjects.directtoweb.Rule".equals(dict
					// .objectForKey("class"))) {
					// dict.setObjectForKey("ERD2WExtendedRule", "class");
					// }
					// }
					//
					// EOKeyValueUnarchiver rulesunarchiver = new
					// EOKeyValueUnarchiver(
					// rulesdictionary);
					// NSArray rules = (NSArray) rulesunarchiver
					// .decodeObjectForKey("rules");
					// for (int i = 0; i < rules.count(); i++) {
					// // model.addRule((ERD2WExtendedRule)
					// // rules.objectAtIndex(i));
					// }
					//
					// // displaygroup.setObjectArray(model.publicRules());
					updateRules();
					table.setSelection(table.getItemCount() - 1);
					updateBottomDisplay();
				} catch (Exception e) {
					RuleEditorPlugin.getDefault().log(e);
				}
				cb.dispose();
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
		searchtext.addListener(SWT.DefaultSelection, new Listener() {
			public void handleEvent(Event e) {
				// displaygroup
				// .setQualifier(EOQualifier
				// .qualifierWithQualifierFormat(
				// "(lhs caseInsensitiveLike '*"
				// + searchtext.getText()
				// + "*') or (rhsKeyPath caseInsensitiveLike '*"
				// + searchtext.getText()
				// + "*') or (rhs.value caseInsensitiveLike '*"
				// + searchtext.getText() + "*')",
				// null));
				// displaygroup.updateDisplayedObjects();
				updateRules();
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

		// Create an editor object to use for text editing
		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;

		// Use a mouse listener, not a selection listener, since we're
		// interested
		// in the selected column as well as row
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				rule = (Rule) e.item.getData("rule");
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
		 * prioritytext.setText((new Integer(rule.author())).toString()); } }
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
		TableColumn c1 = new TableColumn(table, SWT.LEFT, 0);
		c1.setText("Lhs");
		c1.pack();
		TableColumn c2 = new TableColumn(table, SWT.LEFT, 1);
		c2.setText("Rhs Key");
		c2.pack();
		TableColumn c3 = new TableColumn(table, SWT.LEFT, 2);
		c3.setText("Rhs Value");
		c3.pack();
		TableColumn c4 = new TableColumn(table, SWT.RIGHT, 3);
		c4.setText("Priority");
		c4.pack();

		table.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				if (table.getColumnCount() < 2)
					return;

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
			public void focusLost(FocusEvent event) {
				setLHSKeyPath(lhstext.getText());
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
			public void focusLost(FocusEvent event) {
				setClassName(classtext.getText());
			}
		});

		Label keylabel = new Label(rhsgroup, SWT.NONE);
		keylabel.setText("Key:");
		rhstext = new Text(rhsgroup, SWT.BORDER);
		rhstext.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent event) {
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
		prioritytext.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent event) {
				setPriority(prioritytext.getText());
			}
		});

		Label valuelabel = new Label(rhsgroup, SWT.NONE);
		valuelabel.setText("Value:");
		valuetext = new Text(rhsgroup, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		GridData valuedata = new GridData();
		valuedata.grabExcessHorizontalSpace = true;
		valuedata.verticalAlignment = GridData.FILL;
		valuedata.horizontalAlignment = GridData.FILL;
		// valuedata.minimumWidth = 150;
		valuedata.minimumHeight = 150;
		valuedata.horizontalSpan = 3;
		valuetext.setLayoutData(valuedata);
		valuetext.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent event) {
				setRHSValue(valuetext.getText());
			}
		});

		updateRules();
	}

	public void setD2WModel(D2WModel model) {
		this.model = model;
		// displaygroup = new WODisplayGroup();
		// displaygroup.setObjectArray(model.publicRules());
		// displaygroup.setDefaultStringMatchFormat("*%@*");
		updateRules();
	}

	public void updateRules() {
		if (table == null)
			return;

		table.removeAll();
		for (int i = 0; i < model.getRules().size(); i++) {
			Rule currentRule = (Rule) model.getRules().get(i);
			TableItem item = new TableItem(table, SWT.NONE);

			item.setData("rule", currentRule);
			if (currentRule.getLeftHandSide() == null)
				item.setText(0, "*true*");
			else
				item.setText(0, currentRule.getLeftHandSide().getDisplayString());

			item.setText(1, currentRule.getRightHandSide().getKeyPath());

			if (currentRule.getRightHandSide().getValue() != null)
				item.setText(2, currentRule.getRightHandSide().getValue());

			item.setText(3, currentRule.getPriority().toString());
		}
	}

	public void setFocus() {
		// DO NOTHING
	}
}