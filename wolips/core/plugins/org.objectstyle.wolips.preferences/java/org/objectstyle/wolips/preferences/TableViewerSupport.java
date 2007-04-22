package org.objectstyle.wolips.preferences;

import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;


public abstract class TableViewerSupport {
	
	private Composite control;
	private TableViewer viewer;
	private Button buttonAdd;
	private Button buttonEdit;
	private Button buttonRemove;
	private List model;
	
	public TableViewerSupport(List model, Composite parent){
		this.model = model;
		initComponents(parent);
	}
	
	public Control getControl(){
		return this.control;
	}
	
	private void initComponents(Composite parent){
		this.control = new Composite(parent, SWT.NONE);
		this.control.setLayout(new GridLayout(2, false));
		
		viewer = new TableViewer(control, SWT.MULTI|SWT.FULL_SELECTION|SWT.BORDER|SWT.H_SCROLL|SWT.V_SCROLL);
		Table table = viewer.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent evt){
				TableItem[] items = viewer.getTable().getSelection();
				boolean enable = false;
				if(items.length > 0){
					String path = items[0].getText(1);
					if(!path.equals("[Default]")){
						enable = true;
					}
				}
				buttonEdit.setEnabled(enable);
				buttonRemove.setEnabled(enable);
			}
		});
		
		initTableViewer(viewer);
		
		// create buttons
		Composite buttons = new Composite(control,SWT.NONE);
		buttons.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		GridLayout layout = new GridLayout();
		layout.marginBottom = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttons.setLayout(layout);
		buttonAdd = new Button(buttons,SWT.PUSH);
		buttonAdd.setText("Add");
		buttonAdd.setLayoutData(createButtonGridData());
		buttonAdd.addSelectionListener(
				new SelectionAdapter(){
					public void widgetSelected(SelectionEvent evt){
						Object obj = doAdd();
						if(obj!=null){
							model.add(obj);
							viewer.refresh();
						}
					}
				});
		buttonEdit = new Button(buttons,SWT.PUSH);
		buttonEdit.setText("Edit");
		buttonEdit.setLayoutData(createButtonGridData());
		buttonEdit.setEnabled(false);
		buttonEdit.addSelectionListener(
				new SelectionAdapter(){
					public void widgetSelected(SelectionEvent evt){
						IStructuredSelection sel = (IStructuredSelection)viewer.getSelection();
						doEdit(sel.getFirstElement());
						viewer.refresh();
					}
				});
		buttonRemove = new Button(buttons,SWT.PUSH);
		buttonRemove.setText("Remove");
		buttonRemove.setLayoutData(createButtonGridData());
		buttonRemove.setEnabled(false);
		buttonRemove.addSelectionListener(
				new SelectionAdapter(){
					public void widgetSelected(SelectionEvent evt){
						IStructuredSelection sel = (IStructuredSelection)viewer.getSelection();
						doRemove(sel.toList());
						viewer.refresh();
					}
				});
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event){
				IStructuredSelection sel = (IStructuredSelection)viewer.getSelection();
				if(sel==null || sel.getFirstElement()==null){
					buttonEdit.setEnabled(false);
					buttonRemove.setEnabled(false);
				} else {
					buttonEdit.setEnabled(true);
					buttonRemove.setEnabled(true);
				}
			}
		});
		
		viewer.setContentProvider(new ListContentProvider());
		viewer.setLabelProvider(createLabelProvider());
		viewer.setInput(model);
	}
	
	protected abstract void initTableViewer(TableViewer viewer);
	protected abstract Object doAdd();	
	protected abstract void doEdit(Object obj);
	protected void doRemove(List objList){
		model.removeAll(objList);
	}
	protected abstract ITableLabelProvider createLabelProvider();
	
	public Object getModel(){
		return this.model;
	}
	
	public TableViewer getTableViewer(){
		return this.viewer;
	}
	
	/**
	 * Create LayoutData for &quot;add&quot;, &quot;edit&quot; and &quot;remove&quot; buttons.
	 * @return GridData
	 */
	private static GridData createButtonGridData(){
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 100;
		return gd;
	}
	
	public static class ListContentProvider implements IStructuredContentProvider {
		List fContents;	

		public ListContentProvider() {
		}
		
		public Object[] getElements(Object input) {
			if (fContents != null && fContents == input)
				return fContents.toArray();
			return new Object[0];
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (newInput instanceof List) 
				fContents= (List)newInput;
			else
				fContents= null;
			// we use a fixed set.
		}

		public void dispose() {
		}
		
		public boolean isDeleted(Object o) {
			return fContents != null && !fContents.contains(o);
		}
	}
	
}
