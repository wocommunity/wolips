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

import java.io.*;
import java.util.*;

/**
 * Class that knows how to write an Info.plist file for a framework.
 * Needs a suitable template, and some information about the contents of the framework
 * to be given to the constructor.
 *
 * @author Emily Bache, Andrei Adamchik
 */
public class InfoBuilder extends TemplateProcessor {
    private Vector libFiles;

    public InfoBuilder(WOTask task, Vector libFiles) {
        super(task);
        this.libFiles = libFiles;
    }
    

    /**
     * Substitutes a single occurance of "@NAME@" with the value of <code>name</code>
     * instance variable and a single occurrance of "@LOWERC_NAME@" with the
     * lowercase value of <code>name</code>. <p>
     * Substitutes a single occurance of "@JAR_ARRAY@" with a list of
     * jar files enclosed in appropriate tags.
     *
     * TODO: use some regular expressions package to do this.</p>
     *
     */
    protected String replaceTokens(String line) {
        String nameToken = "@NAME@";
        int tokInd = line.indexOf(nameToken);
        if (tokInd >= 0) {
            return replace(nameToken, line, getName());
        }

        String lowerCaseNameToken = "@LOWERC_NAME@";
        int lctokInd = line.indexOf(lowerCaseNameToken);
        if (lctokInd >= 0) {
            return replace(lowerCaseNameToken, line, getName().toLowerCase());
        }

        String jarArrayToken = "@JAR_ARRAY@";
        int jarArrayIndex = line.indexOf(jarArrayToken);
        if (jarArrayIndex >= 0) {
            StringBuffer toInsert = new StringBuffer();
            toInsert.append("<array>");
            if (task.hasClasses()) {
                toInsert.append("\n\t\t<string>");
                toInsert.append(getName().toLowerCase() + ".jar");
                toInsert.append("</string>");
            }
            for (Iterator it = libFiles.iterator(); it.hasNext();) {
                String libFile = (String)it.next();
                toInsert.append("\n\t\t<string>");
                toInsert.append(libFile);
                toInsert.append("</string>");
            }
            toInsert.append("\n\t</array>");
            return replace(jarArrayToken, line, toInsert.toString());
        }
        return line;
    }
}