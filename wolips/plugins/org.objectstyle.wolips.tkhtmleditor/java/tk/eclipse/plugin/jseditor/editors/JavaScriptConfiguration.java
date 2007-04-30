package tk.eclipse.plugin.jseditor.editors;

import org.eclipse.jface.text.IAutoEditStrategy;
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
 * SourceViewerConfiguration implementation for JavaScriptEditor.
 * 
 * @see tk.eclipse.plugin.jseditor.editors.JavaScriptEditor
 * @author Naoki Takezoe
 */
public class JavaScriptConfiguration extends SourceViewerConfiguration {
	
	private ColorProvider colorProvider;
	private RuleBasedScanner commentScanner;
	private RuleBasedScanner defaultScanner;
	private JavaScriptAssistProcessor assistProcessor;
	
	public JavaScriptConfiguration(ColorProvider colorProvider){
		this.colorProvider = colorProvider;
	}
	
	private RuleBasedScanner getCommentScanner(){
		if (commentScanner == null) {
			commentScanner = new RuleBasedScanner();
			commentScanner.setDefaultReturnToken(
					colorProvider.getToken(HTMLPlugin.PREF_COLOR_JSCOMMENT));
		}
		return commentScanner;
	}
	
	private RuleBasedScanner getDefaultScanner(){
		if (defaultScanner == null) {
			defaultScanner = new JavaScriptScanner(colorProvider);
			defaultScanner.setDefaultReturnToken(
					colorProvider.getToken(HTMLPlugin.PREF_COLOR_FG));
		}
		return defaultScanner;
	}
	
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			JavaScriptPartitionScanner.JS_COMMENT};
	}
	
	public JavaScriptAssistProcessor getAssistProcessor(){
		if(assistProcessor==null){
			assistProcessor = new JavaScriptAssistProcessor();
		}
		return assistProcessor;
	}
	
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant assistant = new ContentAssistant();
		assistant.enableAutoInsert(true);
		assistant.setContentAssistProcessor(getAssistProcessor(),IDocument.DEFAULT_CONTENT_TYPE);
		assistant.install(sourceViewer);
		
		return assistant;
	}
	
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();
		
		DefaultDamagerRepairer dr = null;
		
		dr = new DefaultDamagerRepairer(getDefaultScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		
		dr = new DefaultDamagerRepairer(getCommentScanner());
		reconciler.setDamager(dr, JavaScriptPartitionScanner.JS_COMMENT);
		reconciler.setRepairer(dr, JavaScriptPartitionScanner.JS_COMMENT);
		
		return reconciler;
	}

	/**
	 * @since 2.0.3
	 */
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		return new IAutoEditStrategy[]{
			new JavaScriptAutoEditStrategy()
		};
	}

}
