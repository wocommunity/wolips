/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002 - 2006 The ObjectStyle Group
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
package org.objectstyle.wolips.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IPluginContribution;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.activities.IActivityManager;
import org.eclipse.ui.activities.IIdentifier;
import org.eclipse.ui.activities.IWorkbenchActivitySupport;
import org.eclipse.ui.activities.WorkbenchActivityHelper;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.dialogs.WizardNewProjectReferencePage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.IPreferenceConstants;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.ide.IDEInternalPreferences;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.registry.PerspectiveDescriptor;
import org.eclipse.ui.internal.util.PrefUtil;
import org.eclipse.ui.internal.wizards.newresource.ResourceMessages;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.objectstyle.wolips.jdt.ProjectFrameworkAdapter;
import org.objectstyle.wolips.jdt.classpath.model.IEclipseFramework;
import org.objectstyle.wolips.templateengine.InstallTemplateOperation;
import org.objectstyle.wolips.templateengine.ProjectTemplate;
import org.objectstyle.wolips.templateengine.TemplateDefinition;
import org.objectstyle.wolips.templateengine.TemplateEngine;
import org.objectstyle.wolips.wizards.D2WApplicationConfigurationPage.D2WLook;
import org.objectstyle.wolips.wizards.actions.EOModelImportWorkspaceJob;
import org.objectstyle.wolips.wizards.template.TemplateInputsWizardPage;

/**
 * Standard workbench wizard that creates a new project resource in the
 * workspace.
 * <p>
 * This class may be instantiated and used without further configuration. WOLips
 * wizards extensively subclass this for project creation.
 * </p>
 * <p>
 * Example:
 * 
 * <pre>
 * IWorkbenchWizard wizard = new BasicNewProjectResourceWizard();
 * wizard.init(workbench, selection);
 * WizardDialog dialog = new WizardDialog(shell, wizard);
 * dialog.open();
 * </pre>
 * 
 * During the call to <code>open</code>, the wizard dialog is presented to the
 * user. When the user hits Finish, a project resource with the user-specified
 * name is created, the dialog closes, and the call to <code>open</code>
 * returns.
 * </p>
 * 
 * @see D2WApplicationWizard
 * @see WOApplicationWizard
 * @see WOFrameworkWizard
 * @see D2WebServiceApplicationWizard
 * @see WOnderApplicationWizard
 * @see WOnderFrameworkWizard
 * @see WOSubprojectCreationWizard
 * @see WOnderApplicationWizard
 * @see ERRestApplicationWizard
 */
public abstract class NewWOProjectWizard extends BasicNewResourceWizard implements IExecutableExtension {

	private static String WINDOW_PROBLEMS_TITLE = ResourceMessages.NewProject_errorOpeningWindow;

	/**
	 * Extension attribute name for final perspective.
	 */
	private static final String FINAL_PERSPECTIVE = "finalPerspective"; //$NON-NLS-1$

	/**
	 * Extension attribute name for preferred perspectives.
	 */
	private static final String PREFERRED_PERSPECTIVES = "preferredPerspectives"; //$NON-NLS-1$

	/**
	 * New Project from Template wizard
	 */
	protected static final int NEWPROJECT_TEMPLATE_WIZARD = 0;

	/**
	 * Valid project wizard types: WO_APPLICATION_WIZARD,
	 * D2W_APPLICATION_WIZARD, D2WS_APPLICATION_WIZARD, JARPROJECT_WIZARD,
	 * WO_FRAMEWORK_WIZARD, WONDER_APPLICATION_WIZARD,
	 * WONDER_D2W_APPLICATION_WIZARD, WONDER_FRAMEWORK_WIZARD,
	 * NEWPROJECT_TEMPLATE_WIZARD,WOMAVENAPPLICATION, ERREST_APPLICATION_WIZARD
	 */
	public enum WizardType {
		WO_APPLICATION_WIZARD, D2W_APPLICATION_WIZARD, D2JC_APPLICATION_WIZARD, D2WS_APPLICATION_WIZARD, JARPROJECT_WIZARD, WO_FRAMEWORK_WIZARD, WONDER_APPLICATION_WIZARD, WONDER_D2W_APPLICATION_WIZARD, WONDER_FRAMEWORK_WIZARD, NEWPROJ_TEMPLATE_WIZARD, WOMAVENPROJECT, ERREST_APPLICATION_WIZARD
	}

	private WizardNewProjectCreationPage _mainPage;

	private WizardNewProjectReferencePage _referencePage;

	protected EOModelResourceImportPage _eomodelImportPage;

	protected D2WApplicationConfigurationPage _d2wConfigurationPage;

	protected PackageSpecifierWizardPage _packagePage;

	protected WOWebServicesWizardPage _webservicesSupportPage;

	protected WOFrameworkSupportPage _frameworkSupportPage;

	protected TemplateInputsWizardPage _templateInputsWizardPage;

	private ProjectTemplate _projectTemplate;

	private IProject _newProject;

	/**
	 * The configuration element which declares this wizard.
	 */
	private IConfigurationElement _configElement;

	/**
	 * Creates a wizard for creating a new project resource in the workspace.
	 */
	public NewWOProjectWizard() {
		IDialogSettings workbenchSettings = IDEWorkbenchPlugin.getDefault().getDialogSettings();
		IDialogSettings section = workbenchSettings.getSection("BasicNewProjectResourceWizard");//$NON-NLS-1$
		if (section == null) {
			section = workbenchSettings.addNewSection("BasicNewProjectResourceWizard");//$NON-NLS-1$
		}
		setDialogSettings(section);
	}

	/**
	 * @param projectTemplateName
	 */
	public NewWOProjectWizard(String projectTemplateName) {
		this();
		_projectTemplate = ProjectTemplate.loadProjectTemplateNamed(ProjectTemplate.PROJECT_TEMPLATES, projectTemplateName);
		if (_projectTemplate == null) {
			throw new IllegalArgumentException("There was no project template with the name '" + projectTemplateName + "'.");
		}
		_templateInputsWizardPage = new TemplateInputsWizardPage();
		_templateInputsWizardPage.setProjectTemplate(_projectTemplate);
	}

	public void setProjectTemplate(ProjectTemplate projectTemplate) {
		_projectTemplate = projectTemplate;
	}

	public ProjectTemplate getProjectTemplate() {
		return _projectTemplate;
	}

	/**
	 * For simple wizard type identification. A sub-classer must implement.
	 * Valid types are WO_APPLICATION_WIZARD, D2W_APPLICATION_WIZARD,
	 * JARPROJECT_WIZARD, WO_FRAMEWORK_WIZARD, WONDER_APPLICATION_WIZARD,
	 * WONDER_D2W_APPLICATION_WIZARD, WONDER_FRAMEWORK_WIZARD = 7;
	 * 
	 * @return wizard type
	 */
	protected abstract WizardType getWizardType();

	/*
	 * (non-Javadoc) Method declared on IWizard.
	 */
	public void addPages() {
		super.addPages();
		WizardType wizardType = getWizardType();
		_mainPage = createMainPage();
		addPage(_mainPage);

		if (wizardType == WizardType.D2W_APPLICATION_WIZARD || wizardType == WizardType.D2WS_APPLICATION_WIZARD) {
			_packagePage = createPackageSpecifierWizardPage();
			if (_packagePage != null) {
				addPage(_packagePage);
			}
		}

		if (_templateInputsWizardPage != null && _projectTemplate.getInputs().size() > 0) {
			addPage(_templateInputsWizardPage);
		}

		_referencePage = createReferencePage();
		if (_referencePage != null) {
			addPage(_referencePage);
		}

		_eomodelImportPage = createEOModelImportResourcePage();
		if (_eomodelImportPage != null) {
			addPage(_eomodelImportPage);
		}

		if (wizardType == WizardType.D2W_APPLICATION_WIZARD) {
			_d2wConfigurationPage = createD2WConfigurationPage();

			if (_d2wConfigurationPage != null) {
				addPage(_d2wConfigurationPage);
			}
		}

		if (wizardType == WizardType.D2W_APPLICATION_WIZARD || wizardType == WizardType.WO_APPLICATION_WIZARD || wizardType == WizardType.D2WS_APPLICATION_WIZARD || wizardType == WizardType.WO_FRAMEWORK_WIZARD) {
			_frameworkSupportPage = createFrameworkSupportPage();
			if (_frameworkSupportPage != null) {
				addPage(_frameworkSupportPage);
			}
		}

		if (wizardType == WizardType.WO_APPLICATION_WIZARD || wizardType == WizardType.D2W_APPLICATION_WIZARD || wizardType == WizardType.WO_FRAMEWORK_WIZARD) {
			_webservicesSupportPage = createWebServicesSupportPage();
			if (_webservicesSupportPage != null) {
				addPage(_webservicesSupportPage);
			}
		}
	}

	protected String getPageDescription() {
		String description = ResourceMessages.NewProject_description;
		if (getWizardType() == WizardType.D2W_APPLICATION_WIZARD) {
			description = Messages.getString("D2WApplicationWizard.description");
		}

		return description;
	}

	protected WizardNewProjectCreationPage createMainPage() {
		WizardNewProjectCreationPage mainPage = new WizardNewProjectCreationPage("basicNewProjectPage");//$NON-NLS-1$
		mainPage.setTitle(ResourceMessages.NewProject_title);
		mainPage.setDescription(getPageDescription());
		return mainPage;
	}

	protected WizardNewProjectReferencePage createReferencePage() {
		WizardNewProjectReferencePage referencePage = null;
		if (ResourcesPlugin.getWorkspace().getRoot().getProjects().length > 0) {
			referencePage = new WizardNewProjectReferencePage("basicReferenceProjectPage");//$NON-NLS-1$
			referencePage.setTitle(ResourceMessages.NewProject_referenceTitle);
			referencePage.setDescription(ResourceMessages.NewProject_referenceDescription);
		}
		return referencePage;
	}

	protected EOModelResourceImportPage createEOModelImportResourcePage() {
		EOModelResourceImportPage importResourcePage = new EOModelResourceImportPage("EOModelImportWizpardPage");
		importResourcePage.setTitle(Messages.getString("EOModelResourceImportPage.title"));
		importResourcePage.setDescription(Messages.getString("EOModelResourceImportPage.description"));
		importResourcePage.setMessage(Messages.getString("EOModelResourceImportPage.message"));
		return importResourcePage;
	}

	protected D2WApplicationConfigurationPage createD2WConfigurationPage() {
		D2WApplicationConfigurationPage d2wConfigPage = new D2WApplicationConfigurationPage("d2WAppConfigurationWizardPage", IMessageProvider.NONE);
		d2wConfigPage.setTitle(Messages.getString("D2WApplicationConfigurationPage.title"));
		d2wConfigPage.setDescription(Messages.getString("D2WApplicationConfigurationPage.description"));
		return d2wConfigPage;
	}

	protected PackageSpecifierWizardPage createPackageSpecifierWizardPage() {
		PackageSpecifierWizardPage packagePage = new PackageSpecifierWizardPage("packageSpecifierWizardPage", IMessageProvider.NONE);
		packagePage.setTitle("Specify package: ");
		packagePage.setMessage("Default package for all Java source generated for project");
		packagePage.setDescription("Default package for all Java source generated for project");
		return packagePage;
	}

	protected WOWebServicesWizardPage createWebServicesSupportPage() {
		WOWebServicesWizardPage webservicesPage = null;
		if (ResourcesPlugin.getWorkspace().getRoot().getProjects().length > 0) {
			webservicesPage = new WOWebServicesWizardPage("webservicesSupportWizardPage", IMessageProvider.NONE);//$NON-NLS-1$
			webservicesPage.setTitle(Messages.getString("WOWebServicesWizardPage.title"));
			webservicesPage.setDescription(Messages.getString("WOWebServicesWizardPage.description"));
		}
		return webservicesPage;
	}

	protected WOFrameworkSupportPage createFrameworkSupportPage() {
		WOFrameworkSupportPage frameworkPage = null;
		if (ResourcesPlugin.getWorkspace().getRoot().getProjects().length > 0) {
			frameworkPage = new WOFrameworkSupportPage("frameworkSupportWizardPage", IMessageProvider.NONE);//$NON-NLS-1$
			frameworkPage.setTitle(Messages.getString("WOFrameworkSupportPage.title"));
			frameworkPage.setDescription(Messages.getString("WOFrameworkSupportPage.description"));
		}
		return frameworkPage;
	}

	protected void setMainPage(WizardNewProjectCreationPage mainPage) {
		_mainPage = mainPage;
	}

	protected void setReferencePage(WizardNewProjectReferencePage referencePage) {
		_referencePage = referencePage;
	}

	/**
	 * Creates a new project resource with the selected name.
	 * <p>
	 * In normal usage, this method is invoked after the user has pressed Finish
	 * on the wizard; enabling of the Finish button implies that all controls on
	 * the pages currently contain valid values.
	 * </p>
	 * <p>
	 * Note that this wizard caches the new project once it has been
	 * successfully created; subsequent invocations of this method will answer
	 * the same project resource without attempting to create it again.
	 * </p>
	 * 
	 * @return the created project resource, or <code>null</code> if the project
	 *         was not created
	 */
	protected IProject createNewProject() {
		if (_newProject != null) {
			return _newProject;
		}

		// get a project handle
		final IProject newProjectHandle = _mainPage.getProjectHandle();

		// get a project descriptor
		URI location = null;
		if (!_mainPage.useDefaults()) {
			location = _mainPage.getLocationURI();
		}

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProjectDescription description = workspace.newProjectDescription(newProjectHandle.getName());
		description.setLocationURI(location);

		// update the referenced project if provided
		final IProject[] refProjects;
		if (_referencePage != null) {
			refProjects = _referencePage.getReferencedProjects();
		} else {
			refProjects = null;
		}

		// create the new project operation
		try {
			WorkspaceModifyOperation op = createProjectTemplateOperation(_projectTemplate, newProjectHandle, description, refProjects);
			getContainer().run(true, true, op);
		} catch (InterruptedException e) {
			return null;
		} catch (InvocationTargetException e) {
			// ie.- one of the steps resulted in a core exception
			Throwable t = e.getTargetException();
			if (t instanceof CoreException) {
				if (((CoreException) t).getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS) {
					MessageDialog.openError(getShell(), ResourceMessages.NewProject_errorMessage, NLS.bind(ResourceMessages.NewProject_caseVariantExistsError, newProjectHandle.getName()));
				} else {
					ErrorDialog.openError(getShell(), ResourceMessages.NewProject_errorMessage, null, ((CoreException) t).getStatus());
				}
			} else {
				// CoreExceptions are handled above, but unexpected runtime
				// exceptions and errors may still occur.
				IDEWorkbenchPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, IDEWorkbenchPlugin.IDE_WORKBENCH, 0, t.toString(), t));
				MessageDialog.openError(getShell(), ResourceMessages.NewProject_errorMessage, NLS.bind(ResourceMessages.NewProject_internalError, t.getMessage()));
			}
			return null;
		}

		_newProject = newProjectHandle;

		return _newProject;
	}

	protected WorkspaceModifyOperation createProjectTemplateOperation(ProjectTemplate template, IProject project, IProjectDescription projectDescription, IProject[] refProjects) {
		WorkspaceModifyOperation op = new InstallProjectTemplateOperation(template, project, project, projectDescription, refProjects);
		return op;
	}

	protected abstract void postInstallTemplate(IProject project, IProgressMonitor progressMonitor) throws Exception;

	/**
	 * 
	 * Returns the newly created project.
	 * 
	 * @return the created project, or <code>null</code> if project not created
	 */
	public IProject getNewProject() {
		return _newProject;
	}

	public String getWindowTitle() {
		return "New WOLips Project";
	}

	/*
	 * (non-Javadoc) Method declared on IWorkbenchWizard.
	 */
	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
		super.init(workbench, currentSelection);
		setNeedsProgressMonitor(true);
		setWindowTitle(getWindowTitle());
	}

	/*
	 * (non-Javadoc) Method declared on BasicNewResourceWizard.
	 */
	protected void initializeDefaultPageImageDescriptor() {
		setDefaultPageImageDescriptor(WizardsPlugin.WOPROJECT_WIZARD_BANNER());
	}

	/*
	 * (non-Javadoc) Opens a new window with a particular perspective and input.
	 */
	private static void openInNewWindow(IPerspectiveDescriptor desc) {

		// Open the page.
		try {
			PlatformUI.getWorkbench().openWorkbenchWindow(desc.getId(), ResourcesPlugin.getWorkspace().getRoot());
		} catch (WorkbenchException e) {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window != null) {
				ErrorDialog.openError(window.getShell(), WINDOW_PROBLEMS_TITLE, e.getMessage(), e.getStatus());
			}
		}
	}

	/*
	 * (non-Javadoc) Method declared on IWizard.
	 */
	public boolean performFinish() {
		createNewProject();

		if (_newProject == null) {
			return false;
		}

		updatePerspective();
		selectAndReveal(_newProject);

		return true;
	}

	/*
	 * (non-Javadoc) Replaces the current perspective with the new one.
	 */
	private static void replaceCurrentPerspective(IPerspectiveDescriptor persp) {

		// Get the active page.
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			return;
		}
		IWorkbenchPage page = window.getActivePage();
		if (page == null) {
			return;
		}

		// Set the perspective.
		page.setPerspective(persp);
	}

	/**
	 * Stores the configuration element for the wizard. The config element will
	 * be used in <code>performFinish</code> to set the result perspective.
	 * 
	 * @param cfig
	 * @param propertyName
	 * @param data
	 */
	public void setInitializationData(IConfigurationElement cfig, String propertyName, Object data) {
		_configElement = cfig;
	}

	/**
	 * Updates the perspective for the active page within the window.
	 */
	protected void updatePerspective() {
		updatePerspective(_configElement);
	}

	/**
	 * Updates the perspective based on the current settings in the
	 * Workbench/Perspectives preference page.
	 * 
	 * Use the setting for the new perspective opening if we are set to open in
	 * a new perspective.
	 * <p>
	 * A new project wizard class will need to implement the
	 * <code>IExecutableExtension</code> interface so as to gain access to the
	 * wizard's <code>IConfigurationElement</code>. That is the configuration
	 * element to pass into this method.
	 * </p>
	 * 
	 * @param configElement
	 *            - the element we are updating with
	 * 
	 * @see IPreferenceConstants#OPM_NEW_WINDOW
	 * @see IPreferenceConstants#OPM_ACTIVE_PAGE
	 * @see IWorkbenchPreferenceConstants#NO_NEW_PERSPECTIVE
	 */
	@SuppressWarnings("unchecked")
	public static void updatePerspective(IConfigurationElement configElement) {
		// Do not change perspective if the configuration element is
		// not specified.
		if (configElement == null) {
			return;
		}

		// Retrieve the new project open perspective preference setting
		String perspSetting = PrefUtil.getAPIPreferenceStore().getString(IDE.Preferences.PROJECT_OPEN_NEW_PERSPECTIVE);

		String promptSetting = IDEWorkbenchPlugin.getDefault().getPreferenceStore().getString(IDEInternalPreferences.PROJECT_SWITCH_PERSP_MODE);

		// Return if do not switch perspective setting and are not prompting
		if (!(promptSetting.equals(MessageDialogWithToggle.PROMPT)) && perspSetting.equals(IWorkbenchPreferenceConstants.NO_NEW_PERSPECTIVE)) {
			return;
		}

		// Read the requested perspective id to be opened.
		String finalPerspId = configElement.getAttribute(FINAL_PERSPECTIVE);
		if (finalPerspId == null) {
			return;
		}

		// Map perspective id to descriptor.
		IPerspectiveRegistry reg = PlatformUI.getWorkbench().getPerspectiveRegistry();

		// leave this code in - the perspective of a given project may map to
		// activities other than those that the wizard itself maps to.
		IPerspectiveDescriptor finalPersp = reg.findPerspectiveWithId(finalPerspId);
		if (finalPersp != null && finalPersp instanceof IPluginContribution) {
			IPluginContribution contribution = (IPluginContribution) finalPersp;
			if (contribution.getPluginId() != null) {
				IWorkbenchActivitySupport workbenchActivitySupport = PlatformUI.getWorkbench().getActivitySupport();
				IActivityManager activityManager = workbenchActivitySupport.getActivityManager();
				IIdentifier identifier = activityManager.getIdentifier(WorkbenchActivityHelper.createUnifiedId(contribution));
				Set<Object> idActivities = identifier.getActivityIds();

				if (!idActivities.isEmpty()) {
					Set<Object> enabledIds = new HashSet<Object>(activityManager.getEnabledActivityIds());

					if (enabledIds.addAll(idActivities)) {
						workbenchActivitySupport.setEnabledActivityIds(enabledIds);
					}
				}
			}
		} else {
			IDEWorkbenchPlugin.log("Unable to find persective " //$NON-NLS-1$
					+
					finalPerspId + " in BasicNewProjectResourceWizard.updatePerspective"); //$NON-NLS-1$
			return;
		}

		// gather the preferred perspectives
		// always consider the final perspective (and those derived from it)
		// to be preferred
		ArrayList<String> preferredPerspIds = new ArrayList<String>();
		addPerspectiveAndDescendants(preferredPerspIds, finalPerspId);
		String preferred = configElement.getAttribute(PREFERRED_PERSPECTIVES);
		if (preferred != null) {
			StringTokenizer tok = new StringTokenizer(preferred, " \t\n\r\f,"); //$NON-NLS-1$
			while (tok.hasMoreTokens()) {
				addPerspectiveAndDescendants(preferredPerspIds, tok.nextToken());
			}
		}

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				IPerspectiveDescriptor currentPersp = page.getPerspective();

				// don't switch if the current perspective is a preferred
				// perspective
				if (currentPersp != null && preferredPerspIds.contains(currentPersp.getId())) {
					return;
				}
			}

			// prompt the user to switch
			if (!confirmPerspectiveSwitch(window, finalPersp)) {
				return;
			}
		}

		int workbenchPerspectiveSetting = WorkbenchPlugin.getDefault().getPreferenceStore().getInt(IPreferenceConstants.OPEN_PERSP_MODE);

		// open perspective in new window setting
		if (workbenchPerspectiveSetting == IPreferenceConstants.OPM_NEW_WINDOW) {
			openInNewWindow(finalPersp);
			return;
		}

		// replace active perspective setting otherwise
		replaceCurrentPerspective(finalPersp);
	}

	/**
	 * Adds to the list all perspective IDs in the Workbench who's original ID
	 * matches the given ID.
	 * 
	 * @param perspectiveIds
	 *            the list of perspective IDs to supplement.
	 * @param id
	 *            the id to query.
	 * @since 3.0
	 */
	private static void addPerspectiveAndDescendants(List<String> perspectiveIds, String id) {
		IPerspectiveRegistry registry = PlatformUI.getWorkbench().getPerspectiveRegistry();
		IPerspectiveDescriptor[] perspectives = registry.getPerspectives();
		for (int i = 0; i < perspectives.length; i++) {
			// @issue illegal ref to workbench internal class;
			// consider adding getOriginalId() as API on IPerspectiveDescriptor
			PerspectiveDescriptor descriptor = ((PerspectiveDescriptor) perspectives[i]);
			if (descriptor.getOriginalId().equals(id)) {
				perspectiveIds.add(descriptor.getId());
			}
		}
	}

	/**
	 * Prompts the user for whether to switch perspectives.
	 * 
	 * @param window
	 *            The workbench window in which to switch perspectives; must not
	 *            be <code>null</code>
	 * @param finalPersp
	 *            The perspective to switch to; must not be <code>null</code>.
	 * 
	 * @return <code>true</code> if it's OK to switch, <code>false</code>
	 *         otherwise
	 */
	private static boolean confirmPerspectiveSwitch(IWorkbenchWindow window, IPerspectiveDescriptor finalPersp) {
		IPreferenceStore store = IDEWorkbenchPlugin.getDefault().getPreferenceStore();
		String pspm = store.getString(IDEInternalPreferences.PROJECT_SWITCH_PERSP_MODE);
		if (!IDEInternalPreferences.PSPM_PROMPT.equals(pspm)) {
			// Return whether or not we should always switch
			return IDEInternalPreferences.PSPM_ALWAYS.equals(pspm);
		}
		String desc = finalPersp.getDescription();
		String message;
		if (desc == null || desc.length() == 0)
			message = NLS.bind(ResourceMessages.NewProject_perspSwitchMessage, finalPersp.getLabel());
		else
			message = NLS.bind(ResourceMessages.NewProject_perspSwitchMessageWithDesc, new String[] { finalPersp.getLabel(), desc });

		MessageDialogWithToggle dialog = MessageDialogWithToggle.openYesNoQuestion(window.getShell(), ResourceMessages.NewProject_perspSwitchTitle, message, null, false, store, IDEInternalPreferences.PROJECT_SWITCH_PERSP_MODE);
		int result = dialog.getReturnCode();

		// If we are not going to prompt anymore propogate the choice.
		if (dialog.getToggleState()) {
			String preferenceValue;
			if (result == IDialogConstants.YES_ID) {
				// Doesn't matter if it is replace or new window
				// as we are going to use the open perspective setting
				preferenceValue = IWorkbenchPreferenceConstants.OPEN_PERSPECTIVE_REPLACE;
			} else {
				preferenceValue = IWorkbenchPreferenceConstants.NO_NEW_PERSPECTIVE;
			}

			// update PROJECT_OPEN_NEW_PERSPECTIVE to correspond
			PrefUtil.getAPIPreferenceStore().setValue(IDE.Preferences.PROJECT_OPEN_NEW_PERSPECTIVE, preferenceValue);
		}
		return result == IDialogConstants.YES_ID;
	}

	/*
	 * Template Engine Support
	 */

	/*
	 * Default implementation cribbed and modified from AbstractWonderProject
	 * Create a component in the default java package
	 */
	protected void addComponentDefinition(String templateFolder, TemplateEngine engine, String path, String name) {
		addMavenComponentDefinition(templateFolder, engine, path, name, "");
	}

	/**
	 * Create a component within a specific package
	 * 
	 * @param templateFolder
	 * @param engine
	 * @param rootPath
	 * @param name
	 * @param packagePath
	 */
	protected void addMavenComponentDefinition(String templateFolder, TemplateEngine engine, String rootPath, String name, String packagePath) {
		// create component dir
		String componentPath = rootPath + File.separator + name + ".wo";
		File wo = new File(componentPath);
		wo.mkdirs();

		// create src dirs
		String fullPackagePath = (packagePath != null && packagePath.length() > 0) ? rootPath + File.separator + "src" + File.separator + packagePath : rootPath + File.separator + "src";
		File srcPath = new File(fullPackagePath);
		srcPath.mkdirs();

		engine.addTemplate(new TemplateDefinition(templateFolder + "/" + name + ".html.vm", componentPath, name + ".html", name + ".html"));
		engine.addTemplate(new TemplateDefinition(templateFolder + "/" + name + ".wod.vm", componentPath, name + ".wod", name + ".wod"));
		engine.addTemplate(new TemplateDefinition(templateFolder + "/" + name + ".woo.vm", componentPath, name + ".woo", name + ".woo"));
		engine.addTemplate(new TemplateDefinition(templateFolder + "/" + name + ".api.vm", rootPath, name + ".api", name + ".api"));
		engine.addTemplate(new TemplateDefinition(templateFolder + "/" + name + ".java.vm", fullPackagePath, name + ".java", name + ".java"));
	}

	/*
	 * D2W Support
	 */
	/**
	 * @return
	 */
	protected D2WLook currentD2WLook() {
		if (_d2wConfigurationPage == null) {
			return D2WLook.BASIC54;
		}
		return _d2wConfigurationPage.currentLook();
	}

	/**
	 * Create WebServices support
	 * 
	 * @param project
	 *            to add support
	 * @param engine
	 */
	/*
	 * FIXME: technically the templates for these files are stored in
	 * JavaWebObjects.framework/Resources/template_server.wsdd and
	 * JavaWebServicesClient.framework/Resources/template_client.wsdd and could
	 * be copied from there instead if we are able to cleanly detect their
	 * install location (mainly non OSX platforms).
	 */
	public void createWebServicesSupport(IProject project, TemplateEngine engine) {
		String path = null;

		if (getWizardType() == WizardType.WOMAVENPROJECT) {
			path = project.getLocation().append("src/main/resources").toOSString();
		} else {
			path = project.getLocation().toOSString();
		}

		if (_webservicesSupportPage != null) {
			if (_webservicesSupportPage.getClientSupport()) {
				engine.addTemplate(new TemplateDefinition("wowebservices" + "/client.wsdd.vm", path, "client.wsdd", "client.wsdd"));
			}

			if (_webservicesSupportPage.getServerSupport()) {
				engine.addTemplate(new TemplateDefinition("wowebservices" + "/server.wsdd.vm", path, "server.wsdd", "server.wsdd"));
			}
		}
	}

	/*
	 * Java Package Support
	 */
	/**
	 * @param project
	 * @param packagePath
	 */
	public void createJavaPackageSupport(IProject project, String packagePath) {
		String fullPath = project.getLocation().toOSString() + File.separator + "src";
		if (packagePath.length() > 0) {
			fullPath = fullPath + File.separator + packagePath;
		}
		File pFile = new File(fullPath);
		pFile.mkdirs();
	}

	protected class InstallProjectTemplateOperation extends InstallTemplateOperation {
		private IProjectDescription _projectDescription;

		private IProject[] _refProjects;

		public InstallProjectTemplateOperation(ProjectTemplate template, IProject project, IContainer targetContainer, IProjectDescription projectDescription, IProject[] refProjects) {
			super(template, project, targetContainer);
			_projectDescription = projectDescription;
			_refProjects = refProjects;
		}

		public InstallProjectTemplateOperation(ProjectTemplate template, IProject project, IContainer targetContainer, ISchedulingRule rule, IProjectDescription projectDescription, IProject[] refProjects) {
			super(template, project, targetContainer, rule);
			_projectDescription = projectDescription;
			_refProjects = refProjects;
		}

		@Override
		protected void preInstallTemplate(IProgressMonitor monitor) throws Exception {
			IProject project = getProject();
			project.create(_projectDescription, new SubProgressMonitor(monitor, 1000));
		}

		@Override
		protected void postInstallTemplate(IProgressMonitor monitor) throws Exception {
			NewWOProjectWizard.this.postInstallTemplate(getProject(), monitor);
		}

		@Override
		protected void finishInstallTemplate(IProgressMonitor monitor) throws Exception {
			IProject project = getProject();
			project.close(new SubProgressMonitor(monitor, 1000));
			project.open(new SubProgressMonitor(monitor, 1000));

			createEOModelSupport();
			createFrameworksSupport();
			addWebServiceFrameworks();

			if (_refProjects != null && _refProjects.length > 0) {
				IJavaProject javaProject = JavaCore.create(project);
				List<IClasspathEntry> classpathEntries = new LinkedList<IClasspathEntry>();
				for (IClasspathEntry classpathEntry : javaProject.getRawClasspath()) {
					classpathEntries.add(classpathEntry);
				}
				for (IProject referencedProject : _refProjects) {
					IEclipseFramework.Utility.addProjectToProject(referencedProject, javaProject, classpathEntries);
				}
				javaProject.setRawClasspath(classpathEntries.toArray(new IClasspathEntry[classpathEntries.size()]), monitor);
			}
		}

		/*
		 * EOModel Support
		 */
		// key = file name, value = full path to file including file name
		protected HashMap<String, String> getEOModelPaths() {
			if (_eomodelImportPage == null) {
				return new HashMap<String, String>();
			}
			return _eomodelImportPage.getModelPaths();
		}

		/**
		 * Called by subclassers that want EOModel support. Doesn't use the template
		 * engine.
		 * 
		 * @param project
		 *            to add support
		 */
		protected void createEOModelSupport() {
			if (getWizardType() != WizardType.WOMAVENPROJECT) {
				// Move any specified models over
				HashMap<String, String> paths = getEOModelPaths();
				EOModelImportWorkspaceJob job = new EOModelImportWorkspaceJob("eomodel import", paths, getProject(), null);
				job.schedule();
			} else {
				HashMap<String, String> paths = getEOModelPaths();
				EOModelImportWorkspaceJob job = new EOModelImportWorkspaceJob("eomodel import", paths, getProject(), getProject().getLocation().append("src/main/resources").toOSString());
				job.schedule();
			}
		}

		/*
		 * Frameworks Support
		 * 
		 * @param project
		 */
		protected void createFrameworksSupport() {
			if (getWizardType() != WizardType.WOMAVENPROJECT) {
				try {
					List<String> newFrameworks = new LinkedList<String>();
					if (_frameworkSupportPage != null) {
						if (_frameworkSupportPage.getJNDISupport()) {
							newFrameworks.add("JavaJNDIAdaptor");
						}

						if (_frameworkSupportPage.getJ2EESupport()) {
							newFrameworks.add("JavaWOJSPServlet");
						}
					}

					// Classpath surgery
					((ProjectFrameworkAdapter) getProject().getAdapter(ProjectFrameworkAdapter.class)).addFrameworksNamed(newFrameworks);
				} catch (Exception e) {
					WizardsPlugin.getDefault().log(e);
					ErrorDialog.openError(getShell(), Messages.getString("NewWOProjectWizard.errorMessage.classpath.title"), Messages.getString("NewWOProjectWizard.errorMessage.classpath.message"), new Status(IStatus.WARNING, "org.objectstyle.wolips.wizards", Messages.getString("NewWOProjectWizard.errorMessage.classpath.message")));
				}
			}
		}

		/**
		 * Should be invoked after createProject() is called and the IProject is
		 * open
		 * 
		 * @param project
		 */
		protected void addWebServiceFrameworks() {
			if (getWizardType() != WizardType.WOMAVENPROJECT) {
				try {
					List<String> newFrameworks = new LinkedList<String>();
					if (_webservicesSupportPage != null) {
						if (_webservicesSupportPage.getClientSupport()) {
							newFrameworks.add("JavaWebServicesClient");
						}

						if (_webservicesSupportPage.getServerSupport()) {
							newFrameworks.add("JavaWebServicesGeneration");
						}

						if (_webservicesSupportPage.getServerSupport() || _webservicesSupportPage.getClientSupport()) {
							newFrameworks.add("JavaWebServicesSupport");
						}
					}
					((ProjectFrameworkAdapter) getProject().getAdapter(ProjectFrameworkAdapter.class)).addFrameworksNamed(newFrameworks);
				} catch (Exception e) {
					WizardsPlugin.getDefault().log(e);
					ErrorDialog.openError(getShell(), Messages.getString("NewWOProjectWizard.errorMessage.classpath.title"), Messages.getString("NewWOProjectWizard.errorMessage.classpath.message"), new Status(IStatus.WARNING, "org.objectstyle.wolips.wizards", Messages.getString("NewWOProjectWizard.errorMessage.classpath.message")));
				}
			}
		}
	}
}
