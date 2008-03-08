package org.objectstyle.wolips.wodclipse.core.document;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.PlatformUI;
import org.objectstyle.wolips.bindings.wod.BindingValueKey;
import org.objectstyle.wolips.bindings.wod.BindingValueKeyPath;
import org.objectstyle.wolips.bindings.wod.IWodBinding;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.bindings.wod.TypeCache;
import org.objectstyle.wolips.locate.LocateException;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.refactoring.AddActionDialog;
import org.objectstyle.wolips.wodclipse.core.refactoring.AddActionInfo;
import org.objectstyle.wolips.wodclipse.core.refactoring.AddKeyDialog;
import org.objectstyle.wolips.wodclipse.core.refactoring.AddKeyInfo;

public class WodBindingValueHyperlink implements IHyperlink {
  private IWodElement _element;
  private IWodBinding _binding;
  private TypeCache _cache;
  private IRegion _region;
  private String _bindingValue;
  private IType _componentType;

  public WodBindingValueHyperlink(IWodElement element, IWodBinding binding, IRegion region, String bindingValue, IType componentType, TypeCache cache) {
    _element = element;
    _binding = binding;
    _region = region;
    _bindingValue = bindingValue;
    _componentType = componentType;
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
      BindingValueKeyPath bindingValueKeyPath = new BindingValueKeyPath(_bindingValue, _componentType, _componentType.getJavaProject(), WodParserCache.getTypeCache());
      if (bindingValueKeyPath.isValid()) {
        BindingValueKey lastKey = bindingValueKeyPath.getLastBindingKey();
        if (lastKey != null) {
          IMember member = lastKey.getBindingMember();
          if (member != null) {
            JavaUI.openInEditor(member, true, true);
          }
        }
        else if (bindingValueKeyPath.getLength() == 1) {
          if (_binding.isAction()) {
            AddActionInfo info = new AddActionInfo(_componentType);
            info.setName(bindingValueKeyPath.getOriginalKeyPath());
            AddActionDialog.open(info, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
          }
          else {
            AddKeyInfo info = new AddKeyInfo(_componentType);
            info.setName(bindingValueKeyPath.getOriginalKeyPath());
            AddKeyDialog.open(info, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
          }
        }
      }
    }
    catch (Exception ex) {
      Activator.getDefault().log(ex);
    }
  }

  public static WodBindingValueHyperlink toBindingValueHyperlink(IWodElement wodElement, String bindingName, WodParserCache cache) throws JavaModelException, CoreException, LocateException {
    WodBindingValueHyperlink hyperlink = null;
    IWodBinding wodBinding = wodElement.getBindingNamed(bindingName);
    if (wodBinding != null && wodBinding.isKeyPath()) {
      Position valuePosition = wodBinding.getValuePosition();
      if (valuePosition != null) {
        Region elementRegion = new Region(valuePosition.getOffset(), valuePosition.getLength());
        IType componentType = cache.getComponentType();
        if (componentType != null) {
          hyperlink = new WodBindingValueHyperlink(wodElement, wodBinding, elementRegion, wodBinding.getValue(), componentType, WodParserCache.getTypeCache());
        }
      }
    }
    return hyperlink;
  }
}