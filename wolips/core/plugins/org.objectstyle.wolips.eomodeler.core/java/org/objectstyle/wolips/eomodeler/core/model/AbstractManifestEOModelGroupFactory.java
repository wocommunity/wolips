package org.objectstyle.wolips.eomodeler.core.model;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.objectstyle.wolips.baseforplugins.util.URLUtils;

public abstract class AbstractManifestEOModelGroupFactory implements IEOModelGroupFactory {
	public boolean loadModelGroup(Object modelGroupResource, EOModelGroup modelGroup, Set<EOModelVerificationFailure> failures, boolean skipOnDuplicates, IProgressMonitor progressMonitor) throws EOModelException {
		int previousModelCount = modelGroup.getModels().size();
		
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
				LinkedHashSet<ManifestSearchFolder> uniqueSearchFolders = new LinkedHashSet<ManifestSearchFolder>(searchFolders);
				boolean printedHeader = false;
				for (ManifestSearchFolder searchFolder : uniqueSearchFolders) {
					if (!printedHeader) {
						System.out.println(getClass().getSimpleName() + ", Searching: ");
						printedHeader = true;
					}
					System.out.println("  " + searchFolder);
					modelGroup.loadModelsFromURL(searchFolder.getFolder().toURI().toURL(), searchFolder.getDepth(), failures, skipOnDuplicates, progressMonitor);
				}
			}
			if (modelGroupFile != null && modelGroupFile.getName().endsWith(".eomodeld")) {
				modelGroup.loadModelsFromURL(modelGroupFile.toURI().toURL(), 1, failures, skipOnDuplicates, progressMonitor);
			}
		} catch (IOException e) {
			throw new EOModelException("Failed to load model groups.", e);
		}
		
		boolean allModelsLoaded = modelGroup.getModels().size() - previousModelCount > 1;
		return allModelsLoaded;
	}

	public abstract List<ManifestSearchFolder> getSearchFolders(File selectedModelFolder) throws IOException, EOModelException;
}
