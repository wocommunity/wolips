package $clientSideEOFPackage;

import com.webobjects.eoapplication.EOController;
import com.webobjects.eoapplication.EOEditable;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.eocontrol.EOGlobalID;
import com.webobjects.eocontrol.EOObjectStore;
import com.webobjects.eodistribution.client.EODistributedObjectStore;
import com.webobjects.eogeneration.EOControllerFactory;
import com.webobjects.eogeneration.EOFormController;
import com.webobjects.eogeneration.EOListController;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;

@SuppressWarnings("unchecked")
public class MyGenericRecord extends
                            EOGenericRecord {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	public static EODistributedObjectStore _distributedObjectStore() {
		EOObjectStore objectStore = EOEditingContext.defaultParentObjectStore();
		if (objectStore == null || !(objectStore instanceof EODistributedObjectStore)) {
			throw new IllegalStateException("Default parent object store needs to be an EODistributedObjectStore");
		}
		return (EODistributedObjectStore) objectStore;
	}

	public static void formWithEntityName(String entityName,
	                                      EOGlobalID globalID) {
		EOControllerFactory f = EOControllerFactory.sharedControllerFactory();
		EOController controller = f.controllerWithSpecification(new NSDictionary(new Object[] { entityName,
		                                                                                       EOControllerFactory.FormTask,
		                                                                                       EOControllerFactory.TopLevelWindowQuestion },
		                                                                         new Object[] { EOControllerFactory.EntitySpecification,
		                                                                                       EOControllerFactory.TaskSpecification,
		                                                                                       EOControllerFactory.QuestionSpecification }),
		                                                        true);
		if (controller != null) {
			EOFormController formController = (EOFormController) f.controllerWithEntityName(controller,
			                                                                                EOControllerFactory.Open.class,
			                                                                                entityName);
			formController.openObjectWithGlobalID(globalID);
			formController.setEditability(EOEditable.AlwaysEditable);
			formController.makeVisible();
		}
	}

	/**
	 * Invokes {@link #invokeStatelessRemoteMethodWithKeyPath(String, String, Class[], Object[])} with "session" as the
	 * key path argument.
	 */
	public static Object invokeStatelessOnSession(String methodName,
	                                              Class[] argumentTypes,
	                                              Object[] arguments) {

		return invokeStatelessRemoteMethodWithKeyPath("session",
		                                              methodName,
		                                              argumentTypes,
		                                              arguments);
	}

	/**
	 * This method invokes a remote method on an object on the server side that can be specified with a key path (no
	 * enterprise object) relative to the invocation target of the server side EODistributionContext (for example
	 * "session"). The arguments and return values of remote methods invoked through this method cannot be enterprise
	 * objects (but global IDs are okay). You can use this method, for example, to load resources from the server or to
	 * perform checks in background threads (as long as no enterprise objects are involved).
	 * <p>
	 * The keyPath argument has special semantics: <ol type="a"}
	 * <li>If keyPath is a fully qualified key path (for example, "session"), the key path is followed starting from the
	 * invocation target of the EODistributionContext.
	 * <li>If keyPath is an empty string, the method is invoked on the invocation target of the EODistributionContext
	 * directly (typically a subclass of WOJavaClientComponent).
	 * <li>If keyPath is null, the method is invoked on one of the remote method receivers of the server side
	 * EODistributionContext. </ol>
	 * <p>
	 * If an actual key path is specified, the EODistributionContext on the server blocks all invocations sent with this
	 * method unless methodName is prefixed with "clientSideRequest" or unless the EODistributionContext's delegate (on
	 * the server) implements the right delegate methods to explicitly allow the invocation. "clientSideRequest" methods
	 * can be invoked without special delegate methods on the server-side distribution context, on a remote method
	 * receiver registered with the distribution context or on the session of the distribution context's invocation
	 * target.
	 * 
	 * @param keyPath
	 *            the key path identifying the receiver of the method invocation
	 * @param methodName
	 *            the name of the method to be invoked
	 * @param argumentTypes
	 *            the types of the arguments of the method to be invoked
	 * @param arguments
	 *            the arguments of the method to be invoked
	 * @return the return value of the remote method invocation
	 */
	public static Object invokeStatelessRemoteMethodWithKeyPath(String keyPath,
	                                                            String methodName,
	                                                            Class[] argumentTypes,
	                                                            Object[] arguments) {
		if (_distributedObjectStore() == null) {
			throw new IllegalStateException("Distributed object store is null, can not perform RMI");
		}

		return _distributedObjectStore().invokeStatelessRemoteMethodWithKeyPath(keyPath,
		                                                                        methodName,
		                                                                        argumentTypes,
		                                                                        arguments);
	}

	public static void listWithEntityName(String entityName,
	                                      EOFetchSpecification fs) {
		EOControllerFactory f = EOControllerFactory.sharedControllerFactory();
		EOController controller = f.controllerWithSpecification(new NSDictionary(new Object[] { entityName,
		                                                                                       EOControllerFactory.ListTask,
		                                                                                       EOControllerFactory.TopLevelWindowQuestion },
		                                                                         new Object[] { EOControllerFactory.EntitySpecification,
		                                                                                       EOControllerFactory.TaskSpecification,
		                                                                                       EOControllerFactory.QuestionSpecification }),
		                                                        true);
		if (controller != null) {
			EOListController listController = (EOListController) f.controllerWithEntityName(controller,
			                                                                                EOControllerFactory.List.class,
			                                                                                entityName);
			listController.listObjectsWithFetchSpecification(fs);
			listController.setEditability(EOEditable.NeverEditable);
			listController.makeVisible();
		}
	}

	public static void listWithEntityName(String entityName,
	                                      NSArray arrayOfEOs) {
		EOControllerFactory f = EOControllerFactory.sharedControllerFactory();
		EOController controller = f.controllerWithSpecification(new NSDictionary(new Object[] { entityName,
		                                                                                       EOControllerFactory.ListTask,
		                                                                                       EOControllerFactory.TopLevelWindowQuestion },
		                                                                         new Object[] { EOControllerFactory.EntitySpecification,
		                                                                                       EOControllerFactory.TaskSpecification,
		                                                                                       EOControllerFactory.QuestionSpecification }),
		                                                        true);
		if (controller != null) {
			EOListController listController = (EOListController) f.controllerWithEntityName(controller,
			                                                                                EOControllerFactory.List.class,
			                                                                                entityName);
			listController.listObjectsWithGlobalIDs((NSArray) arrayOfEOs.valueForKey("globalID"));
			listController.setEditability(EOEditable.NeverEditable);
			listController.makeVisible();
		}
	}

	public MyGenericRecord() {
		super();
	}

	@Override
	public void awakeFromInsertion(EOEditingContext ec) {
		super.awakeFromInsertion(ec);
	}

	public EOGlobalID globalID() {
		return editingContext().globalIDForObject(this);
	}

}
