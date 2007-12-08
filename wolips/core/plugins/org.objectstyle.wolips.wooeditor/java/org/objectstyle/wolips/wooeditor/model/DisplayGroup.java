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

package org.objectstyle.wolips.wooeditor.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import org.objectstyle.wolips.wooeditor.eomodel.EODataSource;
import org.objectstyle.wolips.wooeditor.eomodel.EODataSourceFactory;
import org.objectstyle.wolips.wooeditor.eomodel.EODatabaseDataSource;
import org.objectstyle.wolips.wooeditor.eomodel.EODetailDataSource;
import org.objectstyle.wolips.wooeditor.eomodel.EODisplayGroupSortOrdering;

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

	public static final String[] SORT_OPTIONS = new String[] { ASCENDING,
			DESCENDING, NOT_SORTED, };

	private static final String QUALIFIER_PREFIX = "%@*";

	private static final String QUALIFIER_SUFFIX = "*%@";

	private static final String QUALIFIER_CONTAINS = "*%@*";

	private static final String[] QUALIFICATION_LABELS = new String[] {
			"Prefix", "Contains", "Suffix", };

	private static final String[] QUALIFICATION_FORMATS = new String[] {
			QUALIFIER_PREFIX, QUALIFIER_CONTAINS, QUALIFIER_SUFFIX, };

	private String myName = "newDisplayGroup";
	private WooModel myWooModel;
	private int myQualificationIndex;
	private List<String> myEntityList;
	private EOEntity myEntity;
	private String myEntityName;
	private EOEntity myMasterEntity;
	private String myMasterEntityName;
	private String myDetailKeyName;
	private boolean myHasMasterDetail;
	private boolean myFetchesOnLoad;
	private String myClass;
	private String myQualifierFormat;
	private int myEntriesPerBatch;
	private List<String> myLocalKeys;
	private boolean mySelectsFirstObject;

	private PropertyChangeSupport myChangeSupport;

	private EODataSource myDataSource;
	private EODatabaseDataSource myDatabaseDataSource;
	private EODetailDataSource myDetailDataSource;
	private EOSortOrdering mySortOrder;
	private boolean myIsSorted;

	protected DisplayGroup(final WooModel model) {
		myWooModel = model;
		myWooModel.getModelGroup();
		myDatabaseDataSource = new EODatabaseDataSource(myWooModel
				.getModelGroup());
		myDetailDataSource = new EODetailDataSource(myWooModel.getModelGroup());
		myDataSource = myDatabaseDataSource;
		myQualificationIndex = 0;
		myQualifierFormat = QUALIFICATION_FORMATS[0];
		myClass = "WODisplayGroup";
		myIsSorted = false;
		myHasMasterDetail = false;
		myChangeSupport = new PropertyChangeSupport(this);
		mySortOrder = new EODisplayGroupSortOrdering();
		mySelectsFirstObject = false;
	}

	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		myChangeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(final String name,
			final PropertyChangeListener listener) {
		myChangeSupport.addPropertyChangeListener(name, listener);

	}

	protected void firePropertyChange(final String propertyName,
			final Object oldValue, final Object newValue) {
		if (oldValue != newValue
				|| (oldValue != null && !oldValue.equals(newValue))
				|| (newValue != null && !newValue.equals(oldValue))) {
			myWooModel.markAsDirty();
			myChangeSupport
					.firePropertyChange(propertyName, oldValue, newValue);
		}
	}

	public List<String> getDetailKeyList() {
		if (myMasterEntity != null) {
			try {
				List<String> keyList = new ArrayList<String>();
				Set<EORelationship> relationships = myMasterEntity
						.getRelationships();
				for (EORelationship relation : relationships) {
					if (relation.getToMany()) {
						keyList.add(relation.getName());
					}
				}
				return keyList;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public String getDetailKeyName() {
		return myDetailKeyName;
	}
	
	public String getEditingContext() {
		return myDatabaseDataSource.getEditingContext();
	}

	public EOEntity getEntity() {
		return myEntity;
	}

	public List<String> getEntityList() {
		if (myEntityList == null) {
			myEntityList = new ArrayList<String>(myWooModel.getModelGroup()
					.getEntityNames());
			Collections.sort(myEntityList);
		}
		return myEntityList;
	}

	public String getEntityName() {
		return myEntityName;
	}

	public int getEntriesPerBatch() {
		return myEntriesPerBatch;
	}

	public boolean getFetchesOnLoad() {
		return myFetchesOnLoad;
	}

	public List<String> getFetchSpecList() {
		if (myEntity != null) {
			try {
				Set<EOFetchSpecification> fetchSpecs = myEntity.getFetchSpecs();
				if (fetchSpecs.size() > 0) {
					List<String> fetchSpecList = new ArrayList<String>(
							fetchSpecs.size() + 1);
					fetchSpecList.add(FETCH_SPEC_NONE);

					for (EOFetchSpecification fspec : fetchSpecs) {
						fetchSpecList.add(fspec.getName());
					}
					Collections.sort(fetchSpecList);
					return fetchSpecList;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public String getFetchSpecName() {
		String name = myDatabaseDataSource.getFetchSpecification().getName();
		return name;
	}

	public List<String> getLocalKeys() {
		return myLocalKeys;
	}

	public EOEntity getMasterEntity() {
		return myMasterEntity;
	}

	public String getMasterEntityName() {
		return myMasterEntityName;
	}

	public String getName() {
		return myName;
	}

	public int getQualificationIndex() {
		return myQualificationIndex;
	}

	public String[] getQualificationList() {
		return QUALIFICATION_LABELS.clone();
	}
	
	public boolean getSelectsFirstObject() {
		return mySelectsFirstObject;
	}

	public List<String> getSortList() {
		if (myEntity != null) {
			try {
				Set<EOAttribute> attributes = myEntity.getClassAttributes();
				List<String> attribList = new ArrayList<String>(attributes.size());
				for (EOAttribute attribute : attributes) {
					attribList.add(attribute.getName());
				}
				Collections.sort(attribList);
				return attribList;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new ArrayList<String>();
	}

	public String getSortOrder() {
		String order = null;
		if (!myIsSorted) {
			order = NOT_SORTED;
		} else {
			String selector = mySortOrder.getSelectorName();
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
		return mySortOrder.getKey();
	}

	public WooModel getWooModel() {
		return myWooModel;
	}

	public boolean isHasMasterDetail() {
		return myHasMasterDetail;
	}

	@SuppressWarnings("unchecked")
	public void loadFromMap(final EOModelMap map,
			final Set<EOModelVerificationFailure> failures) {
		myClass = map.getString("class", true);
		myLocalKeys = map.getList("localKeys");
		if (map.containsKey("numberOfObjectsPerBatch")) {
			myEntriesPerBatch = map.getInteger("numberOfObjectsPerBatch");
		}
		if (map.containsKey("selectsFirstObjectAfterFetch")) {
			mySelectsFirstObject = map
					.getBoolean("selectsFirstObjectAfterFetch");
		}
		if (map.containsKey("fetchesOnLoad")) {
			myFetchesOnLoad = map.getBoolean("fetchesOnLoad");
		}
		myQualifierFormat = map.getString("formatForLikeQualifier", true);

		List<String> qualificationFormatList = Arrays
				.asList(QUALIFICATION_FORMATS);
		myQualificationIndex = qualificationFormatList
				.indexOf(myQualifierFormat);

		if (map.containsKey("dataSource")) {
			EOModelMap dataSourceMap = new EOModelMap(map.getMap("dataSource"));
			EODataSource dataSource = EODataSourceFactory
					.createDataSourceFromMap(dataSourceMap, myWooModel
							.getModelGroup());
			dataSource.loadFromMap(dataSourceMap, failures);
			myDataSource = dataSource;

			if (myDataSource instanceof EODetailDataSource) {
				EODetailDataSource ds = (EODetailDataSource) myDataSource;
				myHasMasterDetail = true;
				setMasterEntityName(ds.getMasterClass());
				setDetailKeyName(ds.getDetailKey());
				myDetailDataSource = ds;
			} else if (myDataSource instanceof EODatabaseDataSource) {
				EODatabaseDataSource ds = (EODatabaseDataSource) myDataSource;
				myHasMasterDetail = false;
				setEntityName(ds.getEntityName());
				myDatabaseDataSource = ds;
			}

			if (myEntityName == null) {
				// XXX Invalid display group
				if (dataSourceMap.containsKey("fetchSpecification")) {
					EOModelMap fetchSpecMap = new EOModelMap(dataSourceMap
							.getMap("fetchSpecification"));
					myEntityName = fetchSpecMap.getString("entityName", true);
				} else if (dataSourceMap.containsKey("masterClassDescription")) {
					myEntityName = dataSourceMap.getString(
							"masterClassDescription", true);
				}
			}
		}

		List<Map<Object, Object>> sortOrderList = map.getList("sortOrdering");
		if (sortOrderList != null) {
			for (Map<Object, Object> sortOrdering : sortOrderList) {
				if (sortOrdering != null) {
					EOModelMap sortOrderingMap = new EOModelMap(sortOrdering);
					mySortOrder.loadFromMap(sortOrderingMap);
					myIsSorted = true;
				}
			}
		}
	}

	public void removePropertyChangeListener(
			final PropertyChangeListener listener) {
		myChangeSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(final String name,
			final PropertyChangeListener listener) {
		myChangeSupport.removePropertyChangeListener(name, listener);
	}

	public void setDetailKeyName(final String key) {
		String oldDetailKeyName = myDetailKeyName;
		myDetailKeyName = key;
		if (key != null && isHasMasterDetail()) {
			EORelationship relation = myMasterEntity.getRelationshipNamed(key);
			if (relation != null) {
				setEntity(relation.getDestination().getEntity());
			} else {
				setEntity(myMasterEntity);
			}
		}

		myDetailDataSource.setDetailKey(myDetailKeyName);
		firePropertyChange(DETAIL_KEY_NAME, oldDetailKeyName, myDetailKeyName);
	}
	
	public void setEditingContext(final String ec) {
		String oldEditingContext = myDatabaseDataSource.getEditingContext();
		myDatabaseDataSource.setEditingContext(ec);
		firePropertyChange(EDITING_CONTEXT, oldEditingContext, 
				myDatabaseDataSource.getEditingContext());
	}

	private void setEntity(final EOEntity entity) {
		if (entity == null || !entity.equals(myEntity)) {
			List<String> oldFetchSpecList = getFetchSpecList();
			List<String> oldSortList = getSortList();

			myEntity = entity;
			if (myEntity != null) {
				setEntityName(myEntity.getName());
			}
			if (!isHasMasterDetail()) {
				setMasterEntity(entity);
			}

			myDatabaseDataSource.getFetchSpecification().setEntity(entity);
			firePropertyChange(FETCH_SPEC_LIST, oldFetchSpecList,
					getFetchSpecList());
			setFetchSpecName(null);

			firePropertyChange(SORT_LIST, oldSortList, getSortList());
			setSortOrderKey(null);
		}
	}

	public void setEntityName(final String entity) {
		String oldEntityName = myEntityName;
		if (entity == null || !entity.equals(myEntityName)) {
			myEntityName = entity;
			firePropertyChange(ENTITY_NAME, oldEntityName, myEntityName);
			try {
				EOEntity eoentity = myWooModel.getModelGroup().getEntityNamed(
						entity);
				setEntity(eoentity);
			} catch (Exception e) {
				e.printStackTrace();
				setEntity(null);
			}
		}
	}

	public void setEntriesPerBatch(final int entriesPerBatch) {
		int oldEntriesPerBatch = myEntriesPerBatch;
		myEntriesPerBatch = entriesPerBatch;
		firePropertyChange(ENTRIES_PER_BATCH, oldEntriesPerBatch,
				myEntriesPerBatch);
	}

	public void setFetchesOnLoad(final boolean fetchesOnLoad) {
		boolean oldFetchesOnLoad = myFetchesOnLoad;
		myFetchesOnLoad = fetchesOnLoad;
		firePropertyChange(FETCHES_ON_LOAD, oldFetchesOnLoad, myFetchesOnLoad);
	}

	public void setFetchSpecName(final String fetchSpec) {
		String _fetchSpec = fetchSpec;
		String oldFetchSpecName = getFetchSpecName();
		EOFetchSpecification myFetchSpec = null;

		if (fetchSpec != null && myEntity != null) {
			myFetchSpec = myEntity.getFetchSpecNamed(fetchSpec);
		}

		if (myFetchSpec != null) {
			myDatabaseDataSource.setFetchSpecification(myFetchSpec);
		} else {
			EOFetchSpecification newFetchSpec = new EOFetchSpecification(null);
			newFetchSpec.setEntity(myEntity);
			myDatabaseDataSource.setFetchSpecification(newFetchSpec);
		}

		if (fetchSpec == null && getFetchSpecList() != null) {
			_fetchSpec = FETCH_SPEC_NONE;
		}

		firePropertyChange(FETCH_SPEC_NAME, oldFetchSpecName, _fetchSpec);
	}

	public void setHasMasterDetail(final boolean hasMasterDetail) {
		if (myHasMasterDetail == hasMasterDetail) {
			return;
		}
		boolean oldHasMasterDetail = myHasMasterDetail;
		myHasMasterDetail = hasMasterDetail;

		if (hasMasterDetail) {
			myDataSource = myDetailDataSource;
		} else {
			myDataSource = myDatabaseDataSource;
		}

		firePropertyChange(HAS_MASTER_DETAIL, oldHasMasterDetail,
				myHasMasterDetail);
	}

	public void setLocalKeys(final List<String> localKeys) {
		// Unused
		myLocalKeys = localKeys;
	}

	private void setMasterEntity(final EOEntity entity) {
		List<String> oldDetailKeyList = getDetailKeyList();
		if (entity == null || !entity.equals(myMasterEntity)) {
			myMasterEntity = entity;
			if (myMasterEntity != null) {
				setMasterEntityName(myMasterEntity.getName());
			}

			if (isHasMasterDetail()) {
				setEntity(entity);
			}

			myDetailDataSource.setMasterClass(myMasterEntityName);

			firePropertyChange(DETAIL_KEY_LIST, oldDetailKeyList,
					getDetailKeyList());
		}
	}

	public void setMasterEntityName(final String entity) {
		String oldMasterEntityName = myMasterEntityName;

		if (entity == null || !entity.equals(myMasterEntityName)) {
			myMasterEntityName = entity;
			firePropertyChange(MASTER_ENTITY_NAME, oldMasterEntityName,
					myMasterEntityName);
			try {
				EOEntity eoentity = myWooModel.getModelGroup().getEntityNamed(
						entity);
				setMasterEntity(eoentity);
			} catch (Exception e) {
				setMasterEntity(null);
			}
		}
	}

	public void setName(final String name) {
		String oldName = myName;
		myName = name;
		firePropertyChange(NAME, oldName, myName);
	}

	public void setQualificationIndex(final int qualification) {
		int oldQualificationIndex = myQualificationIndex;
		myQualificationIndex = qualification;
		myQualifierFormat = QUALIFICATION_FORMATS[myQualificationIndex];
		firePropertyChange(QUALIFICATION_INDEX, oldQualificationIndex,
				myQualificationIndex);
	}
	
	public void setSelectsFirstObject(final boolean value) {
		boolean oldSelectsFirstObject = mySelectsFirstObject;
		mySelectsFirstObject = value;
		firePropertyChange(SELECTS_FIRST_OBJECT, oldSelectsFirstObject, 
				mySelectsFirstObject);
	}

	public void setSortOrder(final String order) {
		String oldSortOrder = getSortOrder();
		if (order.equals(ASCENDING)) {
			mySortOrder.setSelectorName(EOSortOrdering.SELECTOR_ASCENDING);
			myIsSorted = true;
		}
		if (order.equals(DESCENDING)) {
			mySortOrder.setSelectorName(EOSortOrdering.SELECTOR_DESCENDING);
			myIsSorted = true;
		}
		if (order.equals(NOT_SORTED)) {
			myIsSorted = false;
		}
		firePropertyChange(SORT_ORDER, oldSortOrder, getSortOrder());
	}

	public void setSortOrderKey(final String key) {
		String oldSortOrderKey = getSortOrderKey();
		String _key = key;

		if (key == null && getSortList().size() > 0) {
			_key = getSortList().get(0);
		}

		mySortOrder.setKey(_key);
		firePropertyChange(SORT_ORDER_KEY, oldSortOrderKey, _key);
	}

	public void setWooModel(final WooModel model) {
		myWooModel = model;
	}

	public EOModelMap toMap() {
		// XXX Need to validate model before saving
		if (myEntity == null) {
			return null;
		}
		EOModelMap modelMap = new EOModelMap();
		modelMap.setString("class", myClass, true);
		modelMap.setMap("dataSource", myDataSource.toMap(), true);
		modelMap.setBoolean("fetchesOnLoad", myFetchesOnLoad, EOModelMap.YESNO);
		modelMap.setString("formatForLikeQualifier", myQualifierFormat, true);
		modelMap.setList("localKeys", myLocalKeys, true);
		modelMap.setInteger("numberOfObjectsPerBatch", myEntriesPerBatch);
		modelMap.setBoolean("selectsFirstObjectAfterFetch",
				mySelectsFirstObject, EOModelMap.YESNO);
		if (myIsSorted) {
			List<Object> sortOrderingList = new ArrayList<Object>();
			sortOrderingList.add(mySortOrder.toMap());
			modelMap.setList("sortOrdering", sortOrderingList, true);
		}
		return modelMap;
	}
}
