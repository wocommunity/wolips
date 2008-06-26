/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2004 The ObjectStyle Group,
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
/*Portions of this code are Copyright Apple Inc. 2008 and licensed under the
ObjectStyle Group Software License, version 1.0.  This license from Apple
applies solely to the actual code contributed by Apple and to no other code.
No other license or rights are granted by Apple, explicitly, by implication,
by estoppel, or otherwise.  All rights reserved.*/
package org.objectstyle.wolips.apple.mavenintegration.wizards;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.maven.ide.eclipse.MavenPlugin;
import org.objectstyle.wolips.wizards.NewWOProjectWizard;


/**
 * Supports adding Maven references to project
 */
public abstract class AbstractMavenProjectWizard extends NewWOProjectWizard{
	/**
	 *
	 */
	public AbstractMavenProjectWizard() {
		super();
	}

	/**
	 * @return true if settings.xml exists
	 */
	//FIXME: disabling this check for now -dlee
	public boolean hasSettingsAvailable() {
//		String msg;
//		String homeDir = System.getProperty("user.home");
//		File file = new File(homeDir+File.separator+".m2"+File.separator+"settings.xml");
//		if (!file.exists()) {
//			msg = ".m2 settings.xml doesn't exist at: "+file.toString();
//		} else {
//			msg = ".m2 settings.xml exists at: "+file.toString();
//		}
//
//		new StatusLogger().logInfo(msg);
//		return file.exists();
		return false;
	}

	/**
	 * @param version
	 * @return dependency list
	 */
	public static List<Dependency> defaultDependencies(String version) {
		ArrayList<Dependency> list = new ArrayList<Dependency>();
		String aVersion = version;
		String groupID = "com.webobjects";
		if (aVersion == null || aVersion.length() < 0) {
			aVersion = "6.0-SNAPSHOT";
		}
		list.add(createDependency("JavaXML",groupID, aVersion));
		list.add(createDependency("JavaFoundation",groupID, aVersion));
		list.add(createDependency("JavaWebObjects",groupID, aVersion));
		list.add(createDependency("JavaWOExtensions",groupID, aVersion));
		list.add(createDependency("JavaEOControl",groupID, aVersion));
		list.add(createDependency("JavaEOAccess",groupID, aVersion));
		list.add(createDependency("JavaJDBCAdaptor",groupID, aVersion));

		return list;
	}

	/**
	 * @param artifactID
	 * @param groupID
	 * @param version
	 * @return Dependency
	 */
	public static Dependency createDependency(String artifactID, String groupID, String version) {
		Dependency dep = new Dependency();
		dep.setArtifactId(artifactID);
		dep.setGroupId(groupID);
		dep.setVersion(version);

		return dep;
	}

	/**
	 * Write maven project model to project folder
	 * @param project
	 * @param model
	 */
	public void serializeModel(IProject project, Model model) {

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(project.getName()));
		if(!resource.exists() || (resource.getType() & IResource.FOLDER | IResource.PROJECT) == 0) {
			// TODO show warning popup
			new RuntimeException("Folder \"" + project.getName() + "\" does not exist.");
		}

		IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(MavenPlugin.POM_FILE_NAME));
		if( file.exists()) {
			// TODO show warning popup
			new RuntimeException( "POM already exists");
		}

		try {
			StringWriter w = new StringWriter();

			MavenEmbedder mavenEmbedder = MavenPlugin.getDefault().getMavenEmbedderManager().getWorkspaceEmbedder();
			mavenEmbedder.writeModel(w, model, true);

			file.create( new ByteArrayInputStream( w.toString().getBytes( "ASCII" ) ), true, null );
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}


	//FIXME: refactor to IOUtil class

	/**
	 * @param url
	 * @return content
	 */
	public static String readFile(URL url) {
		StringBuilder buffer = new StringBuilder();
		try {
			File file = new File(url.toURI());
			FileReader reader = new FileReader(file);
			BufferedReader is   = new BufferedReader(reader);
			String line;
			while ((line = is.readLine()) != null) {
				buffer.append(line+"\n");
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return buffer.toString();
	}

	/**
	 * Quick and dirty string compare
	 * @param source
	 * @param target
	 * @return true for string equality
	 */
	public static boolean fastFileEquals(URL source, URL target) {
		boolean result = false;
		if (source != null && target != null) {
			String sourceData = readFile(source);
			String targetData = readFile(target);
			if (sourceData.equals(targetData)) {
//				System.out.println("EQUAL!");
				result = true;
			} else {
//				System.out.println("WARNING: "+source.getPath()+" NOT EQUAL to "+target.getPath());
			}
		}
		return result;
	}

}
