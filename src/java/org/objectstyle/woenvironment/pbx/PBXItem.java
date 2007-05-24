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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

/**
 * @author tlg
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
@SuppressWarnings("unchecked")
public class PBXItem {
	public static final String _KISA = "isa";

	protected Object ref;

	public PBXItem(Object ref) {
		this.ref = ref;
	}

	private String serializeString(String aString) {
		char[] chars = aString.toCharArray();
		boolean simple = (chars.length != 0);
		for (int i = 0; i < chars.length & simple; i++) {
			simple = (Character.isLetterOrDigit(chars[i]) || chars[i] == '.' || chars[i] == '/' || chars[i] == '_');
		}
		if (!simple) {
			if (!aString.startsWith("<?xml")) {
				aString = aString.replaceAll("\n", "\\\\n");
			}
			aString = '"' + aString.replaceAll("\"", "\\\\\"") + '"';
		}
		return aString;
	}

	@Override
  public String toString() {
		StringWriter writer = new StringWriter();
		try {
			Field[] fields = this.getClass().getFields();
			SortedSet<Object> keys = new TreeSet<Object>();
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].getName().startsWith("_K"))
					keys.add(fields[i].get(this));
			}
			Iterator iter = keys.iterator();
			writer.write("{\n");
			while (iter.hasNext()) {
				String key = (String) iter.next();
				// Switch first letter to upperCase
				char[] chars = key.toCharArray();
				chars[0] = Character.toUpperCase(chars[0]);
				String methodName = "get" + new String(chars);
				Method method = this.getClass().getMethod(methodName, (Class[])null);
				Object ob = method.invoke(this, (Object[])null);
				if (ob == null)
					continue;
				if (ob instanceof PBXItem)
					ob = ((PBXItem) ob).ref;
				if (ob instanceof Map) {
					SortedSet ks = new TreeSet(((Map) ob).keySet());
					Iterator i = ks.iterator();
					writer.write("\t\t\t" + key + " = {\n");
					while (i.hasNext()) {
						Object k = i.next();
						writer.write("\t\t\t\t" + k + " = " + serializeString(((Map) ob).get(k).toString()) + ";\n");
					}
					writer.write("\t\t\t};\n");
				} else if (ob instanceof Collection) {
					Vector ks = new Vector((Collection) ob);
					Enumeration e = ks.elements();
					writer.write("\t\t\t" + key + " = (\n");
					while (e.hasMoreElements()) {
						Object k = e.nextElement();
						if (k instanceof PBXItem)
							k = ((PBXItem) k).ref;
						writer.write("\t\t\t\t" + k + ",\n");
					}
					writer.write("\t\t\t);\n");
				} else {
					writer.write("\t\t\t" + key + " = " + serializeString(ob.toString()) + ";\n");
				}
			}
			writer.write("\t\t};");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return writer.toString();
	}

	public String getIsa() {
		String isa = this.getClass().toString();
		return isa.substring(isa.lastIndexOf('.') + 1);
	}
}
