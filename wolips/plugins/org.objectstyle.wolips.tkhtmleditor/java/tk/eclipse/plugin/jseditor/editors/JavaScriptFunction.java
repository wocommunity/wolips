package tk.eclipse.plugin.jseditor.editors;

import java.util.ArrayList;
import java.util.List;

/**
 * The model for the JavaScript function.
 * 
 * @author Naoki Takezoe
 */
public class JavaScriptFunction implements JavaScriptElement, JavaScriptContext {
	
	private String _name;
	private String _arguments;
	private int _offset;
	private int _end;
	private List<JavaScriptElement> _children = new ArrayList<JavaScriptElement>();
	private JavaScriptContext _parent;
	
	public JavaScriptFunction(String name, String arguments, int offset){
		this._name = name;
		this._arguments = arguments;
		this._offset = offset;
	}
	
	public String getArguments() {
		return _arguments;
	}
	
	public String getName() {
		return _name;
	}
	
	public int getOffset(){
		return _offset;
	}
	
	public int getStartOffset(){
		return getOffset();
	}
	
	public void setEndOffset(int end){
		this._end = end;
	}
	
	public int getEndOffset(){
		return _end;
	}
	
	public void add(JavaScriptFunction func){
		this._children.add(func);
	}
	
	public void add(JavaScriptVariable var){
		this._children.add(var);
	}
	
	public JavaScriptElement[] getChildren(){
		return this._children.toArray(new JavaScriptElement[this._children.size()]);
	}
	
	public JavaScriptElement[] getVisibleElements(){
		List<JavaScriptElement> list = new ArrayList<JavaScriptElement>();
		JavaScriptContext context = this;
		while(true){
			JavaScriptElement[] children = context.getChildren();
			for(int i=0;i<children.length;i++){
				list.add(children[i]);
			}
			
			if(context.getParent()==null){
				break;
			} else {
				context = context.getParent();
			}
		}
		return list.toArray(new JavaScriptElement[list.size()]);
	}
	
	public void setParent(JavaScriptContext context){
		this._parent = context;
	}
	
	public JavaScriptContext getParent(){
		return _parent;
	}

	
	@Override
  public String toString(){
		return _name + "(" + _arguments + ")";
	}
}
