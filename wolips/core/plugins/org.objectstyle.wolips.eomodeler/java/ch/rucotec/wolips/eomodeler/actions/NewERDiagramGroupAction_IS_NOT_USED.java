package ch.rucotec.wolips.eomodeler.actions;

import java.util.Set;

import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.actions.AbstractNewObjectAction;
import org.objectstyle.wolips.eomodeler.core.model.EODatabaseConfig;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelException;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;

import ch.rucotec.wolips.eomodeler.core.model.EOERDiagramGroup;

public class NewERDiagramGroupAction_IS_NOT_USED extends AbstractNewObjectAction<EOModel, EOERDiagramGroup>{
	public NewERDiagramGroupAction_IS_NOT_USED() {
		super(EOModel.class, Messages.getString("EOERD.newName"));
	}
	
	@Override
	protected EOERDiagramGroup createChild(EOModel parent, Set<EOModelVerificationFailure> failures) throws EOModelException {
		return parent.addBlankERDiagramGroup(Messages.getString("EOERD.newName"));
	}

	@Override
	protected String getNoSelectionMessage() {
		return Messages.getString("EOEntity.noModelSelectedMessage");
	}

	@Override
	protected String getNoSelectionTitle() {
		return Messages.getString("EOEntity.noModelSelectedTitle");
	}
}
