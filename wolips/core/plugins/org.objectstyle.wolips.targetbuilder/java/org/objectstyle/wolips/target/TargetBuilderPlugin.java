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

import java.io.File;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jdt.internal.core.builder.State;

public class TargetBuilderPlugin extends Plugin
{
	//The shared instance.
	private static TargetBuilderPlugin _plugin;

	private TargetBuilderPersistenceStore _persistenceStore;

	public TargetBuilderPlugin(IPluginDescriptor descriptor)
	{
		super(descriptor);
		_plugin = this;
	}

	public static TargetBuilderPlugin getDefault()
	{
		return _plugin;
	}

	public void startup() throws CoreException
	{
		super.startup();
		_persistenceStore = new TargetBuilderPersistenceStore();
		ISavedState lastState = ResourcesPlugin.getWorkspace().addSaveParticipant(this, _persistenceStore);
		// remove target buildstates if the project is removed
		ResourcesPlugin.getWorkspace().addResourceChangeListener(_persistenceStore, IResourceChangeEvent.PRE_DELETE);

		if (lastState == null)
			return;
		IPath location = lastState.lookup(new Path("save"));
		if (location == null)
			return;
		// the plugin instance should read any important state from the file.
		File f = getStateLocation().append(location).toFile();
		_persistenceStore.readStateFrom(f);
	}

	public void shutdown() throws CoreException
	{
		super.shutdown();
	}

	public void setBuildStateForKey(State buildState, String key)
	{
		_persistenceStore.setBuildStateForKey(buildState, key);
	}

	public State buildStateForKey(String key)
	{
		return _persistenceStore.buildStateForKey(key);
	}
}
