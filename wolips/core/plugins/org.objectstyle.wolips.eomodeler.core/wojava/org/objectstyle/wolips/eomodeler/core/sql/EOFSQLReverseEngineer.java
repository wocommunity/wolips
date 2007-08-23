package org.objectstyle.wolips.eomodeler.core.sql;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.webobjects.eoaccess.EOAdaptor;
import com.webobjects.eoaccess.EOAdaptorChannel;
import com.webobjects.eoaccess.EOAdaptorContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;

public class EOFSQLReverseEngineer implements IEOSQLReverseEngineer {
	private String _adaptorName;

	private NSDictionary _connectionDictionary;

	private EOAdaptor _adaptor;

	private EOAdaptorContext _context;

	private EOAdaptorChannel _channel;

	public EOFSQLReverseEngineer(String adaptorName, Map connectionDictionary) {
		_adaptorName = adaptorName;
		_connectionDictionary = (NSDictionary) EOFSQLUtils.toWOCollections(connectionDictionary);
		_adaptor = EOAdaptor.adaptorWithName(_adaptorName);
		_adaptor.setConnectionDictionary(_connectionDictionary);
		_adaptor.assertConnectionDictionaryIsValid();
	}

	public void open() {
		_context = _adaptor.createAdaptorContext();
		_channel = _context.createAdaptorChannel();
		_channel.openChannel();
	}

	public void close() {
		if (_channel != null) {
			_channel.closeChannel();
			_channel = null;
		}
	}

	public List reverseEngineerTableNames() {
		open();
		try {
			NSArray tableNamesArray = _channel.describeTableNames();
			return (List) EOFSQLUtils.toJavaCollections(tableNamesArray);
		} finally {
			close();
		}
	}

	public File reverseEngineerIntoModel() throws IOException {
		List tableNames = reverseEngineerTableNames();
		File eomodelFile = reverseEngineerWithTableNamesIntoModel(tableNames);
		return eomodelFile;
	}

	public File reverseEngineerWithTableNamesIntoModel(List tableNamesList) throws IOException {
		open();
		try {
			NSArray tableNamesArray = (NSArray) EOFSQLUtils.toWOCollections(tableNamesList);
			com.webobjects.eoaccess.EOModel eofModel = _channel.describeModelWithTableNames(tableNamesArray);
			eofModel.beautifyNames();
			File tempFile = File.createTempFile("EntityModeler", "tmp");
			File eomodelFolder = new File(tempFile.getParentFile(), "EM" + System.currentTimeMillis() + ".eomodeld");
			tempFile.delete();
			eofModel.writeToFile(eomodelFolder.getAbsolutePath());
			return eomodelFolder;
		} finally {
			close();
		}
	}
}
