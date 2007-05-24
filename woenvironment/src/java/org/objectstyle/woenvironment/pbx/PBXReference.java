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
package org.objectstyle.woenvironment.pbx;

import java.util.Collection;
import java.util.Vector;

/**
 * @author tlg
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
@SuppressWarnings("unchecked")
public class PBXReference extends PBXItem {
	public static final String _KNAME = "name";

	public static final String _KPATH = "path";

	public static final String _KREFTYPE = "refType";

	protected Collection children;

	protected String name;

	protected String path;

	protected int refType;

	private PBXReference parent;

	public PBXReference(Object ref) {
		super(ref);
		children = new Vector();
		parent = null;
	}

	public void addChildren(Object child) {
		((PBXReference) child).setParent(this);
		this.children.add(child);
	}

	public void setChildren(Object child) {
		System.out.println(child.getClass());
	}

	public Collection getChildren() {
		return this.children;
	}

	public void setName(Object name) {
		this.name = (String) name;
	}

	public String getName() {
		return this.name;
	}

	public void setPath(Object path) {
		this.path = (String) path;
	}

	public String getPath() {
		return this.path;
	}

	public void setRefType(Object refType) {
		this.refType = Integer.parseInt(refType.toString());
	}

	public int getRefType() {
		return this.refType;
	}

	protected void setParent(PBXReference parent) {
		this.parent = parent;
	}

	protected String fileSeparator() {
		return (this.path == null || this.path.equals("")) ? "" : "/";
	}

	/**
	 * @return
	 */
	public String realPath() {
		String realPath = "";
		switch (this.refType) {
		case 0:
			realPath = (this.path == null) ? "" : (this.path + fileSeparator());
			break;
		case 1:
			realPath = (this.path == null) ? "" : (this.path + fileSeparator());
			break;
		case 2:
			realPath = (this.path == null) ? "" : (this.path + fileSeparator());
			break;
		case 3:
			realPath = (this.path == null) ? "" : (this.path + fileSeparator());
			break;
		case 4:
			realPath = ((parent == null) ? "" : parent.realPath()) + ((this.path == null) ? "" : (this.path + fileSeparator()));
			break;
		default:
			realPath = (this.path == null) ? "" : (this.path + fileSeparator());
		}
		return realPath;
	}
}