package org.objectstyle.wolips.wodclipse.core.validation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.aonir.fuzzyxml.FuzzyXMLAttribute;
import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.Position;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.model.HtmlElementName;
import org.objectstyle.wolips.wodclipse.core.model.IWodElement;
import org.objectstyle.wolips.wodclipse.core.model.IWodModel;
import org.objectstyle.wolips.wodclipse.core.model.SimpleWodBinding;
import org.objectstyle.wolips.wodclipse.core.model.SimpleWodElement;
import org.objectstyle.wolips.wodclipse.core.model.WodBindingNameProblem;
import org.objectstyle.wolips.wodclipse.core.model.WodBindingValueProblem;
import org.objectstyle.wolips.wodclipse.core.model.WodProblem;
import org.objectstyle.wolips.wodclipse.core.preferences.PreferenceConstants;
import org.objectstyle.wolips.wodclipse.core.preferences.TagShortcut;
import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;

public class TemplateValidator {
  private boolean _wo54;
  private WodParserCache _cache;
  private Set<FuzzyXMLElement> _woElements;

  public TemplateValidator(WodParserCache cache) {
    _cache = cache;
    _wo54 = Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.WO54_KEY);
  }

  /**
   * Validates the HTML document.
   * @throws CoreException 
   */
  public void validate(FuzzyXMLDocument doc) throws CoreException {
    visitDocument(doc, true);
  }

  public void visitDocument(FuzzyXMLDocument doc, boolean validate) throws CoreException {
    if (doc != null) {
      _woElements = new HashSet<FuzzyXMLElement>();

      final List<InlineWodProblem> inlineProblems = new LinkedList<InlineWodProblem>();

      FuzzyXMLElement rootElement = doc.getDocumentElement();
      //FuzzyXMLElement rootElement = (FuzzyXMLElement) XPath.selectSingleNode(doc.getDocumentElement(), "*");
      visitElement(rootElement, inlineProblems, validate);

      if (validate) {
        IFile wodFile = _cache.getWodFile();
        IWodModel wodModel = _cache.getWodModel();
        for (FuzzyXMLElement woElement : _woElements) {
          String woElementName = woElement.getAttributeValue("name");
          int startOffset = woElement.getOffset();
          int endOffset = woElement.getOffset() + woElement.getLength();
          HtmlElementName elementName = new HtmlElementName(_cache.getHtmlFile(), woElementName, WodHtmlUtils.getLineAtOffset(_cache.getHtmlContents(), startOffset), startOffset, endOffset);
          _cache.addHtmlElement(elementName);

          if (wodModel != null) {
            IWodElement wodElement = wodModel.getElementNamed(woElementName);
            if (wodElement == null) {
              WodProblem undefinedElement = new WodBindingValueProblem("name", "The element '" + woElementName + "' is not defined in " + wodFile.getName(), null, -1, false, _cache.getHtmlFile().getName());
              inlineProblems.add(new InlineWodProblem(woElement, undefinedElement));
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
    int colonIndex = elementName.indexOf(':');
    if (colonIndex > 0) {
      if (validate) {
        String namespace = elementName.substring(0, colonIndex);
        if ("wo".equalsIgnoreCase(namespace)) {
          String namespaceElementName = elementName.substring(colonIndex + 1);
          TagShortcut matchingTagShortcut = null;
          for (TagShortcut tagShortcut : _cache.getTagShortcuts()) {
            if (namespaceElementName.equalsIgnoreCase(tagShortcut.getShortcut())) {
              matchingTagShortcut = tagShortcut;
            }
          }
          if (matchingTagShortcut != null) {
            namespaceElementName = matchingTagShortcut.getActual();
          }
          SimpleWodElement wodElement = new SimpleWodElement("_temp", namespaceElementName);
          wodElement.setTemporary(true);

          if (matchingTagShortcut != null) {
            for (Map.Entry<String, String> shortcutAttribute : matchingTagShortcut.getAttributes().entrySet()) {
              String value = toBindingValue(shortcutAttribute.getValue());
              SimpleWodBinding wodBinding = new SimpleWodBinding(shortcutAttribute.getKey(), value);
              wodElement.addBinding(wodBinding);
            }
          }

          List<WodProblem> wodProblems = new LinkedList<WodProblem>();

          boolean validateBindingValues = Activator.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.VALIDATE_BINDING_VALUES);
          boolean validateOGNL = Activator.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.VALIDATE_OGNL_KEY);
          FuzzyXMLAttribute[] attributes = element.getAttributes();
          for (FuzzyXMLAttribute attribute : attributes) {
            String name = attribute.getName();
            String originalValue = attribute.getValue();
            String value = toBindingValue(originalValue);
            SimpleWodBinding wodBinding = new SimpleWodBinding(name, value);
            wodElement.addBinding(wodBinding);
          }

          try {
            wodElement.fillInProblems(_cache.getJavaProject(), _cache.getComponentType(), validateBindingValues, wodProblems, _cache);
            inlineProblems.add(new InlineWodProblem(element, wodProblems));
          }
          catch (Exception e) {
            Activator.getDefault().log(e);
          }
        }
      }
    }
    else if (WodHtmlUtils.isWOTag(elementName)) {
      String webobjectName = element.getAttributeValue("name");
      if (webobjectName == null) {
        if (validate) {
          inlineProblems.add(new InlineWodProblem(element, "webobject tag missing 'name' attribute", false));
        }
      }
      else {
        _woElements.add(element);
        if (validate && element.getAttributes().length > 1) {
          inlineProblems.add(new InlineWodProblem(element, "webobject tags should only have a 'name' attribute", true));
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

  protected String toBindingValue(String value) {
    String bindingValue = value;
    if (bindingValue.startsWith("$")) {
      bindingValue = bindingValue.substring(1);
    }
    else if (_wo54 && bindingValue.startsWith("[") && bindingValue.endsWith("]")) {
      bindingValue = bindingValue.substring(1, bindingValue.length() - 1);
    }
    else {
      bindingValue = "\"" + bindingValue + "\"";
    }
    return bindingValue;
  }

  public class InlineWodProblem {
    private List<WodProblem> _wodProblems;
    private FuzzyXMLElement _element;

    public InlineWodProblem(FuzzyXMLElement element, String message, boolean warning) {
      _element = element;
      int offset = element.getOffset() + 1;
      int length = element.getName().length();
      int lineNumber = WodHtmlUtils.getLineAtOffset(_cache.getHtmlContents(), offset);
      WodProblem problem = new WodProblem(message, new Position(offset, length), lineNumber, warning, new String[0]);
      _wodProblems = new LinkedList<WodProblem>();
      _wodProblems.add(problem);
    }

    public InlineWodProblem(FuzzyXMLElement element, WodProblem wodProblem) {
      _element = element;
      _wodProblems = new LinkedList<WodProblem>();
      _wodProblems.add(wodProblem);
    }

    public InlineWodProblem(FuzzyXMLElement element, List<WodProblem> problems) {
      _element = element;
      _wodProblems = problems;
    }

    public FuzzyXMLElement getElement() {
      return _element;
    }

    public void createProblemMarkers() {
      for (WodProblem wodProblem : _wodProblems) {
        IMarker marker = wodProblem.createMarker(_cache.getHtmlFile());
        try {
          if (marker != null) {
            boolean elementError = true;
            if (wodProblem instanceof WodBindingNameProblem) {
              String name = ((WodBindingNameProblem) wodProblem).getBindingName();
              FuzzyXMLAttribute attribute = _element.getAttributeNode(name);
              if (attribute != null) {
                elementError = false;
                int offset = attribute.getOffset() + 1;
                marker.setAttribute(IMarker.LINE_NUMBER, WodHtmlUtils.getLineAtOffset(_cache.getHtmlContents(), offset));
                marker.setAttribute(IMarker.CHAR_START, offset);
                marker.setAttribute(IMarker.CHAR_END, offset + attribute.getName().length());
              }
            }
            else if (wodProblem instanceof WodBindingValueProblem) {
              String name = ((WodBindingValueProblem) wodProblem).getBindingName();
              FuzzyXMLAttribute attribute = _element.getAttributeNode(name);
              if (attribute != null) {
                elementError = false;
                int offset = attribute.getOffset() + 1;
                marker.setAttribute(IMarker.LINE_NUMBER, WodHtmlUtils.getLineAtOffset(_cache.getHtmlContents(), offset));
                marker.setAttribute(IMarker.CHAR_START, _element.getOffset() + attribute.getValueDataOffset() + 1);
                marker.setAttribute(IMarker.CHAR_END, _element.getOffset() + attribute.getValueDataOffset() + 1 + attribute.getValueDataLength());
              }
            }

            if (elementError) {
              int offset = _element.getOffset() + 1;
              marker.setAttribute(IMarker.LINE_NUMBER, WodHtmlUtils.getLineAtOffset(_cache.getHtmlContents(), offset));
              marker.setAttribute(IMarker.CHAR_START, offset);
              marker.setAttribute(IMarker.CHAR_END, offset + _element.getName().length());
            }
          }
        }
        catch (CoreException e) {
          Activator.getDefault().log(e);
        }
      }
    }
  }
}
