package org.objectstyle.wolips.templateeditor;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.bindings.api.ApiCache;
import org.objectstyle.wolips.bindings.utils.BindingReflectionUtils;
import org.objectstyle.wolips.bindings.wod.TagShortcut;
import org.objectstyle.wolips.bindings.wod.TypeCache;
import org.objectstyle.wolips.wodclipse.core.completion.WodCompletionProposal;
import org.objectstyle.wolips.wodclipse.core.completion.WodCompletionUtils;

import tk.eclipse.plugin.htmleditor.assist.AttributeInfo;
import tk.eclipse.plugin.htmleditor.assist.TagInfo;

public class InlineWodTagInfo extends TagInfo {
  private String _elementTypeName;
  private TagShortcut _tagShortcut;
  private IJavaProject _javaProject;
  private boolean _attributeInfoCached;
  private TypeCache _cache;

  public InlineWodTagInfo(String elementTypeName, TypeCache cache) {
    super("wo:" + elementTypeName, true, true);
    setRequiresAttributes(true);
    _cache = cache;
    _elementTypeName = elementTypeName;
    _tagShortcut = ApiCache.getTagShortcutNamed(elementTypeName);
  }

  public void setJavaProject(IJavaProject javaProject) {
    _javaProject = javaProject;
  }

  public IJavaProject getJavaProject() {
    return _javaProject;
  }
  
  public IType getElementType() {
	  IType elementType = null;
	  try {
		  elementType = BindingReflectionUtils.findElementType(_javaProject, getExpandedElementTypeName(), false, _cache);
	} catch (JavaModelException e) {
		// ignore;
	}
	  return elementType;
  }

  public String getElementTypeName() {
    return _elementTypeName;
  }

  public String getExpandedElementTypeName() {
    String elementTypeName = _elementTypeName;
    if (_tagShortcut != null) {
      elementTypeName = _tagShortcut.getActual();
    }
    return elementTypeName;
  }

  protected void loadAttributeInfo() {
    if (!_attributeInfoCached) {
      IType elementType;
      try {
        elementType = BindingReflectionUtils.findElementType(_javaProject, getExpandedElementTypeName(), false, _cache);
        if (elementType != null) {
          Set<WodCompletionProposal> proposals = new HashSet<WodCompletionProposal>();
          WodCompletionUtils.fillInBindingNameCompletionProposals(_javaProject, elementType, "", 0, 0, proposals, false, _cache);
          for (WodCompletionProposal proposal : proposals) {
            AttributeInfo attrInfo = new AttributeInfo(proposal.getProposal(), true);
            addAttributeInfo(attrInfo);
          }
        }
        _attributeInfoCached = true;
      }
      catch (JavaModelException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public AttributeInfo[] getAttributeInfo() {
    loadAttributeInfo();
    return super.getAttributeInfo();
  }

  @Override
  public AttributeInfo getAttributeInfo(String name) {
    loadAttributeInfo();
    return super.getAttributeInfo(name);
  }
}
