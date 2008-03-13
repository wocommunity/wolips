/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2007 The ObjectStyle Group 
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
package org.objectstyle.wolips.jdt.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jdt.ui.JavaElementComparator;
import org.eclipse.jface.viewers.Viewer;
import org.objectstyle.wolips.jdt.ui.tags.TaggedComponentsContentProvider;

public class WOJavaElementComparator extends JavaElementComparator {
	private Set<String> _bundleExtensions;

	public WOJavaElementComparator() {
		_bundleExtensions = new HashSet<String>();
		_bundleExtensions.add("wo");
		_bundleExtensions.add("eomodeld");
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof IResource && e2 instanceof IResource) {
			String name1 = ((IResource) e1).getName();
			String extension1 = null;
			int dot1 = name1.lastIndexOf('.');
			if (dot1 != -1) {
				extension1 = name1.substring(dot1 + 1);
			}
			
			String name2 = ((IResource) e2).getName();
			String extension2 = null;
			int dot2 = name2.lastIndexOf('.');
			if (dot2 != -1) {
				extension2 = name2.substring(dot2 + 1);
			}
			
			if (_bundleExtensions.contains(extension1)) {
				if (e2 instanceof IFile || _bundleExtensions.contains(extension2)) {
					return name1.compareTo(name2);
				}
				else if (e2 instanceof IContainer) {
					return 1;
				}
			}
			else if (_bundleExtensions.contains(extension2)) {
				if (e1 instanceof IFile) {
					return name1.compareTo(name2);
				}
				else if (e1 instanceof IContainer) {
					return -1;
				}
			}
		}
		else if (e1 instanceof TaggedComponentsContentProvider || e2 instanceof TaggedComponentsContentProvider) {
			IPackageFragmentRoot root1 = getPackageFragmentRoot(e1);
			IPackageFragmentRoot root2 = getPackageFragmentRoot(e2);
			if (root1 == null && root2 == null) {
				if (e1 instanceof TaggedComponentsContentProvider) {
					return -1;
				}
				return 1;
			}
			if (root1 != null) {
				return -1;
			}
			return 1;
		}
		
		return super.compare(viewer, e1, e2);
	}

	private IPackageFragmentRoot getPackageFragmentRoot(Object element) {
		if (element instanceof PackageFragmentRootContainer) {
			// return first package fragment root from the container
			PackageFragmentRootContainer cp = (PackageFragmentRootContainer) element;
			Object[] roots = cp.getPackageFragmentRoots();
			if (roots.length > 0)
				return (IPackageFragmentRoot) roots[0];
			// non resolvable - return null
			return null;
		}
		if (!(element instanceof IJavaElement)) {
			return null;
		}
		return JavaModelUtil.getPackageFragmentRoot((IJavaElement) element);
	}
}
