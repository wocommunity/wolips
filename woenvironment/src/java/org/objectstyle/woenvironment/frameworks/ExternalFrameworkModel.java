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
package org.objectstyle.woenvironment.frameworks;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.objectstyle.woenvironment.env.WOEnvironment;
import org.objectstyle.woenvironment.env.WOVariables;

public class ExternalFrameworkModel extends FrameworkModel<IFramework> {
  private WOEnvironment environment;

  public ExternalFrameworkModel(Map<Object, Object> existingProperties) {
    this.environment = new WOEnvironment(existingProperties);
  }

  private File fixMissingSeparatorAfterDevice(String string) {
    // if (string != null && string.length() > 1 && string.charAt(1) == ':')
    // {
    // return new File(string.substring(2)).setDevice(string.substring(0,
    // 2));
    // }
    File file;
    if (string == null) {
      file = null;
    }
    else {
      file = new File(string);
    }
    return file;
  }

  public ExternalFolderRoot getRootForFolder(File frameworksFolder) throws IOException {
    File canonicalFolder = frameworksFolder.getCanonicalFile();
    for (Root<?> root : getRoots()) {
      if (root instanceof ExternalFolderRoot) {
        File baseFolder = ((ExternalFolderRoot) root).getFrameworksFolder();
        if (baseFolder != null && baseFolder.getCanonicalFile().equals(canonicalFolder)) {
          return (ExternalFolderRoot) root;
        }
      }
    }
    return null;
  }

  @Override
  protected synchronized List<Root<IFramework>> createRoots() {
    List<Root<IFramework>> roots = new LinkedList<Root<IFramework>>();
    //roots.add(new EclipseProjectRoot(Root.PROJECT_ROOT, "Project Frameworks", ResourcesPlugin.getWorkspace().getRoot()));

    WOVariables variables = this.environment.getWOVariables();
    String projectLocalFrameworksFolder = variables.getProperty("projectFrameworkFolder");
    if (projectLocalFrameworksFolder != null) {
      File projectLocalRoot = new File(projectLocalFrameworksFolder);
      roots.add(new ExternalFolderRoot(Root.PROJECT_LOCAL_ROOT, "Project Local Frameworks", projectLocalRoot, projectLocalRoot));
    }
    else {
      roots.add(new ExternalFolderRoot(Root.PROJECT_LOCAL_ROOT, "Project Local Frameworks", null, null));
    }

    File externalBuildRootPath = fixMissingSeparatorAfterDevice(variables.externalBuildRoot());
    File externalBuildFrameworkPath = fixMissingSeparatorAfterDevice(variables.externalBuildFrameworkPath());
    roots.add(new ExternalFolderRoot(Root.EXTERNAL_ROOT, "External Build Root", externalBuildRootPath, externalBuildFrameworkPath));

    File userRoot = fixMissingSeparatorAfterDevice(variables.userRoot());
    File userFrameworksPath = fixMissingSeparatorAfterDevice(variables.userFrameworkPath());
    roots.add(new ExternalFolderRoot(Root.USER_ROOT, "User Frameworks", userRoot, userFrameworksPath));

    File localRoot = fixMissingSeparatorAfterDevice(variables.localRoot());
    File localFrameworksPath = fixMissingSeparatorAfterDevice(variables.localFrameworkPath());
    roots.add(new ExternalFolderRoot(Root.LOCAL_ROOT, "Local Frameworks", localRoot, localFrameworksPath));

    File systemRoot = fixMissingSeparatorAfterDevice(variables.systemRoot());
    File systemFrameworksPath = fixMissingSeparatorAfterDevice(variables.systemFrameworkPath());
    roots.add(new ExternalFolderRoot(Root.SYSTEM_ROOT, "System Frameworks", systemRoot, systemFrameworksPath));

    File networkRoot = fixMissingSeparatorAfterDevice(variables.networkRoot());
    File networkFrameworksPath = fixMissingSeparatorAfterDevice(variables.networkFrameworkPath());
    roots.add(new ExternalFolderRoot(Root.NETWORK_ROOT, "Network Frameworks", networkRoot, networkFrameworksPath));
    return roots;
  }
}