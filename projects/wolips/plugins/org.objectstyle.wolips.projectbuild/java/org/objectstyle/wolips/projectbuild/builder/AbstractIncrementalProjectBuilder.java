/*
 * Created on 24.07.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.objectstyle.wolips.projectbuild.builder;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.objectstyle.wolips.datasets.listener.PatternsetDeltaVisitor;
import org.objectstyle.wolips.projectbuild.ProjectBuildPlugin;

/**
 * @author ulrich
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class AbstractIncrementalProjectBuilder extends IncrementalProjectBuilder {

	private BuildResourceValidator buildResourceValidator = new BuildResourceValidator();
	
	/**
	 * Method projectNeedsAnUpdate.
	 * 
	 * @return boolean
	 */
	protected boolean projectNeedsAnUpdate() {
		PatternsetDeltaVisitor patternsetDeltaVisitor = new PatternsetDeltaVisitor();
		this.buildResourceValidator.reset();
		if(this.getProject() == null
				|| this.getDelta(this.getProject()) == null)
			return false;
		try {
			this.getDelta(this.getProject()).accept(patternsetDeltaVisitor);
			this.getDelta(this.getProject()).accept(this.buildResourceValidator);
		} catch (CoreException e) {
			ProjectBuildPlugin.getDefault().getPluginLogger().log(e);
			return false;
		}
		return this.buildResourceValidator.isBuildRequired();
	}
	
}
