package tk.eclipse.plugin.jseditor.editors;

/**
 * The interface for JavaScript element models.
 * 
 * @author Naoki Takezoe
 */
public interface JavaScriptElement {
	
	/**
	 * Returns the element offset in the source code.
	 * 
	 * @return the element offset
	 */
	public int getOffset();
	
	/**
	 * Returns the element name.
	 * 
	 * @return the element name
	 */
	public String getName();
	
}
