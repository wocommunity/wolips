package org.objectstyle.woproject.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.objectstyle.woenvironment.env.WOEnvironment;

public class WOLipsPropertiesTask extends Task {
	@Override
	@SuppressWarnings("unchecked")
	public void execute() throws BuildException {
		if (getProject().getProperty("wolips.properties") == null) {
			WOEnvironment environment = new WOEnvironment(getProject().getProperties());
			File wolipsPropertiesFile = environment.getWOVariables().getWOLipsPropertiesFile();
			if (wolipsPropertiesFile != null) {
				getProject().setProperty("wolips.properties", wolipsPropertiesFile.getAbsolutePath());
			}
		}
	}
}
