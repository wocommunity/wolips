package org.objectstyle.woproject.maven2.resources;

//org.apache.maven.plugins:maven-compiler-plugin:compile
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * resources goal for WebObjects projects.
 * 
 * @goal define-woapplication-resources
 * @requiresDependencyResolution compile
 * @author uli
 * @since 2.0
 */
public class DefineWOApplicationResourcesMojo extends DefineResourcesMojo {

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

	public DefineWOApplicationResourcesMojo() throws MojoExecutionException {
		super();
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		this.defineProperties();
		this.defineClasspath();
	}

	private void defineProperties() throws MojoExecutionException {
		String fileName = this.getProjectFolder() + "wobuild.properties";
		getLog().info("Defining wo properties: writing to file: " + fileName);
		File file = new File(fileName);
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(file);
			fileWriter.write("maven.localRepository.baseDir = "
					+ localRepository.getBasedir());
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			throw new MojoExecutionException("Could not write classpath", e);
		}
	}

	private void defineClasspath() throws MojoExecutionException {
		getLog().info("Defining wo classpath: dependencies from parameter");
		Iterator dependenciesIterator = dependencies.iterator();
		StringBuffer classPath = new StringBuffer();
		while (dependenciesIterator.hasNext()) {
			Dependency dependency = (Dependency) dependenciesIterator.next();
			String depenendencyGroup = dependency.getGroupId();
			String depenendencyArtifact = dependency.getArtifactId();
			String depenendencyVersion = dependency.getVersion();
			String dependencyPath = depenendencyGroup + "/"
					+ depenendencyArtifact + "/" + depenendencyVersion + "/"
					+ depenendencyArtifact + "-" + depenendencyVersion + ".jar";
			getLog().info(
					"Defining wo classpath: dependencyPath: " + dependencyPath);
			classPath.append(dependencyPath);
		}
		String fileName = this.getProjectFolder() + "classpath.txt";
		getLog().info("Defining wo classpath: writing to file: " + fileName);
		File file = new File(fileName);
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(file);
			fileWriter.write(classPath.toString());
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			throw new MojoExecutionException("Could not write classpath", e);
		}
	}

	public MavenProject getProject() {
		return project;
	}

	//
	//
	// <j:forEach var="artifact" items="${pom.artifacts}">
	//
	// <j:set var="dep" value="${artifact.dependency}"/>
	// <j:set var="depordering"
	// value="${dep.getProperty('woaclasspath.ordering')}"/>
	// <j:set var="depclasspathsystementry"
	// value="${dep.getProperty('woaclasspath.system.entry')}"/>
	// <j:choose>
	// <j:when test="${dep.jar != null}">
	// <j:set var="libname" value="${dep.jar}"/>
	// </j:when>
	// <j:otherwise>
	// <j:set var="libname"
	// value="${dep.artifactId}-${dep.version}.jar"/>
	// </j:otherwise>
	// </j:choose>
	// <j:set var="lib"
	// value="${maven.repo.local}${file.separator}${dep.groupId}${file.separator}${dep.type}s${file.separator}${libname}"/>
	//
	// <j:choose>
	// <j:when test="${depclasspathsystementry == null}">
	// <j:choose>
	// <j:when
	// test="${dep.type == 'jar' || dep.type=='ejb' || empty(dep.type)}">
	// <j:choose>
	// <echo>Including lib: ${dep.artifactId}</echo>
	// <j:when test="${depordering != null}">
	// <j:set var="libwithordering"
	// value="${maven.ordering.dir}${file.separator}${depordering}${libname}"/>
	// <ant:copy file="${lib}"
	// tofile="${libwithordering}"/>
	// <ant:lib file="${libwithordering}">
	// </ant:lib>
	// </j:when>
	// <j:otherwise>
	// <ant:lib file="${lib}">
	// </ant:lib>
	// </j:otherwise>
	// </j:choose>
	// </j:when>
	// <j:otherwise>
	// <echo>Unknwon type set for dependency ${dep.artifatId}</echo>
	// </j:otherwise>
	// </j:choose>
	// </j:when>
	// <j:otherwise>
	// <ant:frameworks root="${wo.wosystemroot}">
	// <include name="${depclasspathsystementry}"/>
	// </ant:frameworks>
	// <!-- <ant:echo message="${wo.wosystemroot}"/>-->
	// <ant:echo message="Including lib: ${depclasspathsystementry}"/>
	// </j:otherwise>
	// </j:choose>
	//
	// </j:forEach>
}
