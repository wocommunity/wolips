package org.objectstyle.woproject.ant;

import java.io.*;

public class InfoBuilder {

    private String name;

    public InfoBuilder(String fwName) {
        this.name = fwName;
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
     *
     * TODO: use some regular expressions package to do this.</p>
     *
     * @param line Description of the Parameter
     * @return Description of the Return Value
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

}