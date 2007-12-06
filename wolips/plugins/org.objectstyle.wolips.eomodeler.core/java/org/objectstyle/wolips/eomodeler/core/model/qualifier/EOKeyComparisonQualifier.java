package org.objectstyle.wolips.eomodeler.core.model.qualifier;

public class EOKeyComparisonQualifier extends EOQualifier {
	private String _leftKey;

	private EOQualifier.Comparison _comparison;

	private String _rightKey;

	public EOKeyComparisonQualifier() {
		// DO NOTHING
	}

	public EOKeyComparisonQualifier(String leftKey, EOQualifier.Comparison comparison, String rightKey) {
		_leftKey = leftKey;
		_comparison = comparison;
		_rightKey = rightKey;
	}

	public String getLeftKey() {
		return _leftKey;
	}

	public EOQualifier.Comparison getComparison() {
		return _comparison;
	}

	public String getRightKey() {
		return _rightKey;
	}

	public String toString() {
		return _leftKey + " " + _comparison + " " + _rightKey;
	}
}
