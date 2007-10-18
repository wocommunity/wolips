package org.objectstyle.wolips.eomodeler.core.wocompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("all")
public class EMPropertyListSerialization {
	public static class _Utilities {
		public static final String ROOT = "root";

		private int _lineNumber;

		private int _startOfLineCharIndex;

		private static int _savedIndex;

		private static int _savedLineNumber;

		private static int _savedStartOfLineCharIndex;

		private static final int NSToPrecompUnicodeTable[] = { 160, 192, 193, 194, 195, 196, 197, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 217, 218, 219, 220, 221, 222, 181, 215, 247, 169, 161, 162, 163, 8260, 165, 402, 167, 164, 8217, 8220, 171, 8249, 8250, 64257, 64258, 174, 8211, 8224, 8225, 183, 166, 182, 8226, 8218, 8222, 8221, 187, 8230, 8240, 172, 191, 185, 715, 180, 710, 732, 175, 728, 729, 168, 178, 730, 184, 179, 733, 731, 711, 8212, 177, 188, 189, 190, 224, 225, 226, 227, 228, 229, 231, 232, 233, 234, 235, 236, 198, 237, 170, 238, 239, 240, 241, 321, 216, 338, 186, 242, 243, 244, 245, 246, 230, 249, 250, 251, 305, 252, 253, 322, 248, 339, 223, 254, 255, 65533, 65533 };

		private void _saveIndexes(int index, int line, int startOfLine) {
			_savedIndex = index;
			_savedLineNumber = line;
			_savedStartOfLineCharIndex = startOfLine;
		}

		private String _savedIndexesAsString() {
			return "line number: " + _savedLineNumber + ", column: " + (_savedIndex - _savedStartOfLineCharIndex);
		}

		public static String stringFromPropertyList(Object plist) {
			if (plist == null) {
				return null;
			}
			StringBuffer buffer = new StringBuffer(128);
			_appendObjectToStringBuffer(plist, buffer, 0);
			return buffer.toString();
		}

		public static Object propertyListWithContentsOfFile(String path, ParserDataStructureFactory factory) throws IOException {
			String string = EMPropertyListSerialization.stringFromFile(new File(path));
			return propertyListFromString(string, factory);
		}

		public static Object propertyListFromString(String string, ParserDataStructureFactory factory) {
			return (new _Utilities())._propertyListFromString(string, factory);
		}

		private Object _propertyListFromString(String string, ParserDataStructureFactory factory) {
			_lineNumber = _startOfLineCharIndex = 0;
			if (string == null) {
				return null;
			}
			char characters[] = string.toCharArray();
			Object objects[] = new Object[1];
			_lineNumber = 1;
			_startOfLineCharIndex = 0;
			objects[0] = null;
			int index = 0;
			index = _readObjectIntoObjectReference(characters, index, objects, EMPropertyListSerialization._Utilities.ROOT, factory);
			index = _skipWhitespaceAndComments(characters, index);
			if (index != -1) {
				throw new IllegalArgumentException("propertyListFromString parsed an object, but there's still more text in the string. A plist should contain only one top-level object. Line number: " + _lineNumber + ", column: " + (index - _startOfLineCharIndex) + ".");
			}
			return objects[0];
		}

		private static void _appendObjectToStringBuffer(Object object, StringBuffer buffer, int indentionLevel) {
			if (object instanceof String) {
				_appendStringToStringBuffer((String) object, buffer, indentionLevel);
			} else if (object instanceof StringBuffer) {
				_appendStringToStringBuffer(((StringBuffer) object).toString(), buffer, indentionLevel);
			} else if (object instanceof EMMutableData) {
				_appendDataToStringBuffer((EMMutableData) object, buffer, indentionLevel);
			} else if (object instanceof List) {
				_appendArrayToStringBuffer((List) object, buffer, indentionLevel);
			} else if (object instanceof Set) {
				_appendSetToStringBuffer((Set) object, buffer, indentionLevel);
			} else if (object instanceof Map) {
				_appendDictionaryToStringBuffer((Map) object, buffer, indentionLevel);
			} else if (object instanceof Boolean) {
				String value = ((Boolean) object).booleanValue() ? "true" : "false";
				_appendStringToStringBuffer(value, buffer, indentionLevel);
			} else {
				_appendStringToStringBuffer(object.toString(), buffer, indentionLevel);
			}
		}


		/**
		 * Escapes all doublequotes and backslashes.
		 */
		protected static String escapeString(String str) {
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
			boolean shouldQuote = false;

			// scan string for special chars,
			// if we have them, string must be quoted

			String noQuoteExtras = "_$:./";
			char[] chars = string.toCharArray();
			int len = chars.length;
			if (len == 0) {
				shouldQuote = true;
			}
			for (int i = 0; !shouldQuote && i < len; i++) {
				char c = chars[i];

				if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || noQuoteExtras.indexOf(c) >= 0) {
					continue;
				}
				shouldQuote = true;
			}

			String escapedStr = escapeString(string);
			if (shouldQuote) {
				buffer.append('\"');
				buffer.append(escapedStr);
				buffer.append('\"');
			}
			else {
				buffer.append(escapedStr);
			}
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

		private static void _appendArrayToStringBuffer(List array, StringBuffer buffer, int indentionLevel) {
			buffer.append('(');
			int count = array.size();
			if (count > 0) {
				for (int i = 0; i < count; i++) {
					if (i > 0) {
						buffer.append(',');
					}
					buffer.append('\n');
					_appendIndentationToStringBuffer(buffer, indentionLevel + 1);
					_appendObjectToStringBuffer(array.get(i), buffer, indentionLevel + 1);
				}
				buffer.append('\n');
				_appendIndentationToStringBuffer(buffer, indentionLevel);
			}
			buffer.append(')');
		}

		private static void _appendSetToStringBuffer(Set set, StringBuffer buffer, int indentionLevel) {
			buffer.append('(');
			int count = set.size();
			if (count > 0) {
				int i = 0;
				for (Object o : set) {
					if (i > 0) {
						buffer.append(',');
					}
					buffer.append('\n');
					_appendIndentationToStringBuffer(buffer, indentionLevel + 1);
					_appendObjectToStringBuffer(o, buffer, indentionLevel + 1);
					i++;
				}
				buffer.append('\n');
				_appendIndentationToStringBuffer(buffer, indentionLevel);
			}
			buffer.append(')');
		}

		private static void _appendDictionaryToStringBuffer(Map dictionary, StringBuffer buffer, int indentionLevel) {
			buffer.append('{');
			int count = dictionary.size();
			if (count > 0) {
				for (Iterator keyEnumerator = dictionary.keySet().iterator(); keyEnumerator.hasNext(); buffer.append(';')) {
					Object key = keyEnumerator.next();
					if (!(key instanceof String)) {
						throw new IllegalArgumentException("Property list generation failed while attempting to write hashtable. Non-String key found in Hashtable. Property list dictionaries must have String's as keys.");
					}
					buffer.append('\n');
					_appendIndentationToStringBuffer(buffer, indentionLevel + 1);
					_appendStringToStringBuffer((String) key, buffer, indentionLevel + 1);
					buffer.append(" = ");
					_appendObjectToStringBuffer(dictionary.get(key), buffer, indentionLevel + 1);
				}
				buffer.append('\n');
				_appendIndentationToStringBuffer(buffer, indentionLevel);
			}
			buffer.append('}');
		}

		private static void _appendIndentationToStringBuffer(StringBuffer buffer, int indentionLevel) {
			for (int i = 0; i < indentionLevel; i++) {
				buffer.append('\t');
			}
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

		private int _readObjectIntoObjectReference(char characters[], int index, Object objects[], String keypath, ParserDataStructureFactory factory) {
			index = _skipWhitespaceAndComments(characters, index);
			if (index == -1 || index >= characters.length) {
				objects[0] = null;
			} else if (characters[index] == '"') {
				StringBuffer buffer = new StringBuffer(64);
				index = _readQuotedStringIntoStringBuffer(characters, index, buffer);
				objects[0] = buffer.toString();
			} else if (characters[index] == '<') {
				EMMutableData data = new EMMutableData(_lengthOfData(characters, index));
				index = _readDataContentsIntoData(characters, index, data);
				objects[0] = data;
			} else if (characters[index] == '(') {
				Collection array = factory.createCollection(keypath);
				index = _readArrayContentsIntoArray(characters, index, array, keypath, factory);
				objects[0] = array;
			} else if (characters[index] == '{') {
				Map dictionary = factory.createMap(keypath);
				index = _readDictionaryContentsIntoDictionary(characters, index, dictionary, keypath, factory);
				objects[0] = dictionary;
			} else {
				StringBuffer buffer = new StringBuffer(64);
				index = _readUnquotedStringIntoStringBuffer(characters, index, buffer);
				objects[0] = buffer.toString();
			}
			return index < characters.length ? index : -1;
		}

		private int _readUnquotedStringIntoStringBuffer(char characters[], int index, StringBuffer buffer) {
			int originalIndex = index;
			buffer.setLength(0);
			for (; index < characters.length && (characters[index] >= 'a' && characters[index] <= 'z' || characters[index] >= 'A' && characters[index] <= 'Z' || characters[index] >= '0' && characters[index] <= '9' || characters[index] == '_' || characters[index] == '$' || characters[index] == ':' || characters[index] == '.' || characters[index] == '/'); index++) {
				// DO NOTHING
			}
			if (originalIndex < index) {
				buffer.append(characters, originalIndex, index - originalIndex);
			} else {
				throw new IllegalArgumentException("Property list parsing failed while attempting to read unquoted string. No allowable characters were found. At line number: " + _lineNumber + ", column: " + (index - _startOfLineCharIndex) + ".");
			}
			return index < characters.length ? index : -1;
		}

		private int _readQuotedStringIntoStringBuffer(char characters[], int index, StringBuffer buffer) {
			_saveIndexes(index, _lineNumber, _startOfLineCharIndex);
			int seqStart = ++index;
			while (index < characters.length && characters[index] != '"') {
				if (characters[index] == '\\') {
					if (seqStart < index) {
						buffer.append(characters, seqStart, index - seqStart);
					}
					if (++index >= characters.length) {
						throw new IllegalArgumentException("Property list parsing failed while attempting to read quoted string. Input exhausted before closing quote was found. Opening quote was at " + _savedIndexesAsString() + ".");
					}
					if (characters[index] == 'n') {
						buffer.append('\n');
						index++;
					} else if (characters[index] == 'r') {
						buffer.append('\r');
						index++;
					} else if (characters[index] == 't') {
						buffer.append('\t');
						index++;
					} else if (characters[index] == 'f') {
						buffer.append('\f');
						index++;
					} else if (characters[index] == 'b') {
						buffer.append('\b');
						index++;
					} else if (characters[index] == 'a') {
						buffer.append('\007');
						index++;
					} else if (characters[index] == 'v') {
						buffer.append('\013');
						index++;
					} else if (characters[index] == 'u' || characters[index] == 'U') {
						if (index + 4 >= characters.length) {
							throw new IllegalArgumentException("Property list parsing failed while attempting to read quoted string. Input exhausted before escape sequence was completed. Opening quote was at " + _savedIndexesAsString() + ".");
						}
						index++;
						if (!_isHexDigit(characters[index]) || !_isHexDigit(characters[index + 1]) || !_isHexDigit(characters[index + 2]) || !_isHexDigit(characters[index + 3])) {
							throw new IllegalArgumentException("Property list parsing failed while attempting to read quoted string. Improperly formed \\U type escape sequence. At line number: " + _lineNumber + ", column: " + (index - _startOfLineCharIndex) + ".");
						}
						byte nibble4 = _nibbleForHexDigit(characters[index]);
						byte nibble3 = _nibbleForHexDigit(characters[index + 1]);
						byte nibble2 = _nibbleForHexDigit(characters[index + 2]);
						byte nibble1 = _nibbleForHexDigit(characters[index + 3]);
						buffer.append((char) ((nibble4 << 12) + (nibble3 << 8) + (nibble2 << 4) + nibble1));
						index += 4;
					} else if (characters[index] >= '0' && characters[index] <= '7') {
						int temp = 0;
						int numberOfDigits = 1;
						int digits[] = new int[3];
						digits[0] = characters[index] - 48;
						for (index++; numberOfDigits < 3 && index < characters.length && characters[index] >= '0' && characters[index] <= '7'; index++) {
							digits[numberOfDigits++] = characters[index] - 48;
						}
						if (numberOfDigits == 3 && digits[0] > 3) {
							throw new IllegalArgumentException("Property list parsing failed while attempting to read quoted string. Octal escape sequence too large (bigger than octal 377). At line number: " + _lineNumber + ", column: " + (index - _startOfLineCharIndex) + ".");
						}
						for (int i = 0; i < numberOfDigits; i++) {
							temp *= 8;
							temp += digits[i];
						}
						buffer.append(_nsToUnicode(temp));
					} else {
						buffer.append(characters[index]);
						if (characters[index] == '\n') {
							_lineNumber++;
							_startOfLineCharIndex = index + 1;
						}
						index++;
					}
					seqStart = index;
				} else {
					if (characters[index] == '\n') {
						_lineNumber++;
						_startOfLineCharIndex = index + 1;
					}
					index++;
				}
			}
			if (seqStart < index) {
				buffer.append(characters, seqStart, index - seqStart);
			}
			if (index >= characters.length) {
				throw new IllegalArgumentException("Property list parsing failed while attempting to read quoted string. Input exhausted before closing quote was found. Opening quote was at " + _savedIndexesAsString() + ".");
			}
			return ++index < characters.length ? index : -1;
		}

		private int _lengthOfData(char characters[], int index) {
			int numberOfNibbles = 0;
			boolean isHex = false;
			for (index++; index < characters.length && ((isHex = _isHexDigit(characters[index])) || _isWhitespace(characters[index])); index++) {
				if (isHex) {
					numberOfNibbles++;
				}
			}
			if (index >= characters.length) {
				throw new IllegalArgumentException("Property list parsing failed while attempting to read data. Input exhausted before data was terminated with '>'. At line number: " + _lineNumber + ", column: " + (index - _startOfLineCharIndex) + ".");
			}
			if (characters[index] != '>') {
				throw new IllegalArgumentException("Property list parsing failed while attempting to read data. Illegal character encountered in data: '" + characters[index] + "'. At line number: " + _lineNumber + ", column: " + (index - _startOfLineCharIndex) + ".");
			}
			if (numberOfNibbles % 2 != 0) {
				throw new IllegalArgumentException("Property list parsing failed while attempting to read data. An odd number of half-bytes were specified. At line number: " + _lineNumber + ", column: " + (index - _startOfLineCharIndex) + ".");
			}
			return numberOfNibbles / 2;
		}

		private int _readDataContentsIntoData(char characters[], int index, EMMutableData data) {
			index++;
			do {
				if (characters[index] == '>') {
					break;
				}
				index = _skipWhitespaceAndComments(characters, index);
				if (characters[index] == '>') {
					break;
				}
				byte nibble2 = _nibbleForHexDigit(characters[index]);
				index++;
				index = _skipWhitespaceAndComments(characters, index);
				byte nibble1 = _nibbleForHexDigit(characters[index]);
				index++;
				data.appendByte((byte) ((nibble2 << 4) + nibble1));
			} while (true);
			return ++index < characters.length ? index : -1;
		}

		private int _readArrayContentsIntoArray(char characters[], int index, Collection array, String keypath, ParserDataStructureFactory factory) {
			Object objects[] = new Object[1];
			index++;
			array.clear();
			index = _skipWhitespaceAndComments(characters, index);
			do {
				if (index == -1 || characters[index] == ')') {
					break;
				}
				if (array.size() > 0) {
					if (characters[index] != ',') {
						throw new IllegalArgumentException("Property list parsing failed while attempting to read array. No comma found between array elements. At line number: " + _lineNumber + ", column: " + (index - _startOfLineCharIndex) + ".");
					}
					index++;
					index = _skipWhitespaceAndComments(characters, index);
					if (index == -1) {
						throw new IllegalArgumentException("Property list parsing failed while attempting to read array. Input exhausted before end of array was found. At line number: " + _lineNumber + ", column: " + (index - _startOfLineCharIndex) + ".");
					}
				}
				if (characters[index] != ')') {
					objects[0] = null;
					index = _readObjectIntoObjectReference(characters, index, objects, keypath, factory);
					if (objects[0] == null) {
						throw new IllegalArgumentException("Property list parsing failed while attempting to read array. Failed to read content object. At line number: " + _lineNumber + ", column: " + (index - _startOfLineCharIndex) + ".");
					}
					index = _skipWhitespaceAndComments(characters, index);
					array.add(objects[0]);
				}
			} while (true);
			if (index == -1) {
				throw new IllegalArgumentException("Property list parsing failed while attempting to read array. Input exhausted before end of array was found. At line number: " + _lineNumber + ", column: " + (index - _startOfLineCharIndex) + ".");
			}
			return ++index < characters.length ? index : -1;
		}

		private int _readDictionaryContentsIntoDictionary(char characters[], int index, Map dictionary, String keypath, ParserDataStructureFactory factory) {
			Object keys[] = new Object[1];
			Object values[] = new Object[1];
			index++;
			if (dictionary.size() != 0) {
				for (Iterator keyEnumerator = dictionary.keySet().iterator(); keyEnumerator.hasNext(); dictionary.remove(keyEnumerator.next())) {
					// DO NOTHING
				}
			}
			for (index = _skipWhitespaceAndComments(characters, index); index != -1 && characters[index] != '}';) {
				index = _readObjectIntoObjectReference(characters, index, keys, keypath, factory);
				if (keys[0] == null || !(keys[0] instanceof String)) {
					throw new IllegalArgumentException("Property list parsing failed while attempting to read dictionary. Failed to read key or key is not a String. At line number: " + _lineNumber + ", column: " + (index - _startOfLineCharIndex) + ".");
				}
				index = _skipWhitespaceAndComments(characters, index);
				if (index == -1 || characters[index] != '=') {
					throw new IllegalArgumentException("Property list parsing failed while attempting to read dictionary. Read key " + keys[0] + " with no value. At line number: " + _lineNumber + ", column: " + (index - _startOfLineCharIndex) + ".");
				}
				index++;
				index = _skipWhitespaceAndComments(characters, index);
				if (index == -1) {
					throw new IllegalArgumentException("Property list parsing failed while attempting to read dictionary. Read key " + keys[0] + " with no value. At line number: " + _lineNumber + ", column: " + (index - _startOfLineCharIndex) + ".");
				}
				index = _readObjectIntoObjectReference(characters, index, values, keypath + "." + keys[0], factory);
				if (values[0] == null) {
					throw new IllegalArgumentException("Property list parsing failed while attempting to read dictionary. Failed to read value. At line number: " + _lineNumber + ", column: " + (index - _startOfLineCharIndex) + ".");
				}
				index = _skipWhitespaceAndComments(characters, index);
				if (index == -1 || characters[index] != ';') {
					throw new IllegalArgumentException("Property list parsing failed while attempting to read dictionary. Read key and value with no terminating semicolon. At line number: " + _lineNumber + ", column: " + (index - _startOfLineCharIndex) + ".");
				}
				index++;
				index = _skipWhitespaceAndComments(characters, index);
				dictionary.put(keys[0], values[0]);
			}
			if (index >= characters.length) {
				throw new IllegalArgumentException("Property list parsing failed while attempting to read dictionary. Exhausted input before end of dictionary was found. At line number: " + _lineNumber + ", column: " + (index - _startOfLineCharIndex) + ".");
			}
			return ++index < characters.length ? index : -1;
		}

		private int _skipWhitespaceAndComments(char characters[], int index) {
			for (int nextChar = _checkForWhitespaceOrComment(characters, index); nextChar != 1; nextChar = _checkForWhitespaceOrComment(characters, index)) {
				switch (nextChar) {
				case 2:
					index = _processWhitespace(characters, index);
					break;
				case 3:
					index = _processSingleLineComment(characters, index);
					break;
				case 4:
					index = _processMultiLineComment(characters, index);
					break;
				}
			}
			return index < characters.length ? index : -1;
		}

		private int _processWhitespace(char characters[], int index) {
			for (; index < characters.length && _isWhitespace(characters[index]); index++) {
				if (characters[index] == '\n') {
					_lineNumber++;
					_startOfLineCharIndex = index + 1;
				}
			}
			return index < characters.length ? index : -1;
		}

		private int _processSingleLineComment(char characters[], int index) {
			for (index += 2; index < characters.length && characters[index] != '\n'; index++) {
				// DO NOTHING
			}
			return index < characters.length ? index : -1;
		}

		private int _processMultiLineComment(char characters[], int index) {
			_saveIndexes(index, _lineNumber, _startOfLineCharIndex);
			for (index += 2; index + 1 < characters.length && (characters[index] != '*' || characters[index + 1] != '/'); index++) {
				if (characters[index] == '/' && characters[index + 1] == '*') {
					throw new IllegalArgumentException("Property list parsing does not support embedded multi line comments.The first /* was at " + _savedIndexesAsString() + ". A second /* was found at line " + _lineNumber + ", column: " + (index - _startOfLineCharIndex) + ".");
				}
				if (characters[index] == '\n') {
					_lineNumber++;
					_startOfLineCharIndex = index + 1;
				}
			}
			if (index + 1 < characters.length && characters[index] == '*' && characters[index + 1] == '/') {
				index += 2;
			} else {
				throw new IllegalArgumentException("Property list parsing failed while attempting to find closing */ to comment that began at " + _savedIndexesAsString() + ".");
			}
			return index < characters.length ? index : -1;
		}

		private int _checkForWhitespaceOrComment(char characters[], int index) {
			if (index == -1 || index >= characters.length) {
				return 1;
			}
			if (_isWhitespace(characters[index])) {
				return 2;
			}
			if (index + 1 < characters.length) {
				if (characters[index] == '/' && characters[index + 1] == '/') {
					return 3;
				}
				if (characters[index] == '/' && characters[index + 1] == '*') {
					return 4;
				}
			}
			return 1;
		}

		private static final byte _nibbleForHexDigit(char digit) {
			int nibble = 0;
			if (digit >= '0' && digit <= '9') {
				nibble = (byte) (digit - 48);
			} else if (digit >= 'a' && digit <= 'f') {
				nibble = (byte) ((digit - 97) + 10);
			} else if (digit >= 'A' && digit <= 'F') {
				nibble = (byte) ((digit - 65) + 10);
			} else {
				throw new IllegalArgumentException("Non-hex digit passed to _nibbleForHexDigit()");
			}
			return (byte) nibble;
		}

		private static final boolean _isHexDigit(char c) {
			return c >= '0' && c <= '9' || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F';
		}

		private static final boolean _isWhitespace(char c) {
			return c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '\f';
		}

		private static char _nsToUnicode(int c) {
			return c >= 128 ? (char) NSToPrecompUnicodeTable[c - 128] : (char) c;
		}

		private _Utilities() {
			// DO NOTHING
		}
	}

	private EMPropertyListSerialization() {
		throw new IllegalStateException("Can't instantiate an instance of class " + getClass().getName());
	}

	public static String stringFromPropertyList(Object plist) {
		return _Utilities.stringFromPropertyList(plist);
	}

	public static Object propertyListFromString(String string, ParserDataStructureFactory factory) {
		return _Utilities.propertyListFromString(string, factory);
	}

	public static Object propertyListWithPathURL(URL url, ParserDataStructureFactory factory) throws IOException {
		String string = EMPropertyListSerialization.stringFromURL(url);
		return propertyListFromString(string, factory);
	}

	public static Object propertyListWithContentsOfInputStream(InputStream inputStream, ParserDataStructureFactory factory) throws IOException {
		String string = EMPropertyListSerialization.stringFromInputStream(inputStream);
		return propertyListFromString(string, factory);
	}

	public static Object propertyListWithContentsOfFile(String path, ParserDataStructureFactory factory) throws IOException {
		return _Utilities.propertyListWithContentsOfFile(path, factory);
	}

	public static String stringFromFile(File f) throws IOException {
		return new String(bytesFromFile(f));
	}

	public static String stringFromFile(File f, String encoding) throws IOException {
		if (encoding == null) {
			return new String(bytesFromFile(f));
		}
		return new String(bytesFromFile(f), encoding);
	}

	public static byte[] bytesFromFile(File f) throws IOException {
		return bytesFromFile(f, (int) f.length());
	}

	public static byte[] bytesFromFile(File f, int n) throws IOException {
		FileInputStream fis = new FileInputStream(f);
		byte[] data = new byte[n];
		int bytesRead = 0;
		while (bytesRead < n) {
			bytesRead += fis.read(data, bytesRead, n - bytesRead);
		}
		fis.close();
		return data;
	}

	public static byte[] bytesFromInputStream(InputStream in) throws IOException {
		if (in == null)
			throw new IllegalArgumentException("null input stream");

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		int read = -1;
		byte[] buf = new byte[1024 * 50];
		while ((read = in.read(buf)) != -1) {
			bout.write(buf, 0, read);
		}

		return bout.toByteArray();
	}

	public static String stringFromInputStream(InputStream in) throws IOException {
		return new String(bytesFromInputStream(in));
	}

	public static String stringFromInputStream(InputStream in, String encoding) throws IOException {
		return new String(bytesFromInputStream(in), encoding);
	}

	public static String stringFromURL(URL url) throws IOException {
		InputStream is = url.openStream();
		try {
			return stringFromInputStream(is);
		} finally {
			is.close();
		}
	}

	public static String stringFromURL(URL url, String encoding) throws IOException {
		InputStream is = url.openStream();
		try {
			return stringFromInputStream(is, encoding);
		} finally {
			is.close();
		}
	}

	public static class EMMutableData {
		private ByteArrayOutputStream _stream;

		public EMMutableData(int size) {
			_stream = new ByteArrayOutputStream(size);
		}

		public EMMutableData(ByteArrayOutputStream stream) {
			_stream = stream;
		}

		public byte[] bytes() {
			return _stream.toByteArray();
		}

		public EMMutableData clone() {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				baos.write(bytes());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return new EMMutableData(baos);
		}

		public void appendByte(byte b) {
			_stream.write(b);
		}

		public boolean isEqualToData(EMMutableData data) {
			boolean equal;
			if (data == this) {
				equal = true;
			} else {
				byte[] b1 = bytes();
				byte[] b2 = data.bytes();
				if (b1.length != b2.length) {
					equal = false;
				} else {
					equal = true;
					for (int i = 0; equal && i < b1.length; i++) {
						equal = b1[i] == b2[i];
					}
				}
			}
			return equal;
		}
	}
}
