package $restControllersPackage;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;

import er.extensions.appserver.ERXHttpStatusCodes;
import er.rest.routes.ERXDefaultRouteController;

public class BaseRestController extends ERXDefaultRouteController {

	public BaseRestController(WORequest request) {
		super(request);
	}

	public WOActionResults updateAction() throws Throwable {
		return errorResponse(ERXHttpStatusCodes.METHOD_NOT_ALLOWED);
	}

	public WOActionResults createAction() throws Throwable {
		return errorResponse(ERXHttpStatusCodes.METHOD_NOT_ALLOWED);
	}

	@Override
	public WOActionResults newAction() throws Throwable {
		return errorResponse(ERXHttpStatusCodes.METHOD_NOT_ALLOWED);
	}

	@Override
	public WOActionResults destroyAction() throws Throwable {
		return errorResponse(ERXHttpStatusCodes.METHOD_NOT_ALLOWED);
	}

	@Override
	public WOActionResults showAction() throws Throwable {
		return errorResponse(ERXHttpStatusCodes.METHOD_NOT_ALLOWED);
	}

	@Override
	public WOActionResults indexAction() throws Throwable {
		return errorResponse(ERXHttpStatusCodes.METHOD_NOT_ALLOWED);
	}

}
