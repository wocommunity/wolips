package org.objectstyle.woproject.ant;

import java.io.File;
import java.util.HashSet;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

/**
 * @author mnolte
 *
 */
public class OtherClasspathSet extends FileSet {

	protected File aDirectory;
	protected boolean packagesOnly = false;

	/**
	 * Constructor for OtherClasspathSet.
	 */
	public OtherClasspathSet() {
		super();
	}

	private File[] findPackages(Project project, String packageDir) {

		// scan directory for packages (zip or jar)
		DirectoryScanner ds = new DirectoryScanner();
		ds.setIncludes(new String[] { "**/*.jar", "**/*.zip" });
		ds.setBasedir(getDir(project) + File.separator + packageDir);
		ds.setCaseSensitive(true);
		ds.scan();

		String[] foundPackages = ds.getIncludedFiles();
		int size = foundPackages.length;
		File[] finalFiles = new File[size];
		for (int i = 0; i < size; i++) {

			finalFiles[i] = new File(getDir(project), foundPackages[i]);
		}

		return finalFiles;
	}

	public void collectClassPaths(Project project, HashSet pathSet)
		throws BuildException {

		DirectoryScanner ds = getDirectoryScanner(project);
		String[] directories = ds.getIncludedDirectories();
		String[] files = ds.getIncludedFiles();

		// searching for directories or packages
		for (int i = 0; i < directories.length; i++) {
			if (isPackagesOnly()) {
				// don't add any dirs, search for packages in this dirs instead
				File[] paths = findPackages(project, directories[i]);

				if (paths == null || paths.length == 0) {
					log(
						"No Jars in " + paths[i] + ", ignoring.",
						Project.MSG_VERBOSE);
					continue;
				}

				int jsize = paths.length;
				for (int k = 0; k < jsize; k++) {
					pathSet.add(paths[k]);
				}
			} else {
				File directory = new File(getDir(project), directories[i]);
				if (directory.exists()) {
					pathSet.add(directory);
				}
			}
		}

		// files must be packages - ignoring packages only flag
		for (int i = 0; i < files.length; i++) {
			if (!files[i].endsWith(".zip") && !files[i].endsWith(".jar")) {
				log(
					"No Jars in " + files[i] + ", ignoring.",
					Project.MSG_VERBOSE);
				continue;
			}
			File packageFile = new File(getDir(project), files[i]);
			if (packageFile.exists()) {
				pathSet.add(packageFile);
			}
		}
	}

	/**
	 * Returns the packagesOnly.
	 * @return boolean
	 */
	public boolean isPackagesOnly() {
		return packagesOnly;
	}

	public void setDir(File dir) throws BuildException {
		aDirectory = dir;
		super.setDir(this.aDirectory);
	}

	public void setRoot(File aRoot) throws BuildException {
		aDirectory = aRoot;
		super.setDir(this.aDirectory);
	}

	/**
	 * Sets the packagesOnly.
	 * @param packagesOnly The packagesOnly to set
	 */
	public void setPackagesOnly(boolean packagesOnly) {
		this.packagesOnly = packagesOnly;
	}

}
