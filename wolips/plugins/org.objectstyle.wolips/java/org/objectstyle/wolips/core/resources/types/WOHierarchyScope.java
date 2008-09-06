package org.objectstyle.wolips.core.resources.types;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.compiler.util.SuffixConstants;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jdt.internal.core.JavaModel;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.hierarchy.TypeHierarchy;
import org.eclipse.jdt.internal.core.search.AbstractSearchScope;

/**
 * Scope limited to the subtype and supertype hierarchy of a given type.
 */
public class WOHierarchyScope extends AbstractSearchScope implements SuffixConstants {
	public IType _focusType;

	private String _focusPath;

	private ITypeHierarchy _hierarchy;

	private IType[] _types;

	private HashSet<String> _resourcePaths;

	private IPath[] _enclosingProjectsAndJars;

	protected IResource[] _elements;

	protected int _elementCount;

	public boolean _needsRefresh;

	private IJavaProject _javaProject;

	/*
	 * (non-Javadoc) Adds the given resource to this search scope.
	 */
	public void add(IResource element) {
		if (_elementCount == _elements.length) {
			System.arraycopy(_elements, 0, _elements = new IResource[_elementCount * 2], 0, _elementCount);
		}
		_elements[_elementCount++] = element;
	}

	/*
	 * (non-Javadoc) Creates a new hiearchy scope for the given type.
	 */
	public WOHierarchyScope(IType type, IJavaProject javaProject) throws JavaModelException {
		_javaProject = javaProject;
		_focusType = type;

		_enclosingProjectsAndJars = computeProjectsAndJars(type);

		if (type != null) {
			// resource path
			IPackageFragment packageFragment = type.getPackageFragment();
			if (packageFragment != null) {
				IPackageFragmentRoot root = (IPackageFragmentRoot) packageFragment.getParent();
				if (root.isArchive()) {
					IPath jarPath = root.getPath();
					Object target = JavaModel.getTarget(jarPath, true);
					String zipFileName;
					if (target instanceof IFile) {
						// internal jar
						zipFileName = jarPath.toString();
					} else if (target instanceof File) {
						// external jar
						zipFileName = ((File) target).getPath();
					} else {
						return; // unknown target
					}
					_focusPath = zipFileName + JAR_FILE_ENTRY_SEPARATOR + type.getFullyQualifiedName().replace('.', '/') + SUFFIX_STRING_class;
				} else {
					_focusPath = type.getPath().toString();
				}
			} else {
				_focusPath = "";
			}
		} else {
			_focusPath = "";
		}

		_needsRefresh = true;

		// disabled for now as this could be expensive
		// JavaModelManager.getJavaModelManager().rememberScope(this);
	}

	private void buildResourceVector() {
		HashMap<IResource, IResource> resources = new HashMap<IResource, IResource>();
		HashMap<IPath, IType> paths = new HashMap<IPath, IType>();
		_types = _hierarchy.getAllTypes();
		for (int i = 0; i < _types.length; i++) {
			IType type = _types[i];
			IResource resource = type.getResource();
			if (resource != null && resources.get(resource) == null) {
				resources.put(resource, resource);
				add(resource);
			}
			IPackageFragmentRoot root = (IPackageFragmentRoot) type.getPackageFragment().getParent();
			if (root instanceof JarPackageFragmentRoot) {
				// type in a jar
				JarPackageFragmentRoot jar = (JarPackageFragmentRoot) root;
				IPath jarPath = jar.getPath();
				Object target = JavaModel.getTarget(jarPath, true);
				String zipFileName;
				if (target instanceof IFile) {
					// internal jar
					zipFileName = jarPath.toString();
				} else if (target instanceof File) {
					// external jar
					zipFileName = ((File) target).getPath();
				} else {
					continue; // unknown target
				}
				String resourcePath = zipFileName + JAR_FILE_ENTRY_SEPARATOR + type.getFullyQualifiedName().replace('.', '/') + SUFFIX_STRING_class;

				_resourcePaths.add(resourcePath);
				paths.put(jarPath, type);
			} else {
				// type is a project
				paths.put(type.getJavaProject().getProject().getFullPath(), type);
			}
		}
		_enclosingProjectsAndJars = new IPath[paths.size()];
		int i = 0;
		for (Iterator iter = paths.keySet().iterator(); iter.hasNext();) {
			_enclosingProjectsAndJars[i++] = (IPath) iter.next();
		}
	}

	/*
	 * Computes the paths of projects and jars that the hierarchy on the given
	 * type could contain. This is a super set of the project and jar paths once
	 * the hierarchy is computed.
	 */
	private IPath[] computeProjectsAndJars(IType type) throws JavaModelException {
		HashSet<IPath> set = new HashSet<IPath>();
		if (type != null) {
			IPackageFragmentRoot root = (IPackageFragmentRoot) type.getPackageFragment().getParent();
			if (root != null) {
				if (root.isArchive()) {
					// add the root
					set.add(root.getPath());
					// add all projects that reference this archive and their dependents
					IPath rootPath = root.getPath();
					IJavaModel model = JavaModelManager.getJavaModelManager().getJavaModel();
					IJavaProject[] projects = model.getJavaProjects();
					HashSet<IJavaProject> visited = new HashSet<IJavaProject>();
					for (int i = 0; i < projects.length; i++) {
						JavaProject project = (JavaProject) projects[i];
						IClasspathEntry entry = project.getClasspathEntryFor(rootPath);
						if (entry != null) {
							// add the project and its binary pkg fragment roots
							IPackageFragmentRoot[] roots = project.getAllPackageFragmentRoots();
							set.add(project.getPath());
							for (int k = 0; k < roots.length; k++) {
								IPackageFragmentRoot pkgFragmentRoot = roots[k];
								if (pkgFragmentRoot.getKind() == IPackageFragmentRoot.K_BINARY) {
									set.add(pkgFragmentRoot.getPath());
								}
							}
							// add the dependent projects
							computeDependents(project, set, visited);
						}
					}
				} else {
					// add all the project's pkg fragment roots
					IJavaProject project = (IJavaProject) root.getParent();
					IPackageFragmentRoot[] roots = project.getAllPackageFragmentRoots();
					for (int i = 0; i < roots.length; i++) {
						IPackageFragmentRoot pkgFragmentRoot = roots[i];
						if (pkgFragmentRoot.getKind() == IPackageFragmentRoot.K_BINARY) {
							set.add(pkgFragmentRoot.getPath());
						} else {
							set.add(pkgFragmentRoot.getParent().getPath());
						}
					}
					// add the dependent projects
					computeDependents(project, set, new HashSet<IJavaProject>());
				}
			}
		}
		IPath[] result = new IPath[set.size()];
		set.toArray(result);
		return result;
	}

	private void computeDependents(IJavaProject project, HashSet<IPath> set, HashSet<IJavaProject> visited) {
		if (visited.contains(project))
			return;
		visited.add(project);
		IProject[] dependents = project.getProject().getReferencingProjects();
		for (int i = 0; i < dependents.length; i++) {
			try {
				IJavaProject dependent = JavaCore.create(dependents[i]);
				IPackageFragmentRoot[] roots = dependent.getPackageFragmentRoots();
				set.add(dependent.getPath());
				for (int j = 0; j < roots.length; j++) {
					IPackageFragmentRoot pkgFragmentRoot = roots[j];
					if (pkgFragmentRoot.isArchive()) {
						set.add(pkgFragmentRoot.getPath());
					}
				}
				computeDependents(dependent, set, visited);
			} catch (JavaModelException e) {
				// project is not a java project
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IJavaSearchScope#encloses(String)
	 */
	public boolean encloses(String resourcePath) {
		if (_hierarchy == null) {
			if (resourcePath.equals(_focusPath)) {
				return true;
			}
			if (_needsRefresh) {
				try {
					initialize();
				} catch (JavaModelException e) {
					return false;
				}
			}
			// the scope is used only to find enclosing projects and
			// jars
			// clients is responsible for filtering out elements not in
			// the hierarchy (see SearchEngine)
			return true;
		}
		if (_needsRefresh) {
			try {
				refresh();
			} catch (JavaModelException e) {
				return false;
			}
		}
		int separatorIndex = resourcePath.indexOf(JAR_FILE_ENTRY_SEPARATOR);
		if (separatorIndex != -1) {
			return _resourcePaths.contains(resourcePath);
		}
		for (int i = 0; i < _elementCount; i++) {
			if (resourcePath.startsWith(_elements[i].getFullPath().toString())) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IJavaSearchScope#encloses(IJavaElement)
	 */
	public boolean encloses(IJavaElement element) {
		if (_hierarchy == null) {
			if (_focusType.equals(element.getAncestor(IJavaElement.TYPE))) {
				return true;
			}
			if (_needsRefresh) {
				try {
					initialize();
				} catch (JavaModelException e) {
					return false;
				}
			}
			// the scope is used only to find enclosing projects and
			// jars
			// clients is responsible for filtering out elements not in
			// the hierarchy (see SearchEngine)
			return true;
		}
		if (_needsRefresh) {
			try {
				refresh();
			} catch (JavaModelException e) {
				return false;
			}
		}
		IType type = null;
		if (element instanceof IType) {
			type = (IType) element;
		} else if (element instanceof IMember) {
			type = ((IMember) element).getDeclaringType();
		}
		if (type != null) {
			if (_hierarchy.contains(type)) {
				return true;
			}
			// be flexible: look at original element (see bug 14106
			// Declarations in Hierarchy does not find declarations in
			// hierarchy)
			IType original;
			if (!type.isBinary() && (original = (IType) type.getPrimaryElement()) != null) {
				return _hierarchy.contains(original);
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IJavaSearchScope#enclosingProjectsAndJars()
	 * @deprecated
	 */
	public IPath[] enclosingProjectsAndJars() {
		if (_needsRefresh) {
			try {
				refresh();
			} catch (JavaModelException e) {
				return new IPath[0];
			}
		}
		return _enclosingProjectsAndJars;
	}

	protected void initialize() throws JavaModelException {
		_resourcePaths = new HashSet<String>();
		_elements = new IResource[5];
		_elementCount = 0;
		_needsRefresh = false;
		if (_hierarchy == null) {
			_hierarchy = SubTypeHierarchyCache.getTypeHierarchyInProject(_focusType, _javaProject, null);
			//_focusType.newTypeHierarchy(_owner, null);
		} else {
			_hierarchy.refresh(null);
		}
		buildResourceVector();
	}

	/*
	 * @see AbstractSearchScope#processDelta(IJavaElementDelta)
	 */
	public void processDelta(IJavaElementDelta delta, int eventType) {
		if (_needsRefresh)
			return;
		_needsRefresh = _hierarchy == null ? false : ((TypeHierarchy) _hierarchy).isAffected(delta, eventType);
	}

	protected void refresh() throws JavaModelException {
		if (_hierarchy != null) {
			initialize();
		}
	}

	public String toString() {
		return "HierarchyScope on " + ((JavaElement) _focusType).toStringWithAncestors(); //$NON-NLS-1$
	}

}
