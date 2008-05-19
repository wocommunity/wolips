package org.objectstyle.wolips.ruleeditor.listener;

import java.beans.*;

import org.objectstyle.wolips.ruleeditor.editor.*;
import org.objectstyle.wolips.ruleeditor.model.*;

/**
 * This class listen for changes in the {@link D2WModel} object being modified
 * by the RuleEditor.
 * 
 * @author <a href="mailto:hprange@moleque.com.br">Henrique Prange</a>
 */
public class D2WModelChangeListener implements PropertyChangeListener {

	private final RuleEditorPart editorPart;

	/**
	 * Default constructor. Change the state of a RuleEditorPart when some
	 * change happen in the D2WModel.
	 * 
	 * @param editorPart
	 *            The editorPart that modifies a D2WModel
	 */
	public D2WModelChangeListener(RuleEditorPart editorPart) {
		this.editorPart = editorPart;
	}

	/**
	 * Change the dirty property of the RuleEditorPart to <code>true</code> or
	 * <code>false</code> in accordance with the <code>D2WModel</code>
	 * state.
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		D2WModel model = (D2WModel) event.getSource();

		editorPart.setDirty(model.hasUnsavedChanges());
	}
}
