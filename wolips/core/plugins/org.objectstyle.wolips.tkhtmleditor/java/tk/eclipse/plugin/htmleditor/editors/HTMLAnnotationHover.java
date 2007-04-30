package tk.eclipse.plugin.htmleditor.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

/**
 * @author Naoki Takezoe
 */
public class HTMLAnnotationHover implements IAnnotationHover {

	private IEditorPart editor;
	
	public HTMLAnnotationHover(IEditorPart editor) {
		this.editor = editor;
	}
	
	private IMarker[] getMarker() {
		try {
			IFileEditorInput input = (IFileEditorInput)editor.getEditorInput();
			IFile file = input.getFile();
			return file.findMarkers(IMarker.MARKER, true, IFile.DEPTH_ZERO);
		} catch (CoreException e) {
			return new IMarker[0];
		}
	}
	
	public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
		IMarker[] marker = getMarker();
		if(marker != null) {
		    StringBuffer buffer = new StringBuffer();
			for(int i = 0; i < marker.length; i++) {
				try {
					Integer integer = (Integer)marker[i].getAttribute(IMarker.LINE_NUMBER);
					if((integer != null) && (integer.intValue() == lineNumber + 1)) {
					    String message = (String)marker[i].getAttribute(IMarker.MESSAGE);
					    if(message!=null && message.length()!=0) {
						    if(buffer.length() > 0) {
						        buffer.append("\r\n");
						    }
							buffer.append(message);
					    }
					}
				} catch (CoreException e) {
				}
			}
			return buffer.toString();
		}
		return null;
	}

}
