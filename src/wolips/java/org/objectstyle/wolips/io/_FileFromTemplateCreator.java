/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 The ObjectStyle Group 
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

package org.objectstyle.wolips.io;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Hashtable;

import org.eclipse.core.runtime.IPath;
import org.objectstyle.wolips.plugin.WOLipsPlugin;
import org.objectstyle.wolips.utils.WOLipsUtils;
import org.objectstyle.woproject.env.WOVariables;
import org.w3c.dom.Document;
/**
 * @author uli
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public abstract class _FileFromTemplateCreator {
//	translate expected variable strings to int 
	// for switch case in expandVariable
	protected static final int PLUGIN_NAME = 0;
	protected static final int CLASS = 1;
	protected static final int DATE = 2;
	protected static final int PROJECT_NAME = 3;
	protected static final int PACKAGE_NAME = 4;
	protected static final int ADAPTOR_NAME = 5;
	protected static final int BUILD_DIR = 6;
	protected static final int NEXT_ROOT = 7;
	protected static final String[] ALL_KEYS =
		{
			"PLUGIN_NAME",
			"CLASS",
			"DATE",
			"PROJECT_NAME",
			"PACKAGE_NAME",
			"ADAPTOR_NAME",
			"BUILD_DIR",
			"NEXT_ROOT" };
	protected static Hashtable keyToIntegerDict;
	protected static Document templateDocument;
	protected static Hashtable variableInfo;
	//private static Properties templateProperties;
	/**
		 * The file template document is a parsed xml file resource containing
		 * all file templates.
		 * <p> 
		 * @return the templateDocument
		 */
	protected static synchronized Document getFileTemplateDocument()
		throws InvocationTargetException {
		if (templateDocument == null) {
			IPath templatePath;
			File templateFile;
			try {
				InputStream input =
					(new URL(WOLipsPlugin.baseURL(),
						WOLipsUtils.woTemplateDirectory()
							+ WOLipsUtils.woTemplateFiles()))
						.openStream();
				templateDocument =
					XercesDocumentBuilder.documentBuilder().parse(input);
			} catch (Exception e) {
				throw new InvocationTargetException(e);
			}
			finally {
				templatePath = null;
				templateFile = null;
			}
		}
		return templateDocument;
	}
	/**
	 * Method getKeyToIntegerDict.
	 * @return Hashtable
	 */
	protected static Hashtable getKeyToIntegerDict() {
		if (keyToIntegerDict == null) {
			// build keyToIntegerDict
			keyToIntegerDict = new Hashtable(ALL_KEYS.length);
			for (int i = 0; i < ALL_KEYS.length; i++) {
				keyToIntegerDict.put(ALL_KEYS[i], new Integer(i));
			}
		}
		return keyToIntegerDict;
	}
}
