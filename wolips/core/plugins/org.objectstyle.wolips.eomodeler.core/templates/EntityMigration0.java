#set ($migrationTableName = "${entity.initialLowercaseName}Table")
#if ($entity.singleTableInheritance)
		ERXMigrationTable $migrationTableName = database.existingTableNamed("$entity.externalName");
#else
		ERXMigrationTable $migrationTableName = database.newTableNamed("$entity.externalName");
#end
#foreach ($attribute in $entity.sortedAttributes)
#if ($attribute.sqlGenerationCreateProperty)
#if ($attribute.prototype.name == "longText")
		${migrationTableName}.newLargeStringColumn("${attribute.columnName}", ${attribute.sqlGenerationAllowsNullAsConstant}#if ($attribute.userInfo.default), "${attribute.userInfo.default}"#end);
#elseif ($attribute.prototype.name == "ipAddress")
		${migrationTableName}.newIpAddressColumn("${attribute.columnName}", ${attribute.sqlGenerationAllowsNullAsConstant}#if ($attribute.userInfo.default), "${attribute.userInfo.default}"#end);
#elseif ($attribute.prototype.name == "date" || ($attribute.valueClassName == "NSCalendarDate" && $attribute.valueType == "D") || $attribute.javaClassName == "java.time.LocalDate")
		${migrationTableName}.newDateColumn("${attribute.columnName}", ${attribute.sqlGenerationAllowsNullAsConstant}#if ($attribute.userInfo.default), er.extensions.foundation.ERXTimestampUtilities.timestampForString("${attribute.userInfo.default}")#end);
#elseif ($attribute.prototype.name == "jodaLocalDate")
		${migrationTableName}.newDateColumn("${attribute.columnName}", ${attribute.sqlGenerationAllowsNullAsConstant}#if ($attribute.userInfo.default), org.joda.time.LocalDate("${attribute.userInfo.default}")#end);
#elseif ($attribute.javaClassName == "String" && $attribute.width)
		${migrationTableName}.newStringColumn("${attribute.columnName}", ${attribute.width}, ${attribute.sqlGenerationAllowsNullAsConstant}#if ($attribute.userInfo.default), "${attribute.userInfo.default}"#end);
#elseif ($attribute.javaClassName == "String")
		${migrationTableName}.newStringColumn("${attribute.columnName}", ${attribute.sqlGenerationAllowsNullAsConstant}#if ($attribute.userInfo.default), "${attribute.userInfo.default}"#end);
#elseif ($attribute.javaClassName == "BigDecimal" || $attribute.javaClassName == "java.math.BigDecimal")
		${migrationTableName}.newBigDecimalColumn("${attribute.columnName}", ${attribute.precision}, ${attribute.scale}, ${attribute.sqlGenerationAllowsNullAsConstant}#if ($attribute.userInfo.default), new java.math.BigDecimal("${attribute.userInfo.default}")#end);
#elseif ($attribute.javaClassName == "Integer" && $attribute.precision)
		${migrationTableName}.newIntegerColumn("${attribute.columnName}", ${attribute.precision}, ${attribute.sqlGenerationAllowsNullAsConstant}#if ($attribute.userInfo.default), ${attribute.userInfo.default}#end);
#elseif ($attribute.javaClassName == "Integer")
		${migrationTableName}.newIntegerColumn("${attribute.columnName}", ${attribute.sqlGenerationAllowsNullAsConstant}#if ($attribute.userInfo.default), ${attribute.userInfo.default}#end);
#elseif ($attribute.javaClassName == "Long" && $attribute.precision)
		${migrationTableName}.newBigIntegerColumn("${attribute.columnName}", ${attribute.precision}, ${attribute.sqlGenerationAllowsNullAsConstant}#if ($attribute.userInfo.default), ${attribute.userInfo.default}L#end);
#elseif ($attribute.javaClassName == "Long")
		${migrationTableName}.newBigIntegerColumn("${attribute.columnName}", ${attribute.sqlGenerationAllowsNullAsConstant}#if ($attribute.userInfo.default), ${attribute.userInfo.default}L#end);
#elseif ($attribute.javaClassName == "Double")
		${migrationTableName}.newDoubleColumn("${attribute.columnName}", 0, 0, ${attribute.sqlGenerationAllowsNullAsConstant}#if ($attribute.userInfo.default), ${attribute.userInfo.default}d#end);
#elseif ($attribute.javaClassName == "Float")
		${migrationTableName}.newFloatColumn("${attribute.columnName}", ${attribute.precision}, ${attribute.scale}, ${attribute.sqlGenerationAllowsNullAsConstant}#if ($attribute.userInfo.default), ${attribute.userInfo.default}f#end);
#elseif ($attribute.javaClassName == "Boolean" && $attribute.width == 5)
		${migrationTableName}.newBooleanColumn("${attribute.columnName}", ${attribute.sqlGenerationAllowsNullAsConstant}#if ($attribute.userInfo.default), er.extensions.foundation.ERXValueUtilities.booleanValue("${attribute.userInfo.default}")#end);
#elseif ($attribute.javaClassName == "Boolean" && $attribute.externalType == "bool")
		${migrationTableName}.newFlagBooleanColumn("${attribute.columnName}", ${attribute.sqlGenerationAllowsNullAsConstant}#if ($attribute.userInfo.default), er.extensions.foundation.ERXValueUtilities.booleanValue("${attribute.userInfo.default}")#end);
#elseif ($attribute.javaClassName == "Boolean")
		${migrationTableName}.newIntBooleanColumn("${attribute.columnName}", ${attribute.sqlGenerationAllowsNullAsConstant}#if ($attribute.userInfo.default), er.extensions.foundation.ERXValueUtilities.booleanValue("${attribute.userInfo.default}")#end);
#elseif ($attribute.javaClassName == "NSTimestamp" || $attribute.javaClassName == "java.time.LocalDateTime" || $attribute.javaClassName == "java.time.OffsetDateTime")
		${migrationTableName}.newTimestampColumn("${attribute.columnName}", ${attribute.sqlGenerationAllowsNullAsConstant}#if ($attribute.userInfo.default), er.extensions.foundation.ERXTimestampUtilities.timestampForString("${attribute.userInfo.default}")#end);
#elseif ($attribute.javaClassName == "java.time.LocalTime")
		${migrationTableName}.newTimeColumn("${attribute.columnName}", ${attribute.sqlGenerationAllowsNullAsConstant});
#elseif ($attribute.javaClassName == "NSData")
		${migrationTableName}.newBlobColumn("${attribute.columnName}", ${attribute.sqlGenerationAllowsNullAsConstant});
#elseif ($attribute.javaClassName == "ERXMutableDictionary" || $attribute.javaClassName == "er.extensions.foundation.ERXMutableDictionary")
		${migrationTableName}.newBlobColumn("${attribute.columnName}", ${attribute.sqlGenerationAllowsNullAsConstant});
#elseif ($attribute.adaptorValueConversionMethodName)
#if ($attribute.factoryMethodArgumentType.ID == "EOFactoryMethodArgumentIsNSString")
		${migrationTableName}.newStringColumn("${attribute.columnName}"#if ($attribute.width), ${attribute.width}#end, ${attribute.sqlGenerationAllowsNullAsConstant}#if ($attribute.userInfo.default), "${attribute.userInfo.default}"#end);
#elseif ($attribute.factoryMethodArgumentType.ID == "EOFactoryMethodArgumentIsData")
		${migrationTableName}.newBlobColumn("${attribute.columnName}", ${attribute.sqlGenerationAllowsNullAsConstant});
#else
		FIX // Unable to create a migration for ${attribute.name} (Java Class Name: ${attribute.javaClassName}) with the factoryMethodArgumentType ${attribute.factoryMethodArgumentType.ID}
#end
#else
		FIX // Unable to create a migration for ${attribute.name} (Java Class Name: ${attribute.javaClassName})
#end
#end
#end
#if (!$entity.singleTableInheritance)
		${migrationTableName}.create();
	 	${migrationTableName}.setPrimaryKey(${entity.sqlGenerationPrimaryKeyColumnNames});
#end
#foreach ($index in $entity.sortedEntityIndexes)
		${migrationTableName}.addIndex(new ERXMigrationIndex(
			"$index.name",#if ($index.constraint.externalName == "distinct") true #else false #end

#foreach ($attribute in $index.attributes)
			,new ColumnIndex("$attribute.columnName")
#end	
		));
#end