package org.objectstyle.wolips.womodeler;

import java.io.StringReader;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.objectstyle.wolips.womodeler.server.IRequestHandler;
import org.objectstyle.wolips.womodeler.server.Request;
import org.objectstyle.wolips.womodeler.server.Webserver;

public class WOModelerRequestHandler implements IRequestHandler {
  public void init(Webserver server) {
    // DO NOTHING
  }

  public Document handleRequest(Document requestDocument) {
    Element root = new Element("Mike");
    root.setText("Yes");
    Document response = new Document(root);
    return response;
  }

  public void handle(Request request) throws Exception {
    String content = request.getContent();
    if (content != null) {
      Document requestDoc = new SAXBuilder().build(new StringReader(content));
      Document responseDoc = handleRequest(requestDoc);
      if (responseDoc != null) {
        new XMLOutputter().output(responseDoc, request.getWriter());
      }
    }
  }
}
