package org.objectstyle.woproject.wolips.io;

import java.util.Hashtable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author uli
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class FileStringScanner {
	
    public FileStringScanner() {
        super();
    }

    public static void FileOpenReplaceWith(String file, String replace, String with) throws IOException {
        System.out.println("file: " + file);
        String stringFromFile = FileStringScanner.stringFromFile(new File(file));
                        System.out.println("stringFromFile:" + stringFromFile);
        String replacedString = FileStringScanner.replace(stringFromFile, replace, with);
                        System.out.println("replacedString: " + replacedString);
                        //if nothing replaced
        if(replacedString != null)
        	FileStringScanner.stringToFile(new File(file), replacedString);
    }

    public static String stringFromFile(File aFile) throws IOException {
		int size = (int)aFile.length();
                FileInputStream 	fis = new FileInputStream(aFile);
                byte [] 		data = new byte[size];
                int 		bytesRead = 0;
                while(bytesRead < size) {
                    bytesRead += fis.read(data, bytesRead, size - bytesRead);
                }
                fis.close();
                return new String(data);
    }
 
 	public static void stringToFile(File aFile, String aString) throws IOException {
 		int length = aString.length();
 		FileOutputStream 	fos = new FileOutputStream(aFile);
		fos.write(aString.getBytes(), 0 , length);
        fos.close();
 	}

    public static String replace(String text, String replace, String with) {
        if (text != null) {
            int	li = 0;
            int	l = replace.length();
            int	i = text.indexOf(replace, li);
            if(i>=0) {
                StringBuffer aWorkString = new StringBuffer(text.length()+1);
                while (i >= 0) {
                    if (i>li)
                        aWorkString.append(text.substring(li,i));
                    aWorkString.append(with);
                    li = i+l;
                    i = text.indexOf(replace, li);
                }
                aWorkString.append(text.substring(li));
                return aWorkString.toString();
            }
            return null;
        }
        return null;
    }

}

