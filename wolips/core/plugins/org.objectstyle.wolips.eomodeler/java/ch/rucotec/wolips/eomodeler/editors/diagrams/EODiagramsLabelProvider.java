package ch.rucotec.wolips.eomodeler.editors.diagrams;

import org.objectstyle.wolips.eomodeler.utils.TablePropertyLabelProvider;

import ch.rucotec.wolips.eomodeler.core.model.AbstractDiagram;

public class EODiagramsLabelProvider extends TablePropertyLabelProvider {

	public EODiagramsLabelProvider(String[] _columnProperties) {
		super(_columnProperties);
	}
	
	public String getColumnText(Object _element, String _property) {
		AbstractDiagram diagram = (AbstractDiagram) _element;
		String text = null;
		text = super.getColumnText(_element, _property);
		return text;
	}
}
