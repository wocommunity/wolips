package org.objectstyle.wolips.eomodeler.core;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME = "org.objectstyle.wolips.eomodeler.core.messages";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			System.out.println(key + "=" + key);
			return '!' + key + '!';
		}
	}

	public static String getString(String key, Object[] params) {
		try {
			return MessageFormat.format(RESOURCE_BUNDLE.getString(key), params);
		} catch (MissingResourceException e) {
			System.out.println(key + "=" + key);
			return '!' + key + '!';
		}
	}
}
