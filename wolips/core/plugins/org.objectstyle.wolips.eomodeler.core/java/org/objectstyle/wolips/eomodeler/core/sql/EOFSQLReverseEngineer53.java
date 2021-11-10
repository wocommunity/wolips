package org.objectstyle.wolips.eomodeler.core.sql;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public class EOFSQLReverseEngineer53 implements IEOSQLReverseEngineer {
	private String _adaptorName;

	private Object _connectionDictionary;
	
	private Class<?> eoadaptor;

	private Object _adaptor;

	private Class<?> eoadaptorchannel;
	
	private Object _channel;

	public EOFSQLReverseEngineer53(String adaptorName, Map connectionDictionary) {
		try {
			init(adaptorName, connectionDictionary);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	private void init(String adaptorName, Map connectionDictionary) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		eoadaptor = Class.forName("com.webobjects.eoaccess.EOAdaptor");
		Class<?> nsdictionary = Class.forName("com.webobjects.foundation.NSDictionary");
		_adaptorName = adaptorName;
		_adaptor = eoadaptor.getMethod("adaptorWithName", String.class).invoke(null, adaptorName);
		_connectionDictionary = EOFSQLUtils53.toWOCollections(connectionDictionary);
		eoadaptor.getMethod("setConnectionDictionary", nsdictionary).invoke(_adaptor, _connectionDictionary);
		eoadaptor.getMethod("assertConnectionDictionaryIsValid").invoke(_adaptor);
	}

	public void open() {
		try {
			openReflect();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	private void openReflect() throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<?> eoadaptorcontext = Class.forName("com.webobjects.eoaccess.EOAdaptorContext");
		eoadaptorchannel = Class.forName("com.webobjects.eoaccess.EOAdaptorChannel");
		Object context = eoadaptor.getMethod("createAdaptorContext").invoke(_adaptor);
		_channel =  eoadaptorcontext.getMethod("createAdaptorChannel").invoke(context);
		eoadaptorchannel.getMethod("openChannel").invoke(_channel);
	}

	public void close() {
		if (_channel != null) {
			try {
				eoadaptorchannel.getMethod("closeChannel").invoke(_channel);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			}
			_channel = null;
		}
	}

	public List reverseEngineerTableNames() {
		open();
		try {
			Object tableNamesArray;
			try {
				tableNamesArray = eoadaptorchannel.getMethod("describeTableNames").invoke(_channel);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			}
			return (List) EOFSQLUtils53.toJavaCollections(tableNamesArray);
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
		try {
			return reverseEngineerWithTableNamesIntoModelReflect(tableNamesList);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private File reverseEngineerWithTableNamesIntoModelReflect(List tableNamesList) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
		Class<?> eomodel = Class.forName("com.webobjects.eoaccess.EOModel");
		Class<?> nsarray = Class.forName("com.webobjects.foundation.NSArray");
		open();
		try {
			Object tableNamesArray = EOFSQLUtils53.toWOCollections(tableNamesList);
			Object eofModel = eoadaptorchannel.getMethod("describeModelWithTableNames", nsarray).invoke(_channel, tableNamesArray);
			eomodel.getMethod("beautifyNames").invoke(eofModel);
			File tempFile = File.createTempFile("EntityModeler", "tmp");
			File eomodelFolder = new File(tempFile.getParentFile(), "EM" + System.currentTimeMillis() + ".eomodeld");
			tempFile.delete();
			eomodel.getMethod("writeToFile", String.class).invoke(eofModel, eomodelFolder.getAbsolutePath());
			return eomodelFolder;
		} finally {
			close();
		}
	}
}
