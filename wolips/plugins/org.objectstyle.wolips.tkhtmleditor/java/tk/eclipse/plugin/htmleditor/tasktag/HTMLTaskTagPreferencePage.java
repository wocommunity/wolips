package tk.eclipse.plugin.htmleditor.tasktag;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.objectstyle.wolips.preferences.TableViewerSupport;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.HTMLProjectBuilder;
import tk.eclipse.plugin.htmleditor.HTMLProjectNature;

/**
 * The preference page to add / edit / remove TaskTags.
 * 
 * @author Naoki Takezoe
 * @see tk.eclipse.plugin.htmleditor.tasktag.ITaskTagDetector
 * @see tk.eclipse.plugin.htmleditor.tasktag.TaskTag
 * @see tk.eclipse.plugin.htmleditor.HTMLProjectBuilder
 */
public class HTMLTaskTagPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	
	private TableViewer _viewer;
	private List<TaskTag> _model = new ArrayList<TaskTag>();
	private List<TaskTag> _oldModel = new ArrayList<TaskTag>();
	
	public HTMLTaskTagPreferencePage(){
		setPreferenceStore(HTMLPlugin.getDefault().getPreferenceStore());
		setTitle(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.TaskTag"));
	}
	
	@Override
  protected Control createContents(Composite parent) {
		TableViewerSupport support = new TableViewerSupport(_model, parent){

			@Override
      protected void initTableViewer(TableViewer viewer) {
				Table table = viewer.getTable();
				
				TableColumn col1 = new TableColumn(table,SWT.LEFT);
				col1.setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.Tag"));
				col1.setWidth(100);
				
				TableColumn col2 = new TableColumn(table,SWT.LEFT);
				col2.setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.Priority"));
				col2.setWidth(100);
			}

			@Override
      protected Object doAdd() {
				TaskTagDialog dialog = new TaskTagDialog(getShell());
				if(dialog.open()==Dialog.OK){
					return dialog.getTaskTag();
				}
				return null;
			}

			@Override
      protected void doEdit(Object obj) {
				TaskTag element = (TaskTag)obj;
				TaskTagDialog dialog = new TaskTagDialog(getShell(), element);
				if(dialog.open()==Dialog.OK){
					TaskTag newElement = dialog.getTaskTag();
					element.setTag(newElement.getTag());
					element.setPriority(newElement.getPriority());
				}
			}

			@Override
      protected ITableLabelProvider createLabelProvider() {
				return new ITableLabelProvider(){
				    
					public Image getColumnImage(Object element, int columnIndex){
				    	return null;
				    }
				    
				    public String getColumnText(Object element, int columnIndex){
						switch(columnIndex){
						case 0: return ((TaskTag)element).getTag();
						case 1: return ((TaskTag)element).getPriorityName();
					    default: return element.toString();
				    	}
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
				};
			}
			
		};
		
		_viewer = support.getTableViewer();
		_model.addAll(TaskTag.loadFromPreference(false));
		syncModels();
		_viewer.refresh();
		
		return support.getControl();
	}
	
	@Override
  protected void performDefaults() {
		_model.clear();
		_model.addAll(TaskTag.loadFromPreference(true));
		_viewer.refresh();
		processChange();
	}
	
	@Override
  public boolean performOk() {
		TaskTag.saveToPreference(_model);
		processChange();
		return true;
	}
	
	private void syncModels(){
		try {
			_oldModel.clear();
			for(int i=0;i<_model.size();i++){
				_oldModel.add(_model.get(i).clone());
			}
		} catch(Exception ex){
			HTMLPlugin.logException(ex);
		}
	}

	public void init(IWorkbench workbench) {
	}
	
	private void processChange(){
		if(TaskTag.hasChange(_oldModel, _model)){
			syncModels();
			try {
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				IProject[] projects = root.getProjects();
				for(int i=0;i<projects.length;i++){
					if(projects[i].hasNature(HTMLProjectNature.HTML_NATURE_ID)){
						HTMLProjectBuilder.doBuild(projects[i]);
					}
				}
			} catch(Exception ex){
				HTMLPlugin.logException(ex);
			}
		}
	}
	
	/**
	 * The dialog to add / edit TaskTags.
	 */
	private class TaskTagDialog extends Dialog {
		
		private Text textTag;
		private Combo comboPriority;
		private TaskTag element;
		
		public TaskTagDialog(Shell parentShell) {
			super(parentShell);
			setShellStyle(getShellStyle()|SWT.RESIZE);
		}
		
		public TaskTagDialog(Shell parentShell, TaskTag element) {
			super(parentShell);
			this.element = element;
		}
		
		@Override
    protected Point getInitialSize() {
			Point size = super.getInitialSize();
			size.x = 300;
			return size;
		}

		@Override
    protected Control createDialogArea(Composite parent) {
			getShell().setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.TaskTag"));
			
			Composite composite = new Composite(parent, SWT.NULL);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			composite.setLayout(new GridLayout(2,false));
			
			Label label = new Label(composite, SWT.NULL);
			label.setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.Dialog.Tag"));
			
			textTag = new Text(composite, SWT.BORDER);
			if(element!=null){
				textTag.setText(element.getTag());
			}
			textTag.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			label = new Label(composite, SWT.NULL);
			label.setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.Dialog.Priority"));
			
			comboPriority = new Combo(composite, SWT.READ_ONLY);
			comboPriority.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			for(int i=0; i<TaskTag.PRIORITIES.length; i++){
				comboPriority.add(TaskTag.PRIORITIES[i]);
			}
			if(element!=null){
				comboPriority.setText(element.getPriorityName());
			} else {
				comboPriority.setText(TaskTag.NORMAL);
			}
			
			return composite;
		}
		
		@Override
    protected void okPressed() {
			if(textTag.getText().length()==0){
				HTMLPlugin.openAlertDialog(HTMLPlugin.createMessage(
						HTMLPlugin.getResourceString("Error.Required"),
						new String[]{ HTMLPlugin.getResourceString("HTMLEditorPreferencePage.Tag") }));
				return;
			}
			
			element = new TaskTag(textTag.getText(), 
					TaskTag.convertPriority(comboPriority.getText()));
			
			super.okPressed();
		}
		
		public TaskTag getTaskTag(){
			return element;
		}
	}

}
