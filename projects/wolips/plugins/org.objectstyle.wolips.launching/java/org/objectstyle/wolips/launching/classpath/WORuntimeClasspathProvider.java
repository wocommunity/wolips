/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2002 - 2005 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  3. The end-user documentation included with the redistribution, if any,
 * must include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 *  4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse
 * or promote products derived from this software without prior written
 * permission. For written permission, please contact andrus@objectstyle.org.
 *  5. Products derived from this software may not be called "ObjectStyle" nor
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
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/> .
 *  
 */

package org.objectstyle.wolips.launching.classpath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.StandardClasspathProvider;
import org.objectstyle.wolips.commons.logging.PluginLogger;
import org.objectstyle.wolips.datasets.adaptable.JavaProject;
import org.objectstyle.wolips.jdt.JdtPlugin;
import org.objectstyle.wolips.launching.LaunchingPlugin;

/**
 * @author hn3000
 * @author Gary Watkins
 */
public class WORuntimeClasspathProvider extends StandardClasspathProvider {
  /**
   * Comment for <code>ID</code>
   */
  public final static String ID = WORuntimeClasspathProvider.class.getName();

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jdt.launching.IRuntimeClasspathProvider#resolveClasspath(org.eclipse.jdt.launching.IRuntimeClasspathEntry[],
   *      org.eclipse.debug.core.ILaunchConfiguration)
   */
  public IRuntimeClasspathEntry[] resolveClasspath(IRuntimeClasspathEntry[] entries, ILaunchConfiguration configuration) throws CoreException {

    if (entries.length == 0) {
      return entries;
    }

    // keep track of all of the projects
    List projectList = new ArrayList();
    // keeps a list of all the jars by project name
    Map projectJars = new HashMap();

    // keep track of all of the frameworks
    ArrayList frameworks = new ArrayList();

    // pattern to recognize a framework
    Pattern pattern = Pattern.compile("^*/(\\w+\\.framework)/Resources/Java/(.*\\.jar)$");

    // resolve WO framework/application projects ourselves, let super do the rest
    IRuntimeClasspathEntry[] entries2 = super.resolveClasspath(entries, configuration);
    for (int i = 0; i < entries2.length; ++i) {
      IRuntimeClasspathEntry entry = entries2[i];
      String name = entry.getPath().segment(0);
      int entryType = entry.getType();

      if (entryType == IRuntimeClasspathEntry.PROJECT) {
        if (!projectJars.containsKey(name)) {
          IProject project = (IProject) entry.getResource();
          JavaProject javaProject = (JavaProject) JavaCore.create(project).getAdapter(JavaProject.class);
          IPath path = javaProject.getWOJavaArchive();

          entry = JavaRuntime.newArchiveRuntimeClasspathEntry(path);

          ArrayList al = new ArrayList();
          al.add(entry);
          projectJars.put(name, al);
          projectList.add(name);
        }
      }
      else if (entryType == IRuntimeClasspathEntry.ARCHIVE) {
        // if it has .framework in the name it is a framework
        Matcher match = pattern.matcher(entry.getPath().toString());
        if (match.find()) {
          frameworks.add(entry);
        }
        else if (projectJars.containsKey(name)) {
          ArrayList al = (ArrayList) projectJars.get(name);
          al.add(entry);
        }
      }
    }

    // final classpath
    ArrayList resolved = new ArrayList();

    // used to make sure that we don't add anything twice
    Set frameworkSet = new HashSet();

    // loop through all of the projects first
    for (Iterator it = projectList.iterator(); it.hasNext();) {
      String name = (String) it.next();
      ArrayList al = (ArrayList) projectJars.get(name);
      for (Iterator it2 = al.iterator(); it2.hasNext();) {
        IRuntimeClasspathEntry cpe = (IRuntimeClasspathEntry) it2.next();
        Matcher match = pattern.matcher(cpe.getPath().toString());
        if (match.find()) {
          String frameworkJar = match.group(1) + "-" + match.group(2);
          if (!frameworkSet.contains(frameworkJar)) {
            frameworkSet.add(frameworkJar);
            resolved.add(cpe);
            LaunchingPlugin.getDefault().debug(cpe.getPath());
          }
          else {
        	  LaunchingPlugin.getDefault().debug("Bypassing - " + cpe.getPath());
          }
        }
        else {
          resolved.add(cpe);
          LaunchingPlugin.getDefault().debug(cpe.getPath());
        }
      }
    }

    // loop through all of the frameworks
    for (Iterator it = frameworks.iterator(); it.hasNext();) {
      IRuntimeClasspathEntry cpe = (IRuntimeClasspathEntry) it.next();
      Matcher match = pattern.matcher(cpe.getPath().toString());
      if (match.find()) {
        String frameworkJar = match.group(1) + "-" + match.group(2);
        if (!frameworkSet.contains(frameworkJar)) {
          frameworkSet.add(frameworkJar);
          resolved.add(cpe);
          LaunchingPlugin.getDefault().debug(cpe.getPath());
        }
        else {
        	LaunchingPlugin.getDefault().debug("Bypassing - " + cpe.toString());
        }
      }
    }

    return (IRuntimeClasspathEntry[]) resolved.toArray(new IRuntimeClasspathEntry[resolved.size()]);
  }
}
