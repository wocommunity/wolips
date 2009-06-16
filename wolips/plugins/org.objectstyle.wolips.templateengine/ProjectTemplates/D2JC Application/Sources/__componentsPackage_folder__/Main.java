package $componentsPackage;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.eodistribution.WOJavaClientComponent;

public class Main extends WOComponent {
	private static final long serialVersionUID = 1L;

    public Main(WOContext aContext) {
        super(aContext);
    }

	public String javaClientLink() {
		return WOJavaClientComponent.webStartActionURL(context(),
		                                               "JavaClient");
	}


}
