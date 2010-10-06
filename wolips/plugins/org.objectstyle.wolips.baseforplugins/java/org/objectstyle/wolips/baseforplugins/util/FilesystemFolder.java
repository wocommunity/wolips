package org.objectstyle.wolips.baseforplugins.util;

import java.io.File;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.FileInfoMatcherDescription;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceFilterDescription;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

public class FilesystemFolder extends FilesystemResource implements IFolder {
	private List<File> _children;
	
	public FilesystemFolder(File file) {
		this(file, null);
	}
	
	public FilesystemFolder(File file, List<File> children) {
		super(file);
		_children = children;
	}
	
	public int getType() {
		return IResource.FOLDER;
	}

	public void accept(IResourceProxyVisitor visitor, int memberFlags) throws CoreException {
		if (visitor.visit(createProxy())) {
			for (IResource resource : members(memberFlags)) {
				if ((memberFlags & IContainer.INCLUDE_HIDDEN) != 0 || !resource.isHidden()) {
					resource.accept(visitor, memberFlags);
				}
			}
		}
	}

	public boolean exists(IPath path) {
		throw new UnsupportedOperationException();
	}

	public IResource findMember(String name) {
		throw new UnsupportedOperationException();
	}

	public IResource findMember(String name, boolean includePhantoms) {
		throw new UnsupportedOperationException();
	}

	public IResource findMember(IPath path) {
		throw new UnsupportedOperationException();
	}

	public IResource findMember(IPath path, boolean includePhantoms) {
		throw new UnsupportedOperationException();
	}

	public String getDefaultCharset() throws CoreException {
		throw new UnsupportedOperationException();
	}

	public String getDefaultCharset(boolean checkImplicit) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public IFile getFile(IPath path) {
		return new FilesystemFile(new File(_file(), path.toOSString()));
	}

	public IFolder getFolder(IPath path) {
		throw new UnsupportedOperationException();
	}

	public IResource[] members() throws CoreException {
		return members(IResource.NONE);
	}

	public IResource[] members(boolean includePhantoms) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public IResource[] members(int memberFlags) throws CoreException {
		File[] memberFiles = _children == null ? _file().listFiles() : _children.toArray(new File[_children.size()]);
		List<IResource> members = new LinkedList<IResource>();
		if (memberFiles != null) {
			for (File memberFile : memberFiles) {
				members.add(FilesystemResource.resource(memberFile));
			}
		}
		return members.toArray(new IResource[members.size()]);
	}

	public IFile[] findDeletedMembersWithHistory(int depth, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void setDefaultCharset(String charset) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void setDefaultCharset(String charset, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public IResourceFilterDescription createFilter(int type, FileInfoMatcherDescription matcherDescription, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public IResourceFilterDescription[] getFilters() throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void create(boolean force, boolean local, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void create(int updateFlags, boolean local, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void createLink(IPath localLocation, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void createLink(URI location, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void delete(boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public IFile getFile(String name) {
		throw new UnsupportedOperationException();
	}

	public IFolder getFolder(String name) {
		throw new UnsupportedOperationException();
	}

	public void move(IPath destination, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

}
