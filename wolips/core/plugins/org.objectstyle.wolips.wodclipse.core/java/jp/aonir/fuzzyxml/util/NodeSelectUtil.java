package jp.aonir.fuzzyxml.util;

import java.util.ArrayList;
import java.util.List;

import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;

public class NodeSelectUtil {

  /**
   * @param element
   * @param filter
   * @return
   */
  public static FuzzyXMLNode[] getChildren(FuzzyXMLElement element, NodeFilter filter) {
    List<FuzzyXMLNode> result = new ArrayList<FuzzyXMLNode>();
    FuzzyXMLNode[] children = element.getChildren();
    for (int i = 0; i < children.length; i++) {
      if (filter.filter(children[i])) {
        result.add(children[i]);
      }
    }
    return result.toArray(new FuzzyXMLNode[result.size()]);
  }

  /**
   * @param element
   * @param filter
   * @return
   */
  public static FuzzyXMLNode[] getNodeByFilter(FuzzyXMLElement element, NodeFilter filter) {
    List<FuzzyXMLNode> result = new ArrayList<FuzzyXMLNode>();
    if (filter.filter(element)) {
      result.add(element);
    }
    searchNodeByFilter(element, filter, result);
    return result.toArray(new FuzzyXMLElement[result.size()]);
  }

  private static void searchNodeByFilter(FuzzyXMLElement element, NodeFilter filter, List<FuzzyXMLNode> result) {
    FuzzyXMLNode[] children = element.getChildren();
    for (int i = 0; i < children.length; i++) {
      if (filter.filter(children[i])) {
        result.add(children[i]);
      }
      if (children[i] instanceof FuzzyXMLElement) {
        searchNodeByFilter((FuzzyXMLElement) children[i], filter, result);
      }
    }
  }

  /**
   * @param element
   * @param id
   * @return id
   */
  public static FuzzyXMLElement getElementById(FuzzyXMLElement element, String id) {
    FuzzyXMLElement[] elements = getElementByAttribute(element, "id", id);
    if (elements.length == 0) {
      return null;
    }
    return elements[0];
  }

  /**
   * @param element
   * @param name
   * @param value
   * @return
   */
  public static FuzzyXMLElement[] getElementByAttribute(FuzzyXMLElement element, String name, String value) {
    List<FuzzyXMLElement> result = new ArrayList<FuzzyXMLElement>();
    searchElementByAttribute(element, name, value, result);
    return result.toArray(new FuzzyXMLElement[result.size()]);
  }

  private static void searchElementByAttribute(FuzzyXMLElement element, String name, String value, List<FuzzyXMLElement> result) {
    if (value.equals(element.getAttributeValue(name))) {
      result.add(element);
    }
    FuzzyXMLNode[] children = element.getChildren();
    for (int i = 0; i < children.length; i++) {
      if (children[i] instanceof FuzzyXMLElement) {
        searchElementByAttribute(element, name, value, result);
      }
    }
  }

  /**
   * @param element
   * @param name
   * @return
   */
  public static FuzzyXMLElement[] getElementByTagName(FuzzyXMLElement element, String name) {
    List<FuzzyXMLElement> result = new ArrayList<FuzzyXMLElement>();
    searchElementByTagName(element, name, result);
    return result.toArray(new FuzzyXMLElement[result.size()]);
  }

  private static void searchElementByTagName(FuzzyXMLElement element, String name, List<FuzzyXMLElement> result) {
    if (element.getName().equals(name)) {
      result.add(element);
    }
    FuzzyXMLNode[] children = element.getChildren();
    for (int i = 0; i < children.length; i++) {
      if (children[i] instanceof FuzzyXMLElement) {
        searchElementByTagName(element, name, result);
      }
    }
  }

}
