/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
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
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.eomodeler.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.EOModelerPerspectiveFactory;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.editors.arguments.EOArgumentsTableEditor;
import org.objectstyle.wolips.eomodeler.editors.entities.EOEntitiesTableEditor;
import org.objectstyle.wolips.eomodeler.editors.entity.EOEntityEditor;
import org.objectstyle.wolips.eomodeler.model.AbstractEOAttributePath;
import org.objectstyle.wolips.eomodeler.model.EOArgument;
import org.objectstyle.wolips.eomodeler.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.model.EODatabaseConfig;
import org.objectstyle.wolips.eomodeler.model.EOEntity;
import org.objectstyle.wolips.eomodeler.model.EOFetchSpecification;
import org.objectstyle.wolips.eomodeler.model.EOModel;
import org.objectstyle.wolips.eomodeler.model.EOModelException;
import org.objectstyle.wolips.eomodeler.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.model.EORelationship;
import org.objectstyle.wolips.eomodeler.model.EOStoredProcedure;
import org.objectstyle.wolips.eomodeler.model.EclipseEOModelGroupFactory;
import org.objectstyle.wolips.eomodeler.outline.EOModelContentOutlinePage;
import org.objectstyle.wolips.eomodeler.utils.AbstractAddRemoveChangeRefresher;
import org.objectstyle.wolips.eomodeler.utils.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.utils.ErrorUtils;
import org.objectstyle.wolips.eomodeler.utils.URLUtils;
import org.objectstyle.wolips.preferences.Preferences;

public class EOModelEditor extends MultiPageEditorPart implements IResourceChangeListener, ITabbedPropertySheetPageContributor, ISelectionProvider, IEOModelEditor {
	public static final String EOMODEL_EDITOR_ID = "org.objectstyle.wolips.eomodeler.editors.EOModelEditor";

	public static final String EOMODEL_PAGE = "eomodel";

	public static final String EOENTITY_PAGE = "eoentity";

	public static final String EOSTOREDPROCEDURE_PAGE = "eostoredprocedure";

	private EOEntitiesTableEditor myEntitiesTableEditor;

	private EOEntityEditor myEntityEditor;

	private EOArgumentsTableEditor myStoredProcedureEditor;

	private EOModelContentOutlinePage myContentOutlinePage;

	private ListenerList mySelectionChangedListeners;

	private IStructuredSelection mySelection;

	private PropertyChangeListener myDirtyModelListener;

	private EntitiesChangeRefresher myEntitiesChangeListener;

	private StoredProceduresChangeRefresher myStoredProceduresChangeListener;

	private DatabaseConfigsChangeRefresher myDatabaseConfigsChangeListener;

	private AttributeAndRelationshipDeletedRefresher myAttributeAndRelationshipListener;

	private ArgumentDeletedRefresher myArgumentListener;

	private EOStoredProcedure mySelectedStoredProcedure;

	private EOEntity mySelectedEntity;

	private EOEntity myOpeningEntity;

	private EOModel myModel;

	private Set myLoadFailures;

	private boolean myEntityPageVisible;

	private boolean myStoredProcedurePageVisible;

	private int mySelectionDepth;

	public EOModelEditor() {
		mySelectionChangedListeners = new ListenerList();
		myDirtyModelListener = new DirtyModelListener();
		myEntitiesChangeListener = new EntitiesChangeRefresher();
		myStoredProceduresChangeListener = new StoredProceduresChangeRefresher();
		myDatabaseConfigsChangeListener = new DatabaseConfigsChangeRefresher();
		myAttributeAndRelationshipListener = new AttributeAndRelationshipDeletedRefresher();
		myArgumentListener = new ArgumentDeletedRefresher();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	public EOModelContentOutlinePage getContentOutlinePage() {
		if (myContentOutlinePage == null) {
			myContentOutlinePage = new EOModelContentOutlinePage(this);
			myContentOutlinePage.addSelectionChangedListener(new EOModelContentSelectionChangedListener());
		}
		return myContentOutlinePage;
	}

	public EOModel getModel() {
		return myModel;
	}

	public EOEntity getSelectedEntity() {
		return mySelectedEntity;
	}

	public String getContributorId() {
		return getSite().getId();
	}

	public Object getAdapter(Class _adapterClass) {
		Object adapter;
		if (_adapterClass == IPropertySheetPage.class) {
			adapter = new TabbedPropertySheetPage(this);
		} else if (_adapterClass == IContentOutlinePage.class) {
			IContentOutlinePage outlinePage = getContentOutlinePage();
			adapter = outlinePage;
		} else {
			adapter = super.getAdapter(_adapterClass);
		}
		return adapter;
	}

	protected int getPageNum(String _pageType) {
		int pageNum;
		if (_pageType == EOModelEditor.EOENTITY_PAGE) {
			pageNum = getPageNum(myEntityEditor);
		} else if (_pageType == EOModelEditor.EOMODEL_PAGE) {
			pageNum = getPageNum(myEntitiesTableEditor);
		} else if (_pageType == EOModelEditor.EOSTOREDPROCEDURE_PAGE) {
			pageNum = getPageNum(myStoredProcedureEditor);
		} else {
			pageNum = -1;
		}
		return pageNum;
	}

	protected int getPageNum(IEditorPart _editorPart) {
		int matchingPageNum = -1;
		int pageCount = getPageCount();
		for (int pageNum = 0; matchingPageNum == -1 && pageNum < pageCount; pageNum++) {
			IEditorPart editorPart = getEditor(pageNum);
			if (editorPart == _editorPart) {
				matchingPageNum = pageNum;
			}
		}
		return matchingPageNum;
	}

	protected void createPages() {
		try {
			myEntitiesTableEditor = new EOEntitiesTableEditor();

			addPage(myEntitiesTableEditor, getEditorInput());
			setPageText(getPageNum(EOModelEditor.EOMODEL_PAGE), Messages.getString("EOModelEditor.entitiesTab"));

			myEntityEditor = new EOEntityEditor();

			EOModelSelectionChangedListener modelSelectionChangedListener = new EOModelSelectionChangedListener();
			myEntitiesTableEditor.addSelectionChangedListener(modelSelectionChangedListener);
			myEntitiesTableEditor.setModel(myModel);

			EOEntitySelectionChangedListener entitySelectionChangedListener = new EOEntitySelectionChangedListener();
			myEntityEditor.addSelectionChangedListener(entitySelectionChangedListener);

			myStoredProcedureEditor = new EOArgumentsTableEditor();
			EOArgumentSelectionChangedListener argumentSelectionChangedListener = new EOArgumentSelectionChangedListener();
			myStoredProcedureEditor.addSelectionChangedListener(argumentSelectionChangedListener);

			if (myOpeningEntity != null) {
				setSelectedEntity(myOpeningEntity);
				setActivePage(getPageNum(EOModelEditor.EOENTITY_PAGE));
			}
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(), "Error creating editor.", null, e.getStatus());
		}
	}

	protected void setEntityPageVisible(boolean _entityPageVisible) {
		try {
			if (_entityPageVisible) {
				if (!myEntityPageVisible) {
					addPage(myEntityEditor, getEditorInput());
				}
				String entityName = mySelectedEntity.getName();
				if (entityName == null) {
					entityName = "?";
				}
				setPageText(getPageNum(EOModelEditor.EOENTITY_PAGE), entityName);
			} else if (myEntityPageVisible) {
				removePage(getPageNum(EOModelEditor.EOENTITY_PAGE));
			}
			myEntityPageVisible = _entityPageVisible;
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(), "Error creating editor.", null, e.getStatus());
		}
	}

	protected void setStoredProcedurePageVisible(boolean _storedProcedurePageVisible) {
		try {
			if (_storedProcedurePageVisible) {
				if (!myStoredProcedurePageVisible) {
					addPage(myStoredProcedureEditor, getEditorInput());
				}
				String storedProcedureName = mySelectedStoredProcedure.getName();
				if (storedProcedureName == null) {
					storedProcedureName = "?";
				}
				setPageText(getPageNum(EOModelEditor.EOSTOREDPROCEDURE_PAGE), storedProcedureName);
			} else if (myStoredProcedurePageVisible) {
				removePage(getPageNum(EOModelEditor.EOSTOREDPROCEDURE_PAGE));
			}
			myStoredProcedurePageVisible = _storedProcedurePageVisible;
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(), "Error creating editor.", null, e.getStatus());
		}
	}

	public void setSelectedEntity(EOEntity _selectedEntity) {
		if (!ComparisonUtils.equals(mySelectedEntity, _selectedEntity)) {
			if (mySelectedEntity != null) {
				mySelectedEntity.removePropertyChangeListener(EOEntity.ATTRIBUTES, myAttributeAndRelationshipListener);
				mySelectedEntity.removePropertyChangeListener(EOEntity.RELATIONSHIPS, myAttributeAndRelationshipListener);
			}
			mySelectedEntity = _selectedEntity;
			if (mySelectedEntity != null) {
				mySelectedEntity.addPropertyChangeListener(EOEntity.ATTRIBUTES, myAttributeAndRelationshipListener);
				mySelectedEntity.addPropertyChangeListener(EOEntity.RELATIONSHIPS, myAttributeAndRelationshipListener);
			}
			if (_selectedEntity == null) {
				setEntityPageVisible(false);
			} else {
				setEntityPageVisible(true);
			}
			myEntitiesTableEditor.setSelectedEntity(_selectedEntity);
			myEntityEditor.setEntity(_selectedEntity);
			updatePartName();
		}
		if (_selectedEntity != null) {
			setSelectedStoredProcedure(null);
		}
	}

	public EOStoredProcedure getSelectedStoredProcedure() {
		return mySelectedStoredProcedure;
	}

	public void setSelectedStoredProcedure(EOStoredProcedure _selectedStoredProcedure) {
		if (!ComparisonUtils.equals(mySelectedStoredProcedure, _selectedStoredProcedure)) {
			if (mySelectedStoredProcedure != null) {
				mySelectedStoredProcedure.removePropertyChangeListener(EOStoredProcedure.ARGUMENTS, myArgumentListener);
			}
			mySelectedStoredProcedure = _selectedStoredProcedure;
			if (mySelectedStoredProcedure != null) {
				mySelectedStoredProcedure.addPropertyChangeListener(EOStoredProcedure.ARGUMENTS, myArgumentListener);
			}
			if (_selectedStoredProcedure == null) {
				setStoredProcedurePageVisible(false);
			} else {
				setStoredProcedurePageVisible(true);
			}
			myStoredProcedureEditor.setStoredProcedure(_selectedStoredProcedure);
			updatePartName();
		}
		if (_selectedStoredProcedure != null) {
			setSelectedEntity(null);
		}
	}

	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);

		super.dispose();

		if (Preferences.shouldEntityModelerChangePerspectives()) {
			try {
				IWorkbench workbench = Activator.getDefault().getWorkbench();
				IWorkbenchPage workbenchPage = workbench.getActiveWorkbenchWindow().getActivePage();
				if (EOModelerPerspectiveFactory.EOMODELER_PERSPECTIVE_ID.equals(workbenchPage.getPerspective().getId())) {
					IEditorReference[] editorReferences = workbenchPage.getEditorReferences();
					int eomodelerEditorCount = 0;
					for (int editorReferenceNum = 0; editorReferenceNum < editorReferences.length; editorReferenceNum++) {
						IEditorReference editorReference = editorReferences[editorReferenceNum];
						if (EOModelEditor.EOMODEL_EDITOR_ID.equals(editorReference.getId())) {
							eomodelerEditorCount++;
						}
					}
					if (eomodelerEditorCount == 0) {
						workbench.showPerspective("org.objectstyle.wolips.ui.Perspective", workbench.getActiveWorkbenchWindow());
					}
				}
			} catch (WorkbenchException e) {
				ErrorUtils.openErrorDialog(Display.getDefault().getActiveShell(), e);
			}
		}
	}

	public void doSave(IProgressMonitor _monitor) {
		showBusy(true);
		try {
			IEditorInput input = getEditorInput();
			if (input != null && myModel != null) {
				Set failures = new HashSet();
				myModel.verify(failures);
				handleModelErrors(failures);

				IFile originalFile = ((IFileEditorInput) input).getFile();
				IContainer originalFolder = originalFile.getParent();

				myModel.saveToFolder(originalFolder.getParent().getLocation().toFile());
				myModel.setDirty(false);
				originalFolder.refreshLocal(IResource.DEPTH_INFINITE, _monitor);
			}
		} catch (Throwable t) {
			ErrorUtils.openErrorDialog(Display.getDefault().getActiveShell(), t);
		} finally {
			showBusy(false);
		}
	}

	public boolean isDirty() {
		return myModel != null && myModel.isDirty();
	}

	public void doSaveAs() {
		doSave(null);
	}

	public void revert() {
		boolean confirmed = MessageDialog.openConfirm(Display.getDefault().getActiveShell(), Messages.getString("EOModelEditor.revertTitle"), Messages.getString("EOModelEditor.revertMessage"));
		if (confirmed) {
			try {
				init((IEditorSite) getSite(), getEditorInput());
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
	}

	protected static IFile getFile(File _file) {
		return ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(_file.getAbsolutePath()));
	}

	protected static IFile getIndexFile(EOModel _model) throws MalformedURLException, EOModelException {
		if (_model.getIndexURL() == null) {
			throw new EOModelException("Failed to load model.");
		}
		return EOModelEditor.getFile(URLUtils.cheatAndTurnIntoFile(_model.getIndexURL()));
	}

	public void init(IEditorSite _site, IEditorInput _editorInput) throws PartInitException {
		try {
			if (Preferences.shouldEntityModelerChangePerspectives()) {
				IWorkbench workbench = Activator.getDefault().getWorkbench();
				workbench.showPerspective(EOModelerPerspectiveFactory.EOMODELER_PERSPECTIVE_ID, workbench.getActiveWorkbenchWindow());
			}
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}

		try {
			IFileEditorInput fileEditorInput;
			if (_editorInput instanceof IFileEditorInput) {
				fileEditorInput = (IFileEditorInput) _editorInput;
			} else {
				throw new PartInitException("Unknown editor input: " + _editorInput + ".");
			}
			if (myModel != null) {
				myModel.removePropertyChangeListener(EOModel.DIRTY, myDirtyModelListener);
				myModel.removePropertyChangeListener(EOModel.ENTITIES, myEntitiesChangeListener);
				myModel.removePropertyChangeListener(EOModel.STORED_PROCEDURES, myStoredProceduresChangeListener);
				myModel.removePropertyChangeListener(EOModel.DATABASE_CONFIGS, myDatabaseConfigsChangeListener);
			}

			IFile file = fileEditorInput.getFile();
			String openingEntityName = null;
			if ("plist".equalsIgnoreCase(file.getFileExtension())) {
				String name = file.getName();
				openingEntityName = name.substring(0, name.indexOf('.'));
			}

			myLoadFailures = new LinkedHashSet();
			myModel = EclipseEOModelGroupFactory.createModel(fileEditorInput.getFile(), myLoadFailures, true);
			if (myModel == null) {
				super.init(_site, fileEditorInput);
				handleModelErrors(myLoadFailures);
				//throw new EOModelException("Failed to load the requested model.");
			}
			else {
				if (openingEntityName != null) {
					myOpeningEntity = myModel.getEntityNamed(openingEntityName);
				}
				fileEditorInput = new FileEditorInput(EOModelEditor.getIndexFile(myModel));
				handleModelErrors(myLoadFailures);
	
				myModel.addPropertyChangeListener(EOModel.DIRTY, myDirtyModelListener);
				myModel.addPropertyChangeListener(EOModel.ENTITIES, myEntitiesChangeListener);
				myModel.addPropertyChangeListener(EOModel.STORED_PROCEDURES, myStoredProceduresChangeListener);
				myModel.addPropertyChangeListener(EOModel.DATABASE_CONFIGS, myDatabaseConfigsChangeListener);
				super.init(_site, fileEditorInput);
				updatePartName();
				_site.setSelectionProvider(this);
				EOModelEditor.this.editorDirtyStateChanged();
			}
		} catch (Throwable e) {
			handleModelErrors(myLoadFailures);
			throw new PartInitException("Failed to create EOModelEditorInput for " + _editorInput + ".", e);
		}
	}

	protected void handleModelErrors(final Set _failures) {
		if (myModel != null) {
			try {
				Iterator modelsIter = myModel.getModelGroup().getModels().iterator();
				while (modelsIter.hasNext()) {
					EOModel model = (EOModel) modelsIter.next();
					IFile indexFile = EOModelEditor.getIndexFile(model);
					if (indexFile != null) {
						IMarker[] markers = indexFile.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
						for (int markerNum = 0; markerNum < markers.length; markerNum++) {
							// System.out.println("EOModelEditor.handleModelErrors:
							// deleting " + markers[markerNum]);
							markers[markerNum].delete();
						}
					}
				}
				if (Preferences.shouldEntityModelerShowErrorsInProblemsView()) {
					Iterator failuresIter = _failures.iterator();
					while (failuresIter.hasNext()) {
						EOModelVerificationFailure failure = (EOModelVerificationFailure) failuresIter.next();
						EOModel model = failure.getModel();
						IFile indexFile = EOModelEditor.getIndexFile(model);
						if (indexFile != null) {
							IMarker marker = indexFile.createMarker(IMarker.PROBLEM);
							marker.setAttribute(IMarker.MESSAGE, failure.getMessage());
							int severity;
							if (failure.isWarning()) {
								severity = IMarker.SEVERITY_WARNING;
							} else {
								severity = IMarker.SEVERITY_ERROR;
							}
							marker.setAttribute(IMarker.SEVERITY, new Integer(severity));
							marker.setAttribute(IMarker.TRANSIENT, false);
						}
					}
				}
			} catch (CoreException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (EOModelException e) {
				e.printStackTrace();
			}
		}

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (!_failures.isEmpty()) {
					EOModelErrorDialog dialog = new EOModelErrorDialog(Display.getCurrent().getActiveShell(), _failures);
					dialog.setBlockOnOpen(true);
					dialog.open();
				}
			}
		});
	}

	protected void updatePartName() {
		String partName;
		if (myModel != null) {
			partName = myModel.getName();
		} else {
			partName = Messages.getString("EOModelEditor.partName");
		}
		setPartName(partName);
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	protected void pageChange(int _newPageIndex) {
		super.pageChange(_newPageIndex);
		ISelectionProvider selectionProvider = (ISelectionProvider) getEditor(_newPageIndex);
		getSite().setSelectionProvider(selectionProvider);
	}

	public void resourceChanged(final IResourceChangeEvent _event) {
		if (_event.getType() == IResourceChangeEvent.PRE_CLOSE) {
			final IFileEditorInput input = (IFileEditorInput) getEditorInput();
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (int pageNum = 0; pageNum < pages.length; pageNum++) {
						if (input.getFile().getProject().equals(_event.getResource())) {
							IEditorPart editorPart = pages[pageNum].findEditor(input);
							pages[pageNum].closeEditor(editorPart, true);
						}
					}
				}
			});
		}
	}

	public EOEntitiesTableEditor getEntitiesTableEditor() {
		return myEntitiesTableEditor;
	}

	public EOEntityEditor getEntityEditor() {
		return myEntityEditor;
	}

	public EOArgumentsTableEditor getStoredProcedureEditor() {
		return myStoredProcedureEditor;
	}

	public void setActivePage(int _pageIndex) {
		super.setActivePage(_pageIndex);
	}

	public ISelection getSelection() {
		return mySelection;
	}

	public void setSelection(ISelection _selection) {
		setSelection(_selection, true);
	}

	public synchronized void setSelection(ISelection _selection, boolean _updateOutline) {
		// MS: it's really easy to setup a selection loop with so many
		// interrelated components. In reality, we only want the top selection
		// to count, and
		// if the call to setSelection is called again from within this stack,
		// then
		// that's pretty much bad.
		mySelectionDepth++;
		try {
			if (mySelectionDepth == 1) {
				IStructuredSelection previousSelection = mySelection;
				IStructuredSelection selection = (IStructuredSelection) _selection;
				mySelection = selection;
				if (previousSelection == null || !selection.toList().equals(previousSelection.toList())) {
					Object selectedObject = null;
					if (!selection.isEmpty()) {
						selectedObject = selection.getFirstElement();
					}
					if (selectedObject instanceof EOModel) {
						// EOModel selectedModel = (EOModel) selectedObject;
						setSelectedEntity(null);
						setSelectedStoredProcedure(null);
						setActivePage(getPageNum(EOModelEditor.EOMODEL_PAGE));
					} else if (selectedObject instanceof EOEntity) {
						EOEntity selectedEntity = (EOEntity) selectedObject;
						setSelectedEntity(selectedEntity);
						// setActivePage(EOModelEditor.EOENTITY_PAGE);
					} else if (selectedObject instanceof EOAttribute) {
						EOAttribute selectedAttribute = (EOAttribute) selectedObject;
						setSelectedEntity(selectedAttribute.getEntity());
						getEntityEditor().setSelection(_selection);
						setActivePage(getPageNum(EOModelEditor.EOENTITY_PAGE));
					} else if (selectedObject instanceof EORelationship) {
						EORelationship selectedRelationship = (EORelationship) selectedObject;
						setSelectedEntity(selectedRelationship.getEntity());
						getEntityEditor().setSelection(selection);
						setActivePage(getPageNum(EOModelEditor.EOENTITY_PAGE));
					} else if (selectedObject instanceof EOFetchSpecification) {
						EOFetchSpecification selectedFetchSpec = (EOFetchSpecification) selectedObject;
						setSelectedEntity(selectedFetchSpec.getEntity());
						getEntityEditor().setSelection(selection);
						// setActivePage(EOModelEditor.EOENTITY_PAGE);
					} else if (selectedObject instanceof AbstractEOAttributePath) {
						AbstractEOAttributePath selectedAttributePath = (AbstractEOAttributePath) selectedObject;
						setSelectedEntity(selectedAttributePath.getChildIEOAttribute().getEntity());
						getEntityEditor().setSelection(new StructuredSelection(selectedAttributePath.getChildIEOAttribute()));
						setActivePage(getPageNum(EOModelEditor.EOENTITY_PAGE));
					} else if (selectedObject instanceof EOStoredProcedure) {
						EOStoredProcedure selectedStoredProcedure = (EOStoredProcedure) selectedObject;
						setSelectedStoredProcedure(selectedStoredProcedure);
						// setActivePage(EOModel)
					} else if (selectedObject instanceof EOArgument) {
						EOArgument selectedArgument = (EOArgument) selectedObject;
						setSelectedStoredProcedure(selectedArgument.getStoredProcedure());
						getStoredProcedureEditor().setSelection(_selection);
						setActivePage(getPageNum(EOModelEditor.EOSTOREDPROCEDURE_PAGE));
					} else if (selectedObject instanceof EODatabaseConfig) {
						EODatabaseConfig selectedDatabaseConfig = (EODatabaseConfig) selectedObject;
						setSelectedEntity(null);
						setActivePage(getPageNum(EOModelEditor.EOMODEL_PAGE));
					}
					if (_updateOutline) {
						getContentOutlinePage().setSelection(selection);
					}
					fireSelectionChanged(selection);
				}
			}
		} finally {
			mySelectionDepth--;
		}
	}

	public void addSelectionChangedListener(ISelectionChangedListener _listener) {
		mySelectionChangedListeners.add(_listener);
	}

	public void removeSelectionChangedListener(ISelectionChangedListener _listener) {
		mySelectionChangedListeners.remove(_listener);
	}

	public void setFocus() {
		super.setFocus();
		// MS: I'm not sure the right way to do this, but without
		// this call, selecting a relationship in the EOModelEditor
		// before ever activing the outline would not cause the
		// property view to update.
		getSite().setSelectionProvider(this);
	}

	protected void fireSelectionChanged(ISelection _selection) {
		Object[] selectionChangedListeners = mySelectionChangedListeners.getListeners();
		SelectionChangedEvent selectionChangedEvent = new SelectionChangedEvent(this, _selection);
		for (int listenerNum = 0; listenerNum < selectionChangedListeners.length; listenerNum++) {
			ISelectionChangedListener listener = (ISelectionChangedListener) selectionChangedListeners[listenerNum];
			listener.selectionChanged(selectionChangedEvent);
		}
	}

	protected void editorDirtyStateChanged() {
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	protected void doubleClickedObjectInOutline(Object _obj) {
		if (_obj instanceof EOEntity) {
			setActivePage(getPageNum(EOModelEditor.EOENTITY_PAGE));
		} else if (_obj instanceof EOStoredProcedure) {
			setActivePage(getPageNum(EOModelEditor.EOSTOREDPROCEDURE_PAGE));
		}
	}

	protected class EOModelContentSelectionChangedListener implements ISelectionChangedListener {
		private Object mySelectedObject;

		public void selectionChanged(SelectionChangedEvent _event) {
			IStructuredSelection selection = (IStructuredSelection) _event.getSelection();
			Object selectedObject = selection.getFirstElement();
			setSelection(selection, false);
			if (mySelectedObject == null) {
				mySelectedObject = selectedObject;
			} else if (mySelectedObject == selectedObject) {
				EOModelEditor.this.doubleClickedObjectInOutline(selectedObject);
				mySelectedObject = null;
			} else {
				mySelectedObject = selectedObject;
			}
		}
	}

	protected class EOModelSelectionChangedListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent _event) {
			IStructuredSelection selection = (IStructuredSelection) _event.getSelection();
			setSelection(selection);
		}
	}

	protected class EOEntitySelectionChangedListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent _event) {
			IStructuredSelection selection = (IStructuredSelection) _event.getSelection();
			setSelection(selection);
		}
	}

	protected class EOArgumentSelectionChangedListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent _event) {
			IStructuredSelection selection = (IStructuredSelection) _event.getSelection();
			setSelection(selection);
		}
	}

	protected class DirtyModelListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent _event) {
			String propertyName = _event.getPropertyName();
			if (EOModel.DIRTY.equals(propertyName)) {
				EOModelEditor.this.editorDirtyStateChanged();
			}
		}
	}

	protected class EntitiesChangeRefresher extends AbstractAddRemoveChangeRefresher {
		protected void objectsAdded(List _addedObjects) {
			EOModelEditor.this.setSelection(new StructuredSelection(_addedObjects));
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOENTITY_PAGE));
		}

		protected void objectsRemoved(List _removedObjects) {
			EOModelEditor.this.setSelection(new StructuredSelection(EOModelEditor.this.getModel()));
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOMODEL_PAGE));
		}
	}

	protected class StoredProceduresChangeRefresher extends AbstractAddRemoveChangeRefresher {
		protected void objectsAdded(List _addedObjects) {
			EOModelEditor.this.setSelection(new StructuredSelection(_addedObjects));
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOSTOREDPROCEDURE_PAGE));
		}

		protected void objectsRemoved(List _removedObjects) {
			EOModelEditor.this.setSelection(new StructuredSelection(EOModelEditor.this.getModel()));
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOMODEL_PAGE));
		}
	}

	protected class DatabaseConfigsChangeRefresher extends AbstractAddRemoveChangeRefresher {
		protected void objectsAdded(List _addedObjects) {
			EOModelEditor.this.setSelection(new StructuredSelection(_addedObjects));
		}

		protected void objectsRemoved(List _removedObjects) {
			EOModelEditor.this.setSelection(new StructuredSelection(EOModelEditor.this.getModel()));
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOMODEL_PAGE));
		}
	}

	protected class ArgumentDeletedRefresher extends AbstractAddRemoveChangeRefresher {
		protected void objectsAdded(List _addedObjects) {
			// DO NOTHING
		}

		protected void objectsRemoved(List _removedObjects) {
			EOModelEditor.this.setSelection(new StructuredSelection(EOModelEditor.this.getSelectedStoredProcedure()));
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOSTOREDPROCEDURE_PAGE));
		}
	}

	protected class AttributeAndRelationshipDeletedRefresher extends AbstractAddRemoveChangeRefresher {
		protected void objectsAdded(List _addedObjects) {
			// DO NOTHING
		}

		protected void objectsRemoved(List _removedObjects) {
			EOModelEditor.this.setSelection(new StructuredSelection(EOModelEditor.this.getSelectedEntity()));
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOENTITY_PAGE));
		}
	}
}