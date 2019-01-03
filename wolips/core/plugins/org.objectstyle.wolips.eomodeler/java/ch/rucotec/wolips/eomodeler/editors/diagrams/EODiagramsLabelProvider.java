package ch.rucotec.wolips.eomodeler.editors.diagrams;

import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.utils.TablePropertyLabelProvider;

import ch.rucotec.wolips.eomodeler.core.model.AbstractDiagram;

public class EODiagramsLabelProvider extends TablePropertyLabelProvider {

	public EODiagramsLabelProvider(String[] _columnProperties) {
		super(_columnProperties);
	}
	
	public String getColumnText1(Object _element, String _property) {
		EOEntity entity = (EOEntity) _element;
		String text = null;
		if (EOEntity.PARENT.equals(_property)) {
			EOEntity parent = entity.getParent();
			if (parent != null) {
				text = parent.getName();
			}
		} else {
			text = super.getColumnText(_element, _property);
		}
		return text;
	}
	
	public String getColumnText(Object _element, String _property) {
		AbstractDiagram diagram = (AbstractDiagram) _element;
		String text = null;
		text = super.getColumnText(_element, _property);
		return text;
	}
}
