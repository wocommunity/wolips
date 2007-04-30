package tk.eclipse.plugin.jspeditor.editors;

import java.io.InputStream;

public interface ITLDLocator {

	/*
	 * Return the InputStream for a given TLD uri.
	 */
	public InputStream locateTLD( String uri);
	
	/*
	 * Return the URI 
	 */
	public String getURI();
	
	/*
	 * Return a name
	 */
	public String getPath();
}
