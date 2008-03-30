package org.objectstyle.wolips.wodclipse.core.woo;

import java.util.Set;

import org.objectstyle.wolips.eomodeler.core.model.DuplicateFetchSpecNameException;
import org.objectstyle.wolips.eomodeler.core.model.EOFetchSpecification;
import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EOModelMap;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;

public class EODatabaseDataSource extends EODataSource {

  private final static String DEFAULT_EDITING_CONTEXT = "session.defaultEditingContext";

  private String _editingContext;

  private EOFetchSpecification _fetchSpecification;

  public EODatabaseDataSource(final EOModelGroup modelGroup) {
    super(modelGroup);
    _editingContext = DEFAULT_EDITING_CONTEXT;
  }

  @Override
  public void loadFromMap(final EOModelMap map, final Set<EOModelVerificationFailure> failures) {
    EOModelMap fspecMap = new EOModelMap(map.getMap("fetchSpecification"));
    String fspecName = map.getString("fetchSpecificationName", true);
    String entityName = fspecMap.getString("entityName", true);
    if (getModelGroup().getEntityNamed(entityName) != null) {
    	if (fspecName == null) {
    		_fetchSpecification = new EOFetchSpecification(null);
    		_fetchSpecification.loadFromMap(fspecMap, failures);
    		_fetchSpecification.setEntity(getModelGroup().getEntityNamed(entityName));
    	} else {
    		_fetchSpecification = getModelGroup().getEntityNamed(entityName).getFetchSpecNamed(fspecName);
    	}
    }
    _editingContext = map.getString("editingContext", true);

    // Fix missing editing context
    if (_editingContext == null) {
      _editingContext = DEFAULT_EDITING_CONTEXT;
    }
  }

  public String getEditingContext() {
    return _editingContext;
  }

  public void setEditingContext(final String editingContext) {
    _editingContext = editingContext;
  }

  public EOFetchSpecification getFetchSpecification() {
    if (_fetchSpecification == null) {
      _fetchSpecification = new EOFetchSpecification(null);
    }
    return _fetchSpecification;
  }

  public void setFetchSpecification(final EOFetchSpecification fetchSpecification) {
    _fetchSpecification = fetchSpecification;
  }

  public String getEntityName() {
    if (_fetchSpecification == null || _fetchSpecification.getEntity() == null) {
      return null;
    }
    return _fetchSpecification.getEntity().getName();
  }

  @Override
  public EOModelMap toMap() {
    EOModelMap modelMap = new EOModelMap();
    modelMap.setString("class", "EODatabaseDataSource", true);
    modelMap.setString("editingContext", _editingContext, true);
    if (_fetchSpecification != null) {
      modelMap.setMap("fetchSpecification", _fetchSpecification.toMap(), true);
      String fetchSpecName = _fetchSpecification.getName();
      if (fetchSpecName != null) {
        modelMap.setString("fetchSpecificationName", fetchSpecName, true);
      }
    }
    return modelMap;
  }
}
