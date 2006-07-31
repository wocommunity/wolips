/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2006 The ObjectStyle Group 
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne" 
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */
package org.objectstyle.woproject.maven.classpath;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultDocument;
import org.dom4j.tree.DefaultElement;

/**
 * @author uli
 * 
 */
public class WoCP {
	static String path = ".classpath.work";

	static public void main(String[] args) {
		new WoCP();
	}

	public WoCP() {
		doSomething(path);
	}

	void doSomething(String path) {

		try {
			SAXReader xmlReader = new SAXReader();
			
			Document document;

			document = xmlReader.read(new File(path));

			Element element = (Element) document.selectNodes("/classpath").get(
					0);
			List classes = element.selectNodes("classpathentry");
			String woClasspath = "org.objectstyle.wolips.WO_CLASSPATH";
			for (Iterator it = classes.iterator(); it.hasNext();) {
				Element el = (Element) it.next();
				element.remove(el);
				String s;
				boolean found = false;
				if (el.attributeValue("kind").equals("src")) {
					s = el.attributeValue("path");
					if (s.indexOf("src") == -1)
						el.addAttribute("path", "src");
					else {
						if (s.equals(".")) {

							el.addAttribute("path", "src");
						}
					}
					el.addAttribute("excluding", "**/*.wo/**|**/*.api");
				}
				if (el.attributeValue("kind").equals("output")) {
					el.addAttribute("path", "bin");
				}
				if ((s = el.attributeValue("path")).indexOf("WebObjects") != -1) {
					found = true;
					s = s.substring(s.lastIndexOf("/") + 1, s.length());
					s = s.substring(0, s.indexOf("-"));
					System.out.println("Eintrag für " + s + " gefunden.");
				}
				if (found)
					woClasspath = woClasspath + "/" + s;
				else
					element.add(el);

			}
			if (!woClasspath.equals("org.objectstyle.wolips.WO_CLASSPATH")) {
				DefaultElement el = new DefaultElement("classpathentry");
				el.addAttribute("kind", "con");
				el.addAttribute("path", woClasspath);
				element.add(el);
			}
			File f = new File("./");
			File[] dir = f.listFiles();
			// System.out.println(dir.length);

			for (int i = 0; i < dir.length; i++) {
				f = dir[i];
				if (f.isDirectory() && f.getName().indexOf(".subproj") != -1) {
					// System.out.println(f.getName());
					DefaultElement elS = new DefaultElement("classpathentry");
					elS.addAttribute("kind", "src");
					elS.addAttribute("excluding", "**/*.wo/**|**/*.api");
					elS.addAttribute("path", f.getName() + "/src");
					element.add(elS);
				}

			}

			OutputFormat oF = new OutputFormat();
			oF.setTrimText(true);
			oF.setSuppressDeclaration(false);
			// oF.setNewLineAfterDeclaration(false);
			oF.setNewlines(true);
			oF.setIndent(true);
			// System.out.println(((Document)element).getDocType());
			DefaultDocument doc = new DefaultDocument(element);
			XMLWriter writer = new XMLWriter(new FileWriter(new File(
					".classpath.work")), oF);
			writer.write(doc);
			writer.close();
		} catch (DocumentException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

}