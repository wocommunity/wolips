package $basePackage;

import com.webobjects.foundation.NSLog;

import com.webobjects.appserver.WOApplication;

public class Application extends WOApplication {
	public static void main(String[] argv) {
		WOApplication.main(argv, Application.class);
	}

	public Application() {
		NSLog.out.appendln("Welcome to " + name() + " !");
		/* ** put your initialization code in here ** */
		setAllowsConcurrentRequestHandling(true);
	}
}
