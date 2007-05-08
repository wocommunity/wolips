/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 - 2006 The ObjectStyle Group 
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
package org.objectstyle.wolips.wizards;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.objectstyle.wolips.datasets.adaptable.JavaProject;
import org.objectstyle.wolips.datasets.resources.IWOLipsModel;
import org.objectstyle.wolips.eogenerator.core.model.EOGeneratorModel;
import org.objectstyle.wolips.eogenerator.core.model.EOModelReference;
import org.objectstyle.wolips.eogenerator.jdt.EOGeneratorCreator;
import org.objectstyle.wolips.eomodeler.core.model.EODatabaseConfig;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelException;
import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.core.model.IEOModelGroupFactory;
import org.objectstyle.wolips.eomodeler.editors.EOModelErrorDialog;

/**
 * @author mnolte
 * @author uli Creates new eo model file resources from values gathered by
 *         EOModelCreationPage. <br>
 * @author mschrag uses Entity Modeler API's now
 */
public class EOModelCreator implements IRunnableWithProgress {
	private String _modelName;

	private String _adaptorName;

	private IResource _parentResource;

	private boolean _createEOGeneratorFile;

	private EOModelCreationPage _page;

	/**
	 * Constructor for EOModelCreator.
	 * 
	 * @param parentResource
	 * @param modelName
	 * @param adaptorName
	 */
	public EOModelCreator(IResource parentResource, String modelName, String adaptorName, boolean createEOGeneratorFile, EOModelCreationPage page) {
		_parentResource = parentResource;
		_modelName = modelName;
		_adaptorName = adaptorName;
		_page = page;
		_createEOGeneratorFile = createEOGeneratorFile;
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException {
		try {
			createEOModel(monitor);
		} catch (CoreException e) {
			throw new InvocationTargetException(e);
		} catch (IOException e) {
			throw new InvocationTargetException(e);
		} catch (EOModelException e) {
			throw new InvocationTargetException(e);
		}
	}

	/**
	 * Method createEOModelNamed. Creates eo model file resources. All file
	 * resource changes are registered in ResourceChangeListener where the
	 * project file is updated. <br>
	 * All folder resource changes are registered in
	 * 
	 * @link WOProjectResourceCreator#createResourceFolderInProject(IFolder,
	 *       IProgressMonitor). <br>
	 * @param monitor
	 * @throws EOModelException
	 * @throws IOException
	 * @throws CoreException
	 * @throws InvocationTargetException
	 */
	public void createEOModel(IProgressMonitor monitor) throws CoreException, IOException, EOModelException, InvocationTargetException {
		Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();
		IContainer parentContainer = (IContainer) _parentResource;
		IFolder existingModelFolder = parentContainer.getFolder(new Path(_modelName + ".eomodeld"));
		if (existingModelFolder.exists()) {
			failures.add(new EOModelVerificationFailure(null, "There's already a model in " + existingModelFolder.getLocation().toOSString() + ".", true, null));
			EOModelErrorDialog errors = new EOModelErrorDialog(Display.getDefault().getActiveShell(), failures);
			errors.open();
			return;
		}

		boolean createModelGroup = false;
		EOModelGroup modelGroup;
		try {
			modelGroup = IEOModelGroupFactory.Utility.loadModelGroup(_parentResource.getProject(), failures, true, existingModelFolder.getLocation().toFile().toURL());
		} catch (Exception e) {
			failures.clear();
			failures.add(new EOModelVerificationFailure(null, "Creating empty EOModelGroup for this model because " + e.getMessage(), true, e));
			modelGroup = new EOModelGroup();
			createModelGroup = true;
			EOModelErrorDialog errors = new EOModelErrorDialog(Display.getDefault().getActiveShell(), failures);
			errors.open();
		}

		EOModel model = new EOModel(_modelName, _parentResource.getProject());
		model.setEditing(true);
		EODatabaseConfig databaseConfig = new EODatabaseConfig("Default");
		databaseConfig.setAdaptorName(_adaptorName);
		model.addDatabaseConfig(databaseConfig);
		modelGroup.addModel(model);

		File modelFolderFile = model.saveToFolder(parentContainer.getLocation().toFile());
		IFolder modelFolder = parentContainer.getFolder(new Path(modelFolderFile.getName()));
		String baseName = model.getName();

		if (_createEOGeneratorFile) {
			EOGeneratorModel eogenModel = EOGeneratorWizard.createEOGeneratorModel(parentContainer, model);
			IFile eogenFile = parentContainer.getFile(new Path(baseName + ".eogen"));
			if (eogenFile.exists()) {
				for (int dupeNum = 1; !eogenFile.exists(); dupeNum++) {
					eogenFile = parentContainer.getFile(new Path(baseName + dupeNum + ".eogen"));
				}
			}
			eogenModel.writeToFile(eogenFile, monitor);
		}
		parentContainer.refreshLocal(IResource.DEPTH_INFINITE, monitor);

		if (createModelGroup) {
			EOGeneratorModel modelGroupModel = EOGeneratorCreator.createDefaultModel(_parentResource.getProject());
			if (modelFolder != null) {
				Path modelPath = new Path(modelFolderFile.getAbsolutePath());
				EOModelReference modelReference = new EOModelReference(modelPath);
				modelGroupModel.addModel(modelReference);
			}
			IFile modelGroupFile = parentContainer.getFile(new Path(baseName + ".eomodelgroup"));
			modelGroupModel.writeToFile(modelGroupFile, monitor);
			_page.setResourceToReveal(modelGroupFile);
		} else {
			_page.setResourceToReveal(modelFolder.findMember("index.eomodeld"));
		}

		// add adaptor framework
		if (!"None".equals(_adaptorName)) {
			IJavaProject projectToUpdate = JavaCore.create(_parentResource.getProject());
			List newAdaptorFrameworkList = new LinkedList();
			newAdaptorFrameworkList.add("Java" + _adaptorName + "Adaptor." + IWOLipsModel.EXT_FRAMEWORK);
			JavaProject javaProject = (JavaProject) projectToUpdate.getAdapter(JavaProject.class);
			IClasspathEntry[] newClasspathEntries = javaProject.addFrameworkListToClasspathEntries(newAdaptorFrameworkList);
			try {
				projectToUpdate.setRawClasspath(newClasspathEntries, null);
			} catch (JavaModelException e) {
				throw new InvocationTargetException(e);
			} finally {
				projectToUpdate = null;
				newAdaptorFrameworkList = null;
				newClasspathEntries = null;
			}
		}
	}
}
