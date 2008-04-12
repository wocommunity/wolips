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
package org.objectstyle.wolips.eomodeler.core.model;

import java.util.Set;


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
