package org.objectstyle.wolips.eomodeler.core.model;

public class EOModelRenderContext {
	private static ThreadLocal<EOModelRenderContext> _renderContext = new ThreadLocal<EOModelRenderContext>();

	public static EOModelRenderContext getInstance() {
		EOModelRenderContext renderContext = _renderContext.get();
		if (renderContext == null) {
			renderContext = new EOModelRenderContext();
		}
		return renderContext;
	}

	public static void setRenderContext(EOModelRenderContext renderContext) {
		_renderContext.set(renderContext);
	}

	public static void clearRenderContext() {
		_renderContext.remove();
	}

	private String _prefix;

	private String _eogenericRecordClassName;

	private String _superclassPackage;
	
	private boolean _javaClient;
	
	private boolean _javaClientCommon;

	public EOModelRenderContext() {
		_prefix = "_";
		_eogenericRecordClassName = "com.webobjects.eocontrol.EOGenericRecord";
	}
	
	public void setJavaClient(boolean javaClient) {
		_javaClient = javaClient;
	}
	
	public boolean isJavaClient() {
		return _javaClient;
	}
	
	public void setJavaClientCommon(boolean javaClientCommon) {
		_javaClientCommon = javaClientCommon;
	}
	
	public boolean isJavaClientCommon() {
		return _javaClientCommon;
	}

	public void setPrefix(String prefix) {
		_prefix = prefix;
	}

	public String getPrefix() {
		return _prefix;
	}

	public void setSuperclassPackage(String superclassPackage) {
		_superclassPackage = superclassPackage;
	}

	public String getSuperclassPackage() {
		return _superclassPackage;
	}

	public void setEOGenericRecordClassName(String eogenericRecordClassName) {
		_eogenericRecordClassName = eogenericRecordClassName;
	}

	public String getEOGenericRecordClassName() {
		return _eogenericRecordClassName;
	}

	public String getClassNameForEntity(EOEntity entity) {
	  String className;
	  if (_javaClientCommon) {
		className = entity.getParentClassName();
	  }
	  else if (_javaClient) {
	    className = entity.getClientClassName();
	  }
	  else {
	    className = entity.getClassName();
	  }
	  return className;
	}
}
