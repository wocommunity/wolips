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
import java.util.Enumeration;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.*;
import org.apache.tools.ant.types.FileSet;

/**
 * Common superclass for WOApplication and WOFramework that looks
 * after common functionality.
 * 
 * @author Emily Bache, Andrei Adamchik
 */
public abstract class WOTask extends MatchingTask {

    protected Vector classes = new Vector();
    protected String name;
    protected String destDir;
    protected Vector resources = new Vector();
    protected Vector wsresources = new Vector();
    protected SubtaskFactory subtaskFactory = new SubtaskFactory(this);

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
    	return name;
    }

    public void setDestDir(String destDir) {
        this.destDir = destDir;
    }

    public void addClasses(FileSet set) {
        classes.addElement(set);
    }

    public void addResources(FileSet set) {
        resources.addElement(set);
    }


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
     * @throws BuildException Description of the Exception
     */
    protected void validateAttributes() throws BuildException {
        if (name == null) {
            throw new BuildException("'name' attribute is missing.");
        }

        if (destDir == null) {
            throw new BuildException("'destDir' attribute is missing.");
        }
    }

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

    public boolean hasWs() {
        return wsresources.size() > 0;
    }

    public boolean hasResources() {
        return resources.size() > 0;
    }


    public boolean hasClasses() {
        return classes.size() > 0;
    }

    protected void jarClasses() throws BuildException {
        Jar jar = subtaskFactory.getJar();
        File taskJar =
                new File(resourcesDir(), "Java" + File.separator + name.toLowerCase() + ".jar");
        jar.setJarfile(taskJar);

        if (hasClasses()) {
            Enumeration en = classes.elements();
            while (en.hasMoreElements()) {
                jar.addFileset((FileSet) en.nextElement());
            }
        }

        jar.execute();
    }

    protected void copyResources() throws BuildException {
        Copy cp = subtaskFactory.getResourceCopy();

        cp.setTodir(resourcesDir());
        Enumeration en = resources.elements();
        while (en.hasMoreElements()) {
            cp.addFileset((FileSet) en.nextElement());
        }
        cp.execute();
    }

    protected void copyWsresources() throws BuildException {
        Copy cp = subtaskFactory.getResourceCopy();
        cp.setTodir(wsresourcesDir());

        Enumeration en = wsresources.elements();
        while (en.hasMoreElements()) {
            cp.addFileset((FileSet) en.nextElement());
        }
        cp.execute();
    }


}