package org.objectstyle.wolips.eomodeler.core.model;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.objectstyle.wolips.eomodeler.core.utils.URLUtils;

public abstract class AbstractManifestEOModelGroupFactory implements IEOModelGroupFactory {
	public boolean canLoadModelFrom(Object modelResource) {
		return modelResource instanceof IResource || modelResource instanceof File || modelResource instanceof URL || modelResource instanceof URI;
	}

	public boolean canLoadModelGroupFrom(Object modelGroupResource) {
		return modelGroupResource instanceof File;
	}

	public EOModel loadModel(Object modelResource, Set<EOModelVerificationFailure> failures, boolean skipOnDuplicates, IProgressMonitor progressMonitor) throws EOModelException {
		File selectedModelFile;
		if (modelResource instanceof IResource) {
			selectedModelFile = ((IResource) modelResource).getLocation().toFile();
		} else if (modelResource instanceof File) {
			selectedModelFile = (File) modelResource;
		} else if (modelResource instanceof URL) {
			selectedModelFile = URLUtils.cheatAndTurnIntoFile((URL) modelResource);
		} else if (modelResource instanceof URI) {
			selectedModelFile = URLUtils.cheatAndTurnIntoFile((URI) modelResource);
		} else {
			throw new EOModelException("Unknown model resource: " + modelResource);
		}

		if (selectedModelFile == null) {
			throw new EOModelException("Unknown model resource: " + modelResource);
		}

		File selectedModelFolder;
		if (selectedModelFile.isFile()) {
			selectedModelFolder = selectedModelFile.getParentFile();
		} else {
			selectedModelFolder = selectedModelFile;
		}
		String modelFolderName = selectedModelFolder.getName();
		if (!modelFolderName.endsWith(".eomodeld")) {
			throw new EOModelException(selectedModelFolder.getAbsolutePath() + " is not an EOModel folder.");
		}
		try {
			EOModelGroup modelGroup = new EOModelGroup();
			modelGroup.setEditingModelURL(selectedModelFolder.toURL());
			List<ManifestSearchFolder> searchFolders = getSearchFolders(selectedModelFolder);
			for (ManifestSearchFolder searchFolder : searchFolders) {
				modelGroup.loadModelsFromURL(searchFolder.getFolder().toURL(), searchFolder.getDepth(), failures, skipOnDuplicates, progressMonitor);
			}
			EOModel model = modelGroup.loadModelFromURL(selectedModelFolder.toURL(), failures, skipOnDuplicates, progressMonitor);
			modelGroup.resolve(failures);
			modelGroup.verify(failures);
			return model;
		} catch (IOException e) {
			throw new EOModelException("Failed to load model.", e);
		}
	}

	public EOModelGroup loadModelGroup(Object modelGroupResource, Set<EOModelVerificationFailure> failures, boolean skipOnDuplicates, URL editingModelURL, IProgressMonitor progressMonitor) throws EOModelException {
		try {
			EOModelGroup modelGroup = new EOModelGroup();
			List<ManifestSearchFolder> searchFolders = getSearchFolders(null);
			modelGroup.setEditingModelURL(editingModelURL);
			for (ManifestSearchFolder searchFolder : searchFolders) {
				modelGroup.loadModelsFromURL(searchFolder.getFolder().toURL(), searchFolder.getDepth(), failures, skipOnDuplicates, progressMonitor);
			}
			modelGroup.resolve(failures);
			modelGroup.verify(failures);
			return modelGroup;
		} catch (IOException e) {
			throw new EOModelException("Failed to load model groups.", e);
		}
	}

	public abstract List<ManifestSearchFolder> getSearchFolders(File selectedModelFolder) throws IOException;
}
