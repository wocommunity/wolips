package org.objectstyle.wolips.wodclipse.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;

public class WodAnnotationHover implements IAnnotationHover, ITextHover {
	private IAnnotationModel myAnnotationModel;

	public WodAnnotationHover(IAnnotationModel _annotationModel) {
		myAnnotationModel = _annotationModel;
	}

	public String getHoverInfo(ISourceViewer _sourceViewer, int _lineNumber) {
		String hoverInfo = null;
		List annotationsList = getAnnotationsForLine(_sourceViewer, _lineNumber);
		if (annotationsList != null) {
			List<String> messagesList = new ArrayList<String>();
			Iterator annotationsIter = annotationsList.iterator();
			while (annotationsIter.hasNext()) {
				Annotation annotation = (Annotation) annotationsIter.next();
				String message = annotation.getText();
				if (message != null) {
					message = message.trim();
					if (message.length() > 0) {
						messagesList.add(message);
					}
				}
			}
			if (messagesList.size() == 1) {
				hoverInfo = messagesList.get(0);
			} else if (messagesList.size() > 1) {
				hoverInfo = formatMessages(messagesList);
			}
		}
		return hoverInfo;
	}

	public String getHoverInfo(ITextViewer _textViewer, IRegion _hoverRegion) {
		Iterator annotationsIter = myAnnotationModel.getAnnotationIterator();
		while (annotationsIter.hasNext()) {
			Annotation annotation = (Annotation) annotationsIter.next();
			Position position = myAnnotationModel.getPosition(annotation);
			if (position.overlapsWith(_hoverRegion.getOffset(), _hoverRegion.getLength())) {
				String text = annotation.getText();
				if (text != null && text.trim().length() > 0) {
					return text;
				}
			}
		}
		return null;
	}

	public IRegion getHoverRegion(ITextViewer _textViewer, int _offset) {
		// TODO If this is too slow then we might return new Region(offset, 0)
		Iterator annotationsIter = myAnnotationModel.getAnnotationIterator();
		while (annotationsIter.hasNext()) {
			Annotation annotation = (Annotation) annotationsIter.next();
			Position position = myAnnotationModel.getPosition(annotation);
			if (position.overlapsWith(_offset, 0)) {
				String text = annotation.getText();
				if (text != null && text.trim().length() > 0) {
					return new Region(position.offset, position.length);
				}
			}
		}
		return null;
	}

	private String formatMessages(List _messages) {
		StringBuffer buffer = new StringBuffer();
		Iterator e = _messages.iterator();
		while (e.hasNext()) {
			buffer.append("- "); //$NON-NLS-1$
			buffer.append(e.next());
			buffer.append('\n');
		}
		return buffer.toString();
	}

	private List<Annotation> getAnnotationsForLine(ISourceViewer _viewer, int _line) {
		List<Annotation> annotationsList = new ArrayList<Annotation>();
		IDocument document = _viewer.getDocument();
		IAnnotationModel model = _viewer.getAnnotationModel();
		if (model != null) {
			Iterator annotationsIter = model.getAnnotationIterator();
			while (annotationsIter.hasNext()) {
				Annotation annotation = (Annotation) annotationsIter.next();
				Position position = model.getPosition(annotation);
				if (position != null) {
					try {
						int annotationLine = document.getLineOfOffset(position.getOffset());
						if (annotationLine == _line) {
							annotationsList.add(annotation);
						}
					} catch (BadLocationException e1) {
						// ignore
					}
				}
			}
		}
		return annotationsList;
	}

}
