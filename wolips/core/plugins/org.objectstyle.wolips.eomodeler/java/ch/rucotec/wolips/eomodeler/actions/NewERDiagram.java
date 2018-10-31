package ch.rucotec.wolips.eomodeler.actions;

import java.util.Set;

import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.actions.AbstractNewObjectAction;
import org.objectstyle.wolips.eomodeler.core.model.EOModelException;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;

import ch.rucotec.wolips.eomodeler.core.model.EOERDiagram;
import ch.rucotec.wolips.eomodeler.core.model.EOERDiagramGroup;

public class NewERDiagram extends AbstractNewObjectAction<EOERDiagramGroup, EOERDiagram>{
	public NewERDiagram() {
		super(EOERDiagramGroup.class, "HI");
	}

	@Override
	protected EOERDiagram createChild(EOERDiagramGroup parent, Set<EOModelVerificationFailure> failures) throws EOModelException {
		return parent.addBlankERDiagram(Messages.getString("EOERDiagram.newName"));
	}

	@Override
	protected String getNoSelectionMessage() {
		return Messages.getString("EOERDiagram.noERDiagramGroupSelectedMessage");
	}

	@Override
	protected String getNoSelectionTitle() {
		return Messages.getString("EOERDiagram.noERDiagramGroupSelectedTitle");
	}
}
