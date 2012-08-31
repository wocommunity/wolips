package org.objectstyle.wolips.eomodeler.factories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.objectstyle.wolips.eomodeler.core.model.AbstractManifestEOModelGroupFactory;
import org.objectstyle.wolips.eomodeler.core.model.ManifestSearchFolder;

public class SimpleManifestEOModelGroupFactory extends AbstractManifestEOModelGroupFactory {
	@Override
	public List<ManifestSearchFolder> getSearchFolders(File selectedModelFolder) throws IOException {
		List<ManifestSearchFolder> searchFolders = new LinkedList<ManifestSearchFolder>();
		fillInSearchFolders(new File(selectedModelFolder, "EntityModeler.modelpath"), searchFolders);
		fillInSearchFolders(new File(selectedModelFolder, ".EntityModeler.modelpath"), searchFolders);
		if (selectedModelFolder != null) {
			for (File modelFolder = selectedModelFolder.getParentFile(); modelFolder != null; modelFolder = modelFolder.getParentFile()) {
				fillInSearchFolders(new File(modelFolder, "EntityModeler.modelpath"), searchFolders);
				fillInSearchFolders(new File(modelFolder, ".EntityModeler.modelpath"), searchFolders);
			}
		}
		fillInSearchFolders(new File(System.getProperty("user.home"), "EntityModeler.modelpath"), searchFolders);
		fillInSearchFolders(new File(System.getProperty("user.home"), ".EntityModeler.modelpath"), searchFolders);
		fillInSearchFolders(new File(System.getProperty("user.home") + "/Library", "EntityModeler.modelpath"), searchFolders);
		fillInSearchFolders(new File(System.getProperty("user.home") + "/Library", ".EntityModeler.modelpath"), searchFolders);
		fillInSearchFolders(new File(System.getProperty("user.home") + "/Library/Preferences", "EntityModeler.modelpath"), searchFolders);
		fillInSearchFolders(new File(System.getProperty("user.home") + "/Library/Preferences", ".EntityModeler.modelpath"), searchFolders);
		return searchFolders;
	}
	
	protected int fillInSearchFolders(File baseFolder, String searchFolderPath, final List<ManifestSearchFolder> searchFolders) throws IOException {
		int count = SimpleManifestUtilities.fillInSearchFolders(baseFolder, searchFolderPath, new SimpleManifestUtilities.SearchFolderDelegate() {
			public void fileMatched(File file) throws IOException {
				searchFolders.add(new ManifestSearchFolder(file.getAbsoluteFile()));
			}
		});
		return count;
	}
	
	protected void fillInSearchFolders(File manifestFile, final List<ManifestSearchFolder> searchFolders) throws IOException {
		if (manifestFile.exists()) {
			BufferedReader manifestReader = new BufferedReader(new FileReader(manifestFile));
			try {
				String searchFolderPath;
				while ((searchFolderPath = manifestReader.readLine()) != null) {
					searchFolderPath = searchFolderPath.trim();
					if (searchFolderPath.equals("") || searchFolderPath.startsWith("#")) {
						continue;
					}
					if (searchFolderPath.contains(",")) {
						for (String possibleFolderPath : searchFolderPath.split(",")) {
							int count = fillInSearchFolders(manifestFile.getParentFile(), possibleFolderPath, searchFolders);
							if (count > 0) {
								break;
							}
						}
					}
					else {
						fillInSearchFolders(manifestFile.getParentFile(), searchFolderPath, searchFolders);
					}
				}
			} finally {
				manifestReader.close();
			}
		}
	}
}
