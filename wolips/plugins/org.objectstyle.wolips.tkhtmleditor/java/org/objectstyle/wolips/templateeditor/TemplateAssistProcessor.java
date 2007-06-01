package org.objectstyle.wolips.templateeditor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.objectstyle.wolips.wodclipse.core.completion.WodCompletionProposal;
import org.objectstyle.wolips.wodclipse.core.completion.WodCompletionUtils;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.model.IWodElement;
import org.objectstyle.wolips.wodclipse.core.model.IWodModel;
import org.objectstyle.wolips.wodclipse.core.preferences.TagShortcut;
import org.objectstyle.wolips.wodclipse.core.util.WodApiUtils;
import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;
import org.objectstyle.wolips.wodclipse.core.util.WodReflectionUtils;

import tk.eclipse.plugin.htmleditor.assist.AssistInfo;
import tk.eclipse.plugin.htmleditor.assist.AttributeInfo;
import tk.eclipse.plugin.htmleditor.assist.HTMLAssistProcessor;
import tk.eclipse.plugin.htmleditor.assist.TagDefinition;
import tk.eclipse.plugin.htmleditor.assist.TagInfo;
import tk.eclipse.plugin.htmleditor.editors.HTMLSourceEditor;

public class TemplateAssistProcessor extends HTMLAssistProcessor {
  private static final int SCOPE = 100;
  private static final int CLASS = 101;
  private List<TagInfo> _tagList;
  private WodParserCache _cache;
  //private ClassNameAssistProcessor classNameProcessor = new ClassNameAssistProcessor();
  private IFile _file;
  private IEditorPart _editorPart;
  private boolean _wo54;

  public TemplateAssistProcessor(IEditorPart editorPart, WodParserCache wodParserCache, boolean wo54) {
    _wo54 = wo54;
    _editorPart = editorPart;
    _cache = wodParserCache;
    _tagList = new ArrayList<TagInfo>(TagDefinition.getTagInfoAsList());

    TagInfo webobject = new TagInfo("webobject", true);
    webobject.addAttributeInfo(new AttributeInfo("name", true, AttributeInfo.NONE, true));
    _tagList.add(webobject);

    TagInfo wo = new TagInfo("wo", true);
    wo.addAttributeInfo(new AttributeInfo("name", true, AttributeInfo.NONE, true));
    _tagList.add(wo);

    //    // JSP directives
    //    _tagList.add(new TextInfo("<%  %>", 3));
    //    _tagList.add(new TextInfo("<%=  %>", 4));
    //    _tagList.add(new TextInfo("<%@ page %>", 9));
    //    _tagList.add(new TextInfo("<%@ include %>", "<%@ include file=\"\" %>", 18));
    //    _tagList.add(new TextInfo("<%@ taglib %>", "<%@ taglib prefix=\"\" %>", 19));
  }

  @Override
  protected boolean supportTagRelation() {
    return false;
  }

  protected IFile getFile() {
    IFile wodFile = null;
    IEditorInput input = _editorPart.getEditorInput();
    if (input instanceof IPathEditorInput) {
      IPathEditorInput pathInput = (IPathEditorInput) input;
      IPath path = pathInput.getPath();
      wodFile = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
    }
    return wodFile;
  }

  protected IJavaProject getJavaProject() {
    IJavaProject javaProject = null;
    IFile wodFile = getFile();
    if (wodFile != null) {
      IProject project = wodFile.getProject();
      javaProject = JavaCore.create(project);
    }
    return javaProject;
  }

  @Override
  protected List<TagInfo> getDynamicTagInfo(String tagName) {
    List<TagInfo> tagInfos = null;
    if (tagName.startsWith("wo:")) {
      String partialElementType = tagName.substring("wo:".length());
      IJavaProject javaProject = getJavaProject();
      try {
        Set<WodCompletionProposal> proposals = new HashSet<WodCompletionProposal>();
        WodCompletionUtils.fillInElementTypeCompletionProposals(javaProject, partialElementType, 0, partialElementType.length(), proposals, false, null);
        for (TagShortcut tagShortcut : _cache.getTagShortcuts()) {
          String shortcut = tagShortcut.getShortcut();
          if (shortcut.startsWith(partialElementType.toLowerCase())) {
            proposals.add(new WodCompletionProposal(partialElementType, 0, partialElementType.length(), shortcut));
          }
        }
        if (!proposals.isEmpty()) {
          tagInfos = new LinkedList<TagInfo>();
          for (WodCompletionProposal proposal : proposals) {
            InlineWodTagInfo tagInfo = new InlineWodTagInfo(proposal.getProposal(), _cache);
            tagInfo.setJavaProject(javaProject);
            tagInfos.add(tagInfo);
          }
        }
      }
      catch (JavaModelException e) {
        e.printStackTrace();
      }
    }
    return tagInfos;
  }

  @Override
  public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentOffset) {
    // Java code completion for partitions which are not parted as HTMLPartitionScanner#HTML_SCRIPT.
    //String rawText = viewer.getDocument().get();
    //System.out.println("TemplateAssistProcessor.computeCompletionProposals: " + rawText);
    //    rawText = HTMLUtil.comment2space(rawText, false);
    //    String text = rawText.substring(0, documentOffset);
    //    int begin = text.lastIndexOf("<%");
    //    if (begin >= 0) {
    //      int end = rawText.indexOf("%>", begin);
    //      if (end >= 0 && documentOffset < end) {
    //        return scriptletProcessor.computeCompletionProposals(viewer, documentOffset);
    //      }
    //    }
    return super.computeCompletionProposals(viewer, documentOffset);
  }

  @Override
  protected AssistInfo[] getAttributeValues(String tagName, String value, TagInfo tagInfo, AttributeInfo attrInfo) {
    AssistInfo[] attributeValues;
    if (tagInfo instanceof InlineWodTagInfo) {
      List<AssistInfo> attributeValuesList = new LinkedList<AssistInfo>();
      try {
        InlineWodTagInfo wodTagInfo = (InlineWodTagInfo) tagInfo;
        String bindingValue = value;
        String prefix = "$";
        String suffix = "";
        if (value.startsWith("$")) {
          bindingValue = value.substring(1);
          prefix = "$";
        }
        else if (_wo54 && value.startsWith("[")) {
          prefix = "[";
          bindingValue = value.substring(1);
          if (value.endsWith("]")) {
            bindingValue = value.substring(0, bindingValue.length() - 1);
            suffix = "]";
          }
        }
        IFile wodFile = getFile();
        String componentTypeName = wodFile.getLocation().removeFileExtension().lastSegment();
        IType componentType = WodReflectionUtils.findElementType(wodTagInfo.getJavaProject(), componentTypeName, true, _cache);
        Set<WodCompletionProposal> proposals = new HashSet<WodCompletionProposal>();
        int dotIndex = bindingValue.lastIndexOf('.');
        if (dotIndex == -1) {
          dotIndex = 0;
        }
        else {
          dotIndex += 2;
        }
        boolean checkBindingValue = WodCompletionUtils.fillInBindingValueCompletionProposals(wodTagInfo.getJavaProject(), componentType, bindingValue, 0, bindingValue.length(), proposals, _cache);
        if (checkBindingValue) {
          try {
            String elementTypeName = wodTagInfo.getExpandedElementTypeName();
            IType elementType = WodReflectionUtils.findElementType(wodTagInfo.getJavaProject(), elementTypeName, false, _cache);
            String[] validValues = WodApiUtils.getValidValues(wodTagInfo.getJavaProject(), _cache.getComponentType(), elementType, attrInfo.getAttributeName(), _cache);
            if (validValues != null) {
              for (String validValue : validValues) {
                if (validValue.toLowerCase().startsWith(bindingValue)) {
                  proposals.add(new WodCompletionProposal(bindingValue, 0, bindingValue.length(), validValue));
                }
              }
            }
          }
          catch (Exception e) {
            e.printStackTrace();
          }
        }
        for (WodCompletionProposal proposal : proposals) {
          String proposalString = proposal.getProposal();
          if (dotIndex == 0) {
            if (proposalString.startsWith("\"")) {
              proposalString = proposalString.substring(1, proposalString.length() - 1);
            }
            else {
              proposalString = prefix + proposalString + suffix;
            }
          }
          AssistInfo assist = new AssistInfo(proposalString);
          assist.setOffset(dotIndex);
          attributeValuesList.add(assist);
        }
      }
      catch (JavaModelException e) {
        e.printStackTrace();
      }
      attributeValues = attributeValuesList.toArray(new AssistInfo[attributeValuesList.size()]);
    }
    else if ("name".equals(attrInfo.getAttributeName()) && WodHtmlUtils.isWOTag(tagName)) {
      List<AssistInfo> attributeValuesList = new LinkedList<AssistInfo>();
      try {
        IWodModel wodModel = _cache.getWodModel();
        if (wodModel != null) {
          for (IWodElement wodElement : wodModel.getElements()) {
            String wodElementName = wodElement.getElementName();
            if (wodElementName.toLowerCase().startsWith(value.toLowerCase())) {
              AssistInfo assist = new AssistInfo(wodElementName);
              attributeValuesList.add(assist);
            }
          }
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      attributeValues = attributeValuesList.toArray(new AssistInfo[attributeValuesList.size()]);
    }
    else {
      //    if (tagName.indexOf(":") != -1) {
      //      String[] dim = tagName.split(":");
      //      String uri = getUri(dim[0]);
      //      ICustomTagAttributeAssist[] assists = HTMLPlugin.getDefault().getCustomTagAttributeAssists();
      //      for (int i = 0; i < assists.length; i++) {
      //        AssistInfo[] values = assists[i].getAttributeValues(dim[1], uri, value, info);
      //        if (values != null) {
      //          return values;
      //        }
      //      }
      //    }
      //    if (info.getAttributeType() == SCOPE) {
      //      return new AssistInfo[] { new AssistInfo("application"), new AssistInfo("page"), new AssistInfo("request"), new AssistInfo("session") };
      //    }
      //    if (info.getAttributeType() == CLASS && this._file != null) {
      //      return classNameProcessor.getClassAttributeValues(this._file, value);
      //    }
      attributeValues = super.getAttributeValues(tagName, value, tagInfo, attrInfo);
    }
    return attributeValues;
  }

  @Override
  protected TagInfo getTagInfo(String name) {
    if (name.startsWith("wo:")) {
      String elementTypeName = name.substring("wo:".length());
      InlineWodTagInfo tagInfo = new InlineWodTagInfo(elementTypeName, _cache);
      tagInfo.setJavaProject(getJavaProject());
      return tagInfo;
    }
    List tagList = getTagList();
    for (int i = 0; i < tagList.size(); i++) {
      TagInfo info = (TagInfo) tagList.get(i);
      if (info.getTagName() != null) {
        if (name.equals(info.getTagName().toLowerCase())) {
          return info;
        }
      }
    }
    return null;
  }

  @Override
  protected List<TagInfo> getTagList() {
    List<TagInfo> list = new ArrayList<TagInfo>();
    list.addAll(_tagList);
    return list;
  }

  /**
   * Updates informations about code completion.
   * 
   * @param input the <code>HTMLSourceEditor</code> instance
   * @param source JSP source code
   */
  @Override
  public void update(HTMLSourceEditor editor, String source) {
    //    this.scriptletProcessor.update((JSPSourceEditor) editor);
    if (editor.getEditorInput() instanceof IFileEditorInput) {
      IFileEditorInput input = (IFileEditorInput) editor.getEditorInput();
      //      cunstomTagList.clear();
      //      namespace.clear();
      //      JSPInfo jspInfo = JSPInfo.getJSPInfo(input.getFile(), source);
      //      TLDInfo[] tlds = jspInfo.getTLDInfo();
      //      for (int i = 0; i < tlds.length; i++) {
      //        namespace.put(tlds[i].getPrefix(), tlds[i].getTaglibUri());
      //        cunstomTagList.addAll(tlds[i].getTagInfo());
      //      }
      _file = input.getFile();
    }
    super.update(editor, source);
  }

}
