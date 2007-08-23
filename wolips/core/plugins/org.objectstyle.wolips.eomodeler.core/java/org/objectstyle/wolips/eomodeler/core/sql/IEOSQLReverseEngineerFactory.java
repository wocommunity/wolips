package org.objectstyle.wolips.eomodeler.core.sql;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.objectstyle.wolips.eomodeler.core.Activator;
import org.objectstyle.wolips.eomodeler.core.model.EODatabaseConfig;
import org.objectstyle.wolips.eomodeler.core.model.EOModelException;

public interface IEOSQLReverseEngineerFactory {
	public IEOSQLReverseEngineer reverseEngineer(EODatabaseConfig databaseConfig, ClassLoader eomodelClassLoader) throws SecurityException, NoSuchMethodException, ClassNotFoundException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException;
	
	public class Utility {
		public static IEOSQLReverseEngineerFactory reverseEngineerFactory() throws EOModelException {
			IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint("org.objectstyle.wolips.eomodeler.sqlReverseEngineerFactory");
			IExtension[] extensions = extensionPoint.getExtensions();
			List<IEOSQLReverseEngineerFactory> sqlReverseEngineerFactories = new LinkedList<IEOSQLReverseEngineerFactory>();
			for (IExtension extension : extensions) {
				IConfigurationElement[] configurationElements = extension.getConfigurationElements();
				for (IConfigurationElement configurationElement : configurationElements) {
					try {
						IEOSQLReverseEngineerFactory sqlReverseEngineerFactory = (IEOSQLReverseEngineerFactory) configurationElement.createExecutableExtension("class");
						sqlReverseEngineerFactories.add(sqlReverseEngineerFactory);
					} catch (CoreException e) {
						e.printStackTrace();
						Activator.getDefault().log("Could not create SQL generator factory from configuration element: " + configurationElement, e);
					}
				}
			}
			IEOSQLReverseEngineerFactory sqlReverseEngineerFactory = null;
			if (sqlReverseEngineerFactories.size() > 1) {
				throw new EOModelException("There was more than one SQL reverse engineer factory defined.");
			} else if (sqlReverseEngineerFactories.size() == 0) {
				throw new EOModelException("There was no SQL reverse engineer factory defined.");
			} else {
				sqlReverseEngineerFactory = sqlReverseEngineerFactories.get(0);
			}
			return sqlReverseEngineerFactory;
		}
	}
}
