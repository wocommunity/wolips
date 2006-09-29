package $package;

import com.webobjects.appserver.WOApplication;
import com.webobjects.foundation.NSLog;

public class Application extends WOApplication {
    
    public static void main(String argv[]) {
	WOApplication.main(argv, Application.class);
    }

    public Application() {
	NSLog.out.appendln("Welcome to " + this.name() + " !");
	/* ** put your initialization code in here ** */
    }
}