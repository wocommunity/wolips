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
  public static String generateSqlScript(EOModel _model, List _entityNames, Map _flags, Map _overrideConnectionDictionary) throws SecurityException, NoSuchMethodException, MalformedURLException, JavaModelException, ClassNotFoundException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
    ClassLoader eomodelClassLoader = ClasspathUtils.createEOModelClassLoader(_model);
    Class sqlGeneratorClass = Class.forName("org.objectstyle.wolips.eomodeler.sql.EOFSQLGenerator", true, eomodelClassLoader);

    List modelFiles = new LinkedList();
    Iterator modelsIter = _model.getModelGroup().getModels().iterator();
    while (modelsIter.hasNext()) {
      EOModel otherModel = (EOModel) modelsIter.next();
      File otherModelFolder = otherModel.getModelFolder();
      modelFiles.add(otherModelFolder);
    }

    Constructor sqlGeneratorConstructor = sqlGeneratorClass.getConstructor(new Class[] { String.class, List.class, List.class, Map.class, Map.class });
    Object sqlGenerator = sqlGeneratorConstructor.newInstance(new Object[] { _model.getName(), modelFiles, _entityNames, _flags, _overrideConnectionDictionary });
    Method getSchemaCreationScriptMethod = sqlGeneratorClass.getMethod("getSchemaCreationScript", null);
    String sqlScript = (String) getSchemaCreationScriptMethod.invoke(sqlGenerator, null);
    return sqlScript;
  }
}
