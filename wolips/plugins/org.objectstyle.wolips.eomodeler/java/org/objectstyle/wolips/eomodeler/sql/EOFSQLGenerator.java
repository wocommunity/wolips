/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.eomodeler.sql;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.webobjects.eoaccess.EOAdaptorChannel;
import com.webobjects.eoaccess.EOAdaptorContext;
import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EODatabase;
import com.webobjects.eoaccess.EODatabaseChannel;
import com.webobjects.eoaccess.EODatabaseContext;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOModel;
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.eoaccess.EOSchemaGeneration;
import com.webobjects.eoaccess.EOSynchronizationFactory;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSBundle;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSSet;
import com.webobjects.jdbcadaptor.JDBCAdaptor;
import com.webobjects.jdbcadaptor.JDBCContext;

/**
 * Declare a class named "org.objectstyle.wolips.eomodeler.EOModelProcessor" with the following methods:
 * public void processModel(EOModel _model, NSMutableArray _entities, NSMutableDictionary _flags);
 * public void processSQL(StringBuffer _sqlBuffer, EOModel _model, NSMutableArray _entities, NSMutableDictionary _flags);
 * 
 * or declare an "eomodelProcessorClassName" in your extra info dictionary that has methods of the same signature.
 * 
 * processModel will be called prior to sql generation, and processSQL will be called after sql generation but before it retuns to EOModeler.
 * 
 * @author mschrag
 *
 */
public class EOFSQLGenerator {
  private NSMutableArray myEntities;
  private EOModel myModel;
  private EOModelGroup myGroup;
  private Object myModelProcessor;

  public EOFSQLGenerator(String _modelName, List _modelFolders, List _entityNames, Map _databaseConfig) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    Map databaseConfig = (_databaseConfig == null ? new HashMap() : _databaseConfig);
    String prototypeEntityName = (String) databaseConfig.get("prototypeEntityName");

    myGroup = new EOModelGroup();
    Iterator modelFoldersIter = _modelFolders.iterator();
    while (modelFoldersIter.hasNext()) {
      File modelFolder = (File) modelFoldersIter.next();
      EOModel newModel = myGroup.addModelWithPathURL(modelFolder.toURL());
      System.out.println("Loading model: " + newModel.name());
      if (prototypeEntityName != null) {
        EOEntity entity = newModel.entityNamed("EOPrototypes");
        if (entity != null) {
          newModel.removeEntity(entity);
        }
        entity = newModel.entityNamed(prototypeEntityName);
        if (entity != null) {
          newModel.removeEntity(entity);
          entity.setName("EOPrototypes");
          newModel.addEntity(entity);
        }
      }
    }
    myEntities = new NSMutableArray();
    myModel = myGroup.modelNamed(_modelName);
    Map overrideConnectionDictionary = (Map) databaseConfig.get("connectionDictionary");
    if (overrideConnectionDictionary != null) {
      NSMutableDictionary connectionDictionary = new NSMutableDictionary();
      Iterator overrideConnectionDictionaryIter = overrideConnectionDictionary.entrySet().iterator();
      while (overrideConnectionDictionaryIter.hasNext()) {
        Map.Entry overrideEntry = (Map.Entry) overrideConnectionDictionaryIter.next();
        Object key = overrideEntry.getKey();
        Object value = overrideEntry.getValue();
        if (key instanceof String && value instanceof String) {
          connectionDictionary.setObjectForKey(value, key);
        }
      }
      myModel.setConnectionDictionary(connectionDictionary);
      String eomodelProcessorClassName = (String) connectionDictionary.objectForKey("eomodelProcessorClassName");
      if (eomodelProcessorClassName != null) {
        findModelProcessor(eomodelProcessorClassName, true);
      }
    }
    if (myModelProcessor == null) {
      findModelProcessor("org.objectstyle.wolips.eomodeler.EOModelProcessor", false);
    }
    if (_entityNames == null) {
      Enumeration entitiesEnum = myModel.entities().objectEnumerator();
      while (entitiesEnum.hasMoreElements()) {
        EOEntity entity = (EOEntity) entitiesEnum.nextElement();
        if (!isPrototype(entity)) {// && entityUsesSeparateTable(entity)) {
          myEntities.addObject(entity);
        }
      }
    }
    else {
      Iterator entityNamesIter = _entityNames.iterator();
      while (entityNamesIter.hasNext()) {
        String entityName = (String) entityNamesIter.next();
        EOEntity entity = myModel.entityNamed(entityName);
        myEntities.addObject(entity);
      }
    }

    ensureSingleTableInheritanceParentEntitiesAreIncluded();
    ensureSingleTableInheritanceChildEntitiesAreIncluded();
  }

  protected void ensureSingleTableInheritanceParentEntitiesAreIncluded() {
    Enumeration entitiesEnum = new NSArray(myEntities).objectEnumerator();
    while (entitiesEnum.hasMoreElements()) {
      EOEntity entity = (EOEntity) entitiesEnum.nextElement();
      ensureSingleTableInheritanceParentEntitiesAreIncluded(entity);
    }
  }

  protected void ensureSingleTableInheritanceChildEntitiesAreIncluded() {
    Enumeration entitiesEnum = myModel.entities().objectEnumerator();
    while (entitiesEnum.hasMoreElements()) {
      EOEntity entity = (EOEntity) entitiesEnum.nextElement();
      if (isSingleTableInheritance(entity)) {
        EOEntity parentEntity = entity.parentEntity();
        if (myEntities.containsObject(parentEntity) && !myEntities.containsObject(entity)) {
          myEntities.addObject(entity);
        }
      }
    }
  }

  protected void ensureSingleTableInheritanceParentEntitiesAreIncluded(EOEntity _entity) {
    if (isSingleTableInheritance(_entity)) {
      EOEntity parentEntity = _entity.parentEntity();
      if (!myEntities.containsObject(parentEntity)) {
        myEntities.addObject(parentEntity);
        ensureSingleTableInheritanceParentEntitiesAreIncluded(_entity);
      }
    }
  }

  protected boolean isPrototype(EOEntity _entity) {
    String entityName = _entity.name();
    boolean isPrototype = (entityName.startsWith("EO") && entityName.endsWith("Prototypes"));
    return isPrototype;
  }

  protected boolean isSingleTableInheritance(EOEntity _entity) {
    EOEntity parentEntity = _entity.parentEntity();
    return parentEntity != null && _entity.externalName() != null && _entity.externalName().equalsIgnoreCase(parentEntity.externalName());
  }

  protected boolean isInherited(EOAttribute _attribute) {
    boolean inherited = false;
    EOEntity parentEntity = _attribute.entity().parentEntity();
    while (!inherited && parentEntity != null) {
      inherited = (parentEntity.attributeNamed(_attribute.name()) != null);
      parentEntity = parentEntity.parentEntity();
    }
    return inherited;
  }

  protected void fixDuplicateSingleTableInheritanceDropStatements(EOSynchronizationFactory _syncFactory, NSMutableDictionary _flags, StringBuffer _sqlBuffer) {
    if ("YES".equals(_flags.objectForKey(EOSchemaGeneration.DropTablesKey))) {
      NSMutableArray dropEntities = new NSMutableArray(myEntities);
      for (int entityNum = dropEntities.count() - 1; entityNum >= 0; entityNum--) {
        EOEntity entity = (EOEntity) dropEntities.objectAtIndex(entityNum);
        if (isSingleTableInheritance(entity)) {
          dropEntities.removeObjectAtIndex(entityNum);
        }
      }
      if (dropEntities.count() != myEntities.count()) {
        NSMutableDictionary dropFlags = new NSMutableDictionary();
        dropFlags.setObjectForKey("YES", EOSchemaGeneration.DropTablesKey);
        dropFlags.setObjectForKey("NO", EOSchemaGeneration.DropPrimaryKeySupportKey);
        dropFlags.setObjectForKey("NO", EOSchemaGeneration.CreateTablesKey);
        dropFlags.setObjectForKey("NO", EOSchemaGeneration.CreatePrimaryKeySupportKey);
        dropFlags.setObjectForKey("NO", EOSchemaGeneration.PrimaryKeyConstraintsKey);
        dropFlags.setObjectForKey("NO", EOSchemaGeneration.ForeignKeyConstraintsKey);
        dropFlags.setObjectForKey("NO", EOSchemaGeneration.CreateDatabaseKey);
        dropFlags.setObjectForKey("NO", EOSchemaGeneration.DropDatabaseKey);
        _flags.setObjectForKey("NO", EOSchemaGeneration.DropTablesKey);
        String dropSql = _syncFactory.schemaCreationScriptForEntities(dropEntities, dropFlags);
        _sqlBuffer.append(dropSql);
        _sqlBuffer.append("\n");
      }
    }
  }

  public String getSchemaCreationScript(Map _flagsMap) {
    NSMutableDictionary flags = new NSMutableDictionary();
    if (_flagsMap != null) {
      Iterator entriesIter = _flagsMap.entrySet().iterator();
      while (entriesIter.hasNext()) {
        Map.Entry flag = (Map.Entry) entriesIter.next();
        flags.setObjectForKey(flag.getValue(), flag.getKey());
      }
    }

    callModelProcessorMethodIfExists("processModel", new Object[] { myModel, myEntities, flags });

    EODatabaseContext dbc = new EODatabaseContext(new EODatabase(myModel));
    EOAdaptorContext ac = dbc.adaptorContext();
    EOSynchronizationFactory sf = ((JDBCAdaptor) ac.adaptor()).plugIn().synchronizationFactory();

    StringBuffer sqlBuffer = new StringBuffer();
    fixDuplicateSingleTableInheritanceDropStatements(sf, flags, sqlBuffer);

    String sql = sf.schemaCreationScriptForEntities(myEntities, flags);
    sqlBuffer.append(sql);

    callModelProcessorMethodIfExists("processSQL", new Object[] { sqlBuffer, myModel, myEntities, flags });

    String sqlBufferStr = sqlBuffer.toString();
    return sqlBufferStr;
  }

  public Object callModelProcessorMethodIfExists(String _methodName, Object[] _objs) {
    try {
      Object results = null;
      if (myModelProcessor != null) {
        Method[] methods = myModelProcessor.getClass().getMethods();
        Method matchingMethod = null;
        for (int methodNum = 0; matchingMethod == null && methodNum < methods.length; methodNum++) {
          Method method = methods[methodNum];
          if (method.getName().equals(_methodName)) {
            Class[] parameterTypes = method.getParameterTypes();
            boolean parametersMatch = false;
            if ((_objs == null || _objs.length == 0) && parameterTypes.length == 0) {
              parametersMatch = true;
            }
            else if (_objs != null && _objs.length == parameterTypes.length) {
              parametersMatch = true;
              for (int parameterTypeNum = 0; parametersMatch && parameterTypeNum < parameterTypes.length; parameterTypeNum++) {
                Class parameterType = parameterTypes[parameterTypeNum];
                if (_objs[parameterTypeNum] != null && !parameterType.isAssignableFrom(_objs.getClass())) {
                  parametersMatch = false;
                }
              }
            }
            matchingMethod = method;
          }
        }
        if (matchingMethod != null) {
          results = matchingMethod.invoke(myModelProcessor, _objs);
        }
        else {
          System.out.println("EOFSQLGenerator.callModelProcessorMethodIfExists: Missing delegate " + _methodName);
        }
      }
      return results;
    }
    catch (Throwable t) {
      throw new RuntimeException("Failed to execute " + _methodName + " on " + myModelProcessor + ".", t);
    }
  }

  public void findModelProcessor(String _modelProcessorClassName, boolean _throwExceptionIfMissing) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    try {
      Class modelProcessorClass = Class.forName(_modelProcessorClassName);
      myModelProcessor = modelProcessorClass.newInstance();
    }
    catch (ClassNotFoundException e) {
      System.out.println("EOFSQLGenerator.getModelProcessor: Missing model processor " + _modelProcessorClassName);
      if (_throwExceptionIfMissing) {
        throw e;
      }
    }
    catch (InstantiationException e) {
      if (_throwExceptionIfMissing) {
        throw e;
      }
      e.printStackTrace();
    }
    catch (IllegalAccessException e) {
      if (_throwExceptionIfMissing) {
        throw e;
      }
      e.printStackTrace();
    }
    catch (RuntimeException e) {
      if (_throwExceptionIfMissing) {
        throw e;
      }
      e.printStackTrace();
    }
  }

  public void executeSQL(String _sql) throws SQLException {
    System.out.println("EOFSQLGenerator.executeSQL: " + myModel.connectionDictionary());
    EODatabaseContext databaseContext = new EODatabaseContext(new EODatabase(myModel));
    EODatabaseChannel databaseChannel = databaseContext.availableChannel();
    EOAdaptorChannel adaptorChannel = databaseChannel.adaptorChannel();
    if (!adaptorChannel.isOpen()) {
      adaptorChannel.openChannel();
    }
    JDBCContext jdbccontext = (JDBCContext) adaptorChannel.adaptorContext();
    try {
      jdbccontext.beginTransaction();
      Connection conn = jdbccontext.connection();
      Statement stmt = conn.createStatement();
      stmt.executeUpdate(_sql);
      conn.commit();
    }
    catch (SQLException sqlexception) {
      sqlexception.printStackTrace(System.out);
      jdbccontext.rollbackTransaction();
      throw sqlexception;
    }
    adaptorChannel.closeChannel();
  }

  public Map externalTypes() {
    EODatabaseContext dbc = new EODatabaseContext(new EODatabase(myModel));
    EOAdaptorContext ac = dbc.adaptorContext();
    NSDictionary jdbc2Info = ((JDBCAdaptor) ac.adaptor()).plugIn().jdbcInfo();
    return (Map) toJavaCollections(jdbc2Info);
  }

  protected static Object toJavaCollections(Object _obj) {
    Object result;
    if (_obj instanceof NSDictionary) {
      Map map = new HashMap();
      NSDictionary nsDict = (NSDictionary) _obj;
      Enumeration keysEnum = nsDict.allKeys().objectEnumerator();
      while (keysEnum.hasMoreElements()) {
        Object key = keysEnum.nextElement();
        Object value = nsDict.objectForKey(key);
        key = toJavaCollections(key);
        value = toJavaCollections(value);
        map.put(key, value);
      }
      result = map;
    }
    else if (_obj instanceof NSArray) {
      List list = new LinkedList();
      NSArray nsArray = (NSArray) _obj;
      Enumeration valuesEnum = nsArray.objectEnumerator();
      while (valuesEnum.hasMoreElements()) {
        Object value = valuesEnum.nextElement();
        value = toJavaCollections(value);
        list.add(value);
      }
      result = list;
    }
    else if (_obj instanceof NSSet) {
      Set set = new HashSet();
      NSSet nsSet = (NSSet) _obj;
      Enumeration valuesEnum = nsSet.objectEnumerator();
      while (valuesEnum.hasMoreElements()) {
        Object value = valuesEnum.nextElement();
        value = toJavaCollections(value);
        set.add(value);
      }
      result = set;
    }
    else {
      result = _obj;
    }
    return result;
  }

  public static void main(String argv[]) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    Map flags = new HashMap();
    flags.put(EOSchemaGeneration.DropTablesKey, "YES");
    flags.put(EOSchemaGeneration.DropPrimaryKeySupportKey, "YES");
    flags.put(EOSchemaGeneration.CreateTablesKey, "YES");
    flags.put(EOSchemaGeneration.CreatePrimaryKeySupportKey, "YES");
    flags.put(EOSchemaGeneration.PrimaryKeyConstraintsKey, "YES");
    flags.put(EOSchemaGeneration.ForeignKeyConstraintsKey, "YES");
    flags.put(EOSchemaGeneration.CreateDatabaseKey, "NO");
    flags.put(EOSchemaGeneration.DropDatabaseKey, "NO");

    File[] paths = new File[] { new File("/Library/Frameworks/JavaBusinessLogic.framework/Resources/Movies.eomodeld"), new File("/Library/Frameworks/JavaBusinessLogic.framework/Resources/Rentals.eomodeld") };
    // probably should have an option to change the connection dict to use a specific plugin or url
    // all entities in one model
    EOFSQLGenerator generator1 = new EOFSQLGenerator("Movies", Arrays.asList(paths), null, null);
    System.out.println("EOFSQLGenerator.main: " + NSBundle.mainBundle());
    System.out.println(generator1.getSchemaCreationScript(null));
    //
    //    System.out.println("***********************************");
    //
    //    // only movie entity
    //
    //    EOFSQLGenerator generator2 = new EOFSQLGenerator("Movies", Arrays.asList(paths), Arrays.asList(new String[] { "Movie" }), optionsCreate);
    //    System.out.println(generator2.getSchemaCreationScript());
  }
}
