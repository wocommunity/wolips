package tk.eclipse.plugin.jspeditor.editors;

import org.eclipse.core.resources.IFile;

/**
 * The interface for the JSP filter.
 * <p>
 * This filter is called by the <code>JSPValidator</code> before validation processing.
 * 
 * @author Tom Wickham-Jones
 * @since 2.0.5
 */
public interface IJSPFilter {

	/**
	 * Return the possibly modified contents of a JSP.
	 * 
	 * @param raw contents
	 * @param the target JSP file
	 * @return filtered contents
	 */
	public String filterJSP( String contents, IFile file);
	
}
