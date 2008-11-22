package org.objectstyle.wolips.eomodeler.core.utils;

import java.util.LinkedList;
import java.util.List;

public class SQLUtils {
	/**
	 * Splits semicolon-separate sql statements into an array of strings
	 * 
	 * @param sql
	 *            a multi-line sql statement
	 * @return an array of sql statements
	 */
	public static List<String> splitSQLStatements(String sql, char commandSeparatorChar) {
		List<String> statements = new LinkedList<String>();
		if (sql != null) {
			StringBuffer statementBuffer = new StringBuffer();
			int length = sql.length();
			boolean inQuotes = false;
			for (int i = 0; i < length; i++) {
				char ch = sql.charAt(i);
				if (ch == '\r' || ch == '\n') {
					// ignore
				}
				else if (!inQuotes && ch == commandSeparatorChar) {
					String statement = statementBuffer.toString().trim();
					if (statement.length() > 0) {
						statements.add(statement);
					}
					statementBuffer.setLength(0);
				}
				else {
					// Support for escaping apostrophes, e.g. 'Mike\'s Code' 
					if (inQuotes && ch == '\\') {
						statementBuffer.append(ch);
						ch = sql.charAt(++ i);
					}
					else if (ch == '\'') {
						inQuotes = !inQuotes;
					}
					statementBuffer.append(ch);
				}
			}
			String statement = statementBuffer.toString().trim();
			if (statement.length() > 0) {
				statements.add(statement);
			}
		}
		return statements;
	}
}
