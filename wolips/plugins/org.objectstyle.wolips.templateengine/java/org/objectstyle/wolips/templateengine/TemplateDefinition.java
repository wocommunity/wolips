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

package org.objectstyle.wolips.templateengine;

import static org.objectstyle.wolips.baseforplugins.util.CharSetUtils.ENCODING_UTF8;

import java.io.File;

/**
 * @author ulrich
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TemplateDefinition {

	private String templateName;

	private String destination;

	private String finalName;

	private String type;
	
	private String encoding = DEFAULT_ENCODING;
	
	private static final String DEFAULT_ENCODING = ENCODING_UTF8;

	/**
	 * @param templateName
	 * @param destination
	 * @param finalName
	 * @param type
	 */
	public TemplateDefinition(String templateName, String destination, String finalName, String type) {
		this(templateName, destination, finalName, type, DEFAULT_ENCODING);
	}

	/**
	 * @param templateName
	 * @param destination
	 * @param finalName
	 * @param type
	 * @param encoding
	 */
	public TemplateDefinition(String templateName, String destination, String finalName, String type, String encoding) {
		super();
		this.templateName = templateName;
		this.destination = destination;
		this.finalName = finalName;
		this.type = type;
		this.encoding = encoding;
	}

	
	/**
	 * @return
	 */
	public String getTemplateName() {
		return this.templateName;
	}

	/**
	 * @param finalName
	 *            Sets the final name without the extension
	 */
	public void setFinalName(String finalName) {
		this.finalName = finalName;
	}

	/**
	 * @return
	 */
	public String getDestinationPath() {

		StringBuffer returnValue = new StringBuffer(this.destination);
		returnValue.append(File.separator);
		returnValue.append(this.finalName);
		if (!this.finalName.equals(this.type) && !this.finalName.endsWith("." + this.type)) {
			returnValue.append(".");
			returnValue.append(this.type);
		}
		return returnValue.toString();
	}

	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @return Returns the destination.
	 */
	public String getDestination() {
		return this.destination;
	}

	/**
	 * @param destination
	 *            The destination to set.
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}