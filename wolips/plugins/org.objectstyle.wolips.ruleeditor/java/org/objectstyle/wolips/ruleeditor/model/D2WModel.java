/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
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
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.ruleeditor.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.objectstyle.cayenne.wocompat.PropertyListSerialization;

/**
 * @author uli
 */
public class D2WModel {

	private static final String RULES_LIST_KEY = "rules";

	private String modelPath;

	private ArrayList rules;

	private boolean hasUnsavedChanges = false;

	private Map modelMap;

	public D2WModel() {
		super();
	}

	public void init(String path) throws IOException {
		this.modelPath = path;
		File projectFile = null;
		InputStream in = null;
		try {
			projectFile = new File(path);
			in = new FileInputStream(projectFile);
			modelMap = (Map) PropertyListSerialization
					.propertyListFromStream(in);
		} finally {
			projectFile = null;
			if (in != null) {
				in.close();
			}
			in = null;
		}
		if (path == null) {
			throw new IOException("Error reading project file: " + path);
		}
	}

	public List getRules() {
		if (rules != null) {
			return rules;
		}
		List list = (List) this.modelMap.get(RULES_LIST_KEY);
		if (list == null) {
			return null;
		}
		rules = new ArrayList(list.size());
		for (int i = 0; i < list.size(); i++) {
			Map map = (Map) list.get(i);
			Rule rule = new Rule(this, map);
			rules.add(i, rule);
		}
		return rules;
	}

	/**
	 * Stores changes made to this object in the underlying d2wmodel file.
	 */
	public void saveChanges() {
		File projectFile = null;
		try {
			projectFile = new File(modelPath);
			PropertyListSerialization.propertyListToFile(projectFile,
					this.modelMap);
		} finally {
			projectFile = null;
		}
		this.setHasUnsavedChanges(false);
	}

	public boolean hasUnsavedChanges() {
		return hasUnsavedChanges;
	}
	
	public void setHasUnsavedChanges(boolean hasUnsavedChanges) {
		this.hasUnsavedChanges = hasUnsavedChanges;
	}
}
