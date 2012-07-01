package $componentsPackage;

import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXComponent;

import ${basePackage}.Application;
import ${basePackage}.Session;

public class BaseComponent extends ERXComponent {
	public BaseComponent(WOContext context) {
		super(context);
	}
	
	@Override
	public Application application() {
		return (Application)super.application();
	}
	
	@Override
	public Session session() {
		return (Session)super.session();
	}
}
