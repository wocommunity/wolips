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
package org.objectstyle.wolips.wodclipse.core.document;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.ForwardingDocumentProvider;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.objectstyle.wolips.wodclipse.core.parser.IWodFilePartitions;

/**
 * @author mike
 */
public class WodFileDocumentProvider extends TextFileDocumentProvider {
	public static final String WOD_FILE_CONTENT_TYPE_STRING = "org.objectstyle.wolips.wodclipse.wod";

	public static final IContentType WOD_FILE_CONTENT_TYPE = Platform.getContentTypeManager().getContentType(WodFileDocumentProvider.WOD_FILE_CONTENT_TYPE_STRING); //$NON-NLS-1$

	public WodFileDocumentProvider() {
		IDocumentProvider provider = new TextFileDocumentProvider();
		provider = new ForwardingDocumentProvider(IWodFilePartitions.WOD_FILE_PARTITIONING, new WodFileDocumentSetupParticipant(), provider);
		setParentDocumentProvider(provider);
	}

	@Override
  protected FileInfo createFileInfo(Object element) throws CoreException {
		if (WOD_FILE_CONTENT_TYPE == null || !(element instanceof IFileEditorInput)) {
			return null;
		}

		IFileEditorInput input = (IFileEditorInput) element;

		IFile file = input.getFile();
		if (file == null) {
			return null;
		}

		IContentDescription description = file.getContentDescription();
		if (description == null || description.getContentType() == null || !description.getContentType().isKindOf(WOD_FILE_CONTENT_TYPE)) {
			return null;
		}

		return super.createFileInfo(element);
	}

	@Override
  protected DocumentProviderOperation createSaveOperation(final Object element, final IDocument document, final boolean overwrite) throws CoreException {
		if (getFileInfo(element) == null) {
			return null;
		}

		return super.createSaveOperation(element, document, overwrite);
	}
}
