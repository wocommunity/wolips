package org.objectstyle.wolips.bindings.woo;

import java.io.InputStream;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.objectstyle.wolips.bindings.wod.TypeCache;
import org.objectstyle.wolips.bindings.wod.WodProblem;

public interface IWooModel {
	public String getName();

	public List<WodProblem> getProblems(IJavaProject javaProject, IType type, TypeCache typeCache, IEOModelGroupCache modelGroupCache);

	public void parseModel();
	
	public void loadModelFromStream(final InputStream input) throws Throwable;

}
