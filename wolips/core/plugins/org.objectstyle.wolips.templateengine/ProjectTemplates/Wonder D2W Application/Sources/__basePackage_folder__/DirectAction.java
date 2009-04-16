package $basePackage;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;

import er.directtoweb.ERD2WDirectAction;

import ${componentsPackage}.Main;

public class DirectAction extends ERD2WDirectAction {

    public DirectAction(WORequest aRequest) {
        super(aRequest);
    }
   
    /**
     * Checks if a page configuration is allowed to render.
     * Provide a more intelligent access scheme as the default just returns false. And
     * be sure to read the javadoc to the super class.
     * @param pageConfiguration
     * @return
     */
    protected boolean allowPageConfiguration(String pageConfiguration) {
        return false;
    }

    public WOActionResults defaultAction() {
        return pageWithName(Main.class.getName());
    }
}
