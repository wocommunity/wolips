/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2007 The ObjectStyle Group 
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
package org.objectstyle.wolips.jdt.ui.tags;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

public class TagLib {

	public static final String FILENAME = ".wotaglib";

	private static final String TAG = "tag";

	private static final String COMPONENT = "component";

	public IProject project;

	private int lowestID = 0;

	private ArrayList<Tag> tagsList;

	private ArrayList<TaggedComponent> taggedComponentsList;

	public TagLib(IProject project) {
		super();
		this.project = project;
		this.read();
	}

	private void read() {
		tagsList = new ArrayList<Tag>();
		taggedComponentsList = new ArrayList<TaggedComponent>();
		Properties properties = new Properties();
		IFile iFile = project.getFile(FILENAME);
		if (iFile.exists() && iFile.isAccessible()) {
			File file = new File(iFile.getLocation().toOSString());
			FileInputStream fileInputStream;
			try {
				fileInputStream = new FileInputStream(file);
				properties.load(fileInputStream);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Enumeration<Object> enumeration = properties.keys();
		while (enumeration.hasMoreElements()) {
			String key = (String) enumeration.nextElement();
			if (key.startsWith(TAG)) {
				String id = key.substring(key.indexOf('.') + 1);
				Integer idInteger = new Integer(id);
				String value = properties.getProperty(key);
				Tag tag = new Tag(this, idInteger.intValue(), value);
				tagsList.add(tag);
				if (idInteger.intValue() > lowestID) {
					lowestID = idInteger;
				}
			}
			if (key.startsWith(COMPONENT)) {
				String name = key.substring(key.indexOf('.') + 1);
				String idsString = properties.getProperty(key);
				StringTokenizer stringTokenizer = new StringTokenizer(idsString, ",");
				ArrayList<Integer> idsList = new ArrayList<Integer>();
				while (stringTokenizer.hasMoreTokens()) {
					String id = stringTokenizer.nextToken();
					Integer idInteger = new Integer(id);
					idsList.add(idInteger);
				}
				int[] ids = new int[idsList.size()];
				for (int i = 0; i < idsList.size(); i++) {
					ids[i] = idsList.get(i).intValue();
				}
				TaggedComponent taggedComponent = new TaggedComponent(this, name, ids);
				taggedComponentsList.add(taggedComponent);
			}
		}
	}

	private void write() {
		Properties properties = new Properties();
		for (Iterator iterator = tagsList.iterator(); iterator.hasNext();) {
			Tag tag = (Tag) iterator.next();
			properties.put(TAG + '.' + tag.id, tag.name);
		}
		for (Iterator iterator = taggedComponentsList.iterator(); iterator.hasNext();) {
			TaggedComponent taggedComponent = (TaggedComponent) iterator.next();
			String value = "";
			int[] ids = taggedComponent.ids;
			for (int i = 0; i < ids.length; i++) {
				value = value + ids[i];
				if (i != ids.length - 1) {
					value = value + ",";
				}
			}
			properties.put(COMPONENT + '.' + taggedComponent.name, value);
		}
		IFile iFile = project.getFile(FILENAME);
		File file = new File(iFile.getLocation().toOSString());
		FileOutputStream fileOutputStream;
		try {
			fileOutputStream = new FileOutputStream(file);
			properties.store(fileOutputStream, "wotaglib");
			iFile.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	public Tag[] getTags() {
		return tagsList.toArray(new Tag[tagsList.size()]);
	}

	public Tag[] getTags(Tag tag) {
		ArrayList<Tag> filteredTagsList = new ArrayList<Tag>();
		filteredTagsList.addAll(tagsList);
		filteredTagsList.remove(tag);
		return filteredTagsList.toArray(new Tag[filteredTagsList.size()]);
	}

	public TaggedComponent[] getComponents(Tag[] tags) {
		ArrayList<TaggedComponent> filteredTaggedComponentsList = new ArrayList<TaggedComponent>();
		for (Iterator iterator = taggedComponentsList.iterator(); iterator.hasNext();) {
			TaggedComponent taggedComponent = (TaggedComponent) iterator.next();
			int found = 0;
			for (int i = 0; i < taggedComponent.ids.length; i++) {
				int id = taggedComponent.ids[i];
				for (int j = 0; j < tags.length; j++) {
					Tag tag = tags[j];
					if (id == tag.id) {
						found = found + 1;
					}
				}
			}
			if (found == tags.length) {
				filteredTaggedComponentsList.add(taggedComponent);
			}
		}
		return filteredTaggedComponentsList.toArray(new TaggedComponent[filteredTaggedComponentsList.size()]);
	}

	private Tag getTag(String name) {
		for (Iterator iterator = tagsList.iterator(); iterator.hasNext();) {
			Tag tag = (Tag) iterator.next();
			if (tag.name.equalsIgnoreCase(name)) {
				return tag;
			}
		}
		lowestID = lowestID + 1;
		Tag tag = new Tag(this, lowestID, name);
		tagsList.add(tag);
		return tag;
	}

	private TaggedComponent getTaggedComponent(String name) {
		for (Iterator iterator = taggedComponentsList.iterator(); iterator.hasNext();) {
			TaggedComponent taggedComponent = (TaggedComponent) iterator.next();
			if (taggedComponent.name.equalsIgnoreCase(name)) {
				return taggedComponent;
			}
		}
		TaggedComponent taggedComponent = new TaggedComponent(this, name, new int[0]);
		taggedComponentsList.add(taggedComponent);
		return taggedComponent;
	}

	public void tagComponents(String[] componentNames, String tagName) {
		Tag tag = this.getTag(tagName);
		for (int i = 0; i < componentNames.length; i++) {
			String componentName = componentNames[i];
			TaggedComponent taggedComponent = this.getTaggedComponent(componentName);
			taggedComponent.addTag(tag);
		}
		this.write();
	}
}
