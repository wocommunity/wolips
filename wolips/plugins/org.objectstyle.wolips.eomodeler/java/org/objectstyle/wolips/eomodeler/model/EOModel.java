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
package org.objectstyle.wolips.eomodeler.model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.internal.databinding.provisional.observable.list.WritableList;
import org.eclipse.ui.views.properties.IPropertySource;
import org.objectstyle.cayenne.wocompat.PropertyListSerialization;
import org.objectstyle.wolips.eomodeler.properties.EOModelPropertySource;
import org.objectstyle.wolips.eomodeler.utils.MapUtils;
import org.objectstyle.wolips.eomodeler.utils.NotificationMap;

public class EOModel extends UserInfoableEOModelObject implements IUserInfoable {
  public static final String DIRTY = "dirty"; //$NON-NLS-1$
  public static final String ENTITY = "entity"; //$NON-NLS-1$
  public static final String CONNECTION_DICTIONARY = "connectionDictionary"; //$NON-NLS-1$
  public static final String ADAPTOR_NAME = "adaptorName"; //$NON-NLS-1$
  public static final String VERSION = "version"; //$NON-NLS-1$
  public static final String NAME = "name"; //$NON-NLS-1$
  public static final String ENTITIES = "entities"; //$NON-NLS-1$

  private EOModelGroup myModelGroup;
  private String myName;
  private String myVersion;
  private String myAdaptorName;
  private NotificationMap myConnectionDictionary;
  private List myEntities;
  private List myDeletedEntityNamesInObjectStore;
  private EOModelMap myModelMap;
  private boolean myDirty;
  private PropertyChangeRepeater myConnectionDictionaryRepeater;

  public EOModel(EOModelGroup _modelGroup, String _name) {
    myModelGroup = _modelGroup;
    myName = _name;
    myEntities = new WritableList(EOEntity.class);
    myDeletedEntityNamesInObjectStore = new WritableList(String.class);
    myVersion = "2.1"; //$NON-NLS-1$
    myModelMap = new EOModelMap();
    myConnectionDictionaryRepeater = new PropertyChangeRepeater(EOModel.CONNECTION_DICTIONARY);
    setConnectionDictionary(new NotificationMap(), false);
  }

  public EOEntity addBlankEntity(String _name) throws DuplicateEntityNameException {
    String newEntityNameBase = _name;
    String newEntityName = newEntityNameBase;
    int newEntityNum = 0;
    while (getEntityNamed(newEntityName) != null) {
      newEntityNum++;
      newEntityName = newEntityNameBase + newEntityNum;
    }
    EOEntity entity = new EOEntity(this, newEntityName);
    addEntity(entity);
    return entity;
  }

  public boolean isDirty() {
    return myDirty;
  }

  public void setDirty(boolean _dirty) {
    Boolean oldDirty = Boolean.valueOf(myDirty);
    myDirty = _dirty;
    firePropertyChange(EOModel.DIRTY, oldDirty, Boolean.valueOf(myDirty));
  }

  protected void firePropertyChange(String _propertyName, Object _oldValue, Object _newValue) {
    super.firePropertyChange(_propertyName, _oldValue, _newValue);
  }

  protected void _propertyChanged(String _propertyName, Object _oldValue, Object _newValue) {
    if (!myDirty && !EOModel.DIRTY.equals(_propertyName)) {
      setDirty(true);
    }
  }

  protected void _entityChanged(EOEntity _entity) {
    firePropertyChange(EOModel.ENTITY, null, _entity);
  }

  public Object getAdapter(Class _adapter) {
    Object adapter = null;
    if (_adapter == IPropertySource.class) {
      adapter = new EOModelPropertySource(this);
    }
    return adapter;
  }

  public int hashCode() {
    return myName.hashCode();
  }

  public boolean equals(Object _obj) {
    return (_obj instanceof EOModel && ((EOModel) _obj).myName.equals(myName));
  }

  public EOModelGroup getModelGroup() {
    return myModelGroup;
  }

  public String getAdaptorName() {
    return myAdaptorName;
  }

  public void setAdaptorName(String _adaptorName) {
    String oldAdaptorName = myAdaptorName;
    myAdaptorName = _adaptorName;
    firePropertyChange(EOModel.ADAPTOR_NAME, _adaptorName, oldAdaptorName);
  }

  public String getVersion() {
    return myVersion;
  }

  public void setVersion(String _version) {
    String oldVersion = myVersion;
    myVersion = _version;
    firePropertyChange(EOModel.VERSION, _version, oldVersion);
  }

  public void setName(String _name) {
    String oldName = myName;
    myName = _name;
    firePropertyChange(EOModel.NAME, _name, oldName);
  }

  public String getName() {
    return myName;
  }

  public List getEntities() {
    return myEntities;
  }

  public void _checkForDuplicateEntityName(EOEntity _entity, String _newName, Set _failures) throws DuplicateEntityNameException {
    EOEntity entity = getModelGroup().getEntityNamed(_newName);
    if (entity != null && entity != _entity) {
      throw new DuplicateEntityNameException(_newName, this);
    }
  }

  public void _entityNameChanged(String _oldName) {
    if (myDeletedEntityNamesInObjectStore == null) {
      myDeletedEntityNamesInObjectStore = new LinkedList();
    }
    myDeletedEntityNamesInObjectStore.add(_oldName);
  }

  public boolean containsEntityNamed(String _entityName) {
    return getEntityNamed(_entityName) != null;
  }

  public void addEntity(EOEntity _entity) throws DuplicateEntityNameException {
    addEntity(_entity, true, null);
  }

  public void addEntity(EOEntity _entity, boolean _fireEvents, Set _failures) throws DuplicateEntityNameException {
    _checkForDuplicateEntityName(_entity, _entity.getName(), _failures);
    myEntities.add(_entity);
    if (_fireEvents) {
      firePropertyChange(EOModel.ENTITIES, null, null);
    }
  }

  public void removeEntity(EOEntity _entity) {
    myEntities.remove(_entity);
    firePropertyChange(EOModel.ENTITIES, null, null);
  }

  public EOEntity getEntityNamed(String _name) {
    EOEntity matchingEntity = null;
    Iterator entitiesIter = myEntities.iterator();
    while (matchingEntity == null && entitiesIter.hasNext()) {
      EOEntity entity = (EOEntity) entitiesIter.next();
      if (entity.getName().equals(_name)) {
        matchingEntity = entity;
      }
    }
    return matchingEntity;
  }

  public void setConnectionDictionary(Map _connectionDictionary) {
    setConnectionDictionary(_connectionDictionary, true);
  }

  public void setConnectionDictionary(Map _connectionDictionary, boolean _fireEvents) {
    myConnectionDictionary = mapChanged(myConnectionDictionary, _connectionDictionary, myConnectionDictionaryRepeater, _fireEvents);
  }

  public NotificationMap getConnectionDictionary() {
    return myConnectionDictionary;
  }

  public void loadFromFolder(File _modelFolder, boolean _resolveImmediately, Set _failures) throws EOModelException, IOException {
    File indexFile = new File(_modelFolder, "index.eomodeld"); //$NON-NLS-1$
    if (!indexFile.exists()) {
      throw new EOModelException(indexFile + " does not exist.");
    }
    EOModelMap modelMap = new EOModelMap((Map) PropertyListSerialization.propertyListFromFile(indexFile));
    myModelMap = modelMap;
    Object version = modelMap.get("EOModelVersion"); //$NON-NLS-1$
    if (version instanceof String) {
      myVersion = (String) version;
    }
    else if (version instanceof Number) {
      myVersion = String.valueOf(((Number) version).floatValue());
    }
    else {
      throw new IllegalArgumentException("Unknown version format:" + version);
    }
    myAdaptorName = modelMap.getString("adaptorName", true); //$NON-NLS-1$
    setConnectionDictionary(modelMap.getMap("connectionDictionary", true), false); //$NON-NLS-1$
    setUserInfo(MapUtils.toStringMap(modelMap.getMap("userInfo", true)), false); //$NON-NLS-1$

    List entities = modelMap.getList("entities"); //$NON-NLS-1$
    if (entities != null) {
      Iterator entitiesIter = entities.iterator();
      while (entitiesIter.hasNext()) {
        EOModelMap entityMap = new EOModelMap((Map) entitiesIter.next());
        String entityName = entityMap.getString("name", true); //$NON-NLS-1$
        EOEntity entity = new EOEntity(this);
        File entityFile = new File(_modelFolder, entityName + ".plist"); //$NON-NLS-1$
        File fspecFile = new File(_modelFolder, entityName + ".fspec"); //$NON-NLS-1$
        entity.loadFromFile(entityFile, fspecFile, _failures);
        addEntity(entity, false, _failures);
      }
    }

    Map internalInfoMap = modelMap.getMap("internalInfo"); //$NON-NLS-1$
    if (internalInfoMap != null) {
      myDeletedEntityNamesInObjectStore = modelMap.getList("_deletedEntityNamesInObjectStore", true); //$NON-NLS-1$
    }

    if (_resolveImmediately) {
      resolve(_failures);
    }
  }

  public EOModelMap toMap() {
    EOModelMap modelMap = myModelMap.cloneModelMap();
    modelMap.setString("EOModelVersion", myVersion, true); //$NON-NLS-1$
    modelMap.setString("adaptorName", myAdaptorName, true); //$NON-NLS-1$
    modelMap.put("connectionDictionary", myConnectionDictionary); //$NON-NLS-1$

    List entities = new LinkedList();
    List entitiesWithSharedObjects = new LinkedList();
    Iterator entitiesIter = myEntities.iterator();
    while (entitiesIter.hasNext()) {
      EOEntity entity = (EOEntity) entitiesIter.next();
      EOModelMap entityMap = new EOModelMap();
      entityMap.setString("className", entity.getClassName(), true); //$NON-NLS-1$
      EOEntity parent = entity.getParent();
      String parentName = (parent == null) ? null : parent.getName();
      entityMap.setString("parent", parentName, true); //$NON-NLS-1$
      entityMap.setString("name", entity.getName(), true); //$NON-NLS-1$
      entities.add(entityMap);
      if (entity.hasSharedObjects()) {
        entitiesWithSharedObjects.add(entity.getName());
      }
    }
    modelMap.put("entities", entities); //$NON-NLS-1$

    modelMap.setList("entitiesWithSharedObjects", entitiesWithSharedObjects, true); //$NON-NLS-1$

    Map internalInfoMap = modelMap.getMap("internalInfo"); //$NON-NLS-1$
    if (internalInfoMap == null) {
      internalInfoMap = new HashMap();
      modelMap.put("internalInfo", internalInfoMap); //$NON-NLS-1$
    }
    if (myDeletedEntityNamesInObjectStore != null) {
      internalInfoMap.put("_deletedEntityNamesInObjectStore", myDeletedEntityNamesInObjectStore); //$NON-NLS-1$
    }

    modelMap.put("userInfo", getUserInfo()); //$NON-NLS-1$

    return modelMap;
  }

  public void saveToFolder(File _parentFolder) throws IOException {
    File modelFolder = new File(_parentFolder, myName + ".eomodeld"); //$NON-NLS-1$
    if (!modelFolder.exists()) {
      if (!modelFolder.mkdirs()) {
        throw new IOException("Failed to create folder '" + modelFolder + "'.");
      }
    }
    File indexFile = new File(modelFolder, "index.eomodeld"); //$NON-NLS-1$
    EOModelMap modelMap = toMap();
    PropertyListSerialization.propertyListToFile(indexFile, modelMap);

    if (myDeletedEntityNamesInObjectStore != null) {
      Iterator deletedEntityNameIter = myDeletedEntityNamesInObjectStore.iterator();
      while (deletedEntityNameIter.hasNext()) {
        String entityName = (String) deletedEntityNameIter.next();
        File entityFile = new File(modelFolder, entityName + ".plist"); //$NON-NLS-1$
        entityFile.delete();
        File fspecFile = new File(modelFolder, entityName + ".fspec"); //$NON-NLS-1$
        fspecFile.delete();
      }
    }

    Iterator entitiesIter = myEntities.iterator();
    while (entitiesIter.hasNext()) {
      EOEntity entity = (EOEntity) entitiesIter.next();
      String entityName = entity.getName();
      File entityFile = new File(modelFolder, entityName + ".plist"); //$NON-NLS-1$
      File fspecFile = new File(modelFolder, entityName + ".fspec"); //$NON-NLS-1$
      entity.saveToFile(entityFile, fspecFile);
    }
  }

  public void resolve(Set _failures) {
    Iterator entitiesIter = myEntities.iterator();
    while (entitiesIter.hasNext()) {
      EOEntity entity = (EOEntity) entitiesIter.next();
      entity.resolve(_failures);
    }
  }

  public void verify(Set _failures) {
    // TODO

    Iterator entitiesIter = myEntities.iterator();
    while (entitiesIter.hasNext()) {
      EOEntity entity = (EOEntity) entitiesIter.next();
      entity.verify(_failures);
    }
  }

  public String toString() {
    return "[EOModel: name = " + myName + "; entities = " + myEntities + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

  public static void main(String[] args) throws IOException, EOModelException {
    Set failures = new LinkedHashSet();

    EOModelGroup modelGroup = new EOModelGroup();
    modelGroup.addModelsFromFolder(new File("/Library/Frameworks/ERPrototypes.framework/Resources"), false, failures); //$NON-NLS-1$
    modelGroup.addModelsFromFolder(new File("/Users/mschrag/Documents/workspace/MDTask"), false, failures); //$NON-NLS-1$
    modelGroup.addModelsFromFolder(new File("/Users/mschrag/Documents/workspace/MDTAccounting"), false, failures); //$NON-NLS-1$
    modelGroup.addModelsFromFolder(new File("/Users/mschrag/Documents/workspace/MDTCMS"), false, failures); //$NON-NLS-1$
    modelGroup.addModelsFromFolder(new File("/Users/mschrag/Documents/workspace/MDTWOExtensions"), false, failures); //$NON-NLS-1$

    modelGroup.resolve(failures);
    modelGroup.verify(failures);
    Iterator failuresIter = failures.iterator();
    while (failuresIter.hasNext()) {
      EOModelVerificationFailure failure = (EOModelVerificationFailure) failuresIter.next();
      System.out.println("EOModel.main: " + failure); //$NON-NLS-1$
    }

    File outputPath = new File("/tmp"); //$NON-NLS-1$
    System.out.println("EOModel.main: Saving model to " + outputPath + " ..."); //$NON-NLS-1$ //$NON-NLS-2$
    EOModel mdtaskModel = modelGroup.getModelNamed("MDTask"); //$NON-NLS-1$
    mdtaskModel.saveToFolder(outputPath);
    System.out.println("EOModel.main: Done."); //$NON-NLS-1$
  }
}
