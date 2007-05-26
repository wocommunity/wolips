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
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
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
import org.objectstyle.wolips.eomodeler.core.model.AbstractEOAttributePath;
import org.objectstyle.wolips.eomodeler.core.model.EOArgument;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EODatabaseConfig;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOEntityIndex;
import org.objectstyle.wolips.eomodeler.core.model.EOFetchSpecification;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelException;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;
import org.objectstyle.wolips.eomodeler.core.model.EOStoredProcedure;
import org.objectstyle.wolips.eomodeler.core.model.IEOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.IEOModelGroupFactory;
import org.objectstyle.wolips.eomodeler.core.utils.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.core.utils.EOModelUtils;
import org.objectstyle.wolips.eomodeler.core.utils.URLUtils;
import org.objectstyle.wolips.eomodeler.editors.arguments.EOArgumentsTableEditor;
import org.objectstyle.wolips.eomodeler.editors.entities.EOEntitiesTableEditor;
import org.objectstyle.wolips.eomodeler.editors.entity.EOEntityEditor;
import org.objectstyle.wolips.eomodeler.outline.EOModelContentOutlinePage;
import org.objectstyle.wolips.eomodeler.preferences.PreferenceConstants;
import org.objectstyle.wolips.eomodeler.utils.AbstractAddRemoveChangeRefresher;
import org.objectstyle.wolips.eomodeler.utils.ErrorUtils;

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

	private FetchSpecsChangeRefresher myFetchSpecsChangeListener;

	private EntityIndexesChangeRefresher myEntityIndexesChangeListener;

	private StoredProceduresChangeRefresher myStoredProceduresChangeListener;

	private DatabaseConfigsChangeRefresher myDatabaseConfigsChangeListener;

	private AttributeAndRelationshipDeletedRefresher myAttributeAndRelationshipListener;

	private ArgumentDeletedRefresher myArgumentListener;

	private EOStoredProcedure mySelectedStoredProcedure;

	private EOEntity mySelectedEntity;

	private EOEntity myOpeningEntity;

	private EOModel myModel;

	private Set<EOModelVerificationFailure> myLoadFailures;

	private boolean myEntityPageVisible;

	private boolean myStoredProcedurePageVisible;

	private int mySelectionDepth;

	private Object myCreatePagesLock = new Object();

	public EOModelEditor() {
		mySelectionChangedListeners = new ListenerList();
		myDirtyModelListener = new DirtyModelListener();
		myEntitiesChangeListener = new EntitiesChangeRefresher();
		myFetchSpecsChangeListener = new FetchSpecsChangeRefresher();
		myEntityIndexesChangeListener = new EntityIndexesChangeRefresher();
		myStoredProceduresChangeListener = new StoredProceduresChangeRefresher();
		myDatabaseConfigsChangeListener = new DatabaseConfigsChangeRefresher();
		myAttributeAndRelationshipListener = new AttributeAndRelationshipDeletedRefresher();
		myArgumentListener = new ArgumentDeletedRefresher();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	public IUndoContext getUndoContext() {
		return new EOModelEditorUndoContext();
	}

	protected class EOModelEditorUndoContext implements IUndoContext {
		public String getLabel() {
			return EOModelUtils.getUndoContext(EOModelEditor.this.getModel()).getLabel();
		}

		public boolean matches(IUndoContext context) {
			return EOModelUtils.getUndoContext(EOModelEditor.this.getModel()).matches(context);
		}
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
		synchronized (myCreatePagesLock) {
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

		myEntitiesChangeListener.stop();
		myStoredProceduresChangeListener.stop();
		myDatabaseConfigsChangeListener.stop();
		myFetchSpecsChangeListener.stop();
		myEntityIndexesChangeListener.stop();

		super.dispose();

		if (Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.CHANGE_PERSPECTIVES_KEY)) {
			try {
				IWorkbench workbench = Activator.getDefault().getWorkbench();
				IWorkbenchPage workbenchPage = workbench.getActiveWorkbenchWindow().getActivePage();
				if (workbenchPage != null && EOModelerPerspectiveFactory.EOMODELER_PERSPECTIVE_ID.equals(workbenchPage.getPerspective().getId())) {
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
				// ErrorUtils.openErrorDialog(Display.getDefault().getActiveShell(),
				// e);
				Activator.getDefault().log(e);
			}
		}
	}

	public void doSave(IProgressMonitor _monitor) {
		if (!myModel.isEditing()) {
			ErrorUtils.openErrorDialog(Display.getDefault().getActiveShell(), "You cannot save this model because it is read-only.");
			return;
		}
		showBusy(true);
		try {
			IEditorInput input = getEditorInput();
			if (input != null && myModel != null) {
				Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();
				myModel.verify(failures);
				handleModelErrors(failures, false);

				IFile indexFile = EOModelEditor.getIndexFile(myModel);
				IContainer eomodelFolder = indexFile.getParent();
				System.out.println("EOModelEditor.doSave: saving " + eomodelFolder.getName() + " to " + eomodelFolder.getParent().getLocation().toFile());
				myModel.saveToFolder(eomodelFolder.getParent().getLocation().toFile());
				myModel.setDirty(false);
				eomodelFolder.refreshLocal(IResource.DEPTH_INFINITE, _monitor);
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

	protected void loadInBackground(IProgressMonitor progressMonitor) {
		try {
			IFileEditorInput fileEditorInput = (IFileEditorInput) getEditorInput();
			if (myModel != null) {
				myModel.removePropertyChangeListener(EOModel.DIRTY, myDirtyModelListener);
				myEntitiesChangeListener.stop();
				myModel.removePropertyChangeListener(EOModel.ENTITIES, myEntitiesChangeListener);
				myStoredProceduresChangeListener.stop();
				myModel.removePropertyChangeListener(EOModel.STORED_PROCEDURES, myStoredProceduresChangeListener);
				myDatabaseConfigsChangeListener.stop();
				myModel.removePropertyChangeListener(EOModel.DATABASE_CONFIGS, myDatabaseConfigsChangeListener);
				myFetchSpecsChangeListener.stop();
				myModel.removePropertyChangeListener(EOModel.ENTITY + "." + EOEntity.FETCH_SPECIFICATIONS, myFetchSpecsChangeListener);
				myEntityIndexesChangeListener.stop();
				myModel.removePropertyChangeListener(EOModel.ENTITY + "." + EOEntity.ENTITY_INDEXES, myEntityIndexesChangeListener);
			}

			IFile file = fileEditorInput.getFile();
			String openingEntityName = null;
			if ("plist".equalsIgnoreCase(file.getFileExtension())) {
				String name = file.getName();
				openingEntityName = name.substring(0, name.indexOf('.'));
			}

			myLoadFailures = new LinkedHashSet<EOModelVerificationFailure>();

			EOModel model = IEOModelGroupFactory.Utility.loadModel(file, myLoadFailures, true, progressMonitor);
			if (model == null) {
				// super.init(_site, fileEditorInput);
				handleModelErrors(myLoadFailures, true);
				// throw new EOModelException("Failed to load the requested
				// model.");
			} else {
				IFile indexFile = EOModelEditor.getIndexFile(model);
				if (indexFile != null) {
					fileEditorInput = new FileEditorInput(indexFile);
				}
				if (openingEntityName != null) {
					myOpeningEntity = model.getEntityNamed(openingEntityName);
				}
				handleModelErrors(myLoadFailures, false);

				model.addPropertyChangeListener(EOModel.DIRTY, myDirtyModelListener);
				myEntitiesChangeListener.start();
				model.addPropertyChangeListener(EOModel.ENTITIES, myEntitiesChangeListener);
				myStoredProceduresChangeListener.start();
				model.addPropertyChangeListener(EOModel.STORED_PROCEDURES, myStoredProceduresChangeListener);
				myDatabaseConfigsChangeListener.start();
				model.addPropertyChangeListener(EOModel.DATABASE_CONFIGS, myDatabaseConfigsChangeListener);
				myFetchSpecsChangeListener.start();
				model.addPropertyChangeListener(EOModel.ENTITY + "." + EOEntity.FETCH_SPECIFICATIONS, myFetchSpecsChangeListener);
				myEntityIndexesChangeListener.start();
				model.addPropertyChangeListener(EOModel.ENTITY + "." + EOEntity.ENTITY_INDEXES, myEntityIndexesChangeListener);
				// setInput(new EOModelEditorInput(fileEditorInput));
				// init(getEditorSite(), new
				// EOModelEditorInput(fileEditorInput));
				updatePartName();
				getEditorSite().setSelectionProvider(this);
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						EOModelEditor.this.editorDirtyStateChanged();
					}
				});

				synchronized (myCreatePagesLock) {
					myModel = model;
					if (myEntitiesTableEditor != null) {
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								myEntitiesTableEditor.setModel(myModel);
								getContentOutlinePage().getUpdater().setModel(myModel);
							}
						});
					}
				}
			}
		} catch (Throwable e) {
			handleModelErrors(myLoadFailures, true);
			e.printStackTrace();
			// throw new PartInitException("Failed to create EOModelEditorInput
			// for " + getEditorInput() + ".", e);
		}
	}

	public void init(IEditorSite _site, IEditorInput _editorInput) throws PartInitException {
		try {
			if (Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.CHANGE_PERSPECTIVES_KEY)) {
				IWorkbench workbench = Activator.getDefault().getWorkbench();
				workbench.showPerspective(EOModelerPerspectiveFactory.EOMODELER_PERSPECTIVE_ID, workbench.getActiveWorkbenchWindow());
			}
			super.init(_site, _editorInput);

			final Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					ProgressMonitorDialog progressMonitor = new ProgressMonitorDialog(shell);
					try {
						progressMonitor.run(true, true, new IRunnableWithProgress() {
							public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
								loadInBackground(monitor);
							}
						});
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}
	}

	protected void handleModelErrors(final Set<EOModelVerificationFailure> _failures, boolean forceOpen) {
		if (myModel != null) {
			try {
				if (Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.SHOW_ERRORS_IN_PROBLEMS_VIEW_KEY)) {
					final EOModel editingModel = myModel;
					IWorkspaceRunnable body = new IWorkspaceRunnable() {
						public void run(IProgressMonitor monitor) throws CoreException {
							for (EOModel model : editingModel.getModelGroup().getModels()) {
								try {
									IFile indexFile = EOModelEditor.getIndexFile(model);
									if (indexFile != null) {
										IMarker[] oldMarkers = indexFile.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
										for (int markerNum = 0; markerNum < oldMarkers.length; markerNum++) {
											// System.out.println("EOModelEditor.handleModelErrors:
											// deleting " + markers[markerNum]);
											oldMarkers[markerNum].delete();
										}
										IMarker[] newMarkers = indexFile.findMarkers(Activator.EOMODEL_PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
										for (int markerNum = 0; markerNum < newMarkers.length; markerNum++) {
											// System.out.println("EOModelEditor.handleModelErrors:
											// deleting " + markers[markerNum]);
											newMarkers[markerNum].delete();
										}
									}
								} catch (Exception e) {
									Activator.getDefault().log(e);
								}
							}

							for (EOModelVerificationFailure failure : _failures) {
								EOModel model = failure.getModel();
								IFile indexFile;
								try {
									indexFile = EOModelEditor.getIndexFile(model);
									if (indexFile != null) {
										IMarker marker = indexFile.createMarker(Activator.EOMODEL_PROBLEM_MARKER);
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
								} catch (Exception e) {
									Activator.getDefault().log(e);
								}
							}
						}
					};
					IWorkspace workspace = ResourcesPlugin.getWorkspace();
					workspace.run(body, new NullProgressMonitor());
				}
			} catch (Exception e) {
				Activator.getDefault().log(e);
			}
		}

		boolean warnings = false;
		boolean errors = false;
		for (EOModelVerificationFailure failure : _failures) {
			if (failure.isWarning()) {
				warnings = true;
			} else {
				errors = true;
			}
		}

		boolean openWindow = false;
		if (forceOpen) {
			openWindow = true;
		} else {
			if (errors && Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.OPEN_WINDOW_ON_VERIFICATION_ERRORS_KEY)) {
				openWindow = true;
			}
			if (warnings && Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.OPEN_WINDOW_ON_VERIFICATION_WARNINGS_KEY)) {
				openWindow = true;
			}
		}
		if (openWindow) {
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
						// EODatabaseConfig selectedDatabaseConfig =
						// (EODatabaseConfig) selectedObject;
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
			EOModel model = EOModelUtils.getRelatedModel(selectedObject);
			if (model == null || model.isEditing()) {
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

	protected class EntitiesChangeRefresher extends AbstractAddRemoveChangeRefresher<EOEntity> {
		public void changeSelection(ISelection selection) {
			EOModelEditor.this.setSelection(selection);
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOENTITY_PAGE));
		}

		protected void objectsAdded(List<EOEntity> _addedObjects) {
			// DO NOTHING
		}

		protected void objectsRemoved(List<EOEntity> _removedObjects) {
			EOModelEditor.this.setSelection(new StructuredSelection(EOModelEditor.this.getModel()));
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOMODEL_PAGE));
		}
	}

	protected class FetchSpecsChangeRefresher extends AbstractAddRemoveChangeRefresher<EOFetchSpecification> {
		public void changeSelection(ISelection selection) {
			EOModelEditor.this.setSelection(selection);
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOENTITY_PAGE));
		}

		protected void objectsAdded(List<EOFetchSpecification> _addedObjects) {
			// DO NOTHING
		}

		protected void objectsRemoved(List<EOFetchSpecification> _removedObjects) {
			EOModelEditor.this.setSelection(new StructuredSelection(EOModelEditor.this.getModel()));
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOMODEL_PAGE));
		}
	}

	protected class EntityIndexesChangeRefresher extends AbstractAddRemoveChangeRefresher<EOEntityIndex> {
		public void changeSelection(ISelection selection) {
			EOModelEditor.this.setSelection(selection);
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOENTITY_PAGE));
		}

		protected void objectsAdded(List<EOEntityIndex> _addedObjects) {
			// DO NOTHING
		}

		protected void objectsRemoved(List<EOEntityIndex> _removedObjects) {
			EOModelEditor.this.setSelection(new StructuredSelection(EOModelEditor.this.getModel()));
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOMODEL_PAGE));
		}
	}

	protected class StoredProceduresChangeRefresher extends AbstractAddRemoveChangeRefresher<EOStoredProcedure> {
		public void changeSelection(ISelection selection) {
			EOModelEditor.this.setSelection(selection);
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOSTOREDPROCEDURE_PAGE));
		}

		protected void objectsAdded(List<EOStoredProcedure> _addedObjects) {
			// DO NOTHING
		}

		protected void objectsRemoved(List<EOStoredProcedure> _removedObjects) {
			EOModelEditor.this.setSelection(new StructuredSelection(EOModelEditor.this.getModel()));
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOMODEL_PAGE));
		}
	}

	protected class DatabaseConfigsChangeRefresher extends AbstractAddRemoveChangeRefresher<EODatabaseConfig> {
		public void changeSelection(ISelection selection) {
			EOModelEditor.this.setSelection(selection);
		}

		protected void objectsAdded(List<EODatabaseConfig> _addedObjects) {
			// DO NOTHING
		}

		protected void objectsRemoved(List<EODatabaseConfig> _removedObjects) {
			EOModelEditor.this.setSelection(new StructuredSelection(EOModelEditor.this.getModel()));
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOMODEL_PAGE));
		}
	}

	protected class ArgumentDeletedRefresher extends AbstractAddRemoveChangeRefresher<EOArgument> {
		public void changeSelection(ISelection selection) {
			EOModelEditor.this.setSelection(selection);
		}

		protected void objectsAdded(List<EOArgument> _addedObjects) {
			// DO NOTHING
		}

		protected void objectsRemoved(List<EOArgument> _removedObjects) {
			EOModelEditor.this.setSelection(new StructuredSelection(EOModelEditor.this.getSelectedStoredProcedure()));
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOSTOREDPROCEDURE_PAGE));
		}
	}

	protected class AttributeAndRelationshipDeletedRefresher extends AbstractAddRemoveChangeRefresher<IEOAttribute> {
		public void changeSelection(ISelection selection) {
			EOModelEditor.this.setSelection(selection);
		}

		protected void objectsAdded(List<IEOAttribute> _addedObjects) {
			// DO NOTHING
		}

		protected void objectsRemoved(List<IEOAttribute> _removedObjects) {
			EOModelEditor.this.setSelection(new StructuredSelection(EOModelEditor.this.getSelectedEntity()));
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOENTITY_PAGE));
		}
	}
}