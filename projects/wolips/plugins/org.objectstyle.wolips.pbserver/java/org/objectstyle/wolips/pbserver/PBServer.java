package org.objectstyle.wolips.pbserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

public class PBServer {
  public static final int DEFAULT_PB_PORT = 8547;
  private ServerSocket myServerSocket;
  private Thread myServerThread;
  private boolean myRunning;

  public PBServer() {
  }

  public synchronized void start(int _port) throws IOException {
    myServerSocket = new ServerSocket(_port);
    myRunning = true;
    myServerThread = new Thread(new ServerSocketAcceptor());
    myServerThread.start();
  }

  public synchronized void stop() throws IOException {
    myRunning = false;
    myServerSocket.close();
  }

  private Element appendArray(Document _document) {
    Element arrayElement = _document.createElement("array");
    _document.appendChild(arrayElement);
    return arrayElement;
  }

  private Element appendArray(Element _element) {
    Element arrayElement = _element.getOwnerDocument().createElement("array");
    _element.appendChild(arrayElement);
    return arrayElement;
  }

  private Element appendString(Element _element, String _string) {
    Element stringElement = _element.getOwnerDocument().createElement("string");
    _element.appendChild(stringElement);
    Text stringText = _element.getOwnerDocument().createTextNode(_string);
    stringElement.appendChild(stringText);
    return stringElement;
  }

  private boolean booleanValue(String _str) {
    return _str.equals("YES");
  }

  private boolean booleanValue(Element _element, String _tagName) {
    return booleanValue(text(_element, _tagName));
  }

  private String text(Element _element, String _tagName) {
    NodeList elementsList = _element.getElementsByTagName(_tagName);
    String text;
    if (elementsList.getLength() > 0) {
      Element textElementContainer = (Element)elementsList.item(0);
      Text textNode = (Text)textElementContainer.getChildNodes().item(0);
      text = textNode.getNodeValue();
    }
    else {
      text = null;
    }
    return text;
  }

  public String[] strings(Element _parentElement, String _arrayName) {
    Element arrayParentElement = (Element) _parentElement.getElementsByTagName(_arrayName).item(0);
    Element arrayElement = (Element) arrayParentElement.getElementsByTagName("array").item(0);
    NodeList stringsList = arrayElement.getChildNodes();
    String[] strings = new String[stringsList.getLength()];
    for (int i = 0; i < strings.length; i++) {
      Element textElementContainer = (Element)stringsList.item(0);
      Text textNode = (Text)textElementContainer.getChildNodes().item(0);
      strings[i] = textNode.getNodeValue();
    }
    return strings;
  }

  private IProject project(Document _requestDocument, String _cookieName) {
    String cookie = text(_requestDocument.getDocumentElement(), _cookieName);
    IContainer container = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(new Path(cookie));
    IProject project = container.getProject();
    return project;
  }

  private String path(Document _requestDocument) {
    String path = text(_requestDocument.getDocumentElement(), "path");
    return path;
  }

  public Document openProjectsAppropriateForFile(Document _requestDocument) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
    String path = path(_requestDocument);
    Document responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    IContainer[] containers = ResourcesPlugin.getWorkspace().getRoot().findContainersForLocation(new Path(path));
    Element arrayElement = appendArray(responseDocument);
    for (int i = 0; i < containers.length; i++) {
      IProject project = containers[i].getProject();
      IFile xcodeFile = project.getFile(project.getName() + ".xcode");
      String xcodeFilePath = xcodeFile.getLocation().toOSString();

      if (containers[i].getName().endsWith(".woa")) {
        //      <?xml version="1.0" encoding="UTF-8"?>
        //      <!DOCTYPE plist PUBLIC "-//Apple Computer//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
        //      <plist version="1.0">
        //      <dict>
        //              <key>PBXProjectSourcePath</key>
        //              <string>/Users/mschrag/TestApp/TestApp.xcodeproj</string>
        //      </dict>
        //      </plist>

        IFile pbDevelopmentResourceFile = containers[i].getFile(new Path("Contents").append("pbdevelopment.plist"));
        Document pbDevelopmentDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element plistElement = pbDevelopmentDocument.createElement("plist");
        plistElement.setAttribute("version", "1.0");
        pbDevelopmentDocument.appendChild(plistElement);
        Element dictElement = pbDevelopmentDocument.createElement("dict");
        plistElement.appendChild(dictElement);

        Element keyElement = pbDevelopmentDocument.createElement("key");
        dictElement.appendChild(keyElement);
        keyElement.appendChild(pbDevelopmentDocument.createTextNode("PBXProjectSourcePath"));

        Element stringElement = pbDevelopmentDocument.createElement("string");
        dictElement.appendChild(stringElement);
        stringElement.appendChild(pbDevelopmentDocument.createTextNode(xcodeFilePath));

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(pbDevelopmentDocument);
        File pbDevelopmentFile = pbDevelopmentResourceFile.getLocation().toFile();
        StreamResult result = new StreamResult(pbDevelopmentFile);
        transformer.transform(source, result);
      }
      appendString(arrayElement, xcodeFilePath);
    }
    return responseDocument;
  }

  public Document targetsInProjectContainingFile(Document _requestDocument) throws ParserConfigurationException {
    IProject project = project(_requestDocument, "cookie");
    String path = path(_requestDocument);
    IContainer[] containers = ResourcesPlugin.getWorkspace().getRoot().findContainersForLocation(new Path(path));
    Document responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    appendString(appendArray(responseDocument), "mockTarget");
    return responseDocument;
  }

  public Document targetsInProject(Document _requestDocument) throws ParserConfigurationException {
    IProject project = project(_requestDocument, "cookie");
    Document responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    appendString(appendArray(responseDocument), "mockTarget");
    return responseDocument;
  }

  public Document nameOfProject(Document _requestDocument) throws ParserConfigurationException {
    IProject project = project(_requestDocument, "projectCookie");
    Document responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    appendString(appendArray(responseDocument), project.getName());
    return responseDocument;
  }

  public Document addFilesToProject(Document _requestDocument) throws ParserConfigurationException {
    Element documentElement = _requestDocument.getDocumentElement();
    String[] addFiles = strings(documentElement, "addFiles");
    for (int i = 0; i < addFiles.length; i++) {
      System.out.println("PBServer.addFilesToProject: add files " + addFiles[i]);
    }
    IProject project = project(_requestDocument, "toProject");
    String nearFile = text(documentElement, "nearFile");
    String preferredInsertionGroupName = text(documentElement, "preferredInsertionGroupName");
    String[] addToTargets = strings(documentElement, "addToTargets");
    boolean copyIntoGroupFolder = booleanValue(documentElement, "copyIntoGroupFolder");
    boolean createGroupsRecursively = booleanValue(documentElement, "createGroupsRecursively");
    Document responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    // NTS: Not implemented
    return responseDocument;
  }

  public Document filesOfTypesInTargetOfProject(Document _requestDocument) throws ParserConfigurationException, CoreException {
    //    stringbuffer.append("<filesOfTypesInTargetOfProject>");
    //    stringbuffer.append("<cookie>" + s1 + "</cookie>");
    //    stringbuffer.append("<target>" + s + "</target>");
    //    stringbuffer.append("<typesArray>" + _xmlStringArray(nsarray) + "</typesArray>");
    //    stringbuffer.append("</filesOfTypesInTargetOfProject>");
    IProject project = project(_requestDocument, "cookie");
    Element documentElement = _requestDocument.getDocumentElement();
    String targetName = text(documentElement, "target");
    String[] typesArray = strings(documentElement, "typesArray");
    FileTypeResourceVisitor visitor = new FileTypeResourceVisitor(typesArray);
    project.accept(visitor, IResource.DEPTH_INFINITE, IContainer.EXCLUDE_DERIVED);
    Document responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Element arrayElement = appendArray(responseDocument);
    Iterator matchingResourcesIter = visitor.getMatchingFiles().iterator();
    while (matchingResourcesIter.hasNext()) {
      IResource resource = (IResource) matchingResourcesIter.next();
      appendString(arrayElement, resource.getLocation().toOSString());
    }
    return responseDocument;
  }

  public Document nameOfTargetInProject(Document _requestDocument) throws ParserConfigurationException {
    //    stringbuffer.append("<nameOfTarget>");
    //    stringbuffer.append("<targetCookie>" + s + "</targetCookie >");
    //    stringbuffer.append("<projectCookie>" + s1 + "</projectCookie >");
    //    stringbuffer.append("</nameOfTarget>");
    IProject project = project(_requestDocument, "projectCookie");
    Element documentElement = _requestDocument.getDocumentElement();
    String targetCookie = text(documentElement, "targetCookie");
    Document responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    appendString(appendArray(responseDocument), targetCookie);
    return responseDocument;
  }

  public Document openFile(Document _requestDocument) throws ParserConfigurationException {
    //  stringbuffer.append("<OpenFile><filename>");
    //  stringbuffer.append(s);
    //  stringbuffer.append("</filename><linenumber>");
    //  stringbuffer.append(i);
    //  stringbuffer.append("</linenumber><message>");
    //  stringbuffer.append(s1);
    //  stringbuffer.append("</message></OpenFile>");
    String filename = text(_requestDocument.getDocumentElement(), "filename");
    String lineNumberStr = text(_requestDocument.getDocumentElement(), "linenumber");
    String message = text(_requestDocument.getDocumentElement(), "message");
    final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(filename));
    final int lineNumber = Integer.parseInt(lineNumberStr);
    if (file != null) {
      IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
      for (int i = 0; i < workbenchWindows.length; i++) {
        final IWorkbenchPage workbenchPage = workbenchWindows[i].getActivePage();
        if (workbenchPage != null) {
          Display.getDefault().asyncExec(new Runnable() {
            public void run() {
              try {
                IEditorPart editorPart = IDE.openEditor(workbenchPage, file, true);
                if (editorPart != null && editorPart instanceof ITextEditor) {
                  ITextEditor textEditor = (ITextEditor) editorPart;
                  IDocumentProvider documentProvider = textEditor.getDocumentProvider();
                  IDocument document = documentProvider.getDocument(textEditor.getEditorInput());
                  int lineOffset = document.getLineOffset(lineNumber - 1);
                  textEditor.selectAndReveal(lineOffset, 0);
                }
              }
              catch (Throwable t) {
                t.printStackTrace(System.out);
              }
            }
          });
        }
      }
    }
    Document responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    return responseDocument;
  }

  public Document addGroup(Document _requestDocument) throws ParserConfigurationException {
    //    stringbuffer.append("<addGroup>");
    //    stringbuffer.append("<name>" + s + "</name >");
    //    if(s1 != null)
    //        stringbuffer.append("<path>" + s1 + "</path >");
    //    stringbuffer.append("<projectCookie>" + s2 + "</projectCookie >");
    //    if(s3 != null)
    //        stringbuffer.append("<nearFile>" + s3 + "</nearFile >");
    //    stringbuffer.append("</addGroup>");
    String name = text(_requestDocument.getDocumentElement(), "name");
    String path = text(_requestDocument.getDocumentElement(), "path");
    System.out.println("PBServer.addGroup: name = " + name);
    IProject project = project(_requestDocument, "projectCookie");
    String nearFile = text(_requestDocument.getDocumentElement(), "nearFile");
    Document responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    // NTS: Not implemented
    return responseDocument;
  }

  public Document addGroupToPreferredInsertionGroup(Document _requestDocument) throws ParserConfigurationException {
    //    stringbuffer.append("<addGroupToPreferredInsertionGroup>");
    //    stringbuffer.append("<name>" + s + "</name >");
    //    if(s1 != null)
    //        stringbuffer.append("<path>" + s1 + "</path >");
    //    stringbuffer.append("<projectCookie>" + s2 + "</projectCookie >");
    //    if(s3 != null)
    //        stringbuffer.append("<nearFile>" + s3 + "</nearFile >");
    //    if(s4 != null)
    //        stringbuffer.append("<preferredInsertionGroup>" + s4 + "</preferredInsertionGroup >");
    //    stringbuffer.append("</addGroupToPreferredInsertionGroup>");
    String name = text(_requestDocument.getDocumentElement(), "name");
    System.out.println("PBServer.addGroupToPreferredInsertionGroup: name = " + name);
    String path = text(_requestDocument.getDocumentElement(), "path");
    IProject project = project(_requestDocument, "projectCookie");
    String nearFile = text(_requestDocument.getDocumentElement(), "nearFile");
    String preferredInsertionGroup = text(_requestDocument.getDocumentElement(), "preferredInsertionGroup");
    Document responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    // NTS: Not implemented
    return responseDocument;
  }

  public Document handleRequestDocument(Document _requestDocument) throws Throwable {
    Document responseDocument;
    String nodeName = _requestDocument.getDocumentElement().getNodeName();
    if ("openProjectsAppropriateForFile".equals(nodeName)) {
      responseDocument = openProjectsAppropriateForFile(_requestDocument);
    }
    else if ("targetsInProjectContainingFile".equals(nodeName)) {
      responseDocument = targetsInProjectContainingFile(_requestDocument);
    }
    else if ("targetsInProject".equals(nodeName)) {
      responseDocument = targetsInProject(_requestDocument);
    }
    else if ("nameOfProject".equals(nodeName)) {
      responseDocument = nameOfProject(_requestDocument);
    }
    else if ("addFilesToProject".equals(nodeName)) {
      responseDocument = addFilesToProject(_requestDocument);
    }
    else if ("filesOfTypesInTargetOfProject".equals(nodeName)) {
      responseDocument = filesOfTypesInTargetOfProject(_requestDocument);
    }
    else if ("nameOfTarget".equals(nodeName)) {
      responseDocument = nameOfTargetInProject(_requestDocument);
    }
    else if ("OpenFile".equals(nodeName)) {
      responseDocument = openFile(_requestDocument);
    }
    else if ("addGroup".equals(nodeName)) {
      responseDocument = addGroup(_requestDocument);
    }
    else if ("addGroupToPreferredInsertionGroup".equals(nodeName)) {
      responseDocument = addGroupToPreferredInsertionGroup(_requestDocument);
    }
    else {
      System.out.println("PBServer.run: Unknown request: " + nodeName);
      responseDocument = null;
    }
    return responseDocument;
  }

  public class FileTypeResourceVisitor implements IResourceVisitor {
    private String[] myFileTypes;
    private List myMatchingFiles;

    public FileTypeResourceVisitor(String[] _fileTypes) {
      myFileTypes = _fileTypes;
      myMatchingFiles = new LinkedList();
    }

    public List getMatchingFiles() {
      return myMatchingFiles;
    }

    public boolean visit(IResource _resource) throws CoreException {
      boolean matched = false;
      for (int i = 0; !matched && i < myFileTypes.length; i++) {
        if (myFileTypes[i].equalsIgnoreCase(_resource.getFileExtension())) {
          myMatchingFiles.add(_resource);
          matched = true;
        }
      }
      return true;
    }
  }

  public class ServerSocketAcceptor implements Runnable {
    public void run() {
      while (myRunning) {
        try {
          Socket socket = myServerSocket.accept();
          OutputStream os = socket.getOutputStream();
          try {
            InputStream is = socket.getInputStream();
            StringBuffer sb = new StringBuffer();
            sb.append((char) is.read());
            while (is.available() > 0) {
              int ch = is.read();
              sb.append((char) ch);
            }
            Document requestDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(sb.toString())));
            Document responseDocument = handleRequestDocument(requestDocument);
            if (responseDocument != null) {
              Transformer transformer = TransformerFactory.newInstance().newTransformer();
              transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
              transformer.setOutputProperty(OutputKeys.INDENT, "yes");
              DOMSource source = new DOMSource(responseDocument);
              StreamResult result = new StreamResult(os);
              transformer.transform(source, result);
            }
          }
          finally {
            os.flush();
            os.close();
          }
        }
        catch (Throwable t) {
          t.printStackTrace(System.out);
        }
      }
    }
  }
}
