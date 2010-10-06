/**
 * 
 */
package org.objectstyle.wolips.wodclipse.core.completion;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.widgets.Display;
import org.objectstyle.wolips.bindings.Activator;

public class WodParserCacheInvalidator implements IResourceChangeListener, IResourceDeltaVisitor {
  public void resourceChanged(IResourceChangeEvent event) {
    IResourceDelta delta = event.getDelta();
    if (delta != null) {
      try {
        delta.accept(this);
      }
      catch (CoreException e) {
        Activator.getDefault().log(e);
      }
    }
  }

  public boolean visit(IResourceDelta delta) {
    IResource resource = delta.getResource();
    if (resource.isDerived()) {
    	return false;
    }
    else if (resource instanceof IFile) {
      IFile file = (IFile) resource;
      String name = file.getName().toLowerCase();
      if (name.endsWith(".java")) {
        if (delta.getKind() == IResourceDelta.ADDED) {
          // IGNORE
        }
        else if (delta.getKind() == IResourceDelta.REMOVED) {
          WodParserCache.getTypeCache().clearCacheForResource(resource);
        }
        else if (delta.getKind() == IResourceDelta.CHANGED) {
          IJavaElement javaElement = JavaCore.create(file);
          if (javaElement instanceof ICompilationUnit) {
            try {
              IJavaProject javaProject = javaElement.getJavaProject();
              if (javaProject != null && javaProject.isOnClasspath(javaElement)) {
                IType[] types = ((ICompilationUnit) javaElement).getAllTypes();
                for (IType type : types) {
                  WodParserCache.getTypeCache().clearCacheForType(type);
                }
              }
            }
            catch (JavaModelException e) {
              //e.printStackTrace(System.out);
              Activator.getDefault().log("Failed to clear caches for " + resource + ".", e);
            }
          }
        }
      }
      else if (name.endsWith(".api")) {
        IJavaProject javaProject = JavaCore.create(file.getProject());
        if (javaProject != null) {
          String elementName = file.getName().substring(0, file.getName().lastIndexOf('.'));
          WodParserCache.getTypeCache().getApiCache(javaProject).clearCacheForElementNamed(elementName);
        }
      }
      else if (file.getParent() != null && file.getParent().getName().endsWith(".eomodeld")) {
        if (delta.getKind() == IResourceDelta.ADDED) {
          WodParserCache.getModelGroupCache().clearCacheForProject(file.getProject());
        }
        else if (delta.getKind() == IResourceDelta.REMOVED) {
          WodParserCache.getModelGroupCache().clearCacheForProject(file.getProject());
        }
        else if (delta.getKind() == IResourceDelta.CHANGED && ((delta.getFlags() & IResourceDelta.CONTENT) != 0)) {
          WodParserCache.getModelGroupCache().clearCacheForProject(file.getProject());
        }
      }
      else if (file.getParent() != null && file.getParent().getName().endsWith(".wo")) {
        if (delta.getKind() == IResourceDelta.ADDED) {
          String newComponent = file.getParent().getFullPath().removeFileExtension().lastSegment();
          final IFile oldFile = file;
          final IPath newPath = file.getParent().getFullPath().append(newComponent).addFileExtension(file.getFileExtension());
          if (file.getFileExtension().matches("(xml|html|xhtml|wod|woo)") && 
              !file.getFullPath().equals(newPath) && !newPath.toFile().exists()) {
            Display.getDefault().asyncExec(new Runnable() {
              public void run() {
                try {
                	// one last check before it throws an exception ...
                	if (!newPath.toFile().exists()) {
                		oldFile.move(newPath, false, null);
                	}
                } catch (CoreException e) {
                  e.printStackTrace();
                }
              }
            });
          }
        	WodParserCache.invalidateResource(file.getParent());
        }
        else if (delta.getKind() == IResourceDelta.REMOVED) {
        	WodParserCache.invalidateResource(file.getParent());
        }
        else if (delta.getKind() == IResourceDelta.CHANGED && ((delta.getFlags() & IResourceDelta.ENCODING) != 0)) {
        	WodParserCache.invalidateResource(file.getParent());
        }
      }
    }
    return true;
  }
}