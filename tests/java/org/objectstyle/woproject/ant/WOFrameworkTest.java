package org.objectstyle.woproject.ant;

import java.io.*;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.*;
import org.apache.tools.ant.types.FileSet;

import java.io.*;

import junit.framework.TestCase;

public class WOFrameworkTest extends TestCase {

    public WOFrameworkTest(String name) {
        super(name);
    }

    public void testCreateInfo() throws Exception {
        File createdInfoFile = new File("Info.plist");
        try {
            WOFramework wofw = new WOFramework() {
                protected File resourcesDir(){
                    return new File(".");
                }
            };
            wofw.setName("Poodle");
            wofw.buildInfo();
            assertTrue(createdInfoFile.exists());
            String fileContents = getContents(createdInfoFile);
            assertTrue(fileContents.indexOf("poodle") > 0);
        } finally {
            if (createdInfoFile.exists()) {
                createdInfoFile.delete();
            }
        }
    }

    private String getContents(File file) throws Exception {
        FileInputStream fin = new FileInputStream(file);
        DataInput din = new DataInputStream(fin);
        StringBuffer contents = new StringBuffer();
        String line = null;
        while ((line = din.readLine()) != null) {
            contents.append(line);
        }
        return contents.toString();
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.main(new String[]{WOFrameworkTest.class.getName()});
    }
}