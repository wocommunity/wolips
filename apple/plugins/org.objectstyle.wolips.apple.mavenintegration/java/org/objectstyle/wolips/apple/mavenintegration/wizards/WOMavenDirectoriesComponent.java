/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2004 The ObjectStyle Group,
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
/*Portions of this code are Copyright Apple Inc. 2008 and licensed under the
ObjectStyle Group Software License, version 1.0.  This license from Apple
applies solely to the actual code contributed by Apple and to no other code.
No other license or rights are granted by Apple, explicitly, by implication,
by estoppel, or otherwise.  All rights reserved.*/
package org.objectstyle.wolips.apple.mavenintegration.wizards;


/*
 * Licensed to the Codehaus Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.LinkedHashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.maven.ide.eclipse.core.Messages;



/**
 * Simple GUI component which allows the user to choose a set of directories from the default Maven2 directory
 * structure. This component is mainly used for choosing which of the directories of the default Maven2 directory
 * structure to create along with a newly created project.
 */
public class WOMavenDirectoriesComponent extends Composite {

	/** All the directories constituting the default Maven2 directory structure. */
	private static WOMavenDirectory[] defaultMaven2Directories = {
		new WOMavenDirectory("src/main/java", "target/classes", true),
		new WOMavenDirectory("src/main/resources", "target/classes", true),
		new WOMavenDirectory("src/main/webserver-resources", null, true),
		new WOMavenDirectory("src/assembly", null, true),
		new WOMavenDirectory("src/main/woresources", null, false),
		new WOMavenDirectory("src/main/components", null, true),
		new WOMavenDirectory("src/test/java", "target/test-classes", true),
		new WOMavenDirectory("src/test/resources", "target/test-classes", true),
		new WOMavenDirectory("src/test/filters", null, false),
		new WOMavenDirectory("src/site", null, true)
	};

	/** The set of directories currently selected by the user. */
	LinkedHashSet<Object> directories = new LinkedHashSet<Object>();

	/**
	 * Constructor. Constructs all the GUI components contained in this <code>Composite</code>. These components allow
	 * the user to choose a subset of all the directories of the default Maven2 directory structure.
	 *
	 * @param parent The widget which will be the parent of this component.
	 * @param styles The widget style for this component.
	 */
	public WOMavenDirectoriesComponent(Composite parent, int styles) {
		super(parent, styles);

		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);

		Group group = new Group(this, SWT.NONE);
		group.setText(Messages.getString("directoriesComponent.projectLayout"));
		group.setLayout(new GridLayout(3, false));
		group.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));

		DirectorySelectionListener directorySelectionListener = new DirectorySelectionListener();

		// Add checkboxes for all the directories.
		for(int i = 0; i < defaultMaven2Directories.length; i++ ) {
			WOMavenDirectory directory = defaultMaven2Directories[i];

			Button directoryButton = new Button(group, SWT.CHECK);
			directoryButton.setText(directory.getPath());
			directoryButton.addSelectionListener(directorySelectionListener);
			directoryButton.setData(directory);
			if(directory.isDefault()) {
				directoryButton.setSelection(true);
				directories.add(directory);
			}
		}
	}

	/**
	 * Returns all the Maven2 directories currently selected by the user.
	 *
	 * @return All the Maven2 directories currently selected by the user. Neither the array nor any of its elements is
	 *         <code>null</code>.
	 */
	public WOMavenDirectory[] getDirectories() {
		return directories.toArray(new WOMavenDirectory[directories.size()]);
	}

	class DirectorySelectionListener extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			Button button = ((Button) e.getSource());
			if(button.getSelection()) {
				directories.add(button.getData());
			} else {
				directories.remove(button.getData());
			}
		}
	}

}


