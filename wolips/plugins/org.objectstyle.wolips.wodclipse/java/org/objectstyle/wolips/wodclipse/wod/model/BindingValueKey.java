package org.objectstyle.wolips.wodclipse.wod.model;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.objectstyle.wolips.wodclipse.WodclipsePlugin;

public class BindingValueKey {
	private String myBindingName;

	private IMember myBindingMember;

	private IJavaProject myJavaProject;

	private IType myNextType;

	public BindingValueKey(String _bindingName, IMember _bindingMember, IJavaProject _javaProject) {
		myBindingName = _bindingName;
		myBindingMember = _bindingMember;
		myJavaProject = _javaProject;
	}

	public IType getDeclaringType() {
		return myBindingMember.getDeclaringType();
	}

	public String getBindingName() {
		return myBindingName;
	}

	public IMember getBindingMember() {
		return myBindingMember;
	}

	public String getNextTypeName() {
		try {
			String nextTypeName;
			if (myBindingMember instanceof IMethod) {
				nextTypeName = ((IMethod) myBindingMember).getReturnType();
			} else {
				nextTypeName = ((IField) myBindingMember).getTypeSignature();
			}
			return nextTypeName;
		} catch (JavaModelException e) {
			throw new RuntimeException("Failed to get the next type name for " + myBindingMember + ".", e);
		}
	}

	public IType getNextType() throws JavaModelException {
		if (myNextType == null) {
			String nextTypeName = getNextTypeName();
			IType typeContext = getDeclaringType();
			String resolvedNextTypeName = JavaModelUtil.getResolvedTypeName(nextTypeName, typeContext);
			if (resolvedNextTypeName == null) {
				WodclipsePlugin.getDefault().log("Failed to resolve type name " + nextTypeName + " in component " + typeContext.getElementName());
				myNextType = null;
			} else {
				myNextType = JavaModelUtil.findType(myJavaProject, resolvedNextTypeName);
			}
		}
		return myNextType;
	}

	public String toString() {
		return "[BindingKey: bindingName = " + myBindingName + "; bindingMember = " + myBindingMember + "]";
	}
}
