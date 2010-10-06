package org.objectstyle.wolips.baseforplugins.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
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
	
	public static boolean isFolder(URL url) throws IOException {
		boolean isFolder = false;
		String protocol = url.getProtocol();
		if ("file".equals(protocol)) {
			File f = new File(url.getPath());
			isFolder = f.isDirectory();
		} else if ("jar".equals(protocol)) {
			JarURLConnection conn = (JarURLConnection) url.openConnection();
			isFolder = conn.getJarEntry().isDirectory();
		} else {
			throw new IllegalArgumentException(url + " is not a File.");
		}
		return isFolder;
	}
	
	/**
	 * @param url - the url for the file or bundle resource
	 * @return true when the protocol is either <code>bundleresource</code> or <code>file</code> and the file exists. 
	 */
	public static boolean isFileOrBundleResource(URL url) {
		String protocol = url.getProtocol();
		if ("bundleresource".equals(protocol) || "file".equals(protocol))
			return exists(url);
		return false;
	}
	
	public static boolean isJarURL(URL url) {
		return "jar".equals(url.getProtocol());
	}

	public static URL[] getChildrenFolders(URL url) throws IOException {
		URL parentUrl = url;
		URL[] children;
		String protocol = parentUrl.getProtocol();
		if ("bundleresource".equals(protocol)) {
			BundleURLConnection conn = (BundleURLConnection)parentUrl.openConnection();
			parentUrl = conn.getFileURL();
			protocol = parentUrl.getProtocol();
		}
				
		if ("file".equals(protocol)) {
			File f = new File(parentUrl.getPath()).getAbsoluteFile();
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
			JarURLConnection conn = (JarURLConnection) parentUrl.openConnection();
			JarFile jarFile = null;
			try {
				jarFile = conn.getJarFile();
				JarEntry folderJarEntry = conn.getJarEntry();
				String folderName = folderJarEntry.getName();
				Enumeration<JarEntry> jarEntriesEnum = jarFile.entries();
				while (jarEntriesEnum.hasMoreElements()) {
					JarEntry jarEntry = jarEntriesEnum.nextElement();
					String name = jarEntry.getName();
					if (name.startsWith(folderName)) {
						URL childURL = new URL(parentUrl, name);
						childEntries.add(childURL);
					}
				}
				children = childEntries.toArray(new URL[childEntries.size()]);
			} catch (Exception e) {
				children = new URL[0];
			}
		} else {
			throw new IllegalArgumentException(parentUrl + " is not a format that can have its children retrieved.");
		}
		return children;
	}

	public static File cheatAndTurnIntoFile(URI uri) {
		try {
			return cheatAndTurnIntoFile(uri.toURL());
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Unable to turn '" + uri + "' into a URL.", e);
		}
	}

	public static File cheatAndTurnIntoFile(URL url) {
		File f;
		if (url == null) {
			f = null;
		}
		else {
			String protocol = url.getProtocol();
			if ("jar".equals(protocol)) {
				String externalForm = url.toExternalForm();
				int colonIndex = externalForm.indexOf(':');
				int bangIndex = externalForm.indexOf('!');
				String jarPath;
				if (bangIndex == -1) {
					jarPath = externalForm.substring(colonIndex + 1);
				}
				else {
					jarPath = externalForm.substring(colonIndex + 1, bangIndex);
				}
				try {
					f = new File(new URI(jarPath));
				}
				catch (Exception e) {
					throw new IllegalArgumentException(url + " cannot be turned into a File.", e);
				}
			}
			else if ("file".equals(protocol)) {
				try {
					String externalForm = url.getPath();
					f = new File(externalForm);
					if (!f.exists()) {
						externalForm = URLDecoder.decode(externalForm, "UTF-8");
						f = new File(externalForm);
					}
				} catch (IOException e) {
					throw new IllegalArgumentException(url + " cannot be turned into a File.", e);
				}
			} else if ("bundleresource".equals(protocol)) {
				try {
					BundleURLConnection conn = (BundleURLConnection) url.openConnection();
					String externalForm = conn.getFileURL().toExternalForm();
					externalForm = URLDecoder.decode(externalForm, "UTF-8");
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
		boolean exists = false;
		String protocol = url.getProtocol();
		if ("jar".equals(protocol)) {
			try {
				JarURLConnection conn = (JarURLConnection) url.openConnection();
				conn.getJarEntry();
				exists = true;
			} catch (FileNotFoundException e) {
				exists = false;
			} catch (IOException e) {
				// MS: Ah yes ... I remember now. It's chatty as all hell, that's why i commented this out. It just 
				// happens constantly for no particularly good reason. Another victory for crappy core libraries in
				// java!
				//e.printStackTrace();
				//throw new IllegalArgumentException(url + " is not a File.");
			}
		} else if ("file".equals(protocol) || "bundleresource".equals(protocol)) {
			File file = cheatAndTurnIntoFile(url);
			if (file != null) {
				exists = file.exists();
			}
		} else {
			throw new IllegalArgumentException(url + " is not a File.");
		}
		return exists;
	}
}