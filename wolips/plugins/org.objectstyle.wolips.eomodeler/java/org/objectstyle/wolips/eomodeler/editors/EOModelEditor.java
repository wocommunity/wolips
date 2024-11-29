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
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.baseforplugins.util.URLUtils;
import org.objectstyle.wolips.baseforuiplugins.utils.ErrorUtils;
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
import org.objectstyle.wolips.eomodeler.core.model.EOLastModified;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;
import org.objectstyle.wolips.eomodeler.core.model.EORelationshipOptionalityMismatchFailure;
import org.objectstyle.wolips.eomodeler.core.model.EOStoredProcedure;
import org.objectstyle.wolips.eomodeler.core.model.IEOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.IEOModelGroupFactory;
import org.objectstyle.wolips.eomodeler.core.utils.EOModelUtils;
import org.objectstyle.wolips.eomodeler.editors.arguments.EOArgumentsTableEditor;
import org.objectstyle.wolips.eomodeler.editors.entities.EOEntitiesTableEditor;
import org.objectstyle.wolips.eomodeler.editors.entity.EOEntityEditor;
import org.objectstyle.wolips.eomodeler.outline.EOModelContentOutlinePage;
import org.objectstyle.wolips.eomodeler.preferences.PreferenceConstants;
import org.objectstyle.wolips.eomodeler.utils.AbstractAddRemoveChangeRefresher;
import org.objectstyle.wolips.eomodeler.utils.EclipseFileUtils;

import ch.rucotec.wolips.eomodeler.GEFTabFactory;
import ch.rucotec.wolips.eomodeler.IGEFDiagramTab;
import ch.rucotec.wolips.eomodeler.core.model.AbstractDiagram;
import ch.rucotec.wolips.eomodeler.core.model.AbstractDiagramCollection;
import ch.rucotec.wolips.eomodeler.core.model.EOERDiagram;
import ch.rucotec.wolips.eomodeler.core.model.EOERDiagramCollection;
import ch.rucotec.wolips.eomodeler.editors.diagrams.EODiagramsTableEditor;

public class EOModelEditor extends MultiPageEditorPart implements IResourceChangeListener, ITabbedPropertySheetPageContributor, ISelectionProvider, IEOModelEditor {
	protected class ArgumentDeletedRefresher extends AbstractAddRemoveChangeRefresher<EOArgument> {
		public ArgumentDeletedRefresher() {
			super("ArgumentDeleted");
		}

		public void changeSelection(final ISelection selection) {
			EOModelEditor.this.setSelection(selection);
		}

		@Override
		protected void objectsAdded(final List<EOArgument> _addedObjects) {
			// DO NOTHING
		}

		@Override
		protected void objectsRemoved(final List<EOArgument> _removedObjects) {
			EOModelEditor.this.setSelection(new StructuredSelection(EOModelEditor.this.getSelectedStoredProcedure()));
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOSTOREDPROCEDURE_PAGE));
		}
	}

	protected class AttributeAndRelationshipDeletedRefresher extends AbstractAddRemoveChangeRefresher<IEOAttribute> {
		public AttributeAndRelationshipDeletedRefresher() {
			super("AttributeAndRelationshipDeleted");
		}

		public void changeSelection(final ISelection selection) {
			EOModelEditor.this.setSelection(selection);
		}

		@Override
		protected void objectsAdded(final List<IEOAttribute> _addedObjects) {
			// DO NOTHING
		}

		@Override
		protected void objectsRemoved(final List<IEOAttribute> _removedObjects) {
			EOModelEditor.this.setSelection(new StructuredSelection(EOModelEditor.this.getSelectedEntity()));
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOENTITY_PAGE));
		}
	}

	protected class DatabaseConfigsChangeRefresher extends AbstractAddRemoveChangeRefresher<EODatabaseConfig> {
		public DatabaseConfigsChangeRefresher() {
			super("DatabaseConfigsChange");
		}

		public void changeSelection(final ISelection selection) {
			EOModelEditor.this.setSelection(selection);
		}

		@Override
		protected void objectsAdded(final List<EODatabaseConfig> _addedObjects) {
			// DO NOTHING
		}

		@Override
		protected void objectsRemoved(final List<EODatabaseConfig> _removedObjects) {
			EOModelEditor.this.setSelection(new StructuredSelection(EOModelEditor.this.getModel()));
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOMODEL_PAGE));
		}
	}

	protected class DirtyModelListener implements PropertyChangeListener {
		public void propertyChange(final PropertyChangeEvent _event) {
			String propertyName = _event.getPropertyName();
			if (EOModel.DIRTY.equals(propertyName)) {
				EOModelEditor.this.editorDirtyStateChanged();
			}
		}
	}

	protected class EntitiesChangeRefresher extends AbstractAddRemoveChangeRefresher<EOEntity> {
		public EntitiesChangeRefresher() {
			super("EntitiesChange");
		}

		public void changeSelection(final ISelection selection) {
			EOModelEditor.this.setSelection(selection);
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOENTITY_PAGE));
		}

		@Override
		protected void objectsAdded(final List<EOEntity> _addedObjects) {
			// DO NOTHING
		}

		@Override
		protected void objectsRemoved(final List<EOEntity> _removedObjects) {
			EOModelEditor.this.setSelection(new StructuredSelection(EOModelEditor.this.getModel()));
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOMODEL_PAGE));
		}
	}

	protected class EntityIndexesChangeRefresher extends AbstractAddRemoveChangeRefresher<EOEntityIndex> {
		public EntityIndexesChangeRefresher() {
			super("EntityIndexesChange");
		}

		public void changeSelection(final ISelection selection) {
			EOModelEditor.this.setSelection(selection);
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOENTITY_PAGE));
		}

		@Override
		protected void objectsAdded(final List<EOEntityIndex> _addedObjects) {
			// DO NOTHING
		}

		@Override
		protected void objectsRemoved(final List<EOEntityIndex> _removedObjects) {
			EOModelEditor.this.setSelection(new StructuredSelection(EOModelEditor.this.getModel()));
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOMODEL_PAGE));
		}
	}

	protected class EOArgumentSelectionChangedListener implements ISelectionChangedListener {
		public void selectionChanged(final SelectionChangedEvent _event) {
			IStructuredSelection selection = (IStructuredSelection) _event.getSelection();
			setSelection(selection);
		}
	}

	protected class EOEntitySelectionChangedListener implements ISelectionChangedListener {
		public void selectionChanged(final SelectionChangedEvent _event) {
			IStructuredSelection selection = (IStructuredSelection) _event.getSelection();
			setSelection(selection);
		}
	}

	protected class EOModelContentSelectionChangedListener implements ISelectionChangedListener {
		private Object mySelectedObject;

		public void selectionChanged(final SelectionChangedEvent _event) {
			IStructuredSelection selection = (IStructuredSelection) _event.getSelection();
			Object selectedObject = selection.getFirstElement();
			setSelection(selection, false);
			if (myContentOutlinePage.isSelectedWithOutline()) {
				EOModelEditor.this.doubleClickedObjectInOutline(selectedObject);
			} else {
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

	protected class EOModelEditorUndoContext implements IUndoContext {
		public String getLabel() {
			return EOModelUtils.getUndoContext(EOModelEditor.this.getModel()).getLabel();
		}

		public boolean matches(final IUndoContext context) {
			return EOModelUtils.getUndoContext(EOModelEditor.this.getModel()).matches(context);
		}
	}

	protected class EOModelSelectionChangedListener implements ISelectionChangedListener {
		public void selectionChanged(final SelectionChangedEvent _event) {
			IStructuredSelection selection = (IStructuredSelection) _event.getSelection();
			setSelection(selection);
		}
	}

	protected class FetchSpecsChangeRefresher extends AbstractAddRemoveChangeRefresher<EOFetchSpecification> {
		public FetchSpecsChangeRefresher() {
			super("FetchSpecsChange");
		}

		public void changeSelection(final ISelection selection) {
			EOModelEditor.this.setSelection(selection);
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOENTITY_PAGE));
		}

		@Override
		protected void objectsAdded(final List<EOFetchSpecification> _addedObjects) {
			// DO NOTHING
		}

		@Override
		protected void objectsRemoved(final List<EOFetchSpecification> _removedObjects) {
			EOModelEditor.this.setSelection(new StructuredSelection(EOModelEditor.this.getModel()));
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOMODEL_PAGE));
		}
	}

	protected class StoredProceduresChangeRefresher extends AbstractAddRemoveChangeRefresher<EOStoredProcedure> {
		public StoredProceduresChangeRefresher() {
			super("StoredProceduresChange");
		}

		public void changeSelection(final ISelection selection) {
			EOModelEditor.this.setSelection(selection);
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOSTOREDPROCEDURE_PAGE));
		}

		@Override
		protected void objectsAdded(final List<EOStoredProcedure> _addedObjects) {
			// DO NOTHING
		}

		@Override
		protected void objectsRemoved(final List<EOStoredProcedure> _removedObjects) {
			EOModelEditor.this.setSelection(new StructuredSelection(EOModelEditor.this.getModel()));
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EOMODEL_PAGE));
		}
	}
	
	// SAVAS sobald ein neues Diagram erstellt oder geloescht wird, wird die selektion im Outline Tab angepasst.
	protected class DiagramDeletedRefresher extends AbstractAddRemoveChangeRefresher<EOERDiagram> {
		public DiagramDeletedRefresher() {
			super("DiagramDeleted");
		}

		public void changeSelection(final ISelection selection) {
			EOModelEditor.this.setSelection(selection);
		}

		@Override
		protected void objectsAdded(final List<EOERDiagram> _addedObjects) {
			// DO NOTHING
		}

		@Override
		protected void objectsRemoved(final List<EOERDiagram> _removedObjects) {
			EOModelEditor.this.setSelection(new StructuredSelection(EOModelEditor.this.getMySelectedDiagramCollection()));
			EOModelEditor.this.setActivePage(getPageNum(EOModelEditor.EODIAGRAMCOLLECTION_PAGE));
		}
	}
	
	protected class EODiagramSelectionChangedListener implements ISelectionChangedListener {
		public void selectionChanged(final SelectionChangedEvent _event) {
			IStructuredSelection selection = (IStructuredSelection) _event.getSelection();
			setSelection(selection);
		}
	}

	public static final String EOMODEL_EDITOR_ID = "org.objectstyle.wolips.eomodeler.editors.EOModelEditor";

	public static final String EOMODEL_PAGE = "eomodel";

	public static final String EOENTITY_PAGE = "eoentity";
	
	public static final String EOERD_PAGE = "eoerd";

	public static final String EOSTOREDPROCEDURE_PAGE = "eostoredprocedure";

	private EOEntitiesTableEditor myEntitiesTableEditor;

	private EOEntityEditor myEntityEditor;

	private EOArgumentsTableEditor myStoredProcedureEditor;

	private EOModelContentOutlinePage myContentOutlinePage;

	private final ListenerList mySelectionChangedListeners;

	private IStructuredSelection mySelection;

	private final PropertyChangeListener myDirtyModelListener;

	private final EntitiesChangeRefresher myEntitiesChangeListener;

	private final FetchSpecsChangeRefresher myFetchSpecsChangeListener;

	private final EntityIndexesChangeRefresher myEntityIndexesChangeListener;

	private final StoredProceduresChangeRefresher myStoredProceduresChangeListener;

	private final DatabaseConfigsChangeRefresher myDatabaseConfigsChangeListener;

	private final AttributeAndRelationshipDeletedRefresher myAttributeAndRelationshipListener;

	private final ArgumentDeletedRefresher myArgumentListener;

	private EOStoredProcedure mySelectedStoredProcedure;

	private EOEntity mySelectedEntity;

	private EOEntity myOpeningEntity;

	private EOModel myModel;

	private Set<EOModelVerificationFailure> myLoadFailures;

	private boolean myEntityPageVisible;

	private boolean myStoredProcedurePageVisible;

	private int mySelectionDepth;

	private final Object myCreatePagesLock = new Object();

	private int _failuresHashCode;
	
	// SAVAS variablen
	public static final String EODIAGRAMCOLLECTION_PAGE = "eodiagramcollection";
	public static final String EODIAGRAM_PAGE = "eodiagram";
	
	private IGEFDiagramTab diagramTab;
	private EODiagramsTableEditor myDiagramCollectionEditor;
	private AbstractDiagramCollection mySelectedDiagramCollection;
	private final DiagramDeletedRefresher myDiagramListener;
	private AbstractDiagram mySelectedDiagram;
	private boolean myDiagramCollectionPageVisible;
	private boolean myDiagramPageVisible;
	
	
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
		// SAVAS DiagramListener
		myDiagramListener = new DiagramDeletedRefresher();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	public void _loadInBackground(final IProgressMonitor progressMonitor) {
		myLoadFailures = new LinkedHashSet<EOModelVerificationFailure>();

		try {
			IEditorInput editorInput = getEditorInput();
			URI indexURL = null;
			if (editorInput instanceof IURIEditorInput) {
				indexURL = ((IURIEditorInput) editorInput).getURI();
			} else if (editorInput instanceof IStorageEditorInput) {
				// MS: This is a total hackfest here ... This supports
				// double-clicking an index.eomodeld from
				// inside of a jar
				IStorage storage = ((IStorageEditorInput) editorInput).getStorage();
				Class jarEntryClass = storage.getClass();
				IPath jarEntryPath = (IPath) jarEntryClass.getMethod("getFullPath").invoke(storage);
				Object root = jarEntryClass.getMethod("getPackageFragmentRoot").invoke(storage);
				Class packageFragmentRootClass = root.getClass();
				IResource jarResource = (IResource)packageFragmentRootClass.getMethod("getUnderlyingResource").invoke(root);
				IPath jarPath;
				if (jarResource == null) {
					jarPath = (IPath) packageFragmentRootClass.getMethod("getPath").invoke(root);
				}
				else {
					jarPath = jarResource.getLocation();
				}
				indexURL = new URI("jar:" + jarPath.toFile().toURL() + "!" + jarEntryPath.toPortableString());
			}
			if (myModel != null) {
				if (myModel.getModelGroup() != null) {
					myModel.getModelGroup().removePropertyChangeListener(EOModel.DIRTY, myDirtyModelListener);
				}
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
				myDiagramListener.stop();
				myModel.removePropertyChangeListener(AbstractDiagramCollection.DIAGRAMS, myDiagramListener);
			}

			String openingEntityName = null;
			String extension = URLUtils.getExtension(indexURL);
			if ("plist".equalsIgnoreCase(extension)) {
				String name = URLUtils.getName(indexURL);
				openingEntityName = name.substring(0, name.indexOf('.'));
				indexURL = new File(URLUtils.cheatAndTurnIntoFile(indexURL).getParentFile(), "index.eomodeld").toURI();
			} else if ("fspec".equalsIgnoreCase(extension)) {
				indexURL = new File(URLUtils.cheatAndTurnIntoFile(indexURL).getParentFile(), "index.eomodeld").toURI();
			}

			EOModelGroup modelGroup = new EOModelGroup();
			modelGroup.addPropertyChangeListener(EOModelGroup.MODELS, getContentOutlinePage());
			try {
				IEOModelGroupFactory.Utility.loadModelGroup(indexURL, modelGroup, myLoadFailures, true, indexURL.toURL(), progressMonitor);
			} finally {
				modelGroup.removePropertyChangeListener(EOModelGroup.MODELS, getContentOutlinePage());
			}

			EOModel model = modelGroup.getEditingModel();
			boolean showModelGroup = true;
			if (model == null) {
				handleModelErrors(myLoadFailures, true, null);
				Set<EOModel> models = modelGroup.getModels();
				if (models.size() > 0) {
					model = models.iterator().next();
					showModelGroup = true;
				}
			}
			if (model == null) {
				// DO NOTHING
			} else {
				//EclipseFileUtils.getEditorInput(model);
				if (openingEntityName != null) {
					myOpeningEntity = model.getEntityNamed(openingEntityName);
				}
				handleModelErrors(myLoadFailures, false, null);

				if (model.getModelGroup() != null) {
					model.getModelGroup().addPropertyChangeListener(EOModel.DIRTY, myDirtyModelListener);
				}
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
				myDiagramListener.start();
				model.addPropertyChangeListener(AbstractDiagramCollection.DIAGRAMS, myDiagramListener);
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
						final boolean finalShowModelGroup = showModelGroup;
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								if (myEntitiesTableEditor != null) {
									myEntitiesTableEditor.setModel(myModel);
								}
								if (getContentOutlinePage() != null && getContentOutlinePage().getUpdater() != null) {
									getContentOutlinePage().getUpdater().setModel(myModel);
									if (finalShowModelGroup) {
										getContentOutlinePage().showModelGroup();
									}
								}
							}
						});
					}
				}

				if (myOpeningEntity != null) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							setSelectedEntity(myOpeningEntity);
							setActivePage(getPageNum(EOModelEditor.EOENTITY_PAGE));
						}
					});
				}
			}
		} catch (Throwable e) {
			myLoadFailures.add(new EOModelVerificationFailure(null, "Failed to load model.", false, e));
			handleModelErrors(myLoadFailures, true, null);
			e.printStackTrace();
			// throw new PartInitException("Failed to create EOModelEditorInput
			// for " + getEditorInput() + ".", e);
		}
	}

	public void addSelectionChangedListener(final ISelectionChangedListener _listener) {
		mySelectionChangedListeners.add(_listener);
	}

	protected int computeFailuresHashCode(final Set<EOModelVerificationFailure> failures) {
		StringBuffer sb = new StringBuffer();
		for (EOModelVerificationFailure failure : failures) {
			sb.append(failure.getMessage());
		}
		return sb.toString().hashCode();
	}

	@Override
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
				
				// SAVAS: Hier wird das DiagramTab configuriert.
				diagramTab = GEFTabFactory.getDiagramTab();
				
				// das ist nicht notwendig aber wenn ich es mache werden diagramme schneller generiert.
				addPage((IEditorPart) diagramTab, getEditorInput());
				removePage(getPageNum(EOModelEditor.EODIAGRAM_PAGE));

				myDiagramCollectionEditor = new EODiagramsTableEditor();
				EODiagramSelectionChangedListener diagramSelectionChangedListener = new EODiagramSelectionChangedListener();
				myDiagramCollectionEditor.addSelectionChangedListener(diagramSelectionChangedListener);

			} catch (PartInitException e) {
				ErrorDialog.openError(getSite().getShell(), "Error creating editor.", null, e.getStatus());
			}
		}
	}

	@Override
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);

		myEntitiesChangeListener.stop();
		myStoredProceduresChangeListener.stop();
		myDatabaseConfigsChangeListener.stop();
		myFetchSpecsChangeListener.stop();
		myEntityIndexesChangeListener.stop();
		myDiagramListener.stop();

		super.dispose();

		switchFromEntityModelerPerspective();
	}

	@Override
	public void doSave(final IProgressMonitor monitor) {
		// if (!myModel.isEditing()) {
		// ErrorUtils.openErrorDialog(Display.getDefault().getActiveShell(),
		// "You cannot save this model because it is read-only.");
		// return;
		// }
		showBusy(true);
		try {
			IEditorInput input = getEditorInput();
			if (input != null && myModel != null) {
				Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();

				final List<EOModel> dirtyModels = new LinkedList<EOModel>();
				for (EOModel model : myModel.getModelGroup().getModels()) {
					if (model.isDirty()) {
						dirtyModels.add(model);
					}
				}
				
				boolean saveAllModels = true;
				if (dirtyModels.size() > 1 || !dirtyModels.contains(myModel)) {
					Set<String> modelNames = new TreeSet<String>();
					for (EOModel model : dirtyModels) {
						if (model != myModel) {
							modelNames.add(model.getName());
						}
					}
					saveAllModels = MessageDialog.openQuestion(getSite().getShell(), "Additional Models Modified", "You modified the following additional models in this model group: " + modelNames + ". Would you like to save them, also?");
				}

				Set<EOModel> doNotSaveModels = new HashSet<EOModel>();
				for (EOModel model : dirtyModels) {
					if (model == myModel || saveAllModels) {
						Set<EOLastModified> lastModified = new HashSet<EOLastModified>();
						model.checkLastModified(lastModified);
						if (!lastModified.isEmpty()) {
							if (!MessageDialog.openConfirm(Display.getDefault().getActiveShell(), "Model Changed on Disk", "The model '" + model.getName() + "' changed on disk since you opened it. Are you want to overwrite those changes?")) {
								doNotSaveModels.add(model);
							}
						}
					}
				}
				dirtyModels.removeAll(doNotSaveModels);
				
				for (EOModel model : dirtyModels) {
					if (model == myModel || saveAllModels) {
						monitor.beginTask("Checking " + model.getName() + " ...", IProgressMonitor.UNKNOWN);
						try {
							model.verify(failures);
							if (!model.canSave()) {
								failures.add(new EOModelVerificationFailure(model, "You modified the model '" + model.getName() + "', but you are not able to save it to '" + model.getIndexURL() + "'.", false));
							}
						} finally {
							monitor.done();
						}
					}
				}
				
				final boolean finalSaveAllModels = saveAllModels;
				handleModelErrors(failures, false, new Runnable() {
					public void run() {
						showBusy(true);
						try {
							for (EOModel model : dirtyModels) {
								if (model.canSave() && (model == myModel || finalSaveAllModels)) {
									monitor.beginTask("Saving " + model.getName() + " ...", IProgressMonitor.UNKNOWN);
									try {
										model.save();
		
										IFile eclipseIndexFile = EclipseFileUtils.getEclipseFile(myModel.getIndexURL());
										if (eclipseIndexFile != null) {
											eclipseIndexFile.getParent().getParent().refreshLocal(IResource.DEPTH_INFINITE, monitor);
										}
									} finally {
										monitor.done();
									}
								}
							}
						}
						catch (Throwable t) {
							ErrorUtils.openErrorDialog(Display.getDefault().getActiveShell(), t);
						} finally {
							showBusy(false);
						}
					}
				});
			}
		} catch (Throwable t) {
			ErrorUtils.openErrorDialog(Display.getDefault().getActiveShell(), t);
		} finally {
			showBusy(false);
		}
	}

	@Override
	public void doSaveAs() {
		doSave(null);
	}

	protected void doubleClickedObjectInOutline(final Object _obj) {
		if (_obj instanceof EOEntity) {
			setActivePage(getPageNum(EOModelEditor.EOENTITY_PAGE));
		} else if (_obj instanceof EOStoredProcedure) {
			setActivePage(getPageNum(EOModelEditor.EOSTOREDPROCEDURE_PAGE));
		}
	}

	protected void editorDirtyStateChanged() {
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	protected void fireSelectionChanged(final ISelection _selection) {
		// MS: It appears 3.5.1, at least, no longer requires this hack
		// Hack: When the selection changes, update the focus on the outline
		// view or the properties view can sometimes not refresh properly -- It's really
		// silly and I don't know why it's happening.
		// MS: 3.6 needs it again :\ I still don't understand it, but we're a little better now -- we 
		// steal away focus and then steal it back.
		IWorkbenchPart activePart = getSite().getPage().getActivePart();
		if (activePart instanceof PropertySheet) {
			if (getContentOutlinePage().getControl() != null) {
				getContentOutlinePage().setFocus();
			}
			getSite().getPage().activate(activePart);
		}
		Object[] selectionChangedListeners = mySelectionChangedListeners.getListeners();
		SelectionChangedEvent selectionChangedEvent = new SelectionChangedEvent(this, _selection);
		for (int listenerNum = 0; listenerNum < selectionChangedListeners.length; listenerNum++) {
			ISelectionChangedListener listener = (ISelectionChangedListener) selectionChangedListeners[listenerNum];
			listener.selectionChanged(selectionChangedEvent);
		}
	}

	@Override
	public Object getAdapter(final Class _adapterClass) {
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

	public EOModelContentOutlinePage getContentOutlinePage() {
		if (myContentOutlinePage == null) {
			myContentOutlinePage = new EOModelContentOutlinePage(this);
			myContentOutlinePage.addSelectionChangedListener(new EOModelContentSelectionChangedListener());
		}
		return myContentOutlinePage;
	}

	public String getContributorId() {
		return getSite().getId();
	}

	public EOEntitiesTableEditor getEntitiesTableEditor() {
		return myEntitiesTableEditor;
	}

	public EOEntityEditor getEntityEditor() {
		return myEntityEditor;
	}

	public EOModel getModel() {
		return myModel;
	}

	protected int getPageNum(final IEditorPart _editorPart) {
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

	protected int getPageNum(final String _pageType) {
		int pageNum;
		if (_pageType == EOModelEditor.EOENTITY_PAGE) {
			pageNum = getPageNum(myEntityEditor);
		} else if (_pageType == EOModelEditor.EOMODEL_PAGE) {
			pageNum = getPageNum(myEntitiesTableEditor);
		} else if (_pageType == EOModelEditor.EOSTOREDPROCEDURE_PAGE) {
			pageNum = getPageNum(myStoredProcedureEditor);
		} 
		// SAVAS PageNumber
		else if(_pageType == EOModelEditor.EODIAGRAMCOLLECTION_PAGE) {
			pageNum = getPageNum(myDiagramCollectionEditor);
		} else if (_pageType == EOModelEditor.EODIAGRAM_PAGE) {
			pageNum = getPageNum((IEditorPart) diagramTab);
		}else {
			pageNum = -1;
		}
		return pageNum;
	}

	public EOEntity getSelectedEntity() {
		return mySelectedEntity;
	}

	public EOStoredProcedure getSelectedStoredProcedure() {
		return mySelectedStoredProcedure;
	}

	public ISelection getSelection() {
		return mySelection;
	}

	public EOArgumentsTableEditor getStoredProcedureEditor() {
		return myStoredProcedureEditor;
	}

	public IUndoContext getUndoContext() {
		return new EOModelEditorUndoContext();
	}

	protected void handleModelErrors(final Set<EOModelVerificationFailure> failures, final boolean forceOpen, final Runnable executeWhenApproved) {
		if (myModel != null) {
			try {
				if (Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.SHOW_ERRORS_IN_PROBLEMS_VIEW_KEY)) {
					final EOModel editingModel = myModel;
					IWorkspaceRunnable body = new IWorkspaceRunnable() {
						public void run(final IProgressMonitor monitor) throws CoreException {
							for (EOModel model : editingModel.getModelGroup().getModels()) {
								try {
									IFile indexFile = EclipseFileUtils.getEclipseIndexFile(model);
									if (indexFile != null) {
										IMarker[] oldMarkers = indexFile.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
										for (int markerNum = 0; markerNum < oldMarkers.length; markerNum++) {
											// System.out.println("EOModelEditor.handleModelErrors:
											// deleting " + markers[markerNum]);
											oldMarkers[markerNum].delete();
										}
										IMarker[] newMarkers = indexFile.findMarkers(org.objectstyle.wolips.eomodeler.core.Activator.EOMODEL_PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
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

							for (EOModelVerificationFailure failure : failures) {
								EOModel model = failure.getModel();
								IFile indexFile;
								try {
									indexFile = EclipseFileUtils.getEclipseIndexFile(model);
									if (indexFile != null) {
										IMarker marker = indexFile.createMarker(org.objectstyle.wolips.eomodeler.core.Activator.EOMODEL_PROBLEM_MARKER);
										marker.setAttribute(IMarker.MESSAGE, failure.getMessage());
										int severity;
										if (failure.isWarning()) {
											severity = IMarker.SEVERITY_WARNING;
										} else {
											severity = IMarker.SEVERITY_ERROR;
										}
										marker.setAttribute(IMarker.SEVERITY, Integer.valueOf(severity));
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

		if (!Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.SHOW_RELATIONSHIP_ATTRIBUTE_OPTIONALITY_MISMATCH)) {
			Iterator<EOModelVerificationFailure> failuresIter = failures.iterator();
			while (failuresIter.hasNext()) {
				EOModelVerificationFailure failure = failuresIter.next();
				if (failure instanceof EORelationshipOptionalityMismatchFailure) {
					failuresIter.remove();
				}
			}
		}

		boolean warnings = false;
		boolean errors = false;
		for (EOModelVerificationFailure failure : failures) {
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

		int newFailuresHashCode = computeFailuresHashCode(failures);
		if (openWindow && _failuresHashCode != 0 && newFailuresHashCode == _failuresHashCode) {
			openWindow = false;
		}
		_failuresHashCode = newFailuresHashCode;

		if (openWindow) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (!failures.isEmpty()) {
						EOModelErrorDialog dialog;
						if (executeWhenApproved != null) {
							dialog = new EOModelSaveErrorDialog(Display.getCurrent().getActiveShell(), failures, EOModelEditor.this);
						}
						else {
							dialog = new EOModelErrorDialog(Display.getCurrent().getActiveShell(), failures, EOModelEditor.this);
						}
						dialog.setBlockOnOpen(true);
						int result = dialog.open();
						if (executeWhenApproved != null) {
							if (result == Window.OK) {
								executeWhenApproved.run();
							}
							else {
								_failuresHashCode = 0;
							}
						}
					}
				}
			});
		}
		else if (executeWhenApproved != null) {
			executeWhenApproved.run();
		}
	}

	@Override
	public void init(final IEditorSite site, final IEditorInput editorInput) throws PartInitException {
		try {
			switchToEntityModelerPerspective();
			super.init(site, editorInput);

			LoadEOModelWorkspaceJob loadModelJob = new LoadEOModelWorkspaceJob(this, editorInput);
			loadModelJob.schedule();
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isDirty() {
		boolean dirty = false;
		if (myModel != null) {
			if (myModel.isDirty()) {
				dirty = true;
			} else if (myModel.getModelGroup() != null) {
				dirty = myModel.getModelGroup().isDirty();
			}
		}
		return dirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	protected void pageChange(final int _newPageIndex) {
		super.pageChange(_newPageIndex);
		ISelectionProvider selectionProvider = (ISelectionProvider) getEditor(_newPageIndex);
		getSite().setSelectionProvider(selectionProvider);
	}

	public void removeSelectionChangedListener(final ISelectionChangedListener _listener) {
		mySelectionChangedListeners.remove(_listener);
	}

	public void resourceChanged(final IResourceChangeEvent _event) {
		if (_event.getType() == IResourceChangeEvent.PRE_CLOSE) {
			final IURIEditorInput input = (IURIEditorInput) getEditorInput();
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (int pageNum = 0; pageNum < pages.length; pageNum++) {
						IFile eclipseIndexFile = EclipseFileUtils.getEclipseFile(input.getURI());
						if (eclipseIndexFile != null && eclipseIndexFile.getProject().equals(_event.getResource())) {
							IEditorPart editorPart = pages[pageNum].findEditor(input);
							pages[pageNum].closeEditor(editorPart, true);
						}
					}
				}
			});
		}
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

	@Override
	public void setActivePage(final int _pageIndex) {
		if (_pageIndex != getActivePage()) {
			super.setActivePage(_pageIndex);
		}
	}

	protected void setEntityPageVisible(final boolean _entityPageVisible) {
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

	@Override
	public void setFocus() {
		super.setFocus();
		// MS: I'm not sure the right way to do this, but without
		// this call, selecting a relationship in the EOModelEditor
		// before ever activing the outline would not cause the
		// property view to update.
		getSite().setSelectionProvider(this);

		// MS: If an Entity Modeler editor receives focus, and there is
		// more than one editor in the perspective, we want to switch
		// to Entity Modeler perspective. The "more than one editor"
		// restriction is in place, because if you only have an
		// Entity Modeler editor in the perspective, you won't be
		// able to ever switch perspectives to temporarily get access
		// to package explorer, for instance, because it will keep
		// forcing you back. This MIGHT actually be the correct
		// behavior, but it seemed really aggressive.
		if (Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.CHANGE_PERSPECTIVES_KEY)) {
			boolean shouldSwitchToEntityModeler = false;
			IWorkbench workbench = Activator.getDefault().getWorkbench();
			IWorkbenchWindow activeWindow = workbench.getActiveWorkbenchWindow();
			if (activeWindow != null) {
				if (activeWindow != null) {
					IWorkbenchPage workbenchPage = activeWindow.getActivePage();
					if (workbenchPage != null) {
						IEditorReference[] editorReferences = workbenchPage.getEditorReferences();
						if (editorReferences.length > 1) {
							shouldSwitchToEntityModeler = true;
						}
					}
				}
			}
			if (shouldSwitchToEntityModeler) {
				switchToEntityModelerPerspective();
			}
		}
	}

	public void setSelectedEntity(final EOEntity _selectedEntity) {
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
			// Fix a problem where the entity properties view does not refresh
			// This is a hack, but if we steal focus onto the outline, it DOES
			// refresh
			updatePartName();
		}
		if (_selectedEntity != null) {
			setSelectedStoredProcedure(null);
		}
	}

	public void setSelectedStoredProcedure(final EOStoredProcedure _selectedStoredProcedure) {
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

	public void setSelection(final ISelection _selection) {
		setSelection(_selection, true);
	}

	public synchronized void setSelection(final ISelection _selection, final boolean _updateOutline) {
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
//						 setActivePage(EOModelEditor.EOENTITY_PAGE);
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
					// SAVAS selection handle
					if (selectedObject instanceof AbstractDiagram) {
						AbstractDiagram selectedDiagram = (AbstractDiagram) selectedObject;
						setSelectedDiagram(selectedDiagram);
						setSelectedEntity(null);
						setSelectedStoredProcedure(null);
						setActivePage(getPageNum(EOModelEditor.EODIAGRAM_PAGE));
					} else {
						setSelectedDiagram(null);
					}
					
					if (selectedObject instanceof AbstractDiagramCollection) {
						AbstractDiagramCollection selectedDiagramCollection = (AbstractDiagramCollection) selectedObject;
						setSelectedDiagramCollection(selectedDiagramCollection);
						setActivePage(getPageNum(EOModelEditor.EODIAGRAMCOLLECTION_PAGE));
					} else {
						setSelectedDiagramCollection(null);
					}
					
					if (_updateOutline) {
						getContentOutlinePage().setSelection(selection);
					}
					fireSelectionChanged(selection);
					
					updateEntitiesTab(selectedObject);
					updatePartName();
				}
			}
		} finally {
			mySelectionDepth--;
		}
	}

	protected void setStoredProcedurePageVisible(final boolean _storedProcedurePageVisible) {
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

	/**
	 * Called when Entity Modeler should switch from Entity Modeler Perspective
	 * back to WOLips perspective.
	 */
	public void switchFromEntityModelerPerspective() {
		// MS: If "Open in New Window" is selected, then we want to watch for
		// the case where
		// you were in Entity Modeler and then close the editor, which also
		// close the new window,
		// so we don't leave you sitting in a blank window.
		boolean closedWindow = false;
		if (Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.OPEN_IN_WINDOW_KEY)) {
			IWorkbench workbench = Activator.getDefault().getWorkbench();
			if (workbench.getWorkbenchWindows().length > 1) {
				IWorkbenchPage workbenchPage = workbench.getActiveWorkbenchWindow().getActivePage();
				if (workbenchPage != null && EOModelerPerspectiveFactory.EOMODELER_PERSPECTIVE_ID.equals(workbenchPage.getPerspective().getId())) {
					IEditorReference[] editorReferences = workbenchPage.getEditorReferences();
					if (editorReferences.length == 0) {
						closedWindow = workbench.getActiveWorkbenchWindow().close();
					}
				}
			}
		}

		// MS: If the window didn't need to close, then we want to switch
		// perspectives on your current
		// window from Entity Modeler over to the WOLips perspective.
		if (!closedWindow && Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.CHANGE_PERSPECTIVES_KEY)) {
			try {
				IWorkbench workbench = Activator.getDefault().getWorkbench();
				IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
				if (activeWorkbenchWindow != null) {
					IWorkbenchPage workbenchPage = activeWorkbenchWindow.getActivePage();
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
							workbench.showPerspective("org.objectstyle.wolips.ui.Perspective", activeWorkbenchWindow);
						}
					}
				}
			} catch (WorkbenchException e) {
				// ErrorUtils.openErrorDialog(Display.getDefault().getActiveShell(),
				// e);
				Activator.getDefault().log(e);
			}
		}
	}

	/**
	 * Called when Entity Modeler should switch from whatever the current
	 * perspective is to Entity Modeler perspective.
	 */
	public void switchToEntityModelerPerspective() {
		try {
			if (Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.CHANGE_PERSPECTIVES_KEY)) {
				IWorkbench workbench = Activator.getDefault().getWorkbench();
				IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
				if (activeWorkbenchWindow != null) {
					workbench.showPerspective(EOModelerPerspectiveFactory.EOMODELER_PERSPECTIVE_ID, activeWorkbenchWindow);
				}
			}
		} catch (WorkbenchException e) {
			Activator.getDefault().log(e);
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
	
	// SAVAS Hier wird das gewählte Model dem EOmodeller und meinem Tab weitergegeben.
	private void updateEntitiesTab(Object selectedObject) {
		
		if (selectedObject instanceof EOModel) {
			myModel = (EOModel) selectedObject;
		} else if (selectedObject instanceof EOEntity) {
			myModel = ((EOEntity) selectedObject)._getModelParent();
		} else if (selectedObject instanceof EOAttribute) {
			myModel = ((EOAttribute) selectedObject).getEntity()._getModelParent();
		} else if (selectedObject instanceof EORelationship) {
			myModel = ((EORelationship) selectedObject).getEntity()._getModelParent();
		} else if (selectedObject instanceof EOFetchSpecification) {
			myModel = ((EOFetchSpecification) selectedObject).getEntity()._getModelParent();
		} else if (selectedObject instanceof AbstractEOAttributePath) {
			myModel = ((AbstractEOAttributePath) selectedObject).getChildIEOAttribute().getEntity()._getModelParent();
		} else if (selectedObject instanceof EODatabaseConfig) {
			myModel = ((EODatabaseConfig) selectedObject)._getModelParent();
		} else if (selectedObject instanceof EOERDiagramCollection) {
			myModel = ((EOERDiagramCollection) selectedObject)._getModelParent();
		} else if (selectedObject instanceof AbstractDiagram) {
			myModel = ((AbstractDiagram) selectedObject).getDiagramCollection().getModel();
			((AbstractDiagram) selectedObject).setEOModelEditor(this);
		}
		
		// Erneuert die Entitäten Tabele (wird gebraucht, wenn man mehrere Models hat)
		if (myEntitiesTableEditor != null) { 
			myEntitiesTableEditor.setModel(myModel);
		}
	}
	
	public void setSelectedDiagram(final AbstractDiagram _selectedDiagram) {
		if (!ComparisonUtils.equals(mySelectedDiagram, _selectedDiagram)) {
			mySelectedDiagram = _selectedDiagram;
			if (_selectedDiagram == null) {
				setDiagramPageVisible(false);
			} else {
				setDiagramPageVisible(true);
			}
			updatePartName();
		}
		if (_selectedDiagram != null) {
			setSelectedEntity(null);
		}
	}
	
	protected void setDiagramPageVisible(final boolean _setDiagramPageVisible) {
		try {
			if (_setDiagramPageVisible) {
				if (!myDiagramPageVisible) {
					addPage((IEditorPart) diagramTab, getEditorInput());
				}
				// Hier wird festgelegt welches Diagramm angezeigt werden soll.
				diagramTab.setSelectedDiagram(mySelectedDiagram);
				String diagramName = mySelectedDiagram.getName();
				if (diagramName == null) {
					diagramName = "?";
				}
				setPageText(getPageNum(EOModelEditor.EODIAGRAM_PAGE), diagramName);
			} else if (myDiagramPageVisible) {
				removePage(getPageNum(EOModelEditor.EODIAGRAM_PAGE));
			}
			myDiagramPageVisible = _setDiagramPageVisible;
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(), "Error creating editor.", null, e.getStatus());
		}
	}
	
	public void setSelectedDiagramCollection(final AbstractDiagramCollection _selectedDiagramCollection) {
		if (!ComparisonUtils.equals(mySelectedDiagramCollection, _selectedDiagramCollection)) {
			if (mySelectedDiagramCollection != null) {
				mySelectedDiagramCollection.removePropertyChangeListener(AbstractDiagramCollection.DIAGRAMS, myDiagramListener);
			}
			mySelectedDiagramCollection = _selectedDiagramCollection;
			if (mySelectedDiagramCollection != null) {
				mySelectedDiagramCollection.addPropertyChangeListener(AbstractDiagramCollection.DIAGRAMS, myDiagramListener);
			}
			if (_selectedDiagramCollection == null) {
				setDiagramCollectionPageVisible(false);
			} else {
				setDiagramCollectionPageVisible(true);
			}
			myDiagramCollectionEditor.setMyDiagramCollection(_selectedDiagramCollection);
			updatePartName();
		}
		if (_selectedDiagramCollection != null) {
			setSelectedEntity(null);
		}
	}
	
	protected void setDiagramCollectionPageVisible(final boolean _setDiagramCollectionPageVisible) {
		try {
			if (_setDiagramCollectionPageVisible) {
				if (!myDiagramCollectionPageVisible) {
					addPage(myDiagramCollectionEditor, getEditorInput());
				}
				String diagramCollectionName = mySelectedDiagramCollection.getName();
				if (diagramCollectionName == null) {
					diagramCollectionName = "?";
				}
				setPageText(getPageNum(EOModelEditor.EODIAGRAMCOLLECTION_PAGE), diagramCollectionName);
			} else if (myDiagramCollectionPageVisible) {
				removePage(getPageNum(EOModelEditor.EODIAGRAMCOLLECTION_PAGE));
			}
			myDiagramCollectionPageVisible = _setDiagramCollectionPageVisible;
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(), "Error creating editor.", null, e.getStatus());
		}
	}

	public AbstractDiagramCollection getMySelectedDiagramCollection() {
		return mySelectedDiagramCollection;
	}
}