/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2004 The ObjectStyle Group 
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
package org.objectstyle.woproject.ant;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.objectstyle.cayenne.wocompat.PropertyListSerialization;

/**
 * @author ulrich
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class EOModel extends Task {
	protected String password;
	protected String username;
	protected String url;
	protected File eomodelFile;
	/**
	 * Sets the password.
	 * @param password The password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Returns the password.
	 * @return String
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the username.
	 * @param username The username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Returns the username.
	 * @return String
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Sets the url.
	 * @param username The url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Returns the url.
	 * @return String
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * Returns the eomodelFile.
	 * @return File
	 */
	public File getEomodelFile() {
		return eomodelFile;
	}

	/**
	 * Sets the eomodelFile.
	 * @param eomodelFile The eomodelFile to set
	 */
	public void setEomodelFile(File eomodelFile) {
		this.eomodelFile = eomodelFile;
	}

	/**
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute() throws BuildException {
		validateAttributes();
		Map index;
		try {
			index = (Map)PropertyListSerialization.propertyListFromFile(eomodelFile);
			Map connectionDictionary = (Map)index.get("connectionDictionary");
			if(connectionDictionary != null) {
				if(password != null)
					connectionDictionary.put("password", password);
				if(username != null)
					connectionDictionary.put("username", username);
				if(url != null)
					connectionDictionary.put("URL", url);
				index.put("connectionDictionary", connectionDictionary);
				PropertyListSerialization.propertyListToFile(eomodelFile, index);
				}
			} catch (IOException ioex) {
			log("Error reading/saving eomodel file", Project.MSG_ERR);
			throw new BuildException("Error reading/saving eomodel file", ioex);
		} finally {
			index = null;
		}
	}

	/**
	 * Ensure we have a consistent and legal set of attributes, and set any
	 * internal flags necessary based on different combinations of attributes.
	 *
	 * @throws BuildException if task attributes are inconsistent or missing.
	 */
	protected void validateAttributes() throws BuildException {
		if (eomodelFile == null) {
			throw new BuildException("Required 'eomodelFile' attribute is missing.");
		}
	}
}
