package tk.eclipse.plugin.xmleditor.editors;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import tk.eclipse.plugin.htmleditor.HTMLUtil;

/**
 * An implementation of SAX error handler to validate the XML document.
 * When error method is called, this handler creates a marker as an error.
 * 
 * @author takezoe
 */
public class XMLValidationHandler implements ErrorHandler {

	private IResource resource;
	
	public XMLValidationHandler(IResource resource) {
		this.resource = resource;
	}
	
	private void addMarker(int line,String message,int type){
		if(message.startsWith("src-") || message.startsWith("sch-")){
			return;
		}
		HTMLUtil.addMarker(resource, type, line, message);
	}
	
	public void error(SAXParseException exception) throws SAXException {
		int line = exception.getLineNumber();
		String message = exception.getMessage();
		addMarker(line,message,IMarker.SEVERITY_ERROR);
	}
	
	public void fatalError(SAXParseException exception) throws SAXException {
		int line = exception.getLineNumber();
		String message = exception.getMessage();
		addMarker(line,message,IMarker.SEVERITY_ERROR);
	}
	
	public void warning(SAXParseException exception) throws SAXException {
		int line = exception.getLineNumber();
		String message = exception.getMessage();
		addMarker(line,message,IMarker.SEVERITY_WARNING);
	}

}
