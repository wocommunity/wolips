package org.objectstyle.wolips.eomodeler.core.sql;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.objectstyle.wolips.eomodeler.core.model.EOModel;

public class SQLUtils {
	public static Object createEOFSQLGenerator(EOModel model, List entityNames, Map databaseConfig, ClassLoader eomodelClassLoader) throws SecurityException, NoSuchMethodException, ClassNotFoundException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Class sqlGeneratorClass = eomodelClassLoader.loadClass("org.objectstyle.wolips.eomodeler.sql.EOFSQLGenerator");

		List modelURLs = new LinkedList();
		// AK: I hope this does the right thing... we add all other models
		// before the one in question
		Iterator modelsIter = model.getModelGroup().getModels().iterator();
		while (modelsIter.hasNext()) {
			EOModel otherModel = (EOModel) modelsIter.next();
			if (otherModel != model) {
				URL otherModelURL = otherModel.getModelURL();
				modelURLs.add(otherModelURL);
			}
		}
		modelURLs.add(model.getModelURL());

		Constructor sqlGeneratorConstructor = sqlGeneratorClass.getConstructor(new Class[] { String.class, List.class, List.class, Map.class });
		Object sqlGenerator = sqlGeneratorConstructor.newInstance(new Object[] { model.getName(), modelURLs, entityNames, databaseConfig });
		return sqlGenerator;
	}

	public static Object createEOFReverseEngineer(String adaptorName, Map connectionDictionary, ClassLoader eomodelClassLoader) throws SecurityException, NoSuchMethodException, ClassNotFoundException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Class reverseEngineerClass = eomodelClassLoader.loadClass("org.objectstyle.wolips.eomodeler.sql.EOFReverseEngineer");
		Constructor reverseEngineerConstructor = reverseEngineerClass.getConstructor(new Class[] { String.class, Map.class });
		Object reverseEngineer = reverseEngineerConstructor.newInstance(new Object[] { adaptorName, connectionDictionary });
		return reverseEngineer;
	}
}
