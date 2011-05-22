import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;

import er.extensions.migration.ERXMigrationDatabase;
import er.extensions.migration.ERXMigrationTable;
import er.extensions.migration.ERXModelVersion;

public class ${model.name}0 extends ERXMigrationDatabase.Migration {
#if ($model.userInfo.ERXLanguages)
	public ${model.name}0 {
		super(ERXProperties.arrayForKey("${model.name}0.languages"));
	}
#end
	@Override
	public NSArray<ERXModelVersion> modelDependencies() {
		return null;
	}
  
	@Override
	public void downgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		// DO NOTHING
	}

	@Override
	public void upgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
#foreach ($entityMigration in $entityMigrations)${entityMigration}
#end
#foreach ($foreignKeyMigration in $foreignKeyMigrations)${foreignKeyMigration}#end
	}
}