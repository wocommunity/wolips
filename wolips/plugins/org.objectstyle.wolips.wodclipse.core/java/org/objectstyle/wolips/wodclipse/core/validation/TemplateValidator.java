package org.objectstyle.wolips.wodclipse.core.validation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.bindings.Activator;
import org.objectstyle.wolips.bindings.preferences.PreferenceConstants;
import org.objectstyle.wolips.bindings.wod.HtmlElementName;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.bindings.wod.IWodModel;
import org.objectstyle.wolips.bindings.wod.SimpleWodBinding;
import org.objectstyle.wolips.bindings.wod.WodBindingValueProblem;
import org.objectstyle.wolips.bindings.wod.WodProblem;
import org.objectstyle.wolips.locate.LocateException;
import org.objectstyle.wolips.wodclipse.core.completion.HtmlCacheEntry;
import org.objectstyle.wolips.wodclipse.core.completion.WodCacheEntry;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.util.FuzzyXMLWodElement;
import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;

public class TemplateValidator {
  private boolean _wo54;
  private WodParserCache _cache;
  private Set<FuzzyXMLElement> _woElements;

  public TemplateValidator(WodParserCache cache) {
    _cache = cache;
    _wo54 = Activator.getDefault().isWO54(cache.getProject());
  }

  /**
   * Validates the HTML document.
   * @throws Exception 
   */
  public void validate(FuzzyXMLDocument doc) throws Exception {
    visitDocument(doc, true);
  }

  public void visitDocument(FuzzyXMLDocument doc, boolean validate) throws Exception {
    if (doc != null) {
      _woElements = new HashSet<FuzzyXMLElement>();

      final List<InlineWodProblem> inlineProblems = new LinkedList<InlineWodProblem>();

      FuzzyXMLElement rootElement = doc.getDocumentElement();
      //FuzzyXMLElement rootElement = (FuzzyXMLElement) XPath.selectSingleNode(doc.getDocumentElement(), "*");
      visitElement(rootElement, inlineProblems, validate);

      if (validate) {
        HtmlCacheEntry htmlCacheEntry = _cache.getHtmlEntry();
        htmlCacheEntry.getHtmlElementCache().clearCache();
        WodCacheEntry wodCacheEntry = _cache.getWodEntry();
        IFile wodFile = wodCacheEntry.getFile();
        IWodModel wodModel = wodCacheEntry.getModel();
        for (FuzzyXMLElement woElement : _woElements) {
          String woElementName = woElement.getAttributeValue("name");
          int startOffset = woElement.getOffset();
          int endOffset = woElement.getOffset() + woElement.getLength();
          HtmlElementName elementName = new HtmlElementName(htmlCacheEntry.getFile(), woElementName, startOffset, endOffset);
          htmlCacheEntry.getHtmlElementCache().addHtmlElement(elementName);

          if (wodModel != null) {
            IWodElement wodElement = wodModel.getElementNamed(woElementName);
            if (wodElement == null) {
              WodProblem undefinedElement = new WodBindingValueProblem(wodElement, new SimpleWodBinding(null, "name", null), "name", "The element '" + woElementName + "' is not defined in " + wodFile.getName(), null, -1, false);
              inlineProblems.add(new InlineWodProblem(woElement, undefinedElement, _cache));
            }
          }
        }

        for (InlineWodProblem wodProblem : inlineProblems) {
          wodProblem.createProblemMarkers();
        }
      }
    }
  }

  private void visitElement(FuzzyXMLElement element, List<InlineWodProblem> inlineProblems, boolean validate) throws CoreException {
    if (element == null) {
      return;
    }

    String elementName = element.getName();
    if (WodHtmlUtils.isInline(elementName)) {
      if (validate) {
        IWodElement wodElement = new FuzzyXMLWodElement(element, _wo54);
        if (wodElement != null) {
          boolean validateBindingValues = Activator.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.VALIDATE_BINDING_VALUES);
          boolean validateOGNL = Activator.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.VALIDATE_OGNL_KEY);
          List<WodProblem> wodProblems = new LinkedList<WodProblem>();
          try {
            wodElement.fillInProblems(_cache.getJavaProject(), _cache.getComponentType(), validateBindingValues, wodProblems, WodParserCache.getTypeCache(), _cache.getHtmlEntry().getHtmlElementCache());
            inlineProblems.add(new InlineWodProblem(element, wodProblems, _cache));
          }
          catch (JavaModelException e) {
            try {
              WodParserCache.getTypeCache().clearCacheForType(_cache.getComponentType());
            }
            catch (LocateException e1) {
              e1.printStackTrace();
            }
          }
          catch (Throwable t) {
            Activator activator = Activator.getDefault();
            if (activator != null) {
              activator.log(t);
            }
          }
        }
      }
    }
    else if (WodHtmlUtils.isWOTag(elementName)) {
      String webobjectName = element.getAttributeValue("name");
      if (webobjectName == null) {
        if (validate) {
          inlineProblems.add(new InlineWodProblem(element, "webobject tag missing 'name' attribute", false, _cache));
        }
      }
      else {
        _woElements.add(element);
        if (validate && element.getAttributes().length > 1) {
          inlineProblems.add(new InlineWodProblem(element, "webobject tags should only have a 'name' attribute", true, _cache));
        }
      }
    }
    else {
      // System.out.println("TemplateValidator.validateElement: " + elementName);
    }

    FuzzyXMLNode[] nodes = element.getChildren();
    for (int i = 0; i < nodes.length; i++) {
      if (nodes[i] instanceof FuzzyXMLElement) {
        visitElement((FuzzyXMLElement) nodes[i], inlineProblems, validate);
      }
    }
  }
}
