package org.objectstyle.wolips.templateengine;

import java.util.LinkedList;
import java.util.List;

import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;

/**
 * ProjectInput represents a single input variable for a template definition.
 * 
 * @author mschrag
 */
public class ProjectInput {
	/**
	 * Type defines the constants of the data types that template variables
	 * support.
	 * 
	 * @author mschrag
	 */
	public static enum Type {
		String, Integer, Boolean, Package
	}

	private String _name;

	private ProjectInput.Type _type;

	private String _question;

	private Object _defaultValue;

	private List<ProjectInput.Option> _options;

	private Object _value;

	/**
	 * Constructs a new ProjectInput.
	 * 
	 * @param name
	 *            the name of the input
	 * @param type
	 *            the type of the input
	 */
	public ProjectInput(String name, ProjectInput.Type type) {
		_name = name;
		_type = type;
		_options = new LinkedList<Option>();
	}

	/**
	 * Sets the question that should be displayed to the user in the user
	 * interface for this input.
	 * 
	 * @param question
	 *            the question to display to the user
	 */
	public void setQuestion(String question) {
		_question = question;
	}

	/**
	 * Returns the question that should be displayed to the user in the user
	 * interface for this input. If not set, this will return the name of the
	 * input followed by a question mark.
	 * 
	 * @return the question to display to the user
	 */
	public String getQuestion() {
		String question = _question;
		if (question == null) {
			question = _name + "?";
		}
		return question;
	}

	/**
	 * Returns the name of this input.
	 * 
	 * @return the name of this input
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Returns the type of this input.
	 * 
	 * @return the type of this input
	 */
	public ProjectInput.Type getType() {
		return _type;
	}

	/**
	 * Returns the default value for this input.
	 * 
	 * @return the default value for this input
	 */
	public Object getDefaultValue() {
		return _defaultValue;
	}

	/**
	 * Sets the default value for this input.
	 * 
	 * @param defaultValue
	 *            the default value for this input
	 */
	public void setDefaultValue(Object defaultValue) {
		_defaultValue = defaultValue;
	}

	/**
	 * Sets the default value for this input as a string (which is then coerced
	 * into the right type).
	 * 
	 * @param defaultText
	 *            the default value for this input as a string
	 */
	public void setDefaultText(String defaultText) {
		_defaultValue = toObjectValue(defaultText);
	}

	protected Object toObjectValue(String text) {
		Object value;
		if (_type == ProjectInput.Type.String) {
			value = text;
		} else if (_type == ProjectInput.Type.Package) {
			value = text;
		} else if (_type == ProjectInput.Type.Integer) {
			value = Integer.valueOf(text);
		} else if (_type == ProjectInput.Type.Boolean) {
			value = Boolean.valueOf(text);
		} else {
			throw new IllegalArgumentException("Unknown value '" + text + "' for type " + _type + ".");
		}
		return value;
	}

	/**
	 * Sets the current value of this input (from the user interface).
	 * 
	 * @param value
	 *            the current value of this input
	 */
	public void setValue(Object value) {
		_value = value;
	}

	/**
	 * Returns the current value of this input or the default value if the value
	 * is null.
	 * 
	 * @return the current value of this input
	 */
	public Object getValue() {
		Object value = _value;
		if (value == null) {
			value = _defaultValue;
		}
		return value;
	}
	
	/**
	 * Adds an option to the options list.
	 * 
	 * @param name the name of the option
	 * @param textValue the text value of the option
	 */
	public void addOption(String name, String textValue) {
		_options.add(new ProjectInput.Option(name, toObjectValue(textValue)));
	}

	/**
	 * Sets the possible options for this input.
	 * 
	 * @param options
	 *            the options for this input
	 */
	public void setOptions(List<ProjectInput.Option> options) {
		_options = options;
	}

	/**
	 * Returns whether or not this input has multiple options.
	 * 
	 * @return whether or not this input has multiple options
	 */
	public boolean hasOptions() {
		return _options != null && _options.size() > 0; 
	}
	
	/**
	 * Returns the possible options for this input.
	 * 
	 * @return the possible options for this input
	 */
	public List<ProjectInput.Option> getOptions() {
		return _options;
	}

	/**
	 * Sets the currently selected option.
	 * 
	 * @param option
	 *            the currently selected option
	 */
	public void setSelectedOption(ProjectInput.Option option) {
		if (option == null) {
			_value = null;
		} else {
			_value = option.getValue();
		}
	}

	/**
	 * Returns the currently selected option.
	 * 
	 * @return the currently selected option
	 */
	public ProjectInput.Option getSelectedOption() {
		Option selectedOption = null;
		if (_options != null) {
			Object value = getValue();
			for (Option option : _options) {
				if (ComparisonUtils.equals(option.getValue(), value)) {
					selectedOption = option;
				}
			}
		}
		return selectedOption;
	}

	public String toString() {
		return "[ProjectInput: name = " + _name + "; value = " + _value + "]";
	}

	/**
	 * Option represents a single value from an enumerated type value.
	 * 
	 * @author mschrag
	 */
	public static class Option {
		private String _name;

		private Object _value;

		/**
		 * Construct an Option.
		 * 
		 * @param name
		 *            the name of this option
		 * @param value
		 *            the value of this option
		 */
		public Option(String name, Object value) {
			_name = name;
			_value = value;
		}

		/**
		 * Returns the name of this option.
		 * 
		 * @return the name of this option
		 */
		public String getName() {
			return _name;
		}

		/**
		 * Returns the value of this option.
		 * 
		 * @return the value of this option
		 */
		public Object getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return "[ProjectInput.Option: name = " + _name + "]";
		}
	}
}
