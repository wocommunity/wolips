package org.objectstyle.wolips.natures;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * @author mnolte
 *
 */
public class WOFrameworkNature implements IProjectNature {

	/**
	 * Constructor for WOFrameworkNature.
	 */
	public WOFrameworkNature() {
		super();
	}

	/**
	 * @see org.eclipse.core.resources.IProjectNature#configure()
	 */
	public void configure() throws CoreException {
		// set your builder here!
	}

	/**
	 * @see org.eclipse.core.resources.IProjectNature#deconfigure()
	 */
	public void deconfigure() throws CoreException {
	}

	/**
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 */
	public IProject getProject() {
		return null;
	}

	/**
	 * @see org.eclipse.core.resources.IProjectNature#setProject(IProject)
	 */
	public void setProject(IProject project) {
	}

}
