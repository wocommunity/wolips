package org.objectstyle.wolips.eogenerator.core.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.objectstyle.wolips.eogenerator.core.Activator;

public class MarkerEOGeneratorListener implements IEOGeneratorListener {
	public void eogeneratorFailed(IFile eogenFile, String results) {
		try {
			eogenFile.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
			IMarker error = eogenFile.createMarker(IMarker.PROBLEM);
			error.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			error.setAttribute(IMarker.MESSAGE, "EOGenerator Failed (Run manually for details)");
		} catch (CoreException e) {
      e.printStackTrace();
			Activator.getDefault().log(e);
		}
	}

	public void eogeneratorFinished() {
		// DO NOTHING
	}

	public void eogeneratorStarted() {
		// DO NOTHING
	}

	public void eogeneratorSucceeded(IFile eogenFile, String results) {
		try {
			eogenFile.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
		  e.printStackTrace();
			Activator.getDefault().log(e);
		}
	}

}
