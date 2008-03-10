/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 - 2006 The ObjectStyle Group 
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

package org.objectstyle.woenvironment.env;

import java.io.File;
import java.util.Map;

/**
 * @author uli
 * 
 * To prevent static variables create an instance of WOEnvironment to access the
 * environment and WOVariables.
 */

public final class WOEnvironment extends Environment {
	private WOVariables woVariables;

	public WOEnvironment() {
		super();
		woVariables = new WOVariables(this);
	}

	/**
	 * Creates new WOEnvironment, specifying the list of alternative properties.
	 * 
	 * @param properties
	 */
	public WOEnvironment(Map altProperties) {
		super();
		woVariables = new WOVariables(this, altProperties);
	}

	/**
	 * @return WOVariables
	 */
	public WOVariables getWOVariables() {
		return woVariables;
	}

	/**
	 * Method wo5or51 returns true if the installe WO version is 5.0 or 5.1.
	 * 
	 * @return boolean
	 */
	public boolean wo5or51() {
		return (this.bootstrap() == null);
	}

	/**
	 * Method wo52 returns true if the installe WO version is 5.2.
	 * 
	 * @return boolean
	 */
	public boolean wo52() {
		return !this.wo5or51();
	}

	/**
	 * Method bootstrap returns the bootstrap.jar if it exists.
	 * 
	 * @param project
	 * @return File
	 */
	public File bootstrap() {
		File aFile = null;
		String propertiesBootstrapJar = this.getWOVariables().bootstrapJar();
		if (propertiesBootstrapJar != null) {
		  aFile = new File(propertiesBootstrapJar);
		  if (aFile != null && aFile.exists()) {
		    return aFile;
		  }
		}
		aFile = this.macBootstrap();
		if ((aFile != null) && (aFile.exists()))
			return aFile;
		aFile = this.winBootstrap();
		if ((aFile != null) && (aFile.exists()))
			return aFile;
		aFile = this.otherBootstrap();
		if ((aFile != null) && (aFile.exists()))
			return aFile;
		return null;
	}

	/**
	 * Method macBootstrap.
	 * 
	 * @param project
	 * @return File
	 */
	private File macBootstrap() {
		File aFile = null;
		try {
			aFile = new File(this.getWOVariables().systemRoot() + "/Library/WebObjects/JavaApplications/wotaskd.woa/WOBootstrap.jar");
			if (aFile.exists())
				return aFile;
		} catch (Exception anException) {
			System.out.println(anException);
		}
		return null;
	}

	/**
	 * Method winBootstrap.
	 * 
	 * @param project
	 * @return File
	 */
	private File winBootstrap() {
		File aFile = null;
		try {
			aFile = new File(this.getWOVariables().systemRoot() + "/Library/WebObjects/JavaApplications/wotaskd.woa/WOBootstrap.jar");
			if (aFile.exists())
				return aFile;
		} catch (Exception anException) {
			System.out.println(anException);
		}
		return null;
	}

	/**
	 * Method otherBootstrap.
	 * 
	 * @param project
	 * @return File
	 */
	private File otherBootstrap() {
		File aFile = null;
		try {
			aFile = new File(this.getWOVariables().systemRoot() + "\\Library\\WebObjects\\JavaApplications\\wotaskd.woa\\WOBootstrap.jar");
			if (aFile.exists())
				return aFile;
		} catch (Exception anException) {
			System.out.println(anException);
		}
		return null;
	}

	public boolean variablesConfigured() {
		return getWOVariables().systemRoot() != null && getWOVariables().localRoot() != null;
	}
}
