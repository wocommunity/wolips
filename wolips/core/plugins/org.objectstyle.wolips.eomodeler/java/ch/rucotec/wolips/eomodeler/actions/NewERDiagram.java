package ch.rucotec.wolips.eomodeler.actions;

import java.util.Set;

import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.actions.AbstractNewObjectAction;
import org.objectstyle.wolips.eomodeler.core.model.EOModelException;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;

import ch.rucotec.wolips.eomodeler.core.model.EOERDiagram;
import ch.rucotec.wolips.eomodeler.core.model.EOERDiagramCollection;

/**
 * This class is used in the plugin.xml->Extensions->org.eclipse.ui.popupMenus and is used
 * to create EOERDiagram objects by right clicking EOERDiagramCollection in the EOModeller
 * 
 * @author celik
 *
 */
public class NewERDiagram extends AbstractNewObjectAction<EOERDiagramCollection, EOERDiagram>{
	public NewERDiagram() {
		super(EOERDiagramCollection.class, "newERDiagram");
	}

	@Override
	protected EOERDiagram createChild(EOERDiagramCollection parent, Set<EOModelVerificationFailure> failures) throws EOModelException {
		return parent.addBlankERDiagram(Messages.getString("EOERDiagram.newName"));
	}

	@Override
	protected String getNoSelectionMessage() {
		return Messages.getString("EOERDiagram.noERDiagramCollectionSelectedMessage");
	}

	@Override
	protected String getNoSelectionTitle() {
		return Messages.getString("EOERDiagram.noERDiagramCollectionSelectedTitle");
	}
}
