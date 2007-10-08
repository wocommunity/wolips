package org.objectstyle.wolips.wooeditor.eomodel;

import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EOModelMap;

public final class EODataSourceFactory {
	private EODataSourceFactory() {
	}

	public static EODataSource createDataSourceFromMap(
			final EOModelMap dataSourceMap, final EOModelGroup modelGroup) {
		EODataSource dataSource = null;
		if (dataSourceMap != null) {
			String className = dataSourceMap.getString("class", true);
			if ("EODatabaseDataSource".equals(className)) {
				dataSource = new EODatabaseDataSource(modelGroup);
			} else if ("EODetailDataSource".equals(className)) {
				dataSource = new EODetailDataSource(modelGroup);
			} else {
				throw new IllegalArgumentException(
						"Unknown datasource className '" + className + "'.");
			}
		}
		return dataSource;
	}
}
