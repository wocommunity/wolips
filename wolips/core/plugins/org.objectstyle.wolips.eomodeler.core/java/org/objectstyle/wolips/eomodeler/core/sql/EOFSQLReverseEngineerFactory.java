package org.objectstyle.wolips.eomodeler.core.sql;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.objectstyle.wolips.eomodeler.core.model.EODatabaseConfig;

public class EOFSQLReverseEngineerFactory implements IEOSQLReverseEngineerFactory {
	public IEOSQLReverseEngineer reverseEngineer(EODatabaseConfig databaseConfig, ClassLoader eomodelClassLoader) throws SecurityException, NoSuchMethodException, ClassNotFoundException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		WOUtils.setWOSystemProperties();
		String className = "org.objectstyle.wolips.eomodeler.core.sql.EOFSQLReverseEngineer53";
		Class reverseEngineerClass = Class.forName(className, true, eomodelClassLoader);
		Constructor reverseEngineerConstructor = reverseEngineerClass.getConstructor(new Class[] { String.class, Map.class });
		Object reverseEngineerButICantCastItBecauseItCrossesClassLoaders = reverseEngineerConstructor.newInstance(new Object[] { databaseConfig.getAdaptorName(), databaseConfig.getConnectionDictionary() });
		IEOSQLReverseEngineer reverseEngineer = new ReflectionSQLReverseEngineer(reverseEngineerButICantCastItBecauseItCrossesClassLoaders);
		return reverseEngineer;
	}

	protected static class ReflectionSQLReverseEngineer implements IEOSQLReverseEngineer {
		private Object _eofSQLReverseEngineer;

		public ReflectionSQLReverseEngineer(Object eofSQLReverseEngineer) {
			_eofSQLReverseEngineer = eofSQLReverseEngineer;
		}

		public File reverseEngineerIntoModel() throws IOException {
			try {
				return (File) _eofSQLReverseEngineer.getClass().getMethod("reverseEngineerIntoModel").invoke(_eofSQLReverseEngineer);
			} catch (Throwable e) {
				throw new RuntimeException("Failed to reverse engineer.", e);
			}
		}

		public List<String> reverseEngineerTableNames() {
			try {
				return (List<String>) _eofSQLReverseEngineer.getClass().getMethod("reverseEngineerTableNames").invoke(_eofSQLReverseEngineer);
			} catch (Throwable e) {
				throw new RuntimeException("Failed to reverse engineer.", e);
			}
		}

		public File reverseEngineerWithTableNamesIntoModel(List<String> tableNamesList) throws IOException {
			try {
				return (File) _eofSQLReverseEngineer.getClass().getMethod("reverseEngineerWithTableNamesIntoModel", List.class).invoke(_eofSQLReverseEngineer, tableNamesList);
			} catch (Throwable e) {
				throw new RuntimeException("Failed to reverse engineer.", e);
			}
		}
	}
}
