package org.objectstyle.woproject.ant;

import java.io.*;
import java.util.*;

/**
 * Class that knows how to write an Info.plist file for a framework.
 * Needs a suitable template, and some information about the contents of the framework
 * to be given to the constructor.
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