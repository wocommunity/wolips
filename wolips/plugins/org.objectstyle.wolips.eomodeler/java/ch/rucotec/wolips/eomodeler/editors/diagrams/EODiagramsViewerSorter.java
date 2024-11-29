package ch.rucotec.wolips.eomodeler.editors.diagrams;

import org.objectstyle.wolips.eomodeler.utils.TablePropertyViewerSorter;

public class EODiagramsViewerSorter extends TablePropertyViewerSorter {

	public EODiagramsViewerSorter(String tableName) {
		super(tableName);
	}
	
	public Object getComparisonValue(Object _obj, String _property) {
		Object value = null;

		value = super.getComparisonValue(_obj, _property);
		return value;
	}
}
