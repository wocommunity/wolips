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
 * @author Emily Bache
 */
public class InfoBuilder {

    private String name;
    private Vector libFiles;
    private boolean hasOwnClasses;


    public InfoBuilder(String fwName, Vector libFiles, boolean hasOwnClasses) {
        this.name = fwName;
        this.libFiles = libFiles;
        this.hasOwnClasses = hasOwnClasses;
    }

    /**
     * @param templateResourceName name of Info.plist template file, to be found
     * on the classpath.
     * @param infoFile the file to write the output to
     */
    public void writeInfo(String templateResourceName, File infoFile) throws IOException {

        InputStream templateStream = this.getClass().getClassLoader().getResourceAsStream(templateResourceName);

        BufferedWriter out = new BufferedWriter(new FileWriter(infoFile));
        BufferedReader in = new BufferedReader(new InputStreamReader(templateStream));
        writeInfoText(in, out);
    }

    void writeInfoText(BufferedReader in, BufferedWriter out) throws IOException {
        String line = null;

        while ((line = in.readLine()) != null) {
            out.write(subsToken(line));
            out.write("\n");
        }

        out.flush();
        out.close();
        in.close();
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
    private String subsToken(String line) {
        String nameToken = "@NAME@";
        int tokInd = line.indexOf(nameToken);
        if (tokInd >= 0) {
            return replace(nameToken, line, name);
        }

        String lowerCaseNameToken = "@LOWERC_NAME@";
        int lctokInd = line.indexOf(lowerCaseNameToken);
        if (lctokInd >= 0) {
            return replace(lowerCaseNameToken, line, name.toLowerCase());
        }

        String jarArrayToken = "@JAR_ARRAY@";
        int jarArrayIndex = line.indexOf(jarArrayToken);
        if (jarArrayIndex >= 0) {
            StringBuffer toInsert = new StringBuffer();
            toInsert.append("<array>");
            if (this.hasOwnClasses) {
                toInsert.append("\n\t\t<string>");
                toInsert.append(name.toLowerCase() + ".jar");
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

    private String replace(String token, String line, String toInsert) {
        int tokenIndex = line.indexOf(token);
        return line.substring(0, tokenIndex) + toInsert + line.substring(tokenIndex + token.length());
    }

}