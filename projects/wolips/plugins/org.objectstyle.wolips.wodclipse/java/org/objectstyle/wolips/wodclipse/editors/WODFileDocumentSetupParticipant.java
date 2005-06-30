package org.objectstyle.wolips.wodclipse.editors;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;

public class WODFileDocumentSetupParticipant implements IDocumentSetupParticipant {
  public void setup(IDocument document) {
    setupDocument(document);
  }

  public static void setupDocument(IDocument document) {
    IDocumentPartitioner partitioner = createDocumentPartitioner();
    if (document instanceof IDocumentExtension3) {
      IDocumentExtension3 extension3 = (IDocumentExtension3) document;
      extension3.setDocumentPartitioner(IWODFilePartitions.WOD_FILE_PARTITIONING, partitioner);
    }
    else {
      document.setDocumentPartitioner(partitioner);
    }
    partitioner.connect(document);
  }

  private static IDocumentPartitioner createDocumentPartitioner() {
    return new FastPartitioner(new WODFilePartitionScanner(), IWODFilePartitions.PARTITIONS);
  }
}
