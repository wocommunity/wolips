package org.objectstyle.wolips.baseforplugins.util;

import java.io.File;
import java.net.URI;
import java.util.Map;

import org.eclipse.core.internal.runtime.AdapterManager;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IPathVariableManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

public class FilesystemResource implements IResource, IResourceProxy {
	private File _file;

	public FilesystemResource(File file) {
		_file = file;
	}

	protected File _file() {
		return _file;
	}

	public boolean equals(Object obj) {
		return obj instanceof FilesystemResource && ((FilesystemResource) obj)._file.equals(_file);
	}

	public int hashCode() {
		return _file.hashCode();
	}

	public Object getAdapter(Class adapter) {
		return AdapterManager.getDefault().getAdapter(this, adapter);
	}

	public boolean contains(ISchedulingRule rule) {
		throw new UnsupportedOperationException();
	}

	public boolean isConflicting(ISchedulingRule rule) {
		throw new UnsupportedOperationException();
	}

	public void accept(IResourceProxyVisitor visitor, int memberFlags) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void accept(IResourceVisitor visitor) throws CoreException {
		accept(visitor, IResource.DEPTH_INFINITE, IResource.NONE);
	}

	public void accept(IResourceVisitor visitor, int depth, boolean includePhantoms) throws CoreException {
		accept(visitor, depth, includePhantoms ? IContainer.INCLUDE_PHANTOMS : IResource.NONE);
	}

	public void accept(IResourceVisitor visitor, int depth, int memberFlags) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void accept(IResourceProxyVisitor visitor, int depth, int memberFlags) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void clearHistory(IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void copy(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void copy(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void copy(IProjectDescription description, boolean force, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void copy(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public IMarker createMarker(String type) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public IResourceProxy createProxy() {
		return this;
	}

	public void delete(boolean force, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void delete(int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void deleteMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public boolean exists() {
		return _file.exists();
	}

	public IMarker findMarker(long id) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public IMarker[] findMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public int findMaxProblemSeverity(String type, boolean includeSubtypes, int depth) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public String getFileExtension() {
		String name = getName();
		String extension = null;
		if (name != null) {
			int lastDotIndex = name.lastIndexOf('.');
			if (lastDotIndex != -1) {
				extension = name.substring(lastDotIndex + 1);
			}
		}
		return extension;
	}

	public IPath getFullPath() {
		return new Path(_file.getAbsolutePath());
	}

	public long getLocalTimeStamp() {
		throw new UnsupportedOperationException();
	}

	public IPath getLocation() {
		throw new UnsupportedOperationException();
	}

	public URI getLocationURI() {
		return _file().toURI();
	}

	public IMarker getMarker(long id) {
		throw new UnsupportedOperationException();
	}

	public long getModificationStamp() {
		throw new UnsupportedOperationException();
	}

	public String getName() {
		return _file.getName();
	}

	public IPathVariableManager getPathVariableManager() {
		throw new UnsupportedOperationException();
	}

	public IContainer getParent() {
		File parentFile = _file.getParentFile();
		return parentFile == null ? null : FilesystemResource.folder(parentFile);
	}

	public Map getPersistentProperties() throws CoreException {
		throw new UnsupportedOperationException();
	}

	public String getPersistentProperty(QualifiedName key) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public IProject getProject() {
		return FilesystemProject.ROOT;
	}

	public IPath getProjectRelativePath() {
		throw new UnsupportedOperationException();
	}

	public IPath getRawLocation() {
		throw new UnsupportedOperationException();
	}

	public URI getRawLocationURI() {
		throw new UnsupportedOperationException();
	}

	public ResourceAttributes getResourceAttributes() {
		throw new UnsupportedOperationException();
	}

	public Map getSessionProperties() throws CoreException {
		throw new UnsupportedOperationException();
	}

	public Object getSessionProperty(QualifiedName key) {
		throw new UnsupportedOperationException();
	}

	public int getType() {
		throw new UnsupportedOperationException();
	}

	public IWorkspace getWorkspace() {
		throw new UnsupportedOperationException();
	}

	public boolean isAccessible() {
		return true;
	}

	public boolean isDerived() {
		return isDerived(IResource.NONE);
	}

	public boolean isDerived(int options) {
		String name = getName();
		return name.equals(".svn") || name.equals(".git") || name.equals("bin") || name.equals("build");
	}

	public boolean isHidden() {
		return _file.isHidden() || _file.getName().startsWith(".");
	}

	public boolean isHidden(int options) {
		return isHidden();
	}

	public boolean isLinked() {
		throw new UnsupportedOperationException();
	}

	public boolean isVirtual() {
		throw new UnsupportedOperationException();
	}

	public boolean isLinked(int options) {
		throw new UnsupportedOperationException();
	}

	public boolean isLocal(int depth) {
		throw new UnsupportedOperationException();
	}

	public boolean isPhantom() {
		throw new UnsupportedOperationException();
	}

	public boolean isReadOnly() {
		throw new UnsupportedOperationException();
	}

	public boolean isSynchronized(int depth) {
		throw new UnsupportedOperationException();
	}

	public boolean isTeamPrivateMember() {
		throw new UnsupportedOperationException();
	}

	public boolean isTeamPrivateMember(int options) {
		throw new UnsupportedOperationException();
	}

	public void move(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void move(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void move(IProjectDescription description, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void move(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void refreshLocal(int depth, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void revertModificationStamp(long value) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void setDerived(boolean isDerived) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void setDerived(boolean isDerived, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void setHidden(boolean isHidden) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void setLocal(boolean flag, int depth, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public long setLocalTimeStamp(long value) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void setPersistentProperty(QualifiedName key, String value) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void setReadOnly(boolean readOnly) {
		throw new UnsupportedOperationException();
	}

	public void setResourceAttributes(ResourceAttributes attributes) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void setSessionProperty(QualifiedName key, Object value) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void setTeamPrivateMember(boolean isTeamPrivate) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void touch(IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public static FilesystemFolder folder(File file) {
		return (FilesystemFolder) resource(file);
	}

	public static FilesystemResource resource(File file) {
		return file == null ? null : file.isDirectory() ? new FilesystemFolder(file) : new FilesystemFile(file);
	}

	public IPath requestFullPath() {
		throw new UnsupportedOperationException();
	}

	public IResource requestResource() {
		return this;
	}
}
