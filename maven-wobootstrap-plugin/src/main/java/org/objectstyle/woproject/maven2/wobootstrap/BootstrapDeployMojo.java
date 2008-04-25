/*
 * ==================================================================== The
 * ObjectStyle Group Software License, Version 1.0 Copyright (c) 2006 The
 * ObjectStyle Group, and individual authors of the software. All rights
 * reserved. Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowlegement: "This product includes software developed by the ObjectStyle
 * Group (http://objectstyle.org/)." Alternately, this acknowlegement may appear
 * in the software itself, if and wherever such third-party acknowlegements
 * normally appear. 4. The names "ObjectStyle Group" and "Cayenne" must not be
 * used to endorse or promote products derived from this software without prior
 * written permission. For written permission, please contact
 * andrus@objectstyle.org. 5. Products derived from this software may not be
 * called "ObjectStyle" nor may "ObjectStyle" appear in their names without
 * prior written permission of the ObjectStyle Group. THIS SOFTWARE IS PROVIDED
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR ITS
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ==================================================================== This
 * software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 */
package org.objectstyle.woproject.maven2.wobootstrap;

import java.io.File;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.deployer.ArtifactDeployer;
import org.apache.maven.artifact.deployer.ArtifactDeploymentException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.plugin.MojoExecutionException;
import org.objectstyle.woproject.maven2.wobootstrap.locator.WebObjectsLocator;

/**
 * This subclass of <code>AbstractBootstrapMojo</code> allows the deployment
 * of WebObjects artifacts into remote repositories. It is similar to mvn
 * deploy:deploy-file goal.
 * 
 * @goal deploy
 * @requiresProject false
 * @author <a href="mailto:hprange@moleque.com.br">Henrique Prange</a>
 */
public class BootstrapDeployMojo extends AbstractBootstrapMojo {
	/**
	 * The artifact deployer used to deploy the WebObjects artifact.
	 * 
	 * @component
	 * @required
	 */
	private ArtifactDeployer deployer;

	/**
	 * Component used to create a repository
	 * 
	 * @component
	 */
	private ArtifactRepositoryFactory repositoryFactory;

	/**
	 * Server Id to map on the &lt;id&gt; under &lt;server&gt; section of
	 * settings.xml In most cases, this parameter will be required for
	 * authentication
	 * <p>
	 * Description copied from maven-deploy-plugin
	 * 
	 * @parameter expression="${repositoryId}" default-value="remote-repository"
	 * @required
	 */
	private String repositoryId;

	/**
	 * Map that contains the layouts
	 * 
	 * @component role="org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout"
	 */
	private Map repositoryLayouts;

	/**
	 * URL where the artifact will be deployed. <br/> ie ( file://C:\\m2-repo or
	 * scp://host.com/path/to/repo )
	 * <p>
	 * Description copied from maven-deploy-plugin
	 * 
	 * @parameter expression="${url}"
	 * @required
	 */
	private String url;

	/**
	 * @see AbstractBootstrapMojo#AbstractBootstrapMojo()
	 */
	public BootstrapDeployMojo() throws MojoExecutionException {
		super();
	}

	/**
	 * @see AbstractBootstrapMojo#AbstractBootstrapMojo(WebObjectsLocator)
	 */
	BootstrapDeployMojo(WebObjectsLocator locator) throws MojoExecutionException {
		super(locator);
	}

	/**
	 * This method allows the deployment of the given artifact into a remote
	 * Maven repository.
	 * 
	 * @see AbstractBootstrapMojo#executeGoal(File, Artifact)
	 */
	@Override
	protected void executeGoal(File file, Artifact artifact) throws MojoExecutionException {
		ArtifactRepositoryLayout layout;

		layout = (ArtifactRepositoryLayout) repositoryLayouts.get("default");

		ArtifactRepository deploymentRepository = repositoryFactory.createDeploymentArtifactRepository(repositoryId, url, layout, false);

		String protocol = deploymentRepository.getProtocol();

		if (protocol == null || "".equals(protocol)) {
			throw new MojoExecutionException("No transfer protocol found.");
		}

		try {
			deployer.deploy(file, artifact, deploymentRepository, localRepository);
		} catch (ArtifactDeploymentException exception) {
			throw new MojoExecutionException("Error while trying to deploy the artifact.", exception);
		}
	}
}
