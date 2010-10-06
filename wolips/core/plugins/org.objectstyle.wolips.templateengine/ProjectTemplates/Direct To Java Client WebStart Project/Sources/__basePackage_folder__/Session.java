package $basePackage;

import com.webobjects.appserver.WOSession;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.foundation.NSLog;

public class Session extends WOSession {
	private static final long serialVersionUID = 1L;

	public Session() {
	}

	public EOFetchSpecification clientSideRequestGetFetchSpecification(
			String fetchSpecification, String entity) {
		return getFetchSpecification(fetchSpecification, entity);
	}

	/**
	 * Loads and returns an <tt>EOFetchSpecification</tt> objects for the given
	 * specification and entity name.
	 * 
	 * @param fetchSpecification
	 *            Name of the specification
	 * @param entity
	 *            Name of the entity
	 * @return A fetch specification, or <tt>null</tt> if it is not found, or an
	 *         error occurs
	 */
	public EOFetchSpecification getFetchSpecification(
			String fetchSpecification, String entity) {
		try {
			// NSLog.out.appendln("About to call FetchSpecification " +
			// fetchSpecification + " on Entity " + entity);
			return EOFetchSpecification.fetchSpecificationNamed(
					fetchSpecification, entity);
		} catch (Exception ex) {
			NSLog.out.appendln(ex);
			return null;
		}
	}
}
