/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2004 The ObjectStyle Group 
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne" 
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */

package org.objectstyle.wolips.jdt.ui;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.objectstyle.wolips.jdt.classpath.model.Framework;

/**
 * Insert the type's description here.
 * 
 * @see WizardPage
 */
public class ContainerPage extends WizardPage implements IClasspathContainerPage, ISelectionChangedListener {
	private ContainerContentProvider containerContentProvider;

	private CheckboxTreeViewer checkboxTreeViewer;

	private Text sourceField;

	private Button sourceFileButton;

	private Button sourceFolderButton;

	private Text javaDocField;

	private Button javaDocFileButton;

	private Button javaDocFolderButton;

	Text orderField;

	Button exportedButton;

	Framework framework;

	// private

	/**
	 * The constructor.
	 */
	public ContainerPage() {
		super("non-localized WOClassPathContainerPage");
	}

	public void createControl(Composite parent) {
		Composite thisPage = new Composite(parent, SWT.NONE);

		thisPage.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		thisPage.setLayout(new GridLayout());
		// thisPage.setLayout(new RowLayout(SWT.VERTICAL));

		this.checkboxTreeViewer = new CheckboxTreeViewer(thisPage, SWT.MULTI);
		// _uiList = new CheckboxTreeViewer(thisPage, SWT.MULTI | SWT.BORDER |
		// SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		// |GridData.VERTICAL_ALIGN_FILL
		Rectangle trim = this.checkboxTreeViewer.getTree().computeTrim(0, 0, 0, 12 * this.checkboxTreeViewer.getTree().getItemHeight());
		gd.heightHint = trim.height;
		this.checkboxTreeViewer.getTree().setLayoutData(gd);
		this.checkboxTreeViewer.setContentProvider(this.containerContentProvider);
		this.checkboxTreeViewer.setLabelProvider(this.containerContentProvider);
		this.checkboxTreeViewer.setInput(this.containerContentProvider);
		this.checkboxTreeViewer.addSelectionChangedListener(this);
		if (true) {
			Composite row = null;
			Label lbl = null;
			if (false) {
				row = new Composite(thisPage, SWT.NONE);
				row.setLayout(new RowLayout());
				lbl = new Label(row, SWT.SINGLE);
				lbl.setText("Source location");
				sourceField = new Text(row, SWT.SINGLE);
				sourceField.setEditable(false);
				sourceFileButton = new Button(row, SWT.PUSH);
				sourceFileButton.setText("Choose File");
				sourceFileButton.addMouseListener(new MouseListener() {

					public void mouseDoubleClick(MouseEvent e) {
						return;
					}

					public void mouseDown(MouseEvent e) {
						FileDialog fileDialog = new FileDialog(new Shell());
						fileDialog.open();
						String path = fileDialog.getFilterPath();
						String file = fileDialog.getFileName();
						if (path != null && file != null) {
							sourceField.setText(path + file);
							Path srcPath = new Path(path);
							framework.setSrcPath(srcPath.append(file));
						} else {
							sourceField.setText("");
							framework.setSrcPath(null);
						}
					}

					public void mouseUp(MouseEvent e) {
						return;
					}

				});
				sourceFolderButton = new Button(row, SWT.PUSH);
				sourceFolderButton.setText("Choose Folder");
				sourceFolderButton.addMouseListener(new MouseListener() {

					public void mouseDoubleClick(MouseEvent e) {
						return;
					}

					public void mouseDown(MouseEvent e) {
						DirectoryDialog directoryDialog = new DirectoryDialog(new Shell());
						directoryDialog.open();
						String stringPath = directoryDialog.getFilterPath();
						if (stringPath != null) {
							sourceField.setText(stringPath);
							if (framework != null) {
								IPath path = new Path(stringPath);
								/*
								 * if(stringPath.length() > 2 &&
								 * stringPath.charAt(1) == ':') { path =
								 * path.setDevice(stringPath.substring(2)); }
								 */
								framework.setSrcPath(path);
							}
						} else {
							sourceField.setText("");
							framework.setSrcPath(null);
						}
					}

					public void mouseUp(MouseEvent e) {
						return;
					}

				});
				row = new Composite(thisPage, SWT.NONE);
				row.setLayout(new RowLayout());
				lbl = new Label(row, SWT.SINGLE);
				lbl.setText("Javadoc location");
				javaDocField = new Text(row, SWT.SINGLE);
				javaDocField.setEditable(false);
				javaDocFileButton = new Button(row, SWT.PUSH);
				javaDocFileButton.setText("Choose File");
				javaDocFileButton.addMouseListener(new MouseListener() {

					public void mouseDoubleClick(MouseEvent e) {
						return;
					}

					public void mouseDown(MouseEvent e) {
						FileDialog fileDialog = new FileDialog(new Shell());
						fileDialog.open();
						String path = fileDialog.getFilterPath();
						String file = fileDialog.getFileName();
						if (path != null && file != null) {
							javaDocField.setText(path + file);
							Path javaDocPath = new Path(path);
							framework.setJavaDocPath(javaDocPath.append(file));
						} else {
							javaDocField.setText("");
							framework.setJavaDocPath(null);
						}
					}

					public void mouseUp(MouseEvent e) {
						return;
					}

				});
				javaDocFolderButton = new Button(row, SWT.PUSH);
				javaDocFolderButton.setText("Choose Folder");
				javaDocFolderButton.addMouseListener(new MouseListener() {

					public void mouseDoubleClick(MouseEvent e) {
						return;
					}

					public void mouseDown(MouseEvent e) {
						DirectoryDialog directoryDialog = new DirectoryDialog(new Shell());
						directoryDialog.open();
						String path = directoryDialog.getFilterPath();
						if (path != null) {
							javaDocField.setText(path);
							if (framework != null) {
								framework.setJavaDocPath(new Path(path));
							}
						} else {
							javaDocField.setText("");
							framework.setJavaDocPath(null);
						}
					}

					public void mouseUp(MouseEvent e) {
						return;
					}

				});
			}
			row = new Composite(thisPage, SWT.NONE);
			row.setLayout(new RowLayout());
			lbl = new Label(row, SWT.SINGLE);
			lbl.setText("Order");
			orderField = new Text(row, SWT.SINGLE);
			orderField.addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					if (framework != null) {
						framework.setOrder(orderField.getText());
					}
				}

			});

			row = new Composite(thisPage, SWT.NONE);
			row.setLayout(new RowLayout());
			exportedButton = new Button(row, SWT.CHECK);
			exportedButton.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent e) {
					// do nothing
				}

				public void widgetSelected(SelectionEvent e) {
					framework.setExported(exportedButton.getSelection());
				}
				
			});
			lbl = new Label(row, SWT.SINGLE);
			lbl.setText("Exported");
		}
		thisPage.layout();

		setControl(thisPage);
		this.containerContentProvider.setCheckboxTreeViewer(this.checkboxTreeViewer);
		this.frameworkChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.ui.wizards.IClasspathContainerPage#finish()
	 */
	public boolean finish() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.ui.wizards.IClasspathContainerPage#getSelection()
	 */
	public IClasspathEntry getSelection() {
		return this.containerContentProvider.getClasspathEntry();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.ui.wizards.IClasspathContainerPage#setSelection(org.eclipse.jdt.core.IClasspathEntry)
	 */
	public void setSelection(IClasspathEntry containerEntry) {
		if (containerEntry == null) {
			this.containerContentProvider = new ContainerContentProvider();
		} else {
			this.containerContentProvider = new ContainerContentProvider(containerEntry);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		this.framework = null;
		Object selection = event.getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			if (!structuredSelection.isEmpty()) {
				Object object = structuredSelection.getFirstElement();
				if (object instanceof Framework) {
					this.framework = (Framework) object;
				}
			}
		}
		this.frameworkChanged();
	}

	private void frameworkChanged() {
		if (this.framework == null) {
			if (false) {
				sourceField.setText("");
				sourceFileButton.setEnabled(false);
				sourceFolderButton.setEnabled(false);
				javaDocField.setText("");
				javaDocFileButton.setEnabled(false);
				javaDocFolderButton.setEnabled(false);
			}
			orderField.setText("");
			orderField.setEditable(false);
			exportedButton.setSelection(false);
			exportedButton.setEnabled(false);
		} else {
			if (false) {
				if (framework.getSrcPath() != null) {
					sourceField.setText(framework.getSrcPath().toString());
				} else {
					sourceField.setText("");
				}
				sourceFileButton.setEnabled(true);
				sourceFolderButton.setEnabled(true);
				if (framework.getJavaDocPath() != null) {
					javaDocField.setText(framework.getJavaDocPath().toString());
				} else {
					javaDocField.setText("");
				}
				javaDocFileButton.setEnabled(true);
				javaDocFolderButton.setEnabled(true);
				if (framework.getOrder() != null) {
					orderField.setText(framework.getOrder());
				} else {
					javaDocField.setText("");
				}
			}
			orderField.setEditable(true);
			String string = framework.getOrder();
			if (string == null) {
				orderField.setText("");
			} else {
				orderField.setText(string);
			}
			exportedButton.setSelection(framework.isExported());
			exportedButton.setEnabled(true);
		}
	}
}