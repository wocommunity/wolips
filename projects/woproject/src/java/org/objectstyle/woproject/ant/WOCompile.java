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
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.Path;

/**
 * Customized subclass of Javac used to locate jars in frameworks.
 *
 * @author Anjo Krank
 */
public class WOCompile extends Javac {
    private ArrayList frameworkSets = new ArrayList();
    
    public void addFrameworks(FrameworkSet frameworks) throws BuildException {
        frameworkSets.add(frameworks);
    }

    protected String buildFrameworkPaths() {
        StringBuffer buf = new StringBuffer();

        Project project = getProject();

        // track included jar files to avoid double entries
        Vector jarSet = new Vector();
        HashSet processedFrameworks = new HashSet();

        int size = frameworkSets.size();
        for (int i = 0; i < size; i++) {
            FrameworkSet fs = (FrameworkSet) frameworkSets.get(i);
            HashSet frameworksToSkip = new HashSet();

            try {
                String[] frameworks = fs.getFrameworks();

                for (int j = 0; j < frameworks.length; j++) {
                    String frameworkName = frameworks[j];
                    File[] jars = fs.findJars(frameworkName);

                    if (jars == null || jars.length == 0) {
                        log("No Jars in " + fs.getDir(project).getPath() + "/" + frameworkName + ", ignoring.",
                            Project.MSG_VERBOSE);
                        continue;
                    }

                    if(!processedFrameworks.contains(frameworkName)) {
                        int jsize = jars.length;
                        for (int k = 0; k < jsize; k++) {
                            if (!jarSet.contains(jars[k])) {
                                jarSet.add(jars[k]);
                            } else {
                                log("Skipped " + jars[k].getPath(), Project.MSG_VERBOSE);
                            }
                        }
                    }
                    processedFrameworks.add(frameworkName);
                }
            } catch (BuildException be) {
                // directory doesn't exist or is not readable
                log(be.getMessage(), Project.MSG_WARN);
            }
        }
        Object someFiles[] = jarSet.toArray();
        size = someFiles.length;
        for (int i = 0; i < size; i++) {
            log(": Framework JAR " + someFiles[i], Project.MSG_VERBOSE);
            buf.append(someFiles[i]).append(":");
        }
        return buf.toString();
    }

    public void execute() throws BuildException {
        Path path = new Path(getProject(), buildFrameworkPaths());

        setClasspath(path);
        super.execute();
    }
}
