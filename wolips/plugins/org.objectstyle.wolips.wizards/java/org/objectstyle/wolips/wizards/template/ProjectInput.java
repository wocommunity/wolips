package org.objectstyle.wolips.wizards.template;

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
		String, Integer, Boolean
	}

	private String _name;

	private ProjectInput.Type _type;

	private String _question;

	private Object _defaultValue;

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
		if (_type == ProjectInput.Type.String) {
			_defaultValue = defaultText;
		} else if (_type == ProjectInput.Type.Integer) {
			_defaultValue = Integer.valueOf(defaultText);
		} else if (_type == ProjectInput.Type.Boolean) {
			_defaultValue = Boolean.valueOf(defaultText);
		} else {
			throw new IllegalArgumentException("Unknown value '" + defaultText + "' for type " + _type + ".");
		}
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

	public String toString() {
		return "[ProjectInput: name = " + _name + "; value = " + _value + "]";
	}
}
