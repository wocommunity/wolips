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
package org.objectstyle.woproject.pb;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.objectstyle.cayenne.wocompat.PropertyListSerialization;

/**
 * A <b>PBXProject</b> represents a Project Builder X project package
 * (<code>*.pbproj</code>).
 * 
 * @author Jonathan 'Wolf' Rentzsch
 */
public class PBXProject {
	public void addFileReference( String path ) {
		_fileRefs.add( path );
	}
	
	public void addFrameworkReference( String path ) {
		_frameworkRefs.add( path );
	}
	
	public void save( File projectFile ) {
		save( projectFile, false );
	}
	
	public void save( File projectFile, boolean xcodeFormat ) {
		ObjectsTable objectsTable = new ObjectsTable();
		
		ArrayList groupChildIDs = new ArrayList();
		ArrayList buildFileIDs = new ArrayList();
		
		//	Walk file refs, creating a PBXFileReference for each and adding it
		//	to the objects table and group children list.
		String path;
		Iterator it = _fileRefs.iterator();
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
		it = _frameworkRefs.iterator();
		while( it.hasNext() ) {
			path = (String) it.next();
			ObjectsTable.ID frameworkRefID = objectsTable.insert(
				newFrameworkReference((new File(path)).getName(), path ));
			groupChildIDs.add( frameworkRefID );
			
			buildFileIDs.add( objectsTable.insert( newBuildFile( frameworkRefID )));
		}
		
		//	Create the PBXGroup and add it to the objects table.
		ObjectsTable.ID groupID = objectsTable.insert( newGroup( groupChildIDs ) );
		
		//	Create the PBXBuildPhase and add it to the objects table.
		ArrayList buildPhaseIDs = new ArrayList( 1 );
		buildPhaseIDs.add( objectsTable.insert( newBuildPhase( buildFileIDs)));
		
		//	Create the PBXToolTarget and add it to the objects table and
		//	the targets list.
		ArrayList targetIDs = new ArrayList( 1 );
		targetIDs.add( objectsTable.insert( newAppServerTarget(buildPhaseIDs)));
		
		//	Create the PBXProject and add it to the objects table.
		ObjectsTable.ID projectID = objectsTable.insert( newProject( groupID, targetIDs ));
		
		//	Create the root dictionary.
		Map pbxproj = xcodeFormat
			? newXcodeProj( objectsTable, projectID )
			: newPBXProj( objectsTable, projectID );
		
		PropertyListSerialization.propertyListToFile( projectFile, pbxproj );
	}
	
	//---------------------------------------------------------------
	//	Implementation stuff.
	
	protected ArrayList _fileRefs = new ArrayList();
	protected ArrayList _frameworkRefs = new ArrayList();
	
	protected static Map map( Object[] keyValues ) {
		Map result = new HashMap( keyValues.length/2 );
		for( int i = 0; i < keyValues.length; i += 2 ) {
			result.put( keyValues[i], keyValues[i+1] );
		}
		return result;
	}

	protected static Map newFileReference( String name, String path ) {
		return map( new Object[] {
			"isa",		"PBXFileReference",
			"refType",	"0",
			"name",		name,
			"path",		path });
	}

	protected static Map newFrameworkReference( String name, String path ) {
		return map( new Object[] {
			"isa",		"PBXFrameworkReference",
			"refType",	"0",
			"name",		name,
			"path",		path });
	}

	protected static Map newBuildFile( ObjectsTable.ID fileRefID ) {
		return map( new Object[] {
			"isa",		"PBXBuildFile",
			"fileRef",	fileRefID });
	}

	protected static Map newBuildPhase( List buildFileIDs ) {
		return map( new Object[] {
			"isa",		"PBXFrameworksBuildPhase",
			"files",	buildFileIDs });
	}

	protected static Map newGroup( List fileAndFrameworkReferenceIDs ) {
		return map( new Object[] {
			"isa",		"PBXGroup",
			"refType",	"4",
			"children",	fileAndFrameworkReferenceIDs });
	}

	protected static Map newAppServerTarget( List buildPhaseIDs ) {
		return map( new Object[] {
			"isa",				"PBXToolTarget",
			"buildSettings",	new HashMap(),
			"name",				"Application Server",
			"buildPhases",		buildPhaseIDs });
	}
	
	protected static Map newProject( ObjectsTable.ID groupID, List targetIDs ) {
		return map( new Object[] {
			"isa",						"PBXProject",
			"hasScannedForEncodings",	"1",
			"projectDirPath",			".",
			"mainGroup",				groupID,
			"targets",					targetIDs });
	}
	
	protected static Map newPBXProj( Map objectsTable, ObjectsTable.ID rootObject ) {
		return map( new Object[] {
			"archiveVersion",	"1",
			"objectVersion",	"38",
			"rootObject",		rootObject,
			"objects",			objectsTable });
	}
	
	protected static Map newXcodeProj( Map objectsTable, ObjectsTable.ID rootObject ) {
		return map( new Object[] {
			"archiveVersion",	"1",
			"objectVersion",	"39",
			"rootObject",		rootObject,
			"objects",			objectsTable });
	}
	
	protected static class ObjectsTable extends HashMap {
		public static class ID extends Number {
			int _value;
			protected ID( int value ) {
				_value = value;
			}
			public String toString() {
				String hexValue = Integer.toHexString( _value );
				StringBuffer result = new StringBuffer( 24 );
				for( int i = 24 - hexValue.length(); i > 0; i-- ) {
					result.append( '0' );
				}
				result.append( hexValue );
				return result.toString();
			}
			//	Bogus Number abstract class implementation requirements
			//	follow. We have to be a Number since
			//	PropertyListSerialization wouldn't otherwise stream a custom
			//	class -- it only knows Lists, Maps, Strings and Numbers.
			//	Strings are final, so that pretty much leaves us with Number,
			//	as with the container classes PropertyListSerialization
			//	would attempt to look into us, which isn't the right thing
			//	to do.
			public double doubleValue(){ return 0; }
			public float floatValue(){ return 0; }
			public int intValue(){ return 0; }
			public long longValue(){ return 0; }
			public short shortValue(){ return 0; }
		}
		
		public ObjectsTable() {
			super();
		}
		
		public ID insert( Object object ) {
			ID id = new ID( _unique++ );
			put( id, object );
			return id;
		}
		protected int _unique = 1;
	}
}