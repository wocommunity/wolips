package org.objectstyle.wolips.wodclipse.core.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.objectstyle.wolips.locate.LocatePlugin;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.util.WodReflectionUtils;
import org.objectstyle.wolips.workbenchutilities.WorkbenchUtilitiesPlugin;

public class WodHyperlink implements IHyperlink {
  private WodParserCache _cache;

  private IRegion _region;

  private String _elementType;

  public WodHyperlink(IRegion region, String elementType, WodParserCache cache) {
    _region = region;
    _elementType = elementType;
    _cache = cache;
  }

  public IRegion getHyperlinkRegion() {
    return _region;
  }

  public String getTypeLabel() {
    return null;
  }

  public String getHyperlinkText() {
    return null;
  }

  public void open() {
    try {
      IType type = WodReflectionUtils.findElementType(_cache.getJavaProject(), _elementType, false, _cache);
      if (type != null) {
        IJavaElement element = type.getPrimaryElement();
        if (element != null) {
          JavaUI.revealInEditor(JavaUI.openInEditor(element), element);
          LocalizedComponentsLocateResult componentsLocateResults = LocatePlugin.getDefault().getLocalizedComponentsLocateResult(element.getResource());
          IFile wodFile = componentsLocateResults.getFirstWodFile();
          if (wodFile != null) {
            WorkbenchUtilitiesPlugin.open(wodFile, "org.objectstyle.wolips.componenteditor.ComponentEditor");
          }
        }
      }
    }
    catch (Exception ex) {
      Activator.getDefault().log(ex);
    }
  }
}