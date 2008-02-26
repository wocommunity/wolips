package org.objectstyle.wolips.wodclipse.core.document;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.objectstyle.wolips.bindings.utils.BindingReflectionUtils;
import org.objectstyle.wolips.bindings.wod.BindingValueKey;
import org.objectstyle.wolips.bindings.wod.BindingValueKeyPath;
import org.objectstyle.wolips.bindings.wod.IWodBinding;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.bindings.wod.TypeCache;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;

public class WodBindingNameHyperlink implements IHyperlink {
  private IJavaProject _javaProject;
  private TypeCache _cache;
  private IRegion _region;
  private String _bindingName;
  private String _elementTypeName;

  public WodBindingNameHyperlink(IRegion region, String bindingName, IJavaProject javaProject, String elementTypeName, TypeCache cache) {
    _region = region;
    _bindingName = bindingName;
    _javaProject = javaProject;
    _elementTypeName = elementTypeName;
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
      IType elementType = BindingReflectionUtils.findElementType(_javaProject, _elementTypeName, false, _cache);
      if (elementType != null) {
        BindingValueKeyPath bindingValueKeyPath = new BindingValueKeyPath(_bindingName, elementType, elementType.getJavaProject(), WodParserCache.getTypeCache());
        if (bindingValueKeyPath.isValid()) {
          BindingValueKey lastKey = bindingValueKeyPath.getLastBindingKey();
          if (lastKey != null) {
            IMember member = lastKey.getBindingMember();
            if (member != null) {
              JavaUI.openInEditor(member, true, true);
            }
            else {
              JavaUI.openInEditor(elementType, true, true);
            }
          }
          else {
            JavaUI.openInEditor(elementType, true, true);
          }
        }
      }
    }
    catch (Exception ex) {
      Activator.getDefault().log(ex);
    }
  }

  public static WodBindingNameHyperlink toBindingNameHyperlink(IWodElement wodElement, String bindingName, WodParserCache cache) {
    WodBindingNameHyperlink hyperlink = null;
    IWodBinding wodBinding = wodElement.getBindingNamed(bindingName);
    if (wodBinding != null) {
      Position namePosition = wodBinding.getNamePosition();
      if (namePosition != null) {
        Region elementRegion = new Region(namePosition.getOffset(), namePosition.getLength());
        String elementTypeName = wodElement.getElementType();
        if (elementTypeName != null) {
          hyperlink = new WodBindingNameHyperlink(elementRegion, bindingName, cache.getJavaProject(), elementTypeName, WodParserCache.getTypeCache());
        }
      }
    }
    return hyperlink;
  }
}