package $basePackage;

import er.extensions.appserver.ERXApplication;
import er.rest.routes.ERXRoute;
import er.rest.routes.ERXRouteRequestHandler;
import ${componentsPackage}.Main;
import ${restControllersPackage}.PagesController;

public class Application extends ERXApplication {
	public static void main(String[] argv) {
		ERXApplication.main(argv, Application.class);
	}

	public Application() {
		ERXApplication.log.info("Welcome to " + name() + " !");
		/* ** put your initialization code in here ** */
		setAllowsConcurrentRequestHandling(true);
		
		ERXRouteRequestHandler restRequestHandler = new ERXRouteRequestHandler();
		restRequestHandler.insertRoute(new ERXRoute("Pages", "", ERXRoute.Method.Get, PagesController.class, "mainPage"));
    	ERXRouteRequestHandler.register(restRequestHandler);
	    setDefaultRequestHandler(restRequestHandler);
	}
}