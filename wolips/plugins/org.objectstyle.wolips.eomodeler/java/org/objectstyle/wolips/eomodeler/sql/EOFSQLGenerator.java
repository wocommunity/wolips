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
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.webobjects.eoaccess.EOAdaptorContext;
import com.webobjects.eoaccess.EODatabase;
import com.webobjects.eoaccess.EODatabaseContext;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOModel;
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.eoaccess.EOSchemaGeneration;
import com.webobjects.eoaccess.EOSynchronizationFactory;
import com.webobjects.foundation.NSBundle;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSProperties;
import com.webobjects.jdbcadaptor.JDBCAdaptor;

public class EOFSQLGenerator {
  private NSMutableDictionary myFlags;
  private NSMutableArray myEntities;
  private EOModel myModel;
  private EOModelGroup myGroup;

  public EOFSQLGenerator(String _modelName, List _modelFolders, List _entityNames, Map _flags) throws MalformedURLException {
    NSProperties._setMainBundleName("/Volumes/mDT Workspace/workspace/MDTask/build/MDTask.woa/");
    myFlags = new NSMutableDictionary();
    Iterator entriesIter = _flags.entrySet().iterator();
    while (entriesIter.hasNext()) {
      Map.Entry flag = (Map.Entry) entriesIter.next();
      myFlags.setObjectForKey(flag.getValue(), flag.getKey());
    }
    myGroup = new EOModelGroup();
    Iterator modelFoldersIter = _modelFolders.iterator();
    while (modelFoldersIter.hasNext()) {
      File modelFolder = (File) modelFoldersIter.next();
      myGroup.addModelWithPathURL(modelFolder.toURL());
    }
    myEntities = new NSMutableArray();
    myModel = myGroup.modelNamed(_modelName);
    if (_entityNames == null) {
      Enumeration entitiesEnum = myModel.entities().objectEnumerator();
      while (entitiesEnum.hasMoreElements()) {
        EOEntity entity = (EOEntity) entitiesEnum.nextElement();
        String entityName = entity.name();
        boolean isPrototype = (entityName.startsWith("EO") && entityName.endsWith("Prototypes"));
        if (!isPrototype) {// && entityUsesSeparateTable(entity)) {
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
  }

  public String getSchemaCreationScript() {
    EODatabaseContext dbc = new EODatabaseContext(new EODatabase(myModel));
    EOAdaptorContext ac = dbc.adaptorContext();
    EOSynchronizationFactory sf = ((JDBCAdaptor) ac.adaptor()).plugIn().synchronizationFactory();
    String result = sf.schemaCreationScriptForEntities(myEntities, myFlags);
    return result;
  }

  protected boolean entityUsesSeparateTable(EOEntity _entity) {
    boolean usesSeparateTable = false;
    if (_entity.parentEntity() == null) {
      usesSeparateTable = true;
    }
    EOEntity entity = _entity;
    EOEntity parent = entity.parentEntity();
    while (!usesSeparateTable && parent != null) {
      if (!entity.externalName().equals(parent.externalName())) {
        usesSeparateTable = true;
      }
      else {
        entity = parent;
        parent = entity.parentEntity();
      }
    }
    return usesSeparateTable;
  }

  public static void main(String argv[]) throws MalformedURLException {
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
    EOFSQLGenerator generator1 = new EOFSQLGenerator("Movies", Arrays.asList(paths), null, flags);
    System.out.println("EOFSQLGenerator.main: " + NSBundle.mainBundle());
    System.out.println(generator1.getSchemaCreationScript());
    //
    //    System.out.println("***********************************");
    //
    //    // only movie entity
    //
    //    EOFSQLGenerator generator2 = new EOFSQLGenerator("Movies", Arrays.asList(paths), Arrays.asList(new String[] { "Movie" }), optionsCreate);
    //    System.out.println(generator2.getSchemaCreationScript());
  }
}
