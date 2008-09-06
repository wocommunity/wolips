/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2006 The ObjectStyle Group,
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne"
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */
package org.objectstyle.wolips.core.tobeintregrated;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class ASTMethodExplorer extends ASTVisitor {

	Hashtable<String, List<String>> usedMethods;

	Hashtable<String, List<String>> declaredMethods;

	Hashtable<String, List<String>> publicClassVariables;

	Hashtable<String, String> classDependencies;

	ICompilationUnit iCompUnit;

	public ASTMethodExplorer(Hashtable<String, List<String>> myUsedMethods, Hashtable<String, List<String>> myDeclaredMethods, Hashtable<String, List<String>> myClassVariables, Hashtable<String, String> myClassDependencies, ICompilationUnit iComp) {
		super(true);
		usedMethods = myUsedMethods;
		declaredMethods = myDeclaredMethods;
		publicClassVariables = myClassVariables;
		classDependencies = myClassDependencies;
		iCompUnit = iComp;
	}

	public boolean visit(ClassInstanceCreation node) {
		IMethodBinding binding = node.resolveConstructorBinding();
		if (binding != null) {
			List<String> nodesID;
			String handleID = iCompUnit.getHandleIdentifier();

			ITypeBinding[] params = binding.getParameterTypes();
			String paramString = "(";
			for (int i = 0; i < params.length; i++) {
				paramString += params[i].getName();
				if (i < params.length - 1)
					paramString += ",";
			}
			paramString += ")";
			String key = binding.getDeclaringClass().getName() + "." + binding.getName() + paramString;

			if (usedMethods.containsKey(key)) {
				nodesID = usedMethods.get(key);
				nodesID.add(handleID);
			} else {
				nodesID = new ArrayList<String>();
				nodesID.add(handleID);
			}
			usedMethods.put(key, nodesID);
		}
		return true;
	}

	public boolean visit(MethodInvocation node) {
		IMethodBinding binding = node.resolveMethodBinding();
		if (binding != null) {
			List<String> nodesID;
			String handleID = iCompUnit.getHandleIdentifier();

			ITypeBinding[] params = binding.getParameterTypes();
			String paramString = "(";
			for (int i = 0; i < params.length; i++) {
				paramString += params[i].getName();
				if (i < params.length - 1)
					paramString += ",";
			}
			paramString += ")";
			String key = binding.getDeclaringClass().getName() + "." + binding.getName() + paramString;

			if (usedMethods.containsKey(key)) {
				nodesID = usedMethods.get(key);
				nodesID.add(handleID);
			} else {
				nodesID = new ArrayList<String>();
				nodesID.add(handleID);
			}
			usedMethods.put(key, nodesID);
		}
		return true;
	}

	public boolean visit(MethodDeclaration node) {
		IMethodBinding binding = node.resolveBinding();
		if (binding != null) {
			ITypeBinding[] params = binding.getParameterTypes();
			String paramString = "(";
			for (int i = 0; i < params.length; i++) {
				paramString += params[i].getName();
				if (i < params.length - 1)
					paramString += ",";
			}
			paramString += ")";
			String key = binding.getDeclaringClass().getName() + "." + binding.getName() + paramString;
			ITypeBinding declaredClassBinding = binding.getDeclaringClass();

			// overrides super method
			ITypeBinding superClassBinding = declaredClassBinding.getSuperclass();
			boolean skip = checkSuperClass(binding, superClassBinding);

			// is implementation of interface
			ITypeBinding[] interfaceBindings = declaredClassBinding.getInterfaces();
			skip = skip || checkInterfaces(binding, interfaceBindings);

			if (superClassBinding != null) {
				// classDependencies
				classDependencies.put(declaredClassBinding.getName(), superClassBinding.getName());

				// WOComponent && title()
				boolean isWOComponent = superClassBinding.getName().equals("WOComponent");
				skip = skip || (isWOComponent && binding.getName().equals("title"));
			}

			// DirectAction.Action()
			boolean isDirectAction = declaredClassBinding.getName().equals("DirectAction");
			skip = skip || (isDirectAction && (binding.getName().endsWith("Action") || binding.getName().equals("performActionNamed")));

			// app || logic && Constructor
			IPackageBinding packageBinding = declaredClassBinding.getPackage();
			String[] comps = packageBinding.getNameComponents();
			if (comps.length > 0) {
				String comp = comps[comps.length - 1];
				skip = skip || ((comp.equals("app") || comp.equals("logic")) && binding.isConstructor());
			}

			if (!usedMethods.containsKey(key)) {
				List<String> value = new ArrayList<String>();
				value.add(iCompUnit.getHandleIdentifier());
				value.add(binding.getName());
				value.add(binding.getReturnType().getName());
				value.add(skip + "");
				declaredMethods.put(key, value);
			}
		}
		return true;
	}

	/**
	 * Method checks the super classes for overridden methods and returns true
	 * if so.
	 * 
	 * @param binding
	 * @param superClassBinding
	 * @return true if binding overrides any method in super classes
	 */
	private boolean checkSuperClass(IMethodBinding binding, ITypeBinding superClassBinding) {
		boolean hit = false;
		if (superClassBinding != null) {
			IMethodBinding[] superMethods = superClassBinding.getDeclaredMethods();
			for (int i = 0; i < superMethods.length; i++) {
				if (binding.overrides(superMethods[i])) {
					// System.out.println(binding.getKey()+" overrides
					// "+superMethods[i].getKey());
					hit = true;
				}
			}
			if (hit == false) {
				hit = checkSuperClass(binding, superClassBinding.getSuperclass());
			}
		}
		return hit;
	}

	/**
	 * Method checks if method is implementation of interface.
	 * 
	 * @param binding
	 * @param interfaceBindings
	 * @return
	 */
	private boolean checkInterfaces(IMethodBinding binding, ITypeBinding[] interfaceBindings) {
		boolean hit = false;
		for (int i = 0; i < interfaceBindings.length; i++) {
			IMethodBinding[] interfaceMethods = interfaceBindings[i].getDeclaredMethods();
			for (int j = 0; j < interfaceMethods.length; j++) {
				if (binding.overrides(interfaceMethods[j])) {
					hit = true;
				}
			}
			if (hit == false) {
				hit = checkInterfaces(binding, interfaceBindings[i].getInterfaces());
			}
		}
		return hit;
	}

	/**
	 * method checks if class variable is "public" and not "public static final"
	 * and puts it into publicClassVariables if true.
	 */
	public boolean visit(VariableDeclarationFragment node) {
		IVariableBinding binding = node.resolveBinding();
		if (node.getParent().getNodeType() == 23) { // public class variables
			String call = node.getParent().toString();
			boolean isPublic = call.startsWith("public") && !call.startsWith("public static final") && !call.startsWith("public final static");
			if (binding != null && isPublic) {
				String key = binding.getDeclaringClass().getName() + "." + binding.getName();
				List<String> value = new ArrayList<String>();
				value.add(iCompUnit.getHandleIdentifier());
				value.add(binding.getType().getName());
				publicClassVariables.put(key, value);
			}
		}
		return true;
	}

}
