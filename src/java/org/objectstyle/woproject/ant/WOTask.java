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
package org.objectstyle.woproject.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Mkdir;
import org.apache.tools.ant.types.FileSet;
import org.objectstyle.woproject.logging.WOProjectLogFactory;

/**
 * A <b>WOTask</b> is a common superclass of WOApplication and WOFramework that 
 * implements common build functionality.
 * 
 * @author Emily Bache
 * @author Andrei Adamchik
 */
public abstract class WOTask extends Task {

    protected Vector classes = new Vector();
    protected String name;
    protected String destDir;
    protected String principalClass;
    protected String jarName;
    protected Vector resources = new Vector();
    protected Vector wsresources = new Vector();
    protected Vector lib = new Vector();
    protected SubtaskFactory subtaskFactory = new SubtaskFactory(this);
	public static Log log;
	
    public WOTask() {
    	super();
		// set log factory
		System.setProperty(
			"org.apache.commons.logging.LogFactory",
			"org.objectstyle.woproject.logging.WOProjectLogFactory");
		LogFactory.getFactory().setAttribute(WOProjectLogFactory.ATTR_ANT_TASK,this);
		// set own logger
		log=LogFactory.getLog(WOTask.class);	

    }
    /**
	 * Method setName.
	 * @param name
	 */
	public void setName(String name) {
        this.name = name;
    }

    /**
	 * Method getName.
	 * @return String
	 */
	public String getName() {
    	return name;
    }

    /**
	 * Method setJarName.
	 * @param jarName
	 */
	public void setJarName(String jarName) {
        this.jarName = jarName;
    }

    /**
	 * Method getJarName.
	 * @return String
	 */
	public String getJarName() {
        if(jarName == null)
            jarName = getName().toLowerCase();
        return jarName;
    }

    /**
	 * Method setPrincipalClass.
	 * @param principalClass
	 */
	public void setPrincipalClass(String principalClass) {
        this.principalClass = principalClass;
    }

    /**
	 * Method getPrincipalClass.
	 * @return String
	 */
	public String getPrincipalClass() {
    	return principalClass;
    }

    /**
	 * Method setDestDir.
	 * @param destDir
	 */
	public void setDestDir(String destDir) {
        this.destDir = destDir;
    }

    /**
	 * Method addClasses.
	 * @param set
	 */
	public void addClasses(FileSet set) {
        classes.addElement(set);
    }

    /**
	 * Method addResources.
	 * @param set
	 */
	public void addResources(FileSet set) {
        resources.addElement(set);
    }

    /**
	 * Method addLib.
	 * @param set
	 */
	public void addLib(FileSet set) {
        lib.addElement(set);
    }

    
    /**
	 * Method addWsresources.
	 * @param set
	 */
	public void addWsresources(FileSet set) {
        wsresources.addElement(set);
    }

    /**
     * Returns a location where WOTask is being built up.
     * For instance the <code>.woa</code> dir or the </code>.framework</code> dir.
     */
    protected abstract File taskDir();

    /**
     * Returns a location where resources should be put.
     * For instance this can be WOComponents, EOModels etc.
     */
    protected abstract File resourcesDir();


    /**
     * Returns a location where web server resources should be put.
     * WebServerResources are normally images, JavaScript files,
     * stylesheets, etc.
     */
    protected abstract File wsresourcesDir();

    /**
     * Ensure we have a consistent and legal set of attributes, and set any
     * internal flags necessary based on different combinations of attributes.
     *
     * @throws BuildException if task attributes are inconsistent or missing.
     */
    protected void validateAttributes() throws BuildException {
        if (name == null) {
            throw new BuildException("'name' attribute is missing.");
        }

        if (destDir == null) {
            throw new BuildException("'destDir' attribute is missing.");
        }
    }

    /**
	 * Method createDirectories.
	 * @throws BuildException
	 */
	protected void createDirectories() throws BuildException {
    	Mkdir mkdir = subtaskFactory.getMkdir();
 
        File taskDir = taskDir();

        mkdir.setDir(taskDir);
        mkdir.execute();

        File resourceDir = resourcesDir();
        mkdir.setDir(resourceDir);
        mkdir.execute();

        mkdir.setDir(new File(resourceDir, "Java"));
        mkdir.execute();

        if (hasWs()) {
            mkdir.setDir(wsresourcesDir());
            mkdir.execute();
        }
    }

    /**
	 * Method hasWs.
	 * @return boolean
	 */
	public boolean hasWs() {
        return wsresources.size() > 0;
    }

    /**
	 * Method hasResources.
	 * @return boolean
	 */
	public boolean hasResources() {
        return resources.size() > 0;
    }


    /**
	 * Method hasClasses.
	 * @return boolean
	 */
	public boolean hasClasses() {
        return classes.size() > 0;
    }

    /**
	 * Method jarClasses.
	 * @throws BuildException
	 */
	protected void jarClasses() throws BuildException {
        Jar jar = subtaskFactory.getJar();
        File taskJar =
                new File(resourcesDir(), "Java" + File.separator + getJarName() + ".jar");
        //jar.setJarfile(taskJar);
        //jar.setLocation(new Location(resourcesDir() + "Java" + File.separator + getJarName() + ".jar"));
		jar.setDestFile(taskJar);
        if (hasClasses()) {
            Enumeration en = classes.elements();
            while (en.hasMoreElements()) {
                jar.addFileset((FileSet) en.nextElement());
            }
        }

        jar.execute();
    }

    /**
	 * Method copyResources.
	 * @throws BuildException
	 */
	protected void copyResources() throws BuildException {
        Copy cp = subtaskFactory.getResourceCopy();

        cp.setTodir(resourcesDir());
        Enumeration en = resources.elements();
        while (en.hasMoreElements()) {
            cp.addFileset((FileSet) en.nextElement());
        }
        cp.execute();
    }

    /**
	 * Method copyWsresources.
	 * @throws BuildException
	 */
	protected void copyWsresources() throws BuildException {
        Copy cp = subtaskFactory.getResourceCopy();
        cp.setTodir(wsresourcesDir());

        Enumeration en = wsresources.elements();
        while (en.hasMoreElements()) {
            cp.addFileset((FileSet) en.nextElement());
        }
        cp.execute();
    }

    /**
	 * Method copyLibs.
	 * @throws BuildException
	 */
	protected void copyLibs() throws BuildException {
        Copy cp = subtaskFactory.getResourceCopy();
        cp.setTodir(new File(resourcesDir(), "Java"));

        Enumeration en = lib.elements();
        while (en.hasMoreElements()) {
            cp.addFileset((FileSet) en.nextElement());
        }
        cp.execute();
    }

    /**
	 * Method hasLib.
	 * @return boolean
	 */
	protected boolean hasLib() {
        return lib.size() > 0;
    }


    /**
	 * Method hasJava.
	 * @return boolean
	 */
	protected boolean hasJava() {
        return classes.size() > 0 || lib.size() > 0;
    }

    
    /**
        * Returns an Iterator over the file names of the library files
     * included in the lib nested element.
     */
    public Iterator getLibNames() {
        ArrayList libNames = new ArrayList();
        Enumeration en = lib.elements();
        while (en.hasMoreElements()) {
            FileSet fs = (FileSet) en.nextElement();
            DirectoryScanner scanner = fs.getDirectoryScanner(getProject());
            String[] libs = scanner.getIncludedFiles();
            for (int i = 0; i < libs.length; i++) {
                File libFile = new File(libs[i]);
                libNames.add(libFile.getName());
            }
        }
        return libNames.iterator();
    }

}