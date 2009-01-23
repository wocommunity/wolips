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

import com.webobjects.eoaccess.EOAdaptorChannel;
import com.webobjects.eoaccess.EOAdaptorContext;
import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EODatabase;
import com.webobjects.eoaccess.EODatabaseChannel;
import com.webobjects.eoaccess.EODatabaseContext;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOModel;
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.eoaccess.EOSchemaGeneration;
import com.webobjects.eoaccess.EOSynchronizationFactory;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.jdbcadaptor.JDBCAdaptor;
import com.webobjects.jdbcadaptor.JDBCContext;

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
public class EOFSQLGenerator implements IEOSQLGenerator {
	private NSMutableArray _entities;

	private EOModel _model;

	private EOModelGroup _modelGroup;

	private Object _modelProcessor;

	public EOFSQLGenerator(String modelName, List modelURLs, List entityNames, Map selectedDatabaseConfig, boolean runInEntityModeler) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		fixClassPath();
		Map databaseConfig = selectedDatabaseConfig;
		if (databaseConfig == null) {
			databaseConfig = new HashMap();
		}

		_modelGroup = new EOModelGroup();

		Iterator modelURLIter = modelURLs.iterator();
		while (modelURLIter.hasNext()) {
			URL modelURL = (URL) modelURLIter.next();
			_modelGroup.addModelWithPathURL(modelURL);
		}

		String prototypeEntityName = (String) databaseConfig.get("prototypeEntityName");
		if (prototypeEntityName != null) {
			replacePrototypes(_modelGroup, prototypeEntityName);
		}

		_entities = new NSMutableArray();
		_model = _modelGroup.modelNamed(modelName);
		
		NSDictionary defaultConnectionDictionary;
		Map overrideConnectionDictionary = (Map) databaseConfig.get("connectionDictionary");
		if (overrideConnectionDictionary != null) {
			NSDictionary connectionDictionary = (NSDictionary) EOFSQLUtils.toWOCollections(overrideConnectionDictionary);
			_model.setConnectionDictionary(connectionDictionary);
			String eomodelProcessorClassName = (String) connectionDictionary.objectForKey("eomodelProcessorClassName");
			if (eomodelProcessorClassName != null) {
				findModelProcessor(eomodelProcessorClassName, true);
			}
			defaultConnectionDictionary = connectionDictionary;
		}
		else {
			defaultConnectionDictionary = _model.connectionDictionary();
		}
		
		Enumeration modelsEnum = _modelGroup.models().objectEnumerator();
		while (modelsEnum.hasMoreElements()) {
			EOModel model = (EOModel)modelsEnum.nextElement();
			if (model.connectionDictionary() == null) {
				model.setConnectionDictionary(defaultConnectionDictionary);
			}
		}

		if (_modelProcessor == null) {
			findModelProcessor("org.objectstyle.wolips.eomodeler.EOModelProcessor", false);
		}
		if (entityNames == null || entityNames.size() == 0) {
			Enumeration entitiesEnum = _model.entities().objectEnumerator();
			while (entitiesEnum.hasMoreElements()) {
				EOEntity entity = (EOEntity) entitiesEnum.nextElement();
				if (!isPrototype(entity)) {// &&
					// entityUsesSeparateTable(entity))
					// {
					_entities.addObject(entity);
				}
			}
		} else {
			Iterator entityNamesIter = entityNames.iterator();
			while (entityNamesIter.hasNext()) {
				String entityName = (String) entityNamesIter.next();
				EOEntity entity = _model.entityNamed(entityName);
				if (entity != null) {
					_entities.addObject(entity);
				}
			}
		}
		
		// MS: Remove jdbc2Info prior to SQL generation
		NSDictionary connectionDictionary = _model.connectionDictionary();
		if (connectionDictionary != null) {
			NSMutableDictionary mutableConnectionDictionary = connectionDictionary.mutableClone();
			mutableConnectionDictionary.removeObjectForKey("jdbc2Info");
			_model.setConnectionDictionary(mutableConnectionDictionary);
		}
		
		// MS: Add the "inEntityModeler" flag so that plugins can adjust their behavior
		// if they need to.
		if (runInEntityModeler) {
			NSMutableDictionary modelUserInfo = _model.userInfo().mutableClone();
			NSDictionary entityModelerDict = (NSDictionary) modelUserInfo.objectForKey("_EntityModeler");
			NSMutableDictionary mutableEntityModelerDict;
			if (entityModelerDict == null) {
				mutableEntityModelerDict = new NSMutableDictionary();
			}
			else {
				mutableEntityModelerDict = entityModelerDict.mutableClone();
			}
			mutableEntityModelerDict.setObjectForKey(Boolean.TRUE, "inEntityModeler");
			modelUserInfo.setObjectForKey(mutableEntityModelerDict, "_EntityModeler");
			_model.setUserInfo(modelUserInfo);
		}

		ensureSingleTableInheritanceParentEntitiesAreIncluded();
		ensureSingleTableInheritanceChildEntitiesAreIncluded();
		fixAllowsNullOnSingleTableInheritance();
		localizeEntities();
	}

	protected void replacePrototypes(EOModelGroup modelGroup, String prototypeEntityName) {
		String replacementPrototypeName = "EOPrototypes";
		
		// Don't replace prototypes if the selected prototype entity name doesn't exist
		if (modelGroup.entityNamed(prototypeEntityName) == null) {
			return;
		}
		
		// Don't replace prototypes if you're already just using "EOPrototypes"
		if (replacementPrototypeName.equals(prototypeEntityName)) {
			return;
		}
		
		NSMutableDictionary removedPrototypeEntities = new NSMutableDictionary();

		EOModel prototypesModel = null;
		Enumeration modelsEnum = modelGroup.models().objectEnumerator();
		while (modelsEnum.hasMoreElements()) {
			EOModel model = (EOModel) modelsEnum.nextElement();
			EOEntity eoAdaptorPrototypesEntity = _modelGroup.entityNamed("EO" + model.adaptorName() + "Prototypes");
			if (eoAdaptorPrototypesEntity != null) {
				prototypesModel = eoAdaptorPrototypesEntity.model();
				// System.out.println("EOFSQLGenerator.EOFSQLGenerator:
				// removing " + eoAdaptorPrototypesEntity.name() + " from "
				// + prototypesModel.name());
				prototypesModel.removeEntity(eoAdaptorPrototypesEntity);
				removedPrototypeEntities.setObjectForKey(eoAdaptorPrototypesEntity, eoAdaptorPrototypesEntity.name());
			}
		}

		EOEntity eoPrototypesEntity = _modelGroup.entityNamed("EOPrototypes");
		if (eoPrototypesEntity != null) {
			prototypesModel = eoPrototypesEntity.model();
			prototypesModel.removeEntity(eoPrototypesEntity);
			// System.out.println("EOFSQLGenerator.EOFSQLGenerator: removing
			// " + eoPrototypesEntity.name() + " from " +
			// prototypesModel.name());
			removedPrototypeEntities.setObjectForKey(eoPrototypesEntity, eoPrototypesEntity.name());
		}

		EOEntity prototypesEntity = _modelGroup.entityNamed(prototypeEntityName);
		if (prototypesEntity == null) {
			prototypesEntity = (EOEntity) removedPrototypeEntities.objectForKey(prototypeEntityName);
		} else {
			prototypesModel = prototypesEntity.model();
			prototypesModel.removeEntity(prototypesEntity);
		}
		if (prototypesEntity != null && prototypesModel != null) {
			// System.out.println("EOFSQLGenerator.EOFSQLGenerator: setting
			// " + prototypesEntity.name() + " to EOPrototypes in " +
			// prototypesModel.name());
			prototypesEntity.setName(replacementPrototypeName);
			prototypesModel.addEntity(prototypesEntity);
		}

		Enumeration resetModelsEnum = _modelGroup.models().objectEnumerator();
		while (resetModelsEnum.hasMoreElements()) {
			EOModel model = (EOModel) resetModelsEnum.nextElement();
			model._resetPrototypeCache();
		}
	}

	protected void localizeEntities() {
		Enumeration entitiesEnum = new NSArray(_entities).objectEnumerator();
		while (entitiesEnum.hasMoreElements()) {
			EOEntity entity = (EOEntity) entitiesEnum.nextElement();
			createLocalizedAttributes(entity);
		}
	}
	
	protected void fixAllowsNullOnSingleTableInheritance() {
		Enumeration entitiesEnum = new NSArray(_entities).objectEnumerator();
		while (entitiesEnum.hasMoreElements()) {
			EOEntity entity = (EOEntity) entitiesEnum.nextElement();
			if (isSingleTableInheritance(entity)) {
				Enumeration attributeEnum = entity.attributes().objectEnumerator();
				while (attributeEnum.hasMoreElements()) {
					EOAttribute attribute = (EOAttribute) attributeEnum.nextElement();
					if (!isInherited(attribute)) {
						attribute.setAllowsNull(true);
					}
				}
			}
		}
	}

	protected void ensureSingleTableInheritanceParentEntitiesAreIncluded() {
		Enumeration entitiesEnum = new NSArray(_entities).objectEnumerator();
		while (entitiesEnum.hasMoreElements()) {
			EOEntity entity = (EOEntity) entitiesEnum.nextElement();
			ensureSingleTableInheritanceParentEntitiesAreIncluded(entity);
		}
	}

	protected void ensureSingleTableInheritanceChildEntitiesAreIncluded() {
		Enumeration entitiesEnum = _model.entities().objectEnumerator();
		while (entitiesEnum.hasMoreElements()) {
			EOEntity entity = (EOEntity) entitiesEnum.nextElement();
			if (isSingleTableInheritance(entity)) {
				EOEntity parentEntity = entity.parentEntity();
				if (_entities.containsObject(parentEntity) && !_entities.containsObject(entity)) {
					_entities.addObject(entity);
				}
			}
		}
	}

	protected void ensureSingleTableInheritanceParentEntitiesAreIncluded(EOEntity entity) {
		if (isSingleTableInheritance(entity)) {
			EOEntity parentEntity = entity.parentEntity();
			if (!_entities.containsObject(parentEntity)) {
				_entities.addObject(parentEntity);
				ensureSingleTableInheritanceParentEntitiesAreIncluded(entity);
			}
		}
	}

	protected boolean isPrototype(EOEntity _entity) {
		String entityName = _entity.name();
		boolean isPrototype = (entityName.startsWith("EO") && entityName.endsWith("Prototypes"));
		return isPrototype;
	}

	protected boolean isSingleTableInheritance(EOEntity entity) {
		EOEntity parentEntity = entity.parentEntity();
		return parentEntity != null && entity.externalName() != null && entity.externalName().equalsIgnoreCase(parentEntity.externalName());
	}

	protected void createLocalizedAttributes(EOEntity entity) {
		NSArray attributes = entity.attributes().immutableClone();
		NSArray classProperties = entity.classProperties().immutableClone();
		NSArray attributesUsedForLocking = entity.attributesUsedForLocking().immutableClone();
		if (attributes == null) {
			attributes = NSArray.EmptyArray;
		}
		if (classProperties == null) {
			classProperties = NSArray.EmptyArray;
		}
		if (attributesUsedForLocking == null) {
			attributesUsedForLocking = NSArray.EmptyArray;
		}
		NSMutableArray mutableClassProperties = classProperties.mutableClone();
		NSMutableArray mutableAttributesUsedForLocking = attributesUsedForLocking.mutableClone();
		for (Enumeration e = attributes.objectEnumerator(); e.hasMoreElements();) {
			EOAttribute attribute = (EOAttribute) e.nextElement();
			NSDictionary userInfo = attribute.userInfo();
			String name = attribute.name();
			if (userInfo != null) {
				Object l = userInfo.objectForKey("ERXLanguages");
				if (l != null && !(l instanceof NSArray)) {
					l = (entity.model().userInfo() != null ? entity.model().userInfo().objectForKey("ERXLanguages") : null);
				}

				NSArray languages = (NSArray) l;
				if (languages != null && languages.count() > 0) {
					String columnName = attribute.columnName();
					for (int i = 0; i < languages.count(); i++) {
						String language = (String) languages.objectAtIndex(i);
						String newName = name + "_" + language;
						String newColumnName = columnName + "_" + language;

						EOAttribute newAttribute = new EOAttribute();
						newAttribute.setName(newName);
						entity.addAttribute(newAttribute);

						newAttribute.setPrototype(attribute.prototype());
						newAttribute.setColumnName(newColumnName);
						newAttribute.setAllowsNull(attribute.allowsNull());
						newAttribute.setClassName(attribute.className());
						newAttribute.setExternalType(attribute.externalType());
						newAttribute.setWidth(attribute.width());
						newAttribute.setUserInfo(attribute.userInfo());

						if (classProperties.containsObject(attribute)) {
							mutableClassProperties.addObject(newAttribute);
						}
						if (attributesUsedForLocking.containsObject(attribute)) {
							mutableAttributesUsedForLocking.addObject(newAttribute);
						}
					}
					entity.removeAttribute(attribute);
					mutableClassProperties.removeObject(attribute);
					mutableAttributesUsedForLocking.removeObject(attribute);
				}
			}
			entity.setClassProperties(mutableClassProperties);
			entity.setAttributesUsedForLocking(mutableAttributesUsedForLocking);
		}
	}

	protected boolean isInherited(EOAttribute attribute) {
		boolean inherited = false;
		EOEntity parentEntity = attribute.entity().parentEntity();
		while (!inherited && parentEntity != null) {
			inherited = (parentEntity.attributeNamed(attribute.name()) != null);
			parentEntity = parentEntity.parentEntity();
		}
		return inherited;
	}

	protected void fixDuplicateSingleTableInheritanceDropStatements(EOSynchronizationFactory syncFactory, NSMutableDictionary flags, StringBuffer sqlBuffer) {
		if ("YES".equals(flags.objectForKey(EOSchemaGeneration.DropTablesKey))) {
			NSMutableArray dropEntities = new NSMutableArray(_entities);
			for (int entityNum = dropEntities.count() - 1; entityNum >= 0; entityNum--) {
				EOEntity entity = (EOEntity) dropEntities.objectAtIndex(entityNum);
				if (isSingleTableInheritance(entity)) {
					dropEntities.removeObjectAtIndex(entityNum);
				}
			}
			if (dropEntities.count() != _entities.count()) {
				NSMutableDictionary dropFlags = new NSMutableDictionary();
				dropFlags.setObjectForKey("YES", EOSchemaGeneration.DropTablesKey);
				dropFlags.setObjectForKey("NO", EOSchemaGeneration.DropPrimaryKeySupportKey);
				dropFlags.setObjectForKey("NO", EOSchemaGeneration.CreateTablesKey);
				dropFlags.setObjectForKey("NO", EOSchemaGeneration.CreatePrimaryKeySupportKey);
				dropFlags.setObjectForKey("NO", EOSchemaGeneration.PrimaryKeyConstraintsKey);
				dropFlags.setObjectForKey("NO", EOSchemaGeneration.ForeignKeyConstraintsKey);
				dropFlags.setObjectForKey("NO", EOSchemaGeneration.CreateDatabaseKey);
				dropFlags.setObjectForKey("NO", EOSchemaGeneration.DropDatabaseKey);
				flags.setObjectForKey("NO", EOSchemaGeneration.DropTablesKey);
				String dropSql = syncFactory.schemaCreationScriptForEntities(dropEntities, dropFlags);
				sqlBuffer.append(dropSql);
				sqlBuffer.append("\n");
			}
		}
	}

	private String getClassPath() {
		URL urls[] = ((URLClassLoader)getClass().getClassLoader()).getURLs();
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
		fixClassPath();
		NSMutableDictionary flags = new NSMutableDictionary();
		if (flagsMap != null) {
			Iterator entriesIter = flagsMap.entrySet().iterator();
			while (entriesIter.hasNext()) {
				Map.Entry flag = (Map.Entry) entriesIter.next();
				flags.setObjectForKey(flag.getValue(), flag.getKey());
			}
		}

		callModelProcessorMethodIfExists("processModel", new Object[] { _model, _entities, flags });

		EODatabaseContext dbc = new EODatabaseContext(new EODatabase(_model));
		EOAdaptorContext ac = dbc.adaptorContext();
		EOSynchronizationFactory sf = ((JDBCAdaptor) ac.adaptor()).plugIn().synchronizationFactory();

		NSMutableArray beforeOpenChannels = new NSMutableArray();
		Enumeration beforeChannelsEnum = ac.channels().objectEnumerator();
		while (beforeChannelsEnum.hasMoreElements()) {
			EOAdaptorChannel channel = (EOAdaptorChannel)beforeChannelsEnum.nextElement();
			if (channel.isOpen()) {
				beforeOpenChannels.addObject(channel);
			}
		}

		StringBuffer sqlBuffer = new StringBuffer();
		fixDuplicateSingleTableInheritanceDropStatements(sf, flags, sqlBuffer);

		try {
			String sql = sf.schemaCreationScriptForEntities(_entities, flags);
			sql = sql.replaceAll("CREATE TABLE ([^\\s(]+)\\(", "CREATE TABLE $1 (");
			sqlBuffer.append(sql);

			callModelProcessorMethodIfExists("processSQL", new Object[] { sqlBuffer, _model, _entities, flags });
		}
		finally {
			Enumeration afterChannelsEnum = ac.channels().objectEnumerator();
			while (afterChannelsEnum.hasMoreElements()) {
				EOAdaptorChannel channel = (EOAdaptorChannel)afterChannelsEnum.nextElement();
				if (channel.isOpen() && !beforeOpenChannels.containsObject(channel)) {
					channel.closeChannel();
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
			//System.out.println("EOFSQLGenerator.getModelProcessor: Missing model processor " + modelProcessorClassName);
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
		fixClassPath();
		EODatabaseContext databaseContext = new EODatabaseContext(new EODatabase(_model));
		EOAdaptorContext adaptorContext = databaseContext.adaptorContext();

		NSMutableArray beforeOpenChannels = new NSMutableArray();
		Enumeration beforeChannelsEnum = adaptorContext.channels().objectEnumerator();
		while (beforeChannelsEnum.hasMoreElements()) {
			EOAdaptorChannel channel = (EOAdaptorChannel)beforeChannelsEnum.nextElement();
			if (channel.isOpen()) {
				beforeOpenChannels.addObject(channel);
			}
		}
		try {
			EODatabaseChannel databaseChannel = databaseContext.availableChannel();
			EOAdaptorChannel adaptorChannel = databaseChannel.adaptorChannel();
			boolean channelOpen = adaptorChannel.isOpen();
			if (!channelOpen) {
				adaptorChannel.openChannel();
			}
			try {
				JDBCContext jdbccontext = (JDBCContext) adaptorChannel.adaptorContext();
				try {
					jdbccontext.beginTransaction();
					Connection conn = jdbccontext.connection();
					Statement stmt = conn.createStatement();
					stmt.executeUpdate(sql);
					conn.commit();
				} catch (SQLException sqlexception) {
					sqlexception.printStackTrace(System.out);
					jdbccontext.rollbackTransaction();
					throw sqlexception;
				}
			}
			finally {
				if (!channelOpen) {
					adaptorChannel.closeChannel();
				}
			}
		}
		finally {
			Enumeration afterChannelsEnum = adaptorContext.channels().objectEnumerator();
			while (afterChannelsEnum.hasMoreElements()) {
				EOAdaptorChannel channel = (EOAdaptorChannel)afterChannelsEnum.nextElement();
				if (channel.isOpen() && !beforeOpenChannels.containsObject(channel)) {
					channel.closeChannel();
				}
			}
		}
	}

	public Map externalTypes() {
		EODatabaseContext dbc = new EODatabaseContext(new EODatabase(_model));
		EOAdaptorContext ac = dbc.adaptorContext();
		NSDictionary jdbc2Info = ((JDBCAdaptor) ac.adaptor()).plugIn().jdbcInfo();
		return (Map) EOFSQLUtils.toJavaCollections(jdbc2Info);
	}
}
