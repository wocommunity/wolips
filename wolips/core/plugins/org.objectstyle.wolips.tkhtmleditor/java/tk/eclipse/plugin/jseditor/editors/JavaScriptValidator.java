package tk.eclipse.plugin.jseditor.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.HTMLProjectParams;
import tk.eclipse.plugin.htmleditor.HTMLUtil;

/**
 * The validator for JavaScriptEditor.
 * 
 * @see tk.eclipse.plugin.jseditor.editors.JavaScriptEditor
 * @author Naoki Takezoe
 */
public class JavaScriptValidator {
	
	private IFile file;
	
	public JavaScriptValidator(IFile file){
		this.file = file;
	}
	
	public void doValidate(){
		Context context = Context.enter();
		try {
			file.deleteMarkers(IMarker.PROBLEM,false,0);
			
			HTMLProjectParams params = new HTMLProjectParams(file.getProject());
			if(!params.getValidateJavaScript()){
				return;
			}
			
			context.setErrorReporter(new ErrorReporterImpl());
			context.initStandardObjects();
			
			context.compileString( 
					new String(HTMLUtil.readStream(file.getContents()), file.getCharset()), 
					file.getName(), 1, null);
			
		} catch(EvaluatorException ex){
			// ignore
		} catch(Exception ex){
			HTMLPlugin.logException(ex);
		} finally {
			Context.exit();
		}
	}
	
	private class ErrorReporterImpl implements ErrorReporter {

		public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
			HTMLUtil.addMarker(file, IMarker.SEVERITY_ERROR, line, message);
		}

		public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
			//addMarker(IMarker.SEVERITY_ERROR, line, message);
			return new EvaluatorException(message, sourceName, line, lineSource, lineOffset);
		}

		public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
			HTMLUtil.addMarker(file, IMarker.SEVERITY_WARNING, line, message);
		}
		
	}
}
