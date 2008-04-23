package org.objectstyle.woproject.maven2.wobootstrap;

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.installer.ArtifactInstallationException;
import org.apache.maven.artifact.installer.ArtifactInstaller;
import org.apache.maven.plugin.MojoExecutionException;
import org.objectstyle.woproject.maven2.wobootstrap.locator.WebObjectsLocator;

/**
 * This subclass of <code>AbstractBootstrapMojo</code> installs the WebObjects
 * artifacts into the local repository. It is similar to mvn
 * install:install-file goal.
 * 
 * @goal install
 * @requiresProject false
 * @author <a href="mailto:hprange@moleque.com.br">Henrique Prange</a>
 */
public class BootstrapInstallMojo extends AbstractBootstrapMojo
{
	/**
	 * Component used to install an artifact into a local repository.
	 * 
	 * @component
	 */
	private ArtifactInstaller installer;

	/**
	 * @see AbstractBootstrapMojo#AbstractBootstrapMojo()
	 */
	public BootstrapInstallMojo() throws MojoExecutionException
	{
		super();
	}

	/**
	 * @see AbstractBootstrapMojo#AbstractBootstrapMojo(WebObjectsLocator)
	 */
	BootstrapInstallMojo( WebObjectsLocator locator ) throws MojoExecutionException
	{
		super( locator );
	}

	/**
	 * This method installs the given artifact into the local Maven repository.
	 * 
	 * @see AbstractBootstrapMojo#executeGoal(File, Artifact)
	 */
	@Override
	protected void executeGoal( File file, Artifact artifact ) throws MojoExecutionException
	{
		try
		{
			installer.install( file, artifact, localRepository );
		}
		catch( ArtifactInstallationException exception )
		{
			throw new MojoExecutionException( "Error while trying to install the artifact", exception );
		}
	}
}
