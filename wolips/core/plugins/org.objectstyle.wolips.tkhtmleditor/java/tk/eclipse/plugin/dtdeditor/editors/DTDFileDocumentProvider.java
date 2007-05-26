package tk.eclipse.plugin.dtdeditor.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

import tk.eclipse.plugin.htmleditor.editors.HTMLPartitionScanner;

public class DTDFileDocumentProvider extends FileDocumentProvider {
	
	@Override
  public IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner =
				new FastPartitioner(
					new DTDPartitionScanner(),
					new String[] {
						HTMLPartitionScanner.HTML_TAG,
						HTMLPartitionScanner.HTML_COMMENT});
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}
	
}
