package org.objectstyle.wolips.eomodeler.core.model.qualifier;

public abstract class EOQualifier {
	public static class Comparison {
		private String _name;

		private String _displayName;

		public Comparison(String name) {
			this(name, name);
		}

		public Comparison(String name, String displayName) {
			_name = name;
			_displayName = displayName;
		}

		public String getName() {
			return _name;
		}

		public String getDisplayName() {
			return _displayName;
		}

		@Override
		public int hashCode() {
			return _name.hashCode();
		}

		public boolean equals(Object obj) {
			return obj instanceof EOQualifier.Comparison && ((EOQualifier.Comparison) obj)._name.equals(_name);
		}

		public String toString() {
			return _name;
		}
	}

	public String toString() {
		return toString(0);
	}

	public abstract String toString(int depth);
}
