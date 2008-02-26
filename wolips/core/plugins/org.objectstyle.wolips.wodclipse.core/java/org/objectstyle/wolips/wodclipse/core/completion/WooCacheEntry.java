package org.objectstyle.wolips.wodclipse.core.completion;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.text.IDocument;
import org.objectstyle.wolips.bindings.wod.WodProblem;
import org.objectstyle.wolips.wodclipse.core.util.WodModelUtils;
import org.objectstyle.wolips.wodclipse.core.woo.WooModel;
import org.objectstyle.wolips.wodclipse.core.woo.WooModelException;

public class WooCacheEntry extends AbstractCacheEntry<WooModel> {
  public WooCacheEntry(WodParserCache cache) {
    super(cache);
  }

  @Override
  public void validate() throws Exception {
    setValidated(true);
    WooModel wooModel = _getModel();
    if (wooModel != null) {
      WodParserCache cache = getCache();
      IJavaProject javaProject = cache.getJavaProject();
      IType componentType = cache.getComponentType();
      List<WodProblem> wodProblems = wooModel.getProblems(javaProject, componentType, WodParserCache.getTypeCache(), WodParserCache.getModelGroupCache());
      IFile wooFile = getFile();
      if (wooFile != null && wooFile.exists()) {
        for (WodProblem wodProblem : wodProblems) {
          WodModelUtils.createMarker(wooFile, wodProblem);
        }
        try {
          wooModel.loadModelFromStream(wooFile.getContents());
        }
        catch (Throwable e) {
          WodModelUtils.createMarker(wooFile, new WodProblem(e.getMessage(), null, 0, false));
        }
      }
    }
  }

  @Override
  protected WooModel _parse(String contents) throws WooModelException {
    WooModel model = new WooModel(contents);
    return model;
  }

  @Override
  protected WooModel _parse(IDocument document, boolean updateCache) throws WooModelException {
    WooModel model = WodModelUtils.createWooModel(document);
    if (updateCache) {
      _setContents(document.get());
    }
    return model;
  }

  @Override
  protected WooModel _parse(IFile file, boolean updateCache) throws IOException, CoreException {
    WooModel model = WodModelUtils.createWooModel(file);
    if (updateCache) {
      _setContents(null);
      _setDocument(null);
    }
    return model;
  }
}
