package org.objectstyle.wolips.eomodeler.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelException;
import org.objectstyle.wolips.eomodeler.core.utils.URLUtils;

public class EclipseFileUtils {
	public static IURIEditorInput getEditorInput(EOModel model) throws MalformedURLException, EOModelException, CoreException, URISyntaxException {
		IURIEditorInput editorInput;
		IFile indexFile = EclipseFileUtils.getEclipseIndexFile(model);
		if (indexFile == null) {
			String externalForm = model.getIndexURL().toExternalForm().replace(' ', '+');
			IFileStore indexFileStore = EFS.getStore(new URI(externalForm));
			editorInput = new FileStoreEditorInput(indexFileStore);
		}
		else {
			editorInput = new FileEditorInput(indexFile);
		}
		return editorInput;
	}
	
	public static File getExternalIndexFile(EOModel model) throws MalformedURLException, EOModelException {
		if (model.getIndexURL() == null) {
			throw new EOModelException("Failed to load model.");
		}
		return URLUtils.cheatAndTurnIntoFile(model.getIndexURL());
	}
	
	public static IFile getEclipseFile(URL url) {
		return EclipseFileUtils.getEclipseFile(URLUtils.cheatAndTurnIntoFile(url));
	}
	
	public static IFile getEclipseFile(URI uri) {
		return EclipseFileUtils.getEclipseFile(URLUtils.cheatAndTurnIntoFile(uri));
	}

	public static IFile getEclipseFile(File externalFile) {
		return ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(externalFile.getAbsolutePath()));
	}

	public static IFile getEclipseIndexFile(EOModel model) throws MalformedURLException, EOModelException {
		return EclipseFileUtils.getEclipseFile(EclipseFileUtils.getExternalIndexFile(model));
	}
}
