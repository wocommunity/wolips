package tk.eclipse.plugin.htmleditor;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * The preference page to configure appearance of the HTML/JSP/XML editor.
 * 
 * @author Naoki Takezoe
 */
public class HTMLEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	private ColorFieldEditor colorForeground;
	private SystemColorFieldEditor colorBackground;
	private ColorFieldEditor colorTag;
  private ColorFieldEditor colorWoTag;
  private ColorFieldEditor colorAttribute;
  private ColorFieldEditor colorOGNL;
  private ColorFieldEditor colorDynamic;
	private ColorFieldEditor colorComment;
	private ColorFieldEditor colorDoctype;
	private ColorFieldEditor colorString;
	private ColorFieldEditor colorScriptlet;
//	private ColorFieldEditor colorCssProperty;
	private UseSoftTabFieldEditor useSoftTab;
	private SoftTabWidthFieldEditor softTabWidth;
	private RadioGroupFieldEditor editorType;
	private BooleanFieldEditor highlightPair;
	private BooleanFieldEditor showXMLErrors;
	
	public HTMLEditorPreferencePage() {
		super(GRID); 
		setPreferenceStore(HTMLPlugin.getDefault().getPreferenceStore());
	}

	public void init(IWorkbench workbench) {
	}
	
	@Override
  protected void createFieldEditors() {
		setTitle(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.Appearance"));
		
		Composite parent = getFieldEditorParent();
		
		colorForeground = new ColorFieldEditor(HTMLPlugin.PREF_COLOR_FG,
				HTMLPlugin.getResourceString("HTMLEditorPreferencePage.ForegroundColor"),
				parent); 
		addField(colorForeground);

		colorBackground = new SystemColorFieldEditor(HTMLPlugin.PREF_COLOR_BG,HTMLPlugin.PREF_COLOR_BG_DEF,
				HTMLPlugin.getResourceString("HTMLEditorPreferencePage.BackgroundColor"),
				parent); 
		addField(colorBackground);
	
		colorTag = new ColorFieldEditor(HTMLPlugin.PREF_COLOR_TAG,
					HTMLPlugin.getResourceString("HTMLEditorPreferencePage.TagColor"),
					parent); 
		addField(colorTag);
	  
    colorWoTag = new ColorFieldEditor(HTMLPlugin.PREF_COLOR_WO_TAG,
          HTMLPlugin.getResourceString("HTMLEditorPreferencePage.WoTagColor"),
          parent); 
    addField(colorWoTag);
		
    colorAttribute = new ColorFieldEditor(HTMLPlugin.PREF_COLOR_ATTRIBUTE,
        HTMLPlugin.getResourceString("HTMLEditorPreferencePage.AttributeColor"),
        parent); 
    addField(colorAttribute);
    
    colorOGNL = new ColorFieldEditor(HTMLPlugin.PREF_COLOR_OGNL,
        HTMLPlugin.getResourceString("HTMLEditorPreferencePage.AttributeOGNLColor"),
        parent); 
    addField(colorOGNL);
    
    colorDynamic = new ColorFieldEditor(HTMLPlugin.PREF_COLOR_DYNAMIC,
        HTMLPlugin.getResourceString("HTMLEditorPreferencePage.AttributeDynamicColor"),
        parent); 
    addField(colorDynamic);
  
		colorComment = new ColorFieldEditor(HTMLPlugin.PREF_COLOR_COMMENT,
					HTMLPlugin.getResourceString("HTMLEditorPreferencePage.CommentColor"),
					parent); 
		addField(colorComment);
		
		colorDoctype = new ColorFieldEditor(HTMLPlugin.PREF_COLOR_DOCTYPE,
					HTMLPlugin.getResourceString("HTMLEditorPreferencePage.DocTypeColor"),
					parent); 
		addField(colorDoctype);
		
		colorString = new ColorFieldEditor(HTMLPlugin.PREF_COLOR_STRING,
					HTMLPlugin.getResourceString("HTMLEditorPreferencePage.StringColor"),
					parent); 
		addField(colorString);
		
		colorScriptlet = new ColorFieldEditor(HTMLPlugin.PREF_COLOR_SCRIPT,
					HTMLPlugin.getResourceString("HTMLEditorPreferencePage.ScriptColor"),
					parent); 
		addField(colorScriptlet);
		
//		colorCssProperty = new ColorFieldEditor(HTMLPlugin.PREF_COLOR_CSSPROP,
//					HTMLPlugin.getResourceString("HTMLEditorPreferencePage.CSSPropColor"),
//					parent); //$NON-NLS-1$
//		addField(colorCssProperty);
		
		highlightPair = new BooleanFieldEditor(HTMLPlugin.PREF_PAIR_CHAR,
				HTMLPlugin.getResourceString("HTMLEditorPreferencePage.PairCharacter"), parent);
		addField(highlightPair);
		
		showXMLErrors = new BooleanFieldEditor(HTMLPlugin.PREF_SHOW_XML_ERRORS,
				HTMLPlugin.getResourceString("HTMLEditorPreferencePage.ShowXMLErrors"), parent);
		addField(showXMLErrors);
		
		useSoftTab = new UseSoftTabFieldEditor(HTMLPlugin.PREF_USE_SOFTTAB,
					HTMLPlugin.getResourceString("HTMLEditorPreferencePage.UseSoftTab"),
					parent);
		addField(useSoftTab);
		
		softTabWidth = new SoftTabWidthFieldEditor(HTMLPlugin.PREF_SOFTTAB_WIDTH,
					HTMLPlugin.getResourceString("HTMLEditorPreferencePage.SoftTabWidth"),
					parent,4);
		softTabWidth.setEnabled(getPreferenceStore().getBoolean(HTMLPlugin.PREF_USE_SOFTTAB),parent);
		addField(softTabWidth);
		
		editorType = new RadioGroupFieldEditor(HTMLPlugin.PREF_EDITOR_TYPE,
					HTMLPlugin.getResourceString("HTMLEditorPreferencePage.EditorType"),1,
					new String[][]{
						{HTMLPlugin.getResourceString("HTMLEditorPreferencePage.EditorTab"),"tab"},
						{HTMLPlugin.getResourceString("HTMLEditorPreferencePage.EditorSplitHor"),"horizontal"},
						{HTMLPlugin.getResourceString("HTMLEditorPreferencePage.EditorSplitVer"),"vertical"},
						{HTMLPlugin.getResourceString("HTMLEditorPreferencePage.EditorNoPreview"),"noPreview"}
					},parent,true);
		addField(editorType);
	}
	
	/** Background Color Field Editor */
	private class SystemColorFieldEditor extends ColorFieldEditor {
		
		private String booleanName = null;
		private Button colorButton;
		private Button checkbox;
		
		public SystemColorFieldEditor(String colorName, String booleanName, String labelText, Composite parent){
			super(colorName,labelText,parent);
			this.booleanName = booleanName;
		}
		
		@Override
    protected void doFillIntoGrid(Composite parent, int numColumns) {
			Control control = getLabelControl(parent);
			GridData gd = new GridData();
			gd.horizontalSpan = numColumns - 1;
			control.setLayoutData(gd);
			
			Composite composite = new Composite(parent,SWT.NULL);
			GridLayout layout = new GridLayout(2,false);
			layout.horizontalSpacing = 5;
			layout.verticalSpacing = 0;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			composite.setLayout(layout);
			
			colorButton = getChangeControl(composite);
			gd = new GridData();
			int widthHint = convertHorizontalDLUsToPixels(colorButton, IDialogConstants.BUTTON_WIDTH);
			gd.widthHint = Math.max(widthHint, colorButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
			colorButton.setLayoutData(gd);
			
			checkbox = new Button(composite,SWT.CHECK);
			checkbox.setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.SystemDefault"));
			checkbox.addSelectionListener(new SelectionAdapter(){
				@Override
        public void widgetSelected(SelectionEvent evt){
					colorButton.setEnabled(!checkbox.getSelection());
				}
			});
		}
		
		@Override
    protected void doLoad() {
			super.doLoad();
			checkbox.setSelection(getPreferenceStore().getBoolean(booleanName));
			colorButton.setEnabled(!checkbox.getSelection());
		}
		
		@Override
    protected void doLoadDefault() {
			super.doLoadDefault();
			checkbox.setSelection(getPreferenceStore().getDefaultBoolean(booleanName));
			colorButton.setEnabled(!checkbox.getSelection());
		}

		@Override
    protected void doStore() {
			super.doStore();
			getPreferenceStore().setValue(booleanName,checkbox.getSelection());
		}
	}	
	
	/** Soft Tab Field Editor */
	private class UseSoftTabFieldEditor extends BooleanFieldEditor {
		
		private Composite parent;
		
		public UseSoftTabFieldEditor(String name, String label, Composite parent) {
			super(name, label, parent);
			this.parent = parent;
		}
		
		@Override
    protected void valueChanged(boolean oldValue, boolean newValue) {
			super.valueChanged(oldValue, newValue);
			softTabWidth.setEnabled(newValue,parent);
		}
	}
	
	/** Soft Tab Width Field Listener */
	private class SoftTabWidthFieldEditor extends IntegerFieldEditor {
		public SoftTabWidthFieldEditor(String name, String labelText,
				Composite parent, int textLimit) {
			super(name, labelText, parent, textLimit);
		}
		
		
		@Override
    protected void doFillIntoGrid(Composite parent, int numColumns) {
			super.doFillIntoGrid(parent, numColumns);
			GridData gd = (GridData)getTextControl().getLayoutData();
			gd.horizontalAlignment = 0;
			gd.widthHint = 40;
		}
	}
}
