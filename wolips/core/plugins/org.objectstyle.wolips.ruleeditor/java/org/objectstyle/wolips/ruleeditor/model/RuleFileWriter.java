package org.objectstyle.wolips.ruleeditor.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectstyle.woenvironment.plist.PropertyListParserException;
import org.objectstyle.woenvironment.plist.SimpleParserDataStructureFactory;
import org.objectstyle.woenvironment.plist.WOLPropertyListSerialization;
import org.objectstyle.woenvironment.plist.WOLPropertyListSerialization.EMMutableData;

public class RuleFileWriter {
	/**
	 * Saves property list to rule file. This differs from standard plist
	 * serialization in that rules are saved on a single line to reduce churn in
	 * git diffs.
	 * 
	 * @throws PropertyListParserException
	 * @throws IOException
	 */
	public static void propertyListToRuleFile(File f, Map<String, Collection<Map>> plist) throws PropertyListParserException, IOException {
		BufferedWriter out = null;
		try {
			String str = rulesStringFromPropertyList(plist);
			if (str != null) {
				try {
					Object existingPlistContent = WOLPropertyListSerialization.propertyListFromFile(f);
					Object newPlistContent = WOLPropertyListSerialization.propertyListFromString(str, new SimpleParserDataStructureFactory());
					if (existingPlistContent.equals(newPlistContent)) {
						return;
					}
				} catch (Exception e) {
					// in this case, just proceed to write it out
				}

				out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), Charset.forName("UTF-8")));
				out.write(str);
				if (str.length() > 0 && str.charAt(str.length() - 1) != '\n') {
					out.write('\n');
				}
			}
			// writeObject("", out, plist);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public static String rulesStringFromPropertyList(Map<String, Collection<Map>> plist) throws PropertyListParserException {
		if (plist == null) {
			return null;
		}
		StringBuffer buffer = new StringBuffer(128);
		_appendRuleMapToStringBuffer(plist, buffer, 0);
		return buffer.toString();
	}

	private static void _appendRuleMapToStringBuffer(Map<String, Collection<Map>> dictionary, StringBuffer buffer, int indentionLevel) throws PropertyListParserException {
		buffer.append('{');
		if (!dictionary.isEmpty()) {
			String key = "rules";
			buffer.append('\n');
			_appendIndentationToStringBuffer(buffer, indentionLevel + 1);
			_appendStringToStringBuffer((String) key, buffer, indentionLevel + 1);
			buffer.append(" = (");
			Collection<Map> rules = dictionary.get(key);
			if (rules.isEmpty()) {
				buffer.append('\n');
				_appendIndentationToStringBuffer(buffer, indentionLevel + 1);
				buffer.append(");\n");
			} else {
				Iterator<Map> iter = rules.iterator();
				for (int i = 0; iter.hasNext(); i++) {
					if (i > 0) {
						buffer.append(", ");
					}
					buffer.append('\n');
					_appendIndentationToStringBuffer(buffer, indentionLevel + 2);
					_appendDictionaryToStringBuffer(iter.next(), buffer, indentionLevel + 2);
				}
				buffer.append('\n');
				_appendIndentationToStringBuffer(buffer, indentionLevel + 1);
				buffer.append("); \n");
			}
		}
		buffer.append('}');
	}

	private static void _appendIndentationToStringBuffer(StringBuffer buffer, int indentionLevel) {
		for (int i = 0; i < indentionLevel; i++) {
			buffer.append("  ");
		}
	}

	private static void _appendObjectToStringBuffer(Object object, StringBuffer buffer, int indentionLevel) throws PropertyListParserException {
		if (object instanceof String) {
			_appendStringToStringBuffer((String) object, buffer, indentionLevel);
		} else if (object instanceof StringBuffer) {
			_appendStringToStringBuffer(((StringBuffer) object).toString(), buffer, indentionLevel);
		} else if (object instanceof EMMutableData) {
			_appendDataToStringBuffer((EMMutableData) object, buffer, indentionLevel);
		} else if (object instanceof List) {
			_appendCollectionToStringBuffer((List) object, buffer, indentionLevel);
		} else if (object instanceof Set) {
			_appendCollectionToStringBuffer((Set) object, buffer, indentionLevel);
		} else if (object instanceof Map) {
			_appendDictionaryToStringBuffer((Map) object, buffer, indentionLevel);
		} else if (object instanceof Boolean) {
			String value = ((Boolean) object).booleanValue() ? "true" : "false";
			_appendStringToStringBuffer(value, buffer, indentionLevel);
		} else {
			_appendStringToStringBuffer(object.toString(), buffer, indentionLevel);
		}
	}

	private static void _appendDictionaryToStringBuffer(Map dictionary, StringBuffer buffer, int indentionLevel) throws PropertyListParserException {
		buffer.append('{');
		if (!dictionary.isEmpty()) {
			Iterator keyEnumerator;
			Set keySet = dictionary.keySet();
			ArrayList arrayList = new ArrayList(keySet);
			try {
				Collections.sort(arrayList);
				keyEnumerator = arrayList.iterator();
			} catch (Exception e) {
				keyEnumerator = keySet.iterator();
			}
			while (keyEnumerator.hasNext()) {
				Object key = keyEnumerator.next();
				if (key == null) {
					throw new PropertyListParserException("Property list generation failed while attempting to write hashtable. Non-String key found in Hashtable. Property list dictionaries must have String's as keys.  The attempkey was '" + key + "'.");
				} else if (!(key instanceof String)) {
					key = key.toString();
				}
				Object value = dictionary.get(key);
				if (value != null) {
					_appendStringToStringBuffer((String) key, buffer, indentionLevel + 1);
					buffer.append(" = ");
					_appendObjectToStringBuffer(value, buffer, indentionLevel + 1);
					buffer.append("; ");
				}
			}
		}
		buffer.append('}');
	}

	private static void _appendCollectionToStringBuffer(Collection collection, StringBuffer buffer, int indentionLevel) throws PropertyListParserException {
		buffer.append('(');
		if (!collection.isEmpty()) {
			Iterator iter = collection.iterator();
			for (int i = 0; iter.hasNext(); i++) {
				Object obj = iter.next();
				if (obj != null) {
					if (i > 0) {
						buffer.append(", ");
					}
					_appendObjectToStringBuffer(obj, buffer, indentionLevel + 1);
				}
			}
		}
		buffer.append(')');
	}

	private static void _appendDataToStringBuffer(EMMutableData data, StringBuffer buffer, int indentionLevel) {
		buffer.append('<');
		byte bytes[] = data.bytes();
		for (int i = 0; i < bytes.length; i++) {
			byte dataByte = bytes[i];
			byte nibble1 = (byte) (dataByte & 15);
			dataByte >>= 4;
			byte nibble2 = (byte) (dataByte & 15);
			buffer.append(_hexDigitForNibble(nibble2));
			buffer.append(_hexDigitForNibble(nibble1));
		}
		buffer.append('>');
	}

	/**
	 * Returns a quoted String, with all the escapes preprocessed. May return an
	 * unquoted String if it contains no special characters. The rule for a
	 * non-special character is the following:
	 * 
	 * <pre>
	 *       c &gt;= 'a' &amp;&amp; c &lt;= 'z'
	 *       c &gt;= 'A' &amp;&amp; c &lt;= 'Z'
	 *       c &gt;= '0' &amp;&amp; c &lt;= '9'
	 *       c == '_'
	 *       c == '$'
	 *       c == ':'
	 *       c == '.'
	 *       c == '/'
	 * </pre>
	 */
	private static void _appendStringToStringBuffer(String string, StringBuffer buffer, int indentionLevel) {
		String escapedStr = escapeString(string);
		buffer.append('\"');
		buffer.append(escapedStr);
		buffer.append('\"');
	}

	/**
	 * Escapes all doublequotes and backslashes.
	 */
	private static String escapeString(String str) {
		char[] chars = str.toCharArray();
		int len = chars.length;
		StringBuffer buf = new StringBuffer(len + 3);

		for (int i = 0; i < chars.length; i++) {
			if (chars[i] < '\200') {
				if (chars[i] == '\n') {
					buf.append("\\n");
					continue;
				}
				if (chars[i] == '\r') {
					buf.append("\\r");
					continue;
				}
				if (chars[i] == '\t') {
					buf.append("\\t");
					continue;
				}
				if (chars[i] == '"') {
					buf.append("\\\"");
					continue;
				}
				if (chars[i] == '\\') {
					buf.append("\\\\");
					continue;
				}
				if (chars[i] == '\f') {
					buf.append("\\f");
					continue;
				}
				if (chars[i] == '\b') {
					buf.append("\\b");
					continue;
				}
				if (chars[i] == '\007') {
					buf.append("\\a");
					continue;
				}
				if (chars[i] == '\013') {
					buf.append("\\v");
				} else {
					buf.append(chars[i]);
				}
			} else {
				char character = chars[i];
				byte nibble1 = (byte) (character & 15);
				character >>= '\004';
				byte nibble2 = (byte) (character & 15);
				character >>= '\004';
				byte nibble3 = (byte) (character & 15);
				character >>= '\004';
				byte nibble4 = (byte) (character & 15);
				character >>= '\004';
				buf.append("\\U");
				buf.append(_hexDigitForNibble(nibble4));
				buf.append(_hexDigitForNibble(nibble3));
				buf.append(_hexDigitForNibble(nibble2));
				buf.append(_hexDigitForNibble(nibble1));
			}
		}

		return buf.toString();
	}

	private static final char _hexDigitForNibble(byte nibble) {
		char digit = '\0';
		if (nibble >= 0 && nibble <= 9) {
			digit = (char) (48 + (char) nibble);
		} else if (nibble >= 10 && nibble <= 15) {
			digit = (char) (97 + (char) (nibble - 10));
		}
		return digit;
	}

	public static void writeRuleFileTxt(List<Rule> rules, File ruleModel) throws IOException {
		File ruleTxt = new File(ruleModel.getParentFile(), ruleModel.getName() + ".txt");
		StringBuilder builder = new StringBuilder();
		builder.append("(\n");
		for (int i = 0; i < rules.size(); ++i) {
			Rule rule = rules.get(i);
			builder.append("    ")
				.append(rule.toString());
			if(i != rules.size() - 1 ) {
				builder.append(",");
			}
			builder.append("\n");
		}
		builder.append(")");
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ruleTxt), Charset.forName("UTF-8")));
			out.write(builder.toString());
		} finally {
			if(out != null) {
				out.close();
			}
		}
	}
}
