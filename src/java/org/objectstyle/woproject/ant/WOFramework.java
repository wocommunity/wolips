package org.objectstyle.woproject.ant;
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

import java.io.*;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.*;
import org.apache.tools.ant.types.FileSet;

/**
  * Ant task to build WebObjects framework.  
  * 
  * @ant.task category="packaging"
  */
public class WOFramework extends MatchingTask {
    private String name;
    private String destDir;
    private Vector classes = new Vector();
    private Vector lib = new Vector();
    private Vector resources = new Vector();
    private Vector wsresources = new Vector();

    public void setName(String name) {
        this.name = name;
    }

    public void setDestDir(String destDir) {
        this.destDir = destDir;
    }

    public void addClasses(FileSet set) {
        classes.addElement(set);
    }

    public void addLib(FileSet set) {
        lib.addElement(set);
    }

    public void addResources(FileSet set) {
        resources.addElement(set);
    }

    public void addWsresources(FileSet set) {
        wsresources.addElement(set);
    }

    public void execute() throws BuildException {
        validateAttributes();
        createDirectories();
        jarClasses();

        if (hasResources()) {
            copyResources();
        }

        if (hasWs()) {
            copyWsresources();
        }

        buildInfo();
    }

    /**
     * Ensure we have a consistent and legal set of attributes, and set
     * any internal flags necessary based on different combinations
     * of attributes.
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
        Mkdir mkdir = new Mkdir();
        initChildTask(mkdir);

        File frameworkDir = frameworkDir();
        File resourceDir = new File(frameworkDir, "Resources");

        mkdir.setDir(frameworkDir);
        mkdir.execute();

        mkdir.setDir(resourceDir);
        mkdir.execute();

        mkdir.setDir(new File(resourceDir, "Java"));
        mkdir.execute();

        if (hasWs()) {
            mkdir.setDir(new File(frameworkDir, "WebServerResources"));
            mkdir.execute();
        }
    }

    protected void jarClasses() throws BuildException {
        Jar jar = new Jar();
        initChildTask(jar);

        File frameworkJar =
            new File(resourcesDir(), "Java" + File.separator + name.toLowerCase() + ".jar");
        jar.setJarfile(frameworkJar);

        if (hasClasses()) {
            Enumeration en = classes.elements();
            while (en.hasMoreElements()) {
                jar.addFileset((FileSet) en.nextElement());
            }
        }

        jar.execute();
    }

    protected void copyResources() throws BuildException {
        WOCompCopy cp = new WOCompCopy();
        initChildTask(cp);

        cp.setTodir(resourcesDir());
        Enumeration en = resources.elements();
        while (en.hasMoreElements()) {
            cp.addFileset((FileSet) en.nextElement());
        }
        cp.execute();
    }

    protected void copyWsresources() throws BuildException {
        Copy cp = new Copy();
        initChildTask(cp);

        cp.setTodir(wsresourcesDir());

        Enumeration en = wsresources.elements();
        while (en.hasMoreElements()) {
            cp.addFileset((FileSet) en.nextElement());
        }
        cp.execute();
    }

    protected void buildInfo() throws BuildException {
        // copy template Info.plist
        InputStream rin =
            WOFramework.class.getClassLoader().getResourceAsStream("Info.plist");
        File info = new File(resourcesDir(), "Info.plist");

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(info));
            BufferedReader in = new BufferedReader(new InputStreamReader(rin));
            String line = null;

            while ((line = in.readLine()) != null) {
                out.write(subsToken(line));
                out.write("\n");
            }

            out.flush();
            out.close();
            in.close();
        }
        catch (IOException ioex) {
            throw new BuildException("Error copying Info.plist", ioex);
        }
    }
    

    /** Substitutes a single occurance of "@NAME@" with
      * the value of <code>name</code> instance variable
      * and a single occurrance of "@LOWERC_NAME@" with 
      * the lowercase value of  <code>name</code>. 
      * 
      * <p>TODO: use some regular expressions package to do this.</p>
      */
    private String subsToken(String line) {
        int tokInd = line.indexOf("@NAME@");
        if (tokInd >= 0) {
            return line.substring(0, tokInd) + name + line.substring(tokInd + 6);
        }

        int lctokInd = line.indexOf("@LOWERC_NAME@");
        return (lctokInd >= 0)
            ? line.substring(0, lctokInd)
                + name.toLowerCase()
                + line.substring(lctokInd + 13)
            : line;
    }
    

    protected File frameworkDir() {
        return getProject().resolveFile(destDir + File.separator + name + ".framework");
    }

    protected File resourcesDir() {
        return new File(frameworkDir(), "Resources");
    }

    protected File wsresourcesDir() {
        return new File(frameworkDir(), "WebServerResources");
    }

    protected void initChildTask(Task t) {
        t.setOwningTarget(this.getOwningTarget());
        t.setProject(this.getProject());
        t.setTaskName(this.getTaskName());
        t.setLocation(this.getLocation());
    }

    protected boolean hasResources() {
        return resources.size() > 0;
    }

    protected boolean hasClasses() {
        return classes.size() > 0;
    }

    protected boolean hasLib() {
        return lib.size() > 0;
    }

    protected boolean hasJava() {
        return classes.size() > 0 || lib.size() > 0;
    }

    protected boolean hasWs() {
        return wsresources.size() > 0;
    }

    /**
     * Do any clean up necessary to allow this instance to be used again.
     */
    protected void cleanUp() {
        classes.clear();
        lib.clear();
        resources.clear();
        wsresources.clear();
    }
}