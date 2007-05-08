package org.objectstyle.wolips.eogenerator.core.model;

import org.eclipse.core.resources.IFile;

public interface IEOGeneratorListener {
	public void eogeneratorStarted();

	public void eogeneratorSucceeded(IFile eogenFile, String results);

	public void eogeneratorFailed(IFile eogenFile, String results);

	public void eogeneratorFinished();
}
