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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.objectstyle.cayenne.wocompat.PropertyListSerialization;

/**
 * A <b>XcodeProject</b> represents an Xcode project package
 * (<code>*.xcode</code>).
 * 
 * @author Jonathan 'Wolf' Rentzsch
 */
public class XcodeProject extends PBXProject {
	public void addSourceReference( String path ) {
		_sourceRefs.add( path );
	}
	
	public void save( File projectFile ) {
		ObjectsTable objectsTable = new ObjectsTable();
		
		Iterator it;
		String path;
		
		//	Walk file refs, creating a PBXFileReference for each and adding it
		//	to the objects table and group children list.
		ArrayList groupChildIDs = new ArrayList();
		it = _fileRefs.iterator();
		while( it.hasNext() ) {
			path = (String) it.next();
			groupChildIDs.add(
				objectsTable.insert(
					newFileReference(
						(new File(path)).getName(),
						path )));
		}
		//	Walk framework refs, creating a PBXFrameworkReference and
		//	PBXBuildFile for each, and adding it to the objects table and
		//	group children list.
		ArrayList buildFileIDs = new ArrayList();
		it = _frameworkRefs.iterator();
		while( it.hasNext() ) {
			path = (String) it.next();
			ObjectsTable.ID frameworkRefID = objectsTable.insert(
				newFrameworkReference((new File(path)).getName(), path ));
			groupChildIDs.add( frameworkRefID );
			
			buildFileIDs.add( objectsTable.insert( newBuildFile( frameworkRefID )));
		}
		//	Walk source file refs, creating a PBXFileReference for each and
		//	PBXBuildFile for each.
		ArrayList sourceFileIDs = new ArrayList();
		it = _sourceRefs.iterator();
		while( it.hasNext() ) {
			path = (String) it.next();
			ObjectsTable.ID fileRefID = objectsTable.insert(
				newFileReference((new File(path)).getName(), path ));
			groupChildIDs.add( fileRefID );
			
			sourceFileIDs.add( objectsTable.insert( newBuildFile( fileRefID )));
		}
		
		//	Create the PBXGroup and add it to the objects table.
		ObjectsTable.ID groupID = objectsTable.insert( newGroup( groupChildIDs ) );
		
		//	Create the PBXBuildPhase and add it to the objects table.
		ArrayList buildPhaseIDs = new ArrayList( 2 );
		buildPhaseIDs.add( objectsTable.insert( newBuildPhase(buildFileIDs)));
		buildPhaseIDs.add( objectsTable.insert( newSourcesBuildPhase(sourceFileIDs)));
		
		//	Create the PBXToolTarget and add it to the objects table and
		//	the targets list.
		ArrayList targetIDs = new ArrayList( 1 );
		targetIDs.add( objectsTable.insert( newAppServerTarget(buildPhaseIDs)));
		
		//	Create the PBXProject and add it to the objects table.
		ObjectsTable.ID projectID = objectsTable.insert( newProject( groupID, targetIDs ));
		
		//	Create the root dictionary.
		Map pbxproj = newPBXProj( objectsTable, projectID );
		
		PropertyListSerialization.propertyListToFile( projectFile, pbxproj );
	}
	
	//---------------------------------------------------------------
	//	Implementation stuff.
	
	protected ArrayList _sourceRefs = new ArrayList();

	protected static Map newSourcesBuildPhase( List buildFileIDs ) {
		return map( new Object[] {
			"isa",		"PBXSourcesBuildPhase",
			"files",	buildFileIDs });
	}
	
	protected static Map newPBXProj( Map objectsTable, ObjectsTable.ID rootObject ) {
		return map( new Object[] {
			"archiveVersion",	"1",
			"objectVersion",	"39",
			"rootObject",		rootObject,
			"objects",			objectsTable });
	}
}