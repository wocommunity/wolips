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
 
 package org.objectstyle.wolips.project;

import java.io.File;

import org.objectstyle.wolips.io.FileStringScanner;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSPropertyListSerialization;

/**
 * @author uli
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class PBProject {
	
	private File pbProjectFile;
	private NSMutableDictionary pbProject;
	
	public static String DYNAMIC_CODE_GEN = "DYNAMIC_CODE_GEN";
	public static String FILESTABLE = "FILESTABLE";
	public static String CLASSES = "CLASSES";
	public static String FRAMEWORKS = "FRAMEWORKS";
	public static String OTHER_LINKED = "OTHER_LINKED";
	public static String OTHER_SOURCES = "OTHER_SOURCES";
	public static String WOAPP_RESOURCES = "WOAPP_RESOURCES";
	public static String WOCOMPONENTS = "WOCOMPONENTS";
	public static String PROJECTNAME = "PROJECTNAME";
	public static String PROJECTTYPE = "PROJECTTYPE";
	public static String PROJECTVERSION = "PROJECTVERSION";
	public static String YES = "YES";
	public static String NO = "NO";

	
	/**
	 * Constructor for PBProject.
	 */
	public PBProject(File aFile) {
		super();
		pbProjectFile = aFile;
		this.update();
	}
	
	public void update() {
		try {
			String stringFromFile = FileStringScanner.stringFromFile(pbProjectFile);
			pbProject = new NSMutableDictionary(NSPropertyListSerialization.dictionaryForString(stringFromFile));
		}
		catch (Exception anException) {
			System.out.println("update: " + anException.getMessage());
		}
	}
	
	public void saveChanges() {
		try {
			FileStringScanner.stringToFile(pbProjectFile, pbProject.toString());
		}
		catch (Exception anException) {
			System.out.println("saveChanges: " + anException.getMessage());
		}
	}
	
	public boolean dynamicCodeGen(){
		String aString = (String)pbProject.valueForKey(PBProject.DYNAMIC_CODE_GEN);
		return aString.equals(PBProject.YES);
	}
	
	public void setDynamicCodeGen(boolean aBoolean) {
		if(aBoolean) pbProject.setObjectForKey(PBProject.YES, PBProject.DYNAMIC_CODE_GEN);
		else pbProject.setObjectForKey(PBProject.NO , PBProject.DYNAMIC_CODE_GEN);
	}

	public NSArray classes(){
		return (NSArray)pbProject.valueForKey(PBProject.CLASSES);
	}
	
	public void setClasses(NSArray anArray) {
		pbProject.setObjectForKey(anArray, PBProject.CLASSES);
	}

	public NSArray frameworks(){
		return (NSArray)pbProject.valueForKey(PBProject.FRAMEWORKS);
	}
	
	public void setFrameworks(NSArray anArray) {
		pbProject.setObjectForKey(anArray, PBProject.FRAMEWORKS);
	}

	public NSArray otherLinked(){
		return (NSArray)pbProject.valueForKey(PBProject.OTHER_LINKED);
	}
	
	public void setOtherLinked(NSArray anArray) {
		pbProject.setObjectForKey(anArray, PBProject.OTHER_LINKED);
	}

	public NSArray otherSources(){
		return (NSArray)pbProject.valueForKey(PBProject.OTHER_SOURCES);
	}
	
	public void setOtherSources(NSArray anArray) {
		pbProject.setObjectForKey(anArray, PBProject.OTHER_SOURCES);
	}


	public NSArray woAppResources(){
		return (NSArray)pbProject.valueForKey(PBProject.WOAPP_RESOURCES);
	}
	
	public void setWoAppResources(NSArray anArray) {
		pbProject.setObjectForKey(anArray, PBProject.WOAPP_RESOURCES);
	}

	public NSArray woComponents(){
		return (NSArray)pbProject.valueForKey(PBProject.WOCOMPONENTS);
	}
	
	public void setWoComponents(NSArray anArray) {
		pbProject.setObjectForKey(anArray, PBProject.WOCOMPONENTS);
	}

	public String projectName(){
		return (String)pbProject.valueForKey(PBProject.PROJECTNAME);
	}
	
	public void setProjectName(String aString) {
		pbProject.setObjectForKey(aString, PBProject.PROJECTNAME);
	}
	
	public String projectType(){
		return (String)pbProject.valueForKey(PBProject.PROJECTTYPE);
	}
	
	public void setProjectType(String aString) {
		pbProject.setObjectForKey(aString, PBProject.PROJECTTYPE);
	}

	public String projectVersion(){
		return (String)pbProject.valueForKey(PBProject.PROJECTVERSION);
	}
	
	public void setProjectVersion(String aString) {
		pbProject.setObjectForKey(aString, PBProject.PROJECTVERSION);
	}
}
