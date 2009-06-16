package $serverSideEOFPackage;

import com.webobjects.eoaccess.EODatabaseContext;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.eocontrol.EOGlobalID;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;

public class MyGenericRecord extends EOGenericRecord {

	public MyGenericRecord() {
		super();
	}

	public EOQualifier qualifierForRelationshipWithKey(String aKey) {
		willRead();

		if (aKey != null) {
			String anEntityName = entityName();
			EOEntity anEntity = EOModelGroup.defaultGroup().entityNamed(
					anEntityName);
			EORelationship aRelationship = anEntity.relationshipNamed(aKey);

			if (aRelationship != null) {
				EOEditingContext anEditingContext = editingContext();
				EOGlobalID aGlobalID = anEditingContext.globalIDForObject(this);
				String aModelName = anEntity.model().name();
				EODatabaseContext aDatabaseContext = EOUtilities
						.databaseContextForModelNamed(anEditingContext,
								aModelName);
				aDatabaseContext.lock();
				NSDictionary aRow = aDatabaseContext
						.snapshotForGlobalID(aGlobalID);
				aDatabaseContext.unlock();
				EOQualifier aQualifier = aRelationship
						.qualifierWithSourceRow(aRow);

				return aQualifier;
			}
		}

		return null;
	}

}
