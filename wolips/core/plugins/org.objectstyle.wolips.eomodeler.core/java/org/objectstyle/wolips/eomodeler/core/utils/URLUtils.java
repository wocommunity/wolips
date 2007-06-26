package org.objectstyle.wolips.eomodeler.core.utils;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class URLUtils {
	public static boolean isFolder(URL url) {
		boolean isFolder = false;
		String protocol = url.getProtocol();
		if ("file".equals(protocol)) {
			File f = new File(url.getPath());
			isFolder = f.isDirectory();
		} else {
			throw new IllegalArgumentException(url + " is not a File.");
		}
		return isFolder;
	}

	public static URL[] getChildren(URL url) throws IOException {
		URL[] children;
		String protocol = url.getProtocol();
		if ("file".equals(protocol)) {
			File f = new File(url.getPath()).getAbsoluteFile();
			File[] files = f.listFiles();
			children = new URL[files.length];
			for (int i = 0; i < files.length; i++) {
				File child = files[i];
				children[i] = child.toURL();
			}
		} else if ("jar".equals(protocol)) {
			List<URL> childEntries = new LinkedList<URL>();
			JarURLConnection conn = (JarURLConnection) url.openConnection();
			JarFile jarFile = conn.getJarFile();
			JarEntry folderJarEntry = conn.getJarEntry();
			String folderName = folderJarEntry.getName();
			Enumeration<JarEntry> jarEntriesEnum = jarFile.entries();
			while (jarEntriesEnum.hasMoreElements()) {
				JarEntry jarEntry = jarEntriesEnum.nextElement();
				String name = jarEntry.getName();
				if (name.startsWith(folderName)) {
					URL childURL = new URL(url, name);
					childEntries.add(childURL);
				}
			}
			children = childEntries.toArray(new URL[childEntries.size()]);
		} else {
			throw new IllegalArgumentException(url + " is not a File.");
		}
		return children;
	}

	public static File cheatAndTurnIntoFile(URL url) {
		File f;
		String protocol = url.getProtocol();
		if ("file".equals(protocol)) {
			f = new File(url.getPath());
		} else {
			throw new IllegalArgumentException(url + " is not a File.");
		}
		return f;
	}

	public static boolean exists(URL url) {
		boolean exists = true;
		String protocol = url.getProtocol();
		if ("file".equals(protocol)) {
			File f = new File(url.getPath());
			exists = f.exists();
		} else {
			throw new IllegalArgumentException(url + " is not a File.");
		}
		return exists;
	}
}
