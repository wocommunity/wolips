package tk.eclipse.plugin.htmleditor;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Naoki Takezoe
 */
public class JavaScriptEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	private ColorFieldEditor colorComment;
	private ColorFieldEditor colorString;
	private ColorFieldEditor colorKeyword;

	public JavaScriptEditorPreferencePage() {
		super(GRID); 
		setPreferenceStore(HTMLPlugin.getDefault().getPreferenceStore());
	}

	public void init(IWorkbench workbench) {
	}
	
	@Override
  protected void createFieldEditors() {
		setTitle(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.JavaScript"));
		
		Composite parent = getFieldEditorParent();
		
		colorComment = new ColorFieldEditor(HTMLPlugin.PREF_COLOR_JSCOMMENT,
					HTMLPlugin.getResourceString("HTMLEditorPreferencePage.JavaScriptCommentColor"),
					parent); 
		addField(colorComment);
		
		colorString = new ColorFieldEditor(HTMLPlugin.PREF_COLOR_JSSTRING,
					HTMLPlugin.getResourceString("HTMLEditorPreferencePage.JavaScriptStringColor"),
					parent); 
		addField(colorString);
		
		colorKeyword = new ColorFieldEditor(HTMLPlugin.PREF_COLOR_JSKEYWORD,
				HTMLPlugin.getResourceString("HTMLEditorPreferencePage.JavaScriptKeywordColor"),
				parent); 
		addField(colorKeyword);
	}
	
}
