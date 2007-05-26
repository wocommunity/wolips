package tk.eclipse.plugin.htmleditor.editors;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;

import tk.eclipse.plugin.htmleditor.ColorProvider;
import tk.eclipse.plugin.htmleditor.HTMLHyperlinkDetector;
import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.assist.HTMLAssistProcessor;
import tk.eclipse.plugin.htmleditor.assist.InnerCSSAssistProcessor;
import tk.eclipse.plugin.htmleditor.assist.InnerJavaScriptAssistProcessor;

/**
 * <code>SourceViewerConfiguration</code> for <code>HTMLSourceEditor</code>.
 * 
 * @author Naoki Takezoe
 * @see HTMLSourceEditor
 */
public class HTMLConfiguration extends SourceViewerConfiguration {
	
	private HTMLDoubleClickStrategy _doubleClickStrategy;
	
	private HTMLScanner _scanner;
	private HTMLTagScanner _tagScanner;
	private RuleBasedScanner _commentScanner;
	private RuleBasedScanner _scriptScanner;
	private RuleBasedScanner _doctypeScanner;
	private RuleBasedScanner _directiveScanner;
	private RuleBasedScanner _javaScriptScanner;
	private RuleBasedScanner _cssScanner;
	
	private ColorProvider _colorProvider;
	
	private IEditorPart _editor;

	private ContentAssistant _assistant;
	private HTMLAssistProcessor _processor;
	private InnerJavaScriptAssistProcessor _jsProcessor;
	private InnerCSSAssistProcessor _cssProcessor;
	
	private HTMLAutoEditStrategy _autoEditStrategy;
	private HTMLHyperlinkDetector _hyperlinkDetector;

	public HTMLConfiguration(ColorProvider colorProvider) {
		this._colorProvider = colorProvider;
	}
	
  public IEditorPart getEditorPart() {
    return _editor;
  }
  
	public void setEditorPart(IEditorPart editor){
		this._editor = editor;
	}
	
	@Override
  public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		return new HTMLAnnotationHover(_editor);
	}
	
	/**
	 * @since 2.0,3
	 */
	protected HTMLAutoEditStrategy createAutoEditStrategy(){
		return new HTMLAutoEditStrategy();
	}
	
	/**
	 * @since 2.0.3
	 */
	protected HTMLHyperlinkDetector createHyperlinkDetector(){
		return new HTMLHyperlinkDetector();
	}
	
	/**
	 * @since 2.0.3
	 */
	public final HTMLHyperlinkDetector getHyperlinkDetector(){
		if(this._hyperlinkDetector==null){
			this._hyperlinkDetector = createHyperlinkDetector();
		}
		return this._hyperlinkDetector;
	}
	
	/**
	 * @since 2.0.3
	 */
	public final HTMLAutoEditStrategy getAutoEditStrategy(){
		if(this._autoEditStrategy==null){
			this._autoEditStrategy = createAutoEditStrategy();
		}
		return this._autoEditStrategy;
	}
	
	/**
	 * Returns all supportted content types.
	 */
	@Override
  public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			HTMLPartitionScanner.HTML_COMMENT,
			HTMLPartitionScanner.HTML_TAG,
			HTMLPartitionScanner.HTML_SCRIPT,
			HTMLPartitionScanner.HTML_DOCTYPE,
			HTMLPartitionScanner.HTML_DIRECTIVE,
			HTMLPartitionScanner.JAVASCRIPT,
			HTMLPartitionScanner.HTML_CSS};
	}
	
	/**
	 * @since 2.0.3
	 */
	@Override
  public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		if (sourceViewer == null){
			return null;
		}
		
		IHyperlinkDetector[] detectors = super.getHyperlinkDetectors(sourceViewer);
		IHyperlinkDetector[] result = new IHyperlinkDetector[detectors.length + 1];
		System.arraycopy(detectors, 0, result, 0, detectors.length);
		result[result.length-1] = getHyperlinkDetector();
		
		return result;
	}

	/**
	 * @since 2.0.3
	 */
	@Override
  public final IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		return new IAutoEditStrategy[]{ createAutoEditStrategy() };
	}
	
	/**
	 * Creates or Returns the assist processor for HTML.
	 */
	public HTMLAssistProcessor getAssistProcessor(){
		if(_processor==null){
			_processor = createAssistProcessor();
		}
		return _processor;
	}
	
	/**
	 * Creates the assist processor for HTML.
	 */
	protected HTMLAssistProcessor createAssistProcessor(){
		HTMLAssistProcessor processor = new HTMLAssistProcessor();
		return processor;
	}
	
	/**
	 * Creates or Returns the assist processor for inner JavaScript.
	 */
	public InnerJavaScriptAssistProcessor getJavaScriptAssistProcessor(){
		if(_jsProcessor==null){
			_jsProcessor = new InnerJavaScriptAssistProcessor(getAssistProcessor());
		}
		return _jsProcessor;
	}
	
	/**
	 * Creates or Returns the assist processor for inner JavaScript.
	 */
	public InnerCSSAssistProcessor getCSSAssistProcessor(){
		if(_cssProcessor==null){
			_cssProcessor = new InnerCSSAssistProcessor(getAssistProcessor());
		}
		return _cssProcessor;
	}
	
	/**
	 * Creates or Returns the <code>IContentAssistant</code>.
	 */
	@Override
  public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		if(_assistant==null){
			_assistant = new ContentAssistant();
			_assistant.setInformationControlCreator(new IInformationControlCreator() {
				public IInformationControl createInformationControl(Shell parent) {
					return new DefaultInformationControl(parent);
				}});
			_assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
			_assistant.enableAutoInsert(true);
			
			HTMLAssistProcessor processor = getAssistProcessor();
			_assistant.setContentAssistProcessor(processor,IDocument.DEFAULT_CONTENT_TYPE);
			_assistant.setContentAssistProcessor(processor,HTMLPartitionScanner.HTML_TAG);
			
			InnerJavaScriptAssistProcessor jsProcessor = getJavaScriptAssistProcessor();
			_assistant.setContentAssistProcessor(jsProcessor,HTMLPartitionScanner.JAVASCRIPT);
			
			InnerCSSAssistProcessor cssProcessor = getCSSAssistProcessor();
			_assistant.setContentAssistProcessor(cssProcessor,HTMLPartitionScanner.HTML_CSS);
			
			_assistant.install(sourceViewer);
			
			// ï‚äÆÇÃê›íËÇîΩâf
			IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();
			_assistant.enableAutoActivation(store.getBoolean(HTMLPlugin.PREF_ASSIST_AUTO));
			_assistant.setAutoActivationDelay(store.getInt(HTMLPlugin.PREF_ASSIST_TIMES));
			processor.setAutoAssistChars(store.getString(HTMLPlugin.PREF_ASSIST_CHARS).toCharArray());
			processor.setAssistCloseTag(store.getBoolean(HTMLPlugin.PREF_ASSIST_CLOSE));
		}
		return _assistant;
	}
	
	/**
	 * Returns <code>ITextDoubleClickStrategy</code>.
	 */
	@Override
  public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer,String contentType) {
		if (_doubleClickStrategy == null){
			_doubleClickStrategy = new HTMLDoubleClickStrategy();
		}
		return _doubleClickStrategy;
	}
	
	/**
	 * Creates or Returns the scanner for HTML.
	 */
	protected HTMLScanner getHTMLScanner() {
		if (_scanner == null) {
			_scanner = new HTMLScanner(_colorProvider);
			_scanner.setDefaultReturnToken(
					_colorProvider.getToken(HTMLPlugin.PREF_COLOR_FG));
		}
		return _scanner;
	}
	
	/**
	 * Creates or Returns the scanner for HTML tags.
	 */
	protected HTMLTagScanner getTagScanner() {
		if (_tagScanner == null) {
			_tagScanner = new HTMLTagScanner(_colorProvider);
			_tagScanner.setDefaultReturnToken(
					_colorProvider.getToken(HTMLPlugin.PREF_COLOR_TAG));
		}
		return _tagScanner;
	}
	
	/**
	 * Creates or Returns the scanner for HTML comments.
	 */
	protected RuleBasedScanner getCommentScanner() {
		if (_commentScanner == null) {
			_commentScanner = new RuleBasedScanner();
			_commentScanner.setDefaultReturnToken(
					_colorProvider.getToken(HTMLPlugin.PREF_COLOR_COMMENT));
		}
		return _commentScanner;
	}
	
	/**
	 * Creates or Returns the scanner for scriptlets (&lt;% ... %&gt;).
	 */
	protected RuleBasedScanner getScriptScanner() {
		if (_scriptScanner == null) {
			_scriptScanner = new RuleBasedScanner();
			_scriptScanner.setDefaultReturnToken(
					_colorProvider.getToken(HTMLPlugin.PREF_COLOR_SCRIPT));
		}
		return _scriptScanner;
	}
	
	/**
	 * Creates or Returns the scanner for directives (&lt;%@ ... %&gt;).
	 */
	protected RuleBasedScanner getDirectiveScanner(){
		if (_directiveScanner == null) {
			_directiveScanner = new RuleBasedScanner();
			_directiveScanner.setDefaultReturnToken(
					_colorProvider.getToken(HTMLPlugin.PREF_COLOR_SCRIPT));
		}
		return _directiveScanner;
	}
	
	/**
	 * Creates or Returns the scanner for DOCTYPE decl.
	 */
	protected RuleBasedScanner getDoctypeScanner(){
		if (_doctypeScanner == null) {
			_doctypeScanner = new RuleBasedScanner();
			_doctypeScanner.setDefaultReturnToken(
					_colorProvider.getToken(HTMLPlugin.PREF_COLOR_DOCTYPE));
		}
		return _doctypeScanner;
	}
	
	/**
	 * Creates or Returns the scanner for inner JavaScript.
	 */
	protected RuleBasedScanner getJavaScriptScanner() {
		if (_javaScriptScanner == null) {
			_javaScriptScanner = new InnerJavaScriptScanner(_colorProvider);
			_javaScriptScanner.setDefaultReturnToken(
					_colorProvider.getToken(HTMLPlugin.PREF_COLOR_FG));
		}
		return _javaScriptScanner;
	}
	
	/**
	 * Creates or Returns the scanner for inner CSS.
	 */
	protected RuleBasedScanner getCSSScanner() {
		if (_cssScanner == null) {
			_cssScanner = new InnerCSSScanner(_colorProvider);
			_cssScanner.setDefaultReturnToken(
					_colorProvider.getToken(HTMLPlugin.PREF_COLOR_FG));
		}
		return _cssScanner;
	}
	
	@Override
  public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = null;
		
		dr = new HTMLTagDamagerRepairer(getTagScanner());
		reconciler.setDamager(dr, HTMLPartitionScanner.HTML_TAG);
		reconciler.setRepairer(dr, HTMLPartitionScanner.HTML_TAG);
		
		dr = new HTMLTagDamagerRepairer(getHTMLScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr = new HTMLTagDamagerRepairer(getCommentScanner());
		reconciler.setDamager(dr, HTMLPartitionScanner.HTML_COMMENT);
		reconciler.setRepairer(dr, HTMLPartitionScanner.HTML_COMMENT);
		
		dr = new DefaultDamagerRepairer(getScriptScanner());
		reconciler.setDamager(dr, HTMLPartitionScanner.HTML_SCRIPT);
		reconciler.setRepairer(dr, HTMLPartitionScanner.HTML_SCRIPT);
		
		dr = new DefaultDamagerRepairer(getDoctypeScanner());
		reconciler.setDamager(dr, HTMLPartitionScanner.HTML_DOCTYPE);
		reconciler.setRepairer(dr, HTMLPartitionScanner.HTML_DOCTYPE);
		
		dr = new DefaultDamagerRepairer(getDirectiveScanner());
		reconciler.setDamager(dr, HTMLPartitionScanner.HTML_DIRECTIVE);
		reconciler.setRepairer(dr, HTMLPartitionScanner.HTML_DIRECTIVE);
		
		dr = new JavaScriptDamagerRepairer(getJavaScriptScanner());
		reconciler.setDamager(dr, HTMLPartitionScanner.JAVASCRIPT);
		reconciler.setRepairer(dr, HTMLPartitionScanner.JAVASCRIPT);
		
		dr = new JavaScriptDamagerRepairer(getCSSScanner());
		reconciler.setDamager(dr, HTMLPartitionScanner.HTML_CSS);
		reconciler.setRepairer(dr, HTMLPartitionScanner.HTML_CSS);
    
		return reconciler;
	}
	
	/**
	 * Returns the <code>ColorProvider</code>.
	 */
	protected ColorProvider getColorProvider(){
		return this._colorProvider;
	}
	
	private class HTMLTagDamagerRepairer extends DefaultDamagerRepairer {
		
		public HTMLTagDamagerRepairer(ITokenScanner scanner) {
			super(scanner);
		}
		
		// TODO This method works with 3.0 and 3.1.2 but does't work well with Eclipse 3.1.1.
		@Override
    public IRegion getDamageRegion(ITypedRegion partition, DocumentEvent e, boolean documentPartitioningChanged) {
			if (!documentPartitioningChanged) {
				String source = fDocument.get();
				int start = source.substring(0, e.getOffset()).lastIndexOf('<');
				if(start == -1){
					start = 0;
				}
				int end = source.indexOf('>', e.getOffset());
				int nextEnd = source.indexOf('>', end + 1);
				if(nextEnd >= 0 && nextEnd > end){
					end = nextEnd;
				}
				int end2 = e.getOffset() + (e.getText() == null ? e.getLength() : e.getText().length());
				if(end == -1){
					end = source.length();
				} else if(end2 > end){
					end = end2;
				} else {
					end++;
				}
				
				return new Region(start, end - start);
			}
			return partition;
		}
		
	}

	private class JavaScriptDamagerRepairer extends DefaultDamagerRepairer {
		
		public JavaScriptDamagerRepairer(ITokenScanner scanner) {
			super(scanner);
		}
		
		// TODO This method works with 3.0 and 3.1.2 but does't work well with Eclipse 3.1.1.
		@Override
    public IRegion getDamageRegion(ITypedRegion partition, DocumentEvent e, boolean documentPartitioningChanged) {
			if (!documentPartitioningChanged) {
				String source = fDocument.get();
				int start = source.substring(0, e.getOffset()).lastIndexOf("/*");
				if(start == -1){
					start = 0;
				}
				int end = source.indexOf("*/", e.getOffset());
				int end2 = e.getOffset() + (e.getText() == null ? e.getLength() : e.getText().length());
				if(end == -1){
					end = source.length();
				} else if(end2 > end){
					end = end2;
				} else {
					end++;
				}
				
				return new Region(start, end - start);
			}
			return partition;
		}
		
	}
	
}