package org.objectstyle.wolips.eomodeler.sql;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.objectstyle.wolips.eomodeler.model.EOModel;

public class SQLUtils {
	public static Object createEOFSQLGenerator(EOModel _model, List _entityNames, Map _databaseConfig, ClassLoader _eomodelClassLoader) throws SecurityException, NoSuchMethodException, ClassNotFoundException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Class sqlGeneratorClass = _eomodelClassLoader.loadClass("org.objectstyle.wolips.eomodeler.sql.EOFSQLGenerator");

		List modelFiles = new LinkedList();
		// AK: I hope this does the right thing... we add all other models
		// before the one in question
		Iterator modelsIter = _model.getModelGroup().getModels().iterator();
		while (modelsIter.hasNext()) {
			EOModel otherModel = (EOModel) modelsIter.next();
			if (otherModel != _model) {
				File otherModelFolder = otherModel.getModelFolder();
				modelFiles.add(otherModelFolder);
			}
		}
		modelFiles.add(_model.getModelFolder());

		Constructor sqlGeneratorConstructor = sqlGeneratorClass.getConstructor(new Class[] { String.class, List.class, List.class, Map.class });
		Object sqlGenerator = sqlGeneratorConstructor.newInstance(new Object[] { _model.getName(), modelFiles, _entityNames, _databaseConfig });
		return sqlGenerator;
	}
}
