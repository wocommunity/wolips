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
import java.io.IOException;
import java.util.*;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;

/**
 * Ant task to build WebObjects framework. For detailed instructions go to the
 * <a href="../../../../../ant/woframework.html">manual page</a> .
 *
 * @ant.task category="packaging"
 */
public class WOFramework extends WOTask {
    private Vector lib = new Vector();

    public void addLib(FileSet set) {
        lib.addElement(set);
    }

    public void execute() throws BuildException {
        validateAttributes();
        
        log("Installing " + name + " in " + destDir);
        createDirectories();

        if (hasClasses()) {
            jarClasses();
        }

        if (hasLib()) {
            copyLibs();
        }

        if (hasResources()) {
            copyResources();
        }

        if (hasWs()) {
            copyWsresources();
        }

        new FrameworkFormat(this).processTemplates();
    }

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
     * Returns an Iterator over the file names of the library files
     * included in the lib nested element.
     */
    public Iterator getLibNames() {
        ArrayList libNames = new ArrayList();
        Enumeration en = lib.elements();
        while (en.hasMoreElements()) {
            FileSet fs = (FileSet) en.nextElement();
            DirectoryScanner scanner = fs.getDirectoryScanner(project);
            String[] libs = scanner.getIncludedFiles();
            for (int i = 0; i < libs.length; i++) {
                File libFile = new File(libs[i]);
                libNames.add(libFile.getName());
            }
        }
        return libNames.iterator();
    }

    /**
     * location where WOTask is being built up: ie the .woa dir or the .framework dir.
     * In this case, the .framework dir.
     */
    protected File taskDir() {
        return getProject().resolveFile(destDir + File.separator + name + ".framework");
    }


    protected File resourcesDir() {
        return new File(taskDir(), "Resources");
    }


    protected File wsresourcesDir() {
        return new File(taskDir(), "WebServerResources");
    }

    protected boolean hasLib() {
        return lib.size() > 0;
    }


    protected boolean hasJava() {
        return classes.size() > 0 || lib.size() > 0;
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
