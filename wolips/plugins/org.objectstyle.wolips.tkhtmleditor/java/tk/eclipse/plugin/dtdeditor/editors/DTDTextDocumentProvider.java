package tk.eclipse.plugin.dtdeditor.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;

import tk.eclipse.plugin.htmleditor.editors.HTMLPartitionScanner;

/**
 * 
 * @author Naoki Takezoe
 */
public class DTDTextDocumentProvider extends TextFileDocumentProvider {
	
	protected FileInfo createFileInfo(Object element) throws CoreException {
		FileInfo info = super.createFileInfo(element);
		if(info==null){
			info = createEmptyFileInfo();
		}
		IDocument document = info.fTextFileBuffer.getDocument();
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
		return info;
	}
	
}
