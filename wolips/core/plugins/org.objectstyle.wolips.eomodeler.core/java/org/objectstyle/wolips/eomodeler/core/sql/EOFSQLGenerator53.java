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
package org.objectstyle.wolips.eomodeler.core.sql;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Declare a class named "org.objectstyle.wolips.eomodeler.EOModelProcessor"
 * with the following methods: public void processModel(EOModel _model,
 * NSMutableArray _entities, NSMutableDictionary _flags); public void
 * processSQL(StringBuffer _sqlBuffer, EOModel _model, NSMutableArray _entities,
 * NSMutableDictionary _flags);
 * 
 * or declare an "eomodelProcessorClassName" in your extra info dictionary that
 * has methods of the same signature.
 * 
 * processModel will be called prior to sql generation, and processSQL will be
 * called after sql generation but before it retuns to EOModeler.
 * 
 * @author mschrag
 * 
 */
public class EOFSQLGenerator53 implements IEOSQLGenerator {
	
	// com.webobjects.foundation.NSMutableArray
	private Object _entities;

	// com.webobjects.eoaccess.EOModel
	private Object _model;

	// com.webobjects.eoaccess.EOModelGroup
	private Object _modelGroup;

	private Object _modelProcessor;

	public EOFSQLGenerator53(String modelName, List modelURLs, List entityNames, Map selectedDatabaseConfig, boolean runInEntityModeler) {
		try {
			init(modelName, modelURLs, entityNames, selectedDatabaseConfig, runInEntityModeler);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	private void init(String modelName, List modelURLs, List entityNames, Map selectedDatabaseConfig, boolean runInEntityModeler) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		fixClassPath();
		
		Class<?> eomodelgroup = Class.forName("com.webobjects.eoaccess.EOModelGroup");
		Class<?> eomodel = Class.forName("com.webobjects.eoaccess.EOModel");
		Class<?> nsarray = Class.forName("com.webobjects.foundation.NSArray");
		Class<?> nsdictionary = Class.forName("com.webobjects.foundation.NSDictionary");
		Class<?> nsmutarray = Class.forName("com.webobjects.foundation.NSMutableArray");
		Class<?> nsmutdictionary = Class.forName("com.webobjects.foundation.NSMutableDictionary");

		Map databaseConfig = selectedDatabaseConfig;
		if (databaseConfig == null) {
			databaseConfig = new HashMap();
		}

		_modelGroup = eomodelgroup.getConstructor().newInstance();

		Iterator modelURLIter = modelURLs.iterator();
		while (modelURLIter.hasNext()) {
			URL modelURL = (URL) modelURLIter.next();
			eomodelgroup.getMethod("addModelWithPathURL", URL.class).invoke(_modelGroup, modelURL);
		}

		String prototypeEntityName = (String) databaseConfig.get("prototypeEntityName");
		if (prototypeEntityName != null) {
			replacePrototypes(_modelGroup, prototypeEntityName);
		}

		_entities = nsmutarray.getConstructor().newInstance();
		_model = eomodelgroup.getMethod("modelNamed", String.class).invoke(_modelGroup, modelName);

		Object defaultConnectionDictionary;
		Map overrideConnectionDictionary = (Map) databaseConfig.get("connectionDictionary");
		if (overrideConnectionDictionary != null) {
			Object connectionDictionary = EOFSQLUtils53.toWOCollections(overrideConnectionDictionary);
			eomodel.getMethod("setConnectionDictionary", nsdictionary).invoke(_model, connectionDictionary);
			String eomodelProcessorClassName = (String) nsdictionary.getMethod("valueForKey", String.class).invoke(connectionDictionary, "eomodelProcessorClassName");
			if (eomodelProcessorClassName != null) {
				findModelProcessor(eomodelProcessorClassName, true);
			}
			defaultConnectionDictionary = connectionDictionary;
		} else {
			defaultConnectionDictionary = eomodel.getMethod("connectionDictionary").invoke(_model);
		}

		Object modelsarr = eomodelgroup.getMethod("models").invoke(_modelGroup);
		Enumeration modelsEnum = (Enumeration) nsarray.getMethod("objectEnumerator").invoke(modelsarr);
		while (modelsEnum.hasMoreElements()) {
			Object model = modelsEnum.nextElement();
			if (eomodel.getMethod("connectionDictionary").invoke(model) == null) {
				eomodel.getMethod("setConnectionDictionary", nsdictionary).invoke(model, defaultConnectionDictionary);
			}
		}

		if (_modelProcessor == null) {
			findModelProcessor("org.objectstyle.wolips.eomodeler.EOModelProcessor", false);
		}
		if (entityNames == null || entityNames.size() == 0) {
			Object entitiesarr = eomodel.getMethod("entities").invoke(_model);
			Enumeration entitiesEnum = (Enumeration) nsarray.getMethod("objectEnumerator").invoke(entitiesarr);
			while (entitiesEnum.hasMoreElements()) {
				Object entity = entitiesEnum.nextElement();
				if (!isPrototype(entity)) {// &&
					// entityUsesSeparateTable(entity))
					// {
					nsmutarray.getMethod("addObject", Object.class).invoke(_entities, entity);
				}
			}
		} else {
			Iterator entityNamesIter = entityNames.iterator();
			while (entityNamesIter.hasNext()) {
				String entityName = (String) entityNamesIter.next();
				Object entity = eomodel.getMethod("entityNamed", String.class).invoke(_model, entityName);
				if (entity != null) {
					nsmutarray.getMethod("addObject", Object.class).invoke(_entities, entity);
				}
			}
		}

		// MS: Remove jdbc2Info prior to SQL generation
		Object connectionDictionary = eomodel.getMethod("connectionDictionary").invoke(_model);
		if (connectionDictionary != null) {
			Object mutableConnectionDictionary = nsdictionary.getMethod("mutableClone").invoke(connectionDictionary);
			nsmutdictionary.getMethod("removeObjectForKey", Object.class).invoke(mutableConnectionDictionary, "jdbc2Info");
			eomodel.getMethod("setConnectionDictionary", nsdictionary).invoke(_model, mutableConnectionDictionary);
		}

		// MS: Add the "inEntityModeler" flag so that plugins can adjust their
		// behavior if they need to.
		if (runInEntityModeler) {
			Object infodict = eomodel.getMethod("userInfo").invoke(_model);
			Object modelUserInfo = nsdictionary.getMethod("mutableClone").invoke(infodict);
			Object entityModelerDict = nsmutdictionary.getMethod("valueForKey", String.class).invoke(modelUserInfo, "_EntityModeler");
			Object mutableEntityModelerDict;
			if (entityModelerDict == null) {
				mutableEntityModelerDict = nsmutdictionary.getConstructor().newInstance();
			} else {
				mutableEntityModelerDict = nsdictionary.getMethod("mutableClone").invoke(entityModelerDict);
			}
			nsmutdictionary.getMethod("takeValueForKey", Object.class, String.class).invoke(mutableEntityModelerDict, Boolean.TRUE, "inEntityModeler");
			nsmutdictionary.getMethod("takeValueForKey", Object.class, String.class).invoke(modelUserInfo, mutableEntityModelerDict, "_EntityModeler");
			eomodel.getMethod("setUserInfo", nsdictionary).invoke(_model, modelUserInfo);
		}

		ensureSingleTableInheritanceParentEntitiesAreIncluded();
		ensureSingleTableInheritanceChildEntitiesAreIncluded();
		fixAllowsNullOnSingleTableInheritance();
		localizeEntities();
	}

	protected void replacePrototypes(Object modelGroup, String prototypeEntityName) {
		try {
			replacePrototypesReflect(modelGroup, prototypeEntityName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
	}

	private void replacePrototypesReflect(Object modelGroup, String prototypeEntityName) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
		Class<?> eomodelgroup = Class.forName("com.webobjects.eoaccess.EOModelGroup");
		Class<?> eomodel = Class.forName("com.webobjects.eoaccess.EOModel");
		Class<?> eoentity = Class.forName("com.webobjects.eoaccess.EOEntity");
		Class<?> nsarray = Class.forName("com.webobjects.foundation.NSArray");
		Class<?> nsmutdictionary = Class.forName("com.webobjects.foundation.NSMutableDictionary");

		String replacementPrototypeName = "EOPrototypes";

		// Don't replace prototypes if the selected prototype entity name
		// doesn't exist
		if (eomodelgroup.getMethod("entityNamed", String.class).invoke(modelGroup, prototypeEntityName) == null) {
			return;
		}

		// Don't replace prototypes if you're already just using "EOPrototypes"
		if (replacementPrototypeName.equals(prototypeEntityName)) {
			return;
		}

		Object removedPrototypeEntities = nsmutdictionary.getConstructor().newInstance();

		Object prototypesModel = null;
		Object modelsarr = eomodelgroup.getMethod("models").invoke(_modelGroup);
		Enumeration modelsEnum = (Enumeration) nsarray.getMethod("objectEnumerator").invoke(modelsarr);
		while (modelsEnum.hasMoreElements()) {
			Object model = modelsEnum.nextElement();
			String adaptorName = (String) eomodel.getMethod("adaptorName").invoke(model);
			Object eoAdaptorPrototypesEntity = eomodelgroup.getMethod("entityNamed", String.class).invoke(_modelGroup, "EO" + adaptorName + "Prototypes");
			if (eoAdaptorPrototypesEntity != null) {
				prototypesModel = eoentity.getMethod("model").invoke(eoAdaptorPrototypesEntity);
				// System.out.println("EOFSQLGenerator.EOFSQLGenerator:
				// removing " + eoAdaptorPrototypesEntity.name() + " from "
				// + prototypesModel.name());
				eomodel.getMethod("removeEntity", eoentity).invoke(prototypesModel, eoAdaptorPrototypesEntity);
				String entityName = (String) eoentity.getMethod("name").invoke(eoAdaptorPrototypesEntity);
				nsmutdictionary.getMethod("takeValueForKey", Object.class, String.class).invoke(removedPrototypeEntities, eoAdaptorPrototypesEntity, entityName);
			}
		}

		Object eoPrototypesEntity = eomodelgroup.getMethod("entityNamed", String.class).invoke(_modelGroup, replacementPrototypeName);
		if (eoPrototypesEntity != null) {
			prototypesModel = eoentity.getMethod("model").invoke(eoPrototypesEntity);
			eomodel.getMethod("removeEntity", eoentity).invoke(prototypesModel, eoPrototypesEntity);
			// System.out.println("EOFSQLGenerator.EOFSQLGenerator: removing
			// " + eoPrototypesEntity.name() + " from " +
			// prototypesModel.name());
			nsmutdictionary.getMethod("takeValueForKey", Object.class, String.class).invoke(removedPrototypeEntities, eoPrototypesEntity, replacementPrototypeName);
		}

		Object prototypesEntity = eomodelgroup.getMethod("entityNamed", String.class).invoke(_modelGroup, prototypeEntityName);
		if (prototypesEntity == null) {
			prototypesEntity = nsmutdictionary.getMethod("valueForKey", String.class).invoke(removedPrototypeEntities, prototypeEntityName);
		} else {
			prototypesModel= eoentity.getMethod("model").invoke(prototypesEntity);
			eomodel.getMethod("removeEntity", eoentity).invoke(prototypesModel, prototypesEntity);
		}
		if (prototypesEntity != null && prototypesModel != null) {
			// System.out.println("EOFSQLGenerator.EOFSQLGenerator: setting
			// " + prototypesEntity.name() + " to EOPrototypes in " +
			// prototypesModel.name());
			eoentity.getMethod("setName", String.class).invoke(prototypesEntity, replacementPrototypeName);
			eomodel.getMethod("addEntity", eoentity).invoke(prototypesModel, prototypesEntity);
		}

		modelsarr = eomodelgroup.getMethod("models").invoke(_modelGroup);
		Enumeration resetModelsEnum = (Enumeration) nsarray.getMethod("objectEnumerator").invoke(modelsarr);
		while (resetModelsEnum.hasMoreElements()) {
			Object model = resetModelsEnum.nextElement();
			eomodel.getMethod("_resetPrototypeCache").invoke(model);
		}
	}

	protected void localizeEntities() {
		try {
			localizeEntitiesReflect();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	private void localizeEntitiesReflect() throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<?> nsarray = Class.forName("com.webobjects.foundation.NSArray");
		Object entities = nsarray.getConstructor(nsarray).newInstance(_entities);
		Enumeration entitiesEnum = (Enumeration) nsarray.getMethod("objectEnumerator").invoke(entities);
		while (entitiesEnum.hasMoreElements()) {
			Object entity = entitiesEnum.nextElement();
			createLocalizedAttributes(entity);
		}
	}

	protected void fixAllowsNullOnSingleTableInheritance() {
		try {
			fixAllowsNullOnSingleTableInheritanceReflect();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	private void fixAllowsNullOnSingleTableInheritanceReflect() throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<?> eoentity = Class.forName("com.webobjects.eoaccess.EOEntity");
		Class<?> nsarray = Class.forName("com.webobjects.foundation.NSArray");
		Class<?> eoattribute = Class.forName("com.webobjects.eoaccess.EOAttribute");
		Object entities = nsarray.getConstructor(nsarray).newInstance(_entities);
		Enumeration entitiesEnum = (Enumeration) nsarray.getMethod("objectEnumerator").invoke(entities);
		while (entitiesEnum.hasMoreElements()) {
			Object entity = entitiesEnum.nextElement();
			if (isSingleTableInheritance(entity)) {
				Object attributesarr = eoentity.getMethod("attributes").invoke(entity);
				Enumeration attributeEnum = (Enumeration) nsarray.getMethod("objectEnumerator").invoke(attributesarr);
				while (attributeEnum.hasMoreElements()) {
					Object attribute = attributeEnum.nextElement();
					if (!isInherited(attribute)) {
						eoattribute.getMethod("setAllowsNull", Boolean.class).invoke(attribute, Boolean.TRUE);
					}
				}
			}
		}
	}

	protected void ensureSingleTableInheritanceParentEntitiesAreIncluded() {
		try {
			ensureSingleTableInheritanceParentEntitiesAreIncludedReflect();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	private void ensureSingleTableInheritanceParentEntitiesAreIncludedReflect() throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<?> nsarray = Class.forName("com.webobjects.foundation.NSArray");
		Object entities = nsarray.getConstructor(nsarray).newInstance(_entities);
		Enumeration entitiesEnum = (Enumeration) nsarray.getMethod("objectEnumerator").invoke(entities);
		while (entitiesEnum.hasMoreElements()) {
			Object entity = entitiesEnum.nextElement();
			ensureSingleTableInheritanceParentEntitiesAreIncluded(entity);
		}
	}

	protected void ensureSingleTableInheritanceChildEntitiesAreIncluded() {
		try {
			ensureSingleTableInheritanceChildEntitiesAreIncludedReflect();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	private void ensureSingleTableInheritanceChildEntitiesAreIncludedReflect() throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<?> eomodel = Class.forName("com.webobjects.eoaccess.EOModel");
		Class<?> eoentity = Class.forName("com.webobjects.eoaccess.EOEntity");
		Class<?> nsarray = Class.forName("com.webobjects.foundation.NSArray");
		Class<?> nsmutarray = Class.forName("com.webobjects.foundation.NSMutableArray");
		Object entitiesarr = eomodel.getMethod("entities").invoke(_model);
		Enumeration entitiesEnum = (Enumeration) nsarray.getMethod("objectEnumerator").invoke(entitiesarr);
		while (entitiesEnum.hasMoreElements()) {
			Object entity = entitiesEnum.nextElement();
			if (isSingleTableInheritance(entity)) {
				Object parentEntity = eoentity.getMethod("parentEntity").invoke(entity);
				Boolean containsParent = (Boolean) nsmutarray.getMethod("containsObject", Object.class).invoke(_entities, parentEntity);
				Boolean containsEntity = (Boolean) nsmutarray.getMethod("containsObject", Object.class).invoke(_entities, entity);
				if (containsParent && !containsEntity) {
					nsmutarray.getMethod("addObject", Object.class).invoke(_entities, entity);
				}
			}
		}
	}

	protected void ensureSingleTableInheritanceParentEntitiesAreIncluded(Object entity) {
		try {
			ensureSingleTableInheritanceParentEntitiesAreIncludedReflect(entity);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	private void ensureSingleTableInheritanceParentEntitiesAreIncludedReflect(Object entity) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<?> eoentity = Class.forName("com.webobjects.eoaccess.EOEntity");
		Class<?> nsmutarray = Class.forName("com.webobjects.foundation.NSMutableArray");
		if (isSingleTableInheritance(entity)) {
			Object parentEntity = eoentity.getMethod("parentEntity").invoke(entity);
			Boolean containsParent = (Boolean) nsmutarray.getMethod("containsObject", Object.class).invoke(_entities, parentEntity);
			if (!containsParent) {
				nsmutarray.getMethod("addObject", Object.class).invoke(_entities, parentEntity);
				ensureSingleTableInheritanceParentEntitiesAreIncluded(entity);
			}
		}
	}

	protected boolean isPrototype(Object _entity) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Class<?> eoentity = Class.forName("com.webobjects.eoaccess.EOEntity");
		String entityName = (String) eoentity.getMethod("name").invoke(_entity);
		boolean isPrototype = (entityName.startsWith("EO") && entityName.endsWith("Prototypes"));
		return isPrototype;
	}

	protected boolean isSingleTableInheritance(Object entity) {
		try {
			return isSingleTableInheritanceReflect(entity);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean isSingleTableInheritanceReflect(Object entity) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<?> eoentity = Class.forName("com.webobjects.eoaccess.EOEntity");
		Object parentEntity = eoentity.getMethod("parentEntity").invoke(entity);
		String entityExternalName = (String) eoentity.getMethod("externalName").invoke(entity);
		String parentEntityExternalName = (String) eoentity.getMethod("externalName").invoke(parentEntity);
		return parentEntity != null && entityExternalName != null && entityExternalName.equalsIgnoreCase(parentEntityExternalName);
	}

	protected void createLocalizedAttributes(Object entity) {
		try {
			createLocalizedAttributesReflect(entity);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
	}

	private void createLocalizedAttributesReflect(Object entity) throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException, InstantiationException {
		Class<?> eoentity = Class.forName("com.webobjects.eoaccess.EOEntity");
		Class<?> eomodel = Class.forName("com.webobjects.eoaccess.EOModel");
		Class<?> eoattribute = Class.forName("com.webobjects.eoaccess.EOAttribute");
		Class<?> nsarray = Class.forName("com.webobjects.foundation.NSArray");
		Class<?> nsdictionary = Class.forName("com.webobjects.foundation.NSDictionary");
		Class<?> nsmutarray = Class.forName("com.webobjects.foundation.NSMutableArray");
		
		Object emptyarr = nsarray.getField("EmptyArray").get(null);

		Object attributes = nsarray.getMethod("immutableClone").invoke(eoentity.getMethod("attributes").invoke(entity));
		Object classProperties = nsarray.getMethod("immutableClone").invoke(eoentity.getMethod("classProperties").invoke(entity));
		Object attributesUsedForLocking = nsarray.getMethod("immutableClone").invoke(eoentity.getMethod("attributesUsedForLocking").invoke(entity));
		
		if (attributes == null) {
			attributes = emptyarr;
		}
		if (classProperties == null) {
			classProperties = emptyarr;
		}
		if (attributesUsedForLocking == null) {
			attributesUsedForLocking = emptyarr;
		}
		Object mutableClassProperties = nsarray.getMethod("mutableClone").invoke(classProperties);
		Object mutableAttributesUsedForLocking = nsarray.getMethod("mutableClone").invoke(attributesUsedForLocking);
		for (Enumeration e = (Enumeration) nsarray.getMethod("objectEnumerator").invoke(attributes); e.hasMoreElements();) {
			Object attribute = e.nextElement();
			Object userInfo = eoattribute.getMethod("userInfo").invoke(attribute);
			String name = (String) eoattribute.getMethod("name").invoke(attribute);
			if (userInfo != null) {
				Object l = nsdictionary.getMethod("valueForKey", String.class).invoke(userInfo, "ERXLanguages");
				if (l != null && !nsarray.isInstance(l)) {
					Object modelUserInfo = eomodel.getMethod("userInfo").invoke(eoentity.getMethod("model").invoke(entity));
					l = (modelUserInfo != null ? nsdictionary.getMethod("valueForKey", String.class).invoke(modelUserInfo, "ERXLanguages") : null);
				}

				if (l != null && ((Integer) nsarray.getMethod("count").invoke(l)) > 0) {
					List languages = (List) EOFSQLUtils53.toJavaCollections(l);
					String columnName = (String) eoattribute.getMethod("columnName").invoke(attribute);
					for (int i = 0; i < languages.size(); i++) {
						String language = (String) languages.get(i);
						String newName = name + "_" + language;
						String newColumnName = columnName + "_" + language;

						Object newAttribute = eoattribute.getConstructor().newInstance();
						eoattribute.getMethod("setName", String.class).invoke(newAttribute, newName);
						eoentity.getMethod("addAttribute", eoattribute).invoke(entity, newAttribute);

						eoattribute.getMethod("setPrototype", eoattribute).invoke(newAttribute, eoattribute.getMethod("prototype").invoke(attribute));
						eoattribute.getMethod("setColumnName", String.class).invoke(newAttribute, newColumnName);
						eoattribute.getMethod("setAllowsNull", Boolean.class).invoke(newAttribute, eoattribute.getMethod("allowsNull").invoke(attribute));
						eoattribute.getMethod("setClassName", String.class).invoke(newAttribute, eoattribute.getMethod("className").invoke(attribute));
						eoattribute.getMethod("setExternalType", String.class).invoke(newAttribute, eoattribute.getMethod("externalType").invoke(attribute));
						eoattribute.getMethod("setWidth", Integer.class).invoke(newAttribute, eoattribute.getMethod("width").invoke(attribute));
						eoattribute.getMethod("setUserInfo", nsdictionary).invoke(newAttribute, eoattribute.getMethod("userInfo").invoke(attribute));

						if ((Boolean) nsarray.getMethod("containsObject", Object.class).invoke(classProperties, attribute)) {
							nsmutarray.getMethod("addObject", Object.class).invoke(mutableClassProperties, newAttribute);
						}
						if ((Boolean) nsarray.getMethod("containsObject", Object.class).invoke(attributesUsedForLocking, attribute)) {
							nsmutarray.getMethod("addObject", Object.class).invoke(mutableAttributesUsedForLocking, newAttribute);
						}
					}
					eoentity.getMethod("removeAttribute", eoattribute).invoke(entity, attribute);
					nsmutarray.getMethod("removeObject", Object.class).invoke(mutableClassProperties, attribute);
					nsmutarray.getMethod("removeObject", Object.class).invoke(mutableAttributesUsedForLocking, attribute);
				}
			}
			eoentity.getMethod("setClassProperties", nsarray).invoke(entity, mutableClassProperties);
			eoentity.getMethod("setAttributesUsedForLocking", nsarray).invoke(entity, mutableAttributesUsedForLocking);
		}
	}

	protected boolean isInherited(Object attribute) {
		try {
			return isInheritedReflect(attribute);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean isInheritedReflect(Object attribute) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<?> eoattribute = Class.forName("com.webobjects.eoaccess.EOAttribute");
		Class<?> eoentity = Class.forName("com.webobjects.eoaccess.EOEntity");

		boolean inherited = false;
		Object entity = eoattribute.getMethod("entity").invoke(attribute);
		Object parentEntity = eoentity.getMethod("parentEntity").invoke(entity);
		String attributeName = (String) eoattribute.getMethod("name").invoke(attribute);
		while (!inherited && parentEntity != null) {
			inherited = (eoentity.getMethod("attributeNamed", String.class).invoke(parentEntity, attributeName) != null);
			parentEntity = eoentity.getMethod("parentEntity").invoke(parentEntity);
		}
		return inherited;
	}

	protected void fixDuplicateSingleTableInheritanceDropStatements(Object syncFactory, Object flags, StringBuffer sqlBuffer) {
		try {
			fixDuplicateSingleTableInheritanceDropStatementsReflect(syncFactory, flags, sqlBuffer);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
	}

	private void fixDuplicateSingleTableInheritanceDropStatementsReflect(Object syncFactory, Object flags, StringBuffer sqlBuffer) throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException, InstantiationException {
		Class<?> eoschemageneration = Class.forName("com.webobjects.eoaccess.EOSchemaGeneration");
		Class<?> eosynchronizationfactory = Class.forName("com.webobjects.eoaccess.EOSynchronizationFactory");
		Class<?> nsarray = Class.forName("com.webobjects.foundation.NSArray");
		Class<?> nsdictionary = Class.forName("com.webobjects.foundation.NSDictionary");
		Class<?> nsmutarray = Class.forName("com.webobjects.foundation.NSMutableArray");
		Class<?> nsmutdictionary = Class.forName("com.webobjects.foundation.NSMutableDictionary");
		String DropTablesKey = (String) eoschemageneration.getField("DropTablesKey").get(null);
		Object flag = nsmutdictionary.getMethod("valueForKey", String.class).invoke(flags, DropTablesKey);
		if ("YES".equals(flag)) {
			Object dropEntities = nsmutarray.getConstructor(nsarray).newInstance(_entities);
			int count = (Integer) nsmutarray.getMethod("count").invoke(dropEntities);
			for (int entityNum = count - 1; entityNum >= 0; entityNum--) {
				Object entity = nsmutarray.getMethod("objectAtIndex", Integer.class).invoke(dropEntities, entityNum);
				if (isSingleTableInheritance(entity)) {
					nsmutarray.getMethod("removeObjectAtIndex", Integer.class).invoke(dropEntities, entityNum);
				}
			}
			int dropCount = (Integer) nsmutarray.getMethod("count").invoke(dropEntities);
			int entitiesCount = (Integer) nsmutarray.getMethod("count").invoke(_entities);
			if (dropCount != entitiesCount) {
				Object dropFlags = nsmutdictionary.getConstructor().newInstance();
				nsmutdictionary.getMethod("takeValueForKey", Object.class, String.class).invoke(dropFlags, "YES", DropTablesKey);
				String DropPrimaryKeySupportKey = (String) eoschemageneration.getField("DropPrimaryKeySupportKey").get(null);
				nsmutdictionary.getMethod("takeValueForKey", Object.class, String.class).invoke(dropFlags, "NO", DropPrimaryKeySupportKey);
				String CreateTablesKey = (String) eoschemageneration.getField("CreateTablesKey").get(null);
				nsmutdictionary.getMethod("takeValueForKey", Object.class, String.class).invoke(dropFlags, "NO", CreateTablesKey);
				String CreatePrimaryKeySupportKey = (String) eoschemageneration.getField("CreatePrimaryKeySupportKey").get(null);
				nsmutdictionary.getMethod("takeValueForKey", Object.class, String.class).invoke(dropFlags, "NO", CreatePrimaryKeySupportKey);
				String PrimaryKeyConstraintsKey = (String) eoschemageneration.getField("PrimaryKeyConstraintsKey").get(null);
				nsmutdictionary.getMethod("takeValueForKey", Object.class, String.class).invoke(dropFlags, "NO", PrimaryKeyConstraintsKey);
				String ForeignKeyConstraintsKey = (String) eoschemageneration.getField("ForeignKeyConstraintsKey").get(null);
				nsmutdictionary.getMethod("takeValueForKey", Object.class, String.class).invoke(dropFlags, "NO", ForeignKeyConstraintsKey);
				String CreateDatabaseKey = (String) eoschemageneration.getField("CreateDatabaseKey").get(null);
				nsmutdictionary.getMethod("takeValueForKey", Object.class, String.class).invoke(dropFlags, "NO", CreateDatabaseKey);
				String DropDatabaseKey = (String) eoschemageneration.getField("DropDatabaseKey").get(null);
				nsmutdictionary.getMethod("takeValueForKey", Object.class, String.class).invoke(dropFlags, "NO", DropDatabaseKey);
				nsmutdictionary.getMethod("takeValueForKey", Object.class, String.class).invoke(flags, "NO", DropTablesKey);
				String dropSql = (String) eosynchronizationfactory.getMethod("schemaCreationScriptForEntities", nsarray, nsdictionary).invoke(syncFactory, dropEntities, dropFlags);
				sqlBuffer.append(dropSql);
				sqlBuffer.append("\n");
			}
		}
	}

	private String getClassPath() {
		URL urls[] = ((URLClassLoader) getClass().getClassLoader()).getURLs();
		String classPath = "";
		for (int i = 0; i < urls.length; i++) {
			URL url = urls[i];
			classPath += url.getFile() + File.pathSeparator;
		}
		return classPath;
	}

	private void fixClassPath() {
		String classPath = getClassPath();
		System.setProperty("java.class.path", classPath);
		System.setProperty("com.webobjects.classpath", classPath);
	}

	public String generateSchemaCreationScript(Map flagsMap) {
		try {
			return generateSchemaCreationScriptReflect(flagsMap);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	private String generateSchemaCreationScriptReflect(Map flagsMap) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		fixClassPath();
		
		Class<?> eodatabasecontext = Class.forName("com.webobjects.eoaccess.EODatabaseContext");
		Class<?> eodatabase = Class.forName("com.webobjects.eoaccess.EODatabase");
		Class<?> eomodel = Class.forName("com.webobjects.eoaccess.EOModel");
		Class<?> eosynchronizationfactory = Class.forName("com.webobjects.eoaccess.EOSynchronizationFactory");
		Class<?> eoadaptorcontext = Class.forName("com.webobjects.eoaccess.EOAdaptorContext");
		Class<?> jdbcadaptor = Class.forName("com.webobjects.jdbcadaptor.JDBCAdaptor");
		Class<?> jdbcplugin = Class.forName("com.webobjects.jdbcadaptor.JDBCPlugIn");
		Class<?> eoadaptorchannel = Class.forName("com.webobjects.eoaccess.EOAdaptorChannel");
		Class<?> nsarray = Class.forName("com.webobjects.foundation.NSArray");
		Class<?> nsdictionary = Class.forName("com.webobjects.foundation.NSDictionary");
		Class<?> nsmutarray = Class.forName("com.webobjects.foundation.NSMutableArray");

		Object flags = EOFSQLUtils53.toWOCollections(flagsMap);

		callModelProcessorMethodIfExists("processModel", new Object[] { _model, _entities, flags });

		Object db = eodatabase.getConstructor(eomodel).newInstance(_model);
		Object dbc = eodatabasecontext.getConstructor(eodatabase).newInstance(db);
		Object ac = eodatabasecontext.getMethod("adaptorContext").invoke(dbc);
		Object ad = eoadaptorcontext.getMethod("adaptor").invoke(ac);
		Object plug = jdbcadaptor.getMethod("plugIn").invoke(ad);
		Object sf = jdbcplugin.getMethod("synchronizationFactory").invoke(plug);
		
		Object beforeOpenChannels = nsmutarray.getConstructor().newInstance();
		Object channelsarr = eoadaptorcontext.getMethod("channels").invoke(ac);
		Enumeration beforeChannelsEnum = (Enumeration) nsarray.getMethod("objectEnumerator").invoke(channelsarr);
		while (beforeChannelsEnum.hasMoreElements()) {
			Object channel = beforeChannelsEnum.nextElement();
			Boolean isOpen = (Boolean) eoadaptorchannel.getMethod("isOpen").invoke(channel);
			if (isOpen) {
				nsmutarray.getMethod("addObject", Object.class).invoke(beforeOpenChannels, channel);
			}
		}


		StringBuffer sqlBuffer = new StringBuffer();
		fixDuplicateSingleTableInheritanceDropStatements(sf, flags, sqlBuffer);

		try {
			String sql = (String) eosynchronizationfactory.getMethod("schemaCreationScriptForEntities", nsarray, nsdictionary).invoke(sf, _entities, flags);
			sql = sql.replaceAll("CREATE TABLE ([^\\s(]+)\\(", "CREATE TABLE $1 (");
			sqlBuffer.append(sql);

			callModelProcessorMethodIfExists("processSQL", new Object[] { sqlBuffer, _model, _entities, flags });
		} finally {
			Object channelsarr2 = eoadaptorcontext.getMethod("channels").invoke(ac);
			Enumeration afterChannelsEnum = (Enumeration) nsarray.getMethod("objectEnumerator").invoke(channelsarr2);
			while (afterChannelsEnum.hasMoreElements()) {
				Object channel = afterChannelsEnum.nextElement();
				Boolean isOpen = (Boolean) eoadaptorchannel.getMethod("isOpen").invoke(channel);
				Boolean contains = (Boolean) nsmutarray.getMethod("containsObject", Object.class).invoke(beforeOpenChannels, channel);
				if (isOpen && !contains) {
					eoadaptorchannel.getMethod("closeChannel").invoke(channel);
				}
			}
		}

		String sqlBufferStr = sqlBuffer.toString();
		if (sqlBufferStr != null) {
			sqlBufferStr = Pattern.compile("([\\w\\)])(NOT NULL)", Pattern.CASE_INSENSITIVE).matcher(sqlBufferStr).replaceAll("$1 $2");
		}
		return sqlBufferStr;
	}

	public Object callModelProcessorMethodIfExists(String methodName, Object[] _objs) {
		try {
			Object results = null;
			if (_modelProcessor != null) {
				Method[] methods = _modelProcessor.getClass().getMethods();
				Method matchingMethod = null;
				for (int methodNum = 0; matchingMethod == null && methodNum < methods.length; methodNum++) {
					Method method = methods[methodNum];
					if (method.getName().equals(methodName)) {
						Class[] parameterTypes = method.getParameterTypes();
						boolean parametersMatch = false;
						if ((_objs == null || _objs.length == 0) && parameterTypes.length == 0) {
							parametersMatch = true;
						} else if (_objs != null && _objs.length == parameterTypes.length) {
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
					results = matchingMethod.invoke(_modelProcessor, _objs);
				} else {
					System.out.println("EOFSQLGenerator.callModelProcessorMethodIfExists: Missing delegate " + methodName);
				}
			}
			return results;
		} catch (Throwable t) {
			throw new RuntimeException("Failed to execute " + methodName + " on " + _modelProcessor + ".", t);
		}
	}

	public void findModelProcessor(String modelProcessorClassName, boolean throwExceptionIfMissing) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		try {
			Class modelProcessorClass = Class.forName(modelProcessorClassName);
			_modelProcessor = modelProcessorClass.newInstance();
		} catch (ClassNotFoundException e) {
			// System.out.println("EOFSQLGenerator.getModelProcessor: Missing model processor "
			// + modelProcessorClassName);
			if (throwExceptionIfMissing) {
				throw e;
			}
		} catch (InstantiationException e) {
			if (throwExceptionIfMissing) {
				throw e;
			}
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			if (throwExceptionIfMissing) {
				throw e;
			}
			e.printStackTrace();
		} catch (RuntimeException e) {
			if (throwExceptionIfMissing) {
				throw e;
			}
			e.printStackTrace();
		}
	}

	public void executeSQL(String sql) throws SQLException {
		try {
			executeSQLReflect(sql);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void executeSQLReflect(String sql) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		fixClassPath();
		Class<?> eodatabasecontext = Class.forName("com.webobjects.eoaccess.EODatabaseContext");
		Class<?> eodatabase = Class.forName("com.webobjects.eoaccess.EODatabase");
		Class<?> eodatabasechannel = Class.forName("com.webobjects.eoaccess.EODatabaseChannel");
		Class<?> eomodel = Class.forName("com.webobjects.eoaccess.EOModel");
		Class<?> eoadaptorcontext = Class.forName("com.webobjects.eoaccess.EOAdaptorContext");
		Class<?> eoadaptorchannel = Class.forName("com.webobjects.eoaccess.EOAdaptorChannel");
		Class<?> jdbccontext = Class.forName("com.webobjects.jdbcadaptor.JDBCContext");
		Class<?> nsarray = Class.forName("com.webobjects.foundation.NSArray");
		Class<?> nsmutarray = Class.forName("com.webobjects.foundation.NSMutableArray");
		
		Object db = eodatabase.getConstructor(eomodel).newInstance(_model);
		Object dbc = eodatabasecontext.getConstructor(eodatabase).newInstance(db);
		Object ac = eodatabasecontext.getMethod("adaptorContext").invoke(dbc);

		Object beforeOpenChannels = nsmutarray.getConstructor().newInstance();
		Object channelsarr = eoadaptorcontext.getMethod("channels").invoke(ac);
		Enumeration beforeChannelsEnum = (Enumeration) nsarray.getMethod("objectEnumerator").invoke(channelsarr);
		while (beforeChannelsEnum.hasMoreElements()) {
			Object channel = beforeChannelsEnum.nextElement();
			Boolean isOpen = (Boolean) eoadaptorchannel.getMethod("isOpen").invoke(channel);
			if (isOpen) {
				nsmutarray.getMethod("addObject", Object.class).invoke(beforeOpenChannels, channel);
			}
		}
		try {
			Object databaseChannel = eodatabasecontext.getMethod("availableChannel").invoke(dbc);
			Object adaptorChannel = eodatabasechannel.getMethod("adaptorChannel").invoke(databaseChannel);
			Boolean channelOpen = (Boolean) eoadaptorchannel.getMethod("isOpen").invoke(adaptorChannel);
			if (!channelOpen) {
				eoadaptorchannel.getMethod("openChannel").invoke(adaptorChannel);
			}
			try {
				Object jdbcContext = eoadaptorchannel.getMethod("adaptorContext").invoke(adaptorChannel);
				try {
					jdbccontext.getMethod("beginTransaction").invoke(jdbcContext);
					Connection conn = (Connection) jdbccontext.getMethod("connection").invoke(jdbcContext);
					Statement stmt = conn.createStatement();
					stmt.executeUpdate(sql);
					conn.commit();
				} catch (SQLException sqlexception) {
					sqlexception.printStackTrace(System.out);
					jdbccontext.getMethod("rollbackTransaction").invoke(jdbcContext);
					throw sqlexception;
				}
			} finally {
				if (!channelOpen) {
					eoadaptorchannel.getMethod("closeChannel").invoke(adaptorChannel);
				}
			}
		} finally {
			Object channelsarr2 = eoadaptorcontext.getMethod("channels").invoke(ac);
			Enumeration afterChannelsEnum = (Enumeration) nsarray.getMethod("objectEnumerator").invoke(channelsarr2);
			while (afterChannelsEnum.hasMoreElements()) {
				Object channel = afterChannelsEnum.nextElement();
				Boolean isOpen = (Boolean) eoadaptorchannel.getMethod("isOpen").invoke(channel);
				Boolean contains = (Boolean) nsmutarray.getMethod("containsObject", Object.class).invoke(beforeOpenChannels, channel);
				if (isOpen && !contains) {
					eoadaptorchannel.getMethod("closeChannel").invoke(channel);
				}
			}
		}
	}

	public Map externalTypes() {
		try {
			return externalTypesReflect();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	private Map externalTypesReflect() throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<?> eodatabasecontext = Class.forName("com.webobjects.eoaccess.EODatabaseContext");
		Class<?> eodatabase = Class.forName("com.webobjects.eoaccess.EODatabase");
		Class<?> eomodel = Class.forName("com.webobjects.eoaccess.EOModel");
		Class<?> eoadaptorcontext = Class.forName("com.webobjects.eoaccess.EOAdaptorContext");
		Class<?> jdbcadaptor = Class.forName("com.webobjects.jdbcadaptor.JDBCAdaptor");
		Class<?> jdbcplugin = Class.forName("com.webobjects.jdbcadaptor.JDBCPlugIn");
		Object db = eodatabase.getConstructor(eomodel).newInstance(_model);
		Object dbc = eodatabasecontext.getConstructor(eodatabase).newInstance(db);
		Object ac = eodatabasecontext.getMethod("adaptorContext").invoke(dbc);
		Object ad = eoadaptorcontext.getMethod("adaptor").invoke(ac);
		Object plug = jdbcadaptor.getMethod("plugIn").invoke(ad);
		Object jdbc2Info = jdbcplugin.getMethod("jdbcInfo").invoke(plug);
		return (Map) EOFSQLUtils53.toJavaCollections(jdbc2Info);
	}

	
}
