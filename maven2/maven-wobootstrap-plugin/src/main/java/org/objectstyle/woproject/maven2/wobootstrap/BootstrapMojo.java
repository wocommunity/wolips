/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2006 The ObjectStyle Group,
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne"
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */
package org.objectstyle.woproject.maven2.wobootstrap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.maven.BuildFailureException;
import org.apache.maven.cli.ConsoleDownloadMonitor;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderConsoleLogger;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.embedder.PlexusLoggerAdapter;
import org.apache.maven.lifecycle.LifecycleExecutionException;
import org.apache.maven.monitor.event.DefaultEventMonitor;
import org.apache.maven.monitor.event.EventMonitor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.DuplicateProjectException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.plexus.util.dag.CycleDetectedException;
import org.objectstyle.woproject.maven2.wobootstrap.utils.MacOsWebobjectsLocator;
import org.objectstyle.woproject.maven2.wobootstrap.utils.UnixWebobjectsLocator;
import org.objectstyle.woproject.maven2.wobootstrap.utils.WebobjectsLocator;
import org.objectstyle.woproject.maven2.wobootstrap.utils.WebobjectsUtils;
import org.objectstyle.woproject.maven2.wobootstrap.utils.WindowsWebobjectsLocator;

/**
 * Bootstrap goal for WebObjects projects. Copy all necessary WebObjects jars
 * into local maven repository.
 * 
 * @goal bootstrap
 * @author <a href="mailto:hprange@moleque.com.br">Henrique Prange</a>
 * @since 2.0
 */
public class BootstrapMojo extends AbstractMojo
{
	/**
	 * Maven embedder used to call maven plug-ins functions
	 */
	protected MavenEmbedder embedder;

	/**
	 * An event monitor for the maven embedder
	 */
	protected EventMonitor eventMonitor;

	/**
	 * Properties used during install-install-file execution
	 */
	protected Properties installFileProperties;

	/**
	 * Locator for WebObjects resources
	 */
	protected WebobjectsLocator locator;

	/**
	 * Mapping between default WebObjects jar names and woproject naming
	 * convention
	 */
	private Map namesMap;

	/**
	 * Properties defined in bootstrap.properties file
	 */
	protected final Properties pluginProperties = new Properties();

	/**
	 * The maven project (it's not necessary, but maven embedder doesn't seems
	 * to work without it)
	 */
	protected MavenProject pom;

	/**
	 * The execution root directory for maven embedder
	 */
	protected File targetDirectory;

	/**
	 * Creates a new BootstrapMojo. Verify the OS platform and load the
	 * properties of this plug-ing.
	 * 
	 * @throws MojoExecutionException If the os platform is unsupported
	 */
	public BootstrapMojo() throws MojoExecutionException
	{
		super();

		if( SystemUtils.IS_OS_MAC_OSX )
		{
			locator = new MacOsWebobjectsLocator();
		}
		else if( SystemUtils.IS_OS_WINDOWS )
		{
			locator = new WindowsWebobjectsLocator();
		}
		else if( SystemUtils.IS_OS_UNIX )
		{
			locator = new UnixWebobjectsLocator();
		}
		else
		{
			throw new MojoExecutionException( "Unsupported OS platform." );
		}

		InputStream propertiesInputStream = BootstrapMojo.class.getResourceAsStream( "/bootstrap.properties" );

		try
		{
			pluginProperties.load( propertiesInputStream );
		}
		catch( IOException exception )
		{
			throw new MojoExecutionException( "Cannot load plug-in properties." );
		}

		installFileProperties = new Properties();

		installFileProperties.setProperty( "groupId", pluginProperties.getProperty( "woproject.convention.group" ) );
		installFileProperties.setProperty( "version", WebobjectsUtils.getWebobjectsVersion( locator ) );
		installFileProperties.setProperty( "packaging", "jar" );
	
		installFileProperties.setProperty( "generatePom", "true" );

		initializeEmbedder();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		File[] jars = WebobjectsUtils.getWebobjectsJars( locator );

		if( jars == null )
		{
			throw new MojoExecutionException( "WebObjects lib folder is missing. Maybe WebObjects isn't installed." );
		}

		for( int i = 0; i < jars.length; i++ )
		{
			Properties properties = fillProperties( jars[i] );

			if( !executeInstallFile( properties ) )
			{
				getLog().warn( "Cannot import the following jar: " + jars[i].getName() );
			}
		}
	}

	/**
	 * Executes the install-file goal of maven-install-plugin using a properties
	 * whit the following keys:
	 * <p>
	 * <ul>
	 * <li>groupId</li>
	 * <li>version</li>
	 * <li>packaging</li>
	 * <li>file</li>
	 * <li>artifactId</li>
	 * </ul>
	 * 
	 * @param properties The defined properties
	 * @throws MojoExecutionException If any exception occur during embedder
	 *             execution.
	 */
	protected boolean executeInstallFile( Properties properties ) throws MojoExecutionException
	{
		if( properties == null )
		{
			return false;
		}

		try
		{
			embedder.execute( pom, Collections.singletonList( "install:install-file" ), eventMonitor, new ConsoleDownloadMonitor(), properties, targetDirectory );
		}
		catch( CycleDetectedException exception )
		{
			throw new MojoExecutionException( "Error: cycle detected." );
		}
		catch( LifecycleExecutionException exception )
		{
			throw new MojoExecutionException( "Error in lifecycle execution." );
		}
		catch( BuildFailureException exception )
		{
			throw new MojoExecutionException( "Error: build failure." );
		}
		catch( DuplicateProjectException exception )
		{
			throw new MojoExecutionException( "Error: duplicate project." );
		}

		return true;
	}

	/**
	 * Fill the contents of a properties based on a JAR file.
	 * 
	 * @param jar The JAR file
	 * @return Returns the populated properties or <code>null</code> if cannot
	 *         map the JAR file
	 */
	protected Properties fillProperties( File jar )
	{
		String artifactId = getArtifactIdForJar( jar );

		if( artifactId == null )
		{
			return null;
		}

		installFileProperties.setProperty( "file", jar.getAbsolutePath() );
		installFileProperties.setProperty( "artifactId", artifactId );

		return installFileProperties;
	}

	/**
	 * Resolve the artifactId for a specific JAR file.
	 * 
	 * @param jar The JAR file
	 * @return Returns the artifatId or <code>null</code> if cannot find a key
	 *         that match the JAR file
	 */
	protected String getArtifactIdForJar( File jar )
	{
		if( jar == null )
		{
			return null;
		}

		loadNamesMap();

		String jarName = FilenameUtils.getBaseName( jar.getAbsolutePath() );

		return (String) namesMap.get( jarName.toLowerCase() );
	}

	private void initializeEmbedder() throws MojoExecutionException
	{
		embedder = new MavenEmbedder();

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		embedder.setClassLoader( classLoader );
		embedder.setLogger( new MavenEmbedderConsoleLogger() );

		try
		{
			embedder.start();
		}
		catch( MavenEmbedderException exception )
		{
			throw new MojoExecutionException( "Cannot start maven embedder." );
		}

		targetDirectory = SystemUtils.getUserDir();

		File pomFile = new File( targetDirectory, "pom.xml" );

		try
		{
			pom = embedder.readProject( pomFile );
		}
		catch( ProjectBuildingException exception )
		{
			throw new MojoExecutionException( "Cannot read project POM." );
		}

		eventMonitor = new DefaultEventMonitor( new PlexusLoggerAdapter( new MavenEmbedderConsoleLogger() ) );
	}
	
	private void loadNamesMap()
	{
		if( namesMap != null )
		{
			return;
		}

		String defaultNames = pluginProperties.getProperty( "webobjects.default.names" );

		String originalNames[] = StringUtils.split( defaultNames, "," );

		String conventionedNames = pluginProperties.getProperty( "woproject.convention.names" );

		String artifactIds[] = StringUtils.split( conventionedNames, "," );

		namesMap = new HashMap();

		for( int i = 0; i < originalNames.length; i++ )
		{
			namesMap.put( originalNames[i].toLowerCase(), artifactIds[i] );
		}
	}
}
