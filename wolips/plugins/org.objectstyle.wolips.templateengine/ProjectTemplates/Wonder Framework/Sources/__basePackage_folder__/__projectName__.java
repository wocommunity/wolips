package $basePackage;

import er.extensions.ERXFrameworkPrincipal;

public class ${WOLipsContext.getProjectName()} extends ERXFrameworkPrincipal {
	protected static ${WOLipsContext.getProjectName()} sharedInstance;
	@SuppressWarnings("unchecked")
	public final static Class<? extends ERXFrameworkPrincipal> REQUIRES[] = new Class[] {};

	static {
		setUpFrameworkPrincipalClass(${WOLipsContext.getProjectName()}.class);
	}

	public static ${WOLipsContext.getProjectName()} sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = sharedInstance(${WOLipsContext.getProjectName()}.class);
		}
		return sharedInstance;
	}

	@Override
	public void finishInitialization() {
		log.debug("${WOLipsContext.getProjectName()} loaded");
	}
}
