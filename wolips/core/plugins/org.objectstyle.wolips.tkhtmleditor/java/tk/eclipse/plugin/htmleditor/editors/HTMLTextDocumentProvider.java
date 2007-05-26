package tk.eclipse.plugin.htmleditor.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;

public class HTMLTextDocumentProvider extends TextFileDocumentProvider {
	
	@Override
  protected FileInfo createFileInfo(Object element) throws CoreException {
		FileInfo info = super.createFileInfo(element);
		if(info==null){
			info = createEmptyFileInfo();
		}
		IDocument document = info.fTextFileBuffer.getDocument();
		if (document != null) {
			IDocumentPartitioner partitioner =
				new FastPartitioner(
					new HTMLPartitionScanner(),
					new String[] {
						HTMLPartitionScanner.HTML_TAG,
						HTMLPartitionScanner.HTML_COMMENT,
						HTMLPartitionScanner.HTML_SCRIPT,
						HTMLPartitionScanner.HTML_DOCTYPE,
						HTMLPartitionScanner.HTML_DIRECTIVE,
						HTMLPartitionScanner.JAVASCRIPT,
						HTMLPartitionScanner.HTML_CSS});
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return info;
	}
	
}