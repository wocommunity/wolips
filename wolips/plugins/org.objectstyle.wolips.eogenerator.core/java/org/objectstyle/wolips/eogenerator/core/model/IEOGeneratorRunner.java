package org.objectstyle.wolips.eogenerator.core.model;

import org.eclipse.core.runtime.IProgressMonitor;

public interface IEOGeneratorRunner {
	public boolean generate(EOGeneratorModel model, StringBuffer results, IProgressMonitor monitor) throws Throwable;
}
