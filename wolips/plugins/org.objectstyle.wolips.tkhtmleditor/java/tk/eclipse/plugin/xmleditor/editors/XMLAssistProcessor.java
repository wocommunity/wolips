package tk.eclipse.plugin.xmleditor.editors;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jp.aonir.fuzzyxml.FuzzyXMLAttribute;
import jp.aonir.fuzzyxml.FuzzyXMLElement;

import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.eclipse.ui.IFileEditorInput;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.assist.AssistInfo;
import tk.eclipse.plugin.htmleditor.assist.AttributeInfo;
import tk.eclipse.plugin.htmleditor.assist.HTMLAssistProcessor;
import tk.eclipse.plugin.htmleditor.assist.TagInfo;
import tk.eclipse.plugin.htmleditor.editors.HTMLSourceEditor;

import com.wutka.dtd.DTD;
import com.wutka.dtd.DTDAttribute;
import com.wutka.dtd.DTDChoice;
import com.wutka.dtd.DTDDecl;
import com.wutka.dtd.DTDElement;
import com.wutka.dtd.DTDEmpty;
import com.wutka.dtd.DTDEnumeration;
import com.wutka.dtd.DTDItem;
import com.wutka.dtd.DTDMixed;
import com.wutka.dtd.DTDName;
import com.wutka.dtd.DTDParseException;
import com.wutka.dtd.DTDParser;
import com.wutka.dtd.DTDSequence;

/**
 * The AssistProcessor for the <code>XMLEditor</code>.
 * 
 * @author Naoki Takezoe
 * @see XMLEditor
 */
public class XMLAssistProcessor extends HTMLAssistProcessor {

  private List<TagInfo> _tagList = new ArrayList<TagInfo>();
  private Map<String, ArrayList<TagInfo>> _nsTagListMap = new HashMap<String, ArrayList<TagInfo>>();
  private XMLEditor _editor;
  private IFileEditorInput _input;
  private ClassNameAssistProcessor _classNameAssistant = new ClassNameAssistProcessor();

  /**
   * The constructor without DTD / XSD.
   * <p>
   * <code>XMLAssistProcessor</code> that's created by 
   * this constructor can complete only close tags.
   */
  public XMLAssistProcessor() {
    super();
  }

  /**
   * Update informations about code-completion.
   * 
   * @param input the <code>XMLEditor</code>
   * @param source XML source code
   */
  @Override
  public void update(HTMLSourceEditor editor, String source) {
    if (editor.getEditorInput() instanceof IFileEditorInput) {
      this._input = (IFileEditorInput) editor.getEditorInput();
    }
    if (editor instanceof XMLEditor) {
      this._editor = (XMLEditor) editor;
    }
  }

  /**
   * Refresh DTD informations.
   * 
   * @param in the <code>InputStream</code> of the DTD
   */
  public void updateDTDInfo(Reader in) {
    // clear at fisrt
    _tagList.clear();
    //		root = null;
    try {
      DTDParser parser = new DTDParser(in);
      DTD dtd = parser.parse();
      Object[] obj = dtd.getItems();
      for (int i = 0; i < obj.length; i++) {
        if (obj[i] instanceof DTDElement) {
          DTDElement element = (DTDElement) obj[i];
          String name = element.getName();
          DTDItem item = element.getContent();
          boolean hasBody = true;
          if (item instanceof DTDEmpty) {
            hasBody = false;
          }
          TagInfo tagInfo = new TagInfo(name, hasBody);
          Iterator ite = element.attributes.keySet().iterator();

          // set child tags
          if (item instanceof DTDSequence) {
            DTDSequence seq = (DTDSequence) item;
            setChildTagName(tagInfo, seq.getItem());
          }
          else if (item instanceof DTDMixed) {
            // #PCDATA
          }

          while (ite.hasNext()) {
            String attrName = (String) ite.next();
            DTDAttribute attr = element.getAttribute(attrName);

            DTDDecl decl = attr.getDecl();
            boolean required = false;
            if (decl == DTDDecl.REQUIRED) {
              required = true;
            }

            AttributeInfo attrInfo = new AttributeInfo(attrName, true, AttributeInfo.NONE, required);
            tagInfo.addAttributeInfo(attrInfo);

            Object attrType = attr.getType();
            if (attrType instanceof DTDEnumeration) {
              DTDEnumeration dtdEnum = (DTDEnumeration) attrType;
              String[] items = dtdEnum.getItems();
              for (int j = 0; j < items.length; j++) {
                attrInfo.addValue(items[j]);
              }
            }
            else if (attrType.equals("ID")) {
              attrInfo.setAttributeType(AttributeInfo.ID);
            }
            else if (attrType.equals("IDREF")) {
              attrInfo.setAttributeType(AttributeInfo.IDREF);
            }
            else if (attrType.equals("IDREFS")) {
              attrInfo.setAttributeType(AttributeInfo.IDREFS);
            }
          }
          _tagList.add(tagInfo);
          // TODO root tag is an element that was found at first.
        }
      }
    }
    catch (DTDParseException ex) {
      // ignore
    }
    catch (Exception ex) {
      HTMLPlugin.logException(ex);
    }
  }

  /**
   * Refresh XML schema informations.
   * 
   * @param uri the URI of the XML schema
   * @param in the <code>InputStream</code> of XML schema
   */
  public void updateXSDInfo(String uri, Reader in) {
    try {
      SchemaGrammar grammer = (SchemaGrammar) new XMLSchemaLoader().loadGrammar(new XMLInputSource(null, null, null, in, null));

      // clear at first
      String targetNS = grammer.getTargetNamespace();
      _nsTagListMap.put(targetNS, new ArrayList<TagInfo>());
      List<TagInfo> tagList = _nsTagListMap.get(targetNS);
      //			root = null;

      XSNamedMap map = grammer.getComponents(XSConstants.ELEMENT_DECLARATION);
      for (int i = 0; i < map.getLength(); i++) {
        XSElementDeclaration element = (XSElementDeclaration) map.item(i);
        parseXSDElement(tagList, element);
      }
    }
    catch (Exception ex) {

    }
  }

  private void parseXSDElement(List<TagInfo> tagList, XSElementDeclaration element) {
    TagInfo tagInfo = new TagInfo(element.getName(), true);
    if (tagList.contains(tagInfo)) {
      return;
    }
    tagList.add(tagInfo);

    XSTypeDefinition type = element.getTypeDefinition();
    if (type instanceof XSComplexTypeDefinition) {
      XSParticle particle = ((XSComplexTypeDefinition) type).getParticle();
      if (particle != null) {
        XSTerm term = particle.getTerm();
        if (term instanceof XSElementDeclaration) {
          parseXSDElement(tagList, (XSElementDeclaration) term);
          tagInfo.addChildTagName(((XSElementDeclaration) term).getName());
        }
        if (term instanceof XSModelGroup) {
          parseXSModelGroup(tagInfo, tagList, (XSModelGroup) term);
        }
      }
      XSObjectList attrs = ((XSComplexTypeDefinition) type).getAttributeUses();
      for (int i = 0; i < attrs.getLength(); i++) {
        XSAttributeUse attrUse = (XSAttributeUse) attrs.item(i);
        XSAttributeDeclaration attr = attrUse.getAttrDeclaration();
        AttributeInfo attrInfo = new AttributeInfo(attr.getName(), true, AttributeInfo.NONE, attrUse.getRequired());
        tagInfo.addAttributeInfo(attrInfo);
      }

    }
  }

  private void parseXSModelGroup(TagInfo tagInfo, List<TagInfo> tagList, XSModelGroup term) {
    XSObjectList list = (term).getParticles();
    for (int i = 0; i < list.getLength(); i++) {
      XSObject obj = list.item(i);

      if (obj instanceof XSParticle) {
        XSTerm term2 = ((XSParticle) obj).getTerm();

        if (term2 instanceof XSElementDeclaration) {
          parseXSDElement(tagList, (XSElementDeclaration) term2);
          tagInfo.addChildTagName(((XSElementDeclaration) term2).getName());
        }
        if (term2 instanceof XSModelGroup) {
          parseXSModelGroup(tagInfo, tagList, (XSModelGroup) term2);
        }
      }
    }
  }

  /**
   * Sets a child tag name to <code>TagInfo</code>.
   * 
   * @param tagInfo the <code>TagInfo</code>
   * @param items an array of <code>DTDItem</code>
   */
  private void setChildTagName(TagInfo tagInfo, DTDItem[] items) {
    for (int i = 0; i < items.length; i++) {
      if (items[i] instanceof DTDName) {
        DTDName dtdName = (DTDName) items[i];
        tagInfo.addChildTagName(dtdName.getValue());
      }
      else if (items[i] instanceof DTDChoice) {
        DTDChoice dtdChoise = (DTDChoice) items[i];
        setChildTagName(tagInfo, dtdChoise.getItem());
      }
    }
  }

  @Override
  protected boolean supportTagRelation() {
    return true;
  }

  /**
   * Returns an array of an attribute value proposal to complete an attribute value.
   * 
   * @param tagName the tag name
   * @param value the inputed value
   * @param attrInfo the attribute information
   * @return the array of attribute value proposals
   */
  @Override
  protected AssistInfo[] getAttributeValues(String tagName, String value, TagInfo tagInfo, AttributeInfo attrInfo) {
    if (attrInfo.getAttributeType() == AttributeInfo.IDREF || attrInfo.getAttributeType() == AttributeInfo.IDREFS) {
      return super.getAttributeValues(tagName, value, tagInfo, attrInfo);
    }
    String[] values = attrInfo.getValues();
    if (values.length == 0) {
      return getClassAttributeValues(value, attrInfo.getAttributeName());
    }
    AssistInfo[] infos = new AssistInfo[values.length];
    for (int i = 0; i < infos.length; i++) {
      infos[i] = new AssistInfo(values[i]);
    }
    return infos;
  }

  /**
   * Provides classname completion.
   * 
   * @param value the inputed value
   * @param attrName the attribute name
   * @return the array of attribute value proposals
   */
  protected AssistInfo[] getClassAttributeValues(String value, String attrName) {
    if (_input == null || _editor == null || value.length() == 0) {
      return new AssistInfo[0];
    }
    String[] classNameAttributes = _editor.getClassNameAttributes();
    for (int i = 0; i < classNameAttributes.length; i++) {
      if (attrName.equals(classNameAttributes[i])) {
        return _classNameAssistant.getClassAttributeValues(_input.getFile(), value);
      }
    }
    return new AssistInfo[0];
  }

  /**
   * Returns the <code>List</code> of <code>TagInfo</code>.
   * 
   * @return the <code>List</code> of <code>TagInfo</code>
   */
  @Override
  protected List<TagInfo> getTagList() {
    ArrayList<TagInfo> list = new ArrayList<TagInfo>();
    list.addAll(this._tagList);
    // get namespace
    FuzzyXMLElement element = getOffsetElement();
    HashMap<String, String> nsPrefixMap = new HashMap<String, String>();
    getNamespace(nsPrefixMap, element);
    // add prefix to tag names
    Iterator ite = this._nsTagListMap.keySet().iterator();
    while (ite.hasNext()) {
      String uri = (String) ite.next();
      String prefix = nsPrefixMap.get(uri);
      if (prefix == null || prefix.equals("")) {
        list.addAll(this._nsTagListMap.get(uri));
      }
      else {
        List<TagInfo> nsTagList = this._nsTagListMap.get(uri);
        for (int i = 0; i < nsTagList.size(); i++) {
          TagInfo tagInfo = nsTagList.get(i);
          list.add(createPrefixTagInfo(tagInfo, prefix));
        }
      }
    }
    return list;
  }

  /**
   * Adds prefix to <code>TagInfo</code>.
   * 
   * @param tagInfo the <code>TagInfo</code> instance
   * @param prefix the prefix to add
   * @return the new <code>TagInfo</code> instance that is added the prefix
   */
  private TagInfo createPrefixTagInfo(TagInfo tagInfo, String prefix) {
    TagInfo newTagInfo = new TagInfo(prefix + ":" + tagInfo.getTagName(), tagInfo.hasBody());
    AttributeInfo[] attrInfos = tagInfo.getAttributeInfo();
    for (int i = 0; i < attrInfos.length; i++) {
      AttributeInfo newAttrInfo = new AttributeInfo(prefix + ":" + attrInfos[i].getAttributeName(), true, AttributeInfo.NONE, attrInfos[i].isRequired());
      newTagInfo.addAttributeInfo(newAttrInfo);
    }
    String[] children = tagInfo.getChildTagNames();
    for (int i = 0; i < children.length; i++) {
      newTagInfo.addChildTagName(prefix + ":" + children[i]);
    }
    return newTagInfo;
  }

  /**
   * Returns mapping of namespace and prefix at the calet position.
   * 
   * @param map 
   * <ul>
   *   <li>key - namespace</li>
   *   <li>value - prefix</li>
   * </ul>
   * @param element the <code>FuzzyXMLElement</code> at the calet position
   */
  private void getNamespace(Map<String, String> map, FuzzyXMLElement element) {
    FuzzyXMLAttribute[] attrs = element.getAttributes();
    for (int i = 0; i < attrs.length; i++) {
      if (attrs[i].getName().startsWith("xmlns")) {
        String name = attrs[i].getName();
        String prefix = "";
        if (name.indexOf(":") >= 0) {
          prefix = name.substring(name.indexOf(":") + 1);
        }
        if (map.get(attrs[i].getValue()) == null) {
          map.put(attrs[i].getValue(), prefix);
        }
      }
    }
    if (element.getParentNode() != null) {
      getNamespace(map, (FuzzyXMLElement) element.getParentNode());
    }
  }

  /**
   * Returns the <code>TagInfo<code> which has the specified name.
   * 
   * @param name the tag name
   * @return the <code>TagInfo</code> which has the specified name,
   *   or <code>null</code>.
   */
  @Override
  protected TagInfo getTagInfo(String name) {
    List tagList = getTagList();
    for (int i = 0; i < tagList.size(); i++) {
      TagInfo info = (TagInfo) tagList.get(i);
      if (info.getTagName().equals(name)) {
        return info;
      }
    }
    return new XMLTagInfo(name);
  }

  /**
   * The <code>TagInfo</code> object which will be returned 
   * when this processor has no definition for the specified tag.
   */
  private class XMLTagInfo extends TagInfo {

    public XMLTagInfo(String tagName) {
      super(tagName, true);
    }

    @Override
    public AttributeInfo getAttributeInfo(String name) {
      AttributeInfo attrInfo = new AttributeInfo(name, true);
      return attrInfo;
    }
  }

}
