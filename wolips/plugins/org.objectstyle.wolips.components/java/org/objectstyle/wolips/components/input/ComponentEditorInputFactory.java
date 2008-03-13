/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowlegement: "This product includes software developed by the ObjectStyle
 * Group (http://objectstyle.org/)." Alternately, this acknowlegement may
 * appear in the software itself, if and wherever such third-party
 * acknowlegements normally appear. 4. The names "ObjectStyle Group" and
 * "Cayenne" must not be used to endorse or promote products derived from this
 * software without prior written permission. For written permission, please
 * contact andrus@objectstyle.org. 5. Products derived from this software may
 * not be called "ObjectStyle" nor may "ObjectStyle" appear in their names
 * without prior written permission of the ObjectStyle Group.
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
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/> .
 *  
 */
package org.objectstyle.wolips.components.input;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

public class ComponentEditorInputFactory implements IElementFactory {
	private static final String NAME = "name";

	private static final String TAG_COUNT = "count";

	private static final String TAG_EDITOR = "editor";

	private static final String TAG_INPUT = "input";

	private static final String TAG_DISPLAY_HTML_PART_ON_REVEAL = "html";

	private static final String TAG_DISPLAY_WOD_PART_ON_REVEAL = "wod";

	private static final String TAG_DISPLAY_API_PART_ON_REVEAL = "api";

	private static final String TAG_DISPLAY_WOO_PART_ON_REVEAL = "woo";

	public static final String ID_FACTORY = ComponentEditorInputFactory.class.getName();

	public ComponentEditorInputFactory() {
		super();
	}

	public IAdaptable createElement(IMemento memento) {
		String countString = memento.getString(TAG_COUNT);
		if (countString == null) {
			return null;
		}
		int count = Integer.parseInt(countString);
		String[] editors = new String[count];
		ComponentEditorFileEditorInput[] allInputs = new ComponentEditorFileEditorInput[count];
		ComponentEditorFileEditorInput[] allComponentInputs = new ComponentEditorFileEditorInput[count - 1];
		for (int i = 0; i < count; i++) {
			editors[i] = memento.getString(TAG_EDITOR + i);
			String fileName = memento.getString(TAG_INPUT + i);
			if (fileName != null) {
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fileName));
				allInputs[i] = new ComponentEditorFileEditorInput(file);
				if (i < (count - 1)) {
					allComponentInputs[i] = allInputs[i];
				}
			}
		}

		ComponentEditorInput componentEditorInput = new ComponentEditorInput(memento.getString(NAME), editors, allInputs, allComponentInputs, allInputs[allInputs.length - 1]);
		for (int i = 0; i < allInputs.length; i++) {
			allInputs[i].setComponentEditorInput(componentEditorInput);
		}
		if (memento.getString(TAG_DISPLAY_API_PART_ON_REVEAL) != null) {
			componentEditorInput.setDisplayApiPartOnReveal(true);
		} else if (memento.getString(TAG_DISPLAY_HTML_PART_ON_REVEAL) != null) {
			componentEditorInput.setDisplayHtmlPartOnReveal(true);
		} else if (memento.getString(TAG_DISPLAY_WOD_PART_ON_REVEAL) != null) {
			componentEditorInput.setDisplayWodPartOnReveal(true);
		} else if (memento.getString(TAG_DISPLAY_WOO_PART_ON_REVEAL) != null) {
			componentEditorInput.setDisplayWooPartOnReveal(true);
		} else {
			componentEditorInput.setDisplayHtmlPartOnReveal(true);
		}
		return componentEditorInput;
	}

	public static void saveState(IMemento memento, ComponentEditorInput input) {
		String count = input.getEditors().length + "";
		memento.putString(NAME, input.getName());
		memento.putString(TAG_COUNT, count);
		for (int i = 0; i < input.getEditors().length; i++) {
			memento.putString(TAG_EDITOR + i, input.getEditors()[i]);
			ComponentEditorFileEditorInput fileEditorInput = (ComponentEditorFileEditorInput) input.getInput()[i];
			if (fileEditorInput != null) {
				IFile file = fileEditorInput.getFile();
				if (file != null) {
					memento.putString(TAG_INPUT + i, file.getFullPath().toString());
				}
			}
		}
		if (input.isDisplayApiPartOnReveal()) {
			memento.putString(TAG_DISPLAY_API_PART_ON_REVEAL, "true");
		} else if (input.isDisplayWodPartOnReveal()) {
			memento.putString(TAG_DISPLAY_WOD_PART_ON_REVEAL, "true");
		} else if (input.isDisplayWooPartOnReveal()) {
			memento.putString(TAG_DISPLAY_WOO_PART_ON_REVEAL, "true");
		}
		if (input.isDisplayHtmlPartOnReveal()) {
			memento.putString(TAG_DISPLAY_HTML_PART_ON_REVEAL, "true");
		}
	}
}
