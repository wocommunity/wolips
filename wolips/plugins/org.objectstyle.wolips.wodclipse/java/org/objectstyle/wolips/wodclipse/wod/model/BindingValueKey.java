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
	private String _bindingName;

	private IMember _bindingMember;

	private IJavaProject _javaProject;

	private IType _nextType;
	
	private String _helperFunction;

	public BindingValueKey(String bindingName, IMember bindingMember, IJavaProject javaProject, String helperFunction) {
		_bindingName = bindingName;
		_bindingMember = bindingMember;
		_javaProject = javaProject;
		_helperFunction = helperFunction;
	}

	public IType getDeclaringType() {
		return _bindingMember.getDeclaringType();
	}

	public String getBindingName() {
		return _bindingName;
	}

	public IMember getBindingMember() {
		return _bindingMember;
	}
	
	public String getHelperFunction() {
		return _helperFunction;
	}

	public String getNextTypeName() {
		try {
			String nextTypeName;
			if (_bindingMember instanceof IMethod) {
				nextTypeName = ((IMethod) _bindingMember).getReturnType();
			} else {
				nextTypeName = ((IField) _bindingMember).getTypeSignature();
			}
			return nextTypeName;
		} catch (JavaModelException e) {
			throw new RuntimeException("Failed to get the next type name for " + _bindingMember + ".", e);
		}
	}

	public IType getNextType() throws JavaModelException {
		if (_nextType == null) {
			String nextTypeName = getNextTypeName();
			IType typeContext = getDeclaringType();
			String resolvedNextTypeName = JavaModelUtil.getResolvedTypeName(nextTypeName, typeContext);
			if (resolvedNextTypeName == null) {
				WodclipsePlugin.getDefault().log("Failed to resolve type name " + nextTypeName + " in component " + typeContext.getElementName());
				_nextType = null;
			} else {
				_nextType = JavaModelUtil.findType(_javaProject, resolvedNextTypeName);
			}
		}
		return _nextType;
	}

	public String toString() {
		return "[BindingKey: bindingName = " + _bindingName + "; bindingMember = " + _bindingMember + "]";
	}
}
