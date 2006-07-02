package org.objectstyle.wolips.eomodeler.model;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EOModelGroup {
  private List myModels;
  private List myPrototypeAttributeCache;

  public EOModelGroup() {
    myModels = new LinkedList();
  }

  public List getModels() {
    return myModels;
  }

  public List getEntityNames() {
    List entityNames = new LinkedList();
    Iterator modelsIter = myModels.iterator();
    while (modelsIter.hasNext()) {
      EOModel model = (EOModel) modelsIter.next();
      Iterator entitiesIter = model.getEntities().iterator();
      while (entitiesIter.hasNext()) {
        EOEntity entity = (EOEntity) entitiesIter.next();
        entityNames.add(entity.getName());
      }
    }
    return entityNames;
  }

  public List getEntities() {
    List entities = new LinkedList();
    Iterator modelsIter = myModels.iterator();
    while (modelsIter.hasNext()) {
      EOModel model = (EOModel) modelsIter.next();
      entities.addAll(model.getEntities());
    }
    return entities;
  }
  
  public List getPrototypeAttributeNames() {
    List prototypeAttributeNames = new LinkedList();
    Iterator prototypeAttributesIter = getPrototypeAttributes().iterator();
    while (prototypeAttributesIter.hasNext()) {
      EOAttribute attribute = (EOAttribute)prototypeAttributesIter.next();
      prototypeAttributeNames.add(attribute.getName());
    }
    return prototypeAttributeNames;
  }
  
  public synchronized List getPrototypeAttributes() {
    if (myPrototypeAttributeCache == null) {
      List prototypeAttributeCache = new LinkedList();

      Set prototypeEntityNames = new HashSet();
      addPrototypeAttributes("EOPrototypes", prototypeEntityNames, prototypeAttributeCache);
      Iterator modelsIter = myModels.iterator();
      while (modelsIter.hasNext()) {
        EOModel model = (EOModel) modelsIter.next();
        String adaptorName = model.getAdaptorName();
        String adaptorPrototypeEntityName = "EO" + adaptorName + "Prototypes";
        addPrototypeAttributes(adaptorPrototypeEntityName, prototypeEntityNames, prototypeAttributeCache);

        // MS: Hardcoded JDBC reference hack ...
        if ("JDBC".equals(adaptorName)) {
          Map connectionDictionary = model.getConnectionDictionary();
          if (connectionDictionary != null) {
            String jdbcUrl = (String) connectionDictionary.get("URL");
            if (jdbcUrl != null) {
              int firstColon = jdbcUrl.indexOf(':');
              int secondColon = jdbcUrl.indexOf(':', firstColon + 1);
              if (firstColon != -1 && secondColon != -1) {
                String driverName = jdbcUrl.substring(firstColon + 1, secondColon);
                String driverPrototypeEntityName = "EOJDBC" + driverName + "Prototypes";
                addPrototypeAttributes(driverPrototypeEntityName, prototypeEntityNames, prototypeAttributeCache);
              }
            }
          }
        }
      }

      // Do we need to support "EOPrototypesToHide" entity?
      myPrototypeAttributeCache = prototypeAttributeCache;
    }
    return myPrototypeAttributeCache;
  }

  protected void addPrototypeAttributes(String _prototypeEntityName, Set _prototypeEntityNames, List _prototypeAttributeCache) {
    if (!_prototypeEntityNames.contains(_prototypeEntityName)) {
      _prototypeEntityNames.add(_prototypeEntityName);
      EOEntity prototypeEntity = getEntityNamed(_prototypeEntityName);
      if (prototypeEntity != null) {
        _prototypeAttributeCache.addAll(prototypeEntity.getAttributes());
      }
    }
  }

  public EOAttribute getPrototypeAttributeNamed(String _name) {
    EOAttribute matchingAttribute = null;
    List prototypeAttributes = getPrototypeAttributes();
    Iterator attributesIter = prototypeAttributes.iterator();
    while (matchingAttribute == null && attributesIter.hasNext()) {
      EOAttribute attribute = (EOAttribute) attributesIter.next();
      if (attribute.getName().equals(_name)) {
        matchingAttribute = attribute;
      }
    }
    return matchingAttribute;
  }

  public EOEntity getEntityNamed(String _entityName) {
    EOEntity matchingEntity = null;
    Iterator modelsIter = myModels.iterator();
    while (matchingEntity == null && modelsIter.hasNext()) {
      EOModel model = (EOModel) modelsIter.next();
      matchingEntity = model.getEntityNamed(_entityName);
    }
    return matchingEntity;
  }

  public boolean containsModelNamed(String _entityName) {
    return getModelNamed(_entityName) != null;
  }

  public void _checkForDuplicateModelName(EOModel _model, String _newName) {
    EOModel entity = getModelNamed(_newName);
    if (entity != null && entity != _model) {
      throw new IllegalArgumentException("There is already an entity named '" + _newName + "' in " + this + ".");
    }
  }

  public void addModel(EOModel _model) {
    if (_model.getModelGroup() != this) {
      throw new IllegalArgumentException("This model is already a member of another model group.");
    }
    _checkForDuplicateModelName(_model, _model.getName());
    myModels.add(_model);
    myPrototypeAttributeCache = null;
  }

  public void removeModel(EOModel _entity) {
    myModels.remove(_entity);
  }

  public EOModel getModelNamed(String _name) {
    EOModel matchingModel = null;
    Iterator modelsIter = myModels.iterator();
    while (matchingModel == null && modelsIter.hasNext()) {
      EOModel model = (EOModel) modelsIter.next();
      if (model.getName().equals(_name)) {
        matchingModel = model;
      }
    }
    return matchingModel;
  }

  public void addModelsFromFolder(File _folder, boolean _recursive) throws IOException {
    File[] files = _folder.listFiles();
    for (int fileNum = 0; fileNum < files.length; fileNum++) {
      String name = files[fileNum].getName();
      if (files[fileNum].isDirectory()) {
        if (name.endsWith(".eomodeld")) {
          String modelName = name.substring(0, name.indexOf('.'));
          if (!containsModelNamed(modelName)) {
            EOModel model = new EOModel(this, modelName);
            model.loadFromFolder(files[fileNum]);
            addModel(model);
          }
        }
        else if (_recursive) {
          addModelsFromFolder(files[fileNum], true);
        }
      }
    }
  }

  public String toString() {
    return "[EOModelGroup: models = " + myModels + "]";
  }
}
