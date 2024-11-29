package ch.rucotec.wolips.eomodeler.editors.diagrams;

import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ch.rucotec.wolips.eomodeler.core.model.AbstractDiagram;
import ch.rucotec.wolips.eomodeler.core.model.AbstractDiagramCollection;

public class EODiagramsContentProvider implements IStructuredContentProvider {

	public Object[] getElements(Object inputElement) {
		AbstractDiagramCollection diagramGroup = (AbstractDiagramCollection) inputElement;
		Set<AbstractDiagram> diagramList = diagramGroup.getDiagrams();
		AbstractDiagram[] diagrams = diagramList.toArray(new AbstractDiagram[diagramList.size()]);
		return diagrams;
	}
	
	public void dispose() {
		// DO NOTHING
	}

	public void inputChanged(Viewer _viewer, Object _oldInput, Object _newInput) {
		// DO NOTHING
	}

}
