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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.objectstyle.cayenne.wocompat.PropertyListSerialization;
import org.objectstyle.woenvironment.pb.PBXProject.ObjectsTable.ID;

/**
 * A <b>PBXProject</b> represents a Project Builder X project package (<code>*.pbproj</code>).
 * 
 * @author Jonathan 'Wolf' Rentzsch
 * @author Anjo Krank
 */
@SuppressWarnings("unchecked")
public class PBXProject {
	public void addSourceReference(String path) {
		if (!_sourceRefs.contains(path))
			_sourceRefs.add(path);
	}

  public void removeSourceReference(String path) {
      _sourceRefs.remove(path);
  }

	public void addResourceFileReference(String path) {
		if (!_resourceFileRefs.contains(path))
			_resourceFileRefs.add(path);
	}

  public void removeResourceFileReference(String path) {
      _resourceFileRefs.remove(path);
  }

	public void addResourceFolderReference(String path) {
		if (!_resourceFolderRefs.contains(path))
			_resourceFolderRefs.add(path);
	}

  public void removeResourceFolderReference(String path) {
      _resourceFolderRefs.remove(path);
  }

	public void addWSResourceFileReference(String path) {
		if (!_wsresourceFileRefs.contains(path))
			_wsresourceFileRefs.add(path);
	}

  public void removeWSResourceFileReference(String path) {
      _wsresourceFileRefs.remove(path);
  }

	public void addWSResourceFolderReference(String path) {
		if (!_wsresourceFolderRefs.contains(path))
			_wsresourceFolderRefs.add(path);
	}

  public void removeWSResourceFolderReference(String path) {
      _wsresourceFolderRefs.remove(path);
  }

	public void addFrameworkReference(String path) {
		if (!_frameworkRefs.contains(path))
			_frameworkRefs.add(path);
	}

  public void removeFrameworkReference(String path) {
      _frameworkRefs.remove(path);
  }

	public void save(File projectFile) {
		ObjectsTable objectsTable = new ObjectsTable();
		String projectPath;
		try {
			projectPath = projectFile.getCanonicalPath();
		} catch (IOException ex) {
			throw new RuntimeException("Can't get path of project file: " + projectFile);
		}
		int last = projectPath.lastIndexOf(File.separator);
		if (last != -1) {
			projectPath = projectPath.substring(0, last);
		}
		last = projectPath.lastIndexOf(File.separator);
		if (last != -1) {
			projectPath = projectPath.substring(0, last);
		}

		ArrayList sourceFileIDs = new ArrayList();
		ArrayList resourceFileIDs = new ArrayList();
		ArrayList wsresourceFileIDs = new ArrayList();
		ArrayList frameworkFileIDs = new ArrayList();

		// Walk refs, creating the appropriate reference type and an associated
		// build file for each and adding it to the objects table and group
		// children list.
		String path;
		Iterator it;

		it = _sourceRefs.iterator();
		while (it.hasNext()) {
			path = (String) it.next();
			if (path.indexOf(projectPath) == 0) {
				path = path.substring(projectPath.length() + 1);
			}
			File file = new File(path);
			Map reference = newFileReference(file.getName(), path);
			ObjectsTable.ID refID = objectsTable.insert(reference);
			sourceFileIDs.add(refID);
		}

		it = _resourceFileRefs.iterator();
		while (it.hasNext()) {
			path = (String) it.next();
			if (path.indexOf(projectPath) == 0) {
				path = path.substring(projectPath.length() + 1);
			}
			File file = new File(path);
			if (!_resourceFolderRefs.contains(projectPath + "/" + file.getParent())) {
				Map reference = newFileReference(file.getName(), path);
				ObjectsTable.ID refID = objectsTable.insert(reference);
				resourceFileIDs.add(refID);
			}
		}

		it = _resourceFolderRefs.iterator();
		while (it.hasNext()) {
			path = (String) it.next();
			if (path.indexOf(projectPath) == 0) {
				path = path.substring(projectPath.length() + 1);
			}
			File file = new File(path);
			Map reference = newFolderReference(file.getName(), path);
			ObjectsTable.ID refID = objectsTable.insert(reference);
			resourceFileIDs.add(refID);
		}

		it = _wsresourceFileRefs.iterator();
		while (it.hasNext()) {
			path = (String) it.next();
			if (path.indexOf(projectPath) == 0) {
				path = path.substring(projectPath.length() + 1);
			}
			File file = new File(path);
			if (!_wsresourceFolderRefs.contains(projectPath + "/" + file.getParent())) {
				Map reference = newFileReference(file.getName(), path);
				ObjectsTable.ID refID = objectsTable.insert(reference);
				wsresourceFileIDs.add(refID);
			}
		}

		it = _wsresourceFolderRefs.iterator();
		while (it.hasNext()) {
			path = (String) it.next();
			if (path.indexOf(projectPath) == 0) {
				path = path.substring(projectPath.length() + 1);
			}
			File file = new File(path);
			Map reference = newFolderReference(file.getName(), path);
			ObjectsTable.ID refID = objectsTable.insert(reference);
			wsresourceFileIDs.add(refID);
		}

		it = _frameworkRefs.iterator();
		while (it.hasNext()) {
			path = (String) it.next();
			Map reference = newFrameworkReference((new File(path)).getName(), path);
			ObjectsTable.ID refID = objectsTable.insert(reference);
			frameworkFileIDs.add(refID);
		}

		ArrayList childrenIDs = new ArrayList();
		childrenIDs.add(objectsTable.insert(newGroup("Sources", sourceFileIDs)));
		childrenIDs.add(objectsTable.insert(newGroup("Resources", resourceFileIDs)));
		childrenIDs.add(objectsTable.insert(newGroup("Web Server", wsresourceFileIDs)));
		childrenIDs.add(objectsTable.insert(newGroup("Frameworks", frameworkFileIDs)));

		// Create the PBXGroup and add it to the objects table.
		ObjectsTable.ID mainGroupID = objectsTable.insert(newGroup("Root", childrenIDs));

		// Create the PBXToolTarget and add it to the objects table and
		// the targets list.
		ArrayList targetIDs = new ArrayList(2);
		List phases;

		phases = newBuildPhaseList(sourceFileIDs, resourceFileIDs, wsresourceFileIDs, frameworkFileIDs, objectsTable);
		Map appServerTarget = newAppServerTarget(phases, objectsTable);

		phases = newBuildPhaseList(Collections.EMPTY_LIST, Collections.EMPTY_LIST, Collections.EMPTY_LIST, Collections.EMPTY_LIST, objectsTable);
		Map antTarget = newAntTarget(phases, objectsTable);

		phases = newBuildPhaseList(Collections.EMPTY_LIST, Collections.EMPTY_LIST, wsresourceFileIDs, Collections.EMPTY_LIST, objectsTable);
		Map webServerTarget = newWebServerTarget(phases, objectsTable);

		targetIDs.add(objectsTable.insert(appServerTarget));
		targetIDs.add(objectsTable.insert(antTarget));
		targetIDs.add(objectsTable.insert(webServerTarget));

		// Create the PBXProject and add it to the objects table.
		ObjectsTable.ID projectID = objectsTable.insert(newProject(mainGroupID, targetIDs, objectsTable));

		// Create the root dictionary.
		Map pbxproj = newPBXProj(objectsTable, projectID);

		PropertyListSerialization.propertyListToFile(projectFile, pbxproj);
	}

	protected List newBuildFileList(List fileIDs, ObjectsTable objectsTable) {
		List result = new ArrayList(fileIDs.size());
		for (Iterator iter = fileIDs.iterator(); iter.hasNext();) {
			ObjectsTable.ID refID = (ObjectsTable.ID) iter.next();
			result.add(objectsTable.insert(newBuildFile(refID)));
		}
		return result;
	}

	protected List newBuildPhaseList(List sourceFileIDs, List resourceFileIDs, List wsresourceFileIDs, List frameworkFileIDs, ObjectsTable objectsTable) {
		ArrayList buildPhaseIDs = new ArrayList(4);
		if (hasBuildPhases()) {
			buildPhaseIDs.add(objectsTable.insert(newSourcesBuildPhase(newBuildFileList(sourceFileIDs, objectsTable))));
			buildPhaseIDs.add(objectsTable.insert(newResourcesBuildPhase(newBuildFileList(resourceFileIDs, objectsTable))));
			buildPhaseIDs.add(objectsTable.insert(newWSResourcesBuildPhase(newBuildFileList(wsresourceFileIDs, objectsTable))));
			buildPhaseIDs.add(objectsTable.insert(newFrameworkBuildPhase(newBuildFileList(frameworkFileIDs, objectsTable))));
		}
		return buildPhaseIDs;
	}

	// ---------------------------------------------------------------
	// Implementation stuff.

	protected ArrayList _resourceFolderRefs = new ArrayList();

	protected ArrayList _resourceFileRefs = new ArrayList();

	protected ArrayList _wsresourceFolderRefs = new ArrayList();

	protected ArrayList _wsresourceFileRefs = new ArrayList();

	protected ArrayList _frameworkRefs = new ArrayList();

	protected ArrayList _sourceRefs = new ArrayList();

  protected Map map(Object[] keyValues) {
		Map result = new TreeMap();
		for (int i = 0; i < keyValues.length; i += 2) {
			result.put(keyValues[i], keyValues[i + 1]);
		}
		return result;
	}

	protected Map newFileReference(String name, String path) {
		return map(new Object[] { "isa", "PBXFileReference", "refType", (new File(path)).isAbsolute() ? "0" : "2", "name", name, "path", path });
	}

	protected Map newFolderReference(String name, String path) {
		return map(new Object[] { "isa", "PBXFolderReference", "refType", (new File(path)).isAbsolute() ? "0" : "2", "name", name, "path", path });
	}

	protected Map newFrameworkReference(String name, String path) {
		return map(new Object[] { "isa", "PBXFrameworkReference", "refType", "0", "name", name, "path", path });
	}

	protected Map newBuildFile(ObjectsTable.ID fileRefID) {
		return map(new Object[] { "isa", "PBXBuildFile", "fileRef", fileRefID });
	}

	protected Map newFrameworkBuildPhase(List buildFileIDs) {
		return map(new Object[] { "isa", "PBXFrameworksBuildPhase", "files", buildFileIDs });
	}

	protected Map newResourcesBuildPhase(List buildFileIDs) {
		return map(new Object[] { "isa", "PBXResourcesBuildPhase", "files", buildFileIDs });
	}

	protected Map newWSResourcesBuildPhase(List buildFileIDs) {
		return map(new Object[] { "isa", "PBXResourcesBuildPhase", "files", buildFileIDs });
	}

	protected Map newSourcesBuildPhase(List buildFileIDs) {
		return map(new Object[] { "isa", "PBXSourcesBuildPhase", "files", buildFileIDs });
	}

	protected Map newGroup(String name, List childrenIDs) {
		return map(new Object[] { "isa", "PBXGroup", "refType", "4", "name", name, "children", childrenIDs });
	}

	protected Map newAppServerTarget(List buildPhaseIDs, ObjectsTable objectsTable) {
		return map(new Object[] { "isa", "PBXToolTarget", "buildSettings", new HashMap(), "name", "Application Server", "buildPhases", buildPhaseIDs });
	}

	protected Map newWebServerTarget(List buildPhaseIDs, ObjectsTable objectsTable) {
		return map(new Object[] { "isa", "PBXToolTarget", "buildSettings", new HashMap(), "name", "Web Server", "buildPhases", buildPhaseIDs });
	}

  protected Map newAntTarget(List buildPhaseIDs, ObjectsTable objectsTable) {
		Map result = map(new Object[] { "isa", "PBXLegacyTarget", "buildArgumentsString", "-emacs $(ACTION)", "buildSettings", new HashMap(), "buildToolPath", "/Developer/Java/Ant/bin/ant", "passBuildSettingsInEnvironment", "1", "name", "Ant", "buildPhases", buildPhaseIDs });
		List<ID> buildConfigurations = new LinkedList<ID>();
		buildConfigurations.add(objectsTable.insert(newBuildConfiguration(map(new Object[] { "COPY_PHASE_STRIP", "NO" }), "Debug")));
		buildConfigurations.add(objectsTable.insert(newBuildConfiguration(map(new Object[] { "COPY_PHASE_STRIP", "YES" }), "Release")));
		buildConfigurations.add(objectsTable.insert(newBuildConfiguration(new HashMap(), "Default")));
		result.put("buildConfigurationList", objectsTable.insert(newBuildConfigurationList(buildConfigurations, false, "Default")));
		result.put("productName", "Ant");
		return result;
	}

	protected Map newBuildConfigurationList(List _buildConfigurations, boolean _defaultConfigurationIsVisible, String _defaultConfigurationName) {
		Map buildConfigurationList = map(new Object[] { "buildConfigurations", _buildConfigurations, "defaultConfigurationIsVisible", (_defaultConfigurationIsVisible) ? "1" : "0", "defaultConfigurationName", _defaultConfigurationName, "isa", "XCConfigurationList" });
		return buildConfigurationList;
	}

	protected Map newBuildStyle(Map _buildSettings, String _name) {
		return newBuildStyleOrConfiguration(_buildSettings, "PBXBuildStyle", _name);
	}

	// AK: this might be only for XCode and up
	protected Map newBuildConfiguration(Map _buildSettings, String _name) {
		return newBuildStyleOrConfiguration(_buildSettings, "XCBuildConfiguration", _name);
	}

	protected Map newBuildStyleOrConfiguration(Map _buildSettings, String _isa, String _name) {
		return map(new Object[] { "buildSettings", _buildSettings, "isa", _isa, "name", _name });
	}

	protected Map newProject(ObjectsTable.ID groupID, List targetIDs, ObjectsTable objectsTable) {
		return map(new Object[] { "isa", "PBXProject", "hasScannedForEncodings", "1", "projectDirPath", ".", "mainGroup", groupID, "targets", targetIDs });
	}

	protected Map newPBXProj(Map objectsTable, ObjectsTable.ID rootObject) {
		return map(new Object[] { "archiveVersion", "1", "objectVersion", "38", "rootObject", rootObject, "objects", objectsTable });
	}

	protected boolean hasBuildPhases() {
		return true;
	}

	protected static class ObjectsTable extends TreeMap {
		public static class ID extends Number {
			int _value;

			protected ID(int value) {
				_value = value;
			}

			@Override
      public String toString() {
				String hexValue = Integer.toHexString(_value);
				StringBuffer result = new StringBuffer(24);
				for (int i = 24 - hexValue.length(); i > 0; i--) {
					result.append('0');
				}
				result.append(hexValue);
				return result.toString();
			}

			// Bogus Number abstract class implementation requirements
			// follow. We have to be a Number since
			// PropertyListSerialization wouldn't otherwise stream a custom
			// class -- it only knows Lists, Maps, Strings and Numbers.
			// Strings are final, so that pretty much leaves us with Number,
			// as with the container classes PropertyListSerialization
			// would attempt to look into us, which isn't the right thing
			// to do.
			@Override
      public double doubleValue() {
				return 0;
			}

			@Override
      public float floatValue() {
				return 0;
			}

			@Override
      public int intValue() {
				return 0;
			}

			@Override
      public long longValue() {
				return 0;
			}

			@Override
      public short shortValue() {
				return 0;
			}
		}

    public ObjectsTable() {
			super(new Comparator() {
				public int compare(Object a, Object b) {
					if (a == b)
						return 0;
					if (a == null)
						return -1;
					if (b == null)
						return 1;
					return a.toString().compareTo(b.toString());
				}
			});
		}

    public ID insert(Object object) {
			ID id = new ID(_unique++);
			put(id, object);
			return id;
		}

		protected int _unique = 1;
	}
}