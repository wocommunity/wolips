package org.objectstyle.wolips.baseforplugins.util;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.content.IContentDescription;

public class FilesystemFile extends FilesystemResource implements IFile {
	public FilesystemFile(File file) {
		super(file);
	}
	
	public int getType() {
		return IResource.FILE;
	}

	public void accept(IResourceProxyVisitor visitor, int memberFlags) throws CoreException {
		visitor.visit(createProxy());
	}

	public void appendContents(InputStream source, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void appendContents(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void create(InputStream source, boolean force, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void create(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {
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

	public String getCharset() throws CoreException {
		throw new UnsupportedOperationException();
	}

	public String getCharset(boolean checkImplicit) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public String getCharsetFor(Reader reader) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public IContentDescription getContentDescription() throws CoreException {
		throw new UnsupportedOperationException();
	}

	public InputStream getContents() throws CoreException {
		throw new UnsupportedOperationException();
	}

	public InputStream getContents(boolean force) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public int getEncoding() throws CoreException {
		throw new UnsupportedOperationException();
	}

	public IFileState[] getHistory(IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void move(IPath destination, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void setCharset(String newCharset) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void setCharset(String newCharset, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void setContents(InputStream source, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void setContents(IFileState source, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void setContents(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void setContents(IFileState source, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}
}
