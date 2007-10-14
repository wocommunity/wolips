package org.objectstyle.wolips.ui.view;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IFileEditorInput;
import org.objectstyle.wolips.datasets.project.WOLipsCore;
import org.objectstyle.wolips.datasets.resources.IWOLipsResource;
import org.objectstyle.wolips.ui.UIPlugin;

public class RelatedContentProvider implements ITreeContentProvider {
	private RelatedLabelProvider _labelProvider;
	private Object _lastResource;
	private Object[] _lastResultList;

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		// DO NOTHING
	}

	public void dispose() {
		return;
	}
	
	public void setLabelProvider(RelatedLabelProvider labelProvider) {
		_labelProvider = labelProvider;
	}

	public Object[] getElements(Object parent) {
		Object actualParent = parent;
		IWOLipsResource wolipsResource = null;
		// MS: If we add the dependency it is a circular dependency, so that
		// sucks ... We'll just do it Reflection-Style.
		if (actualParent != null && actualParent.getClass().getName().equals("org.objectstyle.wolips.components.input.ComponentEditorFileEditorInput")) {
			try {
				actualParent = actualParent.getClass().getMethod("getFile", (Class[]) null).invoke(actualParent, (Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// System.out.println("ViewContentProvider.getElements: " +
			// parent);
		}
		if (actualParent instanceof IFileEditorInput) {
			IFileEditorInput input = (IFileEditorInput) actualParent;
			actualParent = input.getFile();
		}
		if (actualParent instanceof IFile) {
			try {
				// HACK AK: we should use sth more generic here
				if ("java".equals(((IFile)actualParent).getFileExtension())) {
					actualParent = JavaCore.createCompilationUnitFrom((IFile)actualParent);
				}
			} catch (Exception ex) {
				UIPlugin.getDefault().log(ex);
			}
		}
		if (actualParent instanceof IMember) {
			actualParent = ((IMember) actualParent).getCompilationUnit();
		}
		if (actualParent instanceof IResource) {
			wolipsResource = WOLipsCore.getWOLipsModel().getWOLipsResource((IResource) actualParent);
			// getViewer().setInput(wolipsResource);
		} else if (actualParent instanceof ICompilationUnit) {
			wolipsResource = WOLipsCore.getWOLipsModel().getWOLipsCompilationUnit((ICompilationUnit) actualParent);
		}
		
		Object[] resultList;
		if (_lastResource != null && wolipsResource != null && _lastResource.equals(wolipsResource)) {
			resultList = _lastResultList;
		}
		else {
			List<IResource> result = new LinkedList<IResource>();
			if (wolipsResource != null) {
				try {
					List<IResource> list = wolipsResource.getRelatedResources();
					result.addAll(list);
	
				} catch (Exception e) {
					UIPlugin.getDefault().log(e);
				}
			} else if (actualParent != null && actualParent instanceof IResource) {
				try {
					final IResource resource = (IResource) actualParent;
					final List<IResource> list = new ArrayList<IResource>();
					IContainer lproj = resource.getParent();
					if (lproj != null && "lprog".equals(lproj.getFileExtension())) {
						IContainer p = lproj.getParent();
						p.accept(new IResourceProxyVisitor() {
	
							public boolean visit(IResourceProxy proxy) throws CoreException {
								if (proxy.getName().endsWith(".lproj")) {
									IContainer f = (IContainer) proxy.requestResource();
									IResource m = f.findMember(resource.getName());
									if (m != null) {
										list.add(m);
									}
								}
								return true;
							}
	
						}, IResource.DEPTH_ONE);
						result.addAll(list);
					}
	
				} catch (Exception e) {
					UIPlugin.getDefault().log(e);
				}
			}
			resultList = result.toArray();
		}
		_lastResource = wolipsResource;
		_lastResultList = resultList;
		_labelProvider.setResultList(resultList);
		return resultList;
	}

	RelatedContentProvider() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		return false;
	}
}