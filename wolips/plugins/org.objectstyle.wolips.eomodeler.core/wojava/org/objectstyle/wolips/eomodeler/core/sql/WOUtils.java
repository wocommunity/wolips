package org.objectstyle.wolips.eomodeler.core.sql;

import java.net.URL;

public class WOUtils {
	public static enum Version {
		WO_5_3, WO_5_6
	}

	public static WOUtils.Version version(ClassLoader classLoader) {
		WOUtils.Version version;
		Class dbClass;
		try {
			dbClass = Class.forName("com.webobjects.eoaccess.EODatabase", true, classLoader);
			if (dbClass.isInterface()) {
				version = WOUtils.Version.WO_5_6;
			} else {
				version = WOUtils.Version.WO_5_3;
			}
			return version;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Fixes a bug in WO 5.3 that can't handle a trailing slash on models loaded from jars.
	 * 
	 * @param url the URL to trim
	 * @return the trimmed URL
	 */
	public static URL trimModelURLs(URL url) {
		try {
			String externalForm = url.toExternalForm();
			if (externalForm.endsWith("/")) {
				externalForm = externalForm.substring(0, externalForm.length() - 1);
			}
			return new URL(externalForm);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
	public static void setWOSystemProperties() {
		System.setProperty("NSOpenProjectIDE", "WOLips");
		System.setProperty("NSProjectBundleEnabled", "true");
		System.setProperty("EOUseBundledJDBCInfo", "true");
		System.setProperty("EOLookupEntityClasses", "false");
	}
}
