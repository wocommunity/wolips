/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2005 The ObjectStyle Group,
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
package org.objectstyle.wolips.core.resources.internal.types.project;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.core.CorePlugin;
import org.objectstyle.wolips.core.resources.internal.types.AbstractResourceAdapter;
import org.objectstyle.wolips.core.resources.types.IPBDotProjectOwner;
import org.objectstyle.wolips.core.resources.types.file.IDotWOLipsAdapter;
import org.objectstyle.wolips.core.resources.types.file.IPBDotProjectAdapter;
import org.objectstyle.wolips.core.resources.types.folder.IBuildAdapter;
import org.objectstyle.wolips.core.resources.types.project.IProjectAdapter;

public class ProjectAdapter extends AbstractResourceAdapter implements IProjectAdapter {

  private IProject underlyingProject;

  private boolean isFramework;

  public ProjectAdapter(IProject project, boolean isFramework) {
    super(project);
    this.underlyingProject = project;
    this.isFramework = isFramework;
  }

  public IProject getUnderlyingProject() {
    return this.underlyingProject;
  }

  public boolean isFramework() {
    return this.isFramework;
  }

  public IDotWOLipsAdapter getDotWOLipsAdapter() {
    IResource resource = this.getUnderlyingProject().getFile(IDotWOLipsAdapter.FILE_NAME);
    return (IDotWOLipsAdapter) resource.getAdapter(IDotWOLipsAdapter.class);
  }

  public IPBDotProjectAdapter getPBDotProjectAdapter() {
    IContainer underlyingContainer = this.getUnderlyingProject();
    IResource pbDotProjectResource = underlyingContainer.getFile(new Path(IPBDotProjectAdapter.FILE_NAME));
    IPBDotProjectAdapter pbDotProjectAdapter = (IPBDotProjectAdapter) pbDotProjectResource.getAdapter(IPBDotProjectAdapter.class);
    return pbDotProjectAdapter;
  }

  public IPBDotProjectOwner getPBDotProjectOwner(IResource resource) {
    if (resource == this.getUnderlyingProject()) {
      return this;
    }
    return super.getPBDotProjectOwner(resource);
  }

  public IPBDotProjectOwner getPBDotProjectOwner() {
    return this;
  }

  public boolean hasParentPBDotProjectAdapter() {
    return false;
  }

  public IBuildAdapter getBuildAdapter() {
    IResource resource = this.getUnderlyingProject().getFolder(IBuildAdapter.FILE_NAME);
    return (IBuildAdapter) resource.getAdapter(IBuildAdapter.class);
  }

  public List getFrameworkPaths() {
    ArrayList list = new ArrayList();
    IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
    for (int i = 0; i < projects.length; i++) {
      if (isFrameworkReference(projects[i])) {
        list.add(projects[i].getLocation());
      }
    }
    try {
      IJavaProject javaProject = JavaCore.create(this.getUnderlyingProject());
      list.addAll(toFrameworkPaths(javaProject.getResolvedClasspath(false)));
    }
    catch (JavaModelException e) {
      CorePlugin.getDefault().log(e);
    }
    return list;
  }

  public List getFrameworkNames() {
    Set frameworkNamesSet = new TreeSet();
    IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
    for (int i = 0; i < projects.length; i++) {
      if (isFrameworkReference(projects[i])) {
        frameworkNamesSet.add(projects[i].getName() + "." + "framework");
      }
    }
    try {
      IJavaProject javaProject = JavaCore.create(this.getUnderlyingProject());
      frameworkNamesSet.addAll(this.toFrameworkNames(javaProject.getResolvedClasspath(false)));
    }
    catch (JavaModelException e) {
      CorePlugin.getDefault().log(e);
    }
    return new LinkedList(frameworkNamesSet);
  }

  public String getFrameworkName(IPath frameworkPath) {
    String frameworkName = null;
    if (ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(frameworkPath) instanceof IProject) {
      frameworkName = frameworkPath.lastSegment() + ".framework";
    }
    else {
      frameworkName = frameworkPath.lastSegment();
    }
    return frameworkName;
  }

  private List toFrameworkPaths(IClasspathEntry[] classpathEntries) {
    ArrayList arrayList = new ArrayList();
    for (int i = 0; i < classpathEntries.length; i++) {
      IPath path = classpathEntries[i].getPath();
      IPath choppedFrameworkPath = null;
      int count = path.segmentCount();
      for (int pathElementNum = 0; pathElementNum < count && choppedFrameworkPath == null; pathElementNum++) {
        String segment = path.segment(pathElementNum);
        if (segment.endsWith("." + "framework")) {
          choppedFrameworkPath = path.removeLastSegments(count - pathElementNum - 1);
        }
      }
      if (choppedFrameworkPath != null && !choppedFrameworkPath.lastSegment().startsWith("JavaVM")) {
        arrayList.add(choppedFrameworkPath);
      }
    }
    return arrayList;
  }

  private List toFrameworkNames(IClasspathEntry[] classpathEntries) {
    List pathsList = toFrameworkPaths(classpathEntries);
    ArrayList namesList = new ArrayList(pathsList.size());
    Iterator pathsIter = pathsList.iterator();
    while (pathsIter.hasNext()) {
      IPath path = (IPath) pathsIter.next();
      String name = this.getFrameworkName(path);
      namesList.add(name);
    }
    return namesList;
  }

  /**
   * Method isTheLaunchAppOrFramework.
   * 
   * @param iProject
   * @return boolean
   */
  public boolean isFrameworkReference(IProject iProject) {
    boolean isFrameworkReference;
    IJavaProject javaProject = null;
    try {
      javaProject = JavaCore.create(this.getUnderlyingProject());
      if (javaProject == null) {
        isFrameworkReference = false;
      }
      else {
        IProjectAdapter project = (IProjectAdapter) iProject.getAdapter(IProjectAdapter.class);
        isFrameworkReference = project != null && project.isFramework() && projectIsReferencedByProject(iProject, javaProject.getProject());
      }
    }
    catch (Exception e) {
      CorePlugin.getDefault().log(e);
      isFrameworkReference = false;
    }
    return isFrameworkReference;
  }

  public boolean projectIsReferencedByProject(IProject child, IProject mother) {
    IProject[] projects = null;
    try {
      projects = mother.getReferencedProjects();
    }
    catch (Exception e) {
      CorePlugin.getDefault().log(e);
      return false;
    }
    for (int i = 0; i < projects.length; i++) {
      if (projects[i].equals(child))
        return true;
    }
    return false;
  }
}
