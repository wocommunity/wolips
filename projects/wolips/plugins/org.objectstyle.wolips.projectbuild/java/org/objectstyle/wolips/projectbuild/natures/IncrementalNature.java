/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 The ObjectStyle Group 
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

package org.objectstyle.wolips.projectbuild.natures;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.objectstyle.wolips.core.logging.WOLipsLog;
import org.objectstyle.wolips.core.project.IWOLipsProject;
import org.objectstyle.wolips.core.project.WOLipsCore;
import org.objectstyle.wolips.projectbuild.WOProjectBuildConstants;

/**
 * @author Harald Niesche
 * 
 * 
 */
public class IncrementalNature
	implements IProjectNature, WOProjectBuildConstants
{

	/**
	 * Constructor for WebObjectsNature.
	 */
	public IncrementalNature() {
		super();
	}

	/**
	 * @see org.eclipse.core.resources.IProjectNature#configure()
	 */
	public void configure() throws CoreException {
    IProject project = getProject();

	IWOLipsProject wolipsProject = WOLipsCore.createProject(project);
	wolipsProject.getBuilderAccessor().installIncrementalBuilder();

 		IFolder buildFolder = project.getFolder("build");
		if (!buildFolder.exists()) {
			buildFolder.create(IResource.FORCE, true, null);
		}
		buildFolder.setDerived(true);

		IPath outputPath =
			((IJavaProject) project.getNature(JavaCore.NATURE_ID))
				.getOutputLocation();
		IFolder outputFolder = project.getFolder(outputPath);

		System.out.println(outputFolder.isDerived());
	}

	/**
	 * @see org.eclipse.core.resources.IProjectNature#deconfigure()
	 */
	public void deconfigure() throws CoreException {
		IProject project = getProject();

		IWOLipsProject wolipsProject = WOLipsCore.createProject(project);
		wolipsProject.getBuilderAccessor().removeIncrementalBuilder();

		IFolder buildFolder = project.getFolder("build");
		if (buildFolder.exists() && buildFolder.isDerived()) {
			buildFolder.delete(true, false, null);
		}
	}

	/**
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 */
	public IProject getProject() {
		return _project;
	}

	/**
	 * @see org.eclipse.core.resources.IProjectNature#setProject(IProject)
	 */
	public void setProject(IProject project) {
		_project = project;
   }

	/* ************************************************************************ */

	/**
	 * 
	 */
	public IJavaProject getJavaProject() {
		try {
			return ((IJavaProject) getProject().getNature(JavaCore.NATURE_ID));
		} catch (CoreException up) {
		}

		return (null);
	}

//	public String getStringProperty(String key) {
//		String result = "";
//		try {
//			result =
//				getProject().getPersistentProperty(new QualifiedName("", key));
//		} catch (CoreException up) {
//			up.printStackTrace();
//		}
//
//		return (result);
//	}
//
//	public void setStringProperty(String key, String value) {
//		try {
//			getProject().setPersistentProperty(
//				new QualifiedName("", key),
//				value);
//		} catch (CoreException up) {
//			up.printStackTrace();
//		}
//
//	}
//
//	public boolean getBooleanProperty(String key) {
//		return ("true".equals(getStringProperty(key)));
//	}
//
//	public void setBooleanProperty(String key, boolean value) {
//		setStringProperty(key, value ? "true" : "false");
//	}

	public boolean isFramework() {
		//return (getBooleanProperty(FRAMEWORK_PROPERTY));
    try {
		IWOLipsProject woLipsProject =
								WOLipsCore.createProject(this.getProject());
      return (woLipsProject.getNaturesAccessor().isFramework());
    } catch (CoreException up) {
      WOLipsLog.log(up.getStatus());
    }
    
    return false;
	}

//	public void setIsFramework(boolean isFramework) {
//		setBooleanProperty(FRAMEWORK_PROPERTY, isFramework);
//	}

	/**
	 * either name.woa or name.framework
	 */
	public String getResultName() {
		String name = getProject().getName();

		if (isFramework()) {
			return (name + ".framework");
		} else {
			return (name + ".woa");
		}
	}

	public IPath getBuildPath() {
		return (getProject().getFullPath().append("build"));
	}

	public IPath getResultPath() {
		return (getBuildPath().append(getResultName()));
	}

	public IPath getInfoPath() {
		if (isFramework()) {
			return (getResultPath().append("Resources"));
		} else {
			return (getResultPath().append(APPINFO_PATH));
		}
	}

	public IPath _getResultPath() {
		if (isFramework()) {
			return (getResultPath());
		} else {
			return (getResultPath().append(APPINFO_PATH));
		}
	}

	public IPath getResourceOutputPath() {
		return (_getResultPath().append(RESOURCE_PATH));
	}

	public IPath getJavaOutputPath() {
		return (_getResultPath().append(JAVA_PATH));
	}

	public IPath getWebResourceOutputPath() {
		return (_getResultPath().append(WEBRESOURCE_PATH));
	}

	public String getResourceName() {
		String result = "";
		if (!isFramework()) {
			result += APPINFO_PATH + "/";
		}
		result += RESOURCE_PATH;
		return (result);
	}

	public String getWebResourceName() {
		String result = "";
		if (!isFramework()) {
			result += APPINFO_PATH + "/";
		}
		result += WEBRESOURCE_PATH;
		return (result);
	}

	/* ************************************************************************ */

//	public IPath getDestinationPath(IResource res) {
//
//		IPath fullPath = res.getFullPath();
//
//		if (getBuildPath().isPrefixOf(fullPath)) {
//			return (null);
//		}
//
//		try {
//			if (!getJavaProject()
//				.getOutputLocation()
//				.equals(getJavaProject().getPath())
//				&& getJavaProject().getOutputLocation().isPrefixOf(fullPath)) {
//				return (null);
//			}
//		} catch (CoreException up) {
//			up.printStackTrace();
//		}
//
//		IPath result = asResourcePath(fullPath, res);
//		if (null == result)
//			result = asWebResourcePath(fullPath, res);
//
//		return (result);
//	}

	private IPath _appendSpecial(IPath p1, IPath p2) {
		String segments[] = p2.segments();

		int n = segments.length - 1;
		for (int i = n; i >= 0; --i) {
			if (segments[i].endsWith(".lproj")) {
				n = i;
			}
		}
		IPath tmp = p1;
		while (n < segments.length) {
			tmp = tmp.append(segments[n++]);
		}
		return (tmp);
	}

	public IPath asResourcePath(IPath path, IResource res) {

		/*if (IResource.FOLDER == res.getType()) {
		  if (
		    lastSegment.endsWith (".wo")
		    || lastSegment.endsWith (".eomodeld")
		  ) {
		    return (getResourceOutputPath().append(lastSegment));
		  }
		} else */
		if (IResource.FILE == res.getType()) {
			String lastSegment = path.lastSegment();
			if (
        (-1 == path.toString().indexOf(".eomodeld/"))
        && (-1 == path.toString().indexOf(".wo/"))
			) {
				return (_appendSpecial(getResourceOutputPath(), path));
			}

			String parentName = res.getParent().getName();
			if (parentName.endsWith(".wo")
				|| parentName.endsWith(".eomodeld")) {
				return (
					_appendSpecial(
						getResourceOutputPath(),
						res.getParent().getProjectRelativePath()).append(
						lastSegment));
				//return (getResourceOutputPath().append(parentName).append(lastSegment));
			}

		}

		return (null);
	}

	public IPath asWebResourcePath(IPath path, IResource res) {
		if (IResource.FILE == res.getType()) {
			return _appendSpecial(getWebResourceOutputPath(), path);
		}

		return (null);
	}

	/* ************************************************************************ */
//  public static IncrementalNature s_getNature(IProject project) {
//    try {
//      if (project.hasNature(INCREMENTAL_APPLICATION_NATURE_ID)) {
//        return (IncrementalNature) project.getNature(
//          INCREMENTAL_APPLICATION_NATURE_ID
//        );
//      } else if (project.hasNature(INCREMENTAL_FRAMEWORK_NATURE_ID)) {
//        return (IncrementalNature) project.getNature(
//          INCREMENTAL_FRAMEWORK_NATURE_ID
//        );
//      }
//
//    } catch (CoreException exception) {
//      WOLipsLog.log(exception);
//    }
//    return null;
//  }

//	public static String getNature(boolean isFramework) {
//		if (isFramework)
//			return INCREMENTAL_FRAMEWORK_NATURE_ID;
//		return INCREMENTAL_APPLICATION_NATURE_ID;
//	}
//
//	public static void s_addToProject(IProject project, boolean isFramework)
//		throws CoreException {
//		IProjectDescription desc = project.getDescription();
//
//		String natures_array[] = desc.getNatureIds();
//
//		List natures = new ArrayList(Arrays.asList(natures_array));
//		if (!natures.contains(getNature(isFramework))) {
//			natures.add(getNature(isFramework));
//			natures_array =
//				(String[]) natures.toArray(new String[natures.size()]);
//			desc.setNatureIds(natures_array);
//			s_setDescription(project, desc);
//		}
//	}
//
//	public static void s_removeFromProject(
//		IProject project,
//		boolean isFramework)
//		throws CoreException {
//		IProjectDescription desc = project.getDescription();
//
//		String natures_array[] = desc.getNatureIds();
//
//		List natures = new ArrayList(Arrays.asList(natures_array));
//
//		if (natures.contains(getNature(isFramework))) {
//			natures.remove(getNature(isFramework));
//			natures_array =
//				(String[]) natures.toArray(new String[natures.size()]);
//			desc.setNatureIds(natures_array);
//			s_setDescription(project, desc);
//		}
//	}
//
//	private static void s_setDescription(
//		final IProject f_project,
//		final IProjectDescription f_desc) {
//		s_showProgress(new IRunnableWithProgress() {
//			public void run(IProgressMonitor pm) {
//				try {
//					f_project.setDescription(f_desc, pm);
//				} catch (CoreException up) {
//					pm.done();
//				}
//			}
//		});
//	}
//
//	public static void s_showProgress(IRunnableWithProgress rwp) {
//		IWorkbench workbench = PlatformUI.getWorkbench();
//		Shell shell = null;
//		if (null != workbench) {
//			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
//			if (null != window) {
//				shell = window.getShell();
//			}
//		}
//
//		ProgressMonitorDialog pmd = new ProgressMonitorDialog(shell);
//
//		try {
//			pmd.run(true, true, rwp);
//		} catch (InvocationTargetException e) {
//			// handle exception
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			// handle cancelation
//			e.printStackTrace();
//		}
//	}

	IProject _project = null;

	private static final String APPINFO_PATH = "Contents";
	private static final String RESOURCE_PATH = "Resources";
	private static final String WEBRESOURCE_PATH = "WebServerResources";
	private static final String JAVA_PATH = "Resources/Java";
}
