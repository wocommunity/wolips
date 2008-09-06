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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPageExtension;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPageExtension2;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.objectstyle.wolips.jdt.JdtPlugin;
import org.objectstyle.wolips.jdt.classpath.WOFrameworkClasspathContainer;
import org.objectstyle.wolips.jdt.classpath.model.IEclipseFramework;

public class WOFrameworkContainerPage extends WizardPage implements IClasspathContainerPage, IClasspathContainerPageExtension2, IClasspathContainerPageExtension {
	private CheckboxTableViewer _frameworkTableViewer;

	private Set<IEclipseFramework> _frameworks;

	private Set<IEclipseFramework> _usedFrameworks;

	private IJavaProject _project;

	/**
	 * The constructor.
	 */
	public WOFrameworkContainerPage() {
		super("WebObjects Frameworks");
		setTitle("WebObjects Frameworks");
		setMessage("Select the frameworks to add to this project.");
	}

	protected boolean isFrameworkUsed(IEclipseFramework framework) {
		return _usedFrameworks.contains(framework);
	}
	
	public void createControl(Composite parent) {
		Composite thisPage = new Composite(parent, SWT.NONE);

		thisPage.setLayoutData(new GridData(GridData.FILL_BOTH));
		thisPage.setLayout(new GridLayout());

		_frameworkTableViewer = CheckboxTableViewer.newCheckList(thisPage, SWT.MULTI | SWT.BORDER);

		TableColumn frameworkNameColumn = new TableColumn(_frameworkTableViewer.getTable(), SWT.NONE);
		frameworkNameColumn.setText("Name");
		frameworkNameColumn.setWidth(200);

		TableColumn rootNameColumn = new TableColumn(_frameworkTableViewer.getTable(), SWT.NONE);
		rootNameColumn.setText("Location");
		rootNameColumn.setWidth(200);

		GridData tableLayoutData = new GridData(GridData.FILL_BOTH);
		tableLayoutData.heightHint = 200;
		_frameworkTableViewer.getTable().setHeaderVisible(true);
		_frameworkTableViewer.getTable().setLayoutData(tableLayoutData);
		_frameworkTableViewer.setSorter(new ViewerSorter());
		// _frameworkTableViewer.addSelectionChangedListener(this);

		final CheckboxTableViewer finalFrameworkTableViewer = _frameworkTableViewer;
		_frameworkTableViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				IEclipseFramework checkedFramework = (IEclipseFramework) event.getElement();
				if (!event.getChecked() && isFrameworkUsed(checkedFramework)) {
					finalFrameworkTableViewer.setChecked(checkedFramework, true);
				}
			}
		});

		WOFrameworkContentProvider frameworkContentProvider = new WOFrameworkContentProvider(_frameworks);
		WOFrameworkLabelProvider labelProvider = new WOFrameworkLabelProvider(_usedFrameworks);

		_frameworkTableViewer.setContentProvider(frameworkContentProvider);
		_frameworkTableViewer.setLabelProvider(labelProvider);
		_frameworkTableViewer.setInput(frameworkContentProvider);
		_frameworkTableViewer.setGrayedElements(_usedFrameworks.toArray());
		_frameworkTableViewer.setCheckedElements(_usedFrameworks.toArray());

		setControl(thisPage);
		// frameworkChanged();
	}

	public void initialize(IJavaProject javaProject, IClasspathEntry[] currentEntries) {
		_project = javaProject;
		_frameworks = JdtPlugin.getDefault().getFrameworkModel(_project.getProject()).getAllFrameworks();
		_usedFrameworks = new HashSet<IEclipseFramework>();
		try {
			Map<String, IEclipseFramework> namedFrameworksMap = new HashMap<String, IEclipseFramework>();
			for (IEclipseFramework framework : _frameworks) {
				namedFrameworksMap.put(framework.getName(), framework);
			}

			for (IClasspathEntry classpathEntry : currentEntries) {
				WOFrameworkClasspathContainer frameworkContainer = WOFrameworkClasspathContainer.getFrameworkClasspathContainer(_project, classpathEntry);
				if (frameworkContainer != null) {
					IEclipseFramework framework = frameworkContainer.getFramework();
					IEclipseFramework localFramework = namedFrameworksMap.get(framework.getName());
					if (localFramework != null) {
						_usedFrameworks.add(localFramework);
					}
				}
			}
		} catch (Throwable t) {
			throw new RuntimeException("Failed to remove already linked frameworks from the framework list.", t);
		}

	}

	public boolean finish() {
		return true;
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}

	public void setSelection(IClasspathEntry containerEntry) {
		// DO NOTHING
	}

	public IClasspathEntry getSelection() {
		return null;
	}

	public IClasspathEntry[] getNewContainers() {
		Set<IClasspathEntry> classpathEntries = new HashSet<IClasspathEntry>();
		if (_frameworkTableViewer != null) {
			Object[] checkedObjects = _frameworkTableViewer.getCheckedElements();
			for (Object checkedObject : checkedObjects) {
				IEclipseFramework selectedFramework = (IEclipseFramework) checkedObject;
				if (!isFrameworkUsed(selectedFramework)) {
					WOFrameworkClasspathContainer frameworkContainer = new WOFrameworkClasspathContainer(selectedFramework);
					classpathEntries.add(JavaCore.newContainerEntry(frameworkContainer.getPath()));
				}
			}
		}
		return classpathEntries.toArray(new IClasspathEntry[classpathEntries.size()]);
	}
}