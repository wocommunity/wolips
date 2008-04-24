package org.objectstyle.wolips.wodclipse.core.completion;

import java.util.LinkedList;
import java.util.List;

import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLParser;
import jp.aonir.fuzzyxml.event.FuzzyXMLErrorEvent;
import jp.aonir.fuzzyxml.event.FuzzyXMLErrorListener;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.Position;
import org.objectstyle.wolips.bindings.Activator;
import org.objectstyle.wolips.bindings.preferences.PreferenceConstants;
import org.objectstyle.wolips.bindings.wod.HtmlElementCache;
import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;
import org.objectstyle.wolips.wodclipse.core.validation.HtmlProblem;
import org.objectstyle.wolips.wodclipse.core.validation.TemplateValidator;

public class HtmlCacheEntry extends AbstractCacheEntry<FuzzyXMLDocument> implements FuzzyXMLErrorListener {
  private HtmlElementCache _htmlElementCache;
  private List<HtmlProblem> _parserProblems;

  public HtmlCacheEntry(WodParserCache cache) {
    super(cache);
  }

  public HtmlElementCache getHtmlElementCache() throws Exception {
    getCache().parse();
    getCache().validate();
    return _htmlElementCache;
  }

  @Override
  public void validate() throws Exception {
    setValidated(true);
    IFile htmlFile = getFile();
    FuzzyXMLDocument htmlXmlDocument = _getModel();
    if (htmlXmlDocument != null && (htmlFile == null || htmlFile.exists())) {
      boolean errorOnHtmlErrorsKey = Activator.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.ERROR_ON_HTML_ERRORS_KEY);
      if (errorOnHtmlErrorsKey) {
        if (htmlFile != null && htmlFile.exists()) {
          for (HtmlProblem problem : getParserProblems()) {
            problem.createMarker(htmlFile);
          }
        }
      }
      new TemplateValidator(getCache()).validate(htmlXmlDocument);
    }
  }

  @Override
  protected String _process(String contents) {
    String processedHtmlContents = contents;
    processedHtmlContents = processedHtmlContents.replaceAll("\r\n", " \n");
    processedHtmlContents = processedHtmlContents.replaceAll("\r", "\n");
    return processedHtmlContents;
  }

  @Override
  protected FuzzyXMLDocument _parse(String contents) {
    FuzzyXMLParser parser = new FuzzyXMLParser(Activator.getDefault().isWO54(), true);
    parser.addErrorListener(this);
    FuzzyXMLDocument htmlXmlDocument = parser.parse(contents);
    return htmlXmlDocument;
  }

  @Override
  public void clear() {
    super.clear();
    _parserProblems = new LinkedList<HtmlProblem>();
    _htmlElementCache = new HtmlElementCache();
  }

  public void error(FuzzyXMLErrorEvent event) {
    int offset = event.getOffset();
    int length = event.getLength();
    String message = event.getMessage();
    IFile htmlFile = getFile();
    String contents = getContents();
    HtmlProblem problem = new HtmlProblem(htmlFile, message, new Position(offset, length), WodHtmlUtils.getLineAtOffset(contents, offset), false);
    _parserProblems.add(problem);
  }

  public List<HtmlProblem> getParserProblems() {
    return _parserProblems;
  }
}
