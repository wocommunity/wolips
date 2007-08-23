package org.objectstyle.wolips.eomodeler.core.sql;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface IEOSQLReverseEngineer {
	public List<String> reverseEngineerTableNames();

	public File reverseEngineerIntoModel() throws IOException;

	public File reverseEngineerWithTableNamesIntoModel(List<String> tableNamesList) throws IOException;
}
