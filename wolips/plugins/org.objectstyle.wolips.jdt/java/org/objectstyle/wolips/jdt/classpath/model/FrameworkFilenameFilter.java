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
package org.objectstyle.wolips.jdt.classpath.model;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ulrich
 */
public class FrameworkFilenameFilter implements FilenameFilter {

	private Root root;

	private List<Framework> frameworks = new ArrayList<Framework>();

	protected List<Framework> frameworks() {
		return this.frameworks;
	}

	/**
	 * @param file
	 * @param name
	 * @return
	 */
	public boolean accept(File file, String name) {
		/* name.startsWith("Java") && */
		boolean candidate = name.endsWith(".framework");

		boolean result = false;

		if (candidate) {
			File resDir = new File(file, name + "/Resources/Java");
			if (resDir.exists()) {

				String jarFiles[] = resDir.list(new WildcardFilenameFilter(null, ".jar", "src.jar"));
				String zipFiles[] = resDir.list(new WildcardFilenameFilter(null, ".zip"));

				String srcFiles[] = resDir.list(new WildcardFilenameFilter("src", ".jar"));
				String srcFile = null;
				if (srcFiles != null && srcFiles.length > 0)
					srcFile = srcFiles[0];

				result = (0 != jarFiles.length) || (0 != zipFiles.length);
				Framework framework = new Framework(name.substring(0, name.length() - ".framework".length()), this.root, jarFiles, zipFiles, srcFile);

				this.frameworks.add(framework);
			}

		}

		return (result);
	}

	/**
	 * @param root
	 *            The root to set.
	 */
	protected void setRoot(Root root) {
		this.root = root;
		this.frameworks = new ArrayList<Framework>();
	}

	private static final class WildcardFilenameFilter implements FilenameFilter {
		WildcardFilenameFilter(String prefix, String suffix) {
			this(prefix, suffix, null);
		}

		WildcardFilenameFilter(String prefix, String suffix, String exclude) {
			this._prefix = prefix;
			this._suffix = suffix;
			this._exclude = exclude;
		}

		public boolean accept(File file, String name) {

			String lowerName = name.toLowerCase();

			return (((null == this._exclude) || (!lowerName.equals(this._exclude))) && ((null == this._prefix) || lowerName.startsWith(this._prefix)) && ((null == this._suffix) || lowerName.endsWith(this._suffix)));
		}

		String _prefix;

		String _suffix;

		String _exclude;
	}
}