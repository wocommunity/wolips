package org.objectstyle.wolips.eomodeler.core.sql;

import java.sql.SQLException;
import java.util.Map;

public interface IEOSQLGenerator {
	public String generateSchemaCreationScript(Map flagsMap);

	public void executeSQL(String sql) throws SQLException;
}
