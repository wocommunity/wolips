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
 
 package org.objectstyle.wolips.target;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.internal.core.builder.JavaBuilder;
import org.eclipse.jdt.internal.core.builder.State;

public class TargetBuilderPersistenceStore implements ISaveParticipant, IResourceChangeListener
{
	private HashMap _buildStates;

	public TargetBuilderPersistenceStore()
	{
		super();
		_buildStates = new HashMap();
	}

	public void setBuildStateForKey(State buildState, String key)
	{
		_buildStates.put(key, buildState);
	}

	public State buildStateForKey(String key)
	{
		return (State) _buildStates.get(key);
	}

	public void doneSaving(ISaveContext context)
	{
		TargetBuilderPlugin pluginInstance = TargetBuilderPlugin.getDefault();
		int previousSaveNumber = context.getPreviousSaveNumber();
		String oldFileName = "save-" + Integer.toString(previousSaveNumber);
		File f = pluginInstance.getStateLocation().append(oldFileName).toFile();
		f.delete();
	}

	public void prepareToSave(ISaveContext context) throws CoreException
	{}

	public void rollback(ISaveContext context)
	{
		TargetBuilderPlugin pluginInstance = TargetBuilderPlugin.getDefault();
		// since the save operation has failed, delete the saved state we have just written
		int saveNumber = context.getSaveNumber();
		String saveFileName = "save-" + Integer.toString(saveNumber);
		File f = pluginInstance.getStateLocation().append(saveFileName).toFile();
		f.delete();
	}

	public void saving(ISaveContext context) throws CoreException
	{
		switch (context.getKind())
		{
			case ISaveContext.FULL_SAVE :
				TargetBuilderPlugin pluginInstance = TargetBuilderPlugin.getDefault();
				int saveNumber = context.getSaveNumber();
				String saveFileName = "save-" + Integer.toString(saveNumber);
				File f = pluginInstance.getStateLocation().append(saveFileName).toFile();
				// if we fail to write, an exception is thrown and we do not update the path
				writeImportantState(f);
				context.map(new Path("save"), new Path(saveFileName));
				context.needSaveNumber();
				break;
			case ISaveContext.PROJECT_SAVE :
				// get the project related to this save operation
				IProject project = context.getProject();
				System.out.println("ISaveContext.PROJECT_SAVE:" + project);
				// save its information, if necessary
				break;
			case ISaveContext.SNAPSHOT :
				//System.out.println("ISaveContext.SNAPSHOT:");
				// This operation needs to be really fast because
				// snapshots can be requested frequently by the
				// workspace.
				break;
		}
	}

	protected void writeImportantState(File target) throws CoreException
	{
		try
		{
			DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(target)));
			try
			{
				Set aSet = _buildStates.entrySet();
				out.writeInt(aSet.size());
				for (Iterator iter = aSet.iterator(); iter.hasNext();)
				{
					Map.Entry element = (Map.Entry) iter.next();
					String key = (String) element.getKey();
					State value = (State) element.getValue();
					out.writeUTF(key);
					JavaBuilder.writeState(value, out);
				}
			}
			finally
			{
				out.close();
			}
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}

	protected void readStateFrom(File target)
	{
		try
		{
			DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(target)));
			try
			{
				int stateCount = in.readInt();
				for (int i = 0; i < stateCount; i++)
				{
					String key = in.readUTF();
					//This is not correct.
					State buildState =JavaBuilder.readState(null,in);
					setBuildStateForKey(buildState, key);
				}
			}
			finally
			{
				in.close();
			}
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}

	public void resourceChanged(IResourceChangeEvent event)
	{
		IResource res = event.getResource();
		String keyPrefix = res.getName() + "/";
		for (Iterator iter = _buildStates.entrySet().iterator(); iter.hasNext();)
		{
			Map.Entry element = (Map.Entry) iter.next();
			String key = (String) element.getKey();
			if(key.startsWith(keyPrefix))
				iter.remove();
		}
	}
}