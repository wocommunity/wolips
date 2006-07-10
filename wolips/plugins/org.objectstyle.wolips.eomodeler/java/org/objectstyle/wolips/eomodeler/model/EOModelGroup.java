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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.internal.databinding.provisional.observable.list.WritableList;

public class EOModelGroup extends EOModelObject {
  public static final String MODELS = "models"; //$NON-NLS-1$

  private List myModels;
  private List myPrototypeAttributeCache;

  public EOModelGroup() {
    myModels = new WritableList(EOModel.class);
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
      EOAttribute attribute = (EOAttribute) prototypeAttributesIter.next();
      prototypeAttributeNames.add(attribute.getName());
    }
    return prototypeAttributeNames;
  }

  public synchronized List getPrototypeAttributes() {
    if (myPrototypeAttributeCache == null) {
      List prototypeAttributeCache = new LinkedList();

      Set prototypeEntityNames = new HashSet();
      addPrototypeAttributes("EOPrototypes", prototypeEntityNames, prototypeAttributeCache); //$NON-NLS-1$
      Iterator modelsIter = myModels.iterator();
      while (modelsIter.hasNext()) {
        EOModel model = (EOModel) modelsIter.next();
        String adaptorName = model.getAdaptorName();
        String adaptorPrototypeEntityName = "EO" + adaptorName + "Prototypes"; //$NON-NLS-1$ //$NON-NLS-2$
        addPrototypeAttributes(adaptorPrototypeEntityName, prototypeEntityNames, prototypeAttributeCache);

        // MS: Hardcoded JDBC reference hack ...
        if ("JDBC".equals(adaptorName)) { //$NON-NLS-1$
          Map connectionDictionary = model.getConnectionDictionary();
          if (connectionDictionary != null) {
            String jdbcUrl = (String) connectionDictionary.get("URL"); //$NON-NLS-1$
            if (jdbcUrl != null) {
              int firstColon = jdbcUrl.indexOf(':');
              int secondColon = jdbcUrl.indexOf(':', firstColon + 1);
              if (firstColon != -1 && secondColon != -1) {
                String driverName = jdbcUrl.substring(firstColon + 1, secondColon);
                String driverPrototypeEntityName = "EOJDBC" + driverName + "Prototypes"; //$NON-NLS-1$ //$NON-NLS-2$
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
    firePropertyChange(EOModelGroup.MODELS, null, null);
  }

  public void removeModel(EOModel _entity) {
    myModels.remove(_entity);
    firePropertyChange(EOModelGroup.MODELS, null, null);
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

  public void addModelsFromFolder(File _folder, boolean _resolveImmediately, Set _failures) throws IOException, EOModelException {
    addModelsFromFolder(_folder, true, _resolveImmediately, _failures);
  }

  public void addModelsFromFolder(File _folder, boolean _recursive, boolean _resolveImmediately, Set _failures) throws IOException, EOModelException {
    File[] files = _folder.listFiles();
    for (int fileNum = 0; fileNum < files.length; fileNum++) {
      String name = files[fileNum].getName();
      if (files[fileNum].isDirectory()) {
        if (name.endsWith(".eomodeld")) { //$NON-NLS-1$
          String modelName = name.substring(0, name.indexOf('.'));
          if (!containsModelNamed(modelName)) {
            EOModel model = new EOModel(this, modelName);
            model.loadFromFolder(files[fileNum], false, _failures);
            addModel(model);
          }
        }
        else if (_recursive) {
          addModelsFromFolder(files[fileNum], _recursive, _resolveImmediately, _failures);
        }
      }
    }
  }

  public void verify(Set _failures) {
    Iterator modelsIter = myModels.iterator();
    while (modelsIter.hasNext()) {
      EOModel model = (EOModel) modelsIter.next();
      model.verify(_failures);
    }
  }

  public void resolve(Set _failures) {
    Iterator modelsIter = myModels.iterator();
    while (modelsIter.hasNext()) {
      EOModel model = (EOModel) modelsIter.next();
      model.resolve(_failures);
    }
  }

  public String toString() {
    return "[EOModelGroup: models = " + myModels + "]"; //$NON-NLS-1$ //$NON-NLS-2$
  }
}
