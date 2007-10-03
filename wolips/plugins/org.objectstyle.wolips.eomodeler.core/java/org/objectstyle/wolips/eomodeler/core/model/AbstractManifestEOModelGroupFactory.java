package org.objectstyle.wolips.eomodeler.core.model;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.objectstyle.wolips.baseforplugins.util.URLUtils;

public abstract class AbstractManifestEOModelGroupFactory implements IEOModelGroupFactory {
	public void loadModelGroup(Object modelGroupResource, EOModelGroup modelGroup, Set<EOModelVerificationFailure> failures, boolean skipOnDuplicates, IProgressMonitor progressMonitor) throws EOModelException {
		try {
			File modelGroupFile = null;
			if (modelGroupResource == null) {
				modelGroupFile = null;
			} else if (modelGroupResource instanceof IResource) {
				modelGroupFile = ((IResource) modelGroupResource).getLocation().toFile();
			} else if (modelGroupResource instanceof File) {
				modelGroupFile = (File) modelGroupResource;
			} else if (modelGroupResource instanceof URL) {
				modelGroupFile = URLUtils.cheatAndTurnIntoFile((URL) modelGroupResource);
			} else if (modelGroupResource instanceof URI) {
				modelGroupFile = URLUtils.cheatAndTurnIntoFile((URI) modelGroupResource);
			} else {
				throw new EOModelException("Unknown model resource: " + modelGroupResource);
			}

			List<ManifestSearchFolder> searchFolders = getSearchFolders(modelGroupFile);
			if (searchFolders != null) {
				for (ManifestSearchFolder searchFolder : searchFolders) {
					modelGroup.loadModelsFromURL(searchFolder.getFolder().toURL(), searchFolder.getDepth(), failures, skipOnDuplicates, progressMonitor);
				}
			}
			if (modelGroupFile != null && modelGroupFile.getName().endsWith(".eomodeld")) {
				modelGroup.loadModelsFromURL(modelGroupFile.toURL(), 1, failures, skipOnDuplicates, progressMonitor);
			}
		} catch (IOException e) {
			throw new EOModelException("Failed to load model groups.", e);
		}
	}

	public abstract List<ManifestSearchFolder> getSearchFolders(File selectedModelFolder) throws IOException;
}
