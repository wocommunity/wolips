/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 The ObjectStyle Group 
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne" 
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */

package org.objectstyle.wolips.core.plugin.logging;

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
	public static final String ATTR_GLOBAL_LOG_LEVEL = "global.log.level";
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
		if (object != null){
			if(ATTR_GLOBAL_LOG_LEVEL.equals(key) && object instanceof Integer){
				setGlobalLogLevel(((Integer)object).intValue());
			}		
			attributes.put(key.trim().toLowerCase(), object);
		}
	}
	
	private void setGlobalLogLevel(int newLevel){
		if(newLevel>=WOLipsLog.TRACE && newLevel<=WOLipsLog.FATAL){
			setAttribute(ATTR_LOG_LEVEL,new Integer(newLevel));
			Object[] allLoggerNames = logger.keySet().toArray();
			WOLipsLog currentLogger;
			for (int i = 0; i < allLoggerNames.length; i++) {
				currentLogger = (WOLipsLog)logger.get(allLoggerNames[i]);
				currentLogger.setLevel(newLevel);
			}
		}else{
			log.warn("setGlobalLogLevel -> unable to set level " + newLevel);
		}
	}

}
