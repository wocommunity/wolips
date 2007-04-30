package tk.eclipse.plugin.htmleditor;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import tk.eclipse.plugin.jspeditor.editors.ITLDLocator;

/**
 * The preference page to configure TLD settings.
 * 
 * @author Naoki Takezoe
 */
public class TLDPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	
	private Table table;
	private Button buttonAdd;
	private Button buttonEdit;
	private Button buttonRemove;
	
	public TLDPreferencePage() {
		super(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.TLD"));
		setPreferenceStore(HTMLPlugin.getDefault().getPreferenceStore());
		setDescription(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.LocalTLD"));
	}
	
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2,false));
		// create table
		table = new Table(composite,SWT.MULTI|SWT.FULL_SELECTION|SWT.BORDER|SWT.H_SCROLL|SWT.V_SCROLL);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent evt){
				TableItem[] items = table.getSelection();
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
		
		TableColumn col1 = new TableColumn(table,SWT.LEFT);
		col1.setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.Uri"));
		col1.setWidth(100);
		TableColumn col2 = new TableColumn(table,SWT.LEFT);
		col2.setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.LocalPath"));
		col2.setWidth(150);
		
		// create buttons
		Composite buttons = new Composite(composite,SWT.NONE);
		buttons.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttons.setLayout(layout);
		buttonAdd = new Button(buttons,SWT.PUSH);
		buttonAdd.setText(HTMLPlugin.getResourceString("Button.Add"));
		buttonAdd.setLayoutData(createButtonGridData());
		buttonAdd.addSelectionListener(
				new SelectionAdapter(){
					public void widgetSelected(SelectionEvent evt){
						TLDDialog dialog = new TLDDialog(getShell());
						if(dialog.open()==Dialog.OK){
							TableItem item = new TableItem(table,SWT.NONE);
							item.setText(new String[]{dialog.getUri(),dialog.getPath()});
						}
					}
				});
		buttonEdit = new Button(buttons,SWT.PUSH);
		buttonEdit.setText(HTMLPlugin.getResourceString("Button.Edit"));
		buttonEdit.setLayoutData(createButtonGridData());
		buttonEdit.setEnabled(false);
		buttonEdit.addSelectionListener(
				new SelectionAdapter(){
					public void widgetSelected(SelectionEvent evt){
						TableItem[] items = table.getSelection();
						if(items.length > 0){
							String uri  = items[0].getText(0);
							String path = items[0].getText(1);
							TLDDialog dialog = new TLDDialog(getShell(),uri,path);
							if(dialog.open()==Dialog.OK){
								items[0].setText(new String[]{dialog.getUri(),dialog.getPath()});
							}
						}
					}
				});
		buttonRemove = new Button(buttons,SWT.PUSH);
		buttonRemove.setText(HTMLPlugin.getResourceString("Button.Remove"));
		buttonRemove.setLayoutData(createButtonGridData());
		buttonRemove.setEnabled(false);
		buttonRemove.addSelectionListener(
				new SelectionAdapter(){
					public void widgetSelected(SelectionEvent evt){
						int[] indices = table.getSelectionIndices();
						table.remove(indices);
					}
				});
		
		// set initial values
		performDefaults();
		
		return composite;
	}
	
	public boolean performOk() {
		IPreferenceStore store = getPreferenceStore();
		TableItem[] items = table.getItems();
		StringBuffer uri  = new StringBuffer();
		StringBuffer path = new StringBuffer();
		for(int i=0;i<items.length;i++){
			if(!items[i].getText(1).startsWith("[Default]")){
				uri.append(items[i].getText(0)).append("\n");
				path.append(items[i].getText(1)).append("\n");
			}
		}
		store.setValue(HTMLPlugin.PREF_TLD_URI,uri.toString());
		store.setValue(HTMLPlugin.PREF_TLD_PATH,path.toString());
		
		return true;
	}
	
	protected void performDefaults() {
		IPreferenceStore store = getPreferenceStore();
		table.removeAll();
		
		Map innerTLD = HTMLPlugin.getInnerTLD();
		Iterator ite = innerTLD.keySet().iterator();
		while(ite.hasNext()){
			TableItem item = new TableItem(table,SWT.NONE);
			item.setText(new String[]{(String)ite.next(),"[Default]"});
		}
		
		String[] uri  = store.getString(HTMLPlugin.PREF_TLD_URI).split("\n");
		String[] path = store.getString(HTMLPlugin.PREF_TLD_PATH).split("\n");
		
		for(int i=0;i<uri.length;i++){
			if(!uri[i].trim().equals("") && !path[i].trim().equals("")){
				TableItem item = new TableItem(table,SWT.NONE);
				item.setText(new String[]{uri[i].trim(),path[i].trim()});
			}
		}
		
		ITLDLocator[] contribs = HTMLPlugin.getDefault().getTLDLocatorContributions();
		for ( int i = 0; i < contribs.length; i++) {
			String cURI = contribs[i].getURI();
			String cPath = contribs[i].getPath();
			if(!cURI.trim().equals("") && !cPath.trim().equals("")){
				TableItem item = new TableItem(table,SWT.NONE);
				item.setText(new String[]{cURI.trim(),"[Default]"+cPath.trim()});
			}
			
		}
	}
	
	/**
	 * Creates LayoutData for buttons.
	 * @return GridData
	 */
	private static GridData createButtonGridData(){
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 100;
		return gd;
	}
	
	public void init(IWorkbench workbench) {
	}
	
	/**
	 * A dialog to add or edit URI and local path of TLD.
	 */
	private class TLDDialog extends AbstractValidationDialog {
		
		private Text textUri;
		private Text textPath;
		private String uri  = "";
		private String path = "";
		
		public TLDDialog(Shell parentShell) {
			super(parentShell);
		}
		
		public TLDDialog(Shell parentShell,String uri,String path) {
			super(parentShell);
			this.uri  = uri;
			this.path = path;
		}
		
		protected void constrainShellSize() {
			Shell shell = getShell();
			shell.pack();
			shell.setSize(400,shell.getSize().y);
		}
		
		protected Control createDialogArea(Composite parent) {
			getShell().setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.TLD"));
			
			Composite container = new Composite(parent,SWT.NULL);
			container.setLayoutData(new GridData(GridData.FILL_BOTH));
			container.setLayout(new GridLayout(3,false));
			
			Label label = new Label(container,SWT.NULL);
			label.setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.Dialog.Uri"));
			
			textUri = new Text(container,SWT.BORDER);
			textUri.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			textUri.setText(uri);
			
			// fill GridLayout
			label = new Label(container,SWT.NONE);
			
			label = new Label(container,SWT.NULL);
			label.setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.Dialog.LocalPath"));
			
			textPath = new Text(container,SWT.BORDER);
			textPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			textPath.setText(path);
			
			Button button = new Button(container,SWT.PUSH);
			button.setText("...");
			button.addSelectionListener(
					new SelectionAdapter(){
						public void widgetSelected(SelectionEvent evt){
							FileDialog openDialog = new FileDialog(getShell(),SWT.OPEN);
							String openFile = openDialog.open();
							if(openFile!=null){
								textPath.setText(openFile);
							}
						}
					});
			
			add(textUri);
			add(textPath);
			
			return container;
		}
		
		protected void validate(){
			if(textUri.getText().equals("")){
				setErrorMessage(HTMLPlugin.createMessage(
					HTMLPlugin.getResourceString("Error.Required"),
					new String[]{ HTMLPlugin.getResourceString("HTMLEditorPreferencePage.Uri") }
				));
				return;
			} else if(textPath.getText().equals("")){
				setErrorMessage(HTMLPlugin.createMessage(
					HTMLPlugin.getResourceString("Error.Required"),
					new String[]{ HTMLPlugin.getResourceString("HTMLEditorPreferencePage.LocalPath") }
				));
				return;
			}
			setErrorMessage(null);
		}
		
		protected void okPressed() {
			uri  = textUri.getText();
			path = textPath.getText();
			super.okPressed();
		}
		
		public String getUri(){
			return uri;
		}
		
		public String getPath(){
			return path;
		}
	}
}
