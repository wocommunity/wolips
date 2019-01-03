package ch.rucotec.wolips.eomodeler.editors.diagrams;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.part.EditorPart;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;

import ch.rucotec.wolips.eomodeler.core.model.AbstractDiagramCollection;

public class EODiagramsTableEditor extends EditorPart implements ISelectionProvider {
	
	private EODiagramsTableViewer myDiagramsTableViewer;
	private AbstractDiagramCollection myDiagramCollection;
	private ListenerList myListenerList;
	
	public EODiagramsTableEditor() {
		myListenerList = new ListenerList();
	}

	public void setMyDiagramCollection(AbstractDiagramCollection myDiagramCollection) {
		this.myDiagramCollection = myDiagramCollection;
		updateDiagramsTableViewer();
	}

	public EOModel getModel() {
		return (myDiagramCollection == null) ? null : myDiagramCollection.getModel();
	}

	public AbstractDiagramCollection getMyDiagramCollection() {
		return myDiagramCollection;
	}
	
	public void doSave(IProgressMonitor _monitor) {
		// DO NOTHING
	}

	public void doSaveAs() {
		// DO NOTHING
	}

	public void init(IEditorSite _site, IEditorInput _input) {
		setSite(_site);
		setInput(_input);
		setMyDiagramCollection(null);
	}
	
	public boolean isDirty() {
		return myDiagramCollection != null && myDiagramCollection.getModel().isDirty();
	}
	
	public boolean isSaveAsAllowed() {
		return true;
	}
	
	public void createPartControl(Composite _parent) {
		myDiagramsTableViewer = new EODiagramsTableViewer(_parent, SWT.NONE);
		myDiagramsTableViewer.addSelectionChangedListener(new DiagramSelectionChangedListener());
		myDiagramsTableViewer.setLayoutData(new GridData(GridData.FILL_BOTH));
		updateDiagramsTableViewer();
	}

	public void setFocus() {
		// DO NOTHING
	}

	protected void updateDiagramsTableViewer() {
		if (myDiagramsTableViewer != null) {
			myDiagramsTableViewer.setDiagramCollection(myDiagramCollection);
		}
	}

	public ISelection getSelection() {
		return myDiagramsTableViewer.getSelection();
	}

	public void setSelection(ISelection _selection) {
		myDiagramsTableViewer.setSelection(_selection);
	}

	public void addSelectionChangedListener(ISelectionChangedListener _listener) {
		myListenerList.add(_listener);
	}

	public void removeSelectionChangedListener(ISelectionChangedListener _listener) {
		myListenerList.remove(_listener);
	}

	public void fireSelectionChanged(ISelection _selection) {
		Object[] listeners = myListenerList.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			((ISelectionChangedListener) listeners[i]).selectionChanged(new SelectionChangedEvent(this, _selection));
		}
	}

	protected class DiagramSelectionChangedListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent _event) {
			if (!_event.getSelection().isEmpty()) {
				fireSelectionChanged(_event.getSelection());
			}
		}
	}
}
