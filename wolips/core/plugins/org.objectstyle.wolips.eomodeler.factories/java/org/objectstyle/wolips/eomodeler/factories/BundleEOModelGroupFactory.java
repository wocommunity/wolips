package org.objectstyle.wolips.eomodeler.factories;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.objectstyle.woenvironment.plist.WOLPropertyListSerialization;
import org.objectstyle.wolips.eomodeler.core.model.AbstractManifestEOModelGroupFactory;
import org.objectstyle.wolips.eomodeler.core.model.EOModelException;
import org.objectstyle.wolips.eomodeler.core.model.ManifestSearchFolder;

public class BundleEOModelGroupFactory extends AbstractManifestEOModelGroupFactory {
	public static Properties propertiesFromFile(File propertiesFile) throws IOException {
		Properties properties = null;
		if (propertiesFile.exists()) {
			properties = new Properties();
			InputStream is = new BufferedInputStream(new FileInputStream(propertiesFile));
			try {
				properties.load(is);
			} finally {
				is.close();
			}
		}
		return properties;
	}

	protected int fillInSearchFolders(File bundleFolder, String searchFolderPath, final List<ManifestSearchFolder> manifestSearchFolders) throws IOException {
		int count = SimpleManifestUtilities.fillInSearchFolders(bundleFolder, searchFolderPath, new SimpleManifestUtilities.SearchFolderDelegate() {
			public void fileMatched(File file) throws IOException {
				manifestSearchFolders.add(new ManifestSearchFolder(file.getAbsoluteFile()));
			}
		});
		return count;
	}

	protected void fillInSearchFolders(File bundleFolder, final List<ManifestSearchFolder> manifestSearchFolders, List<File> frameworkSearchFolders, Set<String> loadedBundles) throws IOException {
		System.out.println("BundleEOModelGroupFactory.fillInSearchFolders: Searching " + bundleFolder + " ...");
		if (new File(bundleFolder, "pom.xml").exists()) {
			fillInSearchFolders(bundleFolder, "src" + File.separator + "main" + File.separator + "resources" + File.separator + "*.eomodeld", manifestSearchFolders);
			fillInSearchFolders(bundleFolder, "src" + File.separator + "test" + File.separator + "resources" + File.separator + "*.eomodeld", manifestSearchFolders);
		} else {
			if (new File(bundleFolder, "Resources").exists()) {
				fillInSearchFolders(bundleFolder, "Resources" + File.separator + "*.eomodeld", manifestSearchFolders);
			} else if (new File(bundleFolder, "Contents" + File.separator + "Resources").exists()) {
				fillInSearchFolders(bundleFolder, "Contents" + File.separator + "Resources" + File.separator + "*.eomodeld", manifestSearchFolders);
			}
		}

		Properties buildProperties = BundleEOModelGroupFactory.propertiesFromFile(new File(bundleFolder, "build.properties"));
		if (buildProperties != null) {
			String dependenciesStr = buildProperties.getProperty("dependencies");
			if (dependenciesStr != null && dependenciesStr.trim().length() > 0) {
				for (String dependency : dependenciesStr.trim().split(",")) {
					if (!loadedBundles.contains(dependency)) {
						System.out.println("BundleEOModelGroupFactory.fillInSearchFolders: " + bundleFolder + " depends on " + dependency + ":");
						boolean resolvedDependency = false;
						for (File frameworkSearchFolder : frameworkSearchFolders) {
							File dependencyBundleFolder = new File(frameworkSearchFolder, dependency);
							if (dependencyBundleFolder.exists()) {
								fillInSearchFolders(dependencyBundleFolder, manifestSearchFolders, frameworkSearchFolders, loadedBundles);
								resolvedDependency = true;
								break;
							}
							
							dependencyBundleFolder = new File(frameworkSearchFolder, dependency + ".framework");
							if (dependencyBundleFolder.exists()) {
								fillInSearchFolders(dependencyBundleFolder, manifestSearchFolders, frameworkSearchFolders, loadedBundles);
								resolvedDependency = true;
								break;
							}
							
							dependencyBundleFolder = new File(frameworkSearchFolder, dependency + ".woa");
							if (dependencyBundleFolder.exists()) {
								fillInSearchFolders(dependencyBundleFolder, manifestSearchFolders, frameworkSearchFolders, loadedBundles);
								resolvedDependency = true;
								break;
							}
						}

						if (!resolvedDependency) {
							System.out.println("BundleEOModelGroupFactory.fillInSearchFolders: Couldn't resolve the dependency '" + dependency + "'.");
						}

						loadedBundles.add(dependency);
					}
				}
			}
		}
	}

	@Override
	public List<ManifestSearchFolder> getSearchFolders(File selectedModelFolder) throws IOException, EOModelException {
		final List<ManifestSearchFolder> manifestSearchFolders = new LinkedList<ManifestSearchFolder>();

		List<File> frameworkSearchFolders = new LinkedList<File>();

		File buildPropertiesFile = null;
		for (File buildPropertiesFolder = selectedModelFolder; buildPropertiesFile == null && buildPropertiesFolder != null; buildPropertiesFolder = buildPropertiesFolder.getParentFile()) {
			File possibleBuildPropertiesFile = new File(buildPropertiesFolder, "build.properties");
			if (possibleBuildPropertiesFile.exists()) {
				buildPropertiesFile = possibleBuildPropertiesFile;
			}
		}

		String woPropertiesPath = System.getenv("EntityModelerNSGlobalPropertiesPath");
		if (woPropertiesPath == null) {
			woPropertiesPath = System.getProperty("user.home") + File.separator + "WebObjects.properties";
		}
		Properties woProperties = propertiesFromFile(new File(woPropertiesPath));
		if (woProperties != null) {
			try {
				@SuppressWarnings("unchecked")
				List<String> nsProjectSearchPaths = (List<String>) WOLPropertyListSerialization.propertyListFromString(woProperties.getProperty("NSProjectSearchPath"));
				if (nsProjectSearchPaths != null) {
					for (String nsProjectSearchPath : nsProjectSearchPaths) {
						File nsProjectSearchFolder = new File(nsProjectSearchPath);
						if (nsProjectSearchFolder.exists()) {
							frameworkSearchFolders.add(nsProjectSearchFolder);
						}
					}
				}
			} catch (Throwable t) {
				throw new EOModelException("Failed to parse NSProjectSearchPath.", t);
			}
		}

		File frameworkPathsManifestFile = null;
		for (File frameworkPathsManifestFolder = selectedModelFolder; frameworkPathsManifestFile == null && frameworkPathsManifestFolder != null; frameworkPathsManifestFolder = frameworkPathsManifestFolder.getParentFile()) {
			File possibleFrameworkPathsManifestFile = new File(frameworkPathsManifestFolder, ".EntityModeler.frameworkpath");
			if (possibleFrameworkPathsManifestFile.exists()) {
				frameworkPathsManifestFile = possibleFrameworkPathsManifestFile;
			}
		}

		if (frameworkPathsManifestFile != null) {
			BufferedReader frameworkPathsManifestReader = new BufferedReader(new FileReader(frameworkPathsManifestFile));
			try {
				String frameworkPath;
				while ((frameworkPath = frameworkPathsManifestReader.readLine()) != null) {
					File frameworkFolder = new File(frameworkPath);
					if (!frameworkFolder.isAbsolute()) {
						frameworkFolder = new File(frameworkPathsManifestFile.getCanonicalFile().getParentFile(), frameworkPath);
					}
					if (frameworkFolder.exists()) {
						frameworkSearchFolders.add(frameworkFolder);
					}
				}
			} finally {
				frameworkPathsManifestReader.close();
			}
		}

		if (buildPropertiesFile != null && frameworkSearchFolders.size() > 0) {
			System.out.println("BundleEOModelGroupFactory.getSearchFolders: " + selectedModelFolder);
			Set<String> loadedBundles = new HashSet<String>();
			fillInSearchFolders(buildPropertiesFile.getParentFile(), manifestSearchFolders, frameworkSearchFolders, loadedBundles);
		}

		return manifestSearchFolders;
	}
}
