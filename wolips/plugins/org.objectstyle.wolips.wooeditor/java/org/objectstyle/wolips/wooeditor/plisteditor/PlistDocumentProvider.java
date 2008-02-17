package org.objectstyle.wolips.wooeditor.plisteditor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

public class PlistDocumentProvider extends FileDocumentProvider {

	@Override
  protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner =
				new FastPartitioner(
					new PlistPartitionScanner(),
					new String[] {
						PlistPartitionScanner.PROPERTY,
						PlistPartitionScanner.VALUE }) {
//				public void connect(IDocument document, boolean delayInitialise)
//				{
//				    super.connect(document, delayInitialise);
//				    printPartitions(document);
//				}
//
//				public void printPartitions(IDocument document)
//				{
//				    StringBuffer buffer = new StringBuffer();
//
//				    ITypedRegion[] partitions = computePartitioning(0, document.getLength());
//				    for (int i = 0; i < partitions.length; i++)
//				    {
//				        try
//				        {
//				            buffer.append("Partition type: "
//				              + partitions[i].getType()
//				              + ", offset: " + partitions[i].getOffset()
//				              + ", length: " + partitions[i].getLength());
//				            buffer.append("\n");
//				            buffer.append("Text:\n");
//				            buffer.append(document.get(partitions[i].getOffset(),
//				             partitions[i].getLength()));
//				            buffer.append("\n---------------------------\n\n\n");
//				        }
//				        catch (BadLocationException e)
//				        {
//				            e.printStackTrace();
//				        }
//				    }
//				    System.out.print(buffer);
//				}
			};
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}
}