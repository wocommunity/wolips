package org.objectstyle.wolips.eomodeler.core.model;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;

public abstract class AbstractManifestEOModelGroupFactory implements IEOModelGroupFactory {
	public boolean canLoadModelFrom(Object modelResource) {
		return modelResource instanceof IResource || modelResource instanceof File;
	}

	public EOModel loadModel(Object modelResource, Set<EOModelVerificationFailure> failures, boolean skipOnDuplicates, IProgressMonitor progressMonitor) throws EOModelException {
		File selectedModelFile;
		if (modelResource instanceof IResource) {
			selectedModelFile = ((IResource) modelResource).getLocation().toFile();
		} else if (modelResource instanceof File) {
			selectedModelFile = (File) modelResource;
		} else {
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
				modelGroup.loadModelsFromFolder(searchFolder.getFolder().toURL(), searchFolder.getDepth(), failures, skipOnDuplicates, null, progressMonitor);
			}
			EOModel model = modelGroup.loadModelFromFolder(selectedModelFolder.toURL(), failures, skipOnDuplicates, null, progressMonitor);
			modelGroup.resolve(failures);
			modelGroup.verify(failures);
			return model;
		} catch (IOException e) {
			throw new EOModelException("Failed to load model.", e);
		}
	}

	protected EOModelGroup loadModelGroup(Set<EOModelVerificationFailure> failures, boolean skipOnDuplicates, IProgressMonitor progressMonitor) throws EOModelException {
		try {
			EOModelGroup modelGroup = new EOModelGroup();
			List<ManifestSearchFolder> searchFolders = getSearchFolders(null);
			for (ManifestSearchFolder searchFolder : searchFolders) {
				modelGroup.loadModelsFromFolder(searchFolder.getFolder().toURL(), searchFolder.getDepth(), failures, skipOnDuplicates, null, progressMonitor);
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
