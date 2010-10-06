package org.objectstyle.wolips.jdt;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Platform;
import org.objectstyle.woenvironment.frameworks.Version;
import org.objectstyle.wolips.core.resources.types.project.ProjectAdapter;
import org.objectstyle.wolips.jdt.classpath.model.IEclipseFramework;
import org.objectstyle.wolips.variables.BuildProperties;
import org.objectstyle.wolips.variables.IBuildPropertiesInitializer;
import org.objectstyle.wolips.variables.VariablesPlugin;

public class WOBuildPropertiesInitializer implements IBuildPropertiesInitializer {
	public void initializeDefaults(BuildProperties buildProperties) {
		IProject project = buildProperties.getProject();
		if (!project.isAccessible()) {
			return;
		}

		try {
			ProjectFrameworkAdapter projectFrameworkAdaptor = (ProjectFrameworkAdapter) project.getAdapter(ProjectFrameworkAdapter.class);
			if (projectFrameworkAdaptor != null) {
				IEclipseFramework foundationFramework;
				// ... If you have the JavaFoundation source framework and you're actually talking to it right now, well .. we have
				// to special case that one. If a JavaFoundation framework falls in the forest, does it make a sound?
				if ("JavaFoundation".equals(project.getName())) {
					foundationFramework = JdtPlugin.getDefault().getFrameworkModel(project).getFrameworkWithName("JavaFoundation");
				}
				else {
					foundationFramework = projectFrameworkAdaptor.getLinkedFrameworkNamed("JavaFoundation");
				}
				if (foundationFramework != null) {
					Version version = foundationFramework.getVersion();
					if (version != null && !version.isUndefined()) {
						buildProperties.setWOVersionDefault(version);
					}
				}
				
				boolean wellFormedTemplateRequiredDefault;
				if (projectFrameworkAdaptor.isLinkedToFrameworkNamed("WOOgnl")) {
					buildProperties.setInlineBindingPrefixDefault("$");
					buildProperties.setInlineBindingSuffixDefault("");
					wellFormedTemplateRequiredDefault = false;
				} else if (buildProperties.getWOVersion().isAtLeastVersion(5, 4)) {
					buildProperties.setInlineBindingPrefixDefault("[");
					buildProperties.setInlineBindingSuffixDefault("]");
					wellFormedTemplateRequiredDefault = true;
				} else {
					buildProperties.setInlineBindingPrefixDefault("[");
					buildProperties.setInlineBindingSuffixDefault("]");
					wellFormedTemplateRequiredDefault = false;
				}
				String globalWellFormedTemplateRequiredDefault = Platform.getPreferencesService().getString("org.objectstyle.wolips.bindings", "WellFormedTemplate", null, null);
				if ("yes".equals(globalWellFormedTemplateRequiredDefault)) {
					wellFormedTemplateRequiredDefault = true;
				}
				else if ("no".equals(globalWellFormedTemplateRequiredDefault)) {
					wellFormedTemplateRequiredDefault = false;
				}
				buildProperties.setWellFormedTemplateRequiredDefault(VariablesPlugin.getDefault().getGlobalVariables().getBoolean("component.wellFormedTemplateRequired", wellFormedTemplateRequiredDefault));
				
				IEclipseFramework framework = projectFrameworkAdaptor.getFramework();
				if (framework != null) {
					buildProperties.setVersionDefault(framework.getVersion());
				}
			}
		} catch (Throwable e) {
			e.printStackTrace(System.out);
		}
	}
	

	public void initialize(BuildProperties buildProperties) {
		IProject project = buildProperties.getProject();
		if (!project.isAccessible()) {
			return;
		}

		String buildPropertiesVersionStr = VariablesPlugin.getDefault().getProjectVariables(project).getString("wolips.buildPropertiesVersion");
		int buildPropertiesVersion = buildPropertiesVersionStr == null ? Integer.MAX_VALUE : Integer.parseInt(buildPropertiesVersionStr);
		// MS: if wolips.buildPropertiesVersion is 0, then don't rename framework.name to project.name
		if (!buildProperties.hasValidProjectType() && buildPropertiesVersion > 0) {
			ProjectAdapter projectAdapter = (ProjectAdapter) project.getAdapter(ProjectAdapter.class);
			boolean framework = false;
			if (projectAdapter != null) {
				framework = projectAdapter.isFramework();
			}
			buildProperties.setFramework(framework);
			String projectName = buildProperties.getName();
			if (framework) {
				if (projectName == null) {
					String frameworkName = buildProperties.get("framework.name");
					if (frameworkName == null) {
						buildProperties.setName(project.getName());
					} else {
						buildProperties.setName(frameworkName);
					}
				} else {
					// reset it so we update the dependent properties
					buildProperties.setName(projectName);
				}
				buildProperties.remove("framework.name");
			} else {
				if (projectName == null) {
					buildProperties.setName(project.getName());
				} else {
					// reset it so we update the dependent properties
					buildProperties.setName(projectName);
				}
			}
		}
	}

}
