package org.objectstyle.woproject.maven2.wobootstrap.mojos;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

public abstract class AbstractWOMojo extends AbstractMojo {

	/**
	 * The maven project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * The set of dependencies required by the project
	 * 
	 * @parameter default-value="${project.dependencies}"
	 * @required
	 * @readonly
	 */
	private java.util.ArrayList dependencies;

	/**
	 * @parameter expression="${localRepository}"
	 * @required
	 * @readonly
	 */
	private ArtifactRepository localRepository;

	/**
	 * Read patternsets.
	 * 
	 * @parameter expression="readPatternsets"
	 */

	private String[][] dependencyPaths;

	public AbstractWOMojo() {
		super();
	}

	protected String getProjectFolder() {
		String projectFolder = this.getProject().getFile().getPath().substring(0, this.getProject().getFile().getPath().length() - 7);
		return projectFolder;
	}

	protected String getWOProjectFolder() {
		File file = new File(this.getProjectFolder() + "woproject");
		if (file.exists()) {
			return file.getPath();
		}
		return null;
	}

	public java.util.ArrayList getDependencies() {
		return dependencies;
	}

	public ArtifactRepository getLocalRepository() {
		return localRepository;
	}

	public MavenProject getProject() {
		return project;
	}

	private String[][] getDependencyPaths() {
		if (dependencyPaths == null) {
			ArrayList classPathEntriesArray = new ArrayList();
			ArrayList artfactNameEntriesArray = new ArrayList();
			Iterator dependenciesIterator = dependencies.iterator();
			while (dependenciesIterator.hasNext()) {
				Dependency dependency = (Dependency) dependenciesIterator.next();
				String depenendencyGroup = dependency.getGroupId();
				if (depenendencyGroup != null) {
					depenendencyGroup = depenendencyGroup.replace('.', File.separatorChar);
				}
				String depenendencyArtifact = dependency.getArtifactId();
				String depenendencyVersion = dependency.getVersion();
				String dependencyPath = depenendencyGroup + File.separator + depenendencyArtifact + File.separator + depenendencyVersion + File.separator + depenendencyArtifact + "-" + depenendencyVersion + ".jar";
				classPathEntriesArray.add(dependencyPath);
				artfactNameEntriesArray.add(depenendencyArtifact);
			}
			dependencyPaths = new String[2][];
			dependencyPaths[0] = (String[]) classPathEntriesArray.toArray(new String[classPathEntriesArray.size()]);
			dependencyPaths[1] = (String[]) artfactNameEntriesArray.toArray(new String[artfactNameEntriesArray.size()]);
		}
		return dependencyPaths;
	}
}
