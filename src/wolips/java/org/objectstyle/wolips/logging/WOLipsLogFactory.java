package org.objectstyle.wolips.logging;

import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.commons.logging.LogFactory;

/**
 * @author mnolte
 *
 */
public class WOLipsLogFactory extends LogFactory {

	public static final String ATTR_LOG_LEVEL = "log.level";
	public static final int DEFAULT_LOG_LEVEL = WOLipsLog.INFO;

	public static Log log;

	private boolean initialized = false;
	private Hashtable attributes;
	private Hashtable logger;

	/**
	 * Constructor for WOLipsLogFactory.
	 */
	public WOLipsLogFactory() {
		super();
		attributes = new Hashtable();
		setAttribute(ATTR_LOG_LEVEL, new Integer(DEFAULT_LOG_LEVEL));
		logger = new Hashtable();
		if (log == null) {
			//	build own logger
			log =
				new WOLipsLog(
					WOLipsLogFactory.class.getName(),
					DEFAULT_LOG_LEVEL);
			logger.put(WOLipsLogFactory.class.getName(), log);
		}

	}

	/**
	 * @see org.apache.commons.logging.LogFactory#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String key) {
		return attributes.get(key.trim().toLowerCase());
	}

	/**
	 * @see org.apache.commons.logging.LogFactory#getAttributeNames()
	 */
	public String[] getAttributeNames() {
		Object[] keys = attributes.keySet().toArray();
		String[] result = new String[keys.length];
		for (int i = 0; i < keys.length; i++) {
			result[i] = (String) keys[i];
		}
		return result;
	}

	/**
	 * @see org.apache.commons.logging.LogFactory#getInstance(java.lang.Class)
	 */
	public Log getInstance(Class clazz) throws LogConfigurationException {
		return getInstance(clazz.getName());
	}

	/**
	 * @see org.apache.commons.logging.LogFactory#getInstance(java.lang.String)
	 */
	public Log getInstance(String name) throws LogConfigurationException {
		if (logger.get(name) == null) {
			if (getAttribute(ATTR_LOG_LEVEL) == null) {
				setAttribute(ATTR_LOG_LEVEL, new Integer(DEFAULT_LOG_LEVEL));
			}
			logger.put(
				name,
				new WOLipsLog(
					name,
					((Integer) getAttribute(ATTR_LOG_LEVEL)).intValue()));
		}
		return (Log) logger.get(name);
	}

	/**
	 * @see org.apache.commons.logging.LogFactory#release()
	 */
	public void release() {
		logger = new Hashtable();
	}

	/**
	 * @see org.apache.commons.logging.LogFactory#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String key) {
		// avoid deleting of mandatory attributes
		if (key != null && !key.equals(ATTR_LOG_LEVEL)) {
			attributes.remove(key.trim().toLowerCase());
		}
	}

	/**
	 * @see org.apache.commons.logging.LogFactory#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String key, Object object) {
		if (object != null)
			attributes.put(key.trim().toLowerCase(), object);
	}

}
