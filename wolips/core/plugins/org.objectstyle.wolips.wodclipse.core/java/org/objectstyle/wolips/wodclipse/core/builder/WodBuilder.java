/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 - 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowlegement: "This product includes software developed by the ObjectStyle
 * Group (http://objectstyle.org/)." Alternately, this acknowlegement may
 * appear in the software itself, if and wherever such third-party
 * acknowlegements normally appear. 4. The names "ObjectStyle Group" and
 * "Cayenne" must not be used to endorse or promote products derived from this
 * software without prior written permission. For written permission, please
 * contact andrus@objectstyle.org. 5. Products derived from this software may
 * not be called "ObjectStyle" nor may "ObjectStyle" appear in their names
 * without prior written permission of the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/> .
 *  
 */
package org.objectstyle.wolips.wodclipse.core.builder;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.objectstyle.wolips.bindings.Activator;
import org.objectstyle.wolips.bindings.preferences.PreferenceConstants;
import org.objectstyle.wolips.core.resources.builder.AbstractFullAndIncrementalBuilder;
import org.objectstyle.wolips.core.resources.types.SuperTypeHierarchyCache;
import org.objectstyle.wolips.locate.LocateException;
import org.objectstyle.wolips.locate.LocatePlugin;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.util.WodModelUtils;

public class WodBuilder extends AbstractFullAndIncrementalBuilder {
  private boolean _validateTemplates;
  private int _buildKind;

  public WodBuilder() {
    super();
  }

  @SuppressWarnings("unchecked")
  public boolean buildStarted(int kind, Map args, IProgressMonitor monitor, IProject project, Map buildCache) {
    _buildKind = kind;
    _validateTemplates = Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.VALIDATE_TEMPLATES_KEY);
    return false;
  }

  @SuppressWarnings("unchecked")
  public boolean buildPreparationDone(int kind, Map args, IProgressMonitor monitor, IProject project, Map buildCache) {
    return false;
  }

  @SuppressWarnings("unchecked")
  public void handleClasses(IResource resource, IProgressMonitor monitor, Map buildCache) {
    // DO NOTHING
  }

  @SuppressWarnings("unchecked")
  public void handleSource(IResource resource, IProgressMonitor progressMonitor, Map buildCache) {
    if (_validateTemplates) {
      try {
        if (_buildKind == IncrementalProjectBuilder.INCREMENTAL_BUILD || _buildKind == IncrementalProjectBuilder.AUTO_BUILD) {
          System.out.println("WodBuilder.handleSource: " + resource);
          ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom((IFile) resource);
          if (compilationUnit != null) {
            IType type = compilationUnit.findPrimaryType();
            if (type != null) {
              IType woElementType = type.getJavaProject().findType("com.webobjects.appserver.WOElement", progressMonitor);
              if (woElementType != null) {
                ITypeHierarchy typeHierarchy = SuperTypeHierarchyCache.getTypeHierarchy(type, progressMonitor);
                if (typeHierarchy != null && typeHierarchy.contains(woElementType)) {
                  LocalizedComponentsLocateResult results = LocatePlugin.getDefault().getLocalizedComponentsLocateResult(resource);
                  IFile wodFile = results.getFirstWodFile();
                  if (wodFile != null && wodFile.exists()) {
                    wodFile.touch(progressMonitor);
                    validateWodFile(wodFile, progressMonitor);
                  }
                }
              }
            }
          }
        }
        //touchRelatedResources(_resource, _progressMonitor, _buildCache);
      }
      catch (Throwable t) {
        Activator.getDefault().log(t);
      }
    }
  }

  @SuppressWarnings("unchecked")
  public void handleClasspath(IResource resource, IProgressMonitor monitor, Map buildCache) {
    // DO NOTHING
  }

  @SuppressWarnings("unchecked")
  protected Set<IContainer> componentBuildCache(Map buildCache) {
    Set<IContainer> builtComponents = (Set<IContainer>) buildCache.get("builtComponents");
    if (builtComponents == null) {
      buildCache.put("builtComponents", builtComponents);
    }
    return builtComponents;
  }

  @SuppressWarnings("unchecked")
  public void handleOther(IResource resource, IProgressMonitor monitor, Map buildCache) {
    if (_validateTemplates) {
      try {
        boolean validate = false;
        if (resource instanceof IFile) {
          IFile file = (IFile) resource;
          String fileExtension = file.getFileExtension();
          if ("wod".equals(fileExtension)) {
            Set<IContainer> builtComponents = componentBuildCache(buildCache);
            IContainer woFolder = resource.getParent();
            if (!builtComponents.contains(woFolder)) {
              if (_buildKind == IncrementalProjectBuilder.FULL_BUILD) {
                file.touch(monitor);
              }
              validate = true;
              builtComponents.add(woFolder);
            }
          }
          else if ("html".equals(fileExtension) && resource.getParent().getName().endsWith(".wo")) {
            Set<IContainer> builtComponents = componentBuildCache(buildCache);
            IContainer woFolder = resource.getParent();
            if (!builtComponents.contains(woFolder)) {
              if (_buildKind == IncrementalProjectBuilder.FULL_BUILD) {
                file.touch(monitor);
              }
              validate = true;
              builtComponents.add(woFolder);
            }
          }
          else if ("api".equals(fileExtension)) {
            // should we really do something with the component when
            // we change the api?
            // shoulnd't we validate all files using the api?
            validate = false;
          }

          if (validate) {
            validateWodFile(file, monitor);
          }
        }
      }
      catch (Throwable e) {
        Activator.getDefault().log(e);
      }
    }
    else {
      if (resource instanceof IFile) {
        IFile file = (IFile) resource;
        String fileExtension = file.getFileExtension();
        if ("wod".equals(fileExtension)) {
          WodModelUtils.deleteWodProblems(file);
        }
        else if ("html".equals(fileExtension)) {
          WodModelUtils.deleteWodProblems(file);
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  public void handleWebServerResources(IResource resource, IProgressMonitor monitor, Map buildCache) {
    // DO NOTHING
  }

  @SuppressWarnings("unchecked")
  public void handleWoappResources(IResource resource, IProgressMonitor monitor, Map buildCache) {
    // DO NOTHING
  }

  protected void validateWodFile(IFile file, IProgressMonitor progressMonitor) throws CoreException, LocateException {
    String _resourceName = file.getName();
    if (progressMonitor != null) {
      progressMonitor.subTask("Locating components for " + _resourceName + " ...");
    }
    WodParserCache cache = WodParserCache.parser(file);
    if (progressMonitor != null) {
      progressMonitor.subTask("Building WO " + cache.getWodFile().getName() + " ...");
    }
    try {
      cache.parseHtmlAndWodIfNecessary();
      cache.validate();
    }
    catch (Throwable t) {
      t.printStackTrace();
    }
  }
}