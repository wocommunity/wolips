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

package org.objectstyle.woproject.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.IdentityMapper;

/** 
 * Mapper that handles WebObjects resource copying.
 * It handles issues like localization, flattening of WOComponents
 * paths, etc. 
 * 
 * @author Andrei Adamchik
 */
public class WOMapper extends Mapper {
	private final String LPROJ_SUFFIX = ".lproj";
	private final String SUBPROJ_SUFFIX = ".subproj";
	private final String NON_LOCALIZED = "Nonlocalized" + LPROJ_SUFFIX;

	public WOMapper(Project project) {
		super(project);
	}

	public FileNameMapper getImplementation() throws BuildException {
		return new WOFileNameMapper();
	}

	class WOFileNameMapper extends IdentityMapper {

		/**
		 * Returns an one-element array containing the source file name
		 * with a path rewritten using localization rules.
		 */
		public String[] mapFileName(String sourceFileName) {
			// check for default exclusions
			if (NON_LOCALIZED.equals(sourceFileName)) {
				return null;
			}
			// apply filters
			String subprojPath = subprojFilter(sourceFileName);

			String localizedPath = localizationFilter(subprojPath);

			String wocompPath = wocompFilter(localizedPath);
			String miscFilter = miscFilter(wocompPath);
			String finalPath = eomodelFilter(miscFilter);
			return new String[] { finalPath };
		}
		/**
		* Returns destination path based on source file path applying
		* miscancellous rules, like *.strings to <code>path</code> if
		* applicable.
		*/
		private final String miscFilter(String path) {
			File f = new File(path);
			// add other file extensions here!
			if (path.endsWith(".strings")) {
				return flatten(f);
			}
			// skip the filter
			return path;
		}

		/** 
		 * Returns destination path based on source file path applying
		 * WOComponent rules to <code>path</code> if applicable. 
		 */
		private final String wocompFilter(String path) {
			File f = new File(path);

			// WOComponent directory or WOComponent's api
			if (path.endsWith(".wo") || path.endsWith(".api")) {
				return flatten(f);
			}

			// File in WOComponent directory
			String parent = f.getParent();
			if (parent != null && parent.endsWith(".wo")) {
				return flattenWithParent(f);
			}

			// skip the filter
			return path;
		}

		private final String eomodelFilter(String path) {
			File f = new File(path);

			// EOModel directory
			if (path.endsWith(".eomodeld")
				&& !path.endsWith("index.eomodeld")) {
				return flatten(f);
			}

			// File in EOModel directory
			String parent = f.getParent();
			if (parent != null && parent.endsWith(".eomodeld")) {
				return flattenWithParent(f);
			}

			// skip the filter
			return path;
		}

		/** 
		 * Returns destination path based on source file path applying
		 * subproj rules to <code>path</code>. 
		 */
		private String subprojFilter(String path) {
			File f = new File(path);
			File p1 = f.getParentFile();

			// check for localization
			File p2 = null;
			while (p1 != null) {
				p2 = p1;
				p1 = p1.getParentFile();
			}

			if (p2 != null) {
				String topmostParent = p2.getName();
				if (topmostParent.endsWith(SUBPROJ_SUFFIX))
					return path.substring(topmostParent.length());
			}
			return path;
		}
		/** 
		 * Returns destination path based on source file path applying
		 * localization rules to <code>path</code>. 
		 */
		private String localizationFilter(String path) {
			String startPattern = NON_LOCALIZED + File.separator;

			return (path.startsWith(startPattern))
				? path.substring(startPattern.length())
				: path;
		}

		/** 
		 * Flatten path, taking localization into account. No need to check for 
		 * Nonlocalized.lproj here, since it should've been stripped by localization
		 * filter already.
		 */
		private String flatten(File f) {
			File p1 = f.getParentFile();

			// check for localization
			File p2 = null;
			while (p1 != null) {
				p2 = p1;
				p1 = p1.getParentFile();
			}

			if (p2 != null) {
				String topmostParent = p2.getName();
				if (topmostParent.endsWith(LPROJ_SUFFIX)) {
					return topmostParent + File.separator + f.getName();
				}
			}

			return f.getName();
		}

		/** 
		  * Flatten parent path, taking localization into account. No need to check for 
		  * Nonlocalized.lproj here, since it should've been stripped by localization
		  * filter already.
		  */
		private String flattenWithParent(File f) {
			String name = f.getName();
			String parentName = flatten(f.getParentFile());
			return parentName + File.separator + name;
		}
	}
}