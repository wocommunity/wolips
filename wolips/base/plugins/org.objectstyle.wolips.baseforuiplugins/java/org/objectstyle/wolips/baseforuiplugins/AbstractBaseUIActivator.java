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
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse
 * or promote products derived from this software without prior written
 * permission. For written permission, please contact andrus@objectstyle.org.
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
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.baseforuiplugins;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.osgi.framework.BundleContext;

public abstract class AbstractBaseUIActivator extends AbstractUIPlugin {
	// Resource bundle.
	private ResourceBundle resourceBundle;

	// The shared instance.
	private static AbstractBaseUIActivator Plugin;

	private boolean debug = true;

	private String bundleID;

	/**
	 * The constructor.
	 */
	public AbstractBaseUIActivator() {
		super();
		AbstractBaseUIActivator.Plugin = this;
	}

	public final String getBundleID() {
		if (bundleID == null) {
			bundleID = this.getClass().getName() + "Resources";
		}
		return bundleID;
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		this.resourceBundle = null;
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public final static String getResourceString(String key) {
		ResourceBundle bundle = AbstractBaseUIActivator.Plugin.getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public final ResourceBundle getResourceBundle() {
		try {
			if (this.resourceBundle == null)
				this.resourceBundle = ResourceBundle.getBundle(this.getBundleID());
		} catch (MissingResourceException x) {
			this.resourceBundle = null;
		}
		return this.resourceBundle;
	}

	/**
	 * Prints an IStatus.
	 * 
	 * @param status
	 */
	public void log(IStatus status) {
		this.getLog().log(status);
	}

	/**
	 * Prints a message.
	 * 
	 * @param message
	 */
	public void log(Object message) {
		this.log(new Status(IStatus.ERROR, this.getBundleID(), IStatus.ERROR, "" + message, null));
	}

	/**
	 * Prints a Throwable.
	 * 
	 * @param e
	 */
	public void log(Throwable e) {
		this.log(new Status(IStatus.ERROR, this.getBundleID(), IStatus.ERROR, "Internal Error", e)); //$NON-NLS-1$
	}

	/**
	 * Prints a Throwable.
	 * 
	 * @param message
	 * @param e
	 */
	public void log(Object message, Throwable e) {
		this.log(new Status(IStatus.ERROR, this.getBundleID(), IStatus.ERROR, "" + message, e)); //$NON-NLS-1$
	}

	/**
	 * If debug is true this method prints an Exception to the log.
	 * 
	 * @param aThrowable
	 */
	public void debug(Throwable aThrowable) {
		if (this.debug) {
			this.log(new Status(IStatus.WARNING, this.getBundleID(), IStatus.WARNING, aThrowable.getMessage(), aThrowable)); //$NON-NLS-1$
		}
	}

	/**
	 * @param message
	 * @param t
	 */
	public void debug(Object message, Throwable t) {
		if (this.debug) {
			this.log(new Status(IStatus.WARNING, this.getBundleID(), IStatus.WARNING, "" + message, t)); //$NON-NLS-1$
		}
	}

	/**
	 * @param message
	 */
	public void debug(Object message) {
		if (this.debug) {
			this.log(new Status(IStatus.WARNING, this.getBundleID(), IStatus.WARNING, "" + message, null)); //$NON-NLS-1$
		}
	}

	public void publicSaveDialogSettings() {
		saveDialogSettings();
	}

	public final void selectAndReveal(final IFile file) {
		this.selectAndReveal(file, null, IEditorTarget.TARGET_UNDEFINED);
	}

	public final void selectAndReveal(final IFile file, final String string, final int targetEditorID) {
		IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (int i = 0; i < workbenchWindows.length; i++) {
			final IWorkbenchPage workbenchPage = workbenchWindows[i].getActivePage();
			if (workbenchPage != null) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						runSelectAndReveal(workbenchPage, file, string, targetEditorID);
					}
				});
			}
		}
	}

	protected void runSelectAndReveal(IWorkbenchPage workbenchPage, IFile file, String string, int targetEditorID) {
		try {
			IEditorPart editorPart = IDE.openEditor(workbenchPage, file, true);
			if (string != null && string.length() > 0 && editorPart != null && editorPart instanceof ITextEditor) {
				if (editorPart instanceof IEditorTarget) {
					IEditorTarget editorTarget = (IEditorTarget) editorPart;
					editorPart = editorTarget.switchTo(targetEditorID);
				}
				ITextEditor textEditor = (ITextEditor) editorPart;
				IDocumentProvider documentProvider = textEditor.getDocumentProvider();
				IDocument document = documentProvider.getDocument(textEditor.getEditorInput());
				// it's deprecated but the sse does not provide a
				// FindReplaceDocumentAdapter
				int offset = document.search(0, string, true, true, false);
				if (offset >= 0) {
					textEditor.selectAndReveal(offset, string.length());
				}
				// if (document instanceof IAdaptable) {
				// IAdaptable adaptable = (IAdaptable) document;
				// FindReplaceDocumentAdapter findReplaceDocumentAdapter =
				// (FindReplaceDocumentAdapter)
				// adaptable.getAdapter(FindReplaceDocumentAdapter.class);
				// if (findReplaceDocumentAdapter != null) {
				// IRegion region = findReplaceDocumentAdapter.find(0, string,
				// true, true, false, false);
				// if (region != null) {
				// textEditor.selectAndReveal(region.getOffset(),
				// region.getLength());
				// }
				// }
				// }
			}
		} catch (BadLocationException e) {
			this.log(e);
		} catch (PartInitException e) {
			this.log(e);
		}
	}

}
