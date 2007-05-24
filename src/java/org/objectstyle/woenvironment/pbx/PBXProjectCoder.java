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
package org.objectstyle.woenvironment.pbx;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author tlg
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
@SuppressWarnings("unchecked")
public class PBXProjectCoder {
	protected Map objects;

	protected Map alocatedObjects;

	protected Object root;

	/**
	 * PBXProjectCoder class is used to serialse/unserialise objects from a pbx
	 * proj, the goal is to have an exact match from ProjectBuilder and eclipse
	 * 
	 * @param objects
	 * @param root
	 */
	public PBXProjectCoder(Map objects, Object root) {
		this.objects = objects;
		this.root = root;
		this.alocatedObjects = new Hashtable();
	}

	/**
	 * Returns the dictionary asociated to an object ref
	 * 
	 * @param ref
	 * @return
	 */
	public Map getRef(Object ref) {
		return (Map) objects.get(ref);
	}

	/**
	 * objectForRef returns the object asociated with it's reference.
	 * 
	 * @param ref
	 * @return
	 */
	public Object objectForRef(Object ref) {
		Object object = null;
		/**
		 * First we chack if that object has been build up already
		 */
		object = alocatedObjects.get(ref);
		if (object != null) {
			return object;
		}
		/**
		 * Wee need the infos about the objects to instantiate it
		 */
		Map dico = getRef(ref);
		if (dico == null)
			return null;
		/**
		 * The class name for the object is locate in the isa field
		 */
		String isa = (String) dico.get("isa");
		Class newClass = null;
		try {
			newClass = Class.forName(this.getClass().getPackage().getName() + "." + isa);
			object = newClass.getConstructor(new Class[] { Object.class }).newInstance(new Object[] { ref });
		} catch (Exception e) {
			System.err.println("Class not found : " + isa);
			return null;
		}
		/**
		 * We fill the object with the values found in the dictionary
		 */
		Iterator iter = dico.keySet().iterator();
		while (iter.hasNext()) {
			Object key = iter.next();
			if (key.equals("isa"))
				continue;
			Object value = dico.get(key);
			if (value instanceof Collection) {
				Collection array = (Collection) value;
				// Switch first letter to upper Case
				char[] chars = ((String) key).toCharArray();
				chars[0] = Character.toUpperCase(chars[0]);
				String methodName = "add" + new String(chars);
				Method method = null;
				try {
					method = newClass.getMethod(methodName, new Class[] { Object.class });
					Iterator i = array.iterator();
					Object v, n = null;
					while (i.hasNext()) {
						n = i.next();
						v = objectForRef(n);
						method.invoke(object, new Object[] { ((v == null) ? n : v) });
					}
				} catch (Exception e) {
					System.err.println("Method : " + methodName + " on class [" + isa + "] not found.");
					continue;
				}
			} else {
				char[] chars = ((String) key).toCharArray();
				chars[0] = Character.toUpperCase(chars[0]);
				String methodName = "set" + new String(chars);
				Method method = null;
				Object v = null;
				try {
					method = newClass.getMethod(methodName, new Class[] { Object.class });
					v = objectForRef(value);
					method.invoke(object, new Object[] { ((v == null) ? value : v) });
				} catch (Exception e) {
					System.err.println("Method : " + methodName + " on class [" + isa + "] not found.");
					continue;
				}
			}
		}
		alocatedObjects.put(ref, object);
		return object;
	}

	/**
	 * serialize is used to serialize back the objects in a pbxproj file
	 * 
	 * @return
	 */
	public String serialize() {
		Set sorted = null;
		try {
			sorted = new TreeSet(alocatedObjects.keySet());
		} catch (Exception e) {
			e.printStackTrace();
		}
		StringWriter writer = new StringWriter();
		Iterator iter = sorted.iterator();
		writer.write("// !$*UTF8*$!\n" + "{\n" + "\tarchiveVersion = 1;\n" + "\tclasses = {\n" + "\t};\n" + "\tobjectVersion = 38;\n" + "\tobjects = {\n");
		String oldKey = new String();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			if (oldKey.equals(""))
				oldKey = key.substring(0, 2);
			if (!key.startsWith(oldKey)) {
				for (int i = 0; i <= 4; i++) {
					writer.write("//" + oldKey + i + "\n");
				}
				oldKey = key.substring(0, 2);
				for (int i = 0; i <= 4; i++) {
					writer.write("//" + oldKey + i + "\n");
				}
			}
			writer.write("\t\t" + key + " = " + alocatedObjects.get(key) + "\n");
		}
		writer.write("\t};\n");
		writer.write("\trootObject = " + root + ";\n");
		writer.write("}\n");
		writer.flush();
		return writer.toString();
	}
}