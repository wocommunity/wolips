package tk.eclipse.plugin.htmleditor.assist;

import java.util.ArrayList;

public class TagInfo {
  private String tagName;
  private boolean hasBody;
  private boolean emptyTag;
  private String description;
  private ArrayList attributes = new ArrayList();
  private ArrayList children = new ArrayList();
  private boolean _requiresAttributes;

  public static final int NONE = 0;
  public static final int EVENT = 1;
  public static final int FORM = 2;

  /**
   * The constructor.
   * 
   * @param tagName A tag name.
   * @param hasBody This tag has child elements or not.
   *    <ul>
   *      <li>If hasBody is true, then HTMLAssistProcessor assists &lt;...&gt;&lt;/...&gt;.</li>
   *      <li>If hasBody is false, then HTMLAssistProcessor assists &lt;.../&gt;.</li>
   *    </ul>
   */
  public TagInfo(String tagName, boolean hasBody) {
    this(tagName, hasBody, false);
  }

  /**
   * The constructor.
   * 
   * @param tagName A tag name.
   * @param hasBody This tag has child elements or not.
   *    <ul>
   *      <li>If hasBody is true, then HTMLAssistProcessor assists &lt;...&gt;&lt;/...&gt;.</li>
   *      <li>If hasBody is false, then HTMLAssistProcessor assists &lt;.../&gt;.</li>
   *    </ul>
   * @param emptyTag This tag is an empty tag or not.
   *    <ul>
   *      <li>If hasBody is false and emptyTag is true, then HTMLAssistProcessor assists &lt;...&gt;.</li>
   *    </ul>
   */
  public TagInfo(String tagName, boolean hasBody, boolean emptyTag) {
    this.tagName = tagName;
    this.hasBody = hasBody;
    this.emptyTag = emptyTag;
  }

  public String getTagName() {
    return this.tagName;
  }

  public boolean hasBody() {
    return this.hasBody;
  }

  public boolean isEmptyTag() {
    return this.emptyTag;
  }
  
  public void setRequiresAttributes(boolean requiresAttributes) {
    _requiresAttributes = requiresAttributes;
  }

  public boolean requiresAttributes() {
    boolean requiresAttributes = _requiresAttributes;
    if (!requiresAttributes) {
      ArrayList list = new ArrayList();
      for (int i = 0; !requiresAttributes && i < attributes.size(); i++) {
        AttributeInfo info = (AttributeInfo) attributes.get(i);
        if (info.isRequired()) {
          requiresAttributes = true;
        }
      }
    }
    return requiresAttributes;
  }
  /**
   * Adds an attribute information
   * @param attribute an attributr information
   */
  public void addAttributeInfo(AttributeInfo attribute) {
    int i = 0;
    for (; i < attributes.size(); i++) {
      AttributeInfo info = (AttributeInfo) attributes.get(i);
      if (info.getAttributeName().compareTo(attribute.getAttributeName()) > 0) {
        break;
      }
    }
    this.attributes.add(i, attribute);
  }

  /**
   * Returns all attribute informations.
   * @return an array of all attribute information
   */
  public AttributeInfo[] getAttributeInfo() {
    return (AttributeInfo[]) this.attributes.toArray(new AttributeInfo[this.attributes.size()]);
  }

  /**
   * Returns required attribute informations.
   * @return an array of required attribute information
   */
  public AttributeInfo[] getRequiredAttributeInfo() {
    ArrayList list = new ArrayList();
    for (int i = 0; i < attributes.size(); i++) {
      AttributeInfo info = (AttributeInfo) attributes.get(i);
      if (info.isRequired()) {
        list.add(info);
      }
    }
    return (AttributeInfo[]) list.toArray(new AttributeInfo[list.size()]);
  }

  /**
   * Returns a specified attribute information.
   * @param name an attribute name
   * @return an attribute information specified by an argument.
   */
  public AttributeInfo getAttributeInfo(String name) {
    for (int i = 0; i < attributes.size(); i++) {
      AttributeInfo info = (AttributeInfo) attributes.get(i);
      if (info.getAttributeName().equals(name)) {
        return info;
      }
    }
    return null;
  }

  /**
   * Adds a child tag name.
   * 
   * @param name a child tag name
   */
  public void addChildTagName(String name) {
    children.add(name);
  }

  /**
   * Returns child tag names.
   * 
   * @return an array of child tag names
   */
  public String[] getChildTagNames() {
    return (String[]) children.toArray(new String[children.size()]);
  }

  public boolean equals(Object obj) {
    if (obj instanceof TagInfo) {
      TagInfo tagInfo = (TagInfo) obj;
      if (tagInfo.getTagName().equals(getTagName())) {
        return true;
      }
    }
    return false;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

}
