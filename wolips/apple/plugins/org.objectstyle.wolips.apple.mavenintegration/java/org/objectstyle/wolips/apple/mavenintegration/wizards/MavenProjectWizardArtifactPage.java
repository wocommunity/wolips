/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2004 The ObjectStyle Group,
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
/*Portions of this code are Copyright Apple Inc. 2008 and licensed under the
ObjectStyle Group Software License, version 1.0.  This license from Apple
applies solely to the actual code contributed by Apple and to no other code.
No other license or rights are granted by Apple, explicitly, by implication,
by estoppel, or otherwise.  All rights reserved.*/
package org.objectstyle.wolips.apple.mavenintegration.wizards;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.License;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.objectstyle.wolips.wizards.PackageSpecifierWizardPage;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * MavenProjectWizardArtifactPage
 */
public class MavenProjectWizardArtifactPage extends WizardPage {
	/**  0.0.1-SNAPSHOT */
	public static final String DEFAULT_VERSION = "0.0.1-SNAPSHOT";
	/**woa goal*/
	public static final String DEFAULT_WOAPPLICATION_PACKAGING = "woa";
	/**woframework goal*/
	public static final String DEFAULT_WOFRAMEWORK_PACKAGING = "framework";
	/** */
	public static final String DEFAULT_WEBOBJECTS_VERSION = "5.4.2-SNAPSHOT";

	/**4.0.0*/
	public static final String DEFAULT_POM_VERSION = "4.0.0";

	/**jar by default*/
	public static final String DEFAULT_POM_PACKAGING = "jar";

	/** Supported packaging types*/
	public static final String[] supportedPackaging = new String[] {DEFAULT_WOAPPLICATION_PACKAGING,"legacy woa", "war", "jar"};

	/***/
	//FIXME: should be dynamically fetched
	public static final String[] offlineWebobjectsVersions = new String[] {"5.4.2-SNAPSHOT", "5.5-SNAPSHOT","6.0-SNAPSHOT"};

	/***/
	public static final String GIDKEY = "gid";
	/***/
	public static final String ARTIDKEY = "artid";
	/***/
	public static final String WOVERSIONKEY = "woversion";
	/***/
	public static final String PACKAGINGKEY = "packaging";
	/***/
	public static final String VERSIONKEY = "version";
	/***/
	public static final String DESCRIPTIONKEY = "description";

	private Text _groupIdText;
	private Text _artifactIdText;
	private Text _versionText;
	private Text _descriptionText;
	private Combo _packagingCombo;
	private Combo _webobjectsVersionCombo;
	private HashMap<String, String> _currentSettings;

	/** Component which allows to choose which Maven2 directories to create. */
	private WOMavenDirectoriesComponent directoriesComponent;

	protected MavenProjectWizardArtifactPage(String pageName) {
		super(pageName);
		_currentSettings = new HashMap<String, String>();
		this.setVersion(DEFAULT_VERSION);
		this.setGroupId(getSpecifiedPackageName());
		this.setArtifactId(getSpecifiedProjectName());
		this.setWebObjectsVersion(getWebObjectsVersion());
	}

	private static boolean showArtifactDirGroup = false;

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 2;
		composite.setLayout(layout);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);

		Group artifactGroup = new Group(composite, SWT.NONE);
		artifactGroup.setText("Artifact");
		GridData gd_artifactGroup = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		gd_artifactGroup.minimumHeight = 200;
		artifactGroup.setLayoutData(gd_artifactGroup);
		artifactGroup.setLayout(new GridLayout(2, false));

		Label groupIdlabel = new Label(artifactGroup, SWT.NONE);
		groupIdlabel.setText("Group Id:");

		_groupIdText = new Text(artifactGroup, SWT.BORDER);
		_groupIdText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		_groupIdText.setText(getSpecifiedPackageName());  //by default suggest the main package name

		Label artifactIdLabel = new Label(artifactGroup, SWT.NONE);
		artifactIdLabel.setText("Artifact Id:");

		_artifactIdText = new Text(artifactGroup, SWT.BORDER);
		_artifactIdText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		_artifactIdText.setText(getSpecifiedProjectName());  //by default suggest the project name

		Label versionLabel = new Label(artifactGroup, SWT.NONE);
		versionLabel.setText("Version:");

		_versionText = new Text(artifactGroup, SWT.BORDER);
		_versionText.setLayoutData(new GridData(150, SWT.DEFAULT));
		_versionText.setText(DEFAULT_VERSION);

		Label webobjectsVersionLabel = new Label(artifactGroup, SWT.NONE);
		webobjectsVersionLabel.setText("WebObjects Version:");

		_webobjectsVersionCombo = new Combo(artifactGroup, SWT.DROP_DOWN);
		_webobjectsVersionCombo.setItems(offlineWebobjectsVersions);  //FIXME: dynamic fetch this
		_webobjectsVersionCombo.select(0);
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		_webobjectsVersionCombo.setLayoutData(gd);

		Label packagingLabel = new Label(artifactGroup, SWT.NONE);
		packagingLabel.setText("Packaging:");

		_packagingCombo = new Combo(artifactGroup, SWT.DROP_DOWN);
		_packagingCombo.setItems(supportedPackaging);
		_packagingCombo.select(0);
		GridData gd2 = new GridData();
		gd2.horizontalAlignment = GridData.FILL;
		_packagingCombo.setLayoutData(gd2);

		Label descriptionLabel = new Label(artifactGroup, SWT.NONE);
		descriptionLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		descriptionLabel.setText("Description:");

		_descriptionText = new Text(artifactGroup, SWT.BORDER);
		GridData gd_descriptionText = new GridData(SWT.FILL, SWT.FILL, false, true);
		_descriptionText.setLayoutData(gd_descriptionText);


		// directory structure
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		directoriesComponent = new WOMavenDirectoriesComponent(composite,
				SWT.NONE);
		directoriesComponent.setVisible(false);
		if (showArtifactDirGroup) {
			directoriesComponent.setVisible(true);
			directoriesComponent.setLayoutData(gridData);
		}

		//default values
		setPackaging(DEFAULT_WOAPPLICATION_PACKAGING);
		setVersion(DEFAULT_VERSION);
		setWebObjectsVersion(offlineWebobjectsVersions[0]);

		addListeners();

		setControl(composite);

	}

	protected void addListeners() {
		ModifyListener modifyingListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validate(e);
			}
		};

		_artifactIdText.addModifyListener(modifyingListener);
		_descriptionText.addModifyListener(modifyingListener);
		_versionText.addModifyListener(modifyingListener);
		_groupIdText.addModifyListener(modifyingListener);
		_webobjectsVersionCombo.addModifyListener(modifyingListener);
		_packagingCombo.addModifyListener(modifyingListener);
	}

	/**
	 * @return directories
	 */
	public String[] getDirectoryPaths() {
		WOMavenDirectory[] mavenDirectories = directoriesComponent.getDirectories();
		String[] directories = new String[mavenDirectories.length];
		for(int i = 0; i < directories.length; i++ ) {
			directories[i] = mavenDirectories[i].getPath();
		}

		return directories;
	}

	//lazy match
	/**
	 * @param path
	 * @return true if path is selected
	 */
	public boolean pathIsEnabled(String path) {
		WOMavenDirectory[] mavenDirectories = directoriesComponent.getDirectories();
		for(WOMavenDirectory aDir : mavenDirectories) {
			if (aDir.getPath() != null && aDir.getPath().contains(path)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			_artifactIdText.setText(getSpecifiedProjectName());  //by default suggest the project name
			_groupIdText.setText(getSpecifiedPackageName());  //by default suggest the main package name
//			updateWebObjectsVersions();
		}
	}

	/**
	 * Scroll back through wizard pages to get package name
	 * @return package name or empty string if not found
	 */
	protected String getSpecifiedPackageName() {
		IWizardPage aPage = this.getPreviousPage();
		String packageName = "com.mycompany";
		while (aPage != null) {
			if (aPage instanceof PackageSpecifierWizardPage) {
				packageName = ((PackageSpecifierWizardPage)aPage).getPackageName();
				break;
			}
			aPage = aPage.getPreviousPage();
		}

		return packageName;
	}

	protected String getPrincipalClassName() {
		IWizardPage aPage = this.getPreviousPage();
		String principalClassName = "com.mycompany.Application";
		while (aPage != null) {
			if (aPage instanceof PackageSpecifierWizardPage) {
				principalClassName = ((PackageSpecifierWizardPage)aPage).getPackageName()+".Application";
				break;
			}
			aPage = aPage.getPreviousPage();
		}

		return principalClassName;
	}


	/**
	 * Scroll back through wizard pages to get package name
	 * @return package name or empty string if not found
	 */
	protected String getSpecifiedProjectName() {
		IWizardPage aPage = this.getPreviousPage();
		String projectName = "myProject";
		while (aPage != null) {
			if (aPage instanceof WizardNewProjectCreationPage) {
				projectName = ((WizardNewProjectCreationPage)aPage).getProjectName();
				break;
			}
			aPage = aPage.getPreviousPage();
		}

		return projectName;
	}

	/**
	 * @return artifact id
	 */
	public String getArtifactId() {
		return _currentSettings.get(ARTIDKEY);
	}

	/**
	 * @return gid
	 */
	public String getGroupId() {
		return _currentSettings.get(GIDKEY);
	}

	/**
	 * @return version
	 */
	public String getVersion() {
		return _currentSettings.get(VERSIONKEY);
	}

	/**
	 * @return version
	 */
	public String getWebObjectsVersion() {
		return _currentSettings.get(WOVERSIONKEY);
	}

	/**
	 * @return package selection
	 */
	public String getPackaging() {
		return _currentSettings.get(PACKAGINGKEY);
	}

	/**
	 * @return artifact description
	 */
	public String getArtifactDescription() {
		return _currentSettings.get(DESCRIPTIONKEY);
	}

	/**
	 * @param groupId
	 */
	public void setGroupId( String groupId) {
		_currentSettings.put(GIDKEY, groupId);
	}

	/**
	 * @param artifactId
	 */
	public void setArtifactId( String artifactId) {
		_currentSettings.put(ARTIDKEY, artifactId);
	}

	/**
	 * @param version
	 */
	public void setVersion( String version) {
		_currentSettings.put(VERSIONKEY, version);
	}

	/**
	 * @param version
	 */
	public void setWebObjectsVersion( String version) {
		_currentSettings.put(WOVERSIONKEY, version);
	}

	/**
	 * @param packaging
	 */
	public void setPackaging( String packaging) {
		_currentSettings.put(PACKAGINGKEY, packaging);
	}

	/**
	 * @param description
	 */
	public void setArtifactDescription( String description) {
		_currentSettings.put(DESCRIPTIONKEY, description);
	}

	/**
	 * @param e
	 */
	public void validate(ModifyEvent e) {
		if (e.getSource() != null) {
			if (e.getSource().equals(_groupIdText)) {
				setGroupId(_groupIdText.getText());
			} else if (e.getSource().equals(_artifactIdText)) {
				setArtifactId(_artifactIdText.getText());
			} else if (e.getSource().equals(_packagingCombo)) {
				setPackaging(_packagingCombo.getText());
			} else if (e.getSource().equals(_versionText)) {
				setVersion(_versionText.getText());
			} else if (e.getSource().equals(_descriptionText)) {
				setArtifactDescription(_descriptionText.getText());
			} else if (e.getSource().equals(_webobjectsVersionCombo)) {
				setWebObjectsVersion(_webobjectsVersionCombo.getText());
				if (_webobjectsVersionCombo.getText().endsWith("SNAPSHOT")) {
					//display pre release software warning dialog here
					MessageDialog.openWarning(getShell(), "Pre Release Software Warning", "Warning: PreÐrelease software is Apple confidential information. Your unauthorized distribution of preÐrelease software or disclosure of information relating to preÐrelease software may subject you to both civil and criminal liability and result in immediate termination of your ADC Membership. See the ADC Prototype License and Confidentiality Agreement for details.");
				}
			} else {
				System.out.println("UnKnown event: "+e);
			}
		} else {
			System.out.println("UnKnown event: "+e);
		}
	}

	/**
	 * @param path to where model is
	 * @return maven model
	 */
	public MavenProject getProjectModel(String path) {
		MavenProject proj = new MavenProject();
		Properties properties = defaultProperties();
		properties.setProperty("principalClass", getPrincipalClassName());
		properties.setProperty("woversion", getWebObjectsVersion());
		proj.getModel().setProperties(properties);
		proj.setFile(new File(path));
		proj.setArtifactId(getArtifactId()); //should this be same as artifactID be same as eclipse project name?
		proj.setName(getArtifactId());
		proj.setGroupId(getGroupId());
		proj.setVersion(getVersion());
		proj.setModelVersion(DEFAULT_POM_VERSION);
		proj.setLicenses(defaultLicenses());
		proj.setPackaging(DEFAULT_POM_PACKAGING);
		proj.setDependencies(defaultDependencies("${woversion}"));
		proj.setBuild(defaultBuild());
		proj.setReporting(defaultReporting());

		return proj;
	}

	protected static List<Dependency> defaultDependencies(String version) {
		ArrayList<Dependency> list = new ArrayList<Dependency>();
		String aVersion = version;
		String groupID = "com.webobjects";
		if (aVersion == null || aVersion.length() < 0) {
			aVersion = "6.0-SNAPSHOT";
		}
		list.add(createDependency("JavaXML",groupID, aVersion));
		list.add(createDependency("JavaFoundation",groupID, aVersion));
		list.add(createDependency("JavaWebObjects",groupID, aVersion));
		list.add(createDependency("JavaWOExtensions",groupID, aVersion));
		list.add(createDependency("JavaEOControl",groupID, aVersion));
		list.add(createDependency("JavaEOAccess",groupID, aVersion));
		list.add(createDependency("JavaJDBCAdaptor",groupID, aVersion));
		list.add(createDependency("junit","junit", "4.4"));

		return list;
	}

	/**
	 * @param artifactID
	 * @param groupID
	 * @param version
	 * @return Dependency
	 */
	protected static Dependency createDependency(String artifactID, String groupID, String version) {
		Dependency dep = new Dependency();
		dep.setArtifactId(artifactID);
		dep.setGroupId(groupID);
		dep.setVersion(version);

		return dep;
	}

	protected static Build defaultBuild() {
		Build build = new Build();
		Plugin aPlugin = new Plugin();
		build.setOutputDirectory("${basedir}/target/classes");

		//maven java builder plugin
		aPlugin.setGroupId("org.apache.maven.plugins");
		aPlugin.setArtifactId("maven-compiler-plugin");
		Xpp3Dom configuration = new Xpp3Dom("configuration");
		Xpp3Dom source = new Xpp3Dom("source");
		source.setValue("1.5");
		Xpp3Dom target = new Xpp3Dom("target");
		target.setValue("1.5");
		configuration.addChild(source);
		configuration.addChild(target);
		aPlugin.setConfiguration(configuration);
		build.addPlugin(aPlugin);

		//apple plugin
		aPlugin = new Plugin();
		aPlugin.setGroupId("com.webobjects");
		aPlugin.setArtifactId("maven-apple-plugin");
		//FIXME: bad, we shouldn't have to set the version and mvn should fetch latest
		//dynamic update is currently not working for some reason
		aPlugin.setVersion("1.0-SNAPSHOT");
		PluginExecution pe = new PluginExecution();
		pe.setPhase("package");
		pe.addGoal("woa");  //this should be dynamic
		aPlugin.addExecution(pe);
		build.addPlugin(aPlugin);

		//assembly plugin
		aPlugin = new Plugin();
		aPlugin.setArtifactId("maven-assembly-plugin");
		pe = new PluginExecution();
		pe.setId("assembly");
		pe.setPhase("package");
		pe.addGoal("assembly");
		aPlugin.addExecution(pe);
		configuration = new Xpp3Dom("configuration");
		Xpp3Dom tarLongFileMode = new Xpp3Dom("tarLongFileMode");
		Xpp3Dom descriptors = new Xpp3Dom("descriptors");
		Xpp3Dom descriptor = new Xpp3Dom("descriptor");
		descriptor.setValue("src/assembly/deploy.xml");
		tarLongFileMode.setValue("gnu");
		descriptors.addChild(descriptor);
		configuration.addChild(descriptors);
		configuration.addChild(tarLongFileMode);
		aPlugin.setConfiguration(configuration);
		build.addPlugin(aPlugin);

		Resource resource = new Resource();
		resource.setDirectory("src/main/resources");
		resource.setFiltering(true);
		build.addResource(resource);

		return build;
	}

	protected static Reporting defaultReporting() {
		Reporting reporting = new Reporting();
		ReportPlugin aPlugin = new ReportPlugin();

		//maven javadoc
		aPlugin.setArtifactId("maven-javadoc-plugin");
		reporting.addPlugin(aPlugin);

		//
		aPlugin = new ReportPlugin();
		aPlugin.setGroupId("org.codehaus.mojo");
		aPlugin.setArtifactId("jxr-maven-plugin");
		reporting.addPlugin(aPlugin);

		//unit testing
		aPlugin = new ReportPlugin();
		aPlugin.setArtifactId("maven-surefire-plugin");
		reporting.addPlugin(aPlugin);

		aPlugin = new ReportPlugin();
		aPlugin.setArtifactId("maven-clover-plugin");
		reporting.addPlugin(aPlugin);

		return reporting;

	}

	protected List<License> defaultLicenses() {
		ArrayList<License> list = new ArrayList<License>();
		License license = new License();
		license.setUrl("http://images.apple.com/legal/sla/docs/macosxserver105.pdf");
		license.setComments("WebObjects Deployment License");
		list.add(license);

		//FIXME: should be Xcode development license
		license = new License();
		license.setUrl("http://developer.apple.com/opensource/tools/");
		license.setComments("WebObjects Development License");
		list.add(license);

		return list;
	}

	protected Properties defaultProperties() {
		Properties properties = new Properties();
		properties.setProperty("mainWOPackage", "");
		return properties;
	}

	//FIXME: create dynamic fetching of available snapshots from a remote repository
	protected String[] updateWebObjectsVersions() {
		InputStream is = null;
		ArrayList <String> versionList = new ArrayList();
		try {
			URL releaseURL = new URL("http://manticore.apple.com/maven/libs-releases/com/webobjects/webobjects-parent/maven-metadata.xml");
			System.out.println(releaseURL);
			URLConnection conn = releaseURL.openConnection();
			conn.connect();
			is = conn.getInputStream();

			// Parse the XML as a W3C document.
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = builder.parse(is);
			XPath xpath = XPathFactory.newInstance().newXPath();
			String expression = "/versioning/versions";

//			Node versionsNode = (Node) xpath.evaluate(expression, document, XPathConstants.STRING);
			String versionsNode = (String) xpath.evaluate(expression, document, XPathConstants.STRING);

//			System.out.println("versionsNode="+versionsNode+" "+versionsNode.getNodeValue());
			System.out.println("versionsNode="+versionsNode);


		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					//
				}
			}

			System.out.println("Finished parse");
		}


		return (versionList.size() > 0) ? versionList.toArray(new String[] {}) : null;
	}
}


