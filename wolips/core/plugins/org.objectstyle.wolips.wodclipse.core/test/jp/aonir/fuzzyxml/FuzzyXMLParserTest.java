package jp.aonir.fuzzyxml;

import jp.aonir.fuzzyxml.internal.RenderContext;
import junit.framework.TestCase;

public class FuzzyXMLParserTest extends TestCase {
  public void assertChildCount(FuzzyXMLElement parentNode, int childCount) {
    FuzzyXMLNode[] children = parentNode.getChildren();
    assertEquals(childCount, children.length);
  }

  public void assertChildElement(FuzzyXMLElement parentNode, int index, String childName) {
    FuzzyXMLElement child = parentNode.getChildElement(index);
    assertEquals(childName, child.getName());
  }

  public void assertChildrenElements(FuzzyXMLElement parentNode, int childCount, String... childrenNames) {
    assertChildCount(parentNode, childCount);
    FuzzyXMLNode[] children = parentNode.getChildren();
    for (int i = 0; i < childCount; i++) {
      assertTrue(children[i] instanceof FuzzyXMLElement);
      assertEquals(childrenNames[i], ((FuzzyXMLElement) children[i]).getName());
    }
  }

  public void assertText(FuzzyXMLElement parentNode, String text) {
    assertChildCount(parentNode, 1);
    FuzzyXMLNode[] children = parentNode.getChildren();
    assertTrue(children[0] instanceof FuzzyXMLText);
    assertEquals(text, ((FuzzyXMLText) children[0]).getValue());
  }

  public void assertText(FuzzyXMLElement parentNode, int childIndex, String text) {
    FuzzyXMLNode[] children = parentNode.getChildren();
    assertTrue(children[childIndex] instanceof FuzzyXMLText);
    assertEquals(text, ((FuzzyXMLText) children[childIndex]).getValue());
  }

  public void assertNoAttributes(FuzzyXMLElement parentNode) {
    assertAttributes(parentNode, 0);
  }

  public void assertOnlyAttribute(FuzzyXMLElement parentNode, String name, String value) {
    assertAttributes(parentNode, 1);
    assertAttribute(parentNode, name, value);
  }

  public void assertAttributes(FuzzyXMLElement parentNode, int attributeCount) {
    assertEquals(attributeCount, parentNode.getAttributes().length);
  }

  public void assertAttribute(FuzzyXMLElement parentNode, String name, String value) {
    FuzzyXMLAttribute attribute = parentNode.getAttributeNode(name);
    assertNotNull(attribute);
    assertEquals(value, attribute.getValue());
  }

  public void testSimpleDocument() {
    FuzzyXMLParser parser = new FuzzyXMLParser(false, true);
    FuzzyXMLDocument doc = parser.parse("<html><head><title>this is a title</title></head><body>this is the body</body></html>");
    FuzzyXMLElement docElement = doc.getDocumentElement();
    assertChildrenElements(docElement, 1, "html");
    {
      FuzzyXMLElement htmlElement = docElement.getChildElement(0);
      assertChildrenElements(htmlElement, 2, "head", "body");
      {
        FuzzyXMLElement headElement = htmlElement.getChildElement(0);
        assertChildrenElements(headElement, 1, "title");
        {
          FuzzyXMLElement titleElement = headElement.getChildElement(0);
          assertText(titleElement, "this is a title");
        }
        FuzzyXMLElement bodyElement = htmlElement.getChildElement(1);
        assertText(bodyElement, "this is the body");
      }
    }
  }

  public void testPartialDocument() {
    FuzzyXMLParser parser = new FuzzyXMLParser(false, true);
    FuzzyXMLDocument doc = parser.parse("<div>this is a div</div>");
    FuzzyXMLElement docElement = doc.getDocumentElement();
    assertChildrenElements(docElement, 1, "div");
    {
      FuzzyXMLElement divElement = docElement.getChildElement(0);
      assertText(divElement, "this is a div");
    }
  }

  public void testAttributes() {
    FuzzyXMLParser parser = new FuzzyXMLParser(false, true);
    FuzzyXMLDocument doc = parser.parse("<div id=\"someid\" class=\"someclass\">this is a div</div>");
    FuzzyXMLElement docElement = doc.getDocumentElement();
    assertChildrenElements(docElement, 1, "div");
    {
      FuzzyXMLElement divElement = docElement.getChildElement(0);
      assertAttributes(divElement, 2);
      assertAttribute(divElement, "id", "someid");
      assertAttribute(divElement, "class", "someclass");
      assertText(divElement, "this is a div");
    }
  }

  public void testAttributesWithSpaces() {
    FuzzyXMLParser parser = new FuzzyXMLParser(false, true);
    FuzzyXMLDocument doc = parser.parse("< div id = \"someid\" class  =   \"someclass\"  >this is a div< /div>");
    FuzzyXMLElement docElement = doc.getDocumentElement();
    assertChildrenElements(docElement, 1, "div");
    {
      FuzzyXMLElement divElement = docElement.getChildElement(0);
      assertAttributes(divElement, 2);
      assertAttribute(divElement, "id", "someid");
      assertAttribute(divElement, "class", "someclass");
      assertText(divElement, "this is a div");
    }
  }

  public void testAttributesWithoutQuotes() {
    FuzzyXMLParser parser = new FuzzyXMLParser(false, true);
    FuzzyXMLDocument doc = parser.parse("<div id=someid class=someclass>this is a div</div>");
    FuzzyXMLElement docElement = doc.getDocumentElement();
    assertChildrenElements(docElement, 1, "div");
    {
      FuzzyXMLElement divElement = docElement.getChildElement(0);
      assertAttributes(divElement, 2);
      assertAttribute(divElement, "id", "someid");
      assertAttribute(divElement, "class", "someclass");
      assertText(divElement, "this is a div");
    }
  }

  public void testAttributeWithHTMLValue() {
    FuzzyXMLParser parser = new FuzzyXMLParser(false, true);
    FuzzyXMLDocument doc = parser.parse("<div id=\"<br>\">this is a div</div>");
    FuzzyXMLElement docElement = doc.getDocumentElement();
    assertChildrenElements(docElement, 1, "div");
    {
      FuzzyXMLElement divElement = docElement.getChildElement(0);
      assertAttributes(divElement, 1);
      assertAttribute(divElement, "id", "<br>");
      assertText(divElement, "this is a div");
    }
  }

  public void testAttributesWithoutQuotesWithSpaces() {
    FuzzyXMLParser parser = new FuzzyXMLParser(false, true);
    FuzzyXMLDocument doc = parser.parse("< div id = someid class  =   someclass  >this is a div< /div>");
    FuzzyXMLElement docElement = doc.getDocumentElement();
    assertChildrenElements(docElement, 1, "div");
    {
      FuzzyXMLElement divElement = docElement.getChildElement(0);
      assertAttributes(divElement, 2);
      assertAttribute(divElement, "id", "someid");
      assertAttribute(divElement, "class", "someclass");
      assertText(divElement, "this is a div");
    }
  }

  public void testTagInAttributesWithQoutesWithoutNestedQuote() {
    FuzzyXMLParser parser = new FuzzyXMLParser(false, true);
    FuzzyXMLDocument doc = parser.parse("<div id=\"<webobject name=SomeWO/>\" class=\"someclass\">this is a div</div>");
    FuzzyXMLElement docElement = doc.getDocumentElement();
    assertChildrenElements(docElement, 1, "div");
    {
      FuzzyXMLElement divElement = docElement.getChildElement(0);
      assertAttributes(divElement, 2);
      assertAttribute(divElement, "id", "<webobject name=SomeWO/>");
      assertAttribute(divElement, "class", "someclass");
      assertChildCount(divElement, 2);
      {
        FuzzyXMLElement emptyElement = divElement.getChildElement(0);
        assertEquals("", emptyElement.getName());
        assertChildrenElements(emptyElement, 1, "webobject");
        {
          FuzzyXMLElement woElement = emptyElement.getChildElement(0);
          assertChildCount(woElement, 0);
          assertOnlyAttribute(woElement, "name", "SomeWO");
        }
        assertText(divElement, 1, "this is a div");
      }
    }
  }

  public void testTwoTagsInAttributesWithQoutesWithoutNestedQuote() {
    FuzzyXMLParser parser = new FuzzyXMLParser(false, true);
    FuzzyXMLDocument doc = parser.parse("<div id=\"<webobject name=SomeWO/>\" class=\"<webobject name=AnotherWO/>\">this is a div</div>");
    FuzzyXMLElement docElement = doc.getDocumentElement();
    assertChildrenElements(docElement, 1, "div");
    {
      FuzzyXMLElement divElement = docElement.getChildElement(0);
      assertAttributes(divElement, 2);
      assertAttribute(divElement, "id", "<webobject name=SomeWO/>");
      assertAttribute(divElement, "class", "<webobject name=AnotherWO/>");
      assertChildCount(divElement, 2);
      {
        FuzzyXMLElement emptyElement = divElement.getChildElement(0);
        assertEquals("", emptyElement.getName());
        assertChildrenElements(emptyElement, 2, "webobject", "webobject");
        {
          FuzzyXMLElement woElement = emptyElement.getChildElement(0);
          assertChildCount(woElement, 0);
          assertOnlyAttribute(woElement, "name", "SomeWO");
          
          FuzzyXMLElement woElement2 = emptyElement.getChildElement(1);
          assertChildCount(woElement2, 0);
          assertOnlyAttribute(woElement2, "name", "AnotherWO");
        }
        assertText(divElement, 1, "this is a div");
      }
    }
    RenderContext renderContext = new RenderContext(true);
    String xml = docElement.toXMLString(renderContext);
    assertEquals("<document><div id=\"<webobject name=SomeWO/>\" class=\"<webobject name=AnotherWO/>\">this is a div</div></document>", xml);
  }
  
  public void testNonHTMLTextOnly() {
    FuzzyXMLParser parser = new FuzzyXMLParser(false, true);
    FuzzyXMLDocument doc = parser.parse("This is a test");
    FuzzyXMLElement docElement = doc.getDocumentElement();
    assertChildCount(docElement, 1);
    assertText(docElement, 0, "This is a test");
  }
  
  public void testLeadingNonHTMLText() {
    FuzzyXMLParser parser = new FuzzyXMLParser(false, true);
    FuzzyXMLDocument doc = parser.parse("This is a test<br/>");
    FuzzyXMLElement docElement = doc.getDocumentElement();
    assertChildCount(docElement, 2);
    assertText(docElement, 0, "This is a test");
    assertChildElement(docElement, 1, "br");
  }
  
  public void testComment() {
    FuzzyXMLParser parser = new FuzzyXMLParser(false, true);
    FuzzyXMLDocument doc = parser.parse("<html><!-- This is a comment. --></html>");
    FuzzyXMLElement docElement = doc.getDocumentElement();
    assertChildCount(docElement, 1);
    {
      FuzzyXMLElement divElement = docElement.getChildElement(0);
      assertChildCount(divElement, 1);
      FuzzyXMLComment comment = (FuzzyXMLComment) divElement.getChild(0);
      assertEquals(" This is a comment. ", comment.getValue());
    }
  }
  
  public void testPreTag() {
    FuzzyXMLParser parser = new FuzzyXMLParser(false, true);
    FuzzyXMLDocument doc = parser.parse("<pre>This is inside\nof a pre-tag.<br/>Another.</pre>");
    FuzzyXMLElement docElement = doc.getDocumentElement();
    assertChildrenElements(docElement, 1, "pre");
    {
      FuzzyXMLElement preElement = docElement.getChildElement(0);
      assertChildCount(preElement, 2);
      assertText(preElement, 0, "This is inside\nof a pre-tag.");
      assertText(preElement, 1, "Another.");
    }
    RenderContext renderContext = new RenderContext(true);
    String xml = docElement.toXMLString(renderContext);
    assertEquals("<document><pre>This is inside\nof a pre-tag.<br/>Another.</pre></document>", xml); // MS: How the heck is this possible?  Where did the <br/> go??! 
  }

  /*
  public void testTagInAttributesWithoutQoutesWithoutNestedQuote() {
    FuzzyXMLParser parser = new FuzzyXMLParser(false, true);
    FuzzyXMLDocument doc = parser.parse("<div id=<webobject name=SomeWO/> class=\"someclass\">this is a div</div>");
    FuzzyXMLElement docElement = doc.getDocumentElement();
    System.out.println("FuzzyXMLParserTest.testTagInAttributesWithoutQoutesWithoutNestedQuote: " + docElement.toDebugString());
    assertChildrenElements(docElement, 1, "div");
    {
      FuzzyXMLElement divElement = docElement.getChildElement(0);
      assertAttributes(divElement, 2);
      assertAttribute(divElement, "id", "<webobject name=SomeWO/>");
      assertAttribute(divElement, "class", "someclass");
      assertChildCount(divElement, 2);
      {
        FuzzyXMLElement emptyElement = divElement.getChildElement(0);
        assertEquals("", emptyElement.getName());
        assertChildrenElements(emptyElement, 1, "webobject");
        {
          FuzzyXMLElement woElement = emptyElement.getChildElement(0);
          assertChildCount(woElement, 0);
          assertOnlyAttribute(woElement, "name", "SomeWO");
        }
        assertText(divElement, 1, "this is a div");
      }
    }
  }
  */
}
