/*
 * Created on 15.01.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.objectstyle.wolips.templateengine;


/**
 * @author ulrich
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class WOLipsContext {
	public final static String Key = "WOLipsContext";
	
	private String projectName;
	private String adaptorName;
	private String componentName;
	private boolean createBodyTag = false;
	
	protected WOLipsContext() {
		super();
	}
	/**
	 * @return Returns the projectName.
	 */
	public String getProjectName() {
		return projectName;
	}
	/**
	 * @param projectName The projectName to set.
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public String getPluginName() {
		return TemplateEnginePlugin.getDefault().getDescriptor().getUniqueIdentifier();
	}
	/**
	 * @return Returns the adaptorName.
	 */
	public String getAdaptorName() {
		return adaptorName;
	}
	/**
	 * @param adaptorName The adaptorName to set.
	 */
	public void setAdaptorName(String adaptorName) {
		this.adaptorName = adaptorName;
	}
	/**
	 * @return Returns the createBodyTag.
	 */
	public boolean getCreateBodyTag() {
		return createBodyTag;
	}
	/**
	 * @param createBodyTag The createBodyTag to set.
	 */
	public void setCreateBodyTag(boolean createBodyTag) {
		this.createBodyTag = createBodyTag;
	}
	/**
	 * @return Returns the componentName.
	 */
	public String getComponentName() {
		return componentName;
	}
	/**
	 * @param componentName The componentName to set.
	 */
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
}
