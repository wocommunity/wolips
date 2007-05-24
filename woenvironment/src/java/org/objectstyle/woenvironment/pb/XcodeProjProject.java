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
 * A <b>XcodeProjProject</b> represents an Xcode 2.1 project package (<code>*.xcodeproj</code>).
 * 
 * @author Mike Schrag
 */
@SuppressWarnings("unchecked")
public class XcodeProjProject extends PBXProject {
	@Override
  protected Map newFrameworkReference(String name, String path) {
		return map(new Object[] { "isa", "PBXFileReference", "lastKnownFileType", "wrapper.framework", "sourceTree", "<absolute>", "name", name, "path", path });
	}

	@Override
  protected Map newGroup(String name, List childrenIDs) {
		return map(new Object[] { "isa", "PBXGroup", "sourceTree", "<group>", "name", name, "children", childrenIDs });
	}

	@Override
  protected Map newFileReference(String name, String path) {
		return map(new Object[] { "isa", "PBXFileReference", "lastKnownFileType", "sourcecode.java", "sourceTree", new File(path).isAbsolute() ? "<absolute>" : "<group>", "name", name, "path", path });
	}

	@Override
  protected Map newFolderReference(String name, String path) {
		String lastKnownFileType;
		if (path.endsWith(".eomodeld")) {
			lastKnownFileType = "wrapper.eomodeld";
		} else if (path.endsWith(".wo")) {
			lastKnownFileType = "folder";
		} else if (path.endsWith(".nib")) {
			lastKnownFileType = "wrapper.nib";
		} else {
			lastKnownFileType = "folder";
		}
		Map result = map(new Object[] { "isa", "PBXFileReference", "lastKnownFileType", lastKnownFileType, "sourceTree", new File(path).isAbsolute() ? "<absolute>" : "<group>", "name", name, "path", path });
		return result;
	}

	@Override
  protected Map newAntTarget(List _buildPhaseIDs, ObjectsTable _objectsTable) {
		Map result = map(new Object[] { "isa", "PBXLegacyTarget", "buildArgumentsString", "-emacs $(ACTION)", "buildSettings", new HashMap(), "buildToolPath", "/Developer/Java/Ant/bin/ant", "passBuildSettingsInEnvironment", "1", "name", "Ant", "buildPhases", _buildPhaseIDs });
		List buildConfigurations = new LinkedList();
		buildConfigurations.add(_objectsTable.insert(newBuildConfiguration(map(new Object[] { "COPY_PHASE_STRIP", "NO" }), "Debug")));
		buildConfigurations.add(_objectsTable.insert(newBuildConfiguration(map(new Object[] { "COPY_PHASE_STRIP", "YES" }), "Release")));
		buildConfigurations.add(_objectsTable.insert(newBuildConfiguration(new HashMap(), "Default")));
		result.put("buildConfigurationList", _objectsTable.insert(newBuildConfigurationList(buildConfigurations, false, "Default")));
		result.put("productName", "Ant");
		return result;
	}

	@Override
  protected Map newProject(ID _groupID, List _targetIDs, ObjectsTable _objectsTable) {
		Map project = super.newProject(_groupID, _targetIDs, _objectsTable);

		List buildConfigurations = new LinkedList();
		buildConfigurations.add(_objectsTable.insert(newBuildConfiguration(new HashMap(), "Debug")));
		buildConfigurations.add(_objectsTable.insert(newBuildConfiguration(new HashMap(), "Release")));
		buildConfigurations.add(_objectsTable.insert(newBuildConfiguration(new HashMap(), "Default")));
		project.put("buildConfigurationList", _objectsTable.insert(newBuildConfigurationList(buildConfigurations, false, "Default")));

		project.put("buildSettings", new HashMap());

		List buildStyles = new LinkedList();
		buildStyles.add(_objectsTable.insert(newBuildStyle(map(new Object[] { "COPY_PHASE_STRIP", "NO" }), "Debug")));
		buildStyles.add(_objectsTable.insert(newBuildStyle(map(new Object[] { "COPY_PHASE_STRIP", "YES" }), "Release")));
		project.put("buildStyles", buildStyles);

		return project;
	}

	@Override
  protected Map newBuildConfigurationList(List _buildConfigurations, boolean _defaultConfigurationIsVisible, String _defaultConfigurationName) {
		Map buildConfigurationList = map(new Object[] { "buildConfigurations", _buildConfigurations, "defaultConfigurationIsVisible", (_defaultConfigurationIsVisible) ? "1" : "0", "defaultConfigurationName", _defaultConfigurationName, "isa", "XCConfigurationList" });
		return buildConfigurationList;
	}

	@Override
  protected Map newPBXProj(Map objectsTable, ObjectsTable.ID rootObject) {
		return map(new Object[] { "archiveVersion", "1", "classes", new HashMap(), "objectVersion", "42", "rootObject", rootObject, "objects", objectsTable });
	}

	@Override
  protected boolean hasBuildPhases() {
		return true;
	}

}