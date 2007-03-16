package org.objectstyle.wolips.eomodeler.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

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

	public static URL[] getChildren(URL url) throws MalformedURLException {
		URL[] children;
		String protocol = url.getProtocol();
		if ("file".equals(protocol)) {
			File f = new File(url.getPath());
			File[] files = f.listFiles();
			children = new URL[files.length];
			for (int i = 0; i < files.length; i++) {
				File child = files[i];
				children[i] = child.toURL();
			}
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
