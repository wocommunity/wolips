package org.objectstyle.wolips.wodclipse.wod.model;

import org.eclipse.jface.text.Position;

public class WodProblem {
	public static final String RELATED_TO_FILE_NAMES = "org.objectstyle.wolips.wodclipse.wod.RelatedToFileNames";

	private IWodModel myModel;

	private String myMessage;

	private Position myPosition;

	private boolean myWarning;

	private String[] myRelatedToFileNames;

	public WodProblem(IWodModel _model, String _message, Position _position, boolean _warning, String _relatedToFileNames) {
		this(_model, _message, _position, _warning, new String[] { _relatedToFileNames });
	}

	public WodProblem(IWodModel _model, String _message, Position _position, boolean _warning, String[] _relatedToFileNames) {
		myModel = _model;
		myMessage = _message;
		myPosition = _position;
		myWarning = _warning;
		myRelatedToFileNames = _relatedToFileNames;
	}

	public String getMessage() {
		return myMessage;
	}

	public IWodModel getModel() {
		return myModel;
	}

	public Position getPosition() {
		return myPosition;
	}

	public boolean isWarning() {
		return myWarning;
	}

	public String[] getRelatedToFileNames() {
		return myRelatedToFileNames;
	}

	public String toString() {
		return "[WodProblem: message = " + myMessage + "]";
	}
}
