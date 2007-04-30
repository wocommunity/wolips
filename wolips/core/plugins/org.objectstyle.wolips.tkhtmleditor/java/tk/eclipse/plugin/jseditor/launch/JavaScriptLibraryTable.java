package tk.eclipse.plugin.jseditor.launch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.objectstyle.wolips.preferences.TableViewerSupport;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;

/**
 * The table component to edit JavaScript libraries.
 * 
 * @author Naoki Takezoe
 */
public class JavaScriptLibraryTable {
	
	public static final String PREFIX = "entry:";
	
	private TableViewer tableViewer;
	private List tableModel = new ArrayList();
	
	private Composite composite;
	private Button add;
	private Button addExternal;
	private Button remove;
	private Button up;
	private Button down;
	
	/**
	 * The constructor.
	 * 
	 * @param parent the parent component
	 */
	public JavaScriptLibraryTable(final Composite parent){
		composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		// list
		tableViewer = new TableViewer(composite);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 250;
		tableViewer.getTable().setLayoutData(gd);
		tableViewer.getTable().addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent evt){
				updateButtons();
			}
		});
		tableViewer.setContentProvider(new TableViewerSupport.ListContentProvider());
		tableViewer.setLabelProvider(new ITableLabelProvider(){
			public Image getColumnImage(Object element, int columnIndex) {
				if(element instanceof File){
					return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_JAR_EXT);
				} else if(element instanceof IFile){
					return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_JAR);
				}
				return null;
			}
			public String getColumnText(Object element, int columnIndex) {
				if(element instanceof File){
					return ((File)element).getAbsolutePath();
				} else if(element instanceof IFile){
					return ((IFile)element).getFullPath().toString();
				}
				return element.toString();
			}
			public void addListener(ILabelProviderListener listener) {
			}
			public void dispose() {
			}
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}
			public void removeListener(ILabelProviderListener listener) {
			}
		});
		tableViewer.setInput(tableModel);
		
		// buttons
		Composite buttons = new Composite(composite, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttons.setLayout(layout);
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		
		add = new Button(buttons, SWT.PUSH);
		add.setText(HTMLPlugin.getResourceString("Button.Add"));
		add.setLayoutData(createButtonGridData());
		add.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent evt){
				IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
						parent.getShell(),
						new WorkbenchLabelProvider(), 
						new WorkbenchContentProvider());
				
				dialog.setTitle(HTMLPlugin.getResourceString("JavaScriptPropertyPage.ChooseJavaScript"));
				dialog.setMessage(HTMLPlugin.getResourceString("JavaScriptPropertyPage.ChooseJavaScript.Description"));
				dialog.setInput(wsroot);
				dialog.setValidator(new ISelectionStatusValidator(){
					private IStatus okStatus = new Status(Status.OK, HTMLPlugin.getDefault().getPluginId(), Status.OK, "", null);
					private IStatus ngStatus = new Status(Status.ERROR, HTMLPlugin.getDefault().getPluginId(), Status.ERROR, "", null);
					
					public IStatus validate(Object[] selection) {
						for(int i=0;i<selection.length;i++){
							if(!(selection[i] instanceof IFile)){
								return ngStatus;
							}
							if(!((IFile)selection[i]).getName().endsWith(".js")){
								return ngStatus;
							}
							if(tableModel.contains(selection[i])){
								return ngStatus;
							}
						}
						if(selection.length==0){
							return ngStatus;
						}
						return okStatus;
					}
				});
				if (dialog.open() == Dialog.OK) {
					Object[] results = dialog.getResult();
					for(int i=0;i<results.length;i++){
						tableModel.add((IFile)results[i]);
					}
					tableViewer.refresh();
					modelChanged();
				}
			}
		});
		
		addExternal = new Button(buttons, SWT.PUSH);
		addExternal.setText(HTMLPlugin.getResourceString("Button.AddExternal"));
		addExternal.setLayoutData(createButtonGridData());
		addExternal.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent evt){
				FileDialog dialog = new FileDialog(parent.getShell(), SWT.OPEN|SWT.MULTI);
				dialog.setFilterExtensions(new String[]{"*.js"});
				String result = dialog.open();
				if(result!=null){
					String dir = dialog.getFilterPath();
					String[] fileNames = dialog.getFileNames();
					for(int i=0;i<fileNames.length;i++){
						tableModel.add(new File(dir, fileNames[i]));
					}
					tableViewer.refresh();
					modelChanged();
				}
			}
		});
		
		remove = new Button(buttons, SWT.PUSH);
		remove.setText(HTMLPlugin.getResourceString("Button.Remove"));
		remove.setLayoutData(createButtonGridData());
		remove.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent evt){
				IStructuredSelection sel = (IStructuredSelection)tableViewer.getSelection();
				tableModel.removeAll(sel.toList());
				updateButtons();
				tableViewer.refresh();
				modelChanged();
			}
		});
		
		up = new Button(buttons, SWT.PUSH);
		up.setText(HTMLPlugin.getResourceString("Button.Up"));
		up.setLayoutData(createButtonGridData());
		up.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent evt){
				int index = tableViewer.getTable().getSelectionIndex();
				if(index > 0){
					tableModel.add(index, tableModel.remove(index - 1));
					tableViewer.refresh();
					modelChanged();
					updateButtons();
				}
			}
		});
		
		down = new Button(buttons, SWT.PUSH);
		down.setText(HTMLPlugin.getResourceString("Button.Down"));
		down.setLayoutData(createButtonGridData());
		down.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent evt){
				int index = tableViewer.getTable().getSelectionIndex();
				if(index < tableModel.size() - 1){
					tableModel.add(index, tableModel.remove(index + 1));
					tableViewer.refresh();
					modelChanged();
					updateButtons();
				}
			}
		});
		
		updateButtons();
	}
	
	/**
	 * Updates button status.
	 */
	protected void updateButtons(){
		remove.setEnabled(tableViewer.getTable().getSelectionCount() != 0);
		up.setEnabled(tableViewer.getTable().getSelectionCount() == 1 &&
				tableViewer.getTable().getSelectionIndex() > 0);
		down.setEnabled(tableViewer.getTable().getSelectionCount() == 1 &&
				tableViewer.getTable().getSelectionIndex() < tableModel.size() - 1);
	}
	
	/**
	 * This method would be invoked when the table model changed.
	 * <p>
	 * Please override this method at the subclass, if you want to
	 * do any processing by the table model modifications.
	 */
	protected void modelChanged(){
	}
	
	/**
	 * Returns the control.
	 * 
	 * @return the control
	 */
	public Control getControl(){
		return composite;
	}
	
	/**
	 * Returns the table model.
	 * 
	 * @return the table model
	 */
	public List getModel(){
		return tableModel;
	}
	
	/**
	 * Refreshes the <code>TableViewer</code>.
	 */
	public void refresh(){
		tableViewer.refresh();
	}
	
	/**
	 * Creates <code>GridData</code> for buttons.
	 * 
	 * @return the <code>GridData</code> which has a widthHint(=100)
	 */
	private static GridData createButtonGridData(){
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 100;
		return gd;
	}	
	
}
