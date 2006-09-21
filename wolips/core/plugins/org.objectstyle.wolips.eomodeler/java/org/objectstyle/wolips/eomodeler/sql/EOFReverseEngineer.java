package org.objectstyle.wolips.eomodeler.sql;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.webobjects.eoaccess.EOAdaptor;
import com.webobjects.eoaccess.EOAdaptorChannel;
import com.webobjects.eoaccess.EOAdaptorContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;

public class EOFReverseEngineer {
	private String _adaptorName;

	private NSDictionary _connectionDictionary;

	private EOAdaptor _adaptor;

	private EOAdaptorContext _context;

	private EOAdaptorChannel _channel;

	public EOFReverseEngineer(String adaptorName, Map connectionDictionary) {
		_adaptorName = adaptorName;
		_connectionDictionary = new NSDictionary(connectionDictionary, false);
		_adaptor = EOAdaptor.adaptorWithName(_adaptorName);
		_adaptor.setConnectionDictionary(_connectionDictionary);
		_adaptor.assertConnectionDictionaryIsValid();
	}

	public void open() {
		_context = _adaptor.createAdaptorContext();
		_channel = _context.createAdaptorChannel();
	}

	public void close() {
		if (_channel != null) {
			_channel.closeChannel();
		}
	}

	public List reverseEngineerTableNames() {
		NSArray tableNamesArray = _channel.describeTableNames();
		LinkedList tableNamesList = new LinkedList(tableNamesArray);
		return tableNamesList;
	}

	public void reverseEngineerModelWithTableNamesTo(List tableNamesList, File _folder) {
		NSArray tableNamesArray = new NSArray(tableNamesList, false);
		com.webobjects.eoaccess.EOModel eofModel = _channel.describeModelWithTableNames(tableNamesArray);
		eofModel.writeToFile(_folder.getAbsolutePath());
	}
}
