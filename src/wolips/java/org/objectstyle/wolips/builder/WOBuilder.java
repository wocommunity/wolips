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
 package org.objectstyle.wolips.builder;

import java.util.Map;
import java.util.Vector;

import org.eclipse.ant.core.AntRunner;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.objectstyle.wolips.WOLipsPlugin;

/**
 * @author uli
 */
public abstract class WOBuilder extends IncrementalProjectBuilder {
	
	private static AntRunner antRunner = null;
	private static Vector marker = new Vector();
	
//	public static String WOLIPS_NEXT_ROOT = "wolips.next.root";
	/**
	 * Constructor for WOBuilder.
	 */
	
	public  WOBuilder() {
		super();
	}
	
	private AntRunner antRunner() {
		if(WOBuilder.antRunner == null) WOBuilder.antRunner = new AntRunner();
		return WOBuilder.antRunner;
	}
		
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
		throws CoreException {									
		if (getProject() == null || !getProject().exists()) 
			return new IProject[0];
		Exception anException = null;
		try {
			getProject().deleteMarkers(IMarker.TASK, false, getProject().DEPTH_ONE);
			String aBuildFile = this.buildFile();
			if(checkIfBuildfileExist(aBuildFile)) {
				getProject().getFile(aBuildFile).deleteMarkers(IMarker.TASK, false, getProject().DEPTH_ONE);
				//WOLipsBuild.initWithProject(getProject());
				//AntRunner anAntRunner = new AntRunner();
				antRunner().setBuildFileLocation(getProject().getFile(aBuildFile).getLocation().toOSString());
				//Hashtable aHashtable = this.properties();
				//anAntRunner.addUserProperties(aHashtable);
				antRunner().addUserProperties(args);
				antRunner().run(monitor);
				//getProject().refreshLocal(getProject().DEPTH_INFINITE, monitor);
			}
		} 
		catch(Exception e) {
				anException = e;
		}
		if(anException != null) {
			try {
				IMarker aMarker = getProject().getFile(this.buildFile()).createMarker(IMarker.TASK);
				aMarker.setAttribute(IMarker.MESSAGE, "WOLips: " + anException.getMessage() + " Please visit the Eclipse log for mor details.");
				aMarker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
			}
			catch(Exception e) {
			WOLipsPlugin.log(e);
			}
		}
		
		return new IProject[0];
	}


	private boolean checkIfBuildfileExist(String aBuildFile) {
		try {
			if(getProject().getFile(aBuildFile).exists()) return true;
		}
		catch (Exception anException) {
		}
		try {
			IMarker aMarker = getProject().createMarker(IMarker.TASK);
			aMarker.setAttribute(IMarker.MESSAGE, "WOLips: Can not find: " + this.buildFile());
			aMarker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
		}
		catch (Exception anException) {
			WOLipsPlugin.log(anException);
		}
		return false;
	}
	
	/*	
	public Hashtable properties() {
		Hashtable aHashtable = new Hashtable();
		aHashtable.put(WOBuilder.WOLIPS_NEXT_ROOT, WOVariables.nextRoot());
		return aHashtable;
	}
	*/
	public abstract String buildFile();
		
}
