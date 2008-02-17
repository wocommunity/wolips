package org.objectstyle.wolips.wooeditor.plisteditor;

import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class PlistConfiguration extends SourceViewerConfiguration {
	private PlistDoubleClickStrategy doubleClickStrategy;
	private PlistTokenScanner tokenScanner;
	private PlistScanner scanner;
	private IColorManager colorManager;

	public PlistConfiguration(IColorManager colorManager2) {
		this.colorManager = colorManager2;
	}
	@Override
  public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			PlistPartitionScanner.PROPERTY,
			PlistPartitionScanner.VALUE };
	}
	@Override
  public ITextDoubleClickStrategy getDoubleClickStrategy(
		ISourceViewer sourceViewer,
		String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new PlistDoubleClickStrategy();
		return doubleClickStrategy;
	}

	protected PlistScanner getPlistScanner() {
		if (scanner == null) {
			scanner = new PlistScanner(colorManager);
			scanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(IPlistColorConstants.DEFAULT))));
		}
		return scanner;
	}
	protected PlistTokenScanner getPlistTokenScanner() {
		if (tokenScanner == null) {
			tokenScanner = new PlistTokenScanner(colorManager);
			tokenScanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(IPlistColorConstants.VALUE))));
		}
		return tokenScanner;
	}

	@Override
  public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr =
			new DefaultDamagerRepairer(getPlistTokenScanner());
		reconciler.setDamager(dr, PlistPartitionScanner.PROPERTY);
		reconciler.setRepairer(dr, PlistPartitionScanner.PROPERTY);

		dr = new DefaultDamagerRepairer(getPlistScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

//		dr = new DefaultDamagerRepairer(getPlistScanner());
//		reconciler.setDamager(dr, PlistPartitionScanner.VALUE);
		return reconciler;
	}

}