package org.objectstyle.wolips.eomodeler.core.model.qualifier;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class EOQualifierParser {
	private static final int NONE = 0;

	private static final int IN_SINGLE_QUOTE = 1;

	private static final int IN_DOUBLE_QUOTE = 2;

	private static final int ESCAPED = 3;

	private static final int IN_NUMBER = 4;

	private static final int IN_OPERATOR = 5;

	private static final int IN_KEYWORD = 6;

	private static final int IN_VARIABLE = 7;

	private static final int IN_BINDING_KEY = 8;

	private static Set<String> OPERATORS;

	private static Set<String> SELECTORS;

	private List<Token> _tokens;

	private String _qualifierString;

	private int _offset;

	private int _tokenStartOffset;

	private int _length;

	private int _tokenNum;

	static {
		EOQualifierParser.OPERATORS = new HashSet<String>();
		EOQualifierParser.OPERATORS.add("=");
		EOQualifierParser.OPERATORS.add(">");
		EOQualifierParser.OPERATORS.add("<");
		EOQualifierParser.OPERATORS.add("==");
		EOQualifierParser.OPERATORS.add(">=");
		EOQualifierParser.OPERATORS.add("<=");
		EOQualifierParser.OPERATORS.add("<>");
		EOQualifierParser.OPERATORS.add("contains");
		EOQualifierParser.OPERATORS.add("like");
		EOQualifierParser.OPERATORS.add("caseinsensitivelike");

		EOQualifierParser.SELECTORS = new HashSet<String>();
		EOQualifierParser.SELECTORS.add("isEqualTo");
		EOQualifierParser.SELECTORS.add("isNotEqualTo");
		EOQualifierParser.SELECTORS.add("isLessThan");
		EOQualifierParser.SELECTORS.add("isGreaterThan");
		EOQualifierParser.SELECTORS.add("isLessThanOrEqualTo");
		EOQualifierParser.SELECTORS.add("isGreaterThanOrEqualTo");
		EOQualifierParser.SELECTORS.add("doesContain");
		EOQualifierParser.SELECTORS.add("isLike");
		EOQualifierParser.SELECTORS.add("isCaseInsensitiveLike");
	}

	protected String caseCorrectedSelectorName(String possibleSelector) {
		for (String selector : EOQualifierParser.SELECTORS) {
			if (selector.equalsIgnoreCase(possibleSelector)) {
				return selector;
			}
		}
		return null;
	}

	protected String caseCorrectedOperatorName(String possibleOperator) {
		for (String operator : EOQualifierParser.OPERATORS) {
			if (operator.equalsIgnoreCase(possibleOperator)) {
				return operator;
			}
		}
		return null;
	}

	public synchronized EOQualifier parseQualifier(String qualifierString) throws ParseException {
		_tokens = new LinkedList<Token>();
		_qualifierString = qualifierString;
		_length = _qualifierString.length();
		_offset = 0;
		_tokenStartOffset = -1;
		_tokenNum = 0;

		tokenize(false);
		EOQualifier qualifier = qualifierForTokens(0);
		return qualifier;
	}

	protected EOQualifier qualifierForTokens(int depth) throws ParseException {
		EOQualifier lqualifier = null;
		Token lvalue = popToken();
		if (lvalue instanceof OpenParenToken) {
			lqualifier = qualifierForTokens(depth + 1);
		} else if (lvalue instanceof KeywordToken || lvalue instanceof KeypathToken) {
			Token operator = popToken();
			if (operator instanceof OperatorToken || operator instanceof KeywordToken) {
				EOQualifier.Comparison selector = new EOQualifier.Comparison(operator.getValue());
				Token rvalue = popToken();
				if (rvalue instanceof VariableToken || rvalue instanceof NamedVariableToken) {
					lqualifier = new EOKeyValueQualifier(lvalue.getValue(), selector, new EOQualifierVariable(rvalue.getValue()));
				} else if (rvalue instanceof NumberToken) {
					lqualifier = new EOKeyValueQualifier(lvalue.getValue(), selector, ((NumberToken) rvalue).toNumber());
				} else if (rvalue instanceof LiteralToken) {
					String value = rvalue.getValue();
					value = value.replaceAll("\\\\(.)", "$1");
					lqualifier = new EOKeyValueQualifier(lvalue.getValue(), selector, value);
				} else if (rvalue instanceof KeywordToken || rvalue instanceof KeypathToken) {
					lqualifier = new EOKeyComparisonQualifier(lvalue.getValue(), selector, rvalue.getValue());
				} else if (rvalue == null) {
					throw new ParseException("Expected a qualifier after " + lvalue + " " + operator + " at offset " + lvalue.getOffset() + ".", lvalue.getOffset());
				} else {
					throw new ParseException(lvalue + ", " + operator + ", " + rvalue + " is an invalid token sequence at offset " + lvalue.getOffset() + ".", lvalue.getOffset());
				}
			} else if (operator == null) {
				throw new ParseException("Expected an operator after " + lvalue + " at offset " + lvalue.getOffset() + ".", lvalue.getOffset());
			} else {
				throw new ParseException(lvalue + ", " + operator + " is an invalid token sequence at offset " + lvalue.getOffset() + ".", lvalue.getOffset());
			}
		} else if (lvalue instanceof NotToken) {
			lqualifier = new EONotQualifier(qualifierForTokens(depth));
		} else if (lvalue != null) {
			throw new ParseException("Invalid token " + lvalue + " at offset " + lvalue.getOffset() + ".", lvalue.getOffset());
		}

		EOQualifier qualifier;
		if (lvalue == null) {
			qualifier = null;
		} else {
			Token nextToken = popToken();
			if (nextToken == null) {
				qualifier = lqualifier;
			} else if (nextToken instanceof CloseParenToken) {
				if (depth > 0) {
					qualifier = lqualifier;
				} else {
					throw new ParseException("Invalid close paren at offset " + nextToken.getOffset() + ".", nextToken.getOffset());
				}
			} else if (nextToken instanceof AndToken) {
				EOQualifier rqualifier = qualifierForTokens(depth);
				if (rqualifier == null) {
					throw new ParseException("'and' requires a second qualifier at offset " + nextToken.getOffset() + ".", nextToken.getOffset());
				}
				qualifier = new EOAndQualifier(lqualifier, rqualifier);
			} else if (nextToken instanceof OrToken) {
				EOQualifier rqualifier = qualifierForTokens(depth);
				if (rqualifier == null) {
					throw new ParseException("'or' requires a second qualifier at offset " + nextToken.getOffset() + ".", nextToken.getOffset());
				}
				qualifier = new EOOrQualifier(lqualifier, rqualifier);
			} else {
				throw new ParseException("Illegal token " + nextToken + " at offset " + nextToken.getOffset() + ".", nextToken.getOffset());
			}
		}

		return qualifier;

	}

	protected void pushToken(Token token) {
		_tokens.add(0, token);
		_tokenNum--;
	}

	protected Token popToken() {
		Token token = null;
		if (_tokenNum < _tokens.size()) {
			token = _tokens.get(_tokenNum++);
		}
		return token;
	}

	protected void tokenize(boolean parentInParen) throws ParseException {
		int groupStartOffset = _offset;
		boolean inParen = parentInParen;
		int state = EOQualifierParser.NONE;
		_tokenStartOffset = -1;
		int previousState = EOQualifierParser.NONE;
		while (_offset < _length) {
			char ch = _qualifierString.charAt(_offset++);
			if (state == EOQualifierParser.ESCAPED) {
				state = previousState;
			} else if (ch == '\\') {
				if (state == EOQualifierParser.IN_DOUBLE_QUOTE || state == EOQualifierParser.IN_SINGLE_QUOTE) {
					previousState = state;
					state = EOQualifierParser.ESCAPED;
				} else {
					throw new ParseException("Backslash in invalid state " + state + " at offset " + _offset + ".", _offset);
				}
			} else if (ch == '"') {
				if (state == EOQualifierParser.IN_DOUBLE_QUOTE) {
					endPendingToken(state);
					state = EOQualifierParser.NONE;
				} else if (state == EOQualifierParser.IN_SINGLE_QUOTE) {
					// ignore
				} else {
					endPendingToken(state);
					_tokenStartOffset = _offset + 1;
					state = EOQualifierParser.IN_DOUBLE_QUOTE;
				}
			} else if (ch == '\'') {
				if (state == EOQualifierParser.IN_SINGLE_QUOTE) {
					endPendingToken(state);
					state = EOQualifierParser.NONE;
				} else if (state == EOQualifierParser.IN_DOUBLE_QUOTE) {
					// ignore
				} else {
					endPendingToken(state);
					_tokenStartOffset = _offset + 1;
					state = EOQualifierParser.IN_SINGLE_QUOTE;
				}
			} else if (state != EOQualifierParser.IN_SINGLE_QUOTE && state != EOQualifierParser.IN_DOUBLE_QUOTE) {
				if (state == EOQualifierParser.IN_VARIABLE) {
					if (ch == 's' || ch == 'd' || ch == 'f' || ch == 'f' || ch == '@' || ch == 'K' || ch == '%') {
						_tokens.add(new VariableToken(_offset - 2, "%" + ch));
						state = EOQualifierParser.NONE;
						_tokenStartOffset = -1;
					} else {
						throw new ParseException("Unknown variable %" + ch + " at offset " + (_offset - 1) + ".", (_offset - 1));
					}
				} else if (ch == '%') {
					endPendingToken(state);
					_tokenStartOffset = _offset;
					state = EOQualifierParser.IN_VARIABLE;
				} else if (ch == '$') {
					endPendingToken(state);
					_tokenStartOffset = _offset;
					state = EOQualifierParser.IN_BINDING_KEY;
				} else if (ch == '(') {
					endPendingToken(state);
					_tokens.add(new OpenParenToken(_offset - 1));
					tokenize(true);
				} else if (ch == ')') {
					if (!inParen) {
						throw new ParseException("Close paren without open paren at offset " + (_offset - 1) + ".", (_offset - 1));
					}
					endPendingToken(state);
					_tokens.add(new CloseParenToken(_offset - 1));
					state = EOQualifierParser.NONE;
					inParen = false;
					break;
				} else if (Character.isWhitespace(ch)) {
					endPendingToken(state);
					state = EOQualifierParser.NONE;
					// _tokens.add(new Whitespace(ch));
				} else if (ch == '<' || ch == '>' || ch == '=') {
					if (state != EOQualifierParser.IN_OPERATOR) {
						endPendingToken(state);
						_tokenStartOffset = _offset;
						state = EOQualifierParser.IN_OPERATOR;
					}
				} else if (Character.isJavaIdentifierStart(ch)) {
					if (state == EOQualifierParser.IN_NUMBER) {
						throw new ParseException("Unexpected character " + ch + " at offset " + (_offset - 1) + ".", (_offset - 1));
					} else if (state == EOQualifierParser.IN_KEYWORD) {
						// IGNORE
					} else if (state == EOQualifierParser.IN_BINDING_KEY) {
						// IGNORE
					} else if (state == EOQualifierParser.IN_OPERATOR) {
						endPendingToken(state);
						state = EOQualifierParser.IN_KEYWORD;
						_tokenStartOffset = _offset;
					} else if (state == EOQualifierParser.NONE) {
						state = EOQualifierParser.IN_KEYWORD;
						_tokenStartOffset = _offset;
					} else {
						throw new ParseException("Unexpected character " + ch + " at offset " + (_offset - 1) + ".", (_offset - 1));
					}
				} else if ((Character.isJavaIdentifierPart(ch) || ch == '.') && (state == EOQualifierParser.IN_KEYWORD || state == EOQualifierParser.IN_BINDING_KEY)) {
					// OK
				} else if (Character.isDigit(ch) || ch == '.' || ch == '-' || ch == ':') {
					if (state == EOQualifierParser.IN_NUMBER) {
						// IGNORE
					} else if (state == EOQualifierParser.IN_KEYWORD) {
						// IGNORE
					} else if (state == EOQualifierParser.IN_BINDING_KEY) {
						// IGNORE
					} else if (state == EOQualifierParser.IN_OPERATOR) {
						endPendingToken(state);
						state = EOQualifierParser.IN_NUMBER;
						_tokenStartOffset = _offset;
					} else if (state == EOQualifierParser.NONE) {
						state = EOQualifierParser.IN_NUMBER;
						_tokenStartOffset = _offset;
					} else {
						throw new ParseException("Unexpected number " + ch + " at offset " + (_offset - 1) + ".", (_offset - 1));
					}
				} else {
					throw new ParseException("Unexpected character " + ch + " at offset " + (_offset - 1) + ".", (_offset - 1));
				}
			}
		}

		if (state == EOQualifierParser.IN_SINGLE_QUOTE) {
			throw new ParseException("Missing closing ' starting at offset " + _tokenStartOffset + ".", _tokenStartOffset);
		} else if (state == EOQualifierParser.IN_DOUBLE_QUOTE) {
			throw new ParseException("Missing closing \" starting at offset " + _tokenStartOffset + ".", _tokenStartOffset);
		} else if (state == EOQualifierParser.ESCAPED) {
			throw new ParseException("Backslash found without escaped character at offset " + (_offset - 1) + ".", (_offset - 1));
		} else if (state == EOQualifierParser.IN_VARIABLE) {
			throw new ParseException("Percent found without variable character at offset " + (_offset - 1) + ".", (_offset - 1));
		} else if (inParen) {
			throw new ParseException("Missing closing paren starting at offset " + groupStartOffset + ".", groupStartOffset);
		}

		if (state == EOQualifierParser.IN_KEYWORD || state == EOQualifierParser.IN_NUMBER || state == EOQualifierParser.IN_BINDING_KEY) {
			_offset++;
		}

		endPendingToken(state);
	}

	protected void endPendingToken(int state) throws ParseException {
		if (_tokenStartOffset != -1) {
			int startOffset = _tokenStartOffset - 1;
			String value = _qualifierString.substring(startOffset, _offset - 1);
			Token token;
			if (state == EOQualifierParser.IN_DOUBLE_QUOTE || state == EOQualifierParser.IN_SINGLE_QUOTE) {
				token = new LiteralToken(_tokenStartOffset - 1, value);
			} else if (state == EOQualifierParser.IN_OPERATOR) {
				String operator = caseCorrectedOperatorName(value);
				if (operator != null) {
					token = new OperatorToken(startOffset, value);
				} else {
					throw new ParseException("Unknown operator " + value + " at offset " + (_tokenStartOffset - 1) + ".", _tokenStartOffset - 1);
				}
			} else if (state == EOQualifierParser.IN_NUMBER) {
				token = new NumberToken(startOffset, value);
			} else if (state == EOQualifierParser.IN_BINDING_KEY) {
				if (value == null || value.length() == 0) {
					throw new ParseException("A variable has no name at offset " + (_tokenStartOffset - 1) + ".", _tokenStartOffset - 1);
				}
				token = new NamedVariableToken(startOffset, value);
			} else if (state == EOQualifierParser.IN_KEYWORD) {
				String operator = caseCorrectedOperatorName(value);
				String selector = caseCorrectedSelectorName(value);
				if (operator != null) {
					token = new OperatorToken(startOffset, operator);
				} else if (selector != null) {
					token = new SelectorToken(startOffset, selector);
				} else if (EOQualifierParser.OPERATORS.contains(value.toLowerCase())) {
					token = new OperatorToken(startOffset, value);
				} else if (value.equalsIgnoreCase("and")) {
					token = new AndToken(startOffset);
				} else if (value.equalsIgnoreCase("or")) {
					token = new OrToken(startOffset);
				} else if (value.equalsIgnoreCase("not")) {
					token = new NotToken(startOffset);
				} else if (value.contains(".")) {
					token = new KeypathToken(startOffset, value);
				} else {
					token = new KeywordToken(startOffset, value);
				}
			} else {
				token = new Token(startOffset, value);
			}
			_tokens.add(token);
			_tokenStartOffset = -1;
		}
	}

	protected static class Token {
		private int _offset;

		private String _value;

		public Token(int offset, String value) {
			_offset = offset;
			_value = value;
		}

		public int getOffset() {
			return _offset;
		}

		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return "[Token: " + _value + "]";
		}
	}

	protected static class AndToken extends Token {
		public AndToken(int offset) {
			super(offset, "and");
		}

		@Override
		public String toString() {
			return "[And]";
		}
	}

	protected static class OrToken extends Token {
		public OrToken(int offset) {
			super(offset, "or");
		}

		@Override
		public String toString() {
			return "[Or]";
		}
	}

	protected static class NotToken extends Token {
		public NotToken(int offset) {
			super(offset, "not");
		}

		@Override
		public String toString() {
			return "[Not]";
		}
	}

	protected static class KeywordToken extends Token {
		public KeywordToken(int offset, String value) {
			super(offset, value);
		}

		@Override
		public String toString() {
			return "[Keyword: " + getValue() + "]";
		}
	}

	protected static class OpenParenToken extends OperatorToken {
		public OpenParenToken(int offset) {
			super(offset, "(");
		}

		@Override
		public String toString() {
			return "[OpenParen]";
		}
	}

	protected static class CloseParenToken extends OperatorToken {
		public CloseParenToken(int offset) {
			super(offset, ")");
		}

		@Override
		public String toString() {
			return "[CloseParen]";
		}
	}

	protected static class SelectorToken extends OperatorToken {
		public SelectorToken(int offset, String value) {
			super(offset, value);
		}

		@Override
		public String toString() {
			return "[Selector: " + getValue() + "]";
		}
	}

	protected static class KeypathToken extends Token {
		public KeypathToken(int offset, String value) {
			super(offset, value);
		}

		@Override
		public String toString() {
			return "[Keypath: " + getValue() + "]";
		}
	}

	protected static class LiteralToken extends Token {
		public LiteralToken(int offset, String value) {
			super(offset, value);
		}

		@Override
		public String toString() {
			return "[Literal: " + getValue() + "]";
		}
	}

	protected static class OperatorToken extends Token {
		public OperatorToken(int offset, String value) {
			super(offset, value);
		}

		@Override
		public String toString() {
			return "[Operator: " + getValue() + "]";
		}
	}

	protected static class NumberToken extends Token {
		public NumberToken(int offset, String value) {
			super(offset, value);
		}

		public Number toNumber() {
			Number number;
			String value = getValue();
			if (value.contains(".")) {
				number = new BigDecimal(value);
			} else {
				number = Integer.parseInt(value);
			}
			return number;
		}

		@Override
		public String toString() {
			return "[Number: " + getValue() + "]";
		}
	}

	protected static class VariableToken extends Token {
		public VariableToken(int offset, String value) {
			super(offset, value);
		}

		@Override
		public String toString() {
			return "[Variable: " + getValue() + "]";
		}
	}

	protected static class NamedVariableToken extends Token {
		public NamedVariableToken(int offset, String value) {
			super(offset, value);
		}

		@Override
		public String toString() {
			return "[NamedVariable: " + getValue() + "]";
		}
	}

	public static void main(String[] args) {
		try {
			EOQualifierParser parser = new EOQualifierParser();
			// EOQualifier q = parser.parseQualifier("this is \"mike'\"");
			// EOQualifier q = parser.parseQualifier("name = %@ and
			// person.firstName == \"mi\\ke\" and age = 5");
			//EOQualifier q = parser.parseQualifier("not name = %@ or (age like 'Test*') and (age like 'T?est') and person.firstName == \"mi'ke\" and (lastName caseinsensitiveLike 'schrag' or (age = 5)) or(age>10) and (name<0.10) or (somevar isAnagramOf: 'test')");
			//System.out.println("EOQualifierParser.main: " + q);
			EOQualifier q = parser.parseQualifier("test = \"test\"");
			System.out.println("EOQualifierParser.main: " + q);
		} catch (Throwable t) {
			t.printStackTrace(System.out);
		}
	}
}
