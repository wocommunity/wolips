package org.objectstyle.woproject.ant;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;

public class InfoBuilderTest extends TestCase {

    private final static String NS_JAVA_PATH_BEGIN = "NSJavaPath</key>";
    private final static String NS_JAVA_PATH_END = "<key>CFBundleInfoDictionaryVersion";

    FileReader fin;
    StringWriter sout;
    BufferedWriter bout;
    BufferedReader bin;

    public InfoBuilderTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        fin = new FileReader("src/resources/Info.plist");
        bin = new BufferedReader(fin);
        sout = new StringWriter();
        bout = new BufferedWriter(sout);
    }

    public void tearDown() throws Exception {
        bout.close();
        bin.close();
        fin.close();
        sout.close();
        super.tearDown();
    }

    public void testNoLibs() throws Exception {
        InfoBuilder infoBuilder = new InfoBuilder(getName(), new Vector(), true);
        infoBuilder.writeInfoText(bin, bout);

        String output = sout.toString();
        assertTrue(output.length() > 0);
        String nsJavaPath = output.substring(output.indexOf(NS_JAVA_PATH_BEGIN) + NS_JAVA_PATH_BEGIN.length(), output.indexOf(NS_JAVA_PATH_END));
        assertEquals("\n\t<array>\n"
            + "\t\t<string>"+ getName().toLowerCase() + ".jar</string>\n"
            + "\t</array>\n\t"
            , nsJavaPath);
    }

    public void testLibsAndClasses() throws Exception {
        String lib1 =  "jar1.jar";
        String lib2 = "jar2.zip";
        Vector libs = new Vector();
        libs.add(lib1);
        libs.add(lib2);
        InfoBuilder infoBuilder = new InfoBuilder(getName(), libs, true);
        infoBuilder.writeInfoText(bin, bout);

        String output = sout.toString();
        String nsJavaPath = output.substring(output.indexOf(NS_JAVA_PATH_BEGIN) + NS_JAVA_PATH_BEGIN.length(), output.indexOf(NS_JAVA_PATH_END));
        assertEquals("\n\t<array>\n"
            + "\t\t<string>" + getName().toLowerCase() + ".jar</string>\n"
            + "\t\t<string>" + lib1 + "</string>\n"
            + "\t\t<string>" + lib2 + "</string>\n"
            + "\t</array>\n\t"
            , nsJavaPath);

    }

    public static void main(String[] args) {
        junit.textui.TestRunner.main(new String[]{InfoBuilderTest.class.getName()});
    }
}