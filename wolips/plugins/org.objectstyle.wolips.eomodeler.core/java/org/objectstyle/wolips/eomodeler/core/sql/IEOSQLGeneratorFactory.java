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
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelException;

public interface IEOSQLGeneratorFactory {
	public IEOSQLGenerator sqlGenerator(EOModel model, List<String> entityNames, EODatabaseConfig databaseConfig, ClassLoader eomodelClassLoader, boolean runInEntityModeler) throws SecurityException, NoSuchMethodException, ClassNotFoundException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException;
	
	public class Utility {
		public static IEOSQLGeneratorFactory sqlGeneratorFactory() throws EOModelException {
			IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint("org.objectstyle.wolips.eomodeler.sqlGeneratorFactory");
			IExtension[] extensions = extensionPoint.getExtensions();
			List<IEOSQLGeneratorFactory> sqlGeneratorFactories = new LinkedList<IEOSQLGeneratorFactory>();
			for (IExtension extension : extensions) {
				IConfigurationElement[] configurationElements = extension.getConfigurationElements();
				for (IConfigurationElement configurationElement : configurationElements) {
					try {
						IEOSQLGeneratorFactory sqlGeneratorFactory = (IEOSQLGeneratorFactory) configurationElement.createExecutableExtension("class");
						sqlGeneratorFactories.add(sqlGeneratorFactory);
					} catch (CoreException e) {
						e.printStackTrace();
						Activator.getDefault().log("Could not create SQL generator factory from configuration element: " + configurationElement, e);
					}
				}
			}
			IEOSQLGeneratorFactory sqlGeneratorFactory = null;
			if (sqlGeneratorFactories.size() > 1) {
				throw new EOModelException("There was more than one SQL generator factory defined.");
			} else if (sqlGeneratorFactories.size() == 0) {
				throw new EOModelException("There was no SQL generator factory defined.");
			} else {
				sqlGeneratorFactory = sqlGeneratorFactories.get(0);
			}
			return sqlGeneratorFactory;
		}
	}
}
