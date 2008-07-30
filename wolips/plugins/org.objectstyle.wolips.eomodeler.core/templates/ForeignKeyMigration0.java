#set ($migrationTableName = "${entity.initialLowercaseName}Table")
#foreach ($relationship in $entity.sortedToOneRelationships)
#if ($relationship.sqlGenerationCreateProperty)
#foreach ($join in $relationship.joins)
		${migrationTableName}.addForeignKey("${join.sourceAttribute.columnName}", "${relationship.destination.externalName}", "${join.destinationAttribute.columnName}");
#end
#end
#end\ No newline at end of file
