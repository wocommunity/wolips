package ch.rucotec.wolips.eomodeler.editors.diagrams;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.objectstyle.wolips.baseforuiplugins.utils.WOTableViewer;
import org.objectstyle.wolips.baseforuiplugins.utils.WOTextCellEditor;
import org.objectstyle.wolips.eomodeler.core.model.EOArgument;
import org.objectstyle.wolips.eomodeler.editors.arguments.EOArgumentsLabelProvider;
import org.objectstyle.wolips.eomodeler.utils.StayEditingCellEditorListener;
import org.objectstyle.wolips.eomodeler.utils.TablePropertyViewerSorter;
import org.objectstyle.wolips.eomodeler.utils.TableRefreshPropertyListener;
import org.objectstyle.wolips.eomodeler.utils.TableRowRefreshPropertyListener;
import org.objectstyle.wolips.eomodeler.utils.TableUtils;

import ch.rucotec.wolips.eomodeler.core.model.AbstractDiagram;
import ch.rucotec.wolips.eomodeler.core.model.AbstractDiagramCollection;

public class EODiagramsTableViewer extends Composite implements ISelectionProvider {
	
	private WOTableViewer myDiagramsTableViewer;
	private AbstractDiagramCollection myDiagramCollection;
	private TableRefreshPropertyListener myDiagramsChangedRefresher;
	private TableRowRefreshPropertyListener myTableRowRefresher;


	public EODiagramsTableViewer(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout());
		myDiagramsTableViewer = TableUtils.createTableViewer(this, SWT.MULTI | SWT.FULL_SELECTION, "AbstractDiagram", AbstractDiagram.class.getName(), new EODiagramsContentProvider(), new EODiagramsLabelProvider(TableUtils.getColumnsForTableNamed(AbstractDiagram.class.getName())), new TablePropertyViewerSorter(AbstractDiagram.class.getName()));
		myDiagramsChangedRefresher = new TableRefreshPropertyListener("DiagramsChanged", myDiagramsTableViewer);
		myTableRowRefresher = new TableRowRefreshPropertyListener(myDiagramsTableViewer);
		Table diagramsTable = myDiagramsTableViewer.getTable();
		diagramsTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		TableUtils.sort(myDiagramsTableViewer, AbstractDiagram.NAME);
		
		CellEditor[] cellEditors = new CellEditor[TableUtils.getColumnsForTableNamed(AbstractDiagram.class.getName()).length];
		TableUtils.setCellEditor(AbstractDiagram.class.getName(), AbstractDiagram.NAME, new WOTextCellEditor(diagramsTable), cellEditors);
		myDiagramsTableViewer.setCellModifier(new EODiagramsCellModifier(myDiagramsTableViewer));
		myDiagramsTableViewer.setCellEditors(cellEditors);
		
		new StayEditingCellEditorListener(myDiagramsTableViewer, AbstractDiagram.class.getName(), AbstractDiagram.NAME);
	}
	
	public void setDiagramCollection(AbstractDiagramCollection diagramCollection) {
		if (myDiagramCollection != null) {
			myDiagramsChangedRefresher.stop();
			myDiagramCollection.removePropertyChangeListener(AbstractDiagramCollection.DIAGRAMS, myDiagramsChangedRefresher);
			myDiagramCollection.removePropertyChangeListener(AbstractDiagramCollection.DIAGRAM, myTableRowRefresher);
		}
		myDiagramCollection = diagramCollection;
		if (myDiagramCollection != null) {
			myDiagramsTableViewer.setInput(myDiagramCollection);
			TableUtils.packTableColumns(myDiagramsTableViewer);
			// Falls man will, dass der Selektion auch in der TableView angezeigt wird.
			// Hier einfach Kommentar togglen. 
			// (geht aber nur wenn bei der Selektion vom Diagram-Objekt nicht die GEF-Ansicht kommt; deshalb müsste man in der EOModelEditor die setSelection() Methode anpassen.)
			
//			myDiagramsChangedRefresher.start();
//			myDiagramCollection.addPropertyChangeListener(AbstractDiagramCollection.DIAGRAMS, myDiagramsChangedRefresher);
//			myDiagramCollection.addPropertyChangeListener(AbstractDiagramCollection.DIAGRAM, myTableRowRefresher);
		}
	}
	
	@Override
	public void dispose() {
		myDiagramsChangedRefresher.stop();
		super.dispose();
	}

	public AbstractDiagramCollection getMyDiagramCollection() {
		return myDiagramCollection;
	}
	
	public TableViewer getTableViewer() {
		return myDiagramsTableViewer;
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		myDiagramsTableViewer.addSelectionChangedListener(listener);
		
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		myDiagramsTableViewer.remove(listener);
		
	}
	
	public ISelection getSelection() {
		return myDiagramsTableViewer.getSelection();
	}

	public void setSelection(ISelection selection) {
		myDiagramsTableViewer.setSelection(selection);
		
	}

	public AbstractDiagramCollection getDiagramCollection() {
		return myDiagramCollection;
	}
	
	// SAVAS ich han kei ahnig ob ich da Doubleklick class bruche oder nöd?
}
