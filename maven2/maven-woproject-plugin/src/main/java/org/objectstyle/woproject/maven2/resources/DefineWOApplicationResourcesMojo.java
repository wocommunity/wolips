package org.objectstyle.woproject.maven2.resources;

//org.apache.maven.plugins:maven-compiler-plugin:compile
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * resources goal for WebObjects projects.
 * 
 * @goal define-woapplication-resources
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
	 * The maven repo.
	 * 
	 * @parameter expression="${maven.repo.local}"
	 * @required
	 * @readonly
	 */
	private String mavenRepoLocal;

	public DefineWOApplicationResourcesMojo() throws MojoExecutionException {
		super();
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		this.defineClasspath();
	}

	private void defineClasspath() {
		getLog().info("Defining wo classpath: dependencies");
		List dependencies = this.getProject().getDependencies();
		Iterator dependenciesIterator = dependencies.iterator();
		while (dependenciesIterator.hasNext()) {
			Dependency dependency = (Dependency) dependenciesIterator.next();
			String depenendencyGroup = dependency.getGroupId();
			String depenendencyArtifact = dependency.getArtifactId();
			String depenendencyVersion = dependency.getVersion();
			String dependencyPath = mavenRepoLocal + "/" + depenendencyGroup
					+ "/" + depenendencyArtifact + "/" + depenendencyVersion
					+ "/" + depenendencyArtifact + "-" + depenendencyVersion
					+ ".jar";
			getLog().info(
					"Defining wo classpath: dependencyPath: " + dependencyPath);
		}
		getLog().info("Defining wo classpath: dependencies trail");
		Set set = this.getProject().getArtifacts();
		Iterator artifactsIterator = set.iterator();
		while (artifactsIterator.hasNext()) {
			Artifact artifact = (Artifact) artifactsIterator.next();
			dependenciesIterator = artifact.getDependencyTrail().iterator();
			while (dependenciesIterator.hasNext()) {
				Dependency dependency = (Dependency) dependenciesIterator
						.next();
				String depenendencyGroup = dependency.getGroupId();
				String depenendencyArtifact = dependency.getArtifactId();
				String depenendencyVersion = dependency.getVersion();
				String dependencyPath = mavenRepoLocal + "/"
						+ depenendencyGroup + "/" + depenendencyArtifact + "/"
						+ depenendencyVersion + "/" + depenendencyArtifact
						+ "-" + depenendencyVersion + ".jar";
				getLog().info(
						"Defining wo classpath: dependencyPath: "
								+ dependencyPath);
			}
		}
		getLog().info("Defining wo classpath: artifacts");
		set = this.getProject().getArtifacts();
		artifactsIterator = set.iterator();
		while (artifactsIterator.hasNext()) {
			Artifact artifact = (Artifact) artifactsIterator.next();
			String depenendencyGroup = artifact.getGroupId();
			String depenendencyArtifact = artifact.getArtifactId();
			String depenendencyVersion = artifact.getVersion();
			String dependencyPath = mavenRepoLocal + "/" + depenendencyGroup
					+ "/" + depenendencyArtifact + "/" + depenendencyVersion
					+ "/" + depenendencyArtifact + "-" + depenendencyVersion
					+ ".jar";
			getLog().info(
					"Defining wo classpath: dependencyPath: " + dependencyPath);
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
