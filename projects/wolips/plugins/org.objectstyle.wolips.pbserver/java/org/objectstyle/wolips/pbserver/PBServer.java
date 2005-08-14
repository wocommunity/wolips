/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowlegement: "This product includes software developed by the ObjectStyle
 * Group (http://objectstyle.org/)." Alternately, this acknowlegement may
 * appear in the software itself, if and wherever such third-party
 * acknowlegements normally appear. 4. The names "ObjectStyle Group" and
 * "Cayenne" must not be used to endorse or promote products derived from this
 * software without prior written permission. For written permission, please
 * contact andrus@objectstyle.org. 5. Products derived from this software may
 * not be called "ObjectStyle" nor may "ObjectStyle" appear in their names
 * without prior written permission of the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/> .
 *  
 */
package org.objectstyle.wolips.pbserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
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

/**
 * @author mike
 */
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
      Element textElementContainer = (Element) elementsList.item(0);
      Text textNode = (Text) textElementContainer.getChildNodes().item(0);
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
      Element textElementContainer = (Element) stringsList.item(0);
      Text textNode = (Text) textElementContainer.getChildNodes().item(0);
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
    appendString(appendArray(responseDocument), project.getName());
    appendString(appendArray(responseDocument), "Application Server");
    appendString(appendArray(responseDocument), "Web Server");
    return responseDocument;
  }

  public Document targetsInProject(Document _requestDocument) throws ParserConfigurationException {
    IProject project = project(_requestDocument, "cookie");
    Document responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    appendString(appendArray(responseDocument), "mockTarget");
    return responseDocument;
  }

  public void nameOfProject(Document _requestDocument, OutputStream _os) {
    IProject project = project(_requestDocument, "projectCookie");
    new PrintStream(_os, true).print(project.getName());
  }

  public void addFilesToProject(Document _requestDocument, OutputStream _os) {
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
    new PrintStream(_os, true).print("YES");
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

  public void nameOfTargetInProject(Document _requestDocument, OutputStream _os) {
    //    stringbuffer.append("<nameOfTarget>");
    //    stringbuffer.append("<targetCookie>" + s + "</targetCookie >");
    //    stringbuffer.append("<projectCookie>" + s1 + "</projectCookie >");
    //    stringbuffer.append("</nameOfTarget>");
    IProject project = project(_requestDocument, "projectCookie");
    Element documentElement = _requestDocument.getDocumentElement();
    String targetCookie = text(documentElement, "targetCookie");
    new PrintStream(_os, true).print(targetCookie);
  }

  public void openFile(Document _requestDocument, OutputStream _os) {
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
    new PrintStream(_os, true).print("YES");
  }

  public void addGroup(Document _requestDocument, OutputStream _os) {
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
    new PrintStream(_os, true).print("YES");
  }

  public void addGroupToPreferredInsertionGroup(Document _requestDocument, OutputStream _os) {
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
    new PrintStream(_os, true).print("YES");
  }

  public void handleRequestDocument(Document _requestDocument, OutputStream _os) throws Throwable {
    Document responseDocument = null;
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
      nameOfProject(_requestDocument, _os);
    }
    else if ("addFilesToProject".equals(nodeName)) {
      addFilesToProject(_requestDocument, _os);
    }
    else if ("filesOfTypesInTargetOfProject".equals(nodeName)) {
      responseDocument = filesOfTypesInTargetOfProject(_requestDocument);
    }
    else if ("nameOfTarget".equals(nodeName)) {
      nameOfTargetInProject(_requestDocument, _os);
    }
    else if ("OpenFile".equals(nodeName)) {
      openFile(_requestDocument, _os);
    }
    else if ("addGroup".equals(nodeName)) {
      addGroup(_requestDocument, _os);
    }
    else if ("addGroupToPreferredInsertionGroup".equals(nodeName)) {
      addGroupToPreferredInsertionGroup(_requestDocument, _os);
    }
    else {
      System.out.println("PBServer.run: Unknown request: " + nodeName);
      responseDocument = null;
    }
    if (responseDocument != null) {
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      DOMSource source = new DOMSource(responseDocument);
      StreamResult result = new StreamResult(_os);
      transformer.transform(source, result);
    }
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
            handleRequestDocument(requestDocument, os);
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
