package org.objectstyle.wolips.eomodeler.sql;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.eomodeler.model.EOModel;
import org.objectstyle.wolips.eomodeler.utils.ClasspathUtils;

public class SQLUtils {
  public static String generateSqlScript(EOModel _model, List _entityNames, Map _flags, Map _overrideConnectionDictionary) throws SecurityException, NoSuchMethodException, ClassNotFoundException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, MalformedURLException, JavaModelException {
    ClassLoader eomodelClassLoader = ClasspathUtils.createEOModelClassLoader(_model);
    return generateSqlScript(_model, _entityNames, _flags, _overrideConnectionDictionary, eomodelClassLoader);
  }

  public static String generateSqlScript(EOModel _model, List _entityNames, Map _flags, Map _overrideConnectionDictionary, ClassLoader _eomodelClassLoader) throws SecurityException, NoSuchMethodException, ClassNotFoundException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
    Class sqlGeneratorClass = _eomodelClassLoader.loadClass("org.objectstyle.wolips.eomodeler.sql.EOFSQLGenerator");
    
    List modelFiles = new LinkedList();
    //AK: I hope this does the right thing... we add all other models before the one in question
    Iterator modelsIter = _model.getModelGroup().getModels().iterator();
    while (modelsIter.hasNext()) {
    	EOModel otherModel = (EOModel) modelsIter.next();
    	if(otherModel != _model) {
    		File otherModelFolder = otherModel.getModelFolder();
    		modelFiles.add(otherModelFolder);
    	}
    }
    modelFiles.add(_model.getModelFolder());

    Constructor sqlGeneratorConstructor = sqlGeneratorClass.getConstructor(new Class[] { String.class, List.class, List.class, Map.class, Map.class });
    Object sqlGenerator = sqlGeneratorConstructor.newInstance(new Object[] { _model.getName(), modelFiles, _entityNames, _flags, _overrideConnectionDictionary });
    Method getSchemaCreationScriptMethod = sqlGeneratorClass.getMethod("getSchemaCreationScript", null);
    String sqlScript = (String) getSchemaCreationScriptMethod.invoke(sqlGenerator, null);

    return sqlScript;
  }
}
