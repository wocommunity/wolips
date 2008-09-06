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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class MethodSearch implements IRunnableWithProgress {

	private static final int MAX_RUN = 100;

	private static final String SOURCE_FOLDER = "src";

	private static final String COMPONENTS_FOLDER = "Components";

	private IJavaProject javaProject;

	private Hashtable<String, List<String>> declaredMethods; // key =

	// declaredClassName.methodName(paramterTypes),
	// value = ArrayList{handleID,
	// methodName, returnType, overrids}

	private Hashtable<String, List<String>> usedMethods; // key =

	// declaredClassName.methodName(paramterTypes),
	// value = ArrayList{handleIDs}

	private Hashtable<String, List<String>> unusedMethods; // key =

	// declaredClassName.methodName(paramterTypes),
	// value = ArrayList{handleID,
	// methodName, returnType}

	private Hashtable<String, List<String>> publicClassVariables; // key =

	// declaredClassName.variableName",
	// value = ArrayList{handleID, type}

	private Hashtable<String, String> possibleWodMethods; // key = wodName.methodCall, value =

	// "null"

	private Hashtable<String, List<String>> unusedClassVariables; // key =

	// declaredClassName.variableName",
	// value = ArrayList{handleID, type}

	private Hashtable<String, String> classDependencies; // key = className, value =

	// extendedByClassName

	private ASTParser parser;

	private IProgressMonitor monitor;

	private IProgressMonitor taskMonitor;

	private Pattern intPat = Pattern.compile("(.+) = (\\d+);");

	private Pattern booleanPat = Pattern.compile("(.+) = (true)|(false);");

	private Pattern stringPat = Pattern.compile("(.+) = \"(.*)\";");

	private Pattern woInternPat = Pattern.compile("(.+) = (.+)\\.@(\\w+);");

	private Pattern normPat = Pattern.compile("(.+) = (.+);");

	private Pattern appPat = Pattern.compile("(.+) = application\\.(.+);");

	private Pattern sesPat = Pattern.compile("(.+) = session\\.(.+);");

	private Pattern commentPat = Pattern.compile("//TODO Not used: (.*)\n");

	/**
	 * Constructor.
	 * 
	 * @param javaProject
	 */
	public MethodSearch(IJavaProject javaProject) {
		this.javaProject = javaProject;
		unusedMethods = new Hashtable<String, List<String>>();
		declaredMethods = new Hashtable<String, List<String>>();
		usedMethods = new Hashtable<String, List<String>>();
		publicClassVariables = new Hashtable<String, List<String>>();
		possibleWodMethods = new Hashtable<String, String>();
		unusedClassVariables = new Hashtable<String, List<String>>();
		classDependencies = new Hashtable<String, String>();

		this.monitor = new NullProgressMonitor();
	}

	/**
	 * Method is started when IProgressMonitorDialog.run() is called.
	 */
	public void run(IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException {
		this.taskMonitor = progressMonitor;
		taskMonitor.beginTask("Search for unused WO code", MAX_RUN);

		if (!searchForMethods()) {
			throw new InterruptedException();
		}

		taskMonitor.subTask("checking declared methods against vocated methods");
		if (!fillUnusedMethodsHash()) {
			throw new InterruptedException();
		}

		if (!checkSettersAndGettersFromClassVariables()) {
			throw new InterruptedException();
		}

		if (taskMonitor.isCanceled()) {
			throw new InterruptedException();
		}

		if (!checkWodMethods()) {
			throw new InterruptedException();
		}

		taskMonitor.done();
	}

	/**
	 * Method searches all .java and .wod files and fills the hashtables for
	 * later use.
	 * 
	 * @return true if taskMonitor is not canceled
	 * @throws InvocationTargetException
	 */
	private boolean searchForMethods() throws InvocationTargetException {
		// System.out.println("############### SearchForMethods()
		// #################");
		// System.out.println("############### Java #################");

		try {
			// project source folder: java files
			IPackageFragmentRoot[] packageFragmentRoots = javaProject.getAllPackageFragmentRoots();
			IPackageFragmentRoot scr = null;
			for (int i = 0; i < packageFragmentRoots.length; i++) {
				IPackageFragmentRoot root = packageFragmentRoots[i];
				if (root.getHandleIdentifier().equals("=" + javaProject.getElementName() + "/" + SOURCE_FOLDER))
					scr = root;
			}

			if (scr != null) {
				// IPackageFragments:
				IJavaElement[] IPackageFragments = scr.getChildren();
				for (int i = 0; i < IPackageFragments.length; i++) {
					IPackageFragment packageFragment = (IPackageFragment) IPackageFragments[i];

					// ICompilationUnits:
					ICompilationUnit[] iCompUnits = packageFragment.getCompilationUnits();
					for (int j = 0; j < iCompUnits.length; j++) {
						ICompilationUnit iComp = iCompUnits[j];
						String packageName = "";
						IPackageDeclaration[] packageBindings = iComp.getPackageDeclarations();
						for (int k = 0; k < packageBindings.length; k++) {
							packageName += packageBindings[k];
						}
						// exclusive stubs
						if (!iComp.getElementName().startsWith("_")) {
							parser = ASTParser.newParser(AST.JLS3);
							parser.setResolveBindings(true);
							parser.setSource(iComp);
							CompilationUnit astRoot = (CompilationUnit) parser.createAST(monitor);
							// //System.out.println(" -
							// "+iComp.getElementName());
							taskMonitor.subTask("searching " + iComp.getElementName());
							// check the compilation unit for used and declared
							// methods and for class variables
							ASTVisitor astVisitor = new ASTMethodExplorer(usedMethods, declaredMethods, publicClassVariables, classDependencies, iComp);
							astRoot.accept(astVisitor);
						}
					}
					taskMonitor.worked(1);
					if (taskMonitor.isCanceled())
						return false;
				}
			}

			// Components files: .wod
			// System.out.println("############### WOD #################");
			Object[] nonJava = javaProject.getNonJavaResources();
			for (int i = 0; i < nonJava.length; i++) {
				Object object = nonJava[i];
				if (object instanceof IFolder) {
					IFolder folder = (IFolder) object;

					// Components
					if (folder.getName().equals(COMPONENTS_FOLDER)) {
						IResource[] res = folder.members();
						for (int j = 0; j < res.length; j++) {

							// .wo
							if (res[j] instanceof IFolder) {
								IFolder wo = (IFolder) res[j];

								// .wod
								String wodName = wo.getName().substring(0, wo.getName().indexOf(".")) + ".wod";
								IFile wod = wo.getFile(wodName);
								if (wod != null) {
									// //System.out.println(" -
									// "+wod.getName());
									taskMonitor.subTask("searching " + wod.getName());
									checkWOD(wod);
								}
							}
						}
					}
				}
				taskMonitor.worked(1);
				if (taskMonitor.isCanceled())
					return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvocationTargetException(e, e.toString());
		}
		return true;
	}

	/**
	 * Method checks a .wod file for method or variable calls
	 * 
	 * @param wod
	 * @throws CoreException
	 * @throws IOException
	 * @return true if taskMonitor is not canceled
	 */
	private boolean checkWOD(IFile wod) throws CoreException, IOException {
		String wodName = wod.getName().substring(0, wod.getName().indexOf("."));

		BufferedReader in = new BufferedReader(new InputStreamReader(wod.getContents()));

		String line;
		while ((line = in.readLine()) != null) {

			Matcher intMat = intPat.matcher(line);
			Matcher woInternMat = woInternPat.matcher(line);
			Matcher booleanMat = booleanPat.matcher(line);
			Matcher stringMat = stringPat.matcher(line);
			Matcher normMat = normPat.matcher(line);
			Matcher appMat = appPat.matcher(line);
			Matcher sesMat = sesPat.matcher(line);

			if (intMat.find()) { // do nothing
			} else if (woInternMat.find()) { // do nothing
			} else if (booleanMat.find()) { // do nothing
			} else if (stringMat.find()) { // do nothing
			} else if (appMat.find()) { // application methods
				possibleWodMethods.put("Application." + appMat.group(2), "null");
			} else if (sesMat.find()) { // session methods
				possibleWodMethods.put("Session." + sesMat.group(2), "null");
			} else if (normMat.find()) { // possible method or variable calls
				possibleWodMethods.put(wodName + "." + normMat.group(2), "null");
			}

			if (taskMonitor.isCanceled()) {
				in.close();
				return false;
			}
		}
		in.close();
		return true;
	}

	/**
	 * Method checks the declared methods against the used methods and fills the
	 * unusedMethods-Hashtable.
	 * 
	 * @return true if taskMonitor is not canceled
	 */
	private boolean fillUnusedMethodsHash() {
		// System.out.println("################## fillUnusedMethodsHash() " +
		// declaredMethods.size() + " ##################");
		Enumeration keysEnum = declaredMethods.keys();
		while (keysEnum.hasMoreElements()) {
			String key = (String) keysEnum.nextElement();
			if (!usedMethods.containsKey(key)) {
				List<String> value = declaredMethods.get(key);
				String skip = value.get(3);
				if (skip.equals("false")) {
					unusedMethods.put(key, value);
				}
			}
		}
		taskMonitor.worked(1);
		if (taskMonitor.isCanceled())
			return false;
		return true;
	}

	/**
	 * Method removes the setters and getters for the classVariables (WO
	 * specific).
	 * 
	 * @return true if taskMonitor is not canceled
	 */
	private boolean checkSettersAndGettersFromClassVariables() {
		// System.out.println("################ Check Setter/Getter
		// ##################");
		Enumeration keyEnum = publicClassVariables.keys();
		while (keyEnum.hasMoreElements()) {
			String classVariable = (String) keyEnum.nextElement();
			String[] comps = classVariable.split("\\.");
			String className = comps[0];
			String methodName = comps[1];

			methodName = Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1);
			String key;

			// setter
			ArrayList value = (ArrayList) publicClassVariables.get(classVariable);
			String arg = (String) value.get(1);
			key = className + ".set" + methodName + "(" + arg + ")";
			unusedMethods.remove(key);

			// getter
			key = className + ".get" + methodName + "()";
			unusedMethods.remove(key);
		}
		taskMonitor.worked(1);
		if (taskMonitor.isCanceled())
			return false;
		return true;
	}

	/**
	 * Method checks the possibleWodMethods against the unusedClassVaraibles and
	 * unusedMethods
	 * 
	 * @return true if taskMonitor is not canceled
	 */
	private boolean checkWodMethods() {
		// System.out.println("###################### checkWodMethods() " +
		// possibleWodMethods.size() + " ######################");

		unusedClassVariables = new Hashtable<String, List<String>>(publicClassVariables);

		Enumeration keyEnum = possibleWodMethods.keys();
		while (keyEnum.hasMoreElements()) {
			String call = (String) keyEnum.nextElement();

			// //System.out.println("call: "+call);
			ArrayList<String> splitList = new ArrayList<String>();

			String[] split = call.split("\\.");
			for (int i = 0; i < split.length; i++) {
				splitList.add(split[i]);
			}

			String className = splitList.remove(0);

			// direct class
			recursiveMethodFinder(className, new ArrayList<String>(splitList));
			// extended by
			recursiveMethodFinder(classDependencies.get(className), splitList);

		}
		taskMonitor.worked(1);
		if (taskMonitor.isCanceled())
			return false;
		return true;
	}

	/**
	 * Recursive method for finding the appropriate methods and class variables
	 * in java from the possibleWodMethods
	 * 
	 * @param className
	 * @param splitList
	 */
	private void recursiveMethodFinder(String className, List<String> splitList) {
		try {
			String local = splitList.remove(0);
			String key = className + "." + local; // key = "className.call"

			// local method call
			// declaredClassName.methodName(paramterTypes)
			String key2 = key + "()";
			if (unusedMethods.containsKey(key2)) {
				unusedMethods.remove(key2);
			}

			// class variable in component
			if (publicClassVariables.containsKey(key)) {
				unusedClassVariables.remove(key);
				if (splitList.size() > 0) { // method of local variable =>
					// recursion
					List<String> value = publicClassVariables.get(key);
					String newClass = value.get(1);
					recursiveMethodFinder(newClass, splitList);
				} else { // local variable
					// //System.out.println("local variable: "+className+"
					// "+local);
				}
			}

			// direct method call
			if (declaredMethods.containsKey(key2)) {
				if (splitList.size() > 0) { // return type of method =>
					// recursion
					ArrayList value = (ArrayList) declaredMethods.get(key2);
					String newClass = (String) value.get(2);
					recursiveMethodFinder(newClass, splitList);
				}
			}

			// no method or variable call or method from stubs
			else {
				// //System.out.println("no method or variable: "+key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Method sets the comments for unused code at the beginnig of the
	 * appropiate java file.
	 * 
	 * @return true if taskMonitor is not canceled
	 * @throws InvocationTargetException
	 */
	public boolean setComments() throws InvocationTargetException {
		// System.out.println("################### setComments()
		// ####################");

		// methods
		Enumeration keysEnum = unusedMethods.keys();
		while (keysEnum.hasMoreElements()) {
			String key = (String) keysEnum.nextElement();
			ArrayList value = (ArrayList) unusedMethods.get(key);
			String handleID = (String) value.get(0);
			String name = key.split("\\.")[1];

			try {
				ICompilationUnit iCompUnit = (ICompilationUnit) JavaCore.create(handleID);
				String source = iCompUnit.getBuffer().getContents();
				String comment = "//TODO Not used: Method " + name + "\n";
				if (checkComment(iCompUnit, comment)) {
					iCompUnit.getBuffer().setContents(comment + source);
					iCompUnit.save(monitor, true);
				}

				taskMonitor.worked(1);
				if (taskMonitor.isCanceled()) {
					return false;
				}
			} catch (Exception e) {
				throw new InvocationTargetException(e, e.toString());
			}
		}

		// class variables
		keysEnum = unusedClassVariables.keys();
		while (keysEnum.hasMoreElements()) {
			String key = (String) keysEnum.nextElement();
			ArrayList value = (ArrayList) unusedClassVariables.get(key);
			String handleID = (String) value.get(0);
			String name = key.split("\\.")[1];

			try {
				ICompilationUnit iCompUnit = (ICompilationUnit) JavaCore.create(handleID);
				String source = iCompUnit.getBuffer().getContents();
				String comment = "//TODO Not used: Class variable " + name + "\n";
				if (checkComment(iCompUnit, comment)) {
					iCompUnit.getBuffer().setContents(comment + source);
					iCompUnit.save(monitor, true);
				}
				taskMonitor.worked(1);
				if (taskMonitor.isCanceled()) {
					return false;
				}
			} catch (Exception e) {
				throw new InvocationTargetException(e, e.toString());
			}
		}
		return true;
	}

	/**
	 * Method checks if this comment is already present in the java file.
	 * 
	 * @param iCompUnit
	 * @param comment
	 * @return true if comment is not already present
	 * @throws Exception
	 */
	private boolean checkComment(ICompilationUnit iCompUnit, String comment) throws Exception {
		String source = iCompUnit.getBuffer().getContents();
		Matcher mat = commentPat.matcher(source);
		while (mat.find()) {
			if (mat.group().equals(comment)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Method writes a text file with a list of all unused code to the given
	 * output file.
	 * 
	 * @param outputFile
	 */
	public void writePossiblyUnusedMethodsToFile(File outputFile) throws InvocationTargetException {
		try {
			NameComparator comparator = new NameComparator();
			FileWriter writer = new FileWriter(outputFile);
			writer.write("############ unused methods #############\n\n");

			String[] keyArray = unusedMethods.keySet().toArray(new String[0]);
			Arrays.sort(keyArray, comparator);
			for (int i = 0; i < keyArray.length; i++) {
				String key = keyArray[i];
				writer.write(key + "\n");
			}

			writer.write("\n########## unused class variables ##########\n\n");
			keyArray = unusedClassVariables.keySet().toArray(new String[0]);
			Arrays.sort(keyArray, comparator);
			for (int i = 0; i < keyArray.length; i++) {
				String key = keyArray[i];
				writer.write(key + "\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new InvocationTargetException(e, e.toString());
		}
	}
}
