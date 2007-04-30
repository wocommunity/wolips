package tk.eclipse.plugin.jseditor.editors;

public interface JavaScriptContext {
	
	public void add(JavaScriptFunction func);
	
	public void add(JavaScriptVariable var);
	
	public JavaScriptElement[] getChildren();
	
	public JavaScriptElement[] getVisibleElements();
	
	public JavaScriptContext getParent();
	
	public int getStartOffset();
	
	public int getEndOffset();
	
}
