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
package org.objectstyle.wolips.apple.mavenintegration.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.embedder.Configuration;
import org.apache.maven.embedder.DefaultConfiguration;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.profiles.Profile;
import org.apache.maven.profiles.ProfilesRoot;
import org.apache.maven.profiles.Repository;
import org.apache.maven.profiles.io.xpp3.ProfilesXpp3Reader;
import org.apache.maven.profiles.io.xpp3.ProfilesXpp3Writer;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * Property page for configuring parts of the pom.xml and profiles.xml
 */
public class AppleMavenPropertyPage extends PropertyPage {

	protected static final String DEFAULT_LOCAL_REPO_ID = "local";

	protected static final String userHome = System.getProperty( "user.home" );

	protected static final File userMavenConfigurationHome = new File( userHome, ".m2" );

	protected static final File defaultUserLocalRepository = new File( userMavenConfigurationHome, "repository" );

	protected static final File DEFAULT_USER_SETTINGS_FILE = new File( userMavenConfigurationHome, "settings.xml" );

	protected static final File DEFAULT_GLOBAL_SETTINGS_FILE =
		new File( System.getProperty( "maven.home", System.getProperty( "user.dir", "" ) ), "conf/settings.xml" );

	private static final String PROFILES_XML_FILE = "profiles.xml";
	private static final String POM_XML_FILE = "pom.xml";

	private static final String NOT_AVAILABLE_STRING = "Not Available";

	//if property values have changed
	private static boolean isPOMDirty = false;
	private static boolean isProfileDirty = false;

	//UI controls
	private Combo versionCombo;
	private Text snapshotPathTextField;
	private Text pluginSnapshotPathTextField;

	private Text projectVersionTextField;

	/**
	 * Constructor for SamplePropertyPage.
	 */
	public AppleMavenPropertyPage() {
		super();
	}

	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL | GridData.GRAB_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		Group projectSettingsGroup = new Group(composite, SWT.SHADOW_IN);
		projectSettingsGroup.setText("Project Settings");
		GridData versionGroupData = new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1);
		versionGroupData.minimumHeight = 50;
		projectSettingsGroup.setLayoutData(versionGroupData);
		projectSettingsGroup.setLayout(new GridLayout(2, false));

		Label projectVersionLabel = new Label(projectSettingsGroup, SWT.NONE);
		projectVersionLabel.setText("Project Version: ");

		projectVersionTextField = new Text(projectSettingsGroup, SWT.LEFT | SWT.SINGLE | SWT.SHADOW_IN);
		projectVersionTextField.setText(this.getProjectVersion());
		projectVersionTextField.addModifyListener(new POMTextFieldModificationListener());

		Label versionLabel = new Label(projectSettingsGroup, SWT.NONE);
		versionLabel.setText("WebObjects Runtime Version: ");

		versionCombo = new Combo(projectSettingsGroup, SWT.DROP_DOWN);
		refreshWOVersionsForCombo(versionCombo);
		updateVersionCombo();
		versionCombo.addSelectionListener(new VersionComboSelectionListener());

		Group repositoryGroup = new Group(composite, SWT.SHADOW_IN);
		GridData repositoryGroupData = new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1);
		repositoryGroupData.minimumHeight = 50;
		repositoryGroup.setText("Apple Maven Repository");
		repositoryGroup.setLayoutData(repositoryGroupData);
		repositoryGroup.setLayout(new GridLayout(2, false));

		Label locationURL = new Label(repositoryGroup, SWT.NONE);
		locationURL.setText("Maven Repository URL: ");

		// Path text field
		snapshotPathTextField = new Text(repositoryGroup, SWT.LEFT | SWT.SINGLE | SWT.SHADOW_IN);
		snapshotPathTextField.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		updateSnapshotURL();
		snapshotPathTextField.addModifyListener(new ProfilesTextFieldModificationListener());

		Label pluginLocationURL = new Label(repositoryGroup, SWT.NONE);
		pluginLocationURL.setText("Maven Plugin-Repository URL: ");

		// Path text field
		pluginSnapshotPathTextField = new Text(repositoryGroup, SWT.LEFT | SWT.SINGLE | SWT.SHADOW_IN);
		pluginSnapshotPathTextField.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		updatePluginSnapshotURL();
		pluginSnapshotPathTextField.addModifyListener(new ProfilesTextFieldModificationListener());


		return composite;
	}

	/**
	 * Fetch the available WO library versions
	 * @param combo
	 */
	public void refreshWOVersionsForCombo(Combo combo) {

		//FIXME: query resource plugin for valid snapshot versions -dlee
		ArrayList<String> list = new ArrayList<String>();

		list.add("5.4.2-SNAPSHOT");
		list.add("5.5-SNAPSHOT");
		list.add("6.0-SNAPSHOT");

		if (list.size() > 0) {
			combo.setText(list.get(1));
		}

		for (String aVer : list) {
			combo.add(aVer);
		}

	}

	/**
	 * @param basedir
	 * @return profiles root
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	//inspired from DefaultMavenProfilesBuilder
	public ProfilesRoot buildProfiles( IPath basedir ) throws IOException, XmlPullParserException {
		File profilesXml = new File( basedir.toFile(), PROFILES_XML_FILE );
		ProfilesRoot profilesRoot = null;
		FileReader fr = null;

		if ( profilesXml.exists() )
		{
			ProfilesXpp3Reader reader = new ProfilesXpp3Reader();
			try
			{
				fr = new FileReader(profilesXml);
				profilesRoot = reader.read( fr );
			}
			finally
			{
				if (fr != null) {
					fr.close();
				}
			}
		}

		return profilesRoot;

	}

	/**
	 * @param root
	 * @return profile
	 */
	public Profile getWOProfileFromRoot(ProfilesRoot root) {
		if (root == null) {
			return null;
		}

		Profile woProfile = null;
		for (Object aProfile : root.getProfiles()) {
			if (((Profile) aProfile).getId().equals("webobjects")) {
				woProfile = (Profile)aProfile;
			}
		}

		return woProfile;
	}

	/**
	 * Search a profile for wo-snapshot repository
	 * @param profile
	 * @return wo snapshot repository in a given Profile
	 */
	public Repository getWOSnapshotRepositoryFromProfile(Profile profile) {
		if (profile == null)
			return null;

		List<?> reps = profile.getRepositories();
		Repository woSnapshotRep = null;
		for (Object aRep : reps) {
			if (((Repository)aRep).getId().equals("webobjects-snapshots")) {
				woSnapshotRep = (Repository)aRep;
				break;
			}
		}

		return woSnapshotRep;
	}

	/**
	 * @return string url to current snapshot repository
	 */
	public String getCurrentSnapshotRepositoryURL() {
		String urlPath = NOT_AVAILABLE_STRING;
		try {
			ProfilesRoot pRoot = getProfilesRoot();
			Profile woProfile = getWOProfileFromRoot(pRoot);
			Repository woRep = getWOSnapshotRepositoryFromProfile(woProfile);
			if (woRep != null) {
				urlPath = woRep.getUrl();
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		return urlPath;
	}

	/**
	 * Search a profile for wo-snapshot repository
	 * @param profile
	 * @return wo snapshot repository in a given Profile
	 */
	public Repository getWOPluginSnapshotRepositoryFromProfile(Profile profile) {
		if (profile == null)
			return null;

		List<?> reps = profile.getPluginRepositories();
		Repository woSnapshotRep = null;
		for (Object aRep : reps) {
			if (((Repository)aRep).getId().equals("webobjects-snapshots")) {
				woSnapshotRep = (Repository)aRep;
				break;
			}
		}

		return woSnapshotRep;
	}

	/**
	 * @return string url to current plugin snapshot repository
	 */
	public String getCurrentPluginSnapshotRepositoryURL() {
		String urlPath = NOT_AVAILABLE_STRING;
		try {
			ProfilesRoot pRoot = getProfilesRoot();
			Profile woProfile = getWOProfileFromRoot(pRoot);
			Repository woRep = getWOPluginSnapshotRepositoryFromProfile(woProfile);
			if (woRep != null) {
				urlPath = woRep.getUrl();
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		return urlPath;
	}

	/**
	 * @return java.io.File reference for project profiles.xml
	 *
	 */
	public IPath getWOProfilesFile() {
		IResource res = (IResource) getElement();
		IPath profileRes = res.getProject().getLocation().append("/"+PROFILES_XML_FILE);
		return profileRes;
	}

	/**
	 * @return base directory for selected project
	 */
	public IPath getProjectBaseDir() {
		IResource res = (IResource) getElement();
		return res.getProject().getLocation();
	}

	/**
	 * @return profiles root
	 */
	public ProfilesRoot getProfilesRoot() {
		IPath profileBase = getProjectBaseDir();

		ProfilesRoot pRoot = null;
		try {
			pRoot = this.buildProfiles(profileBase);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return pRoot;
	}

	/**
	 *	Write the current property settings for the profiles.xml
	 */
	public void writeWOMavenProfile() {
		ProfilesRoot root = getProfilesRoot();
		updateProfileRoot(root);
		WriteProfileJob job = new WriteProfileJob(this.getCurrentProject(),"Updating profiles.xml", root, this.getWOProfilesFile());
		job.schedule();
	}

	/**
	 * Write current maven project (pom.xml) for project
	 * @param proj
	 */
	public void writeMavenProject(MavenProject proj) {
		try {
			FileWriter fw = new FileWriter(proj.getFile());
			proj.writeModel(fw);
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return pom file reference
	 */
	public IPath getPOMFile() {
		IResource res = (IResource) getElement();
		IPath pomRes = res.getProject().getLocation().append("/"+POM_XML_FILE);
		return pomRes;
	}

	/**
	 * @return maven model
	 */
	public Model getMavenModel() {
		Model model = null;
		FileInputStream fis = null;

		IPath pomRes = getPOMFile();
		if (pomRes != null && pomRes.toFile().exists()) {
			try {
				fis = new FileInputStream(pomRes.toFile());
				MavenXpp3Reader reader = new MavenXpp3Reader();
				model = reader.read(fis, true);
			} catch (RuntimeException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						//swallow
					}
				}
			}
		}
		return model;
	}

	/**
	 *
	 */
	public void writeMavenModel() {
		IPath pomFile = getPOMFile();
		Model mavenModel = getMavenModel();

		if (pomFile != null && mavenModel != null) {
			this.updateModel(mavenModel);
			writeModel(mavenModel, pomFile);
		}
	}

	/**
	 * @return current IProject
	 */
	public IProject getCurrentProject () {
		IResource res = (IResource) getElement();
		IProject project = res.getProject();
		if (project == null) {
			//last ditch effort
			return ResourcesPlugin.getWorkspace().getRoot().getProject();
		}

		return project;
	}

	/**
	 * @param model
	 * @param pomFile
	 */
	public void writeModel (Model model, IPath pomFile) {
		WriteModelJob job = new WriteModelJob(this.getCurrentProject(),"Updating pom.xml", model, pomFile);
		job.schedule();
	}

	/**
	 * Pull all updates from UI and add to current maven project model (pom.xml)
	 * @param model
	 */
	public void updateModel(Model model) {
		if (isPOMDirty) {
			//pull latest property page settings and update Model
			String woversion = this.versionCombo.getText();
			model.addProperty("woversion", woversion);

			String projectversion = this.projectVersionTextField.getText();
			model.setVersion(projectversion);
		}
	}

	/**
	 * Pull all updates from the UI and add to current profiles root (profile.xml)
	 * @param root
	 */
	public void updateProfileRoot(ProfilesRoot root) {
		if (isProfileDirty) {
			try {
				Profile woProfile = getWOProfileFromRoot(root);
				Repository woRep = getWOSnapshotRepositoryFromProfile(woProfile);
				Repository woPluginRep = getWOPluginSnapshotRepositoryFromProfile(woProfile);
				String currURL = this.snapshotPathTextField.getText();
				System.out.println("Current Snapshot URL setting: "+currURL);
				woRep.setUrl(currURL);

				String currPluginURL = this.pluginSnapshotPathTextField.getText();
				woPluginRep.setUrl(currPluginURL);
				System.out.println("Current Plugin Snapshot URL setting: "+currURL);

			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 *
	 */
	public void updateSnapshotURL() {
		try {
			String url = getCurrentSnapshotRepositoryURL();
			this.snapshotPathTextField.setText(url);
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.snapshotPathTextField.setText("URL to Maven repository");
		}
	}

	/**
	 *
	 */
	public void updatePluginSnapshotURL() {
		try {
			String url = this.getCurrentPluginSnapshotRepositoryURL();
			this.pluginSnapshotPathTextField.setText(url);
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.pluginSnapshotPathTextField.setText("URL to Maven Plugin repository");
		}
	}

	/**
	 * Assign it the current version encoded in the POM.  If version is unrecognized, the select default.
	 */
	public void updateVersionCombo() {
		String[] validVersionStrings = this.versionCombo.getItems();
		String woVersion = getCurrentProjectWOVersion();
		int index = 0;
		if (woVersion != null && woVersion.length() > 0) {
			for (String aVersion : validVersionStrings) {
				if (aVersion.equals(woVersion)) {
					this.versionCombo.select(index);
					break;
				}
				index++;
			}
		}
	}

	/**
	 * @return wo runtime version string
	 */
	public String getCurrentProjectWOVersion() {
		String version = "";
		Model model = getMavenModel();

		if (model != null) {
			Properties props = model.getProperties();
			version = props.getProperty("woversion");
		}
		return version;
	}

	/**
	 * @return maven configuration
	 */
	public Configuration getConfiguration() {
		Configuration config;
		File userSettingsFile = DEFAULT_USER_SETTINGS_FILE;
		ClassWorld classWorld = new ClassWorld( "plexus.core", Thread.currentThread().getContextClassLoader() );


		config = new DefaultConfiguration().setUserSettingsFile( userSettingsFile );
		config.setGlobalSettingsFile(DEFAULT_GLOBAL_SETTINGS_FILE );
		config.setClassWorld( classWorld );

		return config;
	}

	/**
	 * @return maven project version
	 */
	public String getProjectVersion() {
		String version = "";
		Model model = getMavenModel();

		if (model != null) {
			version = model.getVersion();
		}
		return version;
	}

	@Override
	public boolean performOk() {
		//			((IResource) getElement()).setPersistentProperty(
//		new QualifiedName("", USER_PROPERTY),
//		ownerText.getText());
		if (isPOMDirty) {
			writeMavenModel();
			isPOMDirty = false;
		}

		if (isProfileDirty) {
			writeWOMavenProfile();
			isProfileDirty = false;
		}

		return true;
	}

	class ProfilesTextFieldModificationListener implements ModifyListener {

		public void modifyText(ModifyEvent arg0) {
			isProfileDirty = true;
		}

	}

	class POMTextFieldModificationListener implements ModifyListener {

		public void modifyText(ModifyEvent arg0) {
			isPOMDirty = true;
		}

	}

	class VersionComboSelectionListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
			handleUpdatedSelection(e);
		}

		public void widgetSelected(SelectionEvent e) {
			handleUpdatedSelection(e);
		}

		/**
		 * @param e
		 * @return true if handled
		 */
		public boolean handleUpdatedSelection(SelectionEvent e) {
			Object source = e.getSource();
			if (source instanceof Combo) {
				isPOMDirty = true;
				Combo theCombo = (Combo)source;

				if (theCombo.getText().endsWith("SNAPSHOT")) {
					//display warning dialog
					MessageDialog.openWarning(getShell(), "WebObjects Pre-Release Software", "Warning: Pre–release software is Apple confidential information. Your unauthorized distribution of pre–release software or disclosure of information relating to pre–release software may subject you to both civil and criminal liability and result in immediate termination of your ADC Membership. See the ADC Prototype License and Confidentiality Agreement for details.");
				}

				return true;
			}
			return false;
		}
	}

	class WriteModelJob extends WorkspaceJob {
		private Model _model;
		private IPath _path;
		private IProject _project;

		/**
		 * @param project
		 * @param name
		 * @param model
		 * @param modelPath
		 */
		public WriteModelJob(IProject project, String name, Model model, IPath modelPath) {
			super(name);
			_path = modelPath;
			_model = model;
			_project = project;
		}

		@Override
		public IStatus runInWorkspace(IProgressMonitor monitor) {
			FileWriter fw = null;
			MavenXpp3Writer pomWriter = new MavenXpp3Writer();
			IFile ifile = null;
			try {
				monitor.beginTask("Writing updated pom.xml", 100);
				ifile = _project.getFile(_path);
				if (ifile != null) {
					fw = new FileWriter(_path.toFile());
					pomWriter.write(fw, _model);
				}
				if (monitor.isCanceled()) return Status.CANCEL_STATUS;
				_project.refreshLocal(IResource.DEPTH_INFINITE, monitor);

			} catch (IOException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			} finally {
				monitor.done();
			}
			return Status.OK_STATUS;
		}
	}

	class WriteProfileJob extends WorkspaceJob {
		private ProfilesRoot _profile;
		private IPath _path;
		private IProject _project;

		/**
		 * @param project
		 * @param name
		 * @param profile
		 * @param profilePath
		 */
		public WriteProfileJob(IProject project, String name, ProfilesRoot profile, IPath profilePath) {
			super(name);
			_path = profilePath;
			_profile = profile;
			_project = project;
		}

		@Override
		public IStatus runInWorkspace(IProgressMonitor monitor) {

			try {
				monitor.beginTask("Writing updated profiles.xml", 100);

				ProfilesXpp3Writer writer = new ProfilesXpp3Writer();
				File profileFile = _path.toFile();
				FileWriter fwriter = new FileWriter(profileFile);
				writer.write(fwriter, _profile);

				if (monitor.isCanceled()) return Status.CANCEL_STATUS;
				_project.refreshLocal(IResource.DEPTH_INFINITE, monitor);

			} catch (IOException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			} finally {
				monitor.done();
			}

			return Status.OK_STATUS;
		}
	}
}