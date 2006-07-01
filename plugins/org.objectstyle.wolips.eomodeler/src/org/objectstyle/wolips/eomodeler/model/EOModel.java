package org.objectstyle.wolips.eomodeler.model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.objectstyle.cayenne.wocompat.PropertyListSerialization;

public class EOModel {
  private EOModelGroup myModelGroup;
  private String myName;
  private String myVersion;
  private String myAdaptorName;
  private Map myConnectionDictionary;
  private List myEntities;
  private List myDeletedEntityNamesInObjectStore;
  private EOModelMap myModelMap;
  private Map myUserInfo;

  public EOModel(EOModelGroup _modelGroup, String _name) {
    myModelGroup = _modelGroup;
    myName = _name;
    myEntities = new LinkedList();
    myDeletedEntityNamesInObjectStore = new LinkedList();
    myVersion = "2.1";
    myModelMap = new EOModelMap();
  }

  public EOModelGroup getModelGroup() {
    return myModelGroup;
  }

  public String getAdaptorName() {
    return myAdaptorName;
  }

  public void setAdaptorName(String _adaptorName) {
    myAdaptorName = _adaptorName;
  }

  public String getVersion() {
    return myVersion;
  }

  public void setVersion(String _version) {
    myVersion = _version;
  }

  public void setName(String _name) {
    myName = _name;
  }

  public List getEntities() {
    return myEntities;
  }

  public String getName() {
    return myName;
  }

  public void _checkForDuplicateEntityName(EOEntity _entity, String _newName) {
    EOEntity entity = getModelGroup().getEntityNamed(_newName);
    if (entity != null && entity != _entity) {
      throw new IllegalArgumentException("There is already an entity named '" + _newName + "' in " + this + ".");
    }
  }

  public void _entityNameChanged(String _oldName, String _newName) {
    if (myDeletedEntityNamesInObjectStore == null) {
      myDeletedEntityNamesInObjectStore = new LinkedList();
    }
    myDeletedEntityNamesInObjectStore.add(_oldName);
  }

  public boolean containsEntityNamed(String _entityName) {
    return getEntityNamed(_entityName) != null;
  }

  public void addEntity(EOEntity _entity) {
    _checkForDuplicateEntityName(_entity, _entity.getName());
    myEntities.add(_entity);
  }

  public void removeEntity(EOEntity _entity) {
    myEntities.remove(_entity);
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
    myConnectionDictionary = _connectionDictionary;
  }

  public Map getConnectionDictionary() {
    return myConnectionDictionary;
  }

  public void setUserInfo(Map _userInfo) {
    myUserInfo = _userInfo;
  }

  public Map getUserInfo() {
    return myUserInfo;
  }

  public void loadFromFolder(File _modelFolder) throws IOException {
    File indexFile = new File(_modelFolder, "index.eomodeld");
    if (!indexFile.exists()) {
      throw new IOException(indexFile + " does not exist.");
    }
    EOModelMap modelMap = new EOModelMap((Map) PropertyListSerialization.propertyListFromFile(indexFile));
    myModelMap = modelMap;
    myVersion = modelMap.getString("EOModelVersion", true);
    myAdaptorName = modelMap.getString("adaptorName", true);
    myConnectionDictionary = modelMap.getMap("connectionDictionary", true);
    myUserInfo = modelMap.getMap("userInfo", true);

    List entities = modelMap.getList("entities");
    if (entities != null) {
      Iterator entitiesIter = entities.iterator();
      while (entitiesIter.hasNext()) {
        EOModelMap entityMap = new EOModelMap((Map) entitiesIter.next());
        String entityName = entityMap.getString("name", true);
        EOEntity entity = new EOEntity(this);
        File entityFile = new File(_modelFolder, entityName + ".plist");
        File fspecFile = new File(_modelFolder, entityName + ".fspec");
        entity.loadFromFile(entityFile, fspecFile);
        addEntity(entity);
      }
    }

    Map internalInfoMap = modelMap.getMap("internalInfo");
    if (internalInfoMap != null) {
      myDeletedEntityNamesInObjectStore = modelMap.getList("_deletedEntityNamesInObjectStore", true);
    }
  }

  public EOModelMap toMap() {
    EOModelMap modelMap = myModelMap.cloneModelMap();
    modelMap.setString("EOModelVersion", myVersion, true);
    modelMap.setString("adaptorName", myAdaptorName, true);
    modelMap.put("connectionDictionary", myConnectionDictionary);

    List entities = new LinkedList();
    Iterator entitiesIter = myEntities.iterator();
    while (entitiesIter.hasNext()) {
      EOEntity entity = (EOEntity) entitiesIter.next();
      EOModelMap entityMap = new EOModelMap();
      entityMap.setString("className", entity.getClassName(), true);
      EOEntity parent = entity.getParent();
      String parentName = (parent == null) ? null : parent.getName();
      entityMap.setString("parent", parentName, true);
      entityMap.setString("name", entity.getName(), true);
      entities.add(entityMap);
    }
    modelMap.put("entities", entities);

    Map internalInfoMap = modelMap.getMap("internalInfo");
    if (internalInfoMap == null) {
      internalInfoMap = new HashMap();
      modelMap.put("internalInfo", internalInfoMap);
    }
    if (myDeletedEntityNamesInObjectStore != null) {
      internalInfoMap.put("_deletedEntityNamesInObjectStore", myDeletedEntityNamesInObjectStore);
    }

    modelMap.put("userInfo", myUserInfo);

    return modelMap;
  }

  public void saveToFolder(File _parentFolder) throws IOException {
    File modelFolder = new File(_parentFolder, myName + ".eomodeld");
    if (!modelFolder.exists()) {
      if (!modelFolder.mkdirs()) {
        throw new IOException("Failed to create folder '" + modelFolder + "'.");
      }
    }
    File indexFile = new File(modelFolder, "index.eomodeld");
    EOModelMap modelMap = toMap();
    PropertyListSerialization.propertyListToFile(indexFile, modelMap);

    if (myDeletedEntityNamesInObjectStore != null) {
      Iterator deletedEntityNameIter = myDeletedEntityNamesInObjectStore.iterator();
      while (deletedEntityNameIter.hasNext()) {
        String entityName = (String) deletedEntityNameIter.next();
        File entityFile = new File(modelFolder, entityName + ".plist");
        entityFile.delete();
        File fspecFile = new File(modelFolder, entityName + ".fspec");
        fspecFile.delete();
      }
    }

    Iterator entitiesIter = myEntities.iterator();
    while (entitiesIter.hasNext()) {
      EOEntity entity = (EOEntity) entitiesIter.next();
      String entityName = entity.getName();
      File entityFile = new File(modelFolder, entityName + ".plist");
      File fspecFile = new File(modelFolder, entityName + ".fspec");
      entity.saveToFile(entityFile, fspecFile);
    }
  }

  public void verify(List _failures) {
    // TODO

    Iterator entitiesIter = myEntities.iterator();
    while (entitiesIter.hasNext()) {
      EOEntity entity = (EOEntity) entitiesIter.next();
      entity.verify(_failures);
    }
  }

  public String toString() {
    return "[EOModel: name = " + myName + "; entities = " + myEntities + "]";
  }

  public static void main(String[] args) throws IOException {
    EOModelGroup modelGroup = new EOModelGroup();
    modelGroup.addModelsFromFolder(new File("/Library/Frameworks/ERPrototypes.framework/Resources"), false);
    modelGroup.addModelsFromFolder(new File("/Users/mschrag/Documents/workspace/MDTask"), false);
    modelGroup.addModelsFromFolder(new File("/Users/mschrag/Documents/workspace/MDTAccounting"), false);
    modelGroup.addModelsFromFolder(new File("/Users/mschrag/Documents/workspace/MDTCMS"), false);
    modelGroup.addModelsFromFolder(new File("/Users/mschrag/Documents/workspace/MDTWOExtensions"), false);
    EOModel mdtaskModel = modelGroup.getModelNamed("MDTask");
    System.out.println("EOModel.main: Loaded model: " + modelGroup);

    List failures = new LinkedList();
    mdtaskModel.verify(failures);
    Iterator failuresIter = failures.iterator();
    while (failuresIter.hasNext()) {
      EOModelVerificationFailure failure = (EOModelVerificationFailure) failuresIter.next();
      System.out.println("EOModel.main: " + failure);
    }

    File outputPath = new File("/tmp");
    System.out.println("EOModel.main: Saving model to " + outputPath + " ...");
    mdtaskModel.saveToFolder(outputPath);
    System.out.println("EOModel.main: Done.");
  }
}
