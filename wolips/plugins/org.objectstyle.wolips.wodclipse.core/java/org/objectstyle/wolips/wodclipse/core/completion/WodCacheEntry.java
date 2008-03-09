package org.objectstyle.wolips.wodclipse.core.completion;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.bindings.wod.HtmlElementCache;
import org.objectstyle.wolips.bindings.wod.HtmlElementName;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.bindings.wod.IWodElementProblem;
import org.objectstyle.wolips.bindings.wod.IWodModel;
import org.objectstyle.wolips.bindings.wod.WodElementProblem;
import org.objectstyle.wolips.bindings.wod.WodProblem;
import org.objectstyle.wolips.wodclipse.core.document.WodFileDocumentProvider;
import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;
import org.objectstyle.wolips.wodclipse.core.util.WodModelUtils;

public class WodCacheEntry extends AbstractCacheEntry<IWodModel> {
  public WodCacheEntry(WodParserCache cache) {
    super(cache);
  }

  @Override
  public void validate() throws Exception {
    setValidated(true);
    IWodModel wodModel = _getModel();
    if (wodModel != null) {
      WodParserCache cache = getCache();
      IJavaProject javaProject = cache.getJavaProject();
      IType componentType = cache.getComponentType();
      HtmlElementCache htmlElementCache = cache.getHtmlEntry().getHtmlElementCache();
      List<WodProblem> wodProblems = wodModel.getProblems(javaProject, componentType, WodParserCache.getTypeCache(), htmlElementCache);
      IFile wodFile = getFile();
      if (wodFile != null && wodFile.exists()) {
        IFile htmlFile = cache.getHtmlEntry().getFile();
        boolean createHtmlMarkers = htmlFile != null && htmlFile.exists();
        for (WodProblem wodProblem : wodProblems) {
          WodModelUtils.createMarker(wodFile, wodProblem);

          // We create HTML markers for WOD problems so that you can have the
          // wod view closed and still see errors
          if (createHtmlMarkers && wodProblem instanceof IWodElementProblem) {
            IWodElement element = ((IWodElementProblem) wodProblem).getElement();
            if (element != null) {
              List<HtmlElementName> htmlElementNames = htmlElementCache.getHtmlElementNames(element.getElementName());
              if (htmlElementNames != null) {
                for (HtmlElementName htmlElementName : htmlElementNames) {
                  int lineNumber = WodHtmlUtils.getLineAtOffset(cache.getHtmlEntry().getContents(), htmlElementName.getStartOffset());
                  WodElementProblem htmlProblem = new WodElementProblem(element, "In the WOD, " + wodProblem.getMessage(), new Position(htmlElementName.getStartOffset(), htmlElementName.getEndOffset() - htmlElementName.getStartOffset() + 1), lineNumber, wodProblem.isWarning());
                  WodModelUtils.createMarker(htmlFile, htmlProblem);
                }
              }
            }
          }
        }
      }
    }
  }

  @Override
  protected IWodModel _parse(String contents) {
    //_wodModel = WodModelUtils.createWodModel(_wodFile, _wodDocument);
    return null;
  }

  @Override
  protected IWodModel _parse(IDocument document, boolean updateCache) {
    IFile wodFile = getFile();
    IWodModel model = WodModelUtils.createWodModel(wodFile, document);
    _setContents(document.get());
    return model;
  }

  @Override
  protected IWodModel _parse(IFile file, boolean updateCache) throws Exception {
    IWodModel model;
    FileEditorInput input = new FileEditorInput(file);
    WodFileDocumentProvider provider = new WodFileDocumentProvider();
    provider.connect(input);
    try {
      IDocument document = provider.getDocument(input);
      model = parse(document, updateCache);
    }
    finally {
      provider.disconnect(input);
    }
    return model;
  }
}
