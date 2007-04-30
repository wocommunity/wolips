package tk.eclipse.plugin.csseditor.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import tk.eclipse.plugin.htmleditor.ColorProvider;
import tk.eclipse.plugin.htmleditor.HTMLPlugin;

/**
 * 
 * @author Naoki Takezoe
 */
public class CSSConfiguration extends SourceViewerConfiguration {
	
	private ColorProvider colorProvider;
	private RuleBasedScanner commentScanner;
	private CSSBlockScanner defaultScanner;
	
	public CSSConfiguration(ColorProvider colorProvider){
		this.colorProvider = colorProvider;
	}
	
	private RuleBasedScanner getCommentScanner(){
		if (commentScanner == null) {
			commentScanner = new RuleBasedScanner();
			commentScanner.setDefaultReturnToken(
					colorProvider.getToken(HTMLPlugin.PREF_COLOR_CSSCOMMENT));
		}
		return commentScanner;
	}
	
	private RuleBasedScanner getDefaultScanner(){
		if (defaultScanner == null) {
			defaultScanner = new CSSBlockScanner(colorProvider);
			defaultScanner.setDefaultReturnToken(
					colorProvider.getToken(HTMLPlugin.PREF_COLOR_FG));
		}
		return defaultScanner;
	}
	
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			CSSPartitionScanner.CSS_COMMENT};
	}
	
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant assistant = new ContentAssistant();
		assistant.enableAutoInsert(true);
		CSSAssistProcessor processor = new CSSAssistProcessor();
		assistant.setContentAssistProcessor(processor,IDocument.DEFAULT_CONTENT_TYPE);
		assistant.install(sourceViewer);
		
//		IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();
//		assistant.enableAutoActivation(store.getBoolean(HTMLPlugin.PREF_ASSIST_AUTO));
//		assistant.setAutoActivationDelay(store.getInt(HTMLPlugin.PREF_ASSIST_TIMES));
		
		return assistant;
	}
	
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();
		
		DefaultDamagerRepairer dr = null;
		
		dr = new DefaultDamagerRepairer(getDefaultScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		
		dr = new DefaultDamagerRepairer(getCommentScanner());
		reconciler.setDamager(dr, CSSPartitionScanner.CSS_COMMENT);
		reconciler.setRepairer(dr, CSSPartitionScanner.CSS_COMMENT);
		
		return reconciler;
	}
}
