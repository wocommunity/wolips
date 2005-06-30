package org.objectstyle.wolips.wodclipse.editors;

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

public class WODFileDocumentProvider extends TextFileDocumentProvider {
  public static final String WOD_FILE_CONTENT_TYPE_STRING = "com.mdimension.wodclipse.wod";
  public static final IContentType WOD_FILE_CONTENT_TYPE = Platform.getContentTypeManager().getContentType(WODFileDocumentProvider.WOD_FILE_CONTENT_TYPE_STRING); //$NON-NLS-1$

  public WODFileDocumentProvider() {
    IDocumentProvider provider = new TextFileDocumentProvider();
    provider = new ForwardingDocumentProvider(IWODFilePartitions.WOD_FILE_PARTITIONING, new WODFileDocumentSetupParticipant(), provider);
    setParentDocumentProvider(provider);
  }

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

  protected DocumentProviderOperation createSaveOperation(final Object element, final IDocument document, final boolean overwrite) throws CoreException {
    if (getFileInfo(element) == null) {
      return null;
    }

    return super.createSaveOperation(element, document, overwrite);
  }
}
