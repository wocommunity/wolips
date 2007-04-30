package tk.eclipse.plugin.dtdeditor.editors;

import java.io.File;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.HTMLProjectParams;
import tk.eclipse.plugin.htmleditor.editors.HTMLSourceEditor;
import tk.eclipse.plugin.htmleditor.editors.IHTMLOutlinePage;
import tk.eclipse.plugin.xmleditor.editors.SchemaGenerator;

import com.wutka.dtd.DTDParseException;
import com.wutka.dtd.DTDParser;

/**
 * DTD editor.
 * 
 * @author Naoki Takezoe
 */
public class DTDEditor extends HTMLSourceEditor {
	
	public static final String ACTION_GEN_XSD = "_generate_xsd";
	
	public DTDEditor(){
		super(new DTDConfiguration(HTMLPlugin.getDefault().getColorProvider()));
		setAction(ACTION_GEN_XSD,new GenerateXSDAction());
	}
	
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		getPairMatcher().setDelimiter('<');
	}
	
	protected IDocumentProvider createDocumentProvider(IEditorInput input){
		if(input instanceof IFileEditorInput){
			return new DTDTextDocumentProvider();
		} else if(input instanceof IStorageEditorInput){
			return new DTDFileDocumentProvider();
		} else {
			return new DTDTextDocumentProvider();
		}
	}
	
	protected IHTMLOutlinePage createOutlinePage() {
		return new DTDOutlinePage(this);
	}
	
	protected void addContextMenuActions(IMenuManager menu){
		menu.add(new Separator(GROUP_HTML));
		addAction(menu,GROUP_HTML,ACTION_COMMENT);
		addAction(menu,GROUP_HTML,ACTION_GEN_XSD);
	}
	
	protected void doValidate() {
		try {
			ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					IFileEditorInput input = (IFileEditorInput)getEditorInput();
					IFile file = input.getFile();
					
					file.deleteMarkers(IMarker.PROBLEM,false,0);
					
					try {
						HTMLProjectParams params = new HTMLProjectParams(file.getProject());
						if(!params.getValidateDTD()){
							return;
						}
						
						new DTDParser(new StringReader(getHTMLSource())).parse();
					} catch(DTDParseException ex){
						DTDErrorInfo error = new DTDErrorInfo(ex);
						IMarker marker = file.createMarker(IMarker.PROBLEM);
						Map map = new HashMap();
						map.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_ERROR));
						map.put(IMarker.MESSAGE, error.getError());
						map.put(IMarker.LINE_NUMBER,new Integer(error.getLine()));
						marker.setAttributes(map);
					} catch(Exception ex){
						HTMLPlugin.logException(ex);
					}
				}
			},null);
		} catch(Exception ex){
			HTMLPlugin.logException(ex);
		}
	}
	
	private static class DTDErrorInfo {
		
		private Pattern pattern = Pattern.compile("At line ([0-9]+), column ([0-9]+): (.+)$");
		
		private int line;
		private int column;
		private String error;
		
		public DTDErrorInfo(DTDParseException ex){
			Matcher matcher = pattern.matcher(ex.toString());
			if(matcher.find()){
				line   = Integer.parseInt(matcher.group(1));
				column = Integer.parseInt(matcher.group(2));
				error  = matcher.group(3);
			}
		}
		
		public int getColumn() {
			return column;
		}

		public String getError() {
			return error;
		}

		public int getLine() {
			return line;
		}
	}
	
	/** The action to generate XML schema from DTD. */
	private class GenerateXSDAction extends Action {
		public GenerateXSDAction(){
			super(HTMLPlugin.getResourceString("XMLEditor.GenerateXSD"), 
					HTMLPlugin.getDefault().getImageRegistry().getDescriptor(HTMLPlugin.ICON_XSD));
		}
		public void run() {
			FileDialog dialog = new FileDialog(getViewer().getTextWidget().getShell(),SWT.SAVE);
			dialog.setFilterExtensions(new String[]{"*.xsd"});
			String file = dialog.open();
			if(file!=null){
				try {
					SchemaGenerator.generateXSDFromDTD(getFile(),new File(file));
				} catch(Exception ex){
					HTMLPlugin.openAlertDialog(ex.toString());
				}
			}
		}
	}	
}
