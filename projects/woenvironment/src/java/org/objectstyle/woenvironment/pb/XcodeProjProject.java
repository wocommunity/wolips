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
package org.objectstyle.woenvironment.pb;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.objectstyle.woenvironment.pb.PBXProject.ObjectsTable.ID;

/**
 * A <b>XcodeProjProject</b> represents an Xcode 2.1 project package
 * (<code>*.xcodeproj</code>).
 * 
 * @author Mike Schrag
 */
public class XcodeProjProject extends PBXProject {
  protected Map newFrameworkReference(String _name, String _path) {
    return map(new Object[] { "isa", "PBXFileReference", "lastKnownFileType", "wrapper.framework", "name", _name, "path", _path, "sourceTree", "<absolute>" });
  }

  protected Map newGroup(String _name, List _childrenIDs) {
    return map(new Object[] { "isa", "PBXGroup", "refType", "<group>", "name", _name, "children", _childrenIDs });
  }

  protected Map newFileReference(String _name, String _path) {
    return map(new Object[] { "isa", "PBXFileReference", "lastKnownFileType", "sourcecode.java", "sourceTree", new File(_path).isAbsolute() ? "<absolute>" : "SOURCE_ROOT", "name", _name, "path", _path });
  }

  protected Map newFolderReference(String _name, String _path) {
    //    if (_path.endsWith(".wo") || _path.endsWith(".eomodeld") || _path.endsWith(".nib")) {
    //    }
    Map result = map(new Object[] { "isa", "PBXFileReference", "includeInIndex", "0", "lastKnownFileType", "folder", "path", _path, "sourceTree", new File(_path).isAbsolute() ? "<absolute>" : "<group>" });
    // sourceTree = "SOURCE_ROOT";  ?
    return result;
  }

  protected Map newAppServerTarget(List _buildPhaseIDs) {
    Map result = super.newAppServerTarget(_buildPhaseIDs);
    List buildConfigurations = new LinkedList();
    buildConfigurations.add(newBuildConfiguration(map(new Object[] { "COPY_PHASE_STRIP", "NO" }), "Development"));
    buildConfigurations.add(newBuildConfiguration(map(new Object[] { "COPY_PHASE_STRIP", "YES" }), "Deployment"));
    buildConfigurations.add(newBuildConfiguration(new HashMap(), "Default"));
    result.put("buildConfigurationList", newBuildConfigurationList(buildConfigurations, false, "Default"));
    result.put("productName", "Application Server"); 
    return result;
  }

  protected Map newSourcesBuildPhase(List _buildFileIDs) {
    return map(new Object[] { "isa", "PBXSourcesBuildPhase", "files", _buildFileIDs, "buildActionMask", "2147483647", "runOnlyForDeploymentPostprocessing", "0" });
  }

  protected Map newResourcesBuildPhase(List _buildFileIDs) {
    return map(new Object[] { "isa", "PBXResourcesBuildPhase", "files", _buildFileIDs, "buildActionMask", "2147483647", "runOnlyForDeploymentPostprocessing", "0" });
  }

  protected Map newFrameworkBuildPhase(List _buildFileIDs) {
    return map(new Object[] { "isa", "PBXFrameworksBuildPhase", "files", _buildFileIDs, "buildActionMask", "2147483647", "runOnlyForDeploymentPostprocessing", "0" });
  }

  protected Map newProject(ID _groupID, List _targetIDs) {
    Map project = super.newProject(_groupID, _targetIDs);

    List buildConfigurations = new LinkedList();
    buildConfigurations.add(newBuildConfiguration(new HashMap(), "Development"));
    buildConfigurations.add(newBuildConfiguration(new HashMap(), "Deployment"));
    buildConfigurations.add(newBuildConfiguration(new HashMap(), "Default"));
    project.put("buildConfigurationList", newBuildConfigurationList(buildConfigurations, false, "Default"));

    project.put("buildSettings", new HashMap());

    List buildStyles = new LinkedList();
    buildStyles.add(newBuildStyle(map(new Object[] { "COPY_PHASE_STRIP", "NO" }), "Development"));
    buildStyles.add(newBuildStyle(map(new Object[] { "COPY_PHASE_STRIP", "YES" }), "Deployment"));
    project.put("buildStyles", buildStyles);

    return project;
  }

  protected Map newBuildConfigurationList(List _buildConfigurations, boolean _defaultConfigurationIsVisible, String _defaultConfigurationName) {
    Map buildConfigurationList = map(new Object[] { "buildConfigurations", _buildConfigurations, "defaultConfigurationIsVisible", (_defaultConfigurationIsVisible) ? "1" : "0", "defaultConfigurationName", _defaultConfigurationName, "isa", "XCConfigurationList" });
    return buildConfigurationList;
  }

  protected Map newBuildStyle(Map _buildSettings, String _name) {
    return newBuildStyleOrConfiguration(_buildSettings, "PBXBuildStyle", _name);
  }

  protected Map newBuildConfiguration(Map _buildSettings, String _name) {
    return newBuildStyleOrConfiguration(_buildSettings, "XCBuildConfiguration", _name);
  }

  protected Map newBuildStyleOrConfiguration(Map _buildSettings, String _isa, String _name) {
    return map(new Object[] { "buildSettings", _buildSettings, "isa", _isa, "name", _name });
  }

  protected Map newPBXProj(Map objectsTable, ObjectsTable.ID rootObject) {
    return map(new Object[] { "archiveVersion", "1", "classes", new HashMap(), "objectVersion", "42", "rootObject", rootObject, "objects", objectsTable });
  }
}