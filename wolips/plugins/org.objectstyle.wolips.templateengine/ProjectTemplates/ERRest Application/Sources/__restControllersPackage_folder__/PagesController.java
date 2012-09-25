package $restControllersPackage;

import ${componentsPackage}.Main;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;

import er.rest.format.ERXRestFormat;

/**
 * This controller is for content that is not EO entities driven, e.g. "static pages"
*/
public class PagesController extends BaseRestController {

	public PagesController(WORequest request) {
		super(request);
	}

	public WOActionResults mainPageAction() {
		return pageWithName(Main.class);
	}

	@Override
	protected ERXRestFormat defaultFormat() {
		return ERXRestFormat.html();
	}

}
