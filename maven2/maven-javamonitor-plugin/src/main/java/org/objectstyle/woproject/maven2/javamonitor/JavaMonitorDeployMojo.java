package org.objectstyle.woproject.maven2.javamonitor;

/*
 * ====================================================================
 * The ObjectStyle Group Software License, Version 1.0 Copyright (c) 2006 The
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
 * ====================================================================
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 */

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.manager.WagonConfigurationException;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.model.Site;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.wagon.CommandExecutionException;
import org.apache.maven.wagon.CommandExecutor;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.UnsupportedProtocolException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.observers.Debug;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.apache.maven.wagon.repository.Repository;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * Goal which deploy WebObjects Application packages (WOAs) into the JavaMonitor
 * tool.
 *
 * @goal deploy
 * @execute phase="package"
 * @requiresDirectInvocation
 * @author <a href="mailto:hprange@gmail.com">Henrique Prange</a>
 */
public class JavaMonitorDeployMojo extends AbstractMojo implements
		Contextualizable {
	/**
	 * Configure the Wagon with the information from serverConfigurationMap (
	 * which comes from settings.xml )
	 *
	 * @todo Remove when {@link WagonManager#getWagon(Repository) is available}.
	 *       It's available in Maven 2.0.5.
	 * @param wagon
	 * @param repositoryId
	 * @param settings
	 * @param container
	 * @param log
	 * @throws WagonConfigurationException
	 */
	static void configureWagon(Wagon wagon, String repositoryId,
			Settings settings, PlexusContainer container, Log log)
			throws WagonConfigurationException {
		// MSITE-25: Make sure that the server settings are inserted
		for (int i = 0; i < settings.getServers().size(); i++) {
			Server server = (Server) settings.getServers().get(i);
			String id = server.getId();
			if (id != null && id.equals(repositoryId)) {
				if (server.getConfiguration() != null) {
					final PlexusConfiguration plexusConf = new XmlPlexusConfiguration(
							(Xpp3Dom) server.getConfiguration());

					ComponentConfigurator componentConfigurator = null;
					try {
						componentConfigurator = (ComponentConfigurator) container
								.lookup(ComponentConfigurator.ROLE);
						componentConfigurator.configureComponent(wagon,
								plexusConf, container.getContainerRealm());
					} catch (final ComponentLookupException e) {
						throw new WagonConfigurationException(
								repositoryId,
								"Unable to lookup wagon configurator."
										+ " Wagon configuration cannot be applied.",
								e);
					} catch (ComponentConfigurationException e) {
						throw new WagonConfigurationException(repositoryId,
								"Unable to apply wagon configuration.", e);
					} finally {
						if (componentConfigurator != null) {
							try {
								container.release(componentConfigurator);
							} catch (ComponentLifecycleException e) {
								log
										.error("Problem releasing configurator - ignoring: "
												+ e.getMessage());
							}
						}
					}

				}

			}
		}
	}

	/**
	 * <p>
	 * Get the <code>ProxyInfo</code> of the proxy associated with the
	 * <code>host</code> and the <code>protocol</code> of the given
	 * <code>repository</code>.
	 * </p>
	 * <p>
	 * Extract from <a
	 * href="http://java.sun.com/j2se/1.5.0/docs/guide/net/properties.html">
	 * J2SE Doc : Networking Properties - nonProxyHosts</a> : "The value can be
	 * a list of hosts, each separated by a |, and in addition a wildcard
	 * character (*) can be used for matching"
	 * </p>
	 * <p>
	 * Defensively support for comma (",") and semi colon (";") in addition to
	 * pipe ("|") as separator.
	 * </p>
	 *
	 * @return a ProxyInfo object instantiated or <code>null</code> if no
	 *         matching proxy is found
	 */
	public static ProxyInfo getProxyInfo(Repository repository,
			WagonManager wagonManager) {
		ProxyInfo proxyInfo = wagonManager.getProxy(repository.getProtocol());

		if (proxyInfo == null) {
			return null;
		}

		String host = repository.getHost();
		String nonProxyHostsAsString = proxyInfo.getNonProxyHosts();
		String[] nonProxyHosts = StringUtils
				.split(nonProxyHostsAsString, ",;|");
		for (int i = 0; i < nonProxyHosts.length; i++) {
			String nonProxyHost = nonProxyHosts[i];
			if (StringUtils.contains(nonProxyHost, "*")) {
				// Handle wildcard at the end, beginning or middle of the
				// nonProxyHost
				String nonProxyHostPrefix = StringUtils.substringBefore(
						nonProxyHost, "*");
				String nonProxyHostSuffix = StringUtils.substringAfter(
						nonProxyHost, "*");
				// prefix*
				if (StringUtils.isNotEmpty(nonProxyHostPrefix)
						&& host.startsWith(nonProxyHostPrefix)
						&& StringUtils.isEmpty(nonProxyHostSuffix)) {
					return null;
				}
				// *suffix
				if (StringUtils.isEmpty(nonProxyHostPrefix)
						&& StringUtils.isNotEmpty(nonProxyHostSuffix)
						&& host.endsWith(nonProxyHostSuffix)) {
					return null;
				}
				// prefix*suffix
				if (StringUtils.isNotEmpty(nonProxyHostPrefix)
						&& host.startsWith(nonProxyHostPrefix)
						&& StringUtils.isNotEmpty(nonProxyHostSuffix)
						&& host.endsWith(nonProxyHostSuffix)) {
					return null;
				}
			} else if (host.equals(nonProxyHost)) {
				return null;
			}
		}
		return proxyInfo;
	}

	private PlexusContainer container;

	/**
	 * The maven project.
	 *
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * @parameter expression="${server}"
	 */
	private String server;

	/**
	 * The current user system settings for use in Maven.
	 *
	 * @parameter expression="${settings}"
	 * @required
	 * @readonly
	 */
	private Settings settings;

	/**
	 * The WOA package will be deployed to this URL.
	 *
	 * If you don't specify this, the default-value will be
	 * "file:///Library/WebObjects/Applications/${project.build.finalName}.woa".
	 *
	 * @parameter expression="${url}" default-value=
	 *            "file:///Library/WebObjects/Applications/${project.build.finalName}.woa"
	 * @required
	 */
	private String url;

	/**
	 * @component
	 */
	private WagonManager wagonManager;

	/**
	 * @parameter expression="${woaDirectory}"
	 */
	private String woaDirectory;

	/**
	 * @parameter expression="${project.build.finalName}"
	 * @readonly
	 * @required
	 */
	private String finalName;

	public void contextualize(Context context) throws ContextException {
		container = (PlexusContainer) context.get(PlexusConstants.PLEXUS_KEY);
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Using this URL for application deployment: " + url);
		getLog().info("Application package: " + woaDirectory);

		Repository repository = new Repository(server, url);

		Wagon wagon;
		try {
			wagon = wagonManager.getWagon(repository);
			//configureWagon(wagon, server, settings, container, getLog());
		} catch (UnsupportedProtocolException e) {
			throw new MojoExecutionException("Unsupported protocol: '"
					+ repository.getProtocol() + "'", e);
		} catch (WagonConfigurationException e) {
			throw new MojoExecutionException("Unable to configure Wagon: '"
					+ repository.getProtocol() + "'", e);
		}

		if (!wagon.supportsDirectoryCopy()) {
			throw new MojoExecutionException("Wagon protocol '"
					+ repository.getProtocol()
					+ "' doesn't support directory copying");
		}

		try {
			Debug debug = new Debug();

			wagon.addSessionListener(debug);

			wagon.addTransferListener(debug);

			ProxyInfo proxyInfo = getProxyInfo(repository, wagonManager);
			if (proxyInfo != null) {
				wagon.connect(repository, wagonManager
						.getAuthenticationInfo(server), proxyInfo);
			} else {
				wagon.connect(repository, wagonManager
						.getAuthenticationInfo(server));
			}

			wagon.putDirectory(new File(woaDirectory), "./" + finalName + ".woa");

			// TODO: current wagon uses zip which will use the umask on remote
			// host instead of honouring our settings
			// Force group writeable
			if (wagon instanceof CommandExecutor) {
				CommandExecutor exec = (CommandExecutor) wagon;
				exec.executeCommand("chmod -Rf g+w,a+rX "
						+ repository.getBasedir());
			}
		} catch (ResourceDoesNotExistException e) {
			throw new MojoExecutionException("Error uploading site", e);
		} catch (TransferFailedException e) {
			throw new MojoExecutionException("Error uploading site", e);
		} catch (AuthorizationException e) {
			throw new MojoExecutionException("Error uploading site", e);
		} catch (ConnectionException e) {
			throw new MojoExecutionException("Error uploading site", e);
		} catch (AuthenticationException e) {
			throw new MojoExecutionException("Error uploading site", e);
		} catch (CommandExecutionException e) {
			throw new MojoExecutionException("Error uploading site", e);
		} finally {
			try {
				wagon.disconnect();
			} catch (ConnectionException e) {
				getLog().error("Error disconnecting wagon - ignored", e);
			}
		}
	}

	/**
	 * Generates the site structure using the project hiearchy (project and its
	 * modules) or using the distributionManagement elements from the pom.xml.
	 *
	 * @param project
	 * @param ignoreMissingSiteUrl
	 * @return the structure relative path
	 * @throws MojoFailureException
	 *             if any
	 */
	protected static String getStructure(MavenProject project,
			boolean ignoreMissingSiteUrl) throws MojoFailureException {
		if (project.getDistributionManagement() == null) {
			String hierarchy = project.getArtifactId();

			MavenProject parent = project.getParent();
			while (parent != null) {
				hierarchy = parent.getArtifactId() + "/" + hierarchy;
				parent = parent.getParent();
			}

			return hierarchy;
		}

		Site site = project.getDistributionManagement().getSite();
		if (site == null) {
			if (!ignoreMissingSiteUrl) {
				throw new MojoFailureException(
						"Missing site information in the distribution management element in the project: '"
								+ project.getName() + "'.");
			}

			return null;
		}

		if (StringUtils.isEmpty(site.getUrl())) {
			if (!ignoreMissingSiteUrl) {
				throw new MojoFailureException(
						"The URL in the site is missing in the project descriptor.");
			}

			return null;
		}

		Repository repository = new Repository(site.getId(), site.getUrl());
		StringBuffer hierarchy = new StringBuffer(1024);
		hierarchy.append(repository.getHost());
		if (!StringUtils.isEmpty(repository.getBasedir())) {
			if (!repository.getBasedir().startsWith("/")) {
				hierarchy.append('/');
			}
			hierarchy.append(repository.getBasedir());
		}

		return hierarchy.toString().replaceAll("[\\:\\?\\*]", "");
	}

}
