#set ($migrationTableName = "${entity.initialLowercaseName}Table")
#if ($entity.singleTableInheritance)
		ERXMigrationTable $migrationTableName = database.existingTableNamed("$entity.externalName");
#else
		ERXMigrationTable $migrationTableName = database.newTableNamed("$entity.externalName");
#end
#foreach ($attribute in $entity.sortedAttributes)
#if ($attribute.sqlGenerationCreateProperty)
		#if ($attribute.javaClassName == "String")${migrationTableName}.newStringColumn("${attribute.columnName}", ${attribute.width}, ${attribute.sqlGenerationAllowsNull});
#elseif ($attribute.javaClassName == "BigDecimal")${migrationTableName}.newBigDecimalColumn("${attribute.columnName}", ${attribute.precision}, ${attribute.scale}, ${attribute.sqlGenerationAllowsNull});
#elseif ($attribute.javaClassName == "Integer" && $attribute.precision)${migrationTableName}.newIntegerColumn("${attribute.columnName}", ${attribute.precision}, ${attribute.sqlGenerationAllowsNull});
#elseif ($attribute.javaClassName == "Integer")${migrationTableName}.newIntegerColumn("${attribute.columnName}", ${attribute.sqlGenerationAllowsNull});
#elseif ($attribute.javaClassName == "Long" && $attribute.precision)${migrationTableName}.newIntegerColumn("${attribute.columnName}", ${attribute.precision}, ${attribute.sqlGenerationAllowsNull});
#elseif ($attribute.javaClassName == "Long")${migrationTableName}.newIntegerColumn("${attribute.columnName}", ${attribute.sqlGenerationAllowsNull});
#elseif ($attribute.javaClassName == "Float")${migrationTableName}.newIntegerColumn("${attribute.columnName}", ${attribute.precision}, ${attribute.scale}, ${attribute.sqlGenerationAllowsNull});
#elseif ($attribute.javaClassName == "Boolean" && $attribute.width == 5)${migrationTableName}.newBooleanColumn("${attribute.columnName}", ${attribute.sqlGenerationAllowsNull});
#elseif ($attribute.javaClassName == "Boolean")${migrationTableName}.newIntBooleanColumn("${attribute.columnName}", ${attribute.sqlGenerationAllowsNull});
#elseif ($attribute.javaClassName == "NSTimestamp")${migrationTableName}.newTimestampColumn("${attribute.columnName}", ${attribute.sqlGenerationAllowsNull});
#elseif ($attribute.javaClassName == "NSData")${migrationTableName}.newBlobColumn("${attribute.columnName}", ${attribute.sqlGenerationAllowsNull});
#else FIX: // Unable to create a migration for ${attribute.name} (Java Class Name: ${attribute.javaClassName})
#end
#end
#end
#if (!$entity.singleTableInheritance)
		${migrationTableName}.create();
	 	${migrationTableName}.setPrimaryKey(${entity.sqlGenerationPrimaryKeyColumnNames});
#end		
#foreach ($relationship in $entity.sortedToOneRelationships)
#if ($relationship.sqlGenerationCreateProperty)
#foreach ($join in $relationship.joins)
		${migrationTableName}.addForeignKey("${join.sourceAttribute.columnName}", "${relationship.destination.externalName}", "${join.destinationAttribute.columnName}");
#end
#end
#end
