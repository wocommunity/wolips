package tk.eclipse.plugin.jseditor.editors;

/**
 * The model for the JavaScript variable.
 * 
 * @author Naoki Takezoe
 */
public class JavaScriptVariable implements JavaScriptElement {
	
	private String name;
	private int offset;
	
	public JavaScriptVariable(String name, int offset){
		this.name = name;
		this.offset = offset;
	}
	
	public String getName() {
		return name;
	}
	
	public int getOffset(){
		return offset;
	}
	
	@Override
  public String toString(){
		return getName();
	}
}
