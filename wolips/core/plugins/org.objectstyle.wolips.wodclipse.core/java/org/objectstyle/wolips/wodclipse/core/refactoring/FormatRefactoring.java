package org.objectstyle.wolips.wodclipse.core.refactoring;

import java.lang.reflect.InvocationTargetException;

import jp.aonir.fuzzyxml.FuzzyXMLDocType;
import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.FuzzyXMLProcessingInstruction;
import jp.aonir.fuzzyxml.internal.FuzzyXMLFormatComposite;
import jp.aonir.fuzzyxml.internal.RenderContext;
import jp.aonir.fuzzyxml.internal.WOHTMLRenderDelegate;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.preferences.PreferenceConstants;

public class FormatRefactoring implements IRunnableWithProgress {
  private WodParserCache _cache;

  public FormatRefactoring(WodParserCache cache) {
    _cache = cache;
  }

  public void run(IProgressMonitor monitor) throws InvocationTargetException {
    try {
      _cache.clearCache();

      FuzzyXMLDocument htmlModel = _cache.getHtmlEntry().getModel();
      FuzzyXMLElement documentElement = htmlModel.getDocumentElement();
      IDocument htmlDocument = _cache.getHtmlEntry().getDocument();

      IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
      RenderContext renderContext = new RenderContext(true);
      renderContext.setShowNewlines(true);
      renderContext.setIndentSize(prefs.getInt(PreferenceConstants.INDENT_SIZE));
      renderContext.setIndentTabs(prefs.getBoolean(PreferenceConstants.INDENT_TABS));
      renderContext.setTrim(true);
      renderContext.setLowercaseAttributes(prefs.getBoolean(PreferenceConstants.LOWERCASE_ATTRIBUTES));
      renderContext.setLowercaseTags(prefs.getBoolean(PreferenceConstants.LOWERCASE_TAGS));
      renderContext.setSpacesAroundEquals(prefs.getBoolean(PreferenceConstants.SPACES_AROUND_EQUALS));
      renderContext.setSpaceInEmptyTags(true);
      renderContext.setAddMissingQuotes(true);
      renderContext.setDelegate(new WOHTMLRenderDelegate(prefs.getBoolean(PreferenceConstants.STICKY_WOTAGS)));

      StringBuffer htmlBuffer = new StringBuffer();
      FuzzyXMLDocType docType = htmlModel.getDocumentType();
      for (FuzzyXMLNode node : documentElement.getChildren()) {
        if (docType != null) {
          if (!(node instanceof FuzzyXMLProcessingInstruction || FuzzyXMLFormatComposite.isHidden(node))) {
            docType.toXMLString(renderContext, htmlBuffer);
            docType = null;
          }
        }
        node.toXMLString(renderContext, htmlBuffer);
      }
      htmlDocument.set(htmlBuffer.toString().trim());
    }
    catch (Exception e) {
      throw new InvocationTargetException(e, "Failed to reformat.");
    }
  }

  public static void run(WodParserCache cache, IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException, CoreException {
    TemplateRefactoring.processHtmlAndWod(new FormatRefactoring(cache), cache, progressMonitor);
  }
}
