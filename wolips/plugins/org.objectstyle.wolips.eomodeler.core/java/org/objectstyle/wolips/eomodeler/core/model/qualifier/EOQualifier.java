package org.objectstyle.wolips.eomodeler.core.model.qualifier;


public abstract class EOQualifier {
	public static class Comparison {
		private String _name;

		public Comparison(String name) {
			_name = name;
		}

		public String getName() {
			return _name;
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
