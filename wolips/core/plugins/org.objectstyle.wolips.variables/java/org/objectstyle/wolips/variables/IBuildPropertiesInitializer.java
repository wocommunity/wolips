package org.objectstyle.wolips.variables;

public interface IBuildPropertiesInitializer {
	public void initializeDefaults(BuildProperties buildProperties);
	
	public void initialize(BuildProperties buildProperties);
}
