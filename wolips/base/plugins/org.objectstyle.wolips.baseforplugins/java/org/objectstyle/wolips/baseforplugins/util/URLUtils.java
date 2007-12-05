package org.objectstyle.wolips.baseforplugins.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.osgi.framework.internal.core.BundleURLConnection;

public class URLUtils {
	public static String getExtension(URL url) {
		return URLUtils.getExtension(url.getPath());
	}
	
	public static String getExtension(URI uri) {
		return URLUtils.getExtension(uri.getPath());
	}
	
	public static String getExtension(String path) {
		String extension = null;
		if (path != null) {
			int dotIndex = path.lastIndexOf('.');
			if (dotIndex != -1) {
				extension = path.substring(dotIndex + 1);
			}
		}
		return extension;
	}
	
	public static String getName(URL url) {
		return URLUtils.getName(url.getPath());
	}
	
	public static String getName(URI uri) {
		return URLUtils.getName(uri.getPath());
	}
	
	public static String getName(String path) {
		String name = null;
		if (path != null) {
			int slashIndex = path.lastIndexOf('/');
			if (slashIndex != -1) {
				name = path.substring(slashIndex + 1);
			}
			else {
				name = path;
			}
		}
		return name;
	}
	
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

	public static URL[] getChildrenFolders(URL url) throws IOException {
		URL[] children;
		String protocol = url.getProtocol();
		if ("bundleresource".equals(protocol)) {
			BundleURLConnection conn = (BundleURLConnection)url.openConnection();
			url = conn.getFileURL();
			protocol = url.getProtocol();
		}
				
		if ("file".equals(protocol)) {
			File f = new File(url.getPath()).getAbsoluteFile();
			if (!f.exists()) {
				children = new URL[0];
			}
			else {
				File[] files = f.listFiles();
				if (files == null) {
					children = new URL[0];
				}
				else {
					List<URL> childrenList = new LinkedList<URL>();
					for (int i = 0; i < files.length; i++) {
						File child = files[i];
						if (!child.isHidden() && child.isDirectory()) {
							childrenList.add(child.toURL());
						}
					}
					children = childrenList.toArray(new URL[childrenList.size()]);
				}
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
			throw new IllegalArgumentException(url + " is not a format that can have its children retrieved.");
		}
		return children;
	}

	public static File cheatAndTurnIntoFile(URI uri) {
		File f;
		String scheme = uri.getScheme();
		if ("file".equals(scheme)) {
			f = new File(uri.getPath());
		} else {
			throw new IllegalArgumentException(uri + " is not a File.");
		}
		return f;
	}

	public static File cheatAndTurnIntoFile(URL url) {
		File f;
		if (url == null) {
			f = null;
		}
		else {
			String protocol = url.getProtocol();
			if ("file".equals(protocol)) {
				f = new File(url.getPath());
			} else if ("bundleresource".equals(protocol)) {
				try {
					BundleURLConnection conn = (BundleURLConnection) url.openConnection();
					String externalForm = conn.getFileURL().toExternalForm();
					externalForm = externalForm.replaceAll(" ", "%20");
					f = new File(new URI(externalForm));
				} catch (IOException e) {
					throw new IllegalArgumentException(url + " cannot be turned into a File.", e);
				} catch (URISyntaxException e) {
					throw new IllegalArgumentException(url + " cannot be turned into a File.", e);
				}
			} else {
				throw new IllegalArgumentException(url + " is not a File.");
			}
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
