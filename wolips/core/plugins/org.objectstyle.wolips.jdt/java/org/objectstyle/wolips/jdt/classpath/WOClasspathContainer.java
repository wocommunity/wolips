/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 - 2006  The ObjectStyle Group 
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
package org.objectstyle.wolips.jdt.classpath;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.objectstyle.wolips.variables.VariablesPlugin;

/**
 * @author Harald Niesche
 * @author uli
 */
public final class WOClasspathContainer implements IClasspathContainer {
	public static final String WOLIPS_CLASSPATH_CONTAINER_IDENTITY = "org.objectstyle.wolips.WO_CLASSPATH";

	public static final String[] WOLIPS_CLASSPATH_STANDARD_FRAMEWORKS = new String[] { "JavaWebObjects", "JavaFoundation", "JavaXML", "JavaWOExtensions", "JavaEOAccess", "JavaEOControl", "JavaJDBCAdaptor" };

	private IClasspathEntry[] classpathEntries = null;

	private static Map<String, Set<IClasspathEntry>> allClasspathEntries = new HashMap<String, Set<IClasspathEntry>>();

	private IPath id;

	/**
	 * Constructor for WOClassPathContainer.
	 * 
	 * @param id
	 */
	public WOClasspathContainer(IPath id) {
		super();
		this.id = id;
	}

	/**
	 * @see org.eclipse.jdt.core.IClasspathContainer#getClasspathEntries()
	 */
	public IClasspathEntry[] getClasspathEntries() {
		if (classpathEntries == null) {
			initPath();
		}
		return classpathEntries;
	}

	/**
	 * @see org.eclipse.jdt.core.IClasspathContainer#getDescription()
	 */
	public String getDescription() {
		return "WO Frameworks";
	}

	/**
	 * @see org.eclipse.jdt.core.IClasspathContainer#getKind()
	 */
	public int getKind() {
		return IClasspathContainer.K_APPLICATION;
	}

	/**
	 * @see org.eclipse.jdt.core.IClasspathContainer#getPath()
	 */
	public IPath getPath() {
		return id;
	}

	private synchronized void initPath() {
		Set<IClasspathEntry> path = new LinkedHashSet<IClasspathEntry>();
		IPath[] paths = VariablesPlugin.getDefault().getFrameworkRoots();
		for (int i = 1; i < id.segmentCount(); i++) {
			for (int h = 0; h < paths.length; h++) {
				IPath classpathVariable = paths[h];
				String framework = id.segment(i);
				if (!allClasspathEntries.containsKey(framework)) {
					File frameworkFile = new File(classpathVariable.toOSString(), framework + ".framework/Resources/Java");
					if (frameworkFile.isDirectory()) {
						String archives[] = frameworkFile.list(new FilenameFilter() {
							public boolean accept(File dir, String name) {
								String lowerName = name.toLowerCase();
								return (lowerName.endsWith(".zip") || lowerName.endsWith(".jar"));
							}
						});
						IPath source = new Path(classpathVariable.toOSString() + "/" + framework + ".framework/Resources/Java/src.jar");
						if (!source.toFile().exists()) {
							source = null;
						}
						for (int j = 0; j < archives.length; j++) {
							// framework found under this root
							h = paths.length;
							IPath archivePath = new Path(frameworkFile.getAbsolutePath() + "/" + archives[j]);
							// IClasspathEntry entry =
							// JavaCore.newLibraryEntry(archivePath, null,
							// null);
							if (!archives[j].equals("src.jar")) {
								IClasspathAttribute javadoc[] = new IClasspathAttribute[0];
								if (framework.indexOf("Java") == 0) {
									javadoc = new IClasspathAttribute[1];
									String referenceApiString = VariablesPlugin.getDefault().getReferenceApiAsJavaDocCompatibleString();
									if (referenceApiString != null) {
										javadoc[0] = JavaCore.newClasspathAttribute(IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME, referenceApiString);
									}
								}
								IClasspathEntry entry = JavaCore.newLibraryEntry(archivePath, source, null, null, javadoc, false);
								path.add(entry);
								Set<IClasspathEntry> entrySet = WOClasspathContainer.allClasspathEntries.get(framework);
								if (entrySet == null) {
									entrySet = new LinkedHashSet<IClasspathEntry>();
									WOClasspathContainer.allClasspathEntries.put(framework, entrySet);
								}
								entrySet.add(entry);
							}
						}
					}
				} else {
					path.addAll(allClasspathEntries.get(framework));
					h = paths.length;
				}
			}
		}
		classpathEntries = path.toArray(new IClasspathEntry[path.size()]);
	}
}