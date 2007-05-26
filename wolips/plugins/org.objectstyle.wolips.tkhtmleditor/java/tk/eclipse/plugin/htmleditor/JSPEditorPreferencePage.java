package tk.eclipse.plugin.htmleditor;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Naoki Takezoe
 */
public class JSPEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	private ColorFieldEditor colorComment;
	private ColorFieldEditor colorString;
	private ColorFieldEditor colorKeyword;

	public JSPEditorPreferencePage() {
		super(GRID); 
		setPreferenceStore(HTMLPlugin.getDefault().getPreferenceStore());
	}

	public void init(IWorkbench workbench) {
	}
	
	@Override
  protected void createFieldEditors() {
		setTitle(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.JSP"));
		
		Composite parent = getFieldEditorParent();
		
		colorComment = new ColorFieldEditor(HTMLPlugin.PREF_JSP_COMMENT,
					HTMLPlugin.getResourceString("HTMLEditorPreferencePage.JSPCommentColor"),
					parent); 
		addField(colorComment);
		
		colorString = new ColorFieldEditor(HTMLPlugin.PREF_JSP_STRING,
					HTMLPlugin.getResourceString("HTMLEditorPreferencePage.JSPStringColor"),
					parent); 
		addField(colorString);
		
		colorKeyword = new ColorFieldEditor(HTMLPlugin.PREF_JSP_KEYWORD,
				HTMLPlugin.getResourceString("HTMLEditorPreferencePage.JSPKeywordColor"),
				parent); 
		addField(colorKeyword);
	}

}
