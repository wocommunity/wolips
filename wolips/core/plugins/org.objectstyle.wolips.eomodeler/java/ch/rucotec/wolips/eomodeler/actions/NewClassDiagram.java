package ch.rucotec.wolips.eomodeler.actions;

import java.util.Set;

import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.actions.AbstractNewObjectAction;
import org.objectstyle.wolips.eomodeler.core.model.EOModelException;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;

import ch.rucotec.wolips.eomodeler.core.model.EOClassDiagram;
import ch.rucotec.wolips.eomodeler.core.model.EOClassDiagramCollection;
import ch.rucotec.wolips.eomodeler.core.model.EOERDiagram;
import ch.rucotec.wolips.eomodeler.core.model.EOERDiagramCollection;

public class NewClassDiagram extends AbstractNewObjectAction<EOClassDiagramCollection, EOClassDiagram>{
	public NewClassDiagram() {
		super(EOClassDiagramCollection.class, "newClassDiagram");
	}

	@Override
	protected EOClassDiagram createChild(EOClassDiagramCollection parent, Set<EOModelVerificationFailure> failures) throws EOModelException {
		return parent.addBlankClassDiagram(Messages.getString("EOClassDiagram.newName"));
	}

	@Override
	protected String getNoSelectionMessage() {
		return Messages.getString("EOClassDiagram.noClassDiagramCollectionSelectedMessage");
	}

	@Override
	protected String getNoSelectionTitle() {
		return Messages.getString("EOClassDiagram.noClassDiagramCollectionSelectedTitle");
	}
}
