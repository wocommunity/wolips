package org.objectstyle.wolips.baseforplugins.util;

import java.io.File;
import java.net.URI;
import java.util.Map;

import org.eclipse.core.internal.resources.ProjectDescription;
import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.content.IContentTypeMatcher;

public class FilesystemProject extends FilesystemFolder implements IProject {
	public static final FilesystemProject ROOT = new FilesystemProject(new File("/"));
	
	public FilesystemProject(File file) {
		super(file);
	}


	public void build(int kind, String builderName, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void build(int kind, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void close(IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void create(IProjectDescription description, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void create(IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void create(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public IContentTypeMatcher getContentTypeMatcher() throws CoreException {
		throw new UnsupportedOperationException();
	}

	public IProjectDescription getDescription() throws CoreException {
		return new ProjectDescription();
	}

	public IProjectNature getNature(String natureId) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public IPath getPluginWorkingLocation(IPluginDescriptor plugin) {
		throw new UnsupportedOperationException();
	}

	public IPath getWorkingLocation(String id) {
		throw new UnsupportedOperationException();
	}

	public IProject[] getReferencedProjects() throws CoreException {
		throw new UnsupportedOperationException();
	}

	public IProject[] getReferencingProjects() {
		throw new UnsupportedOperationException();
	}

	public boolean hasNature(String natureId) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public boolean isNatureEnabled(String natureId) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public boolean isOpen() {
		throw new UnsupportedOperationException();
	}

	public void loadSnapshot(int options, URI snapshotLocation, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void move(IProjectDescription description, boolean force, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void open(int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void open(IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void saveSnapshot(int options, URI snapshotLocation, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void setDescription(IProjectDescription description, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void setDescription(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void build(IBuildConfiguration config, int kind, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public IBuildConfiguration getActiveBuildConfig() throws CoreException {
		throw new UnsupportedOperationException();
	}

	public IBuildConfiguration getBuildConfig(String configName) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public IBuildConfiguration[] getBuildConfigs() throws CoreException {
		throw new UnsupportedOperationException();
	}

	public IBuildConfiguration[] getReferencedBuildConfigs(String configName, boolean includeMissing) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public boolean hasBuildConfig(String configName) throws CoreException {
		throw new UnsupportedOperationException();
	}
}
