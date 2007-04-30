package tk.eclipse.plugin.htmleditor;

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.objectstyle.wolips.preferences.TableViewerSupport;

import tk.eclipse.plugin.htmleditor.assist.CustomAttribute;
import tk.eclipse.plugin.htmleditor.assist.CustomElement;

/**
 * @author Naoki Takezoe
 */
public class CustomAssistPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	
	private TableViewer attributeTableViewer;
	private TableViewer elementTableViewer;
	
	private List elementModel = new ArrayList();
	private List attributeModel = new ArrayList();
	
	public CustomAssistPreferencePage(){
		super("CustomAssist");
		setPreferenceStore(HTMLPlugin.getDefault().getPreferenceStore());
		setDescription(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.CustomAssist.Desc"));
	}
	
	public void init(IWorkbench workbench) {
	}

	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(1, false));
		
		TabFolder tabFolder = new TabFolder(composite, SWT.NULL);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		TabItem elementTab = new TabItem(tabFolder,SWT.NULL);
		elementTab.setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.CustomElements"));
		elementTab.setControl(createElementArea(tabFolder));
		
		TabItem attributeTab = new TabItem(tabFolder,SWT.NULL);
		attributeTab.setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.CustomAttributes"));
		attributeTab.setControl(createAttributeArea(tabFolder));
		
		// set initial values
		attributeModel.addAll(CustomAttribute.loadFromPreference(false));
		attributeTableViewer.refresh();
		elementModel.addAll(CustomElement.loadFromPreference(false));
		elementTableViewer.refresh();
		
		return composite;
	}
	
	private Control createElementArea(TabFolder tabFolder){
		TableViewerSupport support = new TableViewerSupport(elementModel, tabFolder){

			protected void initTableViewer(TableViewer viewer) {
				Table table = viewer.getTable();
				
				TableColumn col1 = new TableColumn(table, SWT.NULL);
				col1.setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.DisplayName"));
				col1.setWidth(100);
				
				TableColumn col2 = new TableColumn(table, SWT.NULL);
				col2.setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.AssistString"));
				col2.setWidth(200);
			}

			protected Object doAdd() {
				CustomElementDialog dialog = new CustomElementDialog(getShell());
				if(dialog.open()==Dialog.OK){
					return dialog.getCustomElement();
				}
				return null;
			}

			protected void doEdit(Object obj) {
				CustomElement element = (CustomElement)obj;
				CustomElementDialog dialog = new CustomElementDialog(getShell(), element);
				if(dialog.open()==Dialog.OK){
					CustomElement newElement = dialog.getCustomElement();
					element.setDisplayName(newElement.getDisplayName());
					element.setAssistString(newElement.getAssistString());
				}
			}

			protected ITableLabelProvider createLabelProvider() {
				return new CustomAssistLabelProvider();
			}
		};
		
		elementTableViewer = support.getTableViewer();
		return support.getControl();
	}
	
	private Control createAttributeArea(TabFolder tabFolder){
		TableViewerSupport support = new TableViewerSupport(attributeModel, tabFolder){

			protected void initTableViewer(TableViewer viewer) {
				Table table = viewer.getTable();
				
				TableColumn col1 = new TableColumn(table, SWT.NULL);
				col1.setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.TargetTag"));
				col1.setWidth(100);
				
				TableColumn col2 = new TableColumn(table, SWT.NULL);
				col2.setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.AttributeName"));
				col2.setWidth(200);
			}

			protected Object doAdd() {
				CustomAttributeDialog dialog = new CustomAttributeDialog(getShell());
				if(dialog.open()==Dialog.OK){
					return dialog.getCustomAttribute();
				}
				return null;
			}

			protected void doEdit(Object obj) {
				CustomAttribute attrInfo = (CustomAttribute)obj;
				CustomAttributeDialog dialog = new CustomAttributeDialog(getShell(), attrInfo);
				if(dialog.open()==Dialog.OK){
					CustomAttribute newAttrInfo = dialog.getCustomAttribute();
					attrInfo.setTargetTag(newAttrInfo.getTargetTag());
					attrInfo.setAttributeName(newAttrInfo.getAttributeName());
				}
			}

			protected ITableLabelProvider createLabelProvider() {
				return new CustomAssistLabelProvider();
			}
			
		};
		
		attributeTableViewer = support.getTableViewer();
		return support.getControl();
	}
	
	protected void performDefaults() {
		attributeModel.clear();
		attributeModel.addAll(CustomAttribute.loadFromPreference(true));
		attributeTableViewer.refresh();
		
		elementModel.clear();
		elementModel.addAll(CustomElement.loadFromPreference(true));
		elementTableViewer.refresh();
	}
	
	public boolean performOk() {
		CustomAttribute.saveToPreference(attributeModel);
		CustomElement.saveToPreference(elementModel);
		return true;
	}
	
	/**
	 * LabelProvider for TableViewers
	 */
	private class CustomAssistLabelProvider implements ITableLabelProvider {
		
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
		
		public String getColumnText(Object element, int columnIndex) {
			if(element instanceof CustomAttribute){
				CustomAttribute attr = (CustomAttribute)element;
				if(columnIndex==0){
					return attr.getTargetTag();
				} else if(columnIndex==1){
					return attr.getAttributeName();
				}
			} else if(element instanceof CustomElement){
				CustomElement elem = (CustomElement)element;
				if(columnIndex==0){
					return elem.getDisplayName();
				} else if(columnIndex==1){
					return elem.getAssistString();
				}
			}
			return null;
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
	}
	
	/**
	 * The dialog to add / edit the code completion proposal for elements.
	 */
	private class CustomElementDialog extends Dialog {
		
		private Text displayName;
		private Text assistString;
		private CustomElement element;
		
		public CustomElementDialog(Shell parentShell) {
			super(parentShell);
			setShellStyle(getShellStyle()|SWT.RESIZE);
		}
		
		public CustomElementDialog(Shell parentShell, CustomElement element) {
			super(parentShell);
			this.element = element;
		}
		
		protected Point getInitialSize() {
			Point size = super.getInitialSize();
			size.x = 300;
			return size;
		}

		protected Control createDialogArea(Composite parent) {
			getShell().setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.CustomElements"));
			
			Composite composite = new Composite(parent, SWT.NULL);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			composite.setLayout(new GridLayout(2,false));
			
			Label label = new Label(composite, SWT.NULL);
			label.setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.Dialog.DisplayName"));
			
			displayName = new Text(composite, SWT.BORDER);
			if(element!=null){
				displayName.setText(element.getDisplayName());
			}
			displayName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//			displayName.addModifyListener(new ModifyListener(){
//				public void modifyText(ModifyEvent e){
//					if(assistString.getText().equals("")){
//						assistString.setText("<" + displayName.getText() + "/>");
//					}
//				}
//			});
			
			label = new Label(composite, SWT.NULL);
			label.setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.Dialog.AssistString"));
			
			assistString = new Text(composite, SWT.BORDER);
			assistString.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			if(element!=null){
				assistString.setText(element.getAssistString());
			}
			
			return composite;
		}
		
		protected void okPressed() {
			if(displayName.getText().length()==0){
				HTMLPlugin.openAlertDialog(HTMLPlugin.createMessage(
						HTMLPlugin.getResourceString("Error.Required"),
						new String[]{ HTMLPlugin.getResourceString("HTMLEditorPreferencePage.DisplayName") }));
				return;
			}
			if(assistString.getText().length()==0){
				HTMLPlugin.openAlertDialog(HTMLPlugin.createMessage(
						HTMLPlugin.getResourceString("Error.Required"),
						new String[]{ HTMLPlugin.getResourceString("HTMLEditorPreferencePage.AssistString") }));
				return;
			}
			element = new CustomElement(displayName.getText(), assistString.getText());
			super.okPressed();
		}
		
		public CustomElement getCustomElement(){
			return element;
		}
	}
	
	/**
	 * The dialog to add / edit the code completion proposal for attributes.
	 */
	private class CustomAttributeDialog extends Dialog {
		
		private Text target;
		private Text name;
		private CustomAttribute attrInfo;
		
		public CustomAttributeDialog(Shell parentShell) {
			super(parentShell);
			setShellStyle(getShellStyle()|SWT.RESIZE);
		}
		
		public CustomAttributeDialog(Shell parentShell, CustomAttribute attrInfo) {
			super(parentShell);
			this.attrInfo = attrInfo;
		}
		
		protected Point getInitialSize() {
			Point size = super.getInitialSize();
			size.x = 300;
			return size;
		}
		
		protected Control createDialogArea(Composite parent) {
			getShell().setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.CustomAttributes"));
			
			Composite composite = new Composite(parent, SWT.NULL);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			composite.setLayout(new GridLayout(2,false));
			
			Label label = new Label(composite, SWT.NULL);
			label.setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.Dialog.TargetTag"));
			
			target = new Text(composite, SWT.BORDER);
			if(attrInfo!=null){
				target.setText(attrInfo.getTargetTag());
			} else {
				target.setText("*");
			}
			target.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			label = new Label(composite, SWT.NULL);
			label.setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.Dialog.AttributeName"));
			
			name = new Text(composite, SWT.BORDER);
			name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			if(attrInfo!=null){
				name.setText(attrInfo.getAttributeName());
			}
			
			return composite;
		}
		
		protected void okPressed() {
			if(target.getText().length()==0){
				HTMLPlugin.openAlertDialog(HTMLPlugin.createMessage(
						HTMLPlugin.getResourceString("Error.Required"),
						new String[]{ HTMLPlugin.getResourceString("HTMLEditorPreferencePage.TargetTag") }));
				return;
			}
			if(name.getText().length()==0){
				HTMLPlugin.openAlertDialog(HTMLPlugin.createMessage(
						HTMLPlugin.getResourceString("Error.Required"),
						new String[]{ HTMLPlugin.getResourceString("HTMLEditorPreferencePage.AttributeName") }));
				return;
			}
			attrInfo = new CustomAttribute(target.getText(), name.getText());
			super.okPressed();
		}
		
		public CustomAttribute getCustomAttribute(){
			return attrInfo;
		}
	}
	
}
