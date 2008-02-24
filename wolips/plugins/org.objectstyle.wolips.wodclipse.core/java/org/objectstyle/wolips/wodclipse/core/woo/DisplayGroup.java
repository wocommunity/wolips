/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2005 The ObjectStyle Group,
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne"
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */

package org.objectstyle.wolips.wodclipse.core.woo;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOFetchSpecification;
import org.objectstyle.wolips.eomodeler.core.model.EOModelMap;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;
import org.objectstyle.wolips.eomodeler.core.model.EOSortOrdering;
import org.objectstyle.wolips.eomodeler.core.utils.IPropertyChangeSource;

public class DisplayGroup implements IPropertyChangeSource {

  public static final String NAME = "name";

  public static final String QUALIFICATION_INDEX = "qualificationIndex";

  public static final String QUALIFICATION_LIST = "qualificationList";

  public static final String ENTITY_NAME = "entityName";

  public static final String ENTITY_LIST = "entityList";

  public static final String MASTER_ENTITY_NAME = "masterEntityName";

  public static final String DETAIL_KEY_NAME = "detailKeyName";

  public static final String DETAIL_KEY_LIST = "detailKeyList";

  public static final String SORT_LIST = "sortList";

  public static final String HAS_MASTER_DETAIL = "hasMasterDetail";

  public static final String SORT_ORDER = "sortOrder";

  public static final String SORT_ORDER_KEY = "sortOrderKey";

  public static final String FETCH_SPEC_LIST = "fetchSpecList";

  public static final String FETCH_SPEC_NAME = "fetchSpecName";

  public static final String ENTRIES_PER_BATCH = "entriesPerBatch";

  public static final String FETCHES_ON_LOAD = "fetchesOnLoad";

  public static final String SELECTS_FIRST_OBJECT = "selectsFirstObject";

  public static final String EDITING_CONTEXT = "editingContext";

  public static final String ASCENDING = "Ascending";

  public static final String DESCENDING = "Descending";

  public static final String NOT_SORTED = "Not Sorted";

  public static final String FETCH_SPEC_NONE = "<None>";

  public static final String[] SORT_OPTIONS = new String[] { ASCENDING, DESCENDING, NOT_SORTED, };

  private static final String QUALIFIER_PREFIX = "%@*";

  private static final String QUALIFIER_SUFFIX = "*%@";

  private static final String QUALIFIER_CONTAINS = "*%@*";

  private static final String[] QUALIFICATION_LABELS = new String[] { "Prefix", "Contains", "Suffix", };

  private static final String[] QUALIFICATION_FORMATS = new String[] { QUALIFIER_PREFIX, QUALIFIER_CONTAINS, QUALIFIER_SUFFIX, };

  private String _name = "newDisplayGroup";
  private WooModel _wooModel;
  private int _qualificationIndex;
  private List<String> _entityList;
  private EOEntity _entity;
  private String _entityName;
  private EOEntity _masterEntity;
  private String _masterEntityName;
  private String _detailKeyName;
  private boolean _hasMasterDetail;
  private boolean _fetchesOnLoad;
  private String _flass;
  private String _qualifierFormat;
  private int _entriesPerBatch;
  private List<String> _localKeys;
  private boolean _selectsFirstObject;

  private PropertyChangeSupport _changeSupport;

  private EODataSource _dataSource;
  private EODatabaseDataSource _databaseDataSource;
  private EODetailDataSource _detailDataSource;
  private EOSortOrdering _sortOrder;
  private boolean _isSorted;

  protected DisplayGroup(final WooModel model) {
    _wooModel = model;
    _wooModel.getModelGroup();
    _databaseDataSource = new EODatabaseDataSource(_wooModel.getModelGroup());
    _detailDataSource = new EODetailDataSource(_wooModel.getModelGroup());
    _dataSource = _databaseDataSource;
    _qualificationIndex = 0;
    _qualifierFormat = QUALIFICATION_FORMATS[0];
    _flass = "WODisplayGroup";
    _isSorted = false;
    _hasMasterDetail = false;
    _changeSupport = new PropertyChangeSupport(this);
    _sortOrder = new EODisplayGroupSortOrdering();
    _selectsFirstObject = false;
  }

  public void addPropertyChangeListener(final PropertyChangeListener listener) {
    _changeSupport.addPropertyChangeListener(listener);
  }

  public void addPropertyChangeListener(final String name, final PropertyChangeListener listener) {
    _changeSupport.addPropertyChangeListener(name, listener);

  }

  protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
    if (oldValue != newValue || (oldValue != null && !oldValue.equals(newValue)) || (newValue != null && !newValue.equals(oldValue))) {
      _wooModel.markAsDirty();
      _changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
  }

  public List<String> getDetailKeyList() {
    if (_masterEntity != null) {
      List<String> keyList = new ArrayList<String>();
      Set<EORelationship> relationships = _masterEntity.getRelationships();
      for (EORelationship relation : relationships) {
        if (relation.getToMany() != null && relation.getToMany().booleanValue()) {
          keyList.add(relation.getName());
        }
      }
      return keyList;
    }
    return null;
  }

  public String getDetailKeyName() {
    return _detailKeyName;
  }

  public String getEditingContext() {
    return _databaseDataSource.getEditingContext();
  }

  public EOEntity getEntity() {
    return _entity;
  }

  public List<String> getEntityList() {
    if (_entityList == null) {
      _entityList = new ArrayList<String>(_wooModel.getModelGroup().getNonPrototypeEntityNames());
    }
    return _entityList;
  }

  public String getEntityName() {
    return _entityName;
  }

  public int getEntriesPerBatch() {
    return _entriesPerBatch;
  }

  public boolean getFetchesOnLoad() {
    return _fetchesOnLoad;
  }

  public List<String> getFetchSpecList() {
    if (_entity != null) {
      try {
        Set<EOFetchSpecification> fetchSpecs = _entity.getSortedFetchSpecs();
        if (fetchSpecs.size() > 0) {
          List<String> fetchSpecList = new ArrayList<String>(fetchSpecs.size() + 1);
          fetchSpecList.add(FETCH_SPEC_NONE);

          for (EOFetchSpecification fspec : fetchSpecs) {
            fetchSpecList.add(fspec.getName());
          }
          return fetchSpecList;
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  public String getFetchSpecName() {
    String name = _databaseDataSource.getFetchSpecification().getName();
    return name;
  }

  public List<String> getLocalKeys() {
    return _localKeys;
  }

  public EOEntity getMasterEntity() {
    return _masterEntity;
  }

  public String getMasterEntityName() {
    return _masterEntityName;
  }

  public String getName() {
    return _name;
  }

  public int getQualificationIndex() {
    return _qualificationIndex;
  }

  public String[] getQualificationList() {
    return QUALIFICATION_LABELS.clone();
  }

  public boolean getSelectsFirstObject() {
    return _selectsFirstObject;
  }

  public List<String> getSortList() {
    if (_entity != null) {
      Set<EOAttribute> attributes = _entity.getSortedClassAttributes();
      List<String> attribList = new ArrayList<String>(attributes.size());
      for (EOAttribute attribute : attributes) {
        attribList.add(attribute.getName());
      }
      return attribList;
    }
    return new ArrayList<String>();
  }

  public String getSortOrder() {
    String order = null;
    if (!_isSorted) {
      order = NOT_SORTED;
    }
    else {
      String selector = _sortOrder.getSelectorName();
      if (selector.equals(EOSortOrdering.SELECTOR_ASCENDING)) {
        order = ASCENDING;
      }
      if (selector.equals(EOSortOrdering.SELECTOR_DESCENDING)) {
        order = DESCENDING;
      }
    }
    return order;
  }

  public String getSortOrderKey() {
    return _sortOrder.getKey();
  }

  public WooModel getWooModel() {
    return _wooModel;
  }

  public boolean isHasMasterDetail() {
    return _hasMasterDetail;
  }

  @SuppressWarnings("unchecked")
  public void loadFromMap(final EOModelMap map, final Set<EOModelVerificationFailure> failures) {
    _flass = map.getString("class", true);
    _localKeys = map.getList("localKeys");
    if (map.containsKey("numberOfObjectsPerBatch")) {
      _entriesPerBatch = map.getInteger("numberOfObjectsPerBatch");
    }
    if (map.containsKey("selectsFirstObjectAfterFetch")) {
      _selectsFirstObject = map.getBoolean("selectsFirstObjectAfterFetch");
    }
    if (map.containsKey("fetchesOnLoad")) {
      _fetchesOnLoad = map.getBoolean("fetchesOnLoad");
    }
    _qualifierFormat = map.getString("formatForLikeQualifier", true);

    List<String> qualificationFormatList = Arrays.asList(QUALIFICATION_FORMATS);
    _qualificationIndex = qualificationFormatList.indexOf(_qualifierFormat);

    if (map.containsKey("dataSource")) {
      EOModelMap dataSourceMap = new EOModelMap(map.getMap("dataSource"));
      EODataSource dataSource = EODataSourceFactory.createDataSourceFromMap(dataSourceMap, _wooModel.getModelGroup());
      dataSource.loadFromMap(dataSourceMap, failures);
      _dataSource = dataSource;

      if (_dataSource instanceof EODetailDataSource) {
        EODetailDataSource ds = (EODetailDataSource) _dataSource;
        _hasMasterDetail = true;
        setMasterEntityName(ds.getMasterClass());
        setDetailKeyName(ds.getDetailKey());
        _detailDataSource = ds;
      }
      else if (_dataSource instanceof EODatabaseDataSource) {
        EODatabaseDataSource ds = (EODatabaseDataSource) _dataSource;
        _hasMasterDetail = false;
        setEntityName(ds.getEntityName());
        _databaseDataSource = ds;
      }

      if (_entityName == null) {
        // XXX Invalid display group
        if (dataSourceMap.containsKey("fetchSpecification")) {
          EOModelMap fetchSpecMap = new EOModelMap(dataSourceMap.getMap("fetchSpecification"));
          _entityName = fetchSpecMap.getString("entityName", true);
        }
        else if (dataSourceMap.containsKey("masterClassDescription")) {
          _entityName = dataSourceMap.getString("masterClassDescription", true);
        }
      }
    }

    List<Map<Object, Object>> sortOrderList = map.getList("sortOrdering");
    if (sortOrderList != null) {
      for (Map<Object, Object> sortOrdering : sortOrderList) {
        if (sortOrdering != null) {
          EOModelMap sortOrderingMap = new EOModelMap(sortOrdering);
          _sortOrder.loadFromMap(sortOrderingMap);
          _isSorted = true;
        }
      }
    }
  }

  public void removePropertyChangeListener(final PropertyChangeListener listener) {
    _changeSupport.removePropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(final String name, final PropertyChangeListener listener) {
    _changeSupport.removePropertyChangeListener(name, listener);
  }

  public void setDetailKeyName(final String key) {
    String oldDetailKeyName = _detailKeyName;
    _detailKeyName = key;
    if (key != null && isHasMasterDetail()) {
      EORelationship relation = _masterEntity.getRelationshipNamed(key);
      if (relation != null) {
        setEntity(relation.getDestination().getEntity());
      }
      else {
        setEntity(_masterEntity);
      }
    }

    _detailDataSource.setDetailKey(_detailKeyName);
    firePropertyChange(DETAIL_KEY_NAME, oldDetailKeyName, _detailKeyName);
  }

  public void setEditingContext(final String ec) {
    String oldEditingContext = _databaseDataSource.getEditingContext();
    _databaseDataSource.setEditingContext(ec);
    firePropertyChange(EDITING_CONTEXT, oldEditingContext, _databaseDataSource.getEditingContext());
  }

  private void setEntity(final EOEntity entity) {
    if (entity == null || !entity.equals(_entity)) {
      List<String> oldFetchSpecList = getFetchSpecList();
      List<String> oldSortList = getSortList();

      _entity = entity;
      if (_entity != null) {
        setEntityName(_entity.getName());
      }
      if (!isHasMasterDetail()) {
        setMasterEntity(entity);
      }

      _databaseDataSource.getFetchSpecification().setEntity(entity);
      firePropertyChange(FETCH_SPEC_LIST, oldFetchSpecList, getFetchSpecList());
      setFetchSpecName(null);

      firePropertyChange(SORT_LIST, oldSortList, getSortList());
      setSortOrderKey(null);
    }
  }

  public void setEntityName(final String entity) {
    String oldEntityName = _entityName;
    if (entity == null || !entity.equals(_entityName)) {
      _entityName = entity;
      firePropertyChange(ENTITY_NAME, oldEntityName, _entityName);
      try {
        EOEntity eoentity = _wooModel.getModelGroup().getEntityNamed(entity);
        setEntity(eoentity);
      }
      catch (Exception e) {
        e.printStackTrace();
        setEntity(null);
      }
    }
  }

  public void setEntriesPerBatch(final int entriesPerBatch) {
    int oldEntriesPerBatch = _entriesPerBatch;
    _entriesPerBatch = entriesPerBatch;
    firePropertyChange(ENTRIES_PER_BATCH, oldEntriesPerBatch, _entriesPerBatch);
  }

  public void setFetchesOnLoad(final boolean fetchesOnLoad) {
    boolean oldFetchesOnLoad = _fetchesOnLoad;
    _fetchesOnLoad = fetchesOnLoad;
    firePropertyChange(FETCHES_ON_LOAD, oldFetchesOnLoad, _fetchesOnLoad);
  }

  public void setFetchSpecName(final String fetchSpec) {
    String _fetchSpec = fetchSpec;
    String oldFetchSpecName = getFetchSpecName();
    EOFetchSpecification myFetchSpec = null;

    if (fetchSpec != null && _entity != null) {
      myFetchSpec = _entity.getFetchSpecNamed(fetchSpec);
    }

    if (myFetchSpec != null) {
      _databaseDataSource.setFetchSpecification(myFetchSpec);
    }
    else {
      EOFetchSpecification newFetchSpec = new EOFetchSpecification(null);
      newFetchSpec.setEntity(_entity);
      _databaseDataSource.setFetchSpecification(newFetchSpec);
    }

    if (fetchSpec == null && getFetchSpecList() != null) {
      _fetchSpec = FETCH_SPEC_NONE;
    }

    firePropertyChange(FETCH_SPEC_NAME, oldFetchSpecName, _fetchSpec);
  }

  public void setHasMasterDetail(final boolean hasMasterDetail) {
    if (_hasMasterDetail == hasMasterDetail) {
      return;
    }
    boolean oldHasMasterDetail = _hasMasterDetail;
    _hasMasterDetail = hasMasterDetail;

    if (hasMasterDetail) {
      _dataSource = _detailDataSource;
    }
    else {
      _dataSource = _databaseDataSource;
    }

    firePropertyChange(HAS_MASTER_DETAIL, oldHasMasterDetail, _hasMasterDetail);
  }

  public void setLocalKeys(final List<String> localKeys) {
    // Unused
    _localKeys = localKeys;
  }

  private void setMasterEntity(final EOEntity entity) {
    List<String> oldDetailKeyList = getDetailKeyList();
    if (entity == null || !entity.equals(_masterEntity)) {
      _masterEntity = entity;
      if (_masterEntity != null) {
        setMasterEntityName(_masterEntity.getName());
      }

      if (isHasMasterDetail()) {
        setEntity(entity);
      }

      _detailDataSource.setMasterClass(_masterEntityName);

      firePropertyChange(DETAIL_KEY_LIST, oldDetailKeyList, getDetailKeyList());
    }
  }

  public void setMasterEntityName(final String entity) {
    String oldMasterEntityName = _masterEntityName;

    if (entity == null || !entity.equals(_masterEntityName)) {
      _masterEntityName = entity;
      firePropertyChange(MASTER_ENTITY_NAME, oldMasterEntityName, _masterEntityName);
      try {
        EOEntity eoentity = _wooModel.getModelGroup().getEntityNamed(entity);
        setMasterEntity(eoentity);
      }
      catch (Exception e) {
        setMasterEntity(null);
      }
    }
  }

  public void setName(final String name) {
    String oldName = _name;
    _name = name;
    firePropertyChange(NAME, oldName, _name);
  }

  public void setQualificationIndex(final int qualification) {
    int oldQualificationIndex = _qualificationIndex;
    _qualificationIndex = qualification;
    _qualifierFormat = QUALIFICATION_FORMATS[_qualificationIndex];
    firePropertyChange(QUALIFICATION_INDEX, oldQualificationIndex, _qualificationIndex);
  }

  public void setSelectsFirstObject(final boolean value) {
    boolean oldSelectsFirstObject = _selectsFirstObject;
    _selectsFirstObject = value;
    firePropertyChange(SELECTS_FIRST_OBJECT, oldSelectsFirstObject, _selectsFirstObject);
  }

  public void setSortOrder(final String order) {
    String oldSortOrder = getSortOrder();
    if (order.equals(ASCENDING)) {
      _sortOrder.setSelectorName(EOSortOrdering.SELECTOR_ASCENDING);
      _isSorted = true;
    }
    if (order.equals(DESCENDING)) {
      _sortOrder.setSelectorName(EOSortOrdering.SELECTOR_DESCENDING);
      _isSorted = true;
    }
    if (order.equals(NOT_SORTED)) {
      _isSorted = false;
    }
    firePropertyChange(SORT_ORDER, oldSortOrder, getSortOrder());
  }

  public void setSortOrderKey(final String key) {
    String oldSortOrderKey = getSortOrderKey();
    String _key = key;

    if (key == null && getSortList().size() > 0) {
      _key = getSortList().get(0);
    }

    _sortOrder.setKey(_key);
    firePropertyChange(SORT_ORDER_KEY, oldSortOrderKey, _key);
  }

  public void setWooModel(final WooModel model) {
    _wooModel = model;
  }

  public EOModelMap toMap() {
    // XXX Need to validate model before saving
    if (_entity == null) {
      return null;
    }
    EOModelMap modelMap = new EOModelMap();
    modelMap.setString("class", _flass, true);
    modelMap.setMap("dataSource", _dataSource.toMap(), true);
    modelMap.setBoolean("fetchesOnLoad", _fetchesOnLoad, EOModelMap.YESNO);
    modelMap.setString("formatForLikeQualifier", _qualifierFormat, true);
    modelMap.setList("localKeys", _localKeys, true);
    modelMap.setInteger("numberOfObjectsPerBatch", _entriesPerBatch);
    modelMap.setBoolean("selectsFirstObjectAfterFetch", _selectsFirstObject, EOModelMap.YESNO);
    if (_isSorted) {
      List<Object> sortOrderingList = new ArrayList<Object>();
      sortOrderingList.add(_sortOrder.toMap());
      modelMap.setList("sortOrdering", sortOrderingList, true);
    }
    return modelMap;
  }
}
