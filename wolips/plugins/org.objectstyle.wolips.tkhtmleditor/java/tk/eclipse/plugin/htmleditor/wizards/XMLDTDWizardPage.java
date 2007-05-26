package tk.eclipse.plugin.htmleditor.wizards;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSNamedMap;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.xmleditor.editors.DTDResolver;
import tk.eclipse.plugin.xmleditor.editors.IDTDResolver;

import com.wutka.dtd.DTD;
import com.wutka.dtd.DTDElement;
import com.wutka.dtd.DTDParser;

public class XMLDTDWizardPage extends WizardPage {
	
	private Text   textPublicID;
	private Combo  comboSystemID;
	private Combo  comboDocumentRoot;
	private Combo  comboSchemaURI;
	private Button radioNone;
	private Button radioDTD;
	private Button radioXSD;
	private Button buttonLoadDTD;
	private XMLNewWizardPage page1;
	
	public XMLDTDWizardPage(String pageName, XMLNewWizardPage page1) {
		super(pageName);
		setTitle(HTMLPlugin.getResourceString("XMLDTDWizardPage.Title"));
		setDescription(HTMLPlugin.getResourceString("XMLDTDWizardPage.Description"));
		this.page1 = page1;
	}
	
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent,SWT.NULL);
		composite.setLayout(new GridLayout(2,false));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.GRAB_HORIZONTAL));
		
		Composite radios = new Composite(composite,SWT.NULL);
		radios.setLayout(new FillLayout(SWT.VERTICAL));
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		radios.setLayoutData(gd);
		radios.setLayoutData(gd);
		
		radioNone = new Button(radios,SWT.RADIO);
		radioNone.setText(HTMLPlugin.getResourceString("XMLDTDWizardPage.NoSchema"));
		radioNone.setSelection(true);
		radioNone.addSelectionListener(new SelectionAdapter(){
			@Override
      public void widgetSelected(SelectionEvent evt){
				if(radioNone.getSelection()){
					textPublicID.setEnabled(false);
					comboSystemID.setEnabled(false);
					comboSchemaURI.setEnabled(false);
					buttonLoadDTD.setEnabled(false);
					comboDocumentRoot.setEnabled(false);
				}
				setPageComplete(validatePage());
			}
		});
		
		radioDTD = new Button(radios,SWT.RADIO);
		radioDTD.setText(HTMLPlugin.getResourceString("XMLDTDWizardPage.UseDID"));
		radioDTD.addSelectionListener(new SelectionAdapter(){
			@Override
      public void widgetSelected(SelectionEvent evt){
				if(radioDTD.getSelection()){
					textPublicID.setEnabled(true);
					comboSystemID.setEnabled(true);
					comboSchemaURI.setEnabled(false);
					buttonLoadDTD.setEnabled(true);
					comboDocumentRoot.setEnabled(true);
				}
				setPageComplete(validatePage());
			}
		});
		
		radioXSD = new Button(radios,SWT.RADIO);
		radioXSD.setText(HTMLPlugin.getResourceString("XMLDTDWizardPage.UseXSD"));
		radioXSD.addSelectionListener(new SelectionAdapter(){
			@Override
      public void widgetSelected(SelectionEvent evt){
				if(radioXSD.getSelection()){
					textPublicID.setEnabled(false);
					comboSystemID.setEnabled(false);
					comboSchemaURI.setEnabled(true);
					buttonLoadDTD.setEnabled(true);
					comboDocumentRoot.setEnabled(true);
				}
				setPageComplete(validatePage());
			}
		});
		
		
		Label label = new Label(composite,SWT.NULL);
		label.setText(HTMLPlugin.getResourceString("XMLDTDWizardPage.PublicID"));
		
		textPublicID = new Text(composite,SWT.BORDER);
		textPublicID.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		textPublicID.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				setPageComplete(validatePage());
			}
		});
		
		label = new Label(composite,SWT.NULL);
		label.setText(HTMLPlugin.getResourceString("XMLDTDWizardPage.SystemID"));
		
		comboSystemID = new Combo(composite,SWT.DROP_DOWN);
		comboSystemID.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Load DTD / XSD Configuration from PreferenceStore
		IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();
		String[] uri  = store.getString(HTMLPlugin.PREF_DTD_URI).split("\n");
		for(int i=0;i<uri.length;i++){
			if(uri[i].endsWith(".dtd")){
				comboSystemID.add(uri[i]);
			}
		}
		comboSystemID.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				setPageComplete(validatePage());
			}
		});
		
		label = new Label(composite,SWT.NULL);
		label.setText(HTMLPlugin.getResourceString("XMLDTDWizardPage.XMLSchema"));
		comboSchemaURI = new Combo(composite,SWT.DROP_DOWN);
		comboSchemaURI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		for(int i=0;i<uri.length;i++){
			if(uri[i].endsWith(".xsd")){
				comboSchemaURI.add(uri[i]);
			}
		}
		comboSchemaURI.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				setPageComplete(validatePage());
			}
		});
		
		buttonLoadDTD = new Button(composite,SWT.PUSH);
		buttonLoadDTD.setText(HTMLPlugin.getResourceString("XMLDTDWizardPage.LoadDTD"));
		buttonLoadDTD.addSelectionListener(new SelectionAdapter(){
			@Override
      public void widgetSelected(SelectionEvent evt){
				try {
					if(getUseDTD()){
						// Load elements from DTD
						String systemID = comboSystemID.getText();
						comboDocumentRoot.removeAll();
						DTDResolver resolver = new DTDResolver(new IDTDResolver[0],
								page1.getFile().getLocation().makeAbsolute().toFile().getParentFile());
						InputStream in = resolver.getInputStream(systemID);
//						if(in==null){
//							URL url = new URL(systemID);
//							in = url.openStream();
//						}
						if(in!=null){
							Reader reader = new InputStreamReader(in);
							DTDParser parser = new DTDParser(reader);
							DTD dtd = parser.parse();
							Object[] obj = dtd.getItems();
							for(int i=0;i<obj.length;i++){
								if(obj[i] instanceof DTDElement){
									DTDElement element = (DTDElement)obj[i];
									String name = element.getName();
									comboDocumentRoot.add(name);
								}
							}
							comboDocumentRoot.select(0);
						}
					}
					if(getUseXSD()){
						// Load elements from XML Schema
						comboDocumentRoot.removeAll();
						DTDResolver resolver = new DTDResolver(new IDTDResolver[0],
								page1.getFile().getLocation().makeAbsolute().toFile().getParentFile());
						InputStream in = resolver.getInputStream(getSchemaURI());
						if(in!=null){
							SchemaGrammar grammer = (SchemaGrammar)new XMLSchemaLoader().loadGrammar(
									new XMLInputSource(null,null,null,in,null));
							XSNamedMap map = grammer.getComponents(XSConstants.ELEMENT_DECLARATION);
							for(int i=0;i<map.getLength();i++){
								XSElementDeclaration element = (XSElementDeclaration)map.item(i);
								comboDocumentRoot.add(element.getName());
							}
						}
					}
				} catch(Exception ex){
					HTMLPlugin.openAlertDialog(ex.toString());
				}
			}
		});
		
		label = new Label(composite,SWT.NULL);
		
		label = new Label(composite,SWT.NULL);
		label.setText(HTMLPlugin.getResourceString("XMLDTDWizardPage.DocumentRoot"));
		
		comboDocumentRoot = new Combo(composite,SWT.READ_ONLY);
		comboDocumentRoot.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				setPageComplete(validatePage());
			}
		});
		
		// Disable all widgets at first
		textPublicID.setEnabled(false);
		comboSystemID.setEnabled(false);
		comboSchemaURI.setEnabled(false);
		buttonLoadDTD.setEnabled(false);
		comboDocumentRoot.setEnabled(false);
		
		setErrorMessage(null);
		setMessage(null);
		setControl(composite);
	}
	
	public boolean getUseDTD(){
		return radioDTD.getSelection();
	}
	
	public boolean getUseXSD(){
		return radioXSD.getSelection();
	}
	
	public String getSystemID(){
		return comboSystemID.getText();
	}
	
	public String getPublicID(){
		return textPublicID.getText();
	}
	
	public String getSchemaURI(){
		return comboSchemaURI.getText();
	}
	
	public String getDocumentRoot(){
		return comboDocumentRoot.getText();
	}

	private boolean validatePage(){
		if(getUseDTD()){
			if(getPublicID().equals("")){
				setMessage(HTMLPlugin.createMessage(
						HTMLPlugin.getResourceString("Error.Required"),
						new String[]{HTMLPlugin.getResourceString("XMLDTDWizardPage.Message.PublicID")}),
						DialogPage.ERROR);
				return false;
			} else if(getSystemID().equals("")){
				setMessage(HTMLPlugin.createMessage(
						HTMLPlugin.getResourceString("Error.Required"),
						new String[]{HTMLPlugin.getResourceString("XMLDTDWizardPage.Message.SystemID")}),
						DialogPage.ERROR);
				return false;
			} else if(getDocumentRoot().equals("")){
				setMessage(HTMLPlugin.createMessage(
						HTMLPlugin.getResourceString("Error.Required"),
						new String[]{HTMLPlugin.getResourceString("XMLDTDWizardPage.Message.DocumentRoot")}),
						DialogPage.ERROR);
				return false;
			}
		}
		if(getUseXSD()){
			if(getSchemaURI().equals("")){
				setMessage(HTMLPlugin.createMessage(
						HTMLPlugin.getResourceString("Error.Required"),
						new String[]{HTMLPlugin.getResourceString("XMLDTDWizardPage.Message.XMLSchema")}),
						DialogPage.ERROR);
				return false;
			} else if(getDocumentRoot().equals("")){
				setMessage(HTMLPlugin.createMessage(
						HTMLPlugin.getResourceString("Error.Required"),
						new String[]{HTMLPlugin.getResourceString("XMLDTDWizardPage.Message.DocumentRoot")}),
						DialogPage.ERROR);
				return false;
			}
		}
		setMessage(null);
		return true;
	}
}
