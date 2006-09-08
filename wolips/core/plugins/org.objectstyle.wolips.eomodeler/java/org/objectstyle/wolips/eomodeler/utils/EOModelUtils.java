package org.objectstyle.wolips.eomodeler.utils;

import org.objectstyle.wolips.eomodeler.model.EOArgument;
import org.objectstyle.wolips.eomodeler.model.EODatabaseConfig;
import org.objectstyle.wolips.eomodeler.model.EOEntity;
import org.objectstyle.wolips.eomodeler.model.EOModel;
import org.objectstyle.wolips.eomodeler.model.EOStoredProcedure;
import org.objectstyle.wolips.eomodeler.model.IEOEntityRelative;

public class EOModelUtils {
	public static EOModel getRelatedModel(Object _obj) {
		EOModel model = null;
		if (_obj instanceof EOModel) {
			model = (EOModel) _obj;
		} else if (_obj instanceof IEOEntityRelative) {
			model = ((IEOEntityRelative) _obj).getEntity().getModel();
		} else if (_obj instanceof EOStoredProcedure) {
			model = ((EOStoredProcedure) _obj).getModel();
		} else if (_obj instanceof EOArgument) {
			model = ((EOArgument) _obj).getStoredProcedure().getModel();
		} else if (_obj instanceof EODatabaseConfig) {
			model = ((EODatabaseConfig) _obj).getModel();
		}
		return model;
	}

	public static EOEntity getRelatedEntity(Object _obj) {
		EOEntity entity = null;
		if (_obj instanceof IEOEntityRelative) {
			entity = ((IEOEntityRelative) _obj).getEntity();
		}
		return entity;
	}

	public static EOStoredProcedure getRelatedStoredProcedure(Object _obj) {
		EOStoredProcedure storedProcedure = null;
		if (_obj instanceof EOStoredProcedure) {
			storedProcedure = (EOStoredProcedure) _obj;
		} else if (_obj instanceof EOArgument) {
			storedProcedure = ((EOArgument) _obj).getStoredProcedure();
		}
		return storedProcedure;
	}
}
